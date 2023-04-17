package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillHalfDaySession;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.select.FillUserType;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApplyOnDuty extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	String strUserType = null;
	String strSessionEmpId = null;

	private String policy_id;
	
	private String leaveId;
	
	private String userId;
	private String empId; 
	private String strEmpId;
	private String empName;
	private String reason;
	private String managerReason;
	private String empNoOfLeave;
	private String typeOfLeave;
	private String leaveFromTo;
	private String leaveToDate;
	private String approveLeaveFromTo;
	private String approveLeaveToDate;
	private String entrydate;
	private String destinations;
	private String planName;
	private String travelAdvance;
	
	private boolean isHalfDay;
	private String strSession;
	
	int isapproved=0;
	
	private List<FillHalfDaySession> strWorkingSession;

	
	private List<FillUserType> userTypeList;
	private List<FillLeaveType> leaveTypeList;
	private List<FillEmployee> empList;
	
	private boolean isConcierge;
	private String modeOfTravel;
	private boolean isBooking;
	private String bookingDetails;
	private boolean isAccommodation;
	private String accommodationDetails; 
	
	private String type;
	private String placeFrom;
	private String travelFromTime;
	private String travelToTime;
	
	public String execute() throws Exception {
		
//		System.out.println("in execute==========>");
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		 
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PApplyTravel);
		UtilityFunctions uF = new UtilityFunctions();
		
		String strIsHalfDayLeave = ""+CF.getIsHalfDayLeave();
		request.setAttribute("strIsHalfDayLeave", strIsHalfDayLeave);
		
		String strEdit = request.getParameter("E");
		String strDelete = request.getParameter("D");
		String addAnother = request.getParameter("addAnother");
		
