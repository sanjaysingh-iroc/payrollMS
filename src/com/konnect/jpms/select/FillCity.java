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

public class FillCity implements IStatements{

	String cityId;
	String cityName;
	
	public String getCityId() {
		return cityId;
	}
	public void setCityId(String cityId) {
		this.cityId = cityId;
	}
	public String getCityName() {
		return cityName;
	}
	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	public FillCity(String cityId, String cityName) {
		this.cityId = cityId;
		this.cityName = cityName;
	}
	
	HttpServletRequest request;
	public FillCity(HttpServletRequest request) {
		this.request = request;
	}
	public FillCity() {
	}
	
	public List<FillCity> fillCity(){
		List<FillCity> al = new ArrayList<FillCity>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectCity);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillCity(rs1.getString("city_id"), rs1.getString("city_name")));
			}
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	public List<FillCity> fillCity(String stateId){
		List<FillCity> al = new ArrayList<FillCity>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectCity_stateId);
			pst.setInt(1, uF.parseToInt(stateId));
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillCity(rs1.getString("city_id"), rs1.getString("city_name")));
			}
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
}
