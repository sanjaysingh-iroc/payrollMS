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

public class FillUserType implements IStatements {

	
	String userTypeId;
	String userTypeName;
	
	
	public String getUserTypeId() {
		return userTypeId;
	}
	public void setUserTypeId(String userTypeId) {
		this.userTypeId = userTypeId;
	}
	public String getUserTypeName() {
		return userTypeName;
	}
	public void setUserTypeName(String userTypeName) {
		this.userTypeName = userTypeName;
	} 
	
	public FillUserType(String userTypeId, String userTypeName) {
		this.userTypeId = userTypeId;
		this.userTypeName = userTypeName;
	}
	HttpServletRequest request;
	public FillUserType(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillUserType() {
	}
	
	public List<FillUserType> fillUserType(String userType) {
		List<FillUserType> al = new ArrayList<FillUserType>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode  = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectUserType);
			rsEmpCode = pst.executeQuery();
			while(rsEmpCode.next()) { 
				if(userType != null && !userType.equals(ADMIN) && rsEmpCode.getString("user_type").equals(ADMIN)) {
					continue;
				}
				al.add(new FillUserType(rsEmpCode.getString("user_type_id"), rsEmpCode.getString("user_type")));				
			}		
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return al;
	}	
	
	
	public List<FillUserType> fillUserType() {
		List<FillUserType> al = new ArrayList<FillUserType>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectUserType);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillUserType(rs1.getString("user_type_id"), rs1.getString("user_type")));
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
