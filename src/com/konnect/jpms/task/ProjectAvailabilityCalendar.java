package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectAvailabilityCalendar extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType;
	String strSessionEmpId;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(ProjectAvailabilityCalendar.class);
	
	String paycycle;
	String strMonth;
	String strYear;
	
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillMonth> monthList;
	List<FillYears> yearList;
	List<FillWLocation> wLocationList;	
	List<FillPayCycles> payCycleList; 

	List<FillOrganisation> organisationList;
	String f_org;
	
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_level; 
	String[] f_service;
	List<FillLevel> levelList;
	
	String strProType;
	boolean poFlag;
	
	String btnSubmit;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession(); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(TITLE, "Resource Load");
		request.setAttribute(PAGE, PProjectAvailabilityCalendar);
		

		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		} 
		
		if(getF_org()==null) {
			setF_org((String)session.getAttribute(ORGID));
		}

		checkProjectOwner(uF);
		
		viewProjectAvailability(uF);

		return loadProjectAvailability(uF);

	}
	
	
	public String loadProjectAvailability(UtilityFunctions uF) {
		
		payCycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		monthList = new FillMonth().fillMonth();
		yearList = new FillYears().fillFutureYears(uF.getCurrentDate(CF.getStrTimeZone()), 2, 2);
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		
		getSelectedFilter(uF);
		
		if(getBtnSubmit() != null) {
			return SUCCESS;
		} else {
			return LOAD;
		}
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if(isPoFlag()) {
			alFilter.add("PROJECT_TYPE");
			if(getStrProType()!=null) {
				String strProType="";
				if(isPoFlag() && uF.parseToInt(getStrProType()) == 2) {
					strProType = "My Projects";
				}
				if(strProType!=null && !strProType.equals("")) {
					hmFilter.put("PROJECT_TYPE", strProType);
				} else {
					hmFilter.put("PROJECT_TYPE", "All Projects");
				}
			} else {
				hmFilter.put("PROJECT_TYPE", "All Projects");
			}
		}
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++){
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
			
		alFilter.add("MONTH");
		hmFilter.put("MONTH", uF.getMonth(uF.parseToInt(getStrMonth())));
		
		alFilter.add("YEAR");
		hmFilter.put("YEAR", getStrYear());
			
		String selectedFilter=CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	private void checkProjectOwner(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean poFlag = false;
		try{
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 14-10-2022===	
//			sbQuery.append("select * from projectmntnc pmc where project_owner=?");
			sbQuery.append("select * from projectmntnc pmc where project_owners like '%,"+strSessionEmpId+",%'");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
		//===end parvez date: 14-10-2022===	
			rs = pst.executeQuery();
			if(rs.next()) {
				poFlag = true;
			}
			rs.close();
			pst.close();
			
			setPoFlag(poFlag);
			
			if(poFlag && uF.parseToInt(getStrProType()) == 0) {
				setStrProType("2");
			}
		} catch (Exception e) {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String viewProjectAvailability(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map hmEmpName = CF.getEmpNameMap(con, null, null);
			//Map hmEmpCode = CF.getEmpCodeMap(con);
			Map hmServiceMap =  CF.getServicesMap(con, true);
			
			String[] strPayCycleDates = null;
			String strPayCycleStart = null;
			String strPayCycleEnd = null;

			if (getPaycycle() != null) {
				
				strPayCycleDates = getPaycycle().split("-");
				strPayCycleStart = strPayCycleDates[0];
				strPayCycleEnd = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF ,request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1]+ "-" + strPayCycleDates[2]);
				
				strPayCycleStart = strPayCycleDates[0];
				strPayCycleEnd = strPayCycleDates[1];
				 
			}
			
			if(getStrMonth() ==null) {
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
				setStrYear(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"))+"");
			}
			
			Map hmWeekEnds = CF.getWeekEndList(con);
			Map hmEmpWlocation = CF.getEmpWlocationMap(con);
			
			List<String> alDates = new ArrayList<String>();
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1 );
			cal.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
			
			int maxDays = cal.getActualMaximum(Calendar.DATE);
			int minDays = cal.getActualMinimum(Calendar.DATE);
			cal.set(Calendar.DAY_OF_MONTH, minDays);
			
			String strD1 = null;
			for(int i=0; i< maxDays; i++) {
				
				strD1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				
				
				alDates.add(uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT));
				
				cal.add(Calendar.DATE, 1);
			}
			
			
//			System.out.println("alDates===>"+alDates);
			
			List<String> alEmployees  = new ArrayList<String>();
			
			StringBuilder sbQuery = new StringBuilder();			
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true ");

			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2) {
			//===start parvez date: 14-10-2022===	
//				sbQuery.append(" and epd.emp_per_id in (select distinct(emp_id) from project_emp_details where pro_id in (select pro_id from projectmntnc where project_owner = "+uF.parseToInt(strSessionEmpId)+")) ");
				sbQuery.append(" and epd.emp_per_id in (select distinct(emp_id) from project_emp_details where pro_id in (select pro_id from projectmntnc where project_owners like '%,"+strSessionEmpId+",%')) ");
			//===end parvez date: 14-10-2022===	
			}
			if(getF_level()!=null && getF_level().length>0) {
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
			
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and supervisor_emp_id = "+strSessionEmpId+"");
			}
            if(getF_department()!=null && getF_department().length>0) {
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            
            if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                    sbQuery.append(" eod.service_id like '%,"+uF.parseToInt(getF_service()[i])+",%'");
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
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=====>"+pst);
			
			rs = pst.executeQuery();
			while(rs.next()) {
				if(!alEmployees.contains(rs.getString("emp_per_id"))) {
					alEmployees.add(rs.getString("emp_per_id"));
				}
			}
			rs.close();
			pst.close();
			
			
			
//			Map<String, String> hmEmpProjectStatusInner = new HashMap<String, String>();
//			Map hmEmpProjectStatus = new HashMap();
//			
//			Map<String, String> hmEmpProjectStatusInnerPrint = new HashMap<String, String>();
//			Map hmEmpProjectStatusPrint = new HashMap();
			
//			for(int i=0; i<alDates.size(); i++) {
//				hmEmpProjectStatusInner = new HashMap<String, String>();
//				hmEmpProjectStatusInnerPrint = new HashMap<String, String>();
//				
//				pst = con.prepareStatement("select count(*) as task_no, emp_id from activity_info where ? between start_date and deadline and approve_status = 'n' group by emp_id");
//				pst.setDate(1, uF.getDateFormat((String)alDates.get(i), DATE_FORMAT));
//				rs = pst.executeQuery();
//				
//				
//				while(rs.next()) {
//					if(uF.parseToInt(rs.getString("task_no"))>5) {
//						hmEmpProjectStatusInner.put(rs.getString("emp_id"), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: red;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation("+rs.getString("emp_id")+",'"+(String)alDates.get(i)+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
//						hmEmpProjectStatusInnerPrint.put(rs.getString("emp_id"), uF.parseToInt(rs.getString("task_no"))+"");
//					} else if(uF.parseToInt(rs.getString("task_no"))>=2) {
//						hmEmpProjectStatusInner.put(rs.getString("emp_id"), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: yellow;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation("+rs.getString("emp_id")+",'"+(String)alDates.get(i)+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
//						hmEmpProjectStatusInnerPrint.put(rs.getString("emp_id"), uF.parseToInt(rs.getString("task_no"))+"");
//					} else {
//						hmEmpProjectStatusInner.put(rs.getString("emp_id"), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation("+rs.getString("emp_id")+",'"+(String)alDates.get(i)+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
//						hmEmpProjectStatusInnerPrint.put(rs.getString("emp_id"), uF.parseToInt(rs.getString("task_no"))+"");
//					}
//				}
//				rs.close();
//				pst.close();
//				
//				hmEmpProjectStatus.put((String)alDates.get(i), hmEmpProjectStatusInner);
//				hmEmpProjectStatusPrint.put((String)alDates.get(i), hmEmpProjectStatusInnerPrint);
//			}
			
			
//			Map hmLeavesRegister = CF.getLeaveDates(con, (String)alDates.get(0), (String)alDates.get(alDates.size()-1), CF, null, false, null);
//			Map hmLeavesColour = new HashMap();
//			CF.getLeavesAttributes(con, uF, hmLeavesColour, null);
			 
			
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			List<List<String>> reportListPrint = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			List<String> alInnerPrint = new ArrayList<String>();
			
			for (int i=0; i<alEmployees.size(); i++) {
				
				String strEmpId = (String)alEmployees.get(i);
				
				alInner = new ArrayList<String>();
				alInnerPrint = new ArrayList<String>();
				
				
				alInner.add(uF.showData((String)hmEmpName.get((String)alEmployees.get(i)),""));
				alInnerPrint.add(uF.showData((String)hmEmpName.get((String)alEmployees.get(i)),""));
				

				
				
				
//				Map hmLeaves = (Map)hmLeavesRegister.get(strEmpId);
//				if(hmLeaves==null)hmLeaves = new HashMap();
				
				for (int ii=0; ii<alDates.size(); ii++) {
					
					Map<String, String> hmResourceTaskCount = getResourceTaskCountDaywise(con, (String)alDates.get(ii), strEmpId);
					
//					Map<String, String> hmInner  = (Map)hmEmpProjectStatus.get((String)alDates.get(ii));
//					if(hmInner==null)hmInner = new HashMap<String, String>();
//					
//					Map<String, String> hmInnerPrint  = (Map)hmEmpProjectStatusPrint.get((String)alDates.get(ii));
//					if(hmInner==null)hmInnerPrint = new HashMap<String, String>();
//					
//					String strWeekDay = uF.getDateFormat((String)alDates.get(ii), DATE_FORMAT, "EEEE");
//					if(strWeekDay!=null) {
//						strWeekDay = strWeekDay.toUpperCase();
//					}
			
//					String strWLocationId = (String)hmEmpWlocation.get((String)alEmployees.get(i));
//				
//					
//					java.util.Date dtDate = uF.getDateFormatUtil((String)alDates.get(ii), DATE_FORMAT);
//					java.util.Date dtCurrentDate = uF.getCurrentDate(CF.getStrTimeZone());
					
					
					
//					String strLeaveCode = (String)hmLeaves.get((String)alDates.get(ii));
//					String strColour = (String)hmLeavesColour.get(strLeaveCode);
					
					
//					if(strColour==null && hmWeekEnds.containsKey(strWeekDay+"_"+strWLocationId)) {
//						strColour = (String)hmWeekEnds.get(strWeekDay+"_"+strWLocationId);
//					}
					
					alInner.add(uF.showData(hmResourceTaskCount.get("TASK_COUNT_WITH_COLOR"), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\">&nbsp;</div>"));
					alInnerPrint.add(uF.showData(hmResourceTaskCount.get("TASK_COUNT"), "0"));
					
				}
				
				reportList.add(alInner);
				reportListPrint.add(alInnerPrint);
			}
			
//			System.out.println("hmLeavesRegister===>"+hmLeavesRegister);
//			System.out.println("alDates===>"+alDates);
			
			
			
			request.setAttribute("alDates", alDates);
			request.setAttribute("alEmployees", alEmployees);
			
			
			request.setAttribute("hmEmpWlocation", hmEmpWlocation);
			request.setAttribute("hmWeekEnds", hmWeekEnds);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmServiceMap", hmServiceMap);
			
			
			request.setAttribute("reportList", reportList);
			request.setAttribute("reportListPrint", reportListPrint);
			
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

	private Map<String, String> getResourceTaskCountDaywise(Connection con, String currDate, String strEmpId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> hmResourceTaskCount = new HashMap<String, String>();
		
		try {
//			pst = con.prepareStatement("select count(*) as task_no from activity_info where resource_ids like '%,"+strEmpId+",%' and ? between start_date and deadline ");
			pst = con.prepareStatement("select count(task_id) as task_no from activity_info where resource_ids like '%,"+strEmpId+",%' and " +
				"task_id not in (select parent_task_id from activity_info where resource_ids like '%,"+strEmpId+",%' and parent_task_id is not null) " +
				" and (completed < 100 or completed is null) and ? between start_date and deadline");
			pst.setDate(1, uF.getDateFormat(currDate, DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			
			while(rs.next()) {
				if(uF.parseToInt(rs.getString("task_no"))>5) {
					hmResourceTaskCount.put("TASK_COUNT_WITH_COLOR", "<div style=\"width: 100%; height: 100%; text-align: center; background-color: red;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation("+strEmpId+",'"+currDate+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
					hmResourceTaskCount.put("TASK_COUNT", uF.parseToInt(rs.getString("task_no"))+"");
				} else if(uF.parseToInt(rs.getString("task_no"))>=2) {
					hmResourceTaskCount.put("TASK_COUNT_WITH_COLOR", "<div style=\"width: 100%; height: 100%; text-align: center; background-color: yellow;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation("+strEmpId+",'"+currDate+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
					hmResourceTaskCount.put("TASK_COUNT", uF.parseToInt(rs.getString("task_no"))+"");
				} else if(uF.parseToInt(rs.getString("task_no"))>0) {
					hmResourceTaskCount.put("TASK_COUNT_WITH_COLOR", "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation("+strEmpId+",'"+currDate+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
					hmResourceTaskCount.put("TASK_COUNT", uF.parseToInt(rs.getString("task_no"))+"");
				} else {
					hmResourceTaskCount.put("TASK_COUNT_WITH_COLOR", "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\">"+uF.parseToInt(rs.getString("task_no"))+"</div>");
					hmResourceTaskCount.put("TASK_COUNT", uF.parseToInt(rs.getString("task_no"))+"");
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmLeavesRegister===>"+hmLeavesRegister);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally {
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
		return hmResourceTaskCount;

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

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
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

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}
	
	public String getStrProType() {
		return strProType;
	}
	
	public void setStrProType(String strProType) {
		this.strProType = strProType;
	}

	public boolean isPoFlag() {
		return poFlag;
	}

	public void setPoFlag(boolean poFlag) {
		this.poFlag = poFlag;
	}

	public String getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

}
