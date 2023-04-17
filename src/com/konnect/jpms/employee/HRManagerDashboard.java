package com.konnect.jpms.employee;

import java.sql.Connection;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.konnect.jpms.charts.BarChart;
import com.konnect.jpms.charts.SemiCircleMeter;
import com.konnect.jpms.performance.Appraisal;
import com.konnect.jpms.recruitment.JobReport;
import com.konnect.jpms.reports.AttendanceReport;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class HRManagerDashboard implements IStatements {

	HttpServletRequest request;
	HttpSession session;
	public CommonFunctions CF;
	String strEmpId;
	

	HRManagerDashboard(HttpServletRequest request, HttpSession session, CommonFunctions CF, String strEmpId) {
		this.request = request;
		this.session = session; 
		this.CF = CF;
		this.strEmpId = strEmpId; 
	}   
	 
	public String loadDashboard() {

		Database db = new Database(); 
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			String strUserType = (String)session.getAttribute(USERTYPE);
			request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmployeeMap = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map<String, String> hmDesigMap = CF.getDesigMap(con);
			Map<String, String> hmEmpProfileImageMap = CF.getEmpProfileImage(con);
//			Map<String, String> hmServicesMap = CF.getServicesMap(true);
			Map<String, String> hmDepartmentMap = CF.getDeptMap(con);
			Map<String, Map<String, String>> hmWorkLocationMap = CF.getWorkLocationMap(con);
			Map<String, String> hmLevelMap = CF.getEmpLevelMap(con);
			
			pst = con.prepareStatement(selectEmployee1V); 
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();

			if (rs.next()) {
				request.setAttribute("EMPCODE", rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				request.setAttribute("EMPNAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				request.setAttribute("DATE", uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", "yyyy-MM-dd", "EEEE, MMMM dd,yyyy"));
				request.setAttribute("IMAGE", ((rs.getString("emp_image")!=null && rs.getString("emp_image").length()>0)?rs.getString("emp_image"):"avatar_photo.png"));
				request.setAttribute("DEPT", rs.getString("dept_name"));
				request.setAttribute("WL_NAME", rs.getString("wlocation_name"));
				request.setAttribute("EMAIL", rs.getString("emp_email"));
			}
			rs.close();
			pst.close();

//			pst = con.prepareStatement(selectEmployee2V);
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//
//			rs = pst.executeQuery();
//
//
//			if (rs.next()) {
//				request.setAttribute("DESIG", rs.getString("desig_name"));
//				request.setAttribute("WL_NAME", rs.getString("wlocation_name"));
//
//			}

			pst = con.prepareStatement(selectEmployee3V);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			if (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				request.setAttribute("MANAGER", rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();

			
			pst = con.prepareStatement(selectMyClockEntries);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();

			Map<String, Map<String, String>> hmMyAttendence = new LinkedHashMap<String, Map<String, String>>();
			Map<String, String> hmAttendance = new HashMap<String, String>();
			
			String strDateNew = "";
			String strDateOld = "";
			
			while (rs.next()) {
				
				strDateNew = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat());
				if(strDateNew!=null && !strDateNew.equalsIgnoreCase(strDateOld)){
					hmAttendance = new HashMap<String, String>();
				}
				
				if("IN".equalsIgnoreCase(rs.getString("in_out"))){
					hmAttendance.put("IN", uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				}else{
					hmAttendance.put("OUT", uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
				}
				
				hmMyAttendence.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()), hmAttendance);
				strDateOld = strDateNew;
				
			}
			rs.close();
			pst.close();
			request.setAttribute("hmMyAttendence", hmMyAttendence);
			
			
			
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int Day = cal.get(Calendar.DAY_OF_MONTH);
			int MinDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			int MaxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			/*
			pst = con.prepareStatement(selectPresentDays1_HRManager);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), "yyyy-MM-dd"));
			pst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), "yyyy-MM-dd"));

			rs = pst.executeQuery();
			double []PRESENT_ABSENT_DATA = new double[2];
			String []PRESENT_ABSENT_LABEL = new String[2];
			if (rs.next()) {
				request.setAttribute("PRESENT_COUNT", rs.getString("count"));
				request.setAttribute("ABSENT_COUNT", Day - uF.parseToInt(rs.getString("count")));
				
				PRESENT_ABSENT_DATA[0] = rs.getDouble("count");
				PRESENT_ABSENT_DATA[1] = Day - uF.parseToInt(rs.getString("count"));
				
				PRESENT_ABSENT_LABEL[0] = "Worked";
				PRESENT_ABSENT_LABEL[1] = "Absent";
			}

			
			StringBuilder sb = new StringBuilder();
			sb.append("['Present', "+PRESENT_ABSENT_DATA[0]+"], ['Absent', "+PRESENT_ABSENT_DATA[1]+"]");
			
			request.setAttribute("CHART_WORKED_ABSENT_C", sb.toString());

			pst.close();
			
			*/
			
			
			
			pst = con.prepareStatement(selectApprovalsCount);
			pst.setDate(1, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), "yyyy-MM-dd"));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), "yyyy-MM-dd"));
			pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			if (rs.next()) {
				
				if(uF.parseToInt(rs.getString("approved")) == 1){
					request.setAttribute("APPROVED_COUNT", rs.getString("count"));
				}else if(uF.parseToInt(rs.getString("approved")) == -1){
					request.setAttribute("DENIED_COUNT", rs.getString("count"));
				}else if(uF.parseToInt(rs.getString("approved")) == -2){
					request.setAttribute("WAITING_COUNT", rs.getString("count"));
				}  
			}
			rs.close();
			pst.close();
			
			
			double []pending = new double[1];
			double []approved = new double[1];
			double []denied = new double[1];
			String []label = new String[]{""};
			
			pst = con.prepareStatement(selectApprovalsCountForHRManager);
			pst.setDate(1, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), "yyyy-MM-dd"));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), "yyyy-MM-dd"));
			pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			int total=0;
			while (rs.next()) {
				
				if(uF.parseToInt(rs.getString("approved")) == 1){
					request.setAttribute("EMP_APPROVED_COUNT", rs.getString("count"));
					total+=uF.parseToInt(rs.getString("count"));
					approved[0] = uF.parseToInt(rs.getString("count"));
				}else if(uF.parseToInt(rs.getString("approved")) == -1){
					request.setAttribute("EMP_DENIED_COUNT", rs.getString("count"));
					total+=uF.parseToInt(rs.getString("count"));
					denied[0] = uF.parseToInt(rs.getString("count"));
				}else if(uF.parseToInt(rs.getString("approved")) == -2){
					request.setAttribute("EMP_WAITING_COUNT", rs.getString("count"));
					total+=uF.parseToInt(rs.getString("count"));
					pending[0] = uF.parseToInt(rs.getString("count"));
				}  
			}
			rs.close();
			pst.close();
			
			request.setAttribute("CHART_APPROVALS", new BarChart().getMulitCharts(pending, approved, denied, label));
			
			request.setAttribute("TOTAL", total+"");
			
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			
			pst = con.prepareStatement(selectPendingApprovalsHRManager);			
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone() , 7));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst==Attendance=>"+pst);
			List alReasons = new ArrayList();
			rs = pst.executeQuery();
			while (rs.next()) {  
				
				String strDate = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DBDATE);
				
				if(strDate!=null && strDate.equals(strToday)){
					strDate = "<span>today</span>";
				}else if(strDate!=null && strDate.equals(strYesterday)){
					strDate = "<span>yesterday</span>";
				}else {
					strDate = "on "+ uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, "EEEE");
					strDate = "<span>"+strDate.toLowerCase()+"</span>";
				}
				
				
				if("IN".equalsIgnoreCase(rs.getString("in_out"))){
					if(rs.getDouble("early_late")>0){ 
						alReasons.add((String)hmEmployeeMap.get(rs.getString("emp_id")) +", was late for office "+strDate+((rs.getString("reason")!=null && rs.getString("reason").length()>0)?" - \""+rs.getString("reason")+"\".":"."));
					}else if(rs.getDouble("early_late")<0){
						alReasons.add((String)hmEmployeeMap.get(rs.getString("emp_id"))+", has come early "+strDate+((rs.getString("reason")!=null && rs.getString("reason").length()>0)?" - \""+rs.getString("reason")+"\".":"."));
					}
					
				}else{
					if(rs.getDouble("early_late")>0){
						alReasons.add((String)hmEmployeeMap.get(rs.getString("emp_id"))+", has left late "+strDate+((rs.getString("reason")!=null && rs.getString("reason").length()>0)?" - \""+rs.getString("reason")+"\".":"."));
					}else if(rs.getDouble("early_late")<0){
						alReasons.add((String)hmEmployeeMap.get(rs.getString("emp_id"))+", has left early "+strDate+((rs.getString("reason")!=null && rs.getString("reason").length()>0)?" - \""+rs.getString("reason")+"\".":"."));
					}
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReasons",alReasons);
			
			
//			verifyClockDetails();
			
			/*
			List alNotice = new ArrayList();
			new CommonFunctions(CF).getNoticeList(con, uF, alNotice);
			session.setAttribute("NOTICE", alNotice);
			
			
			HolidayReport holidayReport = new HolidayReport();
			holidayReport.setServletRequest(request);
			holidayReport.viewHolidayReport();
			*/
			
			
			
			/**
			 ********************** 	Chart BAR ATTENDANCE START    *********************
			 * */
			
			AttendanceReport objAR = new AttendanceReport();
			objAR.setServletRequest(request);
			objAR.setF_org((String)session.getAttribute(ORGID));
			objAR.setF_org((String)session.getAttribute(WLOCATIONID));
//			objAR.attendanceReport(CF, uF, strUserType, session);
			objAR.attendanceReportForOneWeek(CF, uF, strUserType, session); 
			
			Map hmEmployeeAttendanceCount = (Map)request.getAttribute("hmEmployeeAttendanceCount");
			
			List alDates = (List)request.getAttribute("alDates");
			List alServicesTotal = (List)request.getAttribute("alServicesTotal");
			StringBuilder sbDatesAttendance = new StringBuilder();
			StringBuilder sbDatesAttendanceDate = new StringBuilder();
			
			
			
			
			
			sbDatesAttendance.append("{name: 'Came Early',data: [");
			for(int k=0; k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeAttendanceCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = 0;
				for(int s=0; s<alServicesTotal.size(); s++){
					String strServiceId = (String)alServicesTotal.get(s);
					nCount += uF.parseToInt((String)hm.get("EARLY_IN_"+strServiceId));
				}
				sbDatesAttendance.append(nCount);
//					sbDatesAttendance.append(uF.showData((String)hm.get("EARLY_IN_"+strServiceId), "0"));
				
				sbDatesAttendanceDate.append("'"+uF.getDateFormat((String)alDates.get(k), DATE_FORMAT, "dd MMM")+"'");
				if(k<alDates.size()-1){
					sbDatesAttendance.append(",");
					sbDatesAttendanceDate.append(",");
				}
			}
			sbDatesAttendance.append("]},");
			
			sbDatesAttendance.append("{name: 'Left Early',data: [");
			for(int k=0; k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeAttendanceCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = 0;
				for(int s=0; s<alServicesTotal.size(); s++){
					String strServiceId = (String)alServicesTotal.get(s);
					nCount += uF.parseToInt((String)hm.get("EARLY_OUT_"+strServiceId));
				}
				sbDatesAttendance.append(nCount);
//					sbDatesAttendance.append(uF.showData((String)hm.get("EARLY_OUT_"+strServiceId), "0"));
				if(k<alDates.size()-1){
					sbDatesAttendance.append(",");
				}
			}
			sbDatesAttendance.append("]},");
			
			sbDatesAttendance.append("{name: 'Came Late',data: [");
			for(int k=0; k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeAttendanceCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = 0;
				
				
//					System.out.println("hm= Came Late ="+hm);
				
				for(int s=0; s<alServicesTotal.size(); s++){
					String strServiceId = (String)alServicesTotal.get(s);
//						System.out.println("strServiceId ="+strServiceId);
					
					nCount += uF.parseToInt((String)hm.get("LATE_IN_"+strServiceId));
					
//						System.out.println("Count ="+uF.parseToInt((String)hm.get("LATE_IN_"+strServiceId)));
//						System.out.println("Total count ="+nCount);
				}
				sbDatesAttendance.append(nCount);
//					sbDatesAttendance.append(uF.showData((String)hm.get("LATE_IN_"+strServiceId), "0"));
				if(k<alDates.size()-1){
					sbDatesAttendance.append(",");
				}
			}
			sbDatesAttendance.append("]},");
			
			sbDatesAttendance.append("{name: 'Left Late',data: [");
			for(int k=0; k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeAttendanceCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = 0;
				for(int s=0; s<alServicesTotal.size(); s++){
					String strServiceId = (String)alServicesTotal.get(s);
					nCount += uF.parseToInt((String)hm.get("LATE_OUT_"+strServiceId));
				}
				sbDatesAttendance.append(nCount);
//					sbDatesAttendance.append(uF.showData((String)hm.get("LATE_OUT_"+strServiceId), "0"));
				if(k<alDates.size()-1){
					sbDatesAttendance.append(",");
				}
			}
			sbDatesAttendance.append("]},");
			
			sbDatesAttendance.append("{name: 'Came Ontime',data: [");
			for(int k=0; k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeAttendanceCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = 0;
				for(int s=0; s<alServicesTotal.size(); s++){
					String strServiceId = (String)alServicesTotal.get(s);
					nCount += uF.parseToInt((String)hm.get("ONTIME_IN_"+strServiceId));
				}
				sbDatesAttendance.append(nCount);
//					sbDatesAttendance.append(uF.showData((String)hm.get("ONTIME_IN_"+strServiceId), "0"));
				if(k<alDates.size()-1){
					sbDatesAttendance.append(",");
				}
			}
			sbDatesAttendance.append("]},");
			
			sbDatesAttendance.append("{name: 'Left Ontime',data: [");
			for(int k=0; k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeAttendanceCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = 0;
				for(int s=0; s<alServicesTotal.size(); s++){
					String strServiceId = (String)alServicesTotal.get(s);
					nCount += uF.parseToInt((String)hm.get("ONTIME_OUT_"+strServiceId));
				}
				sbDatesAttendance.append(nCount);
//					sbDatesAttendance.append(uF.showData((String)hm.get("ONTIME_OUT_"+strServiceId), "0"));
				if(k<alDates.size()-1){
					sbDatesAttendance.append(",");
				}
			}
			
			sbDatesAttendance.append("]}");
				
			
			request.setAttribute("sbDatesAttendance", sbDatesAttendance);
			request.setAttribute("sbDatesAttendanceDate", sbDatesAttendanceDate);
			
			/**
			 ********************** 	Chart PIE ATTENDANCE END    *********************
			 * */
			
			
			/**
			 ********************** 	Chart PIE SERVICES START    *********************
			 * */
			
			
			/*StringBuilder sbEmployeesInvolvedInService = new StringBuilder();
			StringBuilder sbTemp = new StringBuilder();
			Map<String, Map<String, String>> hmEmployeesInvolvedInServices = (Map)request.getAttribute("hmEmployeesInvolvedInServices");
			
			for(int d=0; d<1; d++){ // d==0 and d<1 is for current date only
				String strDate = (String)alDates.get(d);
				Map<String, String> hmTemp = hmEmployeesInvolvedInServices.get(strDate);
				if(hmTemp==null)hmTemp = new HashMap<String, String>();
				
				for(int s=0; s<alServicesTotal.size(); s++){
					String strServiceId = (String)alServicesTotal.get(s);
					
					if(hmServicesMap.get(strServiceId)!=null){
						sbTemp.append("['"+hmServicesMap.get(strServiceId)+"',   "+hmTemp.get(strServiceId)+"],");
					}
					
				}
				
			}
			sbEmployeesInvolvedInService.append(sbTemp.substring(0, sbTemp.length()-1));
			
			request.setAttribute("sbEmployeesInvolvedInService", sbEmployeesInvolvedInService.toString());
			*/
			/**
			 ********************** 	Chart PIE SERVICES END    *********************
			 * */
			
			
			/**
			 ********************** 	Chart PIE ISSUES START    *********************
			 * */
			
			StringBuilder sbEmployeesIssues = new StringBuilder();
			Map<String, Map<String, String>> hmEmployeesIssues = (Map)request.getAttribute("hmEmployeesIssues");
			
			for(int d=0; d<1; d++){ // d==0 and d<1 is for current date only
				String strDate = (String)alDates.get(d);
				Map<String, String> hmTemp = hmEmployeesIssues.get(strDate);
				if(hmTemp==null)hmTemp = new HashMap<String, String>();
				
				sbEmployeesIssues.append("['Approved',   "+uF.parseToInt(hmTemp.get("APPROVED"))+"],");
				sbEmployeesIssues.append("{name: 'Pending',y: "+uF.parseToInt(hmTemp.get("PENDING"))+",sliced: true,selected: true}");
			}
			
			request.setAttribute("sbEmployeesIssues", sbEmployeesIssues.toString());
			
			/**
			 ********************** 	Chart PIE ISSUES END    *********************
			 * */
			
			
			
			/**
			 ********************** UPCOMING LEAVE REQUESTS START ************************
			 * 
			 * */
			
			
			/*
			pst = con.prepareStatement(selectUpcomingLeaveHRManager);			
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setString(2, uF.getCurrentDate(CF.getStrTimeZone())+"");
			pst.setString(3, uF.getCurrentDate(CF.getStrTimeZone())+"");
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			
//			System.out.println("pst==Upcoming Leave=>"+pst);
			List alLeaves = new ArrayList();
			rs = pst.executeQuery();
			while (rs.next()) {
				alLeaves.add(hmEmployeeMap.get(rs.getString("emp_id"))+", is on leave from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM"));
			}
			pst.close();
			
			request.setAttribute("alLeaves",alLeaves);
			
			
			pst = con.prepareStatement(selectLeaveRequestHRManager);			
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			
//			System.out.println("pst==Upcoming Leave=>"+pst);
			List alLeaveRequest = new ArrayList();
			rs = pst.executeQuery();
			while (rs.next()) {
				String strDate = rs.getString("entrydate");
				
				if(strDate!=null && strDate.equals(strToday)){
					strDate = ", <span>today</span>";
				}else if(strDate!=null && strDate.equals(strYesterday)){
					strDate = ", <span>yesterday</span>";
				}else {
					strDate = " on "+ uF.getDateFormat(rs.getString("entrydate"), DBDATE, "EEEE");
					strDate = "<span>"+strDate.toLowerCase()+"</span>";
				}
				alLeaveRequest.add("<span style=\"width: 95%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied leave from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM")+strDate+"</span>"+
						"<span style=\"float: right;\"> <a style=\"float:right\" href=\"ManagerLeaveApproval.action?E="+rs.getString("leave_id")+"&LID="+hmLevelMap.get(rs.getString("emp_id"))+"\"><img src=\"images1/icons/icons/approve_icon.png\" alt=\"Approve\" title=\"Approve\" /></a></span>");
			}
			pst.close();
			
			request.setAttribute("alLeaveRequest",alLeaveRequest);
			*/
			
			/**
			 ********************** UPCOMING LEAVE REQUESTS END ************************
			 * 
			 * */
			
			
//			getLeaveRequests(con, uF, hmEmployeeMap, hmLevelMap);
//			getTravelRequests(con, uF, hmEmployeeMap, hmLevelMap);
//			List<String> alUserModulesList = new ArrayList<String>();
//			CF.getUserModulesList(con, alUserModulesList);
			String []arrEnabledModules = CF.getArrEnabledModules();
			
			getPendingExceptionCount(con, uF, hmEmployeeMap);
//			getPendingReimbursementsCount(con, uF);
//			getPendingRequisitionCount(con, uF);
			getPendingLeaveCount(con, uF);
			
//			getReimbursementRequests(con, uF, hmEmployeeMap);
//			getRequisitionRequests(con, uF, hmEmployeeMap);
			
//			getTasksCount(con, uF);
//			getTodaysReportSentCount(con, uF);
			getBestEmployee(con, uF);
			getDepartmentEmployeeCount(con, uF);
			getWlocationEmployeeCount(con, uF);
			getSkillsEmployeeCount(con, uF, 6);
//			getEmployeeTurnoverCount(con, uF);
			getPendingApprovalsCountDetails(con, uF, MONTH, YEAR, MinDay, MaxDay);
			getAchievements(con, uF, CF, hmEmployeeMap);
			
			
			//CF.getBirthday(con, uF, CF, hmEmployeeMap, request);
			getBirthday(con, uF, hmEmployeeMap);
			//CF.getUpcomingEvents(con, uF, CF, request);  
			CF.getDayThought(con, uF, CF, request);
		//	CF.getThoughtByEmp(CF, uF, strEmpId, request);
			CF.getAlertUpdates(CF, strEmpId, request,strUserType);
			
			request.setAttribute("hmEmployeeMap",hmEmployeeMap);
			request.setAttribute("hmDepartmentMap",hmDepartmentMap);  
			request.setAttribute("hmEmpDesigMap",hmEmpDesigMap);
			request.setAttribute("hmEmpProfileImageMap",hmEmpProfileImageMap);
			request.setAttribute("hmWorkLocationMap",hmWorkLocationMap);
			session.setAttribute("arrEnabledModules",arrEnabledModules);
			request.setAttribute("arrEnabledModules", arrEnabledModules);
			
			getProbationEndDate(con, uF, CF, hmEmployeeMap, request);
			
//			for(int i=0;arrEnabledModules!=null && i<arrEnabledModules.length;i++){
//				System.out.println(i+"====arrEnabledModules===>"+arrEnabledModules[i]);
//			}
			
			if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_ONBOARDING+"")>=0){
				JobReport objJobReport = new JobReport();
				objJobReport.setServletRequest(request);
				objJobReport.prepChart1Data();
				objJobReport.prepChart2Data();
				
				
				getRecruitmentDetails(con, uF, hmDesigMap);
				viewAllRequestList();
				viewAllJobProfilesList(uF);
			}
			
			if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_PEOPLE_MANAGEMENT+"")>=0){
				getNewJoineeEmp(uF,strUserType);
				getConfirmationEmp(uF,strUserType);
				getResignAndFinalDayEmp(uF, strUserType);
			}
			
			if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_CAREER_DEV_PLANNING+"")>=0){
				//getRecruitmentDetails(con, uF, hmDesigMap);
//				getTrainingDetails(con, uF, CF);
				getLearningDetails(con, uF, CF);
				getLearningGaps(con, uF, CF);
				getRecentlyAwardedEmp(uF);
			}
			if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_PERFORMANCE_MANAGEMENT+"")>=0){
				getPerformanceDetails(con, uF, CF);
				getGoalsDetails(con, uF, CF);
				
				Appraisal app = new Appraisal();
				app.CF=CF;
				app.strSessionEmpId = (String)session.getAttribute(EMPID);
				app.strSessionUserType = (String) session.getAttribute(USERTYPE);
				app.strSessionUserTypeID = (String) session.getAttribute(USERTYPEID);
				
				app.setServletRequest(request);
				app.getAppraisalDetails();
				app.getOrientationMember();
				app.getAppriesalSections();
				app.getExistUsersInAQA();
				app.getOrientTypeWiseIds();
			}
			
			getEmployeeSkill(con,uF,CF);
			getLeaveSummary(con, uF, CF, strUserType);
			getCompensationSummary(con, uF, CF);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return LOAD;
	}
	
	private void getTravelRequests(Connection con, UtilityFunctions uF, Map<String, String> hmEmployeeMap, Map<String, String> hmLevelMap) {
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			pst = con.prepareStatement("select entrydate,leave_id,a.emp_id,approval_from,approval_to_date,w.emp_id as approveby_id,leave_type_id from " +
					"(select elt.* from emp_leave_entry elt, employee_official_details eod where eod.emp_id = elt.emp_id " +
					" and wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) " +
					" and is_approved =0 and entrydate is not null and encashment_status = false) as a,work_flow_details w " +
					" where a.leave_id=w.effective_id and entrydate>=? and effective_type='"+WORK_FLOW_TRAVEL+"' order by a.entrydate ");			
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));
			rs = pst.executeQuery();
			List<String> alTravelRequest = new ArrayList<String>();
			while (rs.next()) {
				
				String strDate = rs.getString("entrydate");				
				if(strDate!=null && strDate.equals(strToday)){
					strDate = ", <span>today</span>";
				}else if(strDate!=null && strDate.equals(strYesterday)){
					strDate = ", <span>yesterday</span>";
				}else {
					strDate = " on "+ uF.getDateFormat(rs.getString("entrydate"), DBDATE, "EEEE");
					strDate = "<span>"+strDate.toLowerCase()+"</span>";
				}
				
				
				if(uF.parseToInt((String) session.getAttribute("EMPID"))>0 && uF.parseToInt((String) session.getAttribute("EMPID"))==uF.parseToInt(rs.getString("approveby_id"))){
					alTravelRequest.add("<span style=\"width: 95%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied travel from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM")+strDate+"" +
							" and submitted to you</span>"+
							/*"<span style=\"float: right;\"><a href=\"javascript:void(0);\" onclick=\"window.location='ApproveTravel.action?E="+rs.getString("leave_id")+"'; \"><img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" /></a></span>");*/
							"<span style=\"float: right;\"><a href=\"javascript:void(0);\" onclick=\"window.location='ApproveTravel.action?E="+rs.getString("leave_id")+"'; \"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i></a></span>");
					
					
				}else{
					alTravelRequest.add("<span style=\"width: 95%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied travel from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM")+strDate+"" +
							" and submitted to "+hmEmployeeMap.get(rs.getString("approveby_id"))+"</span>");
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alTravelRequest",alTravelRequest);
			
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

	private void getEmployeeSkill(Connection con, UtilityFunctions uF, CommonFunctions CF) {
		
		ResultSet rs = null;
		PreparedStatement pst=null;
		try {
			pst = con.prepareStatement("select count(emp_id) as count,skill_id from skills_description where skill_id is not null group by skill_id"); //emp_id,
			rs = pst.executeQuery();
			List<List<String>> skillwiseEmpCountGraphList = new ArrayList<List<String>>();
//			List<String> alSkillName = new ArrayList<String>();
//			Map<String,Integer> hmSkillCount=new HashMap<String, Integer>();
			int i=0;
			int otherCnt=0;
			List<String> innerList = new ArrayList<String>();
			while(rs.next()) {
				innerList = new ArrayList<String>();
				if(i<=8) {
					innerList.add(CF.getSkillNameBySkillId(con, rs.getString("skill_id")));
					innerList.add(""+rs.getInt("count"));
//					alSkillName.add(rs.getString("skills_name").trim());
//					hmSkillCount.put(rs.getString("skills_name").trim(), rs.getInt("count"));
				}else {
//					if(!alSkillName.contains("Others")){
//						alSkillName.add("Others");
//					}
//					int otherCnt=hmSkillCount.get("Others")!=null ? hmSkillCount.get("Others") : 0;
					otherCnt+=rs.getInt("count");
//					hmSkillCount.put("Others", otherCnt);
				}
				i++;
				skillwiseEmpCountGraphList.add(innerList);
			}
			rs.close();
			pst.close();
			
			if(i>8) {
				innerList = new ArrayList<String>();
				innerList.add("Others");
				innerList.add(""+otherCnt);
				skillwiseEmpCountGraphList.add(innerList);
			}
			
			request.setAttribute("skillwiseEmpCountGraphList", skillwiseEmpCountGraphList);
//			request.setAttribute("alSkillName",alSkillName);
//			request.setAttribute("hmSkillCount",hmSkillCount);			
			
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

	private void getLearningGaps(Connection con, UtilityFunctions uF,CommonFunctions CF) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		List<List<String>> learningGapList = new ArrayList<List<String>>();
		
		try {
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			String strUserType = (String) session.getAttribute(USERTYPE);
			
			
			
			Map<String, String> hmAppraisalName = new HashMap<String, String>();
			pst = con.prepareStatement("select appraisal_details_id,appraisal_name from appraisal_details ");
			rs = pst.executeQuery();
//			System.out.println("pst ===> "+pst);
			while (rs.next()) {
				hmAppraisalName.put(rs.getString("appraisal_details_id"), rs.getString("appraisal_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from learning_plan_details");
			rs=pst.executeQuery();
			Map<String,String> hmLearnPlanName=new HashMap<String, String>();
			while(rs.next()){
				hmLearnPlanName.put(rs.getString("learning_plan_id"),rs.getString("learning_plan_name"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from training_gap_details where appraisal_id > 0 and (attribute_id is not null or attribute_id > 0) and is_training_schedule=false");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===> "+pst);
			rs=pst.executeQuery();
			while(rs.next()) {
				if(!hmAppraisalName.containsKey(rs.getString("appraisal_id"))){
					continue;
				}
				List<String> innerList = new ArrayList<String>();
				innerList.add(hmEmpName.get(rs.getString("emp_id")));
//				innerList.add(hmAppraisalName.get(rs.getString("appraisal_id")));
				innerList.add(uF.showData(hmAppraisalName.get(rs.getString("appraisal_id")), "-")+ " (Aprraisal)");
				double dblGapScore = 0.0d; 
				if(rs.getString("actual_score") != null && rs.getString("required_score") != null) {
					dblGapScore = rs.getDouble("actual_score") - rs.getDouble("required_score");
				}
				innerList.add(""+Math.round(dblGapScore));
				
				learningGapList.add(innerList);
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select * from training_gap_details where learning_id >0 and (learning_attribute_ids is not null or learning_attribute_ids !='') and is_training_schedule=false ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===> "+pst);
			rs=pst.executeQuery();
			while(rs.next()) {
				if(!hmLearnPlanName.containsKey(rs.getString("learning_id"))){
					continue;
				}
				List<String> innerList = new ArrayList<String>();
				innerList.add(hmEmpName.get(rs.getString("emp_id")));
//				innerList.add(hmAppraisalName.get(rs.getString("appraisal_id")));
				innerList.add(uF.showData(hmLearnPlanName.get(rs.getString("learning_id")), "-")+ " (Learning)");
				double dblGapScore = 0.0d; 
				if(rs.getString("actual_score") != null && rs.getString("required_score") != null) {
					dblGapScore = rs.getDouble("actual_score") - rs.getDouble("required_score");
				}
				innerList.add(""+Math.round(dblGapScore));
				
				learningGapList.add(innerList);
			}
			rs.close();
			pst.close();
//			System.out.println("learningGapList ===> "+learningGapList);
			request.setAttribute("learningGapList", learningGapList);
//			request.setAttribute("schedule", ""+scheduleCnt);
		
			
			/*
			
			String strUserType = (String) session.getAttribute(USERTYPE);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select tp.training_title,ts.* from training_schedule ts, training_plan tp " +
					"where ? between ts.start_date and ts.end_date and ts.plan_id=tp.plan_id ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") ");
			}
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,uF.getCurrentDate(CF.getStrTimeZone()));
			rs=pst.executeQuery();
			int scheduleCnt=0;
			while(rs.next()) {
				Map<String, String> hmLearnGap=new HashMap<String, String>();
				hmLearnGap.put("TRAINING_TITLE",rs.getString("training_title"));
				
				String empIds = rs.getString("emp_ids") != null ? rs.getString("emp_ids").substring(1, rs.getString("emp_ids").length()-1) : "";
				String temp[] =empIds.split(",");
				hmLearnGap.put("NO_OF_PARTICIPANT",""+temp.length);
				
				outerList.add(hmLearnGap);
				
				scheduleCnt+=temp.length;
			}
			request.setAttribute("trainingGapList", outerList);
			request.setAttribute("schedule", ""+scheduleCnt);
		*/
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

	}

	private void getGoalsDetails(Connection con, UtilityFunctions uF, CommonFunctions cF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, Map<String, String>> hmManagerGoalCalDetailsCorporate = new HashMap<String, Map<String, String>>();
		try {
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<String>> hmCorporate=new LinkedHashMap<String, List<String>>();
			Map<String,String> hmAttribute=getAttributeMap(con);
				
			pst = con.prepareStatement("select * from goal_details where goal_type=1 and org_id = ? order by goal_id desc");
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(ORGID)));
			rs = pst.executeQuery();
			int corpGoalCount = 0;
//			System.out.println("pst ===> "+pst);
			while (rs.next()) {
				corpGoalCount++;
				List<String> cinnerList=new ArrayList<String>();
				cinnerList.add(rs.getString("goal_id"));
				cinnerList.add(rs.getString("goal_type"));
				cinnerList.add(rs.getString("goal_parent_id"));
				cinnerList.add(rs.getString("goal_title"));
				cinnerList.add(rs.getString("goal_objective"));
				cinnerList.add(rs.getString("goal_description"));
				cinnerList.add(uF.showData(hmAttribute.get(rs.getString("goal_attribute")),""));
				cinnerList.add(rs.getString("measure_type"));
				cinnerList.add(rs.getString("measure_currency_value"));
				cinnerList.add(rs.getString("measure_currency_id"));
				cinnerList.add(rs.getString("measure_effort_days"));
				cinnerList.add(rs.getString("measure_effort_hrs"));
				cinnerList.add(rs.getString("measure_type1"));
				cinnerList.add(rs.getString("measure_kra"));
				cinnerList.add(rs.getString("measure_currency_value1"));
				cinnerList.add(rs.getString("measure_currency1_id"));
				cinnerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat()) );
				cinnerList.add(rs.getString("is_feedback"));
				cinnerList.add(rs.getString("orientation_id"));
				cinnerList.add(rs.getString("weightage"));
				cinnerList.add(uF.showData(getAppendData(con, rs.getString("emp_ids"), hmEmpName), ""));
				cinnerList.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				cinnerList.add(rs.getString("user_id"));
				cinnerList.add(rs.getString("is_measure_kra"));
				cinnerList.add(rs.getString("measure_kra_days"));
				cinnerList.add(rs.getString("measure_kra_hrs"));
				cinnerList.add(rs.getString("grade_id"));
				cinnerList.add(rs.getString("level_id"));
				cinnerList.add(rs.getString("kra"));
				
				cinnerList.add(rs.getString("emp_ids"));
				String priority="";
				String pClass = "";
				if(rs.getString("priority")!=null && !rs.getString("priority").equals("")){
					if(rs.getString("priority").equals("1")){
						pClass = "high";
						priority="High";
					}else if(rs.getString("priority").equals("2")){
						pClass = "medium";
						priority="Medium";
					}else if(rs.getString("priority").equals("3")){
						pClass = "low";
						priority="Low";
					}
				}
				cinnerList.add(priority);
				cinnerList.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()) );
				cinnerList.add(pClass);
				
				hmCorporate.put(rs.getString("goal_id"), cinnerList);
				
				
				Map<String, String> hmManagerGoalCalDetailsParentCorporate = new HashMap<String, String>();
				getManagerGoalData(uF, rs.getString("goal_id"), hmManagerGoalCalDetailsParentCorporate);
				hmManagerGoalCalDetailsCorporate.put(rs.getString("goal_id"), hmManagerGoalCalDetailsParentCorporate);
				
			}
			rs.close();
			pst.close();
			request.setAttribute("corpGoalCount", ""+corpGoalCount);
			request.setAttribute("hmCorporate", hmCorporate);
			request.setAttribute("hmManagerGoalCalDetailsCorporate", hmManagerGoalCalDetailsCorporate);
//			System.out.println("hmManagerGoalCalDetailsCorporate =========> "+hmManagerGoalCalDetailsCorporate);

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

	private void getManagerGoalData(UtilityFunctions uF, String parentID, Map<String, String> hmManagerGoalCalDetailsParentCorporate){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String,Map<String,String>> hmManagerGoalCalCorporate = new HashMap<String, Map<String,String>>();
		Map<String,Map<String,Map<String,String>>> hmManagerGoalCalCorporateParent= new HashMap<String, Map<String,Map<String,String>>>();
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from goal_details where goal_parent_id = ? order by goal_id ");
			pst.setInt(1, uF.parseToInt(parentID));
			rs = pst.executeQuery();
			while (rs.next()) {
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_parent_id"));
				
				Map<String, String> hmCorporateGoalCalDetails = new HashMap<String, String>();
				getTeamGoalData(uF, rs.getString("goal_id"), hmCorporateGoalCalDetails);
				hmManagerGoalCalCorporate.put(rs.getString("goal_id"), hmCorporateGoalCalDetails);
				hmManagerGoalCalCorporateParent.put(rs.getString("goal_parent_id"), hmManagerGoalCalCorporate);
			}
			rs.close();
			pst.close();
			
			String alltwoDeciTotProgressAvg ="0";
	 		String alltotal100 ="100";
	 		String strtwoDeciTot = "0";
	 		double dblalltwoDeciTotProgressAvg = 0;
	 		double dblalltotal100 = 0;
	 		double dblstrtwoDeciTot = 0;
			Iterator<String> it1 = hmManagerGoalCalCorporateParent.keySet().iterator();
			while (it1.hasNext()) {
				String parentid =(String)it1.next();
				Map<String,Map<String,String>> hmManagerGoalCalCorporate1 = hmManagerGoalCalCorporateParent.get(parentid);
				Iterator<String> it2 = hmManagerGoalCalCorporate1.keySet().iterator();
				int cnt=0;
				while (it2.hasNext()) {
					String goalid =(String)it2.next();
					Map<String, String> hmManagerGoalCalDetails = hmManagerGoalCalCorporate1.get(goalid);
					dblalltwoDeciTotProgressAvg += uF.parseToDouble(hmManagerGoalCalDetails.get(goalid+"_PERCENT"));
					double tot100 = uF.parseToDouble(hmManagerGoalCalDetails.get(goalid+"_TOTAL"));
					if(tot100 == 0){
						tot100 = 100;	
					}
					dblalltotal100 += tot100;
					dblstrtwoDeciTot += uF.parseToDouble(hmManagerGoalCalDetails.get(goalid+"_STR_PERCENT"));
					cnt++;
				}
				double percentAmt = dblalltwoDeciTotProgressAvg / cnt;
				double totalAmt = dblalltotal100 / cnt;
				double strPercentAmt = dblstrtwoDeciTot / cnt;
				alltwoDeciTotProgressAvg = ""+Math.round(percentAmt);
				alltotal100 = ""+Math.round(totalAmt);
				strtwoDeciTot = ""+Math.round(strPercentAmt);
				hmManagerGoalCalDetailsParentCorporate.put(parentid+"_PERCENT", alltwoDeciTotProgressAvg);
				hmManagerGoalCalDetailsParentCorporate.put(parentid+"_TOTAL", alltotal100);
				hmManagerGoalCalDetailsParentCorporate.put(parentid+"_STR_PERCENT", strtwoDeciTot);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private void getTeamGoalData(UtilityFunctions uF, String parentID, Map<String, String> hmTeamGoalCalDetailsParentManager){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String,Map<String,String>> hmTeamGoalCalManager = new HashMap<String, Map<String,String>>();
		Map<String,Map<String,Map<String,String>>> hmTeamGoalCalManagerParent= new HashMap<String, Map<String,Map<String,String>>>();
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from goal_details where goal_parent_id = ? order by goal_id ");  
			pst.setInt(1, uF.parseToInt(parentID));
			rs = pst.executeQuery();
			while (rs.next()) {
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_parent_id"));
				
				Map<String, String> hmManagerGoalCalDetails = new HashMap<String, String>();
				getIndGoalData(uF, rs.getString("goal_id"), hmManagerGoalCalDetails);
				hmTeamGoalCalManager.put(rs.getString("goal_id"), hmManagerGoalCalDetails);
				hmTeamGoalCalManagerParent.put(rs.getString("goal_parent_id"), hmTeamGoalCalManager);
			}
			rs.close();
			pst.close();
			
			String alltwoDeciTotProgressAvg ="0";
	 		String alltotal100 ="100";
	 		String strtwoDeciTot = "0";
	 		double dblalltwoDeciTotProgressAvg = 0;
	 		double dblalltotal100 = 0;
	 		double dblstrtwoDeciTot = 0;
			Iterator it1 = hmTeamGoalCalManagerParent.keySet().iterator();
			while (it1.hasNext()) {
				String parentid =(String)it1.next();
				Map<String,Map<String,String>> hmTeamGoalCalManager1 = hmTeamGoalCalManagerParent.get(parentid);
				Iterator it2 = hmTeamGoalCalManager1.keySet().iterator();
				int cnt=0;
				while (it2.hasNext()) {
					String goalid =(String)it2.next();
					Map<String, String> hmTeamGoalCalDetails = hmTeamGoalCalManager1.get(goalid);
					dblalltwoDeciTotProgressAvg += uF.parseToDouble(hmTeamGoalCalDetails.get(goalid+"_PERCENT"));
					double tot100 = uF.parseToDouble(hmTeamGoalCalDetails.get(goalid+"_TOTAL"));
					if(tot100 == 0){
						tot100 = 100;	
					}
					dblalltotal100 += tot100;
					dblstrtwoDeciTot += uF.parseToDouble(hmTeamGoalCalDetails.get(goalid+"_STR_PERCENT"));
					cnt++;
				}
				double percentAmt = dblalltwoDeciTotProgressAvg / cnt;
				double totalAmt = dblalltotal100 / cnt;
				double strPercentAmt = dblstrtwoDeciTot / cnt;
				alltwoDeciTotProgressAvg = ""+Math.round(percentAmt);
				alltotal100 = ""+Math.round(totalAmt);
				strtwoDeciTot = ""+Math.round(strPercentAmt);
				hmTeamGoalCalDetailsParentManager.put(parentid+"_PERCENT", alltwoDeciTotProgressAvg);
				hmTeamGoalCalDetailsParentManager.put(parentid+"_TOTAL", alltotal100);
				hmTeamGoalCalDetailsParentManager.put(parentid+"_STR_PERCENT", strtwoDeciTot);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getIndGoalData(UtilityFunctions uF, String parentID, Map<String, String> hmIndGoalCalDetailsParent){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String,List<List<String>>> hmIndividual=new LinkedHashMap<String, List<List<String>>>();
		Map<String,Map<String,String>> hmIndGoalCalTeam= new HashMap<String, Map<String,String>>();
		Map<String,Map<String,Map<String,String>>> hmIndGoalCalTeamParent = new HashMap<String, Map<String,Map<String,String>>>();
		try {
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from goal_details where goal_parent_id = ? and is_measure_kra = true and measure_type !='' order by goal_id "); //  and goal_type != 5 if you want only team goals then check personalgoal condition
			pst.setInt(1, uF.parseToInt(parentID));
			rs = pst.executeQuery();
			while (rs.next()) {
				List<List<String>> outerList=hmIndividual.get(rs.getString("goal_parent_id"));
				if(outerList==null)outerList=new ArrayList<List<String>>();
				
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("goal_id"));
				innerList.add(rs.getString("goal_type"));
				innerList.add(rs.getString("goal_parent_id"));
				innerList.add(rs.getString("measure_type"));
				innerList.add(rs.getString("measure_currency_value"));
				innerList.add(rs.getString("measure_effort_days"));
				innerList.add(rs.getString("measure_effort_hrs"));
				innerList.add(rs.getString("emp_ids"));				
				outerList.add(innerList);
				
				Map<String, String> hmIndGoalCalDetails = new HashMap<String, String>();
				getIndividualGoalTargetCalculation(rs.getString("goal_id"), rs.getString("emp_ids"), rs.getString("measure_type"), rs.getString("measure_currency_value"),
						rs.getString("measure_effort_days"), rs.getString("measure_effort_hrs"), uF, hmIndGoalCalDetails);
				hmIndGoalCalTeam.put(rs.getString("goal_id"), hmIndGoalCalDetails);
				hmIndGoalCalTeamParent.put(rs.getString("goal_parent_id"), hmIndGoalCalTeam);
			}
			rs.close();
			pst.close();
			
			String alltwoDeciTotProgressAvg ="0";
	 		String alltotal100 ="100";
	 		String strtwoDeciTot = "0";
	 		double dblalltwoDeciTotProgressAvg = 0;
	 		double dblalltotal100 = 0;
	 		double dblstrtwoDeciTot = 0;
			Iterator it1 = hmIndGoalCalTeamParent.keySet().iterator();
			while (it1.hasNext()) {
				String parentid =(String)it1.next();
				Map<String,Map<String,String>> hmIndGoalCalTeam1 = hmIndGoalCalTeamParent.get(parentid);
				Iterator it2 = hmIndGoalCalTeam1.keySet().iterator();
				int cnt=0;
				while (it2.hasNext()) {
					String goalid =(String)it2.next();
					Map<String, String> hmIndGoalCalDetails = hmIndGoalCalTeam1.get(goalid);
					dblalltwoDeciTotProgressAvg += uF.parseToDouble(hmIndGoalCalDetails.get(goalid+"_PERCENT"));
					double tot100 = uF.parseToDouble(hmIndGoalCalDetails.get(goalid+"_TOTAL"));
					if(tot100 == 0){
						tot100 = 100;	
					}
					dblalltotal100 += tot100;
					dblstrtwoDeciTot += uF.parseToDouble(hmIndGoalCalDetails.get(goalid+"_STR_PERCENT"));
					cnt++;
				}
				double percentAmt = dblalltwoDeciTotProgressAvg / cnt;
				double totalAmt = dblalltotal100 / cnt;
				double strPercentAmt = dblstrtwoDeciTot / cnt;
				alltwoDeciTotProgressAvg = ""+Math.round(percentAmt);
				alltotal100 = ""+Math.round(totalAmt);
				strtwoDeciTot = ""+Math.round(strPercentAmt);
				hmIndGoalCalDetailsParent.put(parentid+"_PERCENT", alltwoDeciTotProgressAvg);
				hmIndGoalCalDetailsParent.put(parentid+"_TOTAL", alltotal100);
				hmIndGoalCalDetailsParent.put(parentid+"_STR_PERCENT", strtwoDeciTot);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getIndividualGoalTargetCalculation(String indGoalId, String empIds, String measureType, String targetAmt, String strTargetDays, String strTargetHrs, UtilityFunctions uF,Map<String, String> hmIndGoalCalDetails){
		String alltwoDeciTotProgressAvg ="0";
 		String alltotal100 ="100";
 		String strtwoDeciTot = "0";
 		String strTotTarget = "0";
 		String strTotDays = "0";
 		String strTotHrs = "0";
 		Map<String,String> hmTargetValue=getMaxAchievedTargetBYEmpAndGoalwise(uF);
		if(empIds !=null){
			List<String> emplistID=Arrays.asList(empIds.split(","));
			double alltotalTarget=0, allTotal=0, alltwoDeciTot=0, totTarget=0;
			int empListSize=0;
			int allTotHRS =0;
			for(int i=0; emplistID!=null && i<emplistID.size();i++){
				empListSize = emplistID.size()-1;		
			if(emplistID.get(i)!=null && !emplistID.get(i).equals("")){
			String target="0";
			if(hmTargetValue != null && hmTargetValue.get(emplistID.get(i)+"_"+indGoalId)!= null){
				target=hmTargetValue.get(emplistID.get(i)+"_"+indGoalId);
			}
			
			String twoDeciTotProgressAvg = "0";
			String twoDeciTot = "0";
			String total="100";
			double totalTarget=0;
			if(measureType!=null && !measureType.equals("Effort")){
				totalTarget=(uF.parseToDouble(target)/uF.parseToDouble(targetAmt))*100;
				twoDeciTot=uF.formatIntoTwoDecimal(totalTarget);
			}else{
				String t=""+uF.parseToDouble(target);
				String days="0";
				String hours="0";
				if(t.contains(".")){
					t=t.replace(".","_");
					String[] temp=t.split("_");
					days=temp[0];
					hours=temp[1];
				}	
				String targetDays = strTargetDays;
				String targetHrs = strTargetHrs;
				int daysInHrs = uF.parseToInt(days) * 8;
				int inttotHrs = daysInHrs + uF.parseToInt(hours);
				allTotHRS += inttotHrs;
				
				int targetDaysInHrs = uF.parseToInt(targetDays) * 8;
				int inttotTargetHrs = targetDaysInHrs + uF.parseToInt(targetHrs);
				if(inttotTargetHrs != 0){
					totalTarget= uF.parseToDouble(""+inttotHrs) / uF.parseToDouble(""+inttotTargetHrs) * 100;
				}
				twoDeciTot=uF.formatIntoTwoDecimal(totalTarget);
			}
				if(totalTarget > new Double(100) && totalTarget<=new Double(150)){
					double totalTarget1=(totalTarget/150)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="150";
				}else if(totalTarget > new Double(150) && totalTarget<=new Double(200)){
					double totalTarget1=(totalTarget/200)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="200";
				}else if(totalTarget > new Double(200) && totalTarget<=new Double(250)){
					double totalTarget1=(totalTarget/250)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="250";
				}else if(totalTarget > new Double(250) && totalTarget<=new Double(300)){
					double totalTarget1=(totalTarget/300)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="300";
				}else if(totalTarget > new Double(300) && totalTarget<=new Double(350)){
					double totalTarget1=(totalTarget/350)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="350";
				}else if(totalTarget > new Double(350) && totalTarget<=new Double(400)){
					double totalTarget1=(totalTarget/400)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="400";
				}else if(totalTarget > new Double(400) && totalTarget<=new Double(450)){
					double totalTarget1=(totalTarget/450)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="450";
				}else if(totalTarget > new Double(450) && totalTarget<=new Double(500)){
					double totalTarget1=(totalTarget/500)*100;
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget1);
					total="500";
				}else{
					twoDeciTotProgressAvg=uF.formatIntoTwoDecimal(totalTarget);
					if(uF.parseToDouble(twoDeciTotProgressAvg) > 100){
						twoDeciTotProgressAvg = "100";
						total=""+Math.round(totalTarget);
					}else{
						total="100";
					}
				}
				alltotalTarget += uF.parseToDouble(twoDeciTotProgressAvg);
				allTotal += uF.parseToDouble(total);
				alltwoDeciTot += uF.parseToDouble(twoDeciTot);
				totTarget += uF.parseToDouble(target);
			}
			}
			double alltotAvg = alltotalTarget/empListSize;;
			double alltot100Avg = allTotal/empListSize;
			double alltwoDeciTotAvg = alltwoDeciTot/empListSize;
			double allTotTagetAvg = totTarget/empListSize;
			int allTotHRSAvg = allTotHRS / empListSize;
			int avgDAYS = allTotHRSAvg / 8;
			int avgHRS  = allTotHRSAvg % 8;
			strTotDays = ""+avgDAYS;
			strTotHrs = ""+avgHRS;
			alltwoDeciTotProgressAvg = ""+Math.round(alltotAvg);
			alltotal100 = ""+Math.round(alltot100Avg);
			strtwoDeciTot = ""+Math.round(alltwoDeciTotAvg);
			strTotTarget = ""+Math.round(allTotTagetAvg);
		}
		hmIndGoalCalDetails.put(indGoalId+"_PERCENT", alltwoDeciTotProgressAvg);
		hmIndGoalCalDetails.put(indGoalId+"_TOTAL", alltotal100);
		hmIndGoalCalDetails.put(indGoalId+"_STR_PERCENT", strtwoDeciTot);
		hmIndGoalCalDetails.put(indGoalId+"_ACHIVED_TARGET", strTotTarget);
		hmIndGoalCalDetails.put(indGoalId+"_ACHIVED_DAYS", strTotDays);
		hmIndGoalCalDetails.put(indGoalId+"_ACHIVED_HRS", strTotHrs);
	}
	
	
	private Map<String, String> getMaxAchievedTargetBYEmpAndGoalwise(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> hmTargetValue = new HashMap<String,String>();
		try {
			con = db.makeConnection(con);
			
			
			Map<String, String> hmTargetID= new HashMap<String,String>();
			Map<String, String> hmTargetTmpValue= new HashMap<String,String>();
			pst = con.prepareStatement("select * from target_details where target_id in (select max(target_id) from target_details group by goal_id,emp_id)");
			rs= pst.executeQuery();
			while(rs.next()){
				hmTargetValue.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("amt_percentage"));
				hmTargetID.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("target_id"));
				hmTargetTmpValue.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("emp_amt_percentage"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmTargetValue", hmTargetValue);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmTargetValue;
	}
	
	
	
	private String getAppendData(Connection con, String strID, Map<String, String> mp) {
		StringBuilder sb = new StringBuilder();
		Map<String, String> hmDesignation = CF.getEmpDesigMap(con);
		if (strID != null && !strID.equals("")) {

			if (strID.contains(",")) {

				String[] temp = strID.split(",");

				for (int i = 0; i < temp.length; i++) {
					if (i == 0) {
						sb.append(mp.get(temp[i].trim())+"("+hmDesignation.get(temp[i].trim())+")");
					} else {
						sb.append("," + mp.get(temp[i].trim())+"("+hmDesignation.get(temp[i].trim())+")");
					}
				}
			} else {
				return mp.get(strID)+"("+hmDesignation.get(strID)+")";
			}

		} else {
			return null;
		}

		return sb.toString();
	}

	private Map<String, String> getAttributeMap(Connection con) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmAttribute=new HashMap<String, String>();
		try {
			pst = con.prepareStatement("select * from appraisal_attribute where status=true");
			rs=pst.executeQuery();
			
			while(rs.next()){
				hmAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();
			
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
		return hmAttribute;
	}

	private void getProbationEndDate(Connection con, UtilityFunctions uF, CommonFunctions CF, Map<String, String> hmEmployeeMap, HttpServletRequest request) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<String> alProbationEndDate = new ArrayList<String>();
		try {
			pst = con.prepareStatement("select ead.emp_id, effective_date from employee_activity_details ead, (select max(emp_activity_id) as emp_activity_id, emp_id from employee_activity_details where activity_id = 7 group by emp_id ) eaad where eaad.emp_activity_id = ead.emp_activity_id and effective_date between ? and ? order by effective_date limit 5");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(2, uF.getFutureDate(CF.getStrTimeZone(), 30));
			rs = pst.executeQuery();
			while(rs.next()){
				alProbationEndDate.add("<strong>"+hmEmployeeMap.get(rs.getString("emp_id"))+"</strong>'s probation end date is due on "+uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat())+". <a href=\"ProbationReport.action?emp_id="+rs.getString("emp_id")+"\" >Approve</a>");
			}
			rs.close();
			pst.close();
			request.setAttribute("alProbationEndDate", alProbationEndDate);
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
	
//	private void getTrainingDetails(Connection con, UtilityFunctions uF, CommonFunctions CF) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try {
//			Map<String, String> trainingNamehm = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from training_plan");
//			rs = pst.executeQuery();
//			while(rs.next()){
//				trainingNamehm.put(rs.getString("plan_id"), rs.getString("training_title"));
//			}
//			
//			List<List<String>> trainingDetails = new ArrayList<List<String>>();
//			pst = con.prepareStatement("select * from training_schedule where ? between start_date and end_date order by schedule_id");
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			rs = pst.executeQuery();
//			//System.out.println("pst :::::::::: "+pst);
//			while(rs.next()){
//				List<String> traininginner = new ArrayList<String>();
//				traininginner.add(trainingNamehm.get(rs.getString("plan_id")));
//				traininginner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
//				int totemp = uF.parseToInt(getAppraisalEmpCount(rs.getString("emp_ids")));
//				traininginner.add(totemp+"");
//				traininginner.add("0");
//				trainingDetails.add(traininginner);
//			}
//			request.setAttribute("trainingDetails", trainingDetails);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	
	private void getLearningDetails(Connection con, UtilityFunctions uF, CommonFunctions CF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			List<List<String>> learningDetails = new ArrayList<List<String>>();
//			pst = con.prepareStatement("select * from learning_plan_details where learning_plan_id in (select learning_plan_id from " +
//					"learning_plan_stage_details where ? between from_date and to_date group by learning_plan_id)");
			pst = con.prepareStatement("select * from learning_plan_details where is_publish = true and is_close = false");
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			//System.out.println("pst :::::::::: "+pst);
			while(rs.next()) {
				List<String> learningInner = new ArrayList<String>();
				learningInner.add(rs.getString("learning_plan_name"));
//				traininginner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
				int totemp = uF.parseToInt(getLearningEmpCount(rs.getString("learner_ids")));
				int ongoingEmp = getLearningPlanOngoingEmpCount(con, uF, rs.getString("learning_plan_id"));
				int pendingEmp = totemp - ongoingEmp;
				learningInner.add(totemp+"");
				learningInner.add(""+ongoingEmp);
				learningInner.add(pendingEmp+"");
				learningDetails.add(learningInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("learningDetails", learningDetails);
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
	
	
	public int getLearningPlanOngoingEmpCount(Connection con, UtilityFunctions uF, String lPlanId) {

		PreparedStatement pst = null;
		ResultSet rst = null;
		int count = 0;
		try {
			List<String> empIdList = new ArrayList<String>();
//			StringBuilder sbEmpIds = new StringBuilder();
			pst = con.prepareStatement("select count(distinct emp_id) as count, emp_id from course_read_details where learning_plan_id = ? group by emp_id");
			pst.setInt(1, uF.parseToInt(lPlanId));
			rst = pst.executeQuery();
//			System.out.println("pst course_read_details ===> " + pst);
			while (rst.next()) {
				if(!empIdList.contains(rst.getString("emp_id"))) {
				count += rst.getInt("count");
				empIdList.add(rst.getString("emp_id"));
//				sbEmpIds.append(rst.getString("emp_id")+",");
				}
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("select count(distinct emp_id) as count, tad.emp_id from training_attend_details tad where tad.learning_plan_id = ? " +
					"and tad.emp_id not in(select crd.emp_id from course_read_details crd where crd.learning_plan_id =? group by crd.emp_id) group by tad.emp_id");
			pst.setInt(1, uF.parseToInt(lPlanId));
			pst.setInt(2, uF.parseToInt(lPlanId));
			rst = pst.executeQuery();
//			System.out.println("pst training_attend_details ===> " + pst);
			while (rst.next()) {
				if(!empIdList.contains(rst.getString("emp_id"))) {
					count += rst.getInt("count");
					empIdList.add(rst.getString("emp_id"));
//					sbEmpIds.append(rst.getString("emp_id")+",");
					}
			}
			rst.close();
			pst.close();
			
			String empIds = getAppendData(empIdList);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(distinct emp_id) as count from assessment_question_answer where learning_plan_id = ? ");
			if(!empIds.equals("")){
				sbQuery.append("and emp_id not in("+ empIds +")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(lPlanId));
//			System.out.println("pst assessment_question_answer 1 ===> " + pst);
			rst = pst.executeQuery();
//			System.out.println("pst assessment_question_answer ===> " + pst);
			while (rst.next()) {
				count += rst.getInt("count");
			}
			rst.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
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
		return count;
	}
	
	
	public String getAppendData(List<String> strID) {
		StringBuilder sb = new StringBuilder();
		if (strID != null) {
			for (int i = 0; i < strID.size(); i++) {
				if (i == 0) {
					sb.append(strID.get(i));
				} else {
					sb.append("," + strID.get(i));
				}
			}
		} else {
			return null;
		}
		return sb.toString();
	}
	
	
	private void getPerformanceDetails(Connection con, UtilityFunctions uF, CommonFunctions CF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> freqNamehm = new HashMap<String, String>();
			pst = con.prepareStatement("select * from appraisal_frequency");
			rs = pst.executeQuery();
			while(rs.next()){
				freqNamehm.put(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name"));
			}
			rs.close();
			pst.close();
			
			List<List<String>> liveAppraisalDetails = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_details where is_publish = TRUE and is_close = false and " + // ? between from_date and to_date and
					"my_review_status = 0 order by appraisal_details_id");
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			//System.out.println("pst :::::::::: "+pst);
			while(rs.next()){
				List<String> appraisalinner = new ArrayList<String>();
				appraisalinner.add(rs.getString("appraisal_name"));
				appraisalinner.add(freqNamehm.get(rs.getString("frequency")));
				int totemp = uF.parseToInt(getLearningEmpCount(rs.getString("employee_id")));
				int totempfinal = uF.parseToInt(getEmpFinalAppraisal(con, rs.getString("appraisal_details_id")));
				int totunfinalemp = totemp - totempfinal;
				appraisalinner.add(totemp+"");
				appraisalinner.add(totempfinal+"");
				appraisalinner.add(totunfinalemp+"");
				liveAppraisalDetails.add(appraisalinner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("liveAppraisalDetails", liveAppraisalDetails);
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

	
private String getEmpFinalAppraisal(Connection con, String appraisalid) {
	PreparedStatement pst = null;
	ResultSet rs = null;
	UtilityFunctions uF = new UtilityFunctions();
	String empfinalcount="0";
	try {
		pst = con.prepareStatement("select count(*) as count from appraisal_final_sattlement where appraisal_id =? and if_approved = TRUE ");
		pst.setInt(1, uF.parseToInt(appraisalid));
		rs = pst.executeQuery();
		while(rs.next()){
			empfinalcount = rs.getString("count");
		}
		rs.close();
		pst.close();
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
	return empfinalcount;
}

private String getLearningEmpCount(String empList) {
	int empCount=0;
	try {
		if(empList != null && !empList.equals("")) {
			UtilityFunctions uF = new UtilityFunctions();
			List<String> alEmp = Arrays.asList(empList.split(","));
			for(int i=0;alEmp!=null && i < alEmp.size(); i++){
				if(uF.parseToInt(alEmp.get(i).trim()) == 0){
					continue;
				}
				empCount++;
			}
			
		}
	} catch (Exception e) {
		e.printStackTrace();
	}
	return empCount+"";
}

	
	private void getAchievements(Connection con, UtilityFunctions uF, CommonFunctions CF, Map<String, String> hmEmployeeMap) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			Map<String, String> hmGradeMap = new HashMap<String, String>();
			Map<String, String> hmDesigMap = new HashMap<String, String>();
			pst = con.prepareStatement("select * from designation_details dd, grades_details gd where dd.designation_id = gd.designation_id");
			rs = pst.executeQuery();
			while(rs.next()){
				hmGradeMap.put(rs.getString("grade_id"), rs.getString("grade_name"));
				hmDesigMap.put(rs.getString("designation_id"), rs.getString("designation_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from employee_activity_details ead, activity_details ad where ead.activity_id = ad.activity_id and emp_id in (select emp_id from employee_official_details where wlocation_id = ?) and is_achievements = true and ad.activity_id not in (16) order by effective_date desc limit 4");
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(WLOCATIONID)));
			rs = pst.executeQuery();
			List<String> alAchievements = new ArrayList<String>();
			while(rs.next()){
				CF.getAchievements(con, uF.showData(hmEmployeeMap.get(rs.getString("emp_id")), "Na"), uF.parseToInt(rs.getString("activity_id")), uF, alAchievements, uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()), hmGradeMap.get(rs.getString("grade_id")), hmDesigMap.get(rs.getString("desig_id")));
			}
			rs.close();
			pst.close();

			request.setAttribute("alAchievements", alAchievements);
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
	
	
	public void getTrainingProgramDetails(Connection con, UtilityFunctions uF, Map<String, String> hmDesigMap){
		PreparedStatement pst  = null;
		ResultSet rs = null;

		try {
			List<List<String>> alTrainings = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			pst = con.prepareStatement("");
			rs = pst.executeQuery();
			while(rs.next()){
				alInner = new ArrayList<String>();

			}
			rs.close();
			pst.close();
 			request.setAttribute("alTrainings", alTrainings);
			
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
	
	
	private void getRecentlyAwardedEmp(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			List<String> recentAwardedEmpList = new ArrayList<String>();
			
			StringBuilder strQuery = new StringBuilder();
//			strQuery.append("select emp_per_id,emp_fname,emp_lname,emp_image,learning_plan_id,training_id,assessment_id,certificate_status," +
//					"thumbsup_status,lpfd.added_by,lpfd.entry_date from learning_plan_finalize_details lpfd, employee_personal_details epd " +
//					"where lpfd.emp_id = epd.emp_per_id and lpfd.entry_date >= ? and (certificate_status = true or thumbsup_status = true) order by emp_fname,emp_lname");
			strQuery.append("select emp_per_id,emp_fname,emp_mname,emp_lname,emp_image,lpd.learning_plan_id,training_id,assessment_id,certificate_status," +
				"thumbsup_status,lpfd.added_by,lpfd.entry_date from learning_plan_finalize_details lpfd, employee_personal_details epd, learning_plan_details lpd " +
				"where lpfd.learning_plan_id =lpd.learning_plan_id and is_publish = true and lpfd.emp_id = epd.emp_per_id and lpfd.entry_date >= ? " +
				"and (certificate_status = true or thumbsup_status = true) order by emp_fname,emp_lname");
			pst = con.prepareStatement(strQuery.toString());
			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 90));
//			System.out.println("pst ====> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				StringBuilder sbNewjoineeList = new StringBuilder();
				String empimg = uF.showData(rs.getString("emp_image"), "avatar_photo.png");
				if(CF.getStrDocRetriveLocation()==null) { 
					sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #CCCCCC \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ DOCUMENT_LOCATION +empimg+"\" height=\"20\" width=\"20\"> </span>");
				} else { 
					sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #CCCCCC \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_22x22+"/"+empimg+"\" height=\"20\" width=\"20\"> </span>");
				}  
//				sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #000 \"><img src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +empimg+"\" height=\"20\" width=\"20\"> </span>");
				
				
				sbNewjoineeList.append("<span style=\"float: left; width: 92%; margin-left: 5px;\">");
				sbNewjoineeList.append("<span style=\"float: left; width: 100%; \">");
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				sbNewjoineeList.append(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				
				if(uF.parseToBoolean(rs.getString("certificate_status")) && uF.parseToBoolean(rs.getString("thumbsup_status"))) {
					sbNewjoineeList.append(" gets a certificate ");
//					sbNewjoineeList.append("<img src=\"images1/certificate_img.png\"/>");
					sbNewjoineeList.append("<a style=\"float: right; margin-top: 2px;\" onclick=\"viewCertificate('"+rs.getString("emp_per_id")+"','"+rs.getString("learning_plan_id")+"')\" " +
							"href=\"javascript:void(0)\" style=\"margin-left:10px\"><img src=\"images1/certificate_img.png\"></a>");
					sbNewjoineeList.append(" and thumbs up ");
					/*sbNewjoineeList.append("<img src=\"images1/thumbs_up.png\"/>");*/
					sbNewjoineeList.append("<i class=\"fa fa-thumbs-up\" aria-hidden=\"true\"></i>");
					
				} else if(uF.parseToBoolean(rs.getString("certificate_status"))) {
					sbNewjoineeList.append(" gets a certificate ");
//					sbNewjoineeList.append("<img src=\"images1/certificate_img.png\"/>");
					sbNewjoineeList.append("<a style=\"float: right; margin-top: 2px;\" onclick=\"viewCertificate('"+rs.getString("emp_per_id")+"','"+rs.getString("learning_plan_id")+"')\" " +
					"href=\"javascript:void(0)\" style=\"margin-left:10px\"><img src=\"images1/certificate_img.png\"></a>");
				} else if(uF.parseToBoolean(rs.getString("thumbsup_status"))) {
					sbNewjoineeList.append(" gets a thumbs up ");
					/*sbNewjoineeList.append("<img src=\"images1/thumbs_up.png\"/>");*/
					sbNewjoineeList.append("<i class=\"fa fa-thumbs-up\" aria-hidden=\"true\"></i>");
					
				}
				if(uF.parseToInt(rs.getString("training_id")) > 0) {
					String trainingName = CF.getTrainingNameByTrainingId(con, uF, rs.getString("training_id"));
					sbNewjoineeList.append(" for '"+trainingName+"' classroom training.");
				}
				if(uF.parseToInt(rs.getString("assessment_id")) > 0) {
					String assessmentName = CF.getAssessmentNameByAssessId(con, uF, rs.getString("assessment_id"));
					sbNewjoineeList.append(" for '"+assessmentName+"' assessment.");
				}
				sbNewjoineeList.append("</span>");
				sbNewjoineeList.append("<span style=\"float: left; width: 100%; font-size: 11px; font-style: italic;\">");
				String empName = CF.getEmpNameMapByEmpId(con, rs.getString("added_by"));
				sbNewjoineeList.append("Awarded by " + empName + " on " + uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
				sbNewjoineeList.append("</span>");
				sbNewjoineeList.append("</span>");
				recentAwardedEmpList.add(sbNewjoineeList.toString());
			}
			rs.close();
			pst.close();
			
			request.setAttribute("recentAwardedEmpList", recentAwardedEmpList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getNewJoineeEmp(UtilityFunctions uF,String strUserType) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String,List<String>> hmUserLocationAccessMap = new HashMap<String,List<String>>();
			Map<String,String> hmUserLocationMap = new HashMap<String,String>();
			StringBuilder sQuery = new StringBuilder();
			sQuery.append("select * from employee_official_details eod,user_details ud where eod.emp_id = ud.emp_id and status='ACTIVE' " +
			"and (usertype_id =7 or usertype_id =1) and eod.emp_id in(select emp_per_id from employee_personal_details where is_alive=true) order by user_id desc");
			pst = con.prepareStatement(sQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()){
				List<String> locationList = new ArrayList<String>();
				String accessLocations = (String)rs.getString("wlocation_id_access");
				if(accessLocations!= null && !accessLocations.equals("")){
					locationList = Arrays.asList(accessLocations.split(","));
				}else{
					locationList.add(rs.getString("wlocation_id"));
				}	
				hmUserLocationAccessMap.put(rs.getString("emp_id"),locationList);
				hmUserLocationMap.put(rs.getString("emp_id"),rs.getString("wlocation_id"));
			}
			
			rs.close();
			pst.close();
			
			List<String> newJoineeEmpList = new ArrayList<String>();
			Map<String, String> hmwLocation = CF.getWLocationMap(con, null, null);
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select emp_per_id,emp_fname,emp_mname,emp_lname,emp_image,supervisor_emp_id,wlocation_id,cad.candidate_joining_date from " +
					"employee_personal_details epd, candidate_application_details cad, employee_official_details eod where " +
					"epd.emp_per_id = eod.emp_id and epd.emp_per_id = cad.candididate_emp_id and emp_per_id in(select candididate_emp_id from " +
					"candidate_application_details where candidate_joining_date >= ? and candidate_joining_date <= ? and " +
					"candididate_emp_id is not null) order by candididate_emp_id desc");
			pst = con.prepareStatement(strQuery.toString());
			
			pst.setDate(1, uF.getPrevDate(uF.getCurrentDate(CF.getStrTimeZone())+"", 7));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst ===>>>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				StringBuilder sbNewjoineeList = new StringBuilder();
				String empimg = uF.showData(rs.getString("emp_image"), "avatar_photo.png");
				String supervisorName = CF.getEmpNameMapByEmpId(con, rs.getString("supervisor_emp_id"));
				String empDesignation = CF.getEmpDesigMapByEmpId(con, rs.getString("emp_per_id"));
				
//				sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #000 \"><img src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() + empimg +"\" height=\"20\" width=\"20\"> </span>");
				if(CF.getStrDocRetriveLocation()==null) { 
					sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #CCCCCC \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ DOCUMENT_LOCATION +empimg+"\" height=\"20\" width=\"20\"> </span>");
				} else { 
					sbNewjoineeList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #CCCCCC \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_22x22+"/"+empimg+"\" height=\"20\" width=\"20\"> </span>");
				}  
				sbNewjoineeList.append("<span style=\"float: left; width: 92%; margin-left: 5px;\">");
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				sbNewjoineeList.append(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				sbNewjoineeList.append(" designation is "+uF.showData(empDesignation, "-"));
				sbNewjoineeList.append(", manager is "+uF.showData(supervisorName, "-"));
				sbNewjoineeList.append(", work location is "+uF.showData(hmwLocation.get(rs.getString("wlocation_id")), "-"));
				sbNewjoineeList.append(" and joining date is "+uF.getDateFormat(rs.getString("candidate_joining_date"), DBDATE, CF.getStrReportDateFormat())+".");
				sbNewjoineeList.append("</span>");
				
				if(strUserType!=null && strUserType.equals(HRMANAGER)){
					List<String> locations = hmUserLocationAccessMap.get(strEmpId);
					if(locations!=null && locations.size()>0 && locations.contains(rs.getString("wlocation_id"))){
						newJoineeEmpList.add(sbNewjoineeList.toString());
					}else{
						String empLocation = hmUserLocationMap.get(strEmpId);
						if((empLocation!=null && empLocation.equals(rs.getString("wlocation_id"))) || rs.getString("wlocation_id") == null){
							newJoineeEmpList.add(sbNewjoineeList.toString());
						}
					}
				}else if(strUserType!=null && strUserType.equals(ADMIN)){
					newJoineeEmpList.add(sbNewjoineeList.toString());
				}
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("newJoineeEmpList", newJoineeEmpList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void viewAllJobProfilesList(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		List<String> jobProfileList = new ArrayList<String>();

		try {

			con = db.makeConnection(con);


			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder strQuery = new StringBuilder();

			strQuery.append("(select d.designation_id, r.existing_emp_count,r.recruitment_id,job_profile_updated_date,job_profile_updated_by,"
					+ "d.designation_name,g.grade_code,w.wlocation_name,r.no_position,r.effective_date,r.job_code,r.job_approval_status,"
					+ "di.dept_name,e.emp_fname,e.emp_mname,e.emp_lname,l.level_code,l.level_name,close_job_status"
					+ " ,r.custum_designation,r.priority_job_int from recruitment_details r left join grades_details g using(grade_id)"
					+ " join work_location_info w on r.wlocation=w.wlocation_id left join employee_personal_details e on"
					+ " r.added_by=e.emp_per_id left join department_info di on r.dept_id=di.dept_id "
					+ " left  join designation_details d on r.designation_id=d.designation_id left join level_details l on r.level_id=l.level_id" 
					+ " where r.status=1 and r.job_approval_status = 0 ");

			strQuery.append(" order by r.job_approval_status desc,r.job_profile_updated_date desc,r.approved_date desc) ");
			String strMessage = "waiting for approval";
			
			int nCount = 0;
			pst = con.prepareStatement(strQuery.toString());
			rs = pst.executeQuery();
			StringBuilder sbRequirements = new StringBuilder();
			StringBuilder sbStauts = new StringBuilder();

			while (rs.next()) {

				sbRequirements.replace(0, sbRequirements.length(), "");
				sbStauts.replace(0, sbStauts.length(), "");

				StringBuilder sbApproveDeny = new StringBuilder();

				String strnCount = String.valueOf(nCount);

					sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatusJP" + nCount + "\"> ");
					if (rs.getString("job_profile_updated_date") != null) {
					 /*sbStauts.append("<img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" /></div>");*/
						sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval\"></i></div>");
						
					}else{
						 /*sbStauts.append("<img src=\"images1/icons/pullout.png\" title=\"Waiting for profile updation\" /></div>");*/
						sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Waiting for profile updation\"></i></div>");
					}

						strMessage = "waiting for approval";
						sbApproveDeny.append("<div style=\"float:right;\" id=\"myDivJP" + nCount + "\" > ");

						if (uF.parseToInt(rs.getString("job_profile_updated_by")) > 0) {
							
							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveJob('" + nCount + "','" + rs.getString("recruitment_id")
									+ "');\" ><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve\"></i></a> ");

							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))denyProfile('" + strnCount
									+ "','" + rs.getString("recruitment_id") + "');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a> ");
						}

						sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"viewProfile('" + rs.getString("recruitment_id") + "')\">View</a> ");
						sbApproveDeny.append("</div>");

				String openFont = "", closeFont = "";
				if (rs.getInt("priority_job_int") == 1) {
					openFont = "<span class=\"high\">";
					closeFont = "</span>";
				} else if (rs.getInt("priority_job_int") == 2) {
					openFont = "<span class=\"medium\">";
					closeFont = "</span>";
				} else {
					openFont = "<span class=\"low\">";
					closeFont = "</span>";
				}
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				sbRequirements.append(sbStauts + openFont + "Profile against the <a href=\"javascript:void(0)\" onclick=\"reportJobProfilePopUp(" + rs.getString("recruitment_id")
						+ ")\">" + rs.getString("job_code") + "</a>  <a href=\"javascript:void(0);\" onclick=\"getDesignationDetails(" + rs.getString("designation_id") + ")\">("
						+ rs.getString("designation_name") + ")</a>, requested by <strong>" + rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname")
						+ "</strong> for <strong>" + rs.getString("no_position") + "</strong> resources, is "+strMessage+". " + closeFont + sbApproveDeny.toString());
				jobProfileList.add(sbRequirements.toString());

				nCount++;
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
		request.setAttribute("jobProfileList", jobProfileList);
	}
	   
	
	private void viewAllRequestList() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

//		List<List<String>> requestList = new ArrayList<List<String>>();
		List<String> requestList = new ArrayList<String>();

		try {

			con = db.makeConnection(con);

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("(select d.designation_id, r.priority_job_int,r.status,r.recruitment_id,r.custum_designation,e.emp_fname,e.emp_mname,e.emp_lname,"
					+ "w.wlocation_id,w.wlocation_name,r.entry_date,r.no_position,r.target_deadline,r.comments,existing_emp_count,"
					+ "d.designation_name,r.dept_id  from recruitment_details r join work_location_info w on(r.wlocation=w.wlocation_id) "
					+ "join employee_personal_details e on (r.added_by=e.emp_per_id) left join designation_details d using(designation_id) "
					+ "where recruitment_id>0 and requirement_status = 'generate' and r.status = 0 ");
			strQuery.append(" order by r.status desc,r.recruitment_id desc) ");
			
			pst = con.prepareStatement(strQuery.toString());
			rst = pst.executeQuery();
//			System.out.println("pst ====> "+pst);
			int nCount = 0;
			StringBuilder sbRequirements = new StringBuilder();
			StringBuilder sbDesig = new StringBuilder();
			StringBuilder sbApproveDeny = new StringBuilder();
			StringBuilder sbStauts = new StringBuilder();
			while (rst.next()) {

				sbRequirements.replace(0, sbRequirements.length(), "");
				sbDesig.replace(0, sbDesig.length(), "");
				sbApproveDeny.replace(0, sbApproveDeny.length(), "");
				sbStauts.replace(0, sbStauts.length(), "");
				
				if (rst.getString("custum_designation") != null && !rst.getString("custum_designation").equals("") && rst.getString("designation_name") == null) {
					
						sbDesig.append("<a href=\"javascript:void(0)\" onclick=\"addDesignation(" + rst.getString("recruitment_id") + ");\"> "
								+ rst.getString("custum_designation") + "</a> (new)");
				} else {
					sbDesig.append("<a href=\"javascript:void(0);\" onclick=\"getDesignationDetails("+rst.getString("designation_id")+")\">"+rst.getString("designation_name")+"</a>");
				}

				String strnCount = String.valueOf(nCount);
						if (rst.getString("custum_designation") != null && !rst.getString("custum_designation").equals("") && rst.getString("designation_name") == null) {
							
							sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatusR" + nCount + "\" > ");
							
							/*sbStauts.append("<img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" />");*/
							sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval\"></i>");
							
							sbStauts.append("</div>");

							sbApproveDeny.append("<div style=\"float:right;\" id=\"myDivR" + nCount + "\" > ");

							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"alert( 'Approve Custom Designation First')\">"
									+ "<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve to Create Job Profile\"></i></a> ");

							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))denyRequest('" + strnCount
									+ "','" + rst.getString("recruitment_id") + "');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denial of Job Requirement\"></i></a>  ");
							
							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequest('" + strnCount + "','" + rst.getString("recruitment_id") + "');\">"
									+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
							
							sbApproveDeny.append("</div>");

						} else {
							sbStauts.append("<div style=\"float:left; padding-right: 5px;\" id=\"myDivStatusR" + nCount + "\" > ");

							/*sbStauts.append("<img src=\"images1/icons/pending.png\" title=\"Waiting for approval\" />");*/
							sbStauts.append("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Waiting for approval\" ></i>");
							
							sbStauts.append("</div>");
							
							sbApproveDeny.append("<div style=\"float:right;\" id=\"myDivR" + nCount + "\" > ");

//							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to approve this request?'))getContent('myDivM"
							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"approveRequest('"+ nCount +"','"+ rst.getString("recruitment_id")+"');\" >" +
									"<i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve to Create Job Profile\"></i></a> ");


							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"if(confirm('Are you sure, you want to deny this request?'))denyRequest('" + strnCount
									+ "','" + rst.getString("recruitment_id") + "');\">" + " <i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denial of Job Requirement\"></i></a>  ");

							sbApproveDeny.append("<a href=\"javascript:void(0)\" onclick=\"editRequest('" + strnCount + "','" + rst.getString("recruitment_id") + "');\">"
									+ " <img src=\"images1/edit.png\" title=\"Edit Request\" /> </a> ");
							
							sbApproveDeny.append("</div>");
						}
				String openFont = "", closeFont="";
				if (rst.getInt("priority_job_int") == 1) {
					openFont = "<span class=\"high\">";
					closeFont = "</span>";
				}else if (rst.getInt("priority_job_int") == 2) {
					openFont = "<span class=\"medium\">";
					closeFont = "</span>";
				}else{
					openFont = "<span class=\"low\">";
					closeFont = "</span>";
				}
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rst.getString("emp_mname");
					}
				}
				
				sbRequirements.append(""+sbStauts + openFont + " A request for the requirement of <strong>"+rst.getString("no_position")+"</strong> resources has been generated by <strong>"+rst.getString("emp_fname") +strEmpMName+ " " + rst.getString("emp_lname")+"</strong> for "+sbDesig+" designation and need to be acomplished by "+uF.getDateFormat(rst.getString("target_deadline"), DBDATE, CF.getStrReportDateFormat())+"." + closeFont + sbApproveDeny);
//				System.out.println("sbRequirements ===> "+sbRequirements.toString());
				requestList.add(sbRequirements.toString());
				nCount++;
			}
			rst.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		request.setAttribute("requestList", requestList);
	}
	
	
	public void getRecruitmentDetails(Connection con, UtilityFunctions uF, Map<String, String> hmDesigMap){
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			Map<String, String> hmOfferAcceptCandiCount = new HashMap<String, String>();
			pst = con.prepareStatement("select count(*) as offerAccept, rd.recruitment_id from candidate_application_details cpd, recruitment_details rd where cpd.recruitment_id  = rd.recruitment_id and candidate_status = 1 group by rd.recruitment_id");
			rs = pst.executeQuery();
			while(rs.next()){
				hmOfferAcceptCandiCount.put(rs.getString("recruitment_id"), rs.getString("offerAccept"));
			}
			rs.close();
			pst.close();
			
			
			List<List<String>> alRecruitment = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			pst = con.prepareStatement("select no_position,recruitment_id,job_code from recruitment_details where close_job_status = false and job_approval_status = 1"); //? between effective_date and target_deadline and 
//			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()) {
				alInner = new ArrayList<String>();
				
				alInner.add(rs.getString("job_code"));
				alInner.add(""+uF.parseToInt(rs.getString("no_position")));
				alInner.add(""+uF.parseToInt(hmOfferAcceptCandiCount.get(rs.getString("recruitment_id"))));
				int remainingRequirement =  uF.parseToInt(rs.getString("no_position")) -uF.parseToInt(hmOfferAcceptCandiCount.get(rs.getString("recruitment_id"))); 
				alInner.add(""+(remainingRequirement > 0 ? remainingRequirement : "0"));
				alRecruitment.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alRecruitment", alRecruitment);
			
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
	
	public void getLeaveRequests(Connection con, UtilityFunctions uF, Map<String, String> hmEmployeeMap, Map<String, String> hmLevelMap){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			pst = con.prepareStatement("select * from emp_leave_entry elt, employee_official_details eod " +
					" where elt.emp_id = eod.emp_id and wlocation_id = (select wlocation_id from " +
					" employee_official_details where emp_id = ?) and ((approval_from, approval_to_date) " +
					" overlaps (to_date(?::text, 'YYYY-MM-DD')-1,to_date(?::text, 'YYYY-MM-DD') +1) " +
					" OR approval_from >= ?)  and is_approved=1");			
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setString(2, uF.getCurrentDate(CF.getStrTimeZone())+"");
			pst.setString(3, uF.getCurrentDate(CF.getStrTimeZone())+"");
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			
			List alLeaves = new ArrayList();
			rs = pst.executeQuery();
			while (rs.next()) {
				alLeaves.add(hmEmployeeMap.get(rs.getString("emp_id"))+", is on leave from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT leave_type_id,is_compensatory FROM leave_type  where leave_type_id>0 order by leave_type_name");
			rs = pst.executeQuery();
			Map<String,String> hmLeaveCompensate = new HashMap<String, String>();
			while (rs.next()) {
				hmLeaveCompensate.put(rs.getString("leave_type_id"), rs.getString("is_compensatory"));
			}
			rs.close();
			pst.close();			
			
			pst = con.prepareStatement("select entrydate,leave_id,a.emp_id,approval_from,approval_to_date,w.emp_id as approveby_id,leave_type_id from " +
					"(select elt.* from emp_leave_entry elt, employee_official_details eod where eod.emp_id = elt.emp_id " +
					" and wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) " +
					" and is_approved =0 and entrydate is not null and encashment_status = false) as a,work_flow_details w " +
					" where a.leave_id=w.effective_id and entrydate>=? and effective_type='"+WORK_FLOW_LEAVE+"' order by a.entrydate ");			
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));
			List<String> alLeaveRequest = new ArrayList<String>();
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strDate = rs.getString("entrydate");				
				if(strDate!=null && strDate.equals(strToday)){
					strDate = ", <span>today</span>";
				}else if(strDate!=null && strDate.equals(strYesterday)){
					strDate = ", <span>yesterday</span>";
				}else {
					strDate = " on "+ uF.getDateFormat(rs.getString("entrydate"), DBDATE, "EEEE");
					strDate = "<span>"+strDate.toLowerCase()+"</span>";
				}
				
				
				if(uF.parseToInt((String) session.getAttribute("EMPID"))>0 && uF.parseToInt((String) session.getAttribute("EMPID"))==uF.parseToInt(rs.getString("approveby_id"))){
//					alLeaveRequest.add("<span style=\"width: 95%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied leave from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM")+strDate+"" +
//							" and submitted to you</span>"+
//							"<span style=\"float: right;\"><a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to approve this request?')) window.location='ManagerLeaveApproval.action?type=type&apType=auto&apStatus=1&E="+rs.getString("leave_id")+"&LID="+hmLevelMap.get(rs.getString("emp_id"))+"&strCompensatory="+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"';\"><img title=\"Approved\" src=\"images1/icons/icons/approve_icon.png\" border=\"0\" /></a> " +
//							" <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to denied this request?')) window.location='ManagerLeaveApproval.action?type=type&apType=auto&apStatus=-1&E="+rs.getString("leave_id")+"&LID="+hmLevelMap.get(rs.getString("emp_id"))+"&strCompensatory="+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"';\"><img title=\"Denied\" src=\"images1/icons/icons/close_button_icon.png\" border=\"0\" style=\"width: 16px;\"/></a></span>");
					alLeaveRequest.add("<span style=\"width: 90%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied leave from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM")+strDate+"</span>"+
							"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenyLeave('1','"+rs.getString("leave_id")+"','"+hmLevelMap.get(rs.getString("emp_id"))+"','"+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"approveDenyLeave('-1','"+rs.getString("leave_id")+"','"+hmLevelMap.get(rs.getString("emp_id"))+"','"+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a></span>");
					
				}else{
					alLeaveRequest.add("<span style=\"width: 95%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied leave from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM")+strDate+"" +
							" and submitted to "+hmEmployeeMap.get(rs.getString("approveby_id"))+"</span>");
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alLeaveRequest",alLeaveRequest);
			
			request.setAttribute("alLeaves",alLeaves);
			session.setAttribute("LEAVE_REQUEST_COUNT", alLeaveRequest.size()+"");
			
			
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
	
	public void getReimbursementRequests(Connection con, UtilityFunctions uF, Map<String, String> hmEmployeeMap){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			pst = con.prepareStatement("select entry_date,a.emp_id,reimbursement_amount,from_date,to_date,reimbursement_type," +
					"reimbursement_purpose,w.emp_id as approveby_id,reimbursement_id from (select er.* from emp_reimbursement er, " +
					" employee_official_details eod where eod.emp_id = er.emp_id and wlocation_id = (select wlocation_id " +
					" from employee_official_details where emp_id = ?) and approval_1 =0 and approval_2=0 and entry_date is not null ) " +
					" as a,work_flow_details w where a.reimbursement_id=w.effective_id and entry_date>=? and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' order by a.entry_date "); 		
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));			
			
			List<String> alReimbursementRequest = new ArrayList<String>();
			rs = pst.executeQuery();
			while (rs.next()) {
				String strDate = rs.getString("entry_date");
				 
				if(strDate!=null && strDate.equals(strToday)){
					strDate = ", <span>today</span>";
				}else if(strDate!=null && strDate.equals(strYesterday)){
					strDate = ", <span>yesterday</span>";
				}else {
					strDate = " on "+ uF.getDateFormat(rs.getString("entry_date"), DBDATE, "EEEE");
					strDate = "<span>"+strDate.toLowerCase()+"</span>";
				}
					
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				}
						
				if(uF.parseToInt((String) session.getAttribute("EMPID"))>0 && uF.parseToInt((String) session.getAttribute("EMPID"))==uF.parseToInt(rs.getString("approveby_id"))){
					
//					alReimbursementRequest.add("<span style=\"width: 95%; float: left;\"><strong>"+hmEmployeeMap.get(rs.getString("emp_id"))+"</strong>, has applied for reimbursement for <strong>"+strCurrency+rs.getString("reimbursement_amount")+"</strong> from "+uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat())+"" +
//							" and submitted to you </span>"+
//							"<p style=\"float:left;width:95%;font-size: 10px; font-style: italic;\">"+"Reimbursement Type:"+uF.showData(rs.getString("reimbursement_type"), "")+"<br/>Reason:"+uF.showData(rs.getString("reimbursement_purpose"), "")+"</p>" +
//							"<span style=\"float: right;\"><a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to approve this request?')) window.location='UpdateReimbursements.action?type=type&S=1&RID=" + rs.getString("reimbursement_id")
//							+ "&T=RIM&M=AA';\"><img title=\"Approved\" src=\"images1/icons/icons/approve_icon.png\" border=\"0\" /></a>&nbsp;" +
//							"<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to denied this request?')) window.location='UpdateReimbursements.action?type=type&S=-1&RID=" + rs.getString("reimbursement_id")
//							+ "&T=RIM&M=AA';\"><img title=\"Denied\" src=\"images1/icons/icons/close_button_icon.png\" border=\"0\" style=\"width: 16px;\"/></a></span>");
					alReimbursementRequest.add("<span style=\"width: 95%; float: left;\"><strong>"+hmEmployeeMap.get(rs.getString("emp_id"))+"</strong>, has applied for reimbursement for <strong>"+strCurrency+rs.getString("reimbursement_amount")+"</strong> from "+uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat())+"" +
							" and submitted to you </span>"+
							"<p style=\"float:left;width:95%;font-size: 10px; font-style: italic;\">"+"Reimbursement Type:"+uF.showData(rs.getString("reimbursement_type"), "")+"<br/>Reason:"+uF.showData(rs.getString("reimbursement_purpose"), "")+"</p>" +
							"<span style=\"float: right;\"><a href=\"javascript:void(0);\" onclick=\"approveDenyRembursement('1','" + rs.getString("reimbursement_id")+ "');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a>&nbsp;" +
							"<a href=\"javascript:void(0);\" onclick=\"approveDenyRembursement('-1','" + rs.getString("reimbursement_id")+ "');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a></span>");
				}else{
					alReimbursementRequest.add("<span style=\"width: 95%; float: left;\"><strong>"+hmEmployeeMap.get(rs.getString("emp_id"))+"</strong>, has applied for reimbursement for <strong>"+strCurrency+rs.getString("reimbursement_amount")+"</strong> from "+uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat())+"" +
							" and submitted to "+hmEmployeeMap.get(rs.getString("approveby_id"))+"</span>"+
							"<p style=\"float:left;width:95%;font-size: 10px; font-style: italic;\">"+"Reimbursement Type:"+uF.showData(rs.getString("reimbursement_type"), "")+"<br/>Reason:"+uF.showData(rs.getString("reimbursement_purpose"), "")+"</p>");
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReimbursementRequest",alReimbursementRequest);
			session.setAttribute("PENDING_REIMBURSEMENT_COUNT",alReimbursementRequest.size()+"");
			
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
	
	public void getRequisitionRequests(Connection con, UtilityFunctions uF, Map<String, String> hmEmployeeMap){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			pst = con.prepareStatement(selectRequisitionForDashboard);			
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			
			
			List alRequisitionRequest = new ArrayList();
			rs = pst.executeQuery();
			while (rs.next()) {
				String strDate = rs.getString("requisition_date");
				
				if(strDate!=null && strDate.equals(strToday)){
					strDate = ", <span>today</span>";
				}else if(strDate!=null && strDate.equals(strYesterday)){
					strDate = ", <span>yesterday</span>";
				}else {
					strDate = " on "+ uF.getDateFormat(rs.getString("requisition_date"), DBDATE, "EEEE");
					strDate = "<span>"+strDate.toLowerCase()+"</span>";
				}
				
				String strRequest = null;
				if("IR".equalsIgnoreCase(rs.getString("requisition_type"))){
					strRequest = "infrastructure";
				}else if("OR".equalsIgnoreCase(rs.getString("requisition_type"))){
					strRequest = "other";
				}else if("BF".equalsIgnoreCase(rs.getString("requisition_type"))){
					strRequest = "bonafide certificate";
				}
				
				alRequisitionRequest.add("<span style=\"width: 95%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied for rquisition for "+strRequest+"</span>"+
						"<span style=\"float: right;\"> <a style=\"float:right\" href=\"Requisitions.action\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  alt=\"Approve\" title=\"Click to Approve/Deny\"></i></a></span>");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alRequisitionRequest",alRequisitionRequest);
			session.setAttribute("PENDING_REQUISITION_COUNT",alRequisitionRequest.size()+"");
			
			
			
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


	
	public void getBirthday(Connection con, UtilityFunctions uF, Map hmEmployeeMap){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			String strToday1 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM-dd");
			String strTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 1)+"", DBDATE, "MM-dd");
			String strDayAfterTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 2)+"", DBDATE, "MM-dd");
			
			pst = con.prepareStatement(selectBirthDay);			
			
			pst.setDate(1, uF.getFutureDate(CF.getStrTimeZone(), 365));
			rs = pst.executeQuery();
			
//			System.out.println("pst====>"+pst);
			
			
			List<String> alBirthDays = new ArrayList<String>();
			while (rs.next()) {
				String strBDate = uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM-dd");
				
				String gender = (String)rs.getString("emp_gender");
				if(strBDate!=null && strBDate.equals(strToday1)) {
					if(hmEmployeeMap.get(rs.getString("emp_per_id")) != null && !hmEmployeeMap.get(rs.getString("emp_per_id")).equals("null")) {
						if(gender!=null && gender.equalsIgnoreCase("M")){
							//alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today wish him...!");
							alBirthDays.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday today wish him...!");
						}else if(gender!=null && gender.equalsIgnoreCase("F")){
							//alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today wish her...!");
							alBirthDays.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday today wish her...!");
						}else{
						//	alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today...!");
							alBirthDays.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday today...!");
						}
					}
				}
				
				if(strBDate!=null && strBDate.equals(strTomorrow)) {
					if(hmEmployeeMap.get(rs.getString("emp_per_id")) != null && !hmEmployeeMap.get(rs.getString("emp_per_id")).equals("null")) {
						if(gender!=null && gender.equalsIgnoreCase("M")){
							//alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today wish him...!");
							alBirthDays.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday tomorrow wish him...!");
						}else if(gender!=null && gender.equalsIgnoreCase("F")){
							//alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today wish her...!");
							alBirthDays.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday tomorrow wish her...!");
						}else{
						//	alBirthDays.add("It's "+"<strong>"+hmEmployeeMap.get(rs.getString("emp_per_id"))+"</strong>, in "+strCity+", working for "+hmDepartmentMap.get(hmEmpDepartmentMap.get(rs.getString("emp_per_id")))+"birthday today...!");
							alBirthDays.add(""+hmEmployeeMap.get(rs.getString("emp_per_id"))+", has birthday tomorrow...!");
						}
					}
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alBirthDays",alBirthDays);
			
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
	
	
	public void getUpcomingEvents(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
/*			String strToday1 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM-dd");
//			String strTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 1)+"", DBDATE, "MM-dd");
//			String strDayAfterTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 2)+"", DBDATE, "MM-dd");
			
			pst = con.prepareStatement(selectUpcomingEventsDashboard);			
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			
//			log.debug("EVENTS pst====>"+pst);
			
			
			List<String> alEvents = new ArrayList<String>();
			while (rs.next()) {
				String strEventDate = uF.getDateFormat(rs.getString("event_date"), DBDATE, "MM-dd");
				alEvents.add(strEventDate+": "+rs.getString("event_title"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alEvents",alEvents);*/
			
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
	
	public void getPendingExceptionCount(Connection con, UtilityFunctions uF, Map<String, String> hmEmployeeMap){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {

			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy")));

			int nMinDate = cal.getActualMinimum(Calendar.DATE);
			int nMaxDate = cal.getActualMaximum(Calendar.DATE);
			
			String strDate1 = nMinDate +"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
			String strDate2 = nMaxDate +"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR);
			
			/*pst = con.prepareStatement(pendingExceptionDahsboardHR);			
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate2, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt((String)session.getAttribute(WLOCATIONID)));
			*/
//			pst = con.prepareStatement(pendingExceptionDahsboardHR);
			pst = con.prepareStatement("select count(*) as count from attendance_details ad where approved = ? " +
					"and ad.emp_id in (select emp_id from employee_official_details where is_roster = true and wlocation_id = ?)");
			pst.setInt(1, -2);
			pst.setInt(2, uF.parseToInt((String)session.getAttribute(WLOCATIONID)));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			int nPendingExceptionCount = 0;
			while (rs.next()) {
				nPendingExceptionCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
			session.setAttribute("PENDING_EXCEPTION_COUNT",nPendingExceptionCount+"");
			
			
//			pst = con.prepareStatement(pendingExceptions);
			pst = con.prepareStatement("select * from attendance_details ad where approved = ? " +
					"and ad.emp_id in (select emp_id from employee_official_details where is_roster = true and wlocation_id = ?) order by in_out_timestamp desc");
			pst.setInt(1, -2);
			pst.setInt(2, uF.parseToInt((String)session.getAttribute(WLOCATIONID)));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alAttendance = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			while(rs.next()){
				alInner = new ArrayList<String>();

				alInner.add(hmEmployeeMap.get(rs.getString("emp_id")));
				alInner.add(rs.getString("in_out"));
				alInner.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()));
				alInner.add(rs.getString("reason"));
				alAttendance.add(alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("alAttendance", alAttendance);
		} catch (Exception e) {
			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
	
	
	public void getPendingReimbursementsCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {

			pst = con.prepareStatement(pendingReimbursementDashboardCount);			
			rs = pst.executeQuery();
			
			int nPendingReimbursementCount = 0;
			while (rs.next()) {
				nPendingReimbursementCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
			session.setAttribute("PENDING_REIMBURSEMENT_COUNT",nPendingReimbursementCount+"");
			
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
	
	public void getPendingRequisitionCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			pst = con.prepareStatement(pendingRequisitionDashboardCount);			
			rs = pst.executeQuery();
			int nPendingRequisitionCount = 0;
			while (rs.next()) {
				nPendingRequisitionCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
			session.setAttribute("PENDING_REQUISITION_COUNT",nPendingRequisitionCount+"");
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
	
	public void getPendingLeaveCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		int nPendingLeaveCount = 0;
		try {
			
			session.setAttribute("PENDING_LEAVE_COUNT",nPendingLeaveCount+"");
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
	public void getTasksCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement(pendingTaskDashboardCount);			
			rs = pst.executeQuery();
			int nCompletedTaskCount = 0;
			while (rs.next()) {
				nCompletedTaskCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
			session.setAttribute("COMPLETED_TASK_COUNT",nCompletedTaskCount+"");
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
	
	public void getTodaysReportSentCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement(pendingReportDashboardCount);			
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			int nReportSentCount = 0;
			while (rs.next()) {
				nReportSentCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
			session.setAttribute("REPORT_SENT_COUNT",nReportSentCount+"");
			
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
	
	public void getBestEmployee(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement(pendingTopEmployeeDashboardCount);
			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 30));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();

			double []WORKED_HOURS_DATA_MONTH = new double[2];
			String []WORKED_HOURS_LABEL_MONTH = new String[2];
			double dblWorkedHours = 0;
			double dblActualHours = 0;
			int count = 0;
			Map<String, Map<String, String>> hmTopEmployees = new LinkedHashMap<String, Map<String, String>>(); 
			
			while (rs.next()) {
				if(count==0){
					dblWorkedHours = rs.getDouble("hours_worked");
					dblActualHours = rs.getDouble("actual_hours");
				}
				count++;
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("WORKED_HRS", uF.formatIntoComma(rs.getDouble("hours_worked")));
				hmInner.put("ACTUAL_HRS", uF.formatIntoComma(rs.getDouble("actual_hours")));
				
				hmTopEmployees.put(rs.getString("emp_id"), hmInner);
			}
			rs.close();
			pst.close();
			
			WORKED_HOURS_DATA_MONTH[0] = dblWorkedHours;
			WORKED_HOURS_DATA_MONTH[1] = dblActualHours - dblWorkedHours;
			
			WORKED_HOURS_LABEL_MONTH[0] = "Worked";
			WORKED_HOURS_LABEL_MONTH[1] = "Actual";
			
			request.setAttribute("KPI_BEST", new SemiCircleMeter().getSemiCircleChart(WORKED_HOURS_DATA_MONTH, WORKED_HOURS_LABEL_MONTH));
			request.setAttribute("hmTopEmployees", hmTopEmployees);
 			
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
	
	
	public void getDepartmentEmployeeCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			Map<String, String> hmDepartOrgName = CF.getOrgNameDepartIdwise(con); 
			pst = con.prepareStatement(departmentEmployeeDashboardCount);
			rs = pst.executeQuery();
			int departCount = 0;
			Map<String, String> hmDepartmentEmployeeCount = new HashMap<String, String>();
			while (rs.next()) {
				departCount++;
				hmDepartmentEmployeeCount.put(rs.getString("depart_id"), rs.getString("count"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("departCount", ""+departCount);
			request.setAttribute("hmDepartmentEmployeeCount", hmDepartmentEmployeeCount);
			request.setAttribute("hmDepartOrgName", hmDepartOrgName);
			
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
	

	

	public void getWlocationEmployeeCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			Map<String, String> hmWLocOrgName = CF.getOrgNameWLocationIdwise(con);
			pst = con.prepareStatement(pendingWLocationDashboardCount);
			rs = pst.executeQuery();
			int wLocationCount = 0;
			Map<String, String> hmWLocationEmployeeCount = new HashMap<String, String>();
			while (rs.next()) {
				wLocationCount++;
				hmWLocationEmployeeCount.put(rs.getString("wlocation_id"), uF.formatIntoComma(rs.getDouble("count")) );
			}
			rs.close();
			pst.close();
			request.setAttribute("wLocationCount", ""+wLocationCount);
			request.setAttribute("hmWLocationEmployeeCount", hmWLocationEmployeeCount);
			request.setAttribute("hmWLocOrgName", hmWLocOrgName);
			
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
	
	public void getSkillsEmployeeCount(Connection con, UtilityFunctions uF, int nLimit){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			pst = con.prepareStatement("select skill_id, count(sd.emp_id) as count from employee_official_details eod,employee_personal_details epd, skills_description sd where sd.emp_id = eod.emp_id and sd.emp_id = epd.emp_per_id and epd.emp_per_id = eod.emp_id and eod.wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) and  is_alive=true group by skill_id"+((nLimit>0)?" limit "+nLimit:""));
			pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			List<List<String>> skillwiseEmpCountList = new ArrayList<List<String>>();
//			Map<String, String> hmSkillsEmployeeCount = new HashMap<String, String>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("skill_id"));
				innerList.add(CF.getSkillNameBySkillId(con, rs.getString("skill_id")));
				innerList.add(uF.formatIntoComma(rs.getDouble("count")));
				skillwiseEmpCountList.add(innerList);
//				hmSkillsEmployeeCount.put(rs.getString("skill_id"), uF.formatIntoComma(rs.getDouble("count")) );
			}
			rs.close();
			pst.close();
			request.setAttribute("skillwiseEmpCountList", skillwiseEmpCountList);
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
	
	public void getEmployeeTurnoverCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement(pendingEmployeeTurnoverDashboardCount);
			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 30));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 0));
			pst.setInt(3, uF.parseToInt((String) session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			double dblJoiningDate = 0;
			double dblLeavingDate = 0;

			while (rs.next()) {
				dblJoiningDate = rs.getDouble("joining_count");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement(pendingEmployeeLeavingDashboardCount);
			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 30));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 0));
			pst.setInt(3, uF.parseToInt((String) session.getAttribute(EMPID)));
			rs = pst.executeQuery();

			while (rs.next()) {
				dblLeavingDate = rs.getDouble("leaving_count");
			}
			rs.close();
			pst.close();
			 
			request.setAttribute("JOINING_COUNT", uF.formatIntoComma(dblJoiningDate));
			request.setAttribute("LEAVING_COUNT", uF.formatIntoComma(dblLeavingDate));
			
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
	
	
	public void getPendingApprovalsCountDetails(Connection con, UtilityFunctions uF, int MONTH, int YEAR, int MinDay, int MaxDay){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			
			double[] pending = new double[1];
			double[] approved = new double[1];
			double[] denied = new double[1];
//			String[] label = new String[] { "" };
			
			
			pst = con.prepareStatement(pendingPendingApprovalDashboardCount);
			pst.setDate(1, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));
			rs = pst.executeQuery();
			
			while (rs.next()) {
				if (uF.parseToInt(rs.getString("approved")) == 1) {
					request.setAttribute("EXCEP_APPROVED_COUNT", rs.getString("count"));
					approved[0] = uF.parseToInt(rs.getString("count"));
				} else if (uF.parseToInt(rs.getString("approved")) == -1) {
					request.setAttribute("EXCEP_DENIED_COUNT", rs.getString("count"));   
					denied[0] = uF.parseToInt(rs.getString("count"));
				} else if (uF.parseToInt(rs.getString("approved")) == -2) {
					request.setAttribute("EXCEP_WAITING_COUNT", rs.getString("count"));
					pending[0] = uF.parseToInt(rs.getString("count"));
				}
			}
			rs.close();
			pst.close();
			
			
			//pst = con.prepareStatement(pendingPendingApprovalDashboardCount1);
			pst = con.prepareStatement("select approval_2, count(approval_2) from emp_reimbursement er, employee_official_details eod " +
					" where er.emp_id = eod.emp_id and wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) group By approval_2");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
			rs = pst.executeQuery();

			while (rs.next()) {

				if (uF.parseToInt(rs.getString("approval_2")) == 1) {
					request.setAttribute("REIMB_APPROVED_COUNT", rs.getString("count"));
				} else if (uF.parseToInt(rs.getString("approval_2")) == -1) {
					request.setAttribute("REIMB_DENIED_COUNT", rs.getString("count"));
				} else if (uF.parseToInt(rs.getString("approval_2")) == 0) {
					request.setAttribute("REIMB_WAITING_COUNT", rs.getString("count"));
				}
			}
			rs.close();
			pst.close();
	
			
//			pst = con.prepareStatement(pendingPendingApprovalDashboardCount2);
			
			pst = con.prepareStatement("select count(*) as count, is_approved from emp_leave_entry elt, " +
					" employee_official_details eod where eod.emp_id = elt.emp_id and " +
					" wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) " +
					" and entrydate between ? AND ? group by is_approved");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
			pst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));
			rs = pst.executeQuery();
			while (rs.next()) {

				if (uF.parseToInt(rs.getString("is_approved")) == 1) {
					request.setAttribute("LEAVE_APPROVED_COUNT", rs.getString("count"));
				} else if (uF.parseToInt(rs.getString("is_approved")) == 0) {
					request.setAttribute("LEAVE_PENDING_COUNT", rs.getString("count"));
				}
			}
			rs.close();
			pst.close();
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
	
public void getLeaveSummary(Connection con, UtilityFunctions uF, CommonFunctions CF, String strUserType) {
		
		ResultSet rs = null;
		PreparedStatement pst=null;
		try {
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(leave_no) as leave_no,_date from leave_application_register where is_modify=false " +
					"and leave_id in (select leave_id from emp_leave_entry) and _date between ? and ? ");
			if(strUserType!=null && strUserType.equals(HRMANAGER)){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id="+uF.parseToInt((String)session.getAttribute(WLOCATIONID))+")");
			}
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
	        	sbQuery.append(" and emp_id in (select emp_id from employee_official_details where wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+"))");
			}
			sbQuery.append(" group by _date order by _date");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(2, uF.getFutureDate(CF.getStrTimeZone(), 7));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			
			Map<String, String> hmNoOfLeave = new HashMap<String, String>();
			while(rs.next()) {
				hmNoOfLeave.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("leave_no"));
			}
			rs.close();
			pst.close();
			
			String strDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			cal.set(Calendar.DAY_OF_MONTH, uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "MM")) - 1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strDate, DATE_FORMAT, "yyyy")));
			
			List<List<String>> leaveSummaryList = new ArrayList<List<String>>();
			for(int i=0; i<7; i++){
				String strD1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"+ cal.get(Calendar.YEAR);
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.getDateFormat(strD1, DATE_FORMAT, "dd MMM"));
				innerList.add(uF.showData(hmNoOfLeave.get(strD1), "0"));
				leaveSummaryList.add(innerList);
				
				cal.add(Calendar.DATE, 1);
			}
			
			request.setAttribute("leaveSummaryList", leaveSummaryList);
			
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

public void getCompensationSummary(Connection con, UtilityFunctions uF, CommonFunctions CF) {
	
	ResultSet rs = null;
	PreparedStatement pst=null;
	try {
		
		Map<String, String> hmDepartment = CF.getDepartmentMap(con, null, null);
		if(hmDepartment == null) hmDepartment = new HashMap<String, String>();
		
		Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DATE, 1);
		SimpleDateFormat dateFormat=new  SimpleDateFormat(DBDATE);
	    String strPreMonthDate=dateFormat.format(cal.getTime());
		int nMonth = uF.parseToInt(uF.getDateFormat(""+strPreMonthDate, DBDATE, "MM"));
		int nYear = uF.parseToInt(uF.getDateFormat(""+strPreMonthDate, DBDATE, "yyyy"));
		
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("select earning_deduction,sum(amount) as amount,depart_id from payroll_generation pg, employee_official_details eod " +
				"where pg.emp_id=eod.emp_id and month =? and year =? and is_paid=true and eod.depart_id in (select dept_id from department_info) " +
				"group by depart_id,earning_deduction order by depart_id,earning_deduction desc");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, nMonth);
		pst.setInt(2, nYear); 
		rs = pst.executeQuery();
		Map<String, Map<String, String>> hmEarningAmount = new LinkedHashMap<String, Map<String,String>>();
		Map<String, Map<String, String>> hmDeductionAmount = new HashMap<String, Map<String, String>>();
		while (rs.next()) {
			if(rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equals("E")){
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("AMOUNT", rs.getString("amount"));
				
				hmEarningAmount.put(rs.getString("depart_id"), hmInner);
			} else if(rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equals("D")){
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("AMOUNT", rs.getString("amount"));
				
				hmDeductionAmount.put(rs.getString("depart_id"), hmInner);
			}
		}
		rs.close();
		pst.close();
		
		Iterator<String> it = hmDepartment.keySet().iterator();
		List<List<String>> compensationSummaryList = new ArrayList<List<String>>();
		while(it.hasNext()){
			String strDepartId = it.next();
			String strDepartName = hmDepartment.get(strDepartId);
			
			Map<String, String> hmEarning = hmEarningAmount.get(strDepartId);
			if(hmEarning == null) hmEarning = new HashMap<String, String>();
			Map<String, String> hmDeduction = hmDeductionAmount.get(strDepartId);
			if(hmDeduction == null) hmDeduction = new HashMap<String, String>();
			
			double dblNetSalary = uF.parseToDouble(hmEarning.get("AMOUNT")) - uF.parseToDouble(hmDeduction.get("AMOUNT"));
			
			List<String> innerList = new ArrayList<String>();
			innerList.add(strDepartName);
			innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblNetSalary));
			
			compensationSummaryList.add(innerList);
		}
		request.setAttribute("compensationSummaryList", compensationSummaryList);
		
		String compensationDate = "Compensation Summary ("+uF.getDateFormat(""+strPreMonthDate, DBDATE, "MMM")+" "+uF.getDateFormat(""+strPreMonthDate, DBDATE, "yy")+")";
		request.setAttribute("compensationDate", compensationDate);
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

private void getConfirmationEmp(UtilityFunctions uF,String strUserType) {
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rst=null;
	Database db = new Database();
	db.setRequest(request);

	try {
		con = db.makeConnection(con);
				

		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		
		Map<String, String> hmEmpProbation = new HashMap<String, String>();
		/*
		Map<String,List<String>> hmUserLocationAccessMap = new HashMap<String,List<String>>();
		Map<String,String> hmUserLocationMap = new HashMap<String,String>();
		StringBuilder sQuery = new StringBuilder();
		sQuery.append("select * from employee_official_details eod,user_details ud where eod.emp_id = ud.emp_id and status='ACTIVE' " +
		"and (usertype_id =7 or usertype_id =1) and eod.emp_id in(select emp_per_id from employee_personal_details where is_alive=true) order by user_id desc");
		pst = con.prepareStatement(sQuery.toString());
		rst = pst.executeQuery();
		while(rst.next()){
			List<String> locationList = new ArrayList<String>();
			String accessLocations = (String)rst.getString("wlocation_id_access");
			if(accessLocations!= null && !accessLocations.equals("")){
				locationList = Arrays.asList(accessLocations.split(","));
			}else{
				locationList.add(rst.getString("wlocation_id"));
			}	
			hmUserLocationAccessMap.put(rst.getString("emp_id"),locationList);
			hmUserLocationMap.put(rst.getString("emp_id"),rst.getString("wlocation_id"));
		}
		
		rst.close();
		pst.close();*/
		
		StringBuilder sbQuery1 = new StringBuilder();
		sbQuery1.append("select * from probation_policy order by emp_id desc");
		pst = con.prepareStatement(sbQuery1.toString());
		rst = pst.executeQuery();
		while(rst.next()) {
			int probation = uF.parseToInt((String)rst.getString("probation_duration")) + uF.parseToInt((String)rst.getString("extend_probation_duration"));
			hmEmpProbation.put((String)rst.getString("emp_id"),String.valueOf(probation) );
		}
		rst.close();
		pst.close();
		
		List<String> confirmationEmpList = new ArrayList<String>();
		Map<String, String> hmwLocation = CF.getWLocationMap(con, null, null);
		StringBuilder strQuery = new StringBuilder();
		strQuery.append("select * from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id  and epd.emp_status = 'PROBATION' and approved_flag='true' and is_alive='true' and emp_filled_flag='true' order by epd.emp_per_id desc");
	
		if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
      	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
      		strQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
      	  } else {
      		strQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
      	  }
		}
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
      	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
      		strQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
      	  } else {
      		strQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
      	  }
		}
		
		strQuery.append(" order by epd.emp_per_id desc");
		pst = con.prepareStatement(strQuery.toString());
		rst = pst.executeQuery();
		while (rst.next()) {
			StringBuilder sbConfirmationList = new StringBuilder();
			String empimg = uF.showData(rst.getString("emp_image"), "avatar_photo.png");
			String supervisorName = CF.getEmpNameMapByEmpId(con, rst.getString("supervisor_emp_id"));
			String empDesignation = CF.getEmpDesigMapByEmpId(con, rst.getString("emp_per_id"));
			if(rst.getString("joining_date")!=null && !rst.getString("joining_date").equals("")){
				String joiningDate = uF.getDateFormat(rst.getString("joining_date"), DBDATE,DATE_FORMAT);
				java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT),DATE_FORMAT );
				java.util.Date startDate = uF.getDateFormatUtil(joiningDate,DATE_FORMAT );
				
				int probation = uF.parseToInt(hmEmpProbation.get((String)rst.getString("emp_per_id")));
				String confirmationDate = uF.getDateFormat(rst.getString("joining_date"), DBDATE,CF.getStrReportDateFormat());		
				
				String futureDate = uF.getDateFormat(""+uF.getFutureDate(startDate, probation),DBDATE,DATE_FORMAT);
				java.util.Date confDate = null;
				
				if(probation>0){
					confDate = uF.getDateFormatUtil(futureDate,DATE_FORMAT );
					confirmationDate =  uF.getDateFormat(""+uF.getFutureDate(startDate, probation),DBDATE, CF.getStrReportDateFormat());
				}else{
					confDate = uF.getDateFormatUtil(joiningDate,DATE_FORMAT );
				}
				
				
					
				if(CF.getStrDocRetriveLocation()==null) { 
					sbConfirmationList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #CCCCCC \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ DOCUMENT_LOCATION +empimg+"\" height=\"20\" width=\"20\"> </span>");
				} else { 
					sbConfirmationList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #CCCCCC \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rst.getString("emp_per_id")+"/"+I_22x22+"/"+empimg+"\" height=\"20\" width=\"20\"> </span>");
				}  
				sbConfirmationList.append("<span style=\"float: left; width: 92%; margin-left: 5px;\">");
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rst.getString("emp_mname") != null && rst.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rst.getString("emp_mname");
					}
				}
				
				
				sbConfirmationList.append(rst.getString("emp_fname")+strEmpMName+" "+rst.getString("emp_lname"));
				sbConfirmationList.append(" designation is "+uF.showData(empDesignation, "-"));
				sbConfirmationList.append(", manager is "+uF.showData(supervisorName, "-"));
				sbConfirmationList.append(", work location is "+uF.showData(hmwLocation.get(rst.getString("wlocation_id")), "-"));
				sbConfirmationList.append(" and confirmation date is "+confirmationDate+".");
				sbConfirmationList.append("</span>");
				
				java.util.Date tommorowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 1),DBDATE, DATE_FORMAT),DATE_FORMAT );
				java.util.Date dayAfterTomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 2),DBDATE, DATE_FORMAT),DATE_FORMAT );
				
				if(confDate.equals(currDate) || confDate.equals(tommorowDate) || confDate.equals(dayAfterTomorrowDate) || confDate.before(currDate) ){
					confirmationEmpList.add(sbConfirmationList.toString());
					/*if(strUserType!=null && strUserType.equals(HRMANAGER)){
						List<String> locations = hmUserLocationAccessMap.get(strEmpId);
						if(locations!=null && locations.size()>0 && locations.contains(rst.getString("wlocation_id"))){
							confirmationEmpList.add(sbConfirmationList.toString());
						}else{
							String empLocation = hmUserLocationMap.get(strEmpId);
							if((empLocation!=null && empLocation.equals(rst.getString("wlocation_id"))) || rst.getString("wlocation_id") == null){
								confirmationEmpList.add(sbConfirmationList.toString());
							}
						}
					}else if(strUserType!=null && strUserType.equals(ADMIN)){
						confirmationEmpList.add(sbConfirmationList.toString());
					}*/
					
				}
				
				/*else if(confDate.equals(tommorowDate)){
					confirmationEmpList.add(sbConfirmationList.toString());
					/*if(strUserType!=null && strUserType.equals(HRMANAGER)){
						List<String> locations = hmUserLocationAccessMap.get(strEmpId);
						if(locations!=null && locations.size()>0 && locations.contains(rst.getString("wlocation_id"))){
							confirmationEmpList.add(sbConfirmationList.toString());
						}else{
							String empLocation = hmUserLocationMap.get(strEmpId);
							if((empLocation!=null && empLocation.equals(rst.getString("wlocation_id"))) || rst.getString("wlocation_id") == null){
								confirmationEmpList.add(sbConfirmationList.toString());
							}
						}
					}else if(strUserType!=null && strUserType.equals(ADMIN)){
						confirmationEmpList.add(sbConfirmationList.toString());
					}
					
				}else if(confDate.equals(dayAfterTomorrowDate)){
					confirmationEmpList.add(sbConfirmationList.toString());
					/*if(strUserType!=null && strUserType.equals(HRMANAGER)){
						List<String> locations = hmUserLocationAccessMap.get(strEmpId);
						if(locations!=null && locations.size()>0 && locations.contains(rst.getString("wlocation_id"))){
							confirmationEmpList.add(sbConfirmationList.toString());
						}else{
							String empLocation = hmUserLocationMap.get(strEmpId);
							if((empLocation!=null && empLocation.equals(rst.getString("wlocation_id"))) || rst.getString("wlocation_id") == null){
								confirmationEmpList.add(sbConfirmationList.toString());
							}
						}
					}else if(strUserType!=null && strUserType.equals(ADMIN)){
						confirmationEmpList.add(sbConfirmationList.toString());
					}
					
				}else if(confDate.before(currDate)){
					confirmationEmpList.add(sbConfirmationList.toString());
					/*if(strUserType!=null && strUserType.equals(HRMANAGER)){
						List<String> locations = hmUserLocationAccessMap.get(strEmpId);
						if(locations!=null && locations.size()>0 && locations.contains(rst.getString("wlocation_id"))){
							confirmationEmpList.add(sbConfirmationList.toString());
						}else{
							String empLocation = hmUserLocationMap.get(strEmpId);
							if((empLocation!=null && empLocation.equals(rst.getString("wlocation_id"))) || rst.getString("wlocation_id") == null){
								confirmationEmpList.add(sbConfirmationList.toString());
							}
						}
					}else if(strUserType!=null && strUserType.equals(ADMIN)){
						confirmationEmpList.add(sbConfirmationList.toString());
					}
					
				}*/
				
				
			}
		}
		rst.close();
		pst.close();
		
		request.setAttribute("confirmationEmpList", confirmationEmpList);
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rst);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}

