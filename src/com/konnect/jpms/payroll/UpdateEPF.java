package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author Dattatray
 * @since 20-12-21
 *
 */
public class UpdateEPF extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	public HttpSession session;
	String strEmpId = null;
	String strUserType = null;

	private String strLocation;
	private String strLevel;
	private String f_org;
	private String financialYear;
	private String paycycle;

	private String[] f_level;
	private String[] location;

	private List<FillOrganisation> organisationList;
	private List<FillWLocation> wLocationList;
	private List<FillPayCycles> paycycleList;
	private List<FillFinancialYears> financialYearList;
	private List<FillLevel> levelList;

	public CommonFunctions CF = null;

	private String empId;
	private String amount;
	private String type;

	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/payroll/UpdateEPF.jsp");
		request.setAttribute(TITLE, "EPF Details");

		strEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);

		// if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
		organisationList = new FillOrganisation(request).fillOrganisation((String) session.getAttribute(ORG_ACCESS));
		if (uF.parseToInt(getF_org()) <= 0) {
			for (int i = 0; organisationList != null && i < organisationList.size(); i++) {
				if (uF.parseToInt(organisationList.get(i).getOrgId()) == uF.parseToInt((String) session.getAttribute(ORGID))) {
					setF_org((String) session.getAttribute(ORGID));
				} else {
					if (i == 0) {
						setF_org(organisationList.get(0).getOrgId());
					}
				}
			}
		}
		// } else {
		// if (uF.parseToInt(getF_org()) <= 0) {
		// setF_org((String) session.getAttribute(ORGID));
		// }
		// organisationList = new FillOrganisation(request).fillOrganisation();
		// }

		if (getStrLocation() != null && !getStrLocation().equals("")) {
			setLocation(getStrLocation().split(","));
		} else {
			setLocation(null);
		}
		if (getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}

		boolean pageLoadFlag = false;
		String[] strPayCycleDates = null;
		if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		} else {
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			pageLoadFlag = true;
		}

		System.out.println("f_org : " + getF_org());
		System.out.println("Level : " + getF_level());
		System.out.println("getLocation : " + getLocation());
		System.out.println("getStrLocation : " + getStrLocation());
		System.out.println("getStrLevel : " + getStrLevel());
		System.out.println("getPaycycle : " + getPaycycle());
		System.out.println("Emp : " + getEmpId());
		System.out.println("Ammount : " + getAmount());

		if (getType() != null && !getType().isEmpty() && getType().equals("PFUpdate")) {
			updatePF(uF);
		}
		viewEPFData(uF);
		return loadEPFData(uF, pageLoadFlag);

	}

	public String loadEPFData(UtilityFunctions uF, boolean pageLoadFlag) {
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		// if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
		wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String) session.getAttribute(WLOCATION_ACCESS));
		organisationList = new FillOrganisation(request).fillOrganisation((String) session.getAttribute(ORG_ACCESS));
		// } else {
		// organisationList = new FillOrganisation(request).fillOrganisation();
		// wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		// }
		System.out.println("getStrLevel() ===>> " + getStrLevel());

		System.out.println("getStrLevel() ===>> " + getStrLevel());
		if (!pageLoadFlag) {
			return SUCCESS;
		} else {
			return LOAD;
		}
	}
	public String viewEPFData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			String[] strPayCycleDates = getPaycycle().split("-");
			String strD1 = strPayCycleDates[0];
			String strD2 = strPayCycleDates[1];
			String strPC = strPayCycleDates[2];

			con = db.makeConnection(con);

			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			// if(hmEmpName == null) hmEmpName=new HashMap<String, String>();

			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("SELECT * FROM payroll_generation WHERE salary_head_id = ? AND paycycle=? "
//					+ " and emp_id in (select emp_id from employee_official_details eod where org_id = ?");
			 sbQuery.append("SELECT * FROM payroll_generation WHERE salary_head_id = ? AND amount <=1800 AND paycycle=? and is_paid=false and emp_id in (select emp_id from employee_official_details eod where org_id = ?");
			// if((getStrLevel()!=null && !getStrLevel().isEmpty() &&
			// getStrLocation()!=null && !getStrLocation().isEmpty()) ||
			// (getStrLevel()!=null && !getStrLevel().isEmpty() ||
			// getStrLocation()!=null && !getStrLocation().isEmpty() ) ) {
			// sbQuery.append(" and emp_id in (select emp_id from
			// employee_official_details eod where org_id = ? ");
			// }
			if (getStrLevel() != null && !getStrLevel().isEmpty()) {
				sbQuery.append(
						" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "
								+ getStrLevel() + ") ) ");
			}
			if (getStrLocation() != null && !getStrLocation().isEmpty()) {
				sbQuery.append(" and wlocation_id in (" + getStrLocation() + ") ");
			} else if ((String) session.getAttribute(WLOCATION_ACCESS) != null) {
				sbQuery.append(" and wlocation_id in (" + (String) session.getAttribute(WLOCATION_ACCESS) + ")");
			}

			// if((getStrLevel()!=null && !getStrLevel().isEmpty() &&
			// getStrLocation()!=null && !getStrLocation().isEmpty()) ||
			// (getStrLevel()!=null && !getStrLevel().isEmpty() ||
			// getStrLocation()!=null && !getStrLocation().isEmpty() ) ) {
			sbQuery.append(" ) ");
			// }
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, EMPLOYEE_EPF);
			pst.setInt(2, uF.parseToInt(strPC));
			// if((getStrLevel()!=null && !getStrLevel().isEmpty() &&
			// getStrLocation()!=null && !getStrLocation().isEmpty()) ||
			// (getStrLevel()!=null && !getStrLevel().isEmpty() ||
			// getStrLocation()!=null && !getStrLocation().isEmpty() ) ) {
			pst.setInt(3, uF.parseToInt(getF_org()));
			// }
			System.out.println("pst : " + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmEmployeeEPF = new HashMap<String, List<String>>();
			while (rs.next()) {
				List<String> alEmployeeEPF = hmEmployeeEPF.get(rs.getString("emp_id"));
				if (alEmployeeEPF == null)
					alEmployeeEPF = new ArrayList<String>();
				alEmployeeEPF.add(hmEmpName.get(rs.getString("emp_id")));
				alEmployeeEPF.add(rs.getString("amount"));
				hmEmployeeEPF.put(rs.getString("emp_id"), alEmployeeEPF);
			}
			rs.close();
			pst.close();

			request.setAttribute("employeeEPF", hmEmployeeEPF);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	private void updatePF(UtilityFunctions uF) {
		System.out.println("Updateing....");
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			String[] strPayCycleDates = getPaycycle().split("-");
			String strD1 = strPayCycleDates[0];
			String strD2 = strPayCycleDates[1];
			String strPC = strPayCycleDates[2];

			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];

			con = db.makeConnection(con);
			// Created Dattatray Date:23-12-21 Note: wrongly added is_paid true
			// but removed now
			pst = con.prepareStatement("update payroll_generation set amount=? where emp_id=? and paycycle=? and salary_head_id = ?");
			pst.setDouble(1, uF.parseToDouble(getAmount()));
			pst.setInt(2, uF.parseToInt(getEmpId()));
			pst.setInt(3, uF.parseToInt(strPC));
			pst.setInt(4, EMPLOYEE_EPF);
			pst.executeUpdate();
			System.out.println("pst : " + pst);
			pst.close();

			// Started By Dattatray Date: 28-12-21
			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? "
					+ "and org_id in (select org_id from employee_official_details where emp_id=?) and level_id in (select ld.level_id from grades_details gd, "
					+ "level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  "
					+ "and gd.grade_id in (select grade_id from employee_official_details where emp_id=? ))");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getEmpId()));
			pst.setInt(4, uF.parseToInt(getEmpId()));
			System.out.println("Pst : " + pst);
			rs = pst.executeQuery();
			double dblERPFAmount = 0;
			double dblERPSAmount = 0;
			double dblERDLIAmount = 0;
			double dblPFAdminAmount = 0;
			double dblEDLIAdminAmount = 0;
			double dblEEFAmount = 0;
			double dblActualAmount = 0;
			while (rs.next()) {

				dblEEFAmount = rs.getDouble("eepf_contribution");
				dblERPFAmount = rs.getDouble("erpf_contribution");
				dblERPSAmount = rs.getDouble("erps_contribution");
				dblERDLIAmount = rs.getDouble("erdli_contribution");
				dblPFAdminAmount = rs.getDouble("pf_admin_charges");
				dblEDLIAdminAmount = rs.getDouble("edli_admin_charges");

			}
			rs.close();
			pst.close();
			dblActualAmount = ((uF.parseToDouble(getAmount())) * 100) / dblEEFAmount;
			dblEEFAmount = ((dblActualAmount * dblEEFAmount) / 100);
			dblERPFAmount = ((dblActualAmount * dblERPFAmount) / 100);
			dblERPSAmount = ((dblActualAmount * dblERPSAmount) / 100);
			dblERDLIAmount = ((dblActualAmount * dblERDLIAmount) / 100);
			dblPFAdminAmount = ((dblActualAmount * dblPFAdminAmount) / 100);
			dblEDLIAdminAmount = ((dblActualAmount * dblEDLIAdminAmount) / 100);

			System.out.println("[dblActualAmount : " + dblActualAmount + "] [dblEEFAmount : " + dblEEFAmount + "] [dblERPFAmount : " + dblERPFAmount
					+ "] [dblERPSAmount : " + dblERPSAmount + "] [dblERDLIAmount : " + dblERDLIAmount + "] [dblPFAdminAmount : " + dblPFAdminAmount
					+ "] [dblEDLIAdminAmount : " + dblEDLIAdminAmount + "]");

			pst = con.prepareStatement("update emp_epf_details set eepf_contribution=?,erpf_contribution=?,erps_contribution=?,erdli_contribution=?,"
					+ " pf_admin_charges=?,edli_admin_charges=? where emp_id=? and paycycle=?");
			pst.setDouble(1, dblEEFAmount);
			pst.setDouble(2, dblERPFAmount);
			pst.setDouble(3, dblERPSAmount);
			pst.setDouble(4, dblERDLIAmount);
			pst.setDouble(5, dblPFAdminAmount);
			pst.setDouble(6, dblEDLIAdminAmount);
			pst.setInt(7, uF.parseToInt(getEmpId()));
			pst.setInt(8, uF.parseToInt(strPC));
			System.out.println("pst : " + pst);
			pst.execute();

			pst.close();
			// Ended By Dattatray Date: 28-12-21

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String[] getLocation() {
		return location;
	}

	public void setLocation(String[] location) {
		this.location = location;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}