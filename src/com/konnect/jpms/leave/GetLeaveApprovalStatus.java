package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.training.GetTrainerCalenderAjax;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetLeaveApprovalStatus extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;

	CommonFunctions CF = null;

	private String effectiveid;
	private String type;
	private String appFreqId;
	String strBaseUserType = null;//Created By Dattatray 13-6-2022
	String strAction = null;//Created By Dattatray 13-6-2022

	public String execute() {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) {
			return LOGIN;
		}
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		//Created By Dattatray 10-6-2022
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}

		//System.out.println("getType==>"+getType()); 
		if (getType() != null) {
			if (getType().equals("1")) {
				String effectivetype = WORK_FLOW_LEAVE;
				getApprovalStatus(effectivetype);
			} else if (getType().equals("2")) {
				String effectivetype = WORK_FLOW_TIMESHEET;
				getApprovalStatus(effectivetype);
			} else if (getType().equals("3")) {
				String effectivetype = WORK_FLOW_REQUISITION; 
				getApprovalStatus(effectivetype);
			} else if (getType().equals("4")) {
				String effectivetype = WORK_FLOW_TRAVEL;
				getApprovalStatus(effectivetype);
			} else if (getType().equals("5")) {
				String effectivetype = WORK_FLOW_REIMBURSEMENTS;
				getApprovalStatus(effectivetype);
			} else if (getType().equals("6")) {
				String effectivetype = WORK_FLOW_PERK;
				getApprovalStatus(effectivetype);
			} else if (getType().equals("7")) {
				String effectivetype = WORK_FLOW_LEAVE_ENCASH;
				getApprovalStatus(effectivetype);
			} else if (getType().equals("8")) {
				String effectivetype = WORK_FLOW_LTA;
				getApprovalStatus(effectivetype);
			} else if (getType().equals("9")) {
				String effectivetype = WORK_FLOW_LOAN;
				getApprovalStatus(effectivetype);
			} else if (getType().equals("10")) {
				String effectivetype = WORK_FLOW_RESIGN;
				getApprovalStatus(effectivetype);
			} else if (getType().equals("11")) {
				String effectivetype = WORK_FLOW_RECRUITMENT;
				getApprovalStatus(effectivetype);
			} else if (getType().equals("12")) {
				String effectivetype = WORK_FLOW_SELF_REVIEW;
				getApprovalStatus(effectivetype);
			} else if (getType().equals("13")) {
				String effectivetype = WORK_FLOW_TERMINATION;
				getApprovalStatus(effectivetype);
			} else if (getType().equals("14")) {
				String effectivetype = WORK_FLOW_EXCEPTION;
				getApprovalStatus(effectivetype);
			} else if (getType().equals("15")) {
				String effectivetype = WORK_FLOW_RESUME_SHORTLIST;
				getApprovalStatus(effectivetype);
			} else if (getType().equals("16")) {
				String effectivetype = WORK_FLOW_PERSONAL_GOAL;
				getApprovalStatus(effectivetype);
			}
		}
			return SUCCESS;
		
	}
	
	//Created By Dattatray 09-06-2022 t0 13-06-2022
	private void loadPageVisitAuditTrail(UtilityFunctions uF,String effectivetype) {
		Connection con=null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			Map<String, String> hmEmpProfile = CF.getEmpNameMap(con,null,getEffectiveid());
			StringBuilder builder = new StringBuilder();
			
//				builder.append("work flow of Emp name : "+getEmpName());
			builder.append("work flow of Emp name : ");
				
			CF.pageVisitAuditTrail(con,CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			db.closeConnection(con);
		}
		
	}


	private void getApprovalStatus(String effectivetype) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			loadPageVisitAuditTrail(uF,effectivetype);
			
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmEmpUserId = CF.getEmpUserIdMap(con);
			if(hmEmpUserId == null) hmEmpUserId = new HashMap<String, String>();
			
			if(effectivetype != null && effectivetype.equals(WORK_FLOW_EXCEPTION)) {
				pst = con.prepareStatement("select * from exception_reason er, attendance_details ad where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = er._date " +
					"and er.emp_id=ad.emp_id and er.exception_id=?");
				pst.setInt(1, uF.parseToInt(getEffectiveid()));
//				System.out.println("pst ====> " + pst);
				rs = pst.executeQuery();
				Map<String, String> hmApprovalStatus = new HashMap<String, String>();
				Map<String, String> hmEmpSupervisorId = CF.getEmpSupervisorIdMap(con);
				int step = 1;
				while (rs.next()) {
	
					String memberStep = hmApprovalStatus.get(rs.getString("exception_id"));
//					String strUserTypeName = uF.parseToInt(rs.getString("user_type_id")) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(rs.getString("user_type_id")), "") + ")" : "";
					String strUserTypeName = " ("+MANAGER+")";
					String strApproveMemTick = "";
					String strApproveMemReason = "";
					if (uF.parseToInt(rs.getString("status")) == 1) {
						strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
					} else if (uF.parseToInt(rs.getString("status")) == -1) {
						strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
					} else {
//						strApproveMemTick = "<i class=\"fa fa-minus\" aria-hidden=\"true\" title=\"Waiting\"></i>";
					}
					if(rs.getString("approval_reason")!=null && !rs.getString("approval_reason").trim().equals("") && !rs.getString("approval_reason").trim().equalsIgnoreCase("NULL") && !rs.getString("approval_reason").trim().equalsIgnoreCase("''")) {
						strApproveMemReason = "[Reason: "+rs.getString("approval_reason").trim()+"]";
					}
	
					if (memberStep == null) {
						memberStep = "<strong>Step " + step + ".</strong> " + strApproveMemTick + hmEmployeeNameMap.get(hmEmpSupervisorId.get(rs.getString("emp_id").trim())) + strUserTypeName + strApproveMemReason;
//						strMemberPosition = rs.getString("member_position");
					}/* else {
						if (strMemberPosition != null && strMemberPosition.equals(rs.getString("member_position"))) {
							memberStep += ", "+ strApproveMemTick  + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strUserTypeName + strApproveMemReason;
						} else {
							step++;
							strMemberPosition = rs.getString("member_position");
							// memberStep+=" ======> "+hmEmployeeNameMap.get(rs.getString("emp_id").trim())+strUserTypeName;
							memberStep += "<br/><strong>Step " + step + ".</strong> "+ strApproveMemTick + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strUserTypeName + strApproveMemReason;
						}
					}*/
					hmApprovalStatus.put(rs.getString("exception_id"), memberStep);
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmApprovalStatus", hmApprovalStatus);
				
			} else {
				pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type=? order by member_position");
				pst.setInt(1, uF.parseToInt(getEffectiveid()));
				pst.setString(2, effectivetype);
	//			System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmApprovalStatus = new HashMap<String, String>();
				String strMemberPosition = null;
				int step = 1;
				boolean flag = false;
				while (rs.next()) {
	
					String memberStep = hmApprovalStatus.get(rs.getString("effective_id"));
					String strUserTypeName = uF.parseToInt(rs.getString("user_type_id")) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(rs.getString("user_type_id")), "") + ")" : "";
					String strApproveMemTick = "";
					String strApproveMemReason = "";
					if (uF.parseToInt(rs.getString("is_approved")) == 1 || uF.parseToInt(rs.getString("is_approved")) == 2 || uF.parseToInt(rs.getString("is_approved")) == -1) {
						if (uF.parseToInt(rs.getString("is_approved")) == 2) {
							/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
							strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
						} else if (uF.parseToInt(rs.getString("is_approved")) == 1) {
							/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
							strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
						} else if (uF.parseToInt(rs.getString("is_approved")) == -1) {
							/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
							strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
						} else {
							strApproveMemTick = "<i class=\"fa fa-minus\" aria-hidden=\"true\" title=\"Waiting\"></i>";
						}
						if(rs.getString("reason")!=null && !rs.getString("reason").trim().equals("") && !rs.getString("reason").trim().equalsIgnoreCase("NULL")){
							strApproveMemReason = "[Reason: "+rs.getString("reason").trim()+"]";
						}
					} else if (uF.parseToInt(rs.getString("is_approved")) == 0) {
						flag = true;
					}
	
					if (memberStep == null) {
						memberStep = "<strong>Step " + step + ".</strong> " + strApproveMemTick + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strUserTypeName + strApproveMemReason;
						strMemberPosition = rs.getString("member_position");
					} else {
						if (strMemberPosition != null && strMemberPosition.equals(rs.getString("member_position"))) {
							memberStep += ", "+ strApproveMemTick  + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strUserTypeName + strApproveMemReason;
						} else {
							step++;
							strMemberPosition = rs.getString("member_position");
							// memberStep+=" ======> "+hmEmployeeNameMap.get(rs.getString("emp_id").trim())+strUserTypeName;
							memberStep += "<br/><strong>Step " + step + ".</strong> "+ strApproveMemTick + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strUserTypeName + strApproveMemReason;
						}
					}
					hmApprovalStatus.put(rs.getString("effective_id"), memberStep);
				}
				rs.close();
				pst.close();
	//			System.out.println("hmApprovalStatus ===>> " + hmApprovalStatus);
				request.setAttribute("hmApprovalStatus", hmApprovalStatus);
				
				StringBuilder sbReason = new StringBuilder();;
				if(flag){
					if(effectivetype.equals(WORK_FLOW_LEAVE)){
						pst = con.prepareStatement("select * from emp_leave_entry where leave_id=? and user_id > 0");
						pst.setInt(1, uF.parseToInt(getEffectiveid()));
						rs = pst.executeQuery();
						while (rs.next()) {
							String strApproveMemTick = "";
							String strApproveMemReason = "";
							if (uF.parseToInt(rs.getString("is_approved")) == 1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
								
							} else if (uF.parseToInt(rs.getString("is_approved")) == -1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
								
							}
							if(rs.getString("manager_reason")!=null && !rs.getString("manager_reason").trim().equals("") && !rs.getString("manager_reason").trim().equalsIgnoreCase("NULL")){
								strApproveMemReason = "[Reason: "+rs.getString("manager_reason").trim()+"]";
							}
	
							String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("user_id"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("user_id"))), "") + ")" : "";
							sbReason.append(strApproveMemTick+hmEmployeeNameMap.get(rs.getString("user_id").trim())+ strUserTypeName + strApproveMemReason);
						}
						rs.close();
						pst.close();
					} else if(effectivetype.equals(WORK_FLOW_TIMESHEET)){
						
					} else if(effectivetype.equals(WORK_FLOW_REQUISITION)){
						pst = con.prepareStatement("select * from requisition_details where requisition_id=? and user_id > 0");
						pst.setInt(1, uF.parseToInt(getEffectiveid()));
						rs = pst.executeQuery();
						while (rs.next()) {
							String strApproveMemTick = "";
							String strApproveMemReason = "";
							if (uF.parseToInt(rs.getString("is_approved")) == 1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
								
							} else if (uF.parseToInt(rs.getString("is_approved")) == -1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
								
							}
							if(rs.getString("approve_reason")!=null && !rs.getString("approve_reason").trim().equals("") && !rs.getString("approve_reason").trim().equalsIgnoreCase("NULL")){
								strApproveMemReason = "[Reason: "+rs.getString("approve_reason").trim()+"]";
							}
							String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("user_id"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("user_id"))), "") + ")" : "";
							sbReason.append(strApproveMemTick+hmEmployeeNameMap.get(rs.getString("user_id").trim())+ strUserTypeName +strApproveMemReason);
						}
						rs.close();
						pst.close();
					} else if(effectivetype.equals(WORK_FLOW_TRAVEL)){
						pst = con.prepareStatement("select * from emp_leave_entry where leave_id=? and user_id > 0");
						pst.setInt(1, uF.parseToInt(getEffectiveid()));
						rs = pst.executeQuery();
						while (rs.next()) {
							String strApproveMemTick = "";
							String strApproveMemReason = "";
							if (uF.parseToInt(rs.getString("is_approved")) == 1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
								
							} else if (uF.parseToInt(rs.getString("is_approved")) == -1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
								
							}
							if(rs.getString("manager_reason")!=null && !rs.getString("manager_reason").trim().equals("") && !rs.getString("manager_reason").trim().equalsIgnoreCase("NULL")){
								strApproveMemReason = "[Reason: "+rs.getString("manager_reason").trim()+"]";
							}
							
							String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("user_id"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("user_id"))), "") + ")" : "";
							sbReason.append(strApproveMemTick+hmEmployeeNameMap.get(rs.getString("user_id").trim())+ strUserTypeName +strApproveMemReason);
						}
						rs.close();
						pst.close();
					} else if(effectivetype.equals(WORK_FLOW_REIMBURSEMENTS)){
						pst = con.prepareStatement("select * from emp_reimbursement where reimbursement_id=? and approval_1_emp_id >0");
						pst.setInt(1, uF.parseToInt(getEffectiveid()));
						rs = pst.executeQuery();
						while (rs.next()) {
							String strApproveMemTick = "";
							String strApproveMemReason = "";
							if (uF.parseToInt(rs.getString("approval_1")) == 1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
								
							} else if (uF.parseToInt(rs.getString("approval_1")) == -1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
								
							}
							if(rs.getString("approve_reason")!=null && !rs.getString("approve_reason").trim().equals("") && !rs.getString("approve_reason").trim().equalsIgnoreCase("NULL")){
								strApproveMemReason = "[Reason: "+rs.getString("approve_reason").trim()+"]";
							}
							
							String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("approval_1_emp_id"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("approval_1_emp_id"))), "") + ")" : "";
							sbReason.append(strApproveMemTick+hmEmployeeNameMap.get(rs.getString("approval_1_emp_id").trim())+ strUserTypeName +strApproveMemReason);
						}
						rs.close();
						pst.close();
					} else if(effectivetype.equals(WORK_FLOW_PERK)){
						pst = con.prepareStatement("select * from emp_perks where perks_id=? and approval_1_emp_id>0");
						pst.setInt(1, uF.parseToInt(getEffectiveid()));
						rs = pst.executeQuery();
						while (rs.next()) {
							String strApproveMemTick = "";
							String strApproveMemReason = "";
							if (uF.parseToInt(rs.getString("approval_1")) == 1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
								
							} else if (uF.parseToInt(rs.getString("approval_1")) == -1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
								
							}
							if(rs.getString("approve_reason")!=null && !rs.getString("approve_reason").trim().equals("") && !rs.getString("approve_reason").trim().equalsIgnoreCase("NULL")){
								strApproveMemReason = "[Reason: "+rs.getString("approve_reason").trim()+"]";
							}
	
							String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("approval_1_emp_id"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("approval_1_emp_id"))), "") + ")" : "";
							sbReason.append(strApproveMemTick+hmEmployeeNameMap.get(rs.getString("approval_1_emp_id").trim())+ strUserTypeName +strApproveMemReason);
						}
						rs.close();
						pst.close();					
					} else if(effectivetype.equals(WORK_FLOW_LEAVE_ENCASH)){
						pst = con.prepareStatement("select * from emp_leave_encashment where leave_encash_id=? and approved_by > 0");
						pst.setInt(1, uF.parseToInt(getEffectiveid()));
						rs = pst.executeQuery();
						while (rs.next()) {
							String strApproveMemTick = "";
							String strApproveMemReason = "";
							if (uF.parseToInt(rs.getString("is_approved")) == 1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
								
							} else if (uF.parseToInt(rs.getString("is_approved")) == -1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
								
							}
							if(rs.getString("approve_reason")!=null && !rs.getString("approve_reason").trim().equals("") && !rs.getString("approve_reason").trim().equalsIgnoreCase("NULL")){
								strApproveMemReason = "[Reason: "+rs.getString("approve_reason").trim()+"]";
							}
							
							String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("approved_by"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("approved_by"))), "") + ")" : "";
							sbReason.append(strApproveMemTick+hmEmployeeNameMap.get(rs.getString("approved_by").trim())+ strUserTypeName +strApproveMemReason);
						}
						rs.close();
						pst.close();					
					} else if(effectivetype.equals(WORK_FLOW_LTA)){
						pst = con.prepareStatement("select * from emp_lta_details where emp_lta_id=? and approved_by > 0");
						pst.setInt(1, uF.parseToInt(getEffectiveid()));
	//					System.out.println("pst====>"+pst);
						rs = pst.executeQuery();
						while (rs.next()) {
							String strApproveMemTick = "";
							String strApproveMemReason = "";
							if (uF.parseToInt(rs.getString("is_approved")) == 1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
								
								
							} else if (uF.parseToInt(rs.getString("is_approved")) == -1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
								
							}
							if(rs.getString("approve_reason")!=null && !rs.getString("approve_reason").trim().equals("") && !rs.getString("approve_reason").trim().equalsIgnoreCase("NULL")){
								strApproveMemReason = "[Reason: "+rs.getString("approve_reason").trim()+"]";
							}
							
							String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("approved_by"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("approved_by"))), "") + ")" : "";
							sbReason.append(strApproveMemTick+hmEmployeeNameMap.get(rs.getString("approved_by").trim())+ strUserTypeName +strApproveMemReason);
						}
						rs.close();
						pst.close();						
					} else if(effectivetype.equals(WORK_FLOW_LOAN)){
						pst = con.prepareStatement("select * from loan_applied_details where loan_applied_id=? and approved_by > 0");
						pst.setInt(1, uF.parseToInt(getEffectiveid()));
						rs = pst.executeQuery();
						while (rs.next()) {
							String strApproveMemTick = "";
							String strApproveMemReason = "";
							if (uF.parseToInt(rs.getString("is_approved")) == 1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
								
							} else if (uF.parseToInt(rs.getString("is_approved")) == -1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
							}
							if(rs.getString("approve_reason")!=null && !rs.getString("approve_reason").trim().equals("") && !rs.getString("approve_reason").trim().equalsIgnoreCase("NULL")){
								strApproveMemReason = "[Reason: "+rs.getString("approve_reason").trim()+"]";
							}
							
							String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("approved_by"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("approved_by"))), "") + ")" : "";
							sbReason.append(strApproveMemTick+hmEmployeeNameMap.get(rs.getString("approved_by").trim())+ strUserTypeName +strApproveMemReason);
						}
						rs.close();
						pst.close();	
					} else if(effectivetype.equals(WORK_FLOW_RESIGN)){
						pst = con.prepareStatement("select * from emp_off_board where off_board_id=? and approved_1_by > 0");
						pst.setInt(1, uF.parseToInt(getEffectiveid()));
	//					System.out.println("pst==>"+pst);
						rs = pst.executeQuery();
						while (rs.next()) {
							String strApproveMemTick = "";
							String strApproveMemReason = "";
							if (uF.parseToInt(rs.getString("approved_1")) == 1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
								
							} else if (uF.parseToInt(rs.getString("approved_1")) == -1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
								
							}
							if(rs.getString("approved_1_reason")!=null && !rs.getString("approved_1_reason").trim().equals("") && !rs.getString("approved_1_reason").trim().equalsIgnoreCase("NULL")){
								strApproveMemReason = "[Reason: "+rs.getString("approved_1_reason").trim()+"]";
							}
							//System.out.println("strApproveMemTick==>"+strApproveMemTick+"==>strApproveMemReason==>"+strApproveMemReason);
							String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("approved_1"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("approved_1"))), "") + ")" : "";
							sbReason.append(strApproveMemTick+uF.showData(hmEmployeeNameMap.get(rs.getString("approved_1_by").trim()), "-")+ strUserTypeName +strApproveMemReason);
							//System.out.println("sbReason==>"+sbReason.toString());
						}
						rs.close();
						pst.close();	
					} else if(effectivetype.equals(WORK_FLOW_RECRUITMENT)) {
						pst = con.prepareStatement("select * from recruitment_details where recruitment_id=? and approved_by > 0");
						pst.setInt(1, uF.parseToInt(getEffectiveid()));
						rs = pst.executeQuery();
						while (rs.next()) {
							String strApproveMemTick = "";
							String strApproveMemReason = "";
							if (uF.parseToInt(rs.getString("status")) == 1) {
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
								
							} else if (uF.parseToInt(rs.getString("status")) == -1) {
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
								
							}
							if(rs.getString("req_deny_reason")!=null && !rs.getString("req_deny_reason").trim().equals("") && !rs.getString("req_deny_reason").trim().equalsIgnoreCase("NULL")){
								strApproveMemReason = "[Reason: "+rs.getString("req_deny_reason").trim()+"]";
							}
							
							
							String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("approved_by"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("approved_by"))), "") + ")" : "";
							sbReason.append(strApproveMemTick+hmEmployeeNameMap.get(rs.getString("approved_by").trim())+ strUserTypeName +strApproveMemReason);
							
						}	
						rs.close();
						pst.close();
					} else if(effectivetype.equals(WORK_FLOW_RESUME_SHORTLIST)) {
						pst = con.prepareStatement("select * from candidate_application_details where candi_application_deatils_id=? and added_by > 0");
						pst.setInt(1, uF.parseToInt(getEffectiveid()));
						rs = pst.executeQuery();
						while (rs.next()) {
							String strApproveMemTick = "";
							String strApproveMemReason = "";
							if (uF.parseToInt(rs.getString("application_status")) == 2) {
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
								
							} else if (uF.parseToInt(rs.getString("application_status")) == -1) {
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
								
							}
							if(rs.getString("application_shortlist_reason")!=null && !rs.getString("application_shortlist_reason").trim().equals("") && !rs.getString("application_shortlist_reason").trim().equalsIgnoreCase("NULL")){
								strApproveMemReason = "[Reason: "+rs.getString("application_shortlist_reason").trim()+"]";
							}
							
							
							String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("application_shortlist_by"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("application_shortlist_by"))), "") + ")" : "";
							sbReason.append(strApproveMemTick+hmEmployeeNameMap.get(rs.getString("application_shortlist_by"))+ strUserTypeName +strApproveMemReason);
							
						}	
						rs.close();
						pst.close();
					} else if(effectivetype.equals(WORK_FLOW_PERSONAL_GOAL)) {
						pst = con.prepareStatement("select * from goal_details where goal_id=? and approved_by > 0");
						pst.setInt(1, uF.parseToInt(getEffectiveid()));
						rs = pst.executeQuery();
						while (rs.next()) {
							String strApproveMemTick = "";
							String strApproveMemReason = "";
							if (uF.parseToInt(rs.getString("approve_status")) == 1) {
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
								
							} else if (uF.parseToInt(rs.getString("approve_status")) == -1) {
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
								
							}
							if(rs.getString("approve_reason")!=null && !rs.getString("approve_reason").trim().equals("") && !rs.getString("approve_reason").trim().equalsIgnoreCase("NULL")){
								strApproveMemReason = "[Reason: "+rs.getString("approve_reason").trim()+"]";
							}
							
							
							String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("approved_by"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("approved_by"))), "") + ")" : "";
							sbReason.append(strApproveMemTick+hmEmployeeNameMap.get(rs.getString("approved_by"))+ strUserTypeName +strApproveMemReason);
							
						}	
						rs.close();
						pst.close();
					} else if(effectivetype.equals(WORK_FLOW_SELF_REVIEW)) {
						pst = con.prepareStatement("select * from appraisal_details where my_review_status = 1 and appraisal_details_id=? and publish_approve_deny_by > 0 ");
	//					pst = con.prepareStatement("select * from appraisal_details ad, appraisal_details_frequency adf where ad.appraisal_details_id= adf.appraisal_id "
	//							+ " and  my_review_status = 1 and appraisal_details_id=? and publish_approve_deny_by > 0 and appraisal_freq_id = ? ");
						pst.setInt(1, uF.parseToInt(getEffectiveid()));
	//					pst.setInt(2, uF.parseToInt(getAppFreqId()));
	//					System.out.println("pst==>"+pst);
						rs = pst.executeQuery();
						while (rs.next()) {
							String strApproveMemTick = "";
							String strApproveMemReason = "";
							if (uF.parseToInt(rs.getString("publish_is_approved")) == 1) {
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
							} else if (uF.parseToInt(rs.getString("publish_is_approved")) == -1) {
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
							} else {
								strApproveMemTick = "<i class=\"fa fa-minus\" aria-hidden=\"true\" title=\"Waiting\"></i>";
							}
							if(rs.getString("publish_approve_deny_reason")!=null && !rs.getString("publish_approve_deny_reason").trim().equals("") && !rs.getString("publish_approve_deny_reason").trim().equalsIgnoreCase("NULL")){
								strApproveMemReason = "[Reason: "+rs.getString("publish_approve_deny_reason").trim()+"]";
							}
							
							String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("publish_approve_deny_by"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("publish_approve_deny_by"))), "") + ")" : "";
							sbReason.append(strApproveMemTick+hmEmployeeNameMap.get(rs.getString("publish_approve_deny_by").trim())+ strUserTypeName +strApproveMemReason);
						}
						rs.close();
						pst.close();	
					} else if(effectivetype.equals(WORK_FLOW_TERMINATION)){
						pst = con.prepareStatement("select * from emp_off_board where off_board_id=? and approved_1_by > 0");
						pst.setInt(1, uF.parseToInt(getEffectiveid()));
	//					System.out.println("pst==>"+pst);
						rs = pst.executeQuery();
						while (rs.next()) {
							String strApproveMemTick = "";
							String strApproveMemReason = "";
							if (uF.parseToInt(rs.getString("approved_1")) == 1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\" title=\"Approved\"></i>";
								
							} else if (uF.parseToInt(rs.getString("approved_1")) == -1){
								/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
								strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\" title=\"Denied\"></i>";
								
							}
							if(rs.getString("approved_1_reason")!=null && !rs.getString("approved_1_reason").trim().equals("") && !rs.getString("approved_1_reason").trim().equalsIgnoreCase("NULL")){
								strApproveMemReason = "[Reason: "+rs.getString("approved_1_reason").trim()+"]";
							}
							//System.out.println("strApproveMemTick==>"+strApproveMemTick+"==>strApproveMemReason==>"+strApproveMemReason);
							String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("approved_1"))) > 0 ? " ("+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("approved_1"))), "") + ")" : "";
							sbReason.append(strApproveMemTick+uF.showData(hmEmployeeNameMap.get(rs.getString("approved_1_by").trim()), "-")+ strUserTypeName +strApproveMemReason);
							//System.out.println("sbReason==>"+sbReason.toString());
						}
						rs.close();
						pst.close();	
					}
				}
				
				request.setAttribute("strReason", sbReason.toString());
			}
//			System.out.println("sbReason====>"+sbReason.toString()); 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String getEffectiveid() {
		return effectiveid;
	}

	public void setEffectiveid(String effectiveid) {
		this.effectiveid = effectiveid;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getAppFreqId() {
		return appFreqId;
	}

	public void setAppFreqId(String appFreqId) {
		this.appFreqId = appFreqId;
	}
}
