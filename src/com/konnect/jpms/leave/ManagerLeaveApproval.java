package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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


public class ManagerLeaveApproval extends ActionSupport implements ServletRequestAware, IStatements {

    private static final long serialVersionUID = 1L;
    HttpSession session;
    CommonFunctions CF;
    
    String strUserType;
    String strSessionEmpId;
    
    String leaveId;
    String userId;
    String empId;
    String empName;
    String reason;
    String managerReason;
    String empNoOfLeave;
    String typeOfLeave;
//  String leaveFromTo;
//  String leaveToDate;
    String approvalFromTo;
    String approvalToDate;
    String entryDate;
    boolean isHalfDay;
    String strSession;
    List<FillHalfDaySession> strWorkingSession;
    int isapproved = 0;
    private String isCompensate;

    
//  List<FillUserType> userTypeList;
    List<FillLeaveType> leaveTypeList;
    List<FillApproval> approvalList;
    
    String strCompensatory;
    
    String work_flow_id;
    String type;
    
    String leaveStatus;
    String strStartDate;
	String strEndDate;
	String apType;
	String apStatus;
	String mReason;
	String userType;
    String currUserType; 
    public String execute() throws Exception {
    	
        session = request.getSession();
        CF = (CommonFunctions) session.getAttribute(CommonFunctions);
        if (CF == null) return LOGIN;
        
        request.setAttribute(PAGE, PManagerLeaveApproval);
        strUserType = (String) session.getAttribute(USERTYPE);
        strSessionEmpId = (String) session.getAttribute(EMPID);
        String strEdit = request.getParameter("E");
        String strDelete = request.getParameter("D");
        String strLevelId = request.getParameter("LID");
//      strCompensatory = request.getParameter("COMP");
        UtilityFunctions uF = new UtilityFunctions();
//        System.out.println("strEdit ===>> " + strEdit);
//        System.out.println("getApType() ===>> " + getApType());
//        System.out.println("getApStatus() ===>> " + getApStatus());
//        System.out.println("getType() ===>> " + getType());
        if (strEdit != null) {
            viewLeaveEntry(strEdit, strLevelId, strCompensatory);
            if(getApType()!=null && getApType().equals("auto")){
            	setIsapproved(uF.parseToInt(getApStatus())); 
            	updateLeaveEntry();
            	if (getType() != null && getType().equals("type")) {
 	                return DASHBOARD;
 	            } else {
 	                return UPDATE;
 	            }
            }else{
	            request.setAttribute(TITLE, TViewLeaveApproval);
	            if (getType() != null && getType().equals("type")) {
	                return "popup1";
	            } else {
	                return SUCCESS;
	            }
            }  
        }
        if (strDelete != null) {
            deleteLeaveEntry(strDelete);
            if (getType() != null && getType().equals("type")) {
                return DASHBOARD;
            }
            request.setAttribute(TITLE, TDeleteManagerLeaveApproval);
            return VIEW;
        }      

        if (getLeaveId() != null && getLeaveId().length() > 0) {
            updateLeaveEntry();
            if (getType() != null && getType().equals("type")) {
                return DASHBOARD;
            }
            request.setAttribute(TITLE, TEditManagerLeaveApproval);
            return UPDATE;
        }
        return loadLeaveEntry();
    }
    public String loadVidateLeaveEntry() {
        request.setAttribute(PAGE, PManagerLeaveApproval);
        request.setAttribute(TITLE, TViewLeaveApproval);
        approvalList = new FillApproval().fillApprovalDenied();
        return LOAD;
    }

    public String loadLeaveEntry() {

        request.setAttribute(PAGE, PManagerLeaveApproval);
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
//      setLeaveFromTo(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
//      setLeaveToDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, CF.getStrReportDateFormat()));
        setApprovalFromTo(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat()));
        setApprovalToDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat()));
        setEntryDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE, CF.getStrReportDateFormat()));
        return LOAD;
    }

    public void updateLeaveEntry() {

        Connection con = null;
        PreparedStatement pst = null;
        Database db = new Database();
        db.setRequest(request);
        UtilityFunctions uF = new UtilityFunctions();
        ResultSet rs = null;

        try {

            con = db.makeConnection(con);
            
            Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);	//added by parvez date: 18-03-2023
            Map<String, String> hmLeaveType = CF.getLeaveTypeCode(con);
            
            Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
            if(getmReason()!=null && !getmReason().trim().equals("") && !getmReason().trim().equalsIgnoreCase("NULL")){
            	setManagerReason(getmReason()); 
            }
            
            boolean flag = true;
            boolean flagAdmin = false;
            if (uF.parseToBoolean(CF.getIsWorkFlow())) {
//                pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_LEAVE+"'" +
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
                	//System.out.println("in strUserType=admin=="); 
                	
                    pst = con.prepareStatement("UPDATE emp_leave_entry SET is_approved=?,manager_reason=?, approval_from=?, approval_to_date=?, " +
                    		"emp_no_of_leave=?, user_id=?, approve_date=? WHERE leave_id=?");
                    pst.setInt(1, getIsapproved());
                    pst.setString(2,  getManagerReason());
                    if (getIsHalfDay()) {
                        pst.setDate(3, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
                        pst.setDate(4, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
                    } else {
                        pst.setDate(3, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
                        pst.setDate (4, uF.getDateFormat(getApprovalToDate(), DATE_FORMAT));
                    }
                    int nAppliedDays = 0;
                    if (getIsHalfDay()) {
                        pst.setDouble(5, 0.5);
                    } else {
                        nAppliedDays = uF.parseToInt(uF.dateDifference(getApprovalFromTo(),DATE_FORMAT, getApprovalToDate(), DATE_FORMAT,CF.getStrTimeZone()));
                        pst.setInt  (5, nAppliedDays);
                    }
                    pst.setInt(6, uF.parseToInt(strSessionEmpId)); 
                    pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
                    pst.setInt(8, uF.parseToInt(getLeaveId()));
                    pst.execute();
        			pst.close();
        			flagAdmin = true;
        			
                } else {
                    pst = con.prepareStatement("select work_flow_id from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_LEAVE+"' " +
                		" and is_approved=0 and emp_id=? and user_type_id=? order by work_flow_id");
                    pst.setInt(1, uF.parseToInt(getLeaveId()));
                    pst.setInt(2, uF.parseToInt(strSessionEmpId));
                    pst.setInt(3, uF.parseToInt(getUserType()));
//                    System.out.println("pst=====>"+pst);
                    rs = pst.executeQuery();
                    int work_id = 0;
                    while (rs.next()) {
                        work_id = rs.getInt("work_flow_id");
                        break;
                    }
        			rs.close();
        			pst.close();
                    
                    pst = con.prepareStatement("UPDATE work_flow_details SET is_approved=?,emp_id=?,approve_date=?,reason=? WHERE work_flow_id=?");
                    pst.setInt(1, getIsapproved());
                    pst.setInt(2, uF.parseToInt(strSessionEmpId));
                    pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
                    pst.setString(4, getManagerReason());
                    pst.setInt(5, work_id);
                    pst.execute();
        			pst.close();
                    
                    
                    pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_LEAVE+"' " +
                        " and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id=? " +
                        " and is_approved=0 and effective_type='"+WORK_FLOW_LEAVE+"' and member_position not in " +
                        " (select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_LEAVE+"' )) " +
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
                        pst = con.prepareStatement("UPDATE emp_leave_entry SET is_approved=?,manager_reason=?,approval_from=?, approval_to_date=?," +
                    		" emp_no_of_leave=?, user_id=?, approve_date=? WHERE leave_id=?");
                        pst.setInt(1, getIsapproved());
                        pst.setString(2, getManagerReason());
                        if (getIsHalfDay()) {
                            pst.setDate(3, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
                            pst.setDate(4, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
                        } else {
                            pst.setDate(3, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
                            pst.setDate(4, uF.getDateFormat(getApprovalToDate(), DATE_FORMAT));
                        }
                        int nAppliedDays = 0;
                        if (getIsHalfDay()) {
                            pst.setDouble(5, 0.5);
                        } else {
                            nAppliedDays = uF.parseToInt(uF.dateDifference(getApprovalFromTo(),DATE_FORMAT, getApprovalToDate(), DATE_FORMAT,CF.getStrTimeZone()));
                            pst.setInt(5, nAppliedDays);
                        }
                        pst.setInt(6, uF.parseToInt(strSessionEmpId));
                        pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
                        pst.setInt(8, uF.parseToInt(getLeaveId()));
//                        System.out.println("MLA/291---pst=="+pst);
                        pst.execute();
            			pst.close();
                    }
//                    else if (strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
//                        pst = con.prepareStatement("UPDATE emp_leave_entry SET is_approved=?,manager_reason=?, approval_from=?, approval_to_date=?, emp_no_of_leave=?, user_id=? WHERE leave_id=?");
//                        pst.setInt(1, getIsapproved());
//                        pst.setString(2,  getManagerReason());
//                        if (getIsHalfDay()) {
//                            pst.setDate(3, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
//                            pst.setDate(4, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
//                        } else {
//                            pst.setDate(3, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
//                            pst.setDate(4, uF.getDateFormat(getApprovalToDate(), DATE_FORMAT));
//                        }
//                        int nAppliedDays = 0;
//                        if (getIsHalfDay()) {
//                            pst.setDouble(5, 0.5);
//                        } else {
//                            nAppliedDays = uF.parseToInt(uF.dateDifference(getApprovalFromTo(),DATE_FORMAT, getApprovalToDate(), DATE_FORMAT));
//                            pst.setInt(5, nAppliedDays);
//                        }
//                        pst.setInt(6, uF.parseToInt(strSessionEmpId));
//                        pst.setInt(7, uF.parseToInt(getLeaveId()));
//                        pst.execute();
//            			pst.close();
//                    } 
                    else if (getIsapproved() == -1) {
                        pst = con.prepareStatement("UPDATE emp_leave_entry SET is_approved=?,manager_reason=?, approval_from=?, approval_to_date=?," +
                        		" emp_no_of_leave=?, user_id=?, approve_date=? WHERE leave_id=?");
                        pst.setInt(1, getIsapproved());
                        pst.setString(2,  getManagerReason());
                        if (getIsHalfDay()) {
                            pst.setDate(3, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
                            pst.setDate(4, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
                        } else {
                            pst.setDate(3, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
                            pst.setDate(4, uF.getDateFormat(getApprovalToDate(), DATE_FORMAT));
                        }
                        int nAppliedDays = 0;
                        if (getIsHalfDay()) {
                            pst.setDouble(5, 0.5);
                        } else {
                            nAppliedDays = uF.parseToInt(uF.dateDifference(getApprovalFromTo(),DATE_FORMAT, getApprovalToDate(), DATE_FORMAT,CF.getStrTimeZone()));
                            pst.setInt(5, nAppliedDays);
                        }
                        pst.setInt(6, uF.parseToInt(strSessionEmpId));
                        pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
                        pst.setInt(8, uF.parseToInt(getLeaveId()));
                        pst.execute();
            			pst.close();
                    }
                } 
            } else {
                if (strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
                    pst = con.prepareStatement("UPDATE emp_leave_entry SET is_approved=?,manager_reason=?, approval_from=?, approval_to_date=?, " +
                    		"emp_no_of_leave=?, user_id=?, approve_date=? WHERE leave_id=?");
                    pst.setInt(1, getIsapproved());
                    pst.setString(2,getManagerReason());
                    if (getIsHalfDay()) {
                        pst.setDate(3, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
                        pst.setDate(4, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
                    } else {
                        pst.setDate(3, uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT));
                        pst.setDate(4, uF.getDateFormat(getApprovalToDate(), DATE_FORMAT));
                    }
                    int nAppliedDays = 0;
                    if (getIsHalfDay()) {
                        pst.setDouble(5, 0.5);
                    } else {
                        nAppliedDays = uF.parseToInt(uF.dateDifference(getApprovalFromTo(),DATE_FORMAT, getApprovalToDate(), DATE_FORMAT,CF.getStrTimeZone()));
                        pst.setInt(5, nAppliedDays);
                    }
                    pst.setInt(6, uF.parseToInt(strSessionEmpId)); 
                    pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
                    pst.setInt(8, uF.parseToInt(getLeaveId()));
                    pst.execute();
        			pst.close();
        			flagAdmin = true;
                }
            }
            request.setAttribute(MESSAGE, getEmpId() + " updated successfully!");
           
            String strDomain = request.getServerName().split("\\.")[0];
//            System.out.println("before status====>"+getIsapproved()+"---flag==>"+flag);
            if (flagAdmin || flag || getIsapproved() == -1) {
//            	System.out.println("if status====>"+getIsapproved()+"---flag==>"+flag);
            	if(uF.parseToInt(getLeaveId())>0){
            		int levelId = uF.parseToInt((String) hmEmpLevel.get(getEmpId()));
                 	insertLeaveBalance(con,pst,rs,uF,levelId,CF);
                }
            	Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
            	
            	String strApproveDeny = "approved";
				if(getIsapproved()== -1) {
					strApproveDeny = "denied";
				}
				
				String strDate = "";
				if (getIsHalfDay()) {
					strDate = "date " +uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, CF.getStrReportDateFormat());
                } else {
                	strDate = "date " +uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, CF.getStrReportDateFormat())+" to "+uF.getDateFormat(getApprovalToDate(), DATE_FORMAT, CF.getStrReportDateFormat());
                }
				String strLeaveLbl = "leave";
				if(uF.parseToBoolean(getIsCompensate())) {
					strLeaveLbl = "extra working";
				}
		//===start parvez date: 06-09-2022===		
				String alertData = "<div style=\"float: left;\"> Your "+strLeaveLbl+" "+strDate+" has been "+strApproveDeny+" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
				String alertAction = "MyTime.action?pType=WR&callFrom=MyDashLeaveSummary";
		//===end parvez date: 06-09-2022===		
				
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
//				userAlerts.set_type(LEAVE_APPROVAl_ALERT);
//				userAlerts.setStatus(INSERT_ALERT);
//				Thread t = new Thread(userAlerts);
//				t.run();
            } 
//            else {
//            	if (getIsapproved()==1 && flag){ 
////                	System.out.println("else status====>"+getIsapproved()+"---flag==>"+flag);
//                	 if(uF.parseToInt(getLeaveId())>0){
//                		 int levelId = uF.parseToInt((String) hmEmpLevel.get(getEmpId()));
//                     	insertLeaveBalance(con,pst,rs,uF,levelId,CF);
//                     }            	
//    	            UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//    				userAlerts.setStrDomain(strDomain);
//    				userAlerts.setStrEmpId(getEmpId());
//    				userAlerts.set_type(LEAVE_APPROVAl_ALERT);
//    				userAlerts.setStatus(INSERT_ALERT);
//    				Thread t = new Thread(userAlerts);
//    				t.run();
//                }
//            }
            
            if (!uF.parseToBoolean(getStrCompensatory())) {
            	
            	double approveDays = 0.5d;
            	if (!getIsHalfDay()) {
					pst = con.prepareStatement("select count(leave_register_id) as cnt from leave_application_register where emp_id=? and leave_id=? " +
							"and leave_id in (select leave_id from emp_leave_entry where emp_id=? and leave_id=?)");
					pst.setInt(1, uF.parseToInt(getEmpId()));
					pst.setInt(2, uF.parseToInt(getLeaveId()));
					pst.setInt(3, uF.parseToInt(getEmpId()));
					pst.setInt(4, uF.parseToInt(getLeaveId()));
					rs = pst.executeQuery();
					while(rs.next()){
						approveDays = uF.parseToDouble(rs.getString("cnt"));
					}
					rs.close();
					pst.close();
            	}
             
            	String strSupervisorName = CF.getEmpNameMapByEmpId(con, (String)session.getAttribute(EMPID));
            	
           //===start parvez date: 18-03-2023===
            	String strBackupEmpName = "";
            	if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_BACKUP_EMPLOYEE_FOR_LEAVE))){
            		pst = con.prepareStatement("select backup_emp_name from emp_leave_entry WHERE leave_id=?");
            		pst.setInt(1, uF.parseToInt(getLeaveId()));
            		rs = pst.executeQuery();
            		while(rs.next()){
            			strBackupEmpName = rs.getString("backup_emp_name");
            		}
            		rs.close();
					pst.close();
            	}
           //===end parvez date: 18-03-2023===	
            	
            	String strLeaveNotiLbl = N_EMPLOYEE_LEAVE_APPROVAL+"";
				if(uF.parseToBoolean(getIsCompensate())) {
					strLeaveNotiLbl = N_EMPLOYEE_EXTRA_WORK_APPROVAL+"";
				}
				
                Notifications nF = new Notifications(uF.parseToInt(strLeaveNotiLbl), CF); 
                nF.setDomain(strDomain);
				nF.request = request;
//                nF.setSupervisor(true);
                nF.setStrEmpId(getEmpId());
//                nF.setStrHostAddress(request.getRemoteHost());
                nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
                nF.setStrContextPath(request.getContextPath());
                nF.setStrEmpLeaveFrom(uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, CF.getStrReportDateFormat()));
                nF.setStrEmpLeaveTo(uF.getDateFormat(getApprovalToDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
//                nF.setStrEmpLeaveNoOfDays(uF.dateDifference(getApprovalFromTo(),DATE_FORMAT, getApprovalToDate(), DATE_FORMAT));
                nF.setStrEmpLeaveNoOfDays(""+approveDays);
                nF.setStrEmpLeaveReason(getReason());
                nF.setStrManagerLeaveReason(getManagerReason());
                if (getIsapproved()==1) {
                    nF.setStrApprvedDenied("approved");
                } else {
                    nF.setStrApprvedDenied("denied");
                }
                nF.setStrManagerName(uF.showData(strSupervisorName, ""));
                nF.setStrLeaveTypeName(uF.showData(hmLeaveType.get(getTypeOfLeave()), ""));
				nF.setStrEffectiveDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT_STR));
			//===start parvez date: 18-03-2023===	
				if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_BACKUP_EMPLOYEE_FOR_LEAVE))){
					nF.setStrLeaveEmpBackup(uF.showData(strBackupEmpName, ""));
				}
			//===end parvez date: 18-03-2023===	
				
				nF.setEmailTemplate(true);
                nF.sendNotifications();
                
            }
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(MESSAGE, "Error in updation");
        } finally {
            db.closeResultSet(rs);
            db.closeStatements(pst);
            db.closeConnection(con);
        }
    }
    
    public void insertLeaveBalance(Connection con, PreparedStatement pst,ResultSet rs, UtilityFunctions uF,int levelId,CommonFunctions CF) {
        try{
//        	System.out.println("MLA/502--insertLeaveBalance");
        
        	String strLevelId = CF.getEmpLevelId(con, getEmpId());
        	
            pst = con.prepareStatement("select org_id,wlocation_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.emp_id=?");
            pst.setInt(1,uF.parseToInt(getEmpId()));
            rs = pst.executeQuery();
            int org_id = 0;
            int wlocation = 0;
            while (rs.next()) {
                org_id = uF.parseToInt(rs.getString("org_id"));
                wlocation = uF.parseToInt(rs.getString("wlocation_id"));
            }
			rs.close();
			pst.close();
			
            pst = con.prepareStatement("select is_compensate from emp_leave_entry where leave_id=?");
            pst.setInt(1, uF.parseToInt(getLeaveId()));
            rs = pst.executeQuery();
            boolean iscompensate = false;
            while (rs.next()) {
                iscompensate = uF.parseToBoolean(rs.getString("is_compensate"));
            }
			rs.close();
			pst.close();
            
            int isSandwichLeave = 0;
            boolean isConstantBalance = false;
            boolean isPaid = false;
            boolean isCompensatary = false;
            boolean isWorkFromHome = false;
            boolean isLongLeave = false;
            int compensate_with = 0;
            List<String> prefix = new ArrayList<String>();
            List<String> suffix = new ArrayList<String>();
            List<String> sandwichleavetype = new ArrayList<String>();
            double dblMontlyLimit = 0;
            double dblLongLeaveLimit = 0;
            
            Map<String,String> hmFeatureStatus = CF.getFeatureStatusMap(con);
            if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
            
            pst = con.prepareStatement("select * from emp_leave_type where leave_type_id=? and level_id=? and org_id=? and wlocation_id=?");
            pst.setInt(1, uF.parseToInt(getTypeOfLeave()));
            pst.setInt(2, levelId);
            pst.setInt(3, org_id);
            pst.setInt(4, wlocation);
//            System.out.println("MAL/548--pst====>"+pst);
            rs = pst.executeQuery();
            while (rs.next()) {
                isSandwichLeave = uF.parseToInt(rs.getString("sandwich_type"));
                isConstantBalance = uF.parseToBoolean(rs.getString("is_constant_balance"));
                isCompensatary = rs.getBoolean("is_compensatory");
                isWorkFromHome = rs.getBoolean("is_work_from_home");
                compensate_with = rs.getInt("compensate_with");
                isPaid = uF.parseToBoolean(rs.getString("is_paid"));
                if (rs.getString("sandwich_leave_type") != null){
                    sandwichleavetype = Arrays.asList(rs.getString("sandwich_leave_type").split(","));
                }
                if (rs.getString("leave_suffix") != null) {
                    suffix=Arrays.asList(rs.getString("leave_suffix").split(","));
                }
				if (rs.getString("leave_prefix") != null) {
					prefix = Arrays.asList(rs.getString("leave_prefix").split(","));
				}
				dblMontlyLimit = uF.parseToDouble(rs.getString("monthly_limit"));
				
				isLongLeave = uF.parseToBoolean(rs.getString("is_long_leave"));
				dblLongLeaveLimit = uF.parseToDouble(rs.getString("long_leave_limit"));
            }
			rs.close();
			pst.close();
//			System.out.println("isWorkFromHome ===>> " + isWorkFromHome);
			
//          System.out.println("isSandwichLeave====>"+ isSandwichLeave + "---sandwichleavetype====>"+sandwichleavetype);
//			System.out.println("getIsapproved====>"+ getIsapproved() + "---iscompensate====>"+iscompensate+ "---isWorkFromHome ====>"+isWorkFromHome);
            if (getIsapproved()==1 && !iscompensate && !isWorkFromHome) {
                if (isPaid) {
                    pst = con.prepareStatement("UPDATE emp_leave_entry SET ispaid=true WHERE leave_id=?");
                    pst.setInt(1, uF.parseToInt(getLeaveId()));
                    pst.execute();
        			pst.close();
                }
                
                double dblLeavesApproved = 0;
                if (getIsHalfDay()) {
                    dblLeavesApproved = 0.5;
                } else {
//                    dblLeavesApproved = uF.parseToInt(uF.dateDifference(getApprovalFromTo(), DATE_FORMAT, getApprovalToDate(), DATE_FORMAT));
                	String strDateDiff = uF.dateDifference(getApprovalFromTo(), DATE_FORMAT, getApprovalToDate(), DATE_FORMAT,CF.getStrTimeZone());
                 	strDateDiff = strDateDiff !=null && !strDateDiff.trim().equals("") && !strDateDiff.trim().equalsIgnoreCase("NULL") ? strDateDiff.trim() : "0"; 
                    dblLeavesApproved = uF.parseToDouble(strDateDiff);
                }
                
//                System.out.println("leaveId====>"+getLeaveId()+"---getApprovalFromTo====>"+getApprovalFromTo()
//                		+"---getApprovalToDate====>"+getApprovalToDate()+"---dblLeavesApproved====>"+dblLeavesApproved
//                		+"---dblLeavesApproved====>"+dblLeavesApproved+"---diff====>"+uF.dateDifference(getApprovalFromTo(), DATE_FORMAT, getApprovalToDate(), DATE_FORMAT)
//                		+"---convert====>"+uF.parseToInt(uF.dateDifference(getApprovalFromTo(), DATE_FORMAT, getApprovalToDate(), DATE_FORMAT)));
                
                Calendar cal = GregorianCalendar.getInstance();
                cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, "dd")));
                cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, "MM")) - 1);
                cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, "yyyy")));
                
                Calendar cal2 = GregorianCalendar.getInstance();
                cal2.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(getApprovalToDate(), DATE_FORMAT, "dd")));
                cal2.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getApprovalToDate(), DATE_FORMAT, "MM")) - 1);
                cal2.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getApprovalToDate(), DATE_FORMAT, "yyyy")));
                
