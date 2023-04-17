package com.konnect.jpms.meetingroom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

public class GetBookingDetailsPopup extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	public CommonFunctions CF; 
	
	private String bookingId;
	private static Logger log = Logger.getLogger(GetBookingDetailsPopup.class);
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, "/jsp/meetingroom/GetBookingDetailsPopup.jsp");
		request.setAttribute(TITLE, "Meeting room booking details");
		
		getMeetingRoomBookingDetails(uF);
	   
		return SUCCESS;
	}
	
	private void getMeetingRoomBookingDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmWLocation = CF.getWLocationMap(con,null,request,null); 
			List<String> alInner = new ArrayList<String>();
			Map<String, String> hmDishNames = CF.getDishNamesMap(con);
			
			//Map<String, String> hmDishTypes = CF.getDishTypes();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from meeting_room_details md, meeting_room_booking_details mb where md.meeting_room_id = mb.meeting_room_id  and mb.booking_id =? ");
			pst = con.prepareStatement(sbQuery.toString());
	        pst.setInt(1, uF.parseToInt(getBookingId()));
	     	rs = pst.executeQuery();
			while(rs.next()) {
								
				alInner.add(uF.showData(rs.getString("booking_id"), "-"));//0
				alInner.add(rs.getString("meeting_room_id"));//1
				alInner.add(uF.showData(rs.getString("meeting_room_name"), "-"));//2
				alInner.add(uF.showData(rs.getString("room_color_code"), "-"));//3
				alInner.add(uF.showData(hmEmpName.get(rs.getString("booked_by")), "-"));//4
				alInner.add(uF.getDateFormat(rs.getString("booking_from"),DBDATE,DATE_FORMAT));//5
				alInner.add(uF.getDateFormat(rs.getString("booking_to"),DBDATE,DATE_FORMAT));//6
				
				String from_time = rs.getString("from_time");
				String to_time = rs.getString("to_time");
				
				if(from_time != null && !from_time.equals("")){
					
					alInner.add(uF.getTimeFormatStr(from_time.substring(0,from_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));//7
				}else{
					alInner.add("");//7
				}
				if(to_time != null  && !to_time.equals("")){
					
					alInner.add(uF.getTimeFormatStr(to_time.substring(0,to_time.lastIndexOf(":")),DBTIME,TIME_FORMAT_AM_PM));//8
				}else{
					alInner.add("");//8
				}
				
				alInner.add(uF.getDateFormat(rs.getString("request_date"),DBDATE,DATE_FORMAT));//9
				alInner.add(uF.showData(rs.getString("no_of_people"),""));//10
				alInner.add(uF.showData(hmDishNames.get(rs.getString("food_service_details")),"Not provided"));//11
				alInner.add(hmWLocation.get(rs.getString("meeting_room_location")));//12
				
				String strGuests = rs.getString("guests");
		
				String[] guests = null;
				StringBuilder sbGuests = null;
				if(strGuests!=null && !strGuests.equals("")){
					guests = strGuests.split(",");
					
					for(String guest : guests) {
						if(guest==null || guest.trim().equals("") || guest.trim().equalsIgnoreCase("")){
							continue;
						}
						
						if(sbGuests == null) {
								sbGuests = new StringBuilder();
								sbGuests.append(guest);
						} else {
							sbGuests.append(","+guest);
						}
					}
				}
				
				if(sbGuests == null) {
					sbGuests = new StringBuilder();
				}
				
		
				alInner.add(uF.showData(sbGuests.toString(),""));//13
				alInner.add(rs.getString("request_status"));//14
				alInner.add(rs.getString("booked_by"));//15
				alInner.add(uF.showData(uF.getDateFormat(rs.getString("approved_date"),DBDATE,DATE_FORMAT),""));//16
				
				String strParticipants = rs.getString("participants");
				String[] empIds = null;
				StringBuilder sbParticipants = null;
				if(strParticipants!=null && !strParticipants.equals("")){
					empIds = strParticipants.split(",");
					for(String empId : empIds) {
						
						if(empId==null || empId.trim().equals("") || empId.trim().equalsIgnoreCase("")){
							continue;
						}
						if(uF.parseToInt(empId)>0) {
							if(sbParticipants == null) {
								sbParticipants = new StringBuilder();
								sbParticipants.append(hmEmpName.get(empId));
							} else {
								sbParticipants.append(", "+hmEmpName.get(empId));
							}
						}
					}
				}
				
				if(sbParticipants == null) {
					sbParticipants = new StringBuilder();
				}
				
				if(sbGuests != null && sbGuests.length()>0) {
					alInner.add(uF.showData(sbParticipants.toString()+",",""));//17
				} else {
					alInner.add(uF.showData(sbParticipants.toString()+".",""));//17
				}
				
				int requestStatus = rs.getInt("request_status");
				if(requestStatus == 0) {
					alInner.add("Waiting for approval");//18
				} else if(requestStatus == 1) {
					alInner.add("Approved");//18
				} else if(requestStatus == -1) {
					alInner.add("Denied");//18
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("bookingDetails",alInner);
			
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

	public String getBookingId() {
		return bookingId;
	}

	public void setBookingId(String bookingId) {
		this.bookingId = bookingId;
	}
	
	
}
