package com.konnect.jpms.reports;

import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TravelApprovalReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	String strUserType = null;
	String strUserTypeId = null;
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null;
	String leaveStatus;
	String alertStatus;
	String alert_type;
	
	String currUserType;
	String alertID;
	private String f_org;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		
		UtilityFunctions uF = new UtilityFunctions(); 
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/reports/TravelApprovalReport.jsp");
		request.setAttribute(TITLE, "Approve Travel");
		
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		} 
		 
		if(getF_org() == null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getCurrUserType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setCurrUserType("MYTEAM");
		}
		
		String cancelStatus = (String) request.getParameter("cancelStatus");
		if(uF.parseToInt(cancelStatus)==1) {
			cancelTravelLeave(uF);
			return "ajax";
		}
		
		if(getLeaveStatus()==null) {
			setLeaveStatus("2");
		}
//		System.out.println("getStrStartDate==>"+getStrStartDate()+"--getStrEndDate==>"+getStrEndDate());
		
		String[] arrDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
		if(getStrStartDate() == null || getStrStartDate().trim().equals("") || getStrStartDate().trim().equalsIgnoreCase("NULL")) {
			setStrStartDate(arrDates[0]);
		}
		if(getStrEndDate() == null || getStrEndDate().trim().equals("") || getStrEndDate().trim().equalsIgnoreCase("NULL")) {
			setStrEndDate(arrDates[1]);
		}
		/*if((getStrStartDate() == null || getStrStartDate().trim().equals("") || getStrStartDate().trim().equalsIgnoreCase("NULL"))
				|| (getStrEndDate() == null || getStrEndDate().trim().equals("") || getStrEndDate().trim().equalsIgnoreCase("NULL"))) {
			setStrStartDate(null);
			setStrEndDate(null);
		} else {
			String strSDate = URLDecoder.decode(getStrStartDate());
			String strEDate = URLDecoder.decode(getStrEndDate());
			setStrStartDate(strSDate);
			setStrEndDate(strEDate);
		}*/
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
//		if(getAlertStatus()!=null && getAlert_type()!=null && getAlert_type().equals(TRAVEL_REQUEST_ALERT)) {
//			updateUserAlerts();
//		}

		viewLeaveAppraoval1(uF);
		
		return loadManagerLeaveApproval(uF);

	}
	
	private void cancelTravelLeave(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			String leaveId = (String) request.getParameter("leaveId");
			String cancelReason = (String) request.getParameter("cancelReason");
			
			con = db.makeConnection(con);
			con.setAutoCommit(false);
			
			pst = con.prepareStatement("update travel_application_register set is_modify = true, modify_date=?, modify_by=? where travel_id= ?");
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(3, uF.parseToInt(leaveId));
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0) {
				pst = con.prepareStatement("update emp_leave_entry set is_modify = true, modify_date=?, modify_by=?,cancel_reason=? where leave_id  = ?");
				pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(2, uF.parseToInt((String)session.getAttribute(EMPID)));
				pst.setString(3, cancelReason);
				pst.setInt(4, uF.parseToInt(leaveId));
				pst.execute();
				pst.close();
			}	
			con.commit();
			request.setAttribute("STATUS_MSG", "Canceled");
			
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

