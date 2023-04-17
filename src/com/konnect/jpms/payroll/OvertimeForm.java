package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class OvertimeForm extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */  
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	  
	CommonFunctions CF = null; 
	String profileEmpId;
	 
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	
	String[] f_strWLocation; 
	String[] f_level;
	String[] f_department;  
	String[] f_service;
	String paycycle;
	String fromPage;
	

	List<FillPayCycles> paycycleList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;

	List<FillOrganisation> organisationList;
	String f_org;
	
	private static Logger log = Logger.getLogger(OvertimeForm.class);
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		strEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(USERTYPE);
		
		request.setAttribute(PAGE, "/jsp/payroll/OvertimeForm.jsp");
		request.setAttribute(TITLE, "Overtime");
		
//		System.out.println("in OvertimeForm class paycycle"+paycycle+"fromPage"+fromPage);
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
		if(getF_org()==null) {
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
		
		viewOvertime(uF);
		
		return loadOvertime(uF);
	}
	
	
	public String loadOvertime(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
			alFilter.add("ORGANISATION");
			if(getF_org()!=null) {
				String strOrg="";
				for(int i=0;organisationList!=null && i<organisationList.size();i++) {
					if(getF_org().equals(organisationList.get(i).getOrgId())) {
						strOrg=organisationList.get(i).getOrgName();
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
			
			alFilter.add("PAYCYCLE");	
			if(getPaycycle()!=null) {
				String strPayCycle="";
				int k=0;
				for(int i=0;paycycleList!=null && i<paycycleList.size();i++) {
					if(getPaycycle().equals(paycycleList.get(i).getPaycycleId())) {
						if(k==0) {
							strPayCycle=paycycleList.get(i).getPaycycleName();
						} else {
							strPayCycle+=", "+paycycleList.get(i).getPaycycleName();
						}
						k++;
					}
				}
				if(strPayCycle!=null && !strPayCycle.equals("")) {
					hmFilter.put("PAYCYCLE", strPayCycle);
				} else {
					hmFilter.put("PAYCYCLE", "All Paycycle");
				}
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
			
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
    }
	
	
public String viewOvertime(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			con = db.makeConnection(con);
			
			String []strPayCycleDates;
			if (getPaycycle() != null && !getPaycycle().equals("") && !getPaycycle().equalsIgnoreCase("null")) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, getF_org());
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}
			
			Map<String, String> hmEmpMap = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);
			
			pst = con.prepareStatement("select emp_id from payroll_generation where paid_from = ? and paid_to = ? and paycycle = ? group by emp_id order by emp_id ");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
			rs = pst.executeQuery();
			List<String> ckEmpPayList = new ArrayList<String>();
			while(rs.next()) {
				ckEmpPayList.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("ckEmpPayList", ckEmpPayList);			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select salary_head_id, amount, esd.emp_id from emp_salary_details esd, " +
					"(select max(effective_date) as max_date, emp_id from emp_salary_details where effective_date<=? " +
					"and emp_id in (select eod.emp_id from employee_personal_details epd, employee_official_details eod " +
					"where epd.emp_per_id = eod.emp_id and is_alive = true and joining_date<= ? ");
			if(getF_level()!=null && getF_level().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
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
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(") group by emp_id ) as b where esd.effective_date = b.max_date and b.emp_id = esd.emp_id and isdisplay = true " +
					"and salary_head_id not in ("+GROSS+","+CTC+") order by esd.emp_id, salary_head_id");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			Map hmSalaryList = new HashMap();
			List alSalaryList = new ArrayList();
			String strEmpIdOld = null;
			String strEmpIdNew = null;
			while(rs.next()) {
				strEmpIdNew = rs.getString("emp_id");
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)) {
					alSalaryList = new ArrayList();
				}
				
				alSalaryList.add(rs.getString("salary_head_id"));
				hmSalaryList.put(strEmpIdNew, alSalaryList);
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();

			sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true and joining_date<= ? ");
			if(getF_level()!=null && getF_level().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
//                    sbQuery.append(" eod.service_id in("+getF_service()[i]+")");
                	sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    if(i<getF_service().length-1) {
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            } 
            
            if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append(" and eod.emp_id in (select esd.emp_id from emp_salary_details esd, (select max(effective_date) as max_date, emp_id " +
            		" from emp_salary_details where isdisplay = true and is_approved=true and effective_date <=? group by emp_id ) as b where esd.effective_date = b.max_date " +
            		"and b.emp_id = esd.emp_id and isdisplay = true and is_approved=true and esd.salary_head_id=? and esd.effective_date <=?)");
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setDate(2,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, OVER_TIME);
			pst.setDate(4,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			List<List<String>> alEmpReport = new ArrayList<List<String>>();
			List<String> alEmpList = new ArrayList<String>();
			while(rs.next()) {
				
				List<String> alEmpReportInner = new ArrayList<String>();
				alEmpReportInner.add(rs.getString("emp_per_id"));
				alEmpReportInner.add(hmEmpMap.get(rs.getString("emp_per_id")));
				alEmpReportInner.add("");
				alEmpReportInner.add("");
				alEmpReportInner.add("");
				alEmpReportInner.add("");
				
				
				alEmpReport.add(alEmpReportInner);
				
				alEmpList.add(rs.getString("emp_per_id"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alEmpReport", alEmpReport);
			request.setAttribute("hmSalaryList", hmSalaryList);
			request.setAttribute("hmSalaryHeadsMap", hmSalaryHeadsMap);
			
			pst = con.prepareStatement("select * from overtime_individual_details where paid_from = ? and paid_to=? and pay_paycycle = ?");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
			rs = pst.executeQuery();
			Map<String,String> hmOvertime = new HashMap<String, String>();
			Map<String,String> hmOvertimeId = new HashMap<String, String>();
			Map<String,String> hmOvertimeValue = new HashMap<String, String>();
			while(rs.next()) {
				hmOvertime.put(rs.getString("emp_id"), rs.getString("is_approved"));
				hmOvertimeId.put(rs.getString("emp_id"), rs.getString("overtime_id"));
				hmOvertimeValue.put(rs.getString("emp_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("pay_amount"))));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmOvertime", hmOvertime);
			request.setAttribute("hmOvertimeId", hmOvertimeId);
			request.setAttribute("hmOvertimeValue", hmOvertimeValue);

			Map<String, String> hmEmpOverTimeHRsAmt = getEmpOverTimeHoursAmount(con,CF,uF,alEmpList,strPayCycleDates[0],strPayCycleDates[1],strPayCycleDates[2]);
			
			request.setAttribute("hmEmpOverTimeHRsAmt", hmEmpOverTimeHRsAmt);
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	
	
	private Map<String, String> getEmpOverTimeHoursAmount(Connection con, CommonFunctions CF, UtilityFunctions uF, List<String> alEmpList, String strD1, String strD2, String strPC) {
		Map<String, String> hmEmpOvertimeAmount = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			Date fromDate = uF.getDateFormat(strD1, DATE_FORMAT);
			Date toDate = uF.getDateFormat(strD2, DATE_FORMAT);
			
			List<String> dateList = new ArrayList<String>();
			dateList.add(strD1);			
			Calendar cal = Calendar.getInstance();
			cal.setTime(fromDate);
			while (cal.getTime().before(toDate)) {
			    cal.add(Calendar.DATE, 1);
			    int YEAR = cal.get(Calendar.YEAR);
				int MONTH = cal.get(Calendar.MONTH) + 1;
				int DAY = cal.get(Calendar.DAY_OF_MONTH);
				
				String strDate = (DAY < 10 ? "0"+DAY : DAY)+"/"+(MONTH < 10 ? "0"+MONTH : MONTH)+"/"+YEAR;
				dateList.add(strDate);
			}
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			if(hmFeatureStatus == null) hmFeatureStatus = new HashMap<String, String>();
			
			Map<String, String> hmEmpRosterHours = getEmpRosterHours(con, CF, uF, alEmpList, strD1, strD2, strPC);
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			if(hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String, String>();
			
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
			if(hmWorkLocation == null) hmWorkLocation = new HashMap<String, Map<String, String>>();
			
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, strD1, strD2, CF, uF,hmWeekEndHalfDates,null);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, strD1, strD2,alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEnds,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
			Map<String, Map<String,String>> hmEmpOverTimeHours = CF.getEmpOverTimeHours(con,CF,uF,strD1,strD2,strPC);
			if(hmEmpOverTimeHours == null) hmEmpOverTimeHours = new HashMap<String, Map<String,String>>();
			
			Map<String, Map<String,String>> hmEmpOverTimeLevelPolicy = CF.getEmpOverTimeLevelPolicy(con,CF,uF,strD1,strD2,strPC);
			if(hmEmpOverTimeLevelPolicy == null) hmEmpOverTimeLevelPolicy = new HashMap<String, Map<String,String>>();		
			
			Map<String,Set<String>> hmHolidayDates=CF.getHolidayList(con,request,uF,strD1, strD2);
			
			for(String strEmpId : alEmpList) {
				String strLevelId = hmEmpLevelMap.get(strEmpId);
				String strLocation = hmEmpWlocation.get(strEmpId);
				boolean boolSalCalStatus = CF.getEmpDisableSalaryCalculation(con, uF, strEmpId);
				
				Set<String> weeklyOffSet = null;
				if(alEmpCheckRosterWeektype.contains(strEmpId)) {
					weeklyOffSet = hmRosterWeekEndDates.get(strEmpId);
				} else {
					weeklyOffSet = hmWeekEnds.get(strLocation);
				}
				if(weeklyOffSet==null) weeklyOffSet=new HashSet<String>();
				
				Set<String> holidaySet = hmHolidayDates.get(strLocation);
				if(holidaySet==null) holidaySet=new HashSet<String>();
				
				double dblTotalOverTimeAmount = 0.0d;
				
				Map<String,Map<String,Map<String,String>>> hmSalaryDetails1 = new HashMap<String,Map<String,Map<String,String>>>();
				pst = con.prepareStatement("select * from salary_details where level_id in (select level_id from level_details where " +
						"level_id =?) and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by level_id, earning_deduction desc, salary_head_id, weight");
				pst.setInt(1, uF.parseToInt(strLevelId));
				rs = pst.executeQuery(); 
				while (rs.next()) {
					
					Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(rs.getString("level_id"));
					if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>(); 
					
					Map<String, String> hmInnerSal = new HashMap<String, String>();
					hmInnerSal.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					hmInnerSal.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
					hmInnerSal.put("SALARY_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
					hmInnerSal.put("SUB_SALARY_HEAD_ID", rs.getString("sub_salary_head_id"));
					hmInnerSal.put("SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));
					hmInnerSal.put("IS_CTC_VARIABLE", ""+uF.parseToBoolean(rs.getString("is_ctc_variable")));
					hmInnerSal.put("MULTIPLE_CALCULATION", rs.getString("multiple_calculation"));
					hmInnerSal.put("IS_ALIGN_WITH_PERK", ""+uF.parseToBoolean(rs.getString("is_align_with_perk")));
					hmInnerSal.put("IS_DEFAULT_CAL_ALLOWANCE", ""+uF.parseToBoolean(rs.getString("is_default_cal_allowance")));
					hmInnerSal.put("SALARY_TYPE", rs.getString("salary_type"));
					
					hmSalInner.put(rs.getString("salary_head_id"), hmInnerSal);
					
					hmSalaryDetails1.put(rs.getString("level_id"), hmSalInner);
				}
				rs.close();
				pst.close();
				
				Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(strLevelId);
				if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>(); 
//				System.out.println("OTF/588---dateList=="+dateList);
				for(int i = 0; i < dateList.size(); i++) {
					String strDate = dateList.get(i);
					
					String strOverTimeType = null;
					if(holidaySet!=null && holidaySet.contains(uF.getDateFormat(strDate, DATE_FORMAT, CF.getStrReportDateFormat()) )) {
						strOverTimeType = "PH";
						
						
						Map<String,String> hmOvertimePolicy = hmEmpOverTimeLevelPolicy.get(strLevelId+"_"+strOverTimeType);
						if(hmOvertimePolicy==null) hmOvertimePolicy=new HashMap<String, String>();
						
						String overtimePaymentType=hmOvertimePolicy.get("OVERTIME_PAYMENT_TYPE");
						String strCalcBasic =hmOvertimePolicy.get("CAL_BASIS");
						double dblAmount = uF.parseToDouble(hmOvertimePolicy.get("OVERTIME_PAYMENT_AMOUNT"));
						
						if(strCalcBasic!=null && strCalcBasic.equals("FD")) {
							Map<String,String> hmEmpOvertime = hmEmpOverTimeHours.get(strEmpId);
							if(hmEmpOvertime == null) hmEmpOvertime = new HashMap<String, String>();
							
							double dblOvertimeHours =uF.parseToDouble(hmEmpOvertime.get(strDate));
							if(dblOvertimeHours <= 0.0d) {
								continue;
							}
							
							if(overtimePaymentType!=null && overtimePaymentType.equals("A")) {
								dblTotalOverTimeAmount += dblAmount;
							} else {
								Map<String, String> hmHolidaysCnt = new HashMap<String, String>();
								Map<String, String> hmHolidayDate = new HashMap<String, String>();
								CF.getHolidayListCount(con,request,strD1, strD2, CF, hmHolidayDate, hmHolidaysCnt, weeklyOffSet, false);
								
								String diffInDays = uF.dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT, CF.getStrTimeZone());
								
								String salary_cal_basis = uF.showData(hmOvertimePolicy.get("DAY_CALCULATION"), "");
								double nWorkDays = 0.0d;
								if(salary_cal_basis!=null && "AWD".equalsIgnoreCase(salary_cal_basis)) {
									int nWeekEnd = weeklyOffSet.size();
									int nHolidayCnt = uF.parseToInt(hmHolidaysCnt.get(strLocation));
									nWorkDays = (uF.parseToDouble(diffInDays) - nWeekEnd) - nHolidayCnt;
								} else if(salary_cal_basis!=null && "AMD".equalsIgnoreCase(salary_cal_basis)) {
									nWorkDays = uF.parseToDouble(diffInDays);
								} else {
									nWorkDays = uF.parseToDouble(hmOvertimePolicy.get("FIXED_DAY_CALCULATION"));;
								}
								
								
								Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
								Map<String, Map<String, String>> hmEmpSalaryDetails = CF.getSalaryCalculation(con, hmInnerisDisplay, uF.parseToInt(strEmpId), nWorkDays, 0, 0, ((int)nWorkDays), 0, 0, strLevelId, uF, CF, strD1, hmSalInner, null, boolSalCalStatus+"");
								if(hmEmpSalaryDetails == null) hmEmpSalaryDetails = new HashMap<String, Map<String,String>>();
								
								String salaryHeadId=hmOvertimePolicy.get("SALARY_HEAD_ID");
								List<String> salaryHeadList=null;
								if(salaryHeadId!=null) {
									salaryHeadList=Arrays.asList(salaryHeadId.split(","));
								}
								
								double dblSubSalaryAmount = 0;
								for(int j=0;salaryHeadList!=null && !salaryHeadList.isEmpty() && j<salaryHeadList.size();j++) {
																
									Map<String,String> hmSubSalaryDetails = hmEmpSalaryDetails.get(salaryHeadList.get(j).trim());
									if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap<String,String>();
									dblSubSalaryAmount += uF.parseToDouble(hmSubSalaryDetails.get("AMOUNT"));					
								}
								dblTotalOverTimeAmount += (dblAmount * dblSubSalaryAmount) / 100;
							}
						} else if(strCalcBasic!=null && strCalcBasic.equals("H")) {
							
							Map<String,String> hmEmpOvertime = hmEmpOverTimeHours.get(strEmpId);
							if(hmEmpOvertime == null) hmEmpOvertime = new HashMap<String, String>();
							
							double dblOvertimeHours =uF.parseToDouble(hmEmpOvertime.get(strDate));
							double dblOverTimeCalcHours = 0;
							if("RH".equals(hmOvertimePolicy.get("STANDARD_WKG_HOURS"))) {
								dblOverTimeCalcHours = uF.parseToDouble(hmEmpRosterHours.get(strDate+"_"+strEmpId));
							} else if("SWH".equals(hmOvertimePolicy.get("STANDARD_WKG_HOURS"))) {
								Map<String, String> hmLocation = hmWorkLocation.get(strLocation);
								if(hmLocation == null) hmLocation = new HashMap<String, String>();
								
								Time t = uF.getTimeFormat(hmLocation.get("WL_START_TIME"), DBTIME);
								long long_startTime = t.getTime();
								Time t1 = uF.getTimeFormat(hmLocation.get("WL_END_TIME"), DBTIME);
								long long_endTime = t1.getTime();			
								dblOverTimeCalcHours = uF.parseToDouble(uF.getTimeDiffInHoursMins(long_startTime,long_endTime));
							} else {
								dblOverTimeCalcHours = uF.parseToDouble(hmOvertimePolicy.get("FIXED_STWKG_HOURS"));
							}
							if(dblOverTimeCalcHours <= 0.0d) {
								continue;
							}
							
							Map<String, String> hmHolidaysCnt = new HashMap<String, String>();
							Map<String, String> hmHolidayDate = new HashMap<String, String>();
							CF.getHolidayListCount(con,request, strD1, strD2, CF, hmHolidayDate, hmHolidaysCnt, weeklyOffSet, false);
							
							String diffInDays = uF.dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT, CF.getStrTimeZone());
							
							String salary_cal_basis = uF.showData(hmOvertimePolicy.get("DAY_CALCULATION"), "");
							double nWorkDays = 0.0d;
							if(salary_cal_basis!=null && "AWD".equalsIgnoreCase(salary_cal_basis)) {
								int nWeekEnd = weeklyOffSet.size();
								int nHolidayCnt = uF.parseToInt(hmHolidaysCnt.get(strLocation));
								nWorkDays = (uF.parseToDouble(diffInDays) - nWeekEnd) - nHolidayCnt;
							} else if(salary_cal_basis!=null && "AMD".equalsIgnoreCase(salary_cal_basis)) {
								nWorkDays = uF.parseToDouble(diffInDays);
							} else {
								nWorkDays = uF.parseToDouble(hmOvertimePolicy.get("FIXED_DAY_CALCULATION"));
							}
							
							if(overtimePaymentType!=null && overtimePaymentType.equals("A")) {
								dblTotalOverTimeAmount += dblOvertimeHours * dblAmount;
							} else {
								
								Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
								Map<String, Map<String, String>> hmEmpSalaryDetails = CF.getSalaryCalculation(con,hmInnerisDisplay,uF.parseToInt(strEmpId), nWorkDays, 0, 0, ((int)nWorkDays), 0, 0, strLevelId, uF, CF, strD1,hmSalInner, null, boolSalCalStatus+"");
								if(hmEmpSalaryDetails == null) hmEmpSalaryDetails = new HashMap<String, Map<String,String>>();
								
								String salaryHeadId = hmOvertimePolicy.get("SALARY_HEAD_ID");
								List<String> salaryHeadList = null;
								if(salaryHeadId != null) {
									salaryHeadList = Arrays.asList(salaryHeadId.split(","));
								}
								
								double dblSubSalaryAmount = 0;
								for(int j = 0; salaryHeadList != null && !salaryHeadList.isEmpty() && j < salaryHeadList.size(); j++) {
																
									Map<String,String> hmSubSalaryDetails = hmEmpSalaryDetails.get(salaryHeadList.get(j).trim());
									if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap<String,String>();
									dblSubSalaryAmount += uF.parseToDouble(hmSubSalaryDetails.get("AMOUNT"));					
								}
//								dblTotalOverTimeAmount += dblOvertimeHours * ((dblAmount * dblSubSalaryAmount) / (100 * dblOverTimeCalcHours));
								double dblTotalOverTime = (((((dblAmount * dblSubSalaryAmount)/100)/nWorkDays)/dblOverTimeCalcHours) * dblOvertimeHours);
								dblTotalOverTimeAmount += dblTotalOverTime;
//								if(uF.parseToInt(strEmpId) == 136) {
//									System.out.println("PH dblOvertimeHours====>"+dblOvertimeHours+"--dblAmount====>"+dblAmount+"--dblSubSalaryAmount====>"+dblSubSalaryAmount);
//									System.out.println("nWorkDays====>"+nWorkDays+"--dblOverTimeCalcHours====>"+dblOverTimeCalcHours+"--totalcal====>"+dblTotalOverTime+"---dblTotalOverTimeAmount====>"+dblTotalOverTimeAmount);
//								}
							}
						}
						
					} else if(weeklyOffSet!=null && weeklyOffSet.contains(strDate)) {
						strOverTimeType = "BH";
						
						
						Map<String,String> hmOvertimePolicy = hmEmpOverTimeLevelPolicy.get(strLevelId+"_"+strOverTimeType);
						if(hmOvertimePolicy==null) hmOvertimePolicy=new HashMap<String, String>();
						
						String overtimePaymentType=hmOvertimePolicy.get("OVERTIME_PAYMENT_TYPE");
						String strCalcBasic =hmOvertimePolicy.get("CAL_BASIS");
						double dblAmount = uF.parseToDouble(hmOvertimePolicy.get("OVERTIME_PAYMENT_AMOUNT"));
						
						if(strCalcBasic!=null && strCalcBasic.equals("FD")) {
							Map<String,String> hmEmpOvertime = hmEmpOverTimeHours.get(strEmpId);
							if(hmEmpOvertime == null) hmEmpOvertime = new HashMap<String, String>();
							
							double dblOvertimeHours =uF.parseToDouble(hmEmpOvertime.get(strDate));
							if(dblOvertimeHours <= 0.0d) {
								continue;
							}
							if(overtimePaymentType!=null && overtimePaymentType.equals("A")) {
								dblTotalOverTimeAmount += dblAmount;
							} else {
								Map<String, String> hmHolidaysCnt = new HashMap<String, String>();
								Map<String, String> hmHolidayDate = new HashMap<String, String>();
								CF.getHolidayListCount(con,request, strD1, strD2, CF, hmHolidayDate, hmHolidaysCnt, weeklyOffSet, false);
								
								String diffInDays = uF.dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT, CF.getStrTimeZone());
								
								String salary_cal_basis = uF.showData(hmOvertimePolicy.get("DAY_CALCULATION"), "");
								double nWorkDays = 0.0d;
								if(salary_cal_basis!=null && "AWD".equalsIgnoreCase(salary_cal_basis)) {
									int nWeekEnd = weeklyOffSet.size();
									int nHolidayCnt = uF.parseToInt(hmHolidaysCnt.get(strLocation));
									nWorkDays = (uF.parseToDouble(diffInDays) - nWeekEnd) - nHolidayCnt;
								} else if(salary_cal_basis!=null && "AMD".equalsIgnoreCase(salary_cal_basis)) {
									nWorkDays = uF.parseToDouble(diffInDays);
								} else {
									nWorkDays = uF.parseToDouble(hmOvertimePolicy.get("FIXED_DAY_CALCULATION"));;
								}
								
								Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
								Map<String, Map<String, String>> hmEmpSalaryDetails = CF.getSalaryCalculation(con, hmInnerisDisplay, uF.parseToInt(strEmpId), nWorkDays, 0, 0, ((int)nWorkDays), 0, 0, strLevelId, uF, CF, strD1, hmSalInner, null, boolSalCalStatus+"");
								if(hmEmpSalaryDetails == null) hmEmpSalaryDetails = new HashMap<String, Map<String,String>>();
								
								String salaryHeadId=hmOvertimePolicy.get("SALARY_HEAD_ID");
								List<String> salaryHeadList=null;
								if(salaryHeadId!=null) {
									salaryHeadList=Arrays.asList(salaryHeadId.split(","));
								}
								
								double dblSubSalaryAmount = 0;
								for(int j=0;salaryHeadList!=null && !salaryHeadList.isEmpty() && j<salaryHeadList.size();j++) {
																
									Map<String,String> hmSubSalaryDetails = hmEmpSalaryDetails.get(salaryHeadList.get(j).trim());
									if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap<String,String>();
									
									dblSubSalaryAmount += uF.parseToDouble(hmSubSalaryDetails.get("AMOUNT"));					
								}
								dblTotalOverTimeAmount += (dblAmount * dblSubSalaryAmount) / 100;
							}
						} else if(strCalcBasic!=null && strCalcBasic.equals("H")) {
							
							Map<String,String> hmEmpOvertime = hmEmpOverTimeHours.get(strEmpId);
							if(hmEmpOvertime == null) hmEmpOvertime = new HashMap<String, String>();
							
							double dblOvertimeHours =uF.parseToDouble(hmEmpOvertime.get(strDate));
							double dblOverTimeCalcHours = 0;
							if("RH".equals(hmOvertimePolicy.get("STANDARD_WKG_HOURS"))) {
								dblOverTimeCalcHours = uF.parseToDouble(hmEmpRosterHours.get(strDate+"_"+strEmpId));
							} else if("SWH".equals(hmOvertimePolicy.get("STANDARD_WKG_HOURS"))) {
//								dblOverTimeCalcHours = uF.parseToDouble(hmOvertimePolicy.get("FIXED_STWKG_HOURS"));
								Map<String, String> hmLocation = hmWorkLocation.get(strLocation);
								if(hmLocation == null) hmLocation = new HashMap<String, String>();
								
								Time t = uF.getTimeFormat(hmLocation.get("WL_START_TIME"), DBTIME);
								long long_startTime = t.getTime();
								Time t1 = uF.getTimeFormat(hmLocation.get("WL_END_TIME"), DBTIME);
								long long_endTime = t1.getTime();			
								dblOverTimeCalcHours = uF.parseToDouble(uF.getTimeDiffInHoursMins(long_startTime,long_endTime));
							} else {
								dblOverTimeCalcHours = uF.parseToDouble(hmOvertimePolicy.get("FIXED_STWKG_HOURS"));
							}
							if(dblOverTimeCalcHours <= 0.0d) {
								continue;
							}
							Map<String, String> hmHolidaysCnt = new HashMap<String, String>();
							Map<String, String> hmHolidayDate = new HashMap<String, String>();
							CF.getHolidayListCount(con,request, strD1, strD2, CF, hmHolidayDate, hmHolidaysCnt, weeklyOffSet, false);
							
							String diffInDays = uF.dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT, CF.getStrTimeZone());
							
							String salary_cal_basis = uF.showData(hmOvertimePolicy.get("DAY_CALCULATION"), "");
							double nWorkDays = 0.0d;
							if(salary_cal_basis!=null && "AWD".equalsIgnoreCase(salary_cal_basis)) {
								int nWeekEnd = weeklyOffSet.size();
								int nHolidayCnt = uF.parseToInt(hmHolidaysCnt.get(strLocation));
								nWorkDays = (uF.parseToDouble(diffInDays) - nWeekEnd) - nHolidayCnt;
							} else if(salary_cal_basis!=null && "AMD".equalsIgnoreCase(salary_cal_basis)) {
								nWorkDays = uF.parseToDouble(diffInDays);
							} else {
								nWorkDays = uF.parseToDouble(hmOvertimePolicy.get("FIXED_DAY_CALCULATION"));
							}
							
							if(overtimePaymentType!=null && overtimePaymentType.equals("A")) {
								dblTotalOverTimeAmount += dblOvertimeHours * dblAmount;
							} else {
								
								Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
								Map<String, Map<String, String>> hmEmpSalaryDetails = CF.getSalaryCalculation(con, hmInnerisDisplay, uF.parseToInt(strEmpId), nWorkDays, 0, 0, ((int)nWorkDays), 0, 0, strLevelId, uF, CF, strD1, hmSalInner, null, boolSalCalStatus+"");
								if(hmEmpSalaryDetails == null) hmEmpSalaryDetails = new HashMap<String, Map<String,String>>();
								
								String salaryHeadId = hmOvertimePolicy.get("SALARY_HEAD_ID");
								List<String> salaryHeadList = null;
								if(salaryHeadId != null) {
									salaryHeadList = Arrays.asList(salaryHeadId.split(","));
								}
								
								double dblSubSalaryAmount = 0;
								for(int j = 0; salaryHeadList != null && !salaryHeadList.isEmpty() && j < salaryHeadList.size(); j++) {
																
									Map<String,String> hmSubSalaryDetails = hmEmpSalaryDetails.get(salaryHeadList.get(j).trim());
									if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap<String,String>();
									dblSubSalaryAmount += uF.parseToDouble(hmSubSalaryDetails.get("AMOUNT"));					
								}
//								dblTotalOverTimeAmount += dblOvertimeHours * ((dblAmount * dblSubSalaryAmount) / (100 * dblOverTimeCalcHours));
								double dblTotalOverTime = (((((dblAmount * dblSubSalaryAmount)/100)/nWorkDays)/dblOverTimeCalcHours) * dblOvertimeHours);
								dblTotalOverTimeAmount += dblTotalOverTime;
								if(uF.parseToInt(strEmpId) == 83) {
									System.out.println("BH dblOvertimeHours====>"+dblOvertimeHours+"--dblAmount====>"+dblAmount+"--dblSubSalaryAmount====>"+dblSubSalaryAmount);
									System.out.println("nWorkDays====>"+nWorkDays+"--dblOverTimeCalcHours====>"+dblOverTimeCalcHours+"--totalcal====>"+dblTotalOverTime+"---dblTotalOverTimeAmount====>"+dblTotalOverTimeAmount);
								}
							}
						}
						
						
					} else {
						strOverTimeType = "EH";
						
						Map<String,String> hmOvertimePolicy = hmEmpOverTimeLevelPolicy.get(strLevelId+"_"+strOverTimeType);
						if(hmOvertimePolicy==null) hmOvertimePolicy=new HashMap<String, String>();
						
						String overtimePaymentType=hmOvertimePolicy.get("OVERTIME_PAYMENT_TYPE");
						String strCalcBasic =hmOvertimePolicy.get("CAL_BASIS");
						double dblAmount = uF.parseToDouble(hmOvertimePolicy.get("OVERTIME_PAYMENT_AMOUNT"));
						
						if(strCalcBasic!=null && strCalcBasic.equals("FD")) {
							Map<String,String> hmEmpOvertime = hmEmpOverTimeHours.get(strEmpId);
							if(hmEmpOvertime == null) hmEmpOvertime = new HashMap<String, String>();
							
							double dblOvertimeHours =uF.parseToDouble(hmEmpOvertime.get(strDate));
							if(dblOvertimeHours <= 0.0d) {
								continue;
							}
							if(overtimePaymentType!=null && overtimePaymentType.equals("A")) {
								dblTotalOverTimeAmount += dblAmount;
							} else {
								Map<String, String> hmHolidaysCnt = new HashMap<String, String>();
								Map<String, String> hmHolidayDate = new HashMap<String, String>();
								CF.getHolidayListCount(con,request, strD1, strD2, CF, hmHolidayDate, hmHolidaysCnt, weeklyOffSet, false);
								
								String diffInDays = uF.dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT, CF.getStrTimeZone());
								
								String salary_cal_basis = uF.showData(hmOvertimePolicy.get("DAY_CALCULATION"), "");
								double nWorkDays = 0.0d;
								if(salary_cal_basis!=null && "AWD".equalsIgnoreCase(salary_cal_basis)) {
									int nWeekEnd = weeklyOffSet.size();
									int nHolidayCnt = uF.parseToInt(hmHolidaysCnt.get(strLocation));
									nWorkDays = (uF.parseToDouble(diffInDays) - nWeekEnd) - nHolidayCnt;
								} else if(salary_cal_basis!=null && "AMD".equalsIgnoreCase(salary_cal_basis)) {
									nWorkDays = uF.parseToDouble(diffInDays);
								} else {
									nWorkDays = uF.parseToDouble(hmOvertimePolicy.get("FIXED_DAY_CALCULATION"));;
								}
								
								Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
								Map<String, Map<String, String>> hmEmpSalaryDetails = CF.getSalaryCalculation(con, hmInnerisDisplay, uF.parseToInt(strEmpId), nWorkDays, 0, 0, ((int)nWorkDays), 0, 0, strLevelId, uF, CF, strD1, hmSalInner, null, boolSalCalStatus+"");
								if(hmEmpSalaryDetails == null) hmEmpSalaryDetails = new HashMap<String, Map<String,String>>();
								
								String salaryHeadId=hmOvertimePolicy.get("SALARY_HEAD_ID");
								List<String> salaryHeadList=null;
								if(salaryHeadId!=null) {
									salaryHeadList=Arrays.asList(salaryHeadId.split(","));
								}
								
								double dblSubSalaryAmount = 0;
								for(int j=0;salaryHeadList!=null && !salaryHeadList.isEmpty() && j<salaryHeadList.size();j++) {
																
									Map<String,String> hmSubSalaryDetails = hmEmpSalaryDetails.get(salaryHeadList.get(j).trim());
									if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap<String,String>();
									dblSubSalaryAmount += uF.parseToDouble(hmSubSalaryDetails.get("AMOUNT"));					
								}
								dblTotalOverTimeAmount += (dblAmount * dblSubSalaryAmount) / 100;
							}
						} else if(strCalcBasic!=null && strCalcBasic.equals("H")) {
							
							Map<String,String> hmEmpOvertime = hmEmpOverTimeHours.get(strEmpId);
							if(hmEmpOvertime == null) hmEmpOvertime = new HashMap<String, String>();
							
							double dblOvertimeHours =uF.parseToDouble(hmEmpOvertime.get(strDate));
							double dblOverTimeCalcHours = 0;
							if("RH".equals(hmOvertimePolicy.get("STANDARD_WKG_HOURS"))) {
								dblOverTimeCalcHours = uF.parseToDouble(hmEmpRosterHours.get(strDate+"_"+strEmpId));
								
							} else if("SWH".equals(hmOvertimePolicy.get("STANDARD_WKG_HOURS"))) {
//								dblOverTimeCalcHours = uF.parseToDouble(hmOvertimePolicy.get("FIXED_STWKG_HOURS"));
								Map<String, String> hmLocation = hmWorkLocation.get(strLocation);
								if(hmLocation == null) hmLocation = new HashMap<String, String>();
								
								Time t = uF.getTimeFormat(hmLocation.get("WL_START_TIME"), DBTIME);
								long long_startTime = t.getTime();
								Time t1 = uF.getTimeFormat(hmLocation.get("WL_END_TIME"), DBTIME);
								long long_endTime = t1.getTime();			
								dblOverTimeCalcHours = uF.parseToDouble(uF.getTimeDiffInHoursMins(long_startTime,long_endTime));
							} else {
								dblOverTimeCalcHours = uF.parseToDouble(hmOvertimePolicy.get("FIXED_STWKG_HOURS"));
							}
							
							if(dblOverTimeCalcHours <= 0.0d) {
								continue;
							}
							
							Map<String, String> hmHolidaysCnt = new HashMap<String, String>();
							Map<String, String> hmHolidayDate = new HashMap<String, String>();
							CF.getHolidayListCount(con,request, strD1, strD2, CF, hmHolidayDate, hmHolidaysCnt, weeklyOffSet, false);
							
							String diffInDays = uF.dateDifference(strD1, DATE_FORMAT, strD2, DATE_FORMAT, CF.getStrTimeZone());
							
							String salary_cal_basis = uF.showData(hmOvertimePolicy.get("DAY_CALCULATION"), "");
							double nWorkDays = 0.0d;
							if(salary_cal_basis!=null && "AWD".equalsIgnoreCase(salary_cal_basis)) {
								int nWeekEnd = weeklyOffSet.size();
								int nHolidayCnt = uF.parseToInt(hmHolidaysCnt.get(strLocation));
								nWorkDays = (uF.parseToDouble(diffInDays) - nWeekEnd) - nHolidayCnt;
							} else if(salary_cal_basis!=null && "AMD".equalsIgnoreCase(salary_cal_basis)) {
								nWorkDays = uF.parseToDouble(diffInDays);
							} else {
								nWorkDays = uF.parseToDouble(hmOvertimePolicy.get("FIXED_DAY_CALCULATION"));
							}
							
							if(overtimePaymentType!=null && overtimePaymentType.equals("A")) {
								dblTotalOverTimeAmount += dblOvertimeHours * dblAmount;
							} else {
								
								Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
								Map<String, Map<String, String>> hmEmpSalaryDetails = CF.getSalaryCalculation(con, hmInnerisDisplay, uF.parseToInt(strEmpId), nWorkDays, 0, 0, ((int)nWorkDays), 0, 0, strLevelId, uF, CF, strD1, hmSalInner, null, boolSalCalStatus+"");
								if(hmEmpSalaryDetails == null) hmEmpSalaryDetails = new HashMap<String, Map<String,String>>();
								
								String salaryHeadId = hmOvertimePolicy.get("SALARY_HEAD_ID");
								List<String> salaryHeadList = null;
								if(salaryHeadId != null) {
									salaryHeadList = Arrays.asList(salaryHeadId.split(","));
								}
								
								double dblSubSalaryAmount = 0;
								for(int j = 0; salaryHeadList != null && !salaryHeadList.isEmpty() && j < salaryHeadList.size(); j++) {
																
									Map<String,String> hmSubSalaryDetails = hmEmpSalaryDetails.get(salaryHeadList.get(j).trim());
									if(hmSubSalaryDetails==null)hmSubSalaryDetails =new HashMap<String,String>();
									dblSubSalaryAmount += uF.parseToDouble(hmSubSalaryDetails.get("AMOUNT"));					
								}
//								dblTotalOverTimeAmount += dblOvertimeHours * ((dblAmount * dblSubSalaryAmount) / (100 * dblOverTimeCalcHours)); 
								
							//===start parvez date: 18-02-2023===	
//								double dblTotalOverTime = (((((dblAmount * dblSubSalaryAmount)/100)/nWorkDays)/dblOverTimeCalcHours) * dblOvertimeHours);
								double dblTotalOverTime = 0;
								double actualRosterHrs = 0;
								if(hmFeatureStatus !=null && uF.parseToBoolean(hmFeatureStatus.get(F_ROSTER_TIME_LESS_THAN_ONE_HOUR))){
									actualRosterHrs = uF.parseToDouble(hmEmpRosterHours.get(strDate+"_"+strEmpId));
								}
								if(uF.parseToInt(strEmpId)==82){
									System.out.println("OTF/999--actualRosterHrs=="+actualRosterHrs);
								}
								if(hmFeatureStatus !=null && uF.parseToBoolean(hmFeatureStatus.get(F_DIRECT_OVER_TIME_CALCULATE))){
									if(actualRosterHrs>1){
										dblTotalOverTime = (((((dblAmount * dblSubSalaryAmount)/100)/nWorkDays)/dblOverTimeCalcHours) * dblOvertimeHours);
									} else{
										dblTotalOverTime = (((((dblAmount * dblSubSalaryAmount)/100)/nWorkDays)/8) * dblOvertimeHours);
									}
								} else{
									dblTotalOverTime = (((((dblAmount * dblSubSalaryAmount)/100)/nWorkDays)/dblOverTimeCalcHours) * dblOvertimeHours);
								}
							//===end parvez date: 18-02-2023===	
								dblTotalOverTimeAmount += dblTotalOverTime;
								if(uF.parseToInt(strEmpId) == 82) {
									System.out.println("EH dblOvertimeHours====>"+dblOvertimeHours+"--dblAmount====>"+dblAmount+"--dblSubSalaryAmount====>"+dblSubSalaryAmount);
									System.out.println("nWorkDays====>"+nWorkDays+"--dblOverTimeCalcHours====>"+dblOverTimeCalcHours+"--totalcal====>"+dblTotalOverTime+"---dblTotalOverTimeAmount====>"+dblTotalOverTimeAmount);
								}
							}
						}
						
						
					}
					
				}
				
				hmEmpOvertimeAmount.put(strEmpId, uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblTotalOverTimeAmount));
				
			}
			
			
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
				
		return hmEmpOvertimeAmount;
	}



	private Map<String, String> getEmpRosterHours(Connection con, CommonFunctions CF, UtilityFunctions uF, List<String> alEmpList, String strD1, String strD2, String strPC) {
		PreparedStatement pst=null;
		ResultSet rs=null;
		Map<String, String> hmEmpRosterHours = new HashMap<String, String>();
		try{
			
			if(alEmpList.size() > 0) {
				StringBuilder sbEmp = null;
				for(String strEmp : alEmpList) {
					if(sbEmp == null) {
						sbEmp = new StringBuilder();
						sbEmp.append(strEmp);
					} else {
						sbEmp.append(","+strEmp);
					}
				}
				
				pst = con.prepareStatement("select * from attendance_details ad, roster_details rd where to_date(in_out_timestamp::text, 'YYYY-MM-DD') = _date " +
						"and ad.emp_id = rd.emp_id and  to_date(in_out_timestamp::text, 'YYYY-MM-DD') between ? and ? and ad.emp_id in ("+sbEmp.toString()+")" +
						" order by ad.emp_id, to_date(in_out_timestamp::text, 'YYYY-MM-DD')");
				pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
				rs = pst.executeQuery();  
				while(rs.next()) {
					hmEmpRosterHours.put(uF.getDateFormat(rs.getString("in_out_timestamp"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("emp_id"), rs.getString("actual_hours"));
				}
				rs.close();
				pst.close();
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
				
		return hmEmpRosterHours;
	}


	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
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
	
	public String getFromPage() {
		return fromPage;
	}


	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}


}
