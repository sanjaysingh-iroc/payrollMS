package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

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

public class ProjectPerformanceReportCP extends ActionSupport implements ServletRequestAware, IStatements{
	/**
	 * 
	 */
	private static final long serialVersionUID = 503608531879273532L;
	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	
	String f_org;
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_service;
	String[] f_level;
	String[] f_project_service;
	String[] f_client;
	
	String selectOne;
	String strStartDate;
	String strEndDate;
	String financialYear;
	String paycycle;
	String strMonth;
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillServices> projectServiceList;
	List<FillLevel> levelList;
	List<FillFinancialYears> financialYearList;
	List<FillMonth> monthList;
	List<FillPayCycles> paycycleList;
	
	List<FillClients> clientList; 
	
	public String execute(){
		
		request.setAttribute(PAGE, "/jsp/task/ProjectPerformanceReportCP.jsp");
		request.setAttribute(TITLE, "Project Performance Analytics");
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF=new UtilityFunctions();
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
			getProjectDetails(uF.parseToInt((String)session.getAttribute(EMPID)), uF, CF, 0);
		}else if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(CEO))){
			getProjectDetails(0, uF, CF, 0);
		}
		loadOutstandingReport(uF);
		
		return SUCCESS;
	}

	
	public String loadOutstandingReport(UtilityFunctions uF) {
		organisationList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		projectServiceList = new FillServices(request).fillProjectServices();
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		clientList = new FillClients(request).fillClients(false);
		getSelectedFilter(uF);
		
		return SUCCESS;
	}
	
	
	public void getProjectDetails(int nManagerId, UtilityFunctions uF, CommonFunctions CF, int nLimit) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpNMap = CF.getEmpNameMap(con, null, null);
			
			Map<String, String> hmDeadline  = new HashMap<String, String>();
			Map<String, String> hmApprovedDate  = new HashMap<String, String>();
			pst = con.prepareStatement("select * from projectmntnc where approve_status = 'approved' ");
			rs = pst.executeQuery();
			StringBuilder sbProjects = new StringBuilder();
			while(rs.next()){
				hmDeadline.put(rs.getString("pro_id"), rs.getString("deadline"));
				hmApprovedDate.put(rs.getString("pro_id"), rs.getString("approve_date"));
				sbProjects.append(rs.getString("pro_id")+",");
			}
			rs.close();
			pst.close();
			
			if(sbProjects.length()>0){
				sbProjects.replace(sbProjects.length()-1, sbProjects.length(), "");
			}else{
				sbProjects.replace(0, sbProjects.length(), "-10");
			}
			
			Map<String, String> hmProjectClient  = new HashMap<String, String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select pro_id, client_name from projectmntnc pmc, client_details cd where pmc.client_id = cd.client_id ");
			if(sbProjects != null && !sbProjects.toString().equals("")) {
				sbQuery.append(" and pro_id in ("+sbProjects.toString()+")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			
			rs = pst.executeQuery();
			while(rs.next()) {
				hmProjectClient.put(rs.getString("pro_id"), rs.getString("client_name"));
			}
			rs.close();
			pst.close();
			
			
			Map<String, String> hmTeamLead  = new HashMap<String, String>();
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				pst = con.prepareStatement("select * from project_emp_details where _isteamlead = true ");
				rs = pst.executeQuery();
				String empName = null;
				while(rs.next()) {
					empName = hmTeamLead.get(rs.getString("pro_id"));
					if(empName == null) {
						empName = hmEmpNMap.get(rs.getString("emp_id"));
					} else {
						empName += ", "+ hmEmpNMap.get(rs.getString("emp_id"));
					}
					hmTeamLead.put(rs.getString("pro_id"), empName);
				}
				rs.close();
				pst.close();
			}
			
			Map<String, String> hmAddedBy  = new HashMap<String, String>();
			pst = con.prepareStatement("select * from projectmntnc ");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmAddedBy.put(rs.getString("pro_id"), rs.getString("added_by"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("============ 1 ==============");
			
			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, getStrStartDate(), getStrEndDate(), uF);
//			Map<String, String> hmEmpGrossAmountMap = CF.getEmpNetSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "H");
//			Map<String, String> hmEmpGrossAmountMapH = CF.getEmpNetSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "H",0);
//			Map<String, String> hmEmpGrossAmountMapD = CF.getEmpNetSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "D",0);
			
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
				
			Map<String, String> hmEmpGrossAmountMapH = CF.getEmpNetSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "H",hmWorkDays);
			Map<String, String> hmEmpGrossAmountMapD = CF.getEmpNetSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "D",hmWorkDays);
	//===end parvez date: 25-03-2022===		
			
			/*
			 * if(getStrStartDate()==null || getStrEndDate()==null ||
			 * (getStrStartDate()!=null && getStrEndDate()!=null &&
			 * getStrStartDate().equalsIgnoreCase(LABEL_FROM_DATE) &&
			 * getStrEndDate().equalsIgnoreCase(LABEL_TO_DATE))){ pst = con.
			 * prepareStatement("select  min(start_date), max(start_date) from (select pc.*,pmntc.pro_name, pmntc.start_date, pmntc.added_by from project_cost_billable pc, projectmntnc pmntc where pc.pro_id = pmntc.pro_id  and approve_status = 'approved' order by pmntc.deadline desc)as  b left join project_time pt on pt.pro_id=b.pro_id where b.pro_id>0  "
			 * ); rs = pst.executeQuery(); while(rs.next()){
			 * if(rs.getString("min")!=null){
			 * setStrStartDate(uF.getDateFormat(rs.getString("min"), DBDATE,
			 * DATE_FORMAT));
			 * setStrEndDate(uF.getDateFormat(rs.getString("max"), DBDATE,
			 * DATE_FORMAT)); } } rs.close(); pst.close(); }
			 */
			
