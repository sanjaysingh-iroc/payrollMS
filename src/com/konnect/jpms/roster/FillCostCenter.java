package com.konnect.jpms.roster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;



public class FillCostCenter implements IStatements{

	String costCode;
	String empOffId;
	
	private FillCostCenter(String costCode, String empOffId) {
		this.costCode = costCode;
		this.empOffId = empOffId;
	}
	
	public FillCostCenter() {
	}
	
	final static public String selectCostCenter = "SELECT * FROM services order by service_id";
	public List<FillCostCenter> fillCostCenter(){
		
		List<FillCostCenter> al = new ArrayList<FillCostCenter>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectCostCenter);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillCostCenter(rs.getString("service_name"), rs.getString("service_id")));				
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
	
	HttpServletRequest request;
	public FillCostCenter(HttpServletRequest request) {
		this.request = request;
	}
	
	final static public String selectCostCenter1 = "SELECT * FROM services where org_id =? order by service_name";
	public List<FillCostCenter> fillCostCenter(String strOrgId){
		
		List<FillCostCenter> al = new ArrayList<FillCostCenter>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			
			if(uF.parseToInt(strOrgId)>0){
				pst = con.prepareStatement(selectCostCenter1);
				pst.setInt(1, uF.parseToInt(strOrgId));
			}else{
				pst = con.prepareStatement(selectCostCenter);
			}
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillCostCenter(rs.getString("service_name"), rs.getString("service_id")));				
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

	public String getCostCode() {
		return costCode;
	}

	public void setCostCode(String costCode) {
		this.costCode = costCode;
	}

	public String getEmpOffId() {
		return empOffId;
	}

	public void setEmpOffId(String empOffId) {
		this.empOffId = empOffId;
	}
	
}
