package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillCountry;
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

public class TeamUtilizationReport extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	String strEmpId;
	String strUserType; 
	
	String f_org;
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_service;
	String[] f_level;
	String[] f_project_service;
	String[] f_client;
	String[] f_country;
	
	String selectOne;
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
	List<FillServices> projectServiceList;
	List<FillLevel> levelList;
	List<FillFinancialYears> financialYearList;
	List<FillMonth> monthList;
	List<FillPayCycles> paycycleList;
	
	List<FillClients> clientList;
	List<FillCountry> countryList;
	
	String strProType;
	boolean poFlag;
	
	String reportType;
	String filterType;
	
	public String execute() throws Exception {
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/task/TaskBasedReport.jsp");
		request.setAttribute(TITLE, "Task Based Report");
		strEmpId =(String) session.getAttribute(EMPID);
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		
		if(getF_org() == null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		checkProjectOwner(uF);
		if(getReportType() == null || getReportType().equalsIgnoreCase("null") || getReportType().equals("T")) {
			getTeamUtilizationReport(uF);
			
		} else if(getReportType() != null && getReportType().equals("P")) {
			getProjectwiseTeamUtilizationReport(uF);
			
		} else if(getReportType() != null && getReportType().equals("C")) {
			getProjectwiseTeamUtilizationReport(uF);
			
		}
//		System.out.println("poFlag ===>> " + poFlag);
		return loadTeamUtilizationReport(uF);

	}
	
	
	private void getProjectwiseTeamUtilizationReport(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		List<List<String>> alOuter = new ArrayList<List<String>>();
		
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			if(getSelectOne() != null && getSelectOne().equals("1") && (getStrStartDate() == null || getStrStartDate().equals("") || getStrEndDate() == null || getStrEndDate().equals(""))) {
				Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
				String startdate="01/"+uF.getDateFormat(""+currDate, DBDATE, "MM")+"/"+uF.getDateFormat(""+currDate, DBDATE, "yyyy");
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
				calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

				Date date = calendar.getTime();
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
				int nFYSDay = 0;
				String[] strFinancialYears = null;
				if (getMonthFinancialYear() != null) {
					strFinancialYears = getMonthFinancialYear().split("-");
					setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
					nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
				} else {
					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
					setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
					nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
				}
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
				cal.set(Calendar.DATE, nFYSDay);
				if(nselectedMonth>=nFYSMonth){
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "yyyy")));
				} else {
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
			} else if(uF.parseToInt(getSelectOne()) == 0) {
				Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
				calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
				calendar.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "dd")));

				Date date = calendar.getTime();
				DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
				String strCurrDate=DATE_FORMAT.format(date);
				
				setStrStartDate(strCurrDate);
				setStrEndDate(strCurrDate);
			}

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select cd.client_name, ai.pro_id,p.pro_name,ai.task_id,ai.activity_name,ai.start_date,ai.deadline,ai.idealtime,ai.resource_ids," +
				"p.actual_calculation_type from activity_info ai, projectmntnc p left join client_details cd on cd.client_id = p.client_id " +
				" where ai.pro_id = p.pro_id and p.pro_id > 0 and ai.approve_status = 'n' ");
		//===start parvez date: 15-10-2022===	
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2) {
//				sbQuery.append(" and project_owner = "+uF.parseToInt(strEmpId));
				sbQuery.append(" and project_owners like '%,"+strEmpId+",%'");
			}
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				/*sbQuery.append(" and (pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");*/
				sbQuery.append(" and (pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
						+" or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ) ");
			}
		//===end parvez date: 15-10-2022===	
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and p.org_id in ("+getF_org()+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and p.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and p.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and p.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and p.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service() != null && getF_service().length>0) {
				sbQuery.append(" and p.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			if(getF_project_service() != null && getF_project_service().length>0) {
				String services = uF.getConcateData(getF_project_service());
				sbQuery.append(" and p.service in ("+services+") ");
			}
			if(getF_client() != null && getF_client().length>0) {
				sbQuery.append(" and p.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			if(getF_country() != null && getF_country().length>0) {
				sbQuery.append(" and p.wlocation_id in (select wlocation_id from work_location_info where wlocation_country_id in ("+StringUtils.join(getF_country(), ",")+")) ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and ((p.start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and p.deadline <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"') or " +
					" (p.start_date <= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and p.deadline >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"') or " +
					" (p.start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and p.start_date <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"')) ");
			}
//			sbQuery.append(") ");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			String dblFilterDays = uF.dateDifference(getStrStartDate(), DATE_FORMAT, getStrEndDate(), DATE_FORMAT);
			double dblFilterHrs = uF.parseToDouble(dblFilterDays) * 8;
			String dblTaskDays = "";
			double dblPlannedHrs = 0;
			while(rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("client_name"));
				innerList.add(rs.getString("pro_name"));
				innerList.add(rs.getString("activity_name")); //2
				String strResourceIds = (rs.getString("resource_ids") != null && rs.getString("resource_ids").length()>1) ? rs.getString("resource_ids").substring(1, rs.getString("resource_ids").length()-1) : "";
				List<String> cntResources = Arrays.asList(strResourceIds.split(","));
				innerList.add(cntResources.size()+""); //3
				if(rs.getString("start_date") != null && rs.getString("deadline") != null) {
					dblTaskDays = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("deadline"), DBDATE);
				}
				double dblTaskHrs = uF.parseToDouble(dblTaskDays) * 8;
				if(uF.parseToInt(dblFilterDays)>0 && uF.parseToInt(dblTaskDays)>0 && uF.parseToInt(dblTaskDays)>uF.parseToInt(dblFilterDays)) {
					if(rs.getString("actual_calculation_type") != null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
						dblPlannedHrs = (uF.parseToDouble(rs.getString("idealtime")) * uF.parseToDouble(dblFilterDays)) / uF.parseToDouble(dblTaskDays);
						dblPlannedHrs = dblPlannedHrs * 8; 
					} else if(rs.getString("actual_calculation_type") != null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
						dblPlannedHrs = ((uF.parseToDouble(rs.getString("idealtime")) * 30) * uF.parseToDouble(dblFilterDays)) / uF.parseToDouble(dblTaskDays);
						dblPlannedHrs = dblPlannedHrs * 8 * 30;
					} else {
						dblPlannedHrs = (uF.parseToDouble(rs.getString("idealtime")) * dblFilterHrs) / dblTaskHrs;
					}
				} else {
					dblPlannedHrs = uF.parseToDouble(rs.getString("idealtime"));
					if(rs.getString("actual_calculation_type") != null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
						dblPlannedHrs = dblPlannedHrs * 8; 
					} else if(rs.getString("actual_calculation_type") != null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
						dblPlannedHrs = dblPlannedHrs * 8 * 30;
					}
				}
//				dblPlannedHrs = (dblPlannedHrs * cntResources.size());
				innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblPlannedHrs)); //4
				double dblActualHrs = getResourcewiseTaskwiseActualWorkTime(con, uF, strResourceIds, rs.getString("task_id"));
				innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblActualHrs)); //5
				double dblResourceUtilization = 0;
