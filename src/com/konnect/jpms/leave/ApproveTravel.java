package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillApproval;
import com.konnect.jpms.select.FillHalfDaySession;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApproveTravel extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	private String strUserType;
	private String strSessionEmpId;
	private String destinations;
	private String userType;
	
	private String leaveId;
	private String userId;
	private String empId;
	private String empName;
	private String reason;
	private String managerReason;
	private String empNoOfLeave;
	private String typeOfLeave;
	private String leaveFromTo;
	private String leaveToDate;
	private String approvalFromTo;
	private String approvalToDate;
	private String entryDate;
	private boolean isHalfDay;
	private String strSession;
	private List<FillHalfDaySession> strWorkingSession;
	private int isapproved=0;

	
	private String strAdvRequested;
	private String strAdvDate;
	private String strAdvEligibility;
	private String approveAdvAmount;
	private String approveAdvComment;
	private int isAdvapproved; 
	
	
//	List<FillUserType> userTypeList;
	private List<FillLeaveType> leaveTypeList;
	private List<FillApproval> approvalList;
	
	private String conciergeService;
	private String travelMode;
	private String isBooking;
	private String bookingDetails;
	private String isAccommodation;
	private String accommodationDetails;
	
	private String type;
	private String placeFrom;
	
	
	private String currUserType;
	private String leaveStatus;
	private String strStartDate;
	private String strEndDate;
	private String travelType;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PTravelApproval);
		
		String strEdit = request.getParameter("E");
		String strDelete = request.getParameter("D");

//		System.out.println("userType==>"+getUserType());
		getUserTypeName();
		if (strEdit != null) {
			System.out.println("Edit");
			viewLeaveEntry(strEdit);
			request.setAttribute(TITLE, "Approve / Deny Travel");
			return SUCCESS;
		}
		if (strDelete != null) {
			deleteLeaveEntry(strDelete);
			request.setAttribute(TITLE, TDeleteManagerLeaveApproval);
			return VIEW;
		}
		// TODO : Start Dattatray
		if(getTravelType()!=null && getTravelType().equals("OD")) {
			if (getLeaveId() != null && getLeaveId().length() > 0) {
				updateOnDutyEntry();
				if (getType() != null && getType().equals("myhome")) {
	                return DASHBOARD;
	            } else {
	            	return UPDATE;
		        }
			}
		} // TODO : End Dattatray
		else  {
			if (getLeaveId() != null && getLeaveId().length() > 0) {
				updateLeaveEntry();
				request.setAttribute(TITLE, TEditManagerLeaveApproval);
				if (getType() != null && getType().equals("myhome")) {
	                return DASHBOARD;
	            } else {
	            	return UPDATE;
		        }
			} 
		}
		return loadLeaveEntry();
	}
	
	/**
	 * @author Dattatray
	 * @date 21-Apr-2021
	 *
	 * @return
	 */
	private String updateOnDutyEntry() {
		System.out.println("updateLeaveEntry");
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs=null;
		try {

			con = db.makeConnection(con);
			
			boolean flag = true;
			boolean flagAdmin = false;
			if (uF.parseToBoolean(CF.getIsWorkFlow())) {
    			
                if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) {
                	pst = con.prepareStatement("UPDATE emp_leave_entry SET manager_reason=?,is_approved=?,user_id=? WHERE leave_id=?");
                	pst.setString(1, getManagerReason());
        			pst.setInt(2, getIsapproved());
        			pst.setInt(3, uF.parseToInt(strSessionEmpId));
        			pst.setInt(4, uF.parseToInt(getLeaveId()));
        			pst.execute();
        			pst.close();
        			flagAdmin = true;
                } else {
                    pst = con
                            .prepareStatement("select work_flow_id from work_flow_details where effective_id=? " +
                            		"and effective_type='"+WORK_FLOW_TRAVEL+"' and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
                    pst.setInt(1, uF.parseToInt(getLeaveId()));
                    pst.setInt(2, uF.parseToInt(strSessionEmpId));
                    pst.setInt(3, uF.parseToInt(getUserType()));
                    rs = pst.executeQuery();
                    int work_id = 0;
                    if (rs.next()) {
                        work_id = rs.getInt("work_flow_id");
                    }
        			rs.close();
        			pst.close();
                    
                    pst = con
                            .prepareStatement("UPDATE work_flow_details SET is_approved=?,emp_id=?,approve_date=?,reason=? WHERE work_flow_id=?");
                    pst.setInt(1, getIsapproved());
                    pst.setInt(2, uF.parseToInt(strSessionEmpId));
                    pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
                    pst.setString(4, getManagerReason());
                    pst.setInt(5, work_id);
                    pst.execute();
        			pst.close();
                    
                    pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_TRAVEL+"' " +
                            " and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id=? " +
                            " and is_approved=0 and effective_type='"+WORK_FLOW_TRAVEL+"' and member_position not in " +
                            " (select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_TRAVEL+"' )) " +
                            " order by work_flow_id");
                    pst.setInt(1, uF.parseToInt(getLeaveId()));
                    pst.setInt(2, uF.parseToInt(getLeaveId()));
                    pst.setInt(3, uF.parseToInt(getLeaveId()));
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        flag = false;
                    }
        			rs.close();
        			pst.close();
        			
                    if (flag) {
                    	System.out.println("1");
                    	pst = con.prepareStatement("UPDATE emp_leave_entry SET manager_reason=?,is_approved=?,user_id=? WHERE leave_id=?");
                    	pst.setString(1, getManagerReason());
            			pst.setInt(2, getIsapproved());
            			pst.setInt(3, uF.parseToInt(strSessionEmpId));
            			pst.setInt(4, uF.parseToInt(getLeaveId()));
            			pst.execute();
            			pst.close();
                    } else if (getIsapproved() == -1) {
                    	System.out.println("2");
                    	pst = con.prepareStatement("UPDATE emp_leave_entry SET manager_reason=?,is_approved=?,user_id=? WHERE leave_id=?");
                    	pst.setString(1, getManagerReason());
            			pst.setInt(2, getIsapproved());
            			pst.setInt(3, uF.parseToInt(strSessionEmpId));
            			pst.setInt(4, uF.parseToInt(getLeaveId()));
            			pst.execute();
            			pst.close();
                    }
                } 
            } else {
                if (strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) {
                	System.out.println("strUserType : "+strUserType);
                	pst = con.prepareStatement("UPDATE emp_leave_entry SET manager_reason=?,is_approved=?,user_id=? WHERE leave_id=?");
        			pst.setString(1, getManagerReason());
        			pst.setInt(2, getIsapproved());
        			pst.setInt(3, uF.parseToInt(strSessionEmpId));
        			pst.setInt(4, uF.parseToInt(getLeaveId()));
        			pst.execute();
        			pst.close();
        			flagAdmin = true;
                }
            }
			
			
			//Map hmEmpLevel = CF.getEmpLevelMap();
			if(getIsapproved()==1){
				System.out.println("4");
				pst = con.prepareStatement("UPDATE emp_leave_entry SET ispaid=true WHERE leave_id=?");
				pst.setInt(1, uF.parseToInt(getLeaveId()));
				pst.execute();
				pst.close();
				
				 boolean flag1 = false;
                 pst = con.prepareStatement("select leave_id from emp_leave_entry where leave_id=? and is_approved>0");
                 pst.setInt(1, uF.parseToInt(getLeaveId()));
                 rs = pst.executeQuery();
                 while (rs.next()) {
                     flag1 = true;
                 }
     			rs.close();
     			pst.close();
     			
     			if(flag1){
     				insertTravelRegister(uF);
     			}
				
			}
			
			String strSupervisorName = CF.getEmpNameMapByEmpId(con, (String)session.getAttribute(EMPID));
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(N_EMPLOYEE_TRAVEL_APPROVAL, CF);  
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setSupervisor(true);
			nF.setStrEmpId(getEmpId());
