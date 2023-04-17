 package com.konnect.jpms.employee;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.konnect.jpms.reports.AttendanceReport;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class MyHome implements IStatements {

	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	String strEmpId;
	String empType;
	private static Logger log = Logger.getLogger(EmpDashboard.class);

	public MyHome(HttpServletRequest request, HttpSession session, CommonFunctions CF, String strEmpId, String empType) {
		this.request = request;
		this.session = session;		 
		this.CF = CF; 
		this.strEmpId = strEmpId;
		this.empType = empType;
	}
     
	public String loadMyHome() {
		
//		System.out.println("strEmpId=="+strEmpId);
//		System.out.println("empType=="+empType);

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		ResultSet rs = null;
		CallableStatement cst = null;

		UtilityFunctions uF = new UtilityFunctions();
		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		int orgId = uF.parseToInt((String)session.getAttribute(ORGID));
//		System.out.println("nEmpId==>"+nEmpId);

		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-home\"></i><a href=\"MyHome.action?toAction=MyHome\" style=\"color: #3c8dbc;\"> Home</a></li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		try {
//			String strUserType = (String)session.getAttribute(USERTYPE);
			String strUserTypeId = (String)session.getAttribute(USERTYPEID);
//			System.out.println("strUserTypeId=====>"+strUserTypeId);
			request.setAttribute("strUserTypeId", strUserTypeId);
			
			con = db.makeConnection(con);
			
			String []arrEnabledModules = CF.getArrEnabledModules();
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con, request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			
			CF.getEmpUserTypeId(con,request,""+nEmpId);
			Map<String, String> hmEmployeeMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmLevelMap = CF.getEmpLevelMap(con);
			
			EmpDashboardData dashboardData = new EmpDashboardData(request, session, CF, uF, con, ""+ nEmpId);
			dashboardData.viewProfile(""+ nEmpId);
			request.setAttribute(TITLE, "My Home");
//			dashboardData.getClockEntries();
//			dashboardData.getEmpSkills(uF);
		
//			CF.setHomePage(session, request, uF);
			
			dashboardData.getMyTeam();
			getCountOfHub(con, uF);
			
			if(CF.isTaskRig()) {
				checkProjectOwner(con, uF);
				getTimesheetApproveRequest(con, uF);
			}
			dashboardData.getEmpLeaveStatus();
//			dashboardData.getPosition();
			
//			dashboardData.getEmpRosterInfo(alServices, hmServices);
			
//			String []arrEnabledModules = CF.getArrEnabledModules();
			
//			dashboardData.verifyClockDetails();
			
//			dashboardData.getPaySlipStatus();
			dashboardData.getPendingExceptionCount();
			dashboardData.getApprovedExceptionCount();
//			dashboardData.getRosterStatus();
//			dashboardData.getBusinessRuleStatus();
//			dashboardData.getProbationStatus();
			
//			dashboardData.getWorkedHours();
//			dashboardData.getEmpKPI();
//			dashboardData.getRosterVsWorkedHours();
//			getEmpLeaveCounts(con, uF);
			
//			dashboardData.getEmpServiceWorkingHourCounts();
			
			dashboardData.getDayThought();
			dashboardData.getResignationStatus();
			dashboardData.getMailCount();
			dashboardData.getBirthdays();
			dashboardData.getWorkAnniversary();		//added by parvez date: 28-10-2022
			
			viewTodaysEvent(uF);
			viewLiveJobList(uF, orgId);
			
//			dashboardData.getResignedEmployees();
//			dashboardData.getPendingAttendanceIssues();
			dashboardData.getUpcomingTeamLeaves(hmEmployeeMap);
//			dashboardData.getMyGrowthData();
			dashboardData.getTeamLeaveRequests(hmEmployeeMap, hmLevelMap);
			dashboardData.getTeamReimbursementRequests(hmEmployeeMap);
			dashboardData.getTeamTravelRequests(hmEmployeeMap, hmLevelMap);
			dashboardData.getTeamPerkRequests(hmEmployeeMap);
			dashboardData.getTeamLeaveEncashRequests(hmEmployeeMap);
			dashboardData.getTeamLTARequests(hmEmployeeMap);
			dashboardData.getTeamLoanRequests(hmEmployeeMap);
			dashboardData.getTeamResignRequests(hmEmployeeMap);
			dashboardData.getTeamRequisitionRequests(hmEmployeeMap);
			
			dashboardData.getCandidateSalaryNegotiationRequests();
			
			if(ArrayUtils.contains(arrEnabledModules, MODULE_PERFORMANCE_MANAGEMENT+"")>=0) {
				dashboardData.getTeamSelfReviewRequests(hmEmployeeMap);
			}
			
			dashboardData.getTeamNewJobRequests(hmEmployeeMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(cst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}

		return LOAD;
	}
	
	
	public void viewLiveJobList(UtilityFunctions uF, int orgId) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			List<List<String>> alJobList = new ArrayList<List<String>>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from recruitment_details where job_approval_status=1 and close_job_status=false and org_id="+orgId);
			pst=con.prepareStatement(sbQuery.toString());
//			System.out.println("pst1==>"+pst);
			rst=pst.executeQuery();
			while(rst.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rst.getString("recruitment_id"));
				alInner.add(rst.getString("job_code"));
				alInner.add(rst.getString("job_title"));
				alInner.add(rst.getString("no_position"));
				
				alJobList.add(alInner);
			}
			rst.close();
			pst.close();
			request.setAttribute("alJobList", alJobList);
//			System.out.println("sbEventIds ===>> " + sbEventIds);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	private void checkProjectOwner(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			boolean flag = false;
		//===start parvez date: 12-10-2022===	
//			pst = con.prepareStatement("select project_owner from projectmntnc where project_owner = ?");
//			pst.setInt(1, uF.parseToInt(strEmpId));
			pst = con.prepareStatement("select project_owners from projectmntnc where project_owners like '%,"+strEmpId+",%' ");
		//===end parvez date: 12-10-2022===	
	//		System.out.println("pst =====> "+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("poFlag", flag+"");
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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
	
	
	public void viewTodaysEvent(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmResourceName = CF.getEmpNameMap(con, null, null);
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmLevelMap = getLevelNameMap(con, uF);
			
			String logUserLevel = hmEmpLevelMap.get(strEmpId);
			
			List<String> alInner = new ArrayList<String>();
			pst = con.prepareStatement("select * from events where (sharing_level is null or length(trim(sharing_level))=0 or " +
				"sharing_level like '%,"+logUserLevel+",%' or added_by= ("+strEmpId+")) and (? between event_date and event_end_date or " +
				" ? <event_date) order by event_date desc limit 1");
//			System.out.println("pst ========>>> " + pst);
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()) {
				
				alInner.add(Integer.toString(rs.getInt("event_id")));//0
				if(rs.getString("event_date")!=null && !rs.getString("event_date").equals("")) {
					alInner.add(uF.getDateFormat(rs.getString("event_date"), DBDATE, CF.getStrReportDateFormat()));//1
				} else {
					alInner.add("");//1
				}
				
				if(rs.getString("event_end_date")!=null && !rs.getString("event_end_date").equals("")) {
					alInner.add(uF.getDateFormat(rs.getString("event_end_date"), DBDATE, CF.getStrReportDateFormat()));//2
				} else {
					alInner.add("");//2
				}
				
				alInner.add(uF.getDateFormat(rs.getString("posted_date"),DBTIMESTAMP, DATE_FORMAT_STR+" "+TIME_FORMAT_AM_PM));//3
				alInner.add(rs.getString("event_title"));//4
				alInner.add(rs.getString("event_desc"));//5
				alInner.add(hmResourceName.get(rs.getString("added_by")));//6
				alInner.add(rs.getString("location"));//7
				
//				System.out.println("events==>"+rs.getString("sharing_level"));
				String sharing_level = rs.getString("sharing_level");
				String[] levels = null;
				if(sharing_level!=null && !sharing_level.equals("")) {
					levels = rs.getString("sharing_level").split(",");
					StringBuilder sharing = null;
//					System.out.println("level's=>"+levels.length);
					for(String lvlId : levels) {
						
						if(uF.parseToInt(lvlId)>0) {
							if(sharing == null) {
								sharing = new StringBuilder();
								sharing.append(hmLevelMap.get(lvlId));
							} else {
								sharing.append(", "+hmLevelMap.get(lvlId));
							}
						}
					}
					if(sharing == null) {
						sharing = new StringBuilder();
					}
					alInner.add(sharing.toString());//8
				}else{					
					alInner.add("All Level's");//8
				}
				
				alInner.add(rs.getString("event_image"));//9
				alInner.add(rs.getString("added_by"));//10
				String extenstion = null;
				if(rs.getString("event_image") !=null && !rs.getString("event_image").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rs.getString("event_image").trim());
				}
				alInner.add(extenstion);//11
//				
				String eventImgPath = "";
				if(rs.getString("event_image")!=null && !rs.getString("event_image").equals("")) {
					if(CF.getStrDocSaveLocation()==null) {
						eventImgPath =  DOCUMENT_LOCATION +"/"+rs.getString("added_by")+"/"+rs.getString("event_image") ;
					} else {
						eventImgPath = CF.getStrDocRetriveLocation()+I_EVENTS+"/"+rs.getString("added_by")+"/"+rs.getString("event_image");
					}
				}
				//System.out.println("eventImg==>"+eventImgPath);
				String eventImage = "<img class='lazy' border=\"0\" style=\"max-height: 180px; max-width: 94%; padding: 5px; border-radius: 0%;\" src=\"images1/no-events.jpg\" data-original=\""+eventImgPath+"\" />";
				alInner.add(eventImage); //12
				alInner.add("");//13
				String from_time = rs.getString("from_time");
				String to_time = rs.getString("to_time");
				
				if(from_time != null && !from_time.equals("")){
					
					alInner.add(uF.getTimeFormatStr(from_time.substring(0,from_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));
				}else{
					alInner.add("");//14
				}
				if(to_time != null  && !to_time.equals("")){
					
					alInner.add(uF.getTimeFormatStr(to_time.substring(0,to_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));
				}else{
					alInner.add("");//15
				}
			}
//			System.out.println("eventId==>"+lastEventId);
			rs.close();
			pst.close();
			request.setAttribute("alInner", alInner);
//			System.out.println("sbEventIds ===>> " + sbEventIds);

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	
	public Map<String,String> getLevelNameMap(Connection con,UtilityFunctions uF){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLevelMap = new LinkedHashMap<String,String>();
		Database db = new Database();
		db.setRequest(request);
		try{
			
			pst = con.prepareStatement("SELECT * FROM level_details ");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmLevelMap.put(rs.getString("level_id"), rs.getString("level_name"));
			}
			rs.close();
			pst.close();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
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
		return hmLevelMap;
	}
	
	
	private void getCountOfHub(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
//		System.out.println("empType ===> " + empType);
//		System.out.println("empId ===> " + empId);
		try {
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as cnt from taskrig_user_alerts where alert_data is not null and alert_action is not null ");
			if(empType != null && empType.equalsIgnoreCase(CUSTOMER)){
				sbQuery.append(" and customer_id=?");
			} else {
				sbQuery.append(" and resource_id=?");
			}
//			sbQuery.append("");
			 pst = con.prepareStatement(sbQuery.toString());			
			 pst.setInt(1, uF.parseToInt(strEmpId));
//			 System.out.println("pst ===> " +pst);
			 rs = pst.executeQuery();
			 int feedCount = 0;
			 while (rs.next()) {
				 feedCount = rs.getInt("cnt");
			 }
			rs.close();
			pst.close();
			 
//			 System.out.println("sbNotifications ===>> " + sbNotifications);
			 request.setAttribute("feedCount", feedCount+"");
			 
				Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
				StringBuilder sbQuery1 = new StringBuilder();
				sbQuery1.append("SELECT count(*) as cnt from daythoughts where  day_id = ? and year = ?");
				pst = con.prepareStatement(sbQuery1.toString());
				pst.setInt(1, cal.get(Calendar.DAY_OF_YEAR));
				pst.setInt(2, cal.get(Calendar.YEAR));
				rs = pst.executeQuery();
				int quoteCount = 0;
				while (rs.next()) {
					quoteCount = rs.getInt("cnt");
				}
				rs.close();
				pst.close();
				request.setAttribute("quoteCount", quoteCount+"");
				
				
				StringBuilder sbQuery2 = new StringBuilder();
				sbQuery2.append("select count(*) as cnt from notices where (added_by = "+strEmpId+" or ispublish='t') and _date = ?");
				pst = con.prepareStatement(sbQuery2.toString());
				pst.setDate(1,  uF.getCurrentDate(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				int announcementCount = 0;
				while (rs.next()) {
					announcementCount = rs.getInt("cnt");
				}
				rs.close();
				pst.close();
				request.setAttribute("announcementCount", announcementCount+"");
				
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
				String logUserLevel = hmEmpLevelMap.get(strEmpId);
				StringBuilder sbQuery3 = new StringBuilder();
				
				sbQuery3.append("select count(*) as cnt from events where (sharing_level is null or length(trim(sharing_level))=0 or sharing_level like '%,"+logUserLevel+"%' or added_by = "+strEmpId+") and entry_date = ?");
				pst = con.prepareStatement(sbQuery3.toString());
				pst.setDate(1,uF.getCurrentDate(CF.getStrTimeZone()));
				rs = pst.executeQuery();
				int eventCount = 0;
				while (rs.next()) {
					eventCount = rs.getInt("cnt");
				}
				rs.close();
				pst.close();
				request.setAttribute("eventCount", eventCount+"");
				
				int totalHubCount = feedCount + announcementCount + eventCount + quoteCount;
				request.setAttribute("totalHubCount", totalHubCount+"");
				
//				Date nextDate = uF.getFutureDate(CF.getStrTimeZone(),1);
//				Date afterNextDate = uF.getFutureDate(CF.getStrTimeZone(),2);
//				String strNextDate = uF.getDateFormatUtil(nextDate,DBDATE);
//				String strAfterNextDate = uF.getDateFormatUtil(afterNextDate,DBDATE);
//				
//				StringBuilder sbQuery4 = new StringBuilder();
//				sbQuery4.append("select count(*) as cnt from holidays where _date =? or _date = ? and _year = ? ");
//				pst = con.prepareStatement(sbQuery4.toString());
//				pst.setDate(1,nextDate);
//				pst.setDate(2,afterNextDate);
//				pst.setInt(3, cal.get(Calendar.YEAR));
//				rs = pst.executeQuery();
//				int holidayCount = 0;
//				while (rs.next()) {
//					holidayCount = rs.getInt("cnt");
//				}
//				rs.close();
//				pst.close();
//				request.setAttribute("holidayCount", holidayCount);
				
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
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

	
	
	public void getTimesheetApproveRequest(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null; 
		ResultSet rs = null;
		try {
		
			pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_TIMESHEET+"' and effective_id in" +
				"(select ta.task_id from task_activity ta ) order by effective_id,member_position"); //where to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?	
//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	 
			while(rs.next()) {
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList == null) checkEmpList = new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("=======9========"+new Date());
			
//			System.out.println("=======11========"+new Date());
			
			Map<String, String> hmEmployeeName = CF.getEmpNameMap(con, null, null);
			
//			Map<String, String> hmActualWorkingDays = CF.getEmpNameMap(null, null);
			Map<String, String> hmActualWorkingDays = new HashMap<String, String>();
			pst = con.prepareStatement("select count(distinct task_date) as days, sum(actual_hrs) as actual_hrs, sum(billable_hrs) as billable_hrs, " +
				"ta.emp_id, submited_date from task_activity ta, work_flow_details wfd where  ta.task_id = wfd.effective_id " +
				"and wfd.emp_id = ? and ta.is_approved > 0 group by ta.emp_id,submited_date"); //task_date between ? and ? and
//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmActualWorkingDays.put(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_DAYS", rs.getString("days"));
				hmActualWorkingDays.put(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_ACT_HRS", uF.getTotalTimeMinutes100To60(rs.getString("actual_hrs")));
				hmActualWorkingDays.put(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_BILL_HRS", uF.getTotalTimeMinutes100To60(rs.getString("billable_hrs")));
			}
			rs.close();
			pst.close();
			
//			System.out.println("=======12========"+new Date());
			
//			 System.out.println("hmMemNextApproval 11===>> " + hmMemNextApproval);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select ta.task_id,ta.task_date,ta.emp_id,ta.is_approved,ta.generated_date,ta.approved_by,ta.submited_date from task_activity ta, " +
				"employee_personal_details epd where ta.emp_id = epd.emp_per_id "); //and to_date(ta.task_date::text,'yyyy-MM-dd') between ? and ?
			sbQuery.append(" and ta.is_approved = 1 order by ta.emp_id,ta.submited_date desc ");
//			System.out.println("sbQuery ===>> " + sbQuery.toString());
			pst = con.prepareStatement(sbQuery.toString());				
//			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			System.out.println("pst===>" + pst); 
			rs = pst.executeQuery();
			List<List<String>> alReport = new LinkedList<List<String>>();
//			List<String> alInner = new ArrayList<String>();
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			String strSubmitDTNew = null;
			String strSubmitDTOld = null;
			int count = 0;
			List<String> alTimesheetRequests = new ArrayList<String>();
			while(rs.next()) {
				count++;
				if(count > 10) {
					break;
				}
				strEmpIdNew = rs.getString("emp_id");
				strSubmitDTNew = rs.getString("submited_date");
				
				if(strEmpIdNew!=null && strEmpIdNew.equalsIgnoreCase(strEmpIdOld) && strSubmitDTNew!=null && strSubmitDTNew.equalsIgnoreCase(strSubmitDTOld)) {
					continue;
				}
				
				List<String> checkEmpList = hmCheckEmp.get(rs.getString("task_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				if(!checkEmpList.contains(strEmpId)) { 
					continue;
				}
				
//				List<String> alInner = new ArrayList<String>();
//				StringBuilder sb = new StringBuilder();
//				sb.append("<img src=\"images1/icons/pending.png\" border=\"0\" />");
//				alInner.add(sb.toString()); //0
//				alInner.add(hmEmployeeName.get(rs.getString("emp_id"))); //1
//				alInner.add(uF.getDateFormat(rs.getString("submited_date"), DBDATE, CF.getStrReportDateFormat())); //2
//
//				alInner.add(uF.showData(hmActualWorkingDays.get(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_DAYS"), "0")); //3
//				alInner.add(uF.showData(hmActualWorkingDays.get(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_ACT_HRS"), "0")); //4
//				alInner.add(uF.showData(hmActualWorkingDays.get(rs.getString("emp_id")+"_"+rs.getString("submited_date")+"_BILL_HRS"), "0")); //5
//				alReport.add(alInner);
				
				String strSubmitDate = uF.getDateFormat(rs.getString("submited_date"), DBDATE, DATE_FORMAT_STR);
				
				String orgId = (String)session.getAttribute(ORGID);
//				System.out.println("orgId ===>> "+ orgId);
				String[] strPayCycleDates = CF.getPayCycleFromDate(con, uF.getDateFormat(rs.getString("task_date"), DBDATE, DATE_FORMAT), CF.getStrTimeZone(), CF, orgId);
				String strPaycycle = null;
				if(strPayCycleDates != null) {
					strPaycycle = strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2];
				}
//				System.out.println("strPaycycle ===>> " + strPaycycle);
				
				alTimesheetRequests.add("<span style=\"width: 85%; float: left;\">"+hmEmployeeName.get(rs.getString("emp_id"))+", has submitted timesheet on "+strSubmitDate+"</span>"+
					"<span style=\"float: right;\"><a href=\"Login.action?role=3&product=3&userscreen=myTeamTimesheets&paycycle="+strPaycycle+"\" title=\"Click to go Timesheets\">View</a></span>");
				strEmpIdOld = strEmpIdNew;
				strSubmitDTOld = strSubmitDTNew;
			}
			rs.close();
			pst.close();
//			System.out.println("=======alReport========"+alReport.toString());
			
			request.setAttribute("alTimesheetRequests", alTimesheetRequests);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	
	
//	public void getTasksCount(Connection con, UtilityFunctions uF){
//		
//		PreparedStatement pst  = null;
//		ResultSet rs = null;
//		
//		try {
//			
//			pst = con.prepareStatement("select count(*) as count from activity_info where emp_id = ? and approve_status = 'n'");			
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			rs = pst.executeQuery();
//			
//			
//			int nTaskCount = 0;
//			while (rs.next()) {
//				nTaskCount = rs.getInt("count");
//			}
//			rs.close();
//			pst.close();
//			session.setAttribute("TASK_COUNT",nTaskCount+"");
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if(rs !=null){
//				try {
//					rs.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//			if(pst !=null){
//				try {
//					pst.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
}
