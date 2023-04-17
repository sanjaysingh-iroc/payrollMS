package com.konnect.jpms.reports;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
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
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.ComparatorWeight;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MusterRollCumWagesRegisterReport extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	String exceldownload;
	
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	String strBaseUserType = null;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(MusterRollCumWagesRegisterReport.class);
	
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
	
	public String execute() throws Exception {
		//System.out.println("hii im in execute function_______");
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		
		request.setAttribute(TITLE, "Muster Roll Cum Wages Register");
		request.setAttribute(PAGE,"/jsp/reports/MusterRollCumWagesRegisterReport.jsp");
		
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
		viewloadMusterRollCumWages(uF);
		
		if(getExceldownload()!=null && !getExceldownload().equalsIgnoreCase(null)) {
			//System.out.println("in exceldownload function");
			if(getExceldownload().equalsIgnoreCase("true")) {
				generateMusterRollExcel(CF,con);
				return null;
			}
		}
		return loadMusterRollCumWages(uF);
	}
	 
public String loadMusterRollCumWages(UtilityFunctions uF) {
	
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

	public String viewloadMusterRollCumWages(UtilityFunctions uF){
		
		//System.out.println("hii im in viewloadMusterRollCumWages__________");
		
		Database db=new Database();
		db.setRequest(request);
		
		try {
			con=db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
		
			
			List<String> alInnerPrint = new ArrayList<String>();
			Map<String, List<String>> hmEmpAttendanceData= new LinkedHashMap<String, List<String>>();
			Map<String, List<String>> hmEmpAttendanceDataForExcel= new LinkedHashMap<String, List<String>>();

			Map<String, List<String>> hmEmpData = new LinkedHashMap<String, List<String>>();
			List<String> alInner = new ArrayList<String>();
			List<String> alEmployees = new ArrayList<String>();
			List<String> alServices  = new ArrayList<String>();
			Map<String,List<String>> hmEmpServiceWorkedFor = new HashMap<String,List<String>>();
			StringBuilder sbQuery=new StringBuilder();
			
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
			request.setAttribute("alDates", alDates);
			
////////////////////////////////////////////////////////
			
			
			Map<String, String> hmFamilyInfo = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_family_members where member_type in ('SPOUSE', 'FATHER') ");

			if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0) || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(" and emp_id in (select emp_id from employee_official_details eod where emp_id > 0 ");
			}
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if (getF_emptype()!= null && getF_emptype().length > 0) {
					sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_emptype(), "' , '") + "') ");
			}
			 /*if (getF_grade() != null && getF_grade().length > 0) {
					sbQuery.append(" and eod.grade_id in ( '" + StringUtils.join(getF_grade(), "' , '") + "') ");
				}*/
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            /*if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }*/
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
            if((getF_service()!=null && getF_service().length>0) || (getF_level()!=null && getF_level().length>0) || (getF_department()!=null && getF_department().length>0)  || (getF_strWLocation()!=null && getF_strWLocation().length>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) || (uF.parseToInt(getF_org())>0) || (strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null)){
				sbQuery.append(") ");
			}
          
			pst = con.prepareStatement(sbQuery.toString());
			
			System.out.println("pst for family member==>"+pst);
			
			
			rs = pst.executeQuery();
			while(rs.next()){
				hmFamilyInfo.put(rs.getString("emp_id")+"_"+rs.getString("member_type"), rs.getString("member_name"));
			}
			rs.close();
			pst.close();
			
			
			
			
			
			
			
			
		//////////////////////////////////////////////////	
			
	    sbQuery = new StringBuilder();
			
			sbQuery.append("select distinct eod.emp_id,empcode ,eod.service_id,eod.wlocation_id,eod.emptype,emp_lname,emp_per_id,eod.depart_id,emp_fname,emp_mname,emp_gender,designation_name,dept_name,joining_date,emp_esic_no,emp_pf_no,payment_mode,employment_end_date" +
					" from employee_personal_details epd, payroll_history ph,employee_official_details eod, grades_details gd,designation_details dd,department_info Di" +
					" where epd.emp_per_id = eod.emp_id and gd.designation_id = dd.designation_id and gd.grade_id=eod.grade_id and Di.org_id=eod.org_id  and Di.dept_id=eod.depart_id " +
					"and ph.emp_id=eod.emp_id and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) ");
			if(getPaycycle()!=null){
				sbQuery.append(" and paycycle_from = to_date('" + uF.getDateFormat(strD1, DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')" +
				" and paycycle_to=to_date('" + uF.getDateFormat(strD2, DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')" +
				" and paycycle="+uF.parseToInt(strPC));
			}
			if(strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strBaseUserType.equalsIgnoreCase(HOD))){
				sbQuery.append("and emp_id in (select emp_id from employee_official_details " +
						"where (supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+" " +
						"or hod_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+"))");		
			if(getF_emptype()!=null && getF_emptype().length>0){
					sbQuery.append("and emp_id in (select emp_id from employee_official_details where emptype in ('"+StringUtils.join(getF_emptype(), "' ,'")+"') ");
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
            		 
            sbQuery.append("order by eod.depart_id, eod.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(alDates.get(0), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(alDates.get(alDates.size()-1), DATE_FORMAT));
			
		System.out.println("pst for empList===>> " + pst);
			rs = pst.executeQuery();			
			String strEmpIdNew = null;
			String strEmpIdOld = null;
			
			while(rs.next()){
			
				List<String> empList1 = hmEmpData.get(uF.showData(rs.getString("emp_per_id"), "-"));
				if(empList1==null)empList1=new ArrayList<String>();
				
				alEmployees.add(uF.showData(rs.getString("emp_per_id"),""));
				empList1.add(uF.showData(rs.getString("empcode"),""));
				
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				
				empList1.add(uF.showData(rs.getString("emp_fname"),"")+strEmpMName+" "+ uF.showData(rs.getString("emp_lname"),""));
				//empList1.add(uF.showData(rs.getString("emp_mname"),""));
				
				
				if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("M")) {
					empList1.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id")+"_FATHER"), "-"));
				} else if(rs.getString("emp_gender")!=null && rs.getString("emp_gender").equalsIgnoreCase("F") && rs.getString("marital_status")!=null && rs.getString("marital_status").equalsIgnoreCase("M")) {
					empList1.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id")+"_SPOUSE"), "-"));
				} else {
					empList1.add(uF.showData(hmFamilyInfo.get(rs.getString("emp_per_id")+"_FATHER"), "-"));
				}
				
				
				
				empList1.add(uF.showData(rs.getString("emp_gender"),""));
				empList1.add(uF.showData(rs.getString("designation_name"),""));
				empList1.add(uF.showData(rs.getString("dept_name"),""));
				empList1.add(uF.getDateFormat(rs.getString("joining_date"),DBDATE, DATE_FORMAT));
				empList1.add(uF.showData(rs.getString("emp_esic_no"),""));
				empList1.add(uF.showData(rs.getString("emp_pf_no"),""));
				empList1.add(uF.getDateFormat(rs.getString("employment_end_date"),DBDATE, DATE_FORMAT));
				empList1.add(uF.getPaymentMode(rs.getString("payment_mode")));
				hmEmpData.put(uF.showData(rs.getString("emp_per_id"),""), empList1);
				
				strEmpIdNew = rs.getString("emp_per_id");
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					alServices  = new ArrayList<String>();
				}
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
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpData", hmEmpData);
		
///////////////////////////////////////////////////////////////////////////			
			
			Map<String, String> hmBreakTypeCode = new HashMap<String, String>();
			pst = con.prepareStatement("select * from leave_break_type");
			System.out.println("pst for leavebreak***" +pst);
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
			
			sbQuery=new StringBuilder();
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
				
				System.out.println("pst fro attendance"+pst);
				Map<String, String> hmHalfDayAttendance = new HashMap<String, String>();
				Map<String,String> hmEmpAttendance = new HashMap<String,String>();
				Map<String,String> hmEmpLateEarly = new HashMap<String,String>();
				
				Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
				Map<String, Set<String>> hmWeekEndDates = CF.getWeekEndDateList(con, alDates.get(0), alDates.get(alDates.size()-1), CF, uF,hmWeekEndHalfDates,null);

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
									//double dblLeaveCnt = uF.parseToDouble(hmLeaveCnt.get(alEmployees.get(i)+"_"+hmEmpLeave.get(alDates.get(ii))));
									//dblLeaveCnt +=0.5;
									
									//hmLeaveCnt.put(alEmployees.get(i)+"_"+hmEmpLeave.get(alDates.get(ii)),""+dblLeaveCnt);
									alInner.add("<div style=\"width:100%;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+(strArrendance!=null?"/P":"/A")+"</div>");
									alInnerPrint.add(hmEmpLeave.get(alDates.get(ii))+(strArrendance!=null?"/P":"/A"));

									if(!alLegends.contains("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"/P"+"</div>")){
										alLegends.add("<div style=\"float:left;margin-right:5px;width:38px;height:100%;background-color:"+hmLeaveColor.get(hmEmpLeave.get(alDates.get(ii)))+";text-align:center\" class=\"greenColor\">"+hmEmpLeave.get(alDates.get(ii))+"/P"+"</div>");
									}
									
								}else{
									/*double dblLeaveCnt = uF.parseToDouble(hmLeaveCnt.get(alEmployees.get(i)+"_"+hmEmpLeave.get(alDates.get(ii))));
									dblLeaveCnt +=1;*/
									
									//hmLeaveCnt.put(alEmployees.get(i)+"_"+hmEmpLeave.get(alDates.get(ii)),""+dblLeaveCnt);
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
				    
					//System.out.println("hmEmpServiceWorkedFor.size()"+hmEmpServiceWorkedFor.size());

				}//end of alemployee for loop
				
				request.setAttribute("alLegends", alLegends);
				request.setAttribute("hmEmpAttendanceData", hmEmpAttendanceData);
				
				request.setAttribute("hmEmpAttendanceDataForExcel", hmEmpAttendanceDataForExcel);
				viewDepartmentWiseReport(con, uF, getF_org(),alEmployees);	
				
		}catch(Exception e)//end of try block
		{
			e.printStackTrace();
			log.error(e.getClass()+" : "+e.getMessage(), e);
		} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
		}
	return SUCCESS;
	}//end of viewmethod
	
	
	public void viewDepartmentWiseReport(Connection con, UtilityFunctions uF, String orgId,List<String>alEmployees){
		Database db=new Database();
		db.setRequest(request);
		//System.out.println("im in viewDepartmentWiseReport");
		PreparedStatement pst=null;
		ResultSet rs=null;
		StringBuilder sbEmp = null;
		try
		{
			getEmpPayrollHistory(con,uF);
			Map<String,List<String>> hmEmpSallaryDetail=new LinkedHashMap<String,List<String>>();
			Map<String, Map<String, String>> hmEmpHistory = (Map<String, Map<String, String>>) request.getAttribute("hmEmpHistory");
			if(hmEmpHistory == null) hmEmpHistory = new HashMap<String, Map<String,String>>(); 
			
			Map<String,String> hmSalaryDetails = CF.getSalaryHeadsMap(con);
			Map<String, String> hmSalaryWeight = new HashMap<String, String>();
			Map<String, Boolean> hmSalaryVariable = new HashMap<String, Boolean>();
			
			List<String> alEarningss = new ArrayList<String>();
			List<String> alDeductionss = new ArrayList<String>();
			
			List<ComparatorWeight> alEarnings = new ArrayList<ComparatorWeight>();
			List<ComparatorWeight> alDeductions = new ArrayList<ComparatorWeight>();
			
			Map<String, String> hmPresentDays=new HashMap<String, String>();
			Map<String,List<String>> deptEmpMap=new LinkedHashMap<String,List<String>>();
			Map<String,Map<String,String>> empSalaryMap=new HashMap<String,Map<String,String>>();
			
			pst = con.prepareStatement("select salary_head_id, weight,is_variable from salary_details order by level_id,weight");
			System.out.println("pst for salary_details in viewDepartmentWiseReport"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				if(hmSalaryWeight.get(rs.getString("salary_head_id"))==null){
				hmSalaryWeight.put(rs.getString("salary_head_id"), rs.getString("weight"));
				hmSalaryVariable.put(rs.getString("salary_head_id"),uF.parseToBoolean(rs.getString("is_variable")));
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alEmployees", alEmployees);
			
			
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
			
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true ");
				sbQuery.append(" and pg.emp_id in ("+sbEmp+") ");
				if(getPaycycle()!=null ){
					sbQuery.append(" and paycycle="+uF.parseToInt(strPC));
				} 
				sbQuery.append(" order by eod.depart_id,pg.emp_id, earning_deduction desc, salary_head_id");
				pst = con.prepareStatement(sbQuery.toString());
				System.out.println("pst for payroll_generation====>"+pst);
				rs = pst.executeQuery();
			
				while(rs.next()){
					
				Map<String, String> hm = hmEmpHistory.get(rs.getString("emp_id"));
				String strDepartment = rs.getString("depart_id");
				if(hm != null && uF.parseToInt(hm.get("EMP_DEPART")) > 0){
					strDepartment = uF.showData(hm.get("EMP_DEPART"), "0");
				}
				List<String>empList=deptEmpMap.get(strDepartment);
				if(empList==null)empList=new ArrayList<String>();
				if(!empList.contains(rs.getString("emp_id"))){
					empList.add(rs.getString("emp_id"));
				}
				
				deptEmpMap.put(strDepartment,empList);
				Map<String,String> salaryMap=empSalaryMap.get(rs.getString("emp_id"));
				if(salaryMap==null)salaryMap=new HashMap<String,String>();
				salaryMap.put(rs.getString("salary_head_id"), rs.getString("amount"));
				
				if("E".equalsIgnoreCase(rs.getString("earning_deduction"))){
					double dblAmount = rs.getDouble("amount");
					double dblGross = uF.parseToDouble(salaryMap.get("GROSS"));
					dblGross += dblAmount;
					salaryMap.put("GROSS",  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblGross)));
					
					double dblNet = uF.parseToDouble(salaryMap.get("NET"));
					dblNet+=dblAmount; 
					
					salaryMap.put("NET",  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblNet)));
				}else{
					double dblAmount = rs.getDouble("amount");
					double dblNet = uF.parseToDouble(salaryMap.get("NET"));
					dblNet-=dblAmount;
					salaryMap.put("NET",  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblNet)));
					
					double dblDeduction = uF.parseToDouble(salaryMap.get("DEDUCTION"));
					dblDeduction +=dblAmount;
					salaryMap.put("DEDUCTION",  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblDeduction)));
				}
				empSalaryMap.put(rs.getString("emp_id"),salaryMap);
				
				if("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarningss.contains(rs.getString("salary_head_id"))){
					alEarningss.add(rs.getString("salary_head_id"));
					alEarnings.add(new ComparatorWeight(rs.getString("salary_head_id"),uF.parseToInt(hmSalaryWeight.get(rs.getString("salary_head_id"))),hmSalaryVariable.get(rs.getString("salary_head_id"))));
				}else if("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeductionss.contains(rs.getString("salary_head_id"))){
					alDeductionss.add(rs.getString("salary_head_id"));
					alDeductions.add(new ComparatorWeight(rs.getString("salary_head_id"),uF.parseToInt(hmSalaryWeight.get(rs.getString("salary_head_id"))),hmSalaryVariable.get(rs.getString("salary_head_id"))));
				}
			}
			rs.close();
			pst.close();
			Collections.sort(alEarnings);
			
			//System.out.println("hmSalaryWeight.size()"+hmSalaryWeight.size());
			//System.out.println("hmEmpHistory.size()"+hmEmpHistory.size());
			//System.out.println("alEarnings.size()"+alEarnings.size());
			//System.out.println("alDeductions.size()"+alDeductions.size());
			//System.out.println("hmSalaryDetails.size() at last"+hmSalaryDetails.size());

			request.setAttribute("alEarnings", alEarnings);
			request.setAttribute("alDeductions", alDeductions);
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("empSalaryMap", empSalaryMap);
			request.setAttribute("alEarningss",alEarningss);
			request.setAttribute("alDeductionss",alDeductionss);
			
		}catch(Exception e)
		{
			e.printStackTrace();
			log.error(e.getClass()+ " : "+e.getMessage(),e);
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
			log.error(e.getClass() + ": " + e.getMessage(), e);
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
	
	public void getEmpPayrollHistory(Connection con, UtilityFunctions uF) {
		
		//System.out.println("in getEmpPayrollHistory");
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		Map<String, Map<String, String>> hmEmpHistory = new HashMap<String, Map<String,String>>(); 
		try {
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from payroll_history ph,employee_official_details eod where ph.emp_id=eod.emp_id and ph.emp_id > 0");
			
			if(getPaycycle()!=null){
				sbQuery.append(" and paycycle_from = to_date('" + uF.getDateFormat(strD1, DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')" +
				" and paycycle_to=to_date('" + uF.getDateFormat(strD2, DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')" +
				" and paycycle="+uF.parseToInt(strPC));
			}
			
			if(getF_level()!=null && getF_level().toString().trim().length()>0){
	            sbQuery.append(" and ph.grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
	        if(getF_department()!=null && getF_department().toString().trim().length()>0){
	            sbQuery.append(" and ph.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
	        if (getF_emptype() != null && getF_emptype().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_emptype(), "' , '") + "') ");
			}
	        if(getF_service()!=null && getF_service().toString().trim().length()>0){
	            sbQuery.append(" and (");
	            for(int i=0; i<getF_service().length; i++){
	            	sbQuery.append(" ph.service_id like '%,"+getF_service()[i]+",%'");
	                if(i<getF_service().length-1){
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
	        } 
	        
	        if(getF_strWLocation()!=null && getF_strWLocation().toString().trim().length()>0){
	            sbQuery.append(" and ph.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and ph.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	        
	        if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and ph.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and ph.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(" order by ph.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			System.out.println("pst 0 ====>"+pst);     
			rs = pst.executeQuery();
			Set<String> empSetlist = new HashSet<String>();
			
			while (rs.next()){
				empSetlist.add(rs.getString("emp_id"));
				
				Map<String, String> hm = new HashMap<String, String>();
				hm.put("EMP_ORG", rs.getString("org_id"));
				hm.put("EMP_WLOCATION", rs.getString("wlocation_id"));
				hm.put("EMP_DEPART", rs.getString("depart_id"));
				hm.put("EMP_GRADE", rs.getString("grade_id"));
				hmEmpHistory.put(rs.getString("emp_id"), hm);
				
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmEmpHistory", hmEmpHistory);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select distinct(pg.emp_id) as emp_id from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id and pg.is_paid=true ");
			if(getPaycycle()!=null){
				sbQuery.append(" and paid_from = to_date('" + uF.getDateFormat(strD1, DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')" +
				" and paid_to=to_date('" + uF.getDateFormat(strD2, DATE_FORMAT, DBDATE) + "'::text, 'YYYY-MM-DD')" +
				" and paycycle="+uF.parseToInt(strPC));
			}
			if(getF_level()!=null && getF_level().length>0){
	            sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
	        if(getF_department()!=null && getF_department().length>0){
	            sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
	        if (getF_emptype() != null && getF_emptype().length > 0) {
				sbQuery.append(" and eod.emptype in ( '" + StringUtils.join(getF_emptype(), "' , '") + "') ");
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
	        sbQuery.append(" and pg.emp_id not in (select emp_id from payroll_history where paycycle_from =? and paycycle_to=? and paycycle= ?) ");
			sbQuery.append(" order by pg.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPC));
			System.out.println("pst 1 ====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()){
				empSetlist.add(rs.getString("emp_id"));
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
	
	public void generateMusterRollExcel(CommonFunctions CF,Connection con) {
		
		Database db=new Database();
		db.setRequest(request);
		con=db.makeConnection(con);
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		viewloadMusterRollCumWages(uF);
		
		List<String>alEmployees=(List<String>)request.getAttribute("alEmployees");
	
		viewDepartmentWiseReport(con, uF, getF_org(),alEmployees);	
		
		
		
		Map<String,List<String>> hmEmpData = (Map<String,List<String>>)request.getAttribute("hmEmpData");
    	
		Map<String,List<String>> hmEmpAttendanceDataForExcel=(Map<String,List<String>>)request.getAttribute("hmEmpAttendanceDataForExcel");
    	
    	Map<String,Map<String,String>> empSalaryMap=(Map<String,Map<String,String>>)request.getAttribute("empSalaryMap");

    	Map hmSalaryDetails = (Map) request.getAttribute("hmSalaryDetails");

    	List<String> alEarningss= (ArrayList<String>)request.getAttribute("alEarningss");
    	
    	List alDeductionss=(List)request.getAttribute("alDeductionss");
		
    	List<String> alDates = (List<String>)request.getAttribute("alDates");
		
    	String currentMonth="";
    	 if(alDates!=null && alDates.size()>0)
		   {
			   String strMonth = uF.getDateFormat((String)alDates.get(1), IConstants.DATE_FORMAT, "MM");
				currentMonth=uF.getMonth(uF.parseToInt(strMonth));
		   }
		
		
		int startColmSizefirstRow=0,lastColmSizefirstRow=0;
		int startColmSizeScondRow=0,lastColmSizeScondRow=0;
		int startColmSizeThirdRow=0,lastColmSizeThirdRow=0;
		
		try {
			
		 HSSFSheet sheet = workbook.createSheet("MusterRollCumWagesRegisterExcelReport");
		 
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
	     cell.setCellValue("MUSTER ROLL CUM WAGES REGISTER REPORT");
	     cell.setCellStyle(headerStyle1);
	     row.setHeightInPoints(20);
	     
	     for(int i=1;i<61;i++) {
		     cell = row.createCell(i);
		     cell.setCellValue("");
			     cell.setCellStyle(headerStyle1);
		    }
		     sheet.addMergedRegion(new CellRangeAddress(0,0,0,60));
		    
 //**************************row 3rd***************************
	     row = sheet.createRow(3);
	     sheet.addMergedRegion(new CellRangeAddress(3,3,11,31));
		   cell =row.createCell(11);
		   cell.setCellValue("Form-T");
		   cell.setCellStyle(headerStyle1);
		   sheet.autoSizeColumn(20);
		  
		   sheet.addMergedRegion(new CellRangeAddress(3,3,50,60));
		   cell =row.createCell(50);
		   cell.setCellValue("Form-T");
		   cell.setCellStyle(headerStyle1);
		   sheet.autoSizeColumn(50);
			   
 //**************************row 4rth***************************		     
		  row =  sheet.createRow(4);
		   sheet.addMergedRegion(new CellRangeAddress(4,4,11,31));
		   cell =row.createCell(11);
		   cell.setCellValue("COMBINED MUSTER ROLL CUM REGISTER OF WAGES");
		   cell.setCellStyle(headerStyle1);
		   sheet.autoSizeColumn(20);
		   
		  
		   sheet.addMergedRegion(new CellRangeAddress(4,4,50,60));
		   cell =row.createCell(50);
		   cell.setCellValue("COMBINED MUSTER ROLL CUM REGISTER OF WAGES");
		   cell.setCellStyle(headerStyle1);
		   sheet.autoSizeColumn(50);
		   
		  
//**********************ROW 5TH*************************
		   row=sheet.createRow(5);
		   sheet.addMergedRegion(new CellRangeAddress(5,5,11,31));
		   cell =row.createCell(11);
		   cell.setCellValue("[See Rule 24 (9-B) of Karnataka Shops & Commercial Establishment Rules, 1963]");
		   cell.setCellStyle(headerStyle1);
		   sheet.autoSizeColumn(11);
		   
		  
		   sheet.addMergedRegion(new CellRangeAddress(5,5,50,60));
		   cell =row.createCell(50);
		   cell.setCellValue("[See Rule 24 (9-B) of Karnataka Shops & Commercial Establishment Rules, 1963]");
		   cell.setCellStyle(headerStyle1);
		   sheet.autoSizeColumn(50);
		   
//*******************row 6th*****************************			   
			 	row=sheet.createRow(6);
			 	sheet.addMergedRegion(new CellRangeAddress(6,6,0,3));
			   	cell =row.createCell(0);
				cell.setCellValue("Name And Address Of the Contractor: ");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(0);
				
				sheet.addMergedRegion(new CellRangeAddress(6,6,6,10));
				cell =row.createCell(6);
				cell.setCellValue("Name And Address Of the Establishment: ");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(6);
				
				sheet.addMergedRegion(new CellRangeAddress(6,6,11,31));
				cell =row.createCell(11);
				cell.setCellValue("in lieu of");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(11);
				
				sheet.addMergedRegion(new CellRangeAddress(6,6,41,49));
				cell =row.createCell(41);
				cell.setCellValue("Name And Address Of the Principle Employer: ");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(41);
				
				sheet.addMergedRegion(new CellRangeAddress(6,6,50,60));
				cell =row.createCell(50);
				cell.setCellValue("in lieu of");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(50);
				
//********************row 7th*****************************
				row=sheet.createRow(7);
				
				sheet.addMergedRegion(new CellRangeAddress(7,7,0,3));
			   	cell =row.createCell(0);
				cell.setCellValue("IMPACT INFOTECH PVT LTD (MIT PROJECTS) ");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(0);
				
				sheet.addMergedRegion(new CellRangeAddress(7,7,6,10));
				cell =row.createCell(6);
				cell.setCellValue("IMPACT INFOTECH PVT LTD (MIT PROJECTS)  ");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(6);
				
				sheet.addMergedRegion(new CellRangeAddress(7,7,11,31));
				cell =row.createCell(11);
				cell.setCellValue("1. Forms I & II of Rule 22(4); Form IV of Rule 28(2);Forms V and VII of Rule 29(1) and (5) of the Karnataka Minimum Wages rules 1958.");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(11);
				
				sheet.addMergedRegion(new CellRangeAddress(7,7,41,49));
				cell =row.createCell(41);
				cell.setCellValue("IMPACT INFOTECH PVT LTD (MIT PROJECTS)  ");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(41);
				
				sheet.addMergedRegion(new CellRangeAddress(7,7,50,60));
				cell =row.createCell(50);
				cell.setCellValue("1. Forms I & II of Rule 22(4); Form IV of Rule 28(2);Forms V and VII of Rule 29(1) and (5) of the Karnataka Minimum Wages rules 1958.");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(50);
			
//*******************row 8th***********************		
				row=sheet.createRow(8);
				sheet.addMergedRegion(new CellRangeAddress(8,8,0,3));
			   	cell =row.createCell(0);
				cell.setCellValue("FLAT NO.9, NISHIGANDHA APTS, PLOT NO.17 NAV RAJASTHAN CHS, S.B.ROAD, PUNE-411016.");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(0);
				
				sheet.addMergedRegion(new CellRangeAddress(8,8,6,10));
				cell =row.createCell(6);
				cell.setCellValue("FLAT NO.9, NISHIGANDHA APTS, PLOT NO.17 NAV RAJASTHAN CHS, S.B.ROAD, PUNE-411016 ");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(6);
				
				sheet.addMergedRegion(new CellRangeAddress(8,8,11,31));
				cell =row.createCell(11);
				cell.setCellValue("1. Forms I & II of Rule 22(4); Form IV of Rule 28(2);Forms V and VII of Rule 29(1) and (5) of the Karnataka Minimum Wages rules 1958.");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(11);
				
				sheet.addMergedRegion(new CellRangeAddress(8,8,41,49));
				cell =row.createCell(41);
				cell.setCellValue("FLAT NO.9, NISHIGANDHA APTS, PLOT NO.17 NAV RAJASTHAN CHS, S.B.ROAD, PUNE-411016.");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(41);
				
				sheet.addMergedRegion(new CellRangeAddress(8,8,50,60));
				cell =row.createCell(50);
				cell.setCellValue("2.Form I of Rule 3(1) of the Karmataka Payment Wages Rules,1963.");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(50);
				
//***********************row 9th***********************
				row=sheet.createRow(9);
				
				sheet.addMergedRegion(new CellRangeAddress(9,9,11,31));
				cell =row.createCell(11);
				cell.setCellValue("3. Form XIII of Rule 75;Forms XV,XVII,XX,XXI,XXII and XXIII of rule 78(1)(a)(i),(ii) and (iii) of the Contract labour");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(11);
				
				sheet.addMergedRegion(new CellRangeAddress(9,9,50,60));
				cell =row.createCell(50);
				cell.setCellValue("3. Form XIII of Rule 75;Forms XV,XVII,XX,XXI,XXII and XXIII of rule 78(1)(a)(i),(ii) and (iii) of the Contract labour");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(50);
			
//**********************row 10th***********************				
				row=sheet.createRow(10);
				
				sheet.addMergedRegion(new CellRangeAddress(10,10,11,31));
				cell =row.createCell(11);
				cell.setCellValue("( Regulation and Abolition) (Karnataka ) Rules,1974");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(11);
				
				sheet.addMergedRegion(new CellRangeAddress(10,10,50,60));
				cell =row.createCell(50);
				cell.setCellValue("( Regulation and Abolition) (Karnataka ) Rules,1974");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(50);
			
//********************row 11th**************************
				row=sheet.createRow(11);
				sheet.addMergedRegion(new CellRangeAddress(11,11,11,31));
				cell =row.createCell(11);
				cell.setCellValue("4. Form XIII of Rule 43;Forms XVII,XVIII,XIX,XX,XXI and XXII of Rule 46(2)(a),( c) and (d)  of inter State Migrant Workmen");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(11);
				
				sheet.addMergedRegion(new CellRangeAddress(11,11,50,60));
				cell =row.createCell(50);
				cell.setCellValue("4. Form XIII of Rule 43;Forms XVII,XVIII,XIX,XX,XXI and XXII of Rule 46(2)(a),( c) and (d)  of inter State Migrant Workmen");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(50);
				
//********************row 12th****************************
				row=sheet.createRow(12);
				sheet.addMergedRegion(new CellRangeAddress(12,12,11,31));
				cell =row.createCell(11);
				cell.setCellValue("(Regulation of Employment and Conditions of Service)Karnataka Rules, 1981");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(11);
				
				sheet.addMergedRegion(new CellRangeAddress(12,12,50,60));
				cell =row.createCell(50);
				cell.setCellValue("(Regulation of Employment and Conditions of Service)Karnataka Rules, 1981");
				cell.setCellStyle(headerStyle1);
				sheet.autoSizeColumn(50);

//******************row 13th*******************************
			    row=sheet.createRow(13);
			    sheet.addMergedRegion(new CellRangeAddress(13,13,0,2));
				cell =row.createCell(0);
				cell.setCellValue("Nature and Location of Work ");
				cell.setCellStyle(headerStyle1);
				
			   sheet.addMergedRegion(new CellRangeAddress(13,13,4,5));
				cell =row.createCell(4);
				cell.setCellValue("Desktop Engineer/Desktop Engineer");
				cell.setCellStyle(headerStyle1);
				
				cell =row.createCell(7);
				cell.setCellValue("Wages Period:");
				cell.setCellStyle(headerStyle1);
				
				cell =row.createCell(8);
				cell.setCellValue(currentMonth);
				cell.setCellStyle(headerStyle1);
				
//***********15th row***********************************//    
		     row = sheet.createRow(15);
		     
			 cell = row.createCell(0);
		     cell.setCellValue("Sr.No.");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(0);
		     
		     cell = row.createCell(1);
		     cell.setCellValue("EMPLOYEE ID");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(1);
		     
		     cell = row.createCell(2);
		     cell.setCellValue("EMPLOYEE NAME");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(2);
		     
		     cell = row.createCell(3);
		     cell.setCellValue("FATHER/HUSBAND NAME");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(3);
		   
			 cell = row.createCell(4);
		     cell.setCellValue("GENDER");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(4);
		     
		     cell = row.createCell(5);
		     cell.setCellValue("DEPARTMENT/DESIGNATION");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(5);
		     
		     cell = row.createCell(6);
		     cell.setCellValue("DATE OF JOINNING");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(6);
		     
		     cell = row.createCell(7);
		     cell.setCellValue("ESI NO.");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(7);	 
			
		     cell = row.createCell(8);
		     cell.setCellValue("PF NO.");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(8);
		     
		     cell = row.createCell(9);
		     cell.setCellValue("WAGES FIXED INCLUDING VDA");
		     cell.setCellStyle(tableheaderStyle1);
		     sheet.autoSizeColumn(9);	
		     
		     sheet.addMergedRegion(new CellRangeAddress(15,17,0,0));
		     sheet.addMergedRegion(new CellRangeAddress(15,17,1,1));
		     sheet.addMergedRegion(new CellRangeAddress(15,17,2,2));
		     sheet.addMergedRegion(new CellRangeAddress(15,17,3,3));
		     sheet.addMergedRegion(new CellRangeAddress(15,17,4,4));
		     sheet.addMergedRegion(new CellRangeAddress(15,17,5,5));
		     sheet.addMergedRegion(new CellRangeAddress(15,17,6,6));
		     sheet.addMergedRegion(new CellRangeAddress(15,17,7,7));
		     sheet.addMergedRegion(new CellRangeAddress(15,17,8,8));
		     sheet.addMergedRegion(new CellRangeAddress(15,17,9,9));
		   
		     if(alDates!=null && alDates.size() > 0)
		     {
		    	 startColmSizefirstRow=10;
		    	 lastColmSizefirstRow=alDates.size()+9;
		    	 sheet.addMergedRegion(new CellRangeAddress(15,15,startColmSizefirstRow,lastColmSizefirstRow));
			    
		    	 cell = row.createCell(startColmSizefirstRow);
		    	 cell.setCellValue("ATTENDANCE");
		    	 cell.setCellStyle(tableheaderStyle1);
		    	 sheet.autoSizeColumn(startColmSizefirstRow);	
		    
		    	 startColmSizefirstRow=lastColmSizefirstRow+1;
		    	 sheet.addMergedRegion(new CellRangeAddress(15,17,startColmSizefirstRow,startColmSizefirstRow));
		    	 cell = row.createCell(startColmSizefirstRow);
		    	 cell.setCellValue("NO. OF PAYABLE DAYS ");
		    	 cell.setCellStyle(tableheaderStyle1);
		    	 sheet.autoSizeColumn(startColmSizefirstRow);
		    
		    	 startColmSizefirstRow++;
		    	 sheet.addMergedRegion(new CellRangeAddress(15,17,startColmSizefirstRow,startColmSizefirstRow));
		    	 cell = row.createCell(startColmSizefirstRow);
		    	 cell.setCellValue("DATE OF SUSPENSION IF ANY");
		    	 cell.setCellStyle(tableheaderStyle1);
		    	 sheet.autoSizeColumn(startColmSizefirstRow);
		      
		     
		     startColmSizefirstRow++;


		     if(hmSalaryDetails!=null && hmSalaryDetails.size()>0)
		     {
		    	 List<ComparatorWeight> alEarnings = (List<ComparatorWeight>) request.getAttribute("alEarnings");
		    	 if (alEarnings == null) alEarnings = new ArrayList<ComparatorWeight>();
		    	 List<ComparatorWeight> alDeductions = (List<ComparatorWeight>) request.getAttribute("alDeductions");
		    	 if (alDeductions == null) alDeductions = new ArrayList<ComparatorWeight>();
		     
		    	 if(alEarnings!=null && alEarnings.size()>0)
		    	 {
		    	 //System.out.println("alEarnings.size()"+alEarnings.size());
		    	 //System.out.println("alDeductions.size()"+alDeductions.size());
		 	
		    	 int sallarySize=alEarnings.size()+alDeductions.size();
		    	// System.out.println("sallarySize in excel"+sallarySize);
		    	 lastColmSizefirstRow=sallarySize+startColmSizefirstRow;
		 	 
		    	 sheet.addMergedRegion(new CellRangeAddress(15,15,startColmSizefirstRow,lastColmSizefirstRow));
		    	 cell = row.createCell(startColmSizefirstRow);
		    	 cell.setCellValue(currentMonth);
		    	 cell.setCellStyle(tableheaderStyle1);
		    	 sheet.autoSizeColumn(startColmSizefirstRow);
		    	 
		    	 startColmSizefirstRow=lastColmSizefirstRow+1;
		    	 sheet.addMergedRegion(new CellRangeAddress(15,17,startColmSizefirstRow,startColmSizefirstRow));
		    	 cell = row.createCell(startColmSizefirstRow);
		    	 cell.setCellValue("TOTAL DEDUCTION");
		    	 cell.setCellStyle(tableheaderStyle1);
		    	 sheet.autoSizeColumn(startColmSizefirstRow);
		     
		    	 startColmSizefirstRow++;
		    	 sheet.addMergedRegion(new CellRangeAddress(15,17,startColmSizefirstRow,startColmSizefirstRow));
		    	 cell = row.createCell(startColmSizefirstRow);
		    	 cell.setCellValue("NET PAYABLE");
		    	 cell.setCellStyle(tableheaderStyle1);
		    	 sheet.autoSizeColumn(startColmSizefirstRow);
		 	 
		    	 startColmSizefirstRow++;
		    	 sheet.addMergedRegion(new CellRangeAddress(15,17,startColmSizefirstRow,startColmSizefirstRow));
		    	 cell = row.createCell(startColmSizefirstRow);
		    	 cell.setCellValue("MODE OF PAYMENT CASH/CHECK NO.");
		     	 cell.setCellStyle(tableheaderStyle1);
		     	 sheet.autoSizeColumn(startColmSizefirstRow);
		 	 
		        startColmSizefirstRow++;
		        sheet.addMergedRegion(new CellRangeAddress(15,17,startColmSizefirstRow,startColmSizefirstRow));
		        cell = row.createCell(startColmSizefirstRow);
		        cell.setCellValue("EMPLOYEE SIGNATURE/THUMB IMPRATION");
		        cell.setCellStyle(tableheaderStyle1);
		        sheet.autoSizeColumn(startColmSizefirstRow);
		   
		    }else{
			    	 //sheet.addMergedRegion(new CellRangeAddress(15,15,startColmSizefirstRow,lastColmSizefirstRow));
			    	 cell = row.createCell(startColmSizefirstRow);
			    	 cell.setCellValue(currentMonth);
			    	 cell.setCellStyle(tableheaderStyle1);
			    	 sheet.autoSizeColumn(startColmSizefirstRow);
			    	 
			    	 startColmSizefirstRow++;
			    	 cell = row.createCell(startColmSizefirstRow);
			    	 cell.setCellValue("TOTAL DEDUCTION");
			    	 cell.setCellStyle(tableheaderStyle1);
			    	 sheet.autoSizeColumn(startColmSizefirstRow);
			    	 
			    	 startColmSizefirstRow++;
			    	 cell = row.createCell(startColmSizefirstRow);
			    	 cell.setCellValue("NET PAYABLE");
			    	 cell.setCellStyle(tableheaderStyle1);
			    	 sheet.autoSizeColumn(startColmSizefirstRow);
			    	 
			    	 startColmSizefirstRow++;
			    	 cell = row.createCell(startColmSizefirstRow);
			    	 cell.setCellValue("MODE OF PAYMENT CASH/CHECK NO.");
			     	 cell.setCellStyle(tableheaderStyle1);
			     	 sheet.autoSizeColumn(startColmSizefirstRow);
			 	 
			     	 startColmSizefirstRow++;
			     	 cell = row.createCell(startColmSizefirstRow);
			         cell.setCellValue("EMPLOYEE SIGNATURE/THUMB IMPRATION");
			         cell.setCellStyle(tableheaderStyle1);
			         sheet.autoSizeColumn(startColmSizefirstRow);
			     }
		 	 
		//*******************16TH row**********************/ 	 
	    	 row = sheet.createRow(16);
		     for(int i=0;i<10;i++)
		     {
			     cell = row.createCell(i);
			     cell.setCellValue("");
			     cell.setCellStyle(tableheaderStyle1);
		     }  
		   
		     
		     startColmSizeScondRow=10;
		     lastColmSizeScondRow=startColmSizeScondRow+alDates.size();
		     int count=10;
		    // System.out.println("******alDates.size()"+alDates.size());
		     if(alDates!=null && alDates.size()>0){
		 		     for(int ii=0;ii<alDates.size();ii++){
		 		 sheet.addMergedRegion(new CellRangeAddress(16,17,count,count));
				 String strDate = uF.getDateFormat((String)alDates.get(ii), IConstants.DATE_FORMAT, "dd");
		    	 cell = row.createCell(count);
			     cell.setCellValue(uF.showData(strDate,""));
			     cell.setCellStyle(tableheaderStyle1);
			     sheet.autoSizeColumn(count);
			     count++;
		 		}
		     }

		     if(alEarnings!=null && alEarnings.size()>0){
		    	 
		    	 startColmSizeScondRow=lastColmSizeScondRow+2;
		    	// startColmSizeScondRow=count+2;
		    	 lastColmSizeScondRow=startColmSizeScondRow+alEarnings.size();
		    	 sheet.addMergedRegion(new CellRangeAddress(16,16,startColmSizeScondRow,lastColmSizeScondRow-1));
		    	 cell = row.createCell(startColmSizeScondRow);
		    	 cell.setCellValue("Earned Wages And Other Allowances");
		    	 cell.setCellStyle(tableheaderStyle1);
		    	 sheet.autoSizeColumn(startColmSizeScondRow);
		    
		    	 startColmSizeScondRow=lastColmSizeScondRow;
		    	 lastColmSizeScondRow=startColmSizeScondRow+alDeductions.size();
		    	 sheet.addMergedRegion(new CellRangeAddress(16,16,startColmSizeScondRow,lastColmSizeScondRow));
		    	 cell = row.createCell(startColmSizeScondRow);
		    	 cell.setCellValue("Deduction");
		    	 cell.setCellStyle(tableheaderStyle1);
		    	 sheet.autoSizeColumn(startColmSizeScondRow);
		     }else
		     {
		    	 startColmSizeScondRow=lastColmSizeScondRow+2;
		    	 cell = row.createCell(startColmSizeScondRow);
			     cell.setCellValue("Earned Wages And Other Allowances");
			     cell.setCellStyle(tableheaderStyle1);
			     sheet.autoSizeColumn(startColmSizeScondRow); 
			     
			     startColmSizeScondRow++;
			     cell = row.createCell(startColmSizeScondRow);
			     cell.setCellValue("Deduction");
			     cell.setCellStyle(tableheaderStyle1);
			     sheet.autoSizeColumn(startColmSizeScondRow);
		     }

		 //******************** 17TH row*************************/   
		     row = sheet.createRow(17);
		     for(int i=0;i<43;i++)
		     {
			     cell = row.createCell(i);
			     cell.setCellValue("");
			     cell.setCellStyle(tableheaderStyle1);
		     }
		     
		     startColmSizeThirdRow=alDates.size()+12;
		   
		 	 if(hmSalaryDetails!=null && hmSalaryDetails.size()>0){
		    	
		 	  if(alEarnings!=null && alEarnings.size()>0 && alDeductions!=null && alDeductions.size()>0)
		 	  {
				for (int ii = 0; alEarnings != null && ii < alEarnings.size(); ii++) {
					
				 cell = row.createCell(startColmSizeThirdRow);
			     cell.setCellValue(uF.showData((String) hmSalaryDetails.get(((ComparatorWeight) alEarnings.get(ii)).getStrName())+ "(+)",""));
			     cell.setCellStyle(tableheaderStyle1);
			     sheet.autoSizeColumn(startColmSizeThirdRow);
			     startColmSizeThirdRow++;
				}
			 
			     cell = row.createCell(startColmSizeThirdRow);
			     cell.setCellValue("Gross Salary");
			     cell.setCellStyle(tableheaderStyle1);
			     sheet.autoSizeColumn(startColmSizeThirdRow);
				
			     startColmSizeThirdRow++;
				for (int ii = 0; alDeductions != null && ii < alDeductions.size(); ii++){
					cell = row.createCell(startColmSizeThirdRow);
					   cell.setCellValue(uF.showData((String) hmSalaryDetails.get(((ComparatorWeight) alDeductions.get(ii)).getStrName())+ "(-)",""));
					     cell.setCellStyle(tableheaderStyle1);
					     	sheet.autoSizeColumn(startColmSizeThirdRow);
					     	 startColmSizeThirdRow++;
				}
		 	  }
		 	 }
		 	 else{
		 		//startColmSizeThirdRow= + 2;
		 		 cell = row.createCell(startColmSizeThirdRow);
			     cell.setCellValue("Earned Wages And Other Allowances");
			     cell.setCellStyle(tableheaderStyle1);
			     sheet.autoSizeColumn(startColmSizeThirdRow);
			     
			     startColmSizeThirdRow++;
			     cell = row.createCell(startColmSizeThirdRow);
			     cell.setCellValue("Gros Salary");
			     cell.setCellStyle(tableheaderStyle1);
			     sheet.autoSizeColumn(startColmSizeThirdRow);
			     
			     startColmSizeThirdRow++;
			     cell = row.createCell(startColmSizeThirdRow);
			     cell.setCellValue("Deduction");
			     cell.setCellStyle(tableheaderStyle1);
			     sheet.autoSizeColumn(startColmSizeThirdRow);
			   
			     for(int i=startColmSizeThirdRow;i<(startColmSizeThirdRow+4);i++)
			     {
				     cell = row.createCell(i);
				     cell.setCellValue("");
				     cell.setCellStyle(tableheaderStyle1);
			     } 
		 	 }
		     
		 	for(int i=startColmSizeThirdRow;i<(startColmSizeThirdRow+4);i++)
		     {
			     cell = row.createCell(i);
			     cell.setCellValue("");
			     cell.setCellStyle(tableheaderStyle1);
		     } 
		    }
		   }
		     
		  
		//*************main data***********//
		//****************start of 4rth row*********// 
			
         	 if(hmEmpData!=null && hmEmpData.size()>0 && empSalaryMap!=null && empSalaryMap.size()>0 && hmEmpAttendanceDataForExcel!=null && hmEmpAttendanceDataForExcel.size()>0 ){

         		Iterator<String> it= hmEmpData.keySet().iterator();
         		int countt=0;
         		int row1=17;
         		while(it.hasNext()) {
         			int count1=0;
         			countt++;
         			row1++;
         			String empid = it.next();
         			List<String> innerList = hmEmpData.get(empid); 	
         			
         			row=sheet.createRow(row1);
         			cell = row.createCell(0);
   			        cell.setCellValue(uF.showData(""+countt,""));
   			        cell.setCellStyle(borderStyle1);
		 	
   			        cell = row.createCell(1);
			        cell.setCellValue(uF.showData(innerList.get(0),""));
			        cell.setCellStyle(borderStyle1);
		    
			        cell = row.createCell(2);
			        cell.setCellValue(innerList.get(1));
			        cell.setCellStyle(borderStyle1);
			        
			        cell = row.createCell(3);
			        cell.setCellValue(uF.showData(innerList.get(2),""));
			        cell.setCellStyle(borderStyle1);
			        
			        cell = row.createCell(4);
			        cell.setCellValue(uF.showData(innerList.get(3),""));
			        cell.setCellStyle(borderStyle1);
			        
			        cell = row.createCell(5);
			        cell.setCellValue(uF.showData(innerList.get(4),"")+"/"+uF.showData(innerList.get(5),""));
			        cell.setCellStyle(borderStyle1);
			        
			        cell = row.createCell(6);
			        cell.setCellValue(uF.showData(innerList.get(6),""));
			        cell.setCellStyle(borderStyle1);
			        
			        cell = row.createCell(7);
			        cell.setCellValue(uF.showData(innerList.get(7),""));
			        cell.setCellStyle(borderStyle1);
			        
			        cell = row.createCell(8);
			        cell.setCellValue(uF.showData(innerList.get(8),""));
			        cell.setCellStyle(borderStyle1);
			        
			        cell = row.createCell(9);
    	 		    cell.setCellValue("");
    	 			 cell.setCellStyle(borderStyle1);
    	 			
			        if(hmEmpAttendanceDataForExcel!=null){
             	 		List<String>alInnerPrint=hmEmpAttendanceDataForExcel.get(empid);
             	 		for(int i=0; alInnerPrint != null && i<alInnerPrint.size(); i++){
             	 			 count1=i+10;
             	 			 cell = row.createCell(count1);
             	 			 cell.setCellValue(uF.showData(alInnerPrint.get(i),""));
             	 			 cell.setCellStyle(borderStyle1);
             	 		  }
             	 		}
			        
			        count1++;
			        cell = row.createCell(count1);
			        cell.setCellValue(innerList.get(9));
			        cell.setCellStyle(borderStyle1);
			        
			    	count1++;
			    	int countSalary=0;
						Map<String,String> salaryMap=empSalaryMap.get(empid);
						if(salaryMap!=null){
							for(int i1=0;i1<alEarningss.size();i1++){
								countSalary=count1+i1;
								cell = row.createCell(countSalary);
						        cell.setCellValue(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(salaryMap.get(alEarningss.get(i1)))));
						        cell.setCellStyle(borderStyle2);
						        countSalary++;
							}
							
							cell = row.createCell(countSalary);
					        cell.setCellValue(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(salaryMap.get("GROSS"))));
					        cell.setCellStyle(borderStyle2);
					        
					        countSalary++;
					        int countSalarydl=0;
					       
							for(int i1=0;i1<alDeductionss.size();i1++){ 
								countSalarydl=countSalary+i1;
							
								cell = row.createCell(countSalarydl);
						        cell.setCellValue(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(salaryMap.get(alDeductionss.get(i1)))));
						        cell.setCellStyle(borderStyle2);
						        countSalarydl++;
							}
							
							
							countSalary=countSalarydl;
							cell = row.createCell(countSalary);
					        cell.setCellValue(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(salaryMap.get("DEDUCTION"))));
					        cell.setCellStyle(borderStyle2);
					      
					        countSalary++;
							cell = row.createCell(countSalary);
					        cell.setCellValue(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(salaryMap.get("NET"))));
					        cell.setCellStyle(borderStyle2);
							
							}
					
					 
					 count1=countSalary+1;
					 cell = row.createCell(count1);
			         cell.setCellValue(uF.showData(innerList.get(10),""));
			         cell.setCellStyle(borderStyle1);
			         
			         count1++;
					 cell = row.createCell(count1);
			         cell.setCellValue("");
			         cell.setCellStyle(borderStyle1);
			         
         		}
         	}
		 	
		
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			try {
				workbook.write(buffer);
				buffer.close();
				
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			response.setHeader("Content-Disposition", "attachment; filename=\"MusterRollCumWagesRegisterExcelReport.xls\"");
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());

			try {
				ServletOutputStream op = response.getOutputStream();
				op = response.getOutputStream();
				op.write(buffer.toByteArray());
				op.flush();
				op.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		finally
		{
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
