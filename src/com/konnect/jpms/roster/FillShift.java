package com.konnect.jpms.roster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillShift implements IStatements{
	
	String shiftCode;
	String shiftId;
	
	private FillShift(String shiftCode, String shiftId) {
		this.shiftCode = shiftCode;
		this.shiftId = shiftId;
	}
	
	public FillShift() {
	}
	
	HttpServletRequest request;
	public FillShift(HttpServletRequest request) {
		this.request = request;
	}
	
	public List<FillShift> fillShift(){
		List<FillShift> al = new ArrayList<FillShift>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM shift_details order by shift_id");
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillShift(rs.getString("shift_code"), rs.getString("shift_id")));				
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
	
	
	public List<FillShift> fillShiftNames(int nOrgId) {
		List<FillShift> al = new ArrayList<FillShift>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM shift_details where org_id=? and org_id>0 order by shift_name");
			pst.setInt(1, nOrgId);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillShift(rs.getString("shift_name"), rs.getString("shift_id")));				
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
	
	
	public List<FillShift> fillShiftByOrg(int nOrgId){
		List<FillShift> al = new ArrayList<FillShift>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from shift_details where shift_id = 1 or (org_id=? and org_id>0) order by shift_id");
			pst.setInt(1, nOrgId);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillShift(rs.getString("shift_code"), rs.getString("shift_id")));				
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

	public String getShiftCode() {
		return shiftCode;
	}

	public void setShiftCode(String shiftCode) {
		this.shiftCode = shiftCode;
	}

	public String getShiftId() {
		return shiftId;
	}

	public void setShiftId(String shiftId) {
		this.shiftId = shiftId;
	}
	
	

}
