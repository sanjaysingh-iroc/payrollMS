package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.Employee;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AdditionalHoursE extends ActionSupport implements ServletRequestAware, IStatements {

	/** 
	 *  
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null; 
	String strEmpId = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(AdditionalHoursE.class);	
 
	public String execute() throws Exception {

		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(TITLE, TAdditionalHours);
		isEmpUserType = false;

		request.setAttribute(PAGE, PReportAdditionalHoursReportE); 

		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)
				 && !strUserType.equalsIgnoreCase(CEO) && !strUserType.equalsIgnoreCase(CFO)
				 && !strUserType.equalsIgnoreCase(HRMANAGER) && !strUserType.equalsIgnoreCase(ACCOUNTANT)
				 && !strUserType.equalsIgnoreCase(MANAGER)){
			request.setAttribute(PAGE, PAccessDenied);
			return ACCESS_DENIED;
		}else{
			
			String strP = (String)request.getParameter("param");
			
			if(getParam()==null){
				setParam("AHE");
				strP = getParam();
			}
			
			
			System.out.println("strP===>"+strP);
			System.out.println("getParam()===>"+getParam());
			
			
				employeeAdditionalHours(strP);
				return loadAdditionalHours();
		}

	}
	
	
	
	String param;
	String duration;
	
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	
	List<FillOrganisation> organisationList;
	String f_org;

	public String loadAdditionalHours() {
		UtilityFunctions uF=new UtilityFunctions();
		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		
		
		if(getParam()==null){
			setParam("AHE");
		}
		
		
		return LOAD;
	}

	

	public String employeeAdditionalHours(String strP) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {

		
			con = db.makeConnection(con);
			Map<String, String> hmIdNameMap = new HashMap<String, String>();
			
			if(strP!=null && strP.equalsIgnoreCase("AHWL")){
				hmIdNameMap = CF.getWLocationMap(con,strUserType, request, strEmpId);
			}else if(strP!=null && strP.equalsIgnoreCase("AHE")){
				hmIdNameMap = CF.getEmpNameMap(con, strUserType, strEmpId);
			}else if(strP!=null && strP.equalsIgnoreCase("AHS")){
				hmIdNameMap = CF.getServicesMap(con, true); 
			}else if(strP!=null && strP.equalsIgnoreCase("AHUT")){
				hmIdNameMap = CF.getUserTypeMap(con);
			}else if(strP!=null && strP.equalsIgnoreCase("AHD")){
				hmIdNameMap = CF.getDepartmentMap(con,strUserType,  strEmpId);
			}
			

//			Map<String, String> hmHolidays = new CommonFunctions(CF).getHolidayList();
//			Map<String, String> _hmHolidaysColour = new HashMap<String, String>();
//			List<String> _alHolidays = new ArrayList<String>();
			
			
			Map<String, String> hmTotal = new HashMap<String, String>();
			
			List<String> _allDates = new ArrayList<String>();
			List<String> alInnerChart = new ArrayList<String>();
			List<Employee> alId = new ArrayList<Employee>();
			List<String> alIdTemp = new ArrayList<String>();
			
			Map<String, Map<String, String>> hmAdditionalHours = new HashMap<String, Map<String, String>>();
			String[] strPayCycleDates = null;

			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycle(con, CF.getStrTimeZone(), CF);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
			}

			log.debug("Start ===> " + strPayCycleDates[0]);
			log.debug("End ===> " + strPayCycleDates[1]);

			pst = con.prepareStatement(selectDates);
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			rs = pst.executeQuery();

			while (rs.next()) {
				_allDates.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
				alInnerChart.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();

			if(strUserType!=null){
				
				
				if(strP!=null && strP.equalsIgnoreCase("AHWL")){
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select *, rd_eod.rempid as empl_id from attendance_details ad RIGHT JOIN (select *, rd.emp_id as rempid from roster_details rd, employee_official_details eod where eod.emp_id = rd.emp_id and _date between ? and ?) rd_eod ON rd_eod._date = TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.emp_id = rd_eod.rempid and ad.in_out='OUT'");

				/*	if(uF.parseToInt(getF_strWLocation())>0){
						sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
					}
					if(uF.parseToInt(getF_department())>0){
						sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));
					}
					if(uF.parseToInt(getF_level())>0){
						sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
					}*/
					if(getF_level()!=null && getF_level().length>0){
		                sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") )) ");
		            }
		            if(getF_department()!=null && getF_department().length>0){
		                sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where depart_id in ("+StringUtils.join(getF_department(), ",")+")) ");
		            }
		            
		            if(getF_service()!=null && getF_service().length>0){
		            	 sbQuery.append(" and ad.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
		            } 
		            
		            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
		                sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")) ");
		            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
						sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+"))");
					}
		            
		            if(uF.parseToInt(getF_org())>0){
						sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where org_id = "+uF.parseToInt(getF_org())+")");
					}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
						sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where org_id in ("+(String)session.getAttribute(ORG_ACCESS)+"))");
					}
					
					sbQuery.append("  order by wlocation_id");
