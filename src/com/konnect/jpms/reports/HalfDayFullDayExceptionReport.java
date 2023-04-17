package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.tms.AttendanceRegister;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class HalfDayFullDayExceptionReport extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	String strBaseUserType = null;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(AttendanceRegister.class);
	
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	
	private String paycycle;
	private String f_org;
	private String[] f_strWLocation;
	private String[] f_department;
	private String[] f_level;
	private String[] f_service;
	private String[] f_emptype;
	
	List<FillPayCycles> paycycleList ;
	private List<FillOrganisation> orgList;
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	private List<FillEmploymentType> empTypeList;
	private String strEmpType;
	
	private String strStartDate;
	private String strEndDate;
	private String exceptionStatus;
	
	private String exportType;
	    
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(TITLE, TAttendanceRegister);
		request.setAttribute(PAGE, PAttendanceRegister);

//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}

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
		if(getStrStartDate()!=null && getStrStartDate().equalsIgnoreCase("NULL")) {
			setStrStartDate(null);
			setStrEndDate(null);
		}		
		if(getStrEndDate()!=null && getStrEndDate().equalsIgnoreCase("NULL")) {
			setStrStartDate(null);
			setStrEndDate(null);
		}
		if(getStrStartDate()==null && getStrEndDate()==null) {
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
			int nMaxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
			int nMinDate = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
			
			setStrStartDate(uF.getDateFormat(nMinDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
			setStrEndDate(uF.getDateFormat(nMaxDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
		}
		viewHDFDException(uF);

		return loadHDFDException(uF);

	}
	
	public String loadHDFDException(UtilityFunctions uF) {

		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
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
		
		alFilter.add("STATUS");
		if(uF.parseToInt(getExceptionStatus())==1) { 
			hmFilter.put("STATUS", "Approved");
		} else if(uF.parseToInt(getExceptionStatus())== -1) {
			hmFilter.put("STATUS", "Denied");
		} else if(getExceptionStatus()!= null && !getExceptionStatus().equals("") && uF.parseToInt(getExceptionStatus())==0) {
			hmFilter.put("STATUS", "Pending");
		} else {
			hmFilter.put("STATUS", "All");
		}
		
		if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))){
			alFilter.add("PERIOD");
			hmFilter.put("PERIOD", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	public String viewHDFDException(UtilityFunctions uF) {
			
		int Counter =0;
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			String[] strPayCycleDates = null;
			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String,String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmEmpOrgId = CF.getEmpOrgIdList(con, uF);
			if(hmEmpOrgId == null) hmEmpOrgId = new HashMap<String, String>();
			Map<String, String> hmOrg = CF.getOrgName(con);
			if(hmOrg == null) hmOrg = new HashMap<String, String>();
			Map<String, String> hmWlocationMap = CF.getWLocationMap(con, null, null);
			if(hmWlocationMap == null) hmWlocationMap = new HashMap<String, String>();
			Map<String, String> hmEmpDept = CF.getEmpDepartmentMap(con);
			if(hmEmpDept == null) hmEmpDept = new HashMap<String, String>();
			Map<String, String> hmDeptMap = CF.getDeptMap(con);
			if(hmDeptMap == null) hmDeptMap = new HashMap<String, String>();
			
			Map<String,String> hmEmpTypeMap = CF.getEmpTypeMap(con);
			if(hmEmpTypeMap==null) hmEmpTypeMap = new HashMap<String, String>();
			
//			Map hmEmpCode = CF.getEmpCodeMap();
			Map<String,String> hmServiceMap =  CF.getServicesMap(con, true);
			Map<String, String> hmEmployementTypeMap = CF.getEmployementTypeMap();
			
			String strMonth = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"";
			String strYear = uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"))+"";
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
		
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
		
			alInnerExport.add(new DataStyle("Half Day Full Day Exception For Month of "+ uF.showData(uF.getMonth(uF.parseToInt(strMonth)), "")+" "+ uF.parseToInt(strYear), Element.ALIGN_CENTER, "NEW_ROMAN", 6, "0", "0", BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Sr.NO",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Organization",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Work Location",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Department",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("SBU",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			alInnerExport.add(new DataStyle("Employement Type",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			alInnerExport.add(new DataStyle("Total Days",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			reportListExport.add(alInnerExport);
			
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select er.emp_id,er._date,er.given_reason,er.in_out_type,er.status,er.service_id,er.hours_worked,er.approved_date,er.approved_user_type,ad.approval_emp_id," +
				"ad.approved,ad.in_out_timestamp_actual,ad.approval_reason,epd.empcode,eod.depart_id,eod.wlocation_id,eod.grade_id,eod.org_id," +
				"eod.emptype from exception_reason er, attendance_details ad, employee_personal_details epd, employee_official_details eod " +
				"where er.emp_id = ad.emp_id and er.emp_id = epd.emp_per_id and epd.emp_per_id = eod.emp_id and ad.in_out='OUT' and " +
				"er._date=to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') and  er._date between ? and ? and epd.joining_date <=? and " +
				"(employment_end_date is null or (employment_end_date >=? or employment_end_date between ? and ?)) ");
			if(strUserType != null && (strUserType.equalsIgnoreCase(MANAGER) || strBaseUserType.equalsIgnoreCase(HOD))){
				sbQuery.append("and emp_id in (select emp_id from employee_official_details where (supervisor_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+" or hod_emp_id="+uF.parseToInt((String) session.getAttribute(EMPID))+")) ");		
				if(getF_emptype()!=null && getF_emptype().length>0){
					sbQuery.append("and emp_id in (select emp_id from employee_official_details where emptype in ('"+StringUtils.join(getF_emptype(), "' ,'")+"') ");
				}
			} else {
				if(getF_level()!=null && getF_level().length>0){
	                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
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
			}
        	if(getF_emptype()!=null && getF_emptype().length>0){
				sbQuery.append("and eod.emptype in ('"+StringUtils.join(getF_emptype(), "' ,'")+"') ");
			}
        	if(getExceptionStatus() != null && !getExceptionStatus().equals("")) {
        		sbQuery.append("and er.status = "+getExceptionStatus());
        	}
            sbQuery.append(" order by emp_fname, emp_lname, er._date");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			List<List<String>> reportList = new ArrayList<List<String>>();
			int srNoCnt=0;
			while(rs.next()) {
				List<String> alInner = new ArrayList<String>();
				srNoCnt++;
				alInner.add(""+srNoCnt);
				alInner.add(uF.showData(rs.getString("empcode"), ""));
				alInner.add(hmEmpName.get(rs.getString("emp_id")));
				alInner.add(uF.showData(hmOrg.get(rs.getString("org_id")), ""));
				alInner.add(uF.showData(hmWlocationMap.get(rs.getString("wlocation_id")), ""));
				alInner.add(uF.showData(hmDeptMap.get(hmEmpDept.get(rs.getString("emp_id"))), ""));
				alInner.add(uF.showData(hmServiceMap.get(rs.getString("service_id")), ""));
				alInner.add(uF.showData(hmEmployementTypeMap.get(hmEmpTypeMap.get(rs.getString("emp_id"))), ""));
				alInner.add(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT_STR));
				String strExcptionType = "Full Day";
				if(rs.getString("in_out_type") != null && rs.getString("in_out_type").equals("HD")) {
					strExcptionType = "Half Day";
				}
				alInner.add(uF.showData(strExcptionType, ""));
				alInner.add(uF.getRoundOffValue(2, rs.getDouble("hours_worked")));
				String strStatus = "Pending";
				if(rs.getInt("status") == 1) {
					strStatus = "Approved";
				} else if(rs.getInt("status") == -1) {
					strStatus = "Denied";
				}
				alInner.add(uF.showData(strStatus, ""));
				String strUserType = null;
				if(rs.getInt("approved_user_type")>0) {
					strUserType = " [" +hmUserType.get(rs.getString("approved_user_type"))+ "]";
				}
				alInner.add(uF.showData(hmEmpName.get(rs.getString("approval_emp_id")), "") + uF.showData(strUserType, ""));
				alInner.add(uF.getDateFormat(rs.getString("approved_date"), DBDATE, DATE_FORMAT_STR));
				alInner.add(uF.showData(rs.getString("approval_reason"), ""));
				
				reportList.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", reportList);
			
			
		} catch (Exception e) {
			e.printStackTrace();
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

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
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

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
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

	public String getExceptionStatus() {
		return exceptionStatus;
	}

	public void setExceptionStatus(String exceptionStatus) {
		this.exceptionStatus = exceptionStatus;
	}
	
}