package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillWlocationType implements IStatements{

	String wlocationTypeId;
	String wlocationTypeCodeName;
	
	private FillWlocationType(String wlocationTypeId, String wlocationTypeCodeName) {
		this.wlocationTypeId = wlocationTypeId;
		this.wlocationTypeCodeName = wlocationTypeCodeName;
	}
	HttpServletRequest request;
	public FillWlocationType(HttpServletRequest request) {
		this.request = request;
	}
	public FillWlocationType() {
	}
	
	public List<FillWlocationType> fillWlocationType(){
		 
		List<FillWlocationType> al = new ArrayList<FillWlocationType>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectWlocationType);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillWlocationType(rs.getString("wlocation_type_id"), "["+rs.getString("wlocation_type_code")+"] "+rs.getString("wlocation_type_name")));				
			}		
			rs.close();
			pst.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}

	public String getWlocationTypeId() {
		return wlocationTypeId;
	}

	public void setWlocationTypeId(String wlocationTypeId) {
		this.wlocationTypeId = wlocationTypeId;
	}

	public String getWlocationTypeCodeName() {
		return wlocationTypeCodeName;
	}

	public void setWlocationTypeCodeName(String wlocationTypeCodeName) {
		this.wlocationTypeCodeName = wlocationTypeCodeName;
	}


}  