//                double dblCount = 0;
//                boolean isPaid1 = false;
                double dblLeaveDed = 0;
//                double dblBalance1 = dblBalance;
                
                
                if(isLongLeave && dblLongLeaveLimit > 0 && dblLeavesApproved >= dblLongLeaveLimit){
//                	System.out.println("in long leave");
                	for (int i = 0; i < dblLeavesApproved; i++) {
						Date dtCurrent = uF.getDateFormat(cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR), DATE_FORMAT);
						String strDate = cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR);
						cal.add(Calendar.DATE, 1);
						
//						pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
//		                		"and _date = (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
//		                		"and leave_type_id=? and  _type='C') and  _type='C' and _date<= ?");
						pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
		                		"and register_id in (select max(register_id) from leave_register1 where _date<= ? and emp_id = ? " +
		                		"and leave_type_id=? and  _type='C') and  _type='C' and _date<= ?");
		                pst.setInt(1, uF.parseToInt(getEmpId()));
		                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
		                pst.setDate(3, dtCurrent);
		                pst.setInt(4, uF.parseToInt(getEmpId()));
		                pst.setInt(5, uF.parseToInt(getTypeOfLeave()));
		                pst.setDate(6, dtCurrent);
//		                System.out.println("pst====>"+pst);
		                rs = pst.executeQuery();
		                double dblBalance = 0;
		                String balanceDate=null;
		                while (rs.next()) {
		                    dblBalance = uF.parseToDouble(rs.getString("balance"));
		                    balanceDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
		                }
		    			rs.close();
		    			pst.close();
						
//						pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
//		                		"and register_id >(select max(register_id) as register_id from leave_register1 where _type='C' and emp_id =? " +
//		                		"and leave_type_id=?) and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
//		                		"and leave_type_id=? and  _type='C') and _date<= ?");
//		                pst.setInt(1, uF.parseToInt(getEmpId()));
//		                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
//		                pst.setInt(3, uF.parseToInt(getEmpId()));
//		                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
//		                pst.setDate(5, dtCurrent);
//		                pst.setInt(6, uF.parseToInt(getEmpId()));
//		                pst.setInt(7, uF.parseToInt(getTypeOfLeave()));
//		                pst.setDate(8, dtCurrent);
		    			
		    			pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
		                		"and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
		                		"and leave_type_id=? and  _type='C') and _date<= ?");
		                pst.setInt(1, uF.parseToInt(getEmpId()));
		                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
		                pst.setDate(3, dtCurrent);
		                pst.setInt(4, uF.parseToInt(getEmpId()));
		                pst.setInt(5, uF.parseToInt(getTypeOfLeave()));
		                pst.setDate(6, dtCurrent);
//		                System.out.println("pst====>"+pst);
		                rs = pst.executeQuery();
		                while (rs.next()) {
		                    dblBalance += uF.parseToDouble(rs.getString("accrued"));
		                }
		    			rs.close();
		    			pst.close();
		                
		    			double dblPaidBalance = 0;
		    			pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
		    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
		    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
		    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
		                pst.setInt(1, uF.parseToInt(getEmpId()));
		                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
		                pst.setInt(3, uF.parseToInt(getEmpId()));
		                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
		                pst.setInt(5, uF.parseToInt(getEmpId()));
		                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
		                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
		                pst.setInt(8, uF.parseToInt(getEmpId()));
		                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
		                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
		                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
		//              System.out.println("pst====>"+pst);
		                rs = pst.executeQuery();
		                String balanceNextDate=null;
		                boolean flagNextDate = false;
		                while (rs.next()) {
		                	balanceNextDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
		                	flagNextDate = true;
		                }
		    			rs.close();
		    			pst.close();
    			
		    			if(flagNextDate){
		    				pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null " +
		    						"or is_modify=false) and emp_id=? and leave_type_id=? and _date >=? and _date<?");
			                pst.setInt(1, uF.parseToInt(getEmpId()));
			                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
			                pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
			                pst.setDate(4, uF.getDateFormat(balanceNextDate, DATE_FORMAT));
		//		                System.out.println("pst====>"+pst);
			                rs = pst.executeQuery();
			                while (rs.next()) {
			                	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
			                }
			    			rs.close();
			    			pst.close();
		    			} else {
			    			pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null or is_modify=false)" +
			    					" and emp_id=? and leave_type_id=? and _date >= ?");
			                pst.setInt(1, uF.parseToInt(getEmpId()));
			                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
			                pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
	//		                System.out.println("pst====>"+pst);
			                rs = pst.executeQuery();
			                while (rs.next()) {
			                	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
			                }
			    			rs.close();
			    			pst.close();
		    			}
		    			
		                if(dblBalance > 0 && dblBalance >= dblPaidBalance){
		                	dblBalance = dblBalance - dblPaidBalance; 
		                }
						
		                double dblBalance1 = dblBalance;
		                
		                double dblCount = 0;
		                boolean isPaid1 = false;
						
						if (getIsHalfDay()) {
							dblCount += 0.5;
						} else {

							if ((dblBalance - dblCount) == 0.5) {
								dblCount += 0.5;
							} else {
								dblCount++;
							}

						}
	
						if (dblBalance >= dblCount && isPaid) {
							isPaid1 = true;

							if (getIsHalfDay()) {
								dblBalance1 -= 0.5;
								dblLeaveDed = 0.5;
							} else {

								if (dblBalance1 >= 1) {
									dblBalance1 -= 1;
									dblLeaveDed = 1;
								} else if (dblBalance1 >= 0.5) {
									dblBalance1 -= 0.5;
									dblLeaveDed = 0.5;
								} else {
									dblLeaveDed = 0;
								}
							}
						} else {
							/*isPaid1 = false;
							if (getIsHalfDay()) {
								dblLeaveDed = 0.5;
							} else {
								dblLeaveDed = 1;
							}*/
							
							if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_COLOR_FOR_NIGATIVE_LEAVE_BALANCE))){
								isPaid1 = false;
								if (getIsHalfDay()) {
									dblLeaveDed = -0.5;
								} else {
									dblLeaveDed = -1;
								}
							} else{
								isPaid1 = false;
								if (getIsHalfDay()) {
									dblLeaveDed = 0.5;
								} else {
									dblLeaveDed = 1;
								}
							}

						}
						if (isConstantBalance) {
							isPaid1 = true;
						}
	//						else if (isPaid1 && !isConstantBalance && dblMontlyLimit>0 && (leave+(i+1))>dblMontlyLimit) {
	//							isPaid1 = false;
	//						}
						pst = con.prepareStatement("update  leave_application_register set leave_type_id=?, leave_id=?, leave_no=?, is_paid=?, "
								+ "balance=?, _type=?,is_modify=?,is_long_leave=? where _date=? and emp_id=?");
			
						pst.setInt(1, uF.parseToInt(getTypeOfLeave()));
						pst.setInt(2, uF.parseToInt(getLeaveId()));
						pst.setDouble(3, dblLeaveDed);
						pst.setBoolean(4, isPaid1);
						pst.setDouble(5, dblBalance1);
						pst.setBoolean(6, true);
						pst.setBoolean(7, false);
						pst.setBoolean(8, true);
						pst.setDate(9, dtCurrent);
						pst.setInt(10, uF.parseToInt(getEmpId()));
						int update = pst.executeUpdate();
