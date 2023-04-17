package com.konnect.jpms.reports;

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
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillActivity;
import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ActivityHistoryReport extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(ActivityHistoryReport.class);
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel	;
	String strMonth;
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	
	List<FillMonth> monthList;
	List<FillYears> yearList;
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	private List<FillEmployee> empList;
	private String strEmpId;
	
	String exportType;

	String strActivity;
	List<FillActivity> activityList;
	
	String calendarYear; 
	List<FillCalendarYears> calendarYearList;
	    
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(PAGE, "/jsp/reports/ActivityHistoryReport.jsp");
		request.setAttribute(TITLE, "Activity History Report");
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}

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
		
		viewActivityHistoryReport(uF);

		return loadActivityHistoryReport(uF);

	}
	
	public String loadActivityHistoryReport(UtilityFunctions uF) {
		
		Map<String, String> hmOrg = CF.getOrgDetails(uF, getF_org(),request);
		if(hmOrg == null) hmOrg = new HashMap<String, String>();
		
		activityList = new FillActivity(request).fillActivity(true);
		
		monthList = new FillMonth().fillMonth();
//		yearList = new FillYears().fillYears(uF.getCurrentDate(CF.getStrTimeZone()),uF.getDateFormat(hmOrg.get("ORG_START_PAYCYCLE"), DATE_FORMAT));
		calendarYearList = new FillCalendarYears(request).fillCalendarYears(CF);
		
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
		empList = new FillEmployee(request).fillEmployeeNameOrgLocationDepartSBUDesigGrade(CF, getF_org(), getStrLocation(), getStrDepartment(), getStrSbu(), getStrLevel(), null, null, null, true);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
    
		alFilter.add("ACTIVITY");
		if(getStrActivity()!=null)  {
			String strAct="";
			int k=0;
			for(int i=0;activityList!=null && i<activityList.size();i++) {
				if(getStrActivity().equals(activityList.get(i).getActivityId())) {
					if(k==0) {
						strAct=activityList.get(i).getActivityName();
					} else {
						strAct+=", "+activityList.get(i).getActivityName();
					}
					k++;
				}
			}
			if(strAct!=null && !strAct.equals("")) {
				hmFilter.put("ACTIVITY", strAct);
			} else {
				hmFilter.put("ACTIVITY", "Not Selected");
			}
		} else {
			if(uF.parseToInt(getStrEmpId()) > 0) {
				hmFilter.put("ACTIVITY", "All Activities");
			} else {
				hmFilter.put("ACTIVITY", "Not Selected");
			}
		}
		
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++) {
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
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}
		
		
		alFilter.add("EMP");
		if(getStrEmpId()!=null)  {
			String strEmpName="";
			for(int i=0;empList!=null && i<empList.size();i++){
				if(getStrEmpId().equals(empList.get(i).getEmployeeId())) {
					strEmpName=empList.get(i).getEmployeeName();
				}
			}
			if(strEmpName!=null && !strEmpName.equals("")) {
				hmFilter.put("EMP", strEmpName);
			} else {
				hmFilter.put("EMP", "All Employee");
			}
		} else {
			hmFilter.put("EMP", "All Employee");
		}
		
		
		alFilter.add("CALENDARYEAR");
		String[] strCalendarYearDates = null;
		if (getCalendarYear() != null && getCalendarYear().equals("0")) {
			hmFilter.put("CALENDARYEAR", "Year not defined");
		} else if (getCalendarYear() != null) {
			strCalendarYearDates = getCalendarYear().split("-");
			setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
			hmFilter.put("CALENDARYEAR", uF.getDateFormat(strCalendarYearDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strCalendarYearDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		} else {
			strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
			setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
			hmFilter.put("CALENDARYEAR", uF.getDateFormat(strCalendarYearDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strCalendarYearDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		
		alFilter.add("MONTH");
		int nselectedMonth = uF.parseToInt(getStrMonth());
		String strMonth = uF.getMonth(nselectedMonth);
		hmFilter.put("MONTH", strMonth);
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public String viewActivityHistoryReport(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;
			String strD1 =  null;
			String strD2 = null;
			if (getCalendarYear() != null && getCalendarYear().equals("0")) {
				setStrMonth("0");
			} else if (getCalendarYear() != null && !getCalendarYear().equals("0") && getStrMonth() != null && getStrMonth().equals("0")) {
				strCalendarYearDates = getCalendarYear().split("-");
				strD1 = strCalendarYearDates[0];
				strD2 = strCalendarYearDates[1];
			} else {
				if (getCalendarYear() != null) {
					strCalendarYearDates = getCalendarYear().split("-");
					strCalendarYearStart = strCalendarYearDates[0];
					strCalendarYearEnd = strCalendarYearDates[1];
				} else if (uF.parseToInt(getStrEmpId()) == 0) {
					strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
					setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
					strCalendarYearStart = strCalendarYearDates[0];
					strCalendarYearEnd = strCalendarYearDates[1];
				}
				
				if(getStrMonth() == null) {
					setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
				}
				
				int nselectedMonth = uF.parseToInt(getStrMonth());
				int nFYSMonth = uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "MM"));
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
				if(nselectedMonth>=nFYSMonth) {
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "yyyy")));
				} else {
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, "yyyy")));
				}
				int nMonthStart = cal.getActualMinimum(Calendar.DATE);
				int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
				
				strD1 =  nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
				strD2 =  nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR);
			}
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmActivity = CF.getActivityName(con);
			if(hmActivity == null) hmActivity = new HashMap<String, String>();
			
			Map<String, String> hmDocActivity = CF.getDocActivityName(con);
			if(hmDocActivity == null) hmDocActivity = new HashMap<String, String>();
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			if(hmEmpName == null) hmEmpName = new HashMap<String, String>();
			Map<String, String> hmGradeMap = CF.getGradeMap(con);
			if (hmGradeMap == null) hmGradeMap = new HashMap<String, String>();
			Map<String, String> hmLevelMap = CF.getLevelMap(con);
			if(hmLevelMap == null) hmLevelMap = new HashMap<String, String>();
			Map<String, String> hmDesig = CF.getDesigMap(con);
			if(hmDesig == null) hmDesig = new HashMap<String, String>();
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
			if(hmWorkLocation == null) hmWorkLocation = new HashMap<String, Map<String, String>>();
			Map<String, String> hmDepartment = CF.getDepartmentMap(con, null, null);
			if(hmDepartment == null) hmDepartment = new HashMap<String, String>();
			Map<String, Map<String, String>> hmOrgMap = CF.getOrgDetails(con, uF);
			if(hmOrgMap == null) hmOrgMap = new HashMap<String, Map<String, String>>();
			Map<String, String> hmServices = CF.getServicesMap(con, false);
			if(hmServices == null) hmServices = new HashMap<String, String>();
			
