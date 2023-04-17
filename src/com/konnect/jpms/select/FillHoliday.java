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

public class FillHoliday implements IStatements {

	String holidayId;
	String holidayDate;
	
	public FillHoliday(String holidayId, String holidayDate) {
		this.holidayId = holidayId;
		this.holidayDate = holidayDate;
	}
	HttpServletRequest request;
	public FillHoliday(HttpServletRequest request) {
		this.request = request;
	}

	public String getHolidayId() {
		return holidayId;
	}
	public void setHolidayId(String holidayId) {
		this.holidayId = holidayId;
	}
	public String getHolidayDate() {
		return holidayDate;
	}
	public void setHolidayDate(String holidayDate) {
		this.holidayDate = holidayDate;
	}

	public List<FillHoliday> fillOptionalHolidays(String strOrgId,String strWlocationId,String strFromDate, String strToDate) {

		List<FillHoliday> al = new ArrayList<FillHoliday>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from holidays where is_optional_holiday=true and org_id=? and wlocation_id=?" +
					" and _date between ? and ? order by _date");
			pst.setInt(1, uF.parseToInt(strOrgId));
			pst.setInt(2, uF.parseToInt(strWlocationId));
			pst.setDate(3, uF.getDateFormat(strFromDate, DATE_FORMAT));		
			pst.setDate(4, uF.getDateFormat(strToDate, DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillHoliday(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)+" ["+uF.showData(rs.getString("description"), "")+"]"));
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

}