//			System.out.println("============ 2 ==============");
			
			/*else{
//				return;
				Calendar cal= GregorianCalendar.getInstance();
				cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))-1);
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy")));
				int nActualMin = cal.getActualMinimum(Calendar.DATE);
				int nActualMax = cal.getActualMaximum(Calendar.DATE);
				
				String strDate1 = uF.getDateFormat(nActualMin+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
				String strDate2 = uF.getDateFormat(nActualMax+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
				
				setF_start(uF.getDateFormat(strDate1, DATE_FORMAT, DATE_FORMAT));
				setF_end(uF.getDateFormat(strDate2, DATE_FORMAT, DATE_FORMAT));
			}
			*/
			
			/*
			Calendar cal= GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))-1);
			cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy")));
			int nActualMin = cal.getActualMinimum(Calendar.DATE);
			int nActualMax = cal.getActualMaximum(Calendar.DATE);
			
			
			String strDate1 = uF.getDateFormat(nActualMin+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
			String strDate2 = uF.getDateFormat(nActualMax+"/"+(cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT);
			
			setF_start(uF.getDateFormat(strDate1, DATE_FORMAT, DATE_FORMAT));
			setF_end(uF.getDateFormat(strDate2, DATE_FORMAT, DATE_FORMAT));
			
			*/

//			Map<String, String> hmActualCostMap = new HashMap<String, String>();
//			Map<String, String> hmActualTimeMap = new HashMap<String, String>();

//			CF.getProjectHolidayCalculation(con, uF, CF, hmEmpGrossAmountMap, hmActualCostMap, hmActualTimeMap, getF_start(), getStrEndDate());
			
			
//			StringBuilder sbQuery = new StringBuilder();
////			sbQuery.append("select  variable_cost, actual_amount, billable_amount, budgeted_cost, b.pro_id, already_work, b.pro_name,actual_hrs,idealtime, completed, start_date from (select pc.pro_id, pc.already_work, pc.variable_cost, pc.actual_amount,pc.budgeted_cost, pcb.billable_amount,pmntc.pro_name, pmntc.start_date, pmntc.added_by from project_cost pc, project_cost_billable pcb, projectmntnc pmntc where pc.pro_id = pmntc.pro_id  and pc.pro_id = pcb.pro_id and pcb.pro_id = pmntc.pro_id and approve_status = 'n' order by pmntc.deadline desc )as b left join project_time pt on pt.pro_id=b.pro_id where b.pro_id>0 ");
//			sbQuery.append("select  variable_cost, actual_amount, billable_amount, budgeted_cost, b.pro_id, already_work, b.pro_name,actual_hrs, " +
//				"idealtime, completed, start_date from (select pc.*,pmntc.pro_name, pmntc.start_date, pmntc.added_by from project_cost_billable pc, " +
//				"projectmntnc pmntc where pc.pro_id = pmntc.pro_id  and approve_status = 'approved' order by pmntc.deadline desc)as b left join " +
//				"project_time pt on pt.pro_id=b.pro_id where b.pro_id>0 ");
//			
//			if(getF_start() != null && getStrEndDate() != null && !getF_start().equalsIgnoreCase(LABEL_FROM_DATE) && !getStrEndDate().equalsIgnoreCase(LABEL_TO_DATE)) {
//				sbQuery.append(" and start_date between '"+uF.getDateFormat(getF_start(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
//			}
//			if(nManagerId > 0){
//				sbQuery.append(" and added_by = " + nManagerId);
//			}
//			if(nLimit > 0 ) {
//				sbQuery.append(" limit " + nLimit);
//			}
//			
//			pst = con.prepareStatement(sbQuery.toString());
//			
////			rs=pst.executeQuery();
//			List<List<String>> alOuter=new ArrayList<List<String>>();
//			
//			

