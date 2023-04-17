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

public class FillWLocation implements IStatements{

	private String wLocationId;
	private String wLocationName;
	
	public FillWLocation(){}
	public FillWLocation(String wLocationId, String wLocationName){
		this.wLocationId = wLocationId;
		this.wLocationName = wLocationName;
	}
	
	public FillWLocation(HttpServletRequest request,String wLocationId, String wLocationName){
		this.wLocationId = wLocationId;
		this.wLocationName = wLocationName;
	}
	
	
	public List<FillWLocation> fillWLocation(){
		
		List<FillWLocation> al = new ArrayList<FillWLocation>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectWLocation);
			rs = pst.executeQuery();			
			while(rs.next()){
				al.add(new FillWLocation(rs.getString("wlocation_id"), "["+rs.getString("wloacation_code")+"] "+rs.getString("wlocation_name")+","+rs.getString("wlocation_city")));				
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
	
	public List<FillWLocation> fillWorkLocationName(String strOrgId) {
		
		List<FillWLocation> al = new ArrayList<FillWLocation>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			
			if(uF.parseToInt(strOrgId)>0){
				pst = con.prepareStatement(selectWLocation1);
				pst.setInt(1, uF.parseToInt(strOrgId));
			}else{
				pst = con.prepareStatement(selectWLocation);
			}
//			System.out.println("pst location===>> " + pst);			 
			rs = pst.executeQuery();			
			while(rs.next()){
				al.add(new FillWLocation(rs.getString("wlocation_id"), rs.getString("wlocation_name")));				
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
	
	
	public List<FillWLocation> fillWLocation(String strOrgId){
		
		List<FillWLocation> al = new ArrayList<FillWLocation>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			
			if(uF.parseToInt(strOrgId)>0){
				pst = con.prepareStatement(selectWLocation1);
				pst.setInt(1, uF.parseToInt(strOrgId));
			}else{
				pst = con.prepareStatement(selectWLocation);
			}
//			System.out.println("pst location===>> " + pst);			 
			rs = pst.executeQuery();			
			while(rs.next()){
				al.add(new FillWLocation(rs.getString("wlocation_id"), "["+rs.getString("wloacation_code")+"] "+rs.getString("wlocation_name")+","+rs.getString("wlocation_city")));				
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


	//************************************
	public List<FillWLocation> fillWLocationState(String strOrgId,String strStateId){
		
		List<FillWLocation> al = new ArrayList<FillWLocation>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			
			if(uF.parseToInt(strOrgId)>0 && uF.parseToInt(strStateId)>0){
				pst = con.prepareStatement(selectWLocationState);
				pst.setInt(1, uF.parseToInt(strOrgId));
				pst.setInt(2, uF.parseToInt(strStateId));
			}else{
				pst = con.prepareStatement(selectWLocation);
			}
//			System.out.println("pst location===>> " + pst);			 
			rs = pst.executeQuery();			
			while(rs.next()){
				al.add(new FillWLocation(rs.getString("wlocation_id"), "["+rs.getString("wloacation_code")+"] "+rs.getString("wlocation_name")+","+rs.getString("wlocation_city")));				
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

	//*************************************
	
	public List<FillWLocation> fillWLocation(String strOrgId, String strWLocation){
		
		List<FillWLocation> al = new ArrayList<FillWLocation>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			con = db.makeConnection(con);
			
			if(uF.parseToInt(strOrgId)>0) {
				
				if(strWLocation!=null && !strWLocation.trim().equals("")) {
					pst = con.prepareStatement("SELECT * FROM work_location_info where org_id = ? and wlocation_id in ("+strWLocation+") order by wlocation_name");
					pst.setInt(1, uF.parseToInt(strOrgId));
				} else {
					pst = con.prepareStatement(selectWLocation1);
					pst.setInt(1, uF.parseToInt(strOrgId));
				}
			} else {
				if(strWLocation!=null && !strWLocation.trim().equals("")) {
					pst = con.prepareStatement("SELECT * FROM work_location_info where wlocation_id in ("+strWLocation+") order by wlocation_name");
				} else {
					pst = con.prepareStatement(selectWLocation);
				}
				
			}
			rs = pst.executeQuery();			
			while(rs.next()){
				al.add(new FillWLocation(rs.getString("wlocation_id"), "["+rs.getString("wloacation_code")+"] "+rs.getString("wlocation_name")+","+rs.getString("wlocation_city")));				
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
	
	
	public List<FillWLocation> fillWLocationOrgIdAndWLocationIds(String orgIds, String wlocIds) {
		
		List<FillWLocation> al = new ArrayList<FillWLocation>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			if(orgIds != null && !orgIds.trim().equals("") && wlocIds != null && !wlocIds.trim().equals("")) {
				pst = con.prepareStatement("SELECT * FROM work_location_info where org_id in ("+orgIds+") and wlocation_id in ("+wlocIds+") order by wlocation_name,org_id");
			} else if(orgIds != null && !orgIds.trim().equals("")) {
				pst = con.prepareStatement("SELECT * FROM work_location_info where org_id in ("+orgIds+") order by wlocation_name,org_id");
			} else {
				pst = con.prepareStatement(selectWLocation);
			}
			rs = pst.executeQuery();
//			System.out.println("pst==>"+pst);
			while(rs.next()) {
				al.add(new FillWLocation(rs.getString("wlocation_id"), "["+rs.getString("wloacation_code")+"] "+rs.getString("wlocation_name")+","+rs.getString("wlocation_city")));				
			}	
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return al;
	}
	public List<FillWLocation> fillWLocationWithoutCurrentLocation(int nEmpId) {
		
		List<FillWLocation> al = new ArrayList<FillWLocation>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from work_location_info where org_id in (select org_id from employee_personal_details epd, " +
					"employee_official_details eod where epd.emp_per_id=eod.emp_id and epd.emp_per_id=?) and wlocation_id not in (select wlocation_id " +
					"from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id and epd.emp_per_id=?) order by wlocation_name");
			pst.setInt(1, nEmpId);
			pst.setInt(2, nEmpId);
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				al.add(new FillWLocation(rs.getString("wlocation_id"), "["+rs.getString("wloacation_code")+"] "+rs.getString("wlocation_name")+","+rs.getString("wlocation_city")));				
			}	
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return al;
	}
	
	
	public List<FillWLocation> fillClientLocation(){
		
		List<FillWLocation> al = new ArrayList<FillWLocation>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from client_locations");
			rs = pst.executeQuery();			
			while(rs.next()){
				al.add(new FillWLocation(rs.getString("client_loc_id"), rs.getString("client_loc_name")));				
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
	
	public String getwLocationId() {
		return wLocationId;
	}
	public void setwLocationId(String wLocationId) {
		this.wLocationId = wLocationId;
	}
	public String getwLocationName() {
		return wLocationName;
	}
	public void setwLocationName(String wLocationName) {
		this.wLocationName = wLocationName;
	} 
	
	HttpServletRequest request;
	public FillWLocation(HttpServletRequest request) {
		this.request = request;
	}
}