//			nF.setStrHostAddress(request.getRemoteHost());
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setStrEmpLeaveFrom(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
			nF.setStrEmpLeaveTo(uF.getDateFormat(getLeaveToDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
			nF.setStrEmpLeaveNoOfDays(uF.dateDifference(getLeaveFromTo(),DATE_FORMAT, getLeaveToDate(), DATE_FORMAT,CF.getStrTimeZone()));
			nF.setStrEmpLeaveReason(getReason());
			nF.setStrManagerLeaveReason(getManagerReason());
			if(getIsapproved()==1){
				nF.setStrApprvedDenied("approved");
			}else{
				nF.setStrApprvedDenied("denied");
			}
			nF.setStrManagerName(uF.showData(strSupervisorName, ""));
			nF.setStrEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT_STR));
			nF.setEmailTemplate(true);
			nF.sendNotifications();
			
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			if (flagAdmin || flag || getIsapproved()== -1) {
				String strApproveDeny = "approved";
				if(getIsapproved()== -1) {
					strApproveDeny = "denied";
				}
				String alertData = "<div style=\"float: left;\"> Your On Duty has been "+strApproveDeny+" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
				String alertAction = "MyPay.action?pType=WR&dataType=T&callFrom=NotiApplyTravel";
				
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(getEmpId());
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
				
	        }
			
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
			return SUCCESS;
	}// TODO : End Dattatray


	public void getUserTypeName() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			con = db.makeConnection(con);
			Map<String,String> hmUserType = CF.getUserTypeMap(con);
			if(getUserType()!= null && !getUserType().equals("")) {
				request.setAttribute("userTypeName", hmUserType.get(getUserType()));
//				System.out.println("userTypeName==>"+hmUserType.get(getUserType()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeConnection(con);
		} 
	}
	public String loadVidateLeaveEntry() {
		request.setAttribute(PAGE, PTravelApproval);
		request.setAttribute(TITLE, "Approve / Deny Travel");
//		userTypeList = new FillUserType().fillUserType();
		approvalList = new FillApproval().fillApprovalDenied();
		leaveTypeList = new FillLeaveType(request).fillLeave();
		return LOAD;
	}

	public String loadLeaveEntry() {

		request.setAttribute(PAGE, PTravelApproval);
		request.setAttribute(TITLE, TAddManagerLeaveApproval);
		
		strWorkingSession = new FillHalfDaySession().fillHalfDaySession();
		
		UtilityFunctions uF = new UtilityFunctions();
		setLeaveId("");
		setEmpId("");
		setEmpNoOfLeave("");
		setTypeOfLeave("");
		setReason("");
		setManagerReason("");
		setUserId("");
		setLeaveFromTo(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
		setLeaveToDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
		setApprovalFromTo(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
		setApprovalToDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
		setEntryDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
		
		return LOAD;
	}
	
	public String updateLeaveEntry() {
		System.out.println("updateLeaveEntry");
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		ResultSet rs=null;
		try {

			con = db.makeConnection(con);
			
			boolean flag = true;
			boolean flagAdmin = false;
			if (uF.parseToBoolean(CF.getIsWorkFlow())) {
//                pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_TRAVEL+"'" +
//                        " and effective_id=? order by effective_id,member_position");
//                pst.setInt(1, uF.parseToInt(getLeaveId()));
//                rs = pst.executeQuery();
//                Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();
//                while (rs.next()) {
//                    List<String> checkEmpList = hmCheckEmp.get(rs.getString("effective_id"));
//                    if (checkEmpList == null) checkEmpList = new ArrayList<String>();
//                    checkEmpList.add(rs.getString("emp_id"));
//                    hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
//                }
//    			rs.close();
//    			pst.close();
    			
                if (strUserType != null && strUserType.equalsIgnoreCase(ADMIN)) {
                	pst = con.prepareStatement(updateManagerApproval);
        			pst.setInt(1, uF.parseToInt(getEmpId()));
        			pst.setDate(2,uF.getDateFormat(getEntryDate(), DATE_FORMAT));
        			
        			if(getIsHalfDay()){
        				pst.setDate(3, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
        				pst.setDate(4, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
        			}else{
        				pst.setDate(3, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
        				pst.setDate(4, uF.getDateFormat(getLeaveToDate(), DATE_FORMAT));
        			}
        			
        			pst.setInt(5, TRAVEL_LEAVE);
        			pst.setString(6, getReason());
        			pst.setDate(7, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
        			pst.setDate(8, uF.getDateFormat(getApprovalToDate(), DATE_FORMAT));
        			
        			if(getIsHalfDay()){
        				pst.setDouble	(9, 0.5);
        			}else{
        				pst.setInt(9, uF.parseToInt(uF.dateDifference(getApprovalFromTo(), DATE_FORMAT, getApprovalToDate(), DATE_FORMAT,CF.getStrTimeZone())));
        			}
        			
        			pst.setString(10, getManagerReason());
        			pst.setInt(11, getIsapproved());
//        			pst.setInt(12, uF.parseToInt(getUserId()));
        			pst.setInt(12, uF.parseToInt(strSessionEmpId));
        			pst.setBoolean(13, getIsHalfDay());
        			pst.setInt(14, uF.parseToInt(getLeaveId()));
        			pst.execute();
        			pst.close();
        			flagAdmin = true;
                } else {
                    pst = con
                            .prepareStatement("select work_flow_id from work_flow_details where effective_id=? " +
                            		"and effective_type='"+WORK_FLOW_TRAVEL+"' and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
                    pst.setInt(1, uF.parseToInt(getLeaveId()));
                    pst.setInt(2, uF.parseToInt(strSessionEmpId));
                    pst.setInt(3, uF.parseToInt(getUserType()));
                    rs = pst.executeQuery();
                    int work_id = 0;
                    if (rs.next()) {
                        work_id = rs.getInt("work_flow_id");
                    }
        			rs.close();
        			pst.close();
                    
                    pst = con
                            .prepareStatement("UPDATE work_flow_details SET is_approved=?,emp_id=?,approve_date=?,reason=? WHERE work_flow_id=?");
                    pst.setInt(1, getIsapproved());
                    pst.setInt(2, uF.parseToInt(strSessionEmpId));
                    pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
                    pst.setString(4, getManagerReason());
                    pst.setInt(5, work_id);
                    pst.execute();
        			pst.close();
                    
                    pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_TRAVEL+"' " +
                            " and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id=? " +
                            " and is_approved=0 and effective_type='"+WORK_FLOW_TRAVEL+"' and member_position not in " +
                            " (select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_TRAVEL+"' )) " +
                            " order by work_flow_id");
                    pst.setInt(1, uF.parseToInt(getLeaveId()));
                    pst.setInt(2, uF.parseToInt(getLeaveId()));
                    pst.setInt(3, uF.parseToInt(getLeaveId()));
                    rs = pst.executeQuery();
                    while (rs.next()) {
                        flag = false;
                    }
        			rs.close();
        			pst.close();
        			
                    if (flag) {
                    	pst = con.prepareStatement(updateManagerApproval);
            			pst.setInt(1, uF.parseToInt(getEmpId()));
            			pst.setDate(2,uF.getDateFormat(getEntryDate(), DATE_FORMAT));
            			
            			if(getIsHalfDay()){
            				pst.setDate(3, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
            				pst.setDate(4, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
            			}else{
            				pst.setDate(3, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
            				pst.setDate(4, uF.getDateFormat(getLeaveToDate(), DATE_FORMAT));
            			}
            			
            			pst.setInt(5, TRAVEL_LEAVE);
            			pst.setString(6, getReason());
            			pst.setDate(7, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
            			pst.setDate(8, uF.getDateFormat(getApprovalToDate(), DATE_FORMAT));
            			
            			if(getIsHalfDay()){
            				pst.setDouble	(9, 0.5);
            			}else{
            				pst.setInt(9, uF.parseToInt(uF.dateDifference(getApprovalFromTo(), DATE_FORMAT, getApprovalToDate(), DATE_FORMAT,CF.getStrTimeZone())));
            			}
            			
            			pst.setString(10, getManagerReason());
            			pst.setInt(11, getIsapproved());
//            			pst.setInt(12, uF.parseToInt(getUserId()));
            			pst.setInt(12, uF.parseToInt(strSessionEmpId));
            			pst.setBoolean(13, getIsHalfDay());
            			pst.setInt(14, uF.parseToInt(getLeaveId()));
            			pst.execute();
            			pst.close();
                    }
//                    else if (strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
//                    	pst = con.prepareStatement(updateManagerApproval);
//            			pst.setInt(1, uF.parseToInt(getEmpId()));
//            			pst.setDate(2,uF.getDateFormat(getEntryDate(), DATE_FORMAT));
//            			
//            			if(getIsHalfDay()){
//            				pst.setDate(3, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
//            				pst.setDate(4, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
//            			}else{
//            				pst.setDate(3, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
//            				pst.setDate(4, uF.getDateFormat(getLeaveToDate(), DATE_FORMAT));
//            			}
//            			
//            			pst.setInt(5, TRAVEL_LEAVE);
//            			pst.setString(6, getReason());
//            			pst.setDate(7, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
//            			pst.setDate(8, uF.getDateFormat(getApprovalToDate(), DATE_FORMAT));
//            			
//            			if(getIsHalfDay()){
//            				pst.setDouble(9, 0.5);
//            			}else{
//            				pst.setInt(9, uF.parseToInt(uF.dateDifference(getApprovalFromTo(), DATE_FORMAT, getApprovalToDate(), DATE_FORMAT)));
//            			}
//            			pst.setString(10, getManagerReason());
//            			pst.setInt(11, getIsapproved());
//            			pst.setInt(12, uF.parseToInt(getUserId()));
//            			pst.setBoolean(13, getIsHalfDay());
//            			pst.setInt(14, uF.parseToInt(getLeaveId()));
//            			pst.execute();
//            			pst.close();
//                    }
                    else if (getIsapproved() == -1) {
                    	pst = con.prepareStatement(updateManagerApproval);
            			pst.setInt(1, uF.parseToInt(getEmpId()));
            			pst.setDate(2,uF.getDateFormat(getEntryDate(), DATE_FORMAT));
            			
            			if(getIsHalfDay()){
            				pst.setDate(3, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
            				pst.setDate(4, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
            			}else{
            				pst.setDate(3, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
            				pst.setDate(4, uF.getDateFormat(getLeaveToDate(), DATE_FORMAT));
            			}
            			
            			pst.setInt(5, TRAVEL_LEAVE);
            			pst.setString(6, getReason());
            			pst.setDate(7, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
            			pst.setDate(8, uF.getDateFormat(getApprovalToDate(), DATE_FORMAT));
            			
            			if(getIsHalfDay()){
            				pst.setDouble(9, 0.5);
            			}else{
            				pst.setInt(9, uF.parseToInt(uF.dateDifference(getApprovalFromTo(), DATE_FORMAT, getApprovalToDate(), DATE_FORMAT,CF.getStrTimeZone())));
            			}
            			pst.setString(10, getManagerReason());
            			pst.setInt(11, getIsapproved());
//            			pst.setInt(12, uF.parseToInt(getUserId()));
            			pst.setInt(12, uF.parseToInt(strSessionEmpId));
            			pst.setBoolean(13, getIsHalfDay());
            			pst.setInt(14, uF.parseToInt(getLeaveId()));
            			pst.execute();
            			pst.close();
                    }
                } 
            } else {
                if (strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))) {
                	pst = con.prepareStatement(updateManagerApproval);
        			pst.setInt(1, uF.parseToInt(getEmpId()));
        			pst.setDate(2,uF.getDateFormat(getEntryDate(), DATE_FORMAT));
        			
        			if(getIsHalfDay()){
        				pst.setDate(3, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
        				pst.setDate(4, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
        			}else{
        				pst.setDate(3, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));
        				pst.setDate(4, uF.getDateFormat(getLeaveToDate(), DATE_FORMAT));
        			}
        			
        			pst.setInt(5, TRAVEL_LEAVE);
        			pst.setString(6, getReason());
        			pst.setDate(7, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
        			pst.setDate(8, uF.getDateFormat(getApprovalToDate(), DATE_FORMAT));
        			
        			if(getIsHalfDay()){
        				pst.setDouble(9, 0.5);
        			}else{
        				pst.setInt(9, uF.parseToInt(uF.dateDifference(getApprovalFromTo(), DATE_FORMAT, getApprovalToDate(), DATE_FORMAT,CF.getStrTimeZone())));
        			}
        			
        			pst.setString(10, getManagerReason());
        			pst.setInt(11, getIsapproved());
//        			pst.setInt(12, uF.parseToInt(getUserId()));
        			pst.setInt(12, uF.parseToInt(strSessionEmpId));
        			pst.setBoolean(13, getIsHalfDay());
        			pst.setInt(14, uF.parseToInt(getLeaveId()));
        			pst.execute();
        			pst.close();
        			flagAdmin = true;
                }
            }
			
			
			//Map hmEmpLevel = CF.getEmpLevelMap();
			if(getIsapproved()==1){
				pst = con.prepareStatement("UPDATE emp_leave_entry SET ispaid=true WHERE leave_id=?");
				pst.setInt(1, uF.parseToInt(getLeaveId()));
				pst.execute();
				pst.close();
				
				 boolean flag1 = false;
                 pst = con.prepareStatement("select leave_id from emp_leave_entry where leave_id=? and is_approved>0");
                 pst.setInt(1, uF.parseToInt(getLeaveId()));
                 rs = pst.executeQuery();
                 while (rs.next()) {
                     flag1 = true;
                 }
     			rs.close();
     			pst.close();
     			
     			if(flag1){
     				insertTravelRegister(uF);
     			}
				
			}
			
//			pst = con.prepareStatement("update travel_advance set comments=?, approved_by=?, approved_date=?, approved_amount=?, advance_status=? where advance_id=?");
			pst = con.prepareStatement("update travel_advance set comments=?, approved_by=?, approved_date=?, approved_amount=?, advance_status=? where travel_id=?");
			pst.setString(1, getApproveAdvComment());
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDouble(4, uF.parseToDouble(getApproveAdvAmount()));
			pst.setInt(5, getIsAdvapproved());
			pst.setInt(6, uF.parseToInt(getLeaveId()));
			pst.execute();
			pst.close();
			
			
			String strSupervisorName = CF.getEmpNameMapByEmpId(con, (String)session.getAttribute(EMPID));
			String strDomain = request.getServerName().split("\\.")[0];
			Notifications nF = new Notifications(N_EMPLOYEE_TRAVEL_APPROVAL, CF);  
			nF.setDomain(strDomain);
			nF.request = request;
			nF.setSupervisor(true);
			nF.setStrEmpId(getEmpId());
//			nF.setStrHostAddress(request.getRemoteHost());
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
			nF.setStrContextPath(request.getContextPath());
			nF.setStrEmpLeaveFrom(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
			nF.setStrEmpLeaveTo(uF.getDateFormat(getLeaveToDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
			nF.setStrEmpLeaveNoOfDays(uF.dateDifference(getLeaveFromTo(),DATE_FORMAT, getLeaveToDate(), DATE_FORMAT,CF.getStrTimeZone()));
			nF.setStrEmpLeaveReason(getReason());
			nF.setStrManagerLeaveReason(getManagerReason());
			if(getIsapproved()==1){
				nF.setStrApprvedDenied("approved");
			}else{
				nF.setStrApprvedDenied("denied");
			}
			nF.setStrManagerName(uF.showData(strSupervisorName, ""));
			nF.setStrEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT_STR));
			nF.setEmailTemplate(true);
			nF.sendNotifications();
			
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			if (flagAdmin || flag || getIsapproved()== -1) {
				String strApproveDeny = "approved";
				if(getIsapproved()== -1) {
					strApproveDeny = "denied";
				}
				String alertData = "<div style=\"float: left;\"> Your Travel Plan has been "+strApproveDeny+" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
				String alertAction = "MyPay.action?pType=WR&dataType=T&callFrom=NotiApplyTravel";
				
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(getEmpId());
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
				
//	            UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//				userAlerts.setStrDomain(strDomain);
//				userAlerts.setStrEmpId(getEmpId());
//				userAlerts.set_type(TRAVEL_APPROVAL_ALERT);
//				userAlerts.setStatus(INSERT_ALERT);
//				Thread t = new Thread(userAlerts);
//				t.run();
	        }
			
			
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in updation");
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
			return SUCCESS;
	}

	private void insertTravelRegister(UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		Connection con = null;
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			 double dblLeavesApproved = 0;
             if (getIsHalfDay()) {
                 dblLeavesApproved = 0.5;
             } else {
                 dblLeavesApproved = uF.parseToInt(uF.dateDifference(getApprovalFromTo(), DATE_FORMAT, getApprovalToDate(), DATE_FORMAT,CF.getStrTimeZone()));
             }
             
             Calendar cal = GregorianCalendar.getInstance();
             cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, "dd")));
             cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, "MM")) - 1);
             cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, "yyyy")));
             
             double dblLeaveDed = 0;
             for (int i = 0; i < dblLeavesApproved; i++) {
				Date dtCurrent = uF.getDateFormat(cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR), DATE_FORMAT);
				cal.add(Calendar.DATE, 1);
				
				if (getIsHalfDay()) {
                    dblLeaveDed = 0.5;
                } else {
                    dblLeaveDed = 1;
                }
				
				pst = con.prepareStatement("update travel_application_register set travel_id=?, travel_no=?, is_paid=? where _date=? and emp_id=?");
				pst.setInt(1, uF.parseToInt(getLeaveId()));
				pst.setDouble(2, dblLeaveDed);
				pst.setBoolean(3, true);
				pst.setDate(4, dtCurrent);
				pst.setInt(5, uF.parseToInt(getEmpId()));
				int update=pst.executeUpdate();
				pst.close();
						
				if(update==0){
					pst = con.prepareStatement("insert into travel_application_register (_date, emp_id, travel_id, travel_no,is_paid) values (?,?,?,?,?)");
					pst.setDate(1, dtCurrent);
					pst.setInt(2, uF.parseToInt(getEmpId()));
					pst.setInt(3, uF.parseToInt(getLeaveId()));
					pst.setDouble(4, dblLeaveDed);
					pst.setBoolean(5, true);
					pst.execute();
					pst.close();
				}
				
             }
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String viewLeaveEntry(String strEdit) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con, null, null);
//			pst = con.prepareStatement("select * from leave_type lt, emp_leave_entry elt where lt.leave_type_id = elt.leave_type_id and leave_id =?");
			pst = con.prepareStatement("select * from emp_leave_entry elt where leave_id =?");
			pst.setInt(1, uF.parseToInt(strEdit));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				setEmpName(hmEmpNameMap.get(rs.getString("emp_id")));
				setUserId(rs.getString("emp_id"));
				setLeaveId(rs.getString("leave_id"));
				setEmpId(rs.getString("emp_id"));
				setLeaveFromTo(uF.getDateFormat(rs.getString("leave_from"), DBDATE, DATE_FORMAT));
				setLeaveToDate(uF.getDateFormat(rs.getString("leave_to"), DBDATE, DATE_FORMAT));
				setReason(rs.getString("reason"));
				setManagerReason(uF.showData(rs.getString("manager_reason"), ""));
				setTypeOfLeave(rs.getString("leave_type_id"));
				setEmpNoOfLeave(rs.getString("emp_no_of_leave"));
				setIsapproved(rs.getInt("is_approved"));
				setApprovalFromTo(uF.getDateFormat(rs.getString("leave_from"), DBDATE, DATE_FORMAT));
				setApprovalToDate(uF.getDateFormat(rs.getString("leave_to"), DBDATE, DATE_FORMAT));
				setEntryDate(uF.getDateFormat(rs.getString("entrydate"), DBDATE, DATE_FORMAT));
				setIsHalfDay(rs.getBoolean("ishalfday"));
				setStrSession(rs.getString("session_no"));
				setDestinations(rs.getString("destinations"));
				setPlaceFrom(rs.getString("place_from"));
				
				if(uF.parseToBoolean(rs.getString("is_concierge"))){
					setConciergeService("Yes");
					if(rs.getString("travel_mode")!=null && !rs.getString("travel_mode").trim().equals("")){
						List<String> alTravelMode=Arrays.asList(rs.getString("travel_mode").trim().split(","));
						StringBuilder sbMode = null;
						for(int j=0;alTravelMode!=null && !alTravelMode.isEmpty() && j<alTravelMode.size();j++){
							if(uF.parseToInt(alTravelMode.get(j).trim()) > 0){
								if(sbMode == null){
									sbMode = new StringBuilder();
									sbMode.append(uF.getTravelMode(uF.parseToInt(alTravelMode.get(j).trim())));
								} else {
									sbMode.append(", "+uF.getTravelMode(uF.parseToInt(alTravelMode.get(j).trim())));
								}
							}
						}
						setTravelMode(sbMode!=null ? sbMode.toString() : "");
						
					} else {
						setTravelMode("");
					}
					
					String strBooking = "No";
					String strBookingInfo = "";
					if(uF.parseToBoolean(rs.getString("is_booking"))){
						strBooking = "Yes";
						strBookingInfo = uF.showData(rs.getString("booking_info"), "");
					}
					setIsBooking(strBooking);
					setBookingDetails(strBookingInfo);
					
					String strAccommodation = "No";
					String strAccommodationInfo = "";
					if(uF.parseToBoolean(rs.getString("is_accommodation"))){
						strAccommodation = "Yes";
						strAccommodationInfo = uF.showData(rs.getString("accommodation_info"), "");
					}
					setIsAccommodation(strAccommodation);
					setAccommodationDetails(strAccommodationInfo);
				} else {
					setConciergeService("No");
					setTravelMode("");
					setIsBooking("No");
					setBookingDetails("");
					setIsAccommodation("No");
					setAccommodationDetails("");
				}
				
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			
			pst = con.prepareStatement("select * from travel_advance where travel_id=?");
			pst.setInt(1, uF.parseToInt(strEdit));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
//				System.out.println("adamt==>"+rs.getString("advance_amount"));
				setStrAdvRequested(strCurrency+ uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("advance_amount"))));
				setStrAdvDate(uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
//				setStrAdvEligibility(strCurrency+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble("0")));
				setApproveAdvAmount(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("advance_amount"))));
				setApproveAdvComment(null);
				setIsAdvapproved(0); 
				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from travel_advance_eligibility where emp_id=?");
			pst.setInt	(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
//			System.out.println("pst==>"+pst);
			while(rs.next()) {
				String strCurrency = "";
				if(uF.parseToInt(hmEmpCurrency.get(rs.getString("emp_id"))) > 0){
					Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(rs.getString("emp_id")));
					if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
					strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
				} 
				setStrAdvEligibility(strCurrency+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("eligibility_amount"))));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			return ERROR;
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		leaveTypeList = new FillLeaveType(request).fillLeave();
		strWorkingSession = new FillHalfDaySession().fillHalfDaySession();
		approvalList = new FillApproval().fillApprovalDenied();
		return SUCCESS;

	}

	public String deleteLeaveEntry(String strDelete) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(deleteManagerApproval);
			pst.setInt(1, uF.parseToInt(strDelete));
			pst.execute();
			pst.close();

			request.setAttribute(MESSAGE, "Deleted successfully!");
		} catch (Exception e) {
			e.printStackTrace();
			request.setAttribute(MESSAGE, "Error in deletion");
			return ERROR;
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
	
		
    
	
	
//	public void validate() {
//		UtilityFunctions uF = new UtilityFunctions();
//		strWorkingSession = new FillHalfDaySession().fillHalfDaySession();
//		
//		Connection con = null;
//		Database db = new Database();
//		db.setRequest(request);
//		try {
//			
//			con = db.makeConnection(con);
//			
//			session = request.getSession();
//			CF = (CommonFunctions)session.getAttribute(CommonFunctions);
//			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con, null, null);
//			
//			setEmpName(hmEmpNameMap.get(getEmpId()));
//			      
//	        if (getTypeOfLeave()!=null && uF.parseToInt(getTypeOfLeave())== 0) {
//	            addFieldError("TypeOfLeave", "Type of leave is required");
//	        } 
//	        if (getReason()!=null && getReason().length() == 0) {
//	            addFieldError("Reason", "Employee reason is required");
//	        } 
//	        if (getManagerReason()!=null && getManagerReason().length() == 0) {
//	            addFieldError("ManagerReason", " Manager reason is required");
//	        } 
//	       if (getLeaveFromTo()!=null && getLeaveFromTo().length() == 0) {
//	            addFieldError("LeaveFromTo", " Leave start date is required");
//	        } 
//	        if (getLeaveToDate()!=null && getLeaveToDate().length() == 0) {
//	            addFieldError("LeaveToDate", " Leave end date is required");
//	        }
//	       
//	        
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeConnection(con);
//		}
//		
//		
//		
//        
//        loadVidateLeaveEntry();
//    }
	
	public String getLeaveId() {
		return leaveId;
	}
	public void setLeaveId(String leaveId) {
		this.leaveId = leaveId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getEmpName() {
		return empName;
	}
	public void setEmpName(String empName) {
		this.empName = empName;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getManagerReason() {
		return managerReason;
	}
	public void setManagerReason(String managerReason) {
		this.managerReason = managerReason;
	}
	public String getEmpNoOfLeave() {
		return empNoOfLeave;
	}
	public void setEmpNoOfLeave(String empNoOfLeave) {
		this.empNoOfLeave = empNoOfLeave;
	}
	public String getTypeOfLeave() {
		return typeOfLeave;
	}
	public void setTypeOfLeave(String typeOfLeave) {
		this.typeOfLeave = typeOfLeave;
	}
	public String getLeaveFromTo() {
		return leaveFromTo;
	}
	public void setLeaveFromTo(String leaveFromTo) {
		this.leaveFromTo = leaveFromTo;
	}
	public String getLeaveToDate() {
		return leaveToDate;
	}
	public void setLeaveToDate(String leaveToDate) {
		this.leaveToDate = leaveToDate;
	}
	public String getApprovalFromTo() {
		return approvalFromTo;
	}
	public void setApprovalFromTo(String approvalFromTo) {
		this.approvalFromTo = approvalFromTo;
	}
	public String getApprovalToDate() {
		return approvalToDate;
	}
	public void setApprovalToDate(String approvalToDate) {
		this.approvalToDate = approvalToDate;
	}
	public String getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(String entryDate) {
		this.entryDate = entryDate;
	}
	public int getIsapproved() {
		return isapproved;
	}
	public void setIsapproved(int isapproved) {
		this.isapproved = isapproved;
	}
//	public List<FillUserType> getUserTypeList() {
//		return userTypeList;
//	}
//	public void setUserTypeList(List<FillUserType> userTypeList) {
//		this.userTypeList = userTypeList;
//	}
	public List<FillLeaveType> getLeaveTypeList() {
		return leaveTypeList;
	}
	public void setLeaveTypeList(List<FillLeaveType> leaveTypeList) {
		this.leaveTypeList = leaveTypeList;
	}
	public List<FillApproval> getApprovalList() {
		return approvalList;
	}
	public void setApprovalList(List<FillApproval> approvalList) {
		this.approvalList = approvalList;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	public String getStrSession() {
		return strSession;
	}
	public void setStrSession(String strSession) {
		this.strSession = strSession;
	}
	public List<FillHalfDaySession> getStrWorkingSession() {
		return strWorkingSession;
	}
	public boolean getIsHalfDay() {
		return isHalfDay;
	}
	public void setIsHalfDay(boolean isHalfDay) {
		this.isHalfDay = isHalfDay;
	}
	public void setStrWorkingSession(List<FillHalfDaySession> strWorkingSession) {
		this.strWorkingSession = strWorkingSession;
	}
	public String getDestinations() {
		return destinations;
	}
	public void setDestinations(String destinations) {
		this.destinations = destinations;
	}
	public String getStrAdvRequested() {
		return strAdvRequested;
	}
	public void setStrAdvRequested(String strAdvRequested) {
		this.strAdvRequested = strAdvRequested;
	}
	public String getStrAdvDate() {
		return strAdvDate;
	}
	public void setStrAdvDate(String strAdvDate) {
		this.strAdvDate = strAdvDate;
	}
	public String getStrAdvEligibility() {
		return strAdvEligibility;
	}
	public void setStrAdvEligibility(String strAdvEligibility) {
		this.strAdvEligibility = strAdvEligibility;
	}
	public String getApproveAdvAmount() {
		return approveAdvAmount;
	}
	public void setApproveAdvAmount(String approveAdvAmount) {
		this.approveAdvAmount = approveAdvAmount;
	}
	public String getApproveAdvComment() {
		return approveAdvComment;
	}
	public void setApproveAdvComment(String approveAdvComment) {
		this.approveAdvComment = approveAdvComment;
	}
	public int getIsAdvapproved() {
		return isAdvapproved;
	}
	public void setIsAdvapproved(int isAdvapproved) {
		this.isAdvapproved = isAdvapproved;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getConciergeService() {
		return conciergeService;
	}
	public void setConciergeService(String conciergeService) {
		this.conciergeService = conciergeService;
	}
	public String getTravelMode() {
		return travelMode;
	}
	public void setTravelMode(String travelMode) {
		this.travelMode = travelMode;
	}
	public String getIsBooking() {
		return isBooking;
	}
	public void setIsBooking(String isBooking) {
		this.isBooking = isBooking;
	}
	public String getBookingDetails() {
		return bookingDetails;
	}
	public void setBookingDetails(String bookingDetails) {
		this.bookingDetails = bookingDetails;
	}
	public String getIsAccommodation() {
		return isAccommodation;
	}
	public void setIsAccommodation(String isAccommodation) {
		this.isAccommodation = isAccommodation;
	}
	public String getAccommodationDetails() {
		return accommodationDetails;
	}
	public void setAccommodationDetails(String accommodationDetails) {
		this.accommodationDetails = accommodationDetails;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public String getPlaceFrom() {
		return placeFrom;
	}

	public void setPlaceFrom(String placeFrom) {
		this.placeFrom = placeFrom;
	}


	public String getStrUserType() {
		return strUserType;
	}


	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}


	public String getCurrUserType() {
		return currUserType;
	}


	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}


	public String getLeaveStatus() {
		return leaveStatus;
	}


	public void setLeaveStatus(String leaveStatus) {
		this.leaveStatus = leaveStatus;
	}


	public String getStrStartDate() {
		return strStartDate;
	}


	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}


	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
	}

	public String getTravelType() {
		return travelType;
	}

	public void setTravelType(String travelType) {
		this.travelType = travelType;
	}
	
	
}