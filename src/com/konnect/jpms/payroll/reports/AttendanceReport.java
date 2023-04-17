package com.konnect.jpms.payroll.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AttendanceReport extends ActionSupport implements ServletRequestAware, IStatements {
 
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null; 
	String strUserType;  
	String strSessionEmpId;      
	
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	
	private String paycycle;
	private String f_org;
	private String []f_strWLocation;
	private String []f_level;
	private String []f_department;
	private String []f_service; 
	
	private List<FillPayCycles> paycycleList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	private List<FillWLocation> wLocationList;
	private List<FillOrganisation> organisationList;
	
	private String pageFrom;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/payroll/reports/AttendanceReport.jsp");
		request.setAttribute(TITLE, "Clock Entries Report");  
		
		if(getF_org()==null || getF_org().trim().equals("")){
			setF_org((String)session.getAttribute(ORGID));
		}

		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
		String[] strPayCycleDates = null;
		if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		} else {
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}
		
		String strD1 = strPayCycleDates[0];
		String strD2 = strPayCycleDates[1];
		String strPC = strPayCycleDates[2];
		
		String formType = (String) request.getParameter("formType");
		if(formType != null && formType.trim().equalsIgnoreCase("revoke")){
			revokeandOpenClockEntries(uF, strD1, strD2, strPC);
		}
		
		viewApprovePay(uF, strD1, strD2, strPC);
		
		return loadApprovePay(uF);
	}
	
	private void revokeandOpenClockEntries(UtilityFunctions uF, String strD1, String strD2, String strPC) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			String[] revokeEmpId = request.getParameterValues("revokeEmpId");
			int nEmpIds = revokeEmpId!=null ? revokeEmpId.length : 0;

			if(nEmpIds > 0){
				List<String> alEmp = new ArrayList<String>();
				for(int i = 0; i < nEmpIds; i++){
					if(uF.parseToInt(revokeEmpId[i]) > 0){
						if(!alEmp.contains(revokeEmpId[i])){
							alEmp.add(revokeEmpId[i]);
						}
					}
				}
				if(alEmp.size() > 0){
					String strEmpIds = StringUtils.join(alEmp.toArray(),",");
					pst = con.prepareStatement("delete from approve_attendance where approve_from >=? and approve_to <=? and emp_id in ("+strEmpIds+")");
					pst.setDate(1,  uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(2,  uF.getDateFormat(strD2, DATE_FORMAT));
					int x = pst.executeUpdate();
					if(x > 0){
						session.setAttribute(MESSAGE, SUCCESSM+"You have successfully Revoke & Open Time Entries."+END);
					} else {
						session.setAttribute(MESSAGE, ERRORM+"Colud not Revoke & Open Time Entries. Please,try again."+END);
					}
				} else {
					session.setAttribute(MESSAGE, ERRORM+"Colud not Revoke & Open Time Entries. Please,try again."+END);
				}
			}
		} catch (Exception e) {
			session.setAttribute(MESSAGE, ERRORM+"Colud not Revoke & Open Time Entries. Please,try again."+END);
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void viewApprovePay(UtilityFunctions uF, String strD1, String strD2, String strPC) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String []strFinancialYear = CF.getFinancialYear(con, strD2, CF, uF);
			if(strFinancialYear!=null){
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			Map<String, String> hmPaymentModeMap = CF.getPaymentMode();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from approve_attendance at, employee_personal_details epd, employee_official_details eod where at.emp_id > 0 " +
					"and epd.emp_per_id = eod.emp_id and epd.emp_per_id = at.emp_id and at.emp_id = eod.emp_id and at.approve_from>=? " +
					"and at.approve_to<=? and epd.joining_date<=? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?))");
					
			
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            }
			
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){ 
                sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and eod.org_id in ("+session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" and eod.emp_id not in (select emp_id from payroll_generation where financial_year_from_date=? and financial_year_to_date =? " +
					"and paid_from = ? and paid_to=? group by emp_id) order by emp_fname, emp_lname"); 
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setDate(7, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(9,  uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(10,  uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();
			List<Map<String,String>> alEmp = new ArrayList<Map<String,String>>(); 
			List<String> alEmpIds = new ArrayList<String>();
			while (rs.next()){
				Map<String, String> hmEmpPay = new HashMap<String, String>();
				hmEmpPay.put("EMP_ID", rs.getString("emp_id"));
				hmEmpPay.put("EMPCODE", rs.getString("empcode"));

				//String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " +rs.getString("emp_lname");
				hmEmpPay.put("EMP_NAME", strEmpName);
				hmEmpPay.put("EMP_PAYMENT_MODE_ID", rs.getString("payment_mode"));
				hmEmpPay.put("EMP_PAYMENT_MODE", uF.showData(hmPaymentModeMap.get(rs.getString("payment_mode")),""));
				hmEmpPay.put("EMP_BIRTH_DATE", uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, DATE_FORMAT));
				hmEmpPay.put("EMP_JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, DATE_FORMAT));
				
				if(rs.getString("employment_end_date")!=null){
					hmEmpPay.put("EMP_END_DATE", uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));	
				}
				hmEmpPay.put("EMP_GENDER", rs.getString("emp_gender"));
				String strDays = uF.dateDifference(rs.getString("emp_date_of_birth"), DBDATE, uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE,CF.getStrTimeZone());
				double dblYears = uF.parseToDouble(strDays) / 365;
				hmEmpPay.put("EMP_AGE", dblYears + ""); 
				
				hmEmpPay.put("EMP_APPROVE_ATTENDANCE_ID", rs.getString("approve_attendance_id"));
				hmEmpPay.put("EMP_TOTAL_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("total_days"))));
				hmEmpPay.put("EMP_PAID_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("paid_days"))));
				hmEmpPay.put("EMP_PRESENT_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("present_days"))));
				hmEmpPay.put("EMP_PAID_LEAVES", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("paid_leaves"))));
				hmEmpPay.put("EMP_ABSENT_DAYS", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("absent_days"))));

				if(rs.getString("service_id")!=null) {
					String[] tempService = rs.getString("service_id").split(","); 
					if(tempService.length > 0){
						hmEmpPay.put("EMP_SERVICE_ID", tempService[0]);
					}
				}
				
				alEmp.add(hmEmpPay);
				
				if(!alEmpIds.contains(rs.getString("emp_id"))){
					alEmpIds.add(rs.getString("emp_id"));
				}				
			}
			rs.close();
			pst.close();
			
//			System.out.println("alEmp====>"+alEmp);
			request.setAttribute("alEmp", alEmp);
			request.setAttribute("strD1", strD1);
			request.setAttribute("strD2", strD2);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private String loadApprovePay(UtilityFunctions uF) {
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("PAYCYCLE");	
		if(getPaycycle()!=null){
			String strPayCycle="";
			int k=0;
			for(int i=0;paycycleList!=null && i<paycycleList.size();i++){
				if(getPaycycle().equals(paycycleList.get(i).getPaycycleId())){
					if(k==0){
						strPayCycle=paycycleList.get(i).getPaycycleName();
					}else{
						strPayCycle+=", "+paycycleList.get(i).getPaycycleName();
					}
					k++;
				}
			}
			if(strPayCycle!=null && !strPayCycle.equals("")){
				hmFilter.put("PAYCYCLE", strPayCycle);
			}else{
				hmFilter.put("PAYCYCLE", "All Paycycle");
			}
		}
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null){			
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++){
				if(getF_org().equals(organisationList.get(i).getOrgId())){
					if(k==0){
						strOrg=organisationList.get(i).getOrgName();
					}else{
						strOrg+=", "+organisationList.get(i).getOrgName();
					}
					k++;
				}
			}
			if(strOrg!=null && !strOrg.equals("")){
				hmFilter.put("ORGANISATION", strOrg);
			}else{
				hmFilter.put("ORGANISATION", "All Organisation");
			}
		}else{
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		alFilter.add("LOCATION");
		if(getF_strWLocation()!=null){
			String strLocation=""; 
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++){
				for(int j=0;j<getF_strWLocation().length;j++){
					if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())){
						if(k==0){
							strLocation=wLocationList.get(i).getwLocationName();
						}else{
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if(strLocation!=null && !strLocation.equals("")){
				hmFilter.put("LOCATION", strLocation);
			}else{
				hmFilter.put("LOCATION", "All Locations");
			}
		}else{
			hmFilter.put("LOCATION", "All Locations");
		}
		
		alFilter.add("DEPARTMENT");
		if(getF_department()!=null){
			String strDepartment="";
			int k=0;
			for(int i=0;departmentList!=null && i<departmentList.size();i++){
				for(int j=0;j<getF_department().length;j++){
					if(getF_department()[j].equals(departmentList.get(i).getDeptId())){
						if(k==0){
							strDepartment=departmentList.get(i).getDeptName();
						}else{
							strDepartment+=", "+departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if(strDepartment!=null && !strDepartment.equals("")){
				hmFilter.put("DEPARTMENT", strDepartment);
			}else{
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		}else{
			hmFilter.put("DEPARTMENT", "All Departments");
		}
		
		alFilter.add("SERVICE");
		if(getF_service()!=null){
			String strService="";
			int k=0;
			for(int i=0;serviceList!=null && i<serviceList.size();i++){
				for(int j=0;j<getF_service().length;j++){
					if(getF_service()[j].equals(serviceList.get(i).getServiceId())){
						if(k==0){
							strService=serviceList.get(i).getServiceName();
						}else{
							strService+=", "+serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strService!=null && !strService.equals("")){
				hmFilter.put("SERVICE", strService);
			}else{
				hmFilter.put("SERVICE", "All Services");
			}
		}else{
			hmFilter.put("SERVICE", "All Services");
		}
		
		alFilter.add("LEVEL");
		if(getF_level()!=null){
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++){
				for(int j=0;j<getF_level().length;j++){
					if(getF_level()[j].equals(levelList.get(i).getLevelId())){
						if(k==0){
							strLevel=levelList.get(i).getLevelCodeName();
						}else{
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
				}
			}
			if(strLevel!=null && !strLevel.equals("")){
				hmFilter.put("LEVEL", strLevel);
			}else{
				hmFilter.put("LEVEL", "All Levels");
			}
		}else{
			hmFilter.put("LEVEL", "All Levels");
		}
		
		String selectedFilter=CF.getSelectedFilter(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
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

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrSbu() {
		return strSbu;
	}

	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}
	
}