//	private void updateUserAlerts() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
//		try {
//			con = db.makeConnection(con);
//			
//			String strDomain = request.getServerName().split("\\.")[0];
//			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//			userAlerts.setStrDomain(strDomain);
//			userAlerts.setStrEmpId(""+nEmpId);
//			userAlerts.set_type(TRAVEL_REQUEST_ALERT);
//			userAlerts.setStatus(UPDATE_ALERT);
//			Thread t = new Thread(userAlerts);
//			t.run();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	 
	private void viewLeaveAppraoval1(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		
		try {
			
			/*if(getStrStartDate()==null && getStrEndDate()==null) {

				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
				int nMaxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				int nMinDate = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
				
				setStrStartDate(uF.getDateFormat(nMinDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
				setStrEndDate(uF.getDateFormat(nMaxDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
			}*/
			
			con = db.makeConnection(con);
			
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			
//			Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
//			String locationID=hmEmpWlocationMap.get(strSessionEmpId);
			
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			
//			pst = con.prepareStatement("select effective_id,max(member_position) as member_position from work_flow_details wf where is_approved=0 " +
//					"and effective_type='"+WORK_FLOW_TRAVEL+"' group by effective_id");
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,min(member_position) as member_position from work_flow_details wf where is_approved=0 " +
					"and effective_type='"+WORK_FLOW_TRAVEL+"' and effective_id in (select leave_id from emp_leave_entry ele where leave_type_id="+TRAVEL_LEAVE+" and is_approved > -2 and encashment_status=false ");
			if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
				sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
				sbQuery.append(" and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");	
			}
			sbQuery.append(") group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String, String> hmNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmNextApproval.put(rs.getString("effective_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select effective_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
//			" and is_approved=0 and effective_type='"+WORK_FLOW_TRAVEL+"' group by effective_id ");
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,user_type_id,min(member_position)as member_position from work_flow_details wfd where emp_id=? " +
			" and is_approved=0 and effective_type='"+WORK_FLOW_TRAVEL+"' and effective_id in (select leave_id from emp_leave_entry ele where leave_type_id="+TRAVEL_LEAVE+" and is_approved > -2 and encashment_status=false ");
			if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
				sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
				sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
			}
			sbQuery.append(")");
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append("group by effective_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(strSessionEmpId));
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(2, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(2, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(3, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
			rs = pst.executeQuery();
			Map<String, String> hmMemNextApproval = new HashMap<String, String>();
			while(rs.next()) {
				hmMemNextApproval.put(rs.getString("effective_id")+"_"+rs.getString("user_type_id"), rs.getString("member_position"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_TRAVEL+"' group by effective_id");
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id from work_flow_details where is_approved=-1 and effective_type='"+WORK_FLOW_TRAVEL+"'" +
					" and effective_id in (select leave_id from emp_leave_entry ele where leave_type_id="+TRAVEL_LEAVE+" and is_approved > -2 and encashment_status=false ");
			if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
				sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
				sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
			}
			sbQuery.append(") group by effective_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			List<String> deniedList=new ArrayList<String>();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("effective_id"))) {
					deniedList.add(rs.getString("effective_id"));
				}
			}
			rs.close();
			pst.close();			
			
			pst = con.prepareStatement("select leave_id from emp_leave_entry where is_approved=-1 and leave_type_id="+TRAVEL_LEAVE+" and approval_from >=? and approval_from <=?");
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));	
			rs = pst.executeQuery();			
			while(rs.next()) {
				if(!deniedList.contains(rs.getString("leave_id"))) {
					deniedList.add(rs.getString("leave_id"));
				}
			}
			rs.close();
			pst.close();
			
			
//			pst = con.prepareStatement("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
//			" and effective_type='"+WORK_FLOW_TRAVEL+"' group by effective_id,is_approved");
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,is_approved from work_flow_details where is_approved=1 and member_type=3 " +
					" and effective_type='"+WORK_FLOW_TRAVEL+"' and effective_id in (select leave_id from emp_leave_entry ele where leave_type_id="+TRAVEL_LEAVE+" and is_approved > -2 and encashment_status=false ");
			if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
				sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
				sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
			}
			sbQuery.append(") group by effective_id,is_approved");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproval = new HashMap<String, String>();			
			while(rs.next()) {
				hmAnyOneApproval.put(rs.getString("effective_id"), rs.getString("is_approved"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
//					" and effective_type='"+WORK_FLOW_TRAVEL+"' group by effective_id,emp_id,user_type_id");
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type=3 " +
					" and effective_type='"+WORK_FLOW_TRAVEL+"' and effective_id in (select leave_id from emp_leave_entry ele where leave_type_id="+TRAVEL_LEAVE+" and is_approved > -2 and encashment_status=false ");
			if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
				sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
				sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
			}
			sbQuery.append(") group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			Map<String, String> hmAnyOneApproeBy = new HashMap<String, String>();		
			Map<String,String> hmWorkFlowUserTypeId = new HashMap<String, String>();		
			while(rs.next()) {
				hmAnyOneApproeBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
//				" and effective_type='"+WORK_FLOW_TRAVEL+"' group by effective_id,emp_id,user_type_id");
			sbQuery=new StringBuilder();
			sbQuery.append("select effective_id,emp_id,user_type_id from work_flow_details where member_type!=3 " +
				" and effective_type='"+WORK_FLOW_TRAVEL+"' and effective_id in (select leave_id from emp_leave_entry ele where leave_type_id="+TRAVEL_LEAVE+" and is_approved > -2 and encashment_status=false ");
			if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
				sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
				sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
			}
			sbQuery.append(") group by effective_id,emp_id,user_type_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();			
			Map<String, String> hmotherApproveBy = new HashMap<String, String>();			
			while(rs.next()) {
				hmotherApproveBy.put(rs.getString("effective_id"), rs.getString("emp_id"));
				hmWorkFlowUserTypeId.put(rs.getString("effective_id"), rs.getString("user_type_id"));
			}
			rs.close();
			pst.close();
			
			
//			pst = con.prepareStatement("select emp_id,effective_id from work_flow_details where effective_type='"+WORK_FLOW_TRAVEL+"' order by effective_id,member_position");
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,effective_id,user_type_id from work_flow_details where effective_type='"+WORK_FLOW_TRAVEL+"'" +
					" and effective_id in (select leave_id from emp_leave_entry ele where leave_type_id="+TRAVEL_LEAVE+" and is_approved > -2 and encashment_status=false ");
			if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
				sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' ");
				sbQuery.append("  and to_date(approval_from::text,'"+DBDATE+"') <='"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"'  ");	
			}
			
			sbQuery.append(")");
			
			if(strUserType != null && strUserType.equals(ADMIN)) {
				sbQuery.append(" and (user_type_id=? or user_type_id=?) ");
			} else {
				sbQuery.append(" and user_type_id=? ");
			}
			sbQuery.append(" order by effective_id,member_position");
			
			pst = con.prepareStatement(sbQuery.toString());
			if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
				pst.setInt(1, uF.parseToInt(strBaseUserTypeId));
			} else {
				pst.setInt(1, uF.parseToInt(strUserTypeId));
			}
			if(strUserType != null && strUserType.equals(ADMIN)) {
				pst.setInt(2, uF.parseToInt(hmUserTypeIdMap.get(HRMANAGER)));
			}
			rs = pst.executeQuery();			
			Map<String, List<String>> hmCheckEmp = new HashMap<String, List<String>>();	
			Map<String, List<String>> hmCheckEmpUserType = new HashMap<String, List<String>>();
			while(rs.next()) {
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("effective_id"));
				if(checkEmpList==null)checkEmpList=new ArrayList<String>();				
				checkEmpList.add(rs.getString("emp_id"));
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("effective_id")+"_"+rs.getString("emp_id"));
				if(checkEmpUserTypeList == null)checkEmpUserTypeList = new ArrayList<String>();				
				checkEmpUserTypeList.add(rs.getString("user_type_id"));
				
				hmCheckEmp.put(rs.getString("effective_id"), checkEmpList);
				hmCheckEmpUserType.put(rs.getString("effective_id")+"_"+rs.getString("emp_id"), checkEmpUserTypeList);
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select ud.emp_id from user_details ud,employee_official_details eod,employee_personal_details epd where " +
//					" ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'");
//			pst.setInt(1, uF.parseToInt(locationID));
//			rs = pst.executeQuery();			
//			Map<String, String> hmEmpByLocation = new HashMap<String, String>();			
//			while(rs.next()) {
//				hmEmpByLocation.put(rs.getString("emp_id"), rs.getString("emp_id"));
//			}
//			rs.close();
//			pst.close();
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			List<String> alEmployeeList = new ArrayList<String>();	
			List<String> alList = new ArrayList<String>();		
			sbQuery=new StringBuilder();