//				double  dblFilterHr = dblFilterHrs;
				if(cntResources.size()>0) {
					dblResourceUtilization = ((dblPlannedHrs/cntResources.size()) * 100) / dblFilterHrs;
//					dblFilterHr = (dblFilterHrs * cntResources.size());
				}
//				innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblFilterHr)); //5
				innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblResourceUtilization)+"%"); //6
				alOuter.add(innerList);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alOuter", alOuter);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs); 
			db.closeStatements(pst);  
			db.closeConnection(con);
		}
		
	}


	public String loadTeamUtilizationReport(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
				countryList = new FillCountry(request).fillWorkLocationCountry(StringUtils.join(getF_strWLocation(), ","));
			} else {
				countryList = new FillCountry(request).fillWorkLocationCountry((String)session.getAttribute(WLOCATION_ACCESS));
			}
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
				countryList = new FillCountry(request).fillWorkLocationCountry(StringUtils.join(getF_strWLocation(), ","));
			} else {
				countryList = new FillCountry(request).fillWorkLocationCountry(null);
			}
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		projectServiceList = new FillServices(request).fillProjectServices();
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		clientList = new FillClients(request).fillAllClients(false);
		
		getSelectedFilter(uF);
		 
		return SUCCESS;
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
		//===start parvez date: 15-10-2022===	