//						System.out.println("update ===>> " + update + " --- pst ===>> " + pst);
						pst.close();
								
						if(update==0) {
							pst = con.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, "
								+ "is_paid, balance, _type,is_modify,is_long_leave) values (?,?,?,?,?,?,?,?,?,?)");
							pst.setDate(1, dtCurrent);
							pst.setInt(2, uF.parseToInt(getEmpId()));
							pst.setInt(3, uF.parseToInt(getTypeOfLeave()));
							pst.setInt(4, uF.parseToInt(getLeaveId()));
							pst.setDouble(5, dblLeaveDed);
							pst.setBoolean(6, isPaid1);
							pst.setDouble(7, dblBalance1);
							pst.setBoolean(8, true);
							pst.setBoolean(9, false);
							pst.setBoolean(10, true);
							pst.execute();
							pst.close();
						}
					} 
                	
	            } else {
//	            	System.out.println("not in long leave====>");
//	                String weeklyoff1=null;
//	                String weeklyoff2=null;
//	                String weeklyoff3=null;
//	                
//	                String weeklyoff1type=null;
//	                String weeklyoff2type=null;
//	                String weeklyoff3type=null;
//	                String wlocation_weeknos3=null;	                
//	                
//	                pst = con.prepareStatement("select * from work_location_info where wlocation_id=(select wlocation_id from employee_official_details where emp_id=?)");
//	                pst.setInt(1, uF.parseToInt(getEmpId()));
////	                pst = con.prepareStatement("select * from level_details where level_id=?");
////	                pst.setInt(1, levelId);
//	                rs = pst.executeQuery();
//	                while (rs.next()) {
//	                    weeklyoff1=rs.getString("wlocation_weeklyoff1");
//	                    weeklyoff2=rs.getString("wlocation_weeklyoff2");
//	                    weeklyoff3=rs.getString("wlocation_weeklyoff3");
//	
//	                    weeklyoff1type=rs.getString("wlocation_weeklyofftype1");
//	                    weeklyoff2type=rs.getString("wlocation_weeklyofftype2");
//	                    weeklyoff3type=rs.getString("wlocation_weeklyofftype3");
//	                    wlocation_weeknos3=rs.getString("wlocation_weeknos3");
//	                }
//	    			rs.close();
//	    			pst.close();
	                
	                double leave=0;
//	                List<String> adDay = null;
//	                if (wlocation_weeknos3 != null) {
//	                    adDay = Arrays.asList(wlocation_weeknos3.split(","));
//	                }
//	                if (adDay == null) adDay = new ArrayList<String>();
//	                System.out.println("getIsHalfDay()=="+getIsHalfDay());
					
					if (isSandwichLeave == 2 && !getIsHalfDay()) {
	
						// prefix
						Calendar cal1 = (Calendar) cal.clone();
						boolean prefixFlag = false;
	
						while (true) {
							cal1.add(Calendar.DATE, -1);
							String strDate = cal1.get(Calendar.DATE) + "/" + (cal1.get(Calendar.MONTH) + 1) + "/" + cal1.get(Calendar.YEAR);
							Date dtCurrent1 = uF.getDateFormat(cal1.get(Calendar.DATE) + "/" + (cal1.get(Calendar.MONTH) + 1) + "/" + cal1.get(Calendar.YEAR),
									DATE_FORMAT);
							String day = uF.getDateFormat(strDate, DATE_FORMAT, "EEEE");
	
	
							boolean flag1 = false;
	
							pst = con.prepareStatement("select atten_id from attendance_details where to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and emp_id=?");
							pst.setDate(1, dtCurrent1);
							pst.setInt(2, uF.parseToInt(getEmpId()));
							rs = pst.executeQuery();
							while (rs.next()) {
								flag1 = true;
							}
							rs.close();
							pst.close();
	
							if (!flag1) {
								boolean flag2 = false;
								pst = con.prepareStatement("select leave_register_id from leave_application_register where _date=?  and emp_id=?");
								pst.setDate(1, dtCurrent1);
								pst.setInt(2, uF.parseToInt(getEmpId()));
								rs = pst.executeQuery();
								while (rs.next()) {
									flag2 = true;
								}
								rs.close();
								pst.close();
	
								if (!flag2) {
//									pst = con
//											.prepareStatement("select holiday_id from holidays where _date=? and wlocation_id=(select wlocation_id from employee_official_details where emp_id=?) and (is_optional_holiday is null or is_optional_holiday=false)");
//									pst.setDate(1, dtCurrent1);
//									pst.setInt(2, uF.parseToInt(getEmpId()));
//									rs = pst.executeQuery();
//									while (rs.next()) {
//										prefixFlag = true;
//									}
//									rs.close();
//									pst.close();
//	
//									if (!prefixFlag) {
//										if (weeklyoff1 != null && weeklyoff1.equalsIgnoreCase(day)) {
//											prefixFlag = true;
//										}
//										if (!prefixFlag && weeklyoff2 != null && weeklyoff2.equalsIgnoreCase(day)) {
//											prefixFlag = true;
//										}
//										if (!prefixFlag && weeklyoff3 != null && weeklyoff3.equalsIgnoreCase(day)) {
//											int checkWeek = getMonthCont(new UtilityFunctions(), strDate);
//											if (adDay.contains("" + checkWeek)) {
//												prefixFlag = true;
//											}
//										}
//									}
									
									boolean isEmpRosterWeekOff = CF.checkEmpRosterWeeklyOff(con, CF, uF, getEmpId(), uF.getDateFormat(""+dtCurrent1, DBDATE, DATE_FORMAT), strLevelId, ""+wlocation, ""+org_id);
									if(isEmpRosterWeekOff){
										prefixFlag = true;
									} else {
										boolean isHoliday = CF.checkHoliday(con, uF, uF.getDateFormat(""+dtCurrent1, DBDATE, DATE_FORMAT), ""+wlocation, ""+org_id); 
										if(isHoliday){
											prefixFlag = true;
										}
									}									
								}
							}
							if (prefixFlag) {
								break;
							} else {
								break;
							}
	
						}
	
						// ===========================Suffix===========================
	
						Calendar cal3 = (Calendar) cal2.clone();
						boolean suffixFlag = false;
						while (true) {
							cal3.add(Calendar.DATE, 1);
							String strDate = cal3.get(Calendar.DATE) + "/" + (cal3.get(Calendar.MONTH) + 1) + "/" + cal3.get(Calendar.YEAR);
							Date dtCurrent1 = uF.getDateFormat(cal3.get(Calendar.DATE) + "/" + (cal3.get(Calendar.MONTH) + 1) + "/" + cal3.get(Calendar.YEAR),
									DATE_FORMAT);
							String day = uF.getDateFormat(strDate, DATE_FORMAT, "EEEE");
	
							boolean flag1 = false;
	
							pst = con.prepareStatement("select atten_id from attendance_details where to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and emp_id=?");
							pst.setDate(1, dtCurrent1);
							pst.setInt(2, uF.parseToInt(getEmpId()));
							rs = pst.executeQuery();
							while (rs.next()) {
								flag1 = true;
							}
							rs.close();
							pst.close();
	
							if (!flag1) {
	
								boolean flag2 = false;
								pst = con.prepareStatement("select leave_register_id from leave_application_register where _date=?  and emp_id=?");
								pst.setDate(1, dtCurrent1);
								pst.setInt(2, uF.parseToInt(getEmpId()));
								rs = pst.executeQuery();
								while (rs.next()) {
									flag2 = true;
								}
								rs.close();
								pst.close();
								
								if (!flag2) {
//									pst = con
//											.prepareStatement("select holiday_id from holidays where _date=? and wlocation_id=(select wlocation_id from employee_official_details where emp_id=?) and (is_optional_holiday is null or is_optional_holiday=false)");
//									pst.setDate(1, dtCurrent1);
//									pst.setInt(2, uF.parseToInt(getEmpId()));
//									rs = pst.executeQuery();
//									while (rs.next()) {
//										suffixFlag = true;
//									}
//									rs.close();
//									pst.close();
									
									boolean isEmpRosterWeekOff = CF.checkEmpRosterWeeklyOff(con, CF, uF, getEmpId(), uF.getDateFormat(""+dtCurrent1, DBDATE, DATE_FORMAT), strLevelId, ""+wlocation, ""+org_id);
									if(isEmpRosterWeekOff){
										suffixFlag = true;
									} else {
										boolean isHoliday = CF.checkHoliday(con, uF, uF.getDateFormat(""+dtCurrent1, DBDATE, DATE_FORMAT), ""+wlocation, ""+org_id); 
										if(isHoliday){
											suffixFlag = true;
										}
									}
									
								}
							}
	
//							if (!suffixFlag) {
//								if (weeklyoff1 != null && weeklyoff1.equalsIgnoreCase(day)) {
//									suffixFlag = true;
//								}
//								if (!prefixFlag && weeklyoff2 != null && weeklyoff2.equalsIgnoreCase(day)) {
//									suffixFlag = true;
//								}
//								if (!prefixFlag && weeklyoff3 != null && weeklyoff3.equalsIgnoreCase(day)) {
//									int checkWeek = getMonthCont(new UtilityFunctions(), strDate);
//									if (adDay.contains("" + checkWeek)) {
//										suffixFlag = true;
//									}
//								}
//							}
	
							if (suffixFlag) {
								break;
							} else {
								break;
							}
						}
	
						
						if (prefixFlag && suffixFlag) {
							Calendar cal4 = (Calendar) cal.clone();
							while (true) {
								cal4.add(Calendar.DATE, -1);
								String strDate = cal4.get(Calendar.DATE) + "/" + (cal4.get(Calendar.MONTH) + 1) + "/" + cal4.get(Calendar.YEAR);
								Date dtCurrent1 = uF.getDateFormat(cal4.get(Calendar.DATE) + "/" + (cal4.get(Calendar.MONTH) + 1) + "/" + cal4.get(Calendar.YEAR),
										DATE_FORMAT);
								String day = uF.getDateFormat(strDate, DATE_FORMAT, "EEEE");
	
								boolean flag = false;
								boolean flag1 = false;
	
								String prefix_type = "";
	
								pst = con.prepareStatement("select atten_id from attendance_details where to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and emp_id=?");
								pst.setDate(1, dtCurrent1);
								pst.setInt(2, uF.parseToInt(getEmpId()));
								rs = pst.executeQuery();
								while (rs.next()) {
									flag1 = true;
								}
								rs.close();
								pst.close();
	
								if (!flag1) {
									boolean flag2 = false;
									pst = con.prepareStatement("select leave_register_id from leave_application_register where _date=?  and emp_id=?");
									pst.setDate(1, dtCurrent1);
									pst.setInt(2, uF.parseToInt(getEmpId()));
									rs = pst.executeQuery();
									while (rs.next()) {
										flag2 = true;
									}
									rs.close();
									pst.close();
	
									if (!flag2) {
//											pst = con.prepareStatement("select holiday_id from holidays where _date=? and wlocation_id=(select wlocation_id from employee_official_details where emp_id=?) and (is_optional_holiday is null or is_optional_holiday=false)");
//											pst.setDate(1, dtCurrent1);
//											pst.setInt(2, uF.parseToInt(getEmpId()));
//											rs = pst.executeQuery();
//											while (rs.next()) {
//												flag = true;
//												prefix_type = "H";
//											}
//											rs.close();
//											pst.close();
//	
//											if (!flag) {
//												if (weeklyoff1 != null && weeklyoff1.equalsIgnoreCase(day)) {
//													if (weeklyoff1type != null && weeklyoff1type.equalsIgnoreCase("HD")) {
//														dblLeaveDed = 0.5;
//													} else {
//														dblLeaveDed = 1;
//													}
//													prefix_type = "WO";
//													flag = true;
//												}
//												if (!flag && weeklyoff2 != null && weeklyoff2.equalsIgnoreCase(day)) {
//													if (weeklyoff2type != null && weeklyoff2type.equalsIgnoreCase("HD")) {
//														dblLeaveDed = 0.5;
//													} else {
//														dblLeaveDed = 1;
//													}
//													prefix_type = "WO";
//													flag = true;
//												}
//												if (!flag && weeklyoff3 != null && weeklyoff3.equalsIgnoreCase(day)) {
//													int checkWeek = getMonthCont(new UtilityFunctions(), strDate);
//													if (adDay.contains("" + checkWeek)) {
//														if (weeklyoff3type != null && weeklyoff3type.equalsIgnoreCase("HD")) {
//															dblLeaveDed = 0.5;
//														} else {
//															dblLeaveDed = 1;
//														}
//														prefix_type = "WO";
//														flag = true;
//													}
//												}
//										}
										
										boolean isEmpRosterWeekOff = CF.checkEmpRosterWeeklyOff(con, CF, uF, getEmpId(), uF.getDateFormat(""+dtCurrent1, DBDATE, DATE_FORMAT), strLevelId, ""+wlocation, ""+org_id);
										if(isEmpRosterWeekOff){
											dblLeaveDed = 1;
											prefix_type = "WO";
											flag = true;
										} else {
											boolean isHoliday = CF.checkHoliday(con, uF, uF.getDateFormat(""+dtCurrent1, DBDATE, DATE_FORMAT), ""+wlocation, ""+org_id); 
											if(isHoliday){
												dblLeaveDed = 1;
												flag = true;
												prefix_type = "H";
											}
										}
									}
								}
								if (flag) {
									
//									pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
//						                		"and _date = (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
//						                		"and leave_type_id=? and  _type='C') and  _type='C' and _date<= ?");
									pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
					                		"and register_id in (select max(register_id) from leave_register1 where _date<= ? and emp_id = ? " +
					                		"and leave_type_id=? and  _type='C') and  _type='C' and _date<= ?");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
					                pst.setInt(4, uF.parseToInt(getEmpId()));
					                pst.setInt(5, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(6, uF.getDateFormat(strDate, DATE_FORMAT));
					                rs = pst.executeQuery();
					                double dblBalance = 0;
					                String balanceDate=null;
					                while (rs.next()) {
					                    dblBalance = uF.parseToDouble(rs.getString("balance"));
					                    balanceDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
					                }
					    			rs.close();
					    			pst.close();
									
//									pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
//					                		"and register_id >(select max(register_id) as register_id from leave_register1 where _type='C' and emp_id =? " +
//					                		"and leave_type_id=?) and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
//					                		"and leave_type_id=? and  _type='C') and _date<= ?");
//					                pst.setInt(1, uF.parseToInt(getEmpId()));
//					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
//					                pst.setInt(3, uF.parseToInt(getEmpId()));
//					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
//					                pst.setDate(5, uF.getDateFormat(strDate, DATE_FORMAT));
//					                pst.setInt(6, uF.parseToInt(getEmpId()));
//					                pst.setInt(7, uF.parseToInt(getTypeOfLeave()));
//					                pst.setDate(8, uF.getDateFormat(strDate, DATE_FORMAT));
					    			pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
					                		"and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
					                		"and leave_type_id=? and  _type='C') and _date<= ?");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
					                pst.setInt(4, uF.parseToInt(getEmpId()));
					                pst.setInt(5, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(6, uF.getDateFormat(strDate, DATE_FORMAT));
					                rs = pst.executeQuery();
					                while (rs.next()) {
					                    dblBalance += uF.parseToDouble(rs.getString("accrued"));
					                }
					    			rs.close();
					    			pst.close();
					    			
					    			double dblPaidBalance = 0;
					    			pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					//              System.out.println("pst====>"+pst);
					                rs = pst.executeQuery();
					                String balanceNextDate=null;
					                boolean flagNextDate = false;
					                while (rs.next()) {
					                	balanceNextDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
					                	flagNextDate = true;
					                }
					    			rs.close();
					    			pst.close();
			    			
					    			if(flagNextDate){
					    				pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null " +
					    						"or is_modify=false) and emp_id=? and leave_type_id=? and _date >=? and _date<?");
						                pst.setInt(1, uF.parseToInt(getEmpId()));
						                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
						                pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
						                pst.setDate(4, uF.getDateFormat(balanceNextDate, DATE_FORMAT));
					//		                System.out.println("pst====>"+pst);
						                rs = pst.executeQuery();
						                while (rs.next()) {
						                	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
						                }
						    			rs.close();
						    			pst.close();
					    			} else {
						    			pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null or is_modify=false)" +
						    					" and emp_id=? and leave_type_id=? and _date >= ?");
						                pst.setInt(1, uF.parseToInt(getEmpId()));
						                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
						                pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
				//		                System.out.println("pst====>"+pst);
						                rs = pst.executeQuery();
						                while (rs.next()) {
						                	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
						                }
						    			rs.close();
						    			pst.close();
					    			}
					                
					                if(dblBalance > 0 && dblBalance >= dblPaidBalance){
					                	dblBalance = dblBalance - dblPaidBalance; 
					                }
									
					                double dblBalance1 = dblBalance;
					                double dblCount = 0;
					                boolean isPaid1 = false;
					                
									if (getIsHalfDay()) {
										dblCount += 0.5;
									} else {
										if ((dblBalance - dblCount) == 0.5) {
											dblCount += 0.5;
										} else {
											dblCount++;
										}
									}
	
									if (dblBalance >= dblCount && isPaid) {
										isPaid1 = true;
										if (getIsHalfDay()) {
											dblBalance1 -= 0.5;
											dblLeaveDed = 0.5;
										} else {
											if (dblBalance1 >= 1) {
												dblBalance1 -= 1;
												dblLeaveDed = 1;
											} else if (dblBalance1 >= 0.5) {
												dblBalance1 -= 0.5;
												dblLeaveDed = 0.5;
											} else {
												dblLeaveDed = 0;
											}
										}
									} else {
										/*isPaid1 = false;
										if (getIsHalfDay()) {
											dblLeaveDed = 0.5;
										} else {
											dblLeaveDed = 1;
										}*/
										
										if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_COLOR_FOR_NIGATIVE_LEAVE_BALANCE))){
											isPaid1 = false;
											if (getIsHalfDay()) {
												dblLeaveDed = -0.5;
											} else {
												dblLeaveDed = -1;
											}
										} else{
											isPaid1 = false;
											if (getIsHalfDay()) {
												dblLeaveDed = 0.5;
											} else {
												dblLeaveDed = 1;
											}
										}
									}
									/*if (isConstantBalance) {
										isPaid1 = true;
									}*/
									leave += dblLeaveDed;
									if (isConstantBalance) {
										isPaid1 = true;
									}
	//								else if (isPaid1 && !isConstantBalance && dblMontlyLimit>0 && leave>dblMontlyLimit) {
	//									isPaid1 = false;
	//								}
									pst = con.prepareStatement("update  leave_application_register set leave_type_id=?, leave_id=?, leave_no=?, is_paid=?, "
											+ "balance=?, _type=?,prefix_suffix=?,prefix_suffix_type=?,is_modify=? where _date=? and emp_id=?");
						
									pst.setInt(1, uF.parseToInt(getTypeOfLeave()));
									pst.setInt(2, uF.parseToInt(getLeaveId()));
									pst.setDouble(3, dblLeaveDed);
									pst.setBoolean(4, isPaid1);
									pst.setDouble(5, dblBalance1);
									pst.setBoolean(6, true);
									pst.setString(7, "P");
									pst.setString(8, prefix_type);
									pst.setBoolean(9, false);
									pst.setDate(10, dtCurrent1);
									pst.setInt(11, uF.parseToInt(getEmpId()));
									int update=pst.executeUpdate();
									pst.close();
											
									if(update==0){
										pst = con.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, is_paid, "
														+ "balance, _type,prefix_suffix,prefix_suffix_type,is_modify) values (?,?,?,?,?,?,?,?,?,?,?)");
										pst.setDate(1, dtCurrent1);
										pst.setInt(2, uF.parseToInt(getEmpId()));
										pst.setInt(3, uF.parseToInt(getTypeOfLeave()));
										pst.setInt(4, uF.parseToInt(getLeaveId()));
										pst.setDouble(5, dblLeaveDed);
										pst.setBoolean(6, isPaid1);
										pst.setDouble(7, dblBalance1);
										pst.setBoolean(8, true);
										pst.setString(9, "P");
										pst.setString(10, prefix_type);
										pst.setBoolean(11, false);
										pst.execute();
										pst.close();
									}
								} else {
									break;
								}
							}
	
							// ===========================Suffix===========================
	
							Calendar cal5 = (Calendar) cal2.clone();
							while (true) {
								cal5.add(Calendar.DATE, 1);
								String strDate = cal5.get(Calendar.DATE) + "/" + (cal5.get(Calendar.MONTH) + 1) + "/" + cal5.get(Calendar.YEAR);
								Date dtCurrent1 = uF.getDateFormat(cal5.get(Calendar.DATE) + "/" + (cal5.get(Calendar.MONTH) + 1) + "/" + cal5.get(Calendar.YEAR),
										DATE_FORMAT);
								String day = uF.getDateFormat(strDate, DATE_FORMAT, "EEEE");
	
								boolean flag = false;
	
								boolean flag1 = false;
	
								String prefix_type = "";
	
								pst = con.prepareStatement("select atten_id from attendance_details where to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and emp_id=?");
								pst.setDate(1, dtCurrent1);
								pst.setInt(2, uF.parseToInt(getEmpId()));
								rs = pst.executeQuery();
								while (rs.next()) {
									flag1 = true;
								}
								rs.close();
								pst.close();
	
								if (!flag1) {
	
									boolean flag2 = false;
									pst = con.prepareStatement("select leave_register_id from leave_application_register where _date=?  and emp_id=?");
									pst.setDate(1, dtCurrent1);
									pst.setInt(2, uF.parseToInt(getEmpId()));
									rs = pst.executeQuery();
									while (rs.next()) {
										flag2 = true;
									}
									rs.close();
									pst.close();
									
									if (!flag2) {
//										pst = con
//												.prepareStatement("select holiday_id from holidays where _date=? and wlocation_id=(select wlocation_id from employee_official_details where emp_id=?) and (is_optional_holiday is null or is_optional_holiday=false)");
//										pst.setDate(1, dtCurrent1);
//										pst.setInt(2, uF.parseToInt(getEmpId()));
//										rs = pst.executeQuery();
//										while (rs.next()) {
//											flag = true;
//											prefix_type = "H";
//										}
//										rs.close();
//										pst.close();
										
										boolean isEmpRosterWeekOff = CF.checkEmpRosterWeeklyOff(con, CF, uF, getEmpId(), uF.getDateFormat(""+dtCurrent1, DBDATE, DATE_FORMAT), strLevelId, ""+wlocation, ""+org_id);
										if(isEmpRosterWeekOff){
											dblLeaveDed = 1;
											prefix_type = "WO";
											flag = true;
										} else {
											boolean isHoliday = CF.checkHoliday(con, uF, uF.getDateFormat(""+dtCurrent1, DBDATE, DATE_FORMAT), ""+wlocation, ""+org_id); 
											if(isHoliday){
												dblLeaveDed = 1;
												flag = true;
												prefix_type = "H";
											}
										}
									}
								}
	
//								if (!flag) {
//									if (weeklyoff1 != null && weeklyoff1.equalsIgnoreCase(day)) {
//										if (weeklyoff1type != null && weeklyoff1type.equalsIgnoreCase("HD")) {
//											dblLeaveDed = 0.5;
//										} else {
//											dblLeaveDed = 1;
//										}
//										prefix_type = "WO";
//										flag = true;
//									}
//									if (!flag && weeklyoff2 != null && weeklyoff2.equalsIgnoreCase(day)) {
//										if (weeklyoff2type != null && weeklyoff2type.equalsIgnoreCase("HD")) {
//											dblLeaveDed = 0.5;
//										} else {
//											dblLeaveDed = 1;
//										}
//										prefix_type = "WO";
//										flag = true;
//									}
//									if (!flag && weeklyoff3 != null && weeklyoff3.equalsIgnoreCase(day)) {
//										int checkWeek = getMonthCont(new UtilityFunctions(), strDate);
//										if (adDay.contains("" + checkWeek)) {
//											if (weeklyoff3type != null && weeklyoff3type.equalsIgnoreCase("HD")) {
//												dblLeaveDed = .5;
//											} else {
//												dblLeaveDed = 1;
//											}
//											prefix_type = "WO";
//											flag = true;
//										}
//									}
//								}
	
								if (flag) {
									
//									pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
//						                		"and _date = (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
//						                		"and leave_type_id=? and  _type='C') and  _type='C' and _date<= ?");
									pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
					                		"and register_id in (select max(register_id) from leave_register1 where _date<= ? and emp_id = ? " +
					                		"and leave_type_id=? and  _type='C') and  _type='C' and _date<= ?");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
					                pst.setInt(4, uF.parseToInt(getEmpId()));
					                pst.setInt(5, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(6, uF.getDateFormat(strDate, DATE_FORMAT));
					                rs = pst.executeQuery();
					                double dblBalance = 0;
					                String balanceDate=null;
					                while (rs.next()) {
					                    dblBalance = uF.parseToDouble(rs.getString("balance"));
					                    balanceDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
					                }
					    			rs.close();
					    			pst.close();
									
//									pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
//					                		"and register_id >(select max(register_id) as register_id from leave_register1 where _type='C' and emp_id =? " +
//					                		"and leave_type_id=?) and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
//					                		"and leave_type_id=? and  _type='C') and _date<= ?");
//					                pst.setInt(1, uF.parseToInt(getEmpId()));
//					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
//					                pst.setInt(3, uF.parseToInt(getEmpId()));
//					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
//					                pst.setDate(5, uF.getDateFormat(strDate, DATE_FORMAT));
//					                pst.setInt(6, uF.parseToInt(getEmpId()));
//					                pst.setInt(7, uF.parseToInt(getTypeOfLeave()));
//					                pst.setDate(8, uF.getDateFormat(strDate, DATE_FORMAT));
					    			pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
					                		"and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
					                		"and leave_type_id=? and  _type='C') and _date<= ?");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
					                pst.setInt(4, uF.parseToInt(getEmpId()));
					                pst.setInt(5, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(6, uF.getDateFormat(strDate, DATE_FORMAT));
					                rs = pst.executeQuery();
					                while (rs.next()) {
					                    dblBalance += uF.parseToDouble(rs.getString("accrued"));
					                }
					    			rs.close();
					    			pst.close();
					                
					    			double dblPaidBalance = 0;
					    			/*pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                */
					                pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
//					              System.out.println("pst====>"+pst);
					                rs = pst.executeQuery();
					                String balanceNextDate=null;
					                boolean flagNextDate = false;
					                while (rs.next()) {
					                	balanceNextDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
					                	flagNextDate = true;
					                }
					    			rs.close();
					    			pst.close();
//					    			System.out.println("balanceNextDate ===>> " + balanceNextDate);
					    			
					    			if(flagNextDate){
					    				pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null " +
					    						"or is_modify=false) and emp_id=? and leave_type_id=? and _date >=? and _date<?");
						                pst.setInt(1, uF.parseToInt(getEmpId()));
						                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
						                pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
						                pst.setDate(4, uF.getDateFormat(balanceNextDate, DATE_FORMAT));
					//		                System.out.println("pst====>"+pst);
						                rs = pst.executeQuery();
						                while (rs.next()) {
						                	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
						                }
						    			rs.close();
						    			pst.close();
					    			} else {
						    			pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null or is_modify=false)" +
						    					" and emp_id=? and leave_type_id=? and _date >= ?");
						                pst.setInt(1, uF.parseToInt(getEmpId()));
						                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
						                pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
				//		                System.out.println("pst====>"+pst);
						                rs = pst.executeQuery();
						                while (rs.next()) {
						                	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
						                }
						    			rs.close();
						    			pst.close();
					    			}
					                
