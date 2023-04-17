package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.UtilityFunctions;

public class FillRound {

	String roundId;
	String roundName;
	
	public FillRound(String roundId, String roundName) {
		this.roundId = roundId;
		this.roundName = roundName;
	}
	HttpServletRequest request;
	public FillRound(HttpServletRequest request) {
		this.request = request;
	}
	public FillRound() {
	}
	
	public List<FillRound> fillRound(String recruitID){
		 
		List<FillRound> al = new ArrayList<FillRound>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions(); 

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT distinct(round_id) FROM panel_interview_details where recruitment_id = ? order by round_id");
			pst.setInt(1, uF.parseToInt(recruitID));
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillRound(rs.getString("round_id"), "Round "+rs.getString("round_id")));				
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

	public String getRoundId() {
		return roundId;
	}

	public void setRoundId(String roundId) {
		this.roundId = roundId;
	}

	public String getRoundName() {
		return roundName;
	}

	public void setRoundName(String roundName) {
		this.roundName = roundName;
	}


}
