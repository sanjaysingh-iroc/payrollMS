package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeSummaryReport extends ActionSupport implements ServletRequestAware, IStatements {
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
	String[] f_client;
	
	
	String strStartDate;
	String strEndDate;
	String financialYear;
	String monthFinancialYear;
	String paycycle;
	String strMonth;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillLevel> levelList;
	List<FillFinancialYears> financialYearList;
	List<FillMonth> monthList;
	List<FillPayCycles> paycycleList; 
	
	List<FillClients> clientList;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		
		session = request.getSession();
		request.setAttribute(PAGE, "/jsp/task/EmployeeSummaryReport.jsp");
		request.setAttribute(TITLE, "Employee Summary Report");
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
			
		strEmpId =(String) session.getAttribute(EMPID);
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
			
			
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
			
		viewEmpSummaryReport(uF);
		
		
		return loadEmpSummary(uF);

	}
	
	private void viewEmpSummaryReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
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
			
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select ai.task_id,ai.idealtime,ta.activity_id,ta.task_date,ta.emp_id,ta.actual_hrs,ta.activity,ta.is_approved from task_activity ta,activity_info ai where ta.activity_id= ai.task_id and ta.is_approved >= 0 ");
			sbQuery.append("select sum(actual_hrs) as actual_hrs,emp_id, activity_id from task_activity ta where ta.is_approved >= 0 ");
