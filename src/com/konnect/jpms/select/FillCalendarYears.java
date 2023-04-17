package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillCalendarYears implements IStatements{

	String calendarYearId;
	String calendarYearName;
	
	public FillCalendarYears(String calendarYearId, String calendarYearName) {
		this.calendarYearId = calendarYearId;
		this.calendarYearName = calendarYearName;
	
	}  
	HttpServletRequest request;
	public FillCalendarYears(HttpServletRequest request) {
		this.request = request;
	}
		
	public FillCalendarYears() {
	}
	
	public List<FillCalendarYears> fillCalendarYears(CommonFunctions CF){
		
		List<FillCalendarYears> al = new ArrayList<FillCalendarYears>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			String strReportDateFormat = DATE_FORMAT_STR;
			if(CF.getStrReportDateFormat()!=null) {
				strReportDateFormat = CF.getStrReportDateFormat();
			}
			pst = con.prepareStatement("select * FROM calendar_year_details order by calendar_year_to desc");
			rs = pst.executeQuery();
//			System.out.println("CF ===>>>> " + CF + " -- strReportDateFormat ===>> " + strReportDateFormat);
			while (rs.next()) {
				al.add(new FillCalendarYears(uF.getDateFormat(rs.getString("calendar_year_from"), DBDATE, DATE_FORMAT)+"-"+uF.getDateFormat(rs.getString("calendar_year_to"), DBDATE, DATE_FORMAT), uF.getDateFormat(rs.getString("calendar_year_from"), DBDATE, strReportDateFormat) +" - "+ uF.getDateFormat(rs.getString("calendar_year_to"), DBDATE, strReportDateFormat)));
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
		return al;
	}

	public String getCalendarYearId() {
		return calendarYearId;
	}

	public void setCalendarYearId(String calendarYearId) {
		this.calendarYearId = calendarYearId;
	}

	public String getCalendarYearName() {
		return calendarYearName;
	}

	public void setCalendarYearName(String calendarYearName) {
		this.calendarYearName = calendarYearName;
	}

	public String[] fillLatestCalendarYears() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String []arr = new String[2];

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * FROM calendar_year_details order by calendar_year_to desc limit 1");
			rs = pst.executeQuery();
			if (rs.next()) {
				arr[0] = uF.getDateFormat(rs.getString("calendar_year_from"), DBDATE, DATE_FORMAT);
				arr[1] = uF.getDateFormat(rs.getString("calendar_year_to"), DBDATE, DATE_FORMAT);
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
		return arr;
	}
	
}
