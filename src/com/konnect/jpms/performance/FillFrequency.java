package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillFrequency implements IStatements{

	private String id;
	private String name;
	
	private FillFrequency(String levelId, String levelCodeName) {
		this.id = levelId;
		this.name = levelCodeName;
	}
	
	HttpServletRequest request;
	public FillFrequency(HttpServletRequest request) {
		this.request = request; 
	}
//	public FillFrequency() {
//	}
	
	public List<FillFrequency> fillFrequency(){
		
		List<FillFrequency> al = new ArrayList<FillFrequency>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			
//			pst = con.prepareStatement("select * from appraisal_frequency order by appraisal_frequency_id");
			pst = con.prepareStatement("select * from appraisal_frequency where appraisal_frequency_id !=2 order by appraisal_frequency_id");
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillFrequency(rs.getString("appraisal_frequency_id"), rs.getString("frequency_name")));				
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

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	

}  