//			double dblBillableTotal=0;
//			double dblBudgetedTotal=0;
//			double dblActualTotal=0;
//			double diffTotal = 0;
//			
//			double dblIdealTimeTotal = 0;
//			double dblActualTimeTotal = 0;
//			int nCount = 0;
//			
//			
//			
//			while(rs!=null && rs.next()) {
//				
//				double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
//				
////				double dblBillable=(uF.parseToDouble(rs.getString("billable_amount"))+uF.parseToDouble(rs.getString("variable_cost")));
//				double dblBillable =uF.parseToDouble(rs.getString("billable_amount"));
//				double dblBudgeted = uF.parseToDouble(rs.getString("budgeted_cost")) + uF.parseToDouble(rs.getString("variable_cost"));
////				double dblActual= uF.parseToDouble(rs.getString("actual_amount"))   + dblReimbursement;
//				
//				double dblActual= uF.parseToDouble(hmActualCostMap.get(rs.getString("pro_id")))   + dblReimbursement;
//				
//				double diff = 0;
//				
//				double dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
////				double dblActualTime = uF.parseToDouble(rs.getString("actual_hrs"));
//				double dblActualTime = uF.parseToDouble(hmActualTimeMap.get(rs.getString("pro_id")));
//				
//				dblBillableTotal +=dblBillable;
//				dblBudgetedTotal +=dblBudgeted;
//				dblActualTotal +=dblActual;
//				nCount ++;
//				
//				dblIdealTimeTotal +=dblIdealTime;
//				dblActualTimeTotal +=dblActualTime;
//				
//				if(dblBillable>0) {
//					diff = ((dblBillable-dblActual)/dblBillable) * 100;
//				}
//				diffTotal +=diff;
//				
//				List<String> alInner=new ArrayList<String>();
//				alInner.add(rs.getString("pro_id"));
//				alInner.add(rs.getString("pro_name"));
//				
//				if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
//					alInner.add(uF.showData(hmEmpNMap.get(hmTeamLead.get(rs.getString("pro_id"))), ""));
//				} else {
//					alInner.add(uF.showData(hmEmpNMap.get(hmAddedBy.get(rs.getString("pro_id"))), ""));
//				}
//				
//				//alInner.add(uF.formatIntoOneDecimal(dblBillable-dblActual));
//				
//				alInner.add(uF.formatIntoOneDecimal(dblBudgeted));
//				alInner.add(uF.formatIntoOneDecimal(dblActual));
//				
//				alInner.add(uF.formatIntoOneDecimal(dblBillable));
//				
//				alInner.add(uF.formatIntoOneDecimal(diff)+"%");
//				
//				if (dblActual > dblBudgeted && dblActual < dblBillable) {
//					alInner.add("<img src=\"images1/icons/re_submit.png\" width=\"17px\">");
//				} else if(dblActual < dblBudgeted) {
//					alInner.add("<img src=\"images1/icons/approved.png\" width=\"17px\">");
//				} else if(dblActual > dblBillable) {
//					alInner.add("<img src=\"images1/icons/denied.png\" width=\"17px\">");
//				} else {
//					alInner.add("&nbsp;");
//				}
//				
//				
//				
//				alInner.add(uF.roundOffInTimeInHoursMins(dblIdealTime));
//				alInner.add(uF.roundOffInTimeInHoursMins(dblActualTime));
//				
//				
//				Date dtDeadline = uF.getDateFormat(hmDeadline.get(rs.getString("pro_id")), DBDATE);
//				Date dtCurrentDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
//				
//				if(dtDeadline!=null && dtCurrentDate!=null && dtDeadline.after(dtCurrentDate)) {
//					if(dblActualTime<=dblIdealTime) {
//						alInner.add("<img src=\"images1/icons/approved.png\" width=\"17px\">");
//					} else {
//						alInner.add("<img src=\"images1/icons/re_submit.png\" width=\"17px\">");
//					}
//				} else if(dtDeadline!=null && dtCurrentDate!=null && dtCurrentDate.after(dtDeadline)) {
//					alInner.add("<img src=\"images1/icons/denied.png\" width=\"17px\">");
//				} else {
//					alInner.add("");
//				}
//				
//				alOuter.add(alInner);
//				
//				pro_name.append("'"+rs.getString("pro_name")+"',");
//				billable_amount.append(dblBillable+",");
//				budgeted_cost.append(dblBudgeted+",");
//				actual_amount.append(dblActual+",");
//			}
//			
//			
//			if(pro_name.length()>1) {
//				pro_name.replace(0, pro_name.length(), pro_name.substring(0, pro_name.length()-1));
//				billable_amount.replace(0, billable_amount.length(), billable_amount.substring(0, billable_amount.length()-1));
//				budgeted_cost.replace(0, budgeted_cost.length(), budgeted_cost.substring(0, budgeted_cost.length()-1));
//				actual_amount.replace(0, actual_amount.length(), actual_amount.substring(0, actual_amount.length()-1));
//			}
//			
//			
//			List<String> alInner=new ArrayList<String>();
//			alInner.add("");
//			alInner.add("Aggregate");
//			
//			alInner.add("");
//			
//			
////			alInner.add(uF.formatIntoOneDecimal(dblBillableTotal-dblActualTotal));
//			alInner.add(uF.formatIntoOneDecimal(dblBudgetedTotal));
//			alInner.add(uF.formatIntoOneDecimal(dblActualTotal));
//			alInner.add(uF.formatIntoOneDecimal(dblBillableTotal));
//			alInner.add(uF.formatIntoOneDecimal(diffTotal/nCount)+"%");
//			
//			if (dblActualTotal > dblBudgetedTotal && dblActualTotal < dblBillableTotal) {
//				alInner.add("<img src=\"images1/icons/re_submit.png\" width=\"17px\">");
//			} else if(dblActualTotal < dblBudgetedTotal) {
//				alInner.add("<img src=\"images1/icons/approved.png\" width=\"17px\">");
//			} else if(dblActualTotal > dblBillableTotal) {
//				alInner.add("<img src=\"images1/icons/denied.png\" width=\"17px\">");
//			} else {
//				alInner.add("&nbsp;");
//			}
//			
//			alInner.add(uF.formatIntoOneDecimal(dblIdealTimeTotal));
//			alInner.add(uF.formatIntoOneDecimal(dblActualTimeTotal));
//			alInner.add("");
//			
//			alOuter.add(alInner);
//			
			
			
			
			if(getSelectOne() != null && getSelectOne().equals("1") && getStrStartDate() == null && getStrEndDate() == null) {
				
				Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
				
				String startdate="01/"+uF.getDateFormat(""+currDate, DBDATE, "MM")+"/"+uF.getDateFormat(""+currDate, DBDATE, "yyyy");
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
				calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

				Date date = (Date) calendar.getTime();
				DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
				String endDate=DATE_FORMAT.format(date);
				
				setStrStartDate(startdate);
				setStrEndDate(endDate);
			} else if(getSelectOne() != null && getSelectOne().equals("2")) {
				String[] strFinancialYears = null;
				if (getFinancialYear() != null) {
					strFinancialYears = getFinancialYear().split("-");
					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					
					setStrStartDate(strFinancialYears[0]);
					setStrEndDate(strFinancialYears[1]);
				} else {
					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					
					setStrStartDate(strFinancialYears[0]);
					setStrEndDate(strFinancialYears[1]);
				}
			} else if(getSelectOne() != null && getSelectOne().equals("3")) {
				
				int nselectedMonth = uF.parseToInt(getStrMonth());
//				int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
				int nFYSMonth = 0;
				String[] strFinancialYears = null;
				if (getFinancialYear() != null) {
					strFinancialYears = getFinancialYear().split("-");
					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
				} else {
					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
				}
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
				if(nselectedMonth>=nFYSMonth){
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "yyyy")));
				}else{
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, "yyyy")));
				}
				
				int nMonthStart = cal.getActualMinimum(Calendar.DATE);
				int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
				
				setStrStartDate(nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
				setStrEndDate(nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
				
				
			} else if(getSelectOne() != null && getSelectOne().equals("4")) {
				String[] strPayCycleDates = null;
				if (getPaycycle() != null) {
					strPayCycleDates = getPaycycle().split("-");
					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
					
					setStrStartDate(strPayCycleDates[0]);
					setStrEndDate(strPayCycleDates[1]);
				} else {
					strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
					
					setStrStartDate(strPayCycleDates[0]);
					setStrEndDate(strPayCycleDates[1]);
				}
			}

			
			
			double dblActualTotal=0;
			
//			StringBuilder budgeted_cost 	= new StringBuilder();
//			StringBuilder billable_amount 	= new StringBuilder();
//			StringBuilder actual_amount 	= new StringBuilder();
//			StringBuilder pro_name 			= new StringBuilder();
			
			Map<String, Map<String, String>> hmProjectCost = new HashMap<String, Map<String, String>>();
			
			/*
			 * pst = con.prepareStatement("select * from project_cost"); rs =
			 * pst.executeQuery(); while(rs.next()) { Map<String, String>
			 * hmInnerCost = hmProjectCost.get(rs.getString("pro_id"));
			 * if(hmInnerCost == null) hmInnerCost = new HashMap<String,
			 * String>();
			 * 
			 * hmInnerCost.put("VARIABLE_COST", rs.getString("variable_cost"));
			 * hmInnerCost.put("BILLABLE_AMOUNT",
			 * rs.getString("billable_amount"));
			 * hmInnerCost.put("BUDGETED_COST", rs.getString("budgeted_cost"));
			 * hmInnerCost.put("PRO_ID", rs.getString("pro_id"));
			 * hmInnerCost.put("ALREADY_WORK", rs.getString("already_work"));
			 * hmInnerCost.put("ACTUAL_AMOUNT", rs.getString("actual_amount"));
			 * hmInnerCost.put("DEADLINE",
			 * uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
			 * 
			 * hmProjectCost.put(rs.getString("pro_id"), hmInnerCost); }
			 * rs.close(); pst.close();
			 */
			
			Map<String, String> hmVariableCost = new HashMap<String, String>();
			
			/*
			 * pst = con.
			 * prepareStatement("select sum(variable_cost) as variable_cost, pro_id from variable_cost group by pro_id"
			 * ); rs = pst.executeQuery(); while(rs.next()) {
			 * hmVariableCost.put(rs.getString("pro_id"),
			 * rs.getString("variable_cost")); } rs.close(); pst.close();
			 */
			
			StringBuilder sbQuery1 = new StringBuilder();
			
			sbQuery1.append("select *, ai.emp_id as a_emp_id, ai.idealtime as a_idealtime, pmc.idealtime as pmc_idealtime, ai.already_work " +
					"as a_already_work, ai.already_work_days as a_already_work_days, pmc.actual_calculation_type from activity_info ai, " +
					"projectmntnc pmc where pmc.pro_id = ai.pro_id and pmc.approve_status = 'approved' ");
			
			if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0) || (getF_service() != null && getF_service().length>0) || (getF_level() != null && getF_level().length>0)) {
				sbQuery1.append(" and pmc.pro_id in (select pro_id from projectmntnc where pro_id>0 ");
			}
			 
			if(uF.parseToInt(getF_org())>0) {
				sbQuery1.append(" and pmc.wlocation_id in (select wlocation_id from work_location_info where org_id = "+uF.parseToInt(getF_org())+")");
			}
			if(getF_strWLocation() != null && getF_strWLocation().length>0) {
				sbQuery1.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery1.append(" and pmc.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_project_service() != null && getF_project_service().length>0) {
				String services = uF.getConcateData(getF_project_service());
				sbQuery1.append(" and pmc.service in ("+services+") ");
			}
			if(getF_client() != null && getF_client().length>0) {
				sbQuery1.append(" and pmc.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			
			if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0) || (getF_service() != null && getF_service().length>0) || (getF_level() != null && getF_level().length>0)) {
				sbQuery1.append(" ) ");
			}
			
			if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase(LABEL_FROM_DATE) && !getStrEndDate().equalsIgnoreCase(LABEL_TO_DATE)) {
				sbQuery1.append(" and pmc.start_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}
			if(nManagerId > 0) {
				sbQuery1.append(" and added_by="+nManagerId);
			}
			sbQuery1.append(" order by pmc.pro_id ");
			if(nLimit > 0) {
				sbQuery1.append(" limit "+nLimit);
			}
			
			pst = con.prepareStatement(sbQuery1.toString());
			rs = pst.executeQuery();
			
			double dblBugedtedAmt = 0;
			double dblActualAmt = 0;
			double dblBillableAmt = 0;
			
			double dblBugedtedTime = 0;
			double dblActualTime = 0;
			
			Map<String, String> hmProPerformaceBudget = new HashMap<String, String>();
			Map<String, String> hmProPerformaceActual = new HashMap<String, String>();
			Map<String, String> hmProPerformaceBillable = new HashMap<String, String>();
			
			Map<String, String> hmProPerformaceIdealTime = new HashMap<String, String>();
			Map<String, String> hmProPerformaceActualTime = new HashMap<String, String>();
			Map<String, String> hmProPerformaceProjectName = new HashMap<String, String>();
			Map<String, String> hmProPerformaceProjectProfit = new HashMap<String, String>();
			Map<String, String> hmProPerformaceProjectManager = new HashMap<String, String>();
			
			Map<String, String> hmProPerformaceProjectAmountIndicator = new HashMap<String, String>();
			Map<String, String> hmProPerformaceProjectTimeIndicator = new HashMap<String, String>();
			
			List alProjectId  = new ArrayList(); 
			
			String strProjectIdNew=null;
			String strProjectIdOld=null;
			
			double dblEmpRate = 0.0d;
			
			while(rs.next()) {
				
				strProjectIdNew = rs.getString("pro_id");
				double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
				
				if(strProjectIdNew!=null && !strProjectIdNew.equalsIgnoreCase(strProjectIdOld)) {
					dblBugedtedAmt = 0;
					dblActualAmt = 0;
					dblBillableAmt = 0;
					
					dblBugedtedTime = 0;
					dblActualTime = 0;
				}
				Map<String, String> hmInnerCost = hmProjectCost.get(rs.getString("pro_id"));
				if(hmInnerCost == null) hmInnerCost = new HashMap<String, String>();
				
				 if("F".equalsIgnoreCase(rs.getString("billing_type"))) {
//					 dblBillableAmt = uF.parseToDouble(rs.getString("billing_amount"));
					 dblBillableAmt = uF.parseToDouble(hmInnerCost.get("BILLABLE_AMOUNT"));
				 } else {
//					 dblBillableAmt += uF.parseToDouble(rs.getString("billing_amount"));
					 dblBillableAmt = uF.parseToDouble(hmInnerCost.get("BILLABLE_AMOUNT"));
				 }
				 
				 dblBugedtedTime = uF.parseToDouble(rs.getString("pmc_idealtime"));
				 
				 if("H".equalsIgnoreCase(rs.getString("actual_calculation_type"))) {
					 dblEmpRate = uF.parseToDouble(hmEmpGrossAmountMapH.get(rs.getString("a_emp_id")));
					 dblActualTime += uF.parseToDouble(rs.getString("a_already_work"));
					 hmProPerformaceIdealTime.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblBugedtedTime)+" hours");
					 hmProPerformaceActualTime.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblActualTime)+" hours");
				 } else {
					 dblEmpRate = uF.parseToDouble(hmEmpGrossAmountMapD.get(rs.getString("a_emp_id")));
					 dblActualTime += uF.parseToDouble(rs.getString("a_already_work_days"));
					 hmProPerformaceIdealTime.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblBugedtedTime)+" days");
					 hmProPerformaceActualTime.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblActualTime)+" days");
				 }
				 
				 
				 dblBugedtedAmt += uF.parseToDouble(rs.getString("a_idealtime")) * dblEmpRate;
				 dblActualAmt += dblActualTime * dblEmpRate;
				 
				 
