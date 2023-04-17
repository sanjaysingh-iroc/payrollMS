package com.konnect.jpms.tms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
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

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ClockEntries extends ActionSupport implements ServletRequestAware, IStatements,IConstants {

	/**
	 *
	 *
	 */
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	// String strEmpId;
	private String strSelectedEmpId;
	private String strType;
	private String strPAY;
	
	// String strPC;
	private String strD1;
	private String strD2;
	private String payCycleEnd = null;
	private String strUserType = null;
	String strBaseUserType = null;
	String strWLocationAccess =  null;
	private String strSessionEmpId = null;
	
	private String level;
	private List<FillLevel> levelList;
	private String paycycle;
	private List<FillPayCycles> payCycleList;
	// List<FillEmployee> empList;
	private List<FillEmployee> empNamesList;
	private String profileEmpId;
	
	private List<FillOrganisation> orgList;	
	private String f_org;
	
	private String location;
	private List<FillWLocation> workLocationList;

	private String pageFrom;
	
	private static Logger log = Logger.getLogger(ClockEntries.class);
	public String execute() throws Exception {
	
		session = request.getSession();
		//CommonFunctions CF = (CommonFunctions) session.getAttribute(IConstants.CommonFunctions);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
		strWLocationAccess = (String)session.getAttribute(WLOCATION_ACCESS);
		
		UtilityFunctions uF = new UtilityFunctions();
				
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			
			if(uF.parseToInt(getF_org())==0){
				setF_org((String) session.getAttribute(ORGID));
			}
			

			if (strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
				request.setAttribute(TITLE, "My Time");
			} else {
				request.setAttribute(TITLE, "Clock Entries");
			}
			setProfileEmpId((String) request.getParameter("profileEmpId"));

			strPAY = (String) request.getParameter("PAY");
			strType = (String) request.getParameter("T");
			String[] strPayCycleDates = null;
			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
				strD1 = strPayCycleDates[0];
				strD2 = strPayCycleDates[1];
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(con,CF.getStrTimeZone(), CF, getF_org());
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);

				strD1 = strPayCycleDates[0];
				strD2 = strPayCycleDates[1];
			}

			loadClockEntries(uF);

			if (strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
				setStrSelectedEmpId(strSessionEmpId);
			} else {
				if (profileEmpId != null) {
					setStrSelectedEmpId(profileEmpId);
				} else if (getStrSelectedEmpId() == null) {
					setStrSelectedEmpId("0");
				}
			}
			
//			System.out.println("getStrSelectedEmpId() ===>> " + getStrSelectedEmpId());
			List<String> accessEmpList = CF.viewEmployeeIdsList(request, uF, strBaseUserType, strSessionEmpId, strWLocationAccess);
			if(strBaseUserType != null && strUserType != null && ((strBaseUserType.equals(EMPLOYEE) || strUserType.equals(EMPLOYEE) || !accessEmpList.contains(getStrSelectedEmpId())) && uF.parseToInt(getStrSelectedEmpId())>0)) {
				setStrSelectedEmpId(strSessionEmpId);
			}
