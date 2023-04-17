package com.konnect.jpms.itforms;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class Form3A extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	
	String strSessionEmpId;
	
	String strSubmit; 
	String financialYear;
	String strSelectedEmpId;
	List<FillFinancialYears> financialYearList; 
	List<FillEmployee> empNamesList;
	
	
	String f_strWLocation;
	String f_level;
	String f_org;
	
	List<FillOrganisation> orgList;	
	List<FillWLocation> wLocationList;
	List<FillLevel> levelList;
	
	
	public String execute() throws Exception {
		
				
		request.setAttribute(PAGE, PForm3A);
		request.setAttribute(TITLE, TForm3A);
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		viewForm3A(uF);
		
		return loadForm3A(uF);
	}
	
	
	public String loadForm3A(UtilityFunctions uF){
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		orgList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		empNamesList=getEmployeeList(uF);
		
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
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=orgList.get(i).getOrgName();
					} else {
						strOrg+=", "+orgList.get(i).getOrgName();
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
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
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
		
		alFilter.add("EMP");
		if(getStrSelectedEmpId()!=null) {
			String strEmpName="";
			for(int i=0;empNamesList!=null && i<empNamesList.size();i++) {
				if(getStrSelectedEmpId().equals(empNamesList.get(i).getEmployeeId())) {
					strEmpName=empNamesList.get(i).getEmployeeCode();
				}
			}
			if(strEmpName!=null && !strEmpName.equals("")) {
				hmFilter.put("EMP", strEmpName);
			} else {
				hmFilter.put("EMP", "Select Employee");
			}
		} else {
			hmFilter.put("EMP", "Select Employee");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
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
			
			if(uF.parseToInt(getF_level())>0){
				sbQuery.append(" and eod.grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
			}			
			
			sbQuery.append(" order by epd.emp_fname");
			
			
			pst = con.prepareStatement(sbQuery.toString());
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
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
	
public String viewForm3A(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
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
			
			
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "dd")));
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "MM")) -1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, "yyyy")));
			
			List<String> alMonth = new ArrayList<String>();
			Map<String, String> hmMonthDayCount = new HashMap<String, String>();
			for(int i=0; i<12; i++){
				
//				System.out.println("=====>>>"+uF.getDateFormat(""+(cal.get(Calendar.MONTH)+1),"MM","MM"));
				alMonth.add(uF.getDateFormat(""+(cal.get(Calendar.MONTH)+1),"MM","MM"));
				
//				System.out.println("DATE=====>>>"+(cal.get(Calendar.DATE))+" month=====>>>"+(cal.get(Calendar.MONTH)+1)+" year=====>>>"+(cal.get(Calendar.YEAR)));
				

				int iDay = cal.get(Calendar.DATE);
				int iMonth = cal.get(Calendar.MONTH);
				int iYear = cal.get(Calendar.YEAR);

				// Create a calendar object and set year and month
				Calendar mycal = new GregorianCalendar(iYear, iMonth, iDay);

				// Get the number of days in that month
				int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
				
				hmMonthDayCount.put(""+(iMonth+1), ""+daysInMonth);
				
				cal.add(Calendar.MONTH, 1);
				
			}
			
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmployeeDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from emp_family_members where emp_id = ?");
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
			rs= pst.executeQuery();
			while(rs.next()){
				if(rs.getString("member_type")!=null && rs.getString("member_type").equalsIgnoreCase("SPOUSE")){
					hmEmployeeDetails.put("SPOUSE", rs.getString("member_name"));
				}else if(rs.getString("member_type")!=null && rs.getString("member_type").equalsIgnoreCase("FATHER")){
					hmEmployeeDetails.put("FATHER", rs.getString("member_name"));
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from employee_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
			rs = pst.executeQuery();
			while(rs.next()){
				hmEmployeeDetails.put("EPF_ACC_NO", rs.getString("emp_pf_no"));
				
				
				String strMiddleName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				hmEmployeeDetails.put("NAME", rs.getString("emp_fname")+strMiddleName+" "+rs.getString("emp_lname"));
				if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("M")){
					hmEmployeeDetails.put("FATHER_SPOUSE", hmEmployeeDetails.get("FATHER"));
				}else if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("F") && rs.getString("marital_status")!=null && rs.getString("marital_status").equalsIgnoreCase("M")){
					hmEmployeeDetails.put("FATHER_SPOUSE", hmEmployeeDetails.get("SPOUSE"));
				}else{
					hmEmployeeDetails.put("FATHER_SPOUSE", hmEmployeeDetails.get("FATHER"));
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from epf_details where financial_year_start=? and financial_year_end =? " +
					"and org_id in (select org_id from employee_official_details where emp_id=?) and level_id in (select ld.level_id from grades_details gd, " +
					"level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  " +
					"and gd.grade_id in (select grade_id from employee_official_details where emp_id=? ))");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrSelectedEmpId()));	
			pst.setInt(4, uF.parseToInt(getStrSelectedEmpId()));
