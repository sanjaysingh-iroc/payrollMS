package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;

public class FillCollateral implements IStatements {
	String collateralId;
	String collateralName; 
	
	public FillCollateral() {
		
	}
	public FillCollateral(String collateralId, String collateralName) {
		this.collateralId = collateralId;
		this.collateralName = collateralName;
	}
	public String getCollateralId() {
		return collateralId;
	}
	public void setCollateralId(String collateralId) {
		this.collateralId = collateralId;
	}
	public String getCollateralName() {
		return collateralName;
	}
	public void setCollateralName(String collateralName) {
		this.collateralName = collateralName;
	}
	HttpServletRequest request;
	public FillCollateral(HttpServletRequest request) {
		this.request = request;
	}
	public List<FillCollateral> fillCollateral(String strType,com.konnect.jpms.util.CommonFunctions CF) {
		
		List<FillCollateral> al = new ArrayList<FillCollateral>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from document_collateral where _type=? order by collateral_name");
			pst.setString(1, strType);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillCollateral(rs.getString("collateral_id"), rs.getString("collateral_name")));				
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
