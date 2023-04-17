package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillDesignation implements IStatements{

	String desigId;
	String desigName;
	
	public String getDesigId() { 
		return desigId;
	}

	public void setDesigId(String desigId) {
		this.desigId = desigId;
	}

	public String getDesigName() {
		return desigName;
	}

	public void setDesigName(String desigName) {
		this.desigName = desigName;
	}

	
	
	
	public FillDesignation(String desigId, String desigName) {
		this.desigId = desigId;
		this.desigName = desigName;
	}
	HttpServletRequest request;
	public FillDesignation(HttpServletRequest request) {
		this.request = request;
	}
	public FillDesignation() {
	}
	
	public List<FillDesignation> fillDesignation(){
		
		List<FillDesignation> al = new ArrayList<FillDesignation>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database(); 
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectDesignation);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillDesignation(rs1.getString("desig_id"), rs1.getString("desig_name")));
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
