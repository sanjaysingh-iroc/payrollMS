package com.konnect.jpms.meetingroom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MeetingRoomBooking extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	public CommonFunctions CF; 
	
	private String strMeetingRoomId;
	private String strRoomIdCheck;
	
	String alertID;
	
	private static Logger log = Logger.getLogger(MeetingRoomBooking.class);
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, "/jsp/meetingroom/MeetingRoomBooking.jsp");
		request.setAttribute(TITLE, "Meeting Room Booking");
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
//		if(strUserType != null && strUserType.equals(EMPLOYEE)) {
//			updateUserAlerts(uF, MEETING_ROOM_BOOKING_REQUEST_APPROVED_ALERT);
//		}
		
		
		List<String> reportListEmp = new ArrayList<String>();
		getBookingdates(uF, reportListEmp);
	    getMeetingRoomDetails(uF);
	    getMeetingRoomBookingDetails(uF, reportListEmp);
	    request.setAttribute("reportListEmp", reportListEmp);
		return SUCCESS;
	}

//	private void updateUserAlerts(UtilityFunctions uF, String alertType) {
//		Connection con = null;
//		Database db = new Database();
//		db.setRequest(request);
//		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
//		try {
//			con = db.makeConnection(con);
//			String strDomain = request.getServerName().split("\\.")[0];
//			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//			userAlerts.setStrDomain(strDomain);
//			userAlerts.setStrEmpId(""+nEmpId);
//			userAlerts.set_type(alertType);
//			userAlerts.setStatus(UPDATE_ALERT);
//			Thread t = new Thread(userAlerts);
//			t.run();
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//		}
//	}
	
	private void getBookingdates(UtilityFunctions uF, List<String> reportListEmp) {
		String[] strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
		String strCalendarYearStart = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
		String strCalendarYearEnd = strCalendarYearDates[1];
		String diffInDays = uF.dateDifference(strCalendarYearStart, DATE_FORMAT,strCalendarYearEnd, DATE_FORMAT);
		
		if(uF.parseToInt(diffInDays)>0){
			java.sql.Date sDate = uF.getDateFormat(strCalendarYearStart,DATE_FORMAT);
			String bookingDate = strCalendarYearStart;
			for(int i =1;i<=(uF.parseToInt(diffInDays));i++){
				String newEventDate = uF.getDateFormatUtil(sDate, DBDATE) ;
				reportListEmp.add("{url:'javascript:openMeetingRoomBookingPopup("+uF.getDateFormat(newEventDate, DBDATE, "dd")+", "+(uF.parseToInt(uF.getDateFormat(newEventDate, DBDATE, "M")))+", "+uF.getDateFormat(newEventDate, DBDATE, "yyyy")+");',color:'#e9cb88 !important  ',title: ' Book Here! ',start: new Date("+uF.getDateFormat(newEventDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(newEventDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(newEventDate, DBDATE, "dd")+")}");
				sDate = uF.getBiweeklyDate(bookingDate, i);
			}
		}
		
	}
	
	private void getMeetingRoomBookingDetails(UtilityFunctions uF, List<String> reportListEmp) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String,List<String>> hmMeetingRoomDetails = new LinkedHashMap<String,List<String>>();
			String[] strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
			String strCalendarYearStart = strCalendarYearDates[0];
			String strCalendarYearEnd = strCalendarYearDates[1];
			
			List<String> roomIdList = new ArrayList<String>();
			StringBuilder sb = new StringBuilder();
			String[] meetingRoomIds = request.getParameterValues("strRoomIdCheck");
			StringBuilder sbRoomIds = null;
			StringBuilder sbRooms = null;
			if(getStrMeetingRoomId() != null && !getStrMeetingRoomId().equals("")){
				if(meetingRoomIds != null && meetingRoomIds.length>0) {
	//				System.out.println("meetingRoomIds==>"+meetingRoomIds.length+"==>mr_id==>"+getStrMeetingRoomId());
					for(String roomId:meetingRoomIds){
						if(roomId != null && !roomId.equals("")){
							if(sbRoomIds == null) {
								sbRoomIds = new StringBuilder();
								sbRoomIds.append(roomId);
							} else {
								sbRoomIds.append(","+roomId);
							}
						}
					}
				}
				
				if(sbRoomIds == null) {
					sbRoomIds = new StringBuilder();
				}
			
				//get deSelected RoomIds
				
			    if(sbRoomIds != null && sbRoomIds.length() > 0) {
					sb.append("select * from meeting_room_details where meeting_room_id not in ("+sbRoomIds+")");
				} else {
					sb.append("select * from meeting_room_details where meeting_room_id >0 ");
				}
				
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sb.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sb.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sb.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
				}
					
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
						 sb.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sb.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sb.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
						
				}
			
				pst = con.prepareStatement(sb.toString());
				rs = pst.executeQuery();
				while(rs.next()) {
					roomIdList.add(rs.getString("meeting_room_id"));
					if(rs.getString("meeting_room_id")!= null && !rs.getString("meeting_room_id").equals("")){
						if(sbRooms == null) {
							sbRooms = new StringBuilder();
							sbRooms.append(rs.getString("meeting_room_id"));
						} else {
							sbRooms.append(","+rs.getString("meeting_room_id"));
						}
					}
					
				}
				rs.close();
				pst.close();
				
				if(sbRooms == null) {
					sbRooms = new StringBuilder();
				}
			}
			
			request.setAttribute("roomIdList", roomIdList);
			
			
			StringBuilder sbQuery = new StringBuilder();
			if(getStrMeetingRoomId() != null && !getStrMeetingRoomId().equals("")) {	
				if(sbRoomIds.toString() != null && !sbRoomIds.toString().equals("")) {
					
					if(strUserType != null && (strUserType.equals(ADMIN) || strUserType.equals(HRMANAGER))){
						sbQuery.append("select * from meeting_room_details md, meeting_room_booking_details mb where md.meeting_room_id = mb.meeting_room_id  and md.meeting_room_id in ("+sbRoomIds.toString()+")  ");
					
					} else 	if(strUserType != null && !strUserType.equals("")) {
						sbQuery.append("select * from meeting_room_details md, meeting_room_booking_details mb where md.meeting_room_id = mb.meeting_room_id  and md.meeting_room_id in ("+sbRoomIds.toString()+") and booked_by in ("+strSessionEmpId+")");
					} 
					
				} else {
				//	sbQuery.append("select * from meeting_room_details md, meeting_room_booking_details mb where md.meeting_room_id = mb.meeting_room_id  and md.meeting_room_id not in ("+sbRooms.toString()+")  ");
					if(strUserType != null && (strUserType.equals(ADMIN) || strUserType.equals(HRMANAGER))){
						sbQuery.append("select * from meeting_room_details md, meeting_room_booking_details mb where md.meeting_room_id = mb.meeting_room_id  and md.meeting_room_id not in ("+sbRooms.toString()+")  ");
					
					} else 	if(strUserType != null && !strUserType.equals("")) {
						sbQuery.append("select * from meeting_room_details md, meeting_room_booking_details mb where md.meeting_room_id = mb.meeting_room_id  and md.meeting_room_id not in ("+sbRooms.toString()+") and booked_by in ("+strSessionEmpId+") ");
					} 
				}
			} else {
				
				if(strUserType != null && (strUserType.equals(ADMIN) || strUserType.equals(HRMANAGER))){
					sbQuery.append("select * from meeting_room_details md, meeting_room_booking_details mb where md.meeting_room_id = mb.meeting_room_id");
				
				} else 	if(strUserType != null && !strUserType.equals("")) {
					sbQuery.append("select * from meeting_room_details md, meeting_room_booking_details mb where md.meeting_room_id = mb.meeting_room_id  and booked_by in ("+strSessionEmpId+") ");
				} 
			}	
			
			  if(strUserType != null && strUserType.equals(HRMANAGER)) {
					
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
				}
						
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
						sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
						
				}
				
				sbQuery.append(" and ((booking_from between '"+ uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, DBDATE) + "' )"
						+" or (booking_to between '"+ uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, DBDATE) + "' ))");
				sbQuery.append(" order by request_date");
				
			
			
			
			pst = con.prepareStatement(sbQuery.toString());
