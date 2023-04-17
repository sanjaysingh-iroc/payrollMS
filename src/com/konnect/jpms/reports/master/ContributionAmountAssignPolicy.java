package com.konnect.jpms.reports.master;

import java.net.URLDecoder;
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
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ContributionAmountAssignPolicy extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;

	CommonFunctions CF = null;
	String strUserType;
	HttpSession session;
	
	private String strOrg;
	private String financialYear;
	private String strLevel;
	private String strSalaryHeadId;
	
	private List<FillOrganisation> orgList;
	private List<FillFinancialYears> financialYearList;
	private List<FillLevel> levelList;
	private List<FillSalaryHeads> salaryHeadList;
	
	private String userscreen;
	private String navigationId;
	private String toPage;

	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		strUserType = (String) session.getAttribute(USERTYPE);
		request.setAttribute(PAGE, "/jsp/reports/master/ContributionAmountAssignPolicy.jsp");
		request.setAttribute(TITLE, "Contribution Assign Policy");
		UtilityFunctions uF = new UtilityFunctions();

		if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
			orgList = new FillOrganisation(request).fillOrganisation((String) session.getAttribute(ORG_ACCESS));
			if (uF.parseToInt(getStrOrg()) == 0 && orgList != null && orgList.size() > 0) {
				setStrOrg(orgList.get(0).getOrgId());
			}
		} else {
			if (uF.parseToInt(getStrOrg()) == 0) {
				setStrOrg((String) session.getAttribute(ORGID));
			}
			orgList = new FillOrganisation(request).fillOrganisation();
		}

		viewAnnualVariablePolicy(uF);

		return loadAnnualVariablePolicy(uF);
	}

	
	public String loadAnnualVariablePolicy(UtilityFunctions uF) {
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getStrOrg()));
		salaryHeadList = new FillSalaryHeads(request).fillContributionSalHeadsByLevel(getStrOrg(), getStrLevel());
		
		getSelectedFilter(uF);		
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")
				&& !getFinancialYear().trim().equalsIgnoreCase("NULL-NULL")) {
			String str = URLDecoder.decode(getFinancialYear());
			strFinancialYears = str.split("-");
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		} else {
			strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
			
		}
		hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		alFilter.add("ORGANISATION");
		if(getStrOrg()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++) {
//				for(int j=0;j<getF_sbu().length;j++) {
					if(getStrOrg().equals(orgList.get(i).getOrgId())) {
						strOrg=orgList.get(i).getOrgName();
					}
//				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organizations");
			}
		} else {
			hmFilter.put("ORGANISATION", "All Organizations");
		}
		
		alFilter.add("LEVEL");
		if(getStrLevel()!=null) {
			String strLevel="";
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				if(getStrLevel().equals(levelList.get(i).getLevelId())) {
					strLevel=levelList.get(i).getLevelCodeName();
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "-");
			}
		} else {
			hmFilter.put("LEVEL", "-");
		}
		
		alFilter.add("SAL_HEAD");
		if(getStrSalaryHeadId()!=null) {
			String strSalaryHead="";
			for(int i=0;salaryHeadList!=null && i<salaryHeadList.size();i++) {
				if(getStrSalaryHeadId().equals(salaryHeadList.get(i).getSalaryHeadId())) {
					strSalaryHead=salaryHeadList.get(i).getSalaryHeadName();
				}
			}
			if(strSalaryHead!=null && !strSalaryHead.equals("")) {
				hmFilter.put("SAL_HEAD", strSalaryHead);
			} else {
				hmFilter.put("SAL_HEAD", "-");
			}
		} else {
			hmFilter.put("SAL_HEAD", "-");
		}
		
		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	


	public void viewAnnualVariablePolicy(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {

			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null && !getFinancialYear().trim().equals("") && !getFinancialYear().trim().equalsIgnoreCase("NULL")
					&& !getFinancialYear().trim().equalsIgnoreCase("NULL-NULL")) {
				String str = URLDecoder.decode(getFinancialYear());
				strFinancialYearDates = str.split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}

			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				"and is_alive = true and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd " +
				"where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id=?) and org_id=? " +
				"and eod.emp_id in (select esd.emp_id from emp_salary_details esd, (select max(effective_date) as max_date, emp_id " +
				"from emp_salary_details where isdisplay = true and is_approved=true and salary_head_id=? group by emp_id ) as b " +
				"where esd.effective_date = b.max_date and b.emp_id = esd.emp_id and isdisplay= true and is_approved=true " +
				"and esd.salary_head_id=?) order by epd.emp_fname,epd.emp_mname,epd.emp_lname");
			pst.setInt(1, uF.parseToInt(getStrLevel()));
			pst.setInt(2, uF.parseToInt(getStrOrg()));
			pst.setInt(3, uF.parseToInt(getStrSalaryHeadId()));
			pst.setInt(4, uF.parseToInt(getStrSalaryHeadId()));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alEmp = new ArrayList<Map<String,String>>();
			StringBuilder sbEmp = null;
			while (rs.next()) {
				Map<String, String> hmEmp = new HashMap<String, String>();
				hmEmp.put("EMP_ID", rs.getString("emp_id"));
				
				//String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
				String strMiddleName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				String strEmpName = rs.getString("emp_fname") + uF.showData(strMiddleName, "")+" "+ rs.getString("emp_lname");
				hmEmp.put("EMP_NAME", strEmpName);
				alEmp.add(hmEmp);
				if(sbEmp == null){
					sbEmp = new StringBuilder();
					sbEmp.append(rs.getString("emp_id"));
				} else {
					sbEmp.append(","+rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			
			if(sbEmp != null && sbEmp.length() > 0){
				pst = con.prepareStatement("select * from annual_variable_details where org_id=? and financial_year_start=?" +
					" and financial_year_end=? and salary_head_id=? and level_id=? and emp_id in("+sbEmp.toString()+")");
				pst.setInt(1, uF.parseToInt(getStrOrg()));
				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(4, uF.parseToInt(getStrSalaryHeadId()));
				pst.setInt(5, uF.parseToInt(getStrLevel()));
//				System.out.println("pst ======>"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmSalaryHeadAmount = new HashMap<String, String>();
				while (rs.next()) {
					hmSalaryHeadAmount.put(rs.getString("emp_id"), rs.getString("variable_amount"));
				}
				rs.close();
				pst.close();
				request.setAttribute("hmSalaryHeadAmount", hmSalaryHeadAmount);
			}

			request.setAttribute("alEmp", alEmp);
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

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrSalaryHeadId() {
		return strSalaryHeadId;
	}

	public void setStrSalaryHeadId(String strSalaryHeadId) {
		this.strSalaryHeadId = strSalaryHeadId;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}
	
}