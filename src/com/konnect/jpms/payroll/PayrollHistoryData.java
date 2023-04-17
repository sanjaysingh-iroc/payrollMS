package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class PayrollHistoryData implements ServletRequestAware, IStatements {
	public HttpSession session;
	CommonFunctions CF = null; 
	
	String strD1;
	String strD2;
	String strPC;
	String strEmpId;
	
	String strFinancialYearStart;
	String strFinancialYearEnd;
	String strPaidMonth;
	String strPaidYear;
	String strPaidDate;
	
	
	public void insertHistoryData(Connection con, UtilityFunctions uF) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id=eod.emp_id and eod.emp_id=?");
			pst.setInt(1, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			int nOrgId = 0;
			int nWLocationId = 0;
			int nDepartId = 0;
			String strServiceId = null;
			int nGradeId = 0;
			boolean flag = false;
			while (rs.next()){
				nOrgId = uF.parseToInt(rs.getString("org_id"));
				nWLocationId = uF.parseToInt(rs.getString("wlocation_id"));
				nDepartId = uF.parseToInt(rs.getString("depart_id"));
				strServiceId = rs.getString("service_id");
				nGradeId = uF.parseToInt(rs.getString("grade_id"));
				flag = true; 
			}
			rs.close();
			pst.close();
			
			if (flag){
				pst = con.prepareStatement("update payroll_history set org_id=?,wlocation_id=?,depart_id=?,service_id=?,grade_id=?, " +
						"financial_year_start=?,financial_year_end=?,paid_month=?,paid_year=?,paid_date=? where paycycle_from=? and paycycle_to=? and paycycle=? and emp_id=?");
				pst.setInt(1, nOrgId);
				pst.setInt(2, nWLocationId);
				pst.setInt(3, nDepartId);
				pst.setString(4, strServiceId);
				pst.setInt(5, nGradeId);
				pst.setDate(6, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
				pst.setInt(8, uF.parseToInt(getStrPaidMonth()));
				pst.setInt(9, uF.parseToInt(getStrPaidYear()));
				pst.setDate(10, uF.getDateFormat(getStrPaidDate(), DATE_FORMAT));
				pst.setDate(11, uF.getDateFormat(getStrD1(), DATE_FORMAT));
				pst.setDate(12, uF.getDateFormat(getStrD2(), DATE_FORMAT));
				pst.setInt(13, uF.parseToInt(getStrPC()));
				pst.setInt(14, uF.parseToInt(getStrEmpId()));
				int x = pst.executeUpdate();
				pst.close();
				
				if(x == 0){
					pst = con.prepareStatement("insert into payroll_history(emp_id,org_id,wlocation_id,depart_id,service_id,grade_id,paycycle_from,paycycle_to," +
							"paycycle,financial_year_start,financial_year_end,paid_month,paid_year,paid_date) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					pst.setInt(1, uF.parseToInt(getStrEmpId()));
					pst.setInt(2, nOrgId);
					pst.setInt(3, nWLocationId);
					pst.setInt(4, nDepartId);
					pst.setString(5, strServiceId);
					pst.setInt(6, nGradeId);
					pst.setDate(7, uF.getDateFormat(getStrD1(), DATE_FORMAT));
					pst.setDate(8, uF.getDateFormat(getStrD2(), DATE_FORMAT));
					pst.setInt(9, uF.parseToInt(getStrPC()));
					pst.setDate(10, uF.getDateFormat(getStrFinancialYearStart(), DATE_FORMAT));
					pst.setDate(11, uF.getDateFormat(getStrFinancialYearEnd(), DATE_FORMAT));
					pst.setInt(12, uF.parseToInt(getStrPaidMonth()));
					pst.setInt(13, uF.parseToInt(getStrPaidYear()));
					pst.setDate(14, uF.getDateFormat(getStrPaidDate(), DATE_FORMAT));
					pst.execute();
					pst.close();
				}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
		}
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getStrD1() {
		return strD1;
	}
	public void setStrD1(String strD1) {
		this.strD1 = strD1;
	}
	public String getStrD2() {
		return strD2;
	}
	public void setStrD2(String strD2) {
		this.strD2 = strD2;
	}
	public String getStrPC() {
		return strPC;
	}
	public void setStrPC(String strPC) {
		this.strPC = strPC;
	}
	public String getStrEmpId() {
		return strEmpId;
	}
	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
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
	public String getStrPaidMonth() {
		return strPaidMonth;
	}
	public void setStrPaidMonth(String strPaidMonth) {
		this.strPaidMonth = strPaidMonth;
	}
	public String getStrPaidYear() {
		return strPaidYear;
	}
	public void setStrPaidYear(String strPaidYear) {
		this.strPaidYear = strPaidYear;
	}
	public String getStrPaidDate() {
		return strPaidDate;
	}
	public void setStrPaidDate(String strPaidDate) {
		this.strPaidDate = strPaidDate;
	}

}
