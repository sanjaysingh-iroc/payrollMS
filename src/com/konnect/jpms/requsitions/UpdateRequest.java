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
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class UpdateRequest extends ActionSupport  implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2009608472005407630L;

	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null;
	 
	private CommonFunctions CF;
	
	private String strLeaveTypeId;
	private String strEmpId;
	private String strNod;
	
	private String strStatus;
	private String strId;
	private String strType;
	private String strMode;
	private String operation;
	private String approveDenyReason;
	private String type;
	private String userType;
	private String from;
	
	private String strDivResult;
	private String currUserType;
	private String strReason;
	
	public String execute() {
	
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN;
		
		/*UtilityFunctions uF = new UtilityFunctions();
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		setStrStatus((String)request.getParameter("S"));
		setStrId((String)request.getParameter("RID"));
		setStrType((String)request.getParameter("T"));
		setStrMode((String)request.getParameter("M"));
		
//		System.out.println("status==>"+getStrStatus()+"==>approveType==>"+getStrMode()+"==>offBoardId==>"+getStrId()+"==>userType==>"+getUserType());
//		System.out.println("type==>"+getStrType());
		
		strLeaveTypeId = (String)request.getParameter("LTID");
		strEmpId = (String)request.getParameter("EMPID");
		strNod = (String)request.getParameter("NOD");
		
		System.out.println("status==>"+getStrStatus()+"==>StrId==>"+getStrId()+"==>strType==>"+getStrType()+"==>strMode==>"+getStrMode()+" ===>operation==>"+operation);
		
		if(strMode!=null && strMode.equalsIgnoreCase("VIEW")) {
			viewCancelReason(strId, strType);
			return LOAD;
		} else if(strMode!=null && strMode.equalsIgnoreCase("D")) {
			deleteRequest(strStatus, strId, strType);
		} else {
			if(strType != null && strType.equals("REG") && operation != null && !operation.equals("")) {
				
				updateRequest(strStatus, strId, strType, strMode);
				if(getType()!=null && getType().equals("EA")) {
					return VIEW;
				} else if(getType()!=null && getType().equals("type")){
					return DASHBOARD;
				}
			} else if(strType != null && !strType.equals("REG")) {
				updateRequest(strStatus, strId, strType, strMode);
				if(getType()!=null && getType().equals("type")){
					return DASHBOARD;
				}
			}
		}
		
		
		if(strType != null && strType.equals("REG")) {
			if(operation == null || operation.equals("")) {
				return LOAD;
			} else if(getFrom()!=null && getFrom().equalsIgnoreCase("EA")){
				return VIEW;
			}else{
//				System.out.println("UPDATE ============>> " + UPDATE);
				return UPDATE;
				
			}
		} else {
			return SUCCESS;
		}
		
	}
	
	
	private void viewCancelReason(String strId, String strType) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
//			System.out.println("in viewCancelReason ... ");
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			if(strType!=null && strType.equalsIgnoreCase("RIM")) {
				pst = con.prepareStatement("select cancel_reason,cancel_date,cancel_by from emp_reimbursement where reimbursement_id =?");
				pst.setInt(1, uF.parseToInt(strId));
//				System.out.println("pst ==========>> " + pst);
				rs = pst.executeQuery();
				List<String> alData = new ArrayList<String>();
				while(rs.next()) {
					alData.add(hmEmpName.get(rs.getString("cancel_by")));
					alData.add(uF.getDateFormat(rs.getString("cancel_date"), DBDATE, DATE_FORMAT_STR));
					alData.add(rs.getString("cancel_reason"));
				}
				rs.close();
				pst.close();
//				System.out.println("alData ===>> " + alData);
				request.setAttribute("alData", alData);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}


	public int updateRequest(String strStatus, String strId, String strType, String strMode){
		
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nRequisitionId=0;
		
		try{
			
			con = db.makeConnection(con);
		
			Map<String, String> hmLeaveType = CF.getLeaveTypeCode(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			if(strType!=null && strType.equalsIgnoreCase("RIM")){
				
				if(strMode!=null && strMode.equalsIgnoreCase("AA")){
					pst = con.prepareStatement("update emp_reimbursement set approval_1=?, approval_1_emp_id=?, approval_1_date=?, approval_2=?, approval_2_emp_id=?, approval_2_date=?  where reimbursement_id = ?");
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(strStatus));
					pst.setInt(5, uF.parseToInt(strSessionEmpId));
					pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(7, uF.parseToInt(strId));
					pst.execute();
					pst.close();
				}else if(strMode!=null && strMode.equalsIgnoreCase("MA")){
					pst = con.prepareStatement("update emp_reimbursement set approval_1=?, approval_1_emp_id=?, approval_1_date=?,approval_2=?, approval_2_emp_id=?, approval_2_date=? where reimbursement_id = ?");
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(strStatus));
					pst.setInt(5, uF.parseToInt(strSessionEmpId));
					pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(7, uF.parseToInt(strId));
					pst.execute();
					pst.close();
				}else if(strMode!=null && strMode.equalsIgnoreCase("HRA")){
					pst = con.prepareStatement("update emp_reimbursement set approval_1=?, approval_1_emp_id=?, approval_1_date=?,approval_2=?, approval_2_emp_id=?, approval_2_date=?  where reimbursement_id = ?");
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(strStatus));
					pst.setInt(5, uF.parseToInt(strSessionEmpId));
					pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(7, uF.parseToInt(strId));
					pst.execute();
					pst.close();
				}else if(strMode!=null && strMode.equalsIgnoreCase("PAY")){
					pst = con.prepareStatement("update emp_reimbursement set ispaid=?, paid_date=?, paid_by=?  where reimbursement_id = ?");
					pst.setBoolean(1, true);
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setInt(4, uF.parseToInt(strId));
					pst.execute();
					pst.close();
				}
				
				
//				System.out.println("pst===+>"+pst);
				
				if(strMode!=null && (strMode.equalsIgnoreCase("AA") || strMode.equalsIgnoreCase("HRA"))){
					Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
					if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
//					Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
					Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetailsForPDF(con);
					if(hmCurrency == null) hmCurrency = new HashMap<String, Map<String,String>>();
					
					pst1 = con.prepareStatement("select * from emp_reimbursement where reimbursement_id = ?");
					pst1.setInt(1, uF.parseToInt(strId));
					rs1 = pst1.executeQuery();
					if(rs1.next()){
						String strCurrId = hmEmpCurrency.get(rs1.getString("emp_id"));
						Map<String, String> hmCurrencyInner = hmCurrency.get(strCurrId);
						if (hmCurrencyInner == null)hmCurrencyInner = new HashMap<String, String>();
						String strCurrSymbol = hmCurrencyInner.get("SHORT_CURR");
						
						String strDomain = request.getServerName().split("\\.")[0];
						Notifications nF = new Notifications(N_EMPLOYEE_REIMBURSEMENT_APPROVAL, CF);
						nF.setDomain(strDomain);
						nF.request = request;
						nF.session = session;
						nF.setStrEmpId(rs1.getString("emp_id"));
//						nF.setStrHostAddress(request.getRemoteHost());
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrEmpReimbursementFrom(uF.getDateFormat(rs1.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
						nF.setStrEmpReimbursementTo(uF.getDateFormat(rs1.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
						nF.setStrEmpReimbursementPurpose(rs1.getString("reimbursement_purpose"));
						nF.setStrEmpReimbursementType(rs1.getString("reimbursement_type"));
						nF.setStrEmpReimbursementAmount(rs1.getString("reimbursement_amount"));
						nF.setStrEmpReimbursementDate(uF.getDateFormat(rs1.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
						nF.setStrEmpReimbursementCurrency(strCurrSymbol);
						
						if(uF.parseToInt(strStatus)==1){
							nF.setStrApprvedDenied("approved");	
						}else if(uF.parseToInt(strStatus)==-1){
							nF.setStrApprvedDenied("denied");
						}
						nF.setEmailTemplate(true);
						nF.sendNotifications();
					}
					rs1.close();
					pst1.close();
				}
				
				getStatusMessage(uF.parseToInt(strStatus));
				
			} else if(strType!=null && strType.equalsIgnoreCase("LER")) {
				
				
				if(strMode!=null && strMode.equalsIgnoreCase("HRA")) {
					pst = con.prepareStatement("update emp_reimbursement set approval_2=?, approval_2_emp_id=?, approval_2_date=?  where reimbursement_id = ?");
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(strId));
					pst.execute();
					pst.close();
				} else if(strMode!=null && strMode.equalsIgnoreCase("PAY")) {
					pst = con.prepareStatement("update emp_reimbursement set approval_2=?, approval_2_emp_id=?, approval_2_date=?  where reimbursement_id = ?");
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(strId));
					pst.execute();
					pst.close();
				}
				
				getStatusMessage(uF.parseToInt(strStatus));
				
			} else if(strType!=null && strType.equalsIgnoreCase("LC")) { // Leave Cancelled
				pst = con.prepareStatement("update emp_leave_entry set is_approved=-2  where leave_id = ?");
				pst.setInt(1, uF.parseToInt(strId));
				int x = pst.executeUpdate();
				pst.close();
				
				if(x > 0) {
					getStatusMessage(uF.parseToInt(strStatus));
					
					pst=con.prepareStatement("select * from emp_leave_entry where leave_id=?");
					pst.setInt(1,uF.parseToInt(strId));
					rs=pst.executeQuery();
					int nEmpId = 0;
					boolean isHalfDay = false;
					String strLeaveFrom = null;
					String strLeaveTo = null;
					String strLeaveTypeId = null;
					String strIsCompensate = null;
					while(rs.next()) {
						nEmpId = uF.parseToInt(rs.getString("emp_id"));
						isHalfDay = uF.parseToBoolean(rs.getString("ishalfday"));
						strLeaveFrom = uF.getDateFormat(rs.getString("approval_from"), DBDATE, DATE_FORMAT);
						strLeaveTo = uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, DATE_FORMAT);
						strLeaveTypeId = rs.getString("leave_type_id");
						strIsCompensate = rs.getString("is_compensate");
					}
					rs.close();
					pst.close();
					
					List<String> alManagers = new ArrayList<String>();
					pst=con.prepareStatement("select distinct(wfd.emp_id) from emp_leave_entry ele,work_flow_details wfd where ele.leave_id = wfd.effective_id " +
							"and ele.leave_id=? and wfd.effective_type=?");
					pst.setInt(1,uF.parseToInt(strId));
					pst.setString(2,WORK_FLOW_LEAVE);
					rs=pst.executeQuery();
					while(rs.next()) {
						if(uF.parseToInt(rs.getString("emp_id")) > 0 && !alManagers.contains(rs.getString("emp_id"))) {
							alManagers.add(rs.getString("emp_id"));
						}
					}
					rs.close();
					pst.close();
					
					String strSupervisorName = CF.getEmpNameMapByEmpId(con, (String)session.getAttribute(EMPID));
					
					if(nEmpId > 0){
						pst = con.prepareStatement("select * from leave_type where leave_type_id=?");
						pst.setInt(1, uF.parseToInt(strLeaveTypeId));
						rs = pst.executeQuery();
						String strLeaveTypeName = null;
						while(rs.next()){
							strLeaveTypeName = rs.getString("leave_type_name");
						}
						rs.close();
						pst.close();
						
						String strDomain = request.getServerName().split("\\.")[0];
						
						String strLeaveNotiLbl = N_EMPLOYEE_LEAVE_PULLOUT+"";
						if(uF.parseToBoolean(strIsCompensate)) {
							strLeaveNotiLbl = N_EMPLOYEE_EXTRA_WORK_PULLOUT+"";
						}
						if(TRAVEL_LEAVE == uF.parseToInt(strLeaveTypeId)) {
							strLeaveNotiLbl = N_EMPLOYEE_TRAVEL_PULLOUT+"";
						}
						
						for(int i=0; alManagers!=null && i<alManagers.size();i++) {
							Notifications nF = new Notifications(uF.parseToInt(strLeaveNotiLbl), CF);
							nF.setDomain(strDomain);
							nF.request = request;
							nF.session = session;
							nF.setStrHostAddress(CF.getStrEmailLocalHost());
							nF.setStrHostPort(CF.getStrHostPort());
							nF.setStrContextPath(request.getContextPath());
							nF.setStrEmpId(""+nEmpId);
							nF.setSupervisor(false);
							nF.setEmailTemplate(true);
							
							pst = con.prepareStatement(selectEmpDetails1);
							pst.setInt(1, uF.parseToInt((String)alManagers.get(i)));
							rs = pst.executeQuery();
							boolean flg=false;
							while(rs.next()){
								nF.setStrSupervisorEmail(rs.getString("emp_email"));					
								
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
								
								nF.setStrSupervisorName(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
								nF.setStrSupervisorContactNo(rs.getString("emp_contactno"));
								if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0){
									nF.setStrEmpEmail(rs.getString("emp_email_sec"));
									nF.setStrEmailTo(rs.getString("emp_email_sec"));
								} else {
									nF.setStrEmpEmail(rs.getString("emp_email"));
									nF.setStrEmailTo(rs.getString("emp_email"));
								}
								flg=true;
							}
							rs.close();
							pst.close();
							
							if(flg){
								
								if(isHalfDay){
									nF.setStrEmpLeaveFrom(uF.getDateFormat(strLeaveFrom, DATE_FORMAT, CF.getStrReportDateFormat()));
									nF.setStrEmpLeaveTo(uF.getDateFormat(strLeaveFrom, DATE_FORMAT, CF.getStrReportDateFormat()));
									nF.setStrEmpLeaveNoOfDays("0.5");	
								}else{
									nF.setStrEmpLeaveFrom(uF.getDateFormat(strLeaveFrom, DATE_FORMAT, CF.getStrReportDateFormat()));
									nF.setStrEmpLeaveTo(uF.getDateFormat(strLeaveTo, DATE_FORMAT, CF.getStrReportDateFormat()));
									nF.setStrEmpLeaveNoOfDays(uF.dateDifference(strLeaveFrom,DATE_FORMAT, strLeaveTo, DATE_FORMAT,CF.getStrTimeZone()));
								}
								nF.setStrManagerName(uF.showData(strSupervisorName, ""));
								nF.setStrLeaveTypeName(uF.showData(strLeaveTypeName, ""));
								nF.setStrEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT_STR));
								nF.sendNotifications();
							}
						}
					}
					
				}
			}
//			else if(strType!=null && strType.equalsIgnoreCase("LE")){ // Leave Encashment Approval/Denial
//				if(uF.parseToInt(strStatus)==1){
//					PreparedStatement pst2 = con.prepareStatement("select * from emp_leave_entry where leave_id=?");
//					pst2.setInt(1, uF.parseToInt(strId));
//					rs = pst2.executeQuery();
//					double dblNoOFLeaves = 0;
//					String leave_from=null;
//					String leave_to=null;
//					int leave_type_id=0;
//					while(rs.next()){
//						leave_from=rs.getString("leave_from");
//						leave_to=rs.getString("leave_to");
//						dblNoOFLeaves=rs.getDouble("emp_no_of_leave");
//						leave_type_id=rs.getInt("leave_type_id");
//					}
//					
//					pst = con.prepareStatement("update emp_leave_entry set is_approved=?, user_id=?,approval_from=?,approval_to_date=?  where leave_id = ?");
//					pst.setInt(1, uF.parseToInt(strStatus));
//					pst.setInt(2, uF.parseToInt(strSessionEmpId));
//					pst.setDate(3, uF.getDateFormat(leave_from, DBDATE));
//					pst.setDate(4, uF.getDateFormat(leave_to, DBDATE));
//					pst.setInt(5, uF.parseToInt(strId));
//
//					getStatusMessage(uF.parseToInt(strStatus));
//					
//					PreparedStatement pst1 = con.prepareStatement("select * from leave_register1 where emp_id = ? and leave_type_id=? and _date <= ? order by register_id desc limit 1");
//					pst1.setInt(1, uF.parseToInt(strEmpId));
//					pst1.setInt(2, leave_type_id);
//					pst1.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//					rs = pst1.executeQuery();
//					double dblTakenPaid = 0;
//					double dblTakenUnPaid = 0;   
//					double dblTotalBalance = 0;
//					double dblTotalAccrued = 0;
//					while (rs.next()) {
//						dblTotalBalance = uF.parseToDouble(rs.getString("balance"));
//						dblTakenPaid = uF.parseToDouble(rs.getString("taken_paid"));
//						dblTakenUnPaid = uF.parseToDouble(rs.getString("taken_unpaid"));
//						dblTotalAccrued = uF.parseToDouble(rs.getString("accrued"));
//					}
//
//					
//					pst1 = con.prepareStatement("insert into leave_register1 (taken_paid, taken_unpaid, balance, emp_id,leave_type_id, _date, accrued) values (?,?,?,?,?,?,?)");
//					pst1.setDouble(1, (dblTakenPaid + uF.parseToDouble(strNod)));
//					pst1.setDouble(2, dblTakenUnPaid);
//					pst1.setDouble(3, (dblTotalBalance - uF.parseToDouble(strNod)));
//					pst1.setInt(4, uF.parseToInt(strEmpId));
//					pst1.setInt(5, leave_type_id);
//					pst1.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst1.setDouble(7, dblTotalAccrued);
//					pst1.execute();
//					
//				}else{
//					pst = con.prepareStatement("update emp_leave_entry set is_approved=?, user_id=? where leave_id = ?");
//					pst.setInt(1, uF.parseToInt(strStatus));
//					pst.setInt(2, uF.parseToInt(strSessionEmpId));
//					pst.setInt(3, uF.parseToInt(strId));
//
//					getStatusMessage(uF.parseToInt(strStatus));
//				}
//				
//			}
			else if(strType!=null && strType.equalsIgnoreCase("REG")) { // Resignation Approval / Denial
				
//				if(strMode!=null && strMode.equalsIgnoreCase("1")) {
//					pst = con.prepareStatement("update emp_off_board set approved_1=?, approved_1_date=?, approved_1_by=?,approved_1_reason=? where off_board_id = ?");
//					pst.setInt(1, uF.parseToInt(strStatus));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setInt(3, uF.parseToInt(strSessionEmpId));
//					pst.setString(4, getApproveDenyReason());
//					pst.setInt(5, uF.parseToInt(strId));
//					pst.execute();
//					pst.close();
//				} else if(strMode!=null && strMode.equalsIgnoreCase("2")) {
//					pst = con.prepareStatement("update emp_off_board set approved_2=?, approved_2_date=?, approved_2_by=?,approved_2_reason=? where off_board_id = ?");
//					pst.setInt(1, uF.parseToInt(strStatus));
//					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//					pst.setInt(3, uF.parseToInt(strSessionEmpId));
//					pst.setString(4, getApproveDenyReason());
//					pst.setInt(5, uF.parseToInt(strId));
//					pst.execute();
//					pst.close();
//				}
				
					String empId = "";
					String empStatus = "";
					pst = con.prepareStatement("select emp_id, previous_emp_status from emp_off_board where off_board_id = ?");
					pst.setInt(1, uF.parseToInt(getStrId()));
					rs = pst.executeQuery();			
					while(rs.next()) {
						empId = rs.getString("emp_id");
						empStatus = rs.getString("previous_emp_status");
					}
					rs.close();
					pst.close();
					
//					System.out.println("empId==>"+empId+"==>empStatus==>"+empStatus);
					
					boolean approveFlag = false;
				if(uF.parseToBoolean(CF.getIsWorkFlow())) {
					
//					pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_RESIGN+"'" +
//						" and effective_id=? order by effective_id,member_position");
//					pst.setInt(1, uF.parseToInt(strId));
//					rs = pst.executeQuery();			
//					List<String> checkEmpList=new ArrayList<String>();
//					while(rs.next()){
//						checkEmpList.add(rs.getString("emp_id"));					
//					}
//					rs.close();
//					pst.close();
//					if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !checkEmpList.contains(strSessionEmpId) ){
					
					if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
						pst = con.prepareStatement("update emp_off_board set approved_1=?, approved_1_date=?, approved_1_by=?,approved_1_reason=?, approved_2=?, approved_2_date=?, approved_2_by=?,approved_2_reason=?,off_board_type = ?  where off_board_id = ?");
						pst.setInt(1, uF.parseToInt(strStatus));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(3, uF.parseToInt(strSessionEmpId));
						pst.setString(4, getApproveDenyReason());
						pst.setInt(5, uF.parseToInt(strStatus));
						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(7, uF.parseToInt(strSessionEmpId));
						pst.setString(8, getApproveDenyReason());
						if(strStatus!= null && uF.parseToInt(strStatus) == 1) {
							pst.setString(9, "RESIGNED");
						} else {
							pst.setString(9, empStatus);
						}
						pst.setInt(10, uF.parseToInt(strId));
						int x = pst.executeUpdate();
						pst.close();
						if(x>0) {
							approveFlag = true;
						}
					} else {
						pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? " +
								"and effective_type='"+WORK_FLOW_RESIGN+"' and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
						pst.setInt(1, uF.parseToInt(strId));
						pst.setInt(2, uF.parseToInt(strSessionEmpId));
	                    pst.setInt(3, uF.parseToInt(getUserType()));
//	                    System.out.println("pst ===>> " + pst);
						rs = pst.executeQuery();
						int work_id=0;
						while(rs.next()){
							work_id=rs.getInt("work_flow_id");
							break;
						}
						rs.close();
						pst.close();
//						System.out.println("work_id ===>> " + work_id);
						
						pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,approve_date=?,reason=? WHERE work_flow_id=?");
						pst.setInt(1, uF.parseToInt(strStatus));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setString(3, getApproveDenyReason());
						pst.setInt(4, work_id);
//						System.out.println("pst1==>"+pst);
						pst.execute();			
						pst.close();  
		
						
						boolean flag = true;
						pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_RESIGN+"' " +
								" and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id=? " +
								" and is_approved=0 and effective_type='"+WORK_FLOW_RESIGN+"' and member_position not in " +
								" (select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_RESIGN+"' )) " +
								" order by work_flow_id");
						pst.setInt(1, uF.parseToInt(strId));
						pst.setInt(2, uF.parseToInt(strId));
						pst.setInt(3, uF.parseToInt(strId));
//						System.out.println("pst2==>"+pst);
						rs = pst.executeQuery();
						while(rs.next()) {
							flag=false;
						}
						rs.close();
						pst.close();
						
					
						if(flag) {
							pst = con.prepareStatement("update emp_off_board set approved_1=?, approved_1_date=?, approved_1_by=?,approved_1_reason=?, approved_2=?, approved_2_date=?, approved_2_by=?,approved_2_reason=?, off_board_type = ?  where off_board_id = ?");
							pst.setInt(1, uF.parseToInt(strStatus));
							pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(3, uF.parseToInt(strSessionEmpId));
							pst.setString(4, getApproveDenyReason());
							pst.setInt(5, uF.parseToInt(strStatus));
							pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(7, uF.parseToInt(strSessionEmpId));
							pst.setString(8, getApproveDenyReason());
							if(strStatus!= null && uF.parseToInt(strStatus) == 1) {
								pst.setString(9, "RESIGNED");
							} else {
								pst.setString(9, empStatus);
							}
							pst.setInt(10, uF.parseToInt(strId));
//							System.out.println("pst3==>"+pst);
							int x = pst.executeUpdate();
							pst.close();
							if(x>0) {
								approveFlag = true;
							}
						} else if(uF.parseToInt(strStatus) == -1) {
							pst = con.prepareStatement("update emp_off_board set approved_1=?, approved_1_date=?, approved_1_by=?,approved_1_reason=?, approved_2=?, approved_2_date=?, approved_2_by=?,approved_2_reason=?, off_board_type = ? where off_board_id = ?");
							pst.setInt(1, uF.parseToInt(strStatus));
							pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(3, uF.parseToInt(strSessionEmpId));
							pst.setString(4, getApproveDenyReason());
							pst.setInt(5, uF.parseToInt(strStatus));
							pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(7, uF.parseToInt(strSessionEmpId));
							pst.setString(8, getApproveDenyReason());
							if(strStatus!= null && uF.parseToInt(strStatus) == 1) {
								pst.setString(9, "RESIGNED");
							}else {
								pst.setString(9, empStatus);
							}
							pst.setInt(10, uF.parseToInt(strId));
//							System.out.println("pst4==>"+pst);
							int x = pst.executeUpdate();
							pst.close();
							if(x>0) {
								approveFlag = true;
							}
						}
					}
				} else {
					if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) {
						pst = con.prepareStatement("update emp_off_board set approved_1=?, approved_1_date=?, approved_1_by=?,approved_1_reason=?, approved_2=?, approved_2_date=?, approved_2_by=?,approved_2_reason=?, off_board_type = ?  where off_board_id = ?");
						pst.setInt(1, uF.parseToInt(strStatus));
						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(3, uF.parseToInt(strSessionEmpId));
						pst.setString(4, getApproveDenyReason());
						pst.setInt(5, uF.parseToInt(strStatus));
						pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(7, uF.parseToInt(strSessionEmpId));
						pst.setString(8, getApproveDenyReason());
						
						if(strStatus!= null && uF.parseToInt(strStatus) == 1) {
							pst.setString(9, "RESIGNED");
						}else {
							pst.setString(9, empStatus);
						}
						pst.setInt(10, uF.parseToInt(strId));
//						System.out.println("pst5==>"+pst);
						int x = pst.executeUpdate();
						pst.close();
						if(x>0) {
							approveFlag = true;
						}
					}
				}
				
				
				if(approveFlag) {
					String strDomain = request.getServerName().split("\\.")[0];
					Notifications nF = new Notifications(N_EMPLOYEE_RESIGNATION_APPROVAL, CF);
					nF.setDomain(strDomain);
					nF.request = request;
					nF.setStrHostAddress(CF.getStrEmailLocalHost());
					nF.setStrHostPort(CF.getStrHostPort());
					nF.setStrContextPath(request.getContextPath());
					nF.setStrEmpId(empId);
					nF.setSupervisor(false);
					nF.setEmailTemplate(true);
					
					pst = con.prepareStatement(selectEmpDetails1);
					pst.setInt(1, uF.parseToInt(strSessionEmpId));
					rs = pst.executeQuery();
					boolean flg=false;
					while(rs.next()) {
						nF.setStrSupervisorEmail(rs.getString("emp_email"));					
						
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
					
						
						nF.setStrSupervisorName(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
						nF.setStrSupervisorContactNo(rs.getString("emp_contactno"));
						if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0) {
							nF.setStrEmpEmail(rs.getString("emp_email_sec"));
							nF.setStrEmailTo(rs.getString("emp_email_sec"));
						} else {
							nF.setStrEmpEmail(rs.getString("emp_email"));
							nF.setStrEmailTo(rs.getString("emp_email"));
						}
						flg=true;
					}
					if(flg) {
						if(uF.parseToInt(strStatus) == -1) {
							nF.setStrApprvedDenied("Denied");
						} else {
							nF.setStrApprvedDenied("Approved");
						}
						nF.setStrManagerComment(getApproveDenyReason());
						nF.sendNotifications();
					}
				}
				
				pst = con.prepareStatement("update employee_personal_details set emp_status = ? where emp_per_id = ?");
				if(getStrStatus()!= null && uF.parseToInt(getStrStatus()) == 1) {
					pst.setString(1, "RESIGNED");
				}else {
					pst.setString(1, empStatus);
				}
				pst.setInt(2, uF.parseToInt(empId));
				pst.executeUpdate();
				pst.close();
				
				getStatusMessage(uF.parseToInt(strStatus));
				
				
			} else if(strType!=null && strType.equalsIgnoreCase("EPD")) { // Pending Employee Approval
				
				pst = con.prepareStatement("select * from level_details ld, grades_details gd, designation_details dd where dd.level_id = ld.level_id and dd.designation_id = gd.designation_id and grade_id = (select grade_id from employee_official_details where emp_id = ?)");
				pst.setInt(1, uF.parseToInt(strId));
				rs = pst.executeQuery();
				int nLevelId = 0;
				while(rs.next()) {
					nLevelId = rs.getInt("level_id");
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("update employee_personal_details set approved_flag=?, is_alive=? where emp_per_id = ?");
				pst.setBoolean(1, true);
				pst.setBoolean(2, true);
				pst.setInt(3, uF.parseToInt(strId));
				pst.execute();
				pst.close();
//				AddEmployee objAE = new AddEmployee();
//				objAE.insertEmpActivity(con,strId, CF, strSessionEmpId);  
				
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_NEW_EMPLOYEE, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.session = session;
				nF.setStrEmpId(strId);
//				nF.setStrHostAddress(request.getRemoteHost());
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setEmailTemplate(true);
				nF.sendNotifications();
				
				
				getStatusMessage(uF.parseToInt(strStatus));
				
				CF.insertLeaveRegisterNewEmployee(con, nLevelId+"", strId+"", CF);
				
				
			} else if(strType!=null && strType.equalsIgnoreCase("NOT")) { // Notification status 
				
				if(strMode!=null && strMode.equalsIgnoreCase("EMAIL")){
					pst = con.prepareStatement("update notifications set isemail=? where notification_id = ?");
					pst.setBoolean(1, uF.parseToBoolean(strStatus));
					pst.setInt(2, uF.parseToInt(strId));
//					System.out.println("pst=====>"+pst);
					pst.execute();
					pst.close();
					getNotificationStatusMessage(uF.parseToInt(strStatus), "E");
				}else{
					pst = con.prepareStatement("update notifications set istext=? where notification_id = ?");
					pst.setBoolean(1, uF.parseToBoolean(strStatus));
					pst.setInt(2, uF.parseToInt(strId));
					pst.execute();
					pst.close();
					getNotificationStatusMessage(uF.parseToInt(strStatus), "T");
				}
				
			}else if(strType!=null && strType.equalsIgnoreCase("RP")){ // Roster Policy
				
				
					pst = con.prepareStatement("update roster_policy set policy_status=?, entry_date=? where roster_policy_id = ?");
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(3, uF.parseToInt(strId));
					pst.execute();
					pst.close();
					getStatusMessage1(uF.parseToInt(strStatus));
			}else if(strType!=null && strType.equalsIgnoreCase("RPHD")){ // Roster Policy Half day 
				
				pst = con.prepareStatement("update roster_halfday_policy set policy_status=?, entry_date=? where roster_hd_policy_id = ?");
				pst.setInt(1, uF.parseToInt(strStatus));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3, uF.parseToInt(strId));
				pst.execute();
				pst.close();
				getStatusMessage1(uF.parseToInt(strStatus));
			}else if(strType!=null && strType.equalsIgnoreCase("RPFD")){ // Roster Policy Full day 
				
				pst = con.prepareStatement("update roster_fullday_policy set policy_status=?, entry_date=? where roster_full_policy_id = ?");
				pst.setInt(1, uF.parseToInt(strStatus));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3, uF.parseToInt(strId));
				pst.execute();
				pst.close();
				getStatusMessage1(uF.parseToInt(strStatus));
			} else if(strType!=null && strType.equalsIgnoreCase("RPHDFDMinHrs")) { // Roster Policy Half Day Full day Min Hours
				
				pst = con.prepareStatement("update roster_halfday_fullday_hrs_policy set policy_status=?,update_date=?,updated_by=? where roster_halfday_fullday_hrs_id=?");
				pst.setInt(1, uF.parseToInt(strStatus));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(3, uF.parseToInt(strSessionEmpId));
				pst.setInt(4, uF.parseToInt(strId));
				pst.execute();
				pst.close();
				getStatusMessage1(uF.parseToInt(strStatus));
				
			} else if(strType!=null && strType.equalsIgnoreCase("PERK")){ // Employee Perk Details 
				
				
				if(strMode!=null && strMode.equalsIgnoreCase("AA")){
					pst = con.prepareStatement("update emp_perks set approval_1=?, approval_1_emp_id=?, approval_1_date=?, approval_2=?, approval_2_emp_id=?, approval_2_date=?  where perks_id = ?");
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(strStatus));
					pst.setInt(5, uF.parseToInt(strSessionEmpId));
					pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(7, uF.parseToInt(strId));
					pst.execute();
					pst.close();
				}else if(strMode!=null && strMode.equalsIgnoreCase("MA")){
					pst = con.prepareStatement("update emp_perks set approval_1=?, approval_1_emp_id=?, approval_1_date=? where perks_id = ?");
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(strId));
					pst.execute();
					pst.close();
				}else if(strMode!=null && strMode.equalsIgnoreCase("HRA")){
					pst = con.prepareStatement("update emp_perks set approval_2=?, approval_2_emp_id=?, approval_2_date=?  where perks_id = ?");
					pst.setInt(1, uF.parseToInt(strStatus));
					pst.setInt(2, uF.parseToInt(strSessionEmpId));
					pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(4, uF.parseToInt(strId));
					pst.execute();
					pst.close();
				}else if(strMode!=null && strMode.equalsIgnoreCase("PAY")){
					pst = con.prepareStatement("update emp_perks set ispaid=?, paid_date=?, paid_by=?  where perks_id = ?");
					pst.setBoolean(1, true);
					pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(3, uF.parseToInt(strSessionEmpId));
					pst.setInt(4, uF.parseToInt(strId));
					int x = pst.executeUpdate();
					pst.close();
					
					if(x > 0){
						Map<String,Map<String,String>> hmBankMap = CF.getBankMap(con, uF);;
						if(hmBankMap == null) hmBankMap = new HashMap<String, Map<String,String>>();
						
						pst = con.prepareStatement("select ep.emp_id,emp_fname,emp_mname, emp_lname, emp_bank_name, emp_bank_acct_nbr,perk_amount, paid_date from employee_personal_details epd, " +
								"emp_perks ep where epd.emp_per_id = ep.emp_id and perks_id = ?");
						pst.setInt(1, uF.parseToInt(strId));
						rs = pst.executeQuery();
						double dblAmount = 0;
						double dblTotalAmount = 0;
						int nMonth = 0;
						int nYear = 0;
						int nCount = 0;
						StringBuilder sbEmpAmountBankDetails = new StringBuilder();
						String strBankCode = null;
						String strBankName = null;
						String strBankAddress = null;
						String strEmpId = null;
						while(rs.next()){
							strEmpId = rs.getString("emp_id");
							
							dblAmount = uF.parseToDouble(rs.getString("perk_amount"));
							nMonth = uF.parseToInt(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "MM"));
							nYear = uF.parseToInt(uF.getDateFormat(rs.getString("paid_date"), DBDATE, "yyyy"));
							
							dblTotalAmount+=dblAmount;
							
				  			sbEmpAmountBankDetails.append("<tr>");
							sbEmpAmountBankDetails.append("<td><font size=\"1\">"+ ++nCount+".</font></td>");
					
							String strEmpMName = "";
							if(flagMiddleName) {
								if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
									strEmpMName = " "+rs.getString("emp_mname");
								}
							}
						
							sbEmpAmountBankDetails.append("<td><font size=\"1\">"+uF.showData(rs.getString("emp_fname"),"")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"),"")+"</font></td>");
							sbEmpAmountBankDetails.append("<td><font size=\"1\">"+uF.showData(rs.getString("emp_bank_acct_nbr"),"")+"</font></td>");
							
							Map<String, String> hmBankBranch = hmBankMap.get(rs.getString("emp_bank_name"));
							if(hmBankBranch == null) hmBankBranch = new HashMap<String, String>();
							
							strBankCode = hmBankBranch.get("BANK_CODE");
							strBankName = hmBankBranch.get("BANK_NAME");
							strBankAddress = hmBankBranch.get("BANK_ADDRESS");
							
							sbEmpAmountBankDetails.append("<td><font size=\"1\">"+uF.showData(hmBankBranch.get("BANK_BRANCH"),"")+"</font></td>");
							sbEmpAmountBankDetails.append("<td align=\"right\"><font size=\"1\">"+uF.formatIntoTwoDecimal(dblAmount)+"</font></td>");
							sbEmpAmountBankDetails.append("</tr>");
						}
						rs.close();
						pst.close();
						
						String strContent = null;
						String strName = null;
						
						Map<String, String> hmActivityNode = CF.getActivityNode(con);
						if(hmActivityNode == null) hmActivityNode = new HashMap<String, String>();
						
						int nTriggerNode = uF.parseToInt(hmActivityNode.get(""+ACTIVITY_BANK_ORDER_ID));
						String strEmpOrgId = CF.getEmpOrgId(con, uF, strEmpId); 
						
						if(nMonth>0){
//							pst = con.prepareStatement("select * from document_comm_details where document_text like '%["+strBankCode+"]%' ");
							pst = con.prepareStatement("select * from document_comm_details where document_text like '%["+strBankCode+"]%' and trigger_nodes like '%,"+nTriggerNode+",%' and status=1 and org_id=? order by document_id desc limit 1");
							pst.setInt(1, uF.parseToInt(strEmpOrgId));
							rs = pst.executeQuery();
							while(rs.next()){
								strContent = rs.getString("document_text");
							} 
							rs.close();
							pst.close();
							
							
							if(strContent!=null && strContent.indexOf("["+strBankCode+"]")>=0){
								strContent = strContent.replace("["+strBankCode+"]", strBankName +"<br/>"+strBankAddress);
							}
							
							if(strContent!=null && strContent.indexOf(DATE)>=0){
								strContent = strContent.replace(DATE, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
							}
							
							if(strContent!=null && strContent.indexOf(PAYROLL_AMOUNT)>=0){
								strContent = strContent.replace(PAYROLL_AMOUNT, uF.formatIntoTwoDecimal(dblTotalAmount));
							}
							
							if(strContent!=null && strContent.indexOf(PAYROLL_AMOUNT_WORDS)>=0){
								strContent = strContent.replace(PAYROLL_AMOUNT_WORDS, uF.digitsToWords((int)dblTotalAmount));
							}
							
							if(strContent!=null && strContent.indexOf(PAY_MONTH)>=0){
								strContent = strContent.replace(PAY_MONTH, uF.getMonth(nMonth));
							}
							
							if(strContent!=null && strContent.indexOf(PAY_YEAR)>=0){
								strContent = strContent.replace(PAY_YEAR, ""+nYear);
							}
						}
						
						if(strContent!=null && nMonth>0){
							
							StringBuilder sbEmpBankDetails = new StringBuilder();
							
							sbEmpBankDetails.append("<table width=\"100%\">");
							sbEmpBankDetails.append("<tr>");
							sbEmpBankDetails.append("<td width=\"20\"><b>Sr. No.</b></td>");
							sbEmpBankDetails.append("<td><b>Name</b></td>");
							sbEmpBankDetails.append("<td><b>Account No</b></td>");
							sbEmpBankDetails.append("<td><b>Branch</b></td>");
							sbEmpBankDetails.append("<td align=\"right\"><b>Amount</b></td>");
							sbEmpBankDetails.append("</tr>");
							
							sbEmpBankDetails.append(sbEmpAmountBankDetails);
							
							sbEmpBankDetails.append("<tr>");
							sbEmpBankDetails.append("<td colspan=\"5\"><hr width=\"100%\"/></td>");
							sbEmpBankDetails.append("</tr>");
							
							sbEmpBankDetails.append("<tr>");
							sbEmpBankDetails.append("<td>&nbsp;</td>");
							sbEmpBankDetails.append("<td>&nbsp;</td>");
							sbEmpBankDetails.append("<td>&nbsp;</td>");
							sbEmpBankDetails.append("<td><b>TOTAL</b></td>");
							sbEmpBankDetails.append("<td align=\"right\"><b>"+uF.formatIntoTwoDecimal(dblTotalAmount)+"</b></td>");
							sbEmpBankDetails.append("</tr>");
							
							sbEmpBankDetails.append("</table>");
							
							
							strName = "BankStatement_"+nMonth+"_"+nYear;
							
							
							pst = con.prepareStatement("insert into payroll_bank_statement (statement_name, statement_body, generated_date, generated_by, payroll_amount) values (?,?,?,?,?)");
							pst.setString(1, strName);
							pst.setString(2, strContent+""+sbEmpBankDetails.toString());
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(4, uF.parseToInt((String)session.getAttribute(EMPID)));
							pst.setDouble(5, uF.parseToDouble(uF.formatIntoTwoDecimal(Math.round(dblTotalAmount))));
							pst.execute();
							pst.close();
							
							pst  = con.prepareStatement("select max(statement_id) as statement_id from payroll_bank_statement");
							rs = pst.executeQuery();
							int nMaxStatementId = 0;
							while(rs.next()){
								nMaxStatementId = rs.getInt("statement_id");
							}
							rs.close();
							pst.close();

							pst = con.prepareStatement("update emp_perks set statement_id=? where perks_id=?");
							pst.setInt(1, nMaxStatementId);
							pst.setInt(2, uF.parseToInt(strId));
							pst.executeUpdate();
							pst.close();
					
						}
					}
				}

				getStatusMessage(uF.parseToInt(strStatus));
				
			}else{
			
				pst = con.prepareStatement("update requisition_details set status = ? where requisition_id = ?");
				pst.setInt(1, uF.parseToInt(strStatus));
				pst.setInt(2, uF.parseToInt(strId));
				pst.execute();
				pst.close();
				getStatusMessage(uF.parseToInt(strStatus));
				
			}
			
//			System.out.println("pst====>"+pst);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
		
		return nRequisitionId;
	}
	
	
	public int deleteRequest(String strStatus, String strId, String strType){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		int nRequisitionId=0;
		
		try{
			
			con = db.makeConnection(con);
			
			if(strType!=null && strType.equalsIgnoreCase("RIM")){
				/*pst = con.prepareStatement("delete from emp_reimbursement where reimbursement_id =?");
				pst.setInt(1, uF.parseToInt(strId));*/
				pst = con.prepareStatement("update emp_reimbursement set approval_1=-2,approval_2=-2,cancel_by=?,cancel_date=?, cancel_reason=? where reimbursement_id =?");
				pst.setInt(1, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(3, getStrReason());
				pst.setInt(4, uF.parseToInt(strId));
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("update work_flow_details set is_approved=-2 where effective_id =? and effective_type='"+WORK_FLOW_REIMBURSEMENTS+"' ");
				pst.setInt(1, uF.parseToInt(strId));
				pst.execute();
				pst.close();
				
			}else if(strType!=null && strType.equalsIgnoreCase("LER")){
				pst = con.prepareStatement("delete from emp_leave_entry where leave_id = ?");
				pst.setInt(1, uF.parseToInt(strId));
				pst.execute();
				pst.close();
			}else if(strType!=null && strType.equalsIgnoreCase("PERK")){
				pst = con.prepareStatement("delete from emp_perks where perks_id = ?");
				pst.setInt(1, uF.parseToInt(strId));
				pst.execute();
				pst.close();
			}else if(strType!=null && strType.equalsIgnoreCase("PERKSALARY")){
				pst = con.prepareStatement("delete from perk_salary_applied_details where perk_salary_applied_id = ?");
				pst.setInt(1, uF.parseToInt(strId));
				int x = pst.executeUpdate();
				pst.close();
				
				if(x > 0){
					pst = con.prepareStatement("delete from perk_salary_applied_paycycle where perk_salary_applied_id = ?");
					pst.setInt(1, uF.parseToInt(strId));
					pst.execute();
					pst.close();
				}
			}else if(strType!=null && strType.equalsIgnoreCase("REIMBURSEMENTCTC")){
				pst = con.prepareStatement("delete from reimbursement_ctc_applied_details where reim_ctc_applied_id = ?");
				pst.setInt(1, uF.parseToInt(strId));
				int x = pst.executeUpdate();
				pst.close();
				
				if(x > 0){
					pst = con.prepareStatement("delete from reimbursement_ctc_applied_paycycle where reim_ctc_applied_id = ?");
					pst.setInt(1, uF.parseToInt(strId));
					pst.execute();
					pst.close();
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
		
		return nRequisitionId;
	}
	
	
	public String getStrStatus() {
		return strStatus;
	}

	public void setStrStatus(String strStatus) {
		this.strStatus = strStatus;
	}

	public String getStrId() {
		return strId;
	}

	public void setStrId(String strId) {
		this.strId = strId;
	}

	public String getStrType() {
		return strType;
	}

	public void setStrType(String strType) {
		this.strType = strType;
	}

	public String getStrMode() {
		return strMode;
	}

	public void setStrMode(String strMode) {
		this.strMode = strMode;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getApproveDenyReason() {
		return approveDenyReason;
	}

	public void setApproveDenyReason(String approveDenyReason) {
		this.approveDenyReason = approveDenyReason;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	
	public void getStatusMessage(int nStatus){
		
		switch(nStatus){
		
		case -2:
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Canceled\"></i>");
			break;
			
		case -1:
			  /*request.setAttribute("STATUS_MSG", "<img title=\"Denied\" src=\""+request.getContextPath()+"/images1/icons/denied.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
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
	
	
	public void getStatusMessage1(int nStatus){
		
		switch(nStatus){
		
		case -1:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Disabled\" src=\""+request.getContextPath()+"/images1/icons/denied.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Disabled\"></i>");
			
			break;
			
		case 0:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Pending\" src=\""+request.getContextPath()+"/images1/icons/pending.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"Pending\" ></i>");
			
			break;
			
		case 1:
			/*request.setAttribute("STATUS_MSG", "<img title=\"Enabled\" src=\""+request.getContextPath()+"/images1/icons/approved.png\" border=\"0\">");*/
			request.setAttribute("STATUS_MSG", "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Enabled\"></i>");
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

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getStrDivResult() {
		return strDivResult;
	}

	public void setStrDivResult(String strDivResult) {
		this.strDivResult = strDivResult;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getStrReason() {
		return strReason;
	}

	public void setStrReason(String strReason) {
		this.strReason = strReason;
	}
	
}
