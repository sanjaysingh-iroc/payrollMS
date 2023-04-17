package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillPerspective implements IStatements{

	String perspectiveId;
	String perspectiveName;
	
	public FillPerspective(String perspectiveId, String perspectiveName) {
		this.perspectiveId = perspectiveId;
		this.perspectiveName = perspectiveName;
	}
	
	HttpServletRequest request;
	public FillPerspective(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillPerspective() {
	}
	
	public List<FillPerspective> fillPerspective() {
		
		List<FillPerspective> al = new ArrayList<FillPerspective>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM bsc_perspective_details order by bsc_perspective_id");
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillPerspective(rs.getString("bsc_perspective_id"), rs.getString("bsc_perspective_name")));				
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
	
	public String getPerspectiveId() {
		return perspectiveId;
	}
	
	public void setPerspectiveId(String perspectiveId) {
		this.perspectiveId = perspectiveId;
	}
	
	public String getPerspectiveName() {
		return perspectiveName;
	}
	
	public void setPerspectiveName(String perspectiveName) {
		this.perspectiveName = perspectiveName;
	}
	
}  
