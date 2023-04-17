package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TaskBarNotifications extends ActionSupport implements ServletRequestAware, IStatements {

 	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	String strUserType = null;
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession(true);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		String strUserTypeId = (String)session.getAttribute(USERTYPEID);
		String strType = (String)request.getParameter("type");
		
		getNotifications(uF, CF, strUserTypeId, strType);
		
		
		return SUCCESS;
		
	} 
	
	public void getNotifications(UtilityFunctions uF, CommonFunctions CF, String strUserTypeId, String strType){
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		try {
		
			con = db.makeConnection(con);
			
			switch(uF.parseToInt(strType)){
				case 1: // 1 = Email Unread count for all user Types
					getEmailCount(con, uF, CF, request, strType);
					break;
				case 2:// 2 = Exceptions count for Mangers and HR
					if(strUserType != null && strUserType.equals(HRMANAGER)){
						getExceptionsRequestCountForHr(con, uF, CF, request, strType);
					}else if(strUserType != null && strUserType.equals(MANAGER)){
						getExceptionsRequestApprovedForManager(con, uF, CF, request, strType);
					}
					break;
				case 3:// 3 = Leave Request + Travel Request + Claims + Perks + Overtime for Managers and HR
					getApprovalRequestCount(con, uF, CF, request, strType);
					break;
				case 4:// 4 = HR Requirement + Job Profile Request for HR 
					
					String []arrEnabledModules = CF.getArrEnabledModules();
					if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_CAREER_DEV_PLANNING+"")>=0){
						if(strUserType != null && strUserType.equals(EMPLOYEE)){
							getReviewHiringLearningForEmployee(con, uF, CF, request, strType);
						}  
					}
					if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, MODULE_RECRUITMENT_MANAGEMENT+"")>=0){
						if(strUserType != null && strUserType.equals(HRMANAGER)){
							getRequirementRequestCountForHR(con, uF, CF, request, strType);
						} else if(strUserType != null && strUserType.equals(MANAGER)){
							getRequirementRequestApprovedForManager(con, uF, CF, request, strType);
						}  
					}
					break;
				case 5:// 5 = New Pay Cheque generated for Employee
					if(strUserType != null && strUserType.equals(EMPLOYEE)){
						getPayChequeGeneratedForEmployee(con, uF, CF, request, strType);
					}
					break;
				case 6:// 6 = Leave Request Approved
					if(strUserType != null && strUserType.equals(EMPLOYEE)){
						getLeaveApprovedRequestCountForEmp(con, uF, CF, request, strType);
					}
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
	}

	private void getExceptionsRequestApprovedForManager(Connection con,UtilityFunctions uF, CommonFunctions CF,HttpServletRequest request, String strType) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			String date = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat());
			pst = con.prepareStatement("SELECT * FROM  (Select * from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad, " +
					" employee_personal_details epd WHERE epd.emp_per_id=ad.emp_id AND to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? AND ? " +
					" ORDER BY in_out_timestamp desc) a RIGHT JOIN roster_details rd ON a.emp_id=rd.emp_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t " +
					" WHERE t._date BETWEEN ? AND ? AND empl_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) " +
					" and early_late>0 and approved=0");
			pst.setDate(1, uF.getDateFormat(date, CF.getStrReportDateFormat()));
			pst.setDate(2, uF.getDateFormat(date, CF.getStrReportDateFormat()));
			pst.setDate(3, uF.getDateFormat(date, CF.getStrReportDateFormat()));
			pst.setDate(4, uF.getDateFormat(date, CF.getStrReportDateFormat()));
			pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			int nExceptionCount = 0;
			while (rs.next()) {
				nExceptionCount++;
			}
			rs.close();
			pst.close();
			
			if(nExceptionCount>0){
				StringBuilder sb=new StringBuilder();
				sb.append("<div style=\"float: left;\" class=\"h2class\">"+nExceptionCount+"</div><div class=\"answer\" style=\"display: none;\"><div id=\"triangle\"></div>" +
						"<table border=\"0\" width=\"100%\" style=\"border-collapse:collapse;\"><tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"UpdateClockEntries.action\" title=\"Exceptions\">Exceptions</a></td>" +
						"<td nowrap style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+nExceptionCount+"</td></tr></table></div>");
				
				request.setAttribute("NOTIFICATION_COUNT",sb.toString());
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


	private void getExceptionsRequestCountForHr(Connection con,UtilityFunctions uF, CommonFunctions CF,HttpServletRequest request, String strType) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			String date = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat());
			pst = con.prepareStatement("SELECT * FROM  (Select * from (SELECT *, ad.emp_id as empl_id FROM attendance_details ad, " +
					" employee_official_details eod WHERE eod.emp_id=ad.emp_id AND TO_DATE(in_out_timestamp::text, 'YYYY-MM-DD') BETWEEN ? AND ? " +
					" ORDER BY in_out_timestamp desc) a RIGHT JOIN roster_details rd ON a.empl_id=rd.emp_id and TO_DATE(a.in_out_timestamp::text, 'YYYY-MM-DD')=rd._date ) t " +
					" WHERE t._date BETWEEN ? AND ? AND wlocation_id = (select wlocation_id from employee_official_details where emp_id = ?) and early_late>0 and approved=0");
			pst.setDate(1, uF.getDateFormat(date, CF.getStrReportDateFormat()));
			pst.setDate(2, uF.getDateFormat(date, CF.getStrReportDateFormat()));
			pst.setDate(3, uF.getDateFormat(date, CF.getStrReportDateFormat()));
			pst.setDate(4, uF.getDateFormat(date, CF.getStrReportDateFormat()));
			pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			int nExceptionCount = 0;
			while (rs.next()) {
				nExceptionCount++;
			}
			rs.close();
			pst.close();
			
			if(nExceptionCount>0){
				StringBuilder sb=new StringBuilder();
				sb.append("<div style=\"float: left;\" class=\"h2class\">"+nExceptionCount+"</div><div class=\"answer\" style=\"display: none;\"><div id=\"triangle\"></div>" +
						"<table border=\"0\" width=\"100%\" style=\"border-collapse:collapse;\"><tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"UpdateClockEntries.action\" title=\"Exceptions\">Exceptions</a></td>" +
						"<td nowrap style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+nExceptionCount+"</td></tr></table></div>");
				
				request.setAttribute("NOTIFICATION_COUNT",sb.toString());
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




	private void getPayChequeGeneratedForEmployee(Connection con,UtilityFunctions uF, com.konnect.jpms.util.CommonFunctions CF,HttpServletRequest request, String strType) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("select mypay from user_alerts where emp_id=?");			
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			int nPayChequeCount = 0;
			while (rs.next()) {
				nPayChequeCount = rs.getInt("mypay");
			}
			rs.close();
			pst.close();
			
			if(nPayChequeCount>0){
				StringBuilder sb=new StringBuilder();
				sb.append("<div style=\"float: left;\" class=\"h2class\">"+nPayChequeCount+"</div><div class=\"answer\" style=\"display: none;\"><div id=\"triangle\"></div>" +
						"<table border=\"0\" width=\"100%\" style=\"border-collapse:collapse;\"><tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"MyPay.action?alertStatus=alert&alert_type="+MY_PAY_ALERT+"\" title=\"Pay Checks\">Pay Checks</a></td>" +
						"<td nowrap style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+nPayChequeCount+"</td></tr></table></div>");
				request.setAttribute("NOTIFICATION_COUNT",sb.toString());
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




	private void getApprovalRequestCount(Connection con, UtilityFunctions uF,CommonFunctions CF,HttpServletRequest request, String strType) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("select leave_request,reimbursement_request,travel_request from user_alerts where emp_id=?");			
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			int nLeaveCount = 0;
			int nReimbursementCount = 0;
			int nTravelCount = 0;
			while (rs.next()) {
				nLeaveCount = rs.getInt("leave_request");
				nReimbursementCount = rs.getInt("reimbursement_request");
				nTravelCount = rs.getInt("travel_request");
			}
			rs.close();
			pst.close();
						
//			int nPerkCount = 0;
//			if(strUserType != null && strUserType.equals(MANAGER)){
//				pst = con.prepareStatement("select count(*) as count from emp_perks where emp_id in (select emp_id " +
//						" from employee_official_details where supervisor_emp_id =?) and approval_1=0 ");			
//				pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
//				rs = pst.executeQuery();
//				
//				while (rs.next()) {
//					nPerkCount += rs.getInt("count"); 
//				}
//			}else if(strUserType != null && strUserType.equals(HRMANAGER)){
//				pst = con.prepareStatement("select count(*) as count from emp_perks where emp_id in (select emp_id from employee_official_details " +
//						"where wlocation_id = ( select wlocation_id from employee_official_details where emp_id=?)) and approval_2=0 ");			
//				pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					nPerkCount += rs.getInt("count");
//				}
//			}
//			int nApprovalRequestCount=nLeaveCount+nReimbursementCount+nPerkCount;
			int nApprovalRequestCount=nLeaveCount+nReimbursementCount+nTravelCount;
			if(nApprovalRequestCount>0){
				StringBuilder sb=new StringBuilder();
				sb.append("<div style=\"float: left;\" class=\"h2class\">"+nApprovalRequestCount+"</div>" +
						"<div class=\"answer\" style=\"display: none;\"><div id=\"triangle\"></div>");
					sb.append("<table border=\"0\" width=\"100%\" style=\"border-collapse:collapse;\">");
					sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"ManagerLeaveApprovalReport.action?alertStatus=alert&alert_type="+LEAVE_REQUEST_ALERT+"\" title=\"Requests\">Leave Request</a></td>" +
							"<td style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+nLeaveCount+"</td></tr>");
					sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"Reimbursements.action?alertStatus=alert&alert_type="+REIM_REQUEST_ALERT+"\" title=\"Claims\">Claims</a></td>" +
							"<td style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+nReimbursementCount+"</td></tr>");
//					sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"Perks.action\" title=\"Perks\">Perks</a></td>" +
//							"<td style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+nPerkCount+"</td></tr>");
					sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"TravelApprovalReport.action?alertStatus=alert&alert_type="+TRAVEL_REQUEST_ALERT+"\" title=\"Travel\">Travel</a></td>" +
							"<td style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+nTravelCount+"</td></tr>");
					sb.append("</table>");
					sb.append("</div>");
				
				request.setAttribute("NOTIFICATION_COUNT",sb.toString());
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




	private void getLeaveApprovedRequestCountForEmp(Connection con,UtilityFunctions uF, CommonFunctions CF,HttpServletRequest request, String strType) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
				pst = con.prepareStatement("select leave_approval from user_alerts where emp_id=?");			
				pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
				rs = pst.executeQuery();
				int nLeaveApproveCount = 0;
				while (rs.next()) {
					nLeaveApproveCount = rs.getInt("leave_approval");
				}
				rs.close();
				pst.close();
				
				if(nLeaveApproveCount>0){
					StringBuilder sb=new StringBuilder();
					sb.append("<div style=\"float: left;\" class=\"h2class\">"+nLeaveApproveCount+"</div><div class=\"answer\" style=\"display: none;\"><div id=\"triangle\"></div>" +
							"<table border=\"0\" width=\"100%\" style=\"border-collapse:collapse;\"><tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a  href=\"EmployeeLeaveEntryReport.action?alertStatus=alert&alert_type="+LEAVE_APPROVAl_ALERT+"\" title=\"My Leave Request\">Leave Approved</a></td>" +
							"<td nowrap style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+nLeaveApproveCount+"</td></tr></table></div>");
					request.setAttribute("NOTIFICATION_COUNT",sb.toString());
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




	public void getEmailCount(Connection con, UtilityFunctions uF, CommonFunctions CF, HttpServletRequest request, String strType){
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement(getUnreadMailCount);			
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			int nMailCount = 0;
			while (rs.next()) {
				nMailCount = rs.getInt("count");
			}
			rs.close();
			pst.close();
			
			if(nMailCount>0){
				StringBuilder sb=new StringBuilder();
				sb.append("<div style=\"float: left;\" class=\"h2class\">"+nMailCount+"</div><div class=\"answer\" style=\"display: none;\"><div id=\"triangle\"></div>" +
						"<table border=\"0\" width=\"100%\" style=\"border-collapse:collapse;\"><tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"MyMail.action?alertStatus=alert&alert_type="+UNREAD_MAIL_ALERT+"\" title=\"Mail\">Mail</a></td>" +
						"<td nowrap style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+nMailCount+"</td></tr></table></div>");
				
				request.setAttribute("NOTIFICATION_COUNT",sb.toString());
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
	
	public void getRequirementRequestCountForHR(Connection con, UtilityFunctions uF, CommonFunctions CF, HttpServletRequest request, String strType){
		PreparedStatement pst = null;
		ResultSet rs = null;		
		try {/*
			
			PreparedStatement pst = con.prepareStatement("select count(r.recruitment_id) as count from recruitment_details r join work_location_info w " +
					"on(r.wlocation=w.wlocation_id) join employee_personal_details e on (r.added_by=e.emp_per_id) left join designation_details d " +
					"using(designation_id) where recruitment_id>0 and requirement_status = 'generate' and r.status = 0 and r.wlocation = ?"); //and added_by = ?
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(WLOCATIONID)));
			ResultSet rs = pst.executeQuery();
			int requirementRequestCount = 0;
			while (rs.next()) {
				requirementRequestCount = rs.getInt("count");
			}
			
			pst = con.prepareStatement("select count(r.recruitment_id) as count from recruitment_details r join work_location_info w " +
					"on(r.wlocation=w.wlocation_id) join employee_personal_details e on (r.added_by=e.emp_per_id) left join designation_details d " +
					"using(designation_id) where recruitment_id>0 and requirement_status = 'generate' and r.status = 1 and " +
					"job_approval_status = 0 and job_profile_updated_by is null and r.wlocation = ?"); //and added_by = ?			
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(WLOCATIONID)));
			rs = pst.executeQuery();
			int requirementRequestApprovedCount = 0;
			while (rs.next()) {
				requirementRequestApprovedCount = rs.getInt("count");
			}

			int totReqApproveCount = requirementRequestApprovedCount + requirementRequestCount;
			if(totReqApproveCount>0){
				StringBuilder sb=new StringBuilder();
				sb.append("<div style=\"float: left;\" class=\"h2class\">"+totReqApproveCount+"</div>" +
				"<div class=\"answer\" style=\"display: none;\"><div id=\"triangle\"></div>");
				if(requirementRequestApprovedCount>0 || requirementRequestCount>0){
					sb.append("<table border=\"0\" width=\"100%\" style=\"border-collapse:collapse;\">");
				}
				if(requirementRequestCount>0){
					sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"RequirementApproval.action\" title=\"Requirements\">Requirements</a></td>" +
						"<td nowrap style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+requirementRequestCount+"</td></tr>");
				}
				if(requirementRequestApprovedCount>0){
					sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"JobProfilesApproval.action\" title=\"Job Approvals\">Job Approvals</a></td>" +
						"<td nowrap style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+requirementRequestApprovedCount+"</td></tr>");
				}
				
				if(requirementRequestApprovedCount>0 || requirementRequestCount>0){
					sb.append("</table>");
				}
				sb.append("</div>");
				
				
				request.setAttribute("NOTIFICATION_COUNT",sb.toString());
			}
		*/
			
			pst = con.prepareStatement("select requirement_request,jobcode_request,hr_reviews,reviews from user_alerts where emp_id=?");			
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			int requirementRequestCount = 0;
			int jobApprovalRequestCount = 0;
			int hrReviewsCount = 0;
			int newReviewCount = 0;
			while (rs.next()) {
				jobApprovalRequestCount = rs.getInt("jobcode_request");
				requirementRequestCount = rs.getInt("requirement_request");
				hrReviewsCount = rs.getInt("hr_reviews");
				newReviewCount = rs.getInt("reviews");
			}
			rs.close();
			pst.close();
//			int nApprovalRequestCount=nLeaveCount+nReimbursementCount+nPerkCount;
			int requirementCount=requirementRequestCount+jobApprovalRequestCount+hrReviewsCount+newReviewCount;
			if(requirementCount>0){
				StringBuilder sb=new StringBuilder();
				sb.append("<div style=\"float: left;\" class=\"h2class\">"+requirementCount+"</div>" +
						"<div class=\"answer\" style=\"display: none;\"><div id=\"triangle\"></div>");
					sb.append("<table border=\"0\" width=\"100%\" style=\"border-collapse:collapse;\">");
					if(requirementRequestCount>0) {
						sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"RequirementApproval.action?alertStatus=alert&alert_type="+REQUIREMENT_REQUEST_ALERT+"\" title=\"Requirements\">Requirements</a></td>" +
							"<td style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+requirementRequestCount+"</td></tr>");
					} 
					if(jobApprovalRequestCount>0) {
						sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"JobProfilesApproval.action?alertStatus=alert&alert_type="+JOBCODE_REQUEST_ALERT+"\" title=\"Job Approvals\">Job Approvals</a></td>" +
							"<td style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+jobApprovalRequestCount+"</td></tr>");
					}
					if(newReviewCount>0) {
						sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"Reviews.action?alertStatus=alert&alert_type="+NEW_REVIEW_ALERT+"\" title=\"Reviews\">Reviews</a></td>" +
							"<td style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+newReviewCount+"</td></tr>");
					}
					if(hrReviewsCount>0) {
						sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"Reviews.action?callFrom=Dash&alertStatus=alert&alert_type="+HR_REVIEW_ALERT+"\" title=\"Hr Reviews\">Hr Reviews</a></td>" +
							"<td style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+hrReviewsCount+"</td></tr>");
					}
					sb.append("</table>");
					sb.append("</div>");
				
				request.setAttribute("NOTIFICATION_COUNT",sb.toString());
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
	
	public void getRequirementRequestApprovedForManager(Connection con, UtilityFunctions uF, CommonFunctions CF, HttpServletRequest request, String strType){
		PreparedStatement pst = null;
		ResultSet rs = null;	
		try {/*
			PreparedStatement pst = con.prepareStatement("select count(r.recruitment_id) as count from recruitment_details r join work_location_info w " +
					"on(r.wlocation=w.wlocation_id) join employee_personal_details e on (r.added_by=e.emp_per_id) left join designation_details d " +
					"using(designation_id) where recruitment_id>0 and requirement_status = 'generate' and r.status = 0 and r.added_by = ? and r.wlocation = ?"); //and added_by = ?			
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(2, uF.parseToInt((String)session.getAttribute(WLOCATIONID)));
			ResultSet rs = pst.executeQuery();
			int requirementRequestCount = 0;
			while (rs.next()) {
				requirementRequestCount = rs.getInt("count");
			}
			pst.close();
			
			PreparedStatement pst1 = con.prepareStatement("select count(r.recruitment_id) as count from recruitment_details r join work_location_info w " +
					"on(r.wlocation=w.wlocation_id) join employee_personal_details e on (r.added_by=e.emp_per_id) left join designation_details d " +
					"using(designation_id) where recruitment_id>0 and requirement_status = 'generate' and r.status = 1 and " +
					"job_approval_status = 0 and job_profile_updated_by is null and r.added_by = ? and r.wlocation = ?");			
			pst1.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst1.setInt(2, uF.parseToInt((String)session.getAttribute(WLOCATIONID)));
			ResultSet rs1 = pst1.executeQuery();
			int requirementRequestApprovedCount = 0;
			while (rs1.next()) {
				requirementRequestApprovedCount = rs1.getInt("count");
			}
			pst1.close();
			int totReqApproveCount = requirementRequestApprovedCount + requirementRequestCount;
			if(totReqApproveCount>0){
				StringBuilder sb=new StringBuilder();
				sb.append("<div style=\"float: left;\" class=\"h2class\">"+totReqApproveCount+"</div>" +
				"<div class=\"answer\" style=\"display: none;\"><div id=\"triangle\"></div>");
				if(requirementRequestApprovedCount>0 || requirementRequestCount>0){
					sb.append("<table border=\"0\" width=\"100%\" style=\"border-collapse:collapse;\">");
				}
				if(requirementRequestCount>0){
					sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"RequirementApproval.action\" title=\"Requirements\">Requirements</a></td>" +
						"<td nowrap style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+requirementRequestCount+"</td></tr>");
				}
				if(requirementRequestApprovedCount>0){
					sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"JobProfilesApproval.action\" title=\"Job Approvals\">Job Approvals</a></td>" +
						"<td nowrap style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+requirementRequestApprovedCount+"</td></tr>");
				}
				
				if(requirementRequestApprovedCount>0 || requirementRequestCount>0){
					sb.append("</table>");
				}
				sb.append("</div>");
				
				
				request.setAttribute("NOTIFICATION_COUNT",sb.toString());
			}
		*/
			
			pst = con.prepareStatement("select requirement_approval,jobcode_approval,manager_reviews from user_alerts where emp_id=?");			
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			int jobcodeApprovalCount = 0;
			int requirementApprovalCount = 0;
			int reviewsCount = 0;
			while (rs.next()) {
				jobcodeApprovalCount = rs.getInt("jobcode_approval");
				requirementApprovalCount = rs.getInt("requirement_approval");
				reviewsCount = rs.getInt("manager_reviews");
			}
			rs.close();
			pst.close();
			
//			int nApprovalRequestCount=nLeaveCount+nReimbursementCount+nPerkCount;
			int requirementApprCount=requirementApprovalCount+jobcodeApprovalCount+reviewsCount;
			if(requirementApprCount>0){
				StringBuilder sb=new StringBuilder();
				sb.append("<div style=\"float: left;\" class=\"h2class\">"+requirementApprCount+"</div>" +
						"<div class=\"answer\" style=\"display: none;\"><div id=\"triangle\"></div>");
					sb.append("<table border=\"0\" width=\"100%\" style=\"border-collapse:collapse;\">");
					if(requirementApprovalCount>0) {
						sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"RequirementApproval.action?alertStatus=alert&alert_type="+REQUIREMENT_APPROVAL_ALERT+"\" title=\"Requirements\">Requirements</a></td>" +
							"<td style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+requirementApprovalCount+"</td></tr>");
					} 
					if(jobcodeApprovalCount>0) {
						sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"JobProfilesApproval.action?alertStatus=alert&alert_type="+JOBCODE_APPROVAL_ALERT+"\" title=\"Job Approvals\">Job Approvals</a></td>" +
							"<td style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+jobcodeApprovalCount+"</td></tr>");
					}
					if(reviewsCount>0) {
						sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"Reviews.action?callFrom=Dash&alertStatus=alert&alert_type="+MANAGER_REVIEW_ALERT+"\" title=\"Reviews\">Reviews</a></td>" +
							"<td style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+reviewsCount+"</td></tr>");
					}
					sb.append("</table>");
					sb.append("</div>");
				
				request.setAttribute("NOTIFICATION_COUNT",sb.toString());
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
	
	
	public void getReviewHiringLearningForEmployee(Connection con, UtilityFunctions uF, CommonFunctions CF, HttpServletRequest request, String strType){
		PreparedStatement pst = null;
		ResultSet rs = null;	
		try {
			pst = con.prepareStatement("select my_reviews,my_goals,my_kras,my_targets,my_learning_plans,interviews from user_alerts where emp_id=?");			
			pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
			rs = pst.executeQuery();
			int goalsCount = 0;
			int kRAsCount = 0;
			int targetsCount = 0;
			int reviewsCount = 0;
			int learningPlanCount = 0;
			int interviewsCount = 0;
			while (rs.next()) {
				reviewsCount = rs.getInt("my_reviews");
				goalsCount = rs.getInt("my_goals");
				kRAsCount = rs.getInt("my_kras");
				targetsCount = rs.getInt("my_targets");
				learningPlanCount = rs.getInt("my_learning_plans");
				interviewsCount = rs.getInt("interviews");
			}
			rs.close();
			pst.close();
			
//			int nApprovalRequestCount=nLeaveCount+nReimbursementCount+nPerkCount;
			int reviewGoalKraTargetCount=reviewsCount+goalsCount+kRAsCount+targetsCount+learningPlanCount;
			int goalKraTargetCount=goalsCount+kRAsCount+targetsCount;
			if(reviewGoalKraTargetCount>0){
				StringBuilder sb=new StringBuilder();
				sb.append("<div style=\"float: left;\" class=\"h2class\">"+reviewGoalKraTargetCount+"</div>" +
						"<div class=\"answer\" style=\"display: none;\"><div id=\"triangle\"></div>");
					sb.append("<table border=\"0\" width=\"100%\" style=\"border-collapse:collapse;\">");
					if(reviewsCount>0) {
						sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"Reviews.action?callFrom=Dash&alertStatus=alert&alert_type="+MY_REVIEW_ALERT+"\" title=\"Reviews\">Reviews</a></td>" +
							"<td style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+reviewsCount+"</td></tr>");
					} 
					if(goalKraTargetCount>0) {
						sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"MyHR.action?alertStatus=alert&alert_type="+GOAL_KRA_TARGET_ALERT+"\" title=\"Goals, KRAs, Targets\">Goals, KRAs, Targets</a></td>" +
							"<td style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+goalKraTargetCount+"</td></tr>");
					}
					if(interviewsCount>0) {
						sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"Calendar.action?alertStatus=alert&alert_type="+INTERVIEWS_ALERT+"\" title=\"Interviews\">Interviews</a></td>" +
							"<td style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+interviewsCount+"</td></tr>");
					}
					if(learningPlanCount>0) {
						sb.append("<tr><td nowrap style=\"border-bottom:1px solid #cccccc; padding-left: 4px;\"><a href=\"MyHR.action?callFrom=LPDash&alertStatus=alert&alert_type="+MY_LEARNING_PLAN_ALERT+"\" title=\"My Learnings\">My Learnings</a></td>" +
							"<td style=\"border-bottom:1px solid #cccccc;vertical-align:top;\" class=\"digital\">"+learningPlanCount+"</td></tr>");
					}
					sb.append("</table>");
					sb.append("</div>");
				
				request.setAttribute("NOTIFICATION_COUNT",sb.toString());
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

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
}
