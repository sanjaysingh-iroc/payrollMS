package com.konnect.jpms.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class UserAlerts implements Runnable, IConstants {
	Connection con;
	UtilityFunctions uF;
	CommonFunctions CF;
	String strEmpId;
	String strEmployeeId;
	HttpServletRequest request;
	String strDomain;
	String _type;
	String strData;
	String strAction;
	String alertID;
	String status;
	String strOther;
	String strType;
	String strCount;
	String currUserTypeID;
	String strDate;
	
	public UserAlerts(Connection con, UtilityFunctions uF, CommonFunctions CF, HttpServletRequest request) {
		this.con = con;
		this.uF = uF;
		this.CF = CF;
		this.request = request;
	}
 
	public void run() {
		if(getStrOther()!=null && getStrOther().equalsIgnoreCase("other")){
			if (status.equals(INSERT_TR_ALERT)) {
				insertOtherUserAlerts(con, CF, uF, strEmpId, strData, strAction);
			}else if (status.equals(DELETE_TR_ALERT)) {
				deleteTRUserAlerts(con, CF, uF, alertID);
			}
		} else if (status.equals(INSERT_WR_ALERT)) {
			insertWRUserAlerts(con, CF, uF, strEmpId, strData, strAction, currUserTypeID);
			
		} else if (status.equals(DELETE_WR_ALERT)) {			
			deleteWRUserAlerts(con, CF, uF, alertID);
			
//		} else if (status.equals(INSERT_ALERT)) {
//			insertUserAlerts(con, CF, uF, strEmpId, _type);
//			
//		} else if (status.equals(UPDATE_ALERT)) {
//			updateUserAlerts(con, CF, uF, strEmpId, _type);
			
//		} else if (status.equals(DELETE_ALERT)) {
//			deleteUserAlerts(con, CF, uF, strEmpId, _type);
			
		} else if (status.equals(INSERT_TR_ALERT)) {
			insertTRUserAlerts(con, CF, uF, strEmpId, strData, strAction);
			
		} else if (status.equals(DELETE_TR_ALERT)) {
			deleteTRUserAlerts(con, CF, uF, alertID);
		}
		
	}

	
//	private void deleteUserAlerts(Connection con, CommonFunctions CF, UtilityFunctions uF, String empId, String _type) {
//
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try {
//			pst = con.prepareStatement("select * from user_alerts where emp_id=?");
//			pst.setInt(1, uF.parseToInt(empId));
//			rs = pst.executeQuery();
//			Map<String, String> hmAlerts = new HashMap<String, String>();
//			while (rs.next()) {
//				hmAlerts.put(NEW_JOINEE_PENDING_ALERT, rs.getString("new_joinee_pending"));
//			}
//			rs.close();
//			pst.close();
//
//			int cnt = 0;
//			String columnName = "";
//			if (_type.equals(NEW_JOINEE_PENDING_ALERT)) {
//				if(uF.parseToInt(hmAlerts.get(NEW_JOINEE_PENDING_ALERT)) > 0) {
//					cnt = uF.parseToInt(hmAlerts.get(NEW_JOINEE_PENDING_ALERT)) - 1;
//				}
//				columnName = "new_joinee_pending";
//			}   
//			
//			if (columnName != null && !columnName.equals("")) {
//				pst = con.prepareStatement("update user_alerts set " + columnName + "=? where emp_id=?");
//				pst.setInt(1, cnt);
//				pst.setInt(2, uF.parseToInt(empId));
////				System.out.println("pst ===> " + pst);
//				pst.executeUpdate();
//				pst.close();
//			}
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

	
	private synchronized void deleteWRUserAlerts(Connection con, com.konnect.jpms.util.CommonFunctions CF, UtilityFunctions uF, String alertID) {
//		System.out.println("deleted successfully==>"+alertID);
		PreparedStatement pst = null;
		try {
			 pst = con.prepareStatement("delete from workrig_user_alerts where alerts_id=?");			
			 pst.setInt(1, uF.parseToInt(alertID));
//			 System.out.println("pst ===> " +pst);
			 pst.executeUpdate();
			 pst.close();
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	private synchronized void insertWRUserAlerts(Connection con, com.konnect.jpms.util.CommonFunctions CF, UtilityFunctions uF, String strEmpId, String strData,
			String strAction, String currUserTypeID) {

		PreparedStatement pst = null;
		try {
			pst = con.prepareStatement("insert into workrig_user_alerts (emp_id, alert_data, alert_action, emp_user_type, employee_id, _date, " +
				"entry_date_time) values(?,?,?,?, ?,?,?)");
		 	pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setString(2, strData);
			pst.setString(3, strAction);
			pst.setInt(4, uF.parseToInt(currUserTypeID));
			pst.setInt(5, uF.parseToInt(strEmployeeId));
			pst.setDate(6, uF.getDateFormat(strDate, DATE_FORMAT));
			pst.setTimestamp(7, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()) , DBDATE + DBTIME));
//			System.out.println("pst ===> " + pst);
			pst.executeUpdate();
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	private synchronized void deleteTRUserAlerts(Connection con, CommonFunctions CF, UtilityFunctions uF, String alertID) {
//		System.out.println("deleted successfully==>"+alertID);
		PreparedStatement pst = null;
		try {
			 pst = con.prepareStatement("delete from taskrig_user_alerts where alerts_id=?");			
			 pst.setInt(1, uF.parseToInt(alertID));
//			 System.out.println("pst ===> " +pst);
			 pst.executeUpdate();
			 pst.close();
			 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	

//	private void updateOtherUserAlerts(Connection con, CommonFunctions CF, UtilityFunctions uF, String strCustomerId, String _type) {
//		PreparedStatement pst = null;
//		try {
////			System.out.println("_type ====> " + _type);
//			String columnName = "";
//			if (_type.equals(PRO_CREATED_ALERT)) {
//				columnName = "pro_created";
//			} else if (_type.equals(PRO_COMPLETED_ALERT)) {
//				columnName = "pro_completed";
//			} else if (_type.equals(TASK_COMPLETED_ALERT)) {
//				columnName = "task_completed";
//			} else if (_type.equals(INVOICE_GENERATED_ALERT)) {
//				columnName = "invoice_generated";
//			} else if (_type.equals(SHARE_DOCUMENTS_ALERT)) {
//				columnName = "share_document";
//			}     
// 
//			if (columnName != null && !columnName.equals("")) {
//				pst = con.prepareStatement("update user_alerts set " + columnName + "=? where customer_id=?");
//				pst.setInt(1, 0);
//				pst.setInt(2, uF.parseToInt(strCustomerId));
////				System.out.println("pst ===> " + pst);
//				int x = pst.executeUpdate();
//				pst.close();
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if(pst !=null){
//				try {
//					pst.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
	

	private synchronized void insertOtherUserAlerts(Connection con, CommonFunctions CF, UtilityFunctions uF, String strCustomerId, String strData, String strAction) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("insert into taskrig_user_alerts (customer_id, alert_data, alert_action) values(?,?,?)");
			pst.setInt(1, uF.parseToInt(strCustomerId));
			pst.setString(2, strData);
			pst.setString(3, strAction);
//			System.out.println("pst ===> " + pst);
			pst.execute();
			pst.close();
			
//			pst = con.prepareStatement("select * from user_alerts where customer_id=?");
//			pst.setInt(1, uF.parseToInt(strCustomerId));
//			rs = pst.executeQuery();
//			Map<String, String> hmAlerts = new HashMap<String, String>();
//			while (rs.next()) {
//				hmAlerts.put(PRO_CREATED_ALERT, rs.getString("pro_created"));
//				hmAlerts.put(PRO_COMPLETED_ALERT, rs.getString("pro_completed"));
//				hmAlerts.put(TASK_COMPLETED_ALERT, rs.getString("task_completed"));
//				hmAlerts.put(INVOICE_GENERATED_ALERT, rs.getString("invoice_generated"));
//				hmAlerts.put(SHARE_DOCUMENTS_ALERT, rs.getString("share_document"));
//			}
//			rs.close();
//			pst.close();
//
//			int cnt = 0;
//			String columnName = "";
//			if (_type.equals(PRO_CREATED_ALERT)) {
//				cnt = uF.parseToInt(hmAlerts.get(PRO_CREATED_ALERT)) + 1;
//				columnName = "pro_created";
//			} else if (_type.equals(PRO_COMPLETED_ALERT)) {
//				cnt = uF.parseToInt(hmAlerts.get(PRO_COMPLETED_ALERT)) + 1;
//				columnName = "pro_completed";
//			} else if (_type.equals(TASK_COMPLETED_ALERT)) {
//				cnt = uF.parseToInt(hmAlerts.get(TASK_COMPLETED_ALERT)) + 1;
//				columnName = "task_completed";
//			} else if (_type.equals(INVOICE_GENERATED_ALERT)) {
//				cnt = uF.parseToInt(hmAlerts.get(INVOICE_GENERATED_ALERT)) + 1;
//				columnName = "invoice_generated";
//			} else if (_type.equals(SHARE_DOCUMENTS_ALERT)) {
//				cnt = uF.parseToInt(hmAlerts.get(SHARE_DOCUMENTS_ALERT)) + 1;
//				columnName = "share_document";
//			} 
//			
//			if (columnName != null && !columnName.equals("")) {
//				pst = con.prepareStatement("update user_alerts set " + columnName + "=? where customer_id=?");
//				pst.setInt(1, cnt);
//				pst.setInt(2, uF.parseToInt(strCustomerId));
////				System.out.println("pst ===> " + pst);
//				int x = pst.executeUpdate();
//				pst.close();
//				if (x == 0) {
//					pst = con.prepareStatement("insert into user_alerts (customer_id," + columnName + ") values(?,?)");
//					pst.setInt(1, uF.parseToInt(strCustomerId));
//					pst.setInt(2, cnt);
////					System.out.println("pst ===> " + pst);
//					pst.execute();
//					pst.close();
//				}
//			}
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

	private synchronized void updateUserAlerts(Connection con, CommonFunctions CF, UtilityFunctions uF, String empId, String _type) {
		PreparedStatement pst = null;
		try {
//			System.out.println("_type ====> " + _type);
			String columnName = "";
			if (_type.equals(MY_PAY_ALERT)) {
				columnName = "mypay";
			} else if (_type.equals(LEAVE_REQUEST_ALERT)) {
				columnName = "leave_request";
			} else if (_type.equals(LEAVE_APPROVAl_ALERT)) {
				columnName = "leave_approval";
			} else if (_type.equals(REIM_REQUEST_ALERT)) {
				columnName = "reimbursement_request";
			} else if (_type.equals(REIM_APPROVAL_ALERT)) {
				columnName = "reimbursement_approval";
			} else if (_type.equals(TRAVEL_REQUEST_ALERT)) {
				columnName = "travel_request";
			} else if (_type.equals(TRAVEL_APPROVAL_ALERT)) {
				columnName = "travel_approval";
			} else if (_type.equals(REQUIREMENT_REQUEST_ALERT)) {
				columnName = "requirement_request";
			} else if (_type.equals(REQUIREMENT_APPROVAL_ALERT)) {
				columnName = "requirement_approval";
			} else if (_type.equals(JOBCODE_REQUEST_ALERT)) {
				columnName = "jobcode_request";
			} else if (_type.equals(JOBCODE_APPROVAL_ALERT)) {
				columnName = "jobcode_approval";
			} else if (_type.equals(MY_REVIEW_ALERT)) {
				columnName = "my_reviews";
			} else if (_type.equals(HR_REVIEW_ALERT)) {
				columnName = "hr_reviews";
			} else if (_type.equals(MANAGER_REVIEW_ALERT)) {
				columnName = "manager_reviews";
			} else if (_type.equals(PEER_REVIEW_ALERT)) {
				columnName = "peer_reviews";
			} else if (_type.equals(MY_GOAL_ALERT)) {
				columnName = "my_goals";
			} else if (_type.equals(MY_KRA_ALERT)) {
				columnName = "my_kras";
			} else if (_type.equals(MY_TARGET_ALERT)) {
				columnName = "my_targets";
			} else if (_type.equals(MY_PERSONAL_GOAL_ALERT)) {
				columnName = "my_personal_goals";
			} else if (_type.equals(GOAL_KRA_TARGET_ALERT)) {
				columnName = "my_personal_goals=0,my_goals=0,my_kras=0,my_reviews=0, my_targets";
			} else if (_type.equals(MY_LEARNING_PLAN_ALERT)) {
				columnName = "my_learning_plans";
			} else if (_type.equals(NEW_REVIEW_ALERT)) {
				columnName = "reviews";
			}/* else if (_type.equals(INTERVIEWS_ALERT)) {
				columnName = "interviews";
			}*/ else if (_type.equals(NEW_JOINEES_ALERT)) {
				columnName = "new_joinees";
			} else if (_type.equals(HR_LEARNING_GAPS_ALERT)) {
				columnName = "hr_learning_gaps";
			} else if (_type.equals(MANAGER_LEARNING_GAPS_ALERT)) {
				columnName = "manager_learning_gaps";
			} else if (_type.equals(ADD_MY_INTERVIEWS_SCHEDULED_ALERT)) {
				columnName = "my_interviews_scheduled";
			} else if (_type.equals(REMOVE_MY_INTERVIEWS_SCHEDULED_ALERT)) {
				columnName = "my_interviews_scheduled";
			} else if (_type.equals(CANDIDATE_FINALIZATION_ALERT)) {
				columnName = "candidate_finalization";
			} else if (_type.equals(CANDIDATE_OFFER_ACCEPTREJECT_ALERT)) {
				columnName = "candidate_offer_accept_reject";
			} else if (_type.equals(HR_LEARNING_FINALIZATION_ALERT)) {
				columnName = "hr_learning_finalization"; 
			} else if (_type.equals(REVIEW_FINALIZATION_ALERT)) {
				columnName = "review_finalization"; 
			} else if (_type.equals(MANAGER_GOALS_ALERT)) {
				columnName = "manager_goals"; 
			} else if (_type.equals(PERK_REQUEST_ALERT)) {
				columnName = "perk_request";
			} else if (_type.equals(PERK_APPROVAL_ALERT)) {
				columnName = "perk_approval";
			} else if (_type.equals(PAY_PERK)) {
				columnName = "pay_perk";
			} else if (_type.equals(LTA_REQUEST_ALERT)) {
				columnName = "lta_request";
			} else if (_type.equals(LTA_APPROVAL_ALERT)) {
				columnName = "lta_approval";
			} else if (_type.equals(PAY_LTA)) {
				columnName = "pay_lta";
			} else if (_type.equals(LEAVE_ENCASH_REQUEST_ALERT)) {
				columnName = "leave_encash_request";
			} else if (_type.equals(LEAVE_ENCASH_APPROVAL_ALERT)) {
				columnName = "leave_encash_approval";
			} else if (_type.equals(PAY_GRATUITY)) {
				columnName = "pay_gratuity";
			} else if (_type.equals(PAY_REIM)) {
				columnName = "pay_reimbursement";
			} else if (_type.equals(REQUISITION_REQUEST_ALERT)) {
				columnName = "requisition_request";
			} else if (_type.equals(REQUISITION_APPROVAL_ALERT)) {
				columnName = "requisition_approval";
			} else if (_type.equals(PRO_RECURRING_BILLING_ALERT)) {
				columnName = "pro_recurring_billing";
			} else if (_type.equals(PRO_CREATED_ALERT)) {
				columnName = "pro_created";
			} else if (_type.equals(PRO_COMPLETED_ALERT)) {
				columnName = "pro_completed";
			} else if (_type.equals(PRO_NEW_RESOURCE_ALERT)) {
				columnName = "pro_new_resource";
			} else if (_type.equals(TASK_ALLOCATE_ALERT)) {
				columnName = "task_allocate";
			} else if (_type.equals(TASK_NEW_REQUEST_ALERT)) {
				columnName = "task_new_request";
			} else if (_type.equals(TASK_ACCEPT_ALERT)) {
				columnName = "task_accept";
			} else if (_type.equals(TASK_REQUEST_RESCHEDULE_ALERT)) {
				columnName = "task_request_reschedule";
			} else if (_type.equals(TASK_REQUEST_REASSIGN_ALERT)) {
				columnName = "task_request_reassign";
			} else if (_type.equals(TASK_REASSIGN_ALERT)) {
				columnName = "task_reassign";
			} else if (_type.equals(TASK_RESCHEDULE_ALERT)) {
				columnName = "task_reschedule";
			} else if (_type.equals(TASK_COMPLETED_ALERT)) {
				columnName = "task_completed";
			} else if (_type.equals(TIMESHEET_RECEIVED_ALERT)) {
				columnName = "timesheet_received";
			} else if (_type.equals(INVOICE_GENERATED_ALERT)) {
				columnName = "invoice_generated";
			} else if (_type.equals(SHARE_DOCUMENTS_ALERT)) {
				columnName = "share_document";
			} else if (_type.equals(ADD_MYTEAM_MEMBER_ALERT)) {
				columnName = "add_myteam_member";
			} else if (_type.equals(NEW_JOINEE_PENDING_ALERT)) {
				columnName = "new_joinee_pending";
			} else if (_type.equals(NEWS_AND_ALERTS)) {
				columnName = "news_and_alerts";
			} else if (_type.equals(LOAN_REQUEST_ALERT)) {
				columnName = "loan_request";
			} else if (_type.equals(LOAN_APPROVAL_ALERT)) {
				columnName = "loan_approval";
			} else if (_type.equals(SELF_REVIEW_REQUEST_ALERT)) {
				columnName = "self_review_request";
			} else if (_type.equals(SELF_REVIEW_APPROVAL_ALERT)) {
				columnName = "self_review_approval";
			} else if (_type.equals(EMP_CONFIRMATIONS_ALERT)) {
				columnName = "emp_confirmations";
			} else if (_type.equals(EMP_RESIGNATIONS_ALERT)) {
				columnName = "emp_resignations";
			} else if (_type.equals(EMP_FINAL_DAY_ALERT)) {
				columnName = "emp_final_day";
			} else if (_type.equals(TRAVEL_BOOKING_ALERT)) {
				columnName = "travelbooking";
			} else if (_type.equals(LIBRARY_REQUEST_ALERT)) {
				columnName = "library_request";
			} else if (_type.equals(LIBRARY_REQUEST_APPROVED_ALERT)) {
				columnName = "library_req_approved";
			} else if (_type.equals(NEW_MANUAL_ALERT)) {
				columnName = "new_manual";
			} else if (_type.equals(MEETING_ROOM_BOOKING_REQUEST_ALERT)) {
				columnName = "meeting_room_booking_request";
			} else if (_type.equals(MEETING_ROOM_BOOKING_REQUEST_APPROVED_ALERT)) {
				columnName = "meeting_room_booking_req_approved";
			} else if (_type.equals(FOOD_REQUEST_ALERT)) {
				columnName = "food_Request";
			} else if(_type.equals(NEW_CANDIDATE_FILL_ALERT)) {
				columnName = "new_canditate_fill"; 
			} else if(_type.equals(EMP_TERMINATED_ALERT)) {
				columnName = "emp_terminations"; 
			} else if(_type.equals(FORM16_RELEASE_ALERT)) {
				columnName = "form16_release"; 
			} else if(_type.equals(HOD_REVIEW_ALERT)) {
				columnName = "hod_reviews"; 
			} else if(_type.equals(CEO_REVIEW_ALERT)) {
				columnName = "ceo_reviews"; 
			}

			if (columnName != null && !columnName.equals("")) {
				pst = con.prepareStatement("update user_alerts set " + columnName + "=? where emp_id=?");
				pst.setInt(1, 0);
				pst.setInt(2, uF.parseToInt(empId));
//				System.out.println("pst ===> " + pst);
				int x = pst.executeUpdate();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	public synchronized void insertTRUserAlerts(Connection con, CommonFunctions CF, UtilityFunctions uF, String empId, String strData, String strAction) {

		PreparedStatement pst = null;
		try {
//			pst = con.prepareStatement("select activity_alerts from user_alerts where emp_id=?");
//			pst.setInt(1, uF.parseToInt(empId));
//			rs = pst.executeQuery();
//			StringBuilder empAlertData = new StringBuilder();
//			while (rs.next()) {
//				empAlertData.append(rs.getString("activity_alerts"));
//			}
//			rs.close();
//			pst.close();

//			if(empAlertData == null || empAlertData.toString().equals("null")) empAlertData = new StringBuilder();
//			empAlertData.append(strData+"::::");
			
//				pst = con.prepareStatement("update user_alerts set activity_alerts = ? where emp_id = ?");
//				pst.setString(1, empAlertData.toString());
//				pst.setInt(2, uF.parseToInt(empId));
////				System.out.println("pst ===> " + pst);
//				int x = pst.executeUpdate();
//				pst.close();
//				if (x == 0) {
				String alertType ="";
				if(_type!=null && _type.equals(NEWS_AND_ALERTS)) {
					alertType = "NA";
				}
				
				pst = con.prepareStatement("insert into taskrig_user_alerts (resource_id, alert_data, alert_action,type) values(?,?,?,?)");
			 	pst.setInt(1, uF.parseToInt(empId));
				pst.setString(2, strData);
				pst.setString(3, strAction);
				pst.setString(4, alertType);
//				System.out.println("pst ===> " + pst);
				pst.executeUpdate();
				if(pst != null) {
					try {
						pst.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	private synchronized void insertUserAlerts(Connection con, CommonFunctions CF, UtilityFunctions uF, String empId, String _type) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select * from user_alerts where emp_id=?");
			pst.setInt(1, uF.parseToInt(empId));
			rs = pst.executeQuery();
			Map<String, String> hmAlerts = new HashMap<String, String>();
			while (rs.next()) {
				hmAlerts.put("MY_PAY", rs.getString("mypay"));
				hmAlerts.put("LEAVE_REQUEST", rs.getString("leave_request"));
				hmAlerts.put("LEAVE_APPROVAL", rs.getString("leave_approval"));
				hmAlerts.put("REIMBURSEMENT_REQUEST", rs.getString("reimbursement_request"));
				hmAlerts.put("REIMBURSEMENT_APPROVAL", rs.getString("reimbursement_approval"));
				hmAlerts.put("TRAVEL_REQUEST", rs.getString("travel_request"));
				hmAlerts.put("TRAVEL_APPROVAL", rs.getString("travel_approval"));
				hmAlerts.put("REQUIREMENT_REQUEST", rs.getString("requirement_request"));
				hmAlerts.put("REQUIREMENT_APPROVAL", rs.getString("requirement_approval"));
				hmAlerts.put("JOBCODE_REQUEST", rs.getString("jobcode_request"));
				hmAlerts.put("JOBCODE_APPROVAL", rs.getString("jobcode_approval"));
				hmAlerts.put("MY_REVIEWS", rs.getString("my_reviews"));
				hmAlerts.put("HR_REVIEWS", rs.getString("hr_reviews"));
				hmAlerts.put("MANAGER_REVIEWS", rs.getString("manager_reviews"));
				hmAlerts.put("PEER_REVIEWS", rs.getString("peer_reviews"));
				hmAlerts.put("MY_GOALS", rs.getString("my_goals"));
				hmAlerts.put("MY_KRAS", rs.getString("my_kras"));
				hmAlerts.put("MY_TARGETS", rs.getString("my_targets"));
				hmAlerts.put("MY_PERSONAL_GOALS", rs.getString("my_personal_goals"));
				hmAlerts.put("MY_LEARNING_PLANS", rs.getString("my_learning_plans"));
				hmAlerts.put("NEW_REVIEWS", rs.getString("reviews"));
//				hmAlerts.put("INTERVIEWS", rs.getString("interviews"));
				hmAlerts.put("NEWJOINEES", rs.getString("new_joinees"));
				hmAlerts.put("HR_LEARNING_GAPS", rs.getString("hr_learning_gaps"));
				hmAlerts.put("MANAGER_LEARNING_GAPS", rs.getString("manager_learning_gaps"));
				hmAlerts.put("MY_INTERVIEWS_SCHEDULED", rs.getString("my_interviews_scheduled"));
				hmAlerts.put("CANDIDATE_FINALIZATION", rs.getString("candidate_finalization"));
				hmAlerts.put("CANDIDATE_OFFER_ACCEPTREJECT", rs.getString("candidate_offer_accept_reject"));
				hmAlerts.put("HR_LEARNING_FINALIZATION", rs.getString("hr_learning_finalization"));
				hmAlerts.put("REVIEW_FINALIZATION", rs.getString("review_finalization"));
				hmAlerts.put("MANAGER_GOALS", rs.getString("manager_goals"));
				hmAlerts.put("PERK_REQUEST", rs.getString("perk_request"));
				hmAlerts.put("PERK_APPROVAL", rs.getString("perk_approval"));
				hmAlerts.put("PAY_PERK", rs.getString("pay_perk"));

				hmAlerts.put("LTA_REQUEST", rs.getString("lta_request"));
				hmAlerts.put("LTA_APPROVAL", rs.getString("lta_approval"));
				hmAlerts.put("PAY_LTA", rs.getString("pay_lta"));
				
				hmAlerts.put(LEAVE_ENCASH_REQUEST_ALERT, rs.getString("leave_encash_request"));
				hmAlerts.put(LEAVE_ENCASH_APPROVAL_ALERT, rs.getString("leave_encash_approval"));
				
				hmAlerts.put(PAY_GRATUITY, rs.getString("pay_gratuity"));
				
				hmAlerts.put(PAY_REIM, rs.getString("pay_reimbursement")); 
				
				hmAlerts.put(REQUISITION_REQUEST_ALERT, rs.getString("requisition_request"));
				hmAlerts.put(REQUISITION_APPROVAL_ALERT, rs.getString("requisition_approval"));
				
				hmAlerts.put(PRO_RECURRING_BILLING_ALERT, rs.getString("pro_recurring_billing"));
				
				hmAlerts.put(PRO_CREATED_ALERT, rs.getString("pro_created"));
				hmAlerts.put(PRO_COMPLETED_ALERT, rs.getString("pro_completed"));
				hmAlerts.put(PRO_NEW_RESOURCE_ALERT, rs.getString("pro_new_resource"));
				hmAlerts.put(TASK_ALLOCATE_ALERT, rs.getString("task_allocate"));
				hmAlerts.put(TASK_NEW_REQUEST_ALERT, rs.getString("task_new_request"));
				hmAlerts.put(TASK_ACCEPT_ALERT, rs.getString("task_accept"));
				hmAlerts.put(TASK_REQUEST_RESCHEDULE_ALERT, rs.getString("task_request_reschedule"));
				hmAlerts.put(TASK_REQUEST_REASSIGN_ALERT, rs.getString("task_request_reassign"));
				hmAlerts.put(TASK_REASSIGN_ALERT, rs.getString("task_reassign"));
				hmAlerts.put(TASK_RESCHEDULE_ALERT, rs.getString("task_reschedule"));
				hmAlerts.put(TASK_COMPLETED_ALERT, rs.getString("task_completed"));
				hmAlerts.put(TIMESHEET_RECEIVED_ALERT, rs.getString("timesheet_received"));
				hmAlerts.put(INVOICE_GENERATED_ALERT, rs.getString("invoice_generated"));
				hmAlerts.put(SHARE_DOCUMENTS_ALERT, rs.getString("share_document"));
				hmAlerts.put(ADD_MYTEAM_MEMBER_ALERT, rs.getString("add_myteam_member"));
				hmAlerts.put(NEW_JOINEE_PENDING_ALERT, rs.getString("new_joinee_pending"));
				hmAlerts.put(NEWS_AND_ALERTS, rs.getString("news_and_alerts"));
				
				hmAlerts.put(LOAN_REQUEST_ALERT, rs.getString("loan_request"));
				hmAlerts.put(LOAN_APPROVAL_ALERT, rs.getString("loan_approval"));
				hmAlerts.put(SELF_REVIEW_REQUEST_ALERT, rs.getString("self_review_request"));
				hmAlerts.put(SELF_REVIEW_APPROVAL_ALERT, rs.getString("self_review_approval"));
				hmAlerts.put(EMP_CONFIRMATIONS_ALERT, rs.getString("emp_confirmations"));
				hmAlerts.put(EMP_RESIGNATIONS_ALERT, rs.getString("emp_resignations"));
				hmAlerts.put(EMP_FINAL_DAY_ALERT, rs.getString("emp_final_day"));
				hmAlerts.put(TRAVEL_BOOKING_ALERT, rs.getString("travelbooking"));
				hmAlerts.put(LIBRARY_REQUEST_ALERT, rs.getString("library_request"));
				hmAlerts.put(LIBRARY_REQUEST_APPROVED_ALERT, rs.getString("library_req_approved"));
				hmAlerts.put(NEW_MANUAL_ALERT, rs.getString("new_manual"));
				hmAlerts.put(MEETING_ROOM_BOOKING_REQUEST_ALERT, rs.getString("meeting_room_booking_request"));
				hmAlerts.put(MEETING_ROOM_BOOKING_REQUEST_APPROVED_ALERT, rs.getString("meeting_room_booking_req_approved"));
				hmAlerts.put(FOOD_REQUEST_ALERT, rs.getString("food_request"));
				hmAlerts.put(NEW_CANDIDATE_FILL_ALERT, rs.getString("new_canditate_fill"));
				hmAlerts.put(EMP_TERMINATED_ALERT, rs.getString("emp_terminations"));
				hmAlerts.put(FORM16_RELEASE_ALERT, rs.getString("form16_release"));
				hmAlerts.put(CEO_REVIEW_ALERT, rs.getString("ceo_reviews"));
				hmAlerts.put(HOD_REVIEW_ALERT, rs.getString("hod_reviews"));
			}
			rs.close();
			pst.close();

			int cnt = 0;
			String columnName = "";
			if (_type.equals(MY_PAY_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("MY_PAY")) + 1;
				columnName = "mypay";
			} else if (_type.equals(LEAVE_REQUEST_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("LEAVE_REQUEST")) + 1;
				columnName = "leave_request";
			} else if (_type.equals(LEAVE_APPROVAl_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("LEAVE_APPROVAL")) + 1;
				columnName = "leave_approval";
			} else if (_type.equals(REIM_REQUEST_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("REIMBURSEMENT_REQUEST")) + 1;
				columnName = "reimbursement_request";
			} else if (_type.equals(REIM_APPROVAL_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("REIMBURSEMENT_APPROVAL")) + 1;
				columnName = "reimbursement_approval";
			} else if (_type.equals(TRAVEL_REQUEST_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("TRAVEL_REQUEST")) + 1;
				columnName = "travel_request";
			} else if (_type.equals(TRAVEL_APPROVAL_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("TRAVEL_APPROVAL")) + 1;
				columnName = "travel_approval";
			} else if (_type.equals(REQUIREMENT_REQUEST_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("REQUIREMENT_REQUEST")) + 1;
				columnName = "requirement_request";
			} else if (_type.equals(REQUIREMENT_APPROVAL_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("REQUIREMENT_APPROVAL")) + 1;
				columnName = "requirement_approval";
			} else if (_type.equals(JOBCODE_REQUEST_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("JOBCODE_REQUEST")) + 1;  
				columnName = "jobcode_request";
			} else if (_type.equals(JOBCODE_APPROVAL_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("JOBCODE_APPROVAL")) + 1;
				columnName = "jobcode_approval";
			} else if (_type.equals(MY_REVIEW_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("MY_REVIEWS")) + 1;
				columnName = "my_reviews";
			} else if (_type.equals(HR_REVIEW_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("HR_REVIEWS")) + 1;
				columnName = "hr_reviews";
			} else if (_type.equals(MANAGER_REVIEW_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("MANAGER_REVIEWS")) + 1;
				columnName = "manager_reviews";
			} else if (_type.equals(PEER_REVIEW_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("PEER_REVIEWS")) + 1;
				columnName = "peer_reviews";
			} else if (_type.equals(MY_GOAL_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("MY_GOALS")) + 1;
				columnName = "my_goals";
			} else if (_type.equals(MY_KRA_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("MY_KRAS")) + 1;
				columnName = "my_kras";
			} else if (_type.equals(MY_TARGET_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("MY_TARGETS")) + 1;
				columnName = "my_targets";
			} else if (_type.equals(MY_PERSONAL_GOAL_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("MY_PERSONAL_GOALS")) + 1;
				columnName = "my_personal_goals";
			} else if (_type.equals(MY_LEARNING_PLAN_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("MY_LEARNING_PLANS")) + 1;
				columnName = "my_learning_plans";
			} else if (_type.equals(NEW_REVIEW_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("NEW_REVIEWS")) + 1;
				columnName = "reviews";
			}/* else if (_type.equals(INTERVIEWS_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("INTERVIEWS")) + 1;
				columnName = "interviews";
			}*/ else if (_type.equals(NEW_JOINEES_ALERT)) {
				cnt = uF.parseToInt(strCount);
				//cnt = uF.parseToInt(hmAlerts.get("NEWJOINEES")) + 1;
				columnName = "new_joinees";
			} else if (_type.equals(HR_LEARNING_GAPS_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("HR_LEARNING_GAPS")) + 1;
				columnName = "hr_learning_gaps";
			} else if (_type.equals(MANAGER_LEARNING_GAPS_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("MANAGER_LEARNING_GAPS")) + 1;
				columnName = "manager_learning_gaps";
			} else if (_type.equals(ADD_MY_INTERVIEWS_SCHEDULED_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("MY_INTERVIEWS_SCHEDULED")) + 1;
				columnName = "my_interviews_scheduled";
			} else if (_type.equals(REMOVE_MY_INTERVIEWS_SCHEDULED_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("MY_INTERVIEWS_SCHEDULED")) - 1;
				columnName = "my_interviews_scheduled";
			} else if (_type.equals(CANDIDATE_FINALIZATION_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("CANDIDATE_FINALIZATION")) + 1;
				columnName = "candidate_finalization";
			} else if (_type.equals(CANDIDATE_OFFER_ACCEPTREJECT_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("CANDIDATE_OFFER_ACCEPTREJECT")) + 1;
				columnName = "candidate_offer_accept_reject"; 
			} else if (_type.equals(HR_LEARNING_FINALIZATION_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("HR_LEARNING_FINALIZATION")) + 1;
				columnName = "hr_learning_finalization"; 
			} else if (_type.equals(REVIEW_FINALIZATION_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("REVIEW_FINALIZATION")) + 1;
				columnName = "review_finalization"; 
			} else if (_type.equals(MANAGER_GOALS_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("MANAGER_GOALS")) + 1;
				columnName = "manager_goals"; 
			} else if (_type.equals(PERK_REQUEST_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("PERK_REQUEST")) + 1;
				columnName = "perk_request";
			} else if (_type.equals(PERK_APPROVAL_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("PERK_APPROVAL")) + 1;
				columnName = "perk_approval";
			} else if (_type.equals(PAY_PERK)) {
				cnt = uF.parseToInt(hmAlerts.get("PAY_PERK")) + 1;
				columnName = "pay_perk";
			} else if (_type.equals(LTA_REQUEST_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("LTA_REQUEST")) + 1;
				columnName = "lta_request";
			} else if (_type.equals(LTA_APPROVAL_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get("LTA_APPROVAL")) + 1;
				columnName = "lta_approval";
			} else if (_type.equals(PAY_LTA)) {
				cnt = uF.parseToInt(hmAlerts.get("PAY_LTA")) + 1;
				columnName = "pay_lta";
			} else if (_type.equals(LEAVE_ENCASH_REQUEST_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(LEAVE_ENCASH_REQUEST_ALERT)) + 1;
				columnName = "leave_encash_request";
			} else if (_type.equals(LEAVE_ENCASH_APPROVAL_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(LEAVE_ENCASH_REQUEST_ALERT)) + 1;
				columnName = "leave_encash_approval";
			} else if (_type.equals(PAY_GRATUITY)) {
				cnt = uF.parseToInt(hmAlerts.get(PAY_GRATUITY)) + 1;
				columnName = "pay_gratuity";
			} else if (_type.equals(PAY_REIM)) {
				cnt = uF.parseToInt(hmAlerts.get(PAY_REIM)) + 1;
				columnName = "pay_reimbursement";
			} else if (_type.equals(REQUISITION_REQUEST_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(REQUISITION_REQUEST_ALERT)) + 1;
				columnName = "requisition_request";
			} else if (_type.equals(REQUISITION_APPROVAL_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(REQUISITION_APPROVAL_ALERT)) + 1;
				columnName = "requisition_approval";
			} else if (_type.equals(PRO_RECURRING_BILLING_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(PRO_RECURRING_BILLING_ALERT)) + 1;
				columnName = "pro_recurring_billing";
			} else if (_type.equals(PRO_CREATED_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(PRO_CREATED_ALERT)) + 1;
				columnName = "pro_created";
			} else if (_type.equals(PRO_COMPLETED_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(PRO_COMPLETED_ALERT)) + 1;
				columnName = "pro_completed";
			} else if (_type.equals(PRO_NEW_RESOURCE_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(PRO_NEW_RESOURCE_ALERT)) + 1;
				columnName = "pro_new_resource";
			} else if (_type.equals(TASK_ALLOCATE_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(TASK_ALLOCATE_ALERT)) + 1;
				columnName = "task_allocate";
			} else if (_type.equals(TASK_NEW_REQUEST_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(TASK_NEW_REQUEST_ALERT)) + 1;
				columnName = "task_new_request";
			} else if (_type.equals(TASK_ACCEPT_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(TASK_ACCEPT_ALERT)) + 1;
				columnName = "task_accept";
			} else if (_type.equals(TASK_REQUEST_RESCHEDULE_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(TASK_REQUEST_RESCHEDULE_ALERT)) + 1;
				columnName = "task_request_reschedule";
			} else if (_type.equals(TASK_REQUEST_REASSIGN_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(TASK_REQUEST_REASSIGN_ALERT)) + 1;
				columnName = "task_request_reassign";
			} else if (_type.equals(TASK_REASSIGN_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(TASK_REASSIGN_ALERT)) + 1;
				columnName = "task_reassign";
			} else if (_type.equals(TASK_RESCHEDULE_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(TASK_RESCHEDULE_ALERT)) + 1;
				columnName = "task_reschedule";
			} else if (_type.equals(TASK_COMPLETED_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(TASK_COMPLETED_ALERT)) + 1;
				columnName = "task_completed";
			} else if (_type.equals(TIMESHEET_RECEIVED_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(TIMESHEET_RECEIVED_ALERT)) + 1;
				columnName = "timesheet_received";
			} else if (_type.equals(INVOICE_GENERATED_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(INVOICE_GENERATED_ALERT)) + 1;
				columnName = "invoice_generated";
			} else if (_type.equals(SHARE_DOCUMENTS_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(SHARE_DOCUMENTS_ALERT)) + 1;
				columnName = "share_document";
			} else if (_type.equals(ADD_MYTEAM_MEMBER_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(ADD_MYTEAM_MEMBER_ALERT)) + 1;
				columnName = "add_myteam_member";
			} else if (_type.equals(NEW_JOINEE_PENDING_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(NEW_JOINEE_PENDING_ALERT)) + 1;
				columnName = "new_joinee_pending";
			} else if (_type.equals(NEWS_AND_ALERTS)) {
				cnt = uF.parseToInt(hmAlerts.get(NEWS_AND_ALERTS)) + 1;
				columnName = "news_and_alerts";
			} else if (_type.equals(LOAN_REQUEST_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(LOAN_REQUEST_ALERT)) + 1;
				columnName = "loan_request";
			} else if (_type.equals(LOAN_APPROVAL_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(LOAN_APPROVAL_ALERT)) + 1;
				columnName = "loan_approval";
			} else if (_type.equals(SELF_REVIEW_REQUEST_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(SELF_REVIEW_REQUEST_ALERT)) + 1;
				columnName = "self_review_request";
			} else if (_type.equals(SELF_REVIEW_APPROVAL_ALERT)) {
				cnt = uF.parseToInt(hmAlerts.get(SELF_REVIEW_APPROVAL_ALERT)) + 1;
				columnName = "self_review_approval";
			} else if (_type.equals(EMP_CONFIRMATIONS_ALERT)) {
				cnt = uF.parseToInt(strCount);
				columnName = "emp_confirmations";
			} else if (_type.equals(EMP_RESIGNATIONS_ALERT)) {
				cnt = uF.parseToInt(strCount);
				columnName = "emp_resignations";
			} else if (_type.equals(EMP_FINAL_DAY_ALERT)) {
				cnt = uF.parseToInt(strCount);
				columnName = "emp_final_day";
			} else if (_type.equals(TRAVEL_BOOKING_ALERT)) {
				cnt =  uF.parseToInt(hmAlerts.get(TRAVEL_BOOKING_ALERT)) + 1;
				columnName = "travelbooking";
			} else if (_type.equals(LIBRARY_REQUEST_ALERT)) {
				cnt =  uF.parseToInt(hmAlerts.get(LIBRARY_REQUEST_ALERT)) + 1;
				columnName = "library_request";
			} else if (_type.equals(LIBRARY_REQUEST_APPROVED_ALERT)) {
				cnt =  uF.parseToInt(hmAlerts.get(LIBRARY_REQUEST_APPROVED_ALERT)) + 1;
				columnName = "library_req_approved";
			} else if (_type.equals(NEW_MANUAL_ALERT)) {
				cnt =  uF.parseToInt(hmAlerts.get(NEW_MANUAL_ALERT)) + 1;
				columnName = "new_manual";
			} else if(_type.equals(MEETING_ROOM_BOOKING_REQUEST_ALERT)) {
				cnt =  uF.parseToInt(hmAlerts.get(MEETING_ROOM_BOOKING_REQUEST_ALERT)) + 1;
				columnName = "meeting_room_booking_request";
			} else if(_type.equals(MEETING_ROOM_BOOKING_REQUEST_APPROVED_ALERT)) {
				cnt =  uF.parseToInt(hmAlerts.get(MEETING_ROOM_BOOKING_REQUEST_APPROVED_ALERT)) + 1;
				columnName = "meeting_room_booking_req_approved";
			} else if(_type.equals(FOOD_REQUEST_ALERT)) {
				cnt =  uF.parseToInt(hmAlerts.get(FOOD_REQUEST_ALERT)) + 1;
				columnName = "food_request"; 
			} else if(_type.equals(NEW_CANDIDATE_FILL_ALERT)) {
				cnt =  uF.parseToInt(hmAlerts.get(NEW_CANDIDATE_FILL_ALERT)) + 1;
				columnName = "new_canditate_fill"; 
			} else if(_type.equals(EMP_TERMINATED_ALERT)) {
				cnt =  uF.parseToInt(hmAlerts.get(EMP_TERMINATED_ALERT)) + 1;
				columnName = "emp_terminations"; 
			} else if(_type.equals(FORM16_RELEASE_ALERT)) {
				cnt =  uF.parseToInt(hmAlerts.get(FORM16_RELEASE_ALERT)) + 1;
				columnName = "form16_release"; 
			} else if(_type.equals(CEO_REVIEW_ALERT)) {
				cnt =  uF.parseToInt(hmAlerts.get(CEO_REVIEW_ALERT)) + 1;
				columnName = "ceo_reviews"; 
			} else if(_type.equals(HOD_REVIEW_ALERT)) {
				cnt =  uF.parseToInt(hmAlerts.get(HOD_REVIEW_ALERT)) + 1;
				columnName = "hod_reviews"; 
			}
			
			if (columnName != null && !columnName.equals("")) { 
				pst = con.prepareStatement("update user_alerts set " + columnName + "=? where emp_id=?");
				pst.setInt(1, cnt);
				pst.setInt(2, uF.parseToInt(empId));
//				System.out.println("pst ===> " + pst);
				int x = pst.executeUpdate();
				pst.close();
				if (x == 0) {
					pst = con.prepareStatement("insert into user_alerts (emp_id," + columnName + ") values(?,?)");
					pst.setInt(1, uF.parseToInt(empId));
					pst.setInt(2, cnt);
//					System.out.println("pst ===> " + pst);
					pst.execute();
					pst.close();
				}
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

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getStrDomain() {
		return strDomain;
	}

	public void setStrDomain(String strDomain) {
		this.strDomain = strDomain;
	}

	public String get_type() {
		return _type;
	}

	public void set_type(String _type) {
		this._type = _type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStrOther() {
		return strOther;
	}

	public void setStrOther(String strOther) {
		this.strOther = strOther;
	}

	public String getStrData() {
		return strData;
	}

	public void setStrData(String strData) {
		this.strData = strData;
	}

	public String getStrAction() {
		return strAction;
	}

	public void setStrAction(String strAction) {
		this.strAction = strAction;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getStrType() {
		return strType;
	}

	public void setStrType(String strType) {
		this.strType = strType;
	}

	public String getStrCount() {
		return strCount;
	}

	public void setStrCount(String strCount) {
		this.strCount = strCount;
	}

	public String getCurrUserTypeID() {
		return currUserTypeID;
	}

	public void setCurrUserTypeID(String currUserTypeID) {
		this.currUserTypeID = currUserTypeID;
	}

	public String getStrEmployeeId() {
		return strEmployeeId;
	}

	public void setStrEmployeeId(String strEmployeeId) {
		this.strEmployeeId = strEmployeeId;
	}

	public String getStrDate() {
		return strDate;
	}

	public void setStrDate(String strDate) {
		this.strDate = strDate;
	}


}
