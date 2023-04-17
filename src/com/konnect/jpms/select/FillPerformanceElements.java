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

public class FillPerformanceElements implements IStatements{

	
	String elementId;
	String elementName;
	
	private FillPerformanceElements(String elementId, String elementName) {
		this.elementId = elementId;
		this.elementName = elementName;
	}
	HttpServletRequest request;
	public FillPerformanceElements(HttpServletRequest request) {
		this.request = request;
	}
	public FillPerformanceElements() {
	}
	 
	public List<FillPerformanceElements> fillPerformanceElements() {
		
		List<FillPerformanceElements> al = new ArrayList<FillPerformanceElements>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_element order by appraisal_element_name");
			rs = pst.executeQuery();
			
			while(rs.next()){
				al.add(new FillPerformanceElements(rs.getString("appraisal_element_id"), rs.getString("appraisal_element_name")));				
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
	public String getElementId() {
		return elementId;
	}

	public void setElementId(String elementId) {
		this.elementId = elementId;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}


}  
