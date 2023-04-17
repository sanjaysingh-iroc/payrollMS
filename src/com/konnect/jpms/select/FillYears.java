package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;

public class FillYears implements IConstants{

	
	private int yearsID;
	private String yearsName;
	
	private FillYears(int yearsID, String yearsName) {
		this.yearsID = yearsID;
		this.yearsName = yearsName;
	}
	
	HttpServletRequest request;
	public FillYears(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillYears() {
	}
	
	public List<FillYears> fillYears(Date currentDate){
		
		List<FillYears> al = new ArrayList<FillYears>();
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			int currentYear = uF.parseToInt(uF.getDateFormat(currentDate.toString(), DBDATE, "yyyy"));
			
			for(int i=currentYear; i > currentYear-50; i--) {
				al.add(new FillYears(i, i+""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		return al;
	}
	
	public List<FillYears> fillFutureYears(Date currentDate, int nFutureYears, int nPreeviousYears){
		
		List<FillYears> al = new ArrayList<FillYears>();
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
			int currentYear = uF.parseToInt(uF.getDateFormat(currentDate.toString(), DBDATE, "yyyy"));
			for(int i=currentYear+ nFutureYears; i > currentYear-nPreeviousYears; i--) {
				al.add(new FillYears(i, i+""));
			}
			
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	public List<FillYears> fillYears(Date currentDate,String dob){
		
		List<FillYears> al = new ArrayList<FillYears>();
		UtilityFunctions uF = new UtilityFunctions();
		
		
		try {
			
			int currentYear = uF.parseToInt(uF.getDateFormat(currentDate.toString(), DBDATE, "yyyy"));
			
			for(int i=currentYear; i > uF.parseToInt(dob); i--) {
				
				al.add(new FillYears(i, i+""));
				
			}
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return al;
	}
	
	
	
	public List<FillYears> fillYearsFromStartToCurrent() {
		
		List<FillYears> al = new ArrayList<FillYears>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			String strtCalYear = null;
			String endCalYear = null;
			pst = con.prepareStatement("select * FROM calendar_year_details order by calendar_year_to limit 1");
			rs = pst.executeQuery();
			while (rs.next()) {
				strtCalYear = rs.getString("calendar_year_from");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * FROM calendar_year_details order by calendar_year_to desc limit 1");
			rs = pst.executeQuery();
			while (rs.next()) {
				endCalYear = rs.getString("calendar_year_to");
			}
			rs.close();
			pst.close();

			int currentYear = uF.parseToInt(uF.getDateFormat(endCalYear, DBDATE, "yyyy"));
			int startCalYear = uF.parseToInt(uF.getDateFormat(strtCalYear, DBDATE, "yyyy"));
			
			for(int i=currentYear; i >= startCalYear; i--) {
				al.add(new FillYears(i, i+""));
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	
	public List<FillYears> fillYearsFromStartToNext10Year() {
		
		List<FillYears> al = new ArrayList<FillYears>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			String strtCalYear = null;
			String endCalYear = null;
			pst = con.prepareStatement("select * FROM calendar_year_details order by calendar_year_to limit 1");
			rs = pst.executeQuery();
			while (rs.next()) {
				strtCalYear = rs.getString("calendar_year_from");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * FROM calendar_year_details order by calendar_year_to desc limit 1");
			rs = pst.executeQuery();
			while (rs.next()) {
				endCalYear = rs.getString("calendar_year_to");
			}
			rs.close();
			pst.close();

			int currentYear = uF.parseToInt(uF.getDateFormat(endCalYear, DBDATE, "yyyy"));
			int startCalYear = uF.parseToInt(uF.getDateFormat(strtCalYear, DBDATE, "yyyy"));
			
			for(int i=startCalYear; i <=(currentYear+10); i++) {
				al.add(new FillYears(i, i+""));
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillYears> fillYearsFromCurrentToNext10Year() {
		
		List<FillYears> al = new ArrayList<FillYears>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			String endCalYear = null;
			
			pst = con.prepareStatement("select * FROM calendar_year_details order by calendar_year_to desc limit 1");
			rs = pst.executeQuery();
			while (rs.next()) {
				endCalYear = rs.getString("calendar_year_to");
			}
			rs.close();
			pst.close();

			int currentYear = uF.parseToInt(uF.getDateFormat(endCalYear, DBDATE, "yyyy"));
			
			for(int i=currentYear; i <=(currentYear+10); i++) {
				al.add(new FillYears(i, i+""));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillYears> fillYearType() {
		
		List<FillYears> al = new ArrayList<FillYears>();
		try {
			al.add(new FillYears(1, "Calendar Year"));
			al.add(new FillYears(2, "Financial Year"));
			
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return al;
	}
	
	
	public int getYearsID() {
		return yearsID;
	}

	public void setYearsID(int yearsID) {
		this.yearsID = yearsID;
	}

	public String getYearsName() {
		return yearsName;
	}

	public void setYearsName(String yearsName) {
		this.yearsName = yearsName;
	}


}  
