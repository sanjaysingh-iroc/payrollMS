package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillSalaryHeads implements IStatements{ 
	
	String salaryHeadId;
	String salaryHeadName;

	public String getSalaryHeadId() {
		return salaryHeadId;
	}

	public void setSalaryHeadId(String salaryHeadId) {
		this.salaryHeadId = salaryHeadId;
	}

	public String getSalaryHeadName() {
		return salaryHeadName;
	}

	public void setSalaryHeadName(String salaryHeadName) {
		this.salaryHeadName = salaryHeadName;
	}

	public FillSalaryHeads(String salaryHeadId, String salaryHeadName) {
		this.salaryHeadId = salaryHeadId;
		this.salaryHeadName = salaryHeadName;
	}
	
	HttpServletRequest request;
	public FillSalaryHeads(HttpServletRequest request) {
		this.request = request;
	}
	
	public FillSalaryHeads() {}
	 
	
	public List<FillSalaryHeads> fillSalaryHeads(String strLevelId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			if(uF.parseToInt(strLevelId)<0) {
				pst = con.prepareStatement(selectSalaryDetails2);
			} else {
				pst = con.prepareStatement(selectSalaryDetails);
				pst.setInt(1, uF.parseToInt(strLevelId));
			}
			rs = pst.executeQuery();
			while (rs.next()) { 
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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
	
	
	public List<FillSalaryHeads> fillSalaryHeadsWithDuplication(String strLevelId, String salaryBandId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			List alDuplicationTracker = new ArrayList();
			con = db.makeConnection(con);
			
//			pst = con.prepareStatement("SELECT * FROM salary_details where (level_id = ? OR level_id = 0) " +
//					"and (is_delete is null or is_delete=false) and salary_head_id in ("+DA+") order by weight");
//			pst.setInt(1, uF.parseToInt(strLevelId));
//			rs = pst.executeQuery();
//			boolean isDA = false;
//			while (rs.next()) { 
//				isDA = true;
//			}	
//			rs.close();
//			pst.close();
			
//			pst = con.prepareStatement(selectSalaryDetails);
			pst = con.prepareStatement("SELECT * FROM salary_details where (level_id = ? OR level_id = 0) and salary_band_id=? and (is_delete is null or is_delete=false) and (is_reimbursement_ctc is null or is_reimbursement_ctc=false) order by weight");
			pst.setInt(1, uF.parseToInt(strLevelId));
			pst.setInt(2, uF.parseToInt(salaryBandId));
			rs = pst.executeQuery();
			List<String> salHeadList = new ArrayList<String>();
			while (rs.next()) { 
				int index = 0;
				if(alDuplicationTracker.contains(rs.getString("salary_head_id"))){
					index = alDuplicationTracker.indexOf(rs.getString("salary_head_id"));
					
					alDuplicationTracker.remove(index);
					al.remove(index);
				}
				
				alDuplicationTracker.add(rs.getString("salary_head_id"));
				
//				if(isDA && uF.parseToInt(rs.getString("salary_head_id")) == BASIC && uF.parseToInt(rs.getString("salary_head_id"))!=DA){
//					al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")+" + DA"));
//				} else {
					al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
//				}
				if(!salHeadList.contains(rs.getString("salary_head_id")) && (rs.getInt("salary_head_id") == EMPLOYEE_EPF || rs.getInt("salary_head_id") == EMPLOYEE_ESI || rs.getInt("salary_head_id") == EMPLOYEE_LWF)) {
					salHeadList.add(rs.getString("salary_head_id"));
				}
			}
			rs.close();
			pst.close();
//			System.out.println("salHeadList ===>> " + salHeadList);
			if(salHeadList != null && salHeadList.contains(EMPLOYEE_EPF+"")) {
				al.add(new FillSalaryHeads(EMPLOYER_EPF+"", "Employer PF"));
			} 
			if(salHeadList != null && salHeadList.contains(EMPLOYEE_ESI+"")) {
				al.add(new FillSalaryHeads(EMPLOYER_ESI+"", "Employer ESI"));
			}
			if(salHeadList != null && salHeadList.contains(EMPLOYEE_LWF+"")) {
				al.add(new FillSalaryHeads(EMPLOYER_LWF+"", "Employer LWF"));
			}
			
//			fillSalaryContributionHeads(al, salHeadList);
			
			pst = con.prepareStatement("select * from salary_details where level_id=-1 and salary_head_id =? and is_reimbursement_ctc=?");
			pst.setInt(1, REIMBURSEMENT_CTC);
			pst.setBoolean(2, true);
			rs = pst.executeQuery();
			while (rs.next()) { 
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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
	
	
	public List<FillSalaryHeads> fillSalaryHeadsWithDuplication(String strLevelId, boolean isOther, String strEarningDeduction){
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			List alDuplicationTracker = new ArrayList();
			
			con = db.makeConnection(con);
//			pst = con.prepareStatement("select distinct(salary_head_id),salary_head_name  from salary_details where (level_id = 0 or level_id = -1 or level_id = ?) and earning_deduction =? order by salary_head_name");
//			pst.setInt(1, uF.parseToInt(strLevelId));
//			pst.setString(2, strEarningDeduction);
			
			pst = con.prepareStatement("select distinct(salary_head_id),salary_head_name  from salary_details " +
					"where earning_deduction =? and (is_delete is null or is_delete=false) order by salary_head_name");
			pst.setString(1, strEarningDeduction);
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) { 
				int index = 0;
				if(alDuplicationTracker.contains(rs.getString("salary_head_id"))){
					index = alDuplicationTracker.indexOf(rs.getString("salary_head_id"));
					
					alDuplicationTracker.remove(index);
					al.remove(index);
				}
				alDuplicationTracker.add(rs.getString("salary_head_id"));
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
			}	
			rs.close();
			pst.close();
			if(isOther){
				al.add(new FillSalaryHeads("-1", "Other"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		return al;
	}
	
	public List<FillSalaryHeads> fillSalaryHeads(){
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectSalaryDetails1);
			rs = pst.executeQuery();
			while (rs.next()) { 
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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
	
	public List<FillSalaryHeads> fillSalaryHeadsD(){
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement(selectSalaryDetails);
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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

	public List<FillSalaryHeads> fillSalaryHeadsByVaribles(boolean isvariable,String earning_deduction, String orgId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT distinct(salary_head_id),salary_head_name FROM salary_details where  salary_id>0 ");
			if(earning_deduction!=null && (earning_deduction.equals("E") || earning_deduction.equals("D"))){
				sbQuery.append(" and earning_deduction='"+earning_deduction+"'");
			}
			if(uF.parseToInt(orgId)>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(orgId));
			}
			sbQuery.append(" and is_variable=? and (is_delete is null or is_delete=false) and (is_annual_variable is null or is_annual_variable=false) " +
				"and (is_contribution is null or is_contribution=false) order by salary_head_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setBoolean(1, isvariable);
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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
	
	public List<FillSalaryHeads> fillSalaryHeadsByOrg(String earning_deduction, String orgId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT distinct(salary_head_id),salary_head_name FROM salary_details where  salary_id>0 ");
			if(earning_deduction!=null && (earning_deduction.equals("E") || earning_deduction.equals("D"))){
				sbQuery.append(" and earning_deduction='"+earning_deduction+"'");
			}
			if(uF.parseToInt(orgId)>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(orgId));
			}
			sbQuery.append(" and (is_delete is null or is_delete=false) order by salary_head_name");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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

	public List<FillSalaryHeads> fillSalaryHeadsWithDuplication(){
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			List<String> alDuplicationTracker = new ArrayList<String>();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select distinct(salary_head_id),salary_head_name from salary_details" +
					" where (is_delete is null or is_delete=false) order by salary_head_name");
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) { 
				int index = 0;
				if(alDuplicationTracker.contains(rs.getString("salary_head_id"))){
					index = alDuplicationTracker.indexOf(rs.getString("salary_head_id"));
					
					alDuplicationTracker.remove(index);
					al.remove(index);
				}
				
				
				alDuplicationTracker.add(rs.getString("salary_head_id"));
				
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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

	public List<FillSalaryHeads> fillFixSalaryHeads(){
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
	
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("SELECT * FROM salary_details where level_id=-1 and (is_delete is null or is_delete=false) order by weight ");
			rs = pst.executeQuery();
			while (rs.next()) { 
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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

	public List<FillSalaryHeads> fillLTASalaryHeads(int nEmpId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
	
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("SELECT * FROM salary_details where salary_head_id in (select salary_head_id from payroll_generation_lta " +
					"where emp_id=?) and level_id in (select ld.level_id from level_details ld,designation_details dd where ld.level_id=dd.level_id " +
					"and dd.designation_id in (select designation_id from grades_details where grade_id in (select grade_id from employee_official_details " +
					"where emp_id=?))) order by weight ");
			
			pst.setInt(1, nEmpId);
			pst.setInt(2, nEmpId);
			rs = pst.executeQuery();
			while (rs.next()) { 
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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
	
	public List<FillSalaryHeads> fillLTASalaryHeads1(int nEmpId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("SELECT * FROM salary_details where salary_head_id in (select salary_head_id from payroll_generation_lta " +
					"where emp_id=?) and level_id in (select ld.level_id from level_details ld,designation_details dd where ld.level_id=dd.level_id " +
					"and dd.designation_id in (select designation_id from grades_details where grade_id in (select grade_id from employee_official_details " +
					"where emp_id=?))) and (is_delete is null or is_delete=false) and earning_deduction = 'D' order by weight ");
			
			pst.setInt(1, nEmpId);
			pst.setInt(2, nEmpId);
			rs = pst.executeQuery();
			while (rs.next()) { 
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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

	public List<FillSalaryHeads> fillSalaryHeadsByEmpLeaveTypeId(String strEmpLeaveTypeId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
	
		
		try {
	
			List<String> alDuplicationTracker = new ArrayList<String>();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM salary_details where level_id in (select level_id from emp_leave_type where emp_leave_type_id =?) " +
					"and earning_deduction = 'E' and (is_delete is null or is_delete=false) order by weight ");
			pst.setInt(1, uF.parseToInt(strEmpLeaveTypeId));
			rs = pst.executeQuery();
			while (rs.next()) { 
				int index = 0;
				if(alDuplicationTracker.contains(rs.getString("salary_head_id"))){
					index = alDuplicationTracker.indexOf(rs.getString("salary_head_id"));
					
					alDuplicationTracker.remove(index);
					al.remove(index);
				}
				
				alDuplicationTracker.add(rs.getString("salary_head_id"));
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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

	public List<FillSalaryHeads> fillSalaryHeadsWithStatutoryComplianceHeads(String strLevelId, String salaryBand, boolean isOther){
			
			List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
			
			Connection con = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			Database db = new Database();
			db.setRequest(request);
	
			UtilityFunctions uF = new UtilityFunctions();
			try {
	
				List alDuplicationTracker = new ArrayList();
				
				con = db.makeConnection(con);
				
				pst = con.prepareStatement("select distinct(salary_head_id),salary_head_name from salary_details where earning_deduction =? " +
					"and salary_head_id not in("+PROFESSIONAL_TAX+","+TDS+","+EMPLOYEE_EPF+","+EMPLOYER_EPF+","+EMPLOYER_ESI+","+EMPLOYEE_ESI+","+EMPLOYEE_LWF+","+EMPLOYER_LWF+") " +
					"and (is_delete is null or is_delete=false) and salary_head_id not in (select salary_head_id from salary_details where level_id=? " +
					"and level_id>0 and salary_band_id=? and (is_delete is null or is_delete=false)) order by salary_head_name");
				pst.setString(1, "D"); 
				pst.setInt(2, uF.parseToInt(strLevelId));
				pst.setInt(3, uF.parseToInt(salaryBand));
				System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) { 
					int index = 0;
					if(alDuplicationTracker.contains(rs.getString("salary_head_id"))){
						index = alDuplicationTracker.indexOf(rs.getString("salary_head_id"));
						
						alDuplicationTracker.remove(index);
						al.remove(index);
					}
					alDuplicationTracker.add(rs.getString("salary_head_id"));
					al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
				}	
				rs.close();
				pst.close();
				if(isOther){
					al.add(new FillSalaryHeads("-1", "Other"));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
		
			return al;
		}
	
	public List<FillSalaryHeads> fillSalaryHeads(String strLevelId, String salaryBand, boolean isOther, String strEarningDeduction, int nSalType){
			
			List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
			
			Connection con = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			Database db = new Database();
			db.setRequest(request);
			
			UtilityFunctions uF = new UtilityFunctions();
			try {
				List alDuplicationTracker = new ArrayList();
				
				con = db.makeConnection(con);
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select distinct(salary_head_id),salary_head_name from salary_details where earning_deduction =?");
				if(nSalType == 1){
					sbQuery.append(" and is_incentive=true and (is_allowance is null or is_allowance=false) ");
				} else if(nSalType == 2){
					sbQuery.append(" and is_allowance=true and (is_incentive is null or is_incentive=false)");
				} else {
					sbQuery.append(" and (is_incentive is null or is_incentive=false) and (is_allowance is null or is_allowance=false)");
				}
				sbQuery.append(" and salary_head_id not in (select salary_head_id from salary_details where level_id=? and level_id>0 " +
						"and salary_band_id=? and (is_delete is null or is_delete=false)) and salary_head_id not in("+CTC+")");
				sbQuery.append(" and (is_delete is null or is_delete=false) and (is_reimbursement_ctc is null or is_reimbursement_ctc=false) " +
						"order by salary_head_name"); //and (is_contribution is null or is_contribution=false) 
				pst = con.prepareStatement(sbQuery.toString());
				pst.setString(1, strEarningDeduction);
				pst.setInt(2, uF.parseToInt(strLevelId));
				pst.setInt(3, uF.parseToInt(salaryBand));
	//			System.out.println("pst===>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) { 
					int index = 0;
					if(alDuplicationTracker.contains(rs.getString("salary_head_id"))) {
						index = alDuplicationTracker.indexOf(rs.getString("salary_head_id"));
						
						alDuplicationTracker.remove(index);
						al.remove(index);
					}
					alDuplicationTracker.add(rs.getString("salary_head_id"));
					al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
				}	
				rs.close();
				pst.close();
	//			System.out.println("alDuplicationTracker ===>> " + alDuplicationTracker);
				
				if(isOther) {
					al.add(new FillSalaryHeads("-1", "Other"));
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
			return al;
		}
	
	public List<FillSalaryHeads> fillSalaryHeadsWithoutCTC(String strLevelId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
	
			con = db.makeConnection(con);
			
			if(uF.parseToInt(strLevelId)<0){
				pst = con.prepareStatement(selectSalaryDetails2);
			}else{
				pst = con.prepareStatement(selectSalaryDetails);
				pst.setInt(1, uF.parseToInt(strLevelId));
			}
			rs = pst.executeQuery();
			while (rs.next()) { 
				if(uF.parseToInt(rs.getString("salary_head_id")) == CTC || uF.parseToInt(rs.getString("salary_head_id")) == GROSS){
					continue;
				}
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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
	
	public List<FillSalaryHeads> fillSalaryHeadsByEmpLeaveTypeIdWithoutCTC(String strEmpLeaveTypeId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
	
			List<String> alDuplicationTracker = new ArrayList<String>();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM salary_details where level_id in (select level_id from emp_leave_type where emp_leave_type_id =?) " +
					"and earning_deduction = 'E' and salary_head_id not in ("+GROSS+","+CTC+") and (is_delete is null or is_delete=false) order by weight ");
			pst.setInt(1, uF.parseToInt(strEmpLeaveTypeId));
			rs = pst.executeQuery();
			while (rs.next()) { 
				int index = 0;
				if(alDuplicationTracker.contains(rs.getString("salary_head_id"))){
					index = alDuplicationTracker.indexOf(rs.getString("salary_head_id"));
					
					alDuplicationTracker.remove(index);
					al.remove(index);
				}
				
				alDuplicationTracker.add(rs.getString("salary_head_id"));
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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
	
	public List<FillSalaryHeads> fillSalaryHeadsWithDuplicationWithoutCTC(String strLevelId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
	
			List alDuplicationTracker = new ArrayList();
			
			con = db.makeConnection(con);
			pst = con.prepareStatement(selectSalaryDetails);
			pst.setInt(1, uF.parseToInt(strLevelId));
			
			rs = pst.executeQuery();
			while (rs.next()) { 
				if(uF.parseToInt(rs.getString("salary_head_id")) == CTC || uF.parseToInt(rs.getString("salary_head_id")) == GROSS){
					continue;
				}
				
				int index = 0;
				if(alDuplicationTracker.contains(rs.getString("salary_head_id"))){
					index = alDuplicationTracker.indexOf(rs.getString("salary_head_id"));
					
					alDuplicationTracker.remove(index);
					al.remove(index);
				}
				
				alDuplicationTracker.add(rs.getString("salary_head_id"));
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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
	
	public List<FillSalaryHeads> fillSalaryHeadsByOrgWithoutCTC(String earning_deduction, String orgId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT distinct(salary_head_id),salary_head_name FROM salary_details where  salary_id>0 " +
					"and salary_head_id not in ("+GROSS+","+CTC+") ");
			if(earning_deduction!=null && (earning_deduction.equals("E") || earning_deduction.equals("D"))){
				sbQuery.append(" and earning_deduction='"+earning_deduction+"'");
			}
			if(uF.parseToInt(orgId)>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(orgId));
			}
			sbQuery.append(" and (is_delete is null or is_delete=false) order by salary_head_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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

	public List<FillSalaryHeads> fillAllowanceSalaryHeads(String strLevelId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();
		try {
	
			List alDuplicationTracker = new ArrayList();
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(salary_head_id),salary_head_name  from salary_details where is_allowance=true");
			sbQuery.append(" and (is_incentive is null or is_incentive=false) and level_id=? and level_id>0  and salary_head_id not in("+CTC+")");
			sbQuery.append(" and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by salary_head_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strLevelId));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) { 
				int index = 0;
				if(alDuplicationTracker.contains(rs.getString("salary_head_id"))){
					index = alDuplicationTracker.indexOf(rs.getString("salary_head_id"));
					
					alDuplicationTracker.remove(index);
					al.remove(index);
				}
				
				alDuplicationTracker.add(rs.getString("salary_head_id"));
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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

	public List<FillSalaryHeads> fillSalaryHeadsForAllowancePolicy(String strLevelId, String strEarningDeduction){
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();
		try {

			List alDuplicationTracker = new ArrayList();
			
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(salary_head_id),salary_head_name  from salary_details where earning_deduction =? and level_id=? " +
					"and (is_incentive is null or is_incentive=false) and (is_allowance is null or is_allowance=false) and salary_head_id not in("+CTC+") " +
					"and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by salary_head_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setString(1, strEarningDeduction);
			pst.setInt(2, uF.parseToInt(strLevelId));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) { 
				int index = 0;
				if(alDuplicationTracker.contains(rs.getString("salary_head_id"))){
					index = alDuplicationTracker.indexOf(rs.getString("salary_head_id"));
					
					alDuplicationTracker.remove(index);
					al.remove(index);
				}
				
				alDuplicationTracker.add(rs.getString("salary_head_id"));
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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

	public List<FillSalaryHeads> fillAllowanceSalaryHeadsByOrg(UtilityFunctions uF, String strOrgId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			List alDuplicationTracker = new ArrayList();
			
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(salary_head_id),salary_head_name  from salary_details where earning_deduction ='E' and level_id > 0 and org_id=? " +
					"and (is_incentive is null or is_incentive=false) and is_allowance=true and salary_head_id not in("+CTC+") " +
					"and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by salary_head_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strOrgId));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) { 
				int index = 0;
				if(alDuplicationTracker.contains(rs.getString("salary_head_id"))){
					index = alDuplicationTracker.indexOf(rs.getString("salary_head_id"));
					
					alDuplicationTracker.remove(index);
					al.remove(index);
				}
				
				alDuplicationTracker.add(rs.getString("salary_head_id"));
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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

	public List<FillSalaryHeads> fillSalaryHeadsWithDuplicationByGrade(String strGradeId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			List alDuplicationTracker = new ArrayList();
			
			con = db.makeConnection(con);
//			pst = con.prepareStatement("SELECT * FROM salary_details where grade_id = ? " +
//					"and (is_delete is null or is_delete=false) and salary_head_id in ("+DA+") order by weight");
//			pst.setInt(1, uF.parseToInt(strGradeId));
//			rs = pst.executeQuery();
//			boolean isDA = false;
//			while (rs.next()) { 
//				isDA = true;
//			}	
//			rs.close();
//			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM salary_details where grade_id = ? and (is_delete is null or is_delete=false) " +
				"and (is_reimbursement_ctc is null or is_reimbursement_ctc=false) and (is_contribution is null or is_contribution=false) order by weight");
			pst.setInt(1, uF.parseToInt(strGradeId));
			rs = pst.executeQuery();
			List<String> salHeadList = new ArrayList<String>();
			while (rs.next()) { 
				int index = 0;
				if(alDuplicationTracker.contains(rs.getString("salary_head_id"))){
					index = alDuplicationTracker.indexOf(rs.getString("salary_head_id"));
					
					alDuplicationTracker.remove(index);
					al.remove(index);
				}
				
				alDuplicationTracker.add(rs.getString("salary_head_id"));
//				if(isDA && uF.parseToInt(rs.getString("salary_head_id")) == BASIC && uF.parseToInt(rs.getString("salary_head_id"))!=DA){
//					al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")+" + DA"));
//				} else {
					al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
//				}
				if(!salHeadList.contains(rs.getString("salary_head_id")) && (rs.getInt("salary_head_id") == EMPLOYEE_EPF || rs.getInt("salary_head_id") == EMPLOYEE_ESI || rs.getInt("salary_head_id") == EMPLOYEE_LWF)) {
					salHeadList.add(rs.getString("salary_head_id"));
				}
			}	
			rs.close();
			pst.close();
			
			if(salHeadList != null && salHeadList.contains(EMPLOYEE_EPF+"")) {
				al.add(new FillSalaryHeads(EMPLOYER_EPF+"", "Employer PF"));
			} 
			if(salHeadList != null && salHeadList.contains(EMPLOYEE_ESI+"")) {
				al.add(new FillSalaryHeads(EMPLOYER_ESI+"", "Employer ESI"));
			}
			if(salHeadList != null && salHeadList.contains(EMPLOYEE_LWF+"")) {
				al.add(new FillSalaryHeads(EMPLOYER_LWF+"", "Employer LWF"));
			}
			
			pst = con.prepareStatement("select * from salary_details where grade_id=-1 and salary_head_id =? and is_reimbursement_ctc=?");
			pst.setInt(1, REIMBURSEMENT_CTC);
			pst.setBoolean(2, true);
			rs = pst.executeQuery();
			while (rs.next()) { 
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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

	public List<FillSalaryHeads> fillSalaryHeadsByGrade(String strGradeId, boolean isOther, String strEarningDeduction, int nSalType) {
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			List alDuplicationTracker = new ArrayList();
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(salary_head_id),salary_head_name  from salary_details where earning_deduction =?");
			if(nSalType == 1){
				sbQuery.append(" and is_incentive=true and (is_allowance is null or is_allowance=false)");
			} else if(nSalType == 2){
				sbQuery.append(" and is_allowance=true and (is_incentive is null or is_incentive=false)");
			} else {
				sbQuery.append(" and (is_incentive is null or is_incentive=false) and (is_allowance is null or is_allowance=false)");
			}
			sbQuery.append(" and salary_head_id not in (select salary_head_id from salary_details where grade_id=? and grade_id>0 " +
					"and (is_delete is null or is_delete=false)) and salary_head_id not in("+CTC+")");
			sbQuery.append(" and (is_delete is null or is_delete=false) and (is_reimbursement_ctc is null or is_reimbursement_ctc=false) " +
					"and (is_contribution is null or is_contribution=false) order by salary_head_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setString(1, strEarningDeduction);
			pst.setInt(2, uF.parseToInt(strGradeId));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) { 
				int index = 0;
				if(alDuplicationTracker.contains(rs.getString("salary_head_id"))){
					index = alDuplicationTracker.indexOf(rs.getString("salary_head_id"));
					
					alDuplicationTracker.remove(index);
					al.remove(index);
				}
				
				alDuplicationTracker.add(rs.getString("salary_head_id"));
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
			}	
			rs.close();
			pst.close();
			if(isOther){
				al.add(new FillSalaryHeads("-1", "Other"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		return al;
	}

	public List<FillSalaryHeads> fillSalaryHeadsWithStatutoryComplianceHeadsByGrade(String strGradeId, boolean isOther) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		UtilityFunctions uF = new UtilityFunctions();
		try {

			List alDuplicationTracker = new ArrayList();
			
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select distinct(salary_head_id),salary_head_name from salary_details where earning_deduction =? " +
				"and salary_head_id not in("+PROFESSIONAL_TAX+","+TDS+","+EMPLOYEE_EPF+","+EMPLOYER_EPF+","+EMPLOYER_ESI+","+EMPLOYEE_ESI+","+EMPLOYEE_LWF+","+EMPLOYER_LWF+","+REIMBURSEMENT_CTC+") " +
				"and (is_delete is null or is_delete=false) and salary_head_id not in (select salary_head_id from salary_details where grade_id=? " +
				"and grade_id>0 and (is_delete is null or is_delete=false) and (is_reimbursement_ctc is null or is_reimbursement_ctc=false)) order by salary_head_name");
			pst.setString(1, "D"); 
			pst.setInt(2, uF.parseToInt(strGradeId));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) { 
				int index = 0;
				if(alDuplicationTracker.contains(rs.getString("salary_head_id"))){
					index = alDuplicationTracker.indexOf(rs.getString("salary_head_id"));
					
					alDuplicationTracker.remove(index);
					al.remove(index);
				}
				
				alDuplicationTracker.add(rs.getString("salary_head_id"));
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
			}	
			rs.close();
			pst.close();
			if(isOther){
				al.add(new FillSalaryHeads("-1", "Other"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		return al;
	}
	
	public List<FillSalaryHeads> fillLTASalaryHeads(String orgId, String[] levelId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT distinct(salary_head_id),salary_head_name FROM salary_details where is_ctc_variable=true and org_id=?" +
					" and level_id in (select level_id from level_details where level_id>0 and org_id=? ");
			if(levelId!=null && levelId.length>0){
                sbQuery.append(" and level_id in ( "+StringUtils.join(levelId, ",")+") ");
            }
			sbQuery.append(") and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by salary_head_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(orgId));
			pst.setInt(2, uF.parseToInt(orgId));
			rs = pst.executeQuery();
			while (rs.next()) { 
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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
	
	public List<FillSalaryHeads> fillSalaryHeadsByAnnualVaribles(boolean isvariable,String earning_deduction, String orgId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT distinct(salary_head_id),salary_head_name FROM salary_details where salary_id>0 ");
			if(earning_deduction!=null && (earning_deduction.equals("E") || earning_deduction.equals("D"))){
				sbQuery.append(" and earning_deduction='"+earning_deduction+"'");
			}
			sbQuery.append(" and is_annual_variable=? and org_id=? and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by salary_head_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setBoolean(1, isvariable);
			pst.setInt(2, uF.parseToInt(orgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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

	public List<FillSalaryHeads> fillSalaryHeadsByAnnualVariblesByLevel(boolean isvariable,String earning_deduction, String orgId, String strLevel) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT distinct(salary_head_id),salary_head_name FROM salary_details where salary_id>0 and is_annual_variable=?");
			if(earning_deduction!=null && (earning_deduction.equals("E") || earning_deduction.equals("D"))){
				sbQuery.append(" and earning_deduction='"+earning_deduction+"'");
			}			
			sbQuery.append(" and org_id=? and level_id=? and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by salary_head_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setBoolean(1, isvariable);
			pst.setInt(2, uF.parseToInt(orgId));
			pst.setInt(3, uF.parseToInt(strLevel));
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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
	
	public List<FillSalaryHeads> fillAllowanceSalaryHeadsByLevel(String strLevelId) {
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();
		try {

			List<String> alDuplicationTracker = new ArrayList<String>();			
			con = db.makeConnection(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(salary_head_id),salary_head_name  from salary_details where level_id=? and is_allowance=true ");
			sbQuery.append(" and salary_head_id not in("+CTC+") and (is_delete is null or is_delete=false) " +
					"and (is_reimbursement_ctc is null or is_reimbursement_ctc=false) and (is_contribution is null or is_contribution=false) order by salary_head_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strLevelId));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) { 
				int index = 0;
				if(alDuplicationTracker.contains(rs.getString("salary_head_id"))){
					index = alDuplicationTracker.indexOf(rs.getString("salary_head_id"));
					
					alDuplicationTracker.remove(index);
					al.remove(index);
				}
				
				alDuplicationTracker.add(rs.getString("salary_head_id"));
				
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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
	
	
	public List<FillSalaryHeads> fillSalaryHeadsByVariblesForGrade(boolean isvariable,String earning_deduction, String orgId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT distinct(salary_head_id),salary_head_name FROM salary_details where  salary_id>0 ");
			if(earning_deduction!=null && (earning_deduction.equals("E") || earning_deduction.equals("D"))){
				sbQuery.append(" and earning_deduction='"+earning_deduction+"'");
			}
			if(uF.parseToInt(orgId)>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(orgId));
			}
			sbQuery.append(" and is_variable=? and (is_delete is null or is_delete=false) and (is_annual_variable is null or is_annual_variable=false) " +
				"and (is_contribution is null or is_contribution=false) order by salary_head_name");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setBoolean(1, isvariable);
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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
	
	
	public List<FillSalaryHeads> fillContributionSalHeadsByLevel(String orgId, String strLevel) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("SELECT distinct(salary_head_id),salary_head_name FROM salary_details where salary_id>0 and is_contribution=true");
//			if(earning_deduction!=null && (earning_deduction.equals("E") || earning_deduction.equals("D"))){
//				sbQuery.append(" and earning_deduction='"+earning_deduction+"'");
//			}			
			sbQuery.append(" and org_id=? and level_id=? and (is_delete is null or is_delete=false) order by salary_head_name");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setBoolean(1, isContribution);
			pst.setInt(1, uF.parseToInt(orgId));
			pst.setInt(2, uF.parseToInt(strLevel));
			rs = pst.executeQuery();
			while (rs.next()) {
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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

	/*public void fillSalaryContributionHeads(List<FillSalaryHeads> al, List<String> salHeadList) {
		
		try {
//			System.out.println("salHeadList ===>> " + salHeadList);
			if(salHeadList != null && salHeadList.contains(EMPLOYEE_EPF)) {
				al.add(new FillSalaryHeads(EMPLOYER_EPF+"", "Employer PF"));
			} else if(salHeadList != null && salHeadList.contains(EMPLOYEE_ESI)) {
				al.add(new FillSalaryHeads(EMPLOYER_ESI+"", "Employer ESI"));
			} else if(salHeadList != null && salHeadList.contains(EMPLOYEE_LWF)) {
				al.add(new FillSalaryHeads(EMPLOYER_LWF+"", "Employer LWF"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	
//===created by parvez date: 10-01-2022===	
//===start===	
	public List<FillSalaryHeads> fillSalaryHeads(String CandID, String recruitId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("SELECT * FROM salary_details where level_id in(select level_id from candidate_application_details join recruitment_details using (recruitment_id) where candidate_id= ? and recruitment_id = ?)"
					+ " and (is_delete is null or is_delete=false) and joining_bonus_component=true order by weight");
			pst.setInt(1, uF.parseToInt(CandID));
			pst.setInt(2, uF.parseToInt(recruitId));
			
			rs = pst.executeQuery();
			while (rs.next()) { 
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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
//===end===	

	
	public List<FillSalaryHeads> fillSalaryHeadsWithNetWithoutCTCAndGross(String strLevelId) {
		
		List<FillSalaryHeads> al = new ArrayList<FillSalaryHeads>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

			con = db.makeConnection(con);
			
			if(uF.parseToInt(strLevelId)<0){
				pst = con.prepareStatement(selectSalaryDetails2);
			}else{
				pst = con.prepareStatement(selectSalaryDetails);
				pst.setInt(1, uF.parseToInt(strLevelId));
			}
			rs = pst.executeQuery();
			al.add(new FillSalaryHeads(NET+"", "Net Salary"));
			while (rs.next()) { 
				if(uF.parseToInt(rs.getString("salary_head_id")) == CTC || uF.parseToInt(rs.getString("salary_head_id")) == GROSS){
					continue;
				}
				al.add(new FillSalaryHeads(rs.getString("salary_head_id"), rs.getString("salary_head_name")));
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