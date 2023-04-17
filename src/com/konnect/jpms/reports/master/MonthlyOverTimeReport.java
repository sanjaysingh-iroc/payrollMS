package com.konnect.jpms.reports.master;

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
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MonthlyOverTimeReport extends ActionSupport implements ServletRequestAware, IStatements{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF=null;
	String strUserType = null;
	String strUserTypeId = null; 
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null; 
 	
	String alertStatus;
	String alert_type;
	
	String strStartDate;
	String strEndDate;
	
	String strMonth;
	String calendarYear;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	
	String f_org;
	String[] f_wLocation; 
	String[] f_level;
	String[] f_department;
	String[] f_service;
	List<FillMonth> monthList;
	List<FillCalendarYears> calendarYearList;
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	 
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions(); 
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
			  
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PMonthlyOverTimeReport);
		request.setAttribute(TITLE, "Monthly OverTime Report");
		
		if(getF_org()==null || getF_org().trim().equals("")){
			setF_org((String)session.getAttribute(ORGID));
		}

		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_wLocation(getStrLocation().split(","));
		} else {
			setF_wLocation(null);
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
		
		if(getStrStartDate()!=null && getStrStartDate().equalsIgnoreCase("NULL")){
			setStrStartDate(null);
			setStrEndDate(null);
		}
		
		if(getStrEndDate()!=null && getStrEndDate().equalsIgnoreCase("NULL")){
			setStrStartDate(null);
			setStrEndDate(null);
		}
		
		viewMonthlyOverTimeReport(uF);
		
		return loadMonthlyOverTimeReport(uF);

	}
	 
	private void viewMonthlyOverTimeReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		StringBuilder sb = new StringBuilder();
		
		try {

			con=db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
			Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
			if(hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
			Map<String, String> hmDepartment =CF.getDepartmentMap(con, null, null);
			if(hmDepartment == null) hmDepartment = new HashMap<String, String>();
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			if(hmEmpInfo == null) hmEmpInfo = new HashMap<String, Map<String,String>>();
		
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			Map<String, String> hmEmpDepartmentMap = CF.getEmpDepartmentMap(con);
			
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
			
			StringBuilder sbQuery=new StringBuilder();

			sbQuery.append("select * from overtime_hours ot, employee_personal_details epd, employee_official_details eod where eod.emp_id=ot.emp_id and ot.emp_id=epd.emp_per_id and eod.emp_id=epd.emp_per_id");
				sbQuery.append("  and ot._date between ? and ?");
			
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
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
            
            if(getF_wLocation()!=null && getF_wLocation().length>0){
                sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_wLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append("order by emp_fname,emp_lname");
			pst=con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			rs=pst.executeQuery();	 
			
			alInnerExport.add(new DataStyle("Monthly OverTime Report",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Id",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Department",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Manager's Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("00:00:00-02:00:00 OT Hours",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("02:00:00-04:00:00 OT Hours",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("04:00:00-06:00:00 OT Hours",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("06:00:00-08:00:00 OT Hours",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			reportListExport.add(alInnerExport);
			
			
			Map<String, Map<String, String>> hmEmp = new HashMap<String, Map<String,String>>();
			List<String> alEmp = new ArrayList<String>();
			Map<String, String> hm0To2 = new HashMap<String, String>();
			Map<String, String> hm2To4 = new HashMap<String, String>();
			Map<String, String> hm4To6 = new HashMap<String, String>();
			Map<String, String> hm6To8 = new HashMap<String, String>();
			while(rs.next()){
				
				if(!alEmp.contains(rs.getString("emp_id"))){
					alEmp.add(rs.getString("emp_id")); 
				}
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("EMP_ID", rs.getString("emp_id"));
				hmInner.put("EMP_CODE", rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmInner.put("EMP_NAME", rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				hmInner.put("EMP_DEPARTMENT",hmDepartment.get(rs.getString("depart_id")));
				hmInner.put("EMP_SUPERVISOR",hmEmployeeNameMap.get(rs.getString("supervisor_emp_id")));
				
				hmEmp.put(rs.getString("emp_id"), hmInner);
				
				String otHours =rs.getString("approved_ot_hours");
				
				if(0 < uF.parseToDouble(otHours) && uF.parseToDouble(otHours) <= 2){
					int count1 = uF.parseToInt(hm0To2.get(rs.getString("emp_id")));
					count1++;
					hm0To2.put(rs.getString("emp_id"),""+count1);
				} else if (2 < uF.parseToDouble(otHours) && uF.parseToDouble(otHours) <= 4){
					int count2 = uF.parseToInt(hm2To4.get(rs.getString("emp_id")));
					count2++;
					hm2To4.put(rs.getString("emp_id"),""+count2);
				} else if (4 < uF.parseToDouble(otHours) && uF.parseToDouble(otHours) <= 6){
					int count3 = uF.parseToInt(hm4To6.get(rs.getString("emp_id")));
					count3++;
					hm4To6.put(rs.getString("emp_id"),""+count3);
				} else if(6 < uF.parseToDouble(otHours) && uF.parseToDouble(otHours) <= 8){
					int count4 = uF.parseToInt(hm6To8.get(rs.getString("emp_id")));
					count4++;
					hm6To8.put(rs.getString("emp_id"),""+count4);
				}
				
			}
			rs.close();
			pst.close();
			
			int nEmpSize = alEmp.size();
			for(int i=0; i<nEmpSize; i++){
				String strEmpId = alEmp.get(i);
				Map<String, String> hmInner = hmEmp.get(strEmpId);
				List<String> alInner = new ArrayList<String>();
				alInnerExport = new ArrayList<DataStyle>();
				
				alInner.add(uF.showData(hmInner.get("EMP_CODE"), ""));
				alInner.add(uF.showData(hmInner.get("EMP_NAME"), ""));
				alInner.add(uF.showData(hmInner.get("EMP_DEPARTMENT"), ""));
				alInner.add(uF.showData(hmInner.get("EMP_SUPERVISOR"), ""));
				
				alInner.add(uF.showData(hm0To2.get(strEmpId), "0"));
				alInner.add(uF.showData(hm2To4.get(strEmpId), "0"));
				alInner.add(uF.showData(hm4To6.get(strEmpId), "0"));
				alInner.add(uF.showData(hm6To8.get(strEmpId), "0"));
				

				alInnerExport.add(new DataStyle(""+uF.showData(hmInner.get("EMP_CODE"), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.showData(hmInner.get("EMP_NAME"), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.showData(hmInner.get("EMP_DEPARTMENT"), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.showData(hmInner.get("EMP_SUPERVISOR"), ""),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.showData(hm0To2.get(strEmpId), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.showData(hm2To4.get(strEmpId), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.showData(hm4To6.get(strEmpId), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				alInnerExport.add(new DataStyle(""+uF.showData(hm6To8.get(strEmpId), "0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
		
			
				reportList.add(alInner);
				reportListExport.add(alInnerExport);
				
			}
			
			request.setAttribute("reportList", reportList);
			session.setAttribute("reportListExport", reportListExport);
					
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
}

	public String loadMonthlyOverTimeReport(UtilityFunctions uF){	
		monthList = new FillMonth().fillMonth();
		calendarYearList = new FillCalendarYears(request).fillCalendarYears(CF);
		
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
		
		return "load";
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++) {
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
		
		alFilter.add("LOCATION");
		if(getF_wLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_wLocation().length;j++) {
					if(getF_wLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
					}
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
			int k=0;
			for(int i=0;departmentList!=null && i<departmentList.size();i++) {
				for(int j=0;j<getF_department().length;j++) {
					if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if(k==0) {
							strDepartment=departmentList.get(i).getDeptName();
						} else {
							strDepartment+=", "+departmentList.get(i).getDeptName();
						}
						k++;
					}
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
		
		alFilter.add("SERVICE");
		if(getF_service()!=null) {
			String strService="";
			int k=0;
			for(int i=0;serviceList!=null && i<serviceList.size();i++) {
				for(int j=0;j<getF_service().length;j++) {
					if(getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if(k==0) {
							strService=serviceList.get(i).getServiceName();
						} else {
							strService+=", "+serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strService!=null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All SBUs");
			}
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				for(int j=0;j<getF_level().length;j++) {
					if(getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if(k==0) {
							strLevel=levelList.get(i).getLevelCodeName();
						} else {
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
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
		
		alFilter.add("MONTH");
		int nselectedMonth = uF.parseToInt(getStrMonth());
		String strMonth = uF.getMonth(nselectedMonth);
		hmFilter.put("MONTH", strMonth);
		
		
		alFilter.add("CALENDARYEAR");
		String[] strCalendarYearDates = null;
		if (getCalendarYear() != null) {
			strCalendarYearDates = getCalendarYear().split("-");
			setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
		} else {
			strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
			setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
		}
		hmFilter.put("CALENDARYEAR", uF.getDateFormat(strCalendarYearDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strCalendarYearDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}


	public String getStrStartDate() {
		return strStartDate;
	}


	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}


	public String getStrEndDate() {
		return strEndDate;
	}


	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
	}


	public String getAlertStatus() {
		return alertStatus;
	}


	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}


	public String getAlert_type() {
		return alert_type;
	}


	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_wLocation() {
		return f_wLocation;
	}

	public void setF_wLocation(String[] f_wLocation) {
		this.f_wLocation = f_wLocation;
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

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillCalendarYears> getCalendarYearList() {
		return calendarYearList;
	}

	public void setCalendarYearList(List<FillCalendarYears> calendarYearList) {
		this.calendarYearList = calendarYearList;
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
