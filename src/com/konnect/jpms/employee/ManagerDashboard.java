package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.konnect.jpms.charts.BarChart;
import com.konnect.jpms.charts.LinearZMeter;
import com.konnect.jpms.charts.PieCharts;
import com.konnect.jpms.charts.SemiCircleMeter;
import com.konnect.jpms.recruitment.RequirementApproval;
import com.konnect.jpms.reports.AttendanceReport;
import com.konnect.jpms.reports.HolidayReport;
import com.konnect.jpms.task.ProjectPerformanceReportCP;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class ManagerDashboard implements IStatements {

	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF; 
	String strEmpId;
	private static Logger log = Logger.getLogger(ManagerDashboard.class);
 
	ManagerDashboard(HttpServletRequest request, HttpSession session, CommonFunctions CF, String strEmpId) {
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
			
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmployeeMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpProfileImageMap = CF.getEmpProfileImage(con);
			Map<String, String> hmServicesMap = CF.getServicesMap(con, true);
			Map<String, String> hmServicesDescMap = CF.getServicesMap(con,true);
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

			pst.close();
			rs.close();

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
			
				
				request.setAttribute("MANAGER", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
			}

			pst.close();
			rs.close();
			
			
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

			pst = con.prepareStatement(selectPresentDays1_Manager);
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
			rs.close();
			pst.close();
			
			StringBuilder sb = new StringBuilder();
			sb.append("['Present', "+PRESENT_ABSENT_DATA[0]+"], ['Absent', "+PRESENT_ABSENT_DATA[1]+"]");
			
			request.setAttribute("CHART_WORKED_ABSENT_C", sb.toString());
			
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
			
			pst = con.prepareStatement(selectApprovalsCountForManager);
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
			
			
			pst = con.prepareStatement(selectApprovalsManager);			
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
						alReasons.add(rs.getString("emp_fname")+", was late for office "+strDate+((rs.getString("reason")!=null && rs.getString("reason").length()>0)?" - \""+rs.getString("reason")+"\".":"."));
					}else if(rs.getDouble("early_late")<0){
						alReasons.add(rs.getString("emp_fname")+", has come early "+strDate+((rs.getString("reason")!=null && rs.getString("reason").length()>0)?" - \""+rs.getString("reason")+"\".":"."));
					}
					
				}else{
					if(rs.getDouble("early_late")>0){
						alReasons.add(rs.getString("emp_fname")+", has left late "+strDate+((rs.getString("reason")!=null && rs.getString("reason").length()>0)?" - \""+rs.getString("reason")+"\".":"."));
					}else if(rs.getDouble("early_late")<0){
						alReasons.add(rs.getString("emp_fname")+", has left early "+strDate+((rs.getString("reason")!=null && rs.getString("reason").length()>0)?" - \""+rs.getString("reason")+"\".":"."));
					}
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReasons",alReasons);
			
			
//			verifyClockDetails();
			
			
			/*List alNotice = new ArrayList();
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
			objAR.attendanceReport(CF, uF, strUserType, session);
			
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
			
			
			
			
			/*StringBuilder sbQuery0 = new StringBuilder();
			sbQuery0.append("select emp_id from project_emp_details where _isteamlead = false  and pro_id in (select pro_id from projectmntnc where (start_date, deadline) overlaps (to_date(?::text, 'YYYY-MM-DD') ,to_date(?::text, 'YYYY-MM-DD') +1) and pro_id in (select pro_id from project_emp_details where emp_id != ?  and _isteamlead = true)) ");
			pst = con.prepareStatement(sbQuery0.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs= pst.executeQuery();
			List<String> alEmployeeAssignedProjects = new ArrayList<String>();
			while(rs.next()){
				alEmployeeAssignedProjects.add(rs.getString("emp_id"));
			}
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, emp_leave_entry ee,leave_type lt where ee.leave_type_id = lt.leave_type_id and epd.emp_per_id = ee.emp_id");
			sbQuery.append(" and (approval_from, approval_to_date) overlaps (to_date(?::text, 'YYYY-MM-DD'),to_date(?::text, 'YYYY-MM-DD') +1)");
			sbQuery.append(" and ( ee.emp_id in (select emp_id from project_emp_details where _isteamlead = false  and pro_id in (select pro_id from projectmntnc where (start_date, deadline) overlaps (to_date(?::text, 'YYYY-MM-DD') ,to_date(?::text, 'YYYY-MM-DD') +1) and pro_id = (select pro_id from project_emp_details where emp_id = ?) )) ");
			sbQuery.append(" OR ee.emp_id in (select emp_id from employee_official_details where supervisor_emp_id  = ?) )");
			sbQuery.append(" and is_approved = 1 order by ee.leave_from desc");
			pst = con.prepareStatement(sbQuery.toString());
			
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setInt(6, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			List alLeaves = new ArrayList();
			
			
			System.out.println("====>"+pst);
			
			while(rs.next()){
				if(alEmployeeAssignedProjects.contains(rs.getString("emp_id"))){
					continue;
				}
				
				System.out.println("====>"+rs.getString("emp_id"));
				
				alLeaves.add(hmEmployeeMap.get(rs.getString("emp_id"))+", is on leave from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM"));
			}
			request.setAttribute("alLeaves",alLeaves);
			*/
			
			/*
			
			pst = con.prepareStatement(selectUpcomingLeaveManager);			
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
			*/
			
			String []arrEnabledModules = CF.getArrEnabledModules();
			
			
			
			/**
			 ********************** UPCOMING LEAVE REQUESTS END ************************
			 * 
			 * */
			
			getLeaveRequests(con, uF, hmEmployeeMap, hmLevelMap);
			getPendingExceptionCount(con, uF);
			getPendingReimbursementsCount(con, uF);
			getPendingRequisitionCount(con, uF);
			
			getReimbursementRequests(con, uF, hmEmployeeMap);
			getRequisitionRequests(con, uF, hmEmployeeMap);
			
			getPendingExceptionCount(con, uF, hmEmployeeMap);
			getPendingLeaveCount(con, uF);
			getPendingReimbursementsCount(con, uF);
			getTasksCount(con, uF);
			getTodaysReportSentCount(con, uF);

			
			getBestEmployee(con, uF);			
			getServiceEmployeeCount(con, uF);			
			getWlocationEmployeeCount(con, uF);
			getAchievements(con, uF, CF, hmEmployeeMap);
			
			
			getSkillsEmployeeCount(con, uF, 6);
			getResignedEmployees(con, uF, hmEmployeeMap);
			getTaskDetails(con,uF, hmEmployeeMap);
			
			
			CF.getBirthday(con, uF, CF, hmEmployeeMap, request);
			CF.getUpcomingEvents(con, uF, CF, request);
			CF.getDayThought(con, uF, CF, request);
			
			
//			ProjectPerformanceReportCP objPPR = new ProjectPerformanceReportCP();
//			objPPR.setServletRequest(request);
//			objPPR.getProjectDetails(uF.parseToInt(strEmpId), uF, CF, 4);
			
			
			if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_CAREER_DEV_PLANNING+"")>=0){
				//getRecruitmentDetails(con, uF, hmDesigMap);
				getTrainingDetails(con, uF, CF); 
				getLearningGaps(con, uF, CF);
			}
			if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_PERFORMANCE_MANAGEMENT+"")>=0){
				getPerformanceDetails(con, uF, CF);
			}
			
			
			
			
			
			
			request.setAttribute("hmEmployeeMap",hmEmployeeMap);
			request.setAttribute("hmServicesMap",hmServicesMap);
			request.setAttribute("hmWorkLocationMap",hmWorkLocationMap);
			request.setAttribute("hmEmpDesigMap",hmEmpDesigMap);
			request.setAttribute("hmEmpProfileImageMap",hmEmpProfileImageMap);
			request.setAttribute("hmServicesDescMap",hmServicesDescMap);
			session.setAttribute("arrEnabledModules",arrEnabledModules);
			request.setAttribute("arrEnabledModules", arrEnabledModules);
			
