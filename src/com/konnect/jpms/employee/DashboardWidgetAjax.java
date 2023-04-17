package com.konnect.jpms.employee;

import java.sql.CallableStatement; 
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DashboardWidgetAjax extends ActionSupport implements ServletRequestAware,IStatements{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private static Logger log = Logger.getLogger(DashboardWidgetAjax.class);

	
	HttpSession session;
	CommonFunctions CF;
	String strType; 
	String strUserType;
	UtilityFunctions uF;
	
	public String execute() throws Exception {
		
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		
		strType = (String)request.getParameter("TYPE");
		strUserType = (String)session.getAttribute(USERTYPE);
		uF = new UtilityFunctions();
		
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(EMPLOYEE)){
				getEmployeeData(con, uF, strType);
			}
			
			log.debug(CF.getURLInfo(request)+"=="+strType+"=");
//			System.out.println(CF.getURLInfo(request)+"=="+strType+"=");
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		
		return SUCCESS;
	}
		
	public void getEmployeeData(Connection con, UtilityFunctions uF, String strType){
		
		
		
		StringBuilder sb = null;
		String []arrEnabledModules = null;
		Map<String, String> hmEmployeeMap = null;
		Map<String, String> hmEmpWlocationMap = null;
		Map<String, String> hmEmpDepartmentMap = null;
		Map<String, Map<String, String>> hmWlocationMap = null;
		Map<String, String> hmDepartmentMap = null;
		Map<String, String> hmServices = null;
		
				
		try {
		
			
			
			
			
			switch (uF.parseToInt(strType)){
			case 1:				// 1 is for Achievements
				
				getAchievements(con, uF);
				
				break;
			case 2:				// 2 is for requests Leave + reimbursements
				
				getUpcomingRequests(con, uF);
				
				break;
			case 3:				// 3 is for updates
				
				hmEmployeeMap = CF.getEmpNameMap(con, null, null);
				hmEmpWlocationMap = CF.getEmpWlocationMap(con);
				hmEmpDepartmentMap = CF.getEmpDepartmentMap(con);
				hmWlocationMap = CF.getWorkLocationMap(con);
				hmDepartmentMap = CF.getDeptMap(con);
				
				
				sb = new StringBuilder();				
				getDayThought(con, uF, sb);
				getResignationStatus(con, uF, sb);
				getMailCount(con, uF, sb);
				getBirthdays(con, uF, sb, hmEmpWlocationMap, hmWlocationMap, hmEmployeeMap, hmDepartmentMap, hmEmpDepartmentMap);
				
				break;
			case 4:					// 4 is for upcoming tasks
				
				
				arrEnabledModules = CF.getArrEnabledModules();
				if(ArrayUtils.contains(arrEnabledModules, MODULE_PROJECT_MANAGEMENT+"")>=0){
					sb = new StringBuilder();
					getUpcomingTasks(con, uF, sb);
				}
				break;
				
			case 5:					// 4 is for upcoming tasks
				
				arrEnabledModules = CF.getArrEnabledModules();
				if(ArrayUtils.contains(arrEnabledModules, MODULE_PROJECT_MANAGEMENT+"")>=0){
					hmEmployeeMap = CF.getEmpNameMap(con, null, null);
					sb = new StringBuilder();
					getTaskDetails(con, uF, sb, hmEmployeeMap);
				}
				
				break;
			
			case 6:					// 6 is for upcoming roster
				
				arrEnabledModules = CF.getArrEnabledModules();
				if(ArrayUtils.contains(arrEnabledModules, MODULE_PROJECT_MANAGEMENT+"")>=0){
					hmServices = CF.getServicesMap(con,true);
					List alServices = new ArrayList();
					sb = new StringBuilder();
					getEmpRosterInfo(con, uF, sb, alServices, hmServices);
				}
				
				break;
				

			case 7:					// 6 is for training certificates
				arrEnabledModules = CF.getArrEnabledModules();
				if(ArrayUtils.contains(arrEnabledModules, MODULE_CAREER_DEV_PLANNING+"")>=0){
					sb = new StringBuilder();
					getCertificates(con, uF, sb);
				}
				
				break;
			case 8:
				arrEnabledModules = CF.getArrEnabledModules();
				if(ArrayUtils.contains(arrEnabledModules, MODULE_CAREER_DEV_PLANNING+"")>=0){
					sb = new StringBuilder();
					getUpcomingTrainings(con, uF, sb);
				}
				
				break;
			case 9:
				break;
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	private void getAchievements(Connection con, UtilityFunctions uF) {
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
			
			pst = con.prepareStatement("select * from employee_activity_details ead, activity_details ad where ead.activity_id = ad.activity_id and emp_id = ? and is_achievements = true order by effective_date desc limit 4");
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			
			List<String> alAchievements = new ArrayList<String>();
			while(rs.next()){
				getAchievements(con, uF.parseToInt(rs.getString("activity_id")), uF, alAchievements, uF.getDateFormat(rs.getString("effective_date"), DBDATE, CF.getStrReportDateFormat()), hmGradeMap.get(rs.getString("grade_id")), hmDesigMap.get(rs.getString("desig_id")));
			}
			rs.close();
			pst.close();
			
			StringBuilder sb = new StringBuilder();
			int iA=0; 
			for(iA=0; alAchievements!=null && iA<alAchievements.size(); iA++){
				sb.append("<div style=\"float: left; width:100%;border-bottom: 1px solid #eee;\">");
				sb.append("<div style=\"float:left;margin-right:5px\"><img height=\"35px\" src=\"images1/trophy.png\"></div><div>"+alAchievements.get(iA)+" </div>");
				sb.append("</div>");
			}
			rs.close();
			pst.close();
			
			if(iA==0){
				sb.append("<div style=\"float:left;margin-right:5px\">No achievements made yet.</div>");
			}
			
			
			
			//request.setAttribute("alAchievements", alAchievements);
			request.setAttribute("DATA", sb.toString());
			
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
	
	public void getAchievements(Connection con, int nId, UtilityFunctions uF, List<String> alAchievements, String effectiveDate, String strNewGrade, String strNewDesignation){
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
		

			switch(nId){
			case 1:
				alAchievements.add("An <strong>increment</strong> is given to you since <strong>"+effectiveDate+"</strong>.");
				break;
			case 2:
				alAchievements.add("Wow! You got <strong>double increment</strong> since <strong>"+effectiveDate+"</strong>.");
				break;
			case 5:
				alAchievements.add("Your grade is revised to <strong>"+uF.showData(strNewGrade, "Na")+"</strong> since <strong>"+effectiveDate+"</strong>.");
				break;
			case 6:
				alAchievements.add("You are given a <strong>promotion</strong> since <strong>"+effectiveDate+"</strong>.");
				break;
			case 9:
				alAchievements.add("You are marked <strong>permananet</strong> since <strong>"+effectiveDate+"</strong>. Now you can avail benefits as per your level.");
				break;
			case 16:
				
				Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
				if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
				
				Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
				if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
				
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get((String)session.getAttribute(EMPID))) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get((String)session.getAttribute(EMPID)));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				}
				
				int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
				String strNewGrossSalary = null;
				if(nSalaryStrucuterType == S_GRADE_WISE){
					String strEmpGradeId = CF.getEmpGradeId(con, (String)session.getAttribute(EMPID));
					pst = con.prepareStatement("select sum(amount) as amount from emp_salary_details " +
							"where emp_id=? and effective_date = ? and earning_deduction = 'E' " +
							"and isdisplay = true and grade_id=?");
					pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setDate(2, uF.getDateFormat(effectiveDate, CF.getStrReportDateFormat()));
					pst.setInt(3, uF.parseToInt(strEmpGradeId));
					rs = pst.executeQuery();
					while(rs.next()){
						strNewGrossSalary = strCurrency +uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount")));
					}
					rs.close();
					pst.close();
				} else {
					String strEmpLevelId = CF.getEmpLevelId(con, (String)session.getAttribute(EMPID));
					pst = con.prepareStatement("select sum(amount) as amount from emp_salary_details " +
							"where emp_id=? and effective_date = ? and earning_deduction = 'E' " +
							"and isdisplay = true and level_id=?");
					pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
					pst.setDate(2, uF.getDateFormat(effectiveDate, CF.getStrReportDateFormat()));
					pst.setInt(3, uF.parseToInt(strEmpLevelId));
					rs = pst.executeQuery();
					while(rs.next()){
						strNewGrossSalary = strCurrency +uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount")));
					}
					rs.close();
					pst.close();
				}
				alAchievements.add("Your salary has been revised to "+strNewGrossSalary+" since "+effectiveDate+".");
				break;
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
	
	
	private void getUpcomingRequests(Connection con, UtilityFunctions uF) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<List<String>> leaveList = new ArrayList<List<String>>();
		List<List<String>> reimbursList = new ArrayList<List<String>>();
//		List<List<List<String>>> requestList = new ArrayList<List<List<String>>>();
		try {
			
			
			pst = con.prepareStatement("select leave_id,emp_id,leave_from,leave_to,entrydate,leave_type_id,is_approved " +
					"from emp_leave_entry where emp_id = ? and leave_from > ? order by leave_from limit 5");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				List<String> innLeaveList = new ArrayList<String>();
				
				innLeaveList.add(rs.getString("leave_id"));
				innLeaveList.add(uF.getDateFormat(rs.getString("leave_from"), DBDATE, CF.getStrReportDateFormat()));
				innLeaveList.add(uF.getDateFormat(rs.getString("leave_to"), DBDATE, CF.getStrReportDateFormat()));
//				innReqList.add(rs.getString("is_approved"));
				if(uF.parseToInt(rs.getString("is_approved"))==-1){
					 /*innLeaveList.add("<img width=\"16px\" src=\"images1/icons/denied.png\">");*/
					innLeaveList.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
				}else if(uF.parseToInt(rs.getString("is_approved"))==1){
					 /*innLeaveList.add("<img width=\"16px\" src=\"images1/icons/approved.png\">");*/
					innLeaveList.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
				} else{
					/*innLeaveList.add("<img width=\"16px\" src=\"images1/icons/pending.png\">");*/
					innLeaveList.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i>");
				}
//				innReqList.add(uF.getDateFormat(rs.getString("end_date"), DBDATE, CF.getStrReportDateFormat()));
				
				leaveList.add(innLeaveList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select reimbursement_id,from_date,to_date,reimbursement_type,reimbursement_amount," +
					"emp_id,approval_1,approval_2,ispaid,entry_date from emp_reimbursement where emp_id = ? and from_date > ? " + //and from_date > ?
					" order by from_date limit 5");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			while(rs.next()){
				List<String> innReimbursList = new ArrayList<String>();
				
				innReimbursList.add(rs.getString("reimbursement_id"));
				innReimbursList.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
				innReimbursList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				innReimbursList.add(rs.getString("reimbursement_amount"));
				if(uF.parseToInt(rs.getString("approval_1"))==-1 && uF.parseToInt(rs.getString("approval_2"))==-1){
					 /*innReimbursList.add("<img width=\"16px\" src=\"images1/icons/denied.png\">");*/
					innReimbursList.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
				}else if(uF.parseToInt(rs.getString("approval_1"))==1 && uF.parseToInt(rs.getString("approval_2"))==1){
					/*innReimbursList.add("<img width=\"16px\" src=\"images1/icons/approved.png\">");*/
					innReimbursList.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
				} else{
					/*innReimbursList.add("<img width=\"16px\" src=\"images1/icons/pending.png\">");*/
					innReimbursList.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i>");
				}
				reimbursList.add(innReimbursList);
			}
			rs.close();
			pst.close();
			
			
			StringBuilder sb = new StringBuilder();
			
			int iL=0;
			for(iL =0; leaveList != null && iL<leaveList.size(); iL++){
				List<String> innerList = leaveList.get(iL);
				sb.append("<div style=\"float:left;width:100%\"><div style=\"float:left;margin-right:5px\"><img width=\"20px\" src=\"images1/away.png\"></div>");
				sb.append("<div style=\"font-size: 11px;\">You have requested for a leave from "+innerList.get(1)+" to "+innerList.get(2)+". "+innerList.get(3)+"</div></div>");
			}

			
			int iR = 0;
			for(iR =0; reimbursList != null && iR<reimbursList.size(); iR++){
				List<String> innerList = reimbursList.get(iR);
				sb.append("<div style=\"float:left;width:100%\"><div style=\"float:left;margin-right:5px\"><img width=\"20px\" src=\"images1/summary.png\"></div>");
				sb.append("<div style=\"font-size: 11px;\">You have sumbmitted a reimbursement request for Rs. "+innerList.get(3)+". "+innerList.get(4)+"</div></div>");
        	}
			
			if(iL ==0 && iR==0){
				sb.append("<div class=\"tdDashLabel\">You have not submitted any request till date.</div>");
			
			}
			
			//request.setAttribute("reimbursList",reimbursList);
			//request.setAttribute("leaveList",leaveList);
			
			request.setAttribute("DATA", sb.toString());
			
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
	
	
	public void getDayThought(Connection con, UtilityFunctions uF, StringBuilder sb){
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
			Calendar cal = GregorianCalendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			
//			pst = con.prepareStatement(selectThought);			
//			pst.setInt(1, cal.get(Calendar.DAY_OF_YEAR));
//			rs = pst.executeQuery();
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_thought(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, cal.get(Calendar.DAY_OF_YEAR));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
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
//			cst.close();
			//request.setAttribute("DAY_THOUGHT_TEXT",strThought);
			//request.setAttribute("DAY_THOUGHT_BY",strThoughtBy);
			
			if(strThought!=null){
				sb.append("<p class=\"thought\">"+
		                  "<span> "+strThought+"</span>"+ 
		                  "<br/>"+
		                   "<span style=\"float:right;font-style:italic\">- <strong>"+strThoughtBy+"</strong></span>"+
		                "</p>");
			}
			
			
			  
			request.setAttribute("DATA", sb.toString());
			
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
	
	public void getResignationStatus(Connection con, UtilityFunctions uF, StringBuilder sb){
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst = null;
		try {
			
//			pst = con.prepareStatement("select * from emp_off_board where emp_id =? order by entry_date desc limit 1");			
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			rs = pst.executeQuery();
			
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_resignation_status(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("select * from emp_off_board where emp_id =? order by entry_date desc limit 1");			
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			String strResignationStatus = null;
			String strResignationStatusD = null;
			String strApprovedDate = null;
			int nResigId = 0;
			
			while (rs.next()) {
				nResigId = rs.getInt("off_board_id");
				
				if(rs.getInt("approved_1")==1 && rs.getInt("approved_2")==1){
					if(rs.getString("off_board_type") != null && rs.getString("off_board_type").trim().equalsIgnoreCase("TERMINATED")) {
						strResignationStatus = "Terminated";
						strResignationStatusD = "You are terminated from the service. Please <a href=\"ExitForm.action?id="+session.getAttribute(EMPID)+"&resignId="+nResigId+"\">click here</a> to complete your formalities.";
					}else if(rs.getString("off_board_type") != null && rs.getString("off_board_type").trim().equalsIgnoreCase("RESIGNED")) {
						strResignationStatus = "Your resignation has been accepted";
						strResignationStatusD = "Your resignation has been accepted. Please <a href=\"ExitForm.action?id="+session.getAttribute(EMPID)+"&resignId="+nResigId+"\">click here</a> to complete your formalities.";
					}
					request.setAttribute("RESIG_STATUS", "1");
				}else if(rs.getInt("approved_1")==-1 || rs.getInt("approved_2")==-1){
					strResignationStatus = "Your resignation has been denied";
					strResignationStatusD = "Your resignation has been denied";
				}else if(rs.getInt("approved_1")==1 && rs.getInt("approved_2")==0){
					strResignationStatus = "Your resignation has been approved by your manager and is waiting for HR's approval";
					strResignationStatusD = "Your resignation has been approved by your manager and is waiting for HR's approval";
				}else if(rs.getInt("approved_1")==0 && rs.getInt("approved_2")==1){
					strResignationStatus = "Your resignation has been approved by your HR and is waiting for manager's approval";
					strResignationStatusD = "Your resignation has been approved by your HR and is waiting for manager's approval";
				}else if(rs.getString("off_board_type")!=null && rs.getString("off_board_type").equalsIgnoreCase("TERMINATED")){
					strResignationStatus = "Terminated";
					strResignationStatusD = "Terminated";
				} else if((rs.getInt("approved_1")==0 || rs.getInt("approved_2")==0) && rs.getString("off_board_type")!=null && !rs.getString("off_board_type").equalsIgnoreCase("TERMINATED")) {
					strResignationStatus = "Resigned & waiting for approval";
					strResignationStatusD = "Resigned & waiting for approval";
				}
				
				if(rs.getString("approved_2_date")!=null){
					strApprovedDate = uF.getDateFormat(rs.getString("approved_2_date"), DBDATE, DBDATE);
				}
				
			}
			rs.close();
			pst.close();
//			cst.close();
			
			
			/*pst = con.prepareStatement("select * from employee_activity_details where emp_id = ? and emp_activity_id =(select max(emp_activity_id) from employee_activity_details  where emp_id = ?)");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			int nNotice = 0;
			while(rs.next()){
				nNotice = rs.getInt("notice_period");
			}
			rs.close();
			pst.close();*/
			
			int nNotice = CF.getEmpNoticePeriod(con,(String) session.getAttribute("EMPID"));
			String resigData = "";
			String lastDate = "";
			int nDifference = 0;
			int nRemaining = 0;
			if(strApprovedDate!=null){
				/*nDifference = uF.parseToInt(uF.dateDifference(strApprovedDate, DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE));
				nRemaining = nNotice - nDifference;*/
				String approvedDate = uF.getDateFormat(strApprovedDate, DBDATE,DATE_FORMAT);
				
				lastDate = uF.getDateFormat(""+uF.getBiweeklyDate(approvedDate, nNotice-1),DBDATE,CF.getStrReportDateFormat());
				String ldate = uF.getDateFormat(""+uF.getBiweeklyDate(approvedDate, nNotice-1),DBDATE,DATE_FORMAT);

				
				java.util.Date currDate = uF.getDateFormatUtil(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()),DBDATE, DATE_FORMAT),DATE_FORMAT );
				java.util.Date lstDate = uF.getDateFormatUtil(ldate,DATE_FORMAT );
				nDifference = uF.parseToInt(uF.dateDifference(strApprovedDate, DBDATE, ""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE));
				
				if(lstDate.after(currDate)) {
					nRemaining = nNotice - nDifference;
					resigData = nRemaining + " days remaining";
				} else if(lstDate.before(currDate)) {
					resigData = " last day  "+lastDate;
				} else if(lstDate.equals(currDate)) {
					resigData = " Today is last day  ";
				}
			}
			
			
			//request.setAttribute("RESIGNATION_STATUS",strResignationStatus);
			//request.setAttribute("RESIGNATION_REMAINING",resigData);
			
			if(strResignationStatus!=null){
				request.setAttribute("RESIGNATION_STATUS_D",strResignationStatusD);
			}
			
			
			
			if(strResignationStatus!=null){
				sb.append("<p class=\"notify\">"+
		                  "<span> "+strResignationStatusD+"</span>");
		                  	if(uF.parseToInt(strResignationStatus) ==1){
		                  		sb.append("<span style=\"float:left;color: red;text-align: left;width:100%\">"+uF.showData(nRemaining+"", "0") +" days remaining</span>");												
		                  	}
		                  	sb.append("</p>");
			}
			
			
			request.setAttribute("DATA", sb.toString());
			
			
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
	
	
	public void getMailCount(Connection con, UtilityFunctions uF, StringBuilder sb){
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst =null;
		try {
			
//			pst = con.prepareStatement(getUnreadMailCount);			
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			rs = pst.executeQuery();
			
//			con.setAutoCommit(false);
//			cst = con.prepareCall("{? = call sel_unread_mail_count(?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement(getUnreadMailCount);			
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			
			int nMailCount = 0;
			while (rs.next()) {
				nMailCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
//			cst.close();
			//request.setAttribute("MAIL_COUNT",nMailCount+"");
			
			
			if(nMailCount>0){
				sb.append("<p class=\"mail\">You have <a href=\"MyMail.action\" title=\"My Mail\"><strong>"+nMailCount+" new</strong></a> mails.</p>");
			}
			
			request.setAttribute("DATA", sb.toString());
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

	
	public void getBirthdays(Connection con, UtilityFunctions uF, StringBuilder sb, Map<String, String> hmEmpWlocationMap, Map<String, Map<String, String>> hmWlocationMap, Map<String, String> hmEmployeeMap, Map hmDepartmentMap, Map hmEmpDepartmentMap){
		
		CallableStatement cst  = null;
		ResultSet rs = null;
		PreparedStatement pst =null;
		try {
			
			String strToday = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DBDATE);
			String strToday1 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM-dd");
			String strTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 1)+"", DBDATE, "MM-dd");
			String strDayAfterTomorrow = uF.getDateFormat(uF.getFutureDate(CF.getStrTimeZone(), 2)+"", DBDATE, "MM-dd");

			pst = con.prepareStatement(selectBirthDay);			
			
			pst.setDate(1, uF.getFutureDate(CF.getStrTimeZone(), 365));
			rs = pst.executeQuery();

			List<String> alBirthDays = new ArrayList<String>();
			while (rs.next()) {
				String strBDate = uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM-dd");
				String strWlocationId = hmEmpWlocationMap.get(rs.getString("emp_per_id"));
				
				Map hmWlocation = hmWlocationMap.get(strWlocationId);
				if(hmWlocation==null)hmWlocation=new HashMap();
				
				String strCity = (String)hmWlocation.get("WL_CITY");
				String gender = rs.getString("emp_gender");
				if(strBDate!=null && strBDate.equals(strToday1)){
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
//			cst.close();
			request.setAttribute("alBirthDays",alBirthDays);
			
			
			
			for(int i=0; i<alBirthDays.size(); i++){
				sb.append("<div class=\"repeat_row\">"+
						(String)alBirthDays.get(i)+
						"</div>"); 
			} 
			
			request.setAttribute("DATA", sb.toString()); 
			
			
			
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

	
	public void getTaskDetails(Connection con, UtilityFunctions uF, StringBuilder sb, Map hmEmployeeMap){
		
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
			
			pst = con.prepareStatement("select activity_name,ai.pro_id,pro_name,ai.completed,ai.deadline,ai.start_date," +
					"ai.already_work  from projectmntnc pmc, activity_info ai where pmc.pro_id=ai.pro_id and ai.emp_id = ? " +
					"and ai.approve_status='n' and ai.already_work != 0 order by ai.deadline limit 4");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			List alTaskList = new ArrayList();
			List alTaskInner = new ArrayList();
			while(rs.next()){
				
				Date currentDate = uF.getCurrentDate(CF.getStrTimeZone());
				Date deadLineDate = uF.getDateFormat(rs.getString("deadline"), DBDATE);
				Date startDate = uF.getDateFormat(rs.getString("start_date"), DBDATE);
				
				alTaskInner = new ArrayList();
				
				alTaskInner.add(rs.getString("activity_name") +" ["+rs.getString("pro_name")+"]");
				
				if(uF.parseToInt(rs.getString("completed"))>=100){
					alTaskInner.add("Completed");
				}else{
					
					/*if(currentDate!=null && deadLineDate!=null && currentDate.after(deadLineDate)){
						alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/denied.png\" border=\"0\"><span style=\"color:red\">Overdue</span>");
					}else{
						alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/approved.png\" border=\"0\"><span style=\"color:green\">Working</span>");
					}*/
					
					if(currentDate!=null && deadLineDate!=null && currentDate.after(deadLineDate) && uF.parseToDouble(rs.getString("already_work"))>0){
						/*alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/denied.png\" border=\"0\"><span style=\"color:red\">Overdue</span>");*/
						alTaskInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i><span style=\"color:red\">Overdue</span>");
					}else if(currentDate!=null && startDate!=null && currentDate.before(startDate) && uF.parseToDouble(rs.getString("already_work"))==0){
						/*alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/pullout.png\" border=\"0\"><span style=\"color:orange\">Planned</span>");*/
						alTaskInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"></i><span style=\"color:orange\">Planned</span>");
					}else if(currentDate!=null && deadLineDate!=null && uF.parseToDouble(rs.getString("already_work"))>0){
						/*alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/approved.png\" border=\"0\"><span style=\"color:green\">Working</span>");*/
						alTaskInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i><span style=\"color:green\">Working</span>");
						
					}else{
						/*alTaskInner.add("<img style=\"padding-right:3px\" src=\"images1/icons/pullout.png\" border=\"0\"><span style=\"color:orange\">Planned</span>");*/
						alTaskInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\"></i><span style=\"color:orange\">Planned</span>");
						
						
					}
				}
				
				alTaskInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				alTaskInner.add(uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("already_work"))));
				
				
				
				alTaskInner.add(uF.showData((String)hmEmployeeMap.get((String)hmProjectTeamLead.get(rs.getString("pro_id"))), ""));
				
				alTaskList.add(alTaskInner);
				
			}
			rs.close();
			pst.close();
			//request.setAttribute("alTaskList",alTaskList);
//			System.out.println("alTaskList===>"+alTaskList);
			
			
			
			
			sb.append("<table width=\"100%\">"+
            
            "<tr>"+
                "<td class=\"tdDashLabelheading\">Project Name</td>"+
                "<td class=\"tdDashLabelheading\">Status</td>"+
                "<td class=\"tdDashLabelheading\">Deadline</td>"+
                "<td class=\"tdDashLabelheading\">Time Spent<br/>(hrs/days)</td>"+

            "</tr>");	
            
            
               int countTask=0;
               for(countTask=0; alTaskList!= null && !alTaskList.isEmpty() && countTask<alTaskList.size(); countTask++){ 
               List alInner = (List)alTaskList.get(countTask);
            
                    
               sb.append("<tr>"+
                "<td class=\"tdDashLabel\">"+uF.showData((String)alInner.get(0),"N/A") +"</td>"+
                "<td class=\"tdDashLabel\">"+uF.showData((String)alInner.get(1),"N/A") +"</td>"+
                "<td class=\"tdDashLabel\">"+uF.showData((String)alInner.get(2),"N/A") +"</td>"+
                "<td align=\"right\" style=\"padding-right:20px\" class=\"tdDashLabel\">"+uF.showData((String)alInner.get(3),"N/A") +"</td>"+
                
            "</tr>");		
                
            }
            if(countTask==0){
                
            	sb.append("<tr>"+
                "<td colspan=\"4\" class=\"tdDashLabel\">"+
                "Your have not been assigned any task. Please speak to your manager for allocation of tasks."+
                "</td>"+
            "</tr>");		
            }
         
            sb.append("</table>");
			
			
            request.setAttribute("DATA", sb.toString());
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

	private void getUpcomingTasks(Connection con, UtilityFunctions uF, StringBuilder sb) {

		PreparedStatement pst  = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select activity_name,ai.pro_id,pro_name,ai.completed,ai.deadline,ai.start_date," +
					"ai.already_work  from projectmntnc pmc, activity_info ai where pmc.pro_id=ai.pro_id and ai.emp_id = ? " +
					"and ai.approve_status='n' and ai.already_work = 0 and ai.deadline > ? order by ai.deadline limit 4");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			List upcomeTaskList = new ArrayList();
			List upcomeTaskInner = new ArrayList();

			while(rs.next()){
				Date currentDate = uF.getCurrentDate(CF.getStrTimeZone());
				Date deadLineDate = uF.getDateFormat(rs.getString("deadline"), DBDATE);
				//if(currentDate!=null && deadLineDate!=null && currentDate.before(deadLineDate) && uF.parseToDouble(rs.getString("already_work"))==0){
				upcomeTaskInner = new ArrayList();
				upcomeTaskInner.add(rs.getString("activity_name"));
				upcomeTaskInner.add(rs.getString("pro_name"));
				upcomeTaskInner.add(uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				
				upcomeTaskList.add(upcomeTaskInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("upcomeTaskList",upcomeTaskList);
			
			
			sb.append("<table width=\"100%\">"+
                    "<tr>"+
                        "<td class=\"tdDashLabelheading\" width=\"45%\">Task</td>"+
                        "<td class=\"tdDashLabelheading\" width=\"45%\">Project</td>"+
                        "<td class=\"tdDashLabelheading\" width=\"10%\">Deadline</td>"+
                    "</tr>");
                
           int countUpcomeTask=0;
           for(countUpcomeTask=0; upcomeTaskList != null && !upcomeTaskList.isEmpty() && countUpcomeTask<upcomeTaskList.size(); countUpcomeTask++){ 
               List upcomeInner = (List)upcomeTaskList.get(countUpcomeTask);
            
               sb.append("<tr>"+
                "<td class=\"tdDashLabel\">"+uF.showData((String)upcomeInner.get(0),"N/A") +"</td>"+
                "<td class=\"tdDashLabel\">"+uF.showData((String)upcomeInner.get(1),"N/A") +"</td>"+
                "<td class=\"tdDashLabel\">"+uF.showData((String)upcomeInner.get(2),"N/A") +"</td>"+
            "</tr>");		
                
            }
            if(countUpcomeTask==0){
                
            	 sb.append("<tr>"+
                "<td colspan=\"4\" class=\"tdDashLabel\">"+
                "No task scheduled for you."+
                "</td>"+
            	"</tr>");		
                
            }
            sb.append("</table>");
	
	
            request.setAttribute("DATA", sb.toString());
			
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
	
	
	private void getEmpRosterInfo(Connection con, UtilityFunctions uF, StringBuilder sb, List alServices, Map hmServices) {
		PreparedStatement pst =null;
		ResultSet rs = null;
		try {
			
//			con.setAutoCommit(false);
//			CallableStatement cst = con.prepareCall("{? = call sel_emp_roster_summary(?,?)}");
//			cst.registerOutParameter(1, Types.OTHER);
//			cst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//			cst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
//			cst.execute();
//			ResultSet rs = (ResultSet) cst.getObject(1);
			pst = con.prepareStatement("SELECT * FROM (SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id " +
					"and _date>=? and emp_id=? order by _date, _from)a LIMIT 3;");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt((String) session.getAttribute("EMPID")));
			rs = pst.executeQuery();
			Map hmRoster = new LinkedHashMap();
			Map hmRoster1 = new LinkedHashMap();
			Map hm1 = new HashMap();
			String strOldDate = null;
			String strNewDate;
			String strServiceId = null;
			alServices = new ArrayList();

			while (rs.next()) {
				hm1 = new HashMap();
				strNewDate = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat());
				strServiceId = rs.getString("service_id");

				if (strNewDate != null && !strNewDate.equalsIgnoreCase(strOldDate)) {
					hm1 = new HashMap();
				}
				hm1.put("FROM", uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
				hm1.put("TO", uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
				hm1.put("SERVICE", (String) hmServices.get(strServiceId));

				alServices = (List) hmRoster1.get(strNewDate);
				if (alServices == null) {
					alServices = new ArrayList();
				}
				if (!alServices.contains(strServiceId)) {
					alServices.add(strServiceId);
				}

				hmRoster1.put(strNewDate, alServices);

				hmRoster.put(strNewDate + "_" + strServiceId, hm1);
				strOldDate = strNewDate;
			}
			rs.close();
			pst.close();
//			cst.close();
			//request.setAttribute("hmRoster", hmRoster);
			//request.setAttribute("hmRoster1", hmRoster1);

			
			
			sb.append("<table width=\"100%\">"+
            "<tr>"+
                "<td class=\"tdDashLabelheading\">Date</td>"+
                "<td class=\"tdDashLabelheading\">From</td>"+
                "<td class=\"tdDashLabelheading\">To</td>"+
                "<td class=\"tdDashLabelheading\">Cost center</td>"+
            "</tr>");	
            
                Set set = hmRoster1.keySet();
                Iterator it = set.iterator();
                int i=0;
                while(it.hasNext()){
                    i++;
                    String strDate = (String)it.next();
            //		Map hm = (Map)hmRoster.get(strDate);
                    List alService = (List)hmRoster1.get(strDate);
                    for(int j=0; j<alService.size(); j++){
                        Map hm = (Map)hmRoster.get(strDate+"_"+(String)alService.get(j));
                    
                    
                    
                        sb.append("<tr>"+
                        		"<td class=\"tdDashLabel\">"+uF.showData(strDate,"N/A") +"</td>"+
                        		"<td class=\"tdDashLabel\">"+uF.showData((String)hm.get("FROM"),"N/A") +"</td>"+
                        		"<td class=\"tdDashLabel\">"+uF.showData((String)hm.get("TO"),"N/A") +"</td>"+
                        		"<td class=\"tdDashLabel\">"+uF.showData((String)hm.get("SERVICE"),"N/A") +"</td>"+
                        		"</tr>");		
                
                
                }
            }
            if(i==0){
                
            	sb.append(" <tr>"+
                "<td colspan=\"4\" class=\"tdDashLabel\">"+
                "Your next week roster is not updated, please send a request/ reminder to your manager"+
                "</td>"+
            "</tr>");	
                
            }
            sb.append("</table>");
			
            
            request.setAttribute("DATA", sb.toString());
            
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
	
	
	private void getCertificates(Connection con, UtilityFunctions uF, StringBuilder sb) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		
		List<List<String>> certificateList = new ArrayList<List<String>>();
		try {
			
			pst = con.prepareStatement("SELECT * FROM appraisal_attribute order by arribute_id");
			rs = pst.executeQuery();
			Map<String, String> hmAttribute = new HashMap<String, String>();
			while(rs.next()){
				hmAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select tp.plan_id,tp.training_title,tp.attribute_id,tp.certificate_id,tc.certificate_name,ts.end_date " +
					"from training_schedule ts, training_plan tp, training_status tss, training_certificate tc where ts.plan_id =" +
					" tp.plan_id and tp.certificate_id = tc.certificate_id and ts.emp_ids like '%,"+ (String) session.getAttribute("EMPID") +",%'" +
					" and tss.emp_id = "+ (String) session.getAttribute("EMPID") +" and tss.is_completed = 1 group by tss.emp_id," +
					"tp.plan_id,tp.training_title,tp.attribute_id,tp.certificate_id,tc.certificate_name,ts.end_date order by tp.plan_id");
			
			rs = pst.executeQuery();
			while(rs.next()){
				
				List<String> innCertiList = new ArrayList<String>();
				
				innCertiList.add(rs.getString("plan_id"));
				innCertiList.add(rs.getString("training_title"));
				innCertiList.add(hmAttribute.get(rs.getString("attribute_id")));
				innCertiList.add(rs.getString("certificate_name"));
				innCertiList.add(uF.getDateFormat(rs.getString("end_date"), DBDATE, CF.getStrReportDateFormat()));
				
				certificateList.add(innCertiList);
			}
			rs.close();
			pst.close();
//			System.out.println("certificateList =========== >> "+certificateList.toString());
			//request.setAttribute("certificateList",certificateList);
			
			
			int iC=0;
			for(iC =0; certificateList != null && iC<certificateList.size(); iC++){
				List<String> innerList = certificateList.get(iC);
	        	sb.append("<div style=\"float:left;width:100%\">" +
	        			"<strong>"+innerList.get(3) +"</strong> awarded on <strong>"+innerList.get(4) +"</strong> " +
	        			"for <strong>"+innerList.get(1) +"</strong> .</div>");
			}
			if(iC==0){
				sb.append("<div class=\"tdDashLabel\">You have not earned any certificate till date.</div>");
        	}
			
			request.setAttribute("DATA", sb.toString());
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
	
	private void getUpcomingTrainings(Connection con, UtilityFunctions uF, StringBuilder sb) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		List<List<String>> trainingsList = new ArrayList<List<String>>();
		try {
			
			pst = con.prepareStatement("SELECT * FROM appraisal_attribute order by arribute_id");
			rs = pst.executeQuery();
			Map<String, String> hmAttribute = new HashMap<String, String>();
			while(rs.next()){
				hmAttribute.put(rs.getString("arribute_id"), rs.getString("attribute_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select tp.plan_id,tp.training_title,tp.attribute_id,ts.start_date,ts.end_date," +
					"ts.emp_ids from training_schedule ts, training_plan tp where ts.plan_id = tp.plan_id and ts.emp_ids " +
					"like '%,"+ (String) session.getAttribute("EMPID") +",%'");
			rs = pst.executeQuery();
			while(rs.next()){
				if(rs.getString("start_date")!=null && !rs.getString("start_date").equals("")){
					boolean comparedate = uF.getCurrentDate(CF.getStrTimeZone()).before(uF.getDateFormatUtil(rs.getString("start_date"),DBDATE));
					if(comparedate == true){
						List<String> innTrainList = new ArrayList<String>();
						
						innTrainList.add(rs.getString("plan_id"));
						innTrainList.add(rs.getString("training_title"));
						innTrainList.add(hmAttribute.get(rs.getString("attribute_id")));
						innTrainList.add(uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
						
						trainingsList.add(innTrainList);
					}
				}
			}
			rs.close();
			pst.close();
			//request.setAttribute("trainingsList",trainingsList); 
			
			if(trainingsList != null && !trainingsList.isEmpty() && !trainingsList.isEmpty()){
				for(int i =0; trainingsList != null  && i<trainingsList.size(); i++){
					List<String> innerList = trainingsList.get(i);
				
	            	sb.append("<div style=\"float:left;width:100%\">Your <strong>"+innerList.get(1) +"</strong> " +
	            			"training for <strong>"+innerList.get(2) +"</strong> starts on "+innerList.get(3) +"</div>");
				}
			}else{
				sb.append("<div class=\"tdDashLabel\">No Learning scheduled for you.</div>");
            }
			
			request.setAttribute("DATA", sb.toString());
			
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

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	

}