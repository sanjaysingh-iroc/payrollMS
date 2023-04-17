package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;

import com.konnect.jpms.leave.LeaveApprovedReport;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class DailyOverTimeReport extends ActionSupport implements ServletRequestAware, IStatements {

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
	private static Logger log = Logger.getLogger(LeaveApprovedReport.class);
	
	String alertStatus;
	String alert_type;
	
	String strStartDate;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String strGrade;
	String strEmployeType;
	
	String paycycle;
	String f_org;
	String[] f_strWLocation; 
	String[] f_level;
	String[] f_department;
	String[] f_service;
	String[] f_grade;
	String[] f_employeType;
	
	List<FillPayCycles> paycycleList;
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	
	List<FillGrade> gradeList;
	List<FillEmploymentType> employementTypeList;
	
	String pageNumber;
	String minLimit;
	
	String strSearch;
	 
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
			  
		UtilityFunctions uF = new UtilityFunctions(); 
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PDailyOverTimeReport);
		request.setAttribute(TITLE, "Daily OverTime Report");
		
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
		
		if(getStrEmployeType() !=null && !getStrEmployeType().equals("")){
			setF_employeType(getStrEmployeType().split(","));
			System.out.println("====>"+getF_employeType().length);
		}else{
			setF_employeType(null);
		}
		
		
		if(getStrGrade() !=null && !getStrGrade().equals("")){
			setF_grade(getStrGrade().split(","));
		}else{
			setF_grade(null);
		}
		
		if(f_level!=null)
		{
			
			String level_id ="";
			for (int i = 0; i < f_level.length; i++) {
				
				if(i==0)
				{
					level_id = f_level[i];
					level_id.concat(f_level[i]);
				}else{
					level_id =level_id+","+f_level[i];
				}
			}
			
			gradeList = new FillGrade(request).fillGrade(level_id,getF_org());
		}else{
			gradeList = new FillGrade(request).fillGradebyorganisation(getF_org());
		}
		
		if (uF.parseToInt(getPageNumber()) == 0) {
			setPageNumber("1");
		}
		
		viewOverTimeHours(uF);
		getSearchAutoCompleteData(uF);
		
		return loadDailyOverTimeReport(uF);

	}
	 
	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			SortedSet<String> setSearchList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true ");
			if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))){
				sbQuery.append("and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			}
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0)
            {
            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
            }else {
            	 if(getF_level()!=null && getF_level().length>0){
                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
                 }
            	 if(getF_grade()!=null && getF_grade().length>0){
                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
                 }
			}
            
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
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
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				setSearchList.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
			while (it.hasNext()){
				String strData = it.next();
				if(sbData == null){
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null){
				sbData = new StringBuilder();
			}
			request.setAttribute("sbData", sbData.toString());
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadDailyOverTimeReport(UtilityFunctions uF){	
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		employementTypeList = new FillEmploymentType().fillEmploymentType(request);
		
		if(getStrStartDate()==null || getStrStartDate().equalsIgnoreCase(" ")){
			setStrStartDate(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT));
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
		if(getF_strWLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_strWLocation().length;j++) {
					if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
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
		
		alFilter.add("GRADE");
		if (getF_grade() != null) {
			String strgrade = "";
			int k = 0;
			for (int i = 0; gradeList != null && i < gradeList.size(); i++) {
				for (int j = 0; j < getF_grade().length; j++) {
					if (getF_grade()[j].equals(gradeList.get(i).getGradeId())) {
						if (k == 0) {
							strgrade = gradeList.get(i).getGradeCode();
						} else {
							strgrade += ", " + gradeList.get(i).getGradeCode();
						}
						k++;
					}
				}
			}
			if (strgrade != null && !strgrade.equals("")) {
				hmFilter.put("GRADE", strgrade);
			} else {
				hmFilter.put("GRADE", "All Grade's");
			}
		} else {
			hmFilter.put("GRADE", "All Grade's");
		}
		
		/*if(getStrStartDate()!=null && !getStrStartDate().equals("")) {
			alFilter.add("DATE");
			hmFilter.put("DATE", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}*/
		
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
		
		alFilter.add("GRADE");
		if (getF_grade() != null) {
			String strgrade = "";
			int k = 0;
			for (int i = 0; gradeList != null && i < gradeList.size(); i++) {
				for (int j = 0; j < getF_grade().length; j++) {
					if (getF_grade()[j].equals(gradeList.get(i).getGradeId())) {
						if (k == 0) {
							strgrade = gradeList.get(i).getGradeCode();
						} else {
							strgrade += ", " + gradeList.get(i).getGradeCode();
						}
						k++;
					}
				}
			}
			if (strgrade != null && !strgrade.equals("")) {
				hmFilter.put("GRADE", strgrade);
			} else {
				hmFilter.put("GRADE", "All Grade's");
			}
		} else {
			hmFilter.put("GRADE", "All Grade's");
		}
		
		
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

		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	public void viewOverTimeHours(UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			String[] strPayCycleDates = null;

			if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, getF_org());
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}
			
			String strD1 = strPayCycleDates[0];
			String strD2 = strPayCycleDates[1];
			String strPC = strPayCycleDates[2]; 
			
			List<String> alDates = new ArrayList<String>();
			String s = uF.getDateFormat(""+uF.getDateFormat(strD1, DATE_FORMAT), DBDATE, DBDATE);
			String e = uF.getDateFormat(""+uF.getDateFormat(strD2, DATE_FORMAT), DBDATE, DBDATE);
			LocalDate start = LocalDate.parse(s);
			LocalDate end = LocalDate.parse(e);
			while (!start.isAfter(end)) {
				alDates.add(uF.getDateFormat(""+start, DBDATE, DATE_FORMAT));
			    start = start.plusDays(1);
			}			
			
			
			Map hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmployees  = new ArrayList<String>();
			List alServices  = new ArrayList();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(eod.emp_id) as empCount from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id and epd.is_alive = true and epd.employment_end_date is null ");
			if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))){
				sbQuery.append("and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			}
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0)
            {
            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
            }else {
            	 if(getF_level()!=null && getF_level().length>0){
                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
                 }
            	 if(getF_grade()!=null && getF_grade().length>0){
                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
                 }
			}
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
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
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")){
            	if(flagMiddleName) {
					sbQuery.append(" and (upper(emp_fname)||' '||upper(emp_mname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
				} else {
					sbQuery.append(" and upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
				}
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			int recCnt = 0;
			int pageCount = 0;
			while(rs.next()){
				recCnt = rs.getInt("empCount");
				pageCount = rs.getInt("empCount")/50;
				if(rs.getInt("empCount")%50 != 0) {
					pageCount++;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("pageCount", pageCount+"");
			request.setAttribute("recCnt", recCnt+"");
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
					"and epd.is_alive = true and epd.employment_end_date is null");
			if(strUserType!=null && (strUserType.equalsIgnoreCase(MANAGER))){
				sbQuery.append("and emp_per_id in (select emp_id from employee_official_details where supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+") ");
			}
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
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
            
            if(getF_grade()!=null && getF_grade().length>0 && getF_level()!=null && getF_level().length>0)
            {
            	sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
            	//sbQuery.append(" and grade_id in ( " +StringUtils.join(getF_grade(), ",")+" ) and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");	
            }else {
            	 if(getF_level()!=null && getF_level().length>0){
                     sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
                 }
            	 if(getF_grade()!=null && getF_grade().length>0){
                     sbQuery.append(" and grade_id in ("+StringUtils.join(getF_grade(), ",")+" ) ");
                 }
			}
			if (getF_employeType() != null && getF_employeType().length > 0) {
				sbQuery.append(" and emptype in ( '" + StringUtils.join(getF_employeType(), "' , '") + "') ");
			}
			
            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")){
            	if(flagMiddleName) {
					sbQuery.append(" and (upper(emp_fname)||' '||upper(emp_mname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
				} else {
					sbQuery.append(" and upper(emp_fname)||' '||upper(emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
				}
			}
            sbQuery.append(" order by emp_fname, emp_lname");
            int intOffset = uF.parseToInt(minLimit);
			sbQuery.append(" limit 50 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			Map<String, String> hmEmpCodeMap = new HashMap<String, String>();
			Map<String, String> hmEmpName = new HashMap<String, String>();
			while(rs.next()){
				alEmployees.add(rs.getString("emp_per_id"));
				
				//String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				String strEmpName = rs.getString("emp_fname") +strEmpMName+ " " +rs.getString("emp_lname");
				hmEmpName.put(rs.getString("emp_id"), strEmpName);
				
				hmEmpCodeMap.put(rs.getString("emp_id"), rs.getString("empcode"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmLeaveColor = new HashMap<String, String>(); 
			CF.getLeavesAttributes(con, uF, hmLeaveColor, null);
			
			Map<String, String> hmOTHours = new HashMap<String, String>();
			for (int a=0; a<alEmployees.size(); a++){
					String strEmpId = (String)alEmployees.get(a);
					getClockEntries(con,uF,strEmpId,strD1,strD2, strPC);
					getOverTimeDetails(con,uF,strEmpId,strD1,strD2, strPC);
					
					List alInOut = (List) request.getAttribute("alInOut");
					List alDate = (List) request.getAttribute("alDate");
					List alDay = (List) request.getAttribute("alDay");
					
					Map hmHours = (HashMap) request.getAttribute("hmHours");
					Map hmHoursActual = (HashMap) request.getAttribute("hmHoursActual");
					Map hmStart = (HashMap) request.getAttribute("hmStartClockEntries");
					Map hmEnd = (HashMap) request.getAttribute("hmEndClockEntries");
					
					Map hmActualStart = (HashMap) request.getAttribute("hmActualStartClockEntries");
					Map hmActualEnd = (HashMap) request.getAttribute("hmActualEndClockEntries");

					Map hmRosterStart = (HashMap) request.getAttribute("hmRosterStart");
					Map hmRosterEnd = (HashMap) request.getAttribute("hmRosterEnd");

					Map hmDailyRate = (HashMap) request.getAttribute("hmDailyRate");
					Map hmHoursRates = (HashMap) request.getAttribute("hmHoursRates");
					Map hmServicesWorkedFor = (HashMap) request.getAttribute("hmServicesWorkedFor");
					Map hmDateServices = (HashMap) request.getAttribute("hmDateServices_TS");
					
					Map hmExceptions = (Map) request.getAttribute("hmExceptions");
					
					if(hmDateServices==null)hmDateServices = new HashMap();
						
					
					String TOTALW1 = (String) request.getAttribute("TOTALW1");
					String TOTALW2 = (String) request.getAttribute("TOTALW2");
					String DEDUCTION = (String) request.getAttribute("DEDUCTION");
					String PAYW1 = (String) request.getAttribute("PAYTOTALW1");
					String PAYW2 = (String) request.getAttribute("PAYTOTALW2");

					String _TOTALRosterW1 = (String) request.getAttribute("_TOTALRosterW1");
					String _TOTALRosterW2 = (String) request.getAttribute("_TOTALRosterW2");
					String _ALLOWANCE = (String) request.getAttribute("ALLOWANCE");

					String strPayMode = (String) request.getAttribute("strPayMode");
					String strFIXED = (String) request.getAttribute("FIXED");

					Map<String, Set<String>> hmWeekEndList = (Map<String, Set<String>>) request.getAttribute("hmWeekEndList");
					if(hmWeekEndList == null) hmWeekEndList = new HashMap<String, Set<String>>();
					String strWLocationId = (String)request.getAttribute("strWLocationId");
					String strLevelId = (String)request.getAttribute("strLevelId");
					Set<String> weeklyOffSet= (Set<String>)hmWeekEndList.get(strWLocationId);
					if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
					
					List<String> alEmpCheckRosterWeektype = (List<String>)request.getAttribute("alEmpCheckRosterWeektype");;
					if(alEmpCheckRosterWeektype == null) alEmpCheckRosterWeektype = new ArrayList<String>();
					
					Map<String, Set<String>> hmRosterWeekEndDates = (Map<String, Set<String>>)request.getAttribute("hmRosterWeekEndDates");;
					if(hmRosterWeekEndDates == null) hmRosterWeekEndDates = new HashMap<String, Set<String>>();
					
					Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(strEmpId);
					if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
					
					Map hmWLocationHolidaysName = (Map) request.getAttribute("hmWLocationHolidaysName");
					Map hmHolidaysName = (Map)hmWLocationHolidaysName.get(strWLocationId);
					if(hmHolidaysName==null) hmHolidaysName = new HashMap();
					
					Map _hmHolidaysColour = (Map) request.getAttribute("_hmHolidaysColour");
					Map hmHolidayDates = (Map) request.getAttribute("hmHolidayDates");
					Map hmEarlyLateReporting = (Map) request.getAttribute("hmEarlyLateReporting");
					Map hmServices = (Map) request.getAttribute("hmServices");
					
					Map hmLeavesMap = (Map) request.getAttribute("hmLeaves");
					if(hmLeavesMap==null)hmLeavesMap=new HashMap();

					Map hmLeavesColour = (Map) request.getAttribute("hmLeavesColour");
					if(hmLeavesColour==null)hmLeavesColour=new HashMap();
					
					List _alHolidays = (List) request.getAttribute("_alHolidays");

					String strEmpName = (String) request.getAttribute("EMP_NAME");
					Map hmRosterHours = (Map) request.getAttribute("hmRosterHours");

					if (strEmpName == null) {
						strEmpName = "";
					}

					if (hmHours == null) {
						hmHours = new HashMap();
					}

					if (hmStart == null) {
						hmStart = new HashMap();
					}

					if (hmEnd == null) {
						hmEnd = new HashMap();
					}
					if (hmDailyRate == null) {
						hmDailyRate = new HashMap();
					}
					if (hmHoursRates == null) {
						hmHoursRates = new HashMap();
					}

					if (hmHoursRates == null) {
						hmHoursRates = new HashMap();
					}

					if (hmEarlyLateReporting == null) {
						hmEarlyLateReporting = new HashMap();
					}
					if (hmRosterHours == null) {
						hmRosterHours = new HashMap();
					}
					if (_hmHolidaysColour == null) {
						_hmHolidaysColour = new HashMap();
					}
					if (hmLeavesColour == null) {
						hmLeavesColour = new HashMap();
					}
					if (hmExceptions == null) {
						hmExceptions = new HashMap();
					}
					
					
					Map<String, String> hmBreakPolicy =(Map<String, String>)request.getAttribute("hmBreakPolicy");
					if(hmBreakPolicy==null) hmBreakPolicy = new HashMap<String, String>();
					Boolean flagBreak=(Boolean)request.getAttribute("flagBreak");
					
					Map<String, String> hmShiftBreak = (Map<String, String>) request.getAttribute("hmShiftBreak");
					if(hmShiftBreak == null) hmShiftBreak = new HashMap<String, String>();
					
					Map<String, String> hmRosterShiftId = (Map<String, String>) request.getAttribute("hmRosterShiftId");
					if(hmRosterShiftId == null) hmRosterShiftId = new HashMap<String, String>();
					
					String strDefaultLunchDeduction = (String) request.getAttribute("strDefaultLunchDeduction");
					
					Map<String,Map<String,String>> hmOvertimeType=(Map<String,Map<String,String>>)request.getAttribute("hmOvertimeType");
					if(hmOvertimeType==null)hmOvertimeType=new HashMap<String,Map<String,String>>();
					
					Map<String, List<Map<String,String>>> hmOvertimeMinuteSlab = (Map<String, List<Map<String,String>>>)request.getAttribute("hmOvertimeMinuteSlab");
					if(hmOvertimeMinuteSlab == null) hmOvertimeMinuteSlab = new HashMap<String, List<Map<String,String>>>();
					
					Map<String,String> hmCalculateOT =(Map<String,String>)request.getAttribute("hmCalculateOT");
					if(hmCalculateOT == null) hmCalculateOT = new HashMap<String, String>();
					
					String locationstarttime=(String)request.getAttribute("locationstarttime");
					String locationendtime=(String)request.getAttribute("locationendtime");
					String userlocation=(String)request.getAttribute("userlocation");
					
					for (int i = 0; i < alDate.size(); i++) {
	
						List alDateServices = (List) hmDateServices.get((String) alDate.get(i));
						if (alDateServices == null) {
							alDateServices = new ArrayList();
							alDateServices.add("-1");
						}
	
						int ii = 0;
						for (ii = 0; ii < alDateServices.size(); ii++) {
	
							if (uF.parseToInt((String) alDateServices.get(ii)) == 0)
								continue;
							
							double dblHrsAtten = uF.parseToDouble((String) hmHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
							double dblHrsAttenActual = uF.parseToDouble((String) hmHoursActual.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
							double dblHrsRoster = uF.parseToDouble((String) hmRosterHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
							
							double dblOtHrs = 0.0d;
							if(((String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii))!=null) && ((String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii))!=null)){
								
								dblOtHrs = uF.parseToDouble(hmCalculateOT.get(strEmpId+"_"+uF.getDateFormat(""+uF.getDateFormat((String) alDate.get(i),CF.getStrReportDateFormat()),DBDATE,DATE_FORMAT)));
								
//							    Map<String,String> hmOvertime=null;
//							    String day=uF.getDateFormat(""+uF.getDateFormat((String) alDate.get(i),CF.getStrReportDateFormat()),DBDATE,DATE_FORMAT);
//							    if(hmHolidayDates!=null && hmHolidayDates.containsKey((String)alDate.get(i)+"_"+userlocation)){
//							    	hmOvertime=hmOvertimeType.get("PH");
//							    } else if(hmWeekEndList!=null && hmWeekEndList.containsKey(day+"_"+userlocation)){
//							    	hmOvertime=hmOvertimeType.get("BH");
//							    } else {
//							    	hmOvertime=hmOvertimeType.get("EH");
//							    }
//							    
//							    if(hmOvertime==null) hmOvertime=new HashMap<String,String>();
////							    if(uF.parseToInt(strEmpId) == 126){
////									System.out.println("date==>"+(String) alDate.get(i)+"==STANDARD_WKG_HRS==>"+hmOvertime.get("STANDARD_WKG_HRS"));
////								}
//							    if(hmOvertime.get("STANDARD_WKG_HRS")!=null && hmOvertime.get("STANDARD_WKG_HRS").equals("RH")){
//									Time entryTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),CF.getStrReportDateFormat()+" "+DBTIME);
//									Time rosterEndTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmRosterEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),CF.getStrReportDateFormat()+" "+DBTIME);
//									Time endTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),CF.getStrReportDateFormat()+" "+DBTIME);
//													
//									long milliseconds1 = entryTime.getTime();
//									long milliseconds2 = rosterEndTime.getTime();
//									long milliseconds3 = endTime.getTime();
//										
//									if(milliseconds3>=milliseconds2){
//										double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds2));
//										double actualTime=uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
//										double bufferTime=uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
//										double ottime = (actualTime-dbl) > 0.0d ? uF.parseToDouble(uF.getTimeVariance(uF,CF.getStrTimeZone(),uF.formatIntoTwoDecimal(dbl),uF.formatIntoTwoDecimal(actualTime))): 0.0d;
//										ottime = ottime > 0.0d ? uF.convertHoursMinsInDouble(ottime) : 0.0d;
//										double dblOTInMinute = uF.convertHoursIntoMinutes1(ottime);										
//										List<Map<String,String>> alOtMinute = (List<Map<String,String>>) hmOvertimeMinuteSlab.get(hmOvertime.get("OVERTIME_ID"));
//										if(alOtMinute == null) alOtMinute = new ArrayList<Map<String,String>>();
//										int nRoundOffMinute = 0;
//										int nAlOtMinuteSize = alOtMinute !=null ? alOtMinute.size() : 0;
////										if(uF.parseToInt(strEmpId) == 126){
////											System.out.println("date==>"+(String) alDate.get(i)+"==OVERTIME_ID==>"+hmOvertime.get("OVERTIME_ID")+"--alOtMinute==>"+alOtMinute);
////										}
//										for(int x = 0; x < nAlOtMinuteSize; x++){
//											Map<String,String> hmOvertimeMinute = alOtMinute.get(x);
////											if(uF.parseToInt(strEmpId) == 126){
////												System.out.println("date==>"+(String) alDate.get(i)+"==((int) dblOTInMinute)==>"+((int) dblOTInMinute)
////														+"--OVERTIME_MIN_MINUTE==>"+uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MIN_MINUTE"))
////														+"--OVERTIME_MAX_MINUTE==>"+uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MAX_MINUTE")));
////											}
//											if(uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MIN_MINUTE")) <= ((int) dblOTInMinute) 
//													&& ((int) dblOTInMinute) <= uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MAX_MINUTE"))){
//												nRoundOffMinute = uF.parseToInt(hmOvertimeMinute.get("ROUNDOFF_MINUTE"));
////												if(uF.parseToInt(strEmpId) == 126){
////													System.out.println("date==>"+(String) alDate.get(i)+"==OVERTIME_MIN_MINUTE==>"+hmOvertimeMinute.get("OVERTIME_MIN_MINUTE")+"--OVERTIME_MAX_MINUTE==>"+hmOvertimeMinute.get("OVERTIME_MAX_MINUTE"));
////												}
//												break;
//											}
//										}
////										if(uF.parseToInt(strEmpId) == 126){
////											System.out.println("date==>"+(String) alDate.get(i)+"dblOTInMinute==>"+dblOTInMinute+"--(int) dblOTInMinute)==>"+((int) dblOTInMinute)+"--nRoundOffMinute==>"+nRoundOffMinute);
////										}
//										if(nRoundOffMinute > 0){
//											double dblHour = uF.convertMinutesIntoHours(nRoundOffMinute);
//											String strTotal = ""+dblHour;
//											dblOtHrs=uF.parseToDouble(strTotal);
//										} else {
//											dblOtHrs = 0.0d;
//										}										
//									}
//								}else if(hmOvertime.get("STANDARD_WKG_HRS")!=null && hmOvertime.get("STANDARD_WKG_HRS").equals("SWH")){
//									Time entryTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmStart.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),CF.getStrReportDateFormat()+" "+DBTIME);
//									Time wlocationEndTime = uF.getTimeFormat((String) alDate.get(i)+ " "+locationendtime,CF.getStrReportDateFormat()+" "+DBTIME);
//									Time endTime = uF.getTimeFormat((String) alDate.get(i)+ " "+(String) hmEnd.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)),CF.getStrReportDateFormat()+" "+DBTIME);
//									
//									long milliseconds1 = entryTime.getTime();
//									long milliseconds2 = wlocationEndTime.getTime();
//									long milliseconds3 = endTime.getTime();
//										
//									if(milliseconds3>=milliseconds2){
//										double dbl = uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds2));
//										dbl = dbl > 0.0d ? uF.convertHoursMinsInDouble(dbl) : 0.0d;
//										double actualTime=uF.parseToDouble(uF.getTimeDiffInHoursMins(milliseconds1, milliseconds3));
//										actualTime = actualTime > 0.0d ? uF.convertHoursMinsInDouble(actualTime) : 0.0d;
//										double bufferTime=uF.parseToDouble(hmOvertime.get("BUFFER_STANDARD_TIME"));
//										double ottime=actualTime-dbl;
//										double dblOTInMinute = uF.convertHoursIntoMinutes1(ottime);										
//										List<Map<String,String>> alOtMinute = (List<Map<String,String>>) hmOvertimeMinuteSlab.get(hmOvertime.get("OVERTIME_ID"));
//										if(alOtMinute == null) alOtMinute = new ArrayList<Map<String,String>>();
//										int nRoundOffMinute = 0;
//										int nAlOtMinuteSize = alOtMinute !=null ? alOtMinute.size() : 0;
//										
//										for(int x = 0; x < nAlOtMinuteSize; x++){
//											Map<String,String> hmOvertimeMinute = alOtMinute.get(x);
//											
//											if(uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MIN_MINUTE")) <= ((int) dblOTInMinute) 
//													&& ((int) dblOTInMinute) <= uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MAX_MINUTE"))){
//												nRoundOffMinute = uF.parseToInt(hmOvertimeMinute.get("ROUNDOFF_MINUTE"));
//												
//												break;
//											}
//										}
//										
//										if(nRoundOffMinute > 0){
//											double dblHour = uF.convertMinutesIntoHours(nRoundOffMinute);
//											String strTotal = ""+dblHour;
//											dblOtHrs=uF.parseToDouble(strTotal);
//										} else {
//											dblOtHrs = 0.0d;
//										}
//									}
//								}else if(hmOvertime.get("STANDARD_WKG_HRS")!=null && hmOvertime.get("STANDARD_WKG_HRS").equals("F")){
//									double ottime=uF.parseToDouble((String) hmHours.get((String) alDate.get(i)+"_"+(String)alDateServices.get(ii)));
//									ottime = ottime > 0.0d ? uF.convertHoursMinsInDouble(ottime) : 0.0d;
//									double dblOTInMinute = uF.convertHoursIntoMinutes1(ottime);										
//									List<Map<String,String>> alOtMinute = (List<Map<String,String>>) hmOvertimeMinuteSlab.get(hmOvertime.get("OVERTIME_ID"));
//									if(alOtMinute == null) alOtMinute = new ArrayList<Map<String,String>>();
//									int nRoundOffMinute = 0;
//									int nAlOtMinuteSize = alOtMinute !=null ? alOtMinute.size() : 0;
//									for(int x = 0; x < nAlOtMinuteSize; x++){
//										Map<String,String> hmOvertimeMinute = alOtMinute.get(x);
//										if(uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MIN_MINUTE")) <= ((int) dblOTInMinute) 
//												&& ((int) dblOTInMinute) <= uF.parseToInt(hmOvertimeMinute.get("OVERTIME_MAX_MINUTE"))){
//											nRoundOffMinute = uF.parseToInt(hmOvertimeMinute.get("ROUNDOFF_MINUTE"));
//											break;
//										}
//									}
//									
//									if(nRoundOffMinute > 0){
//										double dblHour = uF.convertMinutesIntoHours(nRoundOffMinute);
//										String strTotal = ""+dblHour;
//										dblOtHrs=uF.parseToDouble(strTotal);
//									} else {
//										dblOtHrs = 0.0d;
//									}							
//								}								
							}
							
							if(dblOtHrs > 0.0d){
								hmOTHours.put(uF.getDateFormat((String) alDate.get(i), CF.getStrReportDateFormat(), DATE_FORMAT)+"_"+(String)alEmployees.get(a), uF.showTime(uF.formatIntoTwoDecimalWithOutComma(dblOtHrs)));
							}
							
						}
					}
			}
			
			
//			List<List<String>> reportList = new ArrayList<List<String>>();
//			int nEmpSize = alEmployees!=null ? alEmployees.size() : 0;
//    		for(int i=0; i < nEmpSize; i++){
//    			String strEmpId = (String)alEmployees.get(i);
//    			
//    			List<String> alInner = new ArrayList<String>();
//    			alInner.add(uF.showData(hmEmpName.get(strEmpId),""));
//    			alInner.add(uF.showData(hmEmpCodeMap.get(strEmpId),""));
//    			
//    			for(int j=0; j<alDates.size(); j++){
//					String strOvertime = "0.00";
//					if(uF.parseToDouble(hmOTHours.get((String)alDates.get(j)+"_"+(String)alEmployees.get(i))) > 0.0d){
//						strOvertime = hmOTHours.get((String)alDates.get(j)+"_"+(String)alEmployees.get(i));
//					}
//					alInner.add(uF.showData(strOvertime,""));
//    			}
//    			reportList.add(alInner);
//    		}
//			System.out.println("reportList==>"+reportList);
			
			request.setAttribute("alDates", alDates);
			request.setAttribute("alEmployees", alEmployees);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmOTHours", hmOTHours);			
			request.setAttribute("hmEmpCodeMap", hmEmpCodeMap);
			request.setAttribute("startPaycycle", strD1);			
			request.setAttribute("endPaycycle", strD2);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		} 
	}
	
	private void getOverTimeDetails(Connection con, UtilityFunctions uF, String strEmpId, String strD1, String strD2,String strPC) {
		PreparedStatement pst=null;
		ResultSet rs = null;
		try {
			
			String orgId = CF.getEmpOrgId(con, uF, strEmpId);
			String userlocation = CF.getEmpWlocationId(con, uF, strEmpId);
			String levelId = CF.getEmpLevelId(con, strEmpId);
			
			Map<String,Map<String,String>> hmOvertimeType=new HashMap<String, Map<String,String>>();
			pst = con.prepareStatement("select * from overtime_details where org_id=? and level_id=? and ((? between date_from and date_to) " +
					"or (? between date_from and date_to)) and overtime_id in (select overtime_id from overtime_minute_slab)");
			pst.setInt(1, uF.parseToInt(orgId));
			pst.setInt(2, uF.parseToInt(levelId));
			pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
//			StringBuffer sbOtId = null; 
			while(rs.next()){
				if(rs.getString("calculation_basis") != null && rs.getString("calculation_basis").trim().equalsIgnoreCase("M")){
//					if(sbOtId == null){
//						 sbOtId = new StringBuffer();
//						 sbOtId.append(rs.getString("overtime_id"));
//					} else {
//						sbOtId.append(","+rs.getString("overtime_id"));
//					}
					continue;
				}
				if(rs.getString("overtime_type").equals("PH")){
					Map<String,String> hmOvertime=new HashMap<String, String>();
					hmOvertime.put("OVERTIME_ID", rs.getString("overtime_id"));
					hmOvertime.put("BUFFER_STANDARD_TIME", rs.getString("buffer_standard_time"));
					hmOvertime.put("OVERTIME_HRS", rs.getString("over_time_hrs"));
					hmOvertime.put("FIXED_OVERTIME_HRS", rs.getString("fixed_overtime_hrs"));
					hmOvertime.put("MIN_OVER_TIME", rs.getString("min_over_time"));				
					hmOvertime.put("STANDARD_WKG_HRS", rs.getString("standard_wkg_hours"));
					hmOvertime.put("FIXED_STWKG_HRS", rs.getString("fixed_stwkg_hrs"));
					hmOvertime.put("OVERTIME_TYPE", rs.getString("overtime_type"));
					hmOvertime.put("ROUND_OFF_OVERTIME",rs.getString("round_off_time"));
					hmOvertime.put("CALCULATION_BASIS",rs.getString("calculation_basis"));
					
					hmOvertimeType.put("PH", hmOvertime);
				}else if(rs.getString("overtime_type").equals("BH")){
					Map<String,String> hmOvertime=new HashMap<String, String>();
					hmOvertime.put("OVERTIME_ID", rs.getString("overtime_id"));
					hmOvertime.put("BUFFER_STANDARD_TIME", rs.getString("buffer_standard_time"));
					hmOvertime.put("OVERTIME_HRS", rs.getString("over_time_hrs"));
					hmOvertime.put("FIXED_OVERTIME_HRS", rs.getString("fixed_overtime_hrs"));
					hmOvertime.put("MIN_OVER_TIME", rs.getString("min_over_time"));				
					hmOvertime.put("STANDARD_WKG_HRS", rs.getString("standard_wkg_hours"));
					hmOvertime.put("FIXED_STWKG_HRS", rs.getString("fixed_stwkg_hrs"));
					hmOvertime.put("OVERTIME_TYPE", rs.getString("overtime_type"));
					hmOvertime.put("ROUND_OFF_OVERTIME",rs.getString("round_off_time"));
					hmOvertime.put("CALCULATION_BASIS",rs.getString("calculation_basis"));
					
					hmOvertimeType.put("BH", hmOvertime);
				}else if(rs.getString("overtime_type").equals("EH")){
					Map<String,String> hmOvertime=new HashMap<String, String>();
					hmOvertime.put("OVERTIME_ID", rs.getString("overtime_id"));
					hmOvertime.put("BUFFER_STANDARD_TIME", rs.getString("buffer_standard_time"));
					hmOvertime.put("OVERTIME_HRS", rs.getString("over_time_hrs"));
					hmOvertime.put("FIXED_OVERTIME_HRS", rs.getString("fixed_overtime_hrs"));
					hmOvertime.put("MIN_OVER_TIME", rs.getString("min_over_time"));				
					hmOvertime.put("STANDARD_WKG_HRS", rs.getString("standard_wkg_hours"));
					hmOvertime.put("FIXED_STWKG_HRS", rs.getString("fixed_stwkg_hrs"));
					hmOvertime.put("OVERTIME_TYPE", rs.getString("overtime_type"));
					hmOvertime.put("ROUND_OFF_OVERTIME",rs.getString("round_off_time"));
					hmOvertime.put("CALCULATION_BASIS",rs.getString("calculation_basis"));
					
					hmOvertimeType.put("EH", hmOvertime);
				}
			}
			rs.close();
			pst.close();			
			request.setAttribute("hmOvertimeType", hmOvertimeType);
//			System.out.println("hmOvertimeType===>"+hmOvertimeType);
			
//			if(sbOtId !=null && sbOtId.length() > 0){
//				pst = con.prepareStatement("select * from overtime_minute_slab where overtime_id in ("+sbOtId.toString()+")");
////				if(uF.parseToInt(strEmpId) == 126){
////					System.out.println("pst====>"+pst);
////				}
//				rs=pst.executeQuery();
//				Map<String, List<Map<String,String>>> hmOvertimeMinuteSlab = new HashMap<String, List<Map<String,String>>>();
//				while(rs.next()){
//					List<Map<String,String>> alOtMinute = (List<Map<String,String>>) hmOvertimeMinuteSlab.get(rs.getString("overtime_id"));
//					if(alOtMinute == null) alOtMinute = new ArrayList<Map<String,String>>();
//					
//					Map<String,String> hmOvertimeMinute =new HashMap<String, String>();
//					hmOvertimeMinute.put("OVERTIME_MINUTE_ID", rs.getString("overtime_minute_id"));
//					hmOvertimeMinute.put("OVERTIME_ID", rs.getString("overtime_id"));
//					hmOvertimeMinute.put("OVERTIME_MIN_MINUTE", rs.getString("min_minute"));
//					hmOvertimeMinute.put("OVERTIME_MAX_MINUTE", rs.getString("max_minute"));
//					hmOvertimeMinute.put("ROUNDOFF_MINUTE", rs.getString("roundoff_minute"));	
//					
//					alOtMinute.add(hmOvertimeMinute);
//					
//					hmOvertimeMinuteSlab.put(rs.getString("overtime_id"), alOtMinute);
//				}
//				rs.close();
//				pst.close();
//				
//				request.setAttribute("hmOvertimeMinuteSlab", hmOvertimeMinuteSlab);
////				if(uF.parseToInt(strEmpId) == 126){
////					System.out.println("hmOvertimeMinuteSlab"+hmOvertimeMinuteSlab);
////				}
//			}
			
			pst= con.prepareStatement("select wlocation_id,wlocation_start_time,wlocation_end_time from work_location_info where wlocation_id =?");
			pst.setInt(1, uF.parseToInt(userlocation));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			String locationstarttime=null;
			String locationendtime=null;				
			while(rs.next()){
				locationstarttime=rs.getString("wlocation_start_time");
				locationendtime=rs.getString("wlocation_end_time");
			}
			rs.close();
			pst.close();
			request.setAttribute("locationstarttime", locationstarttime);
			request.setAttribute("locationendtime", locationendtime);
			request.setAttribute("userlocation", userlocation);
			
			pst = con.prepareStatement("select * from overtime_emp_minute_status where is_approved=1 and emp_id=? and ot_date between ? and ? ");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
			rs = pst.executeQuery();
			Map<String, String> hmCalculateOT = new HashMap<String, String>();
			while(rs.next()){
				hmCalculateOT.put(rs.getString("emp_id")+"_"+uF.getDateFormat(rs.getString("ot_date"), DBDATE, DATE_FORMAT),""+rs.getString("ot_hours"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmCalculateOT", hmCalculateOT);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void getClockEntries(Connection con, UtilityFunctions uF, String strEmpId, String strD1, String strD2,String strPC) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			
			Map<String, String> hmHolidays = new HashMap<String, String>();
			Map<String, String> hmHolidayDates = new HashMap<String, String>();
			Map<String, Map<String, String>> hmWLocationHolidaysName = new HashMap<String, Map<String, String>>();
			Map<String,Set<String>> holidaysMp=CF.getHolidayList(con,request,uF,strD1, strD2);
			
			CF.getHolidayList(strD1,request, strD2, CF, hmHolidayDates, hmHolidays, true);
//			getWLocationHolidayList(strD1, strD2, CF, hmWLocationHolidaysColour, hmWLocationHolidaysName, hmWLocationHolidaysWeekEnd);
			CF.getWLocationHolidayList(con, uF, strD1, strD2, CF, null, hmWLocationHolidaysName, null);
			
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);			
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEndList = CF.getWeekEndDateList(con, strD1, strD2, CF, uF,hmWeekEndHalfDates,null);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, strD1, strD2,alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEndList,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
			
			Map<String, String> hmEmpNameMap = CF.getEmpNameMap(con,null, null);

			List<String> _alHolidays = new ArrayList<String>();
			Map<String, String> _hmHolidaysColour = new HashMap<String, String>();
			Map<String, String> hmEmpType = CF.getEmpTypeMap(con);
			Map<String, String> hmServices = CF.getServicesMap(con,true);

			Map<String, String> hmLunchDeductionService = CF.getLunchDeductionServiceDetails(con);

			List _alDay = new ArrayList();
			List _alDate = new ArrayList();

			Map<String, String> hmLunchDeduction = new HashMap<String, String>();
			CF.getDeductionTime(con,hmLunchDeduction);
			
			java.sql.Date dtMin = null;
			java.sql.Date dtMax = null;
			int ii = 0;
			String s = uF.getDateFormat(""+uF.getDateFormat(strD1, DATE_FORMAT), DBDATE, DBDATE);
			String e = uF.getDateFormat(""+uF.getDateFormat(strD2, DATE_FORMAT), DBDATE, DBDATE);
			LocalDate start = LocalDate.parse(s);
			LocalDate end = LocalDate.parse(e);
			while (!start.isAfter(end)) {
				if (dtMin == null) {
					dtMin = new java.sql.Date(start.toDateTimeAtStartOfDay().toDate().getTime());
				}
				dtMax = new java.sql.Date(start.toDateTimeAtStartOfDay().toDate().getTime());

				_alDate.add(uF.getDateFormat(""+start, DBDATE, CF.getStrReportDateFormat()));
				_alDay.add(uF.getDateFormat(""+start, DBDATE, CF.getStrReportDayFormat()));

				if (hmHolidayDates != null && hmHolidayDates.containsKey(uF.getDateFormat(""+start, DBDATE, CF.getStrReportDateFormat()))) {
					_alHolidays.add(ii + "");
					_hmHolidaysColour.put(ii + "", (String) hmHolidayDates.get(uF.getDateFormat(""+start, DBDATE, CF.getStrReportDateFormat())));
				}
				ii++;
			    start = start.plusDays(1);
			}


			if (dtMax == null) {
				dtMin = null;
				dtMax = null;
				String s1 = uF.getDateFormat(""+uF.getDateFormat(strD1, DATE_FORMAT), DBDATE, DBDATE);
				String e1 = uF.getDateFormat(""+uF.getDateFormat(strD2, DATE_FORMAT), DBDATE, DBDATE);
				LocalDate start1 = LocalDate.parse(s1);
				LocalDate end1 = LocalDate.parse(e1);
				while (!start1.isAfter(end1)) {
					if (dtMin == null) {
						dtMin = new java.sql.Date(start1.toDateTimeAtStartOfDay().toDate().getTime());
					}
					dtMax = new java.sql.Date(start1.toDateTimeAtStartOfDay().toDate().getTime());

					_alDate.add(uF.getDateFormat(""+start1, DBDATE, CF.getStrReportDateFormat()));
					_alDay.add(uF.getDateFormat(""+start1, DBDATE, CF.getStrReportDayFormat()));

					if (hmHolidayDates != null && hmHolidayDates.containsKey(uF.getDateFormat(""+start1, DBDATE, CF.getStrReportDateFormat()))) {
						_alHolidays.add(ii + "");
						_hmHolidaysColour.put(ii + "", (String) hmHolidayDates.get(uF.getDateFormat(""+start1, DBDATE, CF.getStrReportDateFormat())));
					}
					ii++;
				    start1 = start1.plusDays(1);
				}
			}

			Map<String, Map<String, String>> hmRosterHours = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmRosterServices = new HashMap<String, Map<String, String>>();
			new CommonFunctions(CF).getRosterHoursMap(con, dtMin, dtMax, hmRosterHours, hmRosterServices);
			Map hmRosterHoursEmp = (Map) hmRosterHours.get(strEmpId);
			if (hmRosterHoursEmp == null) {
				hmRosterHoursEmp = new HashMap();
			}
			
			String strSelectedEmpType = null;
			String strWLocationId = null;
			pst = con.prepareStatement(selectEmployee2Details);
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				strSelectedEmpType = rs.getString("emptype");
				strWLocationId = rs.getString("wlocation_id");
			}
			rs.close();
			pst.close();

			Map hmRosterLunchDeduction = new HashMap();
			Map hmRosterStart = new HashMap();
			Map hmRosterEnd = new HashMap();

			List alDateServices_CE = new ArrayList();
			Map hmDateServices_CE = new HashMap();


			Map<String, Map<String, String>> hmEarlyLateReporting = new HashMap<String, Map<String, String>>();
			new CommonFunctions(CF).getEarlyLateReporting(con, dtMin, dtMax, hmEarlyLateReporting);
			
			boolean isFixedAdded = false;
			String strOldEmpId = null;
			String strNewEmpId = null;
			String strServiceId = null;
			String strDateNew = null;
			String strDateOld = null;
			List alDateServices_TS = new ArrayList();
			Map hmDateServices_TS = new HashMap();
			List alHours = new ArrayList();
			Map<String, Map<String, String>> hmLeaveDatesType = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmLeavesMap = CF.getLeaveDates(con, strD1, strD2, CF, hmLeaveDatesType, false, null);
			Map<String, Map<String, String>> hmLeavesMap = CF.getActualLeaveDates(con, CF, uF, strD1, strD2, hmLeaveDatesType, false, null);
			Map<String, String> hmLeaves = null;
			Map<String, String> hmLeavePaid = new HashMap<String, String>();
			Map<String, String> hmLeavesColour = new HashMap<String, String>();
			CF.getLeavesAttributes(con, uF, hmLeavesColour, null);
			hmLeaves = hmLeavesMap.get(strEmpId);
			if (hmLeaves == null) {
				hmLeaves = new HashMap<String, String>();
			}
			CF.getEmployeePaidMap(con, strEmpId, hmLeavePaid, null);
			
			pst = con.prepareStatement(selectRosterDetails1);
			pst.setDate(1, dtMin);
			pst.setDate(2, dtMax);
			pst.setInt(3, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {

				isFixedAdded = false;
				strNewEmpId = rs.getString("emp_id");
				strServiceId = rs.getString("service_id");
				strDateNew = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat());


				if (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld)) {
					alDateServices_TS = new ArrayList();
				}
				if (strServiceId != null && !alDateServices_TS.contains(strServiceId) && !hmLeaves.containsKey(strDateNew)) {
					alDateServices_TS.add(strServiceId);
					hmDateServices_TS.put(strDateNew, alDateServices_TS);
				}

				hmRosterStart.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "_" + strServiceId,uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
				hmRosterEnd.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "_" + strServiceId,uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
				hmRosterLunchDeduction.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "_" + strServiceId, rs.getString("is_lunch_ded"));

				String strTempDate = uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat());

				if (!alHours.contains(strTempDate)) {
					alHours.add(strTempDate);
				}

				if (strDateNew != null && !strDateNew.equalsIgnoreCase(strDateOld)) {
					alDateServices_CE = new ArrayList();
				}
				if (strServiceId != null && !alDateServices_CE.contains(strServiceId)) {
					alDateServices_CE.add(strServiceId);
					hmDateServices_CE.put(strDateNew, alDateServices_CE);
				}

				strOldEmpId = strNewEmpId;
				strDateOld = strDateNew;
			}
			rs.close();
			pst.close();

			Map<String, String> hmEmpCodeDesig = new HashMap<String, String>();

			Map<String, Map<String, String>> hmPayrollFT = new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmPayrollPT = new HashMap<String, Map<String, String>>();

			CF.getDailyRates(con, hmPayrollFT, hmPayrollPT);

			List alDay = new ArrayList();
			List alDate = new ArrayList();

			List alInOut = new ArrayList();

			Map<String, String> hmHoursActual = new HashMap<String, String>();
			Map<String, String> hmHours = new HashMap<String, String>();
			Map<String, String> hmStart = new HashMap<String, String>();
			Map<String, String> hmStartClockEntries = new HashMap<String, String>();
			Map<String, String> hmActualStartClockEntries = new HashMap<String, String>();
			Map<String, String> hmEnd = new HashMap<String, String>();
			Map<String, String> hmEndClockEntries = new HashMap<String, String>();
			Map<String, String> hmActualEndClockEntries = new HashMap<String, String>();
			Map<String, String> hmHoursRates = new HashMap<String, String>();
			Map<String, String> hmDailyRate = new HashMap<String, String>();
			Map<String, String> hmServicesWorkedFor = new HashMap<String, String>();

			
			Map<String, String> hmExceptions = new HashMap<String, String>();
			
			
			pst = con.prepareStatement(selectClockEntries);
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, dtMin);
			pst.setDate(3, dtMax);
			rs = pst.executeQuery();
			long _IN = 0L;
			long _OUT = 0L;
			long _INActual = 0L;
			long _OUTActual = 0L;
			double _TOTALW1 = 0L;
			double _TOTALW2 = 0L;
			double _TOTALRosterW1 = 0L;
			double _TOTALRosterW2 = 0L;
			double _PAYTOTALW1 = 0L;
			double _PAYTOTALW2 = 0L;
			double _PAYTOTAL = 0L;
			double _TOTAL = 0L;
			boolean isOut = false;
			boolean isIn = false;
			boolean isInOut = false;
			String strPayMode = null;
			int dayCount = -1;
			Map hmRate = null;
			java.util.Date strWeek1Date = null;
			int nServiceIdNew = 0;
			int nServiceIdOld = 0;
			while (rs.next()) {

				strDateNew = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat());
				String str = rs.getString("in_out");
				nServiceIdNew = rs.getInt("service_id");
				int nApproved = rs.getInt("approved");
				if (!alDate.contains(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
					alDate.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()));
					alDay.add(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDayFormat()));
					_IN = 0L;
					_OUT = 0L;
					_INActual = 0L;
					_OUTActual = 0L;
					isIn = false;
					isOut = false;
					isInOut = false;
					dayCount++;
				} else if (nServiceIdNew != nServiceIdOld) {
					_IN = 0L;
					_OUT = 0L;
					_INActual = 0L;
					_OUTActual = 0L;
					isIn = false;
					isOut = false;
					isInOut = false;
				}

				if (str != null && str.equalsIgnoreCase("IN") && !isIn) {
					_IN = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					_INActual = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					hmStart.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					hmStartClockEntries.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					
					hmActualStartClockEntries.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
							uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					
					isIn = true;

					if (nApproved == 1) {
						String strTemp = (String) hmRosterStart.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew);
						hmRosterStart.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew, uF.showData(strTemp, ""));
					}
					
					hmExceptions.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew+"_IN", rs.getString("approved"));

				} else if (str != null && str.equalsIgnoreCase("OUT") && !isOut) {
					_OUT = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					_OUTActual = uF.getTimeFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP).getTime();
					hmEnd.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					hmEndClockEntries.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew, uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					hmActualEndClockEntries.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew, uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, CF.getStrReportTimeFormat()));
					
					isOut = true;

					if (nApproved == 1) {
						String strTemp = (String) hmRosterEnd.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew);
						hmRosterEnd.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew, uF.showData(strTemp, ""));
					}
					
					hmExceptions.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBDATE, CF.getStrReportDateFormat()) + "_" + nServiceIdNew+"_OUT", rs.getString("approved"));
				}

				if (nServiceIdNew > 0 && !alDateServices_TS.contains(nServiceIdNew + "")
						&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
					alDateServices_TS.add(nServiceIdNew + "");
					hmDateServices_TS.put(strDateNew, alDateServices_TS);
				}

				if (_IN > 0 && _OUT > 0 && !isInOut) {

					int i = _alDate.indexOf(strDateNew);

					/**
					 * LUNCH HOUR CALCULATION FOR DEDUCTION
					 * 
					 * */

					double dblHoursWorked = uF.parseToDouble(uF.getTimeDiffInHoursMins(_IN, _OUT));
					double dblHoursWorkedActual = uF.parseToDouble(uF.getTimeDiffInHoursMins(_INActual, _OUTActual));
					double dblLunchTime = uF.parseToDouble(hmLunchDeduction.get(O_LUNCH_DEDUCT_TIME));
					double dblLunch = uF.parseToDouble(hmLunchDeduction.get(O_LUNCH_DEDUCT));

					boolean isLunchDeductionService = uF.parseToBoolean((String) hmLunchDeductionService.get(nServiceIdNew + ""));
					boolean isLunchDeduct = uF.parseToBoolean((String) hmRosterLunchDeduction.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP,CF.getStrReportDateFormat())+ "_" + nServiceIdNew));

					if (dblHoursWorked >= dblLunchTime && isLunchDeduct && isLunchDeductionService) {
						dblHoursWorked = dblHoursWorked - dblLunch;
					}

					hmHours.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,uF.formatIntoTwoDecimal(dblHoursWorked));
					hmHoursActual.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,uF.formatIntoTwoDecimal(dblHoursWorkedActual));

					String strDate = uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat());
					String strDesig = (String) hmEmpCodeDesig.get(strEmpId);

					strServiceId = null;

					hmServicesWorkedFor.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,(String) hmServices.get(nServiceIdNew + ""));

					String strEmpType = (String) hmEmpType.get(strEmpId);

					if (strEmpType != null && strEmpType.equalsIgnoreCase("PT")) {
						hmRate = (HashMap) hmPayrollPT.get("D" + strDesig + "S" + nServiceIdNew);
					} else {
						hmRate = (HashMap) hmPayrollFT.get("D" + strDesig + "S" + nServiceIdNew);
					}

					String strRate = null;
					String strLoading = null;
					if (hmRate != null) {
						strRate = (String) hmRate.get(uF.getDateFormat((String) _alDate.get(i), CF.getStrReportDateFormat(), CF.getStrReportDayFormat()).toUpperCase());
						strLoading = (String) hmRate.get(CF.getLoadingWeekDayCode(uF.getDateFormat((String) _alDate.get(i), CF.getStrReportDateFormat(),CF.getStrReportDayFormat()).toUpperCase()));
					} else {
						hmRate = new HashMap();
					}
					double dblRate = uF.parseToDouble(strRate);

					strPayMode = (String) hmRate.get("PAYMODE");

					if (strPayMode != null && strPayMode.equalsIgnoreCase("H") && !hmLeaves.containsKey(strDate)) {

						double rate = uF.parseToDouble(strRate);
						double loading = uF.parseToDouble(strLoading);
						double rateLoading = 0.0;

						if (strEmpType != null && strEmpType.equalsIgnoreCase("FT")) {
							if (hmHolidayDates != null && hmHolidayDates.containsKey(strDate)) {
								rateLoading = rate + (rate * loading) / 100;
								hmDailyRate.put(strDate + "_" + nServiceIdNew, uF.formatIntoTwoDecimal(rateLoading));
							} else {
								rateLoading = rate;
								hmDailyRate.put(strDate + "_" + nServiceIdNew, uF.formatIntoTwoDecimal(uF.parseToDouble(strRate)));
							}
						} else {
							rateLoading = rate;
							hmDailyRate.put(strDate + "_" + nServiceIdNew, uF.formatIntoTwoDecimal(uF.parseToDouble(strRate)));
						}

						_PAYTOTAL += uF.convertHoursIntoMinutes(dblHoursWorked) * rateLoading;
						hmHoursRates.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew,
								uF.formatIntoTwoDecimal(uF.convertHoursIntoMinutes(dblHoursWorked) * rateLoading) + "");
					} else if (!hmLeaves.containsKey(strDate)) {

						hmHoursRates.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()) + "_" + nServiceIdNew, "Fixed");
						if (!isFixedAdded) {
							_PAYTOTAL += uF.parseToDouble((String) hmRate.get("FIXED"));
							isFixedAdded = true;
						}

					}

					// _TOTAL = uF.parseToDouble(uF.getTimeDiffInHoursMins(_IN,
					// _OUT));
					_TOTAL = dblHoursWorked;

					isInOut = true;

					// if (_alDate != null && _alDate.size() >= 7) {
					// strWeek1Date = uF.getDateFormatUtil((String)
					// _alDate.get(7), CF.getStrReportDateFormat());
					// }

					if (_alDate != null && _alDate.size() >= 6) {
						strWeek1Date = uF.getDateFormatUtil((String) _alDate.get(6), CF.getStrReportDateFormat());
					}

					java.util.Date currentDate = uF.getDateFormatUtil(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()),
							CF.getStrReportDateFormat());
					if (strWeek1Date != null && strWeek1Date.after(currentDate)
							&& !hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
						_TOTALW1 += _TOTAL;
						_TOTALRosterW1 += uF.parseToDouble((String) hmRosterHoursEmp.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP,
								CF.getStrReportDateFormat())));
					} else if (!hmLeaves.containsKey(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, CF.getStrReportDateFormat()))) {
						_TOTALW2 += _TOTAL;
						_TOTAL = 0;
						_TOTALRosterW2 += uF.parseToDouble((String) hmRosterHoursEmp.get(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP,
								CF.getStrReportDateFormat())));
					}

				}

				nServiceIdOld = nServiceIdNew;
				strDateOld = strDateNew;

			}
			rs.close();
			pst.close();

			String[] label = new String[_alDate.size()];
			double[] workedHours = new double[_alDate.size()];
			double[] rosterHours = new double[_alDate.size()];

			for (int i = 0; i < _alDate.size(); i++) {
				label[i] = (String) _alDate.get(i);
				workedHours[i] = uF.parseToDouble((String) hmHours.get((String) _alDate.get(i)));
				rosterHours[i] = uF.parseToDouble((String) hmRosterHoursEmp.get((String) _alDate.get(i)));
			}

			/**
			 * START HOLIDAY PAYMENT SETTINGS FOR FULL TIME EMPLOYEES
			 * 
			 * */


			if (strSelectedEmpType != null && strSelectedEmpType.equalsIgnoreCase("FT")) {
				pst = con.prepareStatement(selectSettings);
				rs = pst.executeQuery();
				double hrs = 0;
				while (rs.next()) {
					if (rs.getString("options").equalsIgnoreCase(O_STANDARD_FULL_TIME_HOURS)) {
						hrs = uF.parseToDouble(rs.getString("value"));
					}
				}
				rs.close();
				pst.close();


				Set set = hmHolidayDates.keySet();
				Iterator it = set.iterator();
				while (it.hasNext()) {
					String strDate = (String) it.next();
					Date utDate = uF.getDateFormatUtil(strDate, CF.getStrReportDateFormat());

					if (!hmHours.containsKey(strDate) && !hmLeaves.containsKey(strDate)) {

						hmStart.put(strDate + "_", PublicHoliday);

						hmHours.put(strDate + "_", uF.formatIntoTwoDecimal(hrs));

						strServiceId = "";
						String strEmpType = (String) hmEmpType.get(strEmpId);

						if (strEmpType != null && strEmpType.equalsIgnoreCase("PT")) {
							hmRate = (HashMap) hmPayrollPT.get("D" + strEmpId + "S" + strServiceId);
						} else {
							hmRate = (HashMap) hmPayrollFT.get("D" + strEmpId + "S" + strServiceId);
						}
						if (hmRate == null) {
							hmRate = new HashMap();
						}

						String strRate = (String) hmRate.get(uF.getDateFormat(strDate, CF.getStrReportDateFormat(), CF.getStrReportDayFormat()).toUpperCase());
						hmDailyRate.put(strDate + "_", uF.formatIntoTwoDecimal(uF.parseToDouble(strRate)));
						hmHoursRates.put(strDate + "_", uF.formatIntoTwoDecimal(uF.convertHoursIntoMinutes(hrs * uF.parseToDouble(strRate))));


						if (strWeek1Date != null && strWeek1Date.after(utDate)) {
							_TOTALW1 += hrs;
							_PAYTOTALW1 += hrs * uF.parseToDouble(strRate);
							_PAYTOTAL += hrs * uF.parseToDouble(strRate);
						} else {
							_TOTALW2 += hrs;
							_PAYTOTAL += hrs * uF.parseToDouble(strRate);
						}


					}
				}

				/**
				 * END HOLIDAY PAYMENT SETTINGS FOR FULL TIME EMPLOYEES
				 * 
				 * */

				/**
				 * LEAVE MANAGEMENT
				 * */

				Set setLeaves = hmLeaves.keySet();
				Iterator itLeaves = setLeaves.iterator();
				while (itLeaves.hasNext()) {
					String strDate = (String) itLeaves.next();
					Date utDate = uF.getDateFormatUtil(strDate, DATE_FORMAT);

					String strLeaveType = (String) hmLeaves.get(strDate);
					boolean isPaidLeave = uF.parseToBoolean((String) hmLeavePaid.get(strLeaveType));

					hmStart.put(strDate + "_", (String) hmLeaves.get(strDate));
					hrs = LeaveHours; // Leaves standard hours are different
										// from holidays hours for Oracle CMS
										// client

					if (!isPaidLeave)
						continue;

					hmHours.put(strDate + "_", uF.formatIntoTwoDecimal(hrs));
					strServiceId = "";
					String strEmpType = (String) hmEmpType.get(strEmpId);

					if (strEmpType != null && strEmpType.equalsIgnoreCase("PT")) {
						hmRate = (HashMap) hmPayrollPT.get("D" + strEmpId + "S" + strServiceId);
					} else {
						hmRate = (HashMap) hmPayrollFT.get("D" + strEmpId + "S" + strServiceId);
					}
					if (hmRate == null) {
						hmRate = new HashMap();
					}

					// String strRate = (String)
					// hmRate.get(uF.getDateFormat(strDate,
					// CF.getStrReportDateFormat(),
					// CF.getStrReportDayFormat()).toUpperCase());

					String strRate = CF.getMinimumRateForPublicHolidays(con, strEmpId, uF.getDateFormat(strDate, DATE_FORMAT, CF.getStrReportDayFormat())) + "";
					hmDailyRate.put(strDate + "_", uF.formatIntoTwoDecimal(uF.parseToDouble(strRate)));
					hmHoursRates.put(strDate + "_", uF.formatIntoTwoDecimal(uF.convertHoursIntoMinutes(hrs * uF.parseToDouble(strRate))));


					if (strWeek1Date != null && strWeek1Date.after(utDate)) {
						_TOTALW1 += hrs;
						_PAYTOTALW1 += hrs * uF.parseToDouble(strRate);
						_PAYTOTAL += hrs * uF.parseToDouble(strRate);
					} else {
						_TOTALW2 += hrs;
						_PAYTOTAL += hrs * uF.parseToDouble(strRate);
					}
				}
			}

			/**
			 * END LEAVE PAYMENT SETTINGS FOR FULL TIME EMPLOYEES
			 * 
			 * */

			// request.setAttribute("CHART_RSTER_VS_ACTUAL", new
			// BarChart().getChartWithMarks(workedHours, rosterHours, label));
			
