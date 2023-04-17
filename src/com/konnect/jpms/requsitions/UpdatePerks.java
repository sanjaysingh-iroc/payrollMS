package com.konnect.jpms.requsitions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdatePerks extends ActionSupport  implements ServletRequestAware, IStatements {
	
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	 
	private CommonFunctions CF;
	
	String strLeaveTypeId;
	String strEmpId;
	String strNod;
	String type;
	String mReason;
	String userType;
	String currUserType;
	
	public String execute() {
	
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		String strStatus = (String)request.getParameter("S");
		String strId = (String)request.getParameter("RID");
		String strType = (String)request.getParameter("T");
		String strMode = (String)request.getParameter("M");
		
		
		strLeaveTypeId = (String)request.getParameter("LTID");
		strEmpId = (String)request.getParameter("EMPID");
		strNod = (String)request.getParameter("NOD");
		
//		System.out.println("getType()=====>"+getType());
//		System.out.println("strId=====>"+strId);
//		System.out.println("(String)request.getParameter(RID)=====>"+(String)request.getParameter("RID"));
		if(strMode!=null && strMode.equalsIgnoreCase("reset")){
			resetRequest(strStatus, strId, strType);
		}else if(strMode!=null && strMode.equalsIgnoreCase("D")){
			deleteRequest(strStatus, strId, strType);
		}else{			
			updateRequest(strStatus, strId, strType, strMode);
			if(getType()!=null && getType().equals("type")){
//				System.out.println("in update getType()=====>"+getType());
				return DASHBOARD;
			}
		}
		
		return "success";
		
	}
	
	
	private void resetRequest(String strStatus, String strId, String strType) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,approve_date=?,status=? WHERE effective_id=?");
			pst.setInt(1, 0);
			pst.setDate(2, null);
			pst.setInt(3, 1);
			pst.setInt(4, uF.parseToInt(strId));
//			System.out.println("pst====>"+pst);
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
//		return nRequisitionId;
	} 


	public void updateRequest(String strStatus, String strId, String strType, String strMode){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nRequisitionId=0;
		
		try{
			
			con = db.makeConnection(con);
				
//				System.out.println("strMode===+>"+strMode);
				
				boolean flag = true;
				boolean flagAdmin = false;
				if(uF.parseToBoolean(CF.getIsWorkFlow())){
					
//					pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_PERK+"'" +
//						" and effective_id=? order by effective_id,member_position");
//					pst.setInt(1, uF.parseToInt(strId));
//					rs = pst.executeQuery();			
//					List<String> checkEmpList=new ArrayList<String>();
//					while(rs.next()){
//						checkEmpList.add(rs.getString("emp_id"));					
//					}
//					rs.close();
//					pst.close();
			
//					System.out.println("checkEmpList====>"+checkEmpList);
					
//					if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !checkEmpList.contains(strSessionEmpId) ){
					if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)){
						pst = con.prepareStatement("update emp_perks set approval_1=?, approval_1_emp_id=?, approval_1_date=?, approval_2=?, approval_2_emp_id=?, approval_2_date=?,approve_reason=?  where perks_id = ?");
						pst.setInt(1, uF.parseToInt(strStatus));
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt(strStatus));
						pst.setInt(5, uF.parseToInt(strSessionEmpId));
						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(7, getmReason());
						pst.setInt(8, uF.parseToInt(strId));
						pst.execute();
						pst.close();
						flagAdmin = true;
					}else{						
						
						pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? " +
								"and effective_type='"+WORK_FLOW_PERK+"' and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
						pst.setInt(1, uF.parseToInt(strId));
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
	                    pst.setInt(3, uF.parseToInt(getUserType()));
						rs = pst.executeQuery();
						int work_id=0;
						while(rs.next()){
							work_id=rs.getInt("work_flow_id");
							break;
						}
						rs.close();
						pst.close();
						
//							System.out.println("work_id===+>"+work_id);
						
						pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,approve_date=?,reason=? WHERE work_flow_id=?");
						pst.setInt(1, uF.parseToInt(strStatus));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(3, getmReason());
						pst.setInt(4, work_id);
						pst.execute();			
						pst.close();
		
						pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_PERK+"' " +
								" and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id=? " +
								" and is_approved=0 and effective_type='"+WORK_FLOW_PERK+"' and member_position not in " +
								" (select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_PERK+"' )) " +
								" order by work_flow_id");
						pst.setInt(1, uF.parseToInt(strId));
						pst.setInt(2, uF.parseToInt(strId));
						pst.setInt(3, uF.parseToInt(strId));
						rs = pst.executeQuery();
						while(rs.next()){
							flag=false;
						}
						rs.close();
						pst.close();
						
						if(flag){				
							pst = con.prepareStatement("update emp_perks set approval_1=?, approval_1_emp_id=?, approval_1_date=?, approval_2=?, approval_2_emp_id=?, approval_2_date=?,approve_reason=?  where perks_id = ?");
							pst.setInt(1, uF.parseToInt(strStatus));
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strStatus));
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setString(7, getmReason());
							pst.setInt(8, uF.parseToInt(strId));
							pst.execute();
							pst.close();
						} else if(uF.parseToInt(strStatus) == -1){				
							pst = con.prepareStatement("update emp_perks set approval_1=?, approval_1_emp_id=?, approval_1_date=?, approval_2=?, approval_2_emp_id=?, approval_2_date=?,approve_reason=?  where perks_id = ?");
							pst.setInt(1, uF.parseToInt(strStatus));
							pst.setInt(2, uF.parseToInt(strSessionEmpId));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt(strStatus));
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setString(7, getmReason());
							pst.setInt(8, uF.parseToInt(strId));
							pst.execute();
							pst.close();
						}
					}
				}else{
					if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))){
						pst = con.prepareStatement("update emp_perks set approval_1=?, approval_1_emp_id=?, approval_1_date=?, approval_2=?, approval_2_emp_id=?, approval_2_date=?,approve_reason=?  where perks_id = ?");
						pst.setInt(1, uF.parseToInt(strStatus));
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(4, uF.parseToInt(strStatus));
						pst.setInt(5, uF.parseToInt(strSessionEmpId));
						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(7, getmReason());
						pst.setInt(8, uF.parseToInt(strId));			
						pst.execute();
						pst.close();
						flagAdmin = true;
					}
				}
				
				
