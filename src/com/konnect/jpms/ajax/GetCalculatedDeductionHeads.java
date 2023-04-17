package com.konnect.jpms.ajax;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Dattatray
 * @since 20-10-21
 *
 */
public class GetCalculatedDeductionHeads extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 5947893602821384559L;

	private String strUserType;
	private String strSessionEmpId;

	HttpSession session;
	public CommonFunctions CF;
	private String empId;
	private String salHeadsAndAmt;
	private String contributeHeads;
	private String effectiveDate;
	private String CandID;
	private String recruitId;
	private String grossAmount;
	private String strGender;
	private String strStateId;
	private String strOrgId;
	private String strLevelId;

	@Override
	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		UtilityFunctions uF = new UtilityFunctions();
		loadData(uF);
		calculateContributions(uF);

		return SUCCESS;
	}

	private void calculateContributions(UtilityFunctions uF) {
		// TODO Auto-generated method stub

		String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
		String strFinancialYearStart = strFinancialYearDates[0];
		String strFinancialYearEnd = strFinancialYearDates[1];

		List<String> alSalHeadsAndAmt = Arrays.asList(getSalHeadsAndAmt().split(","));
		Map<String, String> hmTotal = new HashMap<String, String>();
		for (int i = 0; alSalHeadsAndAmt != null && i < alSalHeadsAndAmt.size(); i++) {
			if (alSalHeadsAndAmt.get(i).length() > 1) {
				String[] strTemp = alSalHeadsAndAmt.get(i).split("::::");
				hmTotal.put(strTemp[0], strTemp[1]);
			}
		}
		System.out.println("hmTotal : "+hmTotal);
		double dblPT = calculateProfessionalTax(uF, uF.parseToDouble(getGrossAmount()), strFinancialYearStart, strFinancialYearEnd);
		request.setAttribute("dblPT", uF.formatIntoFourDecimalWithOutComma(dblPT));

		double dblEPF = calculateCandiEEPF(uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, getCandID());
		request.setAttribute("dblEPF", uF.formatIntoFourDecimalWithOutComma(dblEPF));
		
		double dblESI = calculateCandiEEESI(uF, strFinancialYearStart, strFinancialYearEnd, hmTotal);
		request.setAttribute("dblESI", uF.formatIntoFourDecimalWithOutComma(dblESI));
	}

	private void loadData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from candidate_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getCandID()));
			rs = pst.executeQuery();
			while(rs.next()){
				setStrGender(rs.getString("emp_gender"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select state_id,rd.org_id from state sd, work_location_info wd, recruitment_details rd " +
					"where rd.wlocation=wd.wlocation_id and wd.wlocation_state_id=sd.state_id and rd.recruitment_id=?");
			pst.setInt(1, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
			while(rs.next()){
				setStrStateId(rs.getString("state_id"));
				setStrOrgId(rs.getInt("org_id")+"");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement(
					"select level_id from candidate_application_details join recruitment_details using (recruitment_id) where candidate_id= ? and recruitment_id = ?");
			pst.setInt(1, uF.parseToInt(getCandID()));
			pst.setInt(2, uF.parseToInt(getRecruitId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				setStrLevelId(rs.getInt("level_id")+"");
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
			db.closeResultSet(rs);
			db.closeStatements(pst);
		}
	}
	private double calculateProfessionalTax(UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblAmount = 0;

		Database db = new Database();
		db.setRequest(request);
		try {

			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? "
					+ "and financial_year_from=? and financial_year_to=? and gender =? limit 1");
			pst.setDouble(1, dblGross);
			pst.setDouble(2, dblGross);
			pst.setInt(3, uF.parseToInt(getStrStateId()));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(6, getStrGender());
			rs = pst.executeQuery();
			while (rs.next()) {
				dblAmount = rs.getDouble("deduction_paycycle");
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
			db.closeResultSet(rs);
			db.closeStatements(pst);
		}
		return dblAmount;
	}

	public double calculateCandiEEPF(UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,
			Map<String, String> hmTotal, String strEmpId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;

		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? and org_id=? and level_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrOrgId()));
			pst.setInt(4, uF.parseToInt(getStrLevelId()));
			rs = pst.executeQuery();
			double dblEEPFAmount = 0;
			double dblMaxAmount = 0;
			String strSalaryHeads = null;
			while (rs.next()) {
				dblEEPFAmount = rs.getDouble("eepf_contribution");
				dblMaxAmount = rs.getDouble("epf_max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();

			String[] arrSalaryHeads = null;
			if (strSalaryHeads != null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}

			double dblAmount = 0;
			for (int i = 0; arrSalaryHeads != null && i < arrSalaryHeads.length; i++) {
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
			}

			/**
			 * Change on 24-04-2012
			 */

			if (dblAmount >= dblMaxAmount) {
				dblAmount = dblMaxAmount;

			}
			dblCalculatedAmount = (dblEEPFAmount * dblAmount) / 100;

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeConnection(con);
			db.closeResultSet(rs);
			db.closeStatements(pst);

		}
		return dblCalculatedAmount;

	}
	
private double calculateCandiEEESI(UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd, Map<String, String> hmTotal) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblCalculatedAmount = 0;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from esi_details where financial_year_start= ? and financial_year_end = ? and state_id=? " +
					"and org_id=? and level_id=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrStateId()));
			pst.setInt(4, uF.parseToInt(getStrOrgId()));
			pst.setInt(5, uF.parseToInt(getStrLevelId()));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			double dblEEESIAmount = 0;
			double dblESIMaxAmount = 0;
			String strSalaryHeads = null;
			while(rs.next()) {
				dblEEESIAmount = rs.getDouble("eesi_contribution");
				dblESIMaxAmount = rs.getDouble("max_limit");
				strSalaryHeads = rs.getString("salary_head_id");
			}
			rs.close();
			pst.close();

			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null) {
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			double dblAmount = 0;
			double dblAmountEligibility = 0; 
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++) {
				if(uF.parseToInt(arrSalaryHeads[i])!=OVER_TIME) {
					dblAmountEligibility += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
				}
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));	
			}
			
			if(dblAmountEligibility<dblESIMaxAmount) {
				dblCalculatedAmount = (( dblEEESIAmount * dblAmount ) / 100);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeConnection(con);
			db.closeResultSet(rs);
			db.closeStatements(pst);
			
		}
		return dblCalculatedAmount;
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

	public String getSalHeadsAndAmt() {
		return salHeadsAndAmt;
	}

	public void setSalHeadsAndAmt(String salHeadsAndAmt) {
		this.salHeadsAndAmt = salHeadsAndAmt;
	}

	public String getContributeHeads() {
		return contributeHeads;
	}

	public void setContributeHeads(String contributeHeads) {
		this.contributeHeads = contributeHeads;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getCandID() {
		return CandID;
	}

	public void setCandID(String candID) {
		CandID = candID;
	}

	public String getRecruitId() {
		return recruitId;
	}

	public void setRecruitId(String recruitId) {
		this.recruitId = recruitId;
	}

	public String getGrossAmount() {
		return grossAmount;
	}

	public void setGrossAmount(String grossAmount) {
		this.grossAmount = grossAmount;
	}

	public String getStrGender() {
		return strGender;
	}

	public void setStrGender(String strGender) {
		this.strGender = strGender;
	}

	public String getStrStateId() {
		return strStateId;
	}

	public void setStrStateId(String strStateId) {
		this.strStateId = strStateId;
	}

	public String getStrOrgId() {
		return strOrgId;
	}

	public void setStrOrgId(String strOrgId) {
		this.strOrgId = strOrgId;
	}

	public String getStrLevelId() {
		return strLevelId;
	}

	public void setStrLevelId(String strLevelId) {
		this.strLevelId = strLevelId;
	}

}
