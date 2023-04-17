package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class IncrementDueReport extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5465296951761706172L;
	public HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	public CommonFunctions CF = null;
	private String strdesignation;
	private String strdepartment;
	private String strorg;
	private String stremptype;
	private List<FillDepartment> departmentList;
	private List<FillDesig> designationList;
	private List<FillOrganisation> organisationList;
	private List<FillEmploymentType> employementTypeList;
	List<FillFinancialYears> financialYearList; 
	List<FillMonth> monthList;
	
	String strMonth;
	String calendarYear;	

	String strDepartment;
	String strDesignation;
	String strEmployeType;
	
	private String f_org;
	String[] f_employeType;
	String[] f_department;
	String[] f_designation;

	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/reports/IncrementDueReport.jsp");
		request.setAttribute(TITLE, "Increment Due Report");
		strEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}

		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrDesignation() != null && !getStrDesignation().equals("")) {
			setF_designation(getStrDesignation().split(","));
		} else {
			setF_designation(null);
		}
		if(getStrEmployeType() != null && !getStrEmployeType().equals("")) {
			setF_employeType(getStrEmployeType().split(","));
		} else {
			setF_employeType(null);
		}
		
		viewIncrementDueReport(uF);
		return loadPaySlips();
	}



	private void viewIncrementDueReport(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);

			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;

			if (getCalendarYear() != null) {
				strCalendarYearDates = getCalendarYear().split("-");
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			} else {
				strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
				setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			}
			
			if(getStrMonth() == null){
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
			
			int nselectedMonth = uF.parseToInt(getStrMonth());
			int nFYSMonth = uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "MM"));
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			if(nselectedMonth>=nFYSMonth){
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "yyyy")));
			}else{
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, "yyyy")));
			}
			int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			
			String strD1 =  nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			String strD2 =  nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			
//			System.out.println("strD1==>"+strD1);
//			System.out.println("strD2==>"+strD2);
						
			List<Map<String, String>> emplist = new ArrayList<Map<String, String>>();

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select epd.increment_date , epd.empcode,epd.emp_fname,epd.emp_lname,epd.joining_date,di.dept_name ,dd.designation_name from employee_personal_details epd, employee_official_details eod left join grades_details gd on gd.grade_id = eod.grade_id left join designation_details dd on dd.designation_id = gd.designation_id left join department_info di on di.dept_id=eod.depart_id where epd.emp_per_id = eod.emp_id and epd.is_alive=true and epd.increment_date between  ? and ?");
			
			if(uF.parseToInt(getStrorg())>0){
				sbQuery.append("  and eod.org_id ="+uF.parseToInt(getStrorg()));
			}
			
			if (getF_designation() != null && getF_designation().length > 0) {
				sbQuery.append(" and desig_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}

			if (getF_department() != null && getF_department().length > 0) {
				sbQuery.append(" and depart_id in (" + StringUtils.join(getF_department(), ",") + ") ");
			}						
			
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}

			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst====>" + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				Map<String, String> hmBloodgroupdata = new HashMap<String, String>();
				hmBloodgroupdata.put("empDepartmentName", uF.showData(rs.getString("dept_name"), ""));
				hmBloodgroupdata.put("empName", uF.showData(rs.getString("emp_fname") + " " + rs.getString("emp_lname"), ""));
				hmBloodgroupdata.put("empCode", uF.showData(rs.getString("empcode"), ""));
				hmBloodgroupdata.put("empdesignationName", uF.showData(rs.getString("designation_name"), ""));
				hmBloodgroupdata.put("empjoining_date",uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				hmBloodgroupdata.put("increment_date",uF.getDateFormat(rs.getString("increment_date"), DBDATE, DATE_FORMAT));
				emplist.add(hmBloodgroupdata);
			}
			rs.close();
			pst.close();
			request.setAttribute("reportList", emplist);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}	
	
	
	

	public String loadPaySlips() {
		UtilityFunctions uF = new UtilityFunctions();
		//organisationList = new FillOrganisation(request).fillOrganisation();
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		designationList = new FillDesig(request).fillDesig(uF.parseToInt(getF_org()));		
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		if (strUserType != null && !strUserType.equalsIgnoreCase(ADMIN)) {
			organisationList = new FillOrganisation(request).fillOrganisation((String) session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();			
		}
		getSelectedFilter(uF);
		return LOAD;
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String, String> hmFilter = new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++){
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=organisationList.get(i).getOrgName();
					} else {
						strOrg+=", "+organisationList.get(i).getOrgName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
			
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		
		alFilter.add("DEPARTMENT");
		if (getF_department() != null) {
			String strDepartment = "";
			int k = 0;
			for (int i = 0; departmentList != null && i < departmentList.size(); i++) {
				for (int j = 0; j < getF_department().length; j++) {
					if (getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if (k == 0) {
							strDepartment = departmentList.get(i).getDeptName();
						} else {
							strDepartment += ", " + departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if (strDepartment != null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}
		
		alFilter.add("EMPTYPE");
		if (getF_employeType() != null) {
			String stremptype = "";
			int k = 0;
			for (int i = 0; employementTypeList != null && i < employementTypeList.size(); i++) {
				for (int j = 0; j < getF_employeType().length; j++) {
					if (getF_employeType()[j].equals(employementTypeList.get(i).getEmpTypeId())) {
						if (k == 0) {
							stremptype = employementTypeList.get(i).getEmpTypeName();
						} else {
							stremptype += ", " + employementTypeList.get(i).getEmpTypeName();
						}
						k++;
					}
				}
			}
			if (stremptype != null && !stremptype.equals("")) {
				hmFilter.put("EMPTYPE", stremptype);
			} else {
				hmFilter.put("EMPTYPE", "All Employee Type");
			}
		} else {
			hmFilter.put("EMPTYPE", "All Employee Type");
		}
		
		alFilter.add("MONTH");
		int nselectedMonth = uF.parseToInt(getStrMonth());
		String strMonth = uF.getMonth(nselectedMonth);
		hmFilter.put("MONTH", strMonth);
		alFilter.add("YEAR");
		hmFilter.put("YEAR", uF.showData(getCalendarYear(), ""));
		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}



	public String getStrdesignation() {
		return strdesignation;
	}

	public void setStrdesignation(String strdesignation) {
		this.strdesignation = strdesignation;
	}

	public String getStremptype() {
		return stremptype;
	}

	public void setStremptype(String stremptype) {
		this.stremptype = stremptype;
	}

	public List<FillDesig> getDesignationList() {
		return designationList;
	}

	public void setDesignationList(List<FillDesig> designationList) {
		this.designationList = designationList;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

	public String getStrorg() {
		return strorg;
	}

	public void setStrorg(String strorg) {
		this.strorg = strorg;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	
	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}


	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getStrdepartment() {
		return strdepartment;
	}

	public void setStrdepartment(String strdepartment) {
		this.strdepartment = strdepartment;
	}
	
	
	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_designation() {
		return f_designation;
	}

	public void setF_designation(String[] f_designation) {
		this.f_designation = f_designation;
	}
	
	
	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}
	
	public String getCalendarYear() {
		return calendarYear;
	}

	public void setCalendarYear(String calendarYear) {
		this.calendarYear = calendarYear;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrDesignation() {
		return strDesignation;
	}

	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}

	public String getStrEmployeType() {
		return strEmployeType;
	}

	public void setStrEmployeType(String strEmployeType) {
		this.strEmployeType = strEmployeType;
	}
	

}