//					pst = con.prepareStatement(selectAdditionalHoursWLocation);
					pst = con.prepareStatement(sbQuery.toString());
					
				}else if(strP!=null && strP.equalsIgnoreCase("AHE")){
					
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select *, rd_eod.rempid as empl_id from attendance_details ad RIGHT JOIN (select *, rd.emp_id as rempid from roster_details rd, employee_official_details eod where eod.emp_id = rd.emp_id and _date between ? and ?) rd_eod ON rd_eod._date = TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.emp_id = rd_eod.rempid and ad.in_out='OUT'");

					/*if(uF.parseToInt(getF_strWLocation())>0){
						sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
					}
					if(uF.parseToInt(getF_department())>0){
						sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));
					}
					if(uF.parseToInt(getF_level())>0){
						sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
					}*/
					if(getF_level()!=null && getF_level().length>0){
		                sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") )) ");
		            }
		            if(getF_department()!=null && getF_department().length>0){
		                sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where depart_id in ("+StringUtils.join(getF_department(), ",")+")) ");
		            }
		            
		            if(getF_service()!=null && getF_service().length>0){
		            	 sbQuery.append(" and ad.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
		            } 
		            
		            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
		                sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")) ");
		            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
						sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+"))");
					}
		            
		            if(uF.parseToInt(getF_org())>0){
						sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where org_id = "+uF.parseToInt(getF_org())+")");
					}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
						sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where org_id in ("+(String)session.getAttribute(ORG_ACCESS)+"))");
					}
					
					sbQuery.append(" order by empl_id");
//					pst = con.prepareStatement(selectAdditionalHoursEmp);
					pst = con.prepareStatement(sbQuery.toString());
					
				}else if(strP!=null && strP.equalsIgnoreCase("AHS")){
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select *, ad.service_id as serviceid from attendance_details ad RIGHT JOIN (select *, rd.emp_id as rempid from roster_details rd, employee_official_details eod where eod.emp_id = rd.emp_id and _date between ? and ?) rd_eod ON rd_eod._date = TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.emp_id = rd_eod.rempid and ad.in_out='OUT'");

					/*if(uF.parseToInt(getF_strWLocation())>0){
						sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
					}
					if(uF.parseToInt(getF_department())>0){
						sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));
					}
					if(uF.parseToInt(getF_level())>0){
						sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
					}*/
					if(getF_level()!=null && getF_level().length>0){
		                sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") )) ");
		            }
		            if(getF_department()!=null && getF_department().length>0){
		                sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where depart_id in ("+StringUtils.join(getF_department(), ",")+")) ");
		            }
		            
		            if(getF_service()!=null && getF_service().length>0){
		            	 sbQuery.append(" and ad.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
		            } 
		            
		            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
		                sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")) ");
		            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
						sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+"))");
					}
		            
		            if(uF.parseToInt(getF_org())>0){
						sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where org_id = "+uF.parseToInt(getF_org())+")");
					}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
						sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where org_id in ("+(String)session.getAttribute(ORG_ACCESS)+"))");
					}
					
					sbQuery.append(" order by ad.service_id");
//					pst = con.prepareStatement(selectAdditionalHoursService);
					pst = con.prepareStatement(sbQuery.toString());
					
				}else if(strP!=null && strP.equalsIgnoreCase("AHUT")){
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select *, rd_eod.rempid as empl_id from attendance_details ad RIGHT JOIN (select *, rd.emp_id as rempid from roster_details rd, employee_official_details eod, user_details ud where eod.emp_id=ud.emp_id and ud.emp_id=rd.emp_id and eod.emp_id = rd.emp_id and _date between ? and ?) rd_eod ON rd_eod._date = TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.emp_id = rd_eod.rempid and ad.in_out='OUT'");

					/*if(uF.parseToInt(getF_strWLocation())>0){
						sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
					}
					if(uF.parseToInt(getF_department())>0){
						sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));
					}
					if(uF.parseToInt(getF_level())>0){
						sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
					}*/
					if(getF_level()!=null && getF_level().length>0){
		                sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") )) ");
		            }
		            if(getF_department()!=null && getF_department().length>0){
		                sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where depart_id in ("+StringUtils.join(getF_department(), ",")+")) ");
		            }
		            
		            if(getF_service()!=null && getF_service().length>0){
		            	 sbQuery.append(" and ad.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
		            } 
		            
		            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
		                sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")) ");
		            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
						sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+"))");
					}
		            
		            if(uF.parseToInt(getF_org())>0){
						sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where org_id = "+uF.parseToInt(getF_org())+")");
					}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
						sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where org_id in ("+(String)session.getAttribute(ORG_ACCESS)+"))");
					}
					
					sbQuery.append(" order by usertype_id");
