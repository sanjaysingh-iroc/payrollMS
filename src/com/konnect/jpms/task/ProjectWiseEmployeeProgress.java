package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.payroll.reports.DepartmentwiseReport;
import com.konnect.jpms.select.FillClients;
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

public class ProjectWiseEmployeeProgress extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId;
	String strUserType; 
	       
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(DepartmentwiseReport.class);
	
	String f_org;
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_service;
	String[] f_level;
	String[] pro_id;
	String[] client;
	
	String paycycle;
	String strStartDate;
	String strEndDate;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillLevel> levelList;
	List<FillPayCycles> paycycleList;
	List<FillProjectList> projectdetailslist;
	List<FillClients> clientList;
	
	public String execute() throws Exception {
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/task/ProjectWiseEmployeeProgress.jsp");
		request.setAttribute(TITLE, "Project Wise Employee Progress");
		strEmpId =(String) session.getAttribute(EMPID);
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		
		if(getF_org() == null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		viewProjectWiseEmployeeProgress(uF);
//		System.out.println("poFlag ===>> " + poFlag);
		return loadProjectWiseEmployeeProgress(uF);

	}
	
	public String loadProjectWiseEmployeeProgress(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		
		projectdetailslist = new FillProjectList(request).fillProjectAllDetails();
		clientList = new FillClients(request).fillClients(false);
		
		getSelectedFilter(uF);
		 
		return SUCCESS;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
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
		
		alFilter.add("PROJECT");
		if(getPro_id()!=null) {
			String strProjects="";
			int k=0;
			for(int i=0;projectdetailslist!=null && i<projectdetailslist.size();i++) {
				for(int j=0;j<getPro_id().length;j++) {
					if(getPro_id()[j].equals(projectdetailslist.get(i).getProjectID())) {
						if(k==0) {
							strProjects=projectdetailslist.get(i).getProjectName();
						} else {
							strProjects+=", "+projectdetailslist.get(i).getProjectName();
						}
						k++;
					}
				}
			}
			if(strProjects!=null && !strProjects.equals("")) {
				hmFilter.put("PROJECT", strProjects);
			} else {
				hmFilter.put("PROJECT", "All Projects");
			}
		} else {
			hmFilter.put("PROJECT", "All Projects");
		}
		
		alFilter.add("CLIENT");
		if(getClient()!=null) {
			String strClient="";
			int k=0;
			for(int i=0; clientList!=null && i<clientList.size();i++) {
				for(int j=0;j<getClient().length;j++) {
					if(getClient()[j].equals(clientList.get(i).getClientId())) {
						if(k==0) {
							strClient=clientList.get(i).getClientName();
						} else {
							strClient+=", "+clientList.get(i).getClientName();
						}
						k++;
					}
				}
			}
			if(strClient!=null && !strClient.equals("")) {
				hmFilter.put("CLIENT", strClient);
			} else {
				hmFilter.put("CLIENT", "All Clients");
			}
		} else {
			hmFilter.put("CLIENT", "All Clients");
		}
		
		if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))){
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	private void viewProjectWiseEmployeeProgress(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, String> hmDeptName = CF.getEmpDepartmentNameMap(con);
//			Map<String, String> hmEmpGrossSalary = CF.getEmpGrossSalary(uF, CF, con, getStrStartDate(), "H");
//			Map<String, String> hmEmpNetHourlySalary = CF.getEmpNetSalary(uF, CF, con, uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE), "H",0);
			
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
			
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			
			StringBuilder sbQuery = new StringBuilder();
			Map<String, String> hmProjectData =  new HashMap<String, String>();
//			Map<String, List<String>> hmClientProId = new HashMap<String, List<String>>();
			
			sbQuery.append("select cd.client_id, cd.client_name, p.pro_id, p.pro_name,p.actual_calculation_type from client_details cd," +
					" projectmntnc p where cd.client_id = p.client_id ");
			if(getClient() != null && getClient().length>0) {
				sbQuery.append(" and p.client_id in ("+StringUtils.join(getClient(), ",")+") ");
			}
			if(getPro_id()!=null && getPro_id().length>0 ){
				sbQuery.append(" and p.pro_id in ("+StringUtils.join(getPro_id(), ",")+") ");
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and p.org_id in ("+getF_org()+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and p.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation() != null && getF_strWLocation().length>0) {
				sbQuery.append(" and p.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and p.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service() != null && getF_service().length>0) {
				sbQuery.append(" and p.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (deadline >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "')) ");
				}
			sbQuery.append(" order by client_name, pro_id desc ");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			List<String> alInner1 = new ArrayList<String>();
			StringBuilder sbProIds = null;
			while(rs.next()) {
//				alInner1 = hmClientProId.get(rs.getString("client_id"));
//				if(alInner1 == null) alInner1 = new ArrayList<String>();
				if(sbProIds==null) {
					sbProIds = new StringBuilder();
					sbProIds.append(rs.getString("pro_id"));
				} else {
					sbProIds.append(","+rs.getString("pro_id"));
				}
//				alInner1.add(rs.getString("pro_id"));			
//				hmClientProId.put(rs.getString("client_id"), alInner1);
				hmProjectData.put(rs.getString("pro_id")+"_CLIENT", rs.getString("client_name"));
				hmProjectData.put(rs.getString("pro_id")+"_PRO_NAME", rs.getString("pro_name"));
				hmProjectData.put(rs.getString("pro_id")+"_CALC_TYPE", rs.getString("actual_calculation_type"));
			}
			rs.close();
			pst.close();
			
			
	//===start parvez date: 25-03-2022===
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, getStrStartDate(), getStrEndDate(), CF, uF, hmWeekEndHalfDates, null);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, getStrStartDate(), getStrEndDate(), alEmpCheckRosterWeektype, hmRosterWeekEndDates, hmWeekEnds, hmEmpLevelMap, hmEmpWlocation, hmWeekEndHalfDates);
			
			Map<String,String> hmWorkDays = new HashMap<String, String>();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_per_id,wlocation_id from employee_personal_details epd, employee_official_details eod WHERE epd.is_alive=true and epd.emp_per_id=eod.emp_id and approved_flag=true ");
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and (supervisor_emp_id = "+uF.parseToInt((String)session.getAttribute(EMPID))+" or eod.emp_id = "+uF.parseToInt((String)session.getAttribute(EMPID))+")");
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
			sbQuery.append(" order by emp_fname ");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()) {
				String strWLocationId = rs.getString("wlocation_id");
				Set<String> weeklyOffEndDate = hmWeekEnds.get(strWLocationId);
				if (weeklyOffEndDate == null) weeklyOffEndDate = new HashSet<String>();

				Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(rs.getString("emp_per_id"));
				if (rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();

				Map<String, String> hmHolidaysCnt = new HashMap<String, String>();
				Map<String, String> hmHolidayDates = new HashMap<String, String>();
				
				if (alEmpCheckRosterWeektype != null && alEmpCheckRosterWeektype.contains(rs.getString("emp_per_id"))) {
					CF.getHolidayListCount(con, request, getStrStartDate(), getStrEndDate(), CF, hmHolidayDates, hmHolidaysCnt, rosterWeeklyOffSet, true);
				} else {
					CF.getHolidayListCount(con, request, getStrStartDate(), getStrEndDate(), CF, hmHolidayDates, hmHolidaysCnt, weeklyOffEndDate, true);
				}
				
				String diffInDays = uF.dateDifference(getStrStartDate(), DATE_FORMAT, getStrEndDate(), DATE_FORMAT, CF.getStrTimeZone());

				int nWeekEnd = (alEmpCheckRosterWeektype != null && alEmpCheckRosterWeektype.contains(rs.getString("emp_per_id"))) ? rosterWeeklyOffSet.size() : weeklyOffEndDate.size();
				int nHolidayCnt = uF.parseToInt(hmHolidaysCnt.get(strWLocationId));
				double nWorkDay = (uF.parseToDouble(diffInDays) - nWeekEnd) - nHolidayCnt;
				
				double avgMonthDays = uF.parseToDouble(diffInDays)/30;
				double avgWorkDay = nWorkDay/avgMonthDays;
				
				hmWorkDays.put(rs.getString("emp_per_id"), avgWorkDay+"");
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpNetHourlySalary = CF.getEmpNetSalary(uF, CF, con, uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE), "H",hmWorkDays);
			
	//===end parvez date: 25-03-2022===
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			List<String> alEmpIds = new ArrayList<String>();
			StringBuilder sbEmpIds = null;
			if(sbProIds !=null) {
				pst = con.prepareStatement("select * from project_emp_details where pro_id in ("+sbProIds.toString()+")");
				rs = pst.executeQuery();
				while(rs.next()) {
					alEmpIds.add(rs.getString("emp_id"));
					if(sbEmpIds == null) {
						sbEmpIds = new StringBuilder();
						sbEmpIds.append(rs.getString("emp_id"));
					} else {
						sbEmpIds.append(","+rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
			}
				
			Map<String, String> hmEmpWiseTaskInfo = new HashMap<String, String>();
			StringBuilder sbTaskIds = null;
			
			for(int j=0;alEmpIds != null && !alEmpIds.isEmpty() && j< alEmpIds.size(); j++) {
				pst = con.prepareStatement("select ai.task_id, ai.activity_name, ai.approve_status, p.pro_name, p.curr_id, cd.client_name from activity_info ai, projectmntnc p, "
					+ "client_details cd where ai.pro_id =p.pro_id and p.client_id = cd.client_id and resource_ids like '%,"+alEmpIds.get(j)+",%' "
					+ " order by cd.client_name, p.pro_name");
//				if(alEmpIds.get(j).equalsIgnoreCase("893")){
//					System.out.println("pst ==>> " + pst);
//				}
				rs = pst.executeQuery();
				while(rs.next()) {
					if(sbTaskIds == null) {
						sbTaskIds = new StringBuilder();
						sbTaskIds.append(rs.getString("task_id"));
					} else {
						sbTaskIds.append(","+rs.getString("task_id"));
					}
					hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_TASKNAME", rs.getString("activity_name"));
					if(rs.getString("approve_status").equalsIgnoreCase("approved")){
						hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_TASKSTATUS", "Close");
					} else{
						hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_TASKSTATUS", "Open");
					}
					hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_PROJECTNAME", rs.getString("pro_name"));
					hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_CLIENTNAME", rs.getString("client_name"));
					hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_CURR_ID", rs.getString("curr_id"));
				}
				rs.close();
				pst.close();
				
//					pst = con.prepareStatement("select task_id, pro_id,activity_name,approve_status from activity_info where resource_ids like '%,"+alEmpIds.get(j)+",%' ");
////					pst.setInt(1, uF.parseToInt(alList.get(i)));
//					rs = pst.executeQuery();
//					/*if(alEmpIds.get(j).equalsIgnoreCase("893")){
//						System.out.println("PWEP/393--pst="+pst);
//					}*/
//					while(rs.next()) {
//						if(sbTaskIds == null) {
//							sbTaskIds = new StringBuilder();
//							sbTaskIds.append(rs.getString("task_id"));
//						} else {
//							sbTaskIds.append(","+rs.getString("task_id"));
//						}
//						hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_TASKNAME", rs.getString("activity_name"));
//						
//						if(rs.getString("approve_status").equalsIgnoreCase("approved")){
//							hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_TASKSTATUS", "Close");
//						} else{
//							hmEmpWiseTaskInfo.put(rs.getString("task_id")+"_TASKSTATUS", "Open");
//						}
//					}
//					rs.close();
//					pst.close();
				
			}
			
			if(sbTaskIds != null && !sbTaskIds.equals("") && sbEmpIds != null && !sbEmpIds.equals("")) {
				Map<String,String> hmBillingAmt = new HashMap<String, String>();
				StringBuilder sbQue = new StringBuilder();
				sbQue.append("select sum(piad.invoice_particulars_amount) as invoice_particulars_amount,emp_id,task_id from promntc_invoice_details pid,promntc_invoice_amt_details piad where pid.promntc_invoice_id=piad.promntc_invoice_id and emp_id in ("+sbEmpIds.toString()+") and task_id in ("+sbTaskIds.toString()+")");
				if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
					sbQue.append(" and invoice_generated_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
				}
				sbQue.append(" group by emp_id, task_id");
				pst = con.prepareStatement(sbQue.toString());
				rs = pst.executeQuery();
				while(rs.next()) {
					double billAmt = uF.parseToDouble(rs.getString("invoice_particulars_amount"));
					hmBillingAmt.put(rs.getString("emp_id") + "_" + rs.getString("task_id"), billAmt+"");
				}
				rs.close();
				pst.close();
				
			/*
			 * StringBuilder sbQue = new StringBuilder(); Map<String,String>
			 * hmBillingAmt = new HashMap<String, String>();
			 * 
			 * sbQue.
			 * append("select invoice_amount from promntc_invoice_details where pro_id in ("
			 * +alList.get(i)+")"); if(getStrStartDate() != null &&
			 * !getStrStartDate().equalsIgnoreCase("null") &&
			 * !getStrStartDate().equals("") && getStrEndDate() != null &&
			 * !getStrEndDate().equalsIgnoreCase("null") &&
			 * !getStrEndDate().equals("")) {
			 * sbQue.append(" and invoice_generated_date between '" +
			 * uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) +
			 * "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT,
			 * DBDATE) + "' "); } pst =
			 * con.prepareStatement(sbQue.toString()); rs =
			 * pst.executeQuery(); double billAmt = 0; while(rs.next()) {
			 * billAmt+= uF.parseToDouble(rs.getString("invoice_amount"));
			 * hmBillingAmt.put(alList.get(i)+"_BILL_AMT", billAmt+""); }
			 * 
			 * rs.close(); pst.close();
			 */
				
				sbQue = new StringBuilder();
				sbQue.append("select emp_id,activity_id,sum(actual_hrs) as hrs from task_activity where emp_id in ("+sbEmpIds.toString()+") and activity_id in ("+sbTaskIds.toString()+") ");
				if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
					sbQue.append(" and task_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
				}
				sbQue.append("group by emp_id, activity_id");
				pst = con.prepareStatement(sbQue.toString());
//					pst.setInt(1, uF.parseToInt(alEmpIds.get(j)));
				rs = pst.executeQuery();
				/*if(alEmpIds.get(j).equalsIgnoreCase("893")){
					System.out.println("PWEP/429--pst="+pst);
				}*/
				Map<String, String> hmEmpDesigName = CF.getEmpDesigMap(con);
				while(rs.next()) {
					List<String> innerList = new ArrayList<String>();
//						String EmpDesig = CF.getEmpDesigMapByEmpId(con, rs.getString("emp_id"));
					
					Map<String, String> hmCurr = hmCurrencyMap.get(hmEmpWiseTaskInfo.get(rs.getString("activity_id")+"_CURR_ID"));
					
					innerList.add(hmDeptName.get(rs.getString("emp_id")));		//0
					innerList.add(hmEmpWiseTaskInfo.get(rs.getString("activity_id")+"_CLIENTNAME"));	//1
					innerList.add(hmEmpWiseTaskInfo.get(rs.getString("activity_id")+"_PROJECTNAME"));	//2
					innerList.add(hmEmpWiseTaskInfo.get(rs.getString("activity_id")+"_TASKNAME"));	//3
					innerList.add(hmEmpWiseTaskInfo.get(rs.getString("activity_id")+"_TASKSTATUS"));	//4
					innerList.add(hmEmpName.get(rs.getString("emp_id")));		//5
					innerList.add(uF.showData(hmEmpDesigName.get(rs.getString("emp_id")), "-"));		//6
					innerList.add(rs.getString("hrs"));	//7
					if(hmBillingAmt != null && !hmBillingAmt.isEmpty() && hmBillingAmt.get(rs.getString("emp_id")+"_"+rs.getString("activity_id")) != null) {
						innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoComma(uF.parseToDouble(hmBillingAmt.get(rs.getString("emp_id")+"_"+rs.getString("activity_id")))));	//8   Billing
					} else{
						innerList.add(hmCurr.get("SHORT_CURR")+" "+"0");	//8   Billing
					}
					double cost = uF.parseToDouble(rs.getString("hrs"))*uF.parseToDouble(hmEmpNetHourlySalary.get(rs.getString("emp_id")));
					innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoComma(cost));	//9
					innerList.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoComma((hmBillingAmt!=null ? uF.parseToDouble(hmBillingAmt.get(rs.getString("emp_id")+"_"+rs.getString("activity_id"))) : 0)-cost));	//10
//						innerList.add(uF.formatIntoComma(uF.parseToDouble(hmBillingAmt.get(alList.get(i)+"_BILL_AMT"))-cost));	//10
					
					reportList.add(innerList);
				}
				rs.close();
				pst.close();
				
			}
				
			request.setAttribute("reportList", reportList);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs); 
			db.closeStatements(pst);  
			db.closeConnection(con);
		}
	}
	
	HttpServletRequest request;
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
	
	public String[] getF_service() {
		return f_service;
	}
	
	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}
	
	public String[] getF_level() {
		return f_level;
	}
	
	public void setF_level(String[] f_level) {
		this.f_level = f_level;
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
	
	public List<FillServices> getServiceList() {
		return serviceList;
	}
	
	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}
	
	public List<FillLevel> getLevelList() {
		return levelList;
	}
	
	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}
	
	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}
	
	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String[] getPro_id() {
		return pro_id;
	}

	public void setPro_id(String[] pro_id) {
		this.pro_id = pro_id;
	}

	public String[] getClient() {
		return client;
	}

	public void setClient(String[] client) {
		this.client = client;
	}

	public List<FillProjectList> getProjectdetailslist() {
		return projectdetailslist;
	}

	public void setProjectdetailslist(List<FillProjectList> projectdetailslist) {
		this.projectdetailslist = projectdetailslist;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}
	
	

}