/*			RequirementApproval objReqApproval = new RequirementApproval();
			objReqApproval.setServletRequest(request);
			objReqApproval.getRquirementRequestsForManager();
*/			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}

		return LOAD;
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
			
			List<List<String>> appraisalDetails = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from appraisal_details where ? between from_date and to_date and is_publish = TRUE order by appraisal_details_id");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			//System.out.println("pst :::::::::: "+pst);
			while(rs.next()){
				List<String> appraisalinner = new ArrayList<String>();
				appraisalinner.add(rs.getString("appraisal_name"));
				appraisalinner.add(freqNamehm.get(rs.getString("frequency")));
				int totemp = uF.parseToInt(getAppraisalEmpCount(rs.getString("employee_id")));
				int totempfinal = uF.parseToInt(getEmpFinalAppraisal(con, rs.getString("appraisal_details_id")));
				int totunfinalemp = totemp - totempfinal;
				appraisalinner.add(totemp+"");
				appraisalinner.add(totunfinalemp+"");
				appraisalinner.add(totempfinal+"");
				appraisalDetails.add(appraisalinner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("appraisalDetails", appraisalDetails);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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
	}
	
	private String getEmpFinalAppraisal(Connection con, String appraisalid) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		String empfinalcount="0";
		try {
			
			List<String> appraisalDetails = new ArrayList<String>();
			pst = con.prepareStatement("select count(*) as count from appraisal_final_sattlement where appraisal_id =? and if_approved = TRUE ");
			pst.setInt(1, uF.parseToInt(appraisalid));
			rs = pst.executeQuery();
			while(rs.next()){
				empfinalcount = rs.getString("count");
			}
			rs.close();
			pst.close();
			request.setAttribute("appraisalDetails", appraisalDetails);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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
		return empfinalcount;
	}

	private void getLearningGaps(Connection con, UtilityFunctions uF,CommonFunctions CF) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		List<Map<String,String>> outerList=new ArrayList<Map<String,String>>();
		
		try {
			
			String strUserType = (String) session.getAttribute(USERTYPE);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select tp.training_title,ts.* from training_schedule ts, training_plan tp " +
					"where ? between ts.start_date and ts.end_date and ts.plan_id=tp.plan_id ");
			if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+") ");
			}
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,uF.getCurrentDate(CF.getStrTimeZone()));
			rs=pst.executeQuery();
			int scheduleCnt=0;
			while(rs.next()){
				Map<String, String> hmLearnGap=new HashMap<String, String>();
				hmLearnGap.put("TRAINING_TITLE",rs.getString("training_title"));
				
				String empIds = rs.getString("emp_ids").substring(1, rs.getString("emp_ids").length()-1);
				String temp[] =empIds.split(",");
				hmLearnGap.put("NO_OF_PARTICIPANT",""+temp.length);
				
				outerList.add(hmLearnGap);
				
				scheduleCnt+=temp.length;
			}
			rs.close();
			pst.close();
			request.setAttribute("trainingGapList", outerList);
			request.setAttribute("schedule", ""+scheduleCnt);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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

	}

	private void getTrainingDetails(Connection con, UtilityFunctions uF, CommonFunctions CF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			Map<String, String> trainingNamehm = new HashMap<String, String>();
			pst = con.prepareStatement("select * from training_plan");
			rs = pst.executeQuery();
			while(rs.next()){
				trainingNamehm.put(rs.getString("plan_id"), rs.getString("training_title"));
			}
			
			List<List<String>> trainingDetails = new ArrayList<List<String>>();
			pst = con.prepareStatement("select * from training_schedule where ? between start_date and end_date order by schedule_id");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			//System.out.println("pst :::::::::: "+pst);
			while(rs.next()){
				List<String> traininginner = new ArrayList<String>();
				traininginner.add(trainingNamehm.get(rs.getString("plan_id")));
				traininginner.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
				int totemp = uF.parseToInt(getAppraisalEmpCount(rs.getString("emp_ids")));
				traininginner.add(totemp+"");
				traininginner.add("0");
				trainingDetails.add(traininginner);
			}
			rs.close();
			pst.close();
			request.setAttribute("trainingDetails", trainingDetails);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
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
	}
	
	private String getAppraisalEmpCount(String empList) {
		int empCount=0;
		try {
			if(empList != null && !empList.equals("")){
				empList = empList.substring(1, empList.length()-1);
				String arremplist[] = empList.split(",");
				empCount = arremplist.length;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return empCount+"";
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
			
			pst = con.prepareStatement(pendingExceptionDahsboard);			
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate2, DATE_FORMAT));
			rs = pst.executeQuery();
			
			
			int nPendingExceptionCount = 0;
			while (rs.next()) {
				nPendingExceptionCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
			session.setAttribute("PENDING_EXCEPTION_COUNT",nPendingExceptionCount+"");
			
			
			pst = con.prepareStatement(pendingExceptionsManager);
			pst.setInt(1, -2);
			pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
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
				if(alAttendance.size()==10)break;
			}
			request.setAttribute("alAttendance", alAttendance);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
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
	}
	
	public void getPendingLeaveCount(Connection con, UtilityFunctions uF){
		
//		PreparedStatement pst  = null;
//		ResultSet rs = null;
		int nPendingLeaveCount = 0;
		try {
			
			session.setAttribute("PENDING_LEAVE_COUNT",nPendingLeaveCount+"");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}
	}
	
	
	public void getResignedEmployees(Connection con, UtilityFunctions uF, Map<String, String> hmEmployeeMap){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement("select * from emp_off_board where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and approved_1 is null order by entry_date desc");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
			rs = pst.executeQuery();

			List<String> alResignedEmployees = new ArrayList<String>();
			int count=0;
			while (rs.next()) {

				StringBuilder sb = new StringBuilder();

				sb.append(hmEmployeeMap.get(rs.getString("emp_id")) +" has resigned on "+rs.getString("entry_date"));
				
				sb.append("<div id=\"myDiv"+count+"\" style=\"float:right;margin-right:10px;\"> ");
				sb.append("<a href=\"ResignationReport.action\">View</a> ");
				/*sb.append("<a href=\"javascript:void(0)\" onclick=\"getContent('myDiv"+count+"','UpdateRequest.action?S=1&M=1&RID="+rs.getString("off_board_id")+"&T=REG');\" > <img src=\"images1/icons/approved.png\" title=\"Approve\" /></a> ");*/
				sb.append("<a href=\"javascript:void(0)\" onclick=\"getContent('myDiv"+count+"','UpdateRequest.action?S=1&M=1&RID="+rs.getString("off_board_id")+"&T=REG');\" > <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approve\" ></i></a> ");
				sb.append("<a href=\"javascript:void(0)\" onclick=\"getContent('myDiv"+count+"','UpdateRequest.action?S=-1&M=1&RID="+rs.getString("off_board_id")+"&T=REG');\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Deny\" ></i></a> ");
				/*sb.append("<a href=\"javascript:void(0)\" onclick=\"getContent('myDiv"+count+"','UpdateRequest.action?S=-1&M=1&RID="+rs.getString("off_board_id")+"&T=REG');\"><img src=\"images1/icons/denied.png\" title=\"Deny\" /></a> ");*/
				sb.append("</div>");
				
				alResignedEmployees.add(sb.toString());
				count++;
			}
			rs.close();
			pst.close();
			request.setAttribute("alResignedEmployees", alResignedEmployees);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
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
	}
	
	public void getLeaveRequests(Connection con, UtilityFunctions uF, Map<String, String> hmEmployeeMap, Map<String, String> hmLevelMap){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			pst = con.prepareStatement("select  ele.emp_id,ele.entrydate,ele.approval_from,ele.approval_to_date,ele.leave_id from emp_leave_entry ele,work_flow_details wft where wft.emp_id=? " +
					"and ele.is_approved=1 and ele.approval_from>=? and ele.approval_to_date<=? " +
					" and ele.leave_id=wft.effective_id and wft.effective_type='"+WORK_FLOW_LEAVE+"' order by ele.approval_from ");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(3, uF.getFutureDate(uF.getCurrentDate(CF.getStrTimeZone()), 30));
			
			rs = pst.executeQuery();
			List<String> alLeaves = new ArrayList<String>();
			while(rs.next()){
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
			
			StringBuilder sbQuery = new StringBuilder();
			/*sbQuery.append("select ele.emp_id,ele.entrydate,ele.approval_from,ele.approval_to_date,ele.leave_id " +
					"from emp_leave_entry ele,work_flow_details wft where wft.emp_id=? and ele.is_approved=0 " +
					"and ele.leave_id=wft.effective_id and wft.effective_type='"+WORK_FLOW_LEAVE+"' and ele.entrydate>=? order by ele.entrydate");
			pst = con.prepareStatement(sbQuery.toString());		
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1)); */
			sbQuery.append("select ele.emp_id,ele.entrydate,ele.approval_from,ele.approval_to_date,ele.leave_id,ele.leave_type_id from emp_leave_entry ele,work_flow_details wft where wft.emp_id=? and ele.is_approved=0 " +
					"and ele.leave_id=wft.effective_id and wft.effective_type='"+WORK_FLOW_LEAVE+"'  order by ele.entrydate desc limit 5");
			pst = con.prepareStatement(sbQuery.toString());		
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			
			List<String> alLeaveRequest = new ArrayList<String>();
			
			while(rs.next()){
					String strDate = rs.getString("entrydate");
					if(strDate!=null && strDate.equals(strToday)){
						strDate = ", <span>today</span>";
					}else if(strDate!=null && strDate.equals(strYesterday)){
						strDate = ", <span>yesterday</span>";
					}else {
						strDate = " on "+ uF.getDateFormat(rs.getString("entrydate"), DBDATE, "dd MMM");
						strDate = "<span>"+strDate.toLowerCase()+"</span>";
					}
					
//					alLeaveRequest.add("<span style=\"width: 95%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied leave from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM")+strDate+"</span>"+
//							"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to approve this request?')) window.location='ManagerLeaveApproval.action?type=type&apType=auto&apStatus=1&E="+rs.getString("leave_id")+"&LID="+hmLevelMap.get(rs.getString("emp_id"))+"&strCompensatory="+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"';\"><img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" /></a> " +
//							" <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to denied this request?')) window.location='ManagerLeaveApproval.action?type=type&apType=auto&apStatus=-1&E="+rs.getString("leave_id")+"&LID="+hmLevelMap.get(rs.getString("emp_id"))+"&strCompensatory="+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"';\"><img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" /></a></span>");
					alLeaveRequest.add("<span style=\"width: 90%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied leave from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM")+strDate+"</span>"+
							"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenyLeave('1','"+rs.getString("leave_id")+"','"+hmLevelMap.get(rs.getString("emp_id"))+"','"+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approved\"></i></a> " +
							" <a href=\"javascript:void(0);\" onclick=\"approveDenyLeave('-1','"+rs.getString("leave_id")+"','"+hmLevelMap.get(rs.getString("emp_id"))+"','"+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i></a></span>");
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alLeaves",alLeaves);
			request.setAttribute("alLeaveRequest",alLeaveRequest);
			session.setAttribute("LEAVE_REQUEST_COUNT", alLeaveRequest.size()+"");
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
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
		
		pst = con.prepareStatement("select  er.emp_id as emp,* from emp_reimbursement er,work_flow_details wft where wft.emp_id=? and wft.is_approved=0 " +
				" and er.reimbursement_id=wft.effective_id and wft.effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and er.entry_date>=? order by er.entry_date");
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
				strDate = " on "+ uF.getDateFormat(rs.getString("entry_date"), DBDATE, "dd MMM");
				strDate = "<span>"+strDate.toLowerCase()+"</span>";
			}
			
			String strCurrency = "";
			if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp"))) > 0){
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp")));
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			} 
			
//			alReimbursementRequest.add("<span style=\"width: 95%; float: left;\"><strong>"+hmEmployeeMap.get(rs.getString("emp"))+"</strong>, has applied for reimbursement for <strong>"+strCurrency+uF.formatIntoComma(uF.parseToDouble(rs.getString("reimbursement_amount")))+"</strong> from "+uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat())+"</span>"+
//					"<p style=\"float:left;width:95%;font-size: 10px; font-style: italic;\">"+"Reimbursement Type:"+uF.showData(rs.getString("reimbursement_type"), "")+"<br/>Reason:"+uF.showData(rs.getString("reimbursement_purpose"), "")+"</p>"+
//					"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to approve this request?')) window.location='UpdateReimbursements.action?type=type&S=1&RID=" + rs.getString("reimbursement_id")
//					+ "&T=RIM&M=AA';\"><img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" /></a>&nbsp;" +
//					"<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to denied this request?')) window.location='UpdateReimbursements.action?type=type&S=-1&RID=" + rs.getString("reimbursement_id")
//					+ "&T=RIM&M=AA';\"><img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" /></a>" +
//							"</span>");
			alReimbursementRequest.add("<span style=\"width: 95%; float: left;\"><strong>"+hmEmployeeMap.get(rs.getString("emp"))+"</strong>, has applied for reimbursement for <strong>"+strCurrency+uF.formatIntoComma(uF.parseToDouble(rs.getString("reimbursement_amount")))+"</strong> from "+uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat())+"</span>"+
					"<p style=\"float:left;width:95%;font-size: 10px; font-style: italic;\">"+"Reimbursement Type:"+uF.showData(rs.getString("reimbursement_type"), "")+"<br/>Reason:"+uF.showData(rs.getString("reimbursement_purpose"), "")+"</p>"+
					/*"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenyRembursement('1','" + rs.getString("reimbursement_id")+ "');\"><img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" /> </a>&nbsp;" +*/
					/*"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenyRembursement('1','" + rs.getString("reimbursement_id")+ "');\"><img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" /> </a>&nbsp;" +*/
					"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenyRembursement('1','" + rs.getString("reimbursement_id")+ "');\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i> </a>&nbsp;" +
					
					/*"<a href=\"javascript:void(0);\" onclick=\"approveDenyRembursement('-1','" + rs.getString("reimbursement_id")+ "');\"><img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" /></a>" +*/
					"<a href=\"javascript:void(0);\" onclick=\"approveDenyRembursement('-1','" + rs.getString("reimbursement_id")+ "');\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i></a>" +
							"</span>");
		}
		pst.close();
		rs.close();
		
		request.setAttribute("alReimbursementRequest",alReimbursementRequest);
		
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
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
}

	public void getRequisitionRequests(Connection con, UtilityFunctions uF, Map<String, String> hmEmployeeMap){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strYesterday = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DBDATE);
			
			pst = con.prepareStatement("select * from requisition_details where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and status=0 ");			
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
						/*"<span style=\"float: right;\"> <a style=\"float:right\" href=\"Requisitions.action\"><img src=\"images1/icons/approved.png\" alt=\"Approve\" title=\"Click to Approve/Deny\" /></a></span>");*/
				"<span style=\"float: right;\"> <a style=\"float:right\" href=\"Requisitions.action\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" alt=\"Approve\" title=\"Click to Approve/Deny\"></i></a></span>");
			}
			pst.close();
			rs.close();
			request.setAttribute("alRequisitionRequest",alRequisitionRequest);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
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
	}

	public void getSkillsEmployeeCount(Connection con, UtilityFunctions uF, int nLimit){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement("select skill_id, count(sd.emp_id) as count  from employee_official_details eod,employee_personal_details epd, skills_description sd where sd.emp_id = eod.emp_id  and sd.emp_id = epd.emp_per_id and epd.emp_per_id = eod.emp_id and eod.emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and  (emp_status = '"+PROBATION+"' or emp_status = '"+PERMANENT+"')group by skill_id"+((nLimit>0)?" limit "+nLimit:""));
			pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			System.out.println("getSkillsEmployeeCount pst ===>> " + pst);
			List<List<String>> skillwiseEmpCountList = new ArrayList<List<String>>();
//			Map<String, String> hmSkillsEmployeeCount = new HashMap<String, String>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("skill_id"));
				innerList.add(CF.getSkillNameBySkillId(con, rs.getString("skill_id")));
				innerList.add(uF.formatIntoComma(rs.getDouble("count")));
				skillwiseEmpCountList.add(innerList);
//				hmSkillsEmployeeCount.put(rs.getString("skills_name"), uF.formatIntoComma(rs.getDouble("count")) );
			}
			rs.close();
			pst.close();
			request.setAttribute("skillwiseEmpCountList", skillwiseEmpCountList);
