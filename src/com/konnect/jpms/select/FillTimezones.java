package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillTimezones implements IStatements{
	String timezoneName;
	String timezoneId;

	
	public FillTimezones(String timezoneId, String timezoneName) {
		this.timezoneName = timezoneName;
		this.timezoneId = timezoneId;
	}
	HttpServletRequest request;
	public FillTimezones(HttpServletRequest request) {
		this.request = request;
	}
	public FillTimezones() {
	}
	
	public List<FillTimezones> fillTimezones(){
		List<FillTimezones> al = new ArrayList<FillTimezones>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectTimezone);
			rs = pst.executeQuery();			
			while(rs.next()){
				al.add(new FillTimezones(rs.getString("timezone_id"), rs.getString("timezone_region") + ((rs.getString("timezone_country1")!=null && rs.getString("timezone_country1").length()>1)?"/"+rs.getString("timezone_country1"):"") + ((rs.getString("timezone_country2")!=null && rs.getString("timezone_country2").length()>1)?"/"+rs.getString("timezone_country2"):"") ));				
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
		return al;
	}
	public String getTimezoneName() {
		return timezoneName;
	}
	public void setTimezoneName(String timezoneName) {
		this.timezoneName = timezoneName;
	}
	public String getTimezoneId() {
		return timezoneId;
	}
	public void setTimezoneId(String timezoneId) {
		this.timezoneId = timezoneId;
	}	
	
}