//					    			System.out.println("dblPaidBalance ===>> " + dblPaidBalance);
					                if(dblBalance > 0 && dblBalance >= dblPaidBalance){
					                	dblBalance = dblBalance - dblPaidBalance; 
					                }
									
					                double dblBalance1 = dblBalance;
					                
					                double dblCount = 0;
					                boolean isPaid1 = false;
									
									if (getIsHalfDay()) {
										dblCount += 0.5;
									} else {
	
										if ((dblBalance - dblCount) == 0.5) {
											dblCount += 0.5;
										} else {
											dblCount++;
										}
	
									}
	
									if (dblBalance >= dblCount && isPaid) {
										isPaid1 = true;
	
										if (getIsHalfDay()) {
											dblBalance1 -= 0.5;
											dblLeaveDed = 0.5;
										} else {
	
											if (dblBalance1 >= 1) {
												dblBalance1 -= 1;
												dblLeaveDed = 1;
											} else if (dblBalance1 >= 0.5) {
												dblBalance1 -= 0.5;
												dblLeaveDed = 0.5;
											} else {
												dblLeaveDed = 0;
											}
										}
									} else {
										/*isPaid1 = false;
										if (getIsHalfDay()) {
											dblLeaveDed = 0.5;
										} else {
											dblLeaveDed = 1;
										}*/
										
										if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_COLOR_FOR_NIGATIVE_LEAVE_BALANCE))){
											isPaid1 = false;
											if (getIsHalfDay()) {
												dblLeaveDed = -0.5;
											} else {
												dblLeaveDed = -1;
											}
										} else{
											isPaid1 = false;
											if (getIsHalfDay()) {
												dblLeaveDed = 0.5;
											} else {
												dblLeaveDed = 1;
											}
										}
	
									}
	
									/*if (isConstantBalance) {
										isPaid1 = true;
									}*/
									leave += dblLeaveDed;
									if (isConstantBalance) {
										isPaid1 = true;
									}
	//								else if (isPaid1 && !isConstantBalance && dblMontlyLimit>0 && leave>dblMontlyLimit) {
	//									isPaid1 = false;
	//								}
									pst = con.prepareStatement("update  leave_application_register set leave_type_id=?, leave_id=?, leave_no=?, is_paid=?, "
											+ "balance=?, _type=?,prefix_suffix=?,prefix_suffix_type=?,is_modify=? where _date=? and emp_id=?");
						
									pst.setInt(1, uF.parseToInt(getTypeOfLeave()));
									pst.setInt(2, uF.parseToInt(getLeaveId()));
									pst.setDouble(3, dblLeaveDed);
									pst.setBoolean(4, isPaid1);
									pst.setDouble(5, dblBalance1);
									pst.setBoolean(6, true);
									pst.setString(7, "S");
									pst.setString(8, prefix_type);
									pst.setBoolean(9, false);
									pst.setDate(10,dtCurrent1);
									pst.setInt(11, uF.parseToInt(getEmpId()));
									int update=pst.executeUpdate();
									pst.close();
											
									if(update==0){
										pst = con
												.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, is_paid, "
														+ "balance, _type,prefix_suffix,prefix_suffix_type,is_modify) values (?,?,?,?,?,?,?,?,?,?,?)");
										pst.setDate(1, dtCurrent1);
										pst.setInt(2, uF.parseToInt(getEmpId()));
										pst.setInt(3, uF.parseToInt(getTypeOfLeave()));
										pst.setInt(4, uF.parseToInt(getLeaveId()));
										pst.setDouble(5, dblLeaveDed);
										pst.setBoolean(6, isPaid1);
										pst.setDouble(7, dblBalance1);
										pst.setBoolean(8, true);
										pst.setString(9, "S");
										pst.setString(10, prefix_type);
										pst.setBoolean(11, false);
										pst.execute();
										pst.close();
									}
	//								leave += dblLeaveDed;
								} else {
									break;
								}
	
							}
	
						}
					}
	                
	              
