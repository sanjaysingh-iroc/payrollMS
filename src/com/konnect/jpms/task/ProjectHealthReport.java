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
import com.konnect.jpms.select.FillProject;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectHealthReport extends ActionSupport implements ServletRequestAware, IStatements {
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
	String[] f_project;
	
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
	List<FillProject> projectList;
	
	String strProType;
	boolean poFlag;
	
	String projectHelthType;
	
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
		getProjectHealthReport(uF);
		
		return loadProjectHealthReport(uF);

	}
	
	
	public String loadProjectHealthReport(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
			projectList = new ArrayList<FillProject>();
			if(getF_client()!=null)	{
				projectList = new FillProject(request).fillProjects(getF_client());
			} else {
				projectList = new FillProject(request).fillProjects();
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
				countryList = new FillCountry(request).fillWorkLocationCountry(StringUtils.join(getF_strWLocation(), ","));
			} else {
				countryList = new FillCountry(request).fillWorkLocationCountry((String)session.getAttribute(WLOCATION_ACCESS));
			}
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
			projectList = new ArrayList<FillProject>();
			if(getF_client()!=null)	{
				projectList = new FillProject(request).fillProjects(getF_client());
			} else {
				projectList = new FillProject(request).fillProjects();
			}
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
		//===start parvez date: 14-10-2022===	
//			sbQuery.append("select * from projectmntnc pmc where project_owner=?");
			sbQuery.append("select * from projectmntnc pmc where project_owners like '%,"+strEmpId+",%'");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strEmpId));
		//===end parvez date: 14-10-2022===	
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
	
	
	private void getProjectHealthReport(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		List<List<String>> alOuter = new ArrayList<List<String>>();
		
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try {
			con = db.makeConnection(con);
			
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

//			System.out.println("getFinancialYear() ===>> "+ getFinancialYear());
			
			StringBuilder sbQuery = new StringBuilder();			
			sbQuery.append("select ai.task_id, p.pro_name, p.actual_calculation_type, ai.activity_name, ai.completed, ai.resource_ids, ai.idealtime, " +
				"ai.start_date, ai.deadline from activity_info ai, projectmntnc p " +
				"where ai.pro_id = p.pro_id and ai.pro_id in (select pro_id from projectmntnc where pro_id > 0 ");	
			
			if(getF_project() != null && getF_project().length>0) {
				sbQuery.append(" and pro_id in ("+StringUtils.join(getF_project(), ",")+") ");
			}
		//===start parvez date: 14-10-2022===	
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
		//===end parvez date: 14-10-2022===	
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
			sbQuery.append(") ");
			
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and ((ai.start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and ai.deadline <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"') or " +
					" (ai.start_date <= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and ai.deadline >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"') or " +
					" (ai.start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and ai.start_date <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"')) ");
			}
			sbQuery.append(" order by ai.pro_id ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
//			System.out.println("getProjectHelthType ===>> " + getProjectHelthType());
			rs = pst.executeQuery();
//			String dblFilterDays = uF.dateDifference(getStrStartDate(), DATE_FORMAT, getStrEndDate(), DATE_FORMAT);
//			double dblFilterHrs = uF.parseToDouble(dblFilterDays) * 8;
//			String dblTaskDays = "";
			double dblPlannedHrs = 0;
			while(rs.next()){
				List<String> alInner = new ArrayList<String>();
				
				alInner.add(rs.getString("task_id"));
				alInner.add(uF.showData(rs.getString("pro_name"), ""));
				alInner.add(uF.showData(rs.getString("activity_name"), ""));
				alInner.add(uF.showData(rs.getString("resource_ids"), "")); //3
//				String[] strResource = (rs.getString("resource_ids") != null && rs.getString("resource_ids").length()>1) ? rs.getString("resource_ids").substring(1, rs.getString("resource_ids").length()-1).split(",") : "".split(","); 
				if(uF.parseToInt(getProjectHelthType()) == 3) {
					alInner.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), null), "")); //4
				} else {
					alInner.add(""); //4
				}
//				if(rs.getString("start_date") != null && rs.getString("deadline") != null) {
//					dblTaskDays = uF.dateDifference(rs.getString("start_date"), DBDATE, rs.getString("deadline"), DBDATE);
//				}
//				double dblTaskHrs = uF.parseToDouble(dblTaskDays) * 8;
				if(rs.getString("actual_calculation_type") != null && rs.getString("actual_calculation_type").equalsIgnoreCase("D")) {
//					dblPlannedHrs = (uF.parseToDouble(rs.getString("idealtime")) * uF.parseToDouble(dblFilterDays)) / uF.parseToDouble(dblTaskDays);
					dblPlannedHrs = uF.parseToDouble(rs.getString("idealtime"));
					dblPlannedHrs = dblPlannedHrs * 8; 
				} else if(rs.getString("actual_calculation_type") != null && rs.getString("actual_calculation_type").equalsIgnoreCase("M")) {
//					dblPlannedHrs = ((uF.parseToDouble(rs.getString("idealtime")) * 30) * uF.parseToDouble(dblFilterDays)) / uF.parseToDouble(dblTaskDays);
					dblPlannedHrs = uF.parseToDouble(rs.getString("idealtime"));
					dblPlannedHrs = dblPlannedHrs * 8 * 30;
				} else {
					dblPlannedHrs = uF.parseToDouble(rs.getString("idealtime"));
				}
//				alInner.add((rs.getString("start_date")!=null && !rs.getString("start_date").equals("")) ? uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT_STR) : "");
//				alInner.add((rs.getString("deadline")!=null && !rs.getString("deadline").equals("")) ? uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT_STR) : "");
				double dblActualWorkTime = getResourcewiseTaskwiseActualWorkTime(con, uF, "", rs.getString("task_id"));
				double dblVariance = dblPlannedHrs - dblActualWorkTime;
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblPlannedHrs)); //5
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblActualWorkTime)); //6
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblVariance)); //7
//				System.out.println("task_id ===>> " + rs.getString("task_id"));
//				System.out.println("activity_name ===>> " + rs.getString("activity_name") + " -- " + rs.getString("resource_ids"));
				
				alOuter.add(alInner);
			}
			rs.close();
			pst.close();
			if(uF.parseToInt(getProjectHelthType()) != 3) {
				request.setAttribute("alOuter", alOuter);
			}
			
			if(uF.parseToInt(getProjectHelthType()) == 3) {
				Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
				List<List<String>> alOuter1 = new ArrayList<List<String>>();
				for(int i=0; alOuter != null && i<alOuter.size(); i++) {
					List<String> innerList = alOuter.get(i);
					if(innerList !=null) {
						List<String> alResource = new ArrayList<String>();
						if(innerList.get(3) != null) {
							alResource = Arrays.asList((innerList.get(3)!=null && innerList.get(3).length()>1) ? innerList.get(3).substring(1, innerList.get(3).length()-1).split(",") : "".split(","));
	//						System.out.println("alResource ===>> " + alResource.size());
							double resourceIdealtime = uF.parseToDouble(innerList.get(5));
							if(alResource.size()>0) {
								resourceIdealtime = uF.parseToDouble(innerList.get(5)) / alResource.size();
							}
							for(int j=0; alResource!=null && j<alResource.size(); j++) {
								if(uF.parseToInt(alResource.get(j)) > 0) {
									List<String> innList = new ArrayList<String>();
									innList.add(innerList.get(0));
									innList.add(innerList.get(1));
									innList.add(innerList.get(2));
									innList.add("");
									innList.add(hmEmpName.get(alResource.get(j)));
									innList.add(uF.formatIntoTwoDecimalWithOutComma(resourceIdealtime));
									double dblActualWorkTime = getResourcewiseTaskwiseActualWorkTime(con, uF, alResource.get(j), innerList.get(0));
									innList.add(uF.formatIntoTwoDecimalWithOutComma(dblActualWorkTime));
									double dblVariance = resourceIdealtime - dblActualWorkTime;
									innList.add(uF.formatIntoTwoDecimalWithOutComma(dblVariance));
									alOuter1.add(innList);
								}
							}
						}
					}
				}
				request.setAttribute("alOuter", alOuter1);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs); 
			db.closeStatements(pst);  
			db.closeConnection(con);
		}
		
	}


	private double getResourcewiseTaskwiseActualWorkTime(Connection con, UtilityFunctions uF, String resourceId, String taskId) {
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		double dblHours = 0;
		try {
			StringBuilder sbQuery = new StringBuilder();			
			sbQuery.append("select sum(actual_hrs) as tot_actual_hrs from task_activity where activity_id=? ");
			if(uF.parseToInt(resourceId) > 0) {
				sbQuery.append(" and emp_id = "+uF.parseToInt(resourceId)+" ");
			}
//			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
//				sbQuery.append(" and task_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"' ");
//			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(taskId));
//			pst.setInt(2, uF.parseToInt(resourceId));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				dblHours = rs.getDouble("tot_actual_hrs");
			}
			rs.close();
			pst.close();
//			System.out.println("dblHours ===>> " + dblHours);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dblHours;
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
		
		alFilter.add("PROJECT");
		if(getF_project()!=null) {
				String strProject="";
				int k=0;
				for(int i=0; projectList!=null && i<projectList.size();i++) {
					for(int j=0;j<getF_project().length;j++) {
						if(getF_project()[j].equals(projectList.get(i).getId())) {
							if(k==0) {
								strProject=projectList.get(i).getName();
							} else {
								strProject+=", "+projectList.get(i).getName();
							}
							k++;
						}
					}
				}
			if(strProject!=null && !strProject.equals("")) {
				hmFilter.put("PROJECT", strProject);
			} else {
				hmFilter.put("PROJECT", "All projects");
			}
		} else {
			hmFilter.put("PROJECT", "All Project");
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
			if (getMonthFinancialYear() != null) {
				strFinancialYears = getMonthFinancialYear().split("-");
				setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
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
//			System.out.println("nselectedMonth ===>> " + nselectedMonth);
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
		
//		System.out.println("filter getFinancialYear() ===>> "+ getFinancialYear());
		
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

	public String getProjectHelthType() {
		return projectHelthType;
	}

	public void setProjectHelthType(String projectHelthType) {
		this.projectHelthType = projectHelthType;
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


	public String[] getF_project() {
		return f_project;
	}


	public void setF_project(String[] f_project) {
		this.f_project = f_project;
	}


	public List<FillProject> getProjectList() {
		return projectList;
	}


	public void setProjectList(List<FillProject> projectList) {
		this.projectList = projectList;
	}

	
		
}