private void getResignAndFinalDayEmp(UtilityFunctions uF,String strUserType) {
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs=null;
	Database db = new Database();
	db.setRequest(request);

	try {
		con = db.makeConnection(con);
		

		Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
		boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
		
		String strUserTypeId = (String) session.getAttribute(USERTYPEID); 
		String strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		String strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
	
		Map<String, String> hmEmpProbation = new HashMap<String, String>();
		
		List<String> resignationEmpList = new ArrayList<String>();
		List<String> finalDayEmpList = new ArrayList<String>();
		Map<String, String> hmwLocation = CF.getWLocationMap(con, null, null);
		
		
		
		java.util.Date tomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 1),DBDATE, DATE_FORMAT),DATE_FORMAT );
		java.util.Date dayAfterTomorrowDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 2),DBDATE, DATE_FORMAT),DATE_FORMAT );
		//1		
		StringBuilder sbQuery=new StringBuilder();
		sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
				"and effective_type='"+WORK_FLOW_RESIGN+"' and effective_id in(select off_board_id from emp_off_board where " +
				"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id ");
		
			
		 if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
        	  }
		}
            
        if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
        	  }
		}
		sbQuery.append(")) group by effective_id");
		pst = con.prepareStatement(sbQuery.toString());
//		System.out.println("pst1==>"+pst);
		rs = pst.executeQuery();
		Map<String, String> hmNextApproval = new HashMap<String, String>();
		while(rs.next()) {
			hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
		}
		rs.close();
		pst.close();
		
		//2
		sbQuery=new StringBuilder();
		sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
				" and is_approved=0 and effective_type='"+WORK_FLOW_RESIGN+"' and effective_id in(select off_board_id from emp_off_board where " +
				"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id ");
		
				
		if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
        	  }
		}
            
         if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
        	  }
		}
		sbQuery.append("))");
		if(strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HOD))) {
			sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
		} else {
			sbQuery.append(" and user_type_id=? ");
		}
		sbQuery.append("group by effective_id,user_type_id");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1,uF.parseToInt(strEmpId));
		pst.setInt(2, uF.parseToInt(strUserTypeId));	
		if(strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HOD))) {
			pst.setInt(3, uF.parseToInt(strBaseUserTypeId));
		}
		
