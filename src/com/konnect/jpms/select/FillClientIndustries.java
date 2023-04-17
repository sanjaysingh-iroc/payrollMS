package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillClientIndustries implements IStatements{

	String industryId;
	String industryName; 
	
	
	
	public FillClientIndustries(String industryId, String industryName) {
		this.industryId = industryId;
		this.industryName = industryName;
	}
	HttpServletRequest request;
	public FillClientIndustries(HttpServletRequest request) {
		this.request = request;
	}
	public FillClientIndustries() {
	}
	
	public List<FillClientIndustries> fillClientIndustries(){
		List<FillClientIndustries> al = new ArrayList<FillClientIndustries>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectIndustry);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillClientIndustries(rs1.getString("industry_id"), rs1.getString("industry_name")));
			}
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public String getIndustryId() {
		return industryId;
	}

	public void setIndustryId(String industryId) {
		this.industryId = industryId;
	}

	public String getIndustryName() {
		return industryName;
	}

	public void setIndustryName(String industryName) {
		this.industryName = industryName;
	}
	

	
}