//			sbQuery.append("select * from emp_leave_entry ele where leave_type_id="+TRAVEL_LEAVE+"" +
//					"and (leave_from,leave_to) overlaps (to_date(?::text,'yyyy-MM-dd'),to_date(?::text,'yyyy-MM-dd')+1) and is_approved > -2 and encashment_status=false ");
			sbQuery.append("select e.*,wfd.user_type_id as user_type from (select * from emp_leave_entry ele " +
					"where leave_type_id="+TRAVEL_LEAVE+" and is_approved > -2 and encashment_status=false ");
			sbQuery.append(" and approval_from >=? and approval_from <=? ");
			if(uF.parseToInt(getLeaveStatus())==1) {
				sbQuery.append(" and is_approved=1");
			} else if(uF.parseToInt(getLeaveStatus())==2) {
				sbQuery.append(" and is_approved=0");
			} else if(uF.parseToInt(getLeaveStatus())==3) {
				sbQuery.append(" and is_approved=-1");
			}			
			sbQuery.append(") e, work_flow_details wfd where e.leave_id = wfd.effective_id and wfd.effective_type = '"+WORK_FLOW_TRAVEL+"' ");
			if(strUserType != null && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and wfd.emp_id = "+uF.parseToInt(strSessionEmpId)+" ");
				if (strBaseUserType != null && strBaseUserType.equalsIgnoreCase(getCurrUserType())) {
//					sbQuery.append(" and (wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" or wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ) ");
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strBaseUserTypeId)+" ");
				} else {
					sbQuery.append(" and wfd.user_type_id = "+uF.parseToInt(strUserTypeId)+" ");
				}
			}
			sbQuery.append(" order by e.entrydate desc");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));		
