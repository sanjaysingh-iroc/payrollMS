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

public class FillBreakType implements IStatements{
	String breakTypeId;
	String breakTypeName; 
	
	
	public FillBreakType(String breakTypeId, String breakTypeName) {
		this.breakTypeId = breakTypeId;
		this.breakTypeName = breakTypeName;
	}
	
	HttpServletRequest request;
	public FillBreakType(HttpServletRequest request) {
		this.request = request;
	}
	public FillBreakType() {
	}
	
	public List<FillBreakType> fillBreakEmp(String strEmpId){
		
		List<FillBreakType> al = new ArrayList<FillBreakType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectBreakTypeE1);
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillBreakType(rs.getString("emp_break_type_id"), rs.getString("break_type_name")));
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

	public List<FillBreakType> fillBreaks(int nOrgId){
		List<FillBreakType> al = new ArrayList<FillBreakType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectBreakTypeF1);
			pst.setInt(1, nOrgId);
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillBreakType(rs.getString("break_type_id"), rs.getString("break_type_name")));
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
	public String getBreakTypeId() {
		return breakTypeId;
	}

	public void setBreakTypeId(String breakTypeId) {
		this.breakTypeId = breakTypeId;
	}

	public String getBreakTypeName() {
		return breakTypeName;
	}

	public void setBreakTypeName(String breakTypeName) {
		this.breakTypeName = breakTypeName;
	}
	public List<FillBreakType> fillBreakEmpPolicy(String strEmpId,com.konnect.jpms.util.CommonFunctions CF) {
		
		List<FillBreakType> al = new ArrayList<FillBreakType>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		try {

			con = db.makeConnection(con);
			Map<String, String> hmEmpLevelMap=CF.getEmpLevelMap(con);
			Map<String, String> hmEmpWlocationMap=CF.getEmpWlocationMap(con);
			pst = con.prepareStatement("select lbt.break_type_id,lbt.break_type_name from leave_break_type lbt left join emp_leave_break_type elbt " +
					" on elbt.break_type_id=lbt.break_type_id  where elbt.level_id=? and elbt.org_id=? " +
					" and elbt.wlocation_id=? or lbt.break_type_id in(-1,-2) order by lbt.break_type_id");
			pst.setInt(1, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
			pst.setInt(2, uF.parseToInt(CF.getEmpOrgId(con, uF, strEmpId)));
			pst.setInt(3, uF.parseToInt(hmEmpWlocationMap.get(strEmpId)));
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillBreakType(rs.getString("break_type_id"), rs.getString("break_type_name")));
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