//			request.setAttribute("hmSkillsEmployeeCount", hmSkillsEmployeeCount);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
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
	}

	
	public void getPendingExceptionCount(Connection con, UtilityFunctions uF){
		
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
			
			pst = con.prepareStatement("select count(*) as count from attendance_details where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and approved = -2 ");			
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strDate2, DATE_FORMAT));
			rs = pst.executeQuery();

			int nPendingExceptionCount = 0;
			while (rs.next()) {
				nPendingExceptionCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
			session.setAttribute("PENDING_EXCEPTION_COUNT",nPendingExceptionCount+"");
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
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
	}
	
	public void getTasksCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement("select count(*) as count from activity_info where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and finish_task = 'y' and approve_status = 'n'");			
			pst.setInt(1, uF.parseToInt(strEmpId));
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
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
	}
	
	public void getTodaysReportSentCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement("select count(*) as count from task_activity where emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and issent_report = true and task_date=?");			
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
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
	}

	public void getBestEmployee(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			pst = con.prepareStatement("select * from ( select sum(hours_worked) as hours_worked, sum(actual_hours) as actual_hours, rd.emp_id from attendance_details ad, roster_details rd where ad.emp_id=rd.emp_id and ad.service_id = rd.service_id and rd._date = to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.in_out = 'OUT' and rd._date between ? and ? group by rd.emp_id order by hours_worked desc limit 5) a where emp_id in (select distinct emp_id from employee_official_details where supervisor_emp_id = ?) ");
			pst.setDate(1, uF.getPrevDate(CF.getStrTimeZone(), 30));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
			
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
				}count ++;
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("WORKED_HRS", uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("hours_worked"))));
				hmInner.put("ACTUAL_HRS", uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("actual_hours"))));
				
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
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
	}
	
	
	public void getServiceEmployeeCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