//			System.out.println("pst=====>"+pst);
			rs=pst.executeQuery();
			while(rs.next()) {
				
				if(!alEmployeeList.contains(rs.getString("emp_id")) ) {
					alEmployeeList.add(rs.getString("emp_id"));
				}
				
				List<String> checkEmpList=hmCheckEmp.get(rs.getString("leave_id"));
				if(checkEmpList==null) checkEmpList=new ArrayList<String>();
				
				List<String> checkEmpUserTypeList = hmCheckEmpUserType.get(rs.getString("leave_id")+"_"+strSessionEmpId);
				if(checkEmpUserTypeList==null) checkEmpUserTypeList = new ArrayList<String>();
				
				boolean checkGHRInWorkflow = true;
				if(checkEmpUserTypeList.contains(hmUserTypeIdMap.get(HRMANAGER)) && !checkEmpUserTypeList.contains(hmUserTypeIdMap.get(ADMIN)) && strUserType != null && strUserType.equals(ADMIN)) {
					checkGHRInWorkflow = false;
				}
				
//				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN) && !strUserType.equalsIgnoreCase(HRMANAGER)) {
				if(!checkEmpList.contains(strSessionEmpId) && !strUserType.equalsIgnoreCase(ADMIN)) {
					continue;
				}
				
//				if(strUserType.equalsIgnoreCase(HRMANAGER) && hmEmpByLocation.get(rs.getString("emp_id"))==null) {
//					continue;
//				}

				String userType = rs.getString("user_type");				
				if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && alList.contains(rs.getString("leave_id"))) {
					continue;
				} else if(!checkEmpList.contains(strSessionEmpId) && strUserType!=null && strUserType.equalsIgnoreCase(ADMIN) && !alList.contains(rs.getString("leave_id"))) {
					userType = strUserTypeId;
					alList.add(rs.getString("leave_id"));
				} else if(!checkEmpUserTypeList.contains(userType)) {
//					System.out.println("4 RID ===>> "+rs.getString("recruitment_id")+" -- USRTYPE ===>> "+rs.getString("user_type"));
					continue;	
				}
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(hmEmployeeNameMap.get(rs.getString("emp_id")));
				alInner.add("");
				alInner.add(uF.getDateFormat(rs.getString("entrydate"), DBDATE, CF.getStrReportDateFormat()));
				
				
				if(rs.getInt("is_approved")==1) {
					alInner.add(uF.getDateFormat(rs.getString("approval_from"), DBDATE, CF.getStrReportDateFormat()));
					alInner.add(uF.getDateFormat(rs.getString("approval_to_date"), DBDATE, CF.getStrReportDateFormat()));
//					alInner.add("");
//					alInner.add("");
				} else {
					alInner.add(uF.getDateFormat(rs.getString("leave_from"), DBDATE, CF.getStrReportDateFormat()));
					alInner.add(uF.getDateFormat(rs.getString("leave_to"), DBDATE, CF.getStrReportDateFormat()));
//					alInner.add(uF.getDateFormat(rs.getString("from_time"), DBTIME, TIME_FORMAT));
//					alInner.add(uF.getDateFormat(rs.getString("to_time"), DBTIME, TIME_FORMAT));
				}
				// TODO: Start Dattatray
				alInner.add(uF.getDateFormat(rs.getString("from_time"), DBTIME, TIME_FORMAT));
				alInner.add(uF.getDateFormat(rs.getString("to_time"), DBTIME, TIME_FORMAT));
				
				if (uF.parseToInt(rs.getString("leave_type_id")) == ON_DUTY) {
					alInner.add(uF.showData(rs.getString("emp_no_of_leave")+  ((uF.parseToBoolean(rs.getString("is_modify")))?"<div title=\"Canceled\" class=\"leftearly\">&nbsp;</div>":""),"")+" (OD)");
				}// TODO: End Dattatray
				else{
					alInner.add(uF.showData(rs.getString("emp_no_of_leave")+  ((uF.parseToBoolean(rs.getString("is_modify")))?"<div title=\"Canceled\" class=\"leftearly\">&nbsp;</div>":""),""));
				}
				