//					System.out.println("MLA/4748--getIsHalfDay() ===>> " + getIsHalfDay());
					if (!getIsHalfDay()) {
						  //=========================Prefix=====================
						Calendar cal1 = (Calendar) cal.clone();
						while (true) {
							cal1.add(Calendar.DATE, -1);
							String strDate = cal1.get(Calendar.DATE) + "/" + (cal1.get(Calendar.MONTH) + 1) + "/" + cal1.get(Calendar.YEAR);
							Date dtCurrent1 = uF.getDateFormat(cal1.get(Calendar.DATE) + "/" + (cal1.get(Calendar.MONTH) + 1) + "/" + cal1.get(Calendar.YEAR),
									DATE_FORMAT);
							String day = uF.getDateFormat(strDate, DATE_FORMAT, "EEEE");
//							System.out.println("prefix ===>> " + prefix);
							if (prefix.contains("-1") || prefix.contains("-2")) {
								boolean flag = false;
	
								boolean flag1 = false;
	
								String prefix_type = "";
	
								pst = con.prepareStatement("select atten_id from attendance_details where to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and emp_id=?");
								pst.setDate(1, dtCurrent1);
								pst.setInt(2, uF.parseToInt(getEmpId()));
								rs = pst.executeQuery();
								while (rs.next()) {
									flag1 = true;
								}
								rs.close();
								pst.close();
								
								if (!flag1) {
									boolean flag2 = false;
									pst = con.prepareStatement("select leave_register_id from leave_application_register where _date=?  and emp_id=?");
									pst.setDate(1, dtCurrent1);
									pst.setInt(2, uF.parseToInt(getEmpId()));
									rs = pst.executeQuery();
									while (rs.next()) {
										flag2 = true;
									}
									rs.close();
									pst.close();
	
									if (!flag2) {
										if (prefix.contains("-2")) {
//											pst = con
//													.prepareStatement("select holiday_id from holidays where _date=? and wlocation_id=(select wlocation_id from employee_official_details where emp_id=?) and (is_optional_holiday is null or is_optional_holiday=false)");
//											pst.setDate(1, dtCurrent1);
//											pst.setInt(2, uF.parseToInt(getEmpId()));
//											rs = pst.executeQuery();
//											while (rs.next()) {
//												flag = true;
//												prefix_type = "H";
//											}
//											rs.close();
//											pst.close();
											
											boolean isHoliday = CF.checkHoliday(con, uF, uF.getDateFormat(""+dtCurrent1, DBDATE, DATE_FORMAT), ""+wlocation, ""+org_id); 
											if(isHoliday){
												dblLeaveDed = 1;
												flag = true;
												prefix_type = "H";
											}											
										}
										if (!flag) {
											if (prefix.contains("-1")) {
//												if (weeklyoff1 != null && weeklyoff1.equalsIgnoreCase(day)) {
//													if (weeklyoff1type != null && weeklyoff1type.equalsIgnoreCase("HD")) {
//														dblLeaveDed = 0.5;
//													} else {
//														dblLeaveDed = 1;
//													}
//													prefix_type = "WO";
//													flag = true;
//												}
//												if (!flag && weeklyoff2 != null && weeklyoff2.equalsIgnoreCase(day)) {
//													if (weeklyoff2type != null && weeklyoff2type.equalsIgnoreCase("HD")) {
//														dblLeaveDed = 0.5;
//													} else {
//														dblLeaveDed = 1;
//													}
//													prefix_type = "WO";
//													flag = true;
//												}
//												if (!flag && weeklyoff3 != null && weeklyoff3.equalsIgnoreCase(day)) {
//													int checkWeek = getMonthCont(uF, strDate);
//													if (adDay.contains("" + checkWeek)) {
//														if (weeklyoff3type != null && weeklyoff3type.equalsIgnoreCase("HD")) {
//															dblLeaveDed = 0.5;
//														} else {
//															dblLeaveDed = 1;
//														}
//														prefix_type = "WO";
//														flag = true;
//													}
//												}
												boolean isEmpRosterWeekOff = CF.checkEmpRosterWeeklyOff(con, CF, uF, getEmpId(), uF.getDateFormat(""+dtCurrent1, DBDATE, DATE_FORMAT), strLevelId, ""+wlocation, ""+org_id);
												if(isEmpRosterWeekOff){
													dblLeaveDed = 1;
													prefix_type = "WO";
													flag = true;
												}
											}
										}
									}
								}
								if (flag) {
									
//									pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
//						                		"and _date = (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
//						                		"and leave_type_id=? and  _type='C') and  _type='C' and _date<= ?");
									pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
					                		"and register_id in (select max(register_id) from leave_register1 where _date<= ? and emp_id = ? " +
					                		"and leave_type_id=? and  _type='C') and  _type='C' and _date<= ?");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
					                pst.setInt(4, uF.parseToInt(getEmpId()));
					                pst.setInt(5, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(6, uF.getDateFormat(strDate, DATE_FORMAT));
					                rs = pst.executeQuery();
					                double dblBalance = 0;
					                String balanceDate=null;
					                while (rs.next()) {
					                    dblBalance = uF.parseToDouble(rs.getString("balance"));
					                    balanceDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
					                }
					    			rs.close();
					    			pst.close();
									
//									pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
//					                		"and register_id >(select max(register_id) as register_id from leave_register1 where _type='C' and emp_id =? " +
//					                		"and leave_type_id=?) and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
//					                		"and leave_type_id=? and  _type='C') and _date<= ?");
//					                pst.setInt(1, uF.parseToInt(getEmpId()));
//					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
//					                pst.setInt(3, uF.parseToInt(getEmpId()));
//					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
//					                pst.setDate(5, uF.getDateFormat(strDate, DATE_FORMAT));
//					                pst.setInt(6, uF.parseToInt(getEmpId()));
//					                pst.setInt(7, uF.parseToInt(getTypeOfLeave()));
//					                pst.setDate(8, uF.getDateFormat(strDate, DATE_FORMAT));
					    			pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
					                		"and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
					                		"and leave_type_id=? and  _type='C') and _date<= ?");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
					                pst.setInt(4, uF.parseToInt(getEmpId()));
					                pst.setInt(5, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(6, uF.getDateFormat(strDate, DATE_FORMAT));
					                rs = pst.executeQuery();
					                while (rs.next()) {
					                    dblBalance += uF.parseToDouble(rs.getString("accrued"));
					                }
					    			rs.close();
					    			pst.close();
					                
					    			double dblPaidBalance = 0;
					    			pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
//					              System.out.println("pst====>"+pst);
					                rs = pst.executeQuery();
					                String balanceNextDate=null;
					                boolean flagNextDate = false;
					                while (rs.next()) {
					                	balanceNextDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
					                	flagNextDate = true;
					                }
					    			rs.close();
					    			pst.close();
			    			
					    			if(flagNextDate){
					    				pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null " +
					    						"or is_modify=false) and emp_id=? and leave_type_id=? and _date >=? and _date<?");
						                pst.setInt(1, uF.parseToInt(getEmpId()));
						                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
						                pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
						                pst.setDate(4, uF.getDateFormat(balanceNextDate, DATE_FORMAT));
					//		                System.out.println("pst====>"+pst);
						                rs = pst.executeQuery();
						                while (rs.next()) {
						                	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
						                }
						    			rs.close();
						    			pst.close();
					    			} else {
						    			pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null or is_modify=false)" +
						    					" and emp_id=? and leave_type_id=? and _date >= ?");
						                pst.setInt(1, uF.parseToInt(getEmpId()));
						                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
						                pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
				//		                System.out.println("pst====>"+pst);
						                rs = pst.executeQuery();
						                while (rs.next()) {
						                	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
						                }
						    			rs.close();
						    			pst.close();
					    			}
					                
					                if(dblBalance > 0 && dblBalance >= dblPaidBalance){
					                	dblBalance = dblBalance - dblPaidBalance; 
					                }
									
					                double dblBalance1 = dblBalance;
					                double dblCount = 0;
					                boolean isPaid1 = false;
									
									if (getIsHalfDay()) {
										dblCount += 0.5;
									} else {
										if ((dblBalance - dblCount) == 0.5) {
											dblCount += 0.5;
										} else {
											dblCount++;
										}
									}
	
									if (dblBalance >= dblCount && isPaid) {
										isPaid1 = true;
										if (getIsHalfDay()) {
											dblBalance1 -= 0.5;
											dblLeaveDed = 0.5;
										} else {
											if (dblBalance1 >= 1) {
												dblBalance1 -= 1;
												dblLeaveDed = 1;
											} else if (dblBalance1 >= 0.5) {
												dblBalance1 -= 0.5;
												dblLeaveDed = 0.5;
											} else {
												dblLeaveDed = 0;
											}
										}
									} else {
										/*isPaid1 = false;
										if (getIsHalfDay()) {
											dblLeaveDed = 0.5;
										} else {
											dblLeaveDed = 1;
										}*/
										
										if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_COLOR_FOR_NIGATIVE_LEAVE_BALANCE))){
											isPaid1 = false;
											if (getIsHalfDay()) {
												dblLeaveDed = -0.5;
											} else {
												dblLeaveDed = -1;
											}
										} else{
											isPaid1 = false;
											if (getIsHalfDay()) {
												dblLeaveDed = 0.5;
											} else {
												dblLeaveDed = 1;
											}
										}
									}
	
									/*if (isConstantBalance) {
										isPaid1 = true;
									}*/
									leave += dblLeaveDed;
									if (isConstantBalance) {
										isPaid1 = true;
									}
	//								else if (isPaid1 && !isConstantBalance && dblMontlyLimit>0 && leave>dblMontlyLimit) {
	//									isPaid1 = false;
	//								}
									
									pst = con.prepareStatement("update  leave_application_register set leave_type_id=?, leave_id=?, leave_no=?, is_paid=?, "
											+ "balance=?, _type=?,prefix_suffix=?,prefix_suffix_type=?,is_modify=? where _date=? and emp_id=?");
									pst.setInt(1, uF.parseToInt(getTypeOfLeave()));
									pst.setInt(2, uF.parseToInt(getLeaveId()));
									pst.setDouble(3, dblLeaveDed);
									pst.setBoolean(4, isPaid1);
									pst.setDouble(5, dblBalance1);
									pst.setBoolean(6, true);
									pst.setString(7, "P");
									pst.setString(8, prefix_type);
									pst.setBoolean(9, false);
									pst.setDate(10, dtCurrent1);
									pst.setInt(11, uF.parseToInt(getEmpId()));
									int update=pst.executeUpdate();
//									System.out.println("MLA/4973---prefix update ===>> " + update + " -- pst ===>> " + pst);
									pst.close();
											
									if(update==0) {
										pst = con.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, is_paid, "
														+ "balance, _type,prefix_suffix,prefix_suffix_type,is_modify) values (?,?,?,?,?,?,?,?,?,?,?)");
										pst.setDate(1, dtCurrent1);
										pst.setInt(2, uF.parseToInt(getEmpId()));
										pst.setInt(3, uF.parseToInt(getTypeOfLeave()));
										pst.setInt(4, uF.parseToInt(getLeaveId()));
										pst.setDouble(5, dblLeaveDed);
										pst.setBoolean(6, isPaid1);
										pst.setDouble(7, dblBalance1);
										pst.setBoolean(8, true);
										pst.setString(9, "P");
										pst.setString(10, prefix_type);
										pst.setBoolean(11, false);
										pst.executeUpdate();
//										System.out.println("prefix insert -- pst ===>> " + pst);
										pst.close();
			//								leave += dblLeaveDed;
									}
								} else {
									break;
								}
							} else {
								break;
							}
						}
						
//						System.out.println("Suffix 1");
						//===========================Suffix===========================
		                
		                Calendar cal3 = (Calendar) cal2.clone();
						while (true) {
							cal3.add(Calendar.DATE, 1);
							String strDate = cal3.get(Calendar.DATE) + "/" + (cal3.get(Calendar.MONTH) + 1) + "/" + cal3.get(Calendar.YEAR);
							Date dtCurrent1 = uF.getDateFormat(cal3.get(Calendar.DATE) + "/" + (cal3.get(Calendar.MONTH) + 1) + "/" + cal3.get(Calendar.YEAR),DATE_FORMAT);
							String day = uF.getDateFormat(strDate, DATE_FORMAT, "EEEE");
//							System.out.println("suffix ===>> " + suffix);
							if (suffix.contains("-1") || suffix.contains("-2")) {
								boolean flag = false;
	
								boolean flag1 = false;
	
								String prefix_type = "";
	
								pst = con.prepareStatement("select atten_id from attendance_details where to_date(in_out_timestamp::text,'yyyy-MM-dd')=? and emp_id=?");
								pst.setDate(1, dtCurrent1);
								pst.setInt(2, uF.parseToInt(getEmpId()));
								rs = pst.executeQuery();
								while (rs.next()) {
									flag1 = true;
								}
								rs.close();
								pst.close();
	
								if (!flag1) {
	
									boolean flag2 = false;
									pst = con.prepareStatement("select leave_register_id from leave_application_register where _date=?  and emp_id=?");
									pst.setDate(1, dtCurrent1);
									pst.setInt(2, uF.parseToInt(getEmpId()));
									rs = pst.executeQuery();
									while (rs.next()) {
										flag2 = true;
									}
									rs.close();
									pst.close();
									
									if (!flag2) {
										if (suffix.contains("-2")) {
//											pst = con.prepareStatement("select holiday_id from holidays where _date=? and wlocation_id=(select wlocation_id from employee_official_details where emp_id=?) and (is_optional_holiday is null or is_optional_holiday=false)");
//											pst.setDate(1, dtCurrent1);
//											pst.setInt(2, uF.parseToInt(getEmpId()));
//											rs = pst.executeQuery();
//											while (rs.next()) {
//												flag = true;
//												prefix_type = "H";
//											}
//											rs.close();
//											pst.close();
											
											boolean isHoliday = CF.checkHoliday(con, uF, uF.getDateFormat(""+dtCurrent1, DBDATE, DATE_FORMAT), ""+wlocation, ""+org_id); 
											if(isHoliday){
												dblLeaveDed = 1;
												flag = true;
												prefix_type = "H";
											}											
										}
									}
								}
	
								if (!flag) {
									if (suffix.contains("-1")) {
//										if (weeklyoff1 != null && weeklyoff1.equalsIgnoreCase(day)) {
//											if (weeklyoff1type != null && weeklyoff1type.equalsIgnoreCase("HD")) {
//												dblLeaveDed = 0.5;
//											} else {
//												dblLeaveDed = 1;
//											}
//											prefix_type = "WO";
//											flag = true;
//										}
//										if (!flag && weeklyoff2 != null && weeklyoff2.equalsIgnoreCase(day)) {
//											if (weeklyoff2type != null && weeklyoff2type.equalsIgnoreCase("HD")) {
//												dblLeaveDed = 0.5;
//											} else {
//												dblLeaveDed = 1;
//											}
//											prefix_type = "WO";
//											flag = true;
//										}
//										if (!flag && weeklyoff3 != null && weeklyoff3.equalsIgnoreCase(day)) {
//											int checkWeek = getMonthCont(new UtilityFunctions(), strDate);
//											if (adDay.contains("" + checkWeek)) {
//												if (weeklyoff3type != null && weeklyoff3type.equalsIgnoreCase("HD")) {
//													dblLeaveDed = .5;
//												} else {
//													dblLeaveDed = 1;
//												}
//												prefix_type = "WO";
//												flag = true;
//											}
//										}
										boolean isEmpRosterWeekOff = CF.checkEmpRosterWeeklyOff(con, CF, uF, getEmpId(), uF.getDateFormat(""+dtCurrent1, DBDATE, DATE_FORMAT), strLevelId, ""+wlocation, ""+org_id);
										if(isEmpRosterWeekOff){
											dblLeaveDed = 1;
											prefix_type = "WO";
											flag = true;
										}
									}
								}
	
								if (flag) {
									
//									pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
//						                		"and _date = (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
//						                		"and leave_type_id=? and  _type='C') and  _type='C' and _date<= ?");
									pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
					                		"and register_id in (select max(register_id) from leave_register1 where _date<= ? and emp_id = ? " +
					                		"and leave_type_id=? and  _type='C') and  _type='C' and _date<= ?");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
					                pst.setInt(4, uF.parseToInt(getEmpId()));
					                pst.setInt(5, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(6, uF.getDateFormat(strDate, DATE_FORMAT));
					                rs = pst.executeQuery();
					                double dblBalance = 0;
					                String balanceDate=null;
					                while (rs.next()) {
					                    dblBalance = uF.parseToDouble(rs.getString("balance"));
					                    balanceDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
					                }
					    			rs.close();
					    			pst.close();
									
//									pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
//					                		"and register_id >(select max(register_id) as register_id from leave_register1 where _type='C' and emp_id =? " +
//					                		"and leave_type_id=?) and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
//					                		"and leave_type_id=? and  _type='C') and _date<= ?");
//					                pst.setInt(1, uF.parseToInt(getEmpId()));
//					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
//					                pst.setInt(3, uF.parseToInt(getEmpId()));
//					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
//					                pst.setDate(5, uF.getDateFormat(strDate, DATE_FORMAT));
//					                pst.setInt(6, uF.parseToInt(getEmpId()));
//					                pst.setInt(7, uF.parseToInt(getTypeOfLeave()));
//					                pst.setDate(8, uF.getDateFormat(strDate, DATE_FORMAT));
					    			pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
					                		"and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
					                		"and leave_type_id=? and  _type='C') and _date<= ?");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
					                pst.setInt(4, uF.parseToInt(getEmpId()));
					                pst.setInt(5, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(6, uF.getDateFormat(strDate, DATE_FORMAT));
					                rs = pst.executeQuery();
					                while (rs.next()) {
					                    dblBalance += uF.parseToDouble(rs.getString("accrued"));
					                }
					    			rs.close();
					    			pst.close();
					                
					    			double dblPaidBalance = 0;
					    			pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
					    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
					    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
					    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
					                pst.setInt(1, uF.parseToInt(getEmpId()));
					                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(3, uF.parseToInt(getEmpId()));
					                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					                pst.setInt(5, uF.parseToInt(getEmpId()));
					                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
					                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setInt(8, uF.parseToInt(getEmpId()));
					                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
					                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
					                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
//					              System.out.println("pst====>"+pst);
					                rs = pst.executeQuery();
					                String balanceNextDate=null;
					                boolean flagNextDate = false;
					                while (rs.next()) {
					                	balanceNextDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
					                	flagNextDate = true;
					                }
					    			rs.close();
					    			pst.close();
			    			
					    			if(flagNextDate){
					    				pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null " +
					    						"or is_modify=false) and emp_id=? and leave_type_id=? and _date >=? and _date<?");
						                pst.setInt(1, uF.parseToInt(getEmpId()));
						                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
						                pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
						                pst.setDate(4, uF.getDateFormat(balanceNextDate, DATE_FORMAT));
					//		                System.out.println("pst====>"+pst);
						                rs = pst.executeQuery();
						                while (rs.next()) {
						                	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
						                }
						    			rs.close();
						    			pst.close();
					    			} else {
						    			pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null or is_modify=false)" +
						    					" and emp_id=? and leave_type_id=? and _date >= ?");
						                pst.setInt(1, uF.parseToInt(getEmpId()));
						                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
						                pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
//						                System.out.println("pst====>"+pst);
						                rs = pst.executeQuery();
						                while (rs.next()) {
						                	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
						                }
						    			rs.close();
						    			pst.close();
					    			}
					                
					                if(dblBalance > 0 && dblBalance >= dblPaidBalance){
					                	dblBalance = dblBalance - dblPaidBalance; 
					                }
									
					                double dblBalance1 = dblBalance;
					                double dblCount = 0;
					                boolean isPaid1 = false;
									
									if (getIsHalfDay()) {
										dblCount += 0.5;
									} else {
	
										if ((dblBalance - dblCount) == 0.5) {
											dblCount += 0.5;
										} else {
											dblCount++;
										}
	
									}
	
									if (dblBalance >= dblCount && isPaid) {
										isPaid1 = true;
	
										if (getIsHalfDay()) {
											dblBalance1 -= 0.5;
											dblLeaveDed = 0.5;
										} else {
	
											if (dblBalance1 >= 1) {
												dblBalance1 -= 1;
												dblLeaveDed = 1;
											} else if (dblBalance1 >= 0.5) {
												dblBalance1 -= 0.5;
												dblLeaveDed = 0.5;
											} else {
												dblLeaveDed = 0;
											}
										}
									} else {
										/*isPaid1 = false;
										if (getIsHalfDay()) {
											dblLeaveDed = 0.5;
										} else {
											dblLeaveDed = 1;
										}*/
										
										if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_COLOR_FOR_NIGATIVE_LEAVE_BALANCE))){
											isPaid1 = false;
											if (getIsHalfDay()) {
												dblLeaveDed = -0.5;
											} else {
												dblLeaveDed = -1;
											}
										} else{
											isPaid1 = false;
											if (getIsHalfDay()) {
												dblLeaveDed = 0.5;
											} else {
												dblLeaveDed = 1;
											}
										}
	
									}
	
									/*if (isConstantBalance) {
										isPaid1 = true;
									}*/
									leave += dblLeaveDed;
									if (isConstantBalance) {
										isPaid1 = true;
									}
	//								else if (isPaid1 && !isConstantBalance && dblMontlyLimit>0 && leave>dblMontlyLimit) {
	//									isPaid1 = false;
	//								}
	
									pst = con.prepareStatement("update  leave_application_register set leave_type_id=?, leave_id=?, leave_no=?, is_paid=?, "
											+ "balance=?, _type=?,prefix_suffix=?,prefix_suffix_type=?,is_modify=? where _date=? and emp_id=?");
						
									pst.setInt(1, uF.parseToInt(getTypeOfLeave()));
									pst.setInt(2, uF.parseToInt(getLeaveId()));
									pst.setDouble(3, dblLeaveDed);
									pst.setBoolean(4, isPaid1);
									pst.setDouble(5, dblBalance1);
									pst.setBoolean(6, true);
									pst.setString(7, "S");
									pst.setString(8, prefix_type);
									pst.setBoolean(9, false);
									pst.setDate(10, dtCurrent1);
									pst.setInt(11, uF.parseToInt(getEmpId()));
									int update=pst.executeUpdate();
