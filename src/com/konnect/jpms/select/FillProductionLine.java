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

public class FillProductionLine implements IStatements{
	String productionLineId;
	String productionLineName;	
	
	public FillProductionLine(String productionLineId, String productionLineName) {
		super();
		this.productionLineId = productionLineId;
		this.productionLineName = productionLineName;
	}
	HttpServletRequest request;
	
	public FillProductionLine(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getProductionLineId() {
		return productionLineId;
	}
	public void setProductionLineId(String productionLineId) {
		this.productionLineId = productionLineId;
	}
	public String getProductionLineName() {
		return productionLineName;
	}
	public void setProductionLineName(String productionLineName) {
		this.productionLineName = productionLineName;
	}
	
	public List<FillProductionLine> fillProductionLine(String strOrgId) {
		List<FillProductionLine> al = new ArrayList<FillProductionLine>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * FROM production_line_details where org_id=? order by production_line_name");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillProductionLine(rs.getString("production_line_id"), rs.getString("production_line_name")));
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

	public List<FillProductionLine> fillProductionLineBySalaryHead(String strOrgId, String strLevel, String strSalaryHeadId) {
		List<FillProductionLine> al = new ArrayList<FillProductionLine>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * FROM production_line_details where org_id=? and production_line_id in (select production_line_id from production_line_heads where level_id=? and salary_heads like '%,"+strSalaryHeadId+",%') order by production_line_name");
			pst.setInt(1, uF.parseToInt(strOrgId));
			pst.setInt(2, uF.parseToInt(strLevel));
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillProductionLine(rs.getString("production_line_id"), rs.getString("production_line_name")));
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
}