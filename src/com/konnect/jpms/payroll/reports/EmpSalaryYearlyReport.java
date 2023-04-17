package com.konnect.jpms.payroll.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmpSalaryYearlyReport  extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(EmpSalaryYearlyReport.class);
	
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		 
		request.setAttribute(TITLE, TReportEmpSalaryYearly);
		request.setAttribute(PAGE, PReportEmpSalaryYearly);
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));

//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		/*empList = new FillEmployee().fillEmployeeName(null, null);
		if(getStrEmpId()==null && empList!=null && empList.size()>0){			
			setStrEmpId(empList.get(0).getEmployeeId());
		}*/
		empList = getEmployeeList(uF);		
		viewSalaryYearlyReport(uF);		
		

		return loadSalaryYearlyReport(uF);

	}
	
	
	private List<FillEmployee> getEmployeeList(UtilityFunctions uF) {
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id ");
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
			}
			if(uF.parseToInt(getF_strWLocation())>0){
				sbQuery.append(" and eod.wlocation_id = "+uF.parseToInt(getF_strWLocation()));
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and eod.depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}	
			if (getF_employeType()!=null && !getF_employeType().equalsIgnoreCase("null") && !getF_employeType().equalsIgnoreCase("")) {
				sbQuery.append(" and eod.emptype='"+getF_employeType()+"'");
            }
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst===>emp"+pst);
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+" " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
				
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}


	String financialYear;
	String strEmpId;
	
	List<FillFinancialYears> financialYearList; 
	List<FillEmployee> empList;
	
	String f_strWLocation;
	String f_department;
	String f_level;
	String f_org;
	String f_employeType;
	
	List<FillOrganisation> orgList;	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillEmploymentType> employementTypeList;
	
	public String loadSalaryYearlyReport(UtilityFunctions uF) {
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		orgList = new FillOrganisation(request).fillOrganisation();
		orgList.add(new FillOrganisation("0","All Organization"));
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);	
		
		Collections.sort(orgList, new Comparator<FillOrganisation>() {

			@Override
			public int compare(FillOrganisation o1, FillOrganisation o2) {
				return o1.getOrgId().compareTo(o2.getOrgId());
			}
		});
		
		if(getF_org()!=null && uF.parseToInt(getF_org())>-1){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
//			wLocationList.add(new FillWLocation("-1","Work Location Wise"));
			wLocationList.add(new FillWLocation("0","All Work Location"));
			
			
			
		}else{
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
			wLocationList.add(new FillWLocation("0","All Work Location"));
		}
		
		Collections.sort(wLocationList, new Comparator<FillWLocation>() {

			@Override
			public int compare(FillWLocation o1, FillWLocation o2) {
				return o1.getwLocationId().compareTo(o2.getwLocationId());
			}
		});
		
		if(getF_org()!=null && uF.parseToInt(getF_org())>-1 && getF_strWLocation()!=null && uF.parseToInt(getF_strWLocation())>-1){
			departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
//			departmentList.add(new FillDepartment("-1","Department Wise"));
			departmentList.add(new FillDepartment("0","All Departments"));
			
			
		}else{   
			departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
			departmentList.add(new FillDepartment("0","All Departments"));
		}
		
		Collections.sort(departmentList, new Comparator<FillDepartment>() {

			@Override
			public int compare(FillDepartment o1, FillDepartment o2) {
				return o1.getDeptId().compareTo(o2.getDeptId());
			}
		});
		
		
		if(getF_org()!=null && uF.parseToInt(getF_org())>-1 && getF_strWLocation()!=null && uF.parseToInt(getF_strWLocation())>-1 && getF_department()!=null && uF.parseToInt(getF_department())>-1){
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
//			levelList.add(new FillLevel("-1","Level Wise"));
			levelList.add(new FillLevel("0","All Levels"));
			
			
			
		}else{		
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
			levelList.add(new FillLevel("0","All Levels"));
		}
		
		Collections.sort(levelList, new Comparator<FillLevel>() {

			@Override
			public int compare(FillLevel o1, FillLevel o2) {
				return o1.getLevelId().compareTo(o2.getLevelId());
			}
		});
		if(getF_employeType() != null && !getF_employeType().equals("")) {
			setF_employeType(getF_employeType());
		} else {
			setF_employeType(null);
		}
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null) {
			strFinancialYears = getFinancialYear().split("-");
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		} else {
			strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		}
		hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					strOrg=orgList.get(i).getOrgName();
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
		
		alFilter.add("LOCATION");
		if(getF_strWLocation()!=null) {
			String strLocation="";
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				if(getF_strWLocation().equals(wLocationList.get(i).getwLocationId())) {
					strLocation=wLocationList.get(i).getwLocationName();
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		alFilter.add("DEPARTMENT");
		if(getF_department()!=null) {
			String strDepartment="";
			for(int i=0;departmentList!=null && i<departmentList.size();i++) {
				if(getF_department().equals(departmentList.get(i).getDeptId())) {
					strDepartment=departmentList.get(i).getDeptName();
				}
			}
			if(strDepartment!=null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				if(getF_level().equals(levelList.get(i).getLevelId())) {
					strLevel=levelList.get(i).getLevelCodeName();
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Level's");
			}
		} else {
			hmFilter.put("LEVEL", "All Level's");
		}
		
		alFilter.add("EMPTYPE");
		if(getF_employeType()!=null) {
			String strEmpType="";
			for(int i=0;employementTypeList!=null && i<employementTypeList.size();i++) {
				if(getF_employeType().equals(employementTypeList.get(i).getEmpTypeId())) {
					strEmpType=employementTypeList.get(i).getEmpTypeName();
				}
			}
			if(strEmpType!=null && !strEmpType.equals("")) {
				hmFilter.put("EMPTYPE", strEmpType);
			} else {
				hmFilter.put("EMPTYPE", "All Employee Type");
			}
		} else {
			hmFilter.put("EMPTYPE", "All Employee Type");
		}
		
		alFilter.add("EMP");
		if(getStrEmpId()!=null) {
			String strEmp="";
			for(int i=0;empList!=null && i<empList.size();i++) {
				if(getStrEmpId().equals(empList.get(i).getEmployeeId())) {
					strEmp=empList.get(i).getEmployeeCode();
				}
			}
			if(strEmp!=null && !strEmp.equals("")) {
				hmFilter.put("EMP", strEmp);
			} else {
				hmFilter.put("EMP", "Select Employee");
			}
		} else {
			hmFilter.put("EMP", "Select Employee");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public String viewSalaryYearlyReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
				 
			}
			
			
			Map hmEarningSalaryMap = new LinkedHashMap();
			Map hmEarningSalaryTotalMap = new HashMap();
			Map hmDeductionSalaryMap = new LinkedHashMap();
			Map hmDeductionSalaryTotalMap = new HashMap();
			Map hmEmpInner = new HashMap();
			
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
			List alMonth = new ArrayList();
			
			for(int i=0; i<12; i++){
				alMonth.add((cal.get(Calendar.MONTH)+1)+"");
				
				cal.add(Calendar.MONTH, 1);
			}
			
			con = db.makeConnection(con);
			
			Map hmSalaryHeadMap = CF.getSalaryHeadsMap(con);
			Map hmEmpName = CF.getEmpNameMap(con,null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			Map hmEmpPANNo = CF.getEmpPANNoMap(con);	//added by parvez
			
			String empPanNo = (String)hmEmpPANNo.get(getStrEmpId());
			
			pst = con.prepareStatement("select salary_head_id, sum(amount) as amount, month, entry_date from payroll_generation where financial_year_from_date=? and financial_year_to_date=? and earning_deduction = ? and emp_id =? and is_paid = true group by salary_head_id, month, entry_date order by salary_head_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(3, "E");
			pst.setInt(4, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			String strMonthNew = null;
			String strMonthOld = null;
			while(rs.next()){
				
				strMonthNew = rs.getString("salary_head_id");
				
				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					hmEmpInner = new HashMap();
				}
				
				hmEmpInner.put(rs.getString("month"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(uF.parseToDouble(rs.getString("amount")))));
				hmEarningSalaryMap.put(rs.getString("salary_head_id"), hmEmpInner);
				
				
				double dblAmount = uF.parseToDouble((String)hmEarningSalaryTotalMap.get(rs.getString("month")));
				dblAmount += rs.getDouble("amount");
				hmEarningSalaryTotalMap.put(rs.getString("month"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
				
				strMonthOld  = strMonthNew ;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select salary_head_id, sum(amount) as amount, month, entry_date from payroll_generation where financial_year_from_date=? and financial_year_to_date=? and earning_deduction = ? and emp_id=? and is_paid = true group by salary_head_id, month, entry_date order by salary_head_id");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(3, "D");
			pst.setInt(4, uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while(rs.next()){
				
				strMonthNew = rs.getString("salary_head_id");
				
				if(strMonthNew!=null && !strMonthNew.equalsIgnoreCase(strMonthOld)){
					hmEmpInner = new HashMap();
				}
				
				
				hmEmpInner.put(rs.getString("month"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("amount"))));
				hmDeductionSalaryMap.put(rs.getString("salary_head_id"), hmEmpInner);
				
				
				double dblAmount = uF.parseToDouble((String)hmDeductionSalaryTotalMap.get(rs.getString("month")));
				dblAmount += rs.getDouble("amount");
				hmDeductionSalaryTotalMap.put(rs.getString("month"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
				
				strMonthOld  = strMonthNew ;
			}
			rs.close();
			pst.close();
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEarningSalaryMap", hmEarningSalaryMap);
			request.setAttribute("hmDeductionSalaryMap", hmDeductionSalaryMap);
			request.setAttribute("hmEarningSalaryTotalMap", hmEarningSalaryTotalMap);
			request.setAttribute("hmDeductionSalaryTotalMap", hmDeductionSalaryTotalMap);
			request.setAttribute("hmSalaryHeadMap", hmSalaryHeadMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPANNo", hmEmpPANNo);
			request.setAttribute("alMonth", alMonth);
			request.setAttribute("empPanNo", empPanNo);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}


	public String getStrEmpId() {
		return strEmpId;
	}


	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}


	public List<FillEmployee> getEmpList() {
		return empList;
	}


	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}


	public String getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public String getF_department() {
		return f_department;
	}


	public void setF_department(String f_department) {
		this.f_department = f_department;
	}


	public String getF_level() {
		return f_level;
	}


	public void setF_level(String f_level) {
		this.f_level = f_level;
	}


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public List<FillOrganisation> getOrgList() {
		return orgList;
	}


	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}


	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}


	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String f_employeType) {
		this.f_employeType = f_employeType;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

}