//		System.out.println("pst2==>"+pst);
		rs = pst.executeQuery();
		Map<String, String> hmMemNextApproval = new HashMap<String, String>();
		while(rs.next()) {
			hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
		}
		rs.close();
		pst.close();
		
		//3
		sbQuery=new StringBuilder();
		sbQuery.append("select off_board_id from emp_off_board where " +
				"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id ");
		
		
		
		 if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
        	  }
		}
            
        if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
        	  }
		}
		sbQuery.append(") and approved_1=-1 and approved_2=-1 ");
		pst = con.prepareStatement(sbQuery.toString());
//		System.out.println("pst3==>"+pst);
		rs = pst.executeQuery();	
		List<String> deniedList=new ArrayList<String>();
		while(rs.next()) {
			if(!deniedList.contains(rs.getString("off_board_id")) ){
				deniedList.add(rs.getString("off_board_id"));
			}
		}
		rs.close();
		pst.close();
		
		
		//4
		sbQuery=new StringBuilder();
		sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_RESIGN+"' " +
				"and effective_id in(select off_board_id from emp_off_board where " +
				"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id ");
		
		
		 if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
        	  }
		}
            
       if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
        	  }
		}
		sbQuery.append(")) group by effective_id");
		pst = con.prepareStatement(sbQuery.toString());