//				alInner.add(uF.showData(rs.getString("emp_no_of_leave"),""));
				alInner.add(uF.showData(rs.getString("reason"),"-"));
				alInner.add(uF.showData(rs.getString("manager_reason"),"-"));
				
				StringBuilder sbCheckApproveby=new StringBuilder();
				if(hmAnyOneApproeBy!=null && hmAnyOneApproeBy.get(rs.getString("leave_id"))!=null) {
//					String approvedby=hmAnyOneApproeBy.get(rs.getString("leave_id"));
//					String strUserTypeName = uF.parseToInt(hmWorkFlowUserTypeId.get(rs.getString("leave_id"))) > 0 ? " ("+uF.showData(hmUserTypeMap.get(hmWorkFlowUserTypeId.get(rs.getString("leave_id"))), "")+")" : "";
//					sbCheckApproveby.append(hmEmployeeNameMap.get(approvedby.trim())+strUserTypeName);
					// TODO: Start Dattatray
					if(uF.parseToInt(rs.getString("leave_type_id")) == ON_DUTY) {
						alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','" + rs.getString("leave_id") + "','" + rs.getString("emp_id") + "','" + getLeaveStatus() + "','"
							+ getStrStartDate() + "','" + getStrEndDate() + "','" + userType
							+ "');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve On Duty\"></i></a> "
							+ "&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','" + rs.getString("leave_id") + "','"
							+ hmEmployeeNameMap.get(rs.getString("emp_id")) + "','" + rs.getString("emp_id") + "','" + getLeaveStatus() + "','"
							+ getStrStartDate() + "','" + getStrStartDate() + "','" + userType
							+ "');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Deny On Duty\"></i></a> ");
						sbCheckApproveby.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("leave_id")+"','"+hmEmployeeNameMap.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
					}// TODO: End Dattatray
					 else {
						sbCheckApproveby.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("leave_id")+"','"+hmEmployeeNameMap.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
					}
				} else if(hmotherApproveBy!=null && hmotherApproveBy.get(rs.getString("leave_id"))!=null) {
					// TODO: Start Dattatray
					if(uF.parseToInt(rs.getString("leave_type_id")) == ON_DUTY) {
						alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','" + rs.getString("leave_id") + "','" + rs.getString("emp_id") + "','" + getLeaveStatus() + "','"
							+ getStrStartDate() + "','" + getStrEndDate() + "','" + userType
							+ "');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve On Duty\"></i></a> "
							+ "&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','" + rs.getString("leave_id") + "','" + rs.getString("emp_id") + "','" + getLeaveStatus() + "','"
							+ getStrStartDate() + "','" + getStrStartDate() + "','" + userType
							+ "');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Deny On Duty\"></i></a> ");
						sbCheckApproveby.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("leave_id")+"','"+hmEmployeeNameMap.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
					}// TODO: End Dattatray
					else {
						sbCheckApproveby.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("leave_id")+"','"+hmEmployeeNameMap.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
					}
				} else {
					sbCheckApproveby.append("");
				}
				
				if(deniedList.contains(rs.getString("leave_id"))) {
					 /*alInner.add("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
					
					alInner.add(sbCheckApproveby.toString());
				} else if(rs.getInt("is_approved")==1) {							
					/*alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
					alInner.add(sbCheckApproveby.toString());
				} else if(uF.parseToInt(hmAnyOneApproval.get(rs.getString("leave_id")))==1 && uF.parseToInt(hmAnyOneApproval.get(rs.getString("leave_id")))==rs.getInt("is_approved")) {
					/*alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
					
					alInner.add(sbCheckApproveby.toString());
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("leave_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("leave_id")+"_"+userType)) && uF.parseToInt(hmNextApproval.get(rs.getString("leave_id")))>0) {
					 /*alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveOrDenyTravelRequest('"+rs.getString("leave_id")+"','"+userType+"','"+hmEmployeeNameMap.get(rs.getString("emp_id"))+"');\"><img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" /></a>");*/
					// TODO: Start Dattatray
					if(uF.parseToInt(rs.getString("leave_type_id")) == ON_DUTY) {
						alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','" + rs.getString("leave_id") + "','" + rs.getString("emp_id") + "','" + getLeaveStatus() + "','"
							+ getStrStartDate() + "','" + getStrEndDate() + "','" + userType
							+ "');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve On Duty\"></i></a> "
							+ "&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','" + rs.getString("leave_id") + "','" + rs.getString("emp_id") + "','" + getLeaveStatus() + "','"
							+ getStrStartDate() + "','" + getStrEndDate() + "','" + userType
							+ "');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Deny On Duty\"></i></a> ");
					}// TODO: End Dattatray
					else {
						alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveOrDenyTravelRequest('"+rs.getString("leave_id")+"','"+userType+"','"+hmEmployeeNameMap.get(rs.getString("emp_id"))+"');\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\"></i></a>");
					}
					
					alInner.add(sbCheckApproveby.toString());
				} else if(uF.parseToInt(hmNextApproval.get(rs.getString("leave_id"))) < uF.parseToInt(hmMemNextApproval.get(rs.getString("leave_id")+"_"+userType)) || (uF.parseToInt(hmNextApproval.get(rs.getString("leave_id")))==0 && uF.parseToInt(hmNextApproval.get(rs.getString("leave_id")))==uF.parseToInt(hmMemNextApproval.get(rs.getString("leave_id")+"_"+userType)))) {
						if(rs.getInt("is_approved")==0) {
							if(strUserType.equalsIgnoreCase(ADMIN) && uF.parseToInt(strUserTypeId) == uF.parseToInt(userType)) {
								/*alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveOrDenyTravelRequest('"+rs.getString("leave_id")+"','"+userType+"','"+hmEmployeeNameMap.get(rs.getString("emp_id"))+"');\"><img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" /></a>");*/
								// TODO: Start Dattatray
								if(uF.parseToInt(rs.getString("leave_type_id")) == ON_DUTY) {
									alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','" + rs.getString("leave_id") + "','" + rs.getString("emp_id") + "','" + getLeaveStatus() + "','"
										+ getStrStartDate() + "','" + getStrStartDate() + "','" + userType
										+ "');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve On Duty\"></i></a> "
										+ "&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','" + rs.getString("leave_id") + "','" + rs.getString("emp_id") + "','" + getLeaveStatus() + "','"
										+ getStrStartDate() + "','" + getStrStartDate() + "','" + userType
										+ "');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Deny On Duty\"></i></a> ");
									sbCheckApproveby.append("<a href=\"javascript:void(0)\" onclick=\"getApprovalStatus('"+rs.getString("leave_id")+"','"+hmEmployeeNameMap.get(rs.getString("emp_id"))+"');\" style=\"margin-left: 10px;\">View</a>");
								}// TODO: End Dattatray 
								else {
									alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveOrDenyTravelRequest('"+rs.getString("leave_id")+"','"+userType+"','"+hmEmployeeNameMap.get(rs.getString("emp_id"))+"');\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"click to approve/deny\"></i></a>");
								}
								
							} else {
//								alInner.add("<a href=\""+request.getContextPath()+"/ApproveTravel.action?E="+rs.getString("leave_id")+" \"><img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" /></a>");
								/*alInner.add("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
								alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"Waiting for workflow\"  style=\"color:#f7ee1d\"></i>");
								
								if(!checkGHRInWorkflow) {
									/*alInner.add("&nbsp;|&nbsp;&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveOrDenyTravelRequest('"+rs.getString("leave_id")+"','"+userType+"','"+hmEmployeeNameMap.get(rs.getString("emp_id"))+"');\"><img title=\"click to approve/deny ("+ADMIN+")\" src=\"images1/icons/pending.png\" border=\"0\" /></a>");*/
									alInner.add("&nbsp;|&nbsp;&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveOrDenyTravelRequest('"+rs.getString("leave_id")+"','"+userType+"','"+hmEmployeeNameMap.get(rs.getString("emp_id"))+"');\"><i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#b71cc5\" title=\"click to approve/deny ("+ADMIN+")\"></i></a>");
								}
							}
							alInner.add(sbCheckApproveby.toString()); 
						} else if(rs.getInt("is_approved")==1) {							
							/* alInner.add("<img title=\"Approved\" src=\"images1/icons/approved.png\" border=\"0\" />");*/
							 alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\" title=\"Approved\"></i>");
							alInner.add(sbCheckApproveby.toString());
						} else {
							/*alInner.add("<img title=\"Denied\" src=\"images1/icons/denied.png\" border=\"0\" />");*/
							alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\" title=\"Denied\"></i>");
							alInner.add(sbCheckApproveby.toString());
						}
						
				} else {
//					alInner.add("<img title=\"Cancel Travel\" src=\"images1/icons/pullout.png\" border=\"0\" />");
					if(strUserType.equalsIgnoreCase(ADMIN)) {
						/*alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveOrDenyTravelRequest('"+rs.getString("leave_id")+"','"+userType+"','"+hmEmployeeNameMap.get(rs.getString("emp_id"))+"');\"><img title=\"click to approve/deny\" src=\"images1/icons/pending.png\" border=\"0\" /></a>");*/
						// TODO: Start Dattatray
						if(uF.parseToInt(rs.getString("leave_type_id")) == ON_DUTY) {
							alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveDeny('1','" + rs.getString("leave_id") + "','" + rs.getString("emp_id") + "','" + getLeaveStatus() + "','"
								+ getStrStartDate() + "','" + getStrStartDate() + "','" + userType
								+ "');\"><i class=\"fa fa-check-circle checknew\" aria-hidden=\"true\"  title=\"Approve On Duty\"></i></a> "
								+ "&nbsp;<a href=\"javascript:void(0);\" onclick=\"approveDeny('-1','" + rs.getString("leave_id") + "','" + rs.getString("emp_id") + "','" + getLeaveStatus() + "','"
								+ getStrStartDate() + "','" + getStrStartDate() + "','" + userType
								+ "');\"><i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Deny On Duty\"></i></a> ");
						}// TODO: End Dattatray
						else {
							alInner.add("<a href=\"javascript:void(0);\" onclick=\"approveOrDenyTravelRequest('"+rs.getString("leave_id")+"','"+userType+"','"+hmEmployeeNameMap.get(rs.getString("emp_id"))+"');\"><i class=\"fa fa-circle\" aria-hidden=\"true\" title=\"click to approve/deny\" style=\"color:#b71cc5\"></i></a>");
						}
					} else {
						/*alInner.add("<img src=\"images1/icons/re_submit.png\" title=\"Waiting for workflow\" />");*/
						alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\" title=\"Waiting for workflow\"></i>");
					}
					alInner.add(sbCheckApproveby.toString());
				}
				
				alInner.add(rs.getString("leave_id"));
				alInner.add(rs.getString("leave_type_id")); 
				
				alInner.add("");
				alInner.add(rs.getString("is_approved"));
				
				alInner.add(rs.getString("is_modify"));
				if(uF.parseToBoolean(rs.getString("is_modify"))) {
					alInner.add("This travel has been canceled by "+uF.showData(hmEmployeeNameMap.get(rs.getString("modify_by")), "")+ " on "+uF.getDateFormat(rs.getString("modify_date"), DBDATE, CF.getStrReportDateFormat()));
				} else {
					alInner.add("");
				}
				alInner.add(uF.showData(hmUserTypeMap.get(userType), ""));
				
				reportList.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", reportList);
					
		} catch (Exception e) {
			e.printStackTrace(); 
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
}

	public String loadManagerLeaveApproval(UtilityFunctions uF) {
		getSelectedFilter(uF);
		return "load";
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
			alFilter.add("STATUS");
			if(uF.parseToInt(getLeaveStatus())==1) { 
				hmFilter.put("STATUS", "Approved");
			} else if(uF.parseToInt(getLeaveStatus())==2) {
				hmFilter.put("STATUS", "Pending");
			} else if(uF.parseToInt(getLeaveStatus())==3) {
				hmFilter.put("STATUS", "Denied");
			} else {
				hmFilter.put("STATUS", "All");
			}
			
			if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))) {
				alFilter.add("FROMTO");
				hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
			}
			
		String selectedFilter = CF.getSelectedFilter1(CF, uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	String strStartDate;
	String strEndDate;
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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

	public String getLeaveStatus() {
		return leaveStatus;
	}

	public void setLeaveStatus(String leaveStatus) {
		this.leaveStatus = leaveStatus;
	}

	public String getAlertStatus() {
		return alertStatus;
	}

	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}

	public String getAlert_type() {
		return alert_type;
	}

	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getCurrUserType() {
		return currUserType;
	}

	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

}