//									System.out.println("MLA/5289---Suffix update ===>> " + update + " -- pst ===>> " + pst);
									pst.close();
											
									if(update==0){
										pst = con.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, is_paid, "
												+ "balance, _type,prefix_suffix,prefix_suffix_type,is_modify) values (?,?,?,?,?,?,?,?,?,?,?)");
										pst.setDate(1, dtCurrent1);
										pst.setInt(2, uF.parseToInt(getEmpId()));
										pst.setInt(3, uF.parseToInt(getTypeOfLeave()));
										pst.setInt(4, uF.parseToInt(getLeaveId()));
										pst.setDouble(5, dblLeaveDed);
										pst.setBoolean(6, isPaid1);
										pst.setDouble(7, dblBalance1);
										pst.setBoolean(8, true);
										pst.setString(9, "S");
										pst.setString(10, prefix_type);
										pst.setBoolean(11, false);
										pst.executeUpdate();
//										System.out.println("Suffix insert -- pst ===>> " + pst);
										pst.close();
									}
	//								leave += dblLeaveDed;
								} else {
									break;
								}
							} else {
								break;
							}
						}
						
						
					}
	                
//					System.out.println("MLA/5408---dblLeavesApproved ===>> " + dblLeavesApproved);
					for (int i = 0; i < dblLeavesApproved; i++) {
						Date dtCurrent = uF.getDateFormat(cal.get(Calendar.DATE) + "/" + (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR), DATE_FORMAT);
						String strDate = uF.zero(cal.get(Calendar.DATE)) + "/" + uF.zero((cal.get(Calendar.MONTH) + 1)) + "/" + cal.get(Calendar.YEAR);
						cal.add(Calendar.DATE, 1);
						
						String day = uF.getDateFormat(strDate, DATE_FORMAT, "EEEE");
//						System.out.println(strDate+" -- dblLeavesApproved ====> " + dblLeavesApproved);
						boolean isSandwichDate = false;
						if ((isSandwichLeave == 1 || isSandwichLeave == 2) && !getIsHalfDay() && dblLeavesApproved > 2.0d) {
							if(sandwichleavetype != null && sandwichleavetype.contains("-2")) {
//								pst = con.prepareStatement("select holiday_id from holidays where _date=? and wlocation_id=(select wlocation_id " +
//										"from employee_official_details where emp_id=?) and (is_optional_holiday is null or is_optional_holiday=false)");
//								pst.setDate(1, uF.getDateFormat(strDate, DATE_FORMAT));
//								pst.setInt(2, uF.parseToInt(getEmpId()));
//								rs = pst.executeQuery();
//								while (rs.next()) {
//									isSandwichDate = true;
//								}
//								rs.close();
//								pst.close();
								
								boolean isHoliday = CF.checkHoliday(con, uF, strDate, ""+wlocation, ""+org_id); 
								if(isHoliday){
									isSandwichDate = true;
								}
							}
							
							if(!isSandwichDate && sandwichleavetype != null && sandwichleavetype.contains("-1") ){
//								if (weeklyoff1 != null && weeklyoff1.equalsIgnoreCase(day)) {
//									isSandwichDate = true;
//								}
//								if (!isSandwichDate && weeklyoff2 != null && weeklyoff2.equalsIgnoreCase(day)) {
//									isSandwichDate = true;
//								}
//								if (!isSandwichDate && weeklyoff3 != null && weeklyoff3.equalsIgnoreCase(day)) {
//									int checkWeek = getMonthCont(new UtilityFunctions(), strDate);
//									if (adDay.contains("" + checkWeek)) {
//										isSandwichDate = true;
//									}
//								}
								boolean isEmpRosterWeekOff = CF.checkEmpRosterWeeklyOff(con, CF, uF, getEmpId(), strDate, strLevelId, ""+wlocation, ""+org_id);
								if(isEmpRosterWeekOff){
									isSandwichDate = true;
								}
							}
						}
						
						boolean isHolidayWeeklyOff = false;
//						pst = con.prepareStatement("select holiday_id from holidays where _date=? and wlocation_id=(select wlocation_id " +
//								"from employee_official_details where emp_id=?) and (is_optional_holiday is null or is_optional_holiday=false)");
//						pst.setDate(1, uF.getDateFormat(strDate, DATE_FORMAT));
//						pst.setInt(2, uF.parseToInt(getEmpId()));
//						rs = pst.executeQuery();
//						while (rs.next()) {
//							isHolidayWeeklyOff = true;
//						}
//						rs.close();
//						pst.close();
						boolean isHoliday = CF.checkHoliday(con, uF, strDate, ""+wlocation, ""+org_id); 
						if(isHoliday){
							isHolidayWeeklyOff = true;
						}
//						System.out.println("1 isSandwichDate====>"+isSandwichDate+"--isHolidayWeeklyOff====>"+isHolidayWeeklyOff);
						if(!isHolidayWeeklyOff){
//							if (weeklyoff1 != null && weeklyoff1.equalsIgnoreCase(day)) {
//								isHolidayWeeklyOff = true;
//							}
//							if (!isHolidayWeeklyOff && weeklyoff2 != null && weeklyoff2.equalsIgnoreCase(day)) {
//								isHolidayWeeklyOff = true;
//							}
//							if (!isHolidayWeeklyOff && weeklyoff3 != null && weeklyoff3.equalsIgnoreCase(day)) {
//								int checkWeek = getMonthCont(new UtilityFunctions(), strDate);
//								if (adDay.contains("" + checkWeek)) {
//									isHolidayWeeklyOff = true;
//								}
//							}
							boolean isEmpRosterWeekOff = CF.checkEmpRosterWeeklyOff(con, CF, uF, getEmpId(), strDate, strLevelId, ""+wlocation, ""+org_id);
							if(isEmpRosterWeekOff){
								isHolidayWeeklyOff = true;
							}
						}
						
//						System.out.println("2 isSandwichDate====>"+isSandwichDate+"--isHolidayWeeklyOff====>"+isHolidayWeeklyOff);
						if((isSandwichLeave == 0 || isSandwichLeave == 1 || isSandwichLeave == 2) && !isSandwichDate && isHolidayWeeklyOff){
							continue;
						}
						
//						pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
//			                		"and _date = (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
//			                		"and leave_type_id=? and  _type='C') and  _type='C' and _date<= ?");
						pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
		                		"and register_id in (select max(register_id) from leave_register1 where _date<= ? and emp_id = ? " +
		                		"and leave_type_id=? and _type='C') and _type='C' and _date<= ?");
		                pst.setInt(1, uF.parseToInt(getEmpId()));
		                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
		                pst.setDate(3, uF.getDateFormat(strDate, DATE_FORMAT));
		                pst.setInt(4, uF.parseToInt(getEmpId()));
		                pst.setInt(5, uF.parseToInt(getTypeOfLeave()));
		                pst.setDate(6, uF.getDateFormat(strDate, DATE_FORMAT));
//		                System.out.println("pst====>"+pst);
		                rs = pst.executeQuery();
		                double dblBalance = 0;
		                String balanceDate=null;
		                while (rs.next()) {
		                    dblBalance = uF.parseToDouble(rs.getString("balance"));
		                    balanceDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
		                }
		    			rs.close();
		    			pst.close();
//		    			System.out.println("dblBalance====>"+dblBalance+"----balanceDate====>"+balanceDate);
							
//						pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
//		                		"and register_id >(select max(register_id) as register_id from leave_register1 where _type='C' and emp_id =? " +
//		                		"and leave_type_id=?) and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
//		                		"and leave_type_id=? and  _type='C') and _date<= ?");
//		                pst.setInt(1, uF.parseToInt(getEmpId()));
//		                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
//		                pst.setInt(3, uF.parseToInt(getEmpId()));
//		                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
//		                pst.setDate(5, uF.getDateFormat(strDate, DATE_FORMAT));
//		                pst.setInt(6, uF.parseToInt(getEmpId()));
//		                pst.setInt(7, uF.parseToInt(getTypeOfLeave()));
//		                pst.setDate(8, uF.getDateFormat(strDate, DATE_FORMAT));
		    			pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
		                		"and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
		                		"and leave_type_id=? and  _type='C') and _date<= ?");
		                pst.setInt(1, uF.parseToInt(getEmpId()));
		                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
		                pst.setDate(3, dtCurrent);
		                pst.setInt(4, uF.parseToInt(getEmpId()));
		                pst.setInt(5, uF.parseToInt(getTypeOfLeave()));
		                pst.setDate(6, dtCurrent);
//		                System.out.println("MLA/5455---pst====>"+pst);
		                rs = pst.executeQuery();
		                while (rs.next()) {
		                    dblBalance += uF.parseToDouble(rs.getString("accrued"));
		                }
		    			rs.close();
		    			pst.close();
			              
		    			double dblPaidBalance = 0;
		    			pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
		    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
		    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
		    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
		                pst.setInt(1, uF.parseToInt(getEmpId()));
		                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
		                pst.setInt(3, uF.parseToInt(getEmpId()));
		                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
		                pst.setInt(5, uF.parseToInt(getEmpId()));
		                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
		                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
		                pst.setInt(8, uF.parseToInt(getEmpId()));
		                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
		                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
		                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
//		              System.out.println("MLA/5479---pst====>"+pst);
		                rs = pst.executeQuery();
		                String balanceNextDate=null;
		                boolean flagNextDate = false;
		                while (rs.next()) {
		                	balanceNextDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
		                	flagNextDate = true;
		                }
		    			rs.close();
		    			pst.close();
    			
		    			if(flagNextDate){
		    				pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null " +
		    						"or is_modify=false) and emp_id=? and leave_type_id=? and _date >=? and _date<?");
			                pst.setInt(1, uF.parseToInt(getEmpId()));
			                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
			                pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
			                pst.setDate(4, uF.getDateFormat(balanceNextDate, DATE_FORMAT));
//				            System.out.println("MLA/5497---pst====>"+pst);
			                rs = pst.executeQuery();
			                while (rs.next()) {
			                	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
			                }
			    			rs.close();
			    			pst.close();
		    			} else {
			    			pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null or is_modify=false)" +
			    					" and emp_id=? and leave_type_id=? and _date >= ?");
			                pst.setInt(1, uF.parseToInt(getEmpId()));
			                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
			                pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
//			                System.out.println("MLA/5510---pst====>"+pst);
			                rs = pst.executeQuery();
			                while (rs.next()) {
			                	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
			                }
			    			rs.close();
			    			pst.close();
		    			}
		    			
//		    			System.out.println("dblPaidBalance====>"+dblPaidBalance);
			                
		                if(dblBalance > 0 && dblBalance >= dblPaidBalance){
		                	dblBalance = dblBalance - dblPaidBalance; 
		                }
//		                System.out.println("dblBalance====>"+dblBalance);
						
		                double dblBalance1 = dblBalance;
		                double dblCount = 0;
		                boolean isPaid1 = false;
		                
						if (getIsHalfDay()) {
							dblCount += 0.5;
						} else {

							if ((dblBalance - dblCount) == 0.5) {
								dblCount += 0.5;
							} else {
								dblCount++;
							}

						}
//						System.out.println("dblCount====>"+dblCount+"----isPaid====>"+isPaid);
						if (dblBalance >= dblCount && isPaid) {
							isPaid1 = true;

							if (getIsHalfDay()) {
								dblBalance1 -= 0.5;
								dblLeaveDed = 0.5;
							} else {

								if (dblBalance1 >= 1) {
									dblBalance1 -= 1;
									dblLeaveDed = 1;
								} else if (dblBalance1 >= 0.5) {
									dblBalance1 -= 0.5;
									dblLeaveDed = 0.5;
								} else {
									dblLeaveDed = 0;
								}
							}
						} else {
						//===start parvez date: 31-10-2022===	
							/*isPaid1 = false;
							if (getIsHalfDay()) {
								dblLeaveDed = 0.5;
							} else {
								dblLeaveDed = 1;
							}*/
							
							if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_COLOR_FOR_NIGATIVE_LEAVE_BALANCE))){
								isPaid1 = false;
								if (getIsHalfDay()) {
									dblLeaveDed = -0.5;
								} else {
									dblLeaveDed = -1;
								}
							} else {
								isPaid1 = false;
								if (getIsHalfDay()) {
									dblLeaveDed = 0.5;
								} else {
									dblLeaveDed = 1;
								}
							}
						//===end parvez date: 31-10-2022===	

						}
						/*if (isConstantBalance) {
							isPaid1 = true;
						}*/
						if (isConstantBalance) {
							isPaid1 = true;
						}
//						else if (isPaid1 && !isConstantBalance && dblMontlyLimit>0 && (leave+(i+1))>dblMontlyLimit) {
//							isPaid1 = false;
//						}
						
				//===start parvez date: 22-09-2022====		
						
						/*pst = con.prepareStatement("update leave_application_register set leave_type_id=?, leave_id=?, leave_no=?, is_paid=?, "
								+ "balance=?, _type=?,is_modify=? where _date=? and emp_id=?");
						pst.setInt(1, uF.parseToInt(getTypeOfLeave()));
						pst.setInt(2, uF.parseToInt(getLeaveId()));
						pst.setDouble(3, dblLeaveDed);
						pst.setBoolean(4, isPaid1);
						pst.setDouble(5, dblBalance1);
						pst.setBoolean(6, true);
						pst.setBoolean(7, false);
						pst.setDate(8, dtCurrent);
						pst.setInt(9, uF.parseToInt(getEmpId()));
						int update = pst.executeUpdate();
						System.out.println("MLA/5588--pst ===>> " + pst);
						pst.close();
								
						if(update==0) {
							pst = con.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, "
									+ "is_paid, balance, _type,is_modify) values (?,?,?,?,?,?,?,?,?)");
							pst.setDate(1, dtCurrent);
							pst.setInt(2, uF.parseToInt(getEmpId()));
							pst.setInt(3, uF.parseToInt(getTypeOfLeave()));
							pst.setInt(4, uF.parseToInt(getLeaveId()));
							pst.setDouble(5, dblLeaveDed);
							pst.setBoolean(6, isPaid1);
							pst.setDouble(7, dblBalance1);
							pst.setBoolean(8, true);
							pst.setBoolean(9, false);
							pst.execute();
							System.out.println("MLA/5604--pst ===>> " + pst);
							pst.close();
						}*/
						
							
