package com.konnect.jpms.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AssignReimbursementHead extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strUserType =  null; 
	String strSessionEmpId; 
	
	public CommonFunctions CF; 
	private String empId;
	private String reimHeadId;
	private String reimHeadStatus;	
	private String strFinancialYearStart;
	private String strFinancialYearEnd;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		if(uF.parseToInt(getReimHeadId()) > 0){
			savePerSalary(uF);
		}
		return SUCCESS;
	}

	private void savePerSalary(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select rhd.reimbursement_ctc_id,rhd.level_id,rhd.org_id,rhad.amount from reimbursement_head_details rhd, " +
					"reimbursement_head_amt_details rhad where rhd.reimbursement_head_id=rhad.reimbursement_head_id and rhd.reimbursement_head_id=? " +
					"and rhad.financial_year_start=? and rhad.financial_year_end=?");
			pst.setInt(1, uF.parseToInt(getReimHeadId()));
			pst.setDate(2, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
			rs = pst.executeQuery();
			int nReimCTCId = 0;
			int nLevelId = 0;
			int nOrgId = 0;
			double dblAmount = 0.0d;
			boolean flag = false;
			while (rs.next()) {
				nReimCTCId = uF.parseToInt(rs.getString("reimbursement_ctc_id"));
				nLevelId = uF.parseToInt(rs.getString("level_id"));
				nOrgId = uF.parseToInt(rs.getString("org_id"));
				dblAmount = uF.parseToDouble(rs.getString("amount"));
				
				flag = true;
			}
			rs.close();
			pst.close();
			
			if(flag){
				String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
				String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, ""+nOrgId);
				if(strPayCycleDate !=null){
					String startDate = strPayCycleDate[0];
					String endDate = strPayCycleDate[1];
					String strPC = strPayCycleDate[2];
					
					pst = con.prepareStatement("select * from reimbursement_assign_head_details where emp_id=? and reimbursement_head_id=? " +
							"and reimbursement_ctc_id=? and level_id=? and org_id=? and financial_year_start=? and financial_year_end=? " +
							"and paycycle_from=? and paycycle_to=? and paycycle=? and trail_status=?");
					pst.setInt(1, uF.parseToInt(getEmpId()));
					pst.setInt(2, uF.parseToInt(getReimHeadId()));
					pst.setInt(3, nReimCTCId);
					pst.setInt(4, nLevelId);
					pst.setInt(5, nOrgId);
					pst.setDate(6, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
					pst.setDate(7, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
					pst.setDate(8, uF.getDateFormat(startDate, DATE_FORMAT));
					pst.setDate(9, uF.getDateFormat(endDate, DATE_FORMAT));
					pst.setInt(10, uF.parseToInt(strPC));
					pst.setBoolean(11, true);
					rs = pst.executeQuery();
					boolean assignStatus = false;
					if(rs.next()){
						assignStatus = true;
					}
					rs.close();
					pst.close();
					
					if(assignStatus){
						pst = con.prepareStatement("update reimbursement_assign_head_details set trail_status=? where emp_id=? and reimbursement_head_id=? " +
								"and reimbursement_ctc_id=? and level_id=? and org_id=? and financial_year_start=? and financial_year_end=? " +
								"and paycycle_from=? and paycycle_to=? and paycycle=?");
						pst.setBoolean(1, false);
						pst.setInt(2, uF.parseToInt(getEmpId()));
						pst.setInt(3, uF.parseToInt(getReimHeadId()));
						pst.setInt(4, nReimCTCId);
						pst.setInt(5, nLevelId);
						pst.setInt(6, nOrgId);
						pst.setDate(7, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
						pst.setDate(8, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
						pst.setDate(9, uF.getDateFormat(startDate, DATE_FORMAT));
						pst.setDate(10, uF.getDateFormat(endDate, DATE_FORMAT));
						pst.setInt(11, uF.parseToInt(strPC));
						int a = pst.executeUpdate();
						pst.close();		
						
						if(a > 0){
							pst = con.prepareStatement("insert into reimbursement_assign_head_details (emp_id,reimbursement_head_id," +
									"reimbursement_ctc_id,level_id,org_id,amount,financial_year_start,financial_year_end,status,trail_status,update_by," +
									"update_date,paycycle_from,paycycle_to,paycycle) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
							pst.setInt(1, uF.parseToInt(getEmpId()));
							pst.setInt(2, uF.parseToInt(getReimHeadId()));
							pst.setInt(3, nReimCTCId);
							pst.setInt(4, nLevelId);
							pst.setInt(5, nOrgId);
							pst.setDouble(6, dblAmount);
							pst.setDate(7, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
							pst.setDate(8, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
							pst.setBoolean(9, uF.parseToBoolean(getReimHeadStatus()));
							pst.setBoolean(10, true);
							pst.setInt(11, uF.parseToInt(strSessionEmpId));
							pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setDate(13, uF.getDateFormat(startDate, DATE_FORMAT));
							pst.setDate(14, uF.getDateFormat(endDate, DATE_FORMAT));
							pst.setInt(15, uF.parseToInt(strPC));
							int x = pst.executeUpdate();
							if(x > 0){
								session.setAttribute(MESSAGE, SUCCESSM+"You have successfully saved Reimbursement option."+END);
							} else {
								session.setAttribute(MESSAGE, ERRORM+"Colud not saved Reimbursement option. Please,try again."+END);
							}
						}
					} else {
						pst = con.prepareStatement("insert into reimbursement_assign_head_details (emp_id,reimbursement_head_id,reimbursement_ctc_id,level_id," +
								"org_id,amount,financial_year_start,financial_year_end,status,trail_status,added_by," +
								"entry_date,paycycle_from,paycycle_to,paycycle) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
						pst.setInt(1, uF.parseToInt(getEmpId()));
						pst.setInt(2, uF.parseToInt(getReimHeadId()));
						pst.setInt(3, nReimCTCId);
						pst.setInt(4, nLevelId);
						pst.setInt(5, nOrgId);
						pst.setDouble(6, dblAmount);
						pst.setDate(7, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
						pst.setDate(8, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
						pst.setBoolean(9, uF.parseToBoolean(getReimHeadStatus()));
						pst.setBoolean(10, true);
						pst.setInt(11, uF.parseToInt(strSessionEmpId));
						pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDate(13, uF.getDateFormat(startDate, DATE_FORMAT));
						pst.setDate(14, uF.getDateFormat(endDate, DATE_FORMAT));
						pst.setInt(15, uF.parseToInt(strPC));
						int x = pst.executeUpdate();
						if(x > 0){
							session.setAttribute(MESSAGE, SUCCESSM+"You have successfully saved Reimbursement option."+END);
						} else {
							session.setAttribute(MESSAGE, ERRORM+"Colud not saved Reimbursement option. Please,try again."+END);
						}
					}
				} else {
					session.setAttribute(MESSAGE, ERRORM+"Colud not saved Reimbursement Option. Please,try again."+END);
				}	
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not saved Reimbursement option. Please,try again."+END);
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not saved Reimbursement option. Please,try again."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getReimHeadId() {
		return reimHeadId;
	}

	public void setReimHeadId(String reimHeadId) {
		this.reimHeadId = reimHeadId;
	}

	public String getReimHeadStatus() {
		return reimHeadStatus;
	}

	public void setReimHeadStatus(String reimHeadStatus) {
		this.reimHeadStatus = reimHeadStatus;
	}

	public String getStrFinancialYearStart() {
		return strFinancialYearStart;
	}

	public void setStrFinancialYearStart(String strFinancialYearStart) {
		this.strFinancialYearStart = strFinancialYearStart;
	}

	public String getStrFinancialYearEnd() {
		return strFinancialYearEnd;
	}

	public void setStrFinancialYearEnd(String strFinancialYearEnd) {
		this.strFinancialYearEnd = strFinancialYearEnd;
	}
	
}