package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillFinancialYears implements IStatements{

	String financialYearId;
	String financialYearName; 
	
	public FillFinancialYears(String financialYearId, String financialYearName) {
		this.financialYearId = financialYearId;
		this.financialYearName = financialYearName;
	
	}
	HttpServletRequest request;
	public FillFinancialYears(HttpServletRequest request) {
		this.request = request;
	}
		
	public FillFinancialYears() {
	}
	
	public List<FillFinancialYears> fillFinancialYears(CommonFunctions CF){
		
		List<FillFinancialYears> al = new ArrayList<FillFinancialYears>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectFinancialYears);
			rs = pst.executeQuery();
//			System.out.println("DBDATE ===>> " + DBDATE + " -- DATE_FORMAT ===>> "  +DATE_FORMAT + " -- CF ===>> " + CF);
			String strDateFrmt = DATE_FORMAT_STR;
			if(CF.getStrReportDateFormat() != null && CF.getStrReportDateFormat().equals("null")) {
				strDateFrmt = CF.getStrReportDateFormat(); 
			}
			while (rs.next()) {
					al.add(new FillFinancialYears(uF.getDateFormat(rs.getString("financial_year_from"), DBDATE, DATE_FORMAT)+"-"+uF.getDateFormat(rs.getString("financial_year_to"), DBDATE, DATE_FORMAT), uF.getDateFormat(rs.getString("financial_year_from"), DBDATE, strDateFrmt) +" - "+ uF.getDateFormat(rs.getString("financial_year_to"), DBDATE, strDateFrmt)));
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
	
	
	public String[] fillLatestFinancialYears(){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		String []arr = new String[2];

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectFinancialYears);
			rs = pst.executeQuery();

			
			if (rs.next()) {
				arr[0] = uF.getDateFormat(rs.getString("financial_year_from"), DBDATE, DATE_FORMAT);
				arr[1] = uF.getDateFormat(rs.getString("financial_year_to"), DBDATE, DATE_FORMAT);
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
		return arr;
	}


	public String getFinancialYearId() {
		return financialYearId;
	}


	public void setFinancialYearId(String financialYearId) {
		this.financialYearId = financialYearId;
	}


	public String getFinancialYearName() {
		return financialYearName;
	}


	public void setFinancialYearName(String financialYearName) {
		this.financialYearName = financialYearName;
	}

	
	
}