//			sbQuery.append(" and ta.emp_id in (948) ");
			sbQuery.append(" and ta.emp_id in (select emp_per_id from employee_personal_details epd, employee_official_details eod WHERE epd.is_alive=true and epd.emp_per_id=eod.emp_id and approved_flag=true ");
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
			sbQuery.append(") ");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and ta.task_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			sbQuery.append(" group by ta.emp_id, ta.activity_id order by ta.emp_id, ta.activity_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			System.out.println("ESR/170--pst="+pst);
			Set<String> alResourceIds = new HashSet<String>();
			Map<String, Map<String, String>> hmEmpwiseTaskHrsCount = new HashMap<String, Map<String,String>>();
			double srtBillAmt = 0;
			while(rs.next()) {
				
				alResourceIds.add(rs.getString("emp_id"));
				Map<String, String> hmInner = hmEmpwiseTaskHrsCount.get(rs.getString("emp_id"));
				if(hmInner==null) hmInner = new HashMap<String, String>();
				double dblDeskJobHrsCnt = uF.parseToDouble(hmInner.get("OFFICE_HOURS"));
				if(uF.parseToInt(rs.getString("activity_id"))==0) {
					dblDeskJobHrsCnt += uF.parseToDouble(rs.getString("actual_hrs"));
				}
				hmInner.put("OFFICE_HOURS", dblDeskJobHrsCnt+"");
				
				double dblProjectTaskHrsCnt = uF.parseToDouble(hmInner.get("PROJECT_HOURS"));
				if(uF.parseToInt(rs.getString("activity_id"))>0 ) {
					dblProjectTaskHrsCnt += uF.parseToDouble(rs.getString("actual_hrs"));
				}
				hmInner.put("PROJECT_HOURS", dblProjectTaskHrsCnt+"");
				
//				System.out.println("BILL_AMT ===>> " + hmInner.get("BILL_AMT"));
				if(hmInner.get("BILL_AMT") == null || hmInner.get("BILL_AMT").equals("") || hmInner.get("BILL_AMT").equalsIgnoreCase("null")) {
					String billAmount = getInvoiceAmount(con, uF, rs.getString("emp_id"));
					hmInner.put("BILL_AMT", billAmount+"");
				}
				hmEmpwiseTaskHrsCount.put(rs.getString("emp_id"), hmInner);
			}
			rs.close();
			pst.close();
			
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			
//			Map<String, String> hmEmpGrossSalary = CF.getEmpGrossSalary(uF, CF, con, getStrStartDate(), "H");
//			Map<String, String> hmEmpNetHourlySalary = CF.getEmpNetSalary(uF, CF, con, uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE), "H", 0);
//			System.out.println("ESR/209--hmEmpNetHourlySalary = "+ hmEmpNetHourlySalary.get("948"));
			if(alResourceIds !=null) {
				sbQuery = new StringBuilder();
				Map<String, String> hmGradeDesigId = CF.getGradeDesig(con);
				Map<String, String> hmDesigName = CF.getDesigMap(con);
				sbQuery.append(" select emp_per_id, empcode, emp_fname, emp_mname, emp_lname,depart_id,grade_id,wlocation_id from employee_personal_details epd, employee_official_details eod "
						+ "WHERE (epd.is_alive=true or employment_end_date>=?) and epd.emp_per_id=eod.emp_id and approved_flag=true ");
//				sbQuery.append(" and emp_per_id in (948) ");
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
//				sbQuery.append(" and emp_per_id in("+sbEmpIds+") ");
				sbQuery.append(" order by emp_fname ");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
				rs = pst.executeQuery();
//				System.out.println("ESR/165--pst="+pst);
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
				Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);
				Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
				Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, getStrStartDate(), getStrEndDate(), CF, uF, hmWeekEndHalfDates, null);
				List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
				Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
				CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, getStrStartDate(), getStrEndDate(), alEmpCheckRosterWeektype, hmRosterWeekEndDates, hmWeekEnds, hmEmpLevelMap, hmEmpWlocation, hmWeekEndHalfDates);
				Map<String,String> hmEmpDetails = new HashMap<String, String>();
				Map<String,String> hmWorkDays = new HashMap<String, String>();
				List<String> alNewEmpIds = new ArrayList<String>();
				while(rs.next()) {
					
					String strMiddleName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strMiddleName = " "+rs.getString("emp_mname");
						}
					}
					String strEmpName = rs.getString("emp_fname") + uF.showData(strMiddleName, "")+" " + rs.getString("emp_lname");
					
					alNewEmpIds.add(rs.getString("emp_per_id"));
					hmEmpDetails.put(rs.getString("emp_per_id")+"_EMP_NAME", strEmpName);
					hmEmpDetails.put(rs.getString("emp_per_id")+"_EMP_DESIG_NAME", hmDesigName.get(hmGradeDesigId.get(rs.getString("grade_id"))));
					hmEmpDetails.put(rs.getString("emp_per_id")+"_EMP_LOC_ID", rs.getString("wlocation_id"));
					
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
					
					hmEmpDetails.put(rs.getString("emp_per_id")+"_EMP_WORK_DAY", nWorkDay+"");
					hmWorkDays.put(rs.getString("emp_per_id"), avgWorkDay+"");
					
					/*Map<String, String> hmInner = hmEmpwiseTaskHrsCount.get(rs.getString("emp_per_id"));
					List<String> innerList = new ArrayList<String>();
					String strMiddleName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strMiddleName = " "+rs.getString("emp_mname");
						}
					}
					String strEmpName = rs.getString("emp_fname") + uF.showData(strMiddleName, "")+" " + rs.getString("emp_lname");
					innerList.add(strEmpName);		//0
					innerList.add(hmDesigName.get(hmGradeDesigId.get(rs.getString("grade_id"))));	//1
					
					alNewEmpIds.add(rs.getString("emp_per_id"));
					hmEmpDetails.put(rs.getString("emp_per_id")+"_EMP_NAME", strEmpName);
					hmEmpDetails.put(rs.getString("emp_per_id")+"_EMP_DESIG_NAME", hmDesigName.get(hmGradeDesigId.get(rs.getString("grade_id"))));
					hmEmpDetails.put(rs.getString("emp_per_id")+"_EMP_LOC_ID", rs.getString("wlocation_id"));
					
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
//					System.out.println("ESR/300--nWorkDay="+nWorkDay);
					double totExpectedHrs = nWorkDay*8;
					
					List<String> leaveList = CF.getEmpAllLeaves(con, uF, getStrStartDate(), getStrEndDate(), rs.getString("emp_per_id"));
					double leaveHrs = 0;
					if(leaveList != null){
						leaveHrs = leaveList.size()*8;
					}
					
					innerList.add(uF.formatIntoComma(totExpectedHrs));	//2
					
					double totActualHrs = 0;
					double projectHrs = 0;
					double officeHrs = 0;
					if(hmInner!=null && hmInner.size()>0) {
						totActualHrs = uF.parseToDouble(hmInner.get("OFFICE_HOURS"))+uF.parseToDouble(hmInner.get("PROJECT_HOURS"));
						projectHrs = uF.parseToDouble(hmInner.get("PROJECT_HOURS"));
						officeHrs = uF.parseToDouble(hmInner.get("OFFICE_HOURS"));
						
						innerList.add(uF.formatIntoComma(totActualHrs));					//3
						innerList.add(uF.formatIntoComma(uF.parseToDouble(hmInner.get("PROJECT_HOURS"))));	//4
						innerList.add(uF.formatIntoComma(uF.parseToDouble(hmInner.get("OFFICE_HOURS"))));		//5
						innerList.add(uF.formatIntoComma(uF.parseToDouble(hmInner.get("BILL_AMT"))));		//6
						
					} else{
						innerList.add("0.0");		//3
						innerList.add("0.0");		//4
						innerList.add("0.0");		//5
						innerList.add("0.0");		//6
					}
					
					double idleHrs = totExpectedHrs-totActualHrs-leaveHrs;
					innerList.add(uF.formatIntoComma(idleHrs));		//7
					innerList.add(uF.formatIntoComma(leaveHrs));	//8
					
					double projectCost = 0;
					double officeCost = 0;
					double idleCost = 0;
					double totCost = 0;
//					System.out.println("ESR/337--salary="+hmEmpGrossSalary.get("emp_per_id"));
					projectCost = projectHrs*uF.parseToDouble(hmEmpNetHourlySalary.get(rs.getString("emp_per_id")));
					officeCost = officeHrs*uF.parseToDouble(hmEmpNetHourlySalary.get(rs.getString("emp_per_id")));
					idleCost = idleHrs*uF.parseToDouble(hmEmpNetHourlySalary.get(rs.getString("emp_per_id")));
					totCost = projectCost+officeCost+idleCost;
					
					innerList.add(uF.formatIntoComma(projectCost));		//9
					innerList.add(uF.formatIntoComma(officeCost));		//10
					innerList.add(uF.formatIntoComma(idleCost));		//11
					innerList.add(uF.formatIntoComma(totCost));			//12
					innerList.add(uF.formatIntoComma((hmInner!=null ? uF.parseToDouble(hmInner.get("BILL_AMT")) : 0) -totCost));		//13
					
					reportList.add(innerList);*/
				}
				rs.close();
				pst.close();
			
			//===start parvez date: 25-03-2022===
				
				Map<String, String> hmEmpNetHourlySalary = CF.getEmpNetSalary(uF, CF, con, uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE), "H", hmWorkDays);
				
				for(int i=0; i<alNewEmpIds.size();i++){
					
					String strEmpId = alNewEmpIds.get(i);
					
					Map<String, String> hmInner = hmEmpwiseTaskHrsCount.get(strEmpId);
					List<String> innerList = new ArrayList<String>();
					
					
					innerList.add(hmEmpDetails.get(strEmpId+"_EMP_NAME"));		//0
					innerList.add(hmEmpDetails.get(strEmpId+"_EMP_DESIG_NAME"));	//1
					
					String strWLocationId = hmEmpDetails.get(strEmpId+"_EMP_LOC_ID");
					Set<String> weeklyOffEndDate = hmWeekEnds.get(strWLocationId);
					if (weeklyOffEndDate == null) weeklyOffEndDate = new HashSet<String>();

					Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(strEmpId);
					if (rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();

					Map<String, String> hmHolidaysCnt = new HashMap<String, String>();
					Map<String, String> hmHolidayDates = new HashMap<String, String>();
					
					if (alEmpCheckRosterWeektype != null && alEmpCheckRosterWeektype.contains(strEmpId)) {
						CF.getHolidayListCount(con, request, getStrStartDate(), getStrEndDate(), CF, hmHolidayDates, hmHolidaysCnt, rosterWeeklyOffSet, true);
					} else {
						CF.getHolidayListCount(con, request, getStrStartDate(), getStrEndDate(), CF, hmHolidayDates, hmHolidaysCnt, weeklyOffEndDate, true);
					}
					
					/*String diffInDays = uF.dateDifference(getStrStartDate(), DATE_FORMAT, getStrEndDate(), DATE_FORMAT, CF.getStrTimeZone());

					int nWeekEnd = (alEmpCheckRosterWeektype != null && alEmpCheckRosterWeektype.contains(strEmpId)) ? rosterWeeklyOffSet.size() : weeklyOffEndDate.size();
					int nHolidayCnt = uF.parseToInt(hmHolidaysCnt.get(strWLocationId));*/
					double nWorkDay = uF.parseToDouble(hmEmpDetails.get(strEmpId+"_EMP_WORK_DAY"));
//					System.out.println("ESR/300--nWorkDay="+nWorkDay);
					double totExpectedHrs = nWorkDay*8;
					
					List<String> leaveList = CF.getEmpAllLeaves(con, uF, getStrStartDate(), getStrEndDate(), strEmpId);
					double leaveHrs = 0;
					if(leaveList != null){
						leaveHrs = leaveList.size()*8;
					}
					
					innerList.add(uF.formatIntoComma(totExpectedHrs));	//2
					
					double totActualHrs = 0;
					double projectHrs = 0;
					double officeHrs = 0;
					if(hmInner!=null && hmInner.size()>0) {
						totActualHrs = uF.parseToDouble(hmInner.get("OFFICE_HOURS"))+uF.parseToDouble(hmInner.get("PROJECT_HOURS"));
						projectHrs = uF.parseToDouble(hmInner.get("PROJECT_HOURS"));
						officeHrs = uF.parseToDouble(hmInner.get("OFFICE_HOURS"));
						
						innerList.add(uF.formatIntoComma(totActualHrs));					//3
						innerList.add(uF.formatIntoComma(uF.parseToDouble(hmInner.get("PROJECT_HOURS"))));	//4
						innerList.add(uF.formatIntoComma(uF.parseToDouble(hmInner.get("OFFICE_HOURS"))));		//5
						innerList.add(uF.formatIntoComma(uF.parseToDouble(hmInner.get("BILL_AMT"))));		//6
						
					} else{
						innerList.add("0.0");		//3
						innerList.add("0.0");		//4
						innerList.add("0.0");		//5
						innerList.add("0.0");		//6
					}
					
					double idleHrs = totExpectedHrs-totActualHrs-leaveHrs;
					innerList.add(uF.formatIntoComma(idleHrs));		//7
					innerList.add(uF.formatIntoComma(leaveHrs));	//8
					
					double projectCost = 0;
					double officeCost = 0;
					double idleCost = 0;
					double totCost = 0;
//					System.out.println("ESR/337--salary="+hmEmpGrossSalary.get("emp_per_id"));
					projectCost = projectHrs*uF.parseToDouble(hmEmpNetHourlySalary.get(strEmpId));
					officeCost = officeHrs*uF.parseToDouble(hmEmpNetHourlySalary.get(strEmpId));
					idleCost = idleHrs*uF.parseToDouble(hmEmpNetHourlySalary.get(strEmpId));
					totCost = projectCost+officeCost+idleCost;
					
					innerList.add(uF.formatIntoComma(projectCost));		//9
					innerList.add(uF.formatIntoComma(officeCost));		//10
					innerList.add(uF.formatIntoComma(idleCost));		//11
					innerList.add(uF.formatIntoComma(totCost));			//12
					innerList.add(uF.formatIntoComma((hmInner!=null ? uF.parseToDouble(hmInner.get("BILL_AMT")) : 0) -totCost));		//13
					
					reportList.add(innerList);
				}
			//===end parvez date: 25-03-2022===	
				
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
	
	public String getInvoiceAmount(Connection con, UtilityFunctions uF, String strNewEmpId){
		
		PreparedStatement pst1 = null;
		ResultSet rst = null;
		double billAmt = 0;
		try {
			StringBuilder sbQue = new StringBuilder();
//			sbQue.append("select piad.invoice_particulars_amount from promntc_invoice_details pid,promntc_invoice_amt_details piad where pid.promntc_invoice_id=piad.promntc_invoice_id and emp_id=? and task_id in ("+taskId+")");
			sbQue.append("select sum(piad.invoice_particulars_amount) as invoice_particulars_amount from promntc_invoice_details pid,promntc_invoice_amt_details piad where pid.promntc_invoice_id=piad.promntc_invoice_id and emp_id=? ");
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQue.append(" and invoice_generated_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			pst1 = con.prepareStatement(sbQue.toString());
			pst1.setInt(1, uF.parseToInt(strNewEmpId));
			rst = pst1.executeQuery();
			while(rst.next()) {
				billAmt = uF.parseToDouble(rst.getString("invoice_particulars_amount"));
			}
			rst.close();
			pst1.close();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst1 != null) {
				try {
					pst1.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		return billAmt+"";
	}
	
	public String loadEmpSummary(UtilityFunctions uF) {
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
		
		
		if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))){
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
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

	public String[] getF_client() {
		return f_client;
	}

	public void setF_client(String[] f_client) {
		this.f_client = f_client;
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

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getMonthFinancialYear() {
		return monthFinancialYear;
	}

	public void setMonthFinancialYear(String monthFinancialYear) {
		this.monthFinancialYear = monthFinancialYear;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
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

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}
	

}
