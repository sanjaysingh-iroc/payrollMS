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

public class FillPerkSalary implements IStatements{
	String perkSalaryId;
	String perkSalaryName;
	
	public FillPerkSalary() {
	}

	HttpServletRequest request;

	public FillPerkSalary(HttpServletRequest request) {
		this.request = request;
	}

	public FillPerkSalary(String perkSalaryId, String perkSalaryName) {
		super();
		this.perkSalaryId = perkSalaryId;
		this.perkSalaryName = perkSalaryName; 
	} 
	
	public String getPerkSalaryId() {
		return perkSalaryId;
	}

	public void setPerkSalaryId(String perkSalaryId) {
		this.perkSalaryId = perkSalaryId;
	}

	public String getPerkSalaryName() {
		return perkSalaryName;
	}

	public void setPerkSalaryName(String perkSalaryName) {
		this.perkSalaryName = perkSalaryName;
	}

	public List<FillPerkSalary> fillPerkSalary(String strEmpId,String paycycle) {
		List<FillPerkSalary> fillPerkSalaries = new ArrayList<FillPerkSalary>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try{
			con = db.makeConnection(con);
			
			int levelId = 0;
			pst = con.prepareStatement("select * from level_details ld, grades_details gd, designation_details dd where dd.level_id = ld.level_id " +
					"and dd.designation_id = gd.designation_id and grade_id in (select grade_id from employee_official_details " +
					"where emp_id = ?)");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while(rs.next()) {
				levelId = rs.getInt("level_id");
			}
			rs.close();
			pst.close();
			
			int orgId = 0;
			pst = con.prepareStatement("select org_id from employee_official_details WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while(rs.next()){
				orgId = rs.getInt("org_id");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(strEmpId) > 0 && levelId > 0 && orgId > 0 && paycycle!=null && !paycycle.trim().equals("") && !paycycle.trim().equalsIgnoreCase("")){
				String[] strPayCycleDate = paycycle.split("-");
				String startDate = strPayCycleDate[0];
				String endDate = strPayCycleDate[1];
				String strPC = strPayCycleDate[2];
				
				pst = con.prepareStatement("select psd.perk_salary_id,psd.perk_name from perk_salary_details psd,perk_assign_salary_details psad where " +
						"psd.perk_salary_id = psad.perk_salary_id and psad.status=true and psad.trail_status=true " +
						"and psd.is_attachment=true and psad.emp_id=? and psd.level_id=? and psd.org_id=? " +
						"and psad.paycycle_from=? and psad.paycycle_to=? and paycycle=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
				pst.setInt(2, levelId);
				pst.setInt(3, orgId);
				pst.setDate(4, uF.getDateFormat(startDate, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(endDate, DATE_FORMAT));
				pst.setInt(6, uF.parseToInt(strPC));
				rs = pst.executeQuery();
				while(rs.next()){
					fillPerkSalaries.add(new FillPerkSalary(rs.getString("perk_salary_id"), rs.getString("perk_name")));
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return fillPerkSalaries;
	}

	public List<FillPerkSalary> fillPerkSalary(String strEmpId, String strFinancialYearStart, String strFinancialYearEnd) {
		List<FillPerkSalary> fillPerkSalaries = new ArrayList<FillPerkSalary>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try{
			con = db.makeConnection(con);
			
			int levelId = 0;
			pst = con.prepareStatement("select * from level_details ld, grades_details gd, designation_details dd where dd.level_id = ld.level_id " +
					"and dd.designation_id = gd.designation_id and grade_id in (select grade_id from employee_official_details " +
					"where emp_id = ?)");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while(rs.next()) {
				levelId = rs.getInt("level_id");
			}
			rs.close();
			pst.close();
			
			int orgId = 0;
			pst = con.prepareStatement("select org_id from employee_official_details WHERE emp_id = ?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while(rs.next()){
				orgId = rs.getInt("org_id");
			}
			rs.close();
			pst.close();
			
			if(uF.parseToInt(strEmpId) > 0 && levelId > 0 && orgId > 0 
					&& strFinancialYearStart!=null && !strFinancialYearStart.trim().equals("") && !strFinancialYearStart.trim().equalsIgnoreCase("NULL")
					&& strFinancialYearEnd!=null && !strFinancialYearEnd.trim().equals("") && !strFinancialYearEnd.trim().equalsIgnoreCase("NULL")){
				
				pst = con.prepareStatement("select perk_salary_id,perk_name from perk_salary_details where is_attachment=true " +
						"and level_id=? and org_id=? and financial_year_start=? and financial_year_end=?");
				pst.setInt(1, levelId);
				pst.setInt(2, orgId);
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()){
					fillPerkSalaries.add(new FillPerkSalary(rs.getString("perk_salary_id"), rs.getString("perk_name")));
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return fillPerkSalaries;
	}
}
