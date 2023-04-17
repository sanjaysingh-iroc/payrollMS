package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillRecruitmentTechnology implements IStatements{

	String technologyId;
	String technologyName;
	
	
	public FillRecruitmentTechnology(String technologyId, String technologyName) {
		this.technologyId = technologyId;
		this.technologyName = technologyName;
	}
	HttpServletRequest request;
	public FillRecruitmentTechnology(HttpServletRequest request) {
		this.request = request;
	}
	public FillRecruitmentTechnology() {}
	
	
public List<FillRecruitmentTechnology> fillRecruitmentTechnologies(){
		
		List<FillRecruitmentTechnology> al = new ArrayList<FillRecruitmentTechnology>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM recruitment_technology ORDER BY recruitment_technology_id");
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillRecruitmentTechnology(rs.getString("recruitment_technology_id"), rs.getString("technology_name")));				
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
	
	public String getTechnologyId() {
		return technologyId;
	}
	public void setTechnologyId(String technologyId) {
		this.technologyId = technologyId;
	}
	public String getTechnologyName() {
		return technologyName;
	}
	public void setTechnologyName(String technologyName) {
		this.technologyName = technologyName;
	}

}