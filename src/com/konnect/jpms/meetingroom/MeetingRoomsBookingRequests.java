package com.konnect.jpms.meetingroom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
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

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillMeetingRooms;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MeetingRoomsBookingRequests extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	public CommonFunctions CF; 
	
	
	private String f_org;
	private String[] f_wlocation;
	
	private List<FillOrganisation> orgList;
	private List<FillWLocation> wLocationList;
	
	private String strSearchJob;
	private static Logger log = Logger.getLogger(MeetingRoomsBookingRequests.class);
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, "/jsp/meetingroom/MeetingRoomsBookingRequests.jsp");
		request.setAttribute(TITLE, "Meeting Room Booking");
		
		loadData(uF);
		getSelectedFilter(uF);
		getSearchAutoCompleteData(uF);
		getMeetingRoomsBookingRequests(uF);
	
		return SUCCESS;
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
			Map<String,List<String>> hmMeetingRoomsBookingReqDetails = new LinkedHashMap<String,List<String>>();
			
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
		
			sbQuery.append(" order by meeting_room_name ");
			
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
				alInner.add(uF.showData(rs.getString("food_service_details"),""));//11
				alInner.add(uF.showData(rs.getString("food_service_location"),""));//12
				
				/*String strGuests = rs.getString("guests");
				String[] guests = null;
				StringBuilder sbGuests = null;
				if(strGuests!=null && !strGuests.equals("")){
					guests = rs.getString("guests").split(",");
					System.out.println("level's=>"+guests.length);
					for(String guest : guests) {
						if(sbGuests == null) {
								sbGuests = new StringBuilder();
								sbGuests.append(guest);
						} else {
							sbGuests.append(", "+guest);
						}
					}
					if(sbGuests == null) {
						sbGuests = new StringBuilder();
					}
					
				}
				
				alInner.add(uF.showData(sbGuests.toString(),""));//13*/	
				
				
				hmMeetingRoomsBookingReqDetails.put(rs.getString("booking_id"), alInner);
				
			}
			rs.close();
			pst.close();

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
	
	private void loadData(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
				orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
				if(uF.parseToInt(getF_org()) == 0  && orgList!=null && orgList.size()>0) {
					setF_org(orgList.get(0).getOrgId());
				}
				wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			} else {
				 orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORGID));
				 if(uF.parseToInt(getF_org()) == 0) {
					setF_org((String) session.getAttribute(ORGID));
				}
				 wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
			 }
		} else if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation();
			if(uF.parseToInt(getF_org()) == 0  && orgList!=null && orgList.size()>0) {
				setF_org(orgList.get(0).getOrgId());
			}
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		} else {
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORGID));
			if(uF.parseToInt(getF_org()) == 0) {
				setF_org((String) session.getAttribute(ORGID));
			}
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
		}
	}
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=orgList.get(i).getOrgName();
					} else {
						strOrg+=", "+orgList.get(i).getOrgName();
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

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String getStrSearchJob() {
		return strSearchJob;
	}

	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}
}