//	        System.out.println("pst==>"+pst);
	     	rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("booking_from")!=null && !rs.getString("booking_from").equals("") && rs.getString("booking_to")!=null && !rs.getString("booking_to").equals("")){
					String diffInDays = uF.dateDifference(uF.getDateFormat(rs.getString("booking_from"), DBDATE, DATE_FORMAT), DATE_FORMAT,uF.getDateFormat(rs.getString("booking_to"), DBDATE, DATE_FORMAT),DATE_FORMAT);
//					System.out.println("diffInDays==>"+diffInDays);
					if(uF.parseToInt(diffInDays)>1){
						java.sql.Date eDate = rs.getDate("booking_from");
						String bookingDate = uF.getDateFormat(rs.getString("booking_from"), DBDATE, DATE_FORMAT);
						for(int i =1;i<=(uF.parseToInt(diffInDays));i++){
							String newEventDate = uF.getDateFormatUtil(eDate, DBDATE) ;
							reportListEmp.add("{url:'javascript:openRoomBookingPopup("+rs.getString("booking_id")+");',color:' "+uF.showData(rs.getString("room_color_code"), "yellow")+"  ',title: '"+rs.getString("meeting_room_name").toUpperCase()+",Booked by "+hmEmpName.get(rs.getString("booked_by"))+". See Details...!"+"',start: new Date("+uF.getDateFormat(newEventDate, DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(newEventDate, DBDATE, "M"))-1)+", "+uF.getDateFormat(newEventDate, DBDATE, "dd")+")}");
							eDate = uF.getBiweeklyDate(bookingDate, i);
						}
					}else{
						reportListEmp.add("{url:'javascript:openRoomBookingPopup("+rs.getString("booking_id")+");',color:'  "+uF.showData(rs.getString("room_color_code"), "yellow")+" ',title: '"+rs.getString("meeting_room_name").toUpperCase()+",Booked by "+hmEmpName.get(rs.getString("booked_by"))+". See Details...!"+"',start: new Date("+uF.getDateFormat(rs.getString("booking_from"), DBDATE, "yyyy")+", "+(uF.parseToInt(uF.getDateFormat(rs.getString("booking_from"), DBDATE, "M"))-1)+", "+uF.getDateFormat(rs.getString("booking_from"), DBDATE, "dd")+")}");
					}
				}
			}
			rs.close();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	private void getMeetingRoomDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String,List<String>> hmMeetingRoomDetails = new LinkedHashMap<String,List<String>>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from meeting_room_details where meeting_room_id > 0 ");
			if(strUserType != null && strUserType.equals(HRMANAGER)) {
				 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
					 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				 } else {
					 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
				 }
			} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
			}
				
			if(strUserType != null && strUserType.equals(HRMANAGER)) {
				 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
					 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				 } else {
					 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
				 }
			} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					
			}
			
			sbQuery.append(" order by meeting_room_name ");
			
	        pst = con.prepareStatement(sbQuery.toString());
//	        System.out.println("pst==>"+pst);
	     	rs = pst.executeQuery();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
			
				alInner.add(rs.getString("meeting_room_id"));//0
				alInner.add(uF.showData(rs.getString("meeting_room_name"), "-"));//1
				alInner.add(uF.showData(rs.getString("room_color_code"), "-"));//2
				hmMeetingRoomDetails.put(rs.getString("meeting_room_id"), alInner);
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmMeetingRoomDetails", hmMeetingRoomDetails);
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
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

	public String getStrMeetingRoomId() {
		return strMeetingRoomId;
	}

	public void setStrMeetingRoomId(String strMeetingRoomId) {
		this.strMeetingRoomId = strMeetingRoomId;
	}

	public String getStrRoomIdCheck() {
		return strRoomIdCheck;
	}

	public void setStrRoomIdCheck(String strRoomIdCheck) {
		this.strRoomIdCheck = strRoomIdCheck;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}

}