//			System.out.println("hmHolidayDates=====>"+hmHolidayDates);
//			System.out.println("hmWeekEndList=====>"+hmWeekEndList);
			request.setAttribute("alInOut", alInOut);
			request.setAttribute("alDate", _alDate);
			request.setAttribute("alDay", _alDay);
			request.setAttribute("hmHours", hmHours);
			request.setAttribute("hmHoursActual", hmHoursActual);
			request.setAttribute("hmStart", hmStart);
			request.setAttribute("hmStartClockEntries", hmStartClockEntries);
			request.setAttribute("hmActualStartClockEntries", hmActualStartClockEntries);

			request.setAttribute("hmRosterStart", hmRosterStart);
			request.setAttribute("hmRosterEnd", hmRosterEnd);
			request.setAttribute("hmEnd", hmEnd);
			request.setAttribute("hmEndClockEntries", hmEndClockEntries);
			request.setAttribute("hmActualEndClockEntries", hmActualEndClockEntries);
			request.setAttribute("TOTALW1", _TOTALW1 + "");
			request.setAttribute("TOTALW2", _TOTALW2 + "");
			request.setAttribute("_TOTALRosterW1", _TOTALRosterW1 + "");
			request.setAttribute("_TOTALRosterW2", _TOTALRosterW2 + "");
			request.setAttribute("PAYTOTALW1", _PAYTOTALW1 + "");
			request.setAttribute("PAYTOTALW2", _PAYTOTALW2 + "");
			request.setAttribute("_PAYTOTAL", _PAYTOTAL + "");
			request.setAttribute("hmDateServices_CE", hmDateServices_CE);
			request.setAttribute("hmDateServices_TS", hmDateServices_TS);
			request.setAttribute("hmServices", hmServices);
			request.setAttribute("_alHolidays", _alHolidays);
			request.setAttribute("hmLeaves", hmLeaves);
			request.setAttribute("EMP_NAME", hmEmpNameMap.get(strEmpId));
			request.setAttribute("EMPID", strEmpId);
			request.setAttribute("hmLeavesColour", hmLeavesColour);
			request.setAttribute("hmExceptions", hmExceptions);

			// System.out.println("hmLeaves====>"+hmLeaves);

			Map<String, String> hmFirstAidAllowance = new HashMap<String, String>();
			CF.getAllowanceMap(con, hmFirstAidAllowance);

			double dblAllowance = 0;
			if (hmFirstAidAllowance.containsKey(strEmpId)) {
				dblAllowance = CF.getAllowanceValue(con, _TOTALW1 + _TOTALW2, uF.parseToInt(strEmpId));
			}

			request.setAttribute("ALLOWANCE", uF.formatIntoTwoDecimal(dblAllowance));
			request.setAttribute("DEDUCTION", CF.getDeductionAmountMap(con, _PAYTOTAL) + "");
			request.setAttribute("hmWeekEndList", hmWeekEndList);
			request.setAttribute("strWLocationId", strWLocationId);
			request.setAttribute("hmDailyRate", hmDailyRate);
			request.setAttribute("hmHoursRates", hmHoursRates);
			request.setAttribute("strPayMode", strPayMode);
			if (hmRate == null) {
				hmRate = new HashMap();
			}
			request.setAttribute("FIXED", (String) hmRate.get("FIXED"));
			
		
			
			request.setAttribute("hmWLocationHolidaysName", hmWLocationHolidaysName);
			request.setAttribute("_hmHolidaysColour", _hmHolidaysColour);
			request.setAttribute("hmHolidayDates", hmHolidayDates);
			request.setAttribute("hmServicesWorkedFor", hmServicesWorkedFor);
			request.setAttribute("hmRosterHours", hmRosterHoursEmp);

			if (hmEarlyLateReporting != null && strUserType != null && !strUserType.equalsIgnoreCase(EMPLOYEE)) {
				request.setAttribute("hmEarlyLateReporting", hmEarlyLateReporting.get(strEmpId));
			}
			request.setAttribute("hmRosterWeekEndDates", hmRosterWeekEndDates);
			request.setAttribute("alEmpCheckRosterWeektype", alEmpCheckRosterWeektype);		

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}

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
	public String[] getF_grade() {
		return f_grade;
	}

	public void setF_grade(String[] f_grade) {
		this.f_grade = f_grade;
	}
	
	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}
	
	public String[] getF_employeType() {
		return f_employeType;
	}

	public void setF_employeType(String[] f_employeType) {
		this.f_employeType = f_employeType;
	}

	public List<FillEmploymentType> getEmployementTypeList() {
		return employementTypeList;
	}

	public void setEmployementTypeList(List<FillEmploymentType> employementTypeList) {
		this.employementTypeList = employementTypeList;
	}

	public String getStrGrade() {
		return strGrade;
	}

	public void setStrGrade(String strGrade) {
		this.strGrade = strGrade;
	}

	public String getStrEmployeType() {
		return strEmployeType;
	}

	public void setStrEmployeType(String strEmployeType) {
		this.strEmployeType = strEmployeType;
	}

	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getStrSearch() {
		return strSearch;
	}

	public void setStrSearch(String strSearch) {
		this.strSearch = strSearch;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}
	
}