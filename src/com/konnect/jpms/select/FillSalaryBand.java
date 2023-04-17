package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;

public class FillSalaryBand { 
	
	String salaryBandId;
	String salaryBandName;

	public FillSalaryBand(String salaryBandId, String salaryBandName) {
		this.salaryBandId = salaryBandId;
		this.salaryBandName = salaryBandName;
	}
	
	HttpServletRequest request;
	public FillSalaryBand(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillSalaryBand() {}
	 
	
	public List<FillSalaryBand> fillSalaryBands(String strLevelId) {
		
		List<FillSalaryBand> al = new ArrayList<FillSalaryBand>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			if(uF.parseToInt(strLevelId)<=0) {
				pst = con.prepareStatement("select * from salary_band_details");
			} else {
				pst = con.prepareStatement("select * from salary_band_details where level_id=?");
				pst.setInt(1, uF.parseToInt(strLevelId));
			}
			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) { 
				al.add(new FillSalaryBand(rs.getString("salary_band_id"), rs.getString("salary_band_name")));
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

	public String getSalaryBandId() {
		return salaryBandId;
	}

	public void setSalaryBandId(String salaryBandId) {
		this.salaryBandId = salaryBandId;
	}

	public String getSalaryBandName() {
		return salaryBandName;
	}

	public void setSalaryBandName(String salaryBandName) {
		this.salaryBandName = salaryBandName;
	}
	
	
}