//			System.out.println("pst======>"+pst); 
			rs = pst.executeQuery();
			Map<String, String> hmEPFMap = new HashMap<String, String>();
			while(rs.next()){
				hmEPFMap.put("EEPF_CONTRIBUTION", ""+uF.parseToDouble(rs.getString("eepf_contribution")));
				hmEPFMap.put("EPF_MAX_LIMIT", ""+uF.parseToDouble(rs.getString("epf_max_limit")));
				hmEPFMap.put("ERPF_CONTRIBUTION", ""+uF.parseToDouble(rs.getString("erpf_contribution")));
				hmEPFMap.put("ERPS_CONTRIBUTION", ""+uF.parseToDouble(rs.getString("erps_contribution")));
				hmEPFMap.put("ERDLI_CONTRIBUTION", ""+uF.parseToDouble(rs.getString("erdli_contribution")));
				hmEPFMap.put("PF_ADMIN_CHARGES", ""+uF.parseToDouble(rs.getString("pf_admin_charges")));
				hmEPFMap.put("EDLI_ADMIN_CHARGES", ""+uF.parseToDouble(rs.getString("edli_admin_charges")));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_epf_details where financial_year_start=? and financial_year_end=? " +
					"and emp_id in (select emp_id from challan_details where emp_id=? and challan_type in ("+ EMPLOYEE_EPF + "," + EMPLOYER_EPF + ") " +
					"and is_paid = true and financial_year_from_date=? and financial_year_to_date=?)");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(getStrSelectedEmpId()));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmDetails = new HashMap<String, Map<String, String>>();
			double dblTotalAmountWages = 0;
			double dblTotalEmployeeAmount = 0;
			double dblTotalEmployerEPFAmount = 0;
			double dblTotalEmployerEPSAmount = 0;
//			System.out.println("pst==>"+pst);
			while(rs.next()){
				
				String month = rs.getString("_month")!=null && uF.parseToInt(rs.getString("_month"))<=9 ? "0"+rs.getString("_month") : rs.getString("_month");
				
				Map<String, String> hmInner = (Map<String, String>)hmDetails.get(month);
				if(hmInner==null) hmInner = new HashMap<String, String>();
				
				hmInner.put("AMOUNT_WAGES", uF.formatIntoTwoDecimal(rs.getDouble("epf_max_limit")));
				dblTotalAmountWages += rs.getDouble("epf_max_limit");
				
				hmInner.put("EMPLOYEE_CONTRIBUTION", uF.formatIntoTwoDecimal(rs.getDouble("eepf_contribution")));
				dblTotalEmployeeAmount += rs.getDouble("eepf_contribution");
				
				
				hmInner.put("EMPLOYER_DIFF_SHARE", uF.formatIntoTwoDecimal(rs.getDouble("eepf_contribution") - rs.getDouble("erps_contribution")));
				dblTotalEmployerEPFAmount += (rs.getDouble("eepf_contribution") - rs.getDouble("erps_contribution"));
				
				hmInner.put("EMPLOYER_SHARE_EPS", uF.formatIntoTwoDecimal(rs.getDouble("erps_contribution")));
				dblTotalEmployerEPSAmount += rs.getDouble("erps_contribution");
				
				hmDetails.put(month, hmInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("dblTotalAmountWages", uF.formatIntoTwoDecimal(dblTotalAmountWages));   
			request.setAttribute("dblTotalEmployeeAmount", uF.formatIntoTwoDecimal(dblTotalEmployeeAmount));
			request.setAttribute("dblTotalEmployerEPFAmount", uF.formatIntoTwoDecimal(dblTotalEmployerEPFAmount));
			request.setAttribute("dblTotalEmployerEPSAmount", uF.formatIntoTwoDecimal(dblTotalEmployerEPSAmount));
			
			
			pst = con.prepareStatement("select paid_days,month from payroll_generation where emp_id=? and is_paid = true " +
					"and financial_year_from_date=? and financial_year_to_date=? group by paid_days,month  order by paid_days,month");
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> hmEmpPaidDays = new HashMap<String, String>();
			while(rs.next()){
				hmEmpPaidDays.put(rs.getString("month"), rs.getString("paid_days"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpAbsentDays = new HashMap<String, String>();
			Iterator<String> it =hmMonthDayCount.keySet().iterator();
			while(it.hasNext()){
				String strMonth = it.next();
				String strDays = hmMonthDayCount.get(strMonth);
				
				double strEmpPaidDays = uF.parseToDouble(hmEmpPaidDays.get(strMonth));
				
				double dblAbsent = uF.parseToDouble(strDays) - strEmpPaidDays;
				if(dblAbsent==uF.parseToDouble(strDays)){
					dblAbsent=0.0d;
				}
				dblAbsent = dblAbsent>0.0d ? dblAbsent: 0.0d;
				
				String months = uF.parseToInt(strMonth)>9 ? strMonth : "0"+strMonth;
				hmEmpAbsentDays.put(months, ""+dblAbsent);
			}
			
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
			request.setAttribute("hmDetails", hmDetails);
			request.setAttribute("hmEmployeeDetails", hmEmployeeDetails);
			request.setAttribute("alMonth", alMonth);
			request.setAttribute("hmEmpAbsentDays", hmEmpAbsentDays);
			request.setAttribute("hmEPFMap", hmEPFMap);
			
			
		} catch (Exception e) {  
			e.printStackTrace();
		}finally{
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

	public String getStrSubmit() {
		return strSubmit;
	}

	public void setStrSubmit(String strSubmit) {
		this.strSubmit = strSubmit;
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


	public String getStrSelectedEmpId() {
		return strSelectedEmpId;
	}


	public void setStrSelectedEmpId(String strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}


	public List<FillEmployee> getEmpNamesList() {
		return empNamesList;
	}


	public void setEmpNamesList(List<FillEmployee> empNamesList) {
		this.empNamesList = empNamesList;
	}


	public String getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
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


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

}