//			System.out.println("getStrSelectedEmpId() aftr ===>> " + getStrSelectedEmpId());
			if (getStrSelectedEmpId() != null) {
				if (strType == null) {
					if (strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
						request.setAttribute(PAGE, PReportClock2Emp);
//					System.out.println("PReportClock2Emp");
					} else { 
						request.setAttribute(PAGE, PReportClock2);
//						System.out.println("PReportClock2");
					}
				} else if (strType != null && strType.equalsIgnoreCase("T")) {
					request.setAttribute(PAGE, PReportClock);
//				System.out.println("PReportClock");
				} else if (strType != null && strType.equalsIgnoreCase("RRA")) {
//				System.out.println("PReportClock3");
					request.setAttribute(PAGE, PReportClock3);
				}

				getSalaryPaidEmployee(con, uF,getStrSelectedEmpId());
				String str = viewClockEntries(con, strD1, strD2);
				getOverTimeDetails(con,uF,strD1,strD2);
				
				if(uF.parseToBoolean(CF.getIsBreakPolicy())){
					getBreakPolicy(con,uF,strD1,strD2);
				}
//				loadClockEntries(uF);
			}

			loadClockEntries(uF);
			
			getSelectedFilter(uF);
			
			if (getStrSelectedEmpId() != null) {
				if (strType == null) {
					if (strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
						return "empload";
					} else { 
						return "ghrload";
					}
				} else if (strType != null && strType.equalsIgnoreCase("T")) {
					return LOAD;
				} else if (strType != null && strType.equalsIgnoreCase("RRA")) {
					return LOAD;
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		
		return LOAD;

	}
	
	private void getOverTimeDetails(Connection con, UtilityFunctions uF, String strD1, String strD2) {
		PreparedStatement pst=null;
		ResultSet rs = null;
		Map<String, String> hmEmpOrgMap=new HashMap<String, String>();
		Map<String, String> hmEmpLevelMap =CF.getEmpLevelMap(con);
		
		try {
			
			CF.getEmpWlocationMap(con, null, null, null, hmEmpOrgMap);
			
			
			Map<String,Map<String,String>> hmOvertimeType=new HashMap<String, Map<String,String>>();
//			pst=con.prepareStatement("select * from overtime_details where org_id=? and level_id=? " +
//					" and (date_from,date_to) overlaps (to_date(?::text,'yyyy-MM-dd'),to_date(?::text,'yyyy-MM-dd'))");
			pst = con.prepareStatement("select * from overtime_details where org_id=? and level_id=? and ((? between date_from and date_to) " +
					"or (? between date_from and date_to))");
			pst.setInt(1, uF.parseToInt(hmEmpOrgMap.get(getStrSelectedEmpId())));
			pst.setInt(2, uF.parseToInt(hmEmpLevelMap.get(getStrSelectedEmpId())));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst====>"+pst); 
			rs=pst.executeQuery();
//			StringBuffer sbOtId = null; 
			while(rs.next()){
				if(rs.getString("calculation_basis") != null && rs.getString("calculation_basis").trim().equalsIgnoreCase("M")){
//					if(sbOtId == null){
//						 sbOtId = new StringBuffer();
//						 sbOtId.append(rs.getString("overtime_id"));
//					} else {
//						sbOtId.append(","+rs.getString("overtime_id"));
//					}
					continue;
				}
				if(rs.getString("overtime_type").equals("PH")){
					Map<String,String> hmOvertime=new HashMap<String, String>();
					hmOvertime.put("OVERTIME_ID", rs.getString("overtime_id"));
					hmOvertime.put("BUFFER_STANDARD_TIME", rs.getString("buffer_standard_time"));
					hmOvertime.put("OVERTIME_HRS", rs.getString("over_time_hrs"));
					
					hmOvertime.put("FIXED_OVERTIME_HRS", rs.getString("fixed_overtime_hrs"));
					hmOvertime.put("MIN_OVER_TIME", rs.getString("min_over_time"));				
					hmOvertime.put("STANDARD_WKG_HRS", rs.getString("standard_wkg_hours"));
					hmOvertime.put("FIXED_STWKG_HRS", rs.getString("fixed_stwkg_hrs"));
					hmOvertime.put("OVERTIME_TYPE", rs.getString("overtime_type"));
					hmOvertime.put("ROUND_OFF_OVERTIME",rs.getString("round_off_time"));
					hmOvertime.put("CALCULATION_BASIS",rs.getString("calculation_basis"));
					
					hmOvertimeType.put("PH", hmOvertime);
				}else if(rs.getString("overtime_type").equals("BH")){
					Map<String,String> hmOvertime=new HashMap<String, String>();
					hmOvertime.put("OVERTIME_ID", rs.getString("overtime_id"));
					hmOvertime.put("BUFFER_STANDARD_TIME", rs.getString("buffer_standard_time"));
					hmOvertime.put("OVERTIME_HRS", rs.getString("over_time_hrs"));
					hmOvertime.put("FIXED_OVERTIME_HRS", rs.getString("fixed_overtime_hrs"));
					hmOvertime.put("MIN_OVER_TIME", rs.getString("min_over_time"));				
					hmOvertime.put("STANDARD_WKG_HRS", rs.getString("standard_wkg_hours"));
					hmOvertime.put("FIXED_STWKG_HRS", rs.getString("fixed_stwkg_hrs"));
					hmOvertime.put("OVERTIME_TYPE", rs.getString("overtime_type"));
					hmOvertime.put("ROUND_OFF_OVERTIME",rs.getString("round_off_time"));
					hmOvertime.put("CALCULATION_BASIS",rs.getString("calculation_basis"));
					
					hmOvertimeType.put("BH", hmOvertime);
				}else if(rs.getString("overtime_type").equals("EH")){
					Map<String,String> hmOvertime=new HashMap<String, String>();
					hmOvertime.put("OVERTIME_ID", rs.getString("overtime_id"));
					hmOvertime.put("BUFFER_STANDARD_TIME", rs.getString("buffer_standard_time"));
					hmOvertime.put("OVERTIME_HRS", rs.getString("over_time_hrs"));
					hmOvertime.put("FIXED_OVERTIME_HRS", rs.getString("fixed_overtime_hrs"));
					hmOvertime.put("MIN_OVER_TIME", rs.getString("min_over_time"));				
					hmOvertime.put("STANDARD_WKG_HRS", rs.getString("standard_wkg_hours"));
					hmOvertime.put("FIXED_STWKG_HRS", rs.getString("fixed_stwkg_hrs"));
					hmOvertime.put("OVERTIME_TYPE", rs.getString("overtime_type"));
					hmOvertime.put("ROUND_OFF_OVERTIME",rs.getString("round_off_time"));
					hmOvertime.put("CALCULATION_BASIS",rs.getString("calculation_basis"));
					
					hmOvertimeType.put("EH", hmOvertime);
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmOvertimeType", hmOvertimeType);
//			System.out.println("hmOvertimeType java ===>> " + hmOvertimeType);
			
//			if(sbOtId !=null && sbOtId.length() > 0){
//				pst = con.prepareStatement("select * from overtime_minute_slab where overtime_id in ("+sbOtId.toString()+")");
////				System.out.println("pst====>"+pst);
//				rs=pst.executeQuery();
//				Map<String, List<Map<String,String>>> hmOvertimeMinuteSlab = new HashMap<String, List<Map<String,String>>>();
//				while(rs.next()){
//					List<Map<String,String>> alOtMinute = (List<Map<String,String>>) hmOvertimeMinuteSlab.get(rs.getString("overtime_id"));
//					if(alOtMinute == null) alOtMinute = new ArrayList<Map<String,String>>();
//					
//					Map<String,String> hmOvertimeMinute =new HashMap<String, String>();
//					hmOvertimeMinute.put("OVERTIME_MINUTE_ID", rs.getString("overtime_minute_id"));
//					hmOvertimeMinute.put("OVERTIME_ID", rs.getString("overtime_id"));
//					hmOvertimeMinute.put("OVERTIME_MIN_MINUTE", rs.getString("min_minute"));
//					hmOvertimeMinute.put("OVERTIME_MAX_MINUTE", rs.getString("max_minute"));
//					hmOvertimeMinute.put("ROUNDOFF_MINUTE", rs.getString("roundoff_minute"));	
//					
//					alOtMinute.add(hmOvertimeMinute);
//					
//					hmOvertimeMinuteSlab.put(rs.getString("overtime_id"), alOtMinute);
//				}
//				rs.close();
//				pst.close();
//				
//				request.setAttribute("hmOvertimeMinuteSlab", hmOvertimeMinuteSlab);
//			}
			
			Map<String, String> hmEmpLocation=CF.getEmpWlocationMap(con);
			String userlocation=hmEmpLocation.get(getStrSelectedEmpId());
			
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
			
			pst= con.prepareStatement("select actual_ot_hours,_date,approved_ot_hours from overtime_hours where emp_id=? " +
					"and paycycle_from=? and paycycle_to=?");
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
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
			pst = con.prepareStatement("select emp_id from payroll_generation where emp_id=? and paid_from=? and paid_to=? group by emp_id");
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println("pst======>"+pst);
			while (rs.next()) {
				hmCheckPayroll.put(rs.getString("emp_id"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmCheckPayroll", hmCheckPayroll);
			
			pst = con.prepareStatement("select * from overtime_emp_minute_status where is_approved=1 and emp_id=? and ot_date between ? and ? ");
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> hmCalculateOT = new HashMap<String, String>();
			while(rs.next()){
				hmCalculateOT.put(rs.getString("emp_id")+"_"+uF.getDateFormat(rs.getString("ot_date"), DBDATE, DATE_FORMAT),""+rs.getString("ot_hours"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmCalculateOT", hmCalculateOT);
	//		System.out.println("hmCalculateOT==>"+hmCalculateOT);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if(strUserType != null && !strUserType.equals(MANAGER) && !strUserType.equals(EMPLOYEE)) {
			alFilter.add("ORGANISATION");
			if(getF_org()!=null) {
				String strOrg="";
				for(int i=0;orgList!=null && i<orgList.size();i++) {
					if(getF_org().equals(orgList.get(i).getOrgId())) {
						strOrg=orgList.get(i).getOrgName();
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
			if(getLocation()!=null) {
				String strLocation="";
				for(int i=0;workLocationList!=null && i<workLocationList.size();i++) {
					if(getLocation().equals(workLocationList.get(i).getwLocationId())) {
						strLocation=workLocationList.get(i).getwLocationName();
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
			
			alFilter.add("LEVEL");
			if(getLevel()!=null) {
				String strLevel="";
				for(int i=0;levelList!=null && i<levelList.size();i++) {
					if(getLevel().equals(levelList.get(i).getLevelId())) {
						strLevel=levelList.get(i).getLevelCodeName();
					}
				}
				if(strLevel!=null && !strLevel.equals("")) {
					hmFilter.put("LEVEL", strLevel);
				} else {
					hmFilter.put("LEVEL", "All Levels");
				}
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}
		}
		
		alFilter.add("PAYCYCLE");
		String strPaycycle = "";
		String[] strPayCycleDates = null;
		if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			
			strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
		}
		hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		if(strUserType != null && !strUserType.equals(EMPLOYEE)) {
			alFilter.add("EMP");
			if(getStrSelectedEmpId()!=null) {
				String strEmpName="";
				for(int i=0;empNamesList!=null && i<empNamesList.size();i++) {
					if(getStrSelectedEmpId().equals(empNamesList.get(i).getEmployeeId())) {
						strEmpName=empNamesList.get(i).getEmployeeName();
//						System.out.println("strEmpName ===>> " + strEmpName);
					}
				}
				if(strEmpName!=null && !strEmpName.equals("")) {
					hmFilter.put("EMP", strEmpName);
				} else {
					hmFilter.put("EMP", "Select Employee");
				}
			} else {
				hmFilter.put("EMP", "Select Employee");
			}
		}
		
		String selectedFilter= CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	
	private void getSalaryPaidEmployee(Connection con, UtilityFunctions uF, String strSelectedEmpId2) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id from payroll_generation where emp_id>0 and emp_id = ? and paid_from=? and paid_to=? group by emp_id order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			List<String> alSalPaidEmpList = new ArrayList<String>();
			while (rs.next()) {
				alSalPaidEmpList.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alSalPaidEmpList", alSalPaidEmpList);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from approve_attendance where emp_id = ? and approve_from>=? and approve_to<=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			List<String> alApproveClockEntrieEmp = new ArrayList<String>();
			while(rs.next()){
				alApproveClockEntrieEmp.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alApproveClockEntrieEmp", alApproveClockEntrieEmp);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from overtime_emp_minute_status where emp_id = ? and ot_date between ? and ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			List<String> alApproveOTMinuteEmp = new ArrayList<String>();
			while(rs.next()){
				alApproveOTMinuteEmp.add(uF.getDateFormat(rs.getString("ot_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			request.setAttribute("alApproveOTMinuteEmp", alApproveOTMinuteEmp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void getBreakPolicy(Connection con, UtilityFunctions uF, String strD1, String strD2) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			Map<String, String> hmEmpLevelMap=CF.getEmpLevelMap(con);
			Map<String, String> hmEmpWlocationMap=CF.getEmpWlocationMap(con);
			pst = con.prepareStatement("select * from emp_leave_break_type where level_id=? and org_id=? and wlocation_id=?");
			pst.setInt(1, uF.parseToInt(hmEmpLevelMap.get(getStrSelectedEmpId())));
			pst.setInt(2, uF.parseToInt(CF.getEmpOrgId(con, uF, getStrSelectedEmpId())));
			pst.setInt(3, uF.parseToInt(hmEmpWlocationMap.get(getStrSelectedEmpId())));
			rs = pst.executeQuery();
			boolean flagBreak=false;
			while(rs.next()){
				flagBreak=true;
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
			pst = con.prepareStatement("select * from break_application_register where _date between ? and ? and emp_id=?");
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrSelectedEmpId()));
			rs = pst.executeQuery();
			while(rs.next()){
				hmBreakPolicy.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), hmBreakTypeCode.get(rs.getString("break_type_id")));
			}
			rs.close();
			pst.close();
			request.setAttribute("flagBreak", flagBreak);
			request.setAttribute("hmBreakPolicy", hmBreakPolicy);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
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

	public String loadClockEntries(UtilityFunctions uF) {
		
		
//		orgList = new FillOrganisation(request).fillOrganisation();
//		if(uF.parseToInt(getF_org())==0){
//			setF_org(orgList.get(0).getOrgId());
//		}
//		workLocationList = new FillWLocation(request).fillWLocation(getF_org());
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			workLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			workLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		payCycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());  
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) 
			|| strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT) || strUserType.equalsIgnoreCase(MANAGER))) {
//			System.out.println("CE/652--strD1="+strD1+"---strD2="+strD2);
			empNamesList = new FillEmployee(request).fillEmployeeNameForAttendance(strD1, strD2, uF.parseToInt(getLevel()),uF.parseToInt(getF_org()),uF.parseToInt(getLocation()));
		}

		return LOAD;
	}

	public String viewClockEntries(Connection con, String strPrevDate, String strNextDate) {
		
//		System.out.println("CE/661--viewClockEntries");

		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {

			String orgId = CF.getEmpOrgId(con, uF, getStrSelectedEmpId());
			
			Map<String, String> hmHolidays = new HashMap<String, String>();
			Map<String, String> hmHolidayDates = new HashMap<String, String>();
			Map<String, Map<String, String>> hmWLocationHolidaysName = new HashMap<String, Map<String, String>>();
			CF.getHolidayList(con,request, strD1, strD2, CF, hmHolidayDates, hmHolidays, true);
//			getWLocationHolidayList(strD1, strD2, CF, hmWLocationHolidaysColour, hmWLocationHolidaysName, hmWLocationHolidaysWeekEnd);
			CF.getWLocationHolidayList(con, uF, strD1, strD2, CF, null, hmWLocationHolidaysName, null);
			
			//System.out.println("hmHolidays==="+hmHolidays);
			
			Map<String, String> hmShiftBreak = CF.getShiftBreak(con,uF,orgId);
			if(hmShiftBreak == null) hmShiftBreak = new HashMap<String, String>();
			Map<String, String> hmWorkLocationLunchDeduction = CF.getWorkLocationLunchDeductionByEmp(con, uF, getStrSelectedEmpId());
			if(hmWorkLocationLunchDeduction == null) hmWorkLocationLunchDeduction = new HashMap<String, String>();
			if(!uF.parseToBoolean(hmWorkLocationLunchDeduction.get("IS_BREAK_TIME_POLICY"))){
				String strLunchDeduction = hmWorkLocationLunchDeduction.get("LUNCH_BREAK_DEDUCT");
				request.setAttribute("strDefaultLunchDeduction", strLunchDeduction);
				request.setAttribute("hmShiftBreak", hmShiftBreak);
			}
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			if(hmEmpLevelMap==null)hmEmpLevelMap = new HashMap<String,String>();	
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEndList = CF.getWeekEndDateList(con, strD1, strD2, CF, uF,hmWeekEndHalfDates,null);
//			System.out.println(getStrSelectedEmpId() + " -- hmWeekEndList ===>> " + hmWeekEndList);
			
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, strD1, strD2,alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEndList,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con, null, null);

			List<String> _alHolidays = new ArrayList<String>();
			Map<String, String> _hmHolidaysColour = new HashMap<String, String>();
			Map<String, String> hmEmpType = CF.getEmpTypeMap(con);
			Map<String, String> hmServices = CF.getServicesMap(con, true);

			Map<String, String> hmLunchDeductionService = CF.getLunchDeductionServiceDetails(con);

			List _alDay = new ArrayList();
			List _alDate = new ArrayList();

			Map<String, String> hmLunchDeduction = new HashMap<String, String>();
			CF.getDeductionTime(con, hmLunchDeduction);
			
			pst = con.prepareStatement(selectDates);
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("CE/715--pst="+pst);
			rs = pst.executeQuery();
			java.sql.Date dtMin = null;
			java.sql.Date dtMax = null;
			int ii = 0;
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
//				System.out.println("CE/745--pst="+pst);
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
			new CommonFunctions(CF).getRosterHoursMap(con,dtMin, dtMax, hmRosterHours, hmRosterServices);
			Map hmRosterHoursEmp = (Map) hmRosterHours.get(getStrSelectedEmpId());
			if (hmRosterHoursEmp == null) {
				hmRosterHoursEmp = new HashMap();
			}

			String strSelectedEmpType = null;
			String strWLocationId = null;
			pst = con.prepareStatement(selectEmployee2Details);
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
			rs = pst.executeQuery();
			String strEmpServiceId = null;
			while (rs.next()) {
				strSelectedEmpType = rs.getString("emptype");
				strWLocationId = rs.getString("wlocation_id");
				if(uF.parseToInt(rs.getString("emp_id")) > 0 && rs.getString("service_id")!=null && !rs.getString("service_id").trim().equals("") && !rs.getString("service_id").trim().equalsIgnoreCase("NULL")){
					String[] tmp =  rs.getString("service_id").trim().split(",");
					strEmpServiceId = tmp[1].trim();
				}   
			}
			rs.close();
			pst.close();
			request.setAttribute("strEmpServiceId", strEmpServiceId);
			
			String strLevelId = hmEmpLevelMap.get(getStrSelectedEmpId());
			
			Map hmRosterLunchDeduction = new HashMap();
			Map hmRosterStart = new HashMap();
			Map hmRosterEnd = new HashMap();

			List alDateServices_CE = new ArrayList();
			Map hmDateServices_CE = new HashMap();

			Map<String, Map<String, String>> hmEarlyLateReporting = new HashMap<String, Map<String, String>>();
			new CommonFunctions(CF).getEarlyLateReporting(con,dtMin, dtMax, hmEarlyLateReporting);
			
			
			String strOldEmpId = null;
			String strNewEmpId = null;
			String strServiceId = null;
			String strDateNew = null;
			String strDateOld = null;

			List alDateServices_TS = new ArrayList();
			Map hmDateServices_TS = new HashMap();

			List alHours = new ArrayList();
//			Map<String, Map<String, String>> hmLeaveDatesType = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmLeavesMap = CF.getLeaveDates(strD1, strD2, CF, hmLeaveDatesType, false, null);
			Map<String,Map<String,String>> leaveEmpMap=getLeaveDetails(con,strD1, strD2,uF);

			Map<String, String> hmLeaves = null;
			Map<String, String> hmLeavePaid = new HashMap<String, String>();
			Map<String, String> hmLeavesColour = new HashMap<String, String>();
			CF.getLeavesAttributes(con, uF, hmLeavesColour, null);
			hmLeavesColour.put("T", "green");
			
//			hmLeaves = hmLeavesMap.get(getStrSelectedEmpId());
			hmLeaves = leaveEmpMap.get(getStrSelectedEmpId());

			if (hmLeaves == null) {
				hmLeaves = new HashMap<String, String>();
			}
   
//			log.debug("getStrSelectedEmpId()==== 1 ==>" + getStrSelectedEmpId());

			CF.getEmployeePaidMap(con, getStrSelectedEmpId(), hmLeavePaid, null);
			
			pst = con.prepareStatement(selectRosterDetails1);
			pst.setDate(1, dtMin);
			pst.setDate(2, dtMax);
			pst.setInt(3, uF.parseToInt(getStrSelectedEmpId()));
//			System.out.println("CE/852--pst ===>> " + pst);
			rs = pst.executeQuery();
			
			boolean isFixedAdded = false;
			Map<String, String> hmRosterShiftId = new HashMap<String, String>();
			String employmentEndDate = null;		//added by parvez date: 14-10-2021
			while (rs.next()) {

				isFixedAdded = false;
				strNewEmpId = rs.getString("emp_id");
				strServiceId = rs.getString("service_id");
				strDateNew = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat());

				//System.out.println("CE/865--empEndDate="+rs.getString("employment_end_date"));
				//===start parvez date: 14-10-2021===
				
				employmentEndDate = rs.getString("employment_end_date");
				//===end parvez date: 14-10-2021===

				if (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld)) {
					alDateServices_TS = new ArrayList();
				}
//				System.out.println("CE/877---hmLeaves="+hmLeaves);
//				if (strServiceId != null && !alDateServices_TS.contains(strServiceId) && !hmLeaves.containsKey(strDateNew)) {
				if (strServiceId != null && !alDateServices_TS.contains(strServiceId) && !hmLeaves.containsKey(strDateNew+"_L") && !hmLeaves.containsKey(strDateNew+"_T")) {
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

				hmRosterShiftId.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "_" + strServiceId,rs.getString("shift_id"));
			}
			rs.close();
			pst.close(); 
			
//			System.out.println("hmDateServices_TS ===>> " + hmDateServices_TS);
//			System.out.println("hmRosterShiftId ===> " + hmRosterShiftId);
			request.setAttribute("hmRosterShiftId", hmRosterShiftId);
//			System.out.println("CE/911--employmentEndDate="+employmentEndDate);
			//===start parvez date: 14-10-2021===
			request.setAttribute("employmentEndDate", employmentEndDate);
			//===end parvez date: 14-10-2021===
			

			Map<String, String> hmEmpCodeDesig = new HashMap<String, String>();

			Map<String, Map<String, String>> hmPayrollFT = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmPayrollPT = new HashMap<String, Map<String, String>>();

			//new CommonFunctions().getDailyRates(hmPayrollFT, hmPayrollPT);
			CF.getDailyRates(con,hmPayrollFT, hmPayrollPT);

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

//			log.debug("selectClockEntries===>" + pst);
//			System.out.println("CE/965---pst=="+pst);
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
//				if (nServiceIdNew > 0 && !alDateServices_TS.contains(nServiceIdNew + "")
//						&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
				if (nServiceIdNew > 0 && !alDateServices_TS.contains(nServiceIdNew + "")
						&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_L")
						&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_T")) {
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

//					if (strPayMode != null && strPayMode.equalsIgnoreCase("H") && !hmLeaves.containsKey(strDate)) {
					if (strPayMode != null && strPayMode.equalsIgnoreCase("H") && !hmLeaves.containsKey(strDate+"_L") && !hmLeaves.containsKey(strDate+"_T")) {

						double rate = uF.parseToDouble(strRate);
						double loading = uF.parseToDouble(strLoading);
						double rateLoading = 0.0;

						// hmDailyRate.put(strDate, strRate);

						log.debug("hmHolidays=" + hmHolidayDates);
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
//					} else if (!hmLeaves.containsKey(strDate)) {
					} else if (!hmLeaves.containsKey(strDate+"_L") && !hmLeaves.containsKey(strDate+"_T")) {

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
//					if (strWeek1Date != null && strWeek1Date.after(currentDate)
//							&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
					if (strWeek1Date != null && strWeek1Date.after(currentDate)
							&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_L")
							&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_T")) {
						_TOTALW1 += _TOTAL;
						_TOTALRosterW1 += uF.parseToDouble((String) hmRosterHoursEmp.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP,
								CF.getStrReportDateFormat())));
//					} else if (!hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
					} else if (!hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_L")
								&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat())+"_T")) {
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
			
			//System.out.println(" hmHoursActual=="+hmHoursActual);
			
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

//			log.debug(getStrSelectedEmpId() + "=strSelectedEmpType==" + strSelectedEmpType);

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

				log.debug(hrs + "=hmHolidays==" + hmHolidayDates);

				Set set = hmHolidayDates.keySet();
				Iterator it = set.iterator();
				while (it.hasNext()) {
					String strDate = (String) it.next();
					Date utDate = uF.getDateFormatUtil(strDate, CF.getStrReportDateFormat());

//					if (!hmHours.containsKey(strDate) && !hmLeaves.containsKey(strDate)) {
					if (!hmHours.containsKey(strDate) && !hmLeaves.containsKey(strDate+"_L") && !hmLeaves.containsKey(strDate+"_T")) {

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


				Set setLeaves = hmLeaves.keySet();
				Iterator itLeaves = setLeaves.iterator();
				while (itLeaves.hasNext()) {
//					String strDate = (String) itLeaves.next();
					String key = (String) itLeaves.next();
					String[] strLeaveDate = key.split("_");
					String strDate = (String) strLeaveDate[0];
					Date utDate = uF.getDateFormatUtil(strDate, DATE_FORMAT);

//					String strLeaveType = (String) hmLeaves.get(strDate);
					String strLeaveType = (String) hmLeaves.get(strDate+"_L")!=null ? hmLeaves.get(strDate+"_L") : hmLeaves.get(strDate+"_T");
					boolean isPaidLeave = uF.parseToBoolean((String) hmLeavePaid.get(strLeaveType));

//					hmStart.put(strDate + "_", (String) hmLeaves.get(strDate));
					hmStart.put(strDate + "_", strLeaveType);
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

			Map<String, String> hmFirstAidAllowance = new HashMap<String, String>();
			CF.getAllowanceMap(con,hmFirstAidAllowance);

			double dblAllowance = 0;
			if (hmFirstAidAllowance.containsKey(getStrSelectedEmpId())) {
				dblAllowance = CF.getAllowanceValue(con,_TOTALW1 + _TOTALW2, uF.parseToInt(getStrSelectedEmpId()));
			}

			log.debug("_PAYTOTAL=" + _PAYTOTAL);

			request.setAttribute("ALLOWANCE", uF.formatIntoTwoDecimal(dblAllowance));
			request.setAttribute("DEDUCTION", CF.getDeductionAmountMap(con,_PAYTOTAL) + "");
			request.setAttribute("hmWeekEndList", hmWeekEndList);
			request.setAttribute("strWLocationId", strWLocationId);
			request.setAttribute("strLevelId", strLevelId);
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

			//System.out.println("hmRosterWeekEndDates==>"+hmRosterWeekEndDates);
			
	
//********************************code for chart****************************************************
			getOverTimeDetails(con,uF,strD1,strD2);
			
			Map<String,String> hmCalculateOT =(Map<String,String>)request.getAttribute("hmCalculateOT");
			if(hmCalculateOT == null) hmCalculateOT = new HashMap<String, String>();
			
			Map<String,Map<String,String>> hmOvertimeType=(Map<String,Map<String,String>>)request.getAttribute("hmOvertimeType");
			if(hmOvertimeType==null)hmOvertimeType=new HashMap<String,Map<String,String>>();
			
			Map<String, String> hmApproveOT=(Map<String, String>)request.getAttribute("hmApproveOT");
			Map<String, String> hmCheckPayroll =(Map<String, String>)request.getAttribute("hmCheckPayroll");
			
			String locationstarttime=(String)request.getAttribute("locationstarttime");
			String locationendtime=(String)request.getAttribute("locationendtime");
			
			Map<String,String> hmActualOT=(Map<String,String>)request.getAttribute("hmActualOT");
			if(hmActualOT==null) hmActualOT=new HashMap<String, String>();
			
			
			int holidayCount=0;
			int weekoffCount=0;
			int weekoffHolidayCount=0;
			double ActualAtenWorkHrs=0;
			double dblTotalRosterHrs=0;
			double dblTotalActualHrs=0;
			double dblTotalOTHrs=0;
			double dblTotalRosterHrsexcludingWeekOf=0;
			
			Set<String> weeklyOffSet= (Set<String>)hmWeekEndList.get(strWLocationId);
			if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
			
			Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(getStrSelectedEmpId());
			if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
			
			
			for(int i = 0; i < _alDate.size(); i++) {
			
				List alDateServices = (List)hmDateServices_TS.get((String) _alDate.get(i));
				if(alDateServices==null){alDateServices=new ArrayList();alDateServices.add("-1");}
			
				for (int i1 = 0; i1 < alDateServices.size(); i1++) {
				
					String strDay = uF.getDateFormat((String) _alDate.get(i), CF.getStrReportDateFormat(), IConstants.DATE_FORMAT);
					if(strDay!=null)strDay=strDay.toUpperCase();
				
				
					String strBgColor = (String)hmHolidayDates.get((String) _alDate.get(i)+"_"+strWLocationId);
					if(strBgColor!=null){
						holidayCount++;
					}
				
					if(alEmpCheckRosterWeektype.contains(getStrSelectedEmpId())){
					
						if(rosterWeeklyOffSet.contains(strDay)){
							weekoffCount++;
						}
					}else if(weeklyOffSet.contains(strDay)){
						weekoffCount++;
					
				}	
				
				ActualAtenWorkHrs=uF.parseToDouble((String) hmRosterHoursEmp.get((String) _alDate.get(i)+"_"+(String)alDateServices.get(i1)));
				
				dblTotalRosterHrs+=uF.parseToDouble((String) hmRosterHoursEmp.get((String) _alDate.get(i)+"_"+(String)alDateServices.get(i1)));
				dblTotalRosterHrs = dblTotalRosterHrs > 0.0d ? uF.convertHoursMinsInDouble(dblTotalRosterHrs) : 0.0d;
				
				double dblHrsAttenActual = uF.parseToDouble((String) hmHoursActual.get((String) _alDate.get(i)+"_"+(String)alDateServices.get(i1)));
			
				dblTotalActualHrs+=dblHrsAttenActual; 
				dblTotalActualHrs = dblTotalActualHrs > 0.0d ? uF.convertHoursMinsInDouble(dblTotalActualHrs) : 0.0d;
		
		//**********************************code for to calculate OTHrs**********************
				if(((String) hmStart.get((String) _alDate.get(i)+"_"+(String)alDateServices.get(i1))!=null) && ((String) hmEnd.get((String) _alDate.get(i)+"_"+(String)alDateServices.get(i1))!=null)){
				  
					String otHrs="0.00";
				    if(hmCalculateOT != null && hmCalculateOT.containsKey(getStrSelectedEmpId()+"_"+uF.getDateFormat(""+uF.getDateFormat((String) _alDate.get(i),CF.getStrReportDateFormat()),IConstants.DBDATE,IConstants.DATE_FORMAT))){
				    	otHrs = uF.showData(hmCalculateOT.get(getStrSelectedEmpId()+"_"+uF.getDateFormat(""+uF.getDateFormat((String) _alDate.get(i),CF.getStrReportDateFormat()),IConstants.DBDATE,IConstants.DATE_FORMAT)),"");
				    //	System.out.println("otHrs==>"+otHrs);
				    	
				    } else {
				    	
					    Map<String,String> hmOvertime=null;
					    
					    String day=uF.getDateFormat(""+uF.getDateFormat((String) _alDate.get(i),CF.getStrReportDateFormat()),IConstants.DBDATE,IConstants.DATE_FORMAT);
					    if(hmHolidayDates!=null && hmHolidayDates.containsKey((String)_alDate.get(i)+"_"+strWLocationId)) {
					    	hmOvertime=hmOvertimeType.get("PH");
					    } else if(hmWeekEndList!=null && hmWeekEndList.containsKey(day+"_"+strWLocationId)) {
					    	hmOvertime=hmOvertimeType.get("BH");
					    } else {
					    	hmOvertime=hmOvertimeType.get("EH"); 
					    }
					    
					    if(hmOvertime==null) hmOvertime=new HashMap<String,String>();

					    if(hmOvertime.get("STANDARD_WKG_HRS")!=null && hmOvertime.get("STANDARD_WKG_HRS").equals("RH")) {
					    
					    	Time entryTime = uF.getTimeFormat((String) _alDate.get(i)+ " "+(String) hmStart.get((String) _alDate.get(i)+"_"+(String)alDateServices.get(i1)),CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
							Time rosterEndTime = uF.getTimeFormat((String) _alDate.get(i)+ " "+(String) hmRosterEnd.get((String) _alDate.get(i)+"_"+(String)alDateServices.get(i1)),CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
							Time endTime = uF.getTimeFormat((String) _alDate.get(i)+ " "+(String) hmEnd.get((String) _alDate.get(i)+"_"+(String)alDateServices.get(i1)),CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
							
							//System.out.println("entryTime=="+entryTime);
							//System.out.println("rosterEndTime=="+rosterEndTime);
							//System.out.println("endTime=="+endTime);

							
							long milliseconds1 = entryTime.getTime();
							long milliseconds2 = rosterEndTime.getTime();
							long milliseconds3 = endTime.getTime();
								
							if(milliseconds3>=milliseconds2){
								
								double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds2));
								double actualTime=uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
								double bufferTime=uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
								double ottime = (actualTime-dbl) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(dbl),uF.formatIntoTwoDecimal(actualTime))): 0.0d;
								ottime = ottime > 0.0d ? uF.convertHoursMinsInDouble(ottime) : 0.0d;
								if(ottime>=bufferTime){
									double minOT=uF.parseToDouble(hmOvertime.get("MIN_OVER_TIME"));
									double otTime=(ottime-bufferTime);
									if(otTime>=minOT){
										double dblOTInMinute = uF.convertHoursIntoMinutes1(otTime);
										int nRoundOffPolicy = uF.parseToInt(hmOvertime.get("ROUND_OFF_OVERTIME"));
										double dblCalTime = otTime;
										if(nRoundOffPolicy > 0){
											double dblCalRoundOff = dblOTInMinute / nRoundOffPolicy;
											String strTotal = uF.formatIntoTwoDecimalWithOutComma(dblCalRoundOff);
											
											if(strTotal!=null && strTotal.contains(".") && strTotal.indexOf(".")>0) {
												String str11 = strTotal.replace(".", ":");
												String[] tempTotal = str11.split(":");
												double dblHr = uF.parseToDouble(tempTotal[1]);
												if(dblHr > 0){
													double dblMain = (uF.parseToDouble(tempTotal[0]))*nRoundOffPolicy;
													double dblHour = uF.convertMinutesIntoHours(dblMain);
													strTotal = ""+dblHour;
													otHrs=strTotal;
												} else {
													double dblMain = (uF.parseToDouble(tempTotal[0]))*nRoundOffPolicy;
													double dblHour = uF.convertMinutesIntoHours(dblMain);
													strTotal = ""+dblHour;
													otHrs=strTotal;
												}
											} else {
												otHrs=""+otTime;		
											}
										} else {
											otHrs=""+otTime;
										}										
									}
								}
							}
						} else if(hmOvertime.get("STANDARD_WKG_HRS")!=null && hmOvertime.get("STANDARD_WKG_HRS").equals("SWH")) {
//							System.out.println("in SWH =================>> (String) _alDate.get(i) ================>> " + (String) _alDate.get(i));
							Time entryTime = uF.getTimeFormat((String) _alDate.get(i)+ " "+(String) hmStart.get((String) _alDate.get(i)+"_"+(String)alDateServices.get(i1)),CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
							Time wlocationEndTime = uF.getTimeFormat((String) _alDate.get(i)+ " "+locationendtime,CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
							Time endTime = uF.getTimeFormat((String) _alDate.get(i)+ " "+(String) hmEnd.get((String) _alDate.get(i)+"_"+(String)alDateServices.get(i1)),CF.getStrReportDateFormat()+" "+IConstants.DBTIME);
							
							long milliseconds1 = entryTime.getTime();
							long milliseconds2 = wlocationEndTime.getTime();
							long milliseconds3 = endTime.getTime();
//							System.out.println("in SWH -- milliseconds1 ===>> " + milliseconds1 + " -- milliseconds2 ===>> " + milliseconds2 + " -- milliseconds3 ===>> " + milliseconds3);
							if(milliseconds3>=milliseconds2) {
								double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds2));
								dbl = dbl > 0.0d ? uF.convertHoursMinsInDouble(dbl) : 0.0d;
								double actualTime=uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
								actualTime = actualTime > 0.0d ? uF.convertHoursMinsInDouble(actualTime) : 0.0d;
								double bufferTime=uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
								double ottime=actualTime-dbl;
//								System.out.println("in SWH -- ottime ===>> " + ottime + " -- bufferTime ===>> " + bufferTime);
								if(ottime>=bufferTime) {
									double minOT=uF.parseToDouble(hmOvertime.get("MIN_OVER_TIME"));
									double otTime = (ottime-bufferTime) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(bufferTime),uF.formatIntoTwoDecimal(ottime))): 0.0d;
									otTime = otTime > 0.0d ? uF.convertHoursMinsInDouble(otTime) : 0.0d;
//									System.out.println("in SWH -- ottime ===>> " + ottime + " -- minOT ===>> " + minOT);
									if(otTime>=minOT) {
										double dblOTInMinute = uF.convertHoursIntoMinutes1(otTime);
										int nRoundOffPolicy = uF.parseToInt(hmOvertime.get("ROUND_OFF_OVERTIME"));
										double dblCalTime = otTime;
//										System.out.println("in SWH -- nRoundOffPolicy ===>> " + nRoundOffPolicy);
										if(nRoundOffPolicy > 0) {
											double dblCalRoundOff = dblOTInMinute / nRoundOffPolicy;
											String strTotal = uF.formatIntoTwoDecimalWithOutComma(dblCalRoundOff);
//											System.out.println("in SWH -- strTotal ===>> " + strTotal);
											if(strTotal!=null && strTotal.contains(".") && strTotal.indexOf(".")>0) {
												String str11 = strTotal.replace(".", ":");
												String[] tempTotal = str11.split(":");
												double dblHr = uF.parseToDouble(tempTotal[1]);
//												System.out.println("in SWH -- dblHr ===>> " + dblHr);
												if(dblHr > 0) {
													double dblMain = (uF.parseToDouble(tempTotal[0]))*nRoundOffPolicy;
													double dblHour = uF.convertMinutesIntoHours(dblMain);
													strTotal = ""+dblHour;
													otHrs=strTotal;
												} else {
													double dblMain = (uF.parseToDouble(tempTotal[0]))*nRoundOffPolicy;
													double dblHour = uF.convertMinutesIntoHours(dblMain);
													strTotal = ""+dblHour;
													otHrs=strTotal;
												}
											} else {
												otHrs=""+otTime;		
											}
										} else {
											otHrs=""+otTime;
										}	
									}
								}
							}
//							System.out.println("otHrs ==================================>> " + otHrs);
						
					} else if(hmOvertime.get("STANDARD_WKG_HRS")!=null && hmOvertime.get("STANDARD_WKG_HRS").equals("F")) {
						
						double bufferTime=uF.parseToDouble(hmOvertime.get("FIXED_STWKG_HRS"))+uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
						double ottime=uF.parseToDouble((String) hmHours.get((String) _alDate.get(i)+"_"+(String)alDateServices.get(i1)));
						ottime = ottime > 0.0d ? uF.convertHoursMinsInDouble(ottime) : 0.0d;
						if(ottime>=bufferTime){
							double minOT=uF.parseToDouble(hmOvertime.get("MIN_OVER_TIME"));
							double otTime=(ottime-bufferTime);
							if(otTime>=minOT){
								double dblOTInMinute = uF.convertHoursIntoMinutes1(otTime);
								int nRoundOffPolicy = uF.parseToInt(hmOvertime.get("ROUND_OFF_OVERTIME"));
								double dblCalTime = otTime;
								if(nRoundOffPolicy > 0){
									double dblCalRoundOff = dblOTInMinute / nRoundOffPolicy;
									String strTotal = uF.formatIntoTwoDecimalWithOutComma(dblCalRoundOff);
									if(strTotal!=null && strTotal.contains(".") && strTotal.indexOf(".")>0) {
										String str11 = strTotal.replace(".", ":");
										String[] tempTotal = str11.split(":");
										double dblHr = uF.parseToDouble(tempTotal[1]);
										if(dblHr > 0){
											double dblMain = (uF.parseToDouble(tempTotal[0]))*nRoundOffPolicy;
											double dblHour = uF.convertMinutesIntoHours(dblMain);
											strTotal = ""+dblHour;
											otHrs=strTotal;
										} else {
											double dblMain = (uF.parseToDouble(tempTotal[0]))*nRoundOffPolicy;
											double dblHour = uF.convertMinutesIntoHours(dblMain);
											strTotal = ""+dblHour;
											otHrs=strTotal;
										}
									} else {
										otHrs=""+otTime;		
									}
								} else {
									otHrs=""+otTime;
								}	
							}
						}	
					}
			}
			if(hmApproveOT.get((String)_alDate.get(i)+"_"+getStrSelectedEmpId())!=null && !hmApproveOT.get((String)_alDate.get(i)+"_"+getStrSelectedEmpId()).equals("0.00")){
				otHrs=(String)hmApproveOT.get((String)_alDate.get(i)+"_"+getStrSelectedEmpId());
			}else if(hmActualOT.get(getStrSelectedEmpId()+"_"+(String)_alDate.get(i))!=null){
				otHrs=hmActualOT.get(getStrSelectedEmpId()+"_"+(String)_alDate.get(i));
			}if(hmCheckPayroll!=null && hmCheckPayroll.containsKey(getStrSelectedEmpId())){ 
				
			} else if(hmApproveOT.get((String)_alDate.get(i)+"_"+getStrSelectedEmpId())!=null && !hmApproveOT.get((String)_alDate.get(i)+"_"+getStrSelectedEmpId()).equals("0.00")){
					otHrs=(String)hmApproveOT.get((String)_alDate.get(i)+"_"+getStrSelectedEmpId());
			}
			
			double dblOT = uF.parseToDouble(otHrs) > 0.0d ? uF.parseToDouble(otHrs): 0.0d;
			dblTotalOTHrs += dblOT;
			dblTotalOTHrs = dblTotalOTHrs > 0.0d ? uF.convertHoursMinsInDouble(dblTotalOTHrs) : 0.0d;
				
		  }
				
	//*********************end of code for calculating OT HRs*********************
		}
	
	}
		
//	System.out.println("dblTotalRosterHrs=="+dblTotalRosterHrs);
	weekoffHolidayCount=weekoffCount+holidayCount;
	//System.out.println("weekoffCount=="+weekoffCount);	
	//System.out.println("holidayCount=="+holidayCount);
	//System.out.println("weekoffHolidayCount"+weekoffHolidayCount);
	//System.out.println("ActualAtenWorkHrs="+ActualAtenWorkHrs);
	//System.out.println("total actual in java="+uF.showTime(uF.convertInHoursMins(dblTotalActualHrs)));
	//System.out.println("dblTotalOTHrs=="+dblTotalOTHrs);
			
	dblTotalRosterHrsexcludingWeekOf=dblTotalRosterHrs-(weekoffHolidayCount*ActualAtenWorkHrs);
			
//	System.out.println("dblTotalRosterHrsexcludingWeekOf=="+dblTotalRosterHrsexcludingWeekOf);
			
	request.setAttribute("dblTotalActualHrs", ""+dblTotalActualHrs);
	request.setAttribute("dblTotalRosterHrsexcludingWeekOf", ""+dblTotalRosterHrsexcludingWeekOf);
	request.setAttribute("dblTotalOTHrs1", ""+dblTotalOTHrs);
			
	} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
			return ERROR;
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

	private Map<String, Map<String, String>> getLeaveDetails(Connection con,String strDate1,String strDate2,UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,Map<String,String>> getMap=new HashMap<String,Map<String,String>>();
		try{
			pst=con.prepareStatement("select lar.*,lt.leave_type_code from leave_application_register lar,leave_type lt where lar.leave_type_id=lt.leave_type_id and (is_modify is null or is_modify=false) and _date between ? and ? order by emp_id");
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate2, DATE_FORMAT));
			rs=pst.executeQuery();
			
			while(rs.next()){
				Map<String,String> a=getMap.get(rs.getString("emp_id"));
				if(a==null)a=new HashMap<String,String>();
		
		//===start parvez date: 04-08-2022===		
				double leaveNo = rs.getDouble("leave_no");
				if(leaveNo == 0.5){
					if(a.containsKey(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_L")){
						a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_L", (a.get(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_L")+" / "+rs.getString("leave_type_code")+"(HD)"));
					}else{
						a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_L", rs.getString("leave_type_code")+"(HD)");
					}
				} else{
					a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_L", rs.getString("leave_type_code"));
				}
		//===end parvez date: 04-08-2022===		
				
//				a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("leave_type_code"));
				getMap.put(rs.getString("emp_id"), a);
			}
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select * from travel_application_register where _date between ? and ? and (is_modify is null or is_modify=false) order by emp_id");
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate2, DATE_FORMAT));
			rs=pst.executeQuery();
			while(rs.next()){
				Map<String,String> a=getMap.get(rs.getString("emp_id"));
				if(a==null)a=new HashMap<String,String>();
				/*if(!a.containsKey(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT))){
					a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), "T");
					getMap.put(rs.getString("emp_id"), a);
				}*/
				
		//===start parvez date: 04-08-2022===
				if(!a.containsKey(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_T")){
					double travelNo = rs.getDouble("travel_no");
					if(travelNo == 0.5){
						a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_T", "T(HD)");
					}
					else{
						a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_T", "T");
					}
					
				} else{
					a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+"_T", "T");
				}
				getMap.put(rs.getString("emp_id"), a);
		//===end parvez date: 04-08-2022===		
				
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
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
		return getMap;
	}

	public String viewClockEntriesForManager() {
		
//		System.out.println("CE/1848--viewClockEntriesForManager");

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		con = db.makeConnection(con);
		try {

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
//			Map hmEmpRates = new CommonFunctions().getDailyRates(con);
			Map hmEmpDesig = CF.getEmpDesigMap(con);

			Map<String, String> hmHolidays = CF.getHolidayList(con,request);
			List<String> _alHolidays = new ArrayList<String>();
			Map<String, String> _hmHolidaysColour = new HashMap<String, String>();
			Map<String, String> hmEmpType = CF.getEmpTypeMap(con);
			Map<String, String> hmServices = CF.getServicesMap(con,true);

			List _alDay = new ArrayList();
			List _alDate = new ArrayList();

			
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
			new CommonFunctions(CF).getEarlyLateReporting(con,dtMin, dtMax, hmEarlyLateReporting);
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
	public Map<String,Map<String,String>> getLeaveDetails(String strDate1,String strDate2,UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String,Map<String,String>> getMap=new HashMap<String,Map<String,String>>();
		try{
			con=db.makeConnection(con);
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
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return getMap;
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

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	public String getProfileEmpId() {
		return profileEmpId;
	}

	public void setProfileEmpId(String profileEmpId) {
		this.profileEmpId = profileEmpId;
	}

}