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

public class FillLevel implements IStatements{

	String levelId;
	String levelCodeName;
	
	public FillLevel(String levelId, String levelCodeName) {
		this.levelId = levelId;
		this.levelCodeName = levelCodeName;
	}
	HttpServletRequest request;
	public FillLevel(HttpServletRequest request) {
		this.request = request;
	}
	public FillLevel() {
	}
	
	public List<FillLevel> fillLevel(){
		
		List<FillLevel> al = new ArrayList<FillLevel>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLevel);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillLevel(rs.getString("level_id"), "["+rs.getString("level_code")+"] "+rs.getString("level_name")));				
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
	
	
	public List<FillLevel> fillLevel(int nOrgId){
		
		List<FillLevel> al = new ArrayList<FillLevel>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			if(nOrgId>0){
				pst = con.prepareStatement(selectLevel1);
				pst.setInt(1, nOrgId);
			}else{
				pst = con.prepareStatement(selectLevel);
			}
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillLevel(rs.getString("level_id"), "["+rs.getString("level_code")+"] "+rs.getString("level_name")));				
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
	
	
	public List<FillLevel> fillLevelBYORG(int nOrgId) {
		
		List<FillLevel> al = new ArrayList<FillLevel>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLevel1);
			pst.setInt(1, nOrgId);
			
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillLevel(rs.getString("level_id"), "["+rs.getString("level_code")+"] "+rs.getString("level_name")));				
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

	
public List<FillLevel> fillLevelOrgIdAndLevelIds(String orgIds, String levelIds){
		
		List<FillLevel> al = new ArrayList<FillLevel>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if(orgIds != null && !orgIds.equals("") && levelIds != null && !levelIds.equals("")){
				pst = con.prepareStatement("SELECT * FROM level_details where org_id in("+orgIds+") and level_id in("+levelIds+") order by level_name");
			} 
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillLevel(rs.getString("level_id"), "["+rs.getString("level_code")+"] "+rs.getString("level_name")));				
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


	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public String getLevelCodeName() {
		return levelCodeName;
	}

	public void setLevelCodeName(String levelCodeName) {
		this.levelCodeName = levelCodeName;
	}
	public List<FillLevel> fillLevelForLeavePolicy(int nOrgId, String leaveTypeId, String strWLocationId) {
		
		List<FillLevel> al = new ArrayList<FillLevel>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM level_details where org_id=? and level_id not in (select level_id " +
					"from emp_leave_type where org_id=? and leave_type_id=? and wlocation_id=?)   order by level_name");
			pst.setInt(1, nOrgId);
			pst.setInt(2, nOrgId);
			pst.setInt(3, uF.parseToInt(leaveTypeId));
			pst.setInt(4, uF.parseToInt(strWLocationId));
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillLevel(rs.getString("level_id"), "["+rs.getString("level_code")+"] "+rs.getString("level_name")));				
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
	public List<FillLevel> fillLevelWithoutCurrentLevel(int nEmpId) {
		
		List<FillLevel> al = new ArrayList<FillLevel>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from level_details where org_id in(select org_id from employee_personal_details epd, " +
					"employee_official_details eod where epd.emp_per_id=eod.emp_id and epd.emp_per_id=?) and level_id not in (select level_id " +
					"from designation_details where designation_id in (select designation_id from grades_details where grade_id in (select grade_id " +
					"from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id and epd.emp_per_id=?))) order by level_name");
			pst.setInt(1, nEmpId);
			pst.setInt(2, nEmpId);
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillLevel(rs.getString("level_id"), "["+rs.getString("level_code")+"] "+rs.getString("level_name")));				
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
