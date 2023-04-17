package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateKRATaskStatus extends ActionSupport implements ServletRequestAware,IStatements {

	HttpSession session;
	
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;
	
	private String type;
	private String operation;
	private String empId;
	private String kraId;
	private String kraTaskId;
	private String completedPercent;
	private String taskRating;
	private String strComment; 
	private String goalType;
	private String feedbackUserType;
	
	private String goalid;
	private String goalFreqId;
	
//	String superUserType;
	
	
	private static final long serialVersionUID = 1L;

	public String execute() {
		
		
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		UtilityFunctions uF=new UtilityFunctions();
		if(uF.parseToDouble(getCompletedPercent())>0.0) {
//			System.out.println("getGoalType() ===>> " + getGoalType());
			if(getGoalType() != null && getGoalType().equals("KRA")) {
//				System.out.println("getGoalType()1 ===>> " + getGoalType());
				updateKRATaskStatus(uF);
			} else {
				updateGoalStatus(uF);
//				System.out.println("getGoalType()2 ===>> " + getGoalType());
			}
		}
		
		if(getFeedbackUserType() != null && !getFeedbackUserType().equals("")) {
			if(getOperation() != null && getOperation().equals("RC")) {
				if(getGoalType() != null && !getGoalType().equals("KRA")) {
						updateGoalTargetEmpRatingAndComment(uF);
				} else {
					updateTaskEmpRatingAndComment(uF);
				}
			}
//			return MYSUCCESS;
		} else {
			
			if(getOperation() != null && getOperation().equals("RC")) {
				if(getGoalType() != null && !getGoalType().equals("KRA")) {
					updateGoalTargetRatingAndComment(uF);
				} else {
					updateTaskRatingAndComment(uF);
				}
			}
		}
		return SUCCESS;
	}
	
	
	private void updateTaskEmpRatingAndComment(UtilityFunctions uF) {
		
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String updateMsg = null;
			StringBuilder sbAllData = new StringBuilder();
			pst=con.prepareStatement("select * from goal_kra_emp_status_rating_details where emp_id =? and kra_id =? and kra_task_id = ? " +
					"and user_id = ? and user_type=? and goal_id=? and goal_freq_id=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getKraId()));
			pst.setDouble(3,uF.parseToDouble(getKraTaskId()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
//			System.out.println("getSuperUserType()==>"+getSuperUserType()+"==>getFeedbackUserType()==>"+getFeedbackUserType());
			pst.setString(5, getFeedbackUserType());
			pst.setInt(6, uF.parseToInt(getGoalid()));
			pst.setInt(7, uF.parseToInt(getGoalFreqId()));
			rs=pst.executeQuery();
//			System.out.println("pst4 ===>>>> " + pst);
			boolean flag = false;
			while(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			if(flag) {
				pst = con.prepareStatement("update goal_kra_emp_status_rating_details set user_rating=?, user_comment=?, updated_by=?, updated_date=? " +
					"where kra_id=? and kra_task_id=? and emp_id=? and user_id=? and user_type=? and goal_id=? and goal_freq_id=?");
				pst.setDouble(1, uF.parseToDouble(getTaskRating()));
				pst.setString(2, getStrComment());
				pst.setInt(3,uF.parseToInt(strSessionEmpId));
				pst.setDate(4,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, uF.parseToInt(getKraId()));
				pst.setInt(6, uF.parseToInt(getKraTaskId()));
				pst.setInt(7, uF.parseToInt(getEmpId()));
				pst.setInt(8, uF.parseToInt(strSessionEmpId));
				pst.setString(9, getFeedbackUserType());
				pst.setInt(10, uF.parseToInt(getGoalid()));
				pst.setInt(11, uF.parseToInt(getGoalFreqId()));
				pst.execute();
				pst.close();
				updateMsg = "Rating Updated";
			} else {
				pst = con.prepareStatement("insert into goal_kra_emp_status_rating_details(kra_id, kra_task_id, emp_id, user_id, user_rating," +
					" user_comment, entry_date, user_type,goal_id, goal_freq_id, updated_by, updated_date) values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getKraId()));
				pst.setInt(2, uF.parseToInt(getKraTaskId()));
				pst.setInt(3, uF.parseToInt(getEmpId()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setDouble(5, uF.parseToDouble(getTaskRating()));
				pst.setString(6, getStrComment());
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(8, getFeedbackUserType());
				pst.setInt(9, uF.parseToInt(getGoalid()));
				pst.setInt(10, uF.parseToInt(getGoalFreqId()));
				pst.setInt(11,uF.parseToInt(strSessionEmpId));
				pst.setDate(12,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();
				
				updateMsg = "Rating Inserted";
			} 
			
				sbAllData.append(updateMsg);
				
				String starID = getEmpId()+"_"+getKraId()+"_"+getKraTaskId();
				sbAllData.append("<script type=\"text/javascript\">" +
					"$(function() {" +
					"$('#starPrimaryGKT'"+starID+").raty({" +
					"readOnly: true," +
					"start: "+getTaskRating()+"," +
					"half: true," +
					"targetType: 'number'" +
					"});" +
					"});" +
					"</script>");
//				System.out.println("sbAllData ===> "+sbAllData);
					request.setAttribute("sbAllData", sbAllData.toString());			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}


	private void updateGoalTargetEmpRatingAndComment(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String updateMsg = null;
			StringBuilder sbAllData = new StringBuilder();
			pst=con.prepareStatement("select * from goal_kra_emp_status_rating_details where emp_id =? and goal_id =? and user_id=? " +
				"and user_type=? and goal_freq_id=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getGoalid()));
			pst.setInt(3, uF.parseToInt(strSessionEmpId));
			pst.setString(4, getFeedbackUserType());
			pst.setInt(5, uF.parseToInt(getGoalFreqId()));
			rs=pst.executeQuery();
//			System.out.println("pst1 ===>>>> " + pst);
			boolean flag = false;
			while(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			if(flag) {
				pst = con.prepareStatement("update goal_kra_emp_status_rating_details set user_rating=?,user_comment=?,updated_by=?,updated_date=? where goal_id=? and " +
					" emp_id=? and user_id=? and user_type=? and goal_freq_id=?");
				pst.setDouble(1, uF.parseToDouble(getTaskRating()));
				pst.setString(2, getStrComment());
				pst.setInt(3,uF.parseToInt(strSessionEmpId));
				pst.setDate(4,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(5, uF.parseToInt(getGoalid()));
				pst.setInt(6, uF.parseToInt(getEmpId()));
				pst.setInt(7, uF.parseToInt(strSessionEmpId));
				pst.setString(8, getFeedbackUserType());
				pst.setInt(9, uF.parseToInt(getGoalFreqId()));
//				System.out.println("pst2 ===>> " + pst);
				pst.execute();
				pst.close();
				
				updateMsg = "Rating Updated";
			} else {
				pst = con.prepareStatement("insert into goal_kra_emp_status_rating_details(goal_id, emp_id, user_id, user_rating, user_comment," +
					"entry_date,user_type,goal_freq_id,updated_by,updated_date) values(?,?,?,?, ?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getGoalid()));
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setDouble(4, uF.parseToDouble(getTaskRating()));
				pst.setString(5, getStrComment());
				pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(7, getFeedbackUserType());
				pst.setInt(8, uF.parseToInt(getGoalFreqId()));
				pst.setInt(9,uF.parseToInt(strSessionEmpId));
				pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
//				System.out.println("pst3 ===>> " + pst);
				pst.execute();
				pst.close();
				
				updateMsg = "Rating Inserted";
			}
			
				sbAllData.append(updateMsg);
				sbAllData.append("::::");
				
				String starID = getEmpId()+"_"+getKraId()+"_"+getKraTaskId();
				sbAllData.append("<script type=\"text/javascript\">" +
					"$(function() {" +
					"$('#starPrimaryGKT'"+starID+").raty({" +
					"readOnly: true," +
					"start: "+getTaskRating()+"," +
					"half: true," +
					"targetType: 'number'" +
					"});" +
					"});" +
					"</script>");
					request.setAttribute("sbAllData", sbAllData.toString());			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}


	private void updateGoalStatus(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
//		System.out.println("getEmptarget() ===> "+getEmptarget());
//		System.out.println("getEmptmptarget() ===> "+getEmptmptarget());
		try {
			con = db.makeConnection(con);
			String updateMsg = null;
			StringBuilder sbAllData = new StringBuilder();
			pst=con.prepareStatement("select * from goal_kra_status_rating_details where emp_id =? and goal_id =? and goal_freq_id =?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getGoalid()));
			pst.setInt(3, uF.parseToInt(getGoalFreqId()));
			rs=pst.executeQuery();
//			System.out.println("pst ===>>>> " + pst);
			boolean flag = false;
			while(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			if(flag) {
				pst = con.prepareStatement("update goal_kra_status_rating_details set complete_percent=?, updated_by=?, updated_date=? where goal_id=? and emp_id=? " +
					" and goal_freq_id =?");
				pst.setDouble(1, uF.parseToDouble(getCompletedPercent()));
				pst.setInt(2,uF.parseToInt(strSessionEmpId));
				pst.setDate(3,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, uF.parseToInt(getGoalid()));
				pst.setInt(5, uF.parseToInt(getEmpId()));
				pst.setInt(6, uF.parseToInt(getGoalFreqId()));
				pst.execute();
				pst.close();
				
				updateMsg = "Status Updated";
			} else {
				pst = con.prepareStatement("insert into goal_kra_status_rating_details(goal_id, emp_id, complete_percent, goal_freq_id, updated_by, updated_date) " +
					" values(?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getGoalid()));
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.setDouble(3, uF.parseToDouble(getCompletedPercent()));
				pst.setInt(4, uF.parseToInt(getGoalFreqId()));
				pst.setInt(5,uF.parseToInt(strSessionEmpId));
				pst.setDate(6,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();
				
				updateMsg = "Status Inserted";
			} 
			
			
				
				sbAllData.append(getCompletedPercent());
				sbAllData.append("::::");
				sbAllData.append(updateMsg);
				sbAllData.append("::::");
				
				String spanMargin100 = "-10"; 
				if(getType() != null && getType().equals("KRATarget")){
					spanMargin100 = "-10";
				}
				
				sbAllData.append("<div class=\"anaAttrib1\"><span style=\"margin-left:" + (uF.parseToDouble(getCompletedPercent()) > 95 ? uF.parseToDouble(getCompletedPercent())-10 : uF.parseToDouble(getCompletedPercent())-2.5) +"%;\">" +uF.showData(getCompletedPercent(), "")+ "%</span></div>" +
						"<div id=\"outbox\">");
						if(uF.parseToDouble(getCompletedPercent()) < 33.33) {
							sbAllData.append("<div id=\"redbox\" style=\"width: " +getCompletedPercent()+ "%;\"></div>");	
						} else if(uF.parseToDouble(getCompletedPercent()) >= 33.33 && uF.parseToDouble(getCompletedPercent()) < 66.67) {
							sbAllData.append("<div id=\"yellowbox\" style=\"width:" +getCompletedPercent()+ "%;\"></div>");
						} else if(uF.parseToDouble(getCompletedPercent()) >= 66.67) {
							sbAllData.append("<div id=\"greenbox\" style=\"width: " +getCompletedPercent()+ "%;\"></div>");
						}
						sbAllData.append("</div>" +
						"<div class=\"anaAttrib1\" style=\"float: left; width: 100%;\"><span style=\"float: left; margin-left:-2.5%;\">0%</span>" +
						"<span style=\"float: right; margin-right:"+spanMargin100+"%;updated_by,updated_date\">100%</span></div>");
						request.setAttribute("sbAllData", sbAllData.toString());			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}


	private void updateGoalTargetRatingAndComment(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String updateMsg = null;
			StringBuilder sbAllData = new StringBuilder();
			pst=con.prepareStatement("select * from goal_kra_status_rating_details where emp_id =? and goal_id =? and goal_freq_id=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getGoalid()));
			pst.setInt(3, uF.parseToInt(getGoalFreqId()));
			rs=pst.executeQuery();
//			System.out.println("pst ===>>>> " + pst);
			boolean flag = false;
			while(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			if(flag) {
				if(strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
					pst = con.prepareStatement("update goal_kra_status_rating_details set manager_id=?,manager_rating=?,manager_comment=?, updated_by = ?, updated_date = ? " +
						"where goal_id=? and emp_id=? and goal_freq_id=?");
				} else {
					pst = con.prepareStatement("update goal_kra_status_rating_details set hr_id=?,hr_rating =?,hr_comment=?, updated_by = ?, updated_date = ? where goal_id=? " +
							"and emp_id=? and goal_freq_id=?");
				}
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setDouble(2, uF.parseToDouble(getTaskRating()));
				pst.setString(3, getStrComment());
				pst.setInt(4,uF.parseToInt(strSessionEmpId));
				pst.setDate(5,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6, uF.parseToInt(getGoalid()));
				pst.setInt(7, uF.parseToInt(getEmpId()));
				pst.setInt(8, uF.parseToInt(getGoalFreqId()));
//				System.out.println("pst1 ===>> " + pst);
				pst.execute();
				pst.close();
				
				updateMsg = "Rating Updated";
			} else {
				if(strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
					pst = con.prepareStatement("insert into goal_kra_status_rating_details(goal_id, emp_id, manager_id, manager_rating, " +
						" manager_comment, goal_freq_id, updated_by, updated_date) values(?,?,?,?, ?,?,?,?)");
				} else {
					pst = con.prepareStatement("insert into goal_kra_status_rating_details(goal_id, emp_id, hr_id, hr_rating, hr_comment," +
						" goal_freq_id, updated_by, updated_date) values(?,?,?,?, ?,?,?,?)");
				}
				pst.setInt(1, uF.parseToInt(getGoalid()));
				pst.setInt(2, uF.parseToInt(getEmpId()));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setDouble(4, uF.parseToDouble(getTaskRating()));
				pst.setString(5, getStrComment());
				pst.setInt(6, uF.parseToInt(getGoalFreqId()));
				pst.setInt(7,uF.parseToInt(strSessionEmpId));
				pst.setDate(8,uF.getCurrentDate(CF.getStrTimeZone()));
//				System.out.println("pst2 ===>> " + pst);
				pst.execute();
				pst.close();
				
				updateMsg = "Rating Inserted";
			} 
			
				sbAllData.append(updateMsg);
				sbAllData.append("::::");
				
				String starID = getEmpId()+"_"+getKraId()+"_"+getKraTaskId();
				sbAllData.append("<script type=\"text/javascript\">" +
						"$(function() {" +
						"$('#starPrimaryGKT'"+starID+").raty({" +
						"readOnly: true," +
						"start: "+getTaskRating()+"," +
						"half: true," +
						"targetType: 'number'" +
						"});" +
						"});" +
						"</script>");
						request.setAttribute("sbAllData", sbAllData.toString());			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}


	private void updateTaskRatingAndComment(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			String updateMsg = null;
			StringBuilder sbAllData = new StringBuilder();
			pst=con.prepareStatement("select * from goal_kra_status_rating_details where emp_id=? and kra_id=? and kra_task_id=? and goal_id=? and goal_freq_id=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getKraId()));
			pst.setInt(3, uF.parseToInt(getKraTaskId()));
			pst.setInt(4, uF.parseToInt(getGoalid()));
			pst.setInt(5, uF.parseToInt(getGoalFreqId()));
			rs=pst.executeQuery();
//			System.out.println("pst5 ===>>>> " + pst);
			boolean flag = false;
			while(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			if(flag) {
				if(strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
					pst = con.prepareStatement("update goal_kra_status_rating_details set manager_id=?,manager_rating=?,manager_comment=?, updated_by=?, updated_date=?" +
						" where kra_id=? and kra_task_id=? and emp_id=? and goal_id=? and goal_freq_id=?");
				} else {
					pst = con.prepareStatement("update goal_kra_status_rating_details set hr_id=?,hr_rating=?,hr_comment=?, updated_by=?, updated_date=? where kra_id=? " +
						" and kra_task_id=? and emp_id=? and goal_id=? and goal_freq_id=?");
				}
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				pst.setDouble(2, uF.parseToDouble(getTaskRating()));
				pst.setString(3, getStrComment());
				pst.setInt(4,uF.parseToInt(strSessionEmpId));
				pst.setDate(5,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6, uF.parseToInt(getKraId()));
				pst.setInt(7, uF.parseToInt(getKraTaskId()));
				pst.setInt(8, uF.parseToInt(getEmpId()));
				pst.setInt(9, uF.parseToInt(getGoalid()));
				pst.setInt(10, uF.parseToInt(getGoalFreqId()));
//				System.out.println("pst3 ===>> " + pst);
				pst.execute();
				pst.close();
				
				updateMsg = "Rating Updated";
			} else {
				if(strSessionUserType != null && strSessionUserType.equals(MANAGER)) {
					pst = con.prepareStatement("insert into goal_kra_status_rating_details(kra_id, kra_task_id, emp_id, manager_id, " +
						" manager_rating, manager_comment,goal_id,goal_freq_id,updated_by,updated_date) values(?,?,?,?, ?,?,?,?, ?,?)");
				} else {
					pst = con.prepareStatement("insert into goal_kra_status_rating_details(kra_id, kra_task_id, emp_id, hr_id, " +
						" hr_rating, hr_comment,goal_id,goal_freq_id,updated_by,updated_date) values(?,?,?,?, ?,?,?,?, ?,?)");
				}
				pst.setInt(1, uF.parseToInt(getKraId()));
				pst.setInt(2, uF.parseToInt(getKraTaskId()));
				pst.setInt(3, uF.parseToInt(getEmpId()));
				pst.setInt(4, uF.parseToInt(strSessionEmpId));
				pst.setDouble(5, uF.parseToDouble(getTaskRating()));
				pst.setString(6, getStrComment());
				pst.setInt(7, uF.parseToInt(getGoalid()));
				pst.setInt(8, uF.parseToInt(getGoalFreqId()));
				pst.setInt(9,uF.parseToInt(strSessionEmpId));
				pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
//				System.out.println("pst4 ===>> " + pst);
				pst.execute();
				pst.close();
				
				updateMsg = "Rating Inserted";
			} 
			
				sbAllData.append(updateMsg);
				
				String starID = getEmpId()+"_"+getKraId()+"_"+getKraTaskId();
				sbAllData.append("<script type=\"text/javascript\">" +
						"$(function() {" +
						"$('#starPrimaryGKT'"+starID+").raty({" +
						"readOnly: true," +
						"start: "+getTaskRating()+"," +
						"half: true," +
						"targetType: 'number'" +
						"});" +
						"});" +
						"</script>");
//				System.out.println("sbAllData ===> "+sbAllData);
						request.setAttribute("sbAllData", sbAllData.toString());			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}
	
	
	private void updateKRATaskStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
//		System.out.println("getEmptarget() ===> "+getEmptarget());
//		System.out.println("getEmptmptarget() ===> "+getEmptmptarget());
		try {
			con = db.makeConnection(con);
			String updateMsg = null;
			StringBuilder sbAllData = new StringBuilder();
			pst=con.prepareStatement("select * from goal_kra_status_rating_details where emp_id =? and kra_id=? and kra_task_id=? and goal_id=? and goal_freq_id=?");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getKraId()));
			pst.setInt(3, uF.parseToInt(getKraTaskId()));
			pst.setInt(4, uF.parseToInt(getGoalid()));
			pst.setInt(5, uF.parseToInt(getGoalFreqId()));
			rs=pst.executeQuery();
//			System.out.println("pst ===>>>> " + pst);
			boolean flag = false;
			while(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			if(flag) {
				pst = con.prepareStatement("update goal_kra_status_rating_details set complete_percent=?, updated_by=?, updated_date=? where kra_id=? and kra_task_id=? and emp_id=? and goal_id=? and goal_freq_id=?");
				pst.setDouble(1, uF.parseToDouble(getCompletedPercent()));
				pst.setInt(2,uF.parseToInt(strSessionEmpId));
				pst.setDate(3,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, uF.parseToInt(getKraId()));
				pst.setInt(5, uF.parseToInt(getKraTaskId()));
				pst.setInt(6, uF.parseToInt(getEmpId()));
				pst.setInt(7, uF.parseToInt(getGoalid()));
				pst.setInt(8, uF.parseToInt(getGoalFreqId()));
				pst.execute();
				System.out.println("pst ===>>>> " + pst);

				pst.close();
				updateMsg = "Status Updated";
			} else {
				pst = con.prepareStatement("insert into goal_kra_status_rating_details(kra_id, kra_task_id, emp_id, complete_percent,goal_id," +
					"goal_freq_id,updated_by,updated_date) values(?,?,?,?, ?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getKraId()));
				pst.setInt(2, uF.parseToInt(getKraTaskId()));
				pst.setInt(3, uF.parseToInt(getEmpId()));
				pst.setDouble(4, uF.parseToDouble(getCompletedPercent()));
				pst.setInt(5, uF.parseToInt(getGoalid()));
				pst.setInt(6, uF.parseToInt(getGoalFreqId()));
				pst.setInt(7,uF.parseToInt(strSessionEmpId));
				pst.setDate(8,uF.getCurrentDate(CF.getStrTimeZone()));
				pst.execute();
				pst.close();
//				System.out.println("pst ===>>>> " + pst);

				updateMsg = "Status Inserted";
			} 
			
				
				sbAllData.append(getCompletedPercent());
				sbAllData.append("::::");
				sbAllData.append(updateMsg);
				sbAllData.append("::::");
				
//				String steadyWidth = "25";
				String spanMargin100 = "-10"; 
				if(getType() != null && getType().equals("KRATarget")){
//					steadyWidth = "10";
					spanMargin100 = "-10";
				}
				
				sbAllData.append("<div class=\"anaAttrib1\"><span style=\"margin-left:" + (uF.parseToDouble(getCompletedPercent()) > 95 ? uF.parseToDouble(getCompletedPercent())-10 : uF.parseToDouble(getCompletedPercent())-2.5) +"%;\">" +uF.showData(getCompletedPercent(), "")+ "%</span></div>" +
						"<div id=\"outbox\">");
						if(uF.parseToDouble(getCompletedPercent()) < 33.33) {
							sbAllData.append("<div id=\"redbox\" style=\"width: " +getCompletedPercent()+ "%;\"></div>");	
						} else if(uF.parseToDouble(getCompletedPercent()) >= 33.33 && uF.parseToDouble(getCompletedPercent()) < 66.67) {
							sbAllData.append("<div id=\"yellowbox\" style=\"width:" +getCompletedPercent()+ "%;\"></div>");
						} else if(uF.parseToDouble(getCompletedPercent()) >= 66.67) {
							sbAllData.append("<div id=\"greenbox\" style=\"width: " +getCompletedPercent()+ "%;\"></div>");
						}
						sbAllData.append("</div>" +
						"<div class=\"anaAttrib1\" style=\"float: left; width: 100%;\"><span style=\"float: left; margin-left:-2.5%;\">0%</span>" +
						"<span style=\"float: right; margin-right:"+spanMargin100+"%;\">100%</span></div>");
//						"<span style=\"color: #808080;\">Slow</span>" +
//						"<span style=\"margin-left:"+steadyWidth+"px; color: #808080;\">Steady</span>" +
//						"<span style=\"float: right; color: #808080;\">Momentum</span>"
//				System.out.println("sbAllData ===> "+sbAllData);
						request.setAttribute("sbAllData", sbAllData.toString());			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
	}
	
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getEmpId() {
		return empId;
	}
	
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	
	public String getKraId() {
		return kraId;
	}
	
	public void setKraId(String kraId) {
		this.kraId = kraId;
	}
	
	public String getKraTaskId() {
		return kraTaskId;
	}
	
	public void setKraTaskId(String kraTaskId) {
		this.kraTaskId = kraTaskId;
	}

	public String getCompletedPercent() {
		return completedPercent;
	}
	
	public void setCompletedPercent(String completedPercent) {
		this.completedPercent = completedPercent;
	}

	public String getOperation() {
		return operation;
	}
	
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public String getTaskRating() {
		return taskRating;
	}
	
	public void setTaskRating(String taskRating) {
		this.taskRating = taskRating;
	}
	
	public String getStrComment() {
		return strComment;
	}
	
	public void setStrComment(String strComment) {
		this.strComment = strComment;
	}

	public String getGoalType() {
		return goalType;
	}

	public void setGoalType(String goalType) {
		this.goalType = goalType;
	}

	public String getFeedbackUserType() {
		return feedbackUserType;
	}

	public void setFeedbackUserType(String feedbackUserType) {
		this.feedbackUserType = feedbackUserType;
	}

	public String getGoalid() {
		return goalid;
	}

	public void setGoalid(String goalid) {
		this.goalid = goalid;
	}

	public String getGoalFreqId() {
		return goalFreqId;
	}

	public void setGoalFreqId(String goalFreqId) {
		this.goalFreqId = goalFreqId;
	}

	HttpServletRequest request;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

}