//						Map<String,String> hmFeatureStatus = CF.getFeatureStatusMap(con);
						String extraWorkingDate= null;
						
						if(hmFeatureStatus!=null && uF.parseToBoolean(hmFeatureStatus.get(F_EXTRA_WORKING_LAPS_DAYS_LIMIT_FOR_COMPOFF_LEAVE))){
							pst = con.prepareStatement("select * from emp_leave_type where compensate_with=? and level_id=? and org_id=? and wlocation_id=?");
				            pst.setInt(1, uF.parseToInt(getTypeOfLeave()));
				            pst.setInt(2, levelId);
				            pst.setInt(3, org_id);
				            pst.setInt(4, wlocation);
//				            System.out.println("MAL/545--pst====>"+pst);
				            int strCompensateWithId = 0;
				            rs = pst.executeQuery();
				            while(rs.next()){
				            	strCompensateWithId = uF.parseToInt(rs.getString("leave_type_id"));
				            }
				            rs.close();
				            pst.close();
				            
				            pst = con.prepareStatement("select extra_working_date from leave_application_register where leave_type_id=? and emp_id=? and (is_modify is null or is_modify=false)");
				            pst.setInt(1, uF.parseToInt(getTypeOfLeave()));
				            pst.setInt(2, uF.parseToInt(getEmpId()));
				            rs = pst.executeQuery();
				            String strCheckEWDate = null;
				            while(rs.next()){
				            	strCheckEWDate = uF.getDateFormat(rs.getString("extra_working_date"), DBDATE, DATE_FORMAT);
				            }
				            rs.close();
				            pst.close();
				            
				            if(strCheckEWDate == null){
				            	pst = con.prepareStatement("select * from leave_application_register where  emp_id=? and leave_type_id=? and (is_modify is null or is_modify=false) " +
					            		" and _date >= ? order by _date limit 1");
				            	pst.setInt(1, uF.parseToInt(getEmpId()));
					            pst.setInt(2, strCompensateWithId);
					            pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
					            rs = pst.executeQuery();
					            while(rs.next()){
					            	extraWorkingDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
					            }
					            rs.close();
					            pst.close();
				            } else{
					            pst = con.prepareStatement("select * from leave_application_register where  emp_id=? and leave_type_id=? and (is_modify is null or is_modify=false) " +
					            		" and _date >= ? and (_date not in (select extra_working_date from leave_application_register " +
					            		" where leave_type_id=? and emp_id=? and (is_modify is null or is_modify=false))) order by _date limit 1");
					            
					            pst.setInt(1, uF.parseToInt(getEmpId()));
					            pst.setInt(2, strCompensateWithId);
					            pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
					            pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
					            pst.setInt(5, uF.parseToInt(getEmpId()));
					            rs = pst.executeQuery();
					            while(rs.next()){
					            	extraWorkingDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
					            }
					            rs.close();
					            pst.close();
				            }
				            
				            
						}
						
						if(extraWorkingDate!=null){
							pst = con.prepareStatement("update leave_application_register set leave_type_id=?, leave_id=?, leave_no=?, is_paid=?, "
									+ "balance=?, _type=?,is_modify=?, extra_working_date=? where _date=? and emp_id=? and leave_id=?");
							pst.setInt(1, uF.parseToInt(getTypeOfLeave()));
							pst.setInt(2, uF.parseToInt(getLeaveId()));
							pst.setDouble(3, dblLeaveDed);
							pst.setBoolean(4, isPaid1);
							pst.setDouble(5, dblBalance1);
							pst.setBoolean(6, true);
							pst.setBoolean(7, false);
							pst.setDate(8, uF.getDateFormat(extraWorkingDate, DATE_FORMAT));
							pst.setDate(9, dtCurrent);
							pst.setInt(10, uF.parseToInt(getEmpId()));
							pst.setInt(11, uF.parseToInt(getLeaveId()));
							int update = pst.executeUpdate();
//							System.out.println("MLA/5662--pst ===>> " + pst);
							pst.close();
									
							if(update==0) {
								pst = con.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, "
										+ "is_paid, balance, _type,is_modify,extra_working_date) values (?,?,?,?, ?,?,?,?, ?,?)");
								pst.setDate(1, dtCurrent);
								pst.setInt(2, uF.parseToInt(getEmpId()));
								pst.setInt(3, uF.parseToInt(getTypeOfLeave()));
								pst.setInt(4, uF.parseToInt(getLeaveId()));
								pst.setDouble(5, dblLeaveDed);
								pst.setBoolean(6, isPaid1);
								pst.setDouble(7, dblBalance1);
								pst.setBoolean(8, true);
								pst.setBoolean(9, false);
								pst.setDate(10, uF.getDateFormat(extraWorkingDate, DATE_FORMAT));
								pst.execute();
//								System.out.println("MLA/5679--pst ===>> " + pst);
								pst.close();
							}
						} else{
							pst = con.prepareStatement("update leave_application_register set leave_type_id=?, leave_id=?, leave_no=?, is_paid=?, "
									+ "balance=?, _type=?,is_modify=? where _date=? and emp_id=? and leave_id=?");
							pst.setInt(1, uF.parseToInt(getTypeOfLeave()));
							pst.setInt(2, uF.parseToInt(getLeaveId()));
							pst.setDouble(3, dblLeaveDed);
							pst.setBoolean(4, isPaid1);
							pst.setDouble(5, dblBalance1);
							pst.setBoolean(6, true);
							pst.setBoolean(7, false);
							pst.setDate(8, dtCurrent);
							pst.setInt(9, uF.parseToInt(getEmpId()));
							pst.setInt(10, uF.parseToInt(getLeaveId()));
							int update = pst.executeUpdate();
//							System.out.println("MLA/5827--update--pst ===>> " + pst);
							pst.close();
									
							if(update==0) {
								pst = con.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, "
										+ "is_paid, balance, _type,is_modify) values (?,?,?,?,?,?,?,?,?)");
								pst.setDate(1, dtCurrent);
								pst.setInt(2, uF.parseToInt(getEmpId()));
								pst.setInt(3, uF.parseToInt(getTypeOfLeave()));
								pst.setInt(4, uF.parseToInt(getLeaveId()));
								pst.setDouble(5, dblLeaveDed);
								pst.setBoolean(6, isPaid1);
								pst.setDouble(7, dblBalance1);
								pst.setBoolean(8, true);
								pst.setBoolean(9, false);
								pst.execute();
//								System.out.println("MLA/5843--insert--pst ===>> " + pst);
								pst.close();
							}
						}
				//===end parvez date: 22-09-2022===	
						
					}
	            }
//                    CF.updateLeaveRegister1(con, CF, uF, (dblLeavesApproved+leave), 0,uF.parseToInt(getTypeOfLeave())+"", uF.parseToInt(getEmpId())+"");
            } else if(getIsapproved()==1 && isWorkFromHome) {
                // Needs to add a condition if manager approves leaves other the dates for which employee has applied.
                if (isPaid) {
                    pst = con.prepareStatement("UPDATE emp_leave_entry SET ispaid=true WHERE leave_id=?");
                    pst.setInt(1, uF.parseToInt(getLeaveId())); 
                    pst.execute();
        			pst.close();
                }
                double dblLeavesApproved = 0;                
                if (getIsHalfDay()) {
                    dblLeavesApproved = 0.5;
                } else {
                	String strDateDiff = uF.dateDifference(getApprovalFromTo(), DATE_FORMAT, getApprovalToDate(), DATE_FORMAT,CF.getStrTimeZone());
                 	strDateDiff = strDateDiff !=null && !strDateDiff.trim().equals("") && !strDateDiff.trim().equalsIgnoreCase("NULL") ? strDateDiff.trim() : "0"; 
                    dblLeavesApproved = uF.parseToDouble(strDateDiff);
                }
                
                Calendar cal = GregorianCalendar.getInstance();
                cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, "dd")));
                cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, "MM")) - 1);
                cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, "yyyy")));
               
                double dblLeaveDed = 0;
                if (getIsHalfDay()) {
                    dblLeaveDed = 0.5;
                } else {
                    dblLeaveDed = 1;
                }
                for(int i=0; i<dblLeavesApproved; i++) {
                    Date dtCurrent = uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT);
                    pst = con.prepareStatement("update leave_application_register set leave_type_id=?, leave_id=?, leave_no=?, is_paid=?, "
						+ "balance=?, _type=?,is_modify=? where _date=? and emp_id=?");
					pst.setInt(1, uF.parseToInt(getTypeOfLeave()));
					pst.setInt(2, uF.parseToInt(getLeaveId()));
					pst.setDouble(3, dblLeaveDed);
					pst.setBoolean(4, isPaid);
					pst.setDouble(5, 0);
					pst.setBoolean(6, true);
					pst.setBoolean(7, false);
					pst.setDate(8, dtCurrent);
					pst.setInt(9, uF.parseToInt(getEmpId()));
					int update=pst.executeUpdate();
					pst.close();
							
					if(update==0) {
	                    pst = con.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, " +
	                    	"is_paid, balance, _type,is_modify) values (?,?,?,?,?,?,?,?,?)");
	                    pst.setDate(1, dtCurrent);
	                    pst.setInt(2, uF.parseToInt(getEmpId()));
	                    pst.setInt(3, uF.parseToInt(getTypeOfLeave()));
	                    pst.setInt(4, uF.parseToInt(getLeaveId()));
	                    pst.setDouble(5, dblLeaveDed);
	                    pst.setBoolean(6, isPaid);
	                    pst.setDouble(7, 0);
	                    pst.setBoolean(8, true);
	                    pst.setBoolean(9, false);
	                    pst.execute();
	        			pst.close();
					}
                    cal.add(Calendar.DATE, 1);
                }

            } else if (getIsapproved()==1 && iscompensate) {
            
                // Needs to add a condition if manager approves leaves other the dates for which employee has applied.
                if (isPaid) {
                    pst = con.prepareStatement("UPDATE emp_leave_entry SET ispaid=true WHERE leave_id=?");
                    pst.setInt(1, uF.parseToInt(getLeaveId())); 
                    pst.execute();
        			pst.close();
                }
                
                double dblLeavesApproved = 0;                
                if (getIsHalfDay()) {
                    dblLeavesApproved = 0.5;
                } else {
//                    dblLeavesApproved = uF.parseToInt(uF.dateDifference(getApprovalFromTo(), DATE_FORMAT, getApprovalToDate(), DATE_FORMAT));
                	String strDateDiff = uF.dateDifference(getApprovalFromTo(), DATE_FORMAT, getApprovalToDate(), DATE_FORMAT,CF.getStrTimeZone());
                 	strDateDiff = strDateDiff !=null && !strDateDiff.trim().equals("") && !strDateDiff.trim().equalsIgnoreCase("NULL") ? strDateDiff.trim() : "0"; 
                    dblLeavesApproved = uF.parseToDouble(strDateDiff);
                }
                
//                System.out.println("else if leaveId====>"+getLeaveId()+"---getApprovalFromTo====>"+getApprovalFromTo()
//                		+"---getApprovalToDate====>"+getApprovalToDate()+"---dblLeavesApproved====>"+dblLeavesApproved
//                		+"---dblLeavesApproved====>"+dblLeavesApproved+"---diff====>"+uF.dateDifference(getApprovalFromTo(), DATE_FORMAT, getApprovalToDate(), DATE_FORMAT,CF.getStrTimeZone())
//                		+"---convert====>"+uF.parseToInt(uF.dateDifference(getApprovalFromTo(), DATE_FORMAT, getApprovalToDate(), DATE_FORMAT,CF.getStrTimeZone())));
                
                Calendar cal = GregorianCalendar.getInstance();
                cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, "dd")));
                cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, "MM")) - 1);
                cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(getApprovalFromTo(), DATE_FORMAT, "yyyy")));
               
        //===start parvez date: 20-04-2022===
                String orgId=CF.getEmpOrgId(con, uF, empId);
                String[] strPayCycle = CF.getPayCycleFromDate(con, getApprovalFromTo(), CF.getStrTimeZone(), CF, orgId);
                String monthStartDate = strPayCycle[0];
                String monthEndDate = strPayCycle[1];
                
//                System.out.println("StartPaycycle="+strPayCycle[0]+"EndPayCycle="+strPayCycle[1]);
        //===end parvez date: 20-04-2022===        
                
                double dblLeaveDed = 0;
//               System.out.println("MLA/5704--date:"+cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                for(int i=0; i<dblLeavesApproved; i++) {
                    Date dtCurrent = uF.getDateFormat(cal.get(Calendar.DATE)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT);
//                    pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
//	                		"and _date = (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
//	                		"and leave_type_id=? and  _type='C') and  _type='C' and _date<= ?");
                    pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id =? and leave_type_id=? " +
	                		"and register_id in (select max(register_id) from leave_register1 where _date<= ? and emp_id = ? " +
	                		"and leave_type_id=? and  _type='C') and  _type='C' and _date<= ?");
	                pst.setInt(1, uF.parseToInt(getEmpId()));
	                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
	                pst.setDate(3, dtCurrent);
	                pst.setInt(4, uF.parseToInt(getEmpId()));
	                pst.setInt(5, uF.parseToInt(getTypeOfLeave()));
	                pst.setDate(6, dtCurrent);
	                rs = pst.executeQuery();
	                double dblBalance = 0;
	                String balanceDate=null;
	                while (rs.next()) {
	                    dblBalance = uF.parseToDouble(rs.getString("balance"));
	                    balanceDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
	                }
	    			rs.close();
	    			pst.close();
					
//					pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
//	                		"and register_id >(select max(register_id) as register_id from leave_register1 where _type='C' and emp_id =? " +
//	                		"and leave_type_id=?) and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
//	                		"and leave_type_id=? and  _type='C') and _date<= ?");
//	                pst.setInt(1, uF.parseToInt(getEmpId()));
//	                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
//	                pst.setInt(3, uF.parseToInt(getEmpId()));
//	                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
//	                pst.setDate(5, dtCurrent);
//	                pst.setInt(6, uF.parseToInt(getEmpId()));
//	                pst.setInt(7, uF.parseToInt(getTypeOfLeave()));
//	                pst.setDate(8, dtCurrent);
	    			pst = con.prepareStatement("select sum(accrued) as accrued from leave_register1 where emp_id =? and leave_type_id=? " +
	                		"and _date >= (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
	                		"and leave_type_id=? and  _type='C') and _date<= ?");
	                pst.setInt(1, uF.parseToInt(getEmpId()));
	                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
	                pst.setDate(3, dtCurrent);
	                pst.setInt(4, uF.parseToInt(getEmpId()));
	                pst.setInt(5, uF.parseToInt(getTypeOfLeave()));
	                pst.setDate(6, dtCurrent);
	                rs = pst.executeQuery();
	                while (rs.next()) {
	                    dblBalance += uF.parseToDouble(rs.getString("accrued"));
	                }
	    			rs.close();
	    			pst.close();
	                
	    			double dblPaidBalance = 0;
	    			pst = con.prepareStatement("select _date from leave_register1 where emp_id=? and leave_type_id=? and _type='C' and _date in (select min(_date) " +
	    					"from leave_register1 where emp_id=? and leave_type_id=? and register_id > (select register_id from leave_register1 where emp_id=? " +
	    					"and leave_type_id=? and register_id in (select max(register_id) from leave_register1 where _date<=? and emp_id=? and leave_type_id=?" +
	    					" and _type='C') and _type='C' and _date<=?) and _type='C' and _date>?)");
	                pst.setInt(1, uF.parseToInt(getEmpId()));
	                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
	                pst.setInt(3, uF.parseToInt(getEmpId()));
	                pst.setInt(4, uF.parseToInt(getTypeOfLeave()));
	                pst.setInt(5, uF.parseToInt(getEmpId()));
	                pst.setInt(6, uF.parseToInt(getTypeOfLeave()));		                
	                pst.setDate(7, uF.getDateFormat(balanceDate, DATE_FORMAT));
	                pst.setInt(8, uF.parseToInt(getEmpId()));
	                pst.setInt(9, uF.parseToInt(getTypeOfLeave()));
	                pst.setDate(10, uF.getDateFormat(balanceDate, DATE_FORMAT));
	                pst.setDate(11, uF.getDateFormat(balanceDate, DATE_FORMAT));
	//              System.out.println("pst====>"+pst);
	                rs = pst.executeQuery();
	                String balanceNextDate=null;
	                boolean flagNextDate = false;
	                while (rs.next()) {
	                	balanceNextDate = uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT);
	                	flagNextDate = true;
	                }
	    			rs.close();
	    			pst.close();
			
	    			if(flagNextDate){
	    				pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null " +
	    						"or is_modify=false) and emp_id=? and leave_type_id=? and _date >=? and _date<?");
		                pst.setInt(1, uF.parseToInt(getEmpId()));
		                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
		                pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
		                pst.setDate(4, uF.getDateFormat(balanceNextDate, DATE_FORMAT));
			            System.out.println("MLA/5829---pst====>"+pst);
		                rs = pst.executeQuery();
		                while (rs.next()) {
		                	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
		                }
		    			rs.close();
		    			pst.close();
	    			} else {
		    			pst = con.prepareStatement("select sum(leave_no) as count from leave_application_register where is_paid=true and (is_modify is null or is_modify=false)" +
		    					" and emp_id=? and leave_type_id=? and _date >= ?");
		                pst.setInt(1, uF.parseToInt(getEmpId()));
		                pst.setInt(2, uF.parseToInt(getTypeOfLeave()));
		                pst.setDate(3, uF.getDateFormat(balanceDate, DATE_FORMAT));
		                System.out.println("MLA/5842---pst====>"+pst);
		                rs = pst.executeQuery();
		                while (rs.next()) {
		                	dblPaidBalance = uF.parseToDouble(rs.getString("count"));
		                }
		    			rs.close();
		    			pst.close();
	    			}
	                
	                if(dblBalance > 0 && dblBalance >= dblPaidBalance){
	                	dblBalance = dblBalance - dblPaidBalance; 
	                }
					
	                double dblBalance1 = dblBalance;
	                double dblCount=0;
	                boolean isPaid1= false;
                    
                    if (getIsHalfDay()) {
                        dblCount+=0.5;
                    } else {
                        if ((dblBalance-dblCount)==0.5) {
                            dblCount+=0.5;
                        } else {
                            dblCount++;
                        }
                    }
                    
                    if (dblBalance>=dblCount && isPaid) {
                        isPaid1 = true;
                        if (getIsHalfDay()) {
                            dblBalance1 += 0.5;
                            dblLeaveDed = 0.5;
                        } else {
                            if (dblBalance1>=1) {
                                dblBalance1 += 1;
                                dblLeaveDed = 1;
                            } else if (dblBalance1>=0.5) {
                                dblBalance1 += 0.5;
                                dblLeaveDed = 0.5;
                            } else {
                                dblLeaveDed = 0;
                            }
                        }
                    } else {
                        isPaid1 = false;
                        if (getIsHalfDay()) {
                            dblLeaveDed = 0.5;
                        } else {
                            dblLeaveDed = 1;
                        }
                    }
                    
                    pst = con.prepareStatement("update  leave_application_register set leave_type_id=?, leave_id=?, leave_no=?, is_paid=?, "
						+ "balance=?, _type=?,is_modify=? where _date=? and emp_id=?");
					pst.setInt(1, uF.parseToInt(getTypeOfLeave()));
					pst.setInt(2, uF.parseToInt(getLeaveId()));
					pst.setDouble(3, dblLeaveDed);
					pst.setBoolean(4, isPaid1);
					pst.setDouble(5, dblBalance1);
					pst.setBoolean(6, true);
					pst.setBoolean(7, false);
					pst.setDate(8, dtCurrent);
					pst.setInt(9, uF.parseToInt(getEmpId()));
					int update=pst.executeUpdate();
					pst.close();
					
