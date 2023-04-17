package com.konnect.jpms.meetingroom;

import java.io.File;
import java.lang.reflect.Array;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;

import com.konnect.jpms.util.IStatements;

import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddMeetingRoom extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strSessionOrgId;
	String strSessionEmpId;
	String strUserType;
	public CommonFunctions CF; 
	
	private String f_org;      
	private String location;
	
	
	private String strMeetingRoomName;
	private String strRoom_Length;
	private String strRoom_Width;
	private String strSeating_Capacity;
	private String strRoom_color_code; 
	

	
	private List<FillOrganisation> orgList;
	private List<FillWLocation> wLocationList;
	private List<FillYears> yearsList;
	
	
    String meetingRoomId;
	String operation;
	
	String strSubmit;
	String strUpdate;
	private static Logger log = Logger.getLogger(AddMeetingRoom.class);
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;

		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		request.setAttribute(PAGE, "/jsp/meetingroom/AddMeetingRoom.jsp");
		request.setAttribute(TITLE, "ADD MEETING ROOM");
		
		if(getF_org() == null || getF_org().equals("")) {
			setF_org((String)session.getAttribute(ORGID));
			
		}
		
		loadEmployee(uF);
		
		if(getOperation()!=null && getOperation().equals("D")) {
			deleteMeetingRoom(uF);
		}else if(getStrUpdate()==null && getOperation()!=null && getOperation().equals("E")) {
			getMeetingRoomDetails(uF);
		}

		
		if(getStrUpdate()!=null) {
			updateMeetingRoomDetails(uF);
			return LOAD;
		}
		
		if(getStrSubmit()!=null) {
			insertMeetingRoomDetails(uF);
			return LOAD;
		}
		
		return SUCCESS;
	}
	
	private void updateMeetingRoomDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("update meeting_room_details set meeting_room_name = ?, room_length = ? , room_width = ?, seating_capacity = ?, room_color_code = ?,"
				+"  org_id = ?, wlocation_id = ?,update_by = ?, updated_date = ?  where meeting_room_id = ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setString(1, getStrMeetingRoomName());
			pst.setString(2, getStrRoom_Length());
			pst.setString(3,getStrRoom_Width());
			pst.setInt(4, uF.parseToInt(getStrSeating_Capacity()));
			pst.setString(5, getStrRoom_color_code());
			pst.setInt(6,uF.parseToInt(getF_org()));
			pst.setInt(7, uF.parseToInt(getLocation()));
			pst.setInt(8, uF.parseToInt(strSessionEmpId));
			pst.setDate(9,uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(10,uF.parseToInt(getMeetingRoomId()));
			pst.executeUpdate();
			pst.close();
			
		} catch (Exception e) {
				e.printStackTrace();
		} finally {
			
				db.closeStatements(pst);
				db.closeConnection(con);
		}
		
	}
	
	private String loadEmployee(UtilityFunctions uF) {
		if(strUserType!=null && strUserType.equalsIgnoreCase(HRMANAGER)) {
			 if(session.getAttribute(WLOCATION_ACCESS) != null && !session.getAttribute(WLOCATION_ACCESS).equals("")) {
				wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
				orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			 } else {
				 wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
				 orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORGID));
			 }
		} else if(strUserType!=null && strUserType.equalsIgnoreCase(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation();
				wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		} else {
			 wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATIONID));
			 orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORGID));
		}
		
		return LOAD;
	}
	
	private void insertMeetingRoomDetails(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("insert into meeting_room_details(meeting_room_name,room_length,room_width,seating_capacity,room_color_code,org_id,wlocation_id, added_by, entry_date)"
					+" values(?,?,?,?,?,?,?,?,?)");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setString(1, getStrMeetingRoomName());
			pst.setString(2, getStrRoom_Length());
			pst.setString(3,getStrRoom_Width());
			pst.setInt(4, uF.parseToInt(getStrSeating_Capacity()));
			pst.setString(5, getStrRoom_color_code());
			pst.setInt(6,uF.parseToInt(getF_org()));
			pst.setInt(7, uF.parseToInt(getLocation()));
			pst.setInt(8, uF.parseToInt(strSessionEmpId));
			pst.setDate(9,uF.getCurrentDate(CF.getStrTimeZone()));
			pst.executeUpdate();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
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
		try{
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from meeting_room_details where meeting_room_id = ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(getMeetingRoomId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setStrMeetingRoomName(uF.showData(rs.getString("meeting_room_name"), ""));
				setStrRoom_Length(uF.showData(rs.getString("room_length"), ""));
				setStrRoom_Width(uF.showData(rs.getString("room_width"), ""));
				setStrSeating_Capacity(uF.showData(rs.getString("seating_capacity"), ""));
				setStrRoom_color_code(uF.showData(rs.getString("room_color_code"), ""));
				setF_org(uF.showData(rs.getString("org_id"), ""));
				setLocation(uF.showData(rs.getString("wlocation_id"), ""));
			
			}
			rs.close();
			pst.close();
			
						
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void deleteMeetingRoom(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("delete from meeting_room_details where meeting_room_id=?");
					
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1,uF.parseToInt(getMeetingRoomId()));
			pst.execute();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public void clearForm(UtilityFunctions uF){
		setStrMeetingRoomName("");
		setStrRoom_Length("");
		setStrRoom_Width("");
		setStrRoom_color_code("");
		setStrSeating_Capacity("");
		setF_org("");
		setLocation("");
	
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(uF.parseToInt(getF_org()) == 0  && orgList!=null && orgList.size()>0){
				setF_org(orgList.get(0).getOrgId());
			}
		}else{
			if(uF.parseToInt(getF_org()) == 0){
				setF_org((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}
		
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		
		yearsList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()));
	
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

	

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
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

	public List<FillYears> getYearsList() {
		return yearsList;
	}

	public void setYearsList(List<FillYears> yearsList) {
		this.yearsList = yearsList;
	}

	
	public String getMeetingRoomId() {
		return meetingRoomId;
	}

	public void setMeetingRoomId(String meetingRoomId) {
		this.meetingRoomId = meetingRoomId;
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

	public String getStrUpdate() {
		return strUpdate;
	}

	public void setStrUpdate(String strUpdate) {
		this.strUpdate = strUpdate;
	}

	public String getStrMeetingRoomName() {
		return strMeetingRoomName;
	}

	public void setStrMeetingRoomName(String strMeetingRoomName) {
		this.strMeetingRoomName = strMeetingRoomName;
	}

	public String getStrRoom_Length() {
		return strRoom_Length;
	}

	public void setStrRoom_Length(String strRoom_Length) {
		this.strRoom_Length = strRoom_Length;
	}

	public String getStrRoom_Width() {
		return strRoom_Width;
	}

	public void setStrRoom_Width(String strRoom_Width) {
		this.strRoom_Width = strRoom_Width;
	}

	public String getStrSeating_Capacity() {
		return strSeating_Capacity;
	}

	public void setStrSeating_Capacity(String strSeating_Capacity) {
		this.strSeating_Capacity = strSeating_Capacity;
	}

	public String getStrRoom_color_code() {
		return strRoom_color_code;
	}

	public void setStrRoom_color_code(String strRoom_color_code) {
		this.strRoom_color_code = strRoom_color_code;
	}
	
}