//					pst = con.prepareStatement(selectAdditionalHoursUserType);
					pst = con.prepareStatement(sbQuery.toString());
					
					
				}else if(strP!=null && strP.equalsIgnoreCase("AHD")){
					
					StringBuilder sbQuery = new StringBuilder();
					sbQuery.append("select *, rd_eod.rempid as empl_id from attendance_details ad RIGHT JOIN (select *, rd.emp_id as rempid from roster_details rd, employee_official_details eod where eod.emp_id = rd.emp_id and _date between ? and ?) rd_eod ON rd_eod._date = TO_DATE(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.emp_id = rd_eod.rempid and ad.in_out='OUT'");

					/*if(uF.parseToInt(getF_strWLocation())>0){
						sbQuery.append(" and wlocation_id = "+uF.parseToInt(getF_strWLocation()));
					}
					if(uF.parseToInt(getF_department())>0){
						sbQuery.append(" and depart_id = "+uF.parseToInt(getF_department()));
					}
					if(uF.parseToInt(getF_level())>0){
						sbQuery.append(" and grade_id in (select grade_id from designation_details dd, level_details ld, grades_details gd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id and ld.level_id = "+uF.parseToInt(getF_level())+")");
					}*/
					if(getF_level()!=null && getF_level().length>0){
		                sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") )) ");
		            }
		            if(getF_department()!=null && getF_department().length>0){
		                sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where depart_id in ("+StringUtils.join(getF_department(), ",")+")) ");
		            }
		            
		            if(getF_service()!=null && getF_service().length>0){
		            	 sbQuery.append(" and ad.service_id in ("+StringUtils.join(getF_service(), ",")+") ");
		            } 
		            
		            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
		                sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")) ");
		            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
						sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+"))");
					}
		            
		            if(uF.parseToInt(getF_org())>0){
						sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where org_id = "+uF.parseToInt(getF_org())+")");
					}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
						sbQuery.append(" and ad.emp_id in (select emp_id from employee_official_details where org_id in ("+(String)session.getAttribute(ORG_ACCESS)+"))");
					}
					
					sbQuery.append(" order by depart_id");
//					pst = con.prepareStatement(selectAdditionalHoursDepartment);
					pst = con.prepareStatement(sbQuery.toString());
					
				}
				
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));

			}
