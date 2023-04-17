package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillLeaveType implements IStatements{
	private String leaveTypeId;
	private String leavetypeName;
	
	public String getLeaveTypeId() {
		return leaveTypeId;
	}

	public void setLeaveTypeId(String leaveTypeId) {
		this.leaveTypeId = leaveTypeId;
	}

	public String getLeavetypeName() {
		return leavetypeName;
	}

	public void setLeavetypeName(String leavetypeName) {
		this.leavetypeName = leavetypeName;
	}

	public FillLeaveType(String leaveTypeId, String leavetypeName) {
		this.leaveTypeId = leaveTypeId;
		this.leavetypeName = leavetypeName;
	}
	HttpServletRequest request;
	public FillLeaveType(HttpServletRequest request) {
		this.request = request;
	}
	public FillLeaveType() {
	}
	
	public List<FillLeaveType> fillLeaveEmp(String strEmpId){
		
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLeaveTypeE1);
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
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
	
	public List<FillLeaveType> fillLeave(){
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLeaveTypeF);
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
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
	
	public List<FillLeaveType> fillCompLeave(){
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLeaveTypeF01);
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
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
	
	
	public List<FillLeaveType> fillLeave(int nOrgId){
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectLeaveTypeF1);
			pst.setInt(1, nOrgId);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
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
	
	public List<FillLeaveType> fillLeaveForEncashment(int orgId){
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM leave_type  where leave_type_id>0  and is_leave_encashment = true and org_id=? order by leave_type_name");
			pst.setInt(1,orgId);
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
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
	
	public List<FillLeaveType> fillLeave(int nLevelId, int nEmpId){
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			
			pst = con.prepareStatement("select * from probation_policy where emp_id = ?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			String strAllowedLeaves = null;
			boolean isProbation = false;
			while(rs.next()){
				strAllowedLeaves = rs.getString("leaves_types_allowed");
				isProbation = rs.getBoolean("is_probation");
			}
			rs.close();
			pst.close();
			
			String arr[] = null;
			if(isProbation && strAllowedLeaves!=null && strAllowedLeaves.length()>0){
				arr = strAllowedLeaves.split(",");
			}
			
			
//			pst = con.prepareStatement(selectLeaveTypeL);
			pst = con.prepareStatement("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id and level_id=? and effective_date = (select max(effective_date) from emp_leave_type where level_id = ?)");
			pst.setInt(1, nLevelId);
			pst.setInt(2, nLevelId);
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery(); 
			while (rs.next()) {
				
				if(isProbation && arr!=null && ArrayUtils.contains(arr, rs.getString("leave_type_id"))>=0){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}else if(!isProbation){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}
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
			
	
	public List<FillLeaveType> fillCompLeave(int nLevelId, int nEmpId){
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			
			pst = con.prepareStatement("select * from probation_policy where emp_id = ?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			String strAllowedLeaves = null;
			boolean isProbation = false;
			while(rs.next()){
				strAllowedLeaves = rs.getString("leaves_types_allowed");
				isProbation = rs.getBoolean("is_probation");
			}
			rs.close();
			pst.close();
			
			String arr[] = null;
			if(isProbation && strAllowedLeaves!=null && strAllowedLeaves.length()>0){
				arr = strAllowedLeaves.split(",");
			}
			
			
			
//			pst = con.prepareStatement(selectLeaveTypeL);
			pst = con.prepareStatement("SELECT * FROM leave_type lt, emp_leave_type elt " +
					"where lt.leave_type_id = elt.leave_type_id and level_id=? " +
					"and effective_date = (select max(effective_date) from emp_leave_type " +
					"where level_id = ?) and lt.is_compensatory = true");
			pst.setInt(1, nLevelId);
			pst.setInt(2, nLevelId);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				
				if(isProbation && arr!=null && ArrayUtils.contains(arr, rs.getString("leave_type_id"))>=0){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}else if(!isProbation){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}
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
	
	public List<FillLeaveType> fillLeave(String leaveTypeId){
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst1 = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst1 = con.prepareStatement(selectLeaveTypeV);
			pst1.setInt(1, uF.parseToInt(leaveTypeId));
			rs1 = pst1.executeQuery();
			while (rs1.next()) {
				al.add(new FillLeaveType(rs1.getString("leave_type_id"), rs1.getString("leave_type_name")));
			}
			rs1.close();
			pst1.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
		return al;
	}

	public List<FillLeaveType> fillLeave(int nLevelId, int nEmpId,int nOrgId, int nLocationId) {
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			
			pst = con.prepareStatement("select * from probation_policy where emp_id = ?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			String strAllowedLeaves = null;
			boolean isProbation = false;
			while(rs.next()){
				strAllowedLeaves = rs.getString("leaves_types_allowed");
				isProbation = rs.getBoolean("is_probation");
			}
			rs.close();
			pst.close();
			
			String arr[] = null;
			if(isProbation && strAllowedLeaves!=null && strAllowedLeaves.length()>0){
				arr = strAllowedLeaves.split(",");
			}
			
			
			
//			pst = con.prepareStatement(selectLeaveTypeL);
			pst = con.prepareStatement("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
					" and level_id=? and lt.org_id=? and wlocation_id=? and effective_date =(select max(effective_date) from emp_leave_type " +
					" where level_id = ?  and lt.org_id=? and wlocation_id=?)");
			pst.setInt(1, nLevelId);
			pst.setInt(2, nOrgId);
			pst.setInt(3, nLocationId);
			pst.setInt(4, nLevelId);
			pst.setInt(5, nOrgId);
			pst.setInt(6, nLocationId);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				
				if(isProbation && arr!=null && ArrayUtils.contains(arr, rs.getString("leave_type_id"))>=0){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}else if(!isProbation){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}
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

	
	public List<FillLeaveType> fillCompLeave(int nLevelId, int nEmpId, int nOrgId, int nLocationId) {
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from probation_policy where emp_id = ?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			String strAllowedLeaves = null;
			boolean isProbation = false;
			while(rs.next()){
				strAllowedLeaves = rs.getString("leaves_types_allowed");
				isProbation = rs.getBoolean("is_probation");
			}
			rs.close();
			pst.close();
			
			String arr[] = null;
			if(isProbation && strAllowedLeaves!=null && strAllowedLeaves.length()>0){
				arr = strAllowedLeaves.split(","); 
			}
			
//			pst = con.prepareStatement(selectLeaveTypeL);
			pst = con.prepareStatement("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
				" and level_id=? and lt.org_id=? and wlocation_id=? and effective_date = (select max(effective_date) from emp_leave_type where level_id = ? " +
				" and is_compensatory = true and lt.org_id=? and wlocation_id=?) and lt.is_compensatory = true");
			pst.setInt(1, nLevelId);
			pst.setInt(2, nOrgId);
			pst.setInt(3, nLocationId);
			pst.setInt(4, nLevelId);
			pst.setInt(5, nOrgId);
			pst.setInt(6, nLocationId);
			rs = pst.executeQuery();
			al.add(new FillLeaveType("", "Select Extra Working Type"));
			while (rs.next()) {
				if(isProbation && arr!=null && ArrayUtils.contains(arr, rs.getString("leave_type_id"))>=0){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}else if(!isProbation){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}
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
	
	
	public List<FillLeaveType> fillWorkFromHomeLeave(int nLevelId, int nEmpId, int nOrgId, int nLocationId) {
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from probation_policy where emp_id=?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			String strAllowedLeaves = null;
			boolean isProbation = false;
			while(rs.next()){
				strAllowedLeaves = rs.getString("leaves_types_allowed");
				isProbation = rs.getBoolean("is_probation");
			}
			rs.close();
			pst.close();
			System.out.println("strAllowedLeaves ===>> " + strAllowedLeaves);
			
			String arr[] = null;
			if(isProbation && strAllowedLeaves!=null && strAllowedLeaves.length()>0){
				arr = strAllowedLeaves.split(","); 
			}
			
//			pst = con.prepareStatement(selectLeaveTypeL);
			pst = con.prepareStatement("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
				" and level_id=? and lt.org_id=? and wlocation_id=? and effective_date = (select max(effective_date) from emp_leave_type where level_id = ? " +
				" and is_work_from_home=true and lt.org_id=? and wlocation_id=?) and lt.is_work_from_home=true");
			pst.setInt(1, nLevelId);
			pst.setInt(2, nOrgId);
			pst.setInt(3, nLocationId);
			pst.setInt(4, nLevelId);
			pst.setInt(5, nOrgId);
			pst.setInt(6, nLocationId);
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			al.add(new FillLeaveType("", "Select Work From Home"));
			while (rs.next()) {
				if(isProbation && arr!=null && ArrayUtils.contains(arr, rs.getString("leave_type_id"))>=0){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}else if(!isProbation){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}
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
	
	

	public List<FillLeaveType> fillCompLeave(int nOrgId) {
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM leave_type  where leave_type_id>0 and is_compensatory = true " +
					" and org_id=? order by leave_type_name");
			pst.setInt(1, nOrgId);
			rs = pst.executeQuery();
			al.add(new FillLeaveType("", "Select Extra Working Type"));
			while (rs.next()) {
				al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
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
	
	
	public List<FillLeaveType> fillWorkFromHome(int nOrgId) {
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM leave_type where leave_type_id>0 and is_work_from_home=true " +
				" and org_id=? order by leave_type_name");
			pst.setInt(1, nOrgId);
			rs = pst.executeQuery();
			al.add(new FillLeaveType("", "Select Work From Home"));
			while (rs.next()) {
				al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
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
	
	
	public List<FillLeaveType> fillLeave(boolean genderflag,boolean flag,int nLevelId, int nEmpId,int nOrgId, int nLocationId) {
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			
			
			pst = con.prepareStatement("select * from probation_policy where emp_id = ?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			String strAllowedLeaves = null;
			boolean isProbation = false;
			while(rs.next()){
				strAllowedLeaves = rs.getString("leaves_types_allowed");
				isProbation = rs.getBoolean("is_probation");
			}
			rs.close();
			pst.close();
			
			String arr[] = null;
			if(isProbation && strAllowedLeaves!=null && strAllowedLeaves.length()>0){
				arr = strAllowedLeaves.split(",");
			}
			
			
			
//			pst = con.prepareStatement(selectLeaveTypeL);
			StringBuilder query=new StringBuilder("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
					" and level_id=? and lt.org_id=? and wlocation_id=? and effective_date =(select max(effective_date) from emp_leave_type " +
					" where level_id = ?  and lt.org_id=? and wlocation_id=?)");
			
			if(genderflag || !flag){
				query.append(" and is_maternity=false");
			}
			pst = con.prepareStatement(query.toString());
			pst.setInt(1, nLevelId);
			pst.setInt(2, nOrgId);
			pst.setInt(3, nLocationId);
			pst.setInt(4, nLevelId);
			pst.setInt(5, nOrgId);
			pst.setInt(6, nLocationId); 
			rs = pst.executeQuery();
			
			while (rs.next()) {
				
				if(isProbation && arr!=null && ArrayUtils.contains(arr, rs.getString("leave_type_id"))>=0){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}else if(!isProbation){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}
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
	public List<FillLeaveType> fillLeaveType(int id){
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			/*pst = con.prepareStatement("SELECT * FROM leave_type  where leave_type_id>0 and leave_type_id !=? order by leave_type_name");
			pst.setInt(1,id);*/
			pst = con.prepareStatement("SELECT * FROM leave_type  where leave_type_id>0 and leave_type_id !=? " +
			" and org_id=(SELECT org_id FROM leave_type  where leave_type_id=?) order by leave_type_name");
			pst.setInt(1,id);
			pst.setInt(2,id);
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
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
	
	public List<FillLeaveType> fillLeaveTypeWithoutCompensatory(int id){
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);	
			
			pst = con.prepareStatement("SELECT * FROM leave_type  where leave_type_id>0 and leave_type_id !=? and is_compensatory=false  " +
					" and org_id=(SELECT org_id FROM leave_type  where leave_type_id=?) order by leave_type_name");
			pst.setInt(1,id);
			pst.setInt(2,id);
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
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

	public List<FillLeaveType> fillLeaveWithoutCompensetary(int nOrgId) {
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM leave_type where leave_type_id>0 and org_id =? and is_compensatory = false and (is_leave_opt_holiday is null or is_leave_opt_holiday = false) and (is_work_from_home is null or is_work_from_home = false) order by leave_type_name");
			pst.setInt(1, nOrgId);
			al.add(new FillLeaveType("", "Select Leave Type"));
			rs = pst.executeQuery();
			
			while (rs.next()) {
				al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
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

	
	public List<FillLeaveType> fillLeaveWithoutCompensetary(boolean genderflag, boolean flag, int nLevelId, int nEmpId, int nOrgId, int nLocationId, Date currentDate) {
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);			
			
			pst = con.prepareStatement("select * from probation_policy where emp_id = ?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			String strAllowedLeaves = null;
			boolean isProbation = false;
			while(rs.next()) {
				strAllowedLeaves = rs.getString("leaves_types_allowed");
				isProbation = rs.getBoolean("is_probation");
			}
			rs.close();
			pst.close();
			
			String arr[] = null;
			if(isProbation && strAllowedLeaves!=null && strAllowedLeaves.length()>0) {
				arr = strAllowedLeaves.split(",");
			}
//			System.out.println("arr ===>> " + arr != null ? arr.length: "");
//			pst = con.prepareStatement(selectLeaveTypeL);
			StringBuilder query = new StringBuilder("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
				" and level_id=? and lt.org_id=? and wlocation_id=? and effective_date <=? and lt.is_compensatory = false " +
				"and (lt.is_leave_opt_holiday is null or lt.is_leave_opt_holiday = false) and (lt.is_work_from_home is null or lt.is_work_from_home = false)");
			if(genderflag || !flag) {
				query.append(" and is_maternity=false");
			}
			pst = con.prepareStatement(query.toString());
			pst.setInt(1, nLevelId);
			pst.setInt(2, nOrgId);
			pst.setInt(3, nLocationId);
			pst.setDate(4, currentDate);
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			al.add(new FillLeaveType("", "Select Leave Type"));  
			while (rs.next()) {
				if(isProbation && arr!=null && ArrayUtils.contains(arr, rs.getString("leave_type_id"))>=0) {
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				} else if(!isProbation) {
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				} 
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

	
	public List<FillLeaveType> fillLeaveWithoutCompensetary(int nOrgId, boolean isConstant,Date currentDate) {
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			/*pst = con.prepareStatement("SELECT * FROM leave_type  where leave_type_id>0 and org_id =? and is_compensatory = false order by leave_type_name");
			pst.setInt(1, nOrgId);*/
			/*StringBuilder query=new StringBuilder("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
			" and lt.org_id=? and effective_date <=? and lt.is_compensatory = false and elt.is_constant_balance=? ");*/
			StringBuilder query=new StringBuilder("SELECT * FROM leave_type lt where lt.org_id=?  and lt.leave_type_id in (select elt.leave_type_id " +
				"from emp_leave_type elt where elt.effective_date <=? and elt.is_constant_balance=?) " +
				"and lt.is_compensatory = false and (lt.is_leave_opt_holiday is null or lt.is_leave_opt_holiday = false) " +
				"and (lt.is_work_from_home is null or lt.is_work_from_home = false)");
			pst = con.prepareStatement(query.toString());
			pst.setInt(1, nOrgId);
			pst.setDate(2, currentDate);
			pst.setBoolean(3, isConstant);
			rs = pst.executeQuery();
//			System.out.println("pst=====>"+pst);
			al.add(new FillLeaveType("", "Select Leave Type"));
			while (rs.next()) {
				al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
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

	public List<FillLeaveType> fillLeaveWithoutCompensetary(boolean genderflag,boolean flag,int nLevelId, int nEmpId,int nOrgId, int nLocationId,Date currentDate, boolean isConstant) {
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from probation_policy where emp_id = ?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			String strAllowedLeaves = null;
			boolean isProbation = false;
			while(rs.next()){
				strAllowedLeaves = rs.getString("leaves_types_allowed");
				isProbation = rs.getBoolean("is_probation");
			}
			rs.close();
			pst.close();
			
			String arr[] = null;
			if(isProbation && strAllowedLeaves!=null && strAllowedLeaves.length()>0){
				arr = strAllowedLeaves.split(",");
			}
			
			
			
//			pst = con.prepareStatement(selectLeaveTypeL);
			StringBuilder query=new StringBuilder("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
					" and level_id=? and lt.org_id=? and wlocation_id=? and effective_date <=? " +
					"and lt.is_compensatory = false and elt.is_constant_balance=? and (lt.is_leave_opt_holiday is null or lt.is_leave_opt_holiday = false) " +
					" and (lt.is_work_from_home is null or lt.is_work_from_home = false)");
			if(genderflag || !flag){
				query.append(" and is_maternity=false");
			}
			pst = con.prepareStatement(query.toString());
			pst.setInt(1, nLevelId);
			pst.setInt(2, nOrgId);
			pst.setInt(3, nLocationId);
			pst.setDate(4, currentDate);
			pst.setBoolean(5, isConstant);
			rs = pst.executeQuery();
			al.add(new FillLeaveType("", "Select Leave Type"));
			while (rs.next()) {
				if(isProbation && arr!=null && ArrayUtils.contains(arr, rs.getString("leave_type_id"))>=0){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}else if(!isProbation){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				} 
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
	
	public List<FillLeaveType> fillOptionalHolidayLeave(int nLevelId, int nEmpId, int nOrgId, int nLocationId) {
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from probation_policy where emp_id = ?");
			pst.setInt(1, nEmpId);
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			String strAllowedLeaves = null;
			boolean isProbation = false;
			while(rs.next()){
				strAllowedLeaves = rs.getString("leaves_types_allowed");
				isProbation = rs.getBoolean("is_probation");
			}
			rs.close();
			pst.close();
			
			String arr[] = null;
			if(isProbation && strAllowedLeaves!=null && strAllowedLeaves.length()>0){
				arr = strAllowedLeaves.split(","); 
			}
			
//			pst = con.prepareStatement("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
//					" and level_id=? and lt.org_id=? and wlocation_id=? and effective_date = (select max(effective_date) from emp_leave_type where level_id = ? " +
//					" and lt.org_id=? and wlocation_id=?) and lt.is_leave_opt_holiday = true");
			pst = con.prepareStatement("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
				" and level_id=? and lt.org_id=? and wlocation_id=? and lt.is_leave_opt_holiday = true");
			pst.setInt(1, nLevelId);
			pst.setInt(2, nOrgId);
			pst.setInt(3, nLocationId);
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			al.add(new FillLeaveType("", "Select Leave Type"));
			while (rs.next()) {
				if(isProbation && arr!=null && ArrayUtils.contains(arr, rs.getString("leave_type_id"))>=0){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}else if(!isProbation){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}
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

	public List<FillLeaveType> fillOptionalHolidayLeave(int nOrgId) {
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM leave_type  where leave_type_id>0 and is_leave_opt_holiday = true " +
					" and org_id=? order by leave_type_name");
			pst.setInt(1, nOrgId);
			rs = pst.executeQuery();
			al.add(new FillLeaveType("", "Select Leave Type"));
			while (rs.next()) {
				al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
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

	public List<FillLeaveType> fillLeaveByLevel(int nOrgId, int nLocationId, int nLevelId) {
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM leave_type where leave_type_id>0 and org_id =? and leave_type_id in " +
					"(select leave_type_id from emp_leave_type where org_id=? and wlocation_id=? and level_id=?) order by leave_type_name");
			pst.setInt(1, nOrgId);
			pst.setInt(2, nOrgId);
			pst.setInt(3, nLocationId);
			pst.setInt(4, nLevelId);
			rs = pst.executeQuery();
			al.add(new FillLeaveType("", "Select Leave Type"));
			while (rs.next()) {
				al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
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
	
	public List<FillLeaveType> fillLeave(int nOrgId,int location,int level,String status, int nEmpId, boolean strNoHeaderLabel){
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {				
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select emp_per_id,emp_gender from employee_personal_details where emp_per_id=?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			String strEmpGender = null;
			while(rs.next()){
				strEmpGender = rs.getString("emp_gender");
			}
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("SELECT leave_type_id,leave_type_name, org_id FROM leave_type where leave_type_id>0 and org_id=? " +
					"and leave_type_id in (select leave_type_id from emp_leave_type where org_id=? and wlocation_id=? and level_id=? " +
					"and (leave_available like '%,0,%' or leave_available like '%,"+status+",%')) ");
			if(strEmpGender!=null && strEmpGender.trim().equalsIgnoreCase("F")){
				sbQuery.append(" and leave_category in (0,1)");
			} else if(strEmpGender!=null && strEmpGender.trim().equalsIgnoreCase("M")){
				sbQuery.append(" and leave_category in (0,2)");
			}else {
				sbQuery.append(" and leave_category in (0)");
			}
			sbQuery.append(" order by leave_type_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, nOrgId);
			pst.setInt(2, nOrgId);
			pst.setInt(3, location);
			pst.setInt(4, level);
			System.out.println("pst====>"+pst); 
			rs = pst.executeQuery();
			if(!strNoHeaderLabel) {
				al.add(new FillLeaveType("", "Select Leave Type"));
			}
			while (rs.next()) {				
				al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
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