//					System.out.println("MLA/5882--update=="+update);
					if(update==0){
		                    
	                    pst = con.prepareStatement("insert into leave_application_register (_date, emp_id, leave_type_id, leave_id, leave_no, " +
	                        "is_paid, balance, _type,is_modify) values (?,?,?,?,?,?,?,?,?)");
	                    pst.setDate(1, dtCurrent);
	                    pst.setInt(2, uF.parseToInt(getEmpId()));
	                    pst.setInt(3, uF.parseToInt(getTypeOfLeave()));
	                    pst.setInt(4, uF.parseToInt(getLeaveId()));
	                    pst.setDouble(5, dblLeaveDed);
	                    pst.setBoolean(6, isPaid1);
	                    pst.setDouble(7, dblBalance1);
	                    pst.setBoolean(8, true);
	                    pst.setBoolean(9, false);
	                    pst.execute();
	        			pst.close();
					}
                    cal.add(Calendar.DATE, 1);
                    
             //===start parvez date: 07-12-2021===
//                    System.out.println("MLA/5887--date:"+cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                    boolean IsAutoCalculateCompOff = CF.getFeatureManagementStatus(request, uF, F_AUTO_CALCULATE_COMPOFF_FOR_EXTRA_WORKING);
                    if(IsAutoCalculateCompOff){
                    	
//                    	Date monthStartDate = uF.getDateFormat(cal.getActualMinimum(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT);
//                        Date monthEndDate = uF.getDateFormat(cal.getActualMaximum(Calendar.DAY_OF_MONTH)+"/"+(cal.get(Calendar.MONTH) + 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT);
//                        System.out.println("MLA/5895--startdate="+monthStartDate+"--endDate="+monthEndDate);
                        pst = con.prepareStatement("select min(leave_register_id) as leave_register_id from leave_application_register where emp_id=? and leave_type_id=? and is_paid=false " +
                        		"and _date between ? and ? and (is_modify is null or is_modify=false)");
                        pst.setInt(1, uF.parseToInt(getEmpId()));
                        pst.setInt(2, compensate_with);
                        pst.setDate(3,uF.getDateFormat(monthStartDate,DATE_FORMAT));
                        pst.setDate(4,uF.getDateFormat(monthEndDate,DATE_FORMAT));
//                        System.out.println("MLA/5900--pst="+pst);
                        rs = pst.executeQuery();
                        int leaveRigesterId = 0;
                        while(rs.next()){
                        	leaveRigesterId = rs.getInt("leave_register_id");
                        }
                        rs.close();
    	    			pst.close();
    	    			
                        pst = con.prepareStatement("update leave_application_register set is_paid=true where leave_register_id=? and emp_id=?");
                        pst.setInt(1, leaveRigesterId);
                        pst.setInt(2, uF.parseToInt(getEmpId()));
//                        System.out.println("MLA/5914---pst="+pst);
                        pst.executeUpdate();
    					pst.close();
                    }
                    
            //===end parvez date: 07-12-2021===
                    
                }

//                if (isCompensatary) {
//                    setTypeOfLeave(compensate_with+"");
//                }
//                CF.updateCompLeaveRegister1(con, CF, uF, dblLeavesApproved, 0, uF.parseToInt(getTypeOfLeave())+"", uF.parseToInt(getEmpId())+"");
                updateCompLeaveRegister1(con, CF, uF, dblLeavesApproved, 0, uF.parseToInt(getTypeOfLeave())+"", uF.parseToInt(getEmpId())+"",compensate_with,getApprovalToDate());
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
    
    
    
	public void updateCompLeaveRegister1(Connection con, CommonFunctions CF, UtilityFunctions uF, double dblLeavesApproved, double dblAccreuedLeave, String strTypeOfLeave, String strEmpId, int compensateId, String strTodate) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
	
			pst = con.prepareStatement("select balance,_date from leave_register1 where emp_id=? and leave_type_id=? " +
	        		"and register_id >=(select max(register_id) as register_id from leave_register1 where _type='C' and emp_id =? " +
	        		"and leave_type_id=?) and _date = (select max(_date) as _date from leave_register1 where _date<= ? and emp_id = ? " +
	        		"and leave_type_id=? and _type='C') ");
	        pst.setInt(1, uF.parseToInt(strEmpId));
	        pst.setInt(2, compensateId);
	        pst.setInt(3, uF.parseToInt(strEmpId));
	        pst.setInt(4, compensateId);
	//        pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
	        pst.setDate(5, uF.getDateFormat(strTodate, DATE_FORMAT));
	        pst.setInt(6, uF.parseToInt(strEmpId));
	        pst.setInt(7, compensateId);
	        rs = pst.executeQuery();
	//        System.out.println("MLA/5936--pst ===>> " + pst);
	        boolean flag=false;
	        if (rs.next()) {
	        	flag=true;
	        }
			rs.close();
			pst.close();
			flag=true;
			
			if (flag) {
				pst = con.prepareStatement("insert into leave_register1 (accrued, emp_id,leave_type_id, _date,_type,compensate_id) values (?,?,?,?,?,?)");
				pst.setDouble(1, dblLeavesApproved);
				pst.setInt(2, uF.parseToInt(strEmpId));
				pst.setInt(3, compensateId);
	//			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDate(4, uF.getDateFormat(strTodate, DATE_FORMAT));
				pst.setString(5, "A");
				pst.setInt(6, uF.parseToInt(getTypeOfLeave()));
				pst.execute();
				pst.close();
			} else {
				pst = con.prepareStatement("insert into leave_register1 (balance, emp_id,leave_type_id, _date,_type,compensate_id) values (?,?,?,?,?,?)");
				pst.setDouble(1, dblLeavesApproved);
				pst.setInt(2, uF.parseToInt(strEmpId));
				pst.setInt(3, compensateId);
	//			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDate(4, uF.getDateFormat(strTodate, DATE_FORMAT));
				pst.setString(5, "C");
				pst.setInt(6, uF.parseToInt(getTypeOfLeave()));
				pst.execute();
				pst.close();
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
	
    public int getMonthCont(UtilityFunctions uF,String strDate) {
        
        Calendar mycal = Calendar.getInstance();
        mycal.setTime(uF.getDateFormat(strDate, DATE_FORMAT));
        
        java.util.Date d1=uF.getDateFormatUtil("01"+"/"+(mycal.get(Calendar.MONTH) + 1)+"/"+mycal.get(Calendar.YEAR), DATE_FORMAT);
        java.util.Date d2=uF.getDateFormatUtil(strDate, DATE_FORMAT);

        int cnt =0;
        while(d1.compareTo(d2)<=0) {
            cnt++;
            mycal.add(Calendar.DATE,-7);
            d2=mycal.getTime();
            if (cnt==10) {
                break;
            }
        }
        
        return cnt;
        
        
    }
    
    public String viewLeaveEntry(String strEdit, String strLevelId, String strCompensatory) {

        Connection con = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        Database db = new Database();
        db.setRequest(request);
        UtilityFunctions uF = new UtilityFunctions();

        try {
            con = db.makeConnection(con);
            Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con, null, null);
            
//          pst = con.prepareStatement(selectManagerApproval);
//          pst.setInt(1, uF.parseToInt(strEdit));
//          pst.setInt(2, uF.parseToInt(strLevelId));
            
            pst = con.prepareStatement("select * from emp_leave_entry ee where leave_id=? order by ee.entrydate desc");
            pst.setInt(1, uF.parseToInt(strEdit));
            
            rs = pst.executeQuery();
            while (rs.next()) {
                setEmpName(hmEmpNameMap.get(rs.getString("emp_id")));
                setUserId(rs.getString("user_id"));
                setLeaveId(rs.getString("leave_id"));
                setEmpId(rs.getString("emp_id"));
//              setLeaveFromTo(uF.getDateFormat(rs.getString("leave_from"), DBDATE, DATE_FORMAT));
//              setLeaveToDate(uF.getDateFormat(rs.getString("leave_to"), DBDATE, DATE_FORMAT));
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
                
                request.setAttribute("RequiredDocument", request.getContextPath() +DOCUMENT_LOCATION+""+rs.getString("document_attached"));
                if (rs.getString("document_attached")!=null && rs.getString("document_attached").length()>0) {
                    request.setAttribute("RequiredDocumentName", ""+rs.getString("document_attached"));
                }
                setStrCompensatory(getStrCompensatory());
                                
            }
			rs.close();
			pst.close();
            
            
            
            /*boolean flag=true;
            int work_flow_id=0;
            
            pst = con.prepareStatement("select * from work_flow_details where effective_id=? and is_approved=-1  and effective_type='"+WORK_FLOW_LEAVE+"'");
            pst.setInt(1, uF.parseToInt(strEdit));

            rs = pst.executeQuery();
            while (rs.next()) {
                flag=false;
            }
            
            if (flag) {
                
                List<Integer> memebrList=new ArrayList<Integer>();
                pst = con.prepareStatement("select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_LEAVE+"' order by work_flow_id");
                pst.setInt(1, uF.parseToInt(strEdit));

                rs = pst.executeQuery();
                while (rs.next()) {
                    memebrList.add(rs.getInt("member_position"));
                }
                
                pst = con.prepareStatement("select min(work_flow_id)as work_flow_id  from work_flow_details where effective_id=? and is_approved=0 and effective_type='"+WORK_FLOW_LEAVE+"' and member_position not in(select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_LEAVE+"' )");
                pst.setInt(1, uF.parseToInt(strEdit));
                pst.setInt(2, uF.parseToInt(strEdit));

                rs = pst.executeQuery();
                while (rs.next()) { 
                    work_flow_id=rs.getInt("work_flow_id");
                }
                
                pst = con.prepareStatement("select * from work_flow_details where effective_id=? and effective_type='"+WORK_FLOW_LEAVE+"' and work_flow_id=(select min(work_flow_id) from work_flow_details where effective_id=? and emp_id=? and is_approved=0 and effective_type='"+WORK_FLOW_LEAVE+"' and member_position not in (select member_position from work_flow_details where effective_id=? and is_approved=1 and effective_type='"+WORK_FLOW_LEAVE+"' )) order by work_flow_id");
                pst.setInt(1, uF.parseToInt(strEdit));
                pst.setInt(2, uF.parseToInt(strEdit));
                pst.setInt(3, uF.parseToInt(strSessionEmpId));
                pst.setInt(4, uF.parseToInt(strEdit));
                rs = pst.executeQuery();
                if (rs.next()) {
                    if (work_flow_id==rs.getInt("work_flow_id") && !memebrList.contains(rs.getInt("member_position"))) {
                        flag=true;                      
                    } else {
                        flag=false;
                    }
                } else {
                    flag=false;
                }
                
            }
            
            
            
            request.setAttribute("flag",flag);
            request.setAttribute("work_flow_id",""+work_flow_id);*/
            

//          leaveTypeList = new FillLeaveType(request).fillLeave();
            Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con); 
            Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
            Map<String, String> hmEmpGenderMap =CF.getEmpGenderMap(con);
            String gender=hmEmpGenderMap.get(empId);
            String orgId=CF.getEmpOrgId(con, uF, empId);
            boolean flag=CF.getMaternityFrequency(con,request,uF.parseToInt(hmEmpLevelMap.get(empId)), uF.parseToInt(empId),uF.parseToInt(orgId), uF.parseToInt(hmEmpWlocationMap.get(empId)));
            leaveTypeList = new FillLeaveType(request).fillLeaveWithoutCompensetary(gender!=null && gender.equalsIgnoreCase("M")?true:false,flag,uF.parseToInt(hmEmpLevelMap.get(empId)), uF.parseToInt(empId),uF.parseToInt(orgId), uF.parseToInt(hmEmpWlocationMap.get(empId)),uF.getCurrentDate(CF.getStrTimeZone()));

            strWorkingSession = new FillHalfDaySession().fillHalfDaySession();
            approvalList = new FillApproval().fillApprovalDenied();

        } catch (Exception e) {
            e.printStackTrace();
            return ERROR;
        } finally {
            db.closeResultSet(rs);
            db.closeStatements(pst);            
            db.closeConnection(con);
        }
        return SUCCESS;

    }

    public void deleteLeaveEntry(String strDelete) {  

        Connection con = null;
        PreparedStatement pst = null;
        Database db = new Database();
        db.setRequest(request);
        UtilityFunctions uF = new UtilityFunctions();

        try {

            con = db.makeConnection(con);
            pst = con.prepareStatement(deleteManagerApproval);
            pst.setInt(1, uF.parseToInt(strDelete));
//            System.out.println("ManagerLeaveApproval pst==>"+pst);
            pst.execute();
			pst.close();

            request.setAttribute(MESSAGE, "Deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute(MESSAGE, "Error in deletion");
        } finally {
            db.closeStatements(pst);
            db.closeConnection(con);
        }

    }
    
    public void validate() {
        UtilityFunctions uF = new UtilityFunctions();

        strWorkingSession = new FillHalfDaySession().fillHalfDaySession();
        
        session = request.getSession();
        CF = (CommonFunctions)session.getAttribute(CommonFunctions);
        if (CF==null)return;
        
        Connection con = null;
        Database db = new Database();
        db.setRequest(request);
        
        try {
            
            con = db.makeConnection(con);

            Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con, null, null);
            
            setEmpName(hmEmpNameMap.get(getEmpId()));
                  
            if (getTypeOfLeave()!=null && uF.parseToInt(getTypeOfLeave())== 0) {
                addFieldError("TypeOfLeave", "Type of leave is required");    
            } 
            if (getReason()!=null && getReason().length() == 0) {
                addFieldError("Reason", "Employee reason is required");
            } 
            /*if (getManagerReason()!=null && getManagerReason().length() == 0) {
                addFieldError("ManagerReason", " Manager reason is required");
            } */
           if (getApprovalFromTo()!=null && getApprovalFromTo().length() == 0) {
                addFieldError("LeaveFromTo", " Leave start date is required");
            } 
            if (getApprovalToDate()!=null && getApprovalToDate().length() == 0) {
                addFieldError("LeaveToDate", " Leave end date is required");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            db.closeConnection(con);
        }
        
        
        loadVidateLeaveEntry();
    }
    
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
    /*public String getLeaveFromTo() {
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
    }*/
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
//  public List<FillUserType> getUserTypeList() {
//      return userTypeList;
//  }
//  public void setUserTypeList(List<FillUserType> userTypeList) {
//      this.userTypeList = userTypeList;
//  }
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
    public String getIsCompensate() {
		return isCompensate;
	}
	public void setIsCompensate(String isCompensate) {
		this.isCompensate = isCompensate;
	}
	public String getStrCompensatory() {
        return strCompensatory;
    }
    public void setStrCompensatory(String strCompensatory) {
        this.strCompensatory = strCompensatory;
    }
    public String getWork_flow_id() {
        return work_flow_id;
    }
    public void setWork_flow_id(String work_flow_id) {
        this.work_flow_id = work_flow_id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
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
	public String getApType() {
		return apType;
	}
	public void setApType(String apType) {
		this.apType = apType;
	}
	public String getApStatus() {
		return apStatus;
	}
	public void setApStatus(String apStatus) {
		this.apStatus = apStatus;
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