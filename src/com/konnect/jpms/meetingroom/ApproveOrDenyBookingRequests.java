package com.konnect.jpms.meetingroom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.library.ApproveOrDenyBookPurchase;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ApproveOrDenyBookingRequests extends ActionSupport implements ServletRequestAware, IStatements{
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	
	public CommonFunctions CF;
	
	private String bookingId;
	
	private String strComment;
	private String operation;
	
	private String strSubmit;
	private String strCancel;
	
	private static Logger log = Logger.getLogger(ApproveOrDenyBookingRequests.class);
	public String execute() throws Exception{
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE,"/jsp/meetingroom/ApproveOrDenyBookingRequests.jsp");
		request.setAttribute(TITLE, "Meeting room booking request approval");
		
		//getBookingRequestDetails(uF);
		
		if(getOperation()!=null && getOperation().equals("A")) {
			approveOrDenyBookIssueRequest(uF,"A");
			return LOAD;
		}
		
		if(getOperation()!=null && getOperation().equals("D")) {
			approveOrDenyBookIssueRequest(uF,"D");
			return LOAD;
		}
		
		return SUCCESS;
	}
	
	
	private void getBookingRequestDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from meeting_room_details md, meeting_room_booking_details mb where md.meeting_room_id= mb.meeting_room_id and booking_id = ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getBookingId()));
//			System.out.println("pst1==> " + pst);
			rs = pst.executeQuery();
			List<String> requestList = new ArrayList<String>();
			while (rs.next()) {
				requestList.add(uF.showData(rs.getString("booking_id"), "-"));
				requestList.add(uF.showData(hmEmpNames.get(rs.getString("booked_by")), "-"));
				requestList.add(uF.showData(rs.getString("meeting_room_name"), "-"));
				requestList.add(uF.getDateFormat(rs.getString("booking_from"),DBDATE,DATE_FORMAT));//5
				requestList.add(uF.getDateFormat(rs.getString("booking_to"),DBDATE,DATE_FORMAT));//6
				
				String from_time = rs.getString("from_time");
				String to_time = rs.getString("to_time");
				
				if(from_time != null && !from_time.equals("")){
					
					requestList.add(uF.getTimeFormatStr(from_time.substring(0,from_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));//7
				}else{
					requestList.add("");//7
				}
				if(to_time != null  && !to_time.equals("")){
					
					requestList.add(uF.getTimeFormatStr(to_time.substring(0,to_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));//8
				}else{
					requestList.add("");//8
				}
				requestList.add(uF.showData(rs.getString("request_quantity"), "0"));
				requestList.add(uF.showData(rs.getString("request_comment"), "-"));
				requestList.add(uF.getDateFormat(rs.getString("request_date"), DBDATE, CF.getStrReportDateFormat()));
				requestList.add(uF.getDateFormat(rs.getString("from_date"), DBDATE, CF.getStrReportDateFormat()));
				requestList.add(uF.getDateFormat(rs.getString("to_date"), DBDATE, CF.getStrReportDateFormat()));
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("requestList", requestList);
			
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void approveOrDenyBookIssueRequest(UtilityFunctions uF,String action) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			
			con = db.makeConnection(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update meeting_room_booking_details set approved_by=?, approved_date=?, booking_request_comment=?, request_status=? "
					+" where booking_id=?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(3,getStrComment());
			if(action!=null && action.equals("A")) {
				pst.setInt(4, 1);
			} else if(action!=null && action.equals("D")) {
				pst.setInt(4, -1);
			}
			pst.setInt(5, uF.parseToInt(getBookingId()));
			pst.executeUpdate();
			pst.close();
			
			String booked_by = "";
			pst = con.prepareStatement("select * from meeting_room_booking_details where booking_id=?");
			pst.setInt(1, uF.parseToInt(getBookingId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				booked_by = rs.getString("booked_by");
			}
			rs.close();
			pst.close();
			
			String strDomain = request.getServerName().split("\\.")[0];
			if(booked_by != null && !booked_by.equals("")) {
				String strApproveDeny = "denied";
				if(action!=null && action.equals("A")) {
					strApproveDeny = "approved";
				}
				String alertData = "<div style=\"float: left;\"> A new Meeting Room request is "+strApproveDeny+" by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
				String alertAction = "Calendar.action?pType=WR&dataType=MRB";
				
				UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
				userAlerts.setStrDomain(strDomain);
				userAlerts.setStrEmpId(booked_by);
				userAlerts.setStrData(alertData);
				userAlerts.setStrAction(alertAction);
				userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
				userAlerts.setStatus(INSERT_WR_ALERT);
				Thread t = new Thread(userAlerts);
				t.run();
				
//				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//				userAlerts.setStrDomain(strDomain);
//				userAlerts.setStrEmpId(booked_by);
//				userAlerts.set_type(MEETING_ROOM_BOOKING_REQUEST_APPROVED_ALERT);
//				userAlerts.setStatus(INSERT_ALERT);
//				Thread t = new Thread(userAlerts);
//				t.run();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}


	public String getBookingId() {
		return bookingId;
	}


	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}


	public String getStrComment() {
		return strComment;
	}


	public void setStrComment(String strComment) {
		this.strComment = strComment;
	}


	public String getOperation() {
		return operation;
	}


	public void setOperation(String operation) {
		this.operation = operation;
	}


	public String getStrSubmit() {
		return strSubmit;
	}


	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
	}


	public String getStrCancel() {
		return strCancel;
	}


	public void setStrCancel(String strCancel) {
		this.strCancel = strCancel;
	}
}
