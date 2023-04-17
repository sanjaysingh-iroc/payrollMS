package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;

public class FillRosterWeeklyOff {
	String typeId;
	String typeName;
	
	public FillRosterWeeklyOff(String typeId, String typeName) {
		super();
		this.typeId = typeId;
		this.typeName = typeName;
	}
	
	HttpServletRequest request;
	public FillRosterWeeklyOff(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillRosterWeeklyOff() {
	}

	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public String getTypeName() {
		return typeName;
	}
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public List<FillRosterWeeklyOff> fillRosterWOff() {
		
		List<FillRosterWeeklyOff> al = new ArrayList<FillRosterWeeklyOff>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request); 
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from roster_weeklyoff_policy order by roster_weeklyoff_id");
			rs1 = pst.executeQuery();
			while (rs1.next()) { 
				al.add(new FillRosterWeeklyOff(rs1.getString("roster_weeklyoff_id"), rs1.getString("weeklyoff_name")));
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
	
public List<FillRosterWeeklyOff> fillRosterWOffByOrg(int nOrgId) {
		
		List<FillRosterWeeklyOff> al = new ArrayList<FillRosterWeeklyOff>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request); 
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from roster_weeklyoff_policy where roster_weeklyoff_id = 1 or (org_id=? and org_id>0) order by roster_weeklyoff_id");
			pst.setInt(1, nOrgId);
//			System.out.println("pst==>"+pst);
			rs1 = pst.executeQuery();
			while (rs1.next()) { 
				al.add(new FillRosterWeeklyOff(rs1.getString("roster_weeklyoff_id"), rs1.getString("weeklyoff_name")));
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
