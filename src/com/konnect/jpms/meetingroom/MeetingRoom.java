package com.konnect.jpms.meetingroom;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;


import com.konnect.jpms.cafeteria.ViewCafeteria;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MeetingRoom extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	
	private List<FillWLocation> wLocationList;
	private List<FillOrganisation> organisationList;
	public CommonFunctions CF;
	
	private String f_org;
	private String  strLocation;
	

	private String[] f_wlocation;
    
	private String strSearchJob;
	
	private String dataType;
	
	private String alertID;
	
	private static Logger log = Logger.getLogger(MeetingRoom.class);
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-wrench\"></i><a href=\"Library.action\" style=\"color: #3c8dbc;\"> Utility</a></li>" +
			"<li class=\"active\">Meeting Rooms</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		UtilityFunctions uF = new UtilityFunctions();
		if(getF_org() == null || getF_org().equals("")){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		request.setAttribute(PAGE, "/jsp/meetingroom/MeetingRoom.jsp");
		request.setAttribute(TITLE, "Meeting Rooms");
		
		String strDomain = request.getServerName().split("\\.")[0];
		CF.deleteWRUserAlerts(CF, request, strDomain, getAlertID());
		
//		if(strUserType != null && strUserType.equals(ADMIN)) {
//			updateUserAlerts(uF, MEETING_ROOM_BOOKING_REQUEST_ALERT);
//		} 
		
		if (getStrLocation() != null && !getStrLocation().equals("") && !getStrLocation().equalsIgnoreCase("null")) {
			setF_wlocation((getStrLocation().split(",")));
		}
		
		loadEmployee(uF);
		getSelectedFilter(uF);
		getSearchAutoCompleteData(uF);
		getBookingRequestCount(uF);
		System.out.println("==>"+getDataType());
		if(getDataType() == null || getDataType().equals("M")) {
			getMeetingRoomDetails(uF);
		}
		
		if(getDataType() == null || getDataType().equals("MBR")) {
			getMeetingRoomsBookingRequests(uF);
		}
		
	
		return SUCCESS;
	}
	
	
	private String getBookingRequestCount(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			int bookingReqCnt = 0;
			StringBuilder sQuery = new StringBuilder();
			sQuery.append("select count(booking_id) as booking_req_count from meeting_room_details md, meeting_room_booking_details mb where md.meeting_room_id= mb.meeting_room_id and request_status = 0 ");
			
			if(getF_org()!=null && !getF_org().equals("")) {
				sQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					
				}
				
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
						 sQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					
				}
			}
			
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
				sQuery.append(" and (upper(meeting_room_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
		
			pst = con.prepareStatement(sQuery.toString());
			
			System.out.println("pst===>"+pst);
	     	rs = pst.executeQuery();
			while(rs.next()) {
				bookingReqCnt = rs.getInt("booking_req_count");
			}
			rs.close();
			pst.close();
			
			request.setAttribute("bookingReqCnt",""+bookingReqCnt);
		
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
	private void updateUserAlerts(UtilityFunctions uF, String alertType) {
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		int nEmpId = uF.parseToInt((String)session.getAttribute(EMPID));
		try {
			con = db.makeConnection(con);
			String strDomain = request.getServerName().split("\\.")[0];
			UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
			userAlerts.setStrDomain(strDomain);
			userAlerts.setStrEmpId(""+nEmpId);
			userAlerts.set_type(alertType);
			userAlerts.setStatus(UPDATE_ALERT);
			Thread t = new Thread(userAlerts);
			t.run();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
		}
	}
	
	private String getMeetingRoomsBookingRequests(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmOrgName = CF.getOrgName(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmWLocationMap = CF.getWLocationMap(con,null,request,null); 
			Map<String, String> hmDishNames = CF.getDishNamesMap(con);
			Map<String,List<String>> hmMeetingRoomsBookingReqDetails = new LinkedHashMap<String,List<String>>();
			
			int bookingReqCnt = 0;
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from meeting_room_details md, meeting_room_booking_details mb where md.meeting_room_id= mb.meeting_room_id and request_status = 0 ");
			
			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					
				}
				
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					
				}
			}
			
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
				sbQuery.append(" and (upper(meeting_room_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
		
			sbQuery.append(" order by request_date desc");
			
	        pst = con.prepareStatement(sbQuery.toString());
	     	rs = pst.executeQuery();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(uF.showData(rs.getString("booking_id"), "-"));//0
				alInner.add(rs.getString("meeting_room_id"));//1
				alInner.add(uF.showData(rs.getString("meeting_room_name"), "-"));//2
				alInner.add(uF.showData(rs.getString("seating_capacity"), "-"));//3
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
				alInner.add(hmWLocationMap.get(rs.getString("meeting_room_location")));//12
				
				hmMeetingRoomsBookingReqDetails.put(rs.getString("booking_id"), alInner);
				bookingReqCnt ++;
			}
			rs.close();
			pst.close();
			
//			request.setAttribute("bookingReqCnt",uF.showData(""+bookingReqCnt,""));
			
			request.setAttribute("hmMeetingRoomsBookingReqDetails", hmMeetingRoomsBookingReqDetails);
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
	
	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			SortedSet<String> setSearchList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("select * from meeting_room_details where meeting_room_id > 0  ");
			
			
			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					
				}
				
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					
				}
			}

			sbQuery.append(" order by meeting_room_name");
			pst = con.prepareStatement(sbQuery.toString());