//				if(strMode!=null && (strMode.equalsIgnoreCase("AA") || strMode.equalsIgnoreCase("HRA"))){
//					PreparedStatement pst1 = con.prepareStatement("select * from emp_reimbursement where reimbursement_id = ?");
//					pst1.setInt(1, uF.parseToInt(strId));
//					ResultSet rs1 = pst1.executeQuery();
//					if(rs1.next()){
//						String strDomain = request.getServerName().split("\\.")[0];
//						Notifications nF = new Notifications(N_EMPLOYEE_REIMBURSEMENT_APPROVAL, CF);
//						nF.setDomain(strDomain);
//						nF.setStrEmpId(rs1.getString("emp_id"));
//						//nF.setStrHostAddress(request.getRemoteHost());
//						nF.setStrHostAddress(CF.getStrEmailLocalHost());
//						nF.setStrContextPath(request.getContextPath());
//						nF.setStrEmpReimbursementFrom(uF.getDateFormat(rs1.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
//						nF.setStrEmpReimbursementTo(uF.getDateFormat(rs1.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
//						nF.setStrEmpReimbursementPurpose(rs1.getString("reimbursement_purpose"));
//						nF.setStrEmpReimbursementType(rs1.getString("reimbursement_type"));
//						nF.setStrEmpReimbursementAmount(rs1.getString("reimbursement_amount"));
//						if(uF.parseToInt(strStatus)==1){
//							nF.setStrApprvedDenied("approved");	
//						}else if(uF.parseToInt(strStatus)==-1){
//							nF.setStrApprvedDenied("denied");
//						}
//						nF.sendNotifications();
//					}
//				}
				
//				System.out.println("flagAdmin ===>> " + flagAdmin + " -- flag ===>> " + flag + " -- strStatus ===>> " + strStatus);
				if(flagAdmin || flag || uF.parseToInt(strStatus)== -1){
					
					pst = con.prepareStatement("select * from emp_perks where perks_id = ?");
					pst.setInt(1, uF.parseToInt(strId));
					rs = pst.executeQuery();
//					System.out.println("pst ===>> " + pst);
					String strEmpId=null;
					String strAmount="";
					if(rs.next()) {
						strEmpId=rs.getString("emp_id");
						strAmount=rs.getString("perk_amount");
					}
					rs.close();
					pst.close();
//					System.out.println("strEmpId ===>> " + strEmpId + " -- strAmount ===>> " + strAmount);
					
					Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
					if(hmUserTypeId == null) hmUserTypeId = new HashMap<String, String>();
					
					String strDomain = request.getServerName().split("\\.")[0];
					String strApproveDeny = "approved";
					if(uF.parseToInt(strStatus)== -1) {
						strApproveDeny = "denied";
					}
					String alertData = "<div style=\"float: left;\"> Your Perk ("+strAmount+") has been "+strApproveDeny+" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "MyPay.action?pType=WR&callFrom=NotiPerkApprove";
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(strEmpId);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
//					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//					userAlerts.setStrDomain(strDomain);
//					userAlerts.setStrEmpId(strEmpId);
//					userAlerts.set_type(PERK_APPROVAL_ALERT);
//					userAlerts.setStatus(UPDATE_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
					
					
					List<String> alAccountant = CF.getEmpAccountantList(con,uF,strEmpId,hmUserTypeId);
					if(alAccountant == null) alAccountant = new ArrayList<String>();
					if(alAccountant.size() > 0){
						int nAccountant = alAccountant.size();
						for(int i = 0; i < nAccountant; i++){
							String strAccountant = alAccountant.get(i);
//							System.out.println("strAccountant ===>> " + strAccountant);
							alertData = "<div style=\"float: left;\"> Perk, <b>"+CF.getEmpNameMapByEmpId(con, strEmpId)+"</b> has been approved by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. Please Check. </div>";
							alertAction = "Pay.action?pType=WR&callFrom=PaidUnpaidPerks";
							userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strAccountant);
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(ACCOUNTANT));
							userAlerts.setStatus(INSERT_WR_ALERT);
							t = new Thread(userAlerts);
							t.run();
						}
					} else {
						List<String> alGlobalHR = CF.getGlobalHRList(con,uF,hmUserTypeId);
						if(alGlobalHR == null) alGlobalHR = new ArrayList<String>();
						int nGlobalHR = alGlobalHR.size();
						for(int i = 0; i < nGlobalHR; i++){
							String strGlobalHR = alGlobalHR.get(i);
//							System.out.println("strGlobalHR ===>> " + strGlobalHR);
							alertData = "<div style=\"float: left;\"> Perk, <b>"+CF.getEmpNameMapByEmpId(con, strEmpId)+"</b> has been approved by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. PLease Check. </div>";
							alertAction = "Pay.action?pType=WR&callFrom=PaidUnpaidPerks";
							userAlerts=new UserAlerts(con, uF, CF, request);
							userAlerts.setStrDomain(strDomain);
							userAlerts.setStrEmpId(strGlobalHR);
							userAlerts.setStrData(alertData);
							userAlerts.setStrAction(alertAction);
							userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
							userAlerts.setStatus(INSERT_WR_ALERT);
							t = new Thread(userAlerts);
							t.run();
						}
					}					
				}
				getStatusMessage(uF.parseToInt(strStatus));
				
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
//		return nRequisitionId;
	}
	
	
	public void deleteRequest(String strStatus, String strId, String strType){
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nRequisitionId=0;
		
		try{
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from emp_perks where perks_id = ?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			getStatusMessage(uF.parseToInt(strStatus));
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
//		return nRequisitionId;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	public void getStatusMessage(int nStatus){
		
		switch(nStatus){
		
		case -1:
			 /*request.setAttribute("STATUS_MSG", "<img title=\"Denied\" src=\""+request.getContextPath()+"/images1/icons/denied.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Denied\" ></i>");
			
			break;
			
		case 0:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pending\" src=\""+request.getContextPath()+"/images1/icons/pending.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Pending\"></i>");
			
			break;
			
		case 1:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Approved\" src=\""+request.getContextPath()+"/images1/icons/approved.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
			
			break;
			
		case 2:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pulled\" src=\""+request.getContextPath()+"/images1/icons/pullout.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#ea9900\" title=\"Pulled\"></i>");
			
			break;
			
		case 3:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Submited\" src=\""+request.getContextPath()+"/images1/icons/re_submit.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Submited\"></i>");
			
			break;
		}
	}
	
	
	public void getNotificationStatusMessage(int nStatus, String str){
		
		switch(nStatus){
		
		case 0:
			request.setAttribute("STATUS_MSG", "<img width=\"20px\" title=\"Pending\" src=\""+request.getContextPath()+"/images1/"+(("E".equalsIgnoreCase(str))?"mail_disbl.png":"mob_disbl.png")+"\" border=\"0\">&nbsp;");
			break;
			
		case 1:
			request.setAttribute("STATUS_MSG", "<img width=\"20px\" title=\"Approved\" src=\""+request.getContextPath()+"/images1/"+(("E".equalsIgnoreCase(str))?"mail_enbl.png":"mob_enbl.png")+"\" border=\"0\">&nbsp;");
			break;
		}
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getmReason() {
		return mReason;
	}

	public void setmReason(String mReason) {
		this.mReason = mReason;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}
	
}
