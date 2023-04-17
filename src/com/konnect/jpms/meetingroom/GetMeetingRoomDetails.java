package com.konnect.jpms.meetingroom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetMeetingRoomDetails extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	public CommonFunctions CF; 
	
	private String meetingRoomId;
	private String fromDate;
	private String toDate;
	private String fromTime;
	private String toTime;
	
	private static Logger log = Logger.getLogger(GetMeetingRoomDetails.class);
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, "/jsp/meetingroom/GetMeetingRoomDetails.jsp");
		request.setAttribute(TITLE, "Get Meeting Room Booking");

		getMeetingRoomDetails(uF);
		getMeetingRoomBookingDetails(uF);
		return SUCCESS;
	}
	
	private void getMeetingRoomDetails(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			con = db.makeConnection(con);
			List<String> meetingRoomDetails = new ArrayList<String>();
			pst = con.prepareStatement("select * from meeting_room_details where meeting_room_id = ?");
			pst.setInt(1, uF.parseToInt(getMeetingRoomId()));
			rs = pst.executeQuery();
			while(rs.next()) {
				meetingRoomDetails.add(uF.showData(rs.getString("meeting_room_id"), ""));//0
				meetingRoomDetails.add(uF.showData(rs.getString("meeting_room_name"), ""));//1
				meetingRoomDetails.add(uF.showData(rs.getString("room_length"), ""));//2
				meetingRoomDetails.add(uF.showData(rs.getString("room_width"), ""));//3
				meetingRoomDetails.add(uF.showData(rs.getString("seating_capacity"), ""));//4
				meetingRoomDetails.add(uF.showData(rs.getString("room_color_code"), ""));//5
				meetingRoomDetails.add(uF.showData(rs.getString("wlocation_id"), ""));//6
			}
			
			rs.close();
			pst.close();
			request.setAttribute("meetingRoomDetails",meetingRoomDetails);
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getMeetingRoomBookingDetails(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
			
			con = db.makeConnection(con);
			Map<String,List<String>> hmMeetingRoomBookingDetails = new HashMap<String,List<String>>();
			String flag = "false";
			pst = con.prepareStatement("select * from meeting_room_booking_details where ((booking_from between '"+ uF.getDateFormat(getFromDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getToDate(), DATE_FORMAT, DBDATE) + "' )"
					+" or (booking_to between '"+ uF.getDateFormat(getFromDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getToDate(), DATE_FORMAT, DBDATE) + "' )) and request_status = 0 and meeting_room_id = ?");

			pst.setInt(1, uF.parseToInt(getMeetingRoomId()));
			
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				if(rs.getString("request_status") != null && uF.parseToInt("request_status") == 0) {
					alInner.add(rs.getString("booking_id"));//0
					alInner.add("Date : From "+uF.getDateFormat(rs.getString("booking_from"),DBDATE,DATE_FORMAT)+" to "+ uF.getDateFormat(rs.getString("booking_to"),DBDATE,DATE_FORMAT));//1
					alInner.add("between "+uF.getTimeFormatStr(rs.getString("from_time").substring(0,rs.getString("from_time").lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM)+" to "+ uF.getTimeFormatStr(rs.getString("to_time").substring(0,rs.getString("to_time").lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));//2
					alInner.add("  waiting for approval.");//3
					hmMeetingRoomBookingDetails.put(rs.getString("booking_id"), alInner);
					
				}
			}
			
			request.setAttribute("hmMeetingRoomBookingDetails", hmMeetingRoomBookingDetails);
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public HttpServletRequest request;
	 
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	public String getMeetingRoomId() {
		return meetingRoomId;
	}
	public void setMeetingRoomId(String meetingRoomId) {
		this.meetingRoomId = meetingRoomId;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDate = toDate;
	}

	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	public String getToTime() {
		return toTime;
	}

	public void setToTime(String toTime) {
		this.toTime = toTime;
	}
}
