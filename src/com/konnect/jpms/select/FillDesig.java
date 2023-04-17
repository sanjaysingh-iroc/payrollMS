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

public class FillDesig implements IStatements{

	
	String desigId;
	String desigCodeName;
	
	public FillDesig(String desigId, String desigCodeName) {
		this.desigId = desigId;
		this.desigCodeName = desigCodeName;
	}
	HttpServletRequest request;
	public FillDesig(HttpServletRequest request) {
		this.request = request;
	}
	public FillDesig() {
	}
	 
	
	public List<FillDesig> fillDesig(){
		
		List<FillDesig> al = new ArrayList<FillDesig>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectDesig);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillDesig(rs.getString("designation_id"), "["+rs.getString("designation_code")+"] "+rs.getString("designation_name")));				
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
	
	
public List<FillDesig> fillDesig(int nOrgId) {
		
		List<FillDesig> al = new ArrayList<FillDesig>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try { 

			con = db.makeConnection(con);
			if(nOrgId>0) {
				pst = con.prepareStatement(selectDesigOrgId);
				pst.setInt(1, nOrgId);
			} else {
				pst = con.prepareStatement(selectDesig);
			}
//			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			while(rs.next()) {
				al.add(new FillDesig(rs.getString("designation_id"), "["+rs.getString("designation_code")+"] "+rs.getString("designation_name")));				
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


	public List<FillDesig> fillDesigByOrgOrAccessOrg(int nOrgId, String strOrgId) {
		
		List<FillDesig> al = new ArrayList<FillDesig>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try { 

			con = db.makeConnection(con);
			if(nOrgId>0) {
				pst = con.prepareStatement(selectDesigOrgId);
				pst.setInt(1, nOrgId);
			} else if(strOrgId != null && strOrgId.trim().length()>0) {
				pst = con.prepareStatement("SELECT * FROM designation_details ald INNER JOIN level_details ld ON ald.level_id = ld.level_id " +
					"where org_id in ("+strOrgId+") order by designation_name");
			} else {
				pst = con.prepareStatement(selectDesig);
			}
//			System.out.println("pst==>"+pst);
			
			rs = pst.executeQuery();
			while(rs.next()) {
				al.add(new FillDesig(rs.getString("designation_id"), "["+rs.getString("designation_code")+"] "+rs.getString("designation_name")));				
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

	
	public List<FillDesig> fillDesigFromLevel(String levelId) {
		
		List<FillDesig> al = new ArrayList<FillDesig>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			if(levelId != null && !levelId.equals("")) {
//				pst = con.prepareStatement(selectDesigFromLevel);
				pst = con.prepareStatement("SELECT * FROM designation_details WHERE level_id in (" + levelId + ") order by designation_name");
//				pst.setInt(1, uF.parseToInt(levelId));
			} else {
				pst = con.prepareStatement("SELECT * FROM designation_details order by designation_name");
			}
			rs = pst.executeQuery();
//			System.out.println("pst selectDesigFromLevel==>"+pst);
			 
			while(rs.next()) {
				al.add(new FillDesig(rs.getString("designation_id"), "["+rs.getString("designation_code")+"] "+rs.getString("designation_name")));				
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
	
	
	public List<FillDesig> fillClientDesig(){
		
		List<FillDesig> al = new ArrayList<FillDesig>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from client_designations");
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillDesig(rs.getString("client_desig_id"), rs.getString("client_desig_name")));				
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


//	public List<FillDesig> fillDesigFromMultipleLevel(String levelId) {
//		
//		List<FillDesig> al = new ArrayList<FillDesig>();
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//		
//		try {
//
//			con = db.makeConnection(con);
//			pst = con.prepareStatement("SELECT * FROM designation_details WHERE level_id in("+levelId+") order by designation_name");
////			pst.setInt(1, uF.parseToInt(levelId));
//			rs = pst.executeQuery();
////			System.out.println("pst selectDesigFromLevel==>"+pst);
//			
//			while(rs.next()){
//				al.add(new FillDesig(rs.getString("designation_id"), "["+rs.getString("designation_code")+"] "+rs.getString("designation_name")));				
//			}
//			rs.close();
//			pst.close();
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			
//		}finally{
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		
//		return al;
//	}
	
	public String getDesigId() {
		return desigId;
	}

	public void setDesignId(String designId) {
		this.desigId = designId;
	}

	public String getDesigCodeName() {
		return desigCodeName;
	}

	public void setDesigCodeName(String desigCodeName) {
		this.desigCodeName = desigCodeName;
	}

	public List<FillDesig> fillDesigFromLevelForPromotion(String empId,String levelId) {
		
		List<FillDesig> al = new ArrayList<FillDesig>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM designation_details WHERE level_id in (?) and  designation_id not in "
					+" (select designation_id from grades_details where grade_id in(select grade_id from employee_official_details "
					+ " where emp_id =?)) order by designation_name ");
			pst.setInt(1, uF.parseToInt(levelId));
			pst.setInt(2, uF.parseToInt(empId));
			rs = pst.executeQuery();
//			System.out.println("pst fillDesigFromLevelForPromotion==>"+pst);
			 
			while(rs.next()) {
				al.add(new FillDesig(rs.getString("designation_id"), "["+rs.getString("designation_code")+"] "+rs.getString("designation_name")));				
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