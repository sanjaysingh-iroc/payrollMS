package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillDepartment implements IStatements{

	String deptId;
	String deptName;
	
	public String getDeptId() { 
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	
	public FillDepartment(String deptId, String deptName) {
		this.deptId = deptId;
		this.deptName = deptName;
	}
	HttpServletRequest request;
	public FillDepartment(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillDepartment() {
	}
	
	public List<FillDepartment> fillDepartment(){
		List<FillDepartment> al = new ArrayList<FillDepartment>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectDepartment);
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillDepartment(rs1.getString("dept_id"), rs1.getString("dept_name")));
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
	
	public List<FillDepartment> fillDepartment(int nOrgId){
		List<FillDepartment> al = new ArrayList<FillDepartment>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			if(nOrgId>0){
				pst = con.prepareStatement(selectDepartmentR1);
				pst.setInt(1, nOrgId);
			}else{
				pst = con.prepareStatement(selectDepartment);	
			}
			//System.out.println("pst ===>> " + pst);
			
			rs1 = pst.executeQuery();
			while (rs1.next()) {  
				al.add(new FillDepartment(rs1.getString("dept_id"), rs1.getString("dept_name")));
			}
			rs1.close();
			pst.close();
			
			/*
			al = new ArrayList<FillDepartment>();
			
			
			pst = con.prepareStatement("select * from department_info di where org_id = ? order by parent");
			pst.setInt(1, nOrgId);
			rs1 = pst.executeQuery();
			
			List<List<String>> parentMap = new ArrayList<List<String>>();
            Map<String, List<List<String>>> childMap = new HashMap<String, List<List<String>>>();
            
			while(rs1.next()){
			
				List<String> alInner = new ArrayList<String>();
                
                alInner.add(rs1.getString("dept_id"));
				alInner.add(rs1.getString("dept_name"));
				
				alInner.add(rs1.getString("parent"));
                
                if(uF.parseToInt(rs1.getString("parent"))==0){
                        parentMap.add(alInner);
                }else{ 
                        
                        List<List<String>> outerList=childMap.get(rs1.getString("parent"));
                        if(outerList==null)outerList=new ArrayList<List<String>>();
                        outerList.add(alInner);
                        childMap.put(rs1.getString("parent"), outerList);
                }
			}
			
			
			
			for(int i=0;i<parentMap.size();i++){
              	 List<String> alInner =parentMap.get(i);
              	 String depart=alInner.get(0);
              	 
              	 
              	rec(depart, childMap,uF, al);
              	 
			}
			*/
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	
	public List<FillDepartment> fillDepartmentByOrgOrAccessOrg(int nOrgId, String strOrgId) {
		List<FillDepartment> al = new ArrayList<FillDepartment>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if(nOrgId>0){
				pst = con.prepareStatement(selectDepartmentR1);
				pst.setInt(1, nOrgId);
			} else if(strOrgId != null &&  strOrgId.trim().length()>0) {
				pst = con.prepareStatement("select * from department_info di where org_id in ("+strOrgId+") order by dept_name");
			} else {
				pst = con.prepareStatement(selectDepartment);	
			}
			rs1 = pst.executeQuery();
			while (rs1.next()) {  
				al.add(new FillDepartment(rs1.getString("dept_id"), rs1.getString("dept_name")));
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

	
	public void rec(String parent, Map<String, List<List<String>>> childMap,UtilityFunctions uF, List al){
//		 System.out.println("===>"+parent);
		 List<List<String>> outer= childMap.get(parent);
		 
		 StringBuilder sbSpace = new StringBuilder();
		 if(outer!=null && !outer.isEmpty()){
			 
			 for(int i=0;i<outer.size();i++){
		       	 List<String> alInner =outer.get(i);
		       	 String depart=alInner.get(0);
		       	
		       	 if(uF.parseToInt(parent)!=0){
		       		sbSpace.append("        &amp;nbsp;");
		       	 }
		       	al.add(new FillDepartment(depart,sbSpace.toString()+ alInner.get(1)));
		       	 
		       	 rec(depart,childMap,uF, al);
	        }
			 
		 }
	}
		 
	
	
	/*public List<FillDepartment> fillDepartmentByOrg(int nOrgId){
		List<FillDepartment> al = new ArrayList<FillDepartment>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();

		try {

			con = db.makeConnection(con);
			if(nOrgId>0){
				pst = con.prepareStatement(selectDepartmentR1);
				pst.setInt(1, nOrgId);
			}else{
				pst = con.prepareStatement(selectDepartment);	
			}
			rs1 = pst.executeQuery();
			while (rs1.next()) {  
				al.add(new FillDepartment(rs1.getString("dept_id"), rs1.getString("dept_name")));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
			db.closeStatements(pst);
			db.closeResultSet(rs1);
		}
		return al;
	}*/
	
//	public List<FillDepartment> fillDepartment(String wLocationId){
//		List<FillDepartment> al = new ArrayList<FillDepartment>();
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs1 = null;
//		Database db = new Database();
//		db.setRequest(request);
//		UtilityFunctions uF = new UtilityFunctions();
//
//		try {
//
//			con = db.makeConnection(con);
//			pst = con.prepareStatement(selectDepartment_WLocationId);
//			pst.setInt(1, uF.parseToInt(wLocationId));
//			rs1 = pst.executeQuery();
//			while (rs1.next()) {
//				al.add(new FillDepartment(rs1.getString("dept_id"), rs1.getString("dept_name")));
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			
//			db.closeStatements(pst);
//			db.closeResultSet(rs1);
//			db.closeConnection(con);
//		}
//		return al;
//	}

	public List<FillDepartment> fillDepartmentBYSBU(String strSerivce, String orgId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		List<FillDepartment> al = new ArrayList<FillDepartment>();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select dept_id,dept_name from department_info where service_id=? and org_id=?");
			pst.setInt(1, uF.parseToInt(strSerivce));
			pst.setInt(2, uF.parseToInt(orgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillDepartment(rs.getString("dept_id"), rs.getString("dept_name")));
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

	public List<FillDepartment> fillDepartmentOrgIdAndDepartIds(String orgIds, String departIds) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		List<FillDepartment> al = new ArrayList<FillDepartment>();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select dept_id,dept_name from department_info where org_id in ("+orgIds+") and dept_id in ("+departIds+") order by dept_name");
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillDepartment(rs.getString("dept_id"), rs.getString("dept_name")));
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

	public List<FillDepartment> fillDepartmentWithoutCurrentDepartment(int nEmpId) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		List<FillDepartment> al = new ArrayList<FillDepartment>();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from department_info where org_id in (select org_id from employee_personal_details epd, " +
					"employee_official_details eod where epd.emp_per_id=eod.emp_id and epd.emp_per_id=?) and dept_id not in (select depart_id " +
					"from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id and epd.emp_per_id=?) order by dept_name");
			pst.setInt(1, nEmpId);
			pst.setInt(2, nEmpId);
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillDepartment(rs.getString("dept_id"), rs.getString("dept_name")));
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
	 

	public List<FillDepartment> fillClientDepartment(){
		List<FillDepartment> al = new ArrayList<FillDepartment>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from client_departments");
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillDepartment(rs1.getString("client_depart_id"), rs1.getString("client_depart_name")));
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
	
}
