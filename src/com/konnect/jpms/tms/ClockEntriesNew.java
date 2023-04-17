package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.CommonFunctionsNew;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ClockEntriesNew extends ActionSupport implements ServletRequestAware,SessionAware, IStatements {

	/**
	 *
	 */
	Map session;
	String strSelectedEmpId; 
	String strUserType = null;
	String strSessionEmpId = null;
	CommonFunctions CF = null;
	private String level;

	
	List<FillLevel> levelList;
	String paycycle;
	List<FillPayCycles> payCycleList;
	List<FillEmployee> empNamesList;
	
	List<FillOrganisation> orgList;	
	String f_org;
	
	String location;
	List<FillWLocation> workLocationList;


	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		CF = (CommonFunctions) session.get(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.get(USERTYPE);
		strSessionEmpId = (String) session.get(EMPID);

//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		
		request.setAttribute(PAGE, "/jsp/tms/ClockEntriesNew.jsp");
		request.setAttribute(TITLE, "ClockEntries");
		
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try{
			con=db.makeConnection(con);
			if(getF_org()==null){
				setF_org((String)session.get(ORGID));
			}
			
			String[] strPayCycleDates=null;
			if(paycycle==null){
				strPayCycleDates=CF.getCurrentPayCycleByOrg(con,CF.getStrTimeZone(),CF,getF_org());
			}else if( paycycle!=null){
				strPayCycleDates = getPaycycle().split("-");
			}
			
			String strPaycycleStartDate=uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, DBDATE);
			String strPaycycleEndDate=uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, DBDATE);
	
			loadClockEntries(CF,uF,strPayCycleDates[0]);
	
			if(uF.parseToInt(strSelectedEmpId)!=0){
				viewClockEntries(con,uF,uF.parseToInt(strSelectedEmpId),strPaycycleStartDate,strPaycycleEndDate);
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
		return LOAD;

	}
	
	public String loadClockEntries(CommonFunctions CF, UtilityFunctions uF,String strPaycycleEndDate) {
		payCycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			workLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.get(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.get(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			workLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		
			empNamesList= new FillEmployee(request).fillEmployeeName(strPaycycleEndDate, uF.parseToInt(getLevel()),uF.parseToInt(getF_org()),uF.parseToInt(getLocation()));
	
		
		return LOAD;
	}
	
	
	public void viewClockEntries(Connection con,UtilityFunctions uF,int empId, String strPaycycleStartDate, String strPaycycleEndDate) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Calendar cal=Calendar.getInstance();
			cal.setTime(uF.getDateFormatUtil(strPaycycleStartDate, DBDATE));
			
			List<String> dateList=new ArrayList<String>();
			Map<String,String> dateDisplayList=new HashMap<String,String>();

			int totalDaysinPaycycle=uF.parseToInt(uF.dateDifference(strPaycycleStartDate, DBDATE, strPaycycleEndDate, DBDATE));
//			SimpleDateFormat sdfDBDATE=new SimpleDateFormat(DBDATE);
			SimpleDateFormat sdf=new SimpleDateFormat(DATE_FORMAT);
			
			SimpleDateFormat timeFormate=new SimpleDateFormat(TIME_FORMAT);
			

			while(totalDaysinPaycycle>0){
				
				dateList.add(sdf.format(cal.getTime()));
				dateDisplayList.put(sdf.format(cal.getTime()),uF.getDay(cal.get(Calendar.DAY_OF_WEEK))+","+sdf.format(cal.getTime()));

				cal.add(Calendar.DATE,1);
				totalDaysinPaycycle--;
			}
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
//			CommonFunctionsNew CF1=new CommonFunctionsNew();
			pst = con.prepareStatement("select wlocation_id,emp_id,empcode,emp_fname,emp_mname, emp_lname,emp_gender,emp_date_of_birth from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id and emp_id=?");
			pst.setInt(1,empId);
			rs=pst.executeQuery();
			String wLocation=null;
			String level=hmEmpLevelMap.get(empId+"");
			String empName=null;
			while(rs.next()){
				wLocation=rs.getString("wlocation_id");
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				empName=rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname");
			
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM holidays WHERE _date BETWEEN ? AND ? and wlocation_id=? and (is_optional_holiday is null or is_optional_holiday=false) order by _date desc");
			pst.setInt(3,uF.parseToInt(wLocation));
			pst.setDate(1, uF.getDateFormat(strPaycycleStartDate, DBDATE));
			pst.setDate(2, uF.getDateFormat(strPaycycleEndDate, DBDATE));
			rs = pst.executeQuery();
			Map<String,Map<String,String>> holidayList=new HashMap<String,Map<String,String>>();
			while (rs.next()) {
				Map<String,String> list=new HashMap<String,String>();
				list.put("HOLIDAY", rs.getString("description"));
				list.put("COLOR_CODE", rs.getString("colour_code"));
				holidayList.put(sdf.format( rs.getDate("_date")),list);
			}
			rs.close();
			pst.close();
			
			Map<String,Set<String>> hmHalfWeekEnds=new HashMap<String,Set<String>>();
			Map<String,Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, uF.getDateFormat(strPaycycleStartDate, DBDATE, DATE_FORMAT), uF.getDateFormat( strPaycycleEndDate, DBDATE, DATE_FORMAT), CF, uF,hmHalfWeekEnds,level);
			System.out.println("level===="+level);
			System.out.println("hmWeekEnds===="+hmWeekEnds);
			System.out.println("hmHalfWeekEnds===="+hmHalfWeekEnds);

			Map<String,Map<String,String>> leaveTypeMap=new HashMap<String,Map<String,String>>();
			pst = con.prepareStatement("select * from leave_application_register lar,leave_type lt where lar.leave_type_id=lt.leave_type_id and _date between ? and ? and is_paid=true and emp_id=?");
			pst.setInt(3,empId);
			pst.setDate(1, uF.getDateFormat(strPaycycleStartDate, DBDATE));
			pst.setDate(2, uF.getDateFormat(strPaycycleEndDate, DBDATE));
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String,String> list=new HashMap<String,String>();
				list.put("LEAVE_CODE", rs.getString("leave_type_code"));
				list.put("COLOR_CODE", rs.getString("leave_type_colour"));
				list.put("LEVE_TYPE_ID", rs.getString("leave_type_id"));
				list.put("LEAVE_NO", rs.getString("leave_no"));

				leaveTypeMap.put(sdf.format( rs.getDate("_date")),list);
			}
			rs.close();
			pst.close();
			

			pst = con.prepareStatement("SELECT * FROM roster_details WHERE _date BETWEEN ? AND ? and emp_id=? order by _date desc");
			pst.setInt(3,empId);
			pst.setDate(1, uF.getDateFormat(strPaycycleStartDate, DBDATE));
			pst.setDate(2, uF.getDateFormat(strPaycycleEndDate, DBDATE));
			rs = pst.executeQuery();
			Map<String,Map<String,String>> rosterMap=new HashMap<String,Map<String,String>>();
			while (rs.next()) {
				Map<String,String> list=new HashMap<String,String>();
				java.util.Date utdt =timeFormate.parse(rs.getString("_from"));
				list.put("IN",timeFormate.format(utdt));
				utdt =timeFormate.parse(rs.getString("_to"));
				list.put("OUT",timeFormate.format(utdt));
				list.put("ACTUAL_HOURS",rs.getString("actual_hours"));
				rosterMap.put(sdf.format( rs.getDate("_date")), list);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select *,cast(in_out_timestamp_actual as date) as attendance_date,cast(in_out_timestamp_actual as time) as attendance_time,cast(in_out_timestamp as time) as attendance_time1 from attendance_details where  cast(in_out_timestamp as date) between ? and ? and emp_id=?");
			pst.setDate(1, uF.getDateFormat(strPaycycleStartDate, DBDATE));
			pst.setDate(2, uF.getDateFormat(strPaycycleEndDate, DBDATE));
			pst.setInt(3,empId);
			rs=pst.executeQuery();
			Map<String,String> hmAttendanceIN = new HashMap<String,String>();
			Map<String,String> hmAttendanceOUT = new HashMap<String,String>();
			Map<String,String> hmhoursWorked = new HashMap<String,String>();

			while(rs.next()){ 
				
				if("IN".equals(rs.getString("in_out"))){
					java.util.Date utdt =timeFormate.parse(rs.getString("attendance_time"));
					java.util.Date utdt1 =timeFormate.parse(rs.getString("attendance_time1"));

					hmAttendanceIN.put( sdf.format( rs.getDate("attendance_date")),timeFormate.format(utdt1)+"["+ timeFormate.format(utdt)+"]");
				}else{
					java.util.Date utdt =timeFormate.parse(rs.getString("attendance_time"));
					java.util.Date utdt1 =timeFormate.parse(rs.getString("attendance_time1"));

					hmAttendanceOUT.put(sdf.format( rs.getDate("attendance_date")),  timeFormate.format(utdt1)+"["+ timeFormate.format(utdt)+"]");
					hmhoursWorked.put(sdf.format( rs.getDate("attendance_date")),uF.formatIntoTwoDecimalWithOutComma( rs.getDouble("hours_worked")));

				}
			}
			rs.close();
			pst.close();
			
			
			request.setAttribute("hmAttendanceIN", hmAttendanceIN);
			request.setAttribute("hmAttendanceOUT", hmAttendanceOUT);
			request.setAttribute("hmhoursWorked", hmhoursWorked);
			request.setAttribute("rosterMap", rosterMap);
			request.setAttribute("hmHalfWeekEnds", hmHalfWeekEnds.get(level));
			request.setAttribute("hmWeekEnds", hmWeekEnds.get(level));
			request.setAttribute("leaveTypeMap", leaveTypeMap);
			request.setAttribute("empName", empName);
			request.setAttribute("holidayList", holidayList);

			request.setAttribute("dateList", dateList);
			request.setAttribute("dateDisplayList", dateDisplayList);
			request.setAttribute("strPaycycleStartDate", strPaycycleStartDate);
			request.setAttribute("strPaycycleEndDate", strPaycycleEndDate);
			request.setAttribute("empId", empId+"");

		}catch(Exception e){
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

	@Override
	public void setSession(Map session) {
		this.session=session;
		
	}

}

