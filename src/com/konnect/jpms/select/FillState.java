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

public class FillState implements IStatements{
	String stateId;
	String stateName;

	public String getStateId() {
		return stateId;
	}

	public void setStateId(String stateId) {
		this.stateId = stateId;
	}

	public String getStateName() {
		return stateName;
	}

	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	public FillState(String stateId, String stateName) {
		this.stateId = stateId;
		this.stateName = stateName;
	}
	HttpServletRequest request;
	public FillState(HttpServletRequest request) {
		this.request = request;
	}
	public FillState() {
	}
	
	public List<FillState> fillState(){
		List<FillState> al = new ArrayList<FillState>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectState);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillState(rs1.getString("state_id"), rs1.getString("state_name")));
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
	
	public List<FillState> fillState(String strCountry){
		List<FillState> al = new ArrayList<FillState>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM state where country_id=? order by state_name");
			pst.setInt(1, uF.parseToInt(strCountry));
//			System.out.println("pst==>"+pst);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillState(rs1.getString("state_id"), rs1.getString("state_name")));
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
	
	public List<FillState> fillWLocationStates(){
		List<FillState> al = new ArrayList<FillState>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectState_WLocation);			
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillState(rs1.getString("state_id"), rs1.getString("state_name")));
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

	public List<FillState> fillWLocationStatesByOrgId(int orgId) {
		List<FillState> al = new ArrayList<FillState>();		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("SELECT distinct(s.state_name), s.state_id FROM work_location_info wi, " +
					"state s where wi.wlocation_state_id = s.state_id and wi.org_id=? order by s.state_name");
			pst.setInt(1, orgId);
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillState(rs.getString("state_id"), rs.getString("state_name")));
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
}