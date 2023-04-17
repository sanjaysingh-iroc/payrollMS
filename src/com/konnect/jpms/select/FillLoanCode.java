package com.konnect.jpms.select;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class FillLoanCode implements IStatements{
	private String loanId;
	private String loanCode;

	private FillLoanCode(String loanId, String loanCode) {
		this.loanId = loanId;
		this.loanCode = loanCode;
	}
	HttpServletRequest request;
	public FillLoanCode(HttpServletRequest request) {
		this.request = request;
	}
	public FillLoanCode() {
	}
	
	public List<FillLoanCode> fillLoanCode(int empId, CommonFunctions CF){
		List<FillLoanCode> al = new ArrayList<FillLoanCode>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		

		try {

			con = db.makeConnection(con);
			
			if(empId > 0){
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
				int nEmpLevel = uF.parseToInt(hmEmpLevelMap.get(""+empId));
				
				pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_per_id =?");
				pst.setInt(1, empId);
//				System.out.println("pst1======>"+pst);
				rs = pst.executeQuery();
				String strJoiningDate = null;
				double dblServiceYears = 0;
				int nOrgId = 0;
				while(rs.next()){
					strJoiningDate = rs.getString("joining_date");
					nOrgId = uF.parseToInt(rs.getString("org_id"));
				}	
				rs.close();
				pst.close();
				
				String serviceDays = null;
				if(strJoiningDate!=null){
					serviceDays  = uF.dateDifference(strJoiningDate, DBDATE, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
				}
				
				if(serviceDays !=null){
					dblServiceYears = uF.parseToDouble(serviceDays ) / 365;
				}
				
				pst = con.prepareStatement("select * from loan_details where min_service_years <= ? and org_id =? and level_id=? order by loan_code");
				pst.setInt(1, (int)dblServiceYears);
				pst.setInt(2, nOrgId);
				pst.setInt(3, nEmpLevel);
//				System.out.println("pst2======>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					al.add(new FillLoanCode(rs.getString("loan_id"), rs.getString("loan_code")));
				}	
				rs.close();
				pst.close();
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
	
	public List<FillLoanCode> fillLoanCode(){
		List<FillLoanCode> al = new ArrayList<FillLoanCode>();
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs1 = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from loan_details order by loan_code");
			rs1 = pst.executeQuery();
			while (rs1.next()) {
				al.add(new FillLoanCode(rs1.getString("loan_id"), rs1.getString("loan_code")));
			}	
			rs1.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs1);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	
		return al;
	}

	public String getLoanId() {
		return loanId;
	}

	public void setLoanId(String loanId) {
		this.loanId = loanId;
	}

	public String getLoanCode() {
		return loanCode;
	}

	public void setLoanCode(String loanCode) {
		this.loanCode = loanCode;
	}
	
}
