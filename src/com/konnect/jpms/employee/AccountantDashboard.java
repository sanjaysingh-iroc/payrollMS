package com.konnect.jpms.employee;

import java.sql.Connection;
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
import com.konnect.jpms.charts.SemiCircleMeter;
import com.konnect.jpms.reports.AttendanceReport;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class AccountantDashboard implements IStatements {

	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	String strEmpId;
	private static Logger log = Logger.getLogger(AccountantDashboard.class);
	AccountantDashboard(HttpServletRequest request, HttpSession session, CommonFunctions CF, String strEmpId) {
		this.request = request;
		this.session = session;
		this.CF = CF;
		this.strEmpId = strEmpId; 
	}
 
	public String loadDashboard() {
		/*Database db = new Database();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectEmployee1V);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();

			if (rs.next()) {
				request.setAttribute("EMPCODE", rs.getString("empcode"));
				request.setAttribute("EMPNAME", rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
				request.setAttribute("DATE", uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", "yyyy-MM-dd", "EEEE, MMMM dd,yyyy"));
				request.setAttribute("IMAGE", ((rs.getString("emp_image") != null && rs.getString("emp_image").length() > 0) ? rs.getString("emp_image") : "avatar_photo.png"));
				request.setAttribute("DEPT", rs.getString("dept_name"));
				request.setAttribute("WL_NAME", rs.getString("wlocation_name"));
				request.setAttribute("EMAIL", rs.getString("emp_email"));
			}
			pst.close();

			pst = con.prepareStatement(selectEmployee3V);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));

			rs = pst.executeQuery();

			if (rs.next()) {
				request.setAttribute(MANAGER, rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
			}

			pst.close();

			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int YEAR = cal.get(Calendar.YEAR);
			int MONTH = cal.get(Calendar.MONTH) + 1;
			int Day = cal.get(Calendar.DAY_OF_MONTH);
			int MinDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			int MaxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

			pst = con.prepareStatement(selectPresentDays1);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));

			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
			pst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));

			rs = pst.executeQuery();

			if (rs.next()) {
				request.setAttribute("COUNT", rs.getString("count"));
			}

			pst.close();

			pst = con.prepareStatement(selectApprovalsCountAdmin);
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			int totalApprovals = 0;
			while (rs.next()) {

				if (rs.getInt("approved") == -2) {
					request.setAttribute("PENDING", rs.getString("count"));
					totalApprovals += rs.getInt("count");
				} else if (rs.getInt("approved") == -1) {
					request.setAttribute("DENIED", rs.getString("count"));
					totalApprovals += rs.getInt("count");
				} else if (rs.getInt("approved") == 1) {
					request.setAttribute("APPROVED", rs.getString("count"));
					totalApprovals += rs.getInt("count");
				}
			}
			request.setAttribute("TOTAL_APPROVALS", totalApprovals);
			pst.close();

			pst = con.prepareStatement(selectWLocation);
			rs = pst.executeQuery();

			String strW1 = null;
			String strW2 = null;
			List alBusinessName = new ArrayList();
			int i = 0;
			while (rs.next()) {
				if (i == 0) {
					strW1 = rs.getString("wlocation_name");

				} else if (i == 1) {
					strW2 = rs.getString("wlocation_name");
				} else {
					break;
				}
				alBusinessName.add(rs.getString("wlocation_name"));
				i++;

			}
			request.setAttribute("WLOCATION", alBusinessName);

			Map hmPresentDays = new HashMap();

			pst = con.prepareStatement(selectPresentDays2);
			pst.setString(1, strW1);
			pst.setString(2, strW2);

			pst.setDate(3, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE));
			// pst.setDate(3, uF.getDateFormat("2011-04-02", "yyyy-MM-dd"));

			rs = pst.executeQuery();

			while (rs.next()) {
				hmPresentDays.put(rs.getString("wlocation_name"), rs.getString("present"));
			}

			request.setAttribute("PRENSENCE", hmPresentDays);
			pst.close();

			Map hmEmployeeCount = new HashMap();
			Map hmAbsenceCount = new HashMap();
			pst = con.prepareStatement(selectEmployeeCount);
			rs = pst.executeQuery();

			while (rs.next()) {

				int presentEmp = 0;

				presentEmp = uF.parseToInt((String) hmPresentDays.get(rs.getString("wlocation_name")));

				hmAbsenceCount.put(rs.getString("wlocation_name"), (rs.getInt("empcount") - presentEmp) + "");
				hmEmployeeCount.put(rs.getString("wlocation_name"), rs.getString("empcount"));

			}

			request.setAttribute("EMPCOUNT", hmEmployeeCount);
			request.setAttribute("ABSENCE", hmAbsenceCount);
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
			db.closeStatements(pst);
			db.closeResultSet(rs);
		}
		return LOAD;*/
 
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			String strUserType = (String)session.getAttribute(USERTYPE);
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmployeeMap = CF.getEmpNameMap(con,null, null);  
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpProfileImageMap = CF.getEmpProfileImage(con);
			Map<String, String> hmServicesMap = CF.getServicesMap(con,true);
			Map<String, Map<String, String>> hmWorkLocationMap = CF.getWorkLocationMap(con);
			Map<String, String> hmLevelMap = CF.getEmpLevelMap(con);
			
			pst = con.prepareStatement(selectEmployee1V);
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();


			if (rs.next()) {
				request.setAttribute("EMPCODE", rs.getString("empcode"));
				request.setAttribute("EMPNAME", rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
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
				request.setAttribute("MANAGER", rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
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
			objAR.attendanceReport(CF, uF, strUserType, session); 
			
			Map hmEmployeeAttendanceCount = (Map)request.getAttribute("hmEmployeeAttendanceCount");
			if(hmEmployeeAttendanceCount == null) hmEmployeeAttendanceCount = new HashMap();
			
			List alDates = (List)request.getAttribute("alDates");
			if(alDates == null) alDates = new ArrayList();
			List alServicesTotal = (List)request.getAttribute("alServicesTotal");
			if(alServicesTotal == null) alServicesTotal = new ArrayList();
			
			StringBuilder sbDatesAttendance = new StringBuilder();
			StringBuilder sbDatesAttendanceDate = new StringBuilder();
			
			
			sbDatesAttendance.append("{name: 'Came Early',data: [");
			for(int k=0; alDates!=null && k<alDates.size(); k++){
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
			for(int k=0; alDates!=null && k<alDates.size(); k++){
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
			for(int k=0; alDates!=null && k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeAttendanceCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = 0;
				
				
//					System.out.println("hm= Came Late ="+hm);
				
				for(int s=0; alServicesTotal != null && s<alServicesTotal.size(); s++){
					String strServiceId = (String)alServicesTotal.get(s);
//						System.out.println("strServiceId ="+strServiceId);
					nCount += uF.parseToInt((String)hm.get("LATE_IN_"+strServiceId));
//						System.out.println("Count ="+uF.parseToInt((String)hm.get("LATE_IN_"+strServiceId)));
//						System.out.println("Total count ="+nCount);
				}
				sbDatesAttendance.append(nCount);
//					sbDatesAttendance.append(uF.showData((String)hm.get("LATE_IN_"+strServiceId), "0"));
				if(alDates != null && k<alDates.size()-1){
					sbDatesAttendance.append(",");
				}
			}
			sbDatesAttendance.append("]},");
			
			sbDatesAttendance.append("{name: 'Left Late',data: [");
			for(int k=0; alDates != null && k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeAttendanceCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = 0;
				for(int s=0; alServicesTotal != null && s<alServicesTotal.size(); s++){
					String strServiceId = (String)alServicesTotal.get(s);
					nCount += uF.parseToInt((String)hm.get("LATE_OUT_"+strServiceId));
				}
				sbDatesAttendance.append(nCount);
//					sbDatesAttendance.append(uF.showData((String)hm.get("LATE_OUT_"+strServiceId), "0"));
				if(alDates != null && k<alDates.size()-1){
					sbDatesAttendance.append(",");
				}
			}
			sbDatesAttendance.append("]},");
			
			sbDatesAttendance.append("{name: 'Came Ontime',data: [");
			for(int k=0; alDates != null && k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeAttendanceCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = 0;
				for(int s=0; alServicesTotal != null && s<alServicesTotal.size(); s++){
					String strServiceId = (String)alServicesTotal.get(s);
					nCount += uF.parseToInt((String)hm.get("ONTIME_IN_"+strServiceId));
				}
				sbDatesAttendance.append(nCount);
//					sbDatesAttendance.append(uF.showData((String)hm.get("ONTIME_IN_"+strServiceId), "0"));
				if(alDates != null && k<alDates.size()-1){
					sbDatesAttendance.append(",");
				}
			}
			sbDatesAttendance.append("]},");
			
			sbDatesAttendance.append("{name: 'Left Ontime',data: [");
			for(int k=0; alDates != null && k<alDates.size(); k++){
				Map hm = (Map)hmEmployeeAttendanceCount.get((String)alDates.get(k));
				if(hm==null)hm=new HashMap();
				int nCount = 0;
				for(int s=0; alServicesTotal != null && s<alServicesTotal.size(); s++){
					String strServiceId = (String)alServicesTotal.get(s);
					nCount += uF.parseToInt((String)hm.get("ONTIME_OUT_"+strServiceId));
				}
				sbDatesAttendance.append(nCount);
//					sbDatesAttendance.append(uF.showData((String)hm.get("ONTIME_OUT_"+strServiceId), "0"));
				if(alDates != null && k<alDates.size()-1){
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
			if(hmEmployeesIssues == null) hmEmployeesIssues = new HashMap<String, Map<String,String>>();
			
			for(int d=0; alDates != null && alDates.size() > 0 && d<1; d++){ // d==0 and d<1 is for current date only
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
			rs.close();
			pst.close();
			
			request.setAttribute("alLeaves", alLeaves);
			
			
//			pst = con.prepareStatement(selectLeaveRequestHRManager);			
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
//			
////			System.out.println("pst==Upcoming Leave=>"+pst);
//			List alLeaveRequest = new ArrayList();
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				String strDate = rs.getString("entrydate");
//				
//				if(strDate!=null && strDate.equals(strToday)){
//					strDate = ", <span>today</span>";
//				}else if(strDate!=null && strDate.equals(strYesterday)){
//					strDate = ", <span>yesterday</span>";
//				}else {
//					strDate = " on "+ uF.getDateFormat(rs.getString("entrydate"), DBDATE, "EEEE");
//					strDate = "<span>"+strDate.toLowerCase()+"</span>";
//				}
//				alLeaveRequest.add("<span style=\"width: 95%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied leave from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM")+strDate+"</span>"+
//						"<span style=\"float: right;\"> <a style=\"float:right\" href=\"ManagerLeaveApproval.action?E="+rs.getString("leave_id")+"&LID="+hmLevelMap.get(rs.getString("emp_id"))+"\"><img src=\"images1/icons/approved.png\" alt=\"Approve\" title=\"Approve\" /></a></span>");
//			}
//			rs.close();
//			pst.close();
			
//			pst = con.prepareStatement("SELECT leave_type_id,is_compensatory FROM leave_type  where leave_type_id>0 order by leave_type_name");
//			rs = pst.executeQuery();
//			Map<String,String> hmLeaveCompensate = new HashMap<String, String>();
//			while (rs.next()) {
//				hmLeaveCompensate.put(rs.getString("leave_type_id"), rs.getString("is_compensatory"));
//			}
//			rs.close();
//			pst.close();
//			
//			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select ele.emp_id,ele.entrydate,ele.approval_from,ele.approval_to_date,ele.leave_id,ele.leave_type_id from emp_leave_entry ele,work_flow_details wft where wft.emp_id=? and ele.is_approved=0 " +
//					"and ele.leave_id=wft.effective_id and wft.effective_type='"+WORK_FLOW_LEAVE+"' and ele.entrydate >=? order by ele.leave_id desc");
//			pst = con.prepareStatement(sbQuery.toString());		
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setDate(2, uF.getPrevDate(CF.getStrTimeZone(), 1));
//			rs = pst.executeQuery();
//			List<String> alLeaveRequest = new ArrayList<String>();
//			while(rs.next()){
//					String strDate = rs.getString("entrydate");
//					if(strDate!=null && strDate.equals(strToday)){
//						strDate = ", <span>today</span>";
//					}else if(strDate!=null && strDate.equals(strYesterday)){
//						strDate = ", <span>yesterday</span>";
//					}else {
//						strDate = " on "+ uF.getDateFormat(rs.getString("entrydate"), DBDATE, "dd MMM");
//						strDate = "<span>"+strDate.toLowerCase()+"</span>";
//					}
//					
////					alLeaveRequest.add("<span style=\"width: 90%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied leave from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM")+strDate+"</span>"+
////							"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to approve this request?')) window.location='ManagerLeaveApproval.action?type=type&apType=auto&apStatus=1&E="+rs.getString("leave_id")+"&LID="+hmLevelMap.get(rs.getString("emp_id"))+"&strCompensatory="+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"';\"><img title=\"Approved\" src=\"images1/icons/icons/approve_icon.png\" border=\"0\" /></a> " +
////							" <a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to denied this request?')) window.location='ManagerLeaveApproval.action?type=type&apType=auto&apStatus=-1&E="+rs.getString("leave_id")+"&LID="+hmLevelMap.get(rs.getString("emp_id"))+"&strCompensatory="+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"';\"><img title=\"Denied\" src=\"images1/icons/icons/close_button_icon.png\" border=\"0\" /></a></span>");
//					alLeaveRequest.add("<span style=\"width: 90%; float: left;\">"+hmEmployeeMap.get(rs.getString("emp_id"))+", has applied leave from "+uF.getDateFormat(rs.getString("approval_from"), DBDATE, "dd MMM")+" to "+uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, "dd MMM")+strDate+"</span>"+
//							"<span style=\"float: right;\"> <a href=\"javascript:void(0);\" onclick=\"approveDenyLeave('1','"+rs.getString("leave_id")+"','"+hmLevelMap.get(rs.getString("emp_id"))+"','"+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"');\"><img title=\"Approved\" src=\"images1/icons/icons/approve_icon.png\" border=\"0\" /></a> " +
//							" <a href=\"javascript:void(0);\" onclick=\"approveDenyLeave('-1','"+rs.getString("leave_id")+"','"+hmLevelMap.get(rs.getString("emp_id"))+"','"+hmLeaveCompensate.get(rs.getString("leave_type_id"))+"');\"><img title=\"Denied\" src=\"images1/icons/icons/close_button_icon.png\" border=\"0\" /></a></span>");
//				 
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("alLeaveRequest",alLeaveRequest);
			
			
			/**
			 ********************** UPCOMING LEAVE REQUESTS END ************************
			 * 
			 * */
			
//			getPendingExceptionCount(con, uF);
//			getPendingReimbursementsCount(con, uF);
//			getPendingRequisitionCount(con, uF);
			
//			getReimbursementRequests(con, uF, hmEmployeeMap);
//			getRequisitionRequests(con, uF, hmEmployeeMap);
			
			getTasksCount(con, uF);
			getTodaysReportSentCount(con, uF);
			getBestEmployee(con, uF);
			getServiceEmployeeCount(con, uF);
			getWlocationEmployeeCount(con, uF);
			getBirthday(con, uF, hmEmployeeMap);
			getUpcomingEvents(con, uF);
			getDayThought(con, uF);
			CF.getAlertUpdates(CF, strEmpId, request,strUserType);
			getSkillsEmployeeCount(con, uF, 6);
			getEmployeeTurnoverCount(con, uF);
			getPendingApprovalsCountDetails(con, uF, MONTH, YEAR, MinDay, MaxDay);
			
			request.setAttribute("hmEmployeeMap",hmEmployeeMap);
			request.setAttribute("hmServicesMap",hmServicesMap);
			request.setAttribute("hmEmpDesigMap",hmEmpDesigMap);
			request.setAttribute("hmEmpProfileImageMap",hmEmpProfileImageMap);
			request.setAttribute("hmWorkLocationMap",hmWorkLocationMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return LOAD;
	
	
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
			
//			pst = con.prepareStatement(selectReimbursementForDashboard); 			
//			List<String> alReimbursementRequest = new ArrayList<String>();
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				String strDate = rs.getString("entry_date");
//				
//				if(strDate!=null && strDate.equals(strToday)){
//					strDate = ", <span>today</span>";
//				}else if(strDate!=null && strDate.equals(strYesterday)){
//					strDate = ", <span>yesterday</span>";
//				}else {
//					strDate = " on "+ uF.getDateFormat(rs.getString("entry_date"), DBDATE, "EEEE");
//					strDate = "<span>"+strDate.toLowerCase()+"</span>";
//				}
//				
//				String strCurrency = "";
//				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0){
//					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
//					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
//					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
//				} 
//				
//				alReimbursementRequest.add("<span style=\"width: 95%; float: left;\"><strong>"+hmEmployeeMap.get(rs.getString("emp_id"))+"</strong>, has applied for reimbursement for <strong>"+strCurrency+rs.getString("reimbursement_amount")+"</strong> from "+uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat())+"</span>"+
//						"<p style=\"float:left;width:95%;font-size: 10px; font-style: italic;\">"+"Reimbursement Type:"+uF.showData(rs.getString("reimbursement_type"), "")+"<br/>Reason:"+uF.showData(rs.getString("reimbursement_purpose"), "")+"</p>"+
//						"<span style=\"float: right;\"> <a style=\"float:right\" href=\"Reimbursements.action\"><img src=\"images1/icons/approved.png\" alt=\"Approve\" title=\"Click to Approve/Deny\" /></a></span>");
//			}
//			rs.close();
//			pst.close();
			
			pst = con.prepareStatement("select  er.emp_id as emp,* from emp_reimbursement er,work_flow_details wft where wft.emp_id=? and wft.is_approved=0 " +
					" and er.reimbursement_id=wft.effective_id and wft.effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' and er.entry_date>=? order by er.reimbursement_id desc");
			pst.setInt(1, uF.parseToInt(strEmpId));
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
				
				StringBuilder sb = new StringBuilder();
				sb.append("<span style=\"width: 90%; float: left;\"><strong>"+hmEmployeeMap.get(rs.getString("emp"))+"</strong>, has applied for reimbursement for <strong>"+strCurrency+uF.formatIntoComma(uF.parseToDouble(rs.getString("reimbursement_amount")))+"</strong> from "+uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat())+"</span>"+
						"<p style=\"float:left;width:95%;font-size: 10px; font-style: italic;\">"+"Reimbursement Type:"+uF.showData(rs.getString("reimbursement_type"), "")+"<br/>Reason:"+uF.showData(rs.getString("reimbursement_purpose"), "")+"</p>");
				
				sb.append("<span style=\"float: right;\"><a href=\"javascript:void(0);\" onclick=\"approveDenyRembursement('1','" + rs.getString("reimbursement_id")+ "');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"></i></a>&nbsp;" +
						"<a href=\"javascript:void(0);\" onclick=\"approveDenyRembursement('-1','" + rs.getString("reimbursement_id")+ "');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"></i></a>" +
						"</span>");
				alReimbursementRequest.add(sb.toString());
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alReimbursementRequest",alReimbursementRequest);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
			
			List<String> alRequisitionRequest = new ArrayList<String>();
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
						/*"<span style=\"float: right;\"> <a style=\"float:right\" href=\"Requisitions.action\"> <img src=\"images1/icons/approved.png\" alt=\"Approve\" title=\"Click to Approve/Deny\" /></a></span>");*/
				"<span style=\"float: right;\"> <a style=\"float:right\" href=\"Requisitions.action\"> <i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" alt=\"Approve\" title=\"Click to Approve/Deny\"></i></a></span>");
			}
			rs.close();
			pst.close();
			request.setAttribute("alRequisitionRequest",alRequisitionRequest);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
			
//			String strToday1 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM-dd");
//			String strTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 1)+"", DBDATE, "MM-dd");
//			String strDayAfterTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 2)+"", DBDATE, "MM-dd");
			
			pst = con.prepareStatement("select * from events where event_date >= ? order by event_date desc limit 5");			
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			
			log.debug("EVENTS pst====>"+pst);
			
			
			List<String> alEvents = new ArrayList<String>();
			while (rs.next()) {
				String strEventDate = uF.getDateFormat(rs.getString("event_date"), DBDATE, "MM-dd");
				alEvents.add(strEventDate+": "+rs.getString("event_title"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alEvents",alEvents);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
				}count ++;
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
	
	
	public void getServiceEmployeeCount(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			pst = con.prepareStatement(pendingServiceEmployeeDashboardCount);
			rs = pst.executeQuery();

			Map<String, String> hmServicesEmployeeCount = new HashMap<String, String>();
			String []arrServices = null;
			while (rs.next()) {
				if(rs.getString("service_id")!=null){
					arrServices = rs.getString("service_id").split(",");
				}
				
				
				for(int i=0; arrServices!=null && i<arrServices.length; i++){
					int nCount = uF.parseToInt(hmServicesEmployeeCount.get(arrServices[i].trim()));
					hmServicesEmployeeCount.put(arrServices[i].trim(),   uF.formatIntoComma(nCount+1) );
				}
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmServicesEmployeeCount", hmServicesEmployeeCount);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
	

	public void getDayThought(Connection con, UtilityFunctions uF){
		
		PreparedStatement pst  = null;
		ResultSet rs = null;
		
		try {
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			
			pst = con.prepareStatement(selectThought);			
			pst.setInt(1, cal.get(Calendar.DAY_OF_YEAR));
			rs = pst.executeQuery();
			String strThought = null;
			String strThoughtBy = null;
			while (rs.next()) {
				strThought = rs.getString("thought_text");
				strThoughtBy = rs.getString("thought_by"); 
			}
			rs.close();
			pst.close();
			request.setAttribute("DAY_THOUGHT_TEXT",strThought);
			request.setAttribute("DAY_THOUGHT_BY",strThoughtBy);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
			pst = con.prepareStatement(pendingWLocationDashboardCount);
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
			List<List<String>> skillwiseEmpCountList = new ArrayList<List<String>>();
			pst = con.prepareStatement("select skill_id, count(sd.emp_id) as count from employee_official_details eod,employee_personal_details epd, skills_description sd where sd.emp_id = eod.emp_id and sd.emp_id = epd.emp_per_id and epd.emp_per_id = eod.emp_id and eod.wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) and  is_alive=true group by skill_id"+((nLimit>0)?" limit "+nLimit:""));
			pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
			rs = pst.executeQuery();

//			Map<String, String> hmSkillsEmployeeCount = new HashMap<String, String>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("skill_id"));
				innerList.add(CF.getSkillNameBySkillId(con, rs.getString("skill_id")));
				innerList.add(uF.formatIntoComma(rs.getDouble("count")));
				skillwiseEmpCountList.add(innerList);
//				hmSkillsEmployeeCount.put(rs.getString("skill_id"), uF.formatIntoComma(rs.getDouble("count")));
			}
			rs.close();
			pst.close();
			request.setAttribute("skillwiseEmpCountList", skillwiseEmpCountList);
//			request.setAttribute("hmSkillsEmployeeCount", hmSkillsEmployeeCount);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
			
			
			
			pst = con.prepareStatement(pendingPendingApprovalDashboardCount1);
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
			
			
//			pst = con.prepareStatement("select count(*) as count, is_approved from emp_leave_entry elt, employee_official_details eod where eod.emp_id = elt.emp_id and wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) and entrydate between ? AND ? group by is_approved ");
//			pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
//			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
//			pst.setDate(3, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));
//			
			
			pst = con.prepareStatement(pendingPendingApprovalDashboardCount2);
			pst.setDate(1, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MinDay < 10) ? "0" + MinDay : MinDay), DBDATE));
			pst.setDate(2, uF.getDateFormat(YEAR + "-" + ((MONTH < 10) ? "0" + MONTH : MONTH) + "-" + ((MaxDay < 10) ? "0" + MaxDay : MaxDay), DBDATE));
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
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
}