//			pst = con.prepareStatement("select count(emp_id) as count, service_id from employee_personal_details epd, attendance_details ad where epd.emp_per_id = ad.emp_id and ad.emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?)  and emp_status = 'ACTIVE' and ad.in_out = 'IN' and to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') between ? and ?  group by service_id");
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 30));
//			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			
			pst = con.prepareStatement("select service_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and supervisor_emp_id = ? and is_alive=true");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));

			rs = pst.executeQuery();

			Map<String, String> hmServicesEmployeeCount = new HashMap<String, String>();
			String []arrServices = null;
			
			while (rs.next()) {
				
				if(rs.getString("service_id")!=null){
					arrServices = rs.getString("service_id").split(",");
				}
				
				
				for(int i=0; arrServices!=null && i<arrServices.length; i++){
					int nCount = uF.parseToInt(hmServicesEmployeeCount.get(arrServices[i]));
					hmServicesEmployeeCount.put(arrServices[i],  (nCount+1)+"");
				}
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmServicesEmployeeCount", hmServicesEmployeeCount);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
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
			
			pst = con.prepareStatement("select * from employee_activity_details ead, activity_details ad where ead.activity_id = ad.activity_id and emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) and is_achievements = true and ad.activity_id not in (16) order by effective_date desc limit 4");
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
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
		}finally {
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
	}
	
	public void getWlocationEmployeeCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
