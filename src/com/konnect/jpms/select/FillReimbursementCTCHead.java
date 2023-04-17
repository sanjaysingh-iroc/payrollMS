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

public class FillReimbursementCTCHead implements IStatements {
	String reimbursementCTCHeadId;
	String reimbursementCTCHeadName;
	
	HttpServletRequest request;

	public String getReimbursementCTCHeadId() {
		return reimbursementCTCHeadId;
	}

	public void setReimbursementCTCHeadId(String reimbursementCTCHeadId) {
		this.reimbursementCTCHeadId = reimbursementCTCHeadId;
	}

	public String getReimbursementCTCHeadName() {
		return reimbursementCTCHeadName;
	}

	public void setReimbursementCTCHeadName(String reimbursementCTCHeadName) {
		this.reimbursementCTCHeadName = reimbursementCTCHeadName;
	}

	public FillReimbursementCTCHead(HttpServletRequest request) {
		super();
		this.request = request;
	}

	public FillReimbursementCTCHead(String reimbursementCTCHeadId, String reimbursementCTCHeadName) {
		super();
		this.reimbursementCTCHeadId = reimbursementCTCHeadId;
		this.reimbursementCTCHeadName = reimbursementCTCHeadName;
	}

	public List<FillReimbursementCTCHead> fillReimbursementCTCHead(String strEmpId, String strFinancialYearStart, String strFinancialYearEnd) {
		List<FillReimbursementCTCHead> fillReimbursementCTCHeads = new ArrayList<FillReimbursementCTCHead>();
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
				
				pst = con.prepareStatement("select reimbursement_head_id,reimbursement_head_name from reimbursement_head_details where level_id=? " +
						"and org_id=? and reimbursement_head_id in (select reimbursement_head_id from reimbursement_head_amt_details " +
						"where is_attachment=true and financial_year_start=? and financial_year_end=?) and reimbursement_ctc_id in " +
						"(select reimbursement_ctc_id from reimbursement_ctc_details where level_id=? and org_id=?)");
				pst.setInt(1, levelId);
				pst.setInt(2, orgId);
				pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, levelId);
				pst.setInt(6, orgId);
				rs = pst.executeQuery();
				while(rs.next()){
					fillReimbursementCTCHeads.add(new FillReimbursementCTCHead(rs.getString("reimbursement_head_id"), rs.getString("reimbursement_head_name")));
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
		return fillReimbursementCTCHeads;
	}
	
}
