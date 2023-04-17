package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.UtilityFunctions;

public class FillPerkType{

	String perkTypeId;
	String perkTypeName;
	
	private FillPerkType(String perkTypeId, String perkTypeName) {
		this.perkTypeId = perkTypeId;
		this.perkTypeName = perkTypeName;
	}
	HttpServletRequest request;
	public FillPerkType(HttpServletRequest request) {
		this.request = request;
	}
	public FillPerkType() {
	}
	
	public List<FillPerkType> fillPerkType(){
		
		List<FillPerkType> al = new ArrayList<FillPerkType>();

		try { 

			al.add(new FillPerkType("E", "Earning"));
			al.add(new FillPerkType("D", "Deduction"));
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	public List<FillPerkType> fillPerkType(int nLevelId, String strFinancialYearStart, String strFinancialYearEnd){
		
		List<FillPerkType> al = new ArrayList<FillPerkType>();

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from perk_details where level_id =? and financial_year_start=? and financial_year_end=? order by perk_name");
			pst.setInt(1, nLevelId);
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) { 
				al.add(new FillPerkType(rs.getString("perk_id"), rs.getString("perk_name") +"["+rs.getString("perk_code")+"]"));
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
	
public List<FillPerkType> fillPerkType(String strFinancialYearStart, String strFinancialYearEnd){
		
		List<FillPerkType> al = new ArrayList<FillPerkType>();

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from perk_details where financial_year_start=? and financial_year_end=? order by perk_name");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, IConstants.DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, IConstants.DATE_FORMAT));
			rs = pst.executeQuery();
			while (rs.next()) { 
				al.add(new FillPerkType(rs.getString("perk_id"), rs.getString("perk_name")));
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

	public String getPerkTypeId() {
		return perkTypeId;
	}

	public void setPerkTypeId(String perkTypeId) {
		this.perkTypeId = perkTypeId;
	}

	public String getPerkTypeName() {
		return perkTypeName;
	}

	public void setPerkTypeName(String perkTypeName) {
		this.perkTypeName = perkTypeName;
	}



}  
