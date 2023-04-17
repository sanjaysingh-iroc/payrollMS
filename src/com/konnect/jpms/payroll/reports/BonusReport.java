package com.konnect.jpms.payroll.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BonusReport extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(BonusReport.class);
	
	public String execute() throws Exception { 
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession(); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(TITLE, TBonusReport);
		request.setAttribute(PAGE, PReportBonus);
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));

//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}

		viewBonusReport(uF);
		
		return loadBonusReport(uF);
	}
	
	String financialYear;
	String strMonth;
	String f_org;
	String f_employeType;
	
	List<FillOrganisation> orgList;
	
	List<FillFinancialYears> financialYearList; 
	List<FillMonth> monthList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillEmploymentType> employementTypeList;
	
	String f_strWLocation;
	String f_department;
	String f_level;
	
	public String loadBonusReport(UtilityFunctions uF) {
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);	
		
		
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		if(getF_org()!=null && uF.parseToInt(getF_org())>-1 && getF_strWLocation()!=null && uF.parseToInt(getF_strWLocation())>-1){
			departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
			//departmentList.add(new FillDepartment("-1","Department Wise"));
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
			levelList.add(new FillLevel("-1","Level Wise"));
			levelList.add(new FillLevel("0","All Levels"));
		}else{		
			levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
			levelList.add(new FillLevel("0","All Levels"));
		}
		if(getF_employeType() != null && !getF_employeType().equals("")) {
			setF_employeType(getF_employeType());
		} else {
			setF_employeType(null);
		}
	
		Collections.sort(levelList, new Comparator<FillLevel>() {

			@Override
			public int compare(FillLevel o1, FillLevel o2) {
				return o1.getLevelId().compareTo(o2.getLevelId());
			}
		});
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

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
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
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
		
		alFilter.add("MONTH");
		hmFilter.put("MONTH", uF.getMonth(uF.parseToInt(getStrMonth())));
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public String viewBonusReport(UtilityFunctions uF) {

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
			
			if(getStrMonth()!=null && !getStrMonth().trim().equals("")){
				setStrMonth(getStrMonth());
			}else{
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
			
			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con, null, null);
			Map hmEmpCode = CF.getEmpCodeMap(con);
			Map hmEmpLevel = CF.getEmpLevelMap(con);
			Map hmEmpPanNo = CF.getEmpPANNoMap(con);	//added by parvez
			
			String strMonth=null;
			String strYear=null;
			Map hmEmpBonusMap = new HashMap();
			
			//pst = con.prepareStatement("select emp_id, amount, month, year from payroll_generation where month=? and financial_year_from_date=? and financial_year_to_date=? and salary_head_id = ? and emp_id in (select emp_id from employee_official_details where org_id =?)");
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_id, amount, month, year from payroll_generation where month=? and financial_year_from_date=? " +
					"and financial_year_to_date=? and salary_head_id = ? ");

			if(uF.parseToInt(getF_strWLocation())>0 || uF.parseToInt(getF_department())>0 || uF.parseToInt(getF_level())>0 || (uF.parseToInt(getF_org())>0 || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null))){
				sbQuery.append(" and emp_id in ( select emp_id from employee_official_details where emp_id>0 ");
			}
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(uF.parseToInt(getF_strWLocation())>0){
	            sbQuery.append(" and wlocation_id="+uF.parseToInt(getF_strWLocation()));
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(uF.parseToInt(getF_department())>0){
				sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));			
			}
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}
			if (getF_employeType() != null && getF_employeType() != "") {
				sbQuery.append(" and emptype in ( '" + getF_employeType()+ "') ");
			}
			
			if((uF.parseToInt(getF_strWLocation())>0 || strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || uF.parseToInt(getF_department())>0 || uF.parseToInt(getF_level())>0 || (uF.parseToInt(getF_org())>0 || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null))){
				sbQuery.append(")");
			}
			pst = con.prepareStatement(sbQuery.toString());			
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, BONUS);			
//			System.out.println("Bonus pst==>"+pst); 
			rs = pst.executeQuery();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("BONUS_PAID", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("amount"))));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				if(rs.getDouble("amount")>0){
					hmEmpBonusMap.put(rs.getString("emp_id"), hmEmpInner);
				}
				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select emp_id, sum(amount) as amount from emp_salary_details " +
					"esd where salary_head_id in (select salary_head_id from salary_details " +
					"where earning_deduction = 'E') group by emp_id");
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpBonusMap.get(rs.getString("emp_id"));
//				if(hmEmpInner==null)hmEmpInner = new HashMap();
				if(hmEmpInner==null)continue;
				
				hmEmpInner.put("GROSS_AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("amount"))));
				hmEmpBonusMap.put(rs.getString("emp_id"), hmEmpInner);
				
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement(selectBonus2);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			
			Map hmBonusMap = new HashMap();
			
			while(rs.next()){
				
				Map hmInner = new HashMap();
				
				hmInner.put("MINIMUM_AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("bonus_minimum"))));
				hmInner.put("MAXIMUM_AMOUNT", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("bonus_maximum"))));
				hmInner.put("BONUS_RATE", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("bonus_amount"))));
				
				hmBonusMap.put(rs.getString("level_id"), hmInner);
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpBonusMap", hmEmpBonusMap);
			request.setAttribute("hmBonusMap", hmBonusMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpPanNo", hmEmpPanNo);
			request.setAttribute("hmEmpLevel", hmEmpLevel);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
			
			
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
	
	/*public String viewBonusReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		

		try {
			
			
			Map hmEmpName = CF.getEmpNameMap(null, null);
			Map hmEmpCode = CF.getEmpCodeMap();
			Map hmEmpLevel = CF.getEmpLevelMap();
			
			
			String[] strPayCycleDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {
				
				strPayCycleDates = getFinancialYear().split("-");
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = new FillFinancialYears().fillLatestFinancialYears();
				setFinancialYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strFinancialYearStart = strPayCycleDates[0];
				strFinancialYearEnd = strPayCycleDates[1];
				 
			}
			
			
			con = db.makeConnection(con);
			
			String strMonth=null;
			String strYear=null;
			Map hmEmpBonusMap = new HashMap();
			
			pst = con.prepareStatement("select emp_id, amount, month, year from payroll_generation where month=? and financial_year_from_date=? and financial_year_to_date=? and salary_head_id = ? and emp_id in (select emp_id from employee_official_details where org_id =?)");
			pst.setInt(1, uF.parseToInt(getStrMonth()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, BONUS);
			pst.setInt(5, uF.parseToInt(getF_org()));
			
			System.out.println("pst==>"+pst); 
			rs = pst.executeQuery();
			
			while(rs.next()){
				Map hmEmpInner = new HashMap();
				hmEmpInner.put("BONUS_PAID", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				strMonth = rs.getString("month");
				strYear = rs.getString("year");
				
				
				if(rs.getDouble("amount")>0){
					hmEmpBonusMap.put(rs.getString("emp_id"), hmEmpInner);
				}
				
			}
			
			
			pst = con.prepareStatement("select emp_id, sum(amount) as amount from  emp_salary_details esd where salary_head_id in (select salary_head_id from salary_details where earning_deduction = 'E') group by emp_id");
			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				
				Map hmEmpInner = (Map)hmEmpBonusMap.get(rs.getString("emp_id"));
//				if(hmEmpInner==null)hmEmpInner = new HashMap();
				if(hmEmpInner==null)continue;
				
				
				hmEmpInner.put("GROSS_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount"))));
				hmEmpBonusMap.put(rs.getString("emp_id"), hmEmpInner);
				
			}
			
			
			
			
			pst = con.prepareStatement(selectBonus2);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));		
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			
			rs = pst.executeQuery();
			
			Map hmBonusMap = new HashMap();
			
			while(rs.next()){
				
				Map hmInner = new HashMap();
				
				hmInner.put("MINIMUM_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("bonus_minimum"))));
				hmInner.put("MAXIMUM_AMOUNT", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("bonus_maximum"))));
				hmInner.put("BONUS_RATE", uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("bonus_amount"))));
				
				hmBonusMap.put(rs.getString("level_id"), hmInner);
				
			}
			
			
			
			
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmEmpBonusMap", hmEmpBonusMap);
			request.setAttribute("hmBonusMap", hmBonusMap);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpCode", hmEmpCode);
			request.setAttribute("hmEmpLevel", hmEmpLevel);
			request.setAttribute("strMonth", strMonth);
			request.setAttribute("strYear", strYear);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeConnection(con);
			db.closeStatements(pst);
			db.closeResultSet(rs);
		}
		return SUCCESS;

	}*/

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


	public String getStrMonth() {
		return strMonth;
	}


	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
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
