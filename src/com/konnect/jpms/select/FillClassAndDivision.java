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

public class FillClassAndDivision implements IStatements{

	String classDivId;
	String classDivName;
	
	
	public FillClassAndDivision(String classDivId, String classDivName) {
		this.classDivId = classDivId;
		this.classDivName = classDivName;
	}
	
	HttpServletRequest request;
	public FillClassAndDivision(HttpServletRequest request) {
		this.request = request;
	}
	public FillClassAndDivision() { }
	
	
	public List<FillClassAndDivision> fillClass(String orgId) {
		List<FillClassAndDivision> al = new ArrayList<FillClassAndDivision>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from class_details where org_id=?");
			pst.setInt(1, uF.parseToInt(orgId));
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillClassAndDivision(rs1.getString("class_id"), rs1.getString("class_name")));
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
	
	
	
	public List<FillClassAndDivision> fillDivision(String classId) {
		List<FillClassAndDivision> al = new ArrayList<FillClassAndDivision>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from division_details where class_id=?");
			pst.setInt(1, uF.parseToInt(classId));
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillClassAndDivision(rs1.getString("division_id"), rs1.getString("division_name")));
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
	
	
	public String getClassDivId() {
		return classDivId;
	}
	
	public void setClassDivId(String classDivId) {
		this.classDivId = classDivId;
	}
	
	public String getClassDivName() {
		return classDivName;
	}

	public void setClassDivName(String classDivName) {
		this.classDivName = classDivName;
	}
	
}
