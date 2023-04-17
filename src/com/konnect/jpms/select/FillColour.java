package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillColour implements IStatements{

	
	String colourId;
	String colourName;
	String colourValue;
	
	private FillColour(String colourValue, String colourName) {
		this.colourValue = colourValue;
		this.colourName = colourName;
	}
	HttpServletRequest request;
	public FillColour(HttpServletRequest request) {
		this.request = request;
	}
	public FillColour() {
	}
	
	public List<FillColour> fillColour(){
		
		List<FillColour> al = new ArrayList<FillColour>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectColourCode);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillColour(rs.getString("colour_value"), rs.getString("colour_name")));				
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

	public String getColourId() {
		return colourId;
	}

	public void setColourId(String colourId) {
		this.colourId = colourId;
	}

	public String getColourName() {
		return colourName;
	}

	public void setColourName(String colourName) {
		this.colourName = colourName;
	}

	public String getColourValue() {
		return colourValue;
	}

	public void setColourValue(String colourValue) {
		this.colourValue = colourValue;
	}
}  
