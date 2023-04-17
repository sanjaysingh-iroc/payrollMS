package com.konnect.jpms.reports;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.Years;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MusterRollJobReport extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements,IConstants {

    private String exceldownload;
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	String strBaseUserType = null;
	CommonFunctions CF = null;
	//private static Logger log = Logger.getLogger(MusterRollJobReport.class);

	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	
	private String paycycle;
	private String strMonth;
	private String strYear;
	private String f_org;
	private String[] f_strWLocation;
	private String[] f_department;
	private String[] f_level;
	private String[] f_service;
	private String[] f_emptype;
	
	private List<FillPayCycles> paycycleList ;
	private List<FillMonth> monthList;
	private List<FillYears> yearList;
	private List<FillOrganisation> orgList;
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	private List<FillEmploymentType> empTypeList;
	private String strEmpType;
	private String exportType;
	
	String strD1 = null;
	String strD2 = null;
	String strPC = null;
	
	UtilityFunctions uF = new UtilityFunctions();
	Connection con=null;
	PreparedStatement pst=null;
	ResultSet rs=null;
	
	public String execute() throws Exception{
		
		//System.out.println("hii in MusterRollJobReport");
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		
		request.setAttribute(TITLE, "Muster Roll Job Report");
		request.setAttribute(PAGE,"/jsp/reports/MusterRollJobReport.jsp");

		if(uF.parseToInt(getF_org()) == 0) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getStrLocation() != null && !getStrLocation().equals("") && !getStrLocation().equalsIgnoreCase("NULL")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("") && !getStrDepartment().equalsIgnoreCase("NULL")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrSbu() != null && !getStrSbu().equals("") && !getStrSbu().equalsIgnoreCase("NULL")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("") && !getStrLevel().equalsIgnoreCase("NULL")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
		if(getStrEmpType() != null && !getStrEmpType().equals("") && !getStrEmpType().equalsIgnoreCase("NULL")) {
			setF_emptype(getStrEmpType().split(","));
		} else {
			setF_emptype(null);
		}
		
		String[] strPayCycleDates = null;
		if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
		} else {
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}
		
		
		strD1 = strPayCycleDates[0];
		strD2 = strPayCycleDates[1];
		strPC = strPayCycleDates[2];
		
		
		String[] arrDate=null;
		if (getPaycycle() != null) {
			arrDate = strD1.split("/");
			setStrMonth(arrDate[1]);
			setStrYear(arrDate[2]);
		} else {
			setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			setStrYear(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"))+"");

		}
		
		if(getExceldownload()!=null && !getExceldownload().equalsIgnoreCase(null)){
			if(getExceldownload().equalsIgnoreCase("True")){
				generateMusterRollJobReportExcel(CF,con);
				return null;
			}
		}
		
		viewMusterRollJobReport(uF);
		return loadMusterRollJobReport(uF);
	}
	
	
	public String loadMusterRollJobReport(UtilityFunctions uF) {
		
		//System.out.println("hii im in loadMusterRollCumWages function_______");
		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		empTypeList = new FillEmploymentType().fillEmploymentType(request);
		
		getSelectedFilter(uF);
		return LOAD;
		}
		
	private void getSelectedFilter(UtilityFunctions uF) {
		//System.out.println("hii im in getSelectedFilter function_______");
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

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
				hmFilter.put("LEVEL", "All Level's");
			}
		} else {
			hmFilter.put("LEVEL", "All Level's");
		}
		
		alFilter.add("EMPTYPE");
		if(getF_emptype()!=null) {
			String strEmpType="";
			int k=0;
			for(int i=0;empTypeList!=null && i<empTypeList.size();i++) {
				for(int j=0;j<getF_emptype().length;j++) {
					if(getF_emptype()[j].equals(empTypeList.get(i).getEmpTypeId())) {
						if(k==0) {
							strEmpType=empTypeList.get(i).getEmpTypeName();
						} else {
							strEmpType+=", "+empTypeList.get(i).getEmpTypeName();
						}
						k++;
					}
				}
			}
			if(strEmpType!=null && !strEmpType.equals("")) {
				hmFilter.put("EMPTYPE", strEmpType);
			} else {
				hmFilter.put("EMPTYPE", "All Employeetype's");
			}
		} else {
			hmFilter.put("EMPTYPE", "All Employeetype's");
		}

			alFilter.add("PAYCYCLE");
			if (getPaycycle() != null) {
				String strPayCycle = "";
				int k = 0;
				for (int i = 0; paycycleList != null && i < paycycleList.size(); i++) {
					if (getPaycycle().equals(paycycleList.get(i).getPaycycleId())) {
						if (k == 0) {
							strPayCycle = paycycleList.get(i).getPaycycleName();
						} else {
							strPayCycle += ", " + paycycleList.get(i).getPaycycleName();
						}
						k++;
					}
				}
				if (strPayCycle != null && !strPayCycle.equals("")) {
					hmFilter.put("PAYCYCLE", strPayCycle);
				} else {
					hmFilter.put("PAYCYCLE", "All Paycycle");
				}
			}

		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	public String viewMusterRollJobReport(UtilityFunctions uF){
		Database db=new Database();
		db.setRequest(request);
		try{
			con=db.makeConnection(con);
			

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
			if(getStrMonth() ==null){
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
				setStrYear(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"))+"");
			}
			
			List<String> alDates = new ArrayList<String>();
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1 );
			cal.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
			int minDays = cal.getActualMinimum(Calendar.DATE);
			cal.set(Calendar.DAY_OF_MONTH, minDays);
			int maxDays = cal.getActualMaximum(Calendar.DATE);
		
			String strD11 = null;
			for(int i=0; i<maxDays; i++){
				strD11 = uF.zero(cal.get(Calendar.DAY_OF_MONTH)) + "/" + uF.zero(cal.get(Calendar.MONTH) + 1) + "/"+ cal.get(Calendar.YEAR);
				alDates.add(uF.getDateFormat(strD11, DATE_FORMAT, DATE_FORMAT));
				cal.add(Calendar.DATE, 1);
			}
			System.out.println("Calendar.DATE"+Calendar.DATE);
			System.out.println("strD11=="+strD11);
			request.setAttribute("alDates", alDates);
			
			Map<String,List<String>> hmEmpServiceWorkedFor = new HashMap<String,List<String>>();
			Map<String,List<String>> hmEmpJobList=new LinkedHashMap<String,List<String>>();
			List<String> alEmployees = new ArrayList<String>();
			List<String> alServices  = new ArrayList<String>();
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select distinct eod.emp_id,empcode,wlocation_start_time,wlocation_end_time,epd.emp_date_of_birth,eod.service_id,eod.wlocation_id,eod.emptype,emp_lname,emp_per_id,eod.depart_id,emp_fname,emp_mname,emp_gender,designation_name,joining_date,emp_esic_no,emp_pf_no,employment_end_date" +
					" from employee_personal_details epd, employee_official_details eod, grades_details gd,designation_details dd,work_location_info wl " +
					" where wl.wlocation_id=eod.wlocation_id and epd.emp_per_id = eod.emp_id and gd.designation_id = dd.designation_id and gd.grade_id=eod.grade_id and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) ");
			if(strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strBaseUserType.equalsIgnoreCase(HOD))){
				sbQuery.append("and emp_id in (select emp_id from employee_official_details " +
						" where (supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+" " +
						" or hod_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+"))");
			if(getF_emptype()!=null && getF_emptype().length>0){
					sbQuery.append(" and emp_id in (select emp_id from employee_official_details where emptype in ('"+StringUtils.join(getF_emptype(), "' ,'")+"') ");
				}
			} else {
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
	            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
					sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
	            if(uF.parseToInt(getF_org())>0){
					sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
				}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
			}
			
        	if(getF_emptype()!=null && getF_emptype().length>0){
				sbQuery.append(" and eod.emptype in ('"+StringUtils.join(getF_emptype(), "' ,'")+"') ");
			}
            		 
            sbQuery.append("order by eod.emp_id,eod.depart_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			
			//System.out.println("alDates.get(0) ===>>********* " + alDates.get(0));
			System.out.println("uF.getDateFormat(alDates.get(0), DATE_FORMAT) ===>>******** " + uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			while(rs.next())
			{
				List<String> alEmpJobList=hmEmpJobList.get(uF.showData(rs.getString("emp_per_id"), ""));
				if(alEmpJobList==null)alEmpJobList=new ArrayList<String>();
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				
				
				alEmpJobList.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				
				alEmpJobList.add(rs.getString("emp_gender"));
				
				DateTime now = new DateTime();
				int years =  0;
				if(rs.getString("emp_date_of_birth")!=null){
					DateMidnight birthdate = new DateMidnight(uF.parseToInt(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "yyyy")), uF.parseToInt(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "MM")), uF.parseToInt(uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, "dd")));
					Years age = Years.yearsBetween(birthdate, now);
					years = age.getYears();
					}
					if(years<18) {
						continue;
					}
					alEmpJobList.add(""+ years);
					alEmpJobList.add(rs.getString("wlocation_start_time"));
					alEmpJobList.add(rs.getString("wlocation_end_time"));
					alEmpJobList.add(rs.getString("joining_date"));
					alEmpJobList.add(rs.getString("designation_name"));
					strEmpIdNew = rs.getString("emp_per_id");
				
					if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
						alServices  = new ArrayList<String>();
					}
				
					alEmployees.add(strEmpIdNew);
				
					String []arrServices = null;
					if(rs.getString("service_id")!=null){
						arrServices = rs.getString("service_id").split(",");
					}
				
					for(int i=0; arrServices!=null && i<arrServices.length; i++){
						if(!alServices.contains(arrServices[i]) && uF.parseToInt(arrServices[i]) > 0){
							alServices.add(arrServices[i]);
						}
					}
					
					hmEmpServiceWorkedFor.put(strEmpIdNew, alServices);
					strEmpIdOld = strEmpIdNew;
				
					hmEmpJobList.put(rs.getString("emp_per_id"), alEmpJobList);
			}
		rs.close();
		pst.close();
		
		request.setAttribute("hmEmpJobList", hmEmpJobList);
		request.setAttribute("alEmployees", alEmployees);
		request.setAttribute("alServices",alServices);
		
		viewEmpAttendanceData(con, uF, getF_org(),strEmpIdOld,strEmpIdNew,alDates,alServices,alEmployees,hmEmpServiceWorkedFor);
		viewLeaveTotalBalanceEnjoyedAndBreakTime(alDates,alEmployees);
		
		}catch(Exception e){
			e.printStackTrace();
		}
		finally{
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	public void viewLeaveTotalBalanceEnjoyedAndBreakTime(List<String>alDates,List<String>alEmployees)
	{
		//System.out.println("hi im in viewLeaveBalanceEnjoyedTotal");
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rs=null;
		StringBuilder sbQuery = null;
		StringBuilder sbEmp = null;
		try
		{
			for(int i=0;i<alEmployees.size();i++)
			{
				String strEmp = alEmployees.get(i);
				if(sbEmp == null){
					sbEmp =new StringBuilder();
					sbEmp.append(strEmp);
				}else{
					sbEmp.append(","+strEmp);
				}
			}
			if(sbEmp!=null){
				sbEmp.toString();
			}
			else{
				sbEmp=null;
			}
			
			sbQuery=new StringBuilder();
			/*sbQuery.append("select emp_id,leave_type_id,balance from leave_register1 where register_id in(select max(register_id) from leave_register1 " +
					"where _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id " +
					"from emp_leave_type where is_constant_balance=false) and is_compensatory=false) and _date<= ? and emp_id in("+sbEmp+") group by emp_id,leave_type_id)");
			sbQuery.append("and emp_id in (select emp_id from employee_official_details where emp_id > 0 and emp_id in("+sbEmp+")) ");
			sbQuery.append(" order by emp_id,leave_type_id");*/
			
			sbQuery.append("select emp_id,leave_type_id,balance from leave_register1 where register_id in(select max(register_id) from leave_register1 " +
					"where _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id " +
					"from emp_leave_type where is_constant_balance=false) and is_compensatory=false) and _date<= ? " );
			
			if(sbEmp!=null && !sbEmp.equals("")){
				sbQuery.append(" and emp_id in("+sbEmp+")) " );
			}
			sbQuery.append("group by emp_id,leave_type_id)");
			
			if(sbEmp!=null && !sbEmp.equals("")){
				sbQuery.append("and emp_id in ("+sbEmp+")");
			}
			sbQuery.append(" order by emp_id,leave_type_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			System.out.println(" pst for leave=====>"+pst);
		    rs = pst.executeQuery();
		    
		    Map<String, String> hmMainBalance=new HashMap<String, String>();
		    Map<String, List<List<String>>> hmEmpLeaveMap=new HashMap<String, List<List<String>>>();
		    
		    while (rs.next()) {
		    	
		        hmMainBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("balance"));
		        
		        List<List<String>> outerList = hmEmpLeaveMap.get(rs.getString("emp_id"));
		        if(outerList==null) outerList = new ArrayList<List<String>>();
		        
		        List<String> innerList = new ArrayList<String>();
		        innerList.add(rs.getString("leave_type_id"));
		        
		        outerList.add(innerList);
		        hmEmpLeaveMap.put(rs.getString("emp_id"), outerList);
		       
		    }
			rs.close(); 
			pst.close();
		
		    request.setAttribute("hmEmpLeaveMap", hmEmpLeaveMap);
			request.setAttribute("hmMainBalance", hmMainBalance);
			
			Map<String, String> hmTakenPaid = new HashMap<String, String>();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select sum(leave_no) as leave_no,leave_type_id, emp_id, is_paid from leave_application_register " +
					"where is_paid=true and (is_modify is null or is_modify=false) and _date between ? and ? ");
					
					if(sbEmp!=null && !sbEmp.equals("")){
						sbQuery.append(" and emp_id in("+sbEmp+")" );
					}
			sbQuery.append("group by leave_type_id, emp_id, is_paid");
			pst = con.prepareStatement(sbQuery.toString());
			
			pst.setDate(1, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				hmTakenPaid.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("leave_no"));
			}
			rs.close();
			pst.close();
	
			request.setAttribute("hmTakenPaid", hmTakenPaid);
		
	    
			Map<String,List<String>>hmRosterBreakTime = new LinkedHashMap<String,List<String>>();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select distinct emp_id, break_start, break_end from roster_details rd,shift_details sd where rd.shift_id=sd.shift_id " +
	    		"and _date between ? and ?" );
	    		if(sbEmp!=null && !sbEmp.equals("")){
					sbQuery.append(" and emp_id in("+sbEmp+")" );
				}
			sbQuery.append(" order by emp_id ");
			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			rs=pst.executeQuery();
			System.out.println("pst=="+pst);
			while(rs.next()){
				List<String> alBreakTime=hmRosterBreakTime.get(rs.getString("emp_id"));
				if(alBreakTime==null)alBreakTime=new ArrayList<String>();
			
					alBreakTime.add(rs.getString("break_start"));
					alBreakTime.add(rs.getString("break_end"));
		
					hmRosterBreakTime.put(rs.getString("emp_id"),alBreakTime);
			}
			
			rs.close();
			pst.close();
			request.setAttribute("hmRosterBreakTime", hmRosterBreakTime);
		
		
			Map<String,List<String>>hmTotalWorkedDays=new LinkedHashMap<String,List<String>>();
			
			sbQuery=new StringBuilder();
			sbQuery.append("select emp_id,paid_days from approve_attendance where approve_from=? and approve_to=? " );
					if(sbEmp!=null && !sbEmp.equals("")){
						sbQuery.append(" and emp_id in("+sbEmp+")" );
					}
							
			sbQuery.append(	"order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			
			pst.setDate(1, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			rs=pst.executeQuery();
			while(rs.next()){
				List<String>alTotalWorkedDay=hmTotalWorkedDays.get(rs.getString("emp_id"));
				if(alTotalWorkedDay==null)alTotalWorkedDay=new ArrayList<String>();
				alTotalWorkedDay.add(rs.getString("paid_days"));
				hmTotalWorkedDays.put(rs.getString("emp_id"),alTotalWorkedDay );
			}
			request.setAttribute("hmTotalWorkedDays", hmTotalWorkedDays);	
			
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(rs!=null){
				try{
					rs.close();
					}catch(SQLException e1){
					e1.printStackTrace();
				}
			}
			if(pst!=null)
			{
				try	{
					pst.close();
				}catch(SQLException e1)	{
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void viewEmpAttendanceData(Connection con, UtilityFunctions uF, String orgId,String strEmpIdOld, String strEmpIdNew,List<String>alDates,List<String>alServices,List<String>alEmployees,Map<String,List<String>> hmEmpServiceWorkedFor)
	{
		//System.out.println("im in viewDepartmentWiseReport");
		Database db=new Database();
		db.setRequest(request);
		PreparedStatement pst=null;
		ResultSet rs=null;
		StringBuilder sbQuery = null;
		try
		{
			sbQuery = new StringBuilder();
			
			Map<String, List<String>> hmEmpAttendanceData= new LinkedHashMap<String, List<String>>();
			Map<String, List<String>> hmEmpAttendanceDataForExcel= new LinkedHashMap<String, List<String>>();
			Map<String, String> hmBreakTypeCode = new HashMap<String, String>();
			Map<String, String> hmHalfDayAttendance = new HashMap<String, String>();
			Map<String,String> hmEmpAttendance = new HashMap<String,String>();
			Map<String,String> hmEmpLateEarly = new HashMap<String,String>();
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEndDates = CF.getWeekEndDateList(con, alDates.get(0), alDates.get(alDates.size()-1), CF, uF,hmWeekEndHalfDates,null);
			
			List<String> alInnerPrint = new ArrayList<String>();
			List<String> alInner = new ArrayList<String>();
			
			pst = con.prepareStatement("select * from leave_break_type");
			//System.out.println("pst for leavebreak***" +pst);
			rs = pst.executeQuery();
			while(rs.next()){
				hmBreakTypeCode.put(rs.getString("break_type_id"), rs.getString("break_type_code"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmBreakPolicy = new HashMap<String, String>();
			pst = con.prepareStatement("select * from break_application_register where _date between ? and ?");
			pst.setDate(1, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				hmBreakPolicy.put(rs.getString("emp_id")+"_"+uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), hmBreakTypeCode.get(rs.getString("break_type_id")));
			}
			rs.close();
			pst.close();
			
			sbQuery.append("select * from attendance_details where to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ?");
			if(strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strBaseUserType.equalsIgnoreCase(HOD))){
			sbQuery.append("and emp_id in (select emp_id from employee_official_details " +
					"where (supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+" " +
					"or hod_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+"))");		
			if(getF_emptype()!=null && getF_emptype().length>0){
				sbQuery.append("and emp_id in (select emp_id from employee_official_details where emptype in ('"+StringUtils.join(getF_emptype(), "' ,'")+"') ");
				}
			} else {
				if((getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_service()!=null && getF_service().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
					sbQuery.append("and emp_id in (select emp_id from employee_official_details where emp_id > 0 ");
				}
				if(getF_level()!=null && getF_level().length>0){
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }
				if(getF_department()!=null && getF_department().length>0){
	                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
				 if(getF_service()!=null && getF_service().length>0){
		                sbQuery.append(" and (");
		                for(int i=0; i<getF_service().length; i++){
		                	sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
		                    
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
		            if(getF_emptype()!=null && getF_emptype().length>0){
						sbQuery.append("and emptype in ('"+StringUtils.join(getF_emptype(), "' ,'")+"') ");
					}
		            if((getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_service()!=null && getF_service().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
						sbQuery.append(") ");
					}
				}
				sbQuery.append("order by emp_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
				rs = pst.executeQuery();
				System.out.println("pst for attendance"+pst);
				
				while(rs.next()){
					
					strEmpIdNew = rs.getString("emp_id");
					if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
						alServices  = new ArrayList<String>();
					}
					if(!alServices.contains(rs.getString("service_id"))){
						alServices.add(rs.getString("service_id"));
					}
					hmEmpServiceWorkedFor.put(strEmpIdNew, alServices);
					
					
					hmEmpAttendance.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id")+"_"+rs.getString("emp_id"), "P");
					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("IN")){
						hmEmpLateEarly.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id")+"_"+rs.getString("emp_id"), rs.getString("early_late"));
					}
					if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
						double workingHour=rs.getDouble("hours_worked");
						if(workingHour<5){
							hmHalfDayAttendance.put(rs.getString("emp_id")+"_"+uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBDATE, DATE_FORMAT),""+workingHour);
						}
					}
					strEmpIdOld = strEmpIdNew;
				}
				rs.close();
				pst.close();
				
				Map<String, Map<String, String>> hmLeaveTypeDays = new HashMap<String, Map<String, String>>();
				Map<String,Map<String,String>> leaveEmpMap=getLeaveDetails(con,alDates.get(0),alDates.get(alDates.size()-1),uF);
				Map<String, Map<String, String>> hmTravelRegister = CF.getTravelDetails(con,uF,alDates.get(0),alDates.get(alDates.size()-1));
				Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
				
				List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
				Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
				
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
				CF.getEmpRosterWeekOffTypeByDate(con,uF,CF,alDates.get(0), alDates.get(alDates.size()-1),alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEndDates,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
				List<String> alLegends = new ArrayList<String>(); 
				Map<String, String> hmLeaveColor = new HashMap<String, String>(); 
				CF.getLeavesAttributes(con, uF, hmLeaveColor, null);
				Map<String,String> hmHolidayDates = new HashMap<String,String>();
				Map<String, String> hmHolidayName = CF.getHolidayName(con, CF,alDates.get(0), alDates.get(alDates.size()-1));
				if(hmHolidayName == null) hmHolidayName = new HashMap<String, String>();
				Map<String,String> hmLeaveCodeMap =  getLeaveCodeMap(con, uF, getF_org());
				
				for (int i=0; i<alEmployees.size(); i++){
					
					Map<String,String> hmEmpLeave = leaveEmpMap.get(alEmployees.get(i));
					if(hmEmpLeave==null)hmEmpLeave = new HashMap<String,String>();
					
					Map<String,String> hmEmpTravel = hmTravelRegister.get(alEmployees.get(i));
					if(hmEmpTravel==null) hmEmpTravel = new HashMap<String,String>();
					
					Map<String,String> hmEmpLeaveType = hmLeaveTypeDays.get(alEmployees.get(i));
					if(hmEmpLeaveType==null)hmEmpLeaveType = new HashMap<String,String>();
					
					String strWLocationId = hmEmpWlocation.get(alEmployees.get(i));
					Set<String> weeklyOffSet= hmWeekEndDates.get(strWLocationId);

					if(weeklyOffSet==null)weeklyOffSet=new HashSet<String>();
					
					Set<String> halfDayWeeklyOffSet= hmWeekEndHalfDates.get(strWLocationId);
					if(halfDayWeeklyOffSet==null) halfDayWeeklyOffSet=new HashSet<String>();
					
					Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(alEmployees.get(i));
					if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
					
					List<String> alServicesInner = hmEmpServiceWorkedFor.get(alEmployees.get(i));
					if(alServicesInner==null)alServicesInner=new ArrayList<String>();
			
					for (int k=0; k<alServicesInner.size(); k++){
						
						alInner = new ArrayList<String>();
						alInnerPrint = new ArrayList<String>();
					
						Map<String,String> hmLeaveCnt = new HashMap<String, String>();
						for (int ii=0; ii<alDates.size(); ii++){
							String strWeekDay = uF.getDateFormat(alDates.get(ii), DATE_FORMAT, "EEEE");
							if(strWeekDay!=null){
								strWeekDay = strWeekDay.toUpperCase();
							}
							String strArrendance = hmEmpAttendance.get(alDates.get(ii)+"_"+alServicesInner.get(k)+"_"+alEmployees.get(i));
							java.util.Date dtDate = uF.getDateFormatUtil(alDates.get(ii), DATE_FORMAT);
							java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
							
							if(hmEmpLeave.containsKey(alDates.get(ii))){
								if("H".equals(hmEmpLeaveType.get(alDates.get(ii)))){
									double dblLeaveCnt = uF.parseToDouble(hmLeaveCnt.get(alEmployees.get(i)+"_"+hmEmpLeave.get(alDates.get(ii))));
									dblLeaveCnt +=0.5;
									
									hmLeaveCnt.put(alEmployees.get(i)+"_"+hmEmpLeave.get(alDates.get(ii)),""+dblLeaveCnt);
									alInner.add("<div style=\"width:100%;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+(strArrendance!=null?"/P":"/A")+"</div>");
									alInnerPrint.add(hmEmpLeave.get(alDates.get(ii))+(strArrendance!=null?"/P":"/A"));

									if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"/P"+"</div>")){
										alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"/P"+"</div>");
									}
									
								}else{
									double dblLeaveCnt = uF.parseToDouble(hmLeaveCnt.get(alEmployees.get(i)+"_"+hmEmpLeave.get(alDates.get(ii))));
									dblLeaveCnt +=1;
									
									hmLeaveCnt.put(alEmployees.get(i)+"_"+hmEmpLeave.get(alDates.get(ii)),""+dblLeaveCnt);
									alInner.add("<div style=\"width:100%;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"</div>");
									alInnerPrint.add(hmEmpLeave.get(alDates.get(ii)));

									if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Leave</div>")){
										alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Leave</div>");
									}
								}
							} else if(hmEmpTravel.containsKey(alDates.get(ii))){
							
								alInner.add("<div style=\"width:100%;height:100%;background-color:green;text-align:center\">"+hmEmpTravel.get(alDates.get(ii))+"</div>");
								alInnerPrint.add(hmEmpTravel.get(alDates.get(ii)));

									if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:green;text-align:center\">"+hmEmpTravel.get(alDates.get(ii))+"</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Travel Leave</div>")){
										alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:green;text-align:center\">"+hmEmpTravel.get(alDates.get(ii))+"</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Travel Leave</div>");
										}
								}else if(strArrendance!=null){
						
									if(hmBreakPolicy.containsKey(alEmployees.get(i)+"_"+alDates.get(ii))){
									alInner.add("<div style=\"text-align:center\" class=\"greenColor\">"+hmBreakPolicy.get(alEmployees.get(i)+"_"+alDates.get(ii))+"</div>");
									alInnerPrint.add("P");

									}else if(hmHalfDayAttendance.containsKey(alEmployees.get(i)+"_"+alDates.get(ii))){
		
									alInner.add("<div style=\"text-align:center\" class=\"greenColor\">HD/P</div>");
									alInnerPrint.add("HD/P");

								}else{
									alInner.add("<div style=\"text-align:center\" class=\"greenColor\">P</div>");
									alInnerPrint.add("P");

								}
							} else if(hmHolidayDates.containsKey(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)){
								if(strArrendance!=null){
									
									alInner.add("<div style=\"width:100%;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"greenColor\">P</div>");
									alInnerPrint.add("P");

								}
								else{
									alInner.add("<div style=\"width:100%;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"blueColor\"> H</div>");
									alInnerPrint.add("H");

									if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"blueColor\"> H</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> "+uF.showData(hmHolidayName.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId), "")+"</div>")){
										alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmHolidayDates.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId)+";text-align:center\" class=\"blueColor\"> H</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> "+uF.showData(hmHolidayName.get(uF.getDateFormat(alDates.get(ii), DATE_FORMAT, CF.getStrReportDateFormat())+"_"+strWLocationId), "")+"</div>");
									}
								}
							}else if(alEmpCheckRosterWeektype.contains(alEmployees.get(i))){
								if(rosterWeeklyOffSet.contains(alDates.get(ii))){
									String strColor=WEEKLYOFF_COLOR;
									alInner.add("<div style=\"width:100%;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div>");
									alInnerPrint.add("W/O");

									if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Weekly Off</div>")){
										alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Weekly Off</div>");
									}
									
								}else if(strArrendance!=null){
									if(hmBreakPolicy.containsKey(alEmployees.get(i)+"_"+alDates.get(ii))){
										alInner.add("<div style=\"text-align:center\" class=\"greenColor\">"+hmBreakPolicy.get(alEmployees.get(i)+"_"+alDates.get(ii))+"</div>");
										alInnerPrint.add("P");

									}else if(hmHalfDayAttendance.containsKey(alEmployees.get(i)+"_"+alDates.get(ii))){
										alInner.add("<div style=\"text-align:center\" class=\"greenColor\">HD/P</div>");
										alInnerPrint.add("HD/P");

									}else{
										alInner.add("<div style=\"text-align:center\" class=\"greenColor\">P</div>");
										alInnerPrint.add("P");

									}
								}else if(dtCurrentDate!=null && dtCurrentDate.after(dtDate)){
									alInner.add("<div style=\"text-align:center\" class=\"redColor\">A</div>");
									alInnerPrint.add("A");

								}else{
									alInner.add("");
									alInnerPrint.add("");

								}
								
							}else if(weeklyOffSet.contains(alDates.get(ii))){
								String strColor=WEEKLYOFF_COLOR;
								alInner.add("<div style=\"width:100%;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div>");
								alInnerPrint.add("W/O");

								if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Weekly Off</div>")){
									alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\"> W/O</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Weekly Off</div>");
								}
								
							}else if(halfDayWeeklyOffSet.contains(alDates.get(ii))){
								String strColor=WEEKLYOFF_COLOR;
								alInner.add("<div style=\"width:100%;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\">W/HD</div>");
								alInnerPrint.add("W/HD");

								if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\">W/HD</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Half Day Weekly Off</div>")){
									alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+strColor+";text-align:center\" class=\"blueColor\">W/HD</div><div style=\"float:left;margin-right:5px;height:100%;text-align:center\"> Half Day Weekly Off</div>");
								}
								
							}else if(dtCurrentDate!=null && dtCurrentDate.after(dtDate)){
								alInner.add("<div style=\"text-align:center\" class=\"redColor\">A</div>");
								alInnerPrint.add("A");

							}else{
								alInner.add("");
								alInnerPrint.add("");

							}
						}
						alInner.add(""+alDates.size());
				    	alInnerPrint.add(""+alDates.size());
					}
				    hmEmpAttendanceDataForExcel.put(alEmployees.get(i),alInnerPrint);
				    hmEmpAttendanceData.put(alEmployees.get(i),alInner);
				}//end of alemployee for loop
			
				request.setAttribute("alLegends", alLegends);
				request.setAttribute("hmEmpAttendanceData", hmEmpAttendanceData);
				request.setAttribute("hmEmpAttendanceDataForExcel",hmEmpAttendanceDataForExcel);
				
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs!=null){
				try{
					rs.close();
					}catch(SQLException e1){
					e1.printStackTrace();
				}
			}
			if(pst!=null)
			{
				try	{
					pst.close();
				}catch(SQLException e1)	{
					e1.printStackTrace();
				}
			}
		}
	}

	public Map<String,Map<String,String>> getLeaveDetails(Connection con,String strDate1,String strDate2,UtilityFunctions uF){
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,Map<String,String>> getMap=new HashMap<String,Map<String,String>>();
		try{
			pst=con.prepareStatement("select lar.*,lt.leave_type_code from leave_application_register lar,leave_type lt where lar.leave_type_id=lt.leave_type_id and is_modify = false and _date between ? and ? order by emp_id");
			pst.setDate(1, uF.getDateFormat(strDate1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDate2, DATE_FORMAT));
			rs=pst.executeQuery();
			
			while(rs.next()){
				Map<String,String> a=getMap.get(rs.getString("emp_id"));
				if(a==null)a=new HashMap<String,String>();
				
				a.put(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT), rs.getString("leave_type_code"));
				getMap.put(rs.getString("emp_id"), a);
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst!=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return getMap;
	}
	
	
	private Map<String, String> getLeaveCodeMap(Connection con, UtilityFunctions uF, String orgId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, String> hmLeaveCodeMap = new HashMap<String,String>();
		try {

			pst = con.prepareStatement("SELECT * FROM leave_type where org_id=? order by leave_type_name");
			pst.setInt(1, uF.parseToInt(orgId));
			rs = pst.executeQuery();
			while (rs.next()) {
				hmLeaveCodeMap.put(rs.getString("leave_type_id"), rs.getString("leave_type_code"));
			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmLeaveCodeMap;
	}

	public void generateMusterRollJobReportExcel(CommonFunctions CF,Connection con)
	{
		//System.out.println("in generateMusterRollJobReportExcel");
		
		Database db=new Database();
		db.setRequest(request);
		con=db.makeConnection(con);
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		viewMusterRollJobReport(uF);
		
		List<String>alDates=(List<String>)request.getAttribute("alDates");
		//System.out.println("alDates in excel"+alDates.size());
		
		String currentMonth="";
		if(alDates!=null && alDates.size()>0)
		   {
			   String strMonth = uF.getDateFormat((String)alDates.get(1), IConstants.DATE_FORMAT, "MM");
				currentMonth=uF.getMonth(uF.parseToInt(strMonth));
		   }
		
		List<String>alEmployees=(List<String>)request.getAttribute("alEmployees");
		if(alEmployees==null)alEmployees=new ArrayList<String>();
		
		int startColmSizeThirdRow=0,lastColmSizeThirdRow=0;
		int startColmSizeForthRow=0,lastColmSizeForthRow=0;
	    double balanceAmount=0.0;
		double paidAmount=0.0;
		  
		try{
			 //System.out.println("in try block ");
			 HSSFSheet sheet=workbook.createSheet("MusterRollJobExcelReport");
			 HSSFCellStyle headerStyle1= workbook.createCellStyle();
			 Font headerFont1 = workbook.createFont();
			 headerStyle1.setFillForegroundColor(IndexedColors.WHITE.getIndex());
			 headerFont1.setColor(IndexedColors.BLACK.getIndex());
			 headerFont1.setFontHeightInPoints((short)9);
			 headerFont1.setBoldweight(Font.BOLDWEIGHT_BOLD);
			 headerStyle1.setBorderTop(XSSFCellStyle.BORDER_NONE);
			 headerStyle1.setBorderRight(XSSFCellStyle.BORDER_NONE);
			 headerStyle1.setBorderLeft(XSSFCellStyle.BORDER_NONE);
			 headerStyle1.setBorderBottom(XSSFCellStyle.BORDER_NONE);
			 headerStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			 headerStyle1.setFont(headerFont1);
			 headerStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		
		 	 HSSFCellStyle tableheaderStyle1= workbook.createCellStyle();
		 	 Font tableheaderFont1 = workbook.createFont();
		 	 tableheaderStyle1.setFillForegroundColor(IndexedColors.WHITE.getIndex());
		 	 tableheaderFont1.setColor(IndexedColors.BLACK.getIndex());
		 	 tableheaderFont1.setFontHeightInPoints((short)9);
		 	 tableheaderFont1.setBoldweight(Font.BOLDWEIGHT_BOLD);
		 	 tableheaderStyle1.setBorderTop(XSSFCellStyle.BORDER_THIN);
		 	 tableheaderStyle1.setBorderRight(XSSFCellStyle.BORDER_THIN);
		 	 tableheaderStyle1.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		 	 tableheaderStyle1.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		 	 tableheaderStyle1.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		 	 tableheaderStyle1.setFont(tableheaderFont1);
		 	 tableheaderStyle1.setFillPattern(CellStyle.SOLID_FOREGROUND);
		 	 
		 	 HSSFCellStyle borderStyle1= workbook.createCellStyle();
		 	 Font borderFont1 = workbook.createFont();
		 	 borderFont1.setColor(IndexedColors.BLACK.getIndex());
		 	 borderFont1.setFontHeightInPoints((short)9);
		 	 borderStyle1.setAlignment(HSSFCellStyle.ALIGN_LEFT);
		 	 borderStyle1.setBorderTop(XSSFCellStyle.BORDER_THIN);
		 	 borderStyle1.setBorderRight(XSSFCellStyle.BORDER_THIN);
		 	 borderStyle1.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		 	 borderStyle1.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		 	 borderStyle1.setFont(borderFont1);
		 	 
		 	HSSFCellStyle borderStyle2= workbook.createCellStyle();
		 	 Font borderFont2 = workbook.createFont();
		 	 borderFont2.setColor(IndexedColors.BLACK.getIndex());
		 	 borderFont2.setFontHeightInPoints((short)9);
		 	 borderStyle2.setAlignment(HSSFCellStyle.ALIGN_RIGHT);
		 	 borderStyle2.setBorderTop(XSSFCellStyle.BORDER_THIN);
		 	 borderStyle2.setBorderRight(XSSFCellStyle.BORDER_THIN);
		 	 borderStyle2.setBorderLeft(XSSFCellStyle.BORDER_THIN);
		 	 borderStyle2.setBorderBottom(XSSFCellStyle.BORDER_THIN);
		 	 borderStyle2.setFont(borderFont2);
			 HSSFRow row=null;
			 HSSFCell cell=null;
			
		     row = sheet.createRow(0);
			 cell = row.createCell(0);
		     cell.setCellValue("MUSTER ROLL JOB REPORT");
		     cell.setCellStyle(headerStyle1);
		     sheet.autoSizeColumn(0);
		     row.setHeightInPoints(20);
		    
		     for(int i=1;i<51;i++)
		     {
		    	 cell.setCellValue("");
		    	 cell.setCellStyle(headerStyle1);
		     }
		     sheet.addMergedRegion(new CellRangeAddress(0,0,0,50));
  
//************row 4rth*******************
		     row=sheet.createRow(4);
 
			 cell = row.createCell(0);
		     cell.setCellValue("Sr.No.");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(0);
		     
		     cell =row.createCell(1);
		     cell.setCellValue("Employee Name");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(1);
		     
		     cell =row.createCell(2);
		     cell.setCellValue("Gender");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(2);
		     
		     cell =row.createCell(3);
		     cell.setCellValue("Age");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(3);
		     
		     sheet.addMergedRegion(new CellRangeAddress(4,5,0,0));
		     sheet.addMergedRegion(new CellRangeAddress(4,5,1,1));
		     sheet.addMergedRegion(new CellRangeAddress(4,5,2,2));
		     sheet.addMergedRegion(new CellRangeAddress(4,5,3,3));
		     
		     cell =row.createCell(4);
		     cell.setCellValue("Working Hours");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(4);
		     sheet.addMergedRegion(new CellRangeAddress(4,4,4,5));
		     
		     cell =row.createCell(6);
		     cell.setCellValue("Leave With Wages");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(6);
		     sheet.addMergedRegion(new CellRangeAddress(4,4,6,7));
		     
		     cell =row.createCell(8);
		     cell.setCellValue("DOE");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(8);
		     sheet.addMergedRegion(new CellRangeAddress(4,5,8,8)); 
		     
		     cell =row.createCell(9);
		     cell.setCellValue("Interval For Rest");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(9);
		     sheet.addMergedRegion(new CellRangeAddress(4,4,9,10));
		     
		     cell =row.createCell(11);
		     cell.setCellValue("Designation");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(11);
		     sheet.addMergedRegion(new CellRangeAddress(4,5,11,11)); 
		     
		      startColmSizeThirdRow=12;
		      
		      
		     if(alDates!=null && alDates.size()>0)
		     {
		    	 lastColmSizeThirdRow=alDates.size()+11;
		    	 cell =row.createCell(12);
			     cell.setCellValue("Attendance");
			     cell.setCellStyle(tableheaderStyle1);
			     sheet.autoSizeColumn(12);
			     sheet.addMergedRegion(new CellRangeAddress(4,4,startColmSizeThirdRow,lastColmSizeThirdRow));
		     }else
		     {
		    	 lastColmSizeThirdRow=11+30;
		         cell =row.createCell(12);
			     cell.setCellValue("Attendance");
			     cell.setCellStyle(tableheaderStyle1);
			     sheet.autoSizeColumn(12);
			     sheet.addMergedRegion(new CellRangeAddress(4,4,startColmSizeThirdRow,lastColmSizeThirdRow));
		     }
		     
		     lastColmSizeThirdRow++;
		     cell =row.createCell(lastColmSizeThirdRow);
		     cell.setCellValue("Total Days");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(lastColmSizeThirdRow);
		     sheet.addMergedRegion(new CellRangeAddress(4,5,lastColmSizeThirdRow,lastColmSizeThirdRow)); 
		     
		     lastColmSizeThirdRow++;
		     cell =row.createCell(lastColmSizeThirdRow);
		     cell.setCellValue("Total Worked Days");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(lastColmSizeThirdRow);
		     sheet.addMergedRegion(new CellRangeAddress(4,5,lastColmSizeThirdRow,lastColmSizeThirdRow)); 

//************row 5th***********
		     row=sheet.createRow(5);
		     
		     for(int i=0;i<4;i++)
		     {
		    	cell =row.createCell(i);
		    	cell.setCellValue("");
		    	cell.setCellStyle(tableheaderStyle1);
		     }
		    
		     cell =row.createCell(4);
		     cell.setCellValue("From");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(4);
		    
		     cell =row.createCell(5);
		     cell.setCellValue("To");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(5);
		     
		     cell =row.createCell(6);
		     cell.setCellValue("Balanced");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(6);
		    
		     cell =row.createCell(7);
		     cell.setCellValue("Enjoyed");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(7);
		    
		     cell =row.createCell(8);
		     cell.setCellValue("");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(8);
		     
		     cell =row.createCell(9);
		     cell.setCellValue("From");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(9);
		    
		     cell =row.createCell(10);
		     cell.setCellValue("To");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(10);
		     
		     cell =row.createCell(11);
		     cell.setCellValue("");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(11);
		     
		     startColmSizeForthRow=12;
		     System.out.println("startColmSizeForthRow"+startColmSizeForthRow);
		     
		     if(alDates!=null && alDates.size()>0)
		     {
		    	 for(int j=0;j<alDates.size();j++)
		    	 {
					 String strDate = uF.getDateFormat((String)alDates.get(j), IConstants.DATE_FORMAT, "dd");
			 		 
					 sheet.addMergedRegion(new CellRangeAddress(16,17,startColmSizeForthRow,startColmSizeForthRow));
					 cell =row.createCell(startColmSizeForthRow);
				     cell.setCellValue(uF.showData(strDate, "-"));
				     cell.setCellStyle(tableheaderStyle1);
				     sheet.autoSizeColumn(startColmSizeForthRow);
				     startColmSizeForthRow++;
		    	 }
		     }
		     else
		     {
		    	 for(int j=0;j<30;j++)
		    	 {
					 String strDate = uF.getDateFormat((String)alDates.get(j), IConstants.DATE_FORMAT, "dd");
			 		 
					 sheet.addMergedRegion(new CellRangeAddress(16,17,startColmSizeForthRow,startColmSizeForthRow));
					 cell =row.createCell(startColmSizeForthRow);
				     cell.setCellValue(uF.showData("", " "));
				     cell.setCellStyle(tableheaderStyle1);
				     sheet.autoSizeColumn(startColmSizeForthRow);
				     startColmSizeForthRow++;
		    	 }
		     }
		     
		     System.out.println("startColmSizeForthRow"+startColmSizeForthRow);
		     cell =row.createCell(startColmSizeForthRow);
		     cell.setCellValue("");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(startColmSizeForthRow);
		     
		     startColmSizeForthRow++;
		     cell =row.createCell(startColmSizeForthRow);
		     cell.setCellValue("");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(startColmSizeForthRow);
		     
//*************row 6th main data*************
		     int rowCount=5;
		    
		     Map<String,List<String>>hmEmpJobList=(Map<String,List<String>>)request.getAttribute("hmEmpJobList");
		     if(hmEmpJobList==null)hmEmpJobList=new LinkedHashMap<String,List<String>>();
			
		     Map<String,List<List<String>>>hmEmpLeaveMap=(Map<String,List<List<String>>>)request.getAttribute("hmEmpLeaveMap");
    		 if(hmEmpLeaveMap==null)hmEmpLeaveMap=new HashMap<String,List<List<String>>>();
    		 
    		 Map<String,String>hmMainBalance=(Map<String,String>)request.getAttribute("hmMainBalance");
    		 if(hmMainBalance==null)hmMainBalance=new HashMap<String,String>();
    		 
    		 Map<String,String>hmTakenPaid=(Map<String,String>)request.getAttribute("hmTakenPaid");
    		 if(hmTakenPaid==null)hmTakenPaid=new HashMap<String,String>();
    		 
    		 Map<String,List<String>>hmRosterBreakTime=(Map<String,List<String>>)request.getAttribute("hmRosterBreakTime");
    		 if(hmRosterBreakTime==null)hmRosterBreakTime=new LinkedHashMap<String,List<String>>();

    		 Map<String,List<String>> hmEmpAttendanceDataForExcel=(Map<String,List<String>>)request.getAttribute("hmEmpAttendanceDataForExcel");
			 if(hmEmpAttendanceDataForExcel==null)hmEmpAttendanceDataForExcel=new LinkedHashMap<String,List<String>>();
		     
			 Map<String,List<String>>hmTotalWorkedDays=(Map<String,List<String>>)request.getAttribute("hmTotalWorkedDays");
			 if(hmTotalWorkedDays==null)hmTotalWorkedDays=new LinkedHashMap<String,List<String>>();

		     if(hmEmpJobList!=null && hmEmpJobList.size()>0 && hmEmpLeaveMap!=null && hmEmpLeaveMap.size()>0
   				&& hmRosterBreakTime!=null && hmRosterBreakTime.size()>0 && hmEmpAttendanceDataForExcel!=null && hmEmpAttendanceDataForExcel.size()>0
   				&& hmTotalWorkedDays!=null && hmTotalWorkedDays.size()>0){ 
		    	 
		    	 int count=1;
		    	 int count1=0;
		    	 Iterator<String> it=hmEmpJobList.keySet().iterator();
		    	 
		    	 while(it.hasNext()){
		    		
		    		 rowCount++;
		    		 String empid=it.next();
		    		 List<String>alEmpJobList=hmEmpJobList.get(empid);
		    		 
		    		 row=sheet.createRow(rowCount);
		    		 
		    		 cell=row.createCell(0);
		    		 cell.setCellValue(uF.showData(""+count++, ""));
		    		 cell.setCellStyle(borderStyle1);
		    		 
		    		 cell=row.createCell(1);
		    		 cell.setCellValue(uF.showData(alEmpJobList.get(0), "-"));
		    		 cell.setCellStyle(borderStyle1);
		    		 
		    		 cell=row.createCell(2);
		    		 cell.setCellValue(uF.showData(alEmpJobList.get(1), "-"));
		    		 cell.setCellStyle(borderStyle1);
		    		 
		    		 cell=row.createCell(3);
		    		 cell.setCellValue(uF.showData(alEmpJobList.get(2), "-"));
		    		 cell.setCellStyle(borderStyle1);
		    		 
		    		 cell=row.createCell(4);
		    		 cell.setCellValue(uF.showData(alEmpJobList.get(3), "-"));
		    		 cell.setCellStyle(borderStyle1);
		    		 sheet.autoSizeColumn(4);
		    		 
		    		 cell=row.createCell(5);
		    		 cell.setCellValue(uF.showData(alEmpJobList.get(4), "-"));
		    		 cell.setCellStyle(borderStyle1);
		    		 sheet.autoSizeColumn(5);
		    	
		    		 List<List<String>>outerList= hmEmpLeaveMap.get(empid);
   						double dblOpeningBalance=0.0;
   						double dblTakenPaid=0.0;
   						if(outerList!=null && outerList.size()>0){
   							for(int i=0;i<outerList.size();i++){
   								List<String>innerList=outerList.get(i);
   									for(int j=0;j<innerList.size();j++){
   										String leaveTypeId = innerList.get(j);
   										dblOpeningBalance = dblOpeningBalance+uF.parseToDouble(hmMainBalance.get(empid+"_"+leaveTypeId));
   										dblTakenPaid = uF.parseToDouble(hmTakenPaid.get(empid+"_"+leaveTypeId));
									 }
   								}
								balanceAmount=dblOpeningBalance;
								dblOpeningBalance=0.0;
								paidAmount=dblTakenPaid;
								dblTakenPaid=0.0;
								
								cell=row.createCell(6);
					    		cell.setCellValue(balanceAmount);
					    		cell.setCellStyle(borderStyle1);		
					
					    		cell=row.createCell(7);
					    		cell.setCellValue(paidAmount);
					    		cell.setCellStyle(borderStyle1);
					    		
   						   }else{
	   							cell=row.createCell(6);
	   							cell.setCellValue(uF.showData("","-"));
	   							cell.setCellStyle(borderStyle1);		
				
	   							cell=row.createCell(7);
	   							cell.setCellValue(uF.showData("","-"));
	   							cell.setCellStyle(borderStyle1); 
   						   } 
		    	
		    		 cell=row.createCell(8);
		    		 cell.setCellValue(uF.showData(alEmpJobList.get(5), "-"));
		    		 cell.setCellStyle(borderStyle1);
		    		 sheet.autoSizeColumn(8);
		    		 
		    		 if(hmRosterBreakTime!=null && hmRosterBreakTime.size()>0){
		    		
		    		 List<String>alBreakTime=hmRosterBreakTime.get(empid);
   					 if(alBreakTime==null)alBreakTime=new ArrayList<String>();
   					 
		    		 if(alBreakTime!=null && alBreakTime.size()>0 ){
		    		 cell=row.createCell(9);
		    		 cell.setCellValue(uF.showData(alBreakTime.get(0),"-"));
		    		 cell.setCellStyle(borderStyle1);
		    		 
		    		 cell=row.createCell(10);
		    		 cell.setCellValue(uF.showData(alBreakTime.get(1),"-"));
		    		 cell.setCellStyle(borderStyle1);
		    		 }
		    		 else
		    		 {
		    			 cell=row.createCell(9);
			    		 cell.setCellValue(uF.showData("","-"));
			    		 cell.setCellStyle(borderStyle1);
			    		 
			    		 cell=row.createCell(10);
			    		 cell.setCellValue(uF.showData("","-"));
			    		 cell.setCellStyle(borderStyle1);
		    		 }
		    		} 
		    		
		    		 cell=row.createCell(11);
		    		 cell.setCellValue(uF.showData(alEmpJobList.get(6), "-"));
		    		 cell.setCellStyle(borderStyle1);
		    		 sheet.autoSizeColumn(11);
		    		 
		    		
		    		 
		    		 if(hmEmpAttendanceDataForExcel!=null && hmEmpAttendanceDataForExcel.size()>0){
		    			 
		    			 List<String>alAttendanceExcel=hmEmpAttendanceDataForExcel.get(empid);
		    			
		    			 if(alAttendanceExcel!=null && alAttendanceExcel.size()>0)
		    			 {
		    			   for(int i=0;i<alAttendanceExcel.size();i++){
		    				 count1=i+12;
             	 			 cell = row.createCell(count1);
             	 			 cell.setCellValue(uF.showData(alAttendanceExcel.get(i),"-"));
             	 			 cell.setCellStyle(borderStyle1);
             	 			 sheet.autoSizeColumn(count1);
		    			   }
		    			 }else
		    			 {
		    				 count1=11+30;
		    				 sheet.addMergedRegion(new CellRangeAddress(16,17,12,count1));
		    				 for(int i=0;i<count1;i++)
		    				 {
		    					 cell = row.createCell(i);
	             	 			 cell.setCellValue(uF.showData(""," "));
	             	 			 cell.setCellStyle(borderStyle1);
	             	 			 sheet.autoSizeColumn(count1);
		    				 }
		    			 }
		    		 }
		    		 count1++;
		    		 
		    		 List<String>alTotalWorkedDays=hmTotalWorkedDays.get(empid);
   						if(alTotalWorkedDays!=null && alTotalWorkedDays.size()>0){
   							for(int i=0;i<alTotalWorkedDays.size();i++){
   								cell = row.createCell(count1);
                	 			 cell.setCellValue(uF.showData(alTotalWorkedDays.get(i),"0"));
                	 			 cell.setCellStyle(borderStyle1);
                	 			 sheet.autoSizeColumn(count1);
   							}
   						}else
   						{
   						 cell=row.createCell(count1);
   			    		 cell.setCellValue(uF.showData("","-"));
   			    		 cell.setCellStyle(borderStyle1);
   						}
		    	 }
		     }
		     
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			try {
				workbook.write(buffer);
				buffer.close();
				}catch (IOException e1){
				e1.printStackTrace();
				}

			response.setHeader("Content-Disposition", "attachment; filename=\"MusterRollJobExcelReport.xls\"");
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());

			try {
				ServletOutputStream op = response.getOutputStream();
				op = response.getOutputStream();
				op.write(buffer.toByteArray());
				op.flush();
				op.close();
				}catch (IOException e){
				e.printStackTrace();
			   }
		
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
	}
	
	
	
	
	
	
	
	
	
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
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

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillYears> getYearList() {
		return yearList;
	}

	public void setYearList(List<FillYears> yearList) {
		this.yearList = yearList;
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

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
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
	public String[] getF_emptype() {
		return f_emptype;
	}

	public void setF_emptype(String[] f_emptype) {
		this.f_emptype = f_emptype;
	}

	public List<FillEmploymentType> getEmpTypeList() {
		return empTypeList;
	}

	public void setEmpTypeList(List<FillEmploymentType> empTypeList) {
		this.empTypeList = empTypeList;
	}

	public String getStrEmpType() {
		return strEmpType;
	}

	public void setStrEmpType(String strEmpType) {
		this.strEmpType = strEmpType;
	}
	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}
	
	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}
	
	public String getExceldownload() {
		return exceldownload;
	}

	public void setExceldownload(String exceldownload) {
		this.exceldownload = exceldownload;
	}
 }