//			sbQuery.append("select * from projectmntnc pmc where project_owner=?");
			sbQuery.append("select * from projectmntnc pmc where project_owners like '%,"+strEmpId+",%'");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strEmpId));
		//===end parvez date: 15-10-2022===	
			rs = pst.executeQuery();
			if(rs.next()) {
				poFlag = true;
			}
			rs.close();
			pst.close();
			
			setPoFlag(poFlag);
			
			if(poFlag && uF.parseToInt(getStrProType()) == 0){
				setStrProType("2");
			}
			
		} catch (Exception e) {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getTeamUtilizationReport(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		List<List<String>> alOuter = new ArrayList<List<String>>();
		
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			if(getSelectOne() != null && getSelectOne().equals("1") && getStrStartDate() == null && getStrEndDate() == null) {
				Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
				String startdate="01/"+uF.getDateFormat(""+currDate, DBDATE, "MM")+"/"+uF.getDateFormat(""+currDate, DBDATE, "yyyy");
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
				calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

				Date date = calendar.getTime();
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
				int nFYSDay = 0;
				String[] strFinancialYears = null;
				if (getFinancialYear() != null) {
					strFinancialYears = getFinancialYear().split("-");
					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
					nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
				} else {
					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
					nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
				}
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
				cal.set(Calendar.DATE, nFYSDay);
				if(nselectedMonth>=nFYSMonth){
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "yyyy")));
				} else {
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
			} else if(uF.parseToInt(getSelectOne()) == 0 && (getStrStartDate() == null || getStrStartDate().equals("")) && (getStrEndDate() == null || getStrEndDate().equals(""))) {
				Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
				calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
				calendar.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "dd")));

				Date date = calendar.getTime();
				DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
				String strCurrDate=DATE_FORMAT.format(date);
				
				setStrStartDate(strCurrDate);
				setStrEndDate(strCurrDate);
			}

			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select distinct(emp_id) as resource_id from project_emp_details where emp_id > 0 " +
				"and pro_id in (select pro_id from projectmntnc where pro_id > 0 and approve_status = 'n' ");
