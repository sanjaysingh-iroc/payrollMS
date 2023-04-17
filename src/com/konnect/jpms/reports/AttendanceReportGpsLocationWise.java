package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycleDuration;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillPayMode;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.tms.ClockEntries;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AttendanceReportGpsLocationWise extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	// String strEmpId;
	private String strSelectedEmpId;
	private String strSessionEmpId;
	// String strPC;
	private String strD1;
	private String strD2;
	private String strUserType = null;
	private String level;
	private List<FillLevel> levelList;
	private String paycycle;
	private List<FillPayCycles> payCycleList;
	private List<FillEmployee> empNamesList;
	private List<FillOrganisation> orgList;	
	private String f_org;
	private String location;
	private List<FillWLocation> workLocationList;
	private String pageFrom;
	private static Logger log = Logger.getLogger(ClockEntries.class);
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;
		if(getF_org()==null || getF_org().trim().equals("")){
			setF_org((String)session.getAttribute(ORGID));
		}
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		request.setAttribute(PAGE, "/jsp/reports/AttendanceReportGpsLocationWise.jsp");
		request.setAttribute(TITLE, "GPS Location Tracker Report");
		
		UtilityFunctions uF = new UtilityFunctions();
		String[] strPayCycleDates = null;
		System.out.println("selcetdempid"+getStrSelectedEmpId());
		if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		} else {
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}	
		
		if(getPaycycle()!=null  && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
			strD1 = strPayCycleDates[0];
			strD2 = strPayCycleDates[1];
			String strPC = strPayCycleDates[2];
			getEmployeeswithlocationacess(strD1,strD2,uF);
			viewlocationreportofEmployee(uF);
		}
		loadClockEntries(uF);
		return LOAD; 

	}
	
	
	
	private void viewlocationreportofEmployee(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			List<Map<String, String>> emplist = new ArrayList<Map<String, String>>();
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select * from attendance_details ad inner join employee_personal_details epd on ad.emp_id = epd.emp_per_id  where epd.emp_per_id =? and in_out_timestamp between ? and ? and user_location!='null' order by atten_id ");
			pst.setInt(1, uF.parseToInt(getStrSelectedEmpId()));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			//System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				Map< String, String> hmClockEntrydata = new HashMap<String, String>();
				
				hmClockEntrydata.put("empCode",uF.showData(rs.getString("empcode"),""));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmClockEntrydata.put("empName",uF.showData(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"),""));
				hmClockEntrydata.put("date",uF.getDateFormat(rs.getString("in_out_timestamp"),DBDATE,DATE_FORMAT));
				hmClockEntrydata.put("rostertime",uF.getTimeFormatStr(rs.getString("in_out_timestamp"),DBTIMESTAMP,TIME_FORMAT));
				hmClockEntrydata.put("actualtime",uF.getTimeFormatStr(rs.getString("in_out_timestamp_actual"),DBTIMESTAMP,TIME_FORMAT));
				hmClockEntrydata.put("Type", rs.getString("in_out"));
				hmClockEntrydata.put("location",rs.getString("user_location"));
				emplist.add(hmClockEntrydata);
			}
			rs.close();
			pst.close();
			request.setAttribute("reportList", emplist);
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getEmployeeswithlocationacess(String strD1, String strD2,UtilityFunctions uF) {
		// TODO Auto-generated method stub
		if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) 
				|| strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(ACCOUNTANT) || strUserType.equalsIgnoreCase(MANAGER))) {
			empNamesList = new FillEmployee(request).fillEmployeeName(strD1,strD2, strSessionEmpId, strUserType);
			}
	}



	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if(strUserType != null && !strUserType.equals(MANAGER) && !strUserType.equals(EMPLOYEE)) {
			alFilter.add("ORGANISATION");
			if(getF_org()!=null) {
				String strOrg="";
				for(int i=0;orgList!=null && i<orgList.size();i++) {
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
			if(getLocation()!=null) {
				String strLocation="";
				for(int i=0;workLocationList!=null && i<workLocationList.size();i++) {
					if(getLocation().equals(workLocationList.get(i).getwLocationId())) {
						strLocation=workLocationList.get(i).getwLocationName();
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
			if(getLevel()!=null) {
				String strLevel="";
				for(int i=0;levelList!=null && i<levelList.size();i++) {
					if(getLevel().equals(levelList.get(i).getLevelId())) {
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
		}
		
		alFilter.add("PAYCYCLE");
		String strPaycycle = "";
		String[] strPayCycleDates = null;
		if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			
			strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
		}
		hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		if(strUserType != null && !strUserType.equals(EMPLOYEE)) {
			alFilter.add("EMP");
			if(getStrSelectedEmpId()!=null) {
				String strEmpName = "";
				for(int i=0;empNamesList!=null && i<empNamesList.size();i++) {
					if(getStrSelectedEmpId().equals(empNamesList.get(i).getEmployeeId())) {
						strEmpName=empNamesList.get(i).getEmployeeName();
//						System.out.println("strEmpName ===>> " + strEmpName);
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
		}
		
		String selectedFilter = CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	public String loadClockEntries(UtilityFunctions uF) {		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			workLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			orgList = new FillOrganisation(request).fillOrganisation();
			workLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		payCycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());  
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		getSelectedFilter(uF);
		return LOAD;
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

	public List<FillPayCycles> getPayCycleList() {
		return payCycleList;
	}

	public void setPayCycleList(List<FillPayCycles> payCycleList) {
		this.payCycleList = payCycleList;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
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

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public List<FillWLocation> getWorkLocationList() {
		return workLocationList;
	}

	public void setWorkLocationList(List<FillWLocation> workLocationList) {
		this.workLocationList = workLocationList;
	}

	public String getPageFrom() {
		return pageFrom;
	}

	public void setPageFrom(String pageFrom) {
		this.pageFrom = pageFrom;
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
}