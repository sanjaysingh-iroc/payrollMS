package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillCountry implements IStatements{
	String countryId;
	String countryName;

	public String getCountryId() {
		return countryId;
	}

	public void setCountryId(String countryId) {
		this.countryId = countryId;
	}

	public String getCountryName() {
		return countryName;
	} 

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public FillCountry(String countryId, String countryName) {
		this.countryId = countryId;
		this.countryName = countryName;
	}
	HttpServletRequest request;
	public FillCountry(HttpServletRequest request) {
		this.request = request;
	}
	public FillCountry() {
	}
	
	public List<FillCountry> fillCountry(){
		List<FillCountry> al = new ArrayList<FillCountry>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectCountry);
			rs = pst.executeQuery();			
			while(rs.next()){
				al.add(new FillCountry(rs.getString("country_id"), rs.getString("country_name")));				
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
	
	
	public List<FillCountry> fillWorkLocationCountry(String strWLocation){
		List<FillCountry> al = new ArrayList<FillCountry>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(c.country_id) as country_id, c.country_name from country c, work_location_info wli where wlocation_country_id = country_id ");
			if(strWLocation != null && !strWLocation.equals("")) {
				sbQuery.append(" and wlocation_id in ("+strWLocation+") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			
			rs = pst.executeQuery();			
			while(rs.next()){
				al.add(new FillCountry(rs.getString("country_id"), rs.getString("country_name")));				
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
	
	
}