//		System.out.println("==>type"+addAnother+" "+strEdit);
		if(uF.parseToInt(getType()) > 0) {
			session.setAttribute(MESSAGE, SUCCESSM+uF.getDigitPosition(uF.parseToInt(getType()))+" On Duty applied successfully."+END);
		}
		if (strEdit != null) {		
			viewLeaveEntry(strEdit);
			request.setAttribute(TITLE, TViewEmployeeLeaveEntry);
			return SUCCESS;  
		}
					
		if (getEmpId() != null && getEmpId().length() > 0 && getReason()!=null) {
			insertOnDutyEntry();
			request.setAttribute(TITLE, TAddEmployeeLeaveEntry);
			
			if(addAnother != null && addAnother.equalsIgnoreCase("Submit & Add Another")) {
				return UPDATE;
			} else {
				return "finish";
			}
		}
		return loadTravelEntry();
	}

	public String loadLaVidateLeaveEntry() {
		
		request.setAttribute(PAGE, PApplyOnDuty);
		request.setAttribute(TITLE, "Apply ON DUTY");
		session = request.getSession();

		userTypeList = new FillUserType(request).fillUserType();
		
		empList = new FillEmployee(request).fillEmployeeName(strUserType, strSessionEmpId, session);
		
		return LOAD;
	}

	public String loadTravelEntry() {
		request.setAttribute(PAGE, PApplyOnDuty);
		request.setAttribute(TITLE, "Apply ON DUTY");
		
		strWorkingSession = new FillHalfDaySession().fillHalfDaySession();
		
		UtilityFunctions uF = new UtilityFunctions();
		setLeaveId("");
		setEmpId(null);
		setEmpName((String)session.getAttribute("EMPNAME"));
		setEmpNoOfLeave("");
		setTypeOfLeave("");
		setReason("");
		setManagerReason("");
		setUserId("");
		setEntrydate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
		setLeaveFromTo(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
		setLeaveToDate(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT));
		
		getAdvanceInfo();
		return LOAD;
	}

	public void getAdvanceInfo() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			if(strUserType!=null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
				setEmpId(strSessionEmpId);
			}

			con = db.makeConnection(con);
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			String strCurrency = "";
			if(uF.parseToInt(hmEmpCurrency.get(getEmpId())) > 0) {
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(getEmpId()));
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			} 
			
			pst = con.prepareStatement("select * from travel_advance_eligibility where emp_id=?");
			pst.setInt	(1, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
//			System.out.println("pst==>"+pst);
			StringBuilder sb = new StringBuilder();
			StringBuilder sbEligible = new StringBuilder();
			while(rs.next()) {
				sb.append("You are eligible for "+strCurrency+uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("eligibility_amount"))));
				sbEligible.append(rs.getString("is_eligible"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("sbEligible=====+>"+sbEligible.toString());
			
			pst = con.prepareStatement("select sum(advance_amount) as advance_amount from travel_advance where emp_id=?" +
					" and travel_id in (select leave_id from emp_leave_entry where emp_id=? and isTravel=true and is_approved=1 " +
					"and (is_modify is null or is_modify=false))");
			pst.setInt(1, uF.parseToInt(getEmpId()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				sb.append(" and you have already taken "+strCurrency+uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("advance_amount"))));
			}
			rs.close();
			pst.close();
			request.setAttribute("sb", sb.toString());
			request.setAttribute("sbEligible", sbEligible.toString());
			request.setAttribute("uF", uF);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String insertOnDutyEntry() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs=null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));

			pst = con.prepareStatement("INSERT INTO emp_leave_entry (emp_id,leave_from,leave_to,entrydate,leave_type_id,istravel, plan_name," +
					" place_from,from_time,to_time,reason,approval_from,approval_to_date,emp_no_of_leave)" +
					" VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
			
			pst.setInt	(1, uF.parseToInt(getEmpId()));//emp_id
			pst.setDate	(2, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));//leave_from
			pst.setDate	(3, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));//leave_to
			pst.setDate	(4, uF.getDateFormat(getEntrydate(), DATE_FORMAT));//entrydate
			pst.setInt	(5, ON_DUTY); //leave_type_id
			pst.setBoolean(6, true);//istravel
			pst.setString(7, getPlanName()); //plan_name
			pst.setString(8, getPlaceFrom()); //place_from
			pst.setTime(9, uF.getTimeFormat(getTravelFromTime(),TIME_FORMAT)); //from_time
			pst.setTime(10,  uF.getTimeFormat(getTravelToTime(),TIME_FORMAT)); //to_time
			pst.setString(11, getReason());//reason
			pst.setDate	(12, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));//approval_from
			pst.setDate	(13, uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT));//approval_to_date
			pst.setInt	(14, uF.parseToInt(uF.dateDifference(getLeaveFromTo(),DATE_FORMAT, getLeaveFromTo(), DATE_FORMAT,CF.getStrTimeZone())));//emp_no_of_leave
			System.out.println("emp_leave_entry pst==>"+pst);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0) {
//				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully applied for "+uF.dateDifference(getLeaveFromTo(),DATE_FORMAT, getLeaveToDate(),DATE_FORMAT,CF.getStrTimeZone()) + " day(s) on duty."+END);
				session.setAttribute(MESSAGE, SUCCESSM+"You have successfully applied for on duty."+END);
				int nType = uF.parseToInt(getType()) + 1;
				setType(""+nType);
				
				String travel_id=null;
				pst = con.prepareStatement("select max(leave_id)as leave_id from emp_leave_entry");
				rs=pst.executeQuery();
				while(rs.next()) {
					travel_id=rs.getString("leave_id");
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("insert into travel_advance (travel_id, advance_amount, entry_date, emp_id) values (?,?,?,?)");
				pst.setInt(1, uF.parseToInt(travel_id));
				pst.setDouble(2, uF.parseToDouble(getTravelAdvance()));
				pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(4, uF.parseToInt(getEmpId()));
				pst.execute();
				pst.close();
				
				List<String> alManagers = null;
//				System.out.println("workFlow"+uF.parseToBoolean(CF.getIsWorkFlow()));
				if(uF.parseToBoolean(CF.getIsWorkFlow())) {
					alManagers = insertLeaveApprovalMember(con,pst,rs,travel_id,uF);
				}
				
				String strDomain = request.getServerName().split("\\.")[0];
				Notifications nF = new Notifications(N_MANAGER_TRAVEL_REQUEST, CF);
				nF.setDomain(strDomain);
				nF.request = request;
				nF.setStrHostAddress(CF.getStrEmailLocalHost());
				nF.setStrHostPort(CF.getStrHostPort());
				nF.setStrContextPath(request.getContextPath());
				nF.setStrEmpId(getEmpId());
				nF.setSupervisor(false);
				nF.setEmailTemplate(true);
				for(int i=0; alManagers!=null && i<alManagers.size();i++) {
					pst = con.prepareStatement(selectEmpDetails1);
					pst.setInt(1, uF.parseToInt((String)alManagers.get(i)));
					rs = pst.executeQuery();
//					boolean flg=false;
					while(rs.next()) {
						nF.setStrSupervisorEmail(rs.getString("emp_email"));
						String strEmpMName = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName = " "+rs.getString("emp_mname");
							}
						}
						nF.setStrEmpMobileNo(rs.getString("emp_contactno_mob"));
						nF.setStrSupervisorName(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
						nF.setStrSupervisorContactNo(rs.getString("emp_contactno"));
						if(rs.getString("emp_email_sec")!=null && rs.getString("emp_email_sec").indexOf("@")>0) {
							nF.setStrEmpEmail(rs.getString("emp_email_sec"));
							nF.setStrEmailTo(rs.getString("emp_email_sec"));
						} else {
							nF.setStrEmpEmail(rs.getString("emp_email"));
							nF.setStrEmailTo(rs.getString("emp_email"));
						}
					}
				}
		
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return SUCCESS;

	}

	private List<String> insertLeaveApprovalMember(Connection con,PreparedStatement pst, ResultSet rs, String leave_id, UtilityFunctions uF) {
		List<String> alManagers = new ArrayList<String>();
		try {
			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			
			pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
			" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
			pst.setInt(1,uF.parseToInt(getPolicy_id()));
//			System.out.println("workflow pst==>"+pst);
			rs=pst.executeQuery();
			Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
			while(rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("member_type"));
				innerList.add(rs.getString("member_id"));
				innerList.add(rs.getString("member_position"));
				innerList.add(rs.getString("work_flow_mem"));
				innerList.add(rs.getString("work_flow_member_id"));
				
				hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
			}
			rs.close();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			Iterator<String> it=hmMemberMap.keySet().iterator();
			while(it.hasNext()) {
				String work_flow_member_id=it.next();
				List<String> innerList=hmMemberMap.get(work_flow_member_id);
				
				int memid=uF.parseToInt(innerList.get(1)); 
				String empid=request.getParameter(innerList.get(3)+memid);
				
				if(empid!=null && !empid.equals("")) {
					int userTypeId = memid;
					if(uF.parseToInt(innerList.get(0)) == 3) {
						userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
					}
					
					pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
							"work_flow_mem_id,is_approved,status,user_type_id)" +
							"values(?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1,uF.parseToInt(empid));
					pst.setInt(2,uF.parseToInt(leave_id));
					pst.setString(3,WORK_FLOW_TRAVEL);
					pst.setInt(4,uF.parseToInt(innerList.get(0)));
					//pst.setInt(5,uF.parseToInt(innerList.get(2)));
					pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
					pst.setInt(6,uF.parseToInt(innerList.get(4)));
					pst.setInt(7,0);
					pst.setInt(8,0);
					pst.setInt(9,userTypeId);
					System.out.println("insert workflow pst==>"+pst);
					pst.execute();
					pst.close();

					
					String date = "";
					if(getIsHalfDay()) {
						date = " date "+ uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat())+" to " + uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat());
					} else {
						date = " date "+ uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat())+" to " + uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT, CF.getStrReportDateFormat());
					}
					
					String alertData = "<div style=\"float: left;\"> Received a new ON Duty Request from <b>"+CF.getEmpNameMapByEmpId(con, getEmpId())+"</b>"+date+". ["+hmUserType.get(userTypeId+"")+"] </div>";
					String strSubAction = "";
					String alertAction = "";
					if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD)) || userTypeId == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
						if(userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
							strSubAction = "&currUserType="+hmUserType.get(userTypeId+"");
						}
						alertAction = "TeamRequests.action?pType=WR&callFrom=NotiApplyTravel"+strSubAction;
					} else {
						 alertAction = "Approvals.action?pType=WR&callFrom=NotiApplyTravel"+strSubAction;
					}
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empid); 
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(userTypeId+"");
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
					if(!alManagers.contains(empid)) {
						alManagers.add(empid);
					}
				}
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return alManagers;
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
			pst = con.prepareStatement(selectEmployeeLeaveEntry);
			pst.setInt(1, uF.parseToInt(strEdit));
			rs = pst.executeQuery();
			
			while (rs.next()) {
				setLeaveId(rs.getString("leave_id"));
				setEmpName((String)session.getAttribute("EMPNAME"));
				setEmpId(rs.getString("emp_id"));
				setStrEmpId(rs.getString("emp_id"));
				setTypeOfLeave(rs.getString("leave_type_id"));
				setReason(rs.getString("reason"));
				setLeaveFromTo(uF.getDateFormat(rs.getString("leave_from"), DBDATE, DATE_FORMAT));
				setLeaveToDate(uF.getDateFormat(rs.getString("leave_to"), DBDATE, DATE_FORMAT));
				setIsapproved(rs.getInt("is_approved"));
				setUserId(rs.getString("user_id"));
				setEntrydate(uF.getDateFormat(rs.getString("entrydate"), DBDATE, DATE_FORMAT));
				setIsHalfDay(rs.getBoolean("ishalfday"));
				setStrSession(rs.getString("session_no"));
				setPlanName(rs.getString("plan_name"));
				setDestinations(rs.getString("destinations"));
				setPlaceFrom(rs.getString("place_from"));
			}
			rs.close();
			pst.close();

			
			pst = con.prepareStatement("select * from travel_advance where travel_id = ?");
			pst.setInt(1, uF.parseToInt(strEdit));
			rs = pst.executeQuery();
			
			while (rs.next()) {
				setTravelAdvance(rs.getString("advance_amount"));
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
		userTypeList = new FillUserType(request).fillUserType();
//		empList = new FillEmployee().fillEmployeeCode(strUserType, strSessionEmpId);
		empList = new FillEmployee(request).fillEmployeeName(strUserType, strSessionEmpId, session);
		
		
		return SUCCESS;

	}

	public void validate() {
		strWorkingSession = new FillHalfDaySession().fillHalfDaySession();
		
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		setEmpName((String)session.getAttribute("EMPNAME"));
		
		if(getStrEmpId()==null) {
			setEmpId(strSessionEmpId);
		} else {
			setEmpId(getStrEmpId());
		}

		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return;
		
		if (getUserId()!=null && uF.parseToInt(getUserId())== 0) {
            addFieldError("UserId", " UserId is required");
        } 
       
        if (getEmpId()!=null && uF.parseToInt(getEmpId()) == 0) {
            addFieldError("EmpId", " Please select an employee from the list");
        }
        
        if (getTypeOfLeave()!=null && uF.parseToInt(getTypeOfLeave())== 0) {
            addFieldError("TypeOfLeave", " Please select leave type");
        } 
        if (getReason()!=null && getReason().length() == 0) {
            addFieldError("Reason", "Please enter valid reason.");
        } 
         
        if (getLeaveFromTo()!=null && getLeaveFromTo().length() == 0) {
            addFieldError("LeaveFromTo", " LeaveFromTo is required");
        } 
        if (getLeaveToDate()!=null && getLeaveToDate().length() == 0) {
            addFieldError("LeaveToDate", " LeaveToDate is required");
        } 
        if (!getIsHalfDay() &&  getLeaveToDate()!=null && getLeaveFromTo()!=null && uF.getDateFormat(getLeaveToDate(),DATE_FORMAT).before(uF.getDateFormat(getLeaveFromTo(), DATE_FORMAT))) {
            addFieldError("FromBeforeTo", "Travel start date should be before travel end date.");
        } 
        
        loadLaVidateLeaveEntry();
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
	public String getApproveLeaveFromTo() {
		return approveLeaveFromTo;
	}

	public void setApproveLeaveFromTo(String approveLeaveFromTo) {
		this.approveLeaveFromTo = approveLeaveFromTo;
	}

	public String getApproveLeaveToDate() {
		return approveLeaveToDate;
	}

	public void setApproveLeaveToDate(String approveLeaveToDate) {
		this.approveLeaveToDate = approveLeaveToDate;
	}
	public String getEntrydate() {
		return entrydate;
	}

	public void setEntrydate(String entrydate) {
		this.entrydate = entrydate;
	}
	public int isIsapproved() {
		return isapproved;
	}
	public void setIsapproved(int isapproved) {
		this.isapproved = isapproved;
	}
	
	public List<FillUserType> getUserTypeList() {
		return userTypeList;
	}
	public void setUserTypeList(List<FillUserType> userTypeList) {
		this.userTypeList = userTypeList;
	}
	public List<FillLeaveType> getLeaveTypeList() {
		return leaveTypeList;
	}
	public void setLeaveTypeList(List<FillLeaveType> leaveTypeList) {
		this.leaveTypeList = leaveTypeList;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public List<FillHalfDaySession> getStrWorkingSession() {
		return strWorkingSession;
	}

	public String getStrSession() {
		return strSession;
	}

	public void setStrSession(String strSession) {
		this.strSession = strSession;
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

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getTravelAdvance() {
		return travelAdvance;
	}

	public void setTravelAdvance(String travelAdvance) {
		this.travelAdvance = travelAdvance;
	}

	public String getPolicy_id() {
		return policy_id;
	}

	public void setPolicy_id(String policy_id) {
		this.policy_id = policy_id;
	}

	public boolean getIsConcierge() {
		return isConcierge;
	}

	public void setIsConcierge(boolean isConcierge) {
		this.isConcierge = isConcierge;
	}

	public String getModeOfTravel() {
		return modeOfTravel;
	}

	public void setModeOfTravel(String modeOfTravel) {
		this.modeOfTravel = modeOfTravel;
	}

	public boolean getIsBooking() {
		return isBooking;
	}

	public void setIsBooking(boolean isBooking) {
		this.isBooking = isBooking;
	}

	public String getBookingDetails() {
		return bookingDetails;
	}

	public void setBookingDetails(String bookingDetails) {
		this.bookingDetails = bookingDetails; 
	}

	public boolean getIsAccommodation() {
		return isAccommodation;
	}

	public void setIsAccommodation(boolean isAccommodation) {
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

	public String getTravelFromTime() {
		return travelFromTime;
	}

	public void setTravelFromTime(String travelFromTime) {
		this.travelFromTime = travelFromTime;
	}

	public String getTravelToTime() {
		return travelToTime;
	}

	public void setTravelToTime(String travelToTime) {
		this.travelToTime = travelToTime;
	}
}
