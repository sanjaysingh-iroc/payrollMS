package com.konnect.jpms.task;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

public class GenerateProjectwiseTimesheet implements ServletRequestAware, ServletResponseAware, IStatements, IConstants {

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
//	HSSFSheet conveyance_sheet;
	HSSFWorkbook workbook;

	{
		workbook = new HSSFWorkbook();
		monthly_time_sheet = workbook.createSheet("Time Report");
//		conveyance_sheet = workbook.createSheet("Conveyance Sheet");
	}

	public String execute() {

		
		Map<String, Map<String, Map<String, String>>> projectdetails = new LinkedHashMap<String, Map<String, Map<String, String>>>();
		List<String> nameheaderlist = new ArrayList<String>();
		List<Integer> leavesdaylist = new ArrayList<Integer>();
		List<Integer> holidayslist = new ArrayList<Integer>();
		Map<String, Map<String, String>> hmfinaltask = new HashMap<String, Map<String, String>>();
		List<String> idList = new ArrayList<String>();
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
//				fillsheet(projectdetails, nameheaderlist, leavesdaylist, holidayslist, hmfinaltask, idList);
				createExcelFile(projectdetails, nameheaderlist, leavesdaylist, holidayslist, hmfinaltask, idList);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;
	}

	int coloumncountmain = 0;
	String downloadSubmit;
//	String datefrom;
	String strPC;

	String proFreqId;
	String proId;
	String proName;
	
	String frmDate;
	String toDate;
	
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
			pst.setDate(2, uF.getDateFormat(getFrmDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getToDate(), DATE_FORMAT));
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
			pst.setDate(9, uF.getDateFormat(getFrmDate(), DATE_FORMAT));
			pst.setDate(10, uF.getDateFormat(getToDate(), DATE_FORMAT));
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

	public void createExcelFile(Map<String, Map<String, Map<String, String>>> projectdetails, List<String> nameheaderlist, List<Integer> leavesdaylist, List<Integer> holidayslist,
			Map<String, Map<String, String>> hmfinaltask, List<String> idList)
			throws IOException {
		FileOutputStream fos = null;
		try {
//			writeTimeSheetReport(projectdetails, nameheaderlist, leavesdaylist, holidayslist, hmfinaltask, idList);
			fillTaskRows();
			String reportName = getReportName(); // get report name as per
													// client requirement.

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

	
	
	
	// *************** Project Timesheet Data *******************************
	
	
	public void fillTaskRows() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("=========>> 1 fillTaskRow ");
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
				"where p.pro_id = pf.pro_id and pf.pro_freq_id=?");
			pst.setInt(1, uF.parseToInt(getProFreqId()));
			rs = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rs.next()) {
				hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
				hmProjectData.put("PRO_FREQ_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_FREQ_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
			setProName(hmProjectData.get("PRO_NAME"));
			
			Map<String, String> hmProjectResourceMap = new HashMap<String, String>();
			Map<String, String> hmProjectResourceWLocationMap = new HashMap<String, String>();
			StringBuilder projectResources = new StringBuilder();
			pst = con.prepareStatement("select resource_ids from activity_info where pro_id = ? ");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				projectResources.append(rs.getString("resource_ids"));
			}
			rs.close();
			pst.close();
			
			List<String> alProResources = new ArrayList<String>();
			List<String> pResourceList = Arrays.asList(projectResources.toString().split(","));
			for(int a=0; pResourceList != null && a<pResourceList.size(); a++) {
				if(pResourceList.get(a) != null && !pResourceList.get(a).equals("")) {
					if(!alProResources.contains(pResourceList.get(a))) {
						alProResources.add(pResourceList.get(a));
					}
					hmProjectResourceWLocationMap.put(pResourceList.get(a), CF.getEmpWlocationId(con, uF, pResourceList.get(a)));
					hmProjectResourceMap.put(pResourceList.get(a), CF.getEmpNameMapByEmpId(con, pResourceList.get(a)));
				}
			}
			
//			System.out.println("alProResources ===>> " + alProResources);
//			System.out.println("hmProjectResourceMap ===>> " + hmProjectResourceMap);
			request.setAttribute("alProResources", alProResources);
			
			Map<String, Map<String, String>> hmLeaveDays = new HashMap<String, Map<String, String>>();
			Map hmLeavesColour = new HashMap();
			CF.getLeavesColour(con, hmLeavesColour);
			
			Map<String, String> hmLeaveCode = new HashMap<String, String>();
			
			hmLeaveDays = getLeaveDetails(hmProjectData.get("PRO_FREQ_START_DATE"), hmProjectData.get("PRO_FREQ_END_DATE"), uF, hmLeaveCode);

			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekendMap = CF.getWeekEndDateList(con, hmProjectData.get("PRO_FREQ_START_DATE"), hmProjectData.get("PRO_FREQ_END_DATE"), CF, uF, hmWeekEndHalfDates, null);
//			String strWLocationId = hmEmpWLocation.get(getStrResourceId()); 
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, hmProjectData.get("PRO_FREQ_START_DATE"), hmProjectData.get("PRO_FREQ_END_DATE"), alEmpCheckRosterWeektype, hmRosterWeekEndDates, hmWeekendMap, hmEmpLevelMap, hmEmpWLocation, hmWeekEndHalfDates);
			

			Map hmHolidays = new HashMap();
			Map hmHolidayDates = new HashMap();
			CF.getHolidayList(con,request, hmProjectData.get("PRO_FREQ_START_DATE"), hmProjectData.get("PRO_FREQ_END_DATE"), CF,hmHolidayDates, hmHolidays, true);

			if (uF.getDateFormat(getFrmDate(), DATE_FORMAT).before(uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT)) || uF.getDateFormat(getFrmDate(), DATE_FORMAT).after(uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT))) {
				setFrmDate(hmProjectData.get("PRO_FREQ_START_DATE"));
			}

			if (uF.getDateFormat(getToDate(), DATE_FORMAT).before(uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT)) || uF.getDateFormat(getToDate(), DATE_FORMAT).after(uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT))) {
				setToDate(hmProjectData.get("PRO_FREQ_END_DATE"));
			}

			pst = con.prepareStatement("select sum(a.actual_hrs) as actual_hrs, sum(billable_hrs) as billable_hrs,ai.parent_task_id,a.task_date," +
				" a.task_location,a.emp_id from (select sum(actual_hrs) as actual_hrs, sum(billable_hrs) as billable_hrs, ta.activity_id," +
				" ta.task_location,ta.task_date, ta.emp_id from task_activity ta where task_date between ? and ? and (is_approved = 1 or is_approved = 2) and ta.activity_id in (" +
				" select task_id from activity_info where pro_id = ? and parent_task_id in (select task_id from activity_info where pro_id = ?))" +
				" group by ta.emp_id,ta.activity_id,ta.task_date,ta.task_location) as a, activity_info ai where a.activity_id = ai.task_id and " +
				" ai.pro_id = ? group by a.emp_id,ai.parent_task_id,a.task_date, a.task_location order by ai.parent_task_id");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getProId()));
			pst.setInt(4, uF.parseToInt(getProId()));
			pst.setInt(5, uF.parseToInt(getProId()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
		
			Map<String, String> hmDateT = new HashMap<String, String>();
			Map<String, String> hmDateBillableHrsT = new HashMap<String, String>();
			Map<String, String> hmTaskIsBillableT = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmployeeT = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeBillableHrsT = new HashMap<String, Map<String, String>>();
			Map<String, String> hmTasksT = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmployeeTasksT = new LinkedHashMap<String, Map<String, String>>();
		
			Map<String, String> hmEmployeeCountT = new HashMap<String, String>();
			Map<String, String> hmEmployeeBillCountT = new HashMap<String, String>();
		
			int nCountT = 0;
			int nBillCountT = 0;
			double dblTotalHrsT = 0;
			double dblTotalBillableHrsT = 0;
			boolean isBillableT = false;
			String strActivityIdNewT = null;
			String strActivityIdOldT = null;
			String strEmpIdNewT = null;
			String strEmpIdOldT = null;
			while (rs.next()) {
		
				strActivityIdNewT = rs.getString("parent_task_id");
				strEmpIdNewT = rs.getString("emp_id");
				
				if (strEmpIdNewT != null && !strEmpIdNewT.equalsIgnoreCase(strEmpIdOldT)) {
					nCountT = 0;
					nBillCountT = 0;
					isBillableT = false;
					strActivityIdOldT = null;
				} else if (strEmpIdNewT == null && strEmpIdOldT != null) {
					nCountT = 0;
					nBillCountT = 0;
					isBillableT = false;
					strActivityIdOldT = null;
				}
				
				if (strActivityIdNewT != null && !strActivityIdNewT.equalsIgnoreCase(strActivityIdOldT)) {
					dblTotalHrsT = 0;
					dblTotalBillableHrsT = 0;
					hmDateT = new HashMap<String, String>();
					hmDateBillableHrsT = new HashMap<String, String>();
					hmTasksT = new HashMap<String, String>();
					nCountT++;
				}
		
				if (strActivityIdNewT != null && !strActivityIdNewT.equalsIgnoreCase(strActivityIdOldT) && uF.parseToDouble(rs.getString("billable_hrs")) > 0) {
					nBillCountT++;
					isBillableT = true;
				} else if (!isBillableT && strActivityIdNewT != null && strActivityIdNewT.equalsIgnoreCase(strActivityIdOldT) && uF.parseToDouble(rs.getString("billable_hrs")) > 0) {
					nBillCountT++;
					isBillableT = true;
				}
				
				double dblHrs = uF.parseToDouble((String) hmDateT.get(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_" + rs.getString("task_location")));
				dblTotalHrsT = dblHrs+ uF.parseToDouble(rs.getString("actual_hrs"));
		
				hmDateT.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimal(dblTotalHrsT));
				
				double dblBillableHrs = uF.parseToDouble((String) hmDateBillableHrsT.get(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_" + rs.getString("task_location")));
				dblTotalBillableHrsT = dblBillableHrs + uF.parseToDouble(rs.getString("billable_hrs"));
		
				hmDateBillableHrsT.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimal(dblTotalBillableHrsT));
				
				hmEmployeeT.put(strEmpIdNewT+"_"+strActivityIdNewT, hmDateT);
		
				hmEmployeeBillableHrsT.put(strEmpIdNewT+"_"+strActivityIdNewT, hmDateBillableHrsT);
				
				hmTaskIsBillableT.put(strEmpIdNewT+"_"+strActivityIdNewT, isBillableT+"");
				
				hmTasksT.put(strEmpIdNewT+"_"+strActivityIdNewT + "_T", CF.getProjectTaskNameByTaskId(con, uF, rs.getString("parent_task_id")));
					
				hmTasksT.put(strEmpIdNewT+"_"+strActivityIdNewT + "_E", rs.getString("emp_id"));
				hmEmployeeTasksT.put(strEmpIdNewT+"_"+strActivityIdNewT, hmTasksT);
		
				hmEmployeeCountT.put(rs.getString("emp_id"), nCountT + "");
				hmEmployeeBillCountT.put(rs.getString("emp_id"), nBillCountT + "");
				
		//		System.out.println("hmProjectBillCount ===>>> " + hmProjectBillCount); 
				
				strActivityIdOldT = strActivityIdNewT;
				strEmpIdOldT = strEmpIdNewT;
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmEmployeeTasksT ===>> " + hmEmployeeTasksT);
//			System.out.println("hmEmployeeCountT ===>> " + hmEmployeeCountT);
//			System.out.println("hmEmployeeBillCountT ===>> " + hmEmployeeBillCountT);
			
			request.setAttribute("hmEmployeeT", hmEmployeeT);
			request.setAttribute("hmEmployeeBillableHrsT", hmEmployeeBillableHrsT);
			request.setAttribute("hmTaskIsBillableT", hmTaskIsBillableT);
			request.setAttribute("hmEmployeeTasksT", hmEmployeeTasksT);
			
			request.setAttribute("hmEmployeeCountT", hmEmployeeCountT);
			request.setAttribute("hmEmployeeBillCountT", hmEmployeeBillCountT);
			
			
			
			pst = con.prepareStatement("select actual_hrs, activity_id, ta.emp_id, task_date, activity_name, activity, task_location, billable_hrs, " +
				"is_billable, is_approved, ta.task_id, ta._comment,is_billable_approved from task_activity ta left join activity_info ai " +
				"on ta.activity_id = ai.task_id where ai.pro_id =? and task_date between ? and ? and (is_approved = 1 or is_approved = 2) order by ta.emp_id, activity_id desc, activity");
			pst.setInt(1, uF.parseToInt(getProId()));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();

			Map<String, String> hmDate = new HashMap<String, String>();
			Map<String, String> hmDateHrsIsApproved = new HashMap<String, String>();
			Map<String, String> hmDateBillableHrsIsApproved = new HashMap<String, String>();
			Map<String, String> hmDateTaskId = new HashMap<String, String>();
			Map<String, String> hmDateBillableHrs = new HashMap<String, String>();
			Map<String, String> hmTaskIsBillable = new HashMap<String, String>();
			Map<String, String> hmTaskDescri = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmployee = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeHrsIsApproved = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeBillableHrsIsApproved = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeTaskId = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeBillableHrs = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeTaskDescri = new HashMap<String, Map<String, String>>();

			Map<String, String> hmTasks = new HashMap<String, String>();
			Map hmEmployeeTasks = new LinkedHashMap();

			Map<String, String> hmEmployeeCount = new HashMap<String, String>();
			Map<String, String> hmEmployeeBillCount = new HashMap<String, String>();

			int nCount = 0;
			int nBillCount = 0;
			double dblTotalHrs = 0;
			double dblTotalBillableHrs = 0;
			boolean isBillable = false;
			String strActivityIdNew = null;
			String strActivityIdOld = null;
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			while (rs.next()) {

				strActivityIdNew = rs.getString("activity_id");
				strEmpIdNew = rs.getString("emp_id");
				if (uF.parseToInt(strActivityIdNew) == 0) {
					strActivityIdNew = rs.getString("activity");
				}

				if (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					nCount = 0;
					nBillCount = 0;
					strActivityIdOld = null;
				} else if (strEmpIdNew == null && strEmpIdOld != null) {
					nCount = 0;
					nBillCount = 0;
					strActivityIdOld = null;
				}
				
				if (strActivityIdNew != null && !strActivityIdNew.equalsIgnoreCase(strActivityIdOld)) {
					dblTotalHrs = 0;
					dblTotalBillableHrs = 0;
					isBillable = false;
					hmDate = new HashMap<String, String>();
					hmDateBillableHrs = new HashMap<String, String>();
					hmDateHrsIsApproved = new HashMap<String, String>();
					hmDateBillableHrsIsApproved = new HashMap<String, String>();
					hmDateTaskId = new HashMap<String, String>();
					hmTasks = new HashMap<String, String>();
					nCount++;
				}

				if(!isBillable) {
					isBillable = rs.getBoolean("is_billable");
					if(isBillable) {
						nBillCount++;
					}
				}
				
				double dblHrs = uF.parseToDouble((String) hmDate.get(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_" + rs.getString("task_location")));
				dblTotalHrs = dblHrs+ uF.parseToDouble(rs.getString("actual_hrs"));

				hmDate.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimal(dblTotalHrs));
				
				double dblBillableHrs = uF.parseToDouble((String) hmDateBillableHrs.get(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_" + rs.getString("task_location")));
				dblTotalBillableHrs = dblBillableHrs + uF.parseToDouble(rs.getString("billable_hrs"));

				hmDateBillableHrs.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimal(dblTotalBillableHrs));
				
				hmDateHrsIsApproved.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("is_approved"));
				
				hmDateBillableHrsIsApproved.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("is_billable_approved"));
				
				hmDateTaskId.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("task_id"));
				
				hmTaskDescri.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("_comment"));

				hmEmployee.put(strEmpIdNew+"_"+strActivityIdNew, hmDate);

				hmEmployeeBillableHrs.put(strEmpIdNew+"_"+strActivityIdNew, hmDateBillableHrs);
				
				hmEmployeeHrsIsApproved.put(strEmpIdNew+"_"+strActivityIdNew, hmDateHrsIsApproved);
				
				hmEmployeeBillableHrsIsApproved.put(strEmpIdNew+"_"+strActivityIdNew, hmDateBillableHrsIsApproved);
				
				hmEmployeeTaskDescri.put(strEmpIdNew+"_"+strActivityIdNew, hmTaskDescri);
				
				hmEmployeeTaskId.put(strEmpIdNew+"_"+strActivityIdNew, hmDateTaskId);
				
				hmTaskIsBillable.put(strEmpIdNew+"_"+strActivityIdNew, isBillable+"");
				
				if (uF.parseToInt(rs.getString("activity_id")) == 0) {
					hmTasks.put(strEmpIdNew+"_"+strActivityIdNew + "_T",rs.getString("activity"));
				} else {
					hmTasks.put(strEmpIdNew+"_"+strActivityIdNew + "_T",rs.getString("activity_name"));
				}

				hmTasks.put(strEmpIdNew+"_"+strActivityIdNew + "_E", rs.getString("emp_id"));
				hmEmployeeTasks.put(strEmpIdNew+"_"+strActivityIdNew, hmTasks);

				hmEmployeeCount.put(rs.getString("emp_id"), nCount + "");
				hmEmployeeBillCount.put(rs.getString("emp_id"), nBillCount + "");
				
//				System.out.println("hmProjectBillCount ===>>> " + hmProjectBillCount); 
				
				strActivityIdOld = strActivityIdNew;
				strEmpIdOld = strEmpIdNew;
			}
			
			rs.close();
			pst.close();
//			System.out.println("hmEmployeeTasks ===>> " + hmEmployeeTasks);
			
//			System.out.println("hmEmployeeBillableHrsIsApproved ===>>> " + hmEmployeeBillableHrsIsApproved);

			request.setAttribute("hmEmployee", hmEmployee);
			request.setAttribute("hmEmployeeBillableHrs", hmEmployeeBillableHrs);
			request.setAttribute("hmEmployeeHrsIsApproved", hmEmployeeHrsIsApproved);
			request.setAttribute("hmEmployeeBillableHrsIsApproved", hmEmployeeBillableHrsIsApproved);
			
			request.setAttribute("hmEmployeeTaskDescri", hmEmployeeTaskDescri);
			request.setAttribute("hmEmployeeTaskId", hmEmployeeTaskId);
			request.setAttribute("hmTaskIsBillable", hmTaskIsBillable);
			request.setAttribute("hmEmployeeTasks", hmEmployeeTasks);
			
			request.setAttribute("hmEmployeeCount", hmEmployeeCount);
			request.setAttribute("hmEmployeeBillCount", hmEmployeeBillCount);
			
			
			List<String> alTaskIds = new ArrayList<String>();
			
			pst = con.prepareStatement("select task_id, pro_id, activity_name, parent_task_id, task_description from activity_info ai where pro_id=?");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			Map<String, List<String>> hmTaskAndSubTaskIds = new LinkedHashMap<String, List<String>>();
			Map<String, String> hmTaskAndSubTaskDescription = new LinkedHashMap<String, String>();
			List<String> alSubTaskIds = new ArrayList<String>();
//			List<String> alTaskIds = new ArrayList<String>();
			while(rs.next()) {
				if(rs.getInt("parent_task_id") > 0) {
					alSubTaskIds = hmTaskAndSubTaskIds.get(rs.getString("parent_task_id"));
					if(alSubTaskIds == null) alSubTaskIds = new ArrayList<String>();
					alSubTaskIds.add(rs.getString("task_id"));
					hmTaskAndSubTaskIds.put(rs.getString("parent_task_id"), alSubTaskIds);
				}
				if(rs.getInt("parent_task_id") == 0) {
					alTaskIds.add(rs.getString("task_id"));
				}
				hmTaskAndSubTaskDescription.put(rs.getString("task_id"), rs.getString("task_description"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alTaskIds", alTaskIds);
			request.setAttribute("hmTaskAndSubTaskIds", hmTaskAndSubTaskIds);
			
			
			StringBuilder sbTasks = new StringBuilder();

			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(getFrmDate(),DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getFrmDate(),DATE_FORMAT, "yyyy")));

//			System.out.println("nDateCount======>"+nDateCount);
//			System.out.println("joiningDate======>"+joiningDate);
			
			String currDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
			
			request.setAttribute("currDate", currDate);
			
			request.setAttribute("sbTasks", sbTasks.toString());
			request.setAttribute("hmProjectResourceMap", hmProjectResourceMap);
			request.setAttribute("hmProjectResourceWLocationMap", hmProjectResourceWLocationMap);
			
			int nDateDiff = uF.parseToInt(uF.dateDifference(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT, hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT, CF.getStrTimeZone()));

			cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT, "yyyy")));
			List<String> alDates = new ArrayList<String>();
			for (int i = 0; i < nDateDiff; i++) {
				alDates.add(uF.getDateFormat(cal.get(Calendar.DATE) + "/"
					+ (cal.get(Calendar.MONTH) + 1) + "/"
					+ cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
				cal.add(Calendar.DATE, 1);

				if (uF.getDateFormat(cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT).equalsIgnoreCase(hmProjectData.get("PRO_FREQ_END_DATE"))) {
					alDates.add(uF.getDateFormat( cal.get(Calendar.DATE) + "/"
					+ (cal.get(Calendar.MONTH) + 1) + "/"
					+ cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
					break;
				}
			}

			request.setAttribute("hmLeaveDays", hmLeaveDays);
			request.setAttribute("hmWeekendMap", hmWeekendMap);
			request.setAttribute("hmHolidayDates", hmHolidayDates);
			request.setAttribute("hmLeavesColour", hmLeavesColour);
//			request.setAttribute("hmLeaveConstant", hmLeaveConstant);

			request.setAttribute("timesheet_title", "Timesheet details from " + hmProjectData.get("PRO_FREQ_START_DATE") + " to " + hmProjectData.get("PRO_FREQ_END_DATE"));
			request.setAttribute("alDates", alDates);

//			request.setAttribute("sbTaskStatus", sbTaskStatus);
			request.setAttribute("hmLeaveCode", hmLeaveCode);
			
			request.setAttribute("hmRosterWeekEndDates", hmRosterWeekEndDates);
			request.setAttribute("alEmpCheckRosterWeektype", alEmpCheckRosterWeektype);

			
			
// ************************************ Create excel sheeet of pro timesheet ***************************************************			

			int lessspace = 0;
			if(nDateDiff < 10){
				lessspace = 1;
			}
			Row firmNameRow = monthly_time_sheet.createRow(0);
			firmNameRow.setHeight((short) 450);
			monthly_time_sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
			for (int i = 0; i < 6 + nDateDiff; i++) {
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
				if (i == 6 + nDateDiff) {
					styleForFirmName.setBorderRight(CellStyle.BORDER_THIN);
				}
				firmNameCell.setCellStyle(styleForFirmName);
			}

			Row firmTypeRow = monthly_time_sheet.createRow(1);
			//monthly_time_sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));
			for (int i = 0; i < 6 + nDateDiff; i++) {
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
				if (i == 6 + nDateDiff) {
					styleForFirmType.setBorderRight(CellStyle.BORDER_THIN);
				}
				firmTypeCell.setCellStyle(styleForFirmType);
			}

			Row firstBlankRow = monthly_time_sheet.createRow(2);
			forBlankRowLeftAndRightBorder(firstBlankRow);

			Row clientNameRow = monthly_time_sheet.createRow(3);
			HSSFCellStyle rightBorderForClientSummary = workbook.createCellStyle();
			rightBorderForClientSummary.setBorderRight(CellStyle.BORDER_THIN);
			HSSFCellStyle clientSummary = workbook.createCellStyle();

			Font clientNameCellFont = workbook.createFont();
			clientNameCellFont.setFontHeight((short) 230);

			Cell clientNameCell = clientNameRow.createCell(0);
			clientNameCell.setCellValue("Project Name: " + hmProjectData.get("PRO_NAME"));
			clientSummary.setFont(clientNameCellFont);
			clientSummary.setBorderLeft(CellStyle.BORDER_THIN);
			clientNameCell.setCellStyle(clientSummary);

			clientSummary.setFont(clientNameCellFont);
			clientSummary.setBorderLeft(CellStyle.BORDER_THIN);

			Cell clientBankACNoCell = clientNameRow.createCell(nDateDiff);
			clientBankACNoCell.setCellValue("");

			Cell lastCellOfClientSummary1 = clientNameRow.createCell(nDateDiff + 6);
			lastCellOfClientSummary1.setCellStyle(rightBorderForClientSummary);

			Row divisionRow = monthly_time_sheet.createRow(4);
			divisionRow.setHeight((short) 300);
			Cell clientDivisionCell = divisionRow.createCell(0);
			clientDivisionCell.setCellValue("Client Name:" + hmProjectData.get("PRO_CUSTOMER_NAME"));
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
				reportNameCell = divisionRow.createCell(9); // + (nDateDiff / 2)
			}
			reportNameCell.setCellValue(hmProjectData.get("PRO_BILLING_FREQUENCY").toUpperCase()+ " TIMESHEET ");
			reportNameCell.setCellStyle(cellStyleforReportName);

			Cell clientPANNoCell = divisionRow.createCell(nDateDiff);
			clientPANNoCell.setCellValue(" Project Owner: " + hmProjectData.get("PRO_OWNER_NAME"));

			Cell lastCellOfClientSummary2 = divisionRow.createCell(nDateDiff + 6);
			lastCellOfClientSummary2.setCellStyle(rightBorderForClientSummary);

			Row empCodeRow = monthly_time_sheet.createRow(5);
			Cell clientEmpIdCell = empCodeRow.createCell(0);
			clientEmpIdCell.setCellValue("SPOC Name: " + hmProjectData.get("PRO_CUST_SPOC_NAME"));
			clientEmpIdCell.setCellStyle(clientSummary);

			Cell clientWorkingHrsCell = empCodeRow.createCell(nDateDiff);
			clientWorkingHrsCell.setCellValue("");

			Cell lastCellOfClientSummary3 = empCodeRow.createCell(nDateDiff + 6);
			lastCellOfClientSummary3.setCellStyle(rightBorderForClientSummary);

			Row secondBlankRow = monthly_time_sheet.createRow(6);
			forBlankRowLeftAndRightBorder(secondBlankRow);

			Row headingRowDesc = monthly_time_sheet.createRow(7);
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
			nameOfClient.setCellValue("Resource Name ");

			monthly_time_sheet.autoSizeColumn((short) 1);
			nameOfClient.setCellStyle(headingStyle);
//			monthly_time_sheet.addMergedRegion(new CellRangeAddress(12, 12, 1, 2));

			Cell assignment = headingRowDesc.createCell(3);
			assignment.setCellValue("Task/SubTask Name ");
			monthly_time_sheet.autoSizeColumn((short) 3);
			assignment.setCellStyle(headingStyle);

			Cell totalDays = headingRowDesc.createCell(4);
			totalDays.setCellValue("Description");
			totalDays.setCellStyle(headingStyle);

			Cell conveyExp = headingRowDesc.createCell(5);
			conveyExp.setCellValue("Total Hours ");
			conveyExp.setCellStyle(headingStyle);

			for (int i = 6; i < 6 + nDateDiff; i++) {
				Cell cell = headingRowDesc.createCell(i);
				HSSFCellStyle cellStyleHeadingMonth = workbook.createCellStyle();
				Font font = workbook.createFont();
				font.setBoldweight((short) 1000);
				cellStyleHeadingMonth.setBorderTop(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setFont(font);

				if (i == 5) {
					cellStyleHeadingMonth.setBorderLeft(CellStyle.BORDER_THIN);
				}
				if (i == 4 + (nDateDiff / 2)) {
					cell.setCellValue("Period: " + getFrmDate() + " to " + getToDate());
					cell.setCellStyle(cellStyleHeadingMonth);
				}
				if (i == nDateDiff + 6) {
					cellStyleHeadingMonth.setBorderRight(CellStyle.BORDER_THIN);
				}
				cell.setCellStyle(cellStyleHeadingMonth);
			}

			Row headingRowNums = monthly_time_sheet.createRow(8); // 13
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

			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "yyyy")));

			for (int i = 6, j = 1; i < 6 + nDateDiff; i++, j++) {
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

			
			
			Map<String, String> hmTotalBillableHrs = new HashMap<String, String>();
			StringBuilder sb = new StringBuilder();
			String strResourcIdNew = null;
			String strResourcIdOld = null;
			int i = 8;
			int resourceCount = 1;
//			for (int i = 9, k = 1; i < linecount; i++, k++) {
//			System.out.println("alProResources ===>> " + alProResources);
			for(int b=0; alProResources != null && b<alProResources.size(); b++) {
				String strResourceId = alProResources.get(b);
				
				String strWLocationId = hmProjectResourceWLocationMap.get(strResourceId);
				Set<String> weeklyOffSet = hmWeekendMap.get(strWLocationId);
				if(weeklyOffSet==null) weeklyOffSet=new HashSet<String>();
				
				Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(strResourceId);
				if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
				
				Map<String, String> hmLeaves = hmLeaveDays.get(strResourceId);
				if(hmLeaves == null) hmLeaves = new HashMap<String, String>();
				
//				System.out.println("alTaskIds ===>> " + alTaskIds);
				for(int a=0; alTaskIds != null && a<alTaskIds.size(); a++) {
					String strActivityId = alTaskIds.get(a);
					
					alSubTaskIds = new ArrayList<String>();
					
					if(hmTaskAndSubTaskIds != null) {
						alSubTaskIds = hmTaskAndSubTaskIds.get(strActivityId);
					}
//					System.out.println("alSubTaskIds1 ===>> " + alSubTaskIds1);
//					System.out.println("hmEmployeeTasks ===>> " + hmEmployeeTasks);
					
					Map hmTasks1 = (Map)hmEmployeeTasks.get(strResourceId+"_"+strActivityId);
					if(hmTasks1 == null)hmTasks1 = new HashMap();
//					System.out.println("hmTasks1 ===>> " + hmTasks1);
					if((hmTasks1 == null || hmTasks1.isEmpty()) && (alSubTaskIds ==null || alSubTaskIds.size()==0)) {
						//System.out.println("strActivityId empty ===>> " + strActivityId);
					} else {
						
						if(hmTasks1 == null || hmTasks1.isEmpty()) {
							
//							System.out.println("hmTasks1 ===>> " + hmTasks1 + " i ==>>> " + i);
							Map hmTasksT1 = (Map)hmEmployeeTasksT.get(strResourceId+"_"+strActivityId);
							if(hmTasksT1 == null)hmTasksT1 = new HashMap();
							//System.out.println("hmTasksT ===>> " + hmTasksT);
							
							if(hmTasksT1 != null && !hmTasksT1.isEmpty()) {
//							String strResourcId = (String)hmTasksT1.get(strResourceId+"_"+strActivityId+"_E");
							strResourcIdNew = (String)hmTasksT1.get(strResourceId+"_"+strActivityId+"_E");
							//System.out.println("strActivityId hmTasks empty ===>> " + strActivityId);
							
//							Map<String, String> hmDatesT = (Map<String, String>)hmEmployeeT.get(strResourceId+"_"+strActivityId);
//							if(hmDatesT == null)hmDatesT = new HashMap<String, String>();
							
							Map<String, String> hmDatesBillableHrsT = (Map<String, String>)hmEmployeeBillableHrsT.get(strResourceId+"_"+strActivityId);
							if(hmDatesBillableHrsT == null) hmDatesBillableHrsT = new HashMap<String, String>();
							
							for(int j=0; j<2; j++) {
								i++;
								sb.replace(0, sb.length(), "");
								if(j==0) {
									sb.append("ONS"); 
								} else {
									sb.append("OFS");
								}
							
				Row row = monthly_time_sheet.createRow(i);
				int dateCnt = 0;
				int dateCnt1 = 0;
				int filldateCnt = 0;
				int filldateCnt1 = 0;
				for (int jj = 0; jj < 6 + nDateDiff; jj++) {
					HSSFCellStyle cellStyle = workbook.createCellStyle();
					cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
					cellStyle.setBorderTop(CellStyle.BORDER_THIN);
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cellStyle.setBorderRight(CellStyle.BORDER_THIN);

					Cell cell = row.createCell(jj);

//					System.out.println("strResourcIdNew ==>> " +strResourcIdNew + " -- strResourcIdOld ====>> " + strResourcIdOld);
					if (strResourcIdNew!=null && (strResourcIdOld == null || !strResourcIdNew.equalsIgnoreCase(strResourcIdOld)) && jj == 0) { //i % 2 == 0 &&
//						System.out.println("iN -- strResourcIdOld ====>> " + strResourcIdOld+ " resourceCount ==>> " + resourceCount);
						cell.setCellValue(resourceCount+"");
						cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
						cell.setCellStyle(cellStyle);
					}
					if ((strResourcIdNew==null || strResourcIdNew.equalsIgnoreCase(strResourcIdOld)) && jj == 0) { //i % 2 == 0 &&
						cell.setCellValue("");
					}
					
					if (jj == 0) {
						cellStyle.setBorderTop(CellStyle.BORDER_NONE);
					}
					if (strResourcIdNew!=null && !strResourcIdNew.equalsIgnoreCase(strResourcIdOld) && jj == 1) {
						cell.setCellValue(CF.getEmpNameMapByEmpId(con, strResourceId));
						cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
						resourceCount++;
					} else if ((strResourcIdNew==null || strResourcIdNew.equalsIgnoreCase(strResourcIdOld)) && jj == 1) {
						cell.setCellValue("");
						cell.setCellStyle(cellStyle);
					}
					
					if (jj == 1) {
						cellStyle.setBorderTop(CellStyle.BORDER_NONE);
					}
					
					if (j==0 && jj == 2) {
						cell.setCellValue("Onsite");
						cell.setCellStyle(cellStyle);
					}
					if (j==1 && jj == 2) {
						cell.setCellValue("Offsite");
						cell.setCellStyle(cellStyle);
					}
					
					
					if (j==0 && jj == 3) { //i % 2 == 0 && 
//						System.out.println("Task Name ===>>  "+ uF.showData((String)hmTasks1.get(strResourceId+"_"+strActivityId+"_T"), "-"));
						cell.setCellValue(uF.showData((String)hmTasksT1.get(strResourceId+"_"+strActivityId+"_T"), "-"));
						cell.setCellStyle(cellStyle);
					}
					if (j==1 && jj == 3) { //i % 2 == 0 && 
//						System.out.println("Task Name ===>>  blank");
						cell.setCellValue("");
						cell.setCellStyle(cellStyle);
					}
//					
//
					if (j==0 && jj == 4) { //i % 2 == 0 && 
						cell.setCellValue(uF.showData(hmTaskAndSubTaskDescription.get(strActivityId), "-"));
						cell.setCellStyle(cellStyle);
					}
					if (j==1 && jj == 4) { //i % 2 == 0 && 
						cell.setCellValue("");
						cell.setCellStyle(cellStyle);
					}
//					
					if (j==0 && jj == 5) {
						double tothrs = 0;
						int totday = 0;
						for(int stsk=0; alDates!=null && stsk<alDates.size(); stsk++) {
							String dayStatus= uF.showData((String)hmDatesBillableHrsT.get((String)alDates.get(stsk)+"_"+sb.toString()), "0");
							if(uF.parseToDouble(dayStatus)>0) {
								totday++;
								tothrs += uF.parseToDouble(dayStatus);
							}
						}
						cell.setCellValue(uF.getTotalTimeMinutes100To60(tothrs+""));
						cell.setCellStyle(cellStyle);
					}
					if (j==1 && jj == 5) {
						double tothrs = 0;
						int totday = 0;
						for(int stsk=0; alDates!=null && stsk<alDates.size(); stsk++) {
							String dayStatus= uF.showData((String)hmDatesBillableHrsT.get((String)alDates.get(stsk)+"_"+sb.toString()), "0");
							if(uF.parseToDouble(dayStatus)>0) {
								totday++;
								tothrs += uF.parseToDouble(dayStatus);
							}
						}
						cell.setCellValue(uF.getTotalTimeMinutes100To60(tothrs+""));
						cell.setCellStyle(cellStyle);
					}
					
					if (j==0 && jj > 5) {
						
						String strText = "-";
						
//						String strBgColor = null;
//						if(alEmpCheckRosterWeektype.contains(strResourceId)) {
//							if(rosterWeeklyOffSet.contains(alDates.get(dateCnt))) {
//								strBgColor =IConstants.WEEKLYOFF_COLOR;
//							}
//						} else if(weeklyOffSet.contains(alDates.get(dateCnt))) {
//							strBgColor =IConstants.WEEKLYOFF_COLOR;
//						}
//						if(strBgColor!=null) {
//							strText = "W/O";
//						}
//						
//						if(strBgColor==null) {
//							strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(dateCnt), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
//							strText = "H";
//						}
//						if(strBgColor==null) {
//							strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(dateCnt)));
//							//strText = (String)hmLeaves.get((String)alDates.get(i));
//							strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(dateCnt))); 
//						}
//						
//						if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(dateCnt).toString(), IConstants.DATE_FORMAT))) {
//							strBgColor = "#EFEFEF";
//						}
//						
//						if(strText==null) {
//							strText = "-";
//						}
						
						String dayStatus= uF.showData((String)hmDatesBillableHrsT.get((String)alDates.get(dateCnt)+"_"+sb.toString()), strText);
//						double dblBillableHrs = uF.parseToDouble((String)hmDatesBillableHrsT.get((String)alDates.get(dateCnt)+"_"+sb.toString()));
//						dblBillableHrs += uF.parseToDouble((String)hmTotalBillableHrs.get((String)alDates.get(dateCnt)));
//						hmTotalBillableHrs.put((String)alDates.get(dateCnt), uF.formatIntoTwoDecimal(dblBillableHrs)+"");
//						System.out.println("dayStatus OFS ===>> " +dayStatus+"  alDates.get(dateCnt) ===>> " + alDates.get(dateCnt));
//						Map<String, Map<String, String>> hmactualhrs = (Map<String, Map<String, String>>) projectdetails.get(alclient_namelist.get(dayrowcount - 1));
						if(uF.parseToDouble(dayStatus) > 0) {
							dayStatus = uF.getTotalTimeMinutes100To60(dayStatus);
						}
						cell.setCellValue(dayStatus);
						cell.setCellStyle(cellStyle);
						if(dateCnt < alDates.size()-1) {
							dateCnt++;
						}
					}
					
					
					if (j==1 && jj > 5) {
						
						String strText = "-";
						
//						String strBgColor = null;
//						if(alEmpCheckRosterWeektype.contains(strResourceId)) {
//							if(rosterWeeklyOffSet.contains(alDates.get(dateCnt1))) {
//								strBgColor =IConstants.WEEKLYOFF_COLOR;
//							}
//						} else if(weeklyOffSet.contains(alDates.get(dateCnt1))) {
//							strBgColor =IConstants.WEEKLYOFF_COLOR;
//						}
//						if(strBgColor!=null) {
//							strText = "W/O";
//						}
//						
//						if(strBgColor==null) {
//							strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(dateCnt1), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
//							strText = "H";
//						}
//						if(strBgColor==null) {
//							strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(dateCnt1)));
//							//strText = (String)hmLeaves.get((String)alDates.get(i));
//							strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(dateCnt1))); 
//						}
//						
//						if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(dateCnt1).toString(), IConstants.DATE_FORMAT))) {
//							strBgColor = "#EFEFEF";
//						}
//						
//						if(strText==null) {
//							strText = "-";
//						}
						
						String dayStatus= uF.showData((String)hmDatesBillableHrsT.get((String)alDates.get(dateCnt1)+"_"+sb.toString()), strText);
//						double dblBillableHrs = uF.parseToDouble((String)hmDatesBillableHrsT.get((String)alDates.get(dateCnt1)+"_"+sb.toString()));
//						dblBillableHrs += uF.parseToDouble((String)hmTotalBillableHrs.get((String)alDates.get(dateCnt1)));
//						hmTotalBillableHrs.put((String)alDates.get(dateCnt1), uF.formatIntoTwoDecimal(dblBillableHrs)+"");
//						System.out.println("dayStatus ONS ===>> " +dayStatus +"  alDates.get(dateCnt1) ===>> " + alDates.get(dateCnt1));
//						Map<String, Map<String, String>> hmactualhrs = (Map<String, Map<String, String>>) projectdetails.get(alclient_namelist.get(dayrowcount - 1));
						if(uF.parseToDouble(dayStatus) > 0) {
							dayStatus = uF.getTotalTimeMinutes100To60(dayStatus);
						}
						cell.setCellValue(dayStatus);
						cell.setCellStyle(cellStyle);
						if(dateCnt1 < alDates.size()-1) {
							dateCnt1++;
						}
					}
					
					cell.setCellStyle(cellStyle);
						}
				strResourcIdOld = strResourcIdNew;
					}
							
//							String strEmployeeIdNewST = null;
//							String strEmployeeIdOldST = null;
							//boolean flagST = false;
							
//							System.out.println("alSubTaskIds ===>> " + alSubTaskIds);
							for(int st=0; alSubTaskIds != null && st<alSubTaskIds.size(); st++) {
								String strSubTaskId = alSubTaskIds.get(st);
//								System.out.println("strSubTaskId ===>> " + strSubTaskId);
								
								hmTasks = (Map)hmEmployeeTasks.get(strResourceId+"_"+strSubTaskId);
								if(hmTasks == null) hmTasks = new HashMap();
//								System.out.println("hmTasks ===>> " + hmTasks);
								
								if(hmTasks != null && !hmTasks.isEmpty()) {
//								String strEmployeeIdST = (String)hmTasks.get(strResourceId+"_"+strSubTaskId+"_E");
//								strEmployeeIdNewST = (String)hmTasks.get(strResourceId+"_"+strSubTaskId+"_E");
								
								
								//System.out.println("nCountST ===>> " + nCountST);
								//System.out.println("nCount ===>> " + nCount);
								
//								System.out.println(" ------ " + hmEmployeeBillableHrsT +" \n------ " + hmEmployeeBillableHrs);
								hmDatesBillableHrsT = (Map<String, String>)hmEmployeeBillableHrs.get(strResourceId+"_"+strSubTaskId);
								if(hmDatesBillableHrsT == null)hmDatesBillableHrsT = new HashMap<String, String>();
								
							
								for(int j=0; j<2; j++) {
									i++;
									sb.replace(0, sb.length(), "");
									if(j==0) {
										sb.append("ONS"); 
									} else {
										sb.append("OFS");
									}
									
								Row row = monthly_time_sheet.createRow(i);
								int dateCnt = 0;
								int dateCnt1 = 0;
								int filldateCnt = 0;
								int filldateCnt1 = 0;
								for (int jj = 0; jj < 6 + nDateDiff; jj++) {
									HSSFCellStyle cellStyle = workbook.createCellStyle();
									cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
									cellStyle.setBorderTop(CellStyle.BORDER_THIN);
									cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
									cellStyle.setBorderRight(CellStyle.BORDER_THIN);

									Cell cell = row.createCell(jj);

									if (j==0 && jj == 0) { //i % 2 == 0 && 
										cell.setCellValue(" ");
										cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
										cell.setCellStyle(cellStyle);
									}
									if (j==1 && jj == 0) { //i % 2 == 0 && 
										cell.setCellValue(" ");
										cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
										cell.setCellStyle(cellStyle);
									}
									/*if (jj == 0) {
										cellStyle.setBorderTop(CellStyle.BORDER_NONE);
									}*/
									if (j==0 && jj == 1) {
										cell.setCellValue(" ");
										cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
										cell.setCellStyle(cellStyle);
									}
									if (j==1 && jj == 1) {
										cell.setCellValue(" ");
										cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
										cell.setCellStyle(cellStyle);
									}
//									if (jj == 1) {
//										cellStyle.setBorderTop(CellStyle.BORDER_NONE);
//									}
									
									if (j==0 && jj == 2) {
										cell.setCellValue("Onsite");
										cell.setCellStyle(cellStyle);
									}
									if (j==1 && jj == 2) {
										cell.setCellValue("Offsite");
										cell.setCellStyle(cellStyle);
									}
									
									
									if (j==0 && jj == 3) { //i % 2 == 0 && 
//										System.out.println("Task Name ===>>  "+ uF.showData((String)hmTasks1.get(strResourceId+"_"+strActivityId+"_T"), "-"));
										cell.setCellValue(uF.showData((String)hmTasks.get(strResourceId+"_"+strSubTaskId+"_T"), "-")+" [ST]");
										cell.setCellStyle(cellStyle);
									}
									if (j==1 && jj == 3) { //i % 2 == 0 && 
//										System.out.println("Task Name ===>>  blank");
										cell.setCellValue("");
										cell.setCellStyle(cellStyle);
									}
//									
				//
									if (j==0 && jj == 4) { //i % 2 == 0 && 
										cell.setCellValue(uF.showData(hmTaskAndSubTaskDescription.get(strSubTaskId), "-"));
										cell.setCellStyle(cellStyle);
									}
									if (j==1 && jj == 4) { //i % 2 == 0 && 
										cell.setCellValue("");
										cell.setCellStyle(cellStyle);
									}
//									
									if (j==0 && jj == 5) {
										double tothrs = 0;
										int totday = 0;
										for(int stsk=0; alDates!=null && stsk<alDates.size(); stsk++) {
											String dayStatus= uF.showData((String)hmDatesBillableHrsT.get((String)alDates.get(stsk)+"_"+sb.toString()), "0");
											if(uF.parseToDouble(dayStatus)>0) {
												totday++;
												tothrs += uF.parseToDouble(dayStatus);
											}
										}
										cell.setCellValue(uF.getTotalTimeMinutes100To60(tothrs+""));
										cell.setCellStyle(cellStyle);
									}
									if (j==1 && jj == 5) {
										double tothrs = 0;
										int totday = 0;
										for(int stsk=0; alDates!=null && stsk<alDates.size(); stsk++) {
											String dayStatus= uF.showData((String)hmDatesBillableHrsT.get((String)alDates.get(stsk)+"_"+sb.toString()), "0");
											if(uF.parseToDouble(dayStatus)>0) {
												totday++;
												tothrs += uF.parseToDouble(dayStatus);
											}
										}
										cell.setCellValue(uF.getTotalTimeMinutes100To60(tothrs+""));
										cell.setCellStyle(cellStyle);
									}
									
									if (j==0 && jj > 5) {
										
										String strText = "-";
										
//										String strBgColor = null;
//										if(alEmpCheckRosterWeektype.contains(strResourceId)) {
//											if(rosterWeeklyOffSet.contains(alDates.get(dateCnt))) {
//												strBgColor =IConstants.WEEKLYOFF_COLOR;
//											}
//										} else if(weeklyOffSet.contains(alDates.get(dateCnt))) {
//											strBgColor =IConstants.WEEKLYOFF_COLOR;
//										}
//										if(strBgColor!=null) {
//											strText = "W/O";
//										}
//										
//										if(strBgColor==null) {
//											strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(dateCnt), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
//											strText = "H";
//										}
//										if(strBgColor==null) {
//											strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(dateCnt)));
//											//strText = (String)hmLeaves.get((String)alDates.get(i));
//											strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(dateCnt))); 
//										}
//										
//										if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(dateCnt).toString(), IConstants.DATE_FORMAT))) {
//											strBgColor = "#EFEFEF";
//										}
										
//										if(strText==null) {
//											strText = "-";
//										}
										
										String dayStatus= uF.showData((String)hmDatesBillableHrsT.get((String)alDates.get(dateCnt)+"_"+sb.toString()), strText);
										double dblBillableHrs = uF.parseToDouble((String)hmDatesBillableHrsT.get((String)alDates.get(dateCnt)+"_"+sb.toString()));
										dblBillableHrs += uF.parseToDouble((String)hmTotalBillableHrs.get((String)alDates.get(dateCnt)));
										hmTotalBillableHrs.put((String)alDates.get(dateCnt), uF.formatIntoTwoDecimal(dblBillableHrs)+"");
//										System.out.println("dayStatus OFS ===>> " +dayStatus +"  alDates.get(dateCnt1) ===>> " + alDates.get(dateCnt1));
										if(uF.parseToDouble(dayStatus) > 0) {
											dayStatus = uF.getTotalTimeMinutes100To60(dayStatus);
										}
										cell.setCellValue(dayStatus);
										cell.setCellStyle(cellStyle);
										if(dateCnt < alDates.size()-1) {
											dateCnt++;
										}
									}
									
									
									if (j==1 && jj > 5) {
										
										String strText = "-";
//										
//										String strBgColor = null;
//										if(alEmpCheckRosterWeektype.contains(strResourceId)) {
//											if(rosterWeeklyOffSet.contains(alDates.get(dateCnt1))) {
//												strBgColor =IConstants.WEEKLYOFF_COLOR;
//											}
//										} else if(weeklyOffSet.contains(alDates.get(dateCnt1))) {
//											strBgColor =IConstants.WEEKLYOFF_COLOR;
//										}
//										if(strBgColor!=null) {
//											strText = "W/O";
//										}
//										
//										if(strBgColor==null) {
//											strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(dateCnt1), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
//											strText = "H";
//										}
//										if(strBgColor==null) {
//											strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(dateCnt1)));
//											//strText = (String)hmLeaves.get((String)alDates.get(i));
//											strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(dateCnt1))); 
//										}
//										
//										if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(dateCnt1).toString(), IConstants.DATE_FORMAT))) {
//											strBgColor = "#EFEFEF";
//										}
//										
//										if(strText==null) {
//											strText = "-";
//										}
										
										String dayStatus= uF.showData((String)hmDatesBillableHrsT.get((String)alDates.get(dateCnt1)+"_"+sb.toString()), strText);
										double dblBillableHrs = uF.parseToDouble((String)hmDatesBillableHrsT.get((String)alDates.get(dateCnt1)+"_"+sb.toString()));
										dblBillableHrs += uF.parseToDouble((String)hmTotalBillableHrs.get((String)alDates.get(dateCnt1)));
										hmTotalBillableHrs.put((String)alDates.get(dateCnt1), uF.formatIntoTwoDecimal(dblBillableHrs)+"");
//										System.out.println("dayStatus ONS ===>> " +dayStatus +"  alDates.get(dateCnt1) ===>> " + alDates.get(dateCnt1));
//										Map<String, Map<String, String>> hmactualhrs = (Map<String, Map<String, String>>) projectdetails.get(alclient_namelist.get(dayrowcount - 1));
										if(uF.parseToDouble(dayStatus) > 0) {
											dayStatus = uF.getTotalTimeMinutes100To60(dayStatus);
										}
										cell.setCellValue(dayStatus);
										cell.setCellStyle(cellStyle);
										if(dateCnt1 < alDates.size()-1) {
											dateCnt1++;
										}
									}
									
									cell.setCellStyle(cellStyle);
										}
								strResourcIdOld = strResourcIdNew;
								}
								
							}
						}
							strResourcIdOld = strResourcIdNew;
						}
							
				} else {
					String strResourcId = (String)hmTasks1.get(strResourceId+"_"+strActivityId+"_E");
					strResourcIdNew = (String)hmTasks1.get(strResourceId+"_"+strActivityId+"_E");
//					System.out.println("strActivityId hmTasks1 ===>> " + strActivityId);
//					System.out.println("strResourcId hmTasks1 ===>> " + strResourcId);
					
					Map<String, String> hmDatesBillableHrs = (Map<String, String>)hmEmployeeBillableHrs.get(strResourceId+"_"+strActivityId);
					if(hmDatesBillableHrs == null) hmDatesBillableHrs = new HashMap<String, String>();
					
					Map<String, String> hmTaskDescri1 = (Map<String, String>)hmEmployeeTaskDescri.get(strResourceId+"_"+strActivityId);
					if(hmTaskDescri1 == null)hmTaskDescri1 = new HashMap<String, String>();
					
					Map<String, String> hmDateTaskId1 = (Map<String, String>)hmEmployeeTaskId.get(strResourceId+"_"+strActivityId);
					if(hmDateTaskId1 == null)hmDateTaskId1 = new HashMap<String, String>();
					
					
					for(int j=0; j<2; j++) {
						i++;
						sb.replace(0, sb.length(), "");
						if(j==0) {
							sb.append("ONS"); 
						} else {
							sb.append("OFS");
						}
						

					Row row = monthly_time_sheet.createRow(i);
					int dateCnt = 0;
					int dateCnt1 = 0;
					int filldateCnt = 0;
					int filldateCnt1 = 0;
					for (int jj = 0; jj < 6 + nDateDiff; jj++) {
						HSSFCellStyle cellStyle = workbook.createCellStyle();
						cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
						cellStyle.setBorderTop(CellStyle.BORDER_THIN);
						cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
						cellStyle.setBorderRight(CellStyle.BORDER_THIN);

						Cell cell = row.createCell(jj);

						if (strResourcIdNew!=null && (strResourcIdOld == null || !strResourcIdNew.equalsIgnoreCase(strResourcIdOld)) && jj == 0) { //i % 2 == 0 && 
							cell.setCellValue(resourceCount);
							cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
						}
						if ((strResourcIdNew==null || strResourcIdNew.equalsIgnoreCase(strResourcIdOld)) && jj == 0) { //i % 2 == 0 &&
							cell.setCellValue("");
						}
						if (jj == 0) {
							cellStyle.setBorderTop(CellStyle.BORDER_NONE);
						}
						if (strResourcIdNew!=null && !strResourcIdNew.equalsIgnoreCase(strResourcIdOld) && jj == 1) {
							cell.setCellValue(CF.getEmpNameMapByEmpId(con, strResourceId));
							cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
							resourceCount++;
						} else if ((strResourcIdNew==null || strResourcIdNew.equalsIgnoreCase(strResourcIdOld)) && jj == 1) {
							cell.setCellValue("");
						}
						
						if (jj == 1) {
							cellStyle.setBorderTop(CellStyle.BORDER_NONE);
						}
						
						if (j==0 && jj == 2) {
							cell.setCellValue("Onsite");
							cell.setCellStyle(cellStyle);
						}
						if (j==1 && jj == 2) {
							cell.setCellValue("Offsite");
							cell.setCellStyle(cellStyle);
						}
						
						
						if (j==0 && jj == 3) { //i % 2 == 0 && 
//							System.out.println("Task Name ===>>  "+ uF.showData((String)hmTasks1.get(strResourceId+"_"+strActivityId+"_T"), "-"));
							cell.setCellValue(uF.showData((String)hmTasks1.get(strResourceId+"_"+strActivityId+"_T"), "-"));
							cell.setCellStyle(cellStyle);
						}
						if (j==1 && jj == 3) { //i % 2 == 0 && 
//							System.out.println("Task Name ===>>  blank");
							cell.setCellValue("");
							cell.setCellStyle(cellStyle);
						}
//						
	//
						if (j==0 && jj == 4) { //i % 2 == 0 && 
							cell.setCellValue(uF.showData(hmTaskAndSubTaskDescription.get(strActivityId), "-"));
							cell.setCellStyle(cellStyle);
						}
						if (j==1 && jj == 4) { //i % 2 == 0 && 
							cell.setCellValue("");
							cell.setCellStyle(cellStyle);
						}
//						
						if (j==0 && jj == 5) {
							double tothrs = 0;
							int totday = 0;
							for(int stsk=0; alDates!=null && stsk<alDates.size(); stsk++) {
								String dayStatus= uF.showData((String)hmDatesBillableHrs.get((String)alDates.get(stsk)+"_"+sb.toString()), "0");
								if(uF.parseToDouble(dayStatus)>0) {
									totday++;
									tothrs += uF.parseToDouble(dayStatus);
								}
							}
							cell.setCellValue(uF.getTotalTimeMinutes100To60(tothrs+""));
							cell.setCellStyle(cellStyle);
						}
						if (j==1 && jj == 5) {
							double tothrs = 0;
							int totday = 0;
							for(int stsk=0; alDates!=null && stsk<alDates.size(); stsk++) {
								String dayStatus= uF.showData((String)hmDatesBillableHrs.get((String)alDates.get(stsk)+"_"+sb.toString()), "0");
								if(uF.parseToDouble(dayStatus)>0) {
									totday++;
									tothrs += uF.parseToDouble(dayStatus);
								}
							}
							cell.setCellValue(uF.getTotalTimeMinutes100To60(tothrs+""));
							cell.setCellStyle(cellStyle);
						}
						
						if (j==0 && jj > 5) {
							
							String strText = "-";
							
//							String isApproved = uF.showData((String)hmDateHrsIsApproved.get((String)alDates.get(dateCnt)+"_"+sb.toString()), strText);
//							String isBillableApproved = uF.showData((String)hmDateBillableHrsIsApproved.get((String)alDates.get(dateCnt)+"_"+sb.toString()), strText);
							
//							String strBgColor = null;
//							if(alEmpCheckRosterWeektype.contains(strResourceId)) {
//								if(rosterWeeklyOffSet.contains(alDates.get(dateCnt))) {
//									strBgColor =IConstants.WEEKLYOFF_COLOR;
//								}
//							} else if(weeklyOffSet.contains(alDates.get(dateCnt))) {
//								strBgColor =IConstants.WEEKLYOFF_COLOR;
//							}
//							if(strBgColor!=null) {
//								strText = "W/O";
//							}
//							
//							if(strBgColor==null) {
//								strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(dateCnt), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
//								strText = "H";
//							}
//							if(strBgColor==null) {
//								strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(dateCnt)));
//								//strText = (String)hmLeaves.get((String)alDates.get(i));
//								strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(dateCnt))); 
//							}
//							
//							if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(dateCnt).toString(), IConstants.DATE_FORMAT))) {
//								strBgColor = "#EFEFEF";
//							}
//							
//							if(strText==null) {
//								strText = "-";
//							}
							
							String dayStatus= uF.showData((String)hmDatesBillableHrs.get((String)alDates.get(dateCnt)+"_"+sb.toString()), strText);
							double dblBillableHrs = uF.parseToDouble((String)hmDatesBillableHrs.get((String)alDates.get(dateCnt)+"_"+sb.toString()));
							dblBillableHrs += uF.parseToDouble((String)hmTotalBillableHrs.get((String)alDates.get(dateCnt)));
							hmTotalBillableHrs.put((String)alDates.get(dateCnt), uF.formatIntoTwoDecimal(dblBillableHrs)+"");
//							System.out.println("dayStatus OFS ===>> " +dayStatus+"  alDates.get(dateCnt) ===>> " + alDates.get(dateCnt));
//							Map<String, Map<String, String>> hmactualhrs = (Map<String, Map<String, String>>) projectdetails.get(alclient_namelist.get(dayrowcount - 1));
							if(uF.parseToDouble(dayStatus) > 0) {
								dayStatus = uF.getTotalTimeMinutes100To60(dayStatus);
							}
							cell.setCellValue(dayStatus);
							cell.setCellStyle(cellStyle);
							if(dateCnt < alDates.size()-1) {
								dateCnt++;
							}
						}
						
						
						if (j==1 && jj > 5) {
							
							String strText = "-";
							
//							String isApproved = uF.showData((String)hmDateHrsIsApproved.get((String)alDates.get(dateCnt1)+"_"+sb.toString()), strText);
//							String isBillableApproved = uF.showData((String)hmDateBillableHrsIsApproved.get((String)alDates.get(dateCnt1)+"_"+sb.toString()), strText);
							
//							String strBgColor = null;
//							if(alEmpCheckRosterWeektype.contains(strResourceId)) {
//								if(rosterWeeklyOffSet.contains(alDates.get(dateCnt1))) {
//									strBgColor =IConstants.WEEKLYOFF_COLOR;
//								}
//							} else if(weeklyOffSet.contains(alDates.get(dateCnt1))) {
//								strBgColor =IConstants.WEEKLYOFF_COLOR;
//							}
//							if(strBgColor!=null) {
//								strText = "W/O";
//							}
//							
//							if(strBgColor==null) {
//								strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(dateCnt1), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
//								strText = "H";
//							}
//							if(strBgColor==null) {
//								strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(dateCnt1)));
//								//strText = (String)hmLeaves.get((String)alDates.get(i));
//								strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(dateCnt1))); 
//							}
//							
//							if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(dateCnt1).toString(), IConstants.DATE_FORMAT))) {
//								strBgColor = "#EFEFEF";
//							}
//							
//							if(strText==null) {
//								strText = "-";
//							}
							
							String dayStatus= uF.showData((String)hmDatesBillableHrs.get((String)alDates.get(dateCnt1)+"_"+sb.toString()), strText);
							double dblBillableHrs = uF.parseToDouble((String)hmDatesBillableHrs.get((String)alDates.get(dateCnt1)+"_"+sb.toString()));
							dblBillableHrs += uF.parseToDouble((String)hmTotalBillableHrs.get((String)alDates.get(dateCnt1)));
							hmTotalBillableHrs.put((String)alDates.get(dateCnt1), uF.formatIntoTwoDecimal(dblBillableHrs)+"");
							if(uF.parseToDouble(dayStatus) > 0) {
								dayStatus = uF.getTotalTimeMinutes100To60(dayStatus);
							}
//							System.out.println("dayStatus ONS ===>> " +dayStatus +"  alDates.get(dateCnt1) ===>> " + alDates.get(dateCnt1));
//							Map<String, Map<String, String>> hmactualhrs = (Map<String, Map<String, String>>) projectdetails.get(alclient_namelist.get(dayrowcount - 1));
							cell.setCellValue(dayStatus);
							cell.setCellStyle(cellStyle);
							if(dateCnt1 < alDates.size()-1) {
								dateCnt1++;
							}
						}
						
						cell.setCellStyle(cellStyle);
							}
					strResourcIdOld = strResourcIdNew;
					}
					
			}
						
		}
				}
				
	}
		
			int dateCnt = 0;
			i++;
			Row grandTotRow = monthly_time_sheet.createRow(i);
			for (int ii = 0; ii < 6 + nDateDiff; ii++) {
				HSSFCellStyle cellStyle = workbook.createCellStyle();
				cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
				cellStyle.setBorderTop(CellStyle.BORDER_THIN);
				cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
				cellStyle.setBorderRight(CellStyle.BORDER_THIN);
				Cell cell = grandTotRow.createCell(ii);
				if (ii == 3) {
					Font font = workbook.createFont();
					font.setBoldweight((short) 1000);
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cell.setCellValue("Total Billable Hrs");
					cellStyle.setFont(font);
					cell.setCellStyle(cellStyle);
				}
				if (ii > 5) {
					cell.setCellValue(uF.showData(uF.getTotalTimeMinutes100To60((String)hmTotalBillableHrs.get((String)alDates.get(dateCnt))), "-"));
					cell.setCellStyle(cellStyle);
					if(dateCnt < alDates.size()-1) {
						dateCnt++;
					}
				}
				cell.setCellStyle(cellStyle);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	

	
	private Map<String, Map<String, String>> getLeaveDetails(String strDate1, String strDate2, UtilityFunctions uF, Map<String, String> hmLeavesCode) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, Map<String, String>> getMap = new HashMap<String, Map<String, String>>();
		try{
			con=db.makeConnection(con);
			pst=con.prepareStatement("select lar.*,lt.leave_type_code from leave_application_register lar,leave_type lt where lar.leave_type_id=lt.leave_type_id and is_modify = false and _date between ? and ? order by emp_id");
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate2, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs=pst.executeQuery();
			
			while(rs.next()) {
				Map<String, String> a = getMap.get(rs.getString("emp_id"));
				if(a == null) a = new HashMap<String, String>(); 
				
//				a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("leave_type_code"));
				a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("leave_type_id"));
				getMap.put(rs.getString("emp_id"), a);
				hmLeavesCode.put(rs.getString("leave_type_id"), rs.getString("leave_type_code"));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return getMap;
	}
	
	
	// // filling all cell values from database ....
//	public void fillsheet(Map<String, Map<String, Map<String, String>>> projectdetails, List<String> nameheaderlist, List<Integer> leavesdaylist, List<Integer> holidayslist,
//			Map<String, Map<String, String>> hmfinaltask, List<String> idList)
//			throws SQLException {
//
//		Connection con = null;
//
//		ResultSet rs = null;
//		ResultSet rs1 = null;
//		Database db = new Database();
//		db.setRequest(request);
//		con = db.makeConnection(con);
//		PreparedStatement pst = null;
//		session = request.getSession();
//		UtilityFunctions uF = new UtilityFunctions();
//		CF = (CommonFunctions) session.getAttribute("CF");
//		UtilityFunctions UF = new UtilityFunctions();
//		// ******** LEAVES.......
//
//		 pst = con.prepareStatement("select epd.emp_fname,epd.emp_lname,epd.empcode,emp_bank_acct_nbr,emp_pan_no,di.dept_name from employee_personal_details epd join employee_official_details eod on(epd.emp_per_id=eod.emp_id) join department_info di on(eod.depart_id=di.dept_id) where epd.emp_per_id=?");
//		pst.setInt(1, UF.parseToInt(getEmpid()));
//		rs1 = pst.executeQuery();
//
//		while (rs1.next()) {
//			nameheaderlist.add(rs1.getString("emp_fname") + " "+rs1.getString("emp_lname"));
//			nameheaderlist.add(rs1.getString("empcode"));
//			nameheaderlist.add(rs1.getString("emp_bank_acct_nbr"));
//			nameheaderlist.add(rs1.getString("emp_pan_no"));
//			nameheaderlist.add(rs1.getString("dept_name"));
//
//		}
//		rs1.close();
//		pst.close();
//		
//		try {
//
//			pst = con.prepareStatement("select client_name,task_location,task_date,SUM(actual_hrs) as actual_hrs from (select clnt.client_name,pmnt.client_id ,ai.pro_id,ai.activity_name,ta.activity_id,ta.task_date,ta.actual_hrs,task_location from task_activity ta left join activity_info ai on ta.activity_id=ai.task_id left join projectmntnc pmnt on ai.pro_id=pmnt.pro_id left join client_details clnt on clnt.client_id=pmnt.client_id where ta.emp_id=? and task_date>=? and task_date<=?) as a  group by client_name,task_date,task_location order by client_name");
//			pst.setInt(1, UF.parseToInt(getEmpid()));
//			pst.setDate(2, UF.getDateFormat(getDatefrom(), DATE_FORMAT));
//			pst.setDate(3, UF.getDateFormat(getDateto(), DATE_FORMAT));
//			rs = pst.executeQuery();
//
//			while (rs.next()) {
//				Map<String, Map<String, String>> a = projectdetails.get(rs.getString("client_name"));
//				if (a == null)
//					a = new HashMap<String, Map<String, String>>();
//
//				if (rs.getString("task_location") != null && rs.getString("task_location").equalsIgnoreCase("ONS")) {
//					Map<String, String> b = a.get(rs.getString("task_location"));
//					if (b == null)
//						b = new HashMap<String, String>();
//					b.put(rs.getString("task_date"), uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rs.getString("actual_hrs"))));
//					a.put(rs.getString("task_location"), b);
//					projectdetails.put(rs.getString("client_name"), a);
//
//				} else if (rs.getString("task_location") == null || rs.getString("task_location").equalsIgnoreCase("OFS") || rs.getString("task_location").equalsIgnoreCase("")) {
//					Map<String, String> b = a.get("OFS");
//					if (b == null)
//						b = new HashMap<String, String>();
//					b.put(rs.getString("task_date"), uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rs.getString("actual_hrs"))));
//					a.put("OFS", b);
//					projectdetails.put(rs.getString("client_name"), a);
//				}
//
//			}
//			rs.close();
//			pst.close();
//
//			if (projectdetails != null) {
//				String strquerytask = "select client_name,activity_name, task_location , ta.activity from activity_info ai right join projectmntnc using(pro_id) right join client_details using(client_id) right join task_activity ta on ta.activity_id=ai.task_id  where ta.emp_id=? group by client_name,activity_name, ta.activity,task_location order by client_name ";
//				pst = con.prepareStatement(strquerytask);
//				pst.setInt(1, UF.parseToInt(getEmpid()));
//				rs = pst.executeQuery();
//
//				while (rs.next()) {
//					Map<String, String> innerhmfinaltask = hmfinaltask.get(rs.getString("client_name"));
//					if (innerhmfinaltask == null) {
//						innerhmfinaltask = new HashMap<String, String>();
//						if(rs.getString("activity_name")!=null){
//							innerhmfinaltask.put(rs.getString("task_location"), rs.getString("activity_name"));
//						}else{
//							innerhmfinaltask.put(rs.getString("task_location"), rs.getString("activity"));
//						}
//						
//						hmfinaltask.put(rs.getString("client_name"), innerhmfinaltask);
//					} else {
//						if (innerhmfinaltask.keySet().contains(rs.getString("task_location"))) {
//							String tasknew = innerhmfinaltask.get(rs.getString("task_location"));
//							
//							if(rs.getString("activity_name")!=null){
//								tasknew = tasknew + "," + rs.getString("activity_name");
//							}else{
//								tasknew = tasknew + "," + rs.getString("activity");
//							}
//							
//							innerhmfinaltask.put(rs.getString("task_location"), tasknew);
//						} else {
//							if(rs.getString("activity_name")!=null){
//								innerhmfinaltask.put(rs.getString("task_location"), rs.getString("activity_name"));
//							}else{
//								innerhmfinaltask.put(rs.getString("task_location"), rs.getString("activity"));
//							}
//							
//							hmfinaltask.put(rs.getString("client_name"), innerhmfinaltask);
//
//						}
//
//					}
//				}
//
//			}
//			// conveyance sheet statement
//			
//			// making conveyance others"""""
//
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeResultSet(rs1);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//
//		}
//
//		Calendar cal = GregorianCalendar.getInstance();
//		cal.set(Calendar.DAY_OF_MONTH, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "dd")));
//		cal.set(Calendar.MONTH, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "MM")) - 1);
//		cal.set(Calendar.YEAR, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "yyyy")));
//
//		Calendar cal1 = GregorianCalendar.getInstance();
//		cal1.set(Calendar.DAY_OF_MONTH, UF.parseToInt(UF.getDateFormat(getDateto(), DATE_FORMAT, "dd")));
//		cal1.set(Calendar.MONTH, UF.parseToInt(UF.getDateFormat(getDateto(), DATE_FORMAT, "MM")) - 1);
//		cal1.set(Calendar.YEAR, UF.parseToInt(UF.getDateFormat(getDateto(), DATE_FORMAT, "yyyy")));
//
//		coloumncountmain = daysBetween(cal.getTime(), cal1.getTime()); 
//
//	}

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
//
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
		for (int i = 0; i < 7 + coloumncountmain; i++) {
			HSSFCellStyle bottomBorder = workbook.createCellStyle();
			if (i == 0) {
				bottomBorder.setBorderLeft(CellStyle.BORDER_THIN);
			}
			if (i == coloumncountmain + 6) {
				bottomBorder.setBorderRight(CellStyle.BORDER_THIN);
			}
			Cell cell = row.createCell(i);
			cell.setCellStyle(bottomBorder);
		}
	}

	public String getReportName() {
		String name = "Timesheet_" + getProName() + "_" + getFrmDate() + "_" + getToDate();
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

	public String getProFreqId() {
		return proFreqId;
	}

	public void setProFreqId(String proFreqId) {
		this.proFreqId = proFreqId;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public String getFrmDate() {
		return frmDate;
	}

	public void setFrmDate(String frmDate) {
		this.frmDate = frmDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public List<Date> getholidayslisttimesheet(String strWlocationId) {

		UtilityFunctions UF = new UtilityFunctions();
		List<Date> dateList = new ArrayList<Date>();
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			Calendar cal3 = GregorianCalendar.getInstance();
			cal3.set(Calendar.DAY_OF_MONTH, UF.parseToInt(UF.getDateFormat(getFrmDate(), DATE_FORMAT, "dd")));
			cal3.set(Calendar.MONTH, UF.parseToInt(UF.getDateFormat(getFrmDate(), DATE_FORMAT, "MM")) - 1);
			cal3.set(Calendar.YEAR, UF.parseToInt(UF.getDateFormat(getFrmDate(), DATE_FORMAT, "yyyy")));

			
			con = db.makeConnection(con);

			Map hmWeekendMap = CF.getWeekEndDateList(con, getFrmDate(), getDateto(), CF, UF,null,null);

			Set<String> set = new HashSet<String>();

			Iterator<String> itr = hmWeekendMap.keySet().iterator();
			while (itr.hasNext()) {
				String temp = itr.next().toString();
				/*if (temp.substring(11, 14).equals(strWlocationId)) {
					set.add(temp.substring(0, 10));
				}*/
				
				
				if (temp!=null && temp.indexOf(strWlocationId)>=0) {
					set.add(temp.substring(0, (temp.length()>10 ? 10 : temp.length())));
				}
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

			Date datefromcheck = dateFormat.parse(getFrmDate());
			Date datetocheck = dateFormat.parse(getDateto());
			itr = set.iterator();

			while (itr.hasNext()) {
				Date finaldate = dateFormat.parse(itr.next().toString());
				if (UF.isDateBetween(datefromcheck, datetocheck, finaldate)) {
					dateList.add(finaldate);
				}

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		return dateList;
	}

}










/*
package com.konnect.jpms.task;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

public class GenerateProjectwiseTimesheet implements ServletRequestAware, ServletResponseAware, IStatements, IConstants {

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
//	HSSFSheet conveyance_sheet;
	HSSFWorkbook workbook;

	{
		workbook = new HSSFWorkbook();
		monthly_time_sheet = workbook.createSheet("Time Report");
//		conveyance_sheet = workbook.createSheet("Conveyance Sheet");
	}

	public String execute() {

		
		Map<String, Map<String, Map<String, String>>> projectdetails = new LinkedHashMap<String, Map<String, Map<String, String>>>();
		List<String> nameheaderlist = new ArrayList<String>();
		List<Integer> leavesdaylist = new ArrayList<Integer>();
		List<Integer> holidayslist = new ArrayList<Integer>();
		Map<String, Map<String, String>> hmfinaltask = new HashMap<String, Map<String, String>>();
		List<String> idList = new ArrayList<String>();
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
//				fillsheet(projectdetails, nameheaderlist, leavesdaylist, holidayslist, hmfinaltask, idList);
				createExcelFile(projectdetails, nameheaderlist, leavesdaylist, holidayslist, hmfinaltask, idList);
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

		return null;
	}

	int coloumncountmain = 0;
	String downloadSubmit;
//	String datefrom;
	String strPC;

	String proFreqId;
	String proId;
	
	String frmDate;
	String toDate;
	
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
			pst.setDate(2, uF.getDateFormat(getFrmDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getToDate(), DATE_FORMAT));
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
			pst.setDate(9, uF.getDateFormat(getFrmDate(), DATE_FORMAT));
			pst.setDate(10, uF.getDateFormat(getToDate(), DATE_FORMAT));
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

	public void createExcelFile(Map<String, Map<String, Map<String, String>>> projectdetails, List<String> nameheaderlist, List<Integer> leavesdaylist, List<Integer> holidayslist,
			Map<String, Map<String, String>> hmfinaltask, List<String> idList)
			throws IOException {
		FileOutputStream fos = null;
		try {
//			writeTimeSheetReport(projectdetails, nameheaderlist, leavesdaylist, holidayslist, hmfinaltask, idList);
			fillTaskRows();
			String reportName = getReportName(); // get report name as per
													// client requirement.

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

	
	
	
	// *************** Project Timesheet Data *******************************
	
	
	public void fillTaskRows() {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("=========>> 1 fillTaskRow ");
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select pf.*,p.pro_id,actual_calculation_type from projectmntnc p, projectmntnc_frequency pf " +
				"where p.pro_id = pf.pro_id and pf.pro_freq_id=?");
			pst.setInt(1, uF.parseToInt(getProFreqId()));
			rs = pst.executeQuery();
			Map<String, String> hmProjectData = new HashMap<String, String>();
			while(rs.next()) {
				hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
				hmProjectData.put("PRO_FREQ_START_DATE", uF.getDateFormat(rs.getString("freq_start_date"), DBDATE, DATE_FORMAT));
				hmProjectData.put("PRO_FREQ_END_DATE", uF.getDateFormat(rs.getString("freq_end_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
		
			Map<String, String> hmProjectResourceMap = new HashMap<String, String>();
			Map<String, String> hmProjectResourceWLocationMap = new HashMap<String, String>();
			StringBuilder projectResources = new StringBuilder();
			pst = con.prepareStatement("select resource_ids from activity_info where pro_id = ? ");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				projectResources.append(rs.getString("resource_ids"));
			}
			rs.close();
			pst.close();
			
			List<String> alProResources = new ArrayList<String>();
			List<String> pResourceList = Arrays.asList(projectResources.toString().split(","));
			for(int a=0; pResourceList != null && a<pResourceList.size(); a++) {
				if(pResourceList.get(a) != null && !pResourceList.get(a).equals("")) {
					if(!alProResources.contains(pResourceList.get(a))) {
						alProResources.add(pResourceList.get(a));
					}
					hmProjectResourceWLocationMap.put(pResourceList.get(a), CF.getEmpWlocationId(con, uF, pResourceList.get(a)));
					hmProjectResourceMap.put(pResourceList.get(a), CF.getEmpNameMapByEmpId(con, pResourceList.get(a)));
				}
			}
			
			System.out.println("alProResources ===>> " + alProResources);
//			System.out.println("hmProjectResourceMap ===>> " + hmProjectResourceMap);
			request.setAttribute("alProResources", alProResources);
			
			Map<String, Map<String, String>> hmLeaveDays = new HashMap<String, Map<String, String>>();
			Map hmLeavesColour = new HashMap();
			CF.getLeavesColour(con, hmLeavesColour);
			
			Map<String, String> hmLeaveCode = new HashMap<String, String>();
			
			hmLeaveDays = getLeaveDetails(hmProjectData.get("PRO_FREQ_START_DATE"), hmProjectData.get("PRO_FREQ_END_DATE"), uF, hmLeaveCode);

			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekendMap = CF.getWeekEndDateList(con, hmProjectData.get("PRO_FREQ_START_DATE"), hmProjectData.get("PRO_FREQ_END_DATE"), CF, uF, hmWeekEndHalfDates, null);
//			String strWLocationId = hmEmpWLocation.get(getStrResourceId()); 
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, hmProjectData.get("PRO_FREQ_START_DATE"), hmProjectData.get("PRO_FREQ_END_DATE"), alEmpCheckRosterWeektype, hmRosterWeekEndDates, hmWeekendMap, hmEmpLevelMap, hmEmpWLocation, hmWeekEndHalfDates);
			

			Map hmHolidays = new HashMap();
			Map hmHolidayDates = new HashMap();
			CF.getHolidayList(con, hmProjectData.get("PRO_FREQ_START_DATE"), hmProjectData.get("PRO_FREQ_END_DATE"), CF,hmHolidayDates, hmHolidays, true);

			if (uF.getDateFormat(getFrmDate(), DATE_FORMAT).before(uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT)) || uF.getDateFormat(getFrmDate(), DATE_FORMAT).after(uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT))) {
				setFrmDate(hmProjectData.get("PRO_FREQ_START_DATE"));
			}

			if (uF.getDateFormat(getToDate(), DATE_FORMAT).before(uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT)) || uF.getDateFormat(getToDate(), DATE_FORMAT).after(uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT))) {
				setToDate(hmProjectData.get("PRO_FREQ_END_DATE"));
			}

			pst = con.prepareStatement("select sum(a.actual_hrs) as actual_hrs, sum(billable_hrs) as billable_hrs,ai.parent_task_id,a.task_date," +
				" a.task_location,a.emp_id from (select sum(actual_hrs) as actual_hrs, sum(billable_hrs) as billable_hrs, ta.activity_id," +
				" ta.task_location,ta.task_date, ta.emp_id from task_activity ta where task_date between ? and ? and (is_approved = 1 or is_approved = 2) and ta.activity_id in (" +
				" select task_id from activity_info where pro_id = ? and parent_task_id in (select task_id from activity_info where pro_id = ?))" +
				" group by ta.emp_id,ta.activity_id,ta.task_date,ta.task_location) as a, activity_info ai where a.activity_id = ai.task_id and " +
				" ai.pro_id = ? group by a.emp_id,ai.parent_task_id,a.task_date, a.task_location order by ai.parent_task_id");
//			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getProId()));
			pst.setInt(4, uF.parseToInt(getProId()));
			pst.setInt(5, uF.parseToInt(getProId()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
		
			Map<String, String> hmDateT = new HashMap<String, String>();
			Map<String, String> hmDateBillableHrsT = new HashMap<String, String>();
			Map<String, String> hmTaskIsBillableT = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmployeeT = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeBillableHrsT = new HashMap<String, Map<String, String>>();
			Map<String, String> hmTasksT = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmployeeTasksT = new LinkedHashMap<String, Map<String, String>>();
		
			Map<String, String> hmEmployeeCountT = new HashMap<String, String>();
			Map<String, String> hmEmployeeBillCountT = new HashMap<String, String>();
		
			int nCountT = 0;
			int nBillCountT = 0;
			double dblTotalHrsT = 0;
			double dblTotalBillableHrsT = 0;
			boolean isBillableT = false;
			String strActivityIdNewT = null;
			String strActivityIdOldT = null;
			String strEmpIdNewT = null;
			String strEmpIdOldT = null;
			while (rs.next()) {
		
				strActivityIdNewT = rs.getString("parent_task_id");
				strEmpIdNewT = rs.getString("emp_id");
				
				if (strEmpIdNewT != null && !strEmpIdNewT.equalsIgnoreCase(strEmpIdOldT)) {
					nCountT = 0;
					nBillCountT = 0;
					isBillableT = false;
					strActivityIdOldT = null;
				} else if (strEmpIdNewT == null && strEmpIdOldT != null) {
					nCountT = 0;
					nBillCountT = 0;
					isBillableT = false;
					strActivityIdOldT = null;
				}
				
				if (strActivityIdNewT != null && !strActivityIdNewT.equalsIgnoreCase(strActivityIdOldT)) {
					dblTotalHrsT = 0;
					dblTotalBillableHrsT = 0;
					hmDateT = new HashMap<String, String>();
					hmDateBillableHrsT = new HashMap<String, String>();
					hmTasksT = new HashMap<String, String>();
					nCountT++;
				}
		
				if (strActivityIdNewT != null && !strActivityIdNewT.equalsIgnoreCase(strActivityIdOldT) && uF.parseToDouble(rs.getString("billable_hrs")) > 0) {
					nBillCountT++;
					isBillableT = true;
				} else if (!isBillableT && strActivityIdNewT != null && strActivityIdNewT.equalsIgnoreCase(strActivityIdOldT) && uF.parseToDouble(rs.getString("billable_hrs")) > 0) {
					nBillCountT++;
					isBillableT = true;
				}
				
				double dblHrs = uF.parseToDouble((String) hmDateT.get(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_" + rs.getString("task_location")));
				dblTotalHrsT = dblHrs+ uF.parseToDouble(rs.getString("actual_hrs"));
		
				hmDateT.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimal(dblTotalHrsT));
				
				double dblBillableHrs = uF.parseToDouble((String) hmDateBillableHrsT.get(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_" + rs.getString("task_location")));
				dblTotalBillableHrsT = dblBillableHrs + uF.parseToDouble(rs.getString("billable_hrs"));
		
				hmDateBillableHrsT.put(uF.getDateFormat(rs.getString("task_date"), DBDATE,DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimal(dblTotalBillableHrsT));
				
				hmEmployeeT.put(strEmpIdNewT+"_"+strActivityIdNewT, hmDateT);
		
				hmEmployeeBillableHrsT.put(strEmpIdNewT+"_"+strActivityIdNewT, hmDateBillableHrsT);
				
				hmTaskIsBillableT.put(strEmpIdNewT+"_"+strActivityIdNewT, isBillableT+"");
				
				hmTasksT.put(strEmpIdNewT+"_"+strActivityIdNewT + "_T", CF.getProjectTaskNameByTaskId(con, uF, rs.getString("parent_task_id")));
					
				hmTasksT.put(strEmpIdNewT+"_"+strActivityIdNewT + "_E", rs.getString("emp_id"));
				hmEmployeeTasksT.put(strEmpIdNewT+"_"+strActivityIdNewT, hmTasksT);
		
				hmEmployeeCountT.put(rs.getString("emp_id"), nCountT + "");
				hmEmployeeBillCountT.put(rs.getString("emp_id"), nBillCountT + "");
				
		//		System.out.println("hmProjectBillCount ===>>> " + hmProjectBillCount); 
				
				strActivityIdOldT = strActivityIdNewT;
				strEmpIdOldT = strEmpIdNewT;
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmProjectsT ===>> " + hmProjectsT);
//			System.out.println("hmEmployeeCountT ===>> " + hmEmployeeCountT);
//			System.out.println("hmEmployeeBillCountT ===>> " + hmEmployeeBillCountT);
			
			request.setAttribute("hmEmployeeT", hmEmployeeT);
			request.setAttribute("hmEmployeeBillableHrsT", hmEmployeeBillableHrsT);
			request.setAttribute("hmTaskIsBillableT", hmTaskIsBillableT);
			request.setAttribute("hmEmployeeTasksT", hmEmployeeTasksT);
			
			request.setAttribute("hmEmployeeCountT", hmEmployeeCountT);
			request.setAttribute("hmEmployeeBillCountT", hmEmployeeBillCountT);
			
			
			
			pst = con.prepareStatement("select actual_hrs, activity_id, ta.emp_id, task_date, activity_name, activity, task_location, billable_hrs, " +
				"is_billable, is_approved, ta.task_id, ta._comment,is_billable_approved from task_activity ta left join activity_info ai " +
				"on ta.activity_id = ai.task_id where ai.pro_id =? and task_date between ? and ? and (is_approved = 1 or is_approved = 2) order by ta.emp_id, activity_id desc, activity");
			pst.setInt(1, uF.parseToInt(getProId()));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();

			Map<String, String> hmDate = new HashMap<String, String>();
			Map<String, String> hmDateHrsIsApproved = new HashMap<String, String>();
			Map<String, String> hmDateBillableHrsIsApproved = new HashMap<String, String>();
			Map<String, String> hmDateTaskId = new HashMap<String, String>();
			Map<String, String> hmDateBillableHrs = new HashMap<String, String>();
			Map<String, String> hmTaskIsBillable = new HashMap<String, String>();
			Map<String, String> hmTaskDescri = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmployee = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeHrsIsApproved = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeBillableHrsIsApproved = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeTaskId = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeBillableHrs = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmEmployeeTaskDescri = new HashMap<String, Map<String, String>>();

			Map<String, String> hmTasks = new HashMap<String, String>();
			Map hmEmployeeTasks = new LinkedHashMap();

			Map<String, String> hmEmployeeCount = new HashMap<String, String>();
			Map<String, String> hmEmployeeBillCount = new HashMap<String, String>();

			int nCount = 0;
			int nBillCount = 0;
			double dblTotalHrs = 0;
			double dblTotalBillableHrs = 0;
			boolean isBillable = false;
			String strActivityIdNew = null;
			String strActivityIdOld = null;
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			while (rs.next()) {

				strActivityIdNew = rs.getString("activity_id");
				strEmpIdNew = rs.getString("emp_id");
				if (uF.parseToInt(strActivityIdNew) == 0) {
					strActivityIdNew = rs.getString("activity");
				}

				if (strEmpIdNew != null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					nCount = 0;
					nBillCount = 0;
					strActivityIdOld = null;
				} else if (strEmpIdNew == null && strEmpIdOld != null) {
					nCount = 0;
					nBillCount = 0;
					strActivityIdOld = null;
				}
				
				if (strActivityIdNew != null && !strActivityIdNew.equalsIgnoreCase(strActivityIdOld)) {
					dblTotalHrs = 0;
					dblTotalBillableHrs = 0;
					isBillable = false;
					hmDate = new HashMap<String, String>();
					hmDateBillableHrs = new HashMap<String, String>();
					hmDateHrsIsApproved = new HashMap<String, String>();
					hmDateBillableHrsIsApproved = new HashMap<String, String>();
					hmDateTaskId = new HashMap<String, String>();
					hmTasks = new HashMap<String, String>();
					nCount++;
				}

				if(!isBillable) {
					isBillable = rs.getBoolean("is_billable");
					if(isBillable) {
						nBillCount++;
					}
				}
				
				double dblHrs = uF.parseToDouble((String) hmDate.get(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_" + rs.getString("task_location")));
				dblTotalHrs = dblHrs+ uF.parseToDouble(rs.getString("actual_hrs"));

				hmDate.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimal(dblTotalHrs));
				
				double dblBillableHrs = uF.parseToDouble((String) hmDateBillableHrs.get(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_" + rs.getString("task_location")));
				dblTotalBillableHrs = dblBillableHrs + uF.parseToDouble(rs.getString("billable_hrs"));

				hmDateBillableHrs.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), uF.formatIntoTwoDecimal(dblTotalBillableHrs));
				
				hmDateHrsIsApproved.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("is_approved"));
				
				hmDateBillableHrsIsApproved.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("is_billable_approved"));
				
				hmDateTaskId.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("task_id"));
				
				hmTaskDescri.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT)+ "_"+ rs.getString("task_location"), rs.getString("_comment"));

				hmEmployee.put(strEmpIdNew+"_"+strActivityIdNew, hmDate);

				hmEmployeeBillableHrs.put(strEmpIdNew+"_"+strActivityIdNew, hmDateBillableHrs);
				
				hmEmployeeHrsIsApproved.put(strEmpIdNew+"_"+strActivityIdNew, hmDateHrsIsApproved);
				
				hmEmployeeBillableHrsIsApproved.put(strEmpIdNew+"_"+strActivityIdNew, hmDateBillableHrsIsApproved);
				
				hmEmployeeTaskDescri.put(strEmpIdNew+"_"+strActivityIdNew, hmTaskDescri);
				
				hmEmployeeTaskId.put(strEmpIdNew+"_"+strActivityIdNew, hmDateTaskId);
				
				hmTaskIsBillable.put(strEmpIdNew+"_"+strActivityIdNew, isBillable+"");
				
				if (uF.parseToInt(rs.getString("activity_id")) == 0) {
					hmTasks.put(strEmpIdNew+"_"+strActivityIdNew + "_T",rs.getString("activity"));
				} else {
					hmTasks.put(strEmpIdNew+"_"+strActivityIdNew + "_T",rs.getString("activity_name"));
				}

				hmTasks.put(strEmpIdNew+"_"+strActivityIdNew + "_E", rs.getString("emp_id"));
				hmEmployeeTasks.put(strEmpIdNew+"_"+strActivityIdNew, hmTasks);

				hmEmployeeCount.put(rs.getString("emp_id"), nCount + "");
				hmEmployeeBillCount.put(rs.getString("emp_id"), nBillCount + "");
				
//				System.out.println("hmProjectBillCount ===>>> " + hmProjectBillCount); 
				
				strActivityIdOld = strActivityIdNew;
				strEmpIdOld = strEmpIdNew;
			}
			
			rs.close();
			pst.close();
			
//			System.out.println("hmEmployeeBillableHrsIsApproved ===>>> " + hmEmployeeBillableHrsIsApproved);

			request.setAttribute("hmEmployee", hmEmployee);
			request.setAttribute("hmEmployeeBillableHrs", hmEmployeeBillableHrs);
			request.setAttribute("hmEmployeeHrsIsApproved", hmEmployeeHrsIsApproved);
			request.setAttribute("hmEmployeeBillableHrsIsApproved", hmEmployeeBillableHrsIsApproved);
			
			request.setAttribute("hmEmployeeTaskDescri", hmEmployeeTaskDescri);
			request.setAttribute("hmEmployeeTaskId", hmEmployeeTaskId);
			request.setAttribute("hmTaskIsBillable", hmTaskIsBillable);
			request.setAttribute("hmEmployeeTasks", hmEmployeeTasks);
			
			request.setAttribute("hmEmployeeCount", hmEmployeeCount);
			request.setAttribute("hmEmployeeBillCount", hmEmployeeBillCount);
			
			
			List<String> alTaskIds = new ArrayList<String>();
			
			pst = con.prepareStatement("select task_id, pro_id, activity_name, parent_task_id from activity_info ai where pro_id=?");
			pst.setInt(1, uF.parseToInt(getProId()));
			rs = pst.executeQuery();
			Map<String, List<String>> hmTaskAndSubTaskIds = new LinkedHashMap<String, List<String>>();
			List<String> alSubTaskIds = new ArrayList<String>();
//			List<String> alTaskIds = new ArrayList<String>();
			while(rs.next()) {
				if(rs.getInt("parent_task_id") > 0) {
					alSubTaskIds = hmTaskAndSubTaskIds.get(rs.getString("parent_task_id"));
					if(alSubTaskIds == null) alSubTaskIds = new ArrayList<String>();
					alSubTaskIds.add(rs.getString("task_id"));
					hmTaskAndSubTaskIds.put(rs.getString("parent_task_id"), alSubTaskIds);
				}
				if(rs.getInt("parent_task_id") == 0) {
					alTaskIds.add(rs.getString("task_id"));
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alTaskIds", alTaskIds);
			request.setAttribute("hmTaskAndSubTaskIds", hmTaskAndSubTaskIds);
			
			
			StringBuilder sbTasks = new StringBuilder();

			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(getFrmDate(),DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getFrmDate(),DATE_FORMAT, "yyyy")));

//			System.out.println("nDateCount======>"+nDateCount);
//			System.out.println("joiningDate======>"+joiningDate);
			
			String currDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
			
			request.setAttribute("currDate", currDate);
			
			request.setAttribute("sbTasks", sbTasks.toString());
			request.setAttribute("hmProjectResourceMap", hmProjectResourceMap);
			request.setAttribute("hmProjectResourceWLocationMap", hmProjectResourceWLocationMap);
			
			int nDateDiff = uF.parseToInt(uF.dateDifference(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT, hmProjectData.get("PRO_FREQ_END_DATE"), DATE_FORMAT, CF.getStrTimeZone()));

			cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(hmProjectData.get("PRO_FREQ_START_DATE"), DATE_FORMAT, "yyyy")));
			List<String> alDates = new ArrayList<String>();
			for (int i = 0; i < nDateDiff; i++) {
				alDates.add(uF.getDateFormat(cal.get(Calendar.DATE) + "/"
					+ (cal.get(Calendar.MONTH) + 1) + "/"
					+ cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
				cal.add(Calendar.DATE, 1);

				if (uF.getDateFormat(cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT).equalsIgnoreCase(hmProjectData.get("PRO_FREQ_END_DATE"))) {
					alDates.add(uF.getDateFormat( cal.get(Calendar.DATE) + "/"
					+ (cal.get(Calendar.MONTH) + 1) + "/"
					+ cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
					break;
				}
			}

			request.setAttribute("hmLeaveDays", hmLeaveDays);
			request.setAttribute("hmWeekendMap", hmWeekendMap);
			request.setAttribute("hmHolidayDates", hmHolidayDates);
			request.setAttribute("hmLeavesColour", hmLeavesColour);
//			request.setAttribute("hmLeaveConstant", hmLeaveConstant);

			request.setAttribute("timesheet_title", "Timesheet details from " + hmProjectData.get("PRO_FREQ_START_DATE") + " to " + hmProjectData.get("PRO_FREQ_END_DATE"));
			request.setAttribute("alDates", alDates);

//			request.setAttribute("sbTaskStatus", sbTaskStatus);
			request.setAttribute("hmLeaveCode", hmLeaveCode);
			
			request.setAttribute("hmRosterWeekEndDates", hmRosterWeekEndDates);
			request.setAttribute("alEmpCheckRosterWeektype", alEmpCheckRosterWeektype);

			
			
// ************************************ Create excel sheeet of pro timesheet ***************************************************			

			int lessspace = 0;
			if(nDateDiff < 10){
				lessspace = 1;
			}
			Row firmNameRow = monthly_time_sheet.createRow(0);
			firmNameRow.setHeight((short) 450);
			monthly_time_sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 10));
			for (int i = 0; i < 7 + nDateDiff; i++) {
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
				if (i == 6 + nDateDiff) {
					styleForFirmName.setBorderRight(CellStyle.BORDER_THIN);
				}
				firmNameCell.setCellStyle(styleForFirmName);
			}

			Row firmTypeRow = monthly_time_sheet.createRow(1);
			monthly_time_sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 10));
			for (int i = 0; i < 7 + nDateDiff; i++) {
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
				if (i == 6 + nDateDiff) {
					styleForFirmType.setBorderRight(CellStyle.BORDER_THIN);
				}
				firmTypeCell.setCellStyle(styleForFirmType);
			}

			Row firstBlankRow = monthly_time_sheet.createRow(2);
			forBlankRowLeftAndRightBorder(firstBlankRow);

			Row clientNameRow = monthly_time_sheet.createRow(3);
			HSSFCellStyle rightBorderForClientSummary = workbook.createCellStyle();
			rightBorderForClientSummary.setBorderRight(CellStyle.BORDER_THIN);
			HSSFCellStyle clientSummary = workbook.createCellStyle();

			Font clientNameCellFont = workbook.createFont();
			clientNameCellFont.setFontHeight((short) 230);

			Cell clientNameCell = clientNameRow.createCell(0);
			clientNameCell.setCellValue("Project Name: " + hmProjectData.get("PRO_NAME"));
			clientSummary.setFont(clientNameCellFont);
			clientSummary.setBorderLeft(CellStyle.BORDER_THIN);
			clientNameCell.setCellStyle(clientSummary);

			clientSummary.setFont(clientNameCellFont);
			clientSummary.setBorderLeft(CellStyle.BORDER_THIN);

			Cell clientBankACNoCell = clientNameRow.createCell(nDateDiff);
			clientBankACNoCell.setCellValue("");

			Cell lastCellOfClientSummary1 = clientNameRow.createCell(nDateDiff + 6);
			lastCellOfClientSummary1.setCellStyle(rightBorderForClientSummary);

			Row divisionRow = monthly_time_sheet.createRow(4);
			divisionRow.setHeight((short) 300);
			Cell clientDivisionCell = divisionRow.createCell(0);
			clientDivisionCell.setCellValue("Client Name:" + hmProjectData.get("PRO_CUSTOMER_NAME"));
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
				reportNameCell = divisionRow.createCell(6 + (nDateDiff / 2));
			}
			reportNameCell.setCellValue(hmProjectData.get("PRO_BILLING_FREQUENCY").toUpperCase()+ " TIME REPORT");
			reportNameCell.setCellStyle(cellStyleforReportName);

			Cell clientPANNoCell = divisionRow.createCell(nDateDiff);
			clientPANNoCell.setCellValue(" Project Owner: " + hmProjectData.get("PRO_OWNER_NAME"));

			Cell lastCellOfClientSummary2 = divisionRow.createCell(nDateDiff + 6);
			lastCellOfClientSummary2.setCellStyle(rightBorderForClientSummary);

			Row empCodeRow = monthly_time_sheet.createRow(5);
			Cell clientEmpIdCell = empCodeRow.createCell(0);
			clientEmpIdCell.setCellValue("SPOC Name: " + hmProjectData.get("PRO_CUST_SPOC_NAME"));
			clientEmpIdCell.setCellStyle(clientSummary);

			Cell clientWorkingHrsCell = empCodeRow.createCell(nDateDiff);
			clientWorkingHrsCell.setCellValue("");

			Cell lastCellOfClientSummary3 = empCodeRow.createCell(nDateDiff + 6);
			lastCellOfClientSummary3.setCellStyle(rightBorderForClientSummary);

			Row secondBlankRow = monthly_time_sheet.createRow(6);
			forBlankRowLeftAndRightBorder(secondBlankRow);

			Row headingRowDesc = monthly_time_sheet.createRow(7);
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
			nameOfClient.setCellValue("Resource Name ");

			monthly_time_sheet.autoSizeColumn((short) 1);
			nameOfClient.setCellStyle(headingStyle);
			monthly_time_sheet.addMergedRegion(new CellRangeAddress(12, 12, 1, 2));

			Cell assignment = headingRowDesc.createCell(3);
			assignment.setCellValue("Task/SubTask Name ");
			monthly_time_sheet.autoSizeColumn((short) 3);
			assignment.setCellStyle(headingStyle);

			Cell totalDays = headingRowDesc.createCell(4);
			totalDays.setCellValue("Task/SubTask Description");
			totalDays.setCellStyle(headingStyle);

			Cell conveyExp = headingRowDesc.createCell(5);
			conveyExp.setCellValue("Total Days");
			conveyExp.setCellStyle(headingStyle);

			for (int i = 6; i < 6 + nDateDiff; i++) {
				Cell cell = headingRowDesc.createCell(i);
				HSSFCellStyle cellStyleHeadingMonth = workbook.createCellStyle();
				Font font = workbook.createFont();
				font.setBoldweight((short) 1000);
				cellStyleHeadingMonth.setBorderTop(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setFont(font);

				if (i == 5) {
					cellStyleHeadingMonth.setBorderLeft(CellStyle.BORDER_THIN);
				}
				if (i == 4 + (nDateDiff / 2)) {
					cell.setCellValue("Month: " + getFrmDate() + " to " + getToDate());
					cell.setCellStyle(cellStyleHeadingMonth);
				}
				if (i == nDateDiff + 6) {
					cellStyleHeadingMonth.setBorderRight(CellStyle.BORDER_THIN);
				}
				cell.setCellStyle(cellStyleHeadingMonth);
			}

			Row headingRowNums = monthly_time_sheet.createRow(8); // 13
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

			Calendar cal1 = GregorianCalendar.getInstance();
			cal1.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "dd")));
			cal1.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "MM")) - 1);
			cal1.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "yyyy")));

			for (int i = 6, j = 1; i < 6 + nDateDiff; i++, j++) {
				System.out.println("cal1 =====>> " + cal1.get(Calendar.DAY_OF_MONTH));
				
				Cell cell = headingRowNums.createCell(i);
				HSSFCellStyle cellStyleHeadingMonth = workbook.createCellStyle();
				cellStyleHeadingMonth.setBorderTop(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setBorderBottom(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setBorderLeft(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setBorderRight(CellStyle.BORDER_THIN);
				cellStyleHeadingMonth.setAlignment(CellStyle.ALIGN_CENTER);
				cellStyleHeadingMonth.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
				cell.setCellValue(cal1.get(Calendar.DAY_OF_MONTH));
				monthly_time_sheet.setColumnWidth(i, 1200);
				cell.setCellStyle(cellStyleHeadingMonth);
				cal1.add(Calendar.DATE, 1);
			}

			
			
			Map hmTotal = new HashMap();
			Map hmTotalBillableHrs = new HashMap();
			StringBuilder sb = new StringBuilder();
			
			String strResourcIdNew = null;
			String strResourcIdOld = null;
			int nCount1 = 0;
			
			int linecount = 9 + (alProResources.size() * 2);
			int i = 8;
			int resourceCount = 0, taskcount = 0, dayscount = 0, dayrowcount = 0;
//			for (int i = 9, k = 1; i < linecount; i++, k++) {
			System.out.println("alProResources ===>> " + alProResources);
			int srCnt = 0;
			for(int b=0; alProResources != null && b<alProResources.size(); b++) {
				String strResourceId = alProResources.get(b);
				
				String strWLocationId = hmProjectResourceWLocationMap.get(strResourceId);
				Set<String> weeklyOffSet = hmWeekendMap.get(strWLocationId);
				if(weeklyOffSet==null) weeklyOffSet=new HashSet<String>();
				
				Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(strResourceId);
				if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
				
				Map<String, String> hmLeaves = hmLeaveDays.get(strResourceId);
				if(hmLeaves == null) hmLeaves = new HashMap<String, String>();
				
				System.out.println("alTaskIds ===>> " + alTaskIds);
				for(int a=0; alTaskIds != null && a<alTaskIds.size(); a++) {
					String strActivityId = alTaskIds.get(a);
					
					List<String> alSubTaskIds1 = new ArrayList<String>();
					
					if(hmTaskAndSubTaskIds != null) {
						alSubTaskIds1 = hmTaskAndSubTaskIds.get(strActivityId);
					}
					System.out.println("alSubTaskIds1 ===>> " + alSubTaskIds1);
					System.out.println("hmEmployeeTasks ===>> " + hmEmployeeTasks);
					
					Map hmTasks1 = (Map)hmEmployeeTasks.get(strResourceId+"_"+strActivityId);
					if(hmTasks1 == null)hmTasks1 = new HashMap();
					System.out.println("hmTasks1 ===>> " + hmTasks1);
					if((hmTasks1 == null || hmTasks1.isEmpty()) && (alSubTaskIds1 ==null || alSubTaskIds1.size()==0)) {
						//System.out.println("strActivityId empty ===>> " + strActivityId);
					} else {
						
						if(hmTasks1 == null || hmTasks1.isEmpty()) {
							i++;
							System.out.println("hmTasks1 ===>> " + hmTasks1 + " i ==>>> " + i);
							Map hmTasksT1 = (Map)hmEmployeeTasksT.get(strResourceId+"_"+strActivityId);
							if(hmTasksT1 == null)hmTasksT1 = new HashMap();
							//System.out.println("hmTasksT ===>> " + hmTasksT);
							
							if(hmTasksT1 != null && !hmTasksT1.isEmpty()) {
							String strResourcId = (String)hmTasksT1.get(strResourceId+"_"+strActivityId+"_E");
							strResourcIdNew = (String)hmTasksT1.get(strResourceId+"_"+strActivityId+"_E");
							//System.out.println("strActivityId hmTasks empty ===>> " + strActivityId);
							int nProCount = uF.parseToInt((String)hmEmployeeCount.get(strResourcId));
							int nProBillCount = uF.parseToInt((String)hmEmployeeBillCount.get(strResourcId));
							int nProCountT = uF.parseToInt((String)hmEmployeeCountT.get(strResourcId));
							int nProBillCountT = uF.parseToInt((String)hmEmployeeCountT.get(strResourcId));
							
							//System.out.println("strProjectIdNew ===>> " +strProjectIdNew + " strProjectIdOld ===>> " + strProjectIdOld);
							if(strResourcIdNew!=null && !strResourcIdNew.equalsIgnoreCase(strResourcIdOld)) {
								nCount1 = 0;
							} else if(strResourcIdNew == null) {
								strResourcIdNew = "";
								if(!strResourcIdNew.equalsIgnoreCase(strResourcIdOld)) {
									nCount1 = 0;
								}
							}
							nCount1++;
							
							Map<String, String> hmDatesT = (Map<String, String>)hmEmployeeT.get(strResourceId+"_"+strActivityId);
							if(hmDatesT == null)hmDatesT = new HashMap<String, String>();
							
							Map<String, String> hmDatesBillableHrsT = (Map<String, String>)hmEmployeeBillableHrsT.get(strResourceId+"_"+strActivityId);
							if(hmDatesBillableHrsT == null) hmDatesBillableHrsT = new HashMap<String, String>();
							
							for(int j=0; j<2; j++) {
								sb.replace(0, sb.length(), "");
								if(j==0) {
									sb.append("OFS"); 
								} else {
									sb.append("ONS");
								}
							
								if(j==0 && nCount==1) { 
									int rwspn = 2;
									int billrwspn = 0;
									if(nProBillCount>0) {
										billrwspn = nProBillCount * 2;
									}
									int billrwspnT = 0;
									if(nProBillCountT>0) {
										billrwspnT = nProBillCountT * 2;
									}
								}
								}
							
							
							
							
//				cal = GregorianCalendar.getInstance();
//				cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "dd")));
//				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "MM")) - 1);
//				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "yyyy")));

				Row row = monthly_time_sheet.createRow(i);

				for (int jj = 0; jj < 6 + nDateDiff; jj++) {
					HSSFCellStyle cellStyle = workbook.createCellStyle();
					cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
					cellStyle.setBorderTop(CellStyle.BORDER_THIN);
					cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
					cellStyle.setBorderRight(CellStyle.BORDER_THIN);

					Cell cell = row.createCell(jj);

					if (jj == 0) { //i % 2 == 0 && 
						cell.setCellValue(srCnt);
						cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
//						k = k - 1;
						dayrowcount++;
					}
					if (jj == 0) {
						cellStyle.setBorderTop(CellStyle.BORDER_NONE);
					}
					if (jj == 1) {
						cell.setCellValue(CF.getEmpNameMapByEmpId(con, strResourceId));
						cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
						resourceCount++;
					}
					
					if (jj == 1) {
						cellStyle.setBorderTop(CellStyle.BORDER_NONE);
					}
					
					if (jj == 2) {
						cell.setCellValue("Onsite");
						cell.setCellStyle(cellStyle);
					}
//					if (jj == 2) {
//						cell.setCellValue("Office");
//						cell.setCellStyle(cellStyle);
//					}
					
					
					if (jj == 3) { //i % 2 == 0 && 
						cell.setCellValue(uF.showData((String)hmTasksT1.get(strResourceId+"_"+strActivityId+"_T"), "-"));
						cell.setCellStyle(cellStyle);
					}
//					if (i % 2 != 0 && j == 3) {
//						Map<String, String> mpprinttask = (Map<String, String>) hmfinaltask.get(alclient_namelist.get(taskcount));
//						if (mpprinttask != null) {
//							cell.setCellValue(mpprinttask.get("OFS"));
//						}
//						cell.setCellStyle(cellStyle);
//						taskcount++;
//					}
//
					if (jj == 4) { //i % 2 == 0 && 
						cell.setCellValue("");
						cell.setCellStyle(cellStyle);
					}
					
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
					if (i % 2 == 0 && jj == 5) {
						cell.setCellValue("");
						cell.setCellStyle(cellStyle);
					}
					if (i % 2 != 0 && jj == 5) {
						cell.setCellValue("");
						cell.setCellStyle(cellStyle);
					}
					if (i % 2 == 0 && jj == 5) {
						cell.setCellValue("");
						cell.setCellStyle(cellStyle);
					}
					if (i % 2 != 0 && jj == 5) {
						cell.setCellValue("");
						cell.setCellStyle(cellStyle);
					}

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

					cell.setCellStyle(cellStyle);
						}
					}
				} else {
					String strResourcId = (String)hmTasks1.get(strResourceId+"_"+strActivityId+"_E");
					strResourcIdNew = (String)hmTasks1.get(strResourceId+"_"+strActivityId+"_E");
					//System.out.println("strActivityId hmTasks1 ===>> " + strActivityId);
					//System.out.println("strResourcId hmTasks1 ===>> " + strResourcId);
					
					int nProCount = uF.parseToInt((String)hmEmployeeCount.get(strResourcId));
					int nProBillCount = uF.parseToInt((String)hmEmployeeBillCount.get(strResourcId));
					int nProCountT = uF.parseToInt((String)hmEmployeeCountT.get(strResourcId));
					int nProBillCountT = uF.parseToInt((String)hmEmployeeBillCountT.get(strResourcId));
					
					//System.out.println("strProjectIdNew ===>> " +strProjectIdNew + " strProjectIdOld ===>> " + strProjectIdOld);
					if(strResourcIdNew!=null && !strResourcIdNew.equalsIgnoreCase(strResourcIdOld)) {
						nCount1 = 0;
					} else if(strResourcIdNew == null) {
						strResourcIdNew = "";
						if(!strResourcIdNew.equalsIgnoreCase(strResourcIdOld)) {
							nCount1 = 0;
						}
					}
					nCount1++;
					
					Map<String, String> hmDates = (Map<String, String>)hmEmployee.get(strResourceId+"_"+strActivityId);
					if(hmDates==null)hmDates = new HashMap<String, String>();
					
					Map<String, String> hmDatesBillableHrs = (Map<String, String>)hmEmployeeBillableHrs.get(strResourceId+"_"+strActivityId);
					if(hmDatesBillableHrs == null) hmDatesBillableHrs = new HashMap<String, String>();
					
					Map<String, String> hmDateHrsIsApproved1 = (Map<String, String>)hmEmployeeHrsIsApproved.get(strResourceId+"_"+strActivityId);
					if(hmDateHrsIsApproved1 == null) hmDateHrsIsApproved1 = new HashMap<String, String>();
					
					Map<String, String> hmDateBillableHrsIsApproved1 = (Map<String, String>)hmEmployeeBillableHrsIsApproved.get(strResourceId+"_"+strActivityId);
					if(hmDateBillableHrsIsApproved1 == null) hmDateBillableHrsIsApproved1 = new HashMap<String, String>();
					
					Map<String, String> hmTaskDescri1 = (Map<String, String>)hmEmployeeTaskDescri.get(strResourceId+"_"+strActivityId);
					if(hmTaskDescri1 == null)hmTaskDescri1 = new HashMap<String, String>();
					
					Map<String, String> hmDateTaskId1 = (Map<String, String>)hmEmployeeTaskId.get(strResourceId+"_"+strActivityId);
					if(hmDateTaskId1 == null)hmDateTaskId1 = new HashMap<String, String>();
					
					
					System.out.println("hmTasks1 in else =====>> " + hmTasks1);
					for(int j=0; j<2; j++) {
						sb.replace(0, sb.length(), "");
						if(j==0) {
							sb.append("OFS"); 
						} else {
							sb.append("ONS");
						}
						
						if(j==0 && nCount==1) { 
							int rwspn = 2;
							int billrwspn = 0;
							if(nProBillCount>0) {
								billrwspn = nProBillCount * 2;
							}
							
							int billrwspnT = 0;
							if(nProBillCountT>0) {
								billrwspnT = nProBillCountT * 2;
							}
						}	
//					}	
//					cal = GregorianCalendar.getInstance();
//					cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "dd")));
//					cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "MM")) - 1);
//					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getFrmDate(), DATE_FORMAT, "yyyy")));

					Row row = monthly_time_sheet.createRow(i);
					int dateCnt = 0;
					for (int jj = 0; jj < 6 + nDateDiff; jj++) {
						HSSFCellStyle cellStyle = workbook.createCellStyle();
						cellStyle.setBorderBottom(CellStyle.BORDER_THIN);
						cellStyle.setBorderTop(CellStyle.BORDER_THIN);
						cellStyle.setBorderLeft(CellStyle.BORDER_THIN);
						cellStyle.setBorderRight(CellStyle.BORDER_THIN);

						Cell cell = row.createCell(jj);

						if (jj == 0) { //i % 2 == 0 && 
							cell.setCellValue(srCnt);
							cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
//							k = k - 1;
							dayrowcount++;
						}
						if (jj == 0) {
							cellStyle.setBorderTop(CellStyle.BORDER_NONE);
						}
						if (jj == 1) {
							cell.setCellValue(CF.getEmpNameMapByEmpId(con, strResourceId));
							cellStyle.setBorderBottom(CellStyle.BORDER_NONE);
							resourceCount++;
						}
						
						if (jj == 1) {
							cellStyle.setBorderTop(CellStyle.BORDER_NONE);
						}
						
						if (j==0 && jj == 2) {
							cell.setCellValue("Onsite");
							cell.setCellStyle(cellStyle);
						}
						if (j==1 && jj == 2) {
							cell.setCellValue("Office");
							cell.setCellStyle(cellStyle);
						}
						
						
						System.out.println("hmTasks1_T =====>> "+strResourceId+" ---- "+ hmTasks1.get(strResourceId+"_"+strActivityId+"_T"));
						if (j==0 && jj == 3) { //i % 2 == 0 && 
							cell.setCellValue(uF.showData((String)hmTasks1.get(strResourceId+"_"+strActivityId+"_T"), "-"));
							cell.setCellStyle(cellStyle);
						}
						if (j==1 && jj == 3) { //i % 2 == 0 && 
							cell.setCellValue("");
							cell.setCellStyle(cellStyle);
						}
//						
	//
						if (j==0 && jj == 4) { //i % 2 == 0 && 
							cell.setCellValue("");
							cell.setCellStyle(cellStyle);
						}
						if (j==1 && jj == 4) { //i % 2 == 0 && 
							cell.setCellValue("");
							cell.setCellStyle(cellStyle);
						}
//						
						if (j==0 && jj == 5) {
							cell.setCellValue("");
							cell.setCellStyle(cellStyle);
						}
						if (j==1 && jj == 5) {
							cell.setCellValue("");
							cell.setCellStyle(cellStyle);
						}
						
						if (j==0 && jj > 5) {
							
							String strText = "-";
							
							String isApproved = uF.showData((String)hmDateHrsIsApproved.get((String)alDates.get(dateCnt)+"_"+sb.toString()), strText);
							String isBillableApproved = uF.showData((String)hmDateBillableHrsIsApproved.get((String)alDates.get(dateCnt)+"_"+sb.toString()), strText);
							
							String strBgColor = null;
							if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
								if(rosterWeeklyOffSet.contains(alDates.get(dateCnt))) {
									strBgColor =IConstants.WEEKLYOFF_COLOR;
								}
							} else if(weeklyOffSet.contains(alDates.get(dateCnt))) {
								strBgColor =IConstants.WEEKLYOFF_COLOR;
							}
							if(strBgColor!=null) {
								strText = "W/O";
							}
							
							if(strBgColor==null) {
								strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(dateCnt), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
								strText = "H";
							}
							if(strBgColor==null) {
								strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(dateCnt)));
								//strText = (String)hmLeaves.get((String)alDates.get(i));
								strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(dateCnt))); 
							}
							
							if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(dateCnt).toString(), IConstants.DATE_FORMAT))) {
								strBgColor = "#EFEFEF";
							}
							
							if(strText==null) {
								strText = "-";
							}
							
							String dayStatus= uF.showData((String)hmDatesBillableHrs.get((String)alDates.get(dateCnt)+"_"+sb.toString()), strText);
							
//							Map<String, Map<String, String>> hmactualhrs = (Map<String, Map<String, String>>) projectdetails.get(alclient_namelist.get(dayrowcount - 1));
							cell.setCellValue(dayStatus);
							cell.setCellStyle(cellStyle);
							dateCnt++;
						}
						
						int dateCnt1 = 0;
						if (j==1 && j > 5) {
							
							String strText = "-";
							
							String isApproved = uF.showData((String)hmDateHrsIsApproved.get((String)alDates.get(dateCnt1)+"_"+sb.toString()), strText);
							String isBillableApproved = uF.showData((String)hmDateBillableHrsIsApproved.get((String)alDates.get(dateCnt1)+"_"+sb.toString()), strText);
							
							String strBgColor = null;
							if(alEmpCheckRosterWeektype.contains((String)request.getAttribute("strEmpId"))) {
								if(rosterWeeklyOffSet.contains(alDates.get(dateCnt1))) {
									strBgColor =IConstants.WEEKLYOFF_COLOR;
								}
							} else if(weeklyOffSet.contains(alDates.get(dateCnt1))) {
								strBgColor =IConstants.WEEKLYOFF_COLOR;
							}
							if(strBgColor!=null) {
								strText = "W/O";
							}
							
							if(strBgColor==null) {
								strBgColor = (String)hmHolidayDates.get(uF.getDateFormat((String) alDates.get(dateCnt1), IConstants.DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId);
								strText = "H";
							}
							if(strBgColor==null) {
								strBgColor = (String) hmLeavesColour.get((String)hmLeaves.get(alDates.get(dateCnt1)));
								//strText = (String)hmLeaves.get((String)alDates.get(i));
								strText = (String)hmLeaveCode.get((String)hmLeaves.get(alDates.get(dateCnt1))); 
							}
							
							if(uF.getDateFormat(currDate, IConstants.DATE_FORMAT).equals(uF.getDateFormat(alDates.get(dateCnt1).toString(), IConstants.DATE_FORMAT))) {
								strBgColor = "#EFEFEF";
							}
							
							if(strText==null) {
								strText = "-";
							}
							
							String dayStatus= uF.showData((String)hmDatesBillableHrs.get((String)alDates.get(dateCnt1)+"_"+sb.toString()), strText);
							
//							Map<String, Map<String, String>> hmactualhrs = (Map<String, Map<String, String>>) projectdetails.get(alclient_namelist.get(dayrowcount - 1));
							cell.setCellValue(dayStatus);
							cell.setCellStyle(cellStyle);
							dateCnt1++;
						}

							}
					}
				}
			}
		}
	}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	

	
	private Map<String, Map<String, String>> getLeaveDetails(String strDate1, String strDate2, UtilityFunctions uF, Map<String, String> hmLeavesCode) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, Map<String, String>> getMap = new HashMap<String, Map<String, String>>();
		try{
			con=db.makeConnection(con);
			pst=con.prepareStatement("select lar.*,lt.leave_type_code from leave_application_register lar,leave_type lt where lar.leave_type_id=lt.leave_type_id and is_modify = false and _date between ? and ? order by emp_id");
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate2, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs=pst.executeQuery();
			
			while(rs.next()) {
				Map<String, String> a = getMap.get(rs.getString("emp_id"));
				if(a == null) a = new HashMap<String, String>(); 
				
//				a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("leave_type_code"));
				a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("leave_type_id"));
				getMap.put(rs.getString("emp_id"), a);
				hmLeavesCode.put(rs.getString("leave_type_id"), rs.getString("leave_type_code"));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return getMap;
	}
	
	
	// // filling all cell values from database ....
//	public void fillsheet(Map<String, Map<String, Map<String, String>>> projectdetails, List<String> nameheaderlist, List<Integer> leavesdaylist, List<Integer> holidayslist,
//			Map<String, Map<String, String>> hmfinaltask, List<String> idList)
//			throws SQLException {
//
//		Connection con = null;
//
//		ResultSet rs = null;
//		ResultSet rs1 = null;
//		Database db = new Database();
//		db.setRequest(request);
//		con = db.makeConnection(con);
//		PreparedStatement pst = null;
//		session = request.getSession();
//		UtilityFunctions uF = new UtilityFunctions();
//		CF = (CommonFunctions) session.getAttribute("CF");
//		UtilityFunctions UF = new UtilityFunctions();
//		// ******** LEAVES.......
//
//		 pst = con.prepareStatement("select epd.emp_fname,epd.emp_lname,epd.empcode,emp_bank_acct_nbr,emp_pan_no,di.dept_name from employee_personal_details epd join employee_official_details eod on(epd.emp_per_id=eod.emp_id) join department_info di on(eod.depart_id=di.dept_id) where epd.emp_per_id=?");
//		pst.setInt(1, UF.parseToInt(getEmpid()));
//		rs1 = pst.executeQuery();
//
//		while (rs1.next()) {
//			nameheaderlist.add(rs1.getString("emp_fname") + " "+rs1.getString("emp_lname"));
//			nameheaderlist.add(rs1.getString("empcode"));
//			nameheaderlist.add(rs1.getString("emp_bank_acct_nbr"));
//			nameheaderlist.add(rs1.getString("emp_pan_no"));
//			nameheaderlist.add(rs1.getString("dept_name"));
//
//		}
//		rs1.close();
//		pst.close();
//		
//		try {
//
//			pst = con.prepareStatement("select client_name,task_location,task_date,SUM(actual_hrs) as actual_hrs from (select clnt.client_name,pmnt.client_id ,ai.pro_id,ai.activity_name,ta.activity_id,ta.task_date,ta.actual_hrs,task_location from task_activity ta left join activity_info ai on ta.activity_id=ai.task_id left join projectmntnc pmnt on ai.pro_id=pmnt.pro_id left join client_details clnt on clnt.client_id=pmnt.client_id where ta.emp_id=? and task_date>=? and task_date<=?) as a  group by client_name,task_date,task_location order by client_name");
//			pst.setInt(1, UF.parseToInt(getEmpid()));
//			pst.setDate(2, UF.getDateFormat(getDatefrom(), DATE_FORMAT));
//			pst.setDate(3, UF.getDateFormat(getDateto(), DATE_FORMAT));
//			rs = pst.executeQuery();
//
//			while (rs.next()) {
//				Map<String, Map<String, String>> a = projectdetails.get(rs.getString("client_name"));
//				if (a == null)
//					a = new HashMap<String, Map<String, String>>();
//
//				if (rs.getString("task_location") != null && rs.getString("task_location").equalsIgnoreCase("ONS")) {
//					Map<String, String> b = a.get(rs.getString("task_location"));
//					if (b == null)
//						b = new HashMap<String, String>();
//					b.put(rs.getString("task_date"), uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rs.getString("actual_hrs"))));
//					a.put(rs.getString("task_location"), b);
//					projectdetails.put(rs.getString("client_name"), a);
//
//				} else if (rs.getString("task_location") == null || rs.getString("task_location").equalsIgnoreCase("OFS") || rs.getString("task_location").equalsIgnoreCase("")) {
//					Map<String, String> b = a.get("OFS");
//					if (b == null)
//						b = new HashMap<String, String>();
//					b.put(rs.getString("task_date"), uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(rs.getString("actual_hrs"))));
//					a.put("OFS", b);
//					projectdetails.put(rs.getString("client_name"), a);
//				}
//
//			}
//			rs.close();
//			pst.close();
//
//			if (projectdetails != null) {
//				String strquerytask = "select client_name,activity_name, task_location , ta.activity from activity_info ai right join projectmntnc using(pro_id) right join client_details using(client_id) right join task_activity ta on ta.activity_id=ai.task_id  where ta.emp_id=? group by client_name,activity_name, ta.activity,task_location order by client_name ";
//				pst = con.prepareStatement(strquerytask);
//				pst.setInt(1, UF.parseToInt(getEmpid()));
//				rs = pst.executeQuery();
//
//				while (rs.next()) {
//					Map<String, String> innerhmfinaltask = hmfinaltask.get(rs.getString("client_name"));
//					if (innerhmfinaltask == null) {
//						innerhmfinaltask = new HashMap<String, String>();
//						if(rs.getString("activity_name")!=null){
//							innerhmfinaltask.put(rs.getString("task_location"), rs.getString("activity_name"));
//						}else{
//							innerhmfinaltask.put(rs.getString("task_location"), rs.getString("activity"));
//						}
//						
//						hmfinaltask.put(rs.getString("client_name"), innerhmfinaltask);
//					} else {
//						if (innerhmfinaltask.keySet().contains(rs.getString("task_location"))) {
//							String tasknew = innerhmfinaltask.get(rs.getString("task_location"));
//							
//							if(rs.getString("activity_name")!=null){
//								tasknew = tasknew + "," + rs.getString("activity_name");
//							}else{
//								tasknew = tasknew + "," + rs.getString("activity");
//							}
//							
//							innerhmfinaltask.put(rs.getString("task_location"), tasknew);
//						} else {
//							if(rs.getString("activity_name")!=null){
//								innerhmfinaltask.put(rs.getString("task_location"), rs.getString("activity_name"));
//							}else{
//								innerhmfinaltask.put(rs.getString("task_location"), rs.getString("activity"));
//							}
//							
//							hmfinaltask.put(rs.getString("client_name"), innerhmfinaltask);
//
//						}
//
//					}
//				}
//
//			}
//			// conveyance sheet statement
//			
//			// making conveyance others"""""
//
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeResultSet(rs1);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//
//		}
//
//		Calendar cal = GregorianCalendar.getInstance();
//		cal.set(Calendar.DAY_OF_MONTH, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "dd")));
//		cal.set(Calendar.MONTH, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "MM")) - 1);
//		cal.set(Calendar.YEAR, UF.parseToInt(UF.getDateFormat(getDatefrom(), DATE_FORMAT, "yyyy")));
//
//		Calendar cal1 = GregorianCalendar.getInstance();
//		cal1.set(Calendar.DAY_OF_MONTH, UF.parseToInt(UF.getDateFormat(getDateto(), DATE_FORMAT, "dd")));
//		cal1.set(Calendar.MONTH, UF.parseToInt(UF.getDateFormat(getDateto(), DATE_FORMAT, "MM")) - 1);
//		cal1.set(Calendar.YEAR, UF.parseToInt(UF.getDateFormat(getDateto(), DATE_FORMAT, "yyyy")));
//
//		coloumncountmain = daysBetween(cal.getTime(), cal1.getTime()); 
//
//	}

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
//
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
		for (int i = 0; i < 7 + coloumncountmain; i++) {
			HSSFCellStyle bottomBorder = workbook.createCellStyle();
			if (i == 0) {
				bottomBorder.setBorderLeft(CellStyle.BORDER_THIN);
			}
			if (i == coloumncountmain + 6) {
				bottomBorder.setBorderRight(CellStyle.BORDER_THIN);
			}
			Cell cell = row.createCell(i);
			cell.setCellStyle(bottomBorder);
		}
	}

	public String getReportName() {
		String name = "Timesheet_" + getEmpid() + "_" + getFrmDate() + "_" + getToDate();
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

	public String getProFreqId() {
		return proFreqId;
	}

	public void setProFreqId(String proFreqId) {
		this.proFreqId = proFreqId;
	}

	public String getProId() {
		return proId;
	}

	public void setProId(String proId) {
		this.proId = proId;
	}

	public String getFrmDate() {
		return frmDate;
	}

	public void setFrmDate(String frmDate) {
		this.frmDate = frmDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public List<Date> getholidayslisttimesheet(String strWlocationId) {

		UtilityFunctions UF = new UtilityFunctions();
		List<Date> dateList = new ArrayList<Date>();
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			Calendar cal3 = GregorianCalendar.getInstance();
			cal3.set(Calendar.DAY_OF_MONTH, UF.parseToInt(UF.getDateFormat(getFrmDate(), DATE_FORMAT, "dd")));
			cal3.set(Calendar.MONTH, UF.parseToInt(UF.getDateFormat(getFrmDate(), DATE_FORMAT, "MM")) - 1);
			cal3.set(Calendar.YEAR, UF.parseToInt(UF.getDateFormat(getFrmDate(), DATE_FORMAT, "yyyy")));

			
			con = db.makeConnection(con);

			Map hmWeekendMap = CF.getWeekEndDateList(con, getFrmDate(), getDateto(), CF, UF,null,null);

			Set<String> set = new HashSet<String>();

			Iterator<String> itr = hmWeekendMap.keySet().iterator();
			while (itr.hasNext()) {
				String temp = itr.next().toString();
				if (temp.substring(11, 14).equals(strWlocationId)) {
					set.add(temp.substring(0, 10));
				}
				
				
				if (temp!=null && temp.indexOf(strWlocationId)>=0) {
					set.add(temp.substring(0, (temp.length()>10 ? 10 : temp.length())));
				}
			}

			SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

			Date datefromcheck = dateFormat.parse(getFrmDate());
			Date datetocheck = dateFormat.parse(getDateto());
			itr = set.iterator();

			while (itr.hasNext()) {
				Date finaldate = dateFormat.parse(itr.next().toString());
				if (UF.isDateBetween(datefromcheck, datetocheck, finaldate)) {
					dateList.add(finaldate);
				}

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		return dateList;
	}

}*/