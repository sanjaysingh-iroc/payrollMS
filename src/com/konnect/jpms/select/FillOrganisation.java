package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillOrganisation implements IStatements{

	String orgId;
	String orgName;
	
	public FillOrganisation(String orgId, String orgName) {
		this.orgId = orgId;
		this.orgName = orgName;
	}
	HttpServletRequest request;
	public FillOrganisation(HttpServletRequest request) {
		this.request = request;
	}
	public FillOrganisation() {
	}
	
	public List<FillOrganisation> fillOrganisation(){
		 
		List<FillOrganisation> al = new ArrayList<FillOrganisation>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectOrg);
//			pst = con.prepareStatement("SELECT * FROM org_details order by org_id");
			
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillOrganisation(rs.getString("org_id"), rs.getString("org_name")+" ["+rs.getString("org_code")+"]"));
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

	public List<FillOrganisation> fillOrganisation(String strOrgId){
		 
		List<FillOrganisation> al = new ArrayList<FillOrganisation>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			if(strOrgId!=null && !strOrgId.equals("") && ! strOrgId.equalsIgnoreCase("null")){
				pst = con.prepareStatement("SELECT * FROM org_details where org_id in ("+strOrgId+") order by org_name");
			}else{
				pst = con.prepareStatement("SELECT * FROM org_details order by org_name");
			}
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillOrganisation(rs.getString("org_id"), rs.getString("org_name")+" ["+rs.getString("org_code")+"]"));				
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
	
	
	public List<FillOrganisation> fillOrganisationWithoutCurrentOrgId(String orgId) {
		 
		List<FillOrganisation> al = new ArrayList<FillOrganisation>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			if(orgId != null && !orgId.equals("")) {
				pst = con.prepareStatement("SELECT * FROM org_details where org_id not in("+orgId+") order by org_name");
			} else {
				pst = con.prepareStatement("SELECT * FROM org_details order by org_name");
			}
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillOrganisation(rs.getString("org_id"), rs.getString("org_name")+" ["+rs.getString("org_code")+"]"));				
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
	
	
	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	



}  