//				 dblActualTime += uF.parseToDouble(rs.getString("a_already_work"));
				 
				 hmProPerformaceBudget.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(dblBugedtedAmt + uF.parseToDouble(hmVariableCost.get(rs.getString("pro_id")))));
				 hmProPerformaceActual.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(dblActualAmt + dblReimbursement));
				 hmProPerformaceBillable.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(dblBillableAmt));
				 
//				 hmProPerformaceIdealTime.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblBugedtedTime));
//				 hmProPerformaceActualTime.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblActualTime));
				 
				 hmProPerformaceProjectName.put(rs.getString("pro_id"), rs.getString("pro_name"));
				 
				 double diff = 0;
				 if(dblBillableAmt > 0) {
					 diff = ((dblBillableAmt-(dblActualAmt + dblReimbursement))/dblBillableAmt) * 100;
				 }
				 hmProPerformaceProjectProfit.put(rs.getString("pro_id"), Math.round(diff)+"%");
				
					
				if (dblActualAmt > dblBugedtedAmt && dblActualAmt < dblBillableAmt) {
					/* hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
					 hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
					 
				} else if(dblActualAmt < dblBugedtedAmt) {
					/*hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
					hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
					
					
				} else if(dblActualAmt > dblBillableAmt) {
					/*hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
					hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
					
				} else {
					hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "&nbsp;");
				}
				
				Date dtDeadline = uF.getDateFormat(hmDeadline.get(rs.getString("pro_id")), DBDATE);
				Date dtCurrentDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
				
				if(dtDeadline!=null && dtCurrentDate != null && dtDeadline.after(dtCurrentDate)) {
					if(dblActualTime<=dblBugedtedTime) {
						/*hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
						hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
						
					} else {
						/*hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
						hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
						
					}
				} else if(dtDeadline!=null && dtCurrentDate!=null && dtCurrentDate.after(dtDeadline)) {
					/*hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
					hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
					
				} else {
					hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "");
				}
				
				
			 	if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
			 		hmProPerformaceProjectManager.put(rs.getString("pro_id"), uF.showData(hmTeamLead.get(rs.getString("pro_id")), ""));
				} else {
					hmProPerformaceProjectManager.put(rs.getString("pro_id"), uF.showData(hmEmpNMap.get(hmAddedBy.get(rs.getString("pro_id"))), ""));
				}
				 
				 if(!alProjectId.contains(rs.getString("pro_id"))) {
					 alProjectId.add(rs.getString("pro_id"));
				 }
				 
				 strProjectIdOld = strProjectIdNew;
			}
			rs.close();
			pst.close();
			

//			pro_name= new StringBuilder();
//			billable_amount= new StringBuilder();
//			budgeted_cost= new StringBuilder();
//			actual_amount= new StringBuilder();
//			
//			for(int i=0; i<alProjectId.size(); i++) {
//				pro_name.append("'"+hmProPerformaceProjectName.get(alProjectId.get(i))+"',");
//				billable_amount.append(uF.parseToDouble(hmProPerformaceBillable.get(alProjectId.get(i)))+",");
//				budgeted_cost.append(uF.parseToDouble(hmProPerformaceBudget.get(alProjectId.get(i)))+",");
//				actual_amount.append(uF.parseToDouble(hmProPerformaceActual.get(alProjectId.get(i)))+",");
//			}
			
			
			request.setAttribute("hmProPerformaceBillable", hmProPerformaceBillable);
			request.setAttribute("hmProPerformaceActual", hmProPerformaceActual);
			request.setAttribute("hmProPerformaceBudget", hmProPerformaceBudget);
			request.setAttribute("hmProPerformaceProjectProfit", hmProPerformaceProjectProfit);
			request.setAttribute("hmProPerformaceProjectAmountIndicator", hmProPerformaceProjectAmountIndicator);
			request.setAttribute("hmProPerformaceProjectTimeIndicator", hmProPerformaceProjectTimeIndicator);
			
			request.setAttribute("hmProPerformaceActualTime", hmProPerformaceActualTime);
			request.setAttribute("hmProPerformaceIdealTime", hmProPerformaceIdealTime);
			
			request.setAttribute("hmProPerformaceProjectName", hmProPerformaceProjectName);
			request.setAttribute("hmProPerformaceProjectManager", hmProPerformaceProjectManager);
			
			request.setAttribute("hmProjectClient", hmProjectClient);
			
			request.setAttribute("alProjectId", alProjectId);
			
//			request.setAttribute("alOuter",alOuter);
//			request.setAttribute("pro_name",pro_name.toString());
//			request.setAttribute("billable_amount",billable_amount.toString());
//			request.setAttribute("budgeted_cost",budgeted_cost.toString());
//			request.setAttribute("actual_amount",actual_amount.toString());

			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
		
		alFilter.add("PROJECT_SERVICE");
		if(getF_project_service()!=null) {
			String strProjectService="";
			int k=0;
			for(int i=0;projectServiceList!=null && i<projectServiceList.size();i++) {
				for(int j=0;j<getF_project_service().length;j++) {
					if(getF_project_service()[j].equals(projectServiceList.get(i).getServiceId())) {
						if(k==0) {
							strProjectService=projectServiceList.get(i).getServiceName();
						} else {
							strProjectService+=", "+projectServiceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strProjectService!=null && !strProjectService.equals("")) {
				hmFilter.put("PROJECT_SERVICE", strProjectService);
			} else {
				hmFilter.put("PROJECT_SERVICE", "All Services");
			}
		} else {
			hmFilter.put("PROJECT_SERVICE", "All Services");
		}
		
		alFilter.add("CLIENT");
		if(getF_client()!=null) {
			String strClient="";
			int k=0;
			for(int i=0; clientList!=null && i<clientList.size();i++) {
				for(int j=0;j<getF_client().length;j++) {
					if(getF_client()[j].equals(clientList.get(i).getClientId())) {
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
		
		
			
		if(getSelectOne()!= null && !getSelectOne().equals("")) {
			alFilter.add("PERIOD");
			
			String strSelectOne="";
			if(uF.parseToInt(getSelectOne()) == 1) {
				strSelectOne="From - To";
			} else if(uF.parseToInt(getSelectOne()) == 2) {
				strSelectOne="Financial Year";
			} else if(uF.parseToInt(getSelectOne()) == 3) {
				strSelectOne="Month";
			} else if(uF.parseToInt(getSelectOne()) == 4) {
				strSelectOne="Paycycle";
			}
			if(strSelectOne!=null && !strSelectOne.equals("")) {
				hmFilter.put("PERIOD", strSelectOne);
			}
			
		}
		
		if(uF.parseToInt(getSelectOne()) == 1) {
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		} else if(uF.parseToInt(getSelectOne()) == 2) {
			alFilter.add("FINANCIALYEAR");
			String[] strFinancialYears = null;
			if (getFinancialYear() != null) {
				strFinancialYears = getFinancialYear().split("-");
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				
				setStrStartDate(strFinancialYears[0]);
				setStrEndDate(strFinancialYears[1]);
			} else {
				strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				
				setStrStartDate(strFinancialYears[0]);
				setStrEndDate(strFinancialYears[1]);
			}
			hmFilter.put("FINANCIALYEAR", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		} else if(uF.parseToInt(getSelectOne()) == 3) {
			alFilter.add("MONTH");
			int nselectedMonth = uF.parseToInt(getStrMonth());
			String strMonth = uF.getMonth(nselectedMonth);
//			int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
			int nFYSMonth = 0;
			String[] strFinancialYears = null;
			if (getFinancialYear() != null) {
				strFinancialYears = getFinancialYear().split("-");
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
			} else {
				strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
			}
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			if(nselectedMonth>=nFYSMonth){
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "yyyy")));
			}else{
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, "yyyy")));
			}
			
			int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			
			setStrStartDate(nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
			setStrEndDate(nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
			
			hmFilter.put("MONTH", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + strMonth);
		} else if(uF.parseToInt(getSelectOne()) == 4) {
			alFilter.add("PAYCYCLE");
			String strPaycycle = "";
			String[] strPayCycleDates = null;
			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				
				strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
				setStrStartDate(strPayCycleDates[0]);
				setStrEndDate(strPayCycleDates[1]);
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				
				strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
				setStrStartDate(strPayCycleDates[0]);
				setStrEndDate(strPayCycleDates[1]);
			}
			hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
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


	public String[] getF_project_service() {
		return f_project_service;
	}


	public void setF_project_service(String[] f_project_service) {
		this.f_project_service = f_project_service;
	}


	public String[] getF_client() {
		return f_client;
	}


	public void setF_client(String[] f_client) {
		this.f_client = f_client;
	}


	public String getSelectOne() {
		return selectOne;
	}


	public void setSelectOne(String selectOne) {
		this.selectOne = selectOne;
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


	public List<FillServices> getProjectServiceList() {
		return projectServiceList;
	}


	public void setProjectServiceList(List<FillServices> projectServiceList) {
		this.projectServiceList = projectServiceList;
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
