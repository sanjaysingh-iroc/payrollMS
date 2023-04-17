package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;

public class FillRequirementEmploymentType {

	String employmentId;
	String employmentName;
	
	public FillRequirementEmploymentType(String employmentId, String employmentName) {
		this.employmentId = employmentId;
		this.employmentName = employmentName;
	}
	
	HttpServletRequest request;
	public FillRequirementEmploymentType(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillRequirementEmploymentType() {
	}
	 
	public List<FillRequirementEmploymentType> fillRequirementEmploymentType(){
		List<FillRequirementEmploymentType> al = new ArrayList<FillRequirementEmploymentType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
//			pst = con.prepareStatement("select * from skills_details order by skill_name");
			pst = con.prepareStatement("select employment_type_id,employment_type from requirement_employment_type order by employment_type_id");
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillRequirementEmploymentType(rs1.getString("employment_type_id"), rs1.getString("employment_type")));
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

	public String getEmploymentId() {
		return employmentId;
	}
	public void setEmploymentId(String employmentId) {
		this.employmentId = employmentId;
	}
	public String getEmploymentName() {
		return employmentName;
	}
	public void setEmploymentName(String employmentName) {
		this.employmentName = employmentName;
	}
	

}
