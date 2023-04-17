package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillInfrastructureType implements IStatements {

	String strInfraTypeId;
	String strInfraTypeName;
	
	private FillInfrastructureType(String strInfraTypeId, String strInfraTypeName) {
		this.strInfraTypeId = strInfraTypeId;
		this.strInfraTypeName = strInfraTypeName;
	}
	
	public FillInfrastructureType() {
	}
	
	HttpServletRequest request;
	public FillInfrastructureType(HttpServletRequest request) {
		this.request = request;
	}
	
//	public List<FillInfrastructureType> fillInfrastructureType(){
//		List<FillInfrastructureType> al = new ArrayList<FillInfrastructureType>();
//	
//		try {
//
//			al.add(new FillInfrastructureType("Room", "Room"));
//			al.add(new FillInfrastructureType("Laptop", "Laptop"));
//			al.add(new FillInfrastructureType("Projector", "Projector"));
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return al;
//	}
	
	public List<FillInfrastructureType> fillInfrastructureType(){
		List<FillInfrastructureType> al = new ArrayList<FillInfrastructureType>();
	
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from infrastructure_type order by infra_type_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillInfrastructureType(rs.getString("infra_type_id"), rs.getString("infra_type")));
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public String getStrInfraTypeId() {
		return strInfraTypeId;
	}

	public void setStrInfraTypeId(String strInfraTypeId) {
		this.strInfraTypeId = strInfraTypeId;
	}

	public String getStrInfraTypeName() {
		return strInfraTypeName;
	}

	public void setStrInfraTypeName(String strInfraTypeName) {
		this.strInfraTypeName = strInfraTypeName;
	}

	
}  