//			else if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
//				pst = con.prepareStatement(selectAdditionalHoursEmpManager);
//				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//				pst.setInt(3, uF.parseToInt((String) session.getAttribute("EMPID")));
//				
//			}
			rs = pst.executeQuery();

			
			
			System.out.println("pst===>"+pst);
			
			
			
			Map<String, String> hm = new HashMap<String, String>();
			String strId = null;
			String strName = null;
			while (rs.next()) {
				

				if(strP!=null && strP.equalsIgnoreCase("AHWL")){
					strId = rs.getString("wlocation_id");
					strName = (String)hmIdNameMap.get(strId);
				}else if(strP!=null && strP.equalsIgnoreCase("AHE")){
					strId = rs.getString("emp_id");
					strName = (String)hmIdNameMap.get(strId);
				}else if(strP!=null && strP.equalsIgnoreCase("AHS")){
					strId = rs.getString("serviceid");
					strName = (String)hmIdNameMap.get(strId);
				}else if(strP!=null && strP.equalsIgnoreCase("AHUT")){
					strId = rs.getString("usertype_id");
					strName = (String)hmIdNameMap.get(strId);
				}else if(strP!=null && strP.equalsIgnoreCase("AHD")){
					strId = rs.getString("depart_id");
					strName = (String)hmIdNameMap.get(strId);
				}
				
				
				hm = hmAdditionalHours.get(strId);
				if (hm == null) {
					hm = new HashMap<String, String>();
				}
				double dblWorkedHrs = rs.getDouble("hours_worked");
				double dblActualHrs = rs.getDouble("actual_hours");
				
				
				hm.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), ((dblWorkedHrs>dblActualHrs?uF.formatIntoTwoDecimal(dblWorkedHrs - dblActualHrs ):"0"))+"");
				hmAdditionalHours.put(strId, hm);

				if(alIdTemp!=null && !alIdTemp.contains(strId)){
					alIdTemp.add(strId);
					if(strName!=null){
						alId.add(new Employee(strName, strId));
					}
				}
			}
			rs.close();
			pst.close();
			Collections.sort(alId, new BeanComparator("strName"));
			

			
			
			
			
			List alReport = new ArrayList();
			List<String> alInner = new ArrayList<String>();
			
			
			
			for (int j = 0; j < alId.size(); j++) {
				 alInner = new ArrayList();
				 
				String strCol = ((j%2==0)?"dark":"light");
				Employee objAl = (Employee)alId.get(j);
				String strId1 = objAl.getStrEmpId();
				String strName1 = objAl.getStrName();
				
				
				Map hmAdditional = (Map)hmAdditionalHours.get(strId1);
				if(hmAdditional==null){
					hmAdditional = new HashMap();
				}
				

				alInner.add(strName1);
			
				for (int i = 0 ; i<_allDates.size(); i++) {
					
					alInner.add(uF.showData((String)hmAdditional.get((String)_allDates.get(i)),"0")) ;
					
					
					double nWFCount = uF.parseToDouble((String)hmTotal.get(i+""));
					double dblActual = uF.parseToDouble((String)hmAdditional.get((String)_allDates.get(i)));
					
					nWFCount +=uF.parseToDouble(uF.formatIntoOneDecimal(dblActual));
					hmTotal.put(i+"", uF.formatIntoOneDecimal(nWFCount)); 
					
				}
			
				alReport.add(alInner);
			
			}
			
			
			
			
			
			request.setAttribute("hmTotal", hmTotal);
			request.setAttribute("alReport", alReport);
			request.setAttribute("_allDates", _allDates);
			request.setAttribute("hmAdditionalHours", hmAdditionalHours);			
			request.setAttribute("FROM", strPayCycleDates[0]);
			request.setAttribute("TO", strPayCycleDates[1]);
			request.setAttribute("alId", alId);
			request.setAttribute("hmIDNameMap", hmIdNameMap);

			
			//Charts ==>
						StringBuilder sbActualHours = new StringBuilder();
						StringBuilder sbActualPC = new StringBuilder();
						
						StringBuilder sbActualPieHours = new StringBuilder();
						
						
						
						for (int i = alInnerChart.size()-1 ; i>=0; i--) {
							
							log.debug("alInnerChart.get(i)==>"+alInnerChart.get(i));
							
							sbActualPC.append("'"+uF.getDateFormat(alInnerChart.get(i), CF.getStrReportDateFormat(), "dd/MM")+"'");
							
							if(i>0) 
								sbActualPC.append(",");
						}
						
						for (int j = 0; j < alId.size(); j++) {
							
							Employee objEmp = (Employee) alId.get(j);
							String strEmpId = (String) objEmp.getStrEmpId();
							String strEmpName = (String) objEmp.getStrName();
							
							sbActualHours.append(
									
									"{"+
									"name: '"+strEmpName+"',"+
									"data: ["
									
									);
							
							
							
//							sbRosterHours.append(
//									
//									"{"+
//									"name: '"+strEmpName+"',"+
//									"data: ["
//									
//									);

							for (int i = alInnerChart.size()-1 ; i>=0; i--) {
								
									String strDate = (String)alInnerChart.get(i);
								
									Map hmActualInner1 =  (Map)hmAdditionalHours.get(strEmpId);
									
									sbActualHours.append(
											uF.showData((String)hmActualInner1.get(strDate), 0 + "")
								    );
									
									if(i==alInnerChart.size()-1){
										sbActualPieHours.append("['"+strEmpName+"','"+uF.showData((String)hmActualInner1.get(strDate), 0 + "")+"']");
									}
									
								
									if (i>0) {
										sbActualHours.append(",");
									}
									
									if (i==0) {
										sbActualHours.append("]}");
									}
									
							}
							
							if(j<alId.size()-1) {
								sbActualHours.append(",");
								sbActualPieHours.append(",");
//								sbRosterHours.append(",");
							}
							
						}
						
						request.setAttribute("alPayCyclesChart", alInnerChart);
						request.setAttribute("sbActualHours", sbActualHours);
						request.setAttribute("sbActualPieHours", sbActualPieHours.toString());
						request.setAttribute("sbActualPC", sbActualPC.toString());
//						request.setAttribute("sbRosterHours", sbRosterHours);
						request.setAttribute("strP", strP);
						
			
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

	String strP;
	String paycycle;
	List<FillPayCycles> paycycleList;

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

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getStrP() {
		return strP;
	}

	public void setStrP(String strP) {
		this.strP = strP;
	}



	public String getParam() {
		return param;
	}



	public void setParam(String param) {
		this.param = param;
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



	public String getDuration() {
		return duration;
	}



	public void setDuration(String duration) {
		this.duration = duration;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}



	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
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



	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}



	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}



	public String getF_org() {
		return f_org;
	}



	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

}
