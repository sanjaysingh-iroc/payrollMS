package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillMeetingRooms implements IStatements {
String meetingRoomId;
String meetingRoomName;

public FillMeetingRooms(String meetingRoomId, String meetingRoomName) {
	this.meetingRoomId = meetingRoomId;
	this.meetingRoomName = meetingRoomName;
}

public FillMeetingRooms() {
	
}

HttpServletRequest request;
public FillMeetingRooms(HttpServletRequest request) {
	this.request = request;
}



public List<FillMeetingRooms> fillMeetingRooms(String strUserType, String wLocation){
	 
	List<FillMeetingRooms> al = new ArrayList<FillMeetingRooms>();
	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	try {
		con = db.makeConnection(con);
		StringBuilder sbQuery = new StringBuilder();
		sbQuery.append("select * from meeting_room_details where meeting_room_id > 0");
		if(strUserType!=null && !strUserType.equals(ADMIN) && wLocation != null && !wLocation.equals("")) {
			sbQuery.append(" and wlocation_id in ("+wLocation+")");
		} 
		
		sbQuery.append(" order by meeting_room_name");
		pst = con.prepareStatement(sbQuery.toString());
		
		rs = pst.executeQuery();
		while(rs.next()){
			al.add(new FillMeetingRooms(rs.getString("meeting_room_id"), rs.getString("meeting_room_name")));				
		}		
		rs.close();
		pst.close();
		
	}catch (Exception e) {
		e.printStackTrace();
	}finally{
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	return al;
}

public String getMeetingRoomId() {
	return meetingRoomId;
}

public void setMeetingRoomId(String meetingRoomId) {
	this.meetingRoomId = meetingRoomId;
}

public String getMeetingRoomName() {
	return meetingRoomName;
}

public void setMeetingRoomName(String meetingRoomName) {
	this.meetingRoomName = meetingRoomName;
}


}
