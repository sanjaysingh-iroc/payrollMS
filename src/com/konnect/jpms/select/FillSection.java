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

public class FillSection implements IStatements{

	String sectionId;
	String sectionCode;
	
	private FillSection(String sectionId, String sectionCode) {
		this.sectionId = sectionId;
		this.sectionCode = sectionCode;
	}
	HttpServletRequest request;
	public FillSection(HttpServletRequest request) {
		this.request = request;
	}
	public FillSection() {
	}
	
	public List<FillSection> fillSection(){
		
		List<FillSection> al = new ArrayList<FillSection>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectSection);
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillSection(rs.getString("section_id"), rs.getString("section_code")));				
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

	public String getSectionId() {
		return sectionId;
	}

	public void setSectionId(String sectionId) {
		this.sectionId = sectionId;
	}

	public String getSectionCode() {
		return sectionCode;
	}

	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}
	public List<FillSection> fillSectionFinancialYear(String strFinancialYearStart, String strFinancialYearEnd) {
		
		List<FillSection> al = new ArrayList<FillSection>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				al.add(new FillSection(rs.getString("section_id"), rs.getString("section_code")));				
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
