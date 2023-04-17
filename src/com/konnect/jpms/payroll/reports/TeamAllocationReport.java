package com.konnect.jpms.payroll.reports;

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
import org.apache.log4j.Logger;
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
import com.konnect.jpms.task.FillProjectList;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

/**
 * @author workrig
 *
 */
public class TeamAllocationReport extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType; 
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	private static Logger log = Logger.getLogger(TeamAllocationReport.class);
	 
	String reportType;
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
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		request.setAttribute(TITLE, "Team Allocation Report");
		request.setAttribute(PAGE, "/jsp/payroll/reports/TeamAllocationReport.jsp");
		
		String[] strPayCycleDates = null;
		
		 if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
			//strPayCycleDates = CF.getPrevPayCycle(strPayCycleDates[1], CF.getStrTimeZone(), CF);
		} else {
//			strPayCycleDates = CF.getCurrentPayCycle(CF.getStrTimeZone(), CF,request);
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
//		getPTaxReport(uF);
		
//		System.out.println("reportType === " + reportType);
		if(reportType!=null && uF.parseToInt(reportType)==3) {
//			getServiceWiseProject(uF,0);
		} else if(reportType!=null && uF.parseToInt(reportType)==2) {
			getDepartMentWiseProject(uF,0);
		} else if(reportType!=null && uF.parseToInt(reportType)==4) {
			getProjectWiseProject(uF,0);
		} else {
			reportType = "1";
			getClientWiseProject(uF,0);
		}
		return loadPTaxReport(uF);

	}
	
	public String getReportType() {
		return reportType;
	}

	public void setReportType(String reportType) {
		this.reportType = reportType;
	}


	public String loadPTaxReport(UtilityFunctions uF) {
			
//		paycycleList = new FillPayCycles(request).fillPayCycles(CF);
//		departmentList=new FillDepartment(request).fillDepartment();
		
		/*if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation();
			orgList = new FillOrganisation(request).fillOrganisation();
		}else{
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}*/
		
		/*	
		Collections.sort(orgList, new Comparator<FillOrganisation>() {

			@Override
			public int compare(FillOrganisation o1, FillOrganisation o2) {
				return o1.getOrgId().compareTo(o2.getOrgId());
			}
		});*/
		
		/*wLocationList = new FillWLocation().fillWLocation(getF_org());
		wLocationList.add(new FillWLocation("0","All Work Location"));
				
		Collections.sort(wLocationList, new Comparator<FillWLocation>() {

			@Override
			public int compare(FillWLocation o1, FillWLocation o2) {
				return o1.getwLocationId().compareTo(o2.getwLocationId());
			}
		});*/
		
//		projectdetailslist=new FillProjectList(request).fillAllApprovedProjectDetails();
//		clientlist=new FillClients(request).fillClients();

		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			wLocationList = new FillWLocation(request).fillWLocation();
			organisationList = new FillOrganisation(request).fillOrganisation();
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
		
		return LOAD;
	}
	
	
	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}


	public void getClientWiseProject(UtilityFunctions uF,int nLimit){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con=db.makeConnection(con);
			if(getSelectOne() != null && getSelectOne().equals("1") && (getStrStartDate() == null || getStrStartDate().equals("")) && (getStrEndDate() == null || getStrEndDate().equals(""))) {
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
//				System.out.println("getMonthFinancialYear() ===>> " + getMonthFinancialYear());
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
	
			
			StringBuilder sbQuery = new StringBuilder("select eod.emp_id from employee_personal_details epd, employee_official_details eod, " +
				"department_info di where epd.emp_per_id=eod.emp_id and eod.depart_id=di.dept_id ");
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id in ("+getF_org()+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(getF_department() != null && getF_department().length>0) {
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
			if(getF_level()!=null && getF_level().length>0) {
	            sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
			sbQuery.append(" order by eod.emp_id");
			pst=con.prepareStatement(sbQuery.toString());
			rs=pst.executeQuery();
			List<String> empIdList = new ArrayList<String>();
			while(rs.next()) {
				empIdList.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery1 = new StringBuilder("select pm.client_id, ai.task_id, ai.resource_ids from activity_info ai, projectmntnc pm where ai.pro_id = pm.pro_id ");
			if(getF_project_service() != null && getF_project_service().length>0) {
				String services = uF.getConcateData(getF_project_service());
				sbQuery1.append(" and pm.service in ("+services+") ");
			}
			if(getF_service() != null && getF_service().length>0) {
				sbQuery1.append(" and pm.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery1.append(" and pm.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_client() != null && getF_client().length>0) {
				sbQuery1.append(" and pm.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery1.append(" and ((ai.start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and ai.deadline <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"') or " +
					" (ai.start_date <= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and ai.deadline >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"') or " +
					" (ai.start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and ai.start_date <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"')) ");
			}
			/*if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery1.append(" and deadline between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}*/
		
			pst=con.prepareStatement(sbQuery1.toString());
			rs=pst.executeQuery();
			Map<String, List<String>> hmClientTaskResources = new HashMap<String, List<String>>();
			while(rs.next()) {
				List<String> innerList = hmClientTaskResources.get(rs.getString("client_id"));
				if(innerList == null) innerList = new ArrayList<String>();
				
				if(rs.getString("resource_ids") != null && !rs.getString("resource_ids").equals("")) {
					List<String> empList = Arrays.asList(rs.getString("resource_ids").split(","));
					for(int i=0; empList != null && i<empList.size(); i++) {
						if(empList.get(i) != null && !empList.get(i).trim().equals("") && !innerList.contains(empList.get(i).trim()) && empIdList != null && empIdList.contains(empList.get(i).trim())) {
							innerList.add(empList.get(i).trim());
	//						System.out.println(rs.getString("client_id") +" -- " + empList.get(i).trim() + " " + CF.getEmpNameMapByEmpId(con, empList.get(i).trim()));
						}
					}
				}
				hmClientTaskResources.put(rs.getString("client_id"), innerList);
			}
			rs.close();
			pst.close();
			
			List<List<String>> outerList = new ArrayList<List<String>>();
			StringBuilder sbQuery2 = new StringBuilder("select cd.client_id, client_name from projectmntnc pm, client_details cd where pm.pro_id>0 and pm.client_id = cd.client_id ");
			if(getF_project_service() != null && getF_project_service().length>0) {
				String services = uF.getConcateData(getF_project_service());
				sbQuery2.append(" and pm.service in ("+services+") ");
			}
			if(getF_service() != null && getF_service().length>0) {
				sbQuery2.append(" and pm.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery2.append(" and pm.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_client() != null && getF_client().length>0) {
				sbQuery2.append(" and pm.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery2.append(" and ((pm.start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and pm.deadline <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"') or " +
					" (pm.start_date <= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and pm.deadline >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"') or " +
					" (pm.start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and pm.start_date <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"')) ");
			}
			/*if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery2.append(" and deadline between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}*/
			sbQuery2.append(" group by cd.client_id, client_name order by client_name ");
			pst=con.prepareStatement(sbQuery2.toString());
			rs=pst.executeQuery();
			while(rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("client_name"));
				innerList.add(hmClientTaskResources != null && hmClientTaskResources.get(rs.getString("client_id")) != null ? ""+hmClientTaskResources.get(rs.getString("client_id")).size() : "0");
				outerList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("outerList",outerList);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
	}
	

//	public void getServiceWiseProject(UtilityFunctions uF,int nLimit) {
//		
//		Connection con = null;
//		PreparedStatement pst=null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		try {
//			
//			con=db.makeConnection(con);
//			
//			if(getSelectOne() != null && getSelectOne().equals("1") && getStrStartDate() == null && getStrEndDate() == null) {
//				
//				Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
//				
//				String startdate="01/"+uF.getDateFormat(""+currDate, DBDATE, "MM")+"/"+uF.getDateFormat(""+currDate, DBDATE, "yyyy");
//				
//				Calendar calendar = Calendar.getInstance();
//				calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
//				calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
//				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
//	
//				Date date = calendar.getTime();
//				DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
//				String endDate=DATE_FORMAT.format(date);
//				
//				setStrStartDate(startdate);
//				setStrEndDate(endDate);
//			} else if(getSelectOne() != null && getSelectOne().equals("2")) {
//				String[] strFinancialYears = null;
//				if (getFinancialYear() != null) {
//					strFinancialYears = getFinancialYear().split("-");
//					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
//					
//					setStrStartDate(strFinancialYears[0]);
//					setStrEndDate(strFinancialYears[1]);
//				} else {
//					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
//					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
//					
//					setStrStartDate(strFinancialYears[0]);
//					setStrEndDate(strFinancialYears[1]);
//				}
//			} else if(getSelectOne() != null && getSelectOne().equals("3")) {
//				
//				int nselectedMonth = uF.parseToInt(getStrMonth());
////				int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
//				int nFYSMonth = 0;
//				String[] strFinancialYears = null;
//				if (getFinancialYear() != null) {
//					strFinancialYears = getFinancialYear().split("-");
//					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
//					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
//				} else {
//					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
//					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
//					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
//				}
//				
//				Calendar cal = GregorianCalendar.getInstance();
//				cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
//				if(nselectedMonth>=nFYSMonth){
//					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "yyyy")));
//				}else{
//					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, "yyyy")));
//				}
//				
//				int nMonthStart = cal.getActualMinimum(Calendar.DATE);
//				int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
//				
//				setStrStartDate(nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
//				setStrEndDate(nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
//				
//				
//			} else if(getSelectOne() != null && getSelectOne().equals("4")) {
//				String[] strPayCycleDates = null;
//				if (getPaycycle() != null) {
//					strPayCycleDates = getPaycycle().split("-");
//					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
//					
//					setStrStartDate(strPayCycleDates[0]);
//					setStrEndDate(strPayCycleDates[1]);
//				} else {
//					strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
//					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
//					
//					setStrStartDate(strPayCycleDates[0]);
//					setStrEndDate(strPayCycleDates[1]);
//				}
//			}
//			
//	
//	//		StringBuilder sb=new StringBuilder("select a.*,service_name from (select pm.service,count(*)as empcount from projectmntnc pm ,(select a.*,ai.pro_id from activity_info ai,(select activity_id,emp_id from task_activity where activity_id in(select task_id from activity_info where pro_id in(select pro_id from projectmntnc) )group by activity_id,emp_id) as a where ai.task_id=a.activity_id) a where pm.pro_id=a.pro_id group by  pm.service)as a, services s where  cast(a.service as int )= s.service_id "+((nLimit>0)?" limit "+nLimit :""));
//			
//			StringBuilder sbQuery = new StringBuilder("select a.*,service_name from (select pm.service,count(*)as empcount from projectmntnc pm ,(select a.*,ai.pro_id from activity_info ai,(select activity_id,ta.emp_id from task_activity ta ,employee_personal_details epd ,employee_official_details eod ,department_info di where ta.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and eod.depart_id=di.dept_id and activity_id in(select task_id from activity_info where pro_id in(select pro_id from projectmntnc) )");
//			if(uF.parseToInt(getF_org())>0) {
//				sbQuery.append(" and eod.org_id in ("+getF_org()+")");
//			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
//				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//			}
//			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
//	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
//	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//			}
//			if(getF_department() != null && getF_department().length>0) {
//				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
//			}
//			if(getF_service()!=null && getF_service().length>0) {
//	            sbQuery.append(" and (");
//	            for(int i=0; i<getF_service().length; i++) {
//	                sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
//	                
//	                if(i<getF_service().length-1) {
//	                    sbQuery.append(" OR "); 
//	                }
//	            }
//	            sbQuery.append(" ) ");
//	        }
//			if(getF_level()!=null && getF_level().length>0) {
//	            sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
//	        }
//			
//			sbQuery.append(" group by activity_id,ta.emp_id) as a where ai.task_id=a.activity_id) a where pm.pro_id=a.pro_id " );
//			if(getF_project_service() != null && getF_project_service().length>0) {
//				String services = uF.getConcateData(getF_project_service());
//				sbQuery.append(" and pm.service in ("+services+") ");
//			}
//			if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase(LABEL_FROM_DATE) && !getStrEndDate().equalsIgnoreCase(LABEL_TO_DATE)) {
//				sbQuery.append(" and pm.start_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
//			}
//			sbQuery.append(" group by  pm.service)as a, services_project s where cast(a.service as int )= s.service_project_id ");
//			
//			if(getF_project_service() != null && getF_project_service().length>0) {
//				sbQuery.append(" and s.service_project_id in ("+StringUtils.join(getF_project_service(), ",")+") ");
//			}
//			
//			sbQuery.append(" order by service_name");
//			
//			sbQuery.append((nLimit>0)?" limit "+nLimit :"");	
//			
//			pst=con.prepareStatement(sbQuery.toString());
//	
//			rs=pst.executeQuery();
//			List<List<String>> outerList=new ArrayList<List<String>>();
//			while(rs.next()){
////				List<List<String>> outerList=clientwiseMap.get(rs.getString("service"));
////				if(outerList==null){
////					outerList=new ArrayList<List<String>>();
////					List<String> innerList=new ArrayList<String>();
////					innerList.add(rs.getString("service_name"));
////					outerList.add(innerList);
////				}
//				List<String> innerList=new ArrayList<String>();
//				innerList.add(rs.getString("service_name"));
//				innerList.add(rs.getString("empcount"));
////				innerList.add(rs.getString(""));
//				outerList.add(innerList);
////				clientwiseMap.put(rs.getString("service"), outerList);
//			}
//			rs.close();
//			pst.close();
//			request.setAttribute("outerList",outerList);
//			
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//			log.error(e.getClass() + ": " +  e.getMessage(), e);
//		}finally{
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//		
//		
//	}
	
public void getDepartMentWiseProject(UtilityFunctions uF,int nLimit) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
		con=db.makeConnection(con);
		
		if(getSelectOne() != null && getSelectOne().equals("1") && (getStrStartDate() == null || getStrStartDate().equals("")) && (getStrEndDate() == null || getStrEndDate().equals(""))) {
			
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
//			int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
			int nFYSMonth = 0;
			int nFYSDay = 0;
			String[] strFinancialYears = null;
//			System.out.println("getMonthFinancialYear() ===>> " + getMonthFinancialYear());
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
			if(nselectedMonth>=nFYSMonth) {
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
		}
		
		
		
		StringBuilder sbQuery = new StringBuilder("select eod.emp_id from employee_personal_details epd, employee_official_details eod, " +
			"department_info di where epd.emp_per_id=eod.emp_id and eod.depart_id=di.dept_id ");
		if(uF.parseToInt(getF_org())>0) {
			sbQuery.append(" and eod.org_id in ("+getF_org()+")");
		} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
			sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
		}
		if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	        sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	    } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
			sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
		}
		if(getF_department() != null && getF_department().length>0) {
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
		if(getF_level()!=null && getF_level().length>0) {
	        sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	    }
		sbQuery.append(" order by eod.emp_id");
		pst=con.prepareStatement(sbQuery.toString());
		rs=pst.executeQuery();
		List<String> empIdList = new ArrayList<String>();
		while(rs.next()) {
			empIdList.add(rs.getString("emp_id"));
		}
		rs.close();
		pst.close();
		
		StringBuilder sbQuery1 = new StringBuilder("select pm.department_id, ai.task_id, ai.resource_ids from activity_info ai, projectmntnc pm where ai.pro_id = pm.pro_id ");
		if(getF_project_service() != null && getF_project_service().length>0) {
			String services = uF.getConcateData(getF_project_service());
			sbQuery1.append(" and pm.service in ("+services+") ");
		}
		if(getF_service() != null && getF_service().length>0) {
			sbQuery1.append(" and pm.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
		}
		if(getF_department() != null && getF_department().length>0) {
			sbQuery1.append(" and pm.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
		}
		if(getF_client() != null && getF_client().length>0) {
			sbQuery1.append(" and pm.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
		}
		/*if(getStrStartDate() != null && !getStrStartDate().equals("From Date") && getStrEndDate() != null && !getStrEndDate().equals("To Date")) {
			sbQuery.append(" and start_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
		}*/
		if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
//			sbQuery.append(" and deadline between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			sbQuery.append(" and ('"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' between ai.start_date and ai.deadline or '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' between ai.start_date and ai.deadline " +
				"or ai.start_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' or ai.deadline between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ) ");
//			sbQuery.append(" and ('"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' between ai.start_date and ai.deadline or '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' between ai.start_date and ai.deadline) ");
		}
	
		pst=con.prepareStatement(sbQuery1.toString());
		rs=pst.executeQuery();
		Map<String, List<String>> hmClientTaskResources = new HashMap<String, List<String>>();
		while(rs.next()) {
			List<String> innerList = hmClientTaskResources.get(rs.getString("department_id"));
			if(innerList == null) innerList = new ArrayList<String>();
			
			if(rs.getString("resource_ids") != null && !rs.getString("resource_ids").equals("")) {
				List<String> empList = Arrays.asList(rs.getString("resource_ids").split(","));
				for(int i=0; empList != null && i<empList.size(); i++) {
					if(empList.get(i) != null && !empList.get(i).trim().equals("") && !innerList.contains(empList.get(i).trim()) && empIdList != null && empIdList.contains(empList.get(i).trim())) {
						innerList.add(empList.get(i).trim());
					}
				}
			}
			hmClientTaskResources.put(rs.getString("department_id"), innerList);
		}
		rs.close();
		pst.close();
		
		List<List<String>> outerList = new ArrayList<List<String>>();
		StringBuilder sbQuery2 = new StringBuilder("select di.dept_id, dept_name from projectmntnc pm, department_info di where pm.pro_id>0 and pm.department_id = di.dept_id ");
		if(getF_project_service() != null && getF_project_service().length>0) {
			String services = uF.getConcateData(getF_project_service());
			sbQuery2.append(" and pm.service in ("+services+") ");
		}
		if(getF_service() != null && getF_service().length>0) {
			sbQuery2.append(" and pm.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
		}
		if(getF_department() != null && getF_department().length>0) {
			sbQuery2.append(" and pm.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
		}
		if(getF_client() != null && getF_client().length>0) {
			sbQuery2.append(" and pm.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
		}
		/*if(getStrStartDate() != null && !getStrStartDate().equals("From Date") && getStrEndDate() != null && !getStrEndDate().equals("To Date")) {
			sbQuery.append(" and start_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
		}*/
		if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
//			sbQuery.append(" and deadline between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			sbQuery.append(" and ('"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' between pm.start_date and pm.deadline or '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' between pm.start_date and pm.deadline " +
					"or pm.start_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' or pm.deadline between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ) ");
		}
		sbQuery2.append(" group by di.dept_id, dept_name order by dept_name ");
		pst=con.prepareStatement(sbQuery2.toString());
		rs=pst.executeQuery();
		while(rs.next()) {
			List<String> innerList=new ArrayList<String>();
			innerList.add(rs.getString("dept_name"));
			innerList.add(hmClientTaskResources != null && hmClientTaskResources.get(rs.getString("dept_id")) != null ? ""+hmClientTaskResources.get(rs.getString("dept_id")).size() : "0");
			outerList.add(innerList);
		}
		rs.close();
		pst.close();
		request.setAttribute("outerList",outerList);
		
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public void getProjectWiseProject(UtilityFunctions uF,int nLimit) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
		con=db.makeConnection(con);
		if(getSelectOne() != null && getSelectOne().equals("1") && (getStrStartDate() == null || getStrStartDate().equals("")) && (getStrEndDate() == null || getStrEndDate().equals(""))) {
			
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
//			int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
			int nFYSMonth = 0;
			int nFYSDay = 0;
			String[] strFinancialYears = null;
//			System.out.println("getMonthFinancialYear() ===>> " + getMonthFinancialYear());
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
		
			StringBuilder sbQuery = new StringBuilder("select eod.emp_id from employee_personal_details epd, employee_official_details eod, " +
				"department_info di where epd.emp_per_id=eod.emp_id and eod.depart_id=di.dept_id ");
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and eod.org_id in ("+getF_org()+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(getF_department() != null && getF_department().length>0) {
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
			if(getF_level()!=null && getF_level().length>0) {
	            sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
			sbQuery.append(" order by eod.emp_id");
//			System.out.println("sbQuery ===>> " + sbQuery.toString());
			pst=con.prepareStatement(sbQuery.toString());
			rs=pst.executeQuery();
			List<String> empIdList = new ArrayList<String>();
			while(rs.next()) {
				empIdList.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
//			System.out.println("empIdList ===>> " + empIdList);
			
			StringBuilder sbQuery1 = new StringBuilder("select pm.pro_id, ai.task_id, ai.resource_ids from activity_info ai, projectmntnc pm where ai.pro_id = pm.pro_id ");
			if(getF_project_service() != null && getF_project_service().length>0) {
				String services = uF.getConcateData(getF_project_service());
				sbQuery1.append(" and pm.service in ("+services+") ");
			}
			if(getF_service() != null && getF_service().length>0) {
				sbQuery1.append(" and pm.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery1.append(" and pm.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_client() != null && getF_client().length>0) {
				sbQuery1.append(" and pm.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery1.append(" and ((ai.start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and ai.deadline <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"') or " +
					" (ai.start_date <= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and ai.deadline >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"') or " +
					" (ai.start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and ai.start_date <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"')) ");
			}
			/*if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and deadline between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}*/
//			System.out.println("sbQuery1 ===>> " + sbQuery1.toString());
		
			pst=con.prepareStatement(sbQuery1.toString());
			rs=pst.executeQuery();
			Map<String, List<String>> hmProTaskResources = new HashMap<String, List<String>>();
			while(rs.next()) {
				List<String> innerList = hmProTaskResources.get(rs.getString("pro_id"));
				if(innerList == null) innerList = new ArrayList<String>();
				
				if(rs.getString("resource_ids") != null && !rs.getString("resource_ids").equals("")) {
					List<String> empList = Arrays.asList(rs.getString("resource_ids").split(","));
					for(int i=0; empList != null && i<empList.size(); i++) {
						if(empList.get(i) != null && !empList.get(i).trim().equals("") && !innerList.contains(empList.get(i).trim()) && empIdList != null && empIdList.contains(empList.get(i).trim())) {
							innerList.add(empList.get(i).trim());
						}
					}
				}
				hmProTaskResources.put(rs.getString("pro_id"), innerList);
			}
			rs.close();
			pst.close();
//			System.out.println("hmProTaskResources ===>> " + hmProTaskResources);
			
			List<List<String>> outerList = new ArrayList<List<String>>();
			StringBuilder sbQuery2 = new StringBuilder("select pm.pro_id, pro_name from projectmntnc pm where pm.pro_id>0 ");
			if(getF_project_service() != null && getF_project_service().length>0) {
				String services = uF.getConcateData(getF_project_service());
				sbQuery2.append(" and pm.service in ("+services+") ");
			}
			if(getF_service() != null && getF_service().length>0) {
				sbQuery2.append(" and pm.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery2.append(" and pm.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_client() != null && getF_client().length>0) {
				sbQuery2.append(" and pm.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery2.append(" and ((pm.start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and pm.deadline <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"') or " +
					" (pm.start_date <= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and pm.deadline >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"') or " +
					" (pm.start_date >= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and pm.start_date <= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"')) ");
			}
			/*if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and deadline between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}*/
			sbQuery2.append(" order by pro_id desc, pro_name ");
			pst = con.prepareStatement(sbQuery2.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("pro_name"));
				innerList.add(hmProTaskResources != null && hmProTaskResources.get(rs.getString("pro_id")) != null ? ""+hmProTaskResources.get(rs.getString("pro_id")).size() : "0");
				outerList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("outerList",outerList);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
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
			String strtDate = "";
			String endDate = "";
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("") && !getStrStartDate().equals("")) {
				strtDate = uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat());
			}
			if(getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("") && !getStrEndDate().equals("")) {
				endDate = uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat());
			}
			hmFilter.put("FROMTO", strtDate +" - "+ endDate);
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
//			int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
			int nFYSMonth = 0;
			int nFYSDay = 0;
			String[] strFinancialYears = null;
//			System.out.println("getMonthFinancialYear() ===>> " + getMonthFinancialYear());
			if (getMonthFinancialYear() != null) {
				strFinancialYears = getMonthFinancialYear().split("-");
				setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
				nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
			} else {
				strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
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
			
			hmFilter.put("MONTH", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + uF.getMonth(uF.parseToInt(getStrMonth())));
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
	
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
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

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public String getMonthFinancialYear() {
		return monthFinancialYear;
	}

	public void setMonthFinancialYear(String monthFinancialYear) {
		this.monthFinancialYear = monthFinancialYear;
	}

}