//			System.out.println("minDays====>"+minDays+" ======maxDays======>"+maxDays);
//			System.out.println("strD1====>"+strD1+" ======strD2======>"+strD2);
			boolean isDocActivity = false;
			if(getStrActivity()!= null && hmDocActivity.containsKey(getStrActivity().trim())) {
				isDocActivity = true;
			}
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select b.document_id as b_document_id, b.emp_id as b_emp_id, b.emp_activity_id as emp_activity_id " +
				"from (select * from (SELECT * FROM employee_activity_details WHERE activity_id > 0 ");
			if(strD1 != null && strD2 != null) {
				sbQuery.append(" and effective_date between '"+uF.getDateFormat(strD1, DATE_FORMAT)+"' and '"+uF.getDateFormat(strD2, DATE_FORMAT)+"' ");
			}
			if(uF.parseToInt(getStrActivity()) > 0) {
				sbQuery.append(" and activity_id = "+getStrActivity()+" ");
            }
			if(uF.parseToInt(getStrEmpId()) > 0) {
                sbQuery.append(" and emp_id = "+getStrEmpId()+" ");
            }
			if(uF.parseToInt(getStrActivity()) == 0 && uF.parseToInt(getStrEmpId()) == 0) {
				sbQuery.append(" and activity_id = 0 and emp_id = 0 ");
			}
			sbQuery.append(" and emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id ");
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                	sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            } 
            
            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
                sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(")) aad JOIN activity_details ad ON aad.activity_id = ad.activity_id order by effective_date desc, emp_activity_id desc) a," +
				"document_activities b where a.emp_id = b.emp_id and a.effective_date = b.effective_date and a.emp_activity_id=b.emp_activity_id order by b.effective_date desc");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(getStrActivity()));
			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmEmpDoc = new HashMap<String, Map<String,String>>(); 
			while(rs.next()){
				Map<String, String> hmInner = (Map<String, String>) hmEmpDoc.get(rs.getString("emp_activity_id"));
				if(hmInner == null) hmInner = new HashMap<String, String>();
				
				hmInner.put("DOCUMENT_ID", rs.getString("b_document_id"));
				hmInner.put("EMP_ID", rs.getString("b_emp_id"));
				hmInner.put("EMP_ACTIVITY_ID", rs.getString("emp_activity_id"));
				
				hmEmpDoc.put(rs.getString("emp_activity_id"), hmInner);
			}
			rs.close();
			pst.close();
			
			
			sbQuery = new StringBuilder();
			sbQuery.append("select ead.*,epd.*, eod.org_id as eod_org_id, eod.grade_id as eod_grade_id from employee_activity_details ead, employee_personal_details epd, employee_official_details eod " +
				"where epd.emp_per_id = eod.emp_id and epd.emp_per_id = ead.emp_id and ead.emp_id = eod.emp_id and ead.activity_id > 0 ");
			if(strD1 != null && strD2 != null) {
				sbQuery.append(" and ead.effective_date between '"+uF.getDateFormat(strD1, DATE_FORMAT)+"' and '"+uF.getDateFormat(strD2, DATE_FORMAT)+"' ");
			}
			if(uF.parseToInt(getStrActivity()) > 0) {
                sbQuery.append(" and ead.activity_id = "+getStrActivity()+" ");
            }
			if(uF.parseToInt(getStrEmpId()) > 0) {
                sbQuery.append(" and ead.emp_id = "+getStrEmpId()+" ");
            }
			if(uF.parseToInt(getStrActivity()) == 0 && uF.parseToInt(getStrEmpId()) == 0) {
				sbQuery.append(" and ead.activity_id = 0 and ead.emp_id = 0 ");
			}
			if(getF_level()!=null && getF_level().length>0) {
                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                	sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
            
            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
                sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			
            sbQuery.append(" order by epd.emp_fname, epd.emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//			pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//			pst.setInt(3, uF.parseToInt(getStrActivity()));
			System.out.println("pst=======>"+pst);
			rs = pst.executeQuery();
			List<List<String>> reportList = new ArrayList<List<String>>();
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("empcode")); // 0
				
				String strMiddleName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				
				alInner.add(rs.getString("emp_fname") + strMiddleName + " " + rs.getString("emp_lname")); // 1
				alInner.add(uF.showData(hmActivity.get(rs.getString("activity_id")), "")); // 2 
				alInner.add(uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT)); //3 
				
				String strNoOfDays = "";
				if(uF.parseToInt(rs.getString("activity_id")) == uF.parseToInt(ACTIVITY_EXTEND_PROBATION_ID)){
					strNoOfDays = uF.showData(rs.getString("extend_probation_period"), "0");
				} else if(uF.parseToInt(rs.getString("activity_id")) == uF.parseToInt(ACTIVITY_NOTICE_PERIOD_ID)){
					strNoOfDays = uF.showData(rs.getString("notice_period"), "0");
				} else if(uF.parseToInt(rs.getString("activity_id")) == uF.parseToInt(ACTIVITY_PROBATION_ID)){
					strNoOfDays = uF.showData(rs.getString("probation_period"), "0");
				}	
				alInner.add(strNoOfDays); // 4
				alInner.add(uF.showData(rs.getString("reason"), ""));				// 5 
				alInner.add(uF.showData(rs.getString("increment_percent"), "0"));   // 6
				alInner.add(uF.showData(hmGradeMap.get(rs.getString("grade_id")), "")); // 7
				
				String strIncrType = "";
				if(uF.parseToInt(rs.getString("increment_type")) == 1){
					strIncrType = "Single";
				} else if(uF.parseToInt(rs.getString("increment_type")) == 2){
					strIncrType = "Double";
				} 
				alInner.add(strIncrType); // 8
				
				alInner.add(uF.showData(hmLevelMap.get(rs.getString("level_id")), "")); // 9
				alInner.add(uF.showData(hmDesig.get(rs.getString("desig_id")), "")); // 10
				
				if(rs.getString("transfer_type") != null && rs.getString("transfer_type").equals("WL")) {
					String strTransType = "Work Location";
					alInner.add(strTransType); // 11
					alInner.add(""); // 12
					Map<String, String> hm =  hmWorkLocation.get(rs.getString("wlocation_id"));
					if(hm == null) hm = new HashMap<String, String>();
					alInner.add(uF.showData(hm.get("WL_NAME"), "")); //13
					alInner.add("");  //14
					alInner.add("");  //15
					alInner.add("");  //16
					alInner.add("");  //17
					alInner.add("");  //18
				} else if(rs.getString("transfer_type") != null && rs.getString("transfer_type").equals("DEPT")) {
					String strTransType = "Department";
					alInner.add(strTransType); // 11
					alInner.add(""); // 12
					alInner.add(""); //13
					alInner.add("");  //14
					alInner.add(uF.showData(hmDepartment.get(rs.getString("department_id")), ""));  //15
					alInner.add("");  //16
					alInner.add("");  //17
					alInner.add("");  //18
				} else if(rs.getString("transfer_type") != null && rs.getString("transfer_type").equals("LE")) {
					String strTransType = "Legal Entity";
					alInner.add(strTransType); // 11
					
					Map<String, String> hmOrg =  hmOrgMap.get(rs.getString("org_id"));
					if(hmOrg == null) hmOrg = new HashMap<String, String>();
					alInner.add(uF.showData(hmOrg.get("ORG_NAME"), "")); //12
					
					Map<String, String> hm =  hmWorkLocation.get(rs.getString("wlocation_id"));
					if(hm == null) hm = new HashMap<String, String>();
					alInner.add(uF.showData(hm.get("WL_NAME"), "")); //13
					
					String serviceId = rs.getString("service_id")!= null && rs.getString("service_id").contains(",") ? rs.getString("service_id").substring(1,rs.getString("service_id").length()-1) : "";
					alInner.add(uF.showData(hmServices.get(serviceId), ""));  //14
					
					alInner.add(uF.showData(hmDepartment.get(rs.getString("department_id")), ""));  //15
					alInner.add(uF.showData(hmLevelMap.get(rs.getString("level_id")), ""));  //16
					alInner.add(uF.showData(hmDesig.get(rs.getString("desig_id")), ""));  //17
					alInner.add(uF.showData(hmGradeMap.get(rs.getString("grade_id")), ""));  //18
				} else {
					alInner.add(""); // 11
					alInner.add(""); // 12
					alInner.add(""); //13
					alInner.add("");  //14
					alInner.add("");  //15
					alInner.add("");  //16
					alInner.add("");  //17
					alInner.add("");  //18
				}
				alInner.add(uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));  //19
				alInner.add(uF.showData(hmEmpName.get(rs.getString("user_id")), ""));  //20
				
				if(isDocActivity){
					String strDoc = "";
					if(hmEmpDoc.containsKey(rs.getString("emp_activity_id"))){
						Map<String, String> hmInner = (Map<String, String>) hmEmpDoc.get(rs.getString("emp_activity_id"));
						if(hmInner == null) hmInner = new HashMap<String, String>();
						strDoc = "<a href=\"DownloadDocument.action?doc_id=" + hmInner.get("DOCUMENT_ID")+ "\"><i class=\"fa fa-file-pdf-o\" aria-hidden=\"true\" style=\"margin-left:5px;height:16px;float:right;\" ></i></a>";
					}
					alInner.add(strDoc);  //21
				} else {
					alInner.add("");  //21
				}
				
				Map<String, String> hmFitmentData = new HashMap<String, String>();
				String strEodLevelId = CF.getEmpLevelId(con, rs.getString("emp_per_id"));
				String strEodDesigId = CF.getEmpDesigId(con, rs.getString("emp_per_id"));
				String strEodOrgId = rs.getString("eod_org_id");
				if(rs.getInt("org_id")>0) {
					strEodOrgId = rs.getString("org_id");
				}
				if(rs.getInt("level_id")>0) {
					strEodLevelId = rs.getString("level_id");
				}
				if(rs.getInt("desig_id")>0) {
					strEodDesigId = rs.getString("desig_id");
				}
				String strEodGradeId = rs.getString("eod_grade_id");
				if(rs.getInt("grade_id")>0) {
					strEodGradeId = rs.getString("grade_id");
				}
				CF.getFitmentDetails(con, uF, CF, uF.parseToInt(strEodOrgId), uF.parseToInt(strEodLevelId), uF.parseToInt(strEodDesigId), hmFitmentData);
				String strScaleData = "N/A";
				if(hmFitmentData != null && hmFitmentData.size()>0) {
					strScaleData = uF.showData(hmFitmentData.get("MINSCALE"), "0") + "-" + uF.showData(hmFitmentData.get("INCREMENTAMOUNT"), "0") + "-" + uF.showData(hmFitmentData.get("MAXSCALE"), "0");
				}
				alInner.add(strScaleData);  //22
				alInner.add(hmDesig.get(strEodDesigId));  //23
				alInner.add(hmGradeMap.get(strEodGradeId));  //24
				
				reportList.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("reportList", reportList);
			request.setAttribute("isDocActivity", ""+isDocActivity);
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
	
	public Map<String,Map<String,String>> getLeaveDetails(String strDate1,String strDate2,UtilityFunctions uF){
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String,Map<String,String>> getMap=new HashMap<String,Map<String,String>>();
		try{
			con=db.makeConnection(con);
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
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return getMap;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
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

	public String getStrActivity() {
		return strActivity;
	}

	public void setStrActivity(String strActivity) {
		this.strActivity = strActivity;
	}

	public List<FillActivity> getActivityList() {
		return activityList;
	}

	public void setActivityList(List<FillActivity> activityList) {
		this.activityList = activityList;
	}

	public String getCalendarYear() {
		return calendarYear;
	}

	public void setCalendarYear(String calendarYear) {
		this.calendarYear = calendarYear;
	}

	public List<FillCalendarYears> getCalendarYearList() {
		return calendarYearList;
	}

	public void setCalendarYearList(List<FillCalendarYears> calendarYearList) {
		this.calendarYearList = calendarYearList;
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

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

}