//			pst = con.prepareStatement("select count(eod.emp_id) as count, wlocation_id from employee_personal_details epd, attendance_details ad, employee_official_details eod where epd.emp_per_id = eod.emp_id and eod.emp_id = ad.emp_id and epd.emp_per_id = ad.emp_id and ad.emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?)  and emp_status = 'ACTIVE' and ad.in_out = 'IN' and to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? group by wlocation_id");
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 30));
//			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			
			pst = con.prepareStatement("select count(emp_per_id), wlocation_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and supervisor_emp_id = ? and is_alive=true group by wlocation_id");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
			
			
			rs = pst.executeQuery();

			Map<String, String> hmWLocationEmployeeCount = new HashMap<String, String>();
			while (rs.next()) {
				hmWLocationEmployeeCount.put(rs.getString("wlocation_id"), uF.formatIntoComma(rs.getDouble("count")) );
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmWLocationEmployeeCount", hmWLocationEmployeeCount);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
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
	}
	
	public void getTaskDetails(Connection con, UtilityFunctions uF, Map hmEmployeeMap){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			
					
			Map hmProjectTeamLead = new HashMap();
			pst = con.prepareStatement("select * from project_emp_details where _isteamlead = true");
			rs = pst.executeQuery();
			while(rs.next()){
				hmProjectTeamLead.put(rs.getString("pro_id"), rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			
			
//			pst = con.prepareStatement("select activity_name,ai.pro_id,pro_name,pmc.completed,pmc.deadline,ai.already_work  from projectmntnc pmc, activity_info ai where pmc.pro_id=ai.pro_id and pmc.added_by = ? and ai.approve_status='n' and pmc.approve_status='n' order by ai.deadline limit 4");
			pst = con.prepareStatement("select *  from projectmntnc where added_by = ? and approve_status='n' order by deadline limit 4");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			
			List alTaskList = new ArrayList();
			List alTaskInner = new ArrayList();
			
			while(rs.next()){
				
				Date currentDate = uF.getCurrentDate(CF.getStrTimeZone());
				Date deadLineDate = uF.getDateFormat(rs.getString("deadline"), DBDATE);
				Date startDate = uF.getDateFormat(rs.getString("start_date"), DBDATE);
				alTaskInner = new ArrayList();
//				alTaskInner.add(rs.getString("activity_name") +" ["+rs.getString("pro_name")+"]");
				alTaskInner.add(rs.getString("pro_name"));
				
				if(uF.parseToInt(rs.getString("completed"))>=100){
					alTaskInner.add("Completed");
				}else{
					
					/*if(currentDate!=null && deadLineDate!=null && currentDate.after(deadLineDate)){
						alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/denied.png\" border=\"0\"><span style=\"color:red\">Overdue</span>");
					}else{
						alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/approved.png\" border=\"0\"><span style=\"color:green\">Working</span>");
					}
					*/
					
					
					if(currentDate!=null && deadLineDate!=null && currentDate.after(deadLineDate) && uF.parseToDouble(rs.getString("already_work"))>0){
						/*alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/denied.png\" border=\"0\"><span style=\"color:red\">Overdue</span>");*/
						alTaskInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i><span style=\"color:red\">Overdue</span>");
					}else if(currentDate!=null && startDate!=null && currentDate.before(startDate) && uF.parseToDouble(rs.getString("already_work"))==0){
						/*alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/pullout.png\" border=\"0\"><span style=\"color:orange\">Planned</span>");*/
						alTaskInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" style=\"padding-right:3px\"></i><span style=\"color:orange\">Planned</span>");
					}else if(currentDate!=null && deadLineDate!=null && uF.parseToDouble(rs.getString("already_work"))>0){
						/*alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/approved.png\" border=\"0\"><span style=\"color:green\">Working</span>");*/
						alTaskInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" style=\"padding-right:3px\"></i><span style=\"color:green\">Working</span>");
					}else{
						/*alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/pullout.png\" border=\"0\"><span style=\"color:orange\">Planned</span>");*/
						alTaskInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" style=\"padding-right:3px\"><span style=\"color:orange\">Planned</span>");
					}
					
				}
				
				alTaskInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				alTaskInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("already_work")))); 
				alTaskInner.add(uF.showData((String)hmEmployeeMap.get((String)hmProjectTeamLead.get(rs.getString("pro_id"))), ""));
				alTaskList.add(alTaskInner);
				
			}
			rs.close();
			pst.close();
			request.setAttribute("alTaskList",alTaskList);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
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
	}
	
}  
