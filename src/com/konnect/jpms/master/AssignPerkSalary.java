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

public class AssignPerkSalary extends ActionSupport implements ServletRequestAware, IStatements {
 
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strUserType =  null; 
	String strSessionEmpId; 
	
	public CommonFunctions CF; 
	private String empId;
	private String perkSalaryId;
	private String perkStatus;	
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);

		if(uF.parseToInt(getPerkSalaryId()) > 0){
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
			
			pst = con.prepareStatement("SELECT * FROM perk_salary_details where perk_salary_id=?");
			pst.setInt(1, uF.parseToInt(getPerkSalaryId()));
			rs = pst.executeQuery();
			int nSalaryHeadId = 0;
			int nLevelId = 0;
			int nOrgId = 0;
			double dblAmount = 0.0d;
			String strFinancialStart = null;
			String strFinancialEnd = null;
			boolean flag = false;
			while (rs.next()) {
				nSalaryHeadId = uF.parseToInt(rs.getString("salary_head_id"));
				nLevelId = uF.parseToInt(rs.getString("level_id"));
				nOrgId = uF.parseToInt(rs.getString("org_id"));
				dblAmount = uF.parseToDouble(rs.getString("amount"));
				strFinancialStart = uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, DATE_FORMAT);
				strFinancialEnd = uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, DATE_FORMAT);
				
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
					
					pst = con.prepareStatement("select * from perk_assign_salary_details where emp_id=? and perk_salary_id=? " +
							"and salary_head_id=? and level_id=? and org_id=? and financial_year_start=? and financial_year_end=? " +
							"and paycycle_from=? and paycycle_to=? and paycycle=? and trail_status=?");
					pst.setInt(1, uF.parseToInt(getEmpId()));
					pst.setInt(2, uF.parseToInt(getPerkSalaryId()));
					pst.setInt(3, nSalaryHeadId);
					pst.setInt(4, nLevelId);
					pst.setInt(5, nOrgId);
					pst.setDate(6, uF.getDateFormat(strFinancialStart, DATE_FORMAT));
					pst.setDate(7, uF.getDateFormat(strFinancialEnd, DATE_FORMAT));
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
						pst = con.prepareStatement("update perk_assign_salary_details set trail_status=? where emp_id=? and perk_salary_id=? " +
								"and salary_head_id=? and level_id=? and org_id=? and financial_year_start=? and financial_year_end=? " +
								"and paycycle_from=? and paycycle_to=? and paycycle=?");
						pst.setBoolean(1, false);
						pst.setInt(2, uF.parseToInt(getEmpId()));
						pst.setInt(3, uF.parseToInt(getPerkSalaryId()));
						pst.setInt(4, nSalaryHeadId);
						pst.setInt(5, nLevelId);
						pst.setInt(6, nOrgId);
						pst.setDate(7, uF.getDateFormat(strFinancialStart, DATE_FORMAT));
						pst.setDate(8, uF.getDateFormat(strFinancialEnd, DATE_FORMAT));
						pst.setDate(9, uF.getDateFormat(startDate, DATE_FORMAT));
						pst.setDate(10, uF.getDateFormat(endDate, DATE_FORMAT));
						pst.setInt(11, uF.parseToInt(strPC));
						int a = pst.executeUpdate();
						pst.close();		
						
						if(a > 0){
							pst = con.prepareStatement("insert into perk_assign_salary_details (emp_id,perk_salary_id,salary_head_id,level_id," +
									"org_id,amount,financial_year_start,financial_year_end,status,trail_status,update_by," +
									"update_date,paycycle_from,paycycle_to,paycycle) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
							pst.setInt(1, uF.parseToInt(getEmpId()));
							pst.setInt(2, uF.parseToInt(getPerkSalaryId()));
							pst.setInt(3, nSalaryHeadId);
							pst.setInt(4, nLevelId);
							pst.setInt(5, nOrgId);
							pst.setDouble(6, dblAmount);
							pst.setDate(7, uF.getDateFormat(strFinancialStart, DATE_FORMAT));
							pst.setDate(8, uF.getDateFormat(strFinancialEnd, DATE_FORMAT));
							pst.setBoolean(9, uF.parseToBoolean(getPerkStatus()));
							pst.setBoolean(10, true);
							pst.setInt(11, uF.parseToInt(strSessionEmpId));
							pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setDate(13, uF.getDateFormat(startDate, DATE_FORMAT));
							pst.setDate(14, uF.getDateFormat(endDate, DATE_FORMAT));
							pst.setInt(15, uF.parseToInt(strPC));
							int x = pst.executeUpdate();
							if(x > 0){
								session.setAttribute(MESSAGE, SUCCESSM+"You have successfully saved perk options."+END);
							} else {
								session.setAttribute(MESSAGE, ERRORM+"Colud not saved perk options. Please,try again."+END);
							}
						}
					} else {
						pst = con.prepareStatement("insert into perk_assign_salary_details (emp_id,perk_salary_id,salary_head_id,level_id," +
								"org_id,amount,financial_year_start,financial_year_end,status,trail_status,added_by," +
								"entry_date,paycycle_from,paycycle_to,paycycle) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
						pst.setInt(1, uF.parseToInt(getEmpId()));
						pst.setInt(2, uF.parseToInt(getPerkSalaryId()));
						pst.setInt(3, nSalaryHeadId);
						pst.setInt(4, nLevelId);
						pst.setInt(5, nOrgId);
						pst.setDouble(6, dblAmount);
						pst.setDate(7, uF.getDateFormat(strFinancialStart, DATE_FORMAT));
						pst.setDate(8, uF.getDateFormat(strFinancialEnd, DATE_FORMAT));
						pst.setBoolean(9, uF.parseToBoolean(getPerkStatus()));
						pst.setBoolean(10, true);
						pst.setInt(11, uF.parseToInt(strSessionEmpId));
						pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setDate(13, uF.getDateFormat(startDate, DATE_FORMAT));
						pst.setDate(14, uF.getDateFormat(endDate, DATE_FORMAT));
						pst.setInt(15, uF.parseToInt(strPC));
						int x = pst.executeUpdate();
						if(x > 0){
							session.setAttribute(MESSAGE, SUCCESSM+"You have successfully saved perk options."+END);
						} else {
							session.setAttribute(MESSAGE, ERRORM+"Colud not saved perk options. Please,try again."+END);
						}
					}
				} else {
					session.setAttribute(MESSAGE, ERRORM+"Colud not saved perk options. Please,try again."+END);
				}	
			} else {
				session.setAttribute(MESSAGE, ERRORM+"Colud not saved perk options. Please,try again."+END);
			}
			
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not saved perk options. Please,try again."+END);
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

	public String getPerkSalaryId() {
		return perkSalaryId;
	}

	public void setPerkSalaryId(String perkSalaryId) {
		this.perkSalaryId = perkSalaryId;
	}

	public String getPerkStatus() {
		return perkStatus;
	}

	public void setPerkStatus(String perkStatus) {
		this.perkStatus = perkStatus;
	}
}