//		System.out.println("pst4==>"+pst);
		rs = pst.executeQuery();			
		while(rs.next()) {
			if(!deniedList.contains(rs.getString("effective_id"))) {
				deniedList.add(rs.getString("effective_id"));
			}
		}
		rs.close();
		pst.close();
		
		//5
		sbQuery=new StringBuilder();
		sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
				" and effective_type='"+WORK_FLOW_RESIGN+"' and effective_id in(select off_board_id from emp_off_board where " +
				"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id ");
				
		 if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
        	  }
		}
            
        if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
        	  }
		}
		sbQuery.append("))  group by effective_id,is_approved");
		pst = con.prepareStatement(sbQuery.toString());
//		System.out.println("pst5==>"+pst);
		rs = pst.executeQuery();			
		Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
		while(rs.next()) {
			hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
		}
		rs.close();
		pst.close();
		
		//6
		sbQuery=new StringBuilder();
		sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
				" and effective_type='"+WORK_FLOW_RESIGN+"' and effective_id in(select off_board_id from emp_off_board where " +
				"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id ");
				
		 if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
        	  }
		}
            
         if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
        	  }
		}
		sbQuery.append(")) group by effective_id,emp_id,user_type_id");
		pst = con.prepareStatement(sbQuery.toString());
//		System.out.println("pst6==>"+pst);
		rs = pst.executeQuery();			
		Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();	
		Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();
		while(rs.next()) {
			hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
			hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
		}
		rs.close();
		pst.close();
		
		//7
		sbQuery=new StringBuilder();
		sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
			" and effective_type='"+WORK_FLOW_RESIGN+"' and effective_id in(select off_board_id from emp_off_board where " +
				"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id ");
		
		 if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
        	  }
		}
            
         if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
        	  }
		}
		
		sbQuery.append(")) group by effective_id,emp_id,user_type_id");
		pst = con.prepareStatement(sbQuery.toString());
//		System.out.println("pst7==>"+pst);
		rs = pst.executeQuery();			
		Map<String, String> hmotherApproveBy = new HashMap<String, String>();	
		while(rs.next()) {
			hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
			hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
		}
		rs.close();
		pst.close();
		
		//8
		sbQuery=new StringBuilder();
		sbQuery.append("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_RESIGN+"' " +
				"and effective_id in(select off_board_id from emp_off_board where " +
				"emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id ");
		
		 if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
        	  }
		}
            
         if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
        	  }
		}
		
		sbQuery.append(")) ");
		if(strBaseUserType != null && (strBaseUserType.equals(CEO) || strBaseUserType.equals(HOD))) {
			sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
		} else {
			sbQuery.append(" and user_type_id=? ");
		}
		sbQuery.append(" order by effective_id,member_position");
		pst = con.prepareStatement(sbQuery.toString());
		pst.setInt(1, uF.parseToInt(strUserTypeId));
//		System.out.println("pst8==>"+pst);
		rs = pst.executeQuery();			
		Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
		while(rs.next()) {
			List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
			if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
			checkEmpList.add(rs.getString("emp_id"));
			
			hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
		}
		rs.close();
		pst.close();
		

		//9
		sbQuery=new StringBuilder();
		sbQuery.append("select e.*,wfd.user_type_id as user_type from (select * from emp_off_board eob, employee_official_details eod, employee_personal_details epd where eod.emp_id = eob.emp_id " +
				" and epd.emp_per_id = eob.emp_id and eod.emp_id = epd.emp_per_id ");
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)session.getAttribute(WLOCATION_ACCESS) != null && !((String)session.getAttribute(WLOCATION_ACCESS)).equals("")) {
        		  sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
        	  }
		}
            
        if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
        	  if((String)(String)session.getAttribute(ORG_ACCESS) != null && !((String)session.getAttribute(ORG_ACCESS)).equals("")) {
        		  sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
        	  } else {
        		  sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORGID)+")");
        	  }
		}
       
		
        sbQuery.append(") e, work_flow_details wfd where e.off_board_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_RESIGN+"' ");
		if(strUserType != null && !strUserType.equals(ADMIN)) {
			sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strEmpId)+" ");
			if (strBaseUserType != null && (strBaseUserType.equalsIgnoreCase(CEO) || strBaseUserType.equalsIgnoreCase(HOD))) {
				sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
			} else {
				sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
			}
		}
		sbQuery.append(" order by e.entry_date desc");
		pst = con.prepareStatement(sbQuery.toString());
		rs = pst.executeQuery();
		List<String> alList = new ArrayList<String>();	
		Map<String,String> statuaMp = (Map<String,String>)request.getAttribute("statuaMp");
		while(rs.next()) {
			
			List<String> checkEmpList=hmCheckEmp.get(rs.getString("off_board_id"));
			if(checkEmpList==null) checkEmpList=new ArrayList<String>();
			

			if(!checkEmpList.contains(strEmpId) && !strUserType.equalsIgnoreCase(ADMIN)){
				continue;
			}
			
			String userType = rs.getString("user_type");				
			if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER)) && alList.contains(rs.getString("off_board_id"))){
			
				continue;
			} else if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER)) && !alList.contains(rs.getString("off_board_id"))){
				userType = strUserTypeId;
			
				alList.add(rs.getString("off_board_id"));
			}
			
			StringBuilder sbResignationList = new StringBuilder();
			StringBuilder sbFinalDayList = new StringBuilder();
			String empimg = uF.showData(rs.getString("emp_image"), "avatar_photo.png");
			String supervisorName = CF.getEmpNameMapByEmpId(con, rs.getString("supervisor_emp_id"));
			String empDesignation = CF.getEmpDesigMapByEmpId(con, rs.getString("emp_per_id"));
			
			java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT),DATE_FORMAT );
			if(rs.getString("entry_date")!=null && !rs.getString("entry_date").equals("")) {
				String lastDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT);
				java.util.Date regDate = uF.getDateFormatUtil(lastDate,DATE_FORMAT );
				
				String resignationDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat());
				if(CF.getStrDocRetriveLocation()==null) { 
					sbResignationList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #CCCCCC \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ DOCUMENT_LOCATION +empimg+"\" height=\"20\" width=\"20\"> </span>");
				} else { 
					sbResignationList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #CCCCCC \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_22x22+"/"+empimg+"\" height=\"20\" width=\"20\"> </span>");
				}  
				sbResignationList.append("<span style=\"float: left; width: 92%; margin-left: 5px;\">");
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				sbResignationList.append(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				sbResignationList.append(" designation is "+uF.showData(empDesignation, "-"));
				sbResignationList.append(", manager is "+uF.showData(supervisorName, "-"));
				sbResignationList.append(", work location is "+uF.showData(hmwLocation.get(rs.getString("wlocation_id")), "-"));
				sbResignationList.append(" and resignation date is "+resignationDate+".");
				sbResignationList.append("</span>");
				
				if(regDate.equals(currDate) ) {
					resignationEmpList.add(sbResignationList.toString());
				} else {
					if(rs.getString("emp_status")!=null && !rs.getString("emp_status").equalsIgnoreCase("TERMINATED")){	
						if((rs.getInt("approved_1")!=1 && rs.getInt("approved_1")!= -1) && (rs.getInt("approved_2")!=1 && rs.getInt("approved_2")!=-1)) {
							resignationEmpList.add(sbResignationList.toString());
				        }
					}
				}
					
			}
		    
			if(rs.getString("emp_status")!=null && !rs.getString("emp_status").equalsIgnoreCase("TERMINATED")){		
			    if(rs.getString("last_day_date")!=null && !rs.getString("last_day_date").equals("")){
						String resignDate = uF.getDateFormat(rs.getString("last_day_date"), DBDATE, DATE_FORMAT);
						java.util.Date resignationDate = uF.getDateFormatUtil(resignDate,DATE_FORMAT );
					
						String finalDate = uF.getDateFormat(rs.getString("last_day_date"), DBDATE, CF.getStrReportDateFormat());
	
						if(CF.getStrDocRetriveLocation()==null) { 
							sbFinalDayList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #CCCCCC \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ DOCUMENT_LOCATION +empimg+"\" height=\"20\" width=\"20\"> </span>");
						} else { 
							sbFinalDayList.append("<span style=\"float: left; width:20px; height:20px; border:1px solid #CCCCCC \"><img class=\"lazy img-circle\" src=\"userImages/avatar_photo.png\" data-original=\""+ CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+rs.getString("emp_per_id")+"/"+I_22x22+"/"+empimg+"\" height=\"20\" width=\"20\"> </span>");
						}  
						sbFinalDayList.append("<span style=\"float: left; width: 92%; margin-left: 5px;\">");
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						
						sbFinalDayList.append(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
						sbFinalDayList.append(" designation is "+uF.showData(empDesignation, "-"));
						sbFinalDayList.append(", manager is "+uF.showData(supervisorName, "-"));
						sbFinalDayList.append(", work location is "+uF.showData(hmwLocation.get(rs.getString("wlocation_id")), "-"));
						sbFinalDayList.append(" and final day is "+finalDate+".");
						sbFinalDayList.append("</span>");
						if(rs.getInt("approved_1")==1 && rs.getInt("approved_2")==1){
							finalDayEmpList.add(sbFinalDayList.toString());
						}
					  }
			    }
			}
			rs.close();
			pst.close();
			
			request.setAttribute("resignationEmpList", resignationEmpList);
			request.setAttribute("finalDayEmpList", finalDayEmpList);
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
}

}