//		    System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				setSearchList.add(rs.getString("meeting_room_name"));
				
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
			while (it.hasNext()) {
				String strData = it.next();
				if(sbData == null) {
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null) {
				sbData = new StringBuilder();
			}
			request.setAttribute("sbData", sbData.toString());
				
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	
	private Map<String,String> getMeetingRoomBookingCount(Connection con) {
		Map<String,String> hmMeetingRoomBookings = new LinkedHashMap<String,String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("select meeting_room_id, count(booking_id) as booking_count from meeting_room_booking_details where request_status = 1 group by meeting_room_id");
			rs  = pst.executeQuery();
			while(rs.next()) {
				hmMeetingRoomBookings.put(rs.getString("meeting_room_id"), rs.getString("booking_count"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmMeetingRoomBookings==>"+hmMeetingRoomBookings.size());
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			if(rs != null) {
				try {
					rs.close();
				}catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			
			if(pst != null) {
				try {
					pst.close();
				}catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmMeetingRoomBookings;
	}
	private String getMeetingRoomDetails(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmOrgName = CF.getOrgName(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmWLocationMap = CF.getWLocationMap(con,null,request,null); 
			Map<String,List<String>> hmMeetingRoomDetails = new LinkedHashMap<String,List<String>>();
			Map<String,String> hmMeetingRoomBookings = getMeetingRoomBookingCount(con);
			if(hmMeetingRoomBookings == null) hmMeetingRoomBookings = new LinkedHashMap<String,String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from meeting_room_details where meeting_room_id > 0 ");
			
			if(getF_org()!=null && !getF_org().equals("")) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(ORG_ACCESS) != null && !session.getAttribute(ORG_ACCESS).equals("")) {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
					 } else {
						 sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORGID)+")");
					
				}
				
			}
			
			if(getF_wlocation()!=null && !getF_wlocation().equals("")) {
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wlocation(), ",")+")");
			} else {
				if(strUserType != null && strUserType.equals(HRMANAGER)) {
					 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
					 } else {
						 sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					 }
				} else if(strUserType != null && !strUserType.equals(HRMANAGER) && !strUserType.equals(ADMIN)) {
					sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATIONID)+")");
					
				}
			}
			
			if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
				sbQuery.append(" and (upper(meeting_room_name) like '%"+getStrSearchJob().trim().toUpperCase()+"%' )");
			}
		
			sbQuery.append(" order by entry_date desc");
			
	        pst = con.prepareStatement(sbQuery.toString());
	     	rs = pst.executeQuery();
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("meeting_room_id"));//0
				alInner.add(uF.showData(rs.getString("meeting_room_name"), "-"));//1
				alInner.add(uF.showData(rs.getString("room_length"), "-"));//2
				alInner.add(uF.showData(rs.getString("room_width"), "-"));//3
				alInner.add(uF.showData(rs.getString("seating_capacity"), "-"));//4
				alInner.add(uF.showData(rs.getString("room_color_code"), "-"));//5
				alInner.add(uF.showData(hmOrgName.get(rs.getString("org_id")),"-"));//6
				alInner.add(hmWLocationMap.get(rs.getString("wlocation_id")));//7
				alInner.add(hmMeetingRoomBookings.get(rs.getString("meeting_room_id")));//8
				hmMeetingRoomDetails.put(rs.getString("meeting_room_id"), alInner);
				
			}
			rs.close();
			pst.close();

			request.setAttribute("hmMeetingRoomDetails", hmMeetingRoomDetails);
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

	private String loadEmployee(UtilityFunctions uF) {
		if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
			 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
				wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
				organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			 } else {
				 wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
				 organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORGID));
			 }
		} else if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
			    organisationList = new FillOrganisation(request).fillOrganisation();
				wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		} else {
			 wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
			 organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORGID));
		}
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++){
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=organisationList.get(i).getOrgName();
					} else {
						strOrg+=", "+organisationList.get(i).getOrgName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
			
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		alFilter.add("LOCATION");
		if(getF_wlocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_wlocation().length;j++) {
					if(getF_wlocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public HttpSession getSession() {
		return session;
	}

	public void setSession(HttpSession session) {
		this.session = session;
	}

	public String getStrSessionOrgId() {
		return strSessionOrgId;
	}

	public void setStrSessionOrgId(String strSessionOrgId) {
		this.strSessionOrgId = strSessionOrgId;
	}

	public String getStrSessionEmpId() {
		return strSessionEmpId;
	}

	public void setStrSessionEmpId(String strSessionEmpId) {
		this.strSessionEmpId = strSessionEmpId;
	}

	public String getStrUserType() {
		return strUserType;
	}

	public void setStrUserType(String strUserType) {
		this.strUserType = strUserType;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public CommonFunctions getCF() {
		return CF;
	}

	public void setCF(CommonFunctions cF) {
		CF = cF;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_wlocation() {
		return f_wlocation;
	}

	public void setF_wlocation(String[] f_wlocation) {
		this.f_wlocation = f_wlocation;
	}

    public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getAlertID() {
		return alertID;
	}

	public void setAlertID(String alertID) {
		this.alertID = alertID;
	}
	
	
	public String getStrLocation() {
		return strLocation;
	}


	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}
}