//			and (pro_id in (select pro_id from projectmntnc " +
//				"where project_owner=?) or pro_id in (select pro_id from project_emp_details where _isteamlead =true and emp_id=?))
		//===start parvez date: 15-10-2022===	
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2) {
//				sbQuery.append(" and project_owner = "+uF.parseToInt(strEmpId));
				sbQuery.append(" and project_owners like '%,"+strEmpId+",%'");
			}
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				/*sbQuery.append(" and (pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
					+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
					+" or project_owner = "+uF.parseToInt((String)session.getAttribute(EMPID))+" ) ");*/
				sbQuery.append(" and (pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "
						+uF.parseToInt((String)session.getAttribute(EMPID))+" ) or added_by = "+uF.parseToInt((String)session.getAttribute(EMPID))
						+" or project_owners like '%,"+(String)session.getAttribute(EMPID)+",%' ) ");
			}
		//===end parvez date: 15-10-2022===	
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
				sbQuery.append(" and department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service() != null && getF_service().length>0) {
				sbQuery.append(" and sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			if(getF_project_service() != null && getF_project_service().length>0) {
				String services = uF.getConcateData(getF_project_service());
				sbQuery.append(" and service in ("+services+") ");
			}
			if(getF_client() != null && getF_client().length>0) {
				sbQuery.append(" and client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			if(getF_country() != null && getF_country().length>0) {
				sbQuery.append(" and wlocation_id in (select wlocation_id from work_location_info where wlocation_country_id in ("+StringUtils.join(getF_country(), ",")+")) ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and ((start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and deadline <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"') or " +
					" (start_date <= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and deadline >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"') or " +
					" (start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and start_date <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"')) ");
			}
			sbQuery.append(") ");
			
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setInt(2, uF.parseToInt(strEmpId));
//			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			List<String> alResourceIds = new ArrayList<String>();
			while(rs.next()) {
				alResourceIds.add(rs.getString("resource_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("alResourceIds ===>> " + alResourceIds														);
			
			List<String> alAllResourceData = new ArrayList<String>();
			double dblTotResourcePlannedHrs = 0;
			double dblTotResourceActualHrs = 0;
//			double dblTotResourceUtilization = 0;
			String dblFilterDays = uF.dateDifference(getStrStartDate(), DATE_FORMAT, getStrEndDate(), DATE_FORMAT);
//			System.out.println("dblFilterDays ===>> " + dblFilterDays);
			double dblFilterHrs = uF.parseToDouble(dblFilterDays) * 8;
//			System.out.println("dblFilterHrs ===>> " + dblFilterHrs);
//			System.out.println("alResourceIds ===>> " + alResourceIds);
			for(int i=0; alResourceIds != null && i<alResourceIds.size(); i++) {
				List<String> alResourceData = getResourcewisePlannedActualTimeUtilization(con, uF, alResourceIds.get(i));
//				System.out.println("alResourceIds.get(i) ===>> "  +alResourceIds.get(i));
				if(uF.parseToInt(getFilterType()) != 3) {
					dblTotResourcePlannedHrs += uF.parseToDouble(alResourceData.get(0));
					dblTotResourceActualHrs += uF.parseToDouble(alResourceData.get(1));
	//				dblTotResourceUtilization += uF.parseToDouble(alResourceData.get(2));
				} else if(uF.parseToInt(getFilterType()) == 3) {
					alAllResourceData = new ArrayList<String>();
					dblTotResourcePlannedHrs = uF.parseToDouble(alResourceData.get(0));
					dblTotResourceActualHrs = uF.parseToDouble(alResourceData.get(1));
					double dblTotResourceUtilization = (dblTotResourcePlannedHrs * 100) / dblFilterHrs;
					alAllResourceData.add(hmEmpName.get(alResourceIds.get(i)));
					alAllResourceData.add(alResourceData.get(2));
					alAllResourceData.add(uF.formatIntoTwoDecimalWithOutComma(dblTotResourcePlannedHrs));
					alAllResourceData.add(uF.formatIntoTwoDecimalWithOutComma(dblTotResourceActualHrs));
//					alAllResourceData.add(uF.formatIntoTwoDecimalWithOutComma(dblFilterHrs));
					alAllResourceData.add(uF.formatIntoTwoDecimalWithOutComma(dblTotResourceUtilization));
					alOuter.add(alAllResourceData);
//					System.out.println("alAllResourceData ===>> " + alAllResourceData);
				}
			}
//			System.out.println("dblTotResourcePlannedHrs ===>> " + dblTotResourcePlannedHrs);
			if(alResourceIds != null && alResourceIds.size()>0 && uF.parseToInt(getFilterType()) != 3) {
				double dblTotResourceUtilization = ((dblTotResourcePlannedHrs/alResourceIds.size()) * 100) / dblFilterHrs;
//				double avgUtilization = dblTotResourceUtilization / alResourceIds.size();
				alAllResourceData.add(alResourceIds.size()+"");
				alAllResourceData.add(uF.formatIntoTwoDecimalWithOutComma(dblTotResourcePlannedHrs));
				alAllResourceData.add(uF.formatIntoTwoDecimalWithOutComma(dblTotResourceActualHrs));
//				alAllResourceData.add(uF.formatIntoTwoDecimalWithOutComma((dblFilterHrs * alResourceIds.size())));
				alAllResourceData.add(uF.formatIntoTwoDecimalWithOutComma(dblTotResourceUtilization));
				alOuter.add(alAllResourceData);
			}
			
			request.setAttribute("alOuter", alOuter);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs); 
			db.closeStatements(pst);  
			db.closeConnection(con);
		}
		
	}


	private List<String> getResourcewisePlannedActualTimeUtilization(Connection con, UtilityFunctions uF, String resourceId) {
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		List<String> alResourceData = new ArrayList<String>();
		try {
			StringBuilder sbQuery = new StringBuilder();			
			sbQuery = new StringBuilder();
			sbQuery.append("select ai.task_id,ai.activity_name,ai.idealtime,ai.start_date,ai.deadline,ai.resource_ids, p.actual_calculation_type " +
					" from activity_info ai, projectmntnc p where p.pro_id = ai.pro_id and ai.resource_ids like '%,"+resourceId+",%' ");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and ((ai.start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and ai.deadline <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"') or " +
					" (ai.start_date <= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and ai.deadline >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"') or " +
					" (ai.start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and ai.start_date <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"')) ");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			List<List<String>> alData = new ArrayList<List<String>>();
			while(rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("task_id"));
				innerList.add(rs.getString("activity_name"));
				innerList.add(rs.getString("idealtime"));
				innerList.add(rs.getString("start_date"));
				innerList.add(rs.getString("deadline"));
				innerList.add(rs.getString("resource_ids"));
				innerList.add(rs.getString("actual_calculation_type")); //6
				
				alData.add(innerList);
			}
			rs.close();
			pst.close();
//			System.out.println(resourceId + " --- alData ===>> " + alData);
			double dblResourcePlannedHrs = 0;
			double dblResourceActualHrs = 0;
			String dblFilterDays = uF.dateDifference(getStrStartDate(), DATE_FORMAT, getStrEndDate(), DATE_FORMAT);
//			System.out.println("dblFilterDays ===>> " + dblFilterDays);
			double dblFilterHrs = uF.parseToDouble(dblFilterDays) * 8;
//			System.out.println("dblFilterHrs ===>> " + dblFilterHrs);
			for(int i=0; alData !=null && i<alData.size(); i++) {
				List<String> innerList = alData.get(i);
				if(innerList.get(3) != null && innerList.get(4) != null) {
					String dblTaskDays = uF.dateDifference(innerList.get(3), DBDATE, innerList.get(4), DBDATE);
					double dblTaskHrs = uF.parseToDouble(dblTaskDays) * 8;
					double dblPlannedHrs = 0;
					if(uF.parseToInt(dblTaskDays)>0) {
					}
					if(uF.parseToInt(dblFilterDays)>0 && uF.parseToInt(dblTaskDays)>0 && uF.parseToInt(dblTaskDays)>uF.parseToInt(dblFilterDays)) {
						if(innerList.get(6) != null && innerList.get(6).equalsIgnoreCase("D")) {
							dblPlannedHrs = (uF.parseToDouble(innerList.get(2)) * uF.parseToDouble(dblFilterDays)) / uF.parseToDouble(dblTaskDays);
							dblPlannedHrs = dblPlannedHrs * 8; 
						} else if(innerList.get(6) != null && innerList.get(6).equalsIgnoreCase("M")) {
							dblPlannedHrs = ((uF.parseToDouble(innerList.get(2)) * 30) * uF.parseToDouble(dblFilterDays)) / uF.parseToDouble(dblTaskDays);
							dblPlannedHrs = dblPlannedHrs * 8 * 30;
						} else {
							dblPlannedHrs = (uF.parseToDouble(innerList.get(2)) * dblFilterHrs) / dblTaskHrs;
						}
						
					} else {
						dblPlannedHrs = uF.parseToDouble(innerList.get(2));
						if(innerList.get(6) != null && innerList.get(6).equalsIgnoreCase("D")) {
							dblPlannedHrs = dblPlannedHrs * 8; 
						} else if(innerList.get(6) != null && innerList.get(6).equalsIgnoreCase("M")) {
							dblPlannedHrs = dblPlannedHrs * 8 * 30;
						}
					}
					List<String> alResources = Arrays.asList(((innerList.get(5) != null && innerList.get(5).length()>1) ? innerList.get(5).substring(1, innerList.get(5).length()-1) : "").split(","));
					if(alResources != null && alResources.size()>1) {
						dblPlannedHrs = dblPlannedHrs / alResources.size();
					}
					
					dblResourcePlannedHrs += dblPlannedHrs;
					
					double dblActualHrs = getResourcewiseTaskwiseActualWorkTime(con, uF, resourceId, innerList.get(0));
					dblResourceActualHrs += dblActualHrs;
				}
			}
//			System.out.println("dblResourcePlannedHrs ===>> " + dblResourcePlannedHrs);
//			double dblPlannedUtilization = (dblResourcePlannedHrs * 100) / dblFilterHrs;
//			System.out.println("dblPlannedUtilization ===>> " + dblPlannedUtilization);
			
//			double dblActualUtilization = 0;
			alResourceData.add(uF.formatIntoTwoDecimalWithOutComma(dblResourcePlannedHrs));
			alResourceData.add(uF.formatIntoTwoDecimalWithOutComma(dblResourceActualHrs));
			alResourceData.add(alData.size()+"");
//			alResourceData.add(uF.formatIntoTwoDecimalWithOutComma(dblPlannedUtilization));
//			alResourceData.add(uF.formatIntoTwoDecimalWithOutComma(dblResourcePlannedHrs));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return alResourceData;
	}
	
	private double getResourcewiseTaskwiseActualWorkTime(Connection con, UtilityFunctions uF, String resourceId, String taskId) {
		PreparedStatement pst = null;
		ResultSet rs  = null;
		double dblActualHrs = 0;
		try {
			if(resourceId.trim().length()>0) {
				StringBuilder sbQuery = new StringBuilder();			
				sbQuery.append("select sum(actual_hrs) as tot_actual_hrs from task_activity where activity_id=? and emp_id in ("+resourceId+") ");
				if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
					sbQuery.append(" and task_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"' ");
				}
				pst = con.prepareStatement(sbQuery.toString());
				pst.setInt(1, uF.parseToInt(taskId));
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					dblActualHrs = rs.getDouble("tot_actual_hrs");
				}
				rs.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblActualHrs;
	}
	

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		if(isPoFlag()){
			alFilter.add("PROJECT_TYPE");
			if(getStrProType()!=null) {
				String strProType="";
				if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
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
		
		
		/*alFilter.add("LEVEL");
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
		}*/
		
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
		
		
		alFilter.add("COUNTRY");
		if(getF_country()!=null) {
			String strCountry="";
			int k=0;
			for(int i=0; countryList!=null && i<countryList.size();i++) {
				for(int j=0;j<getF_country().length;j++) {
					if(getF_country()[j].equals(countryList.get(i).getCountryId())) {
						if(k==0) {
							strCountry=countryList.get(i).getCountryName();
						} else {
							strCountry+=", "+countryList.get(i).getCountryName();
						}
						k++;
					}
				}
			}
			if(strCountry!=null && !strCountry.equals("")) {
				hmFilter.put("COUNTRY", strCountry);
			} else {
				hmFilter.put("COUNTRY", "All Countries");
			}
		} else {
			hmFilter.put("COUNTRY", "All Countries");
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
			String strFdt = "-";
			String strEdt = "-";
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null")) {
				strFdt = uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat());
			}
			if(getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				strEdt = uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat());
			}
			hmFilter.put("FROMTO",  strFdt+" - "+ strEdt);
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
			int nFYSDay = 0;
			String[] strFinancialYears = null;
			if (getFinancialYear() != null) {
				strFinancialYears = getFinancialYear().split("-");
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
				nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
			} else {
				strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
				nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
			}
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			cal.set(Calendar.DATE, nFYSDay);
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
	

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
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

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
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

	public String[] getF_department() {
		return f_department;
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

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
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

	public List<FillServices> getProjectServiceList() {
		return projectServiceList;
	}

	public void setProjectServiceList(List<FillServices> projectServiceList) {
		this.projectServiceList = projectServiceList;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
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

	public List<FillCountry> getCountryList() {
		return countryList;
	}

	public void setCountryList(List<FillCountry> countryList) {
		this.countryList = countryList;
	}

	public String[] getF_country() {
		return f_country;
	}

	public void setF_country(String[] f_country) {
		this.f_country = f_country;
	}

	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}

	public String getFilterType() {
		return filterType;
	}

	public void setFilterType(String filterType) {
		this.filterType = filterType;
	}

	public String getMonthFinancialYear() {
		return monthFinancialYear;
	}

	public void setMonthFinancialYear(String monthFinancialYear) {
		this.monthFinancialYear = monthFinancialYear;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

}