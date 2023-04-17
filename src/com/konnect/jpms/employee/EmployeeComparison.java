package com.konnect.jpms.employee;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.performance.FillAttribute;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillPeriod;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;


public class EmployeeComparison extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -2703809772420309012L;
	private static Logger log = Logger.getLogger(EmployeeComparison.class);
	
	private HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strBaseUserType;
	String strSessionEmpId;
	
	private String submit;
	private String[] strEmpId;
	private String dataType;
	private String strEmpIds1;
	
	private String f_strWLocation;
	private String f_department;
	private String f_service;
	private String f_level;
	private String f_org;
	
	private List<FillOrganisation> organisationList;
	private List<FillWLocation> wLocationList;
	private List<FillServices> serviceList;
	private List<FillDepartment> departmentList;
//	private List<List<String>> attriblist;
	private Map<String, String> hmEmpPerformanceAvg;
	private Map<String, String> hmEmpSkillAvg;
	private Map<String, String> hmEmpReviewAvg;
	private Map<String, String> hmEmpKRAAvg;
	private Map<String, String> hmEmpTargetAvg;
	private Map<String, String> hmEmpGoalsAvg;
	private Map<String, String> hmEmpGoalsKRATargetsAvg;
	
	private String level;
	
	private List<FillLevel> levelList;
	
	private List<FillAttribute> attributeList;
	
	private String dateParam;
	private String []filterParam;
	private String attribParam;
	private String filterParam1;
	
	private List<FillPeriod> periodList;
	private String period;
	private String strStartDate;
	private String strEndDate;
	
	public String execute() {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		/*	boolean isView  = CF.getAccess(session, request, uF);
		if(!isView) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		request.setAttribute(PAGE, PEmployeeComparison);
		request.setAttribute(TITLE, TEmployeeComparison);*/
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		/*StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-cubes\"></i><a href=\"TeamPerformance.action\" style=\"color: #3c8dbc;\"> Analytics</a></li>" +
			"<li class=\"active\">Team Comparison</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());*/
		
		if(getF_org()==null) {
			setF_org((String)session.getAttribute(ORGID));
			setF_strWLocation((String)session.getAttribute(WLOCATIONID));
		} else if(uF.parseToInt(getF_org()) == uF.parseToInt(((String)session.getAttribute(ORGID))) && getF_strWLocation() == null) {
			setF_strWLocation((String)session.getAttribute(WLOCATIONID));
		}
		
		if(getDataType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setDataType("MYTEAM");
		}
		
		attributeList=new FillAttribute(request).fillAttribute();
		
		String []arrEnabledModules = CF.getArrEnabledModules();
		request.setAttribute("arrEnabledModules", arrEnabledModules);
		
		if(getStrEmpIds1() != null && !getStrEmpIds1().equals("") && !getStrEmpIds1().equalsIgnoreCase("null")) {
			setStrEmpId(getStrEmpIds1().split(","));
		}
		
		if(getFilterParam1() != null && !getFilterParam1().equals("") && !getFilterParam1().equalsIgnoreCase("null")) {
			setFilterParam(getFilterParam1().split(","));
		} 
		
		if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) {
			getElementList();
			getAttributeList1();
		}
		String strD1 = "", strD2 = "";
//	System.out.println("getDateParam() ===>> " + getDateParam());
		if(getDateParam()!=null && uF.parseToInt(getDateParam())==1) {
			if(getPeriod()!=null && getPeriod().equalsIgnoreCase("T")) {
				strD2 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("Y")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1W")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 7)+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1M")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 30)+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L3M")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 90)+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L6M")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 180)+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1Y")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 365)+"", DBDATE, DATE_FORMAT);
			}
		} else if(getDateParam()!=null && uF.parseToInt(getDateParam())==2) {
			strD1 = getStrStartDate();
			strD2 = getStrEndDate();
		} else if(getDateParam()==null) {
			strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
			strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 30)+"", DBDATE, DATE_FORMAT);
	
			setDateParam("2");
		}
		setStrStartDate(strD1);
		setStrEndDate(strD2);
		
		loadLists(uF);
//		compareEmployees(uF);

		return SUCCESS;
	}
	 
	final public static String selectEmp = "SELECT * FROM(SELECT * FROM(SELECT * FROM(SELECT * FROM (SELECT * FROM " +
											"(SELECT * FROM employee_personal_details WHERE emp_per_id = ?) jeod " +
											"LEFT JOIN employee_official_details eod ON jeod.emp_per_id = eod.emp_id) jgd " +
											"LEFT JOIN grades_details gd ON jgd.grade_id = gd.grade_id) jdd " +
											"LEFT JOIN designation_details dd ON jdd.designation_id = dd.designation_id) jwli " +
											"LEFT JOIN work_location_info wli ON jwli.wlocation_id = wli.wlocation_id) jdi " +
											"LEFT JOIN department_info di ON jdi.depart_id = di.dept_id";
	
	final public static String selectEmployeeDetails = "SELECT * FROM (SELECT * FROM employee_personal_details WHERE emp_per_id=?) jeod " +
											"LEFT JOIN employee_official_details eod ON jeod.emp_per_id = eod.emp_id";
	
	final public static String selectGrossCompensation = "SELECT sum(amount) FROM payroll_generation WHERE emp_id=? and paycycle=? and " +
			"								financial_year_from_date <= ? and financial_year_to_date >= ? and earning_deduction=?";
	
	final public static String selectLoggedEfforts = "SELECT sum(hours_worked) FROM attendance_details WHERE emp_id=? and " +
													"to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ?";
	

	
//	private void compareEmployees(UtilityFunctions uF) {
//		
//		Connection con = null;
//		PreparedStatement pst = null;
//		PreparedStatement pst1 =null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		List<List<String>> alEmpCompareData = new ArrayList<List<String>>(); 
//		List<String> alEmployeeNames = new ArrayList<String>();
//		List<String> alGrossComp = new ArrayList<String>();
//		List<String> alLoggedHours = new ArrayList<String>();
////		List<String> alProjectHours = new ArrayList<String>();
//		
//		try {
//			
//			con = db.makeConnection(con);
//			
//			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
//			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
//			
//			
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
//			getSelectedFilter(uF, hmEmpName);
//			
//					String strD1 = "", strD2 = "", nPayCycle = "";
////					System.out.println("getDateParam() ===>> " + getDateParam());
//					if(getDateParam()!=null && uF.parseToInt(getDateParam())==1) {
//						if(getPeriod()!=null && getPeriod().equalsIgnoreCase("T")) {
//							strD2 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
//							strD1 = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
//						} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("Y")) {
//							strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DATE_FORMAT);
//							strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1)+"", DBDATE, DATE_FORMAT);
//						} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1W")) {
//							strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
//							strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 7)+"", DBDATE, DATE_FORMAT);
//						} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1M")) {
//							strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
//							strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 30)+"", DBDATE, DATE_FORMAT);
//						} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L3M")) {
//							strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
//							strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 90)+"", DBDATE, DATE_FORMAT);
//						} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L6M")) {
//							strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
//							strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 180)+"", DBDATE, DATE_FORMAT);
//						} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1Y")) {
//							strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
//							strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 365)+"", DBDATE, DATE_FORMAT);
//						}
//					} else if(getDateParam()!=null && uF.parseToInt(getDateParam())==2) {
//						strD1 = getStrStartDate();
//						strD2 = getStrEndDate();
//					} else if(getDateParam()==null) {
//						strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
//						strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 30)+"", DBDATE, DATE_FORMAT);
//
//						setDateParam("2");
//					}
//					setStrStartDate(strD1);
//					setStrEndDate(strD2);
//					
//					
//					
//					
//					boolean flagGC = false;
//					boolean flagLH = false;
//					for(int i=0; getFilterParam()!=null && i<getFilterParam().length; i++) {
//						if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("GC")) {
//							request.setAttribute("GC", "GC");
//							flagGC = true;
//						}
//						
//						if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("LH")) {
//							request.setAttribute("LH", "LH");
//							flagLH = true;
//						}
//						
//						if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("SKILL")) {
//							request.setAttribute("SKILL", "SKILL");
//							hmEmpSkillAvg = getEmpSkillRating(con);
//							request.setAttribute("hmEmpSkillAvg", hmEmpSkillAvg);
//						}
//						if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("REVIEW")) {
//							request.setAttribute("REVIEW", "REVIEW");
//							hmEmpReviewAvg = getEmpOverallReviewRating(con);
//							request.setAttribute("hmEmpReviewAvg", hmEmpReviewAvg);
//						}
//						if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("GOAL_KRA_TARGET")) {
//							request.setAttribute("GOAL_KRA_TARGET", "GOAL_KRA_TARGET");
//							hmEmpGoalsKRATargetsAvg = getEmpOverallGoalsKRATargetsRating(con);
//							request.setAttribute("hmEmpGoalsKRATargetsAvg", hmEmpGoalsKRATargetsAvg);
//						}
//						
//						/*if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("GOAL")) {
//							request.setAttribute("GOAL", "GOAL");
//								hmEmpGoalsAvg = getEmpOverallGoalsRating(con);
//							request.setAttribute("hmEmpGoalsAvg", hmEmpGoalsAvg);
//						}
//						if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("KRA")) {
//							request.setAttribute("KRA", "KRA");
//								hmEmpKRAAvg = getEmpOverallKRARating(con);
//							request.setAttribute("hmEmpKRAAvg", hmEmpKRAAvg);
//						}
//						if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("TARGET")) {
//							request.setAttribute("TARGET", "TARGET");
//								hmEmpTargetAvg = getEmpOverallTargetRating(con);
//							request.setAttribute("hmEmpTargetAvg", hmEmpTargetAvg);
//						}*/
//						
//						if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("AT")) {
////							setFilterParamVal("AT");
//							request.setAttribute("AT", "AT");
//							getEmpPerformanceOverall(con);
//							if(getAttribParam()!=null && !getAttribParam().equals("")) {
//								hmEmpPerformanceAvg = getEmpPerformanceAttribWise(con);
//							}
//							request.setAttribute("attriblist", attriblist);
//							request.setAttribute("hmEmpPerformanceAvg", hmEmpPerformanceAvg);
//						}
//					}
//					Map<String, String> hmLoggedHours = new HashMap<String, String>();
//					pst = con.prepareStatement("SELECT sum(hours_worked) as hours_worked, emp_id FROM attendance_details WHERE in_out = 'OUT' and to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ? group by emp_id");
//					pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
//					pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
//					rs = pst.executeQuery();
//					while(rs.next()) {
//						hmLoggedHours.put(rs.getString("emp_id"), rs.getString("hours_worked"));
//					}
//					rs.close();
//					pst.close();
//					
////					Map<String, String> hmProjectHours = new HashMap<String, String>();
////					pst = con.prepareStatement("select sum(actual_hrs) as actual_hrs, emp_id from task_activity where task_date between ? and ? and activity_id >0 group by emp_id");
////					pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
////					pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
////					rs = pst.executeQuery();
////					while(rs.next()) {
////						hmProjectHours.put(rs.getString("emp_id"), rs.getString("actual_hrs"));
////					}
//					
////					System.out.println("pst===>"+pst);
//					
//					Map<String, String> hmGrossSalary = CF.getEmpGrossSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "H");
//					
////					getMyTeam(con, uF);
////					CF.getEmpUserTypeId(con, request, strSessionEmpId);
//					
//					for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
//						pst = con.prepareStatement(selectEmp);
//	//					pst.setString(1, sbEmpIds.toString());
//						pst.setInt(1, uF.parseToInt(getStrEmpId()[i]));
//						rs = pst.executeQuery();
//						
//						while(rs.next()) {
//							
//							String strEmpMName = "";
//							if(flagMiddleName) {
//								if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
//									strEmpMName = " "+rs.getString("emp_mname");
//								}
//							}
//							
//							alEmployeeNames.add("'"+rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname")+"'");
//							List<String> alInner = new ArrayList<String>();
//							int emp_id = rs.getInt("emp_per_id");
//							alInner.add(rs.getString("emp_image"));
//							alInner.add(rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
//							alInner.add(rs.getString("empcode"));
//							alInner.add(rs.getString("designation_name"));
//							
//							//Supervisor =>
//							String supervisorName = hmEmpName.get(rs.getString("supervisor_emp_id"));
//							
//							alInner.add(supervisorName);	//3
//							alInner.add(rs.getString("wlocation_name"));	//4
//							alInner.add(rs.getString("dept_name"));	//5
//							alInner.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));	//6
//							
//							
//							double dblLoggedEffort = uF.parseToDouble(hmLoggedHours.get(rs.getString("emp_per_id")));
//							double dblHOurlyRate = uF.parseToDouble(hmGrossSalary.get(rs.getString("emp_per_id")));
////							double dblProjectEffort = uF.parseToDouble(hmProjectHours.get(rs.getString("emp_per_id")));
//							
//							alInner.add(uF.formatIntoOneDecimal(dblLoggedEffort * dblHOurlyRate));
//							alInner.add(uF.formatIntoOneDecimal(dblLoggedEffort));
////							alInner.add(uF.formatIntoOneDecimal(dblProjectEffort));
//							if(flagGC) {
//								alGrossComp.add(uF.formatIntoOneDecimalWithOutComma(dblLoggedEffort * dblHOurlyRate));
//							} else {
//								alGrossComp.add("");
//							}
//							if(flagLH) {
//								alLoggedHours.add(uF.formatIntoOneDecimalWithOutComma(dblLoggedEffort));
//							} else {
//								alLoggedHours.add("");
//							}
////							alProjectHours.add(uF.formatIntoOneDecimalWithOutComma(dblProjectEffort));
//							/*
//							
//							//Gross Compensation =>
//							
//							pst = con.prepareStatement(selectGrossCompensation);
//							pst.setInt(1, emp_id);
//							pst.setInt(2, uF.parseToInt(nPayCycle));
//							pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
//							pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
//							pst.setString(5, "E");
//							
//							log.debug("pst selectGrossCompensation=>"+pst);
//							rsGrossComp = pst.executeQuery();
//							
//							double dblGrossComp = 0;
//							
//							while(rsGrossComp.next()) {
//								dblGrossComp = uF.parseToDouble(rsGrossComp.getString(1));
//							}
//							
//							alInner.add(uF.formatIntoOneDecimal(dblGrossComp));
//							alGrossComp.add(uF.formatIntoOneDecimalWithOutComma(dblGrossComp));
//							
//							//Logged Effort =>
//							
//							pst = con.prepareStatement(selectLoggedEfforts);
//							pst.setInt(1, emp_id);
//							pst.setDate(2, uF.getDateFormat(strD1, DATE_FORMAT));
//							pst.setDate(3, uF.getDateFormat(strD2, DATE_FORMAT));
//							
//							
//							log.debug("pst selectLoggedEfforts=>"+pst);
//							rsLoggedEffort = pst.executeQuery();
//							
//							double dblLoggedEffort = 0;
//							
//							while(rsLoggedEffort.next()) {
//							elementouterList	dblLoggedEffort = uF.parseToDouble(rsLoggedEffort.getString(1));
//							}
//							
//							alInner.add(uF.formatIntoOneDecimal(dblLoggedEffort));
//							alLoggedHours.add(uF.formatIntoOneDecimalWithOutComma(dblLoggedEffort));
//							
//							log.debug("alInner==>"+alInner);
//							*/
//							alInner.add(rs.getString("emp_per_id"));
//							alEmpCompareData.add(alInner);
//							
//						}
//						rs.close();
//						pst.close();
//					}
//					
////					log.debug("alEmpCompareData==>"+alEmpCompareData);
//					
//					request.setAttribute("alEmpCompareData", alEmpCompareData);
//					request.setAttribute("alEmployeeNames", alEmployeeNames);
//					request.setAttribute("alGrossComp", alGrossComp);
//					request.setAttribute("alLoggedHours", alLoggedHours);
////					request.setAttribute("alProjectHours", alProjectHours);
//			
//		} catch (Exception e) {
//				e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeStatements(pst1);
//			db.closeConnection(con);
//		}
//			
//		
//	}

//	public void getMyTeam(Connection con, UtilityFunctions uF) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try {
//			Map<String, String> hmEmp = new HashMap<String, String>();
//			Map<String,String> empImageMap=new HashMap<String,String>();
//				pst=con.prepareStatement("select emp_per_id,emp_fname,emp_lname from employee_personal_details epd join " +
//	        		"employee_official_details eod on (epd.emp_per_id = eod.emp_id) join department_info di on (depart_id=dept_id) " +
//	        		"join grades_details gd on eod.grade_id=gd.grade_id join designation_details dd on gd.designation_id=dd.designation_id " +
//	        		"join level_details ld on dd.level_id=ld.level_id join org_details od  on ld.org_id=od.org_id where  is_alive= true " +
//	        		" and emp_per_id >0 and supervisor_emp_id = ? order by emp_id");
//				pst.setInt(1, uF.parseToInt(strSessionEmpId));
//				rs=pst.executeQuery();
//				while(rs.next()) {
//					hmEmp.put(rs.getString("emp_per_id"), rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
//				}
//			request.setAttribute("hmEmp", hmEmp);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	
	
	private Map<String, String> getEmpOverallTargetRating(Connection con) {
		Map<String, String> hmEmpListAvg = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			StringBuilder tmpempid = new StringBuilder();
			for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
				if(i==0) {
					tmpempid.append(getStrEmpId()[i]);
				} else {
					tmpempid.append(","+getStrEmpId()[i]);
				}
			} 
			if(tmpempid.length() > 0) {
				Map<String, String> hmEmpReviewAverage = new HashMap<String, String>();
				pst = con.prepareStatement("select *,((marks*100/weightage)/20) as average from(select emp_id,sum(marks) as marks, sum(weightage) " +
					"as weightage from appraisal_question_answer where question_id in (select question_bank_id from question_bank where kra_id " +
					"is not null and goal_kra_target_id in (select goal_id from goal_details where measure_kra = 'Measure')) and " +
					"emp_id in ("+tmpempid.toString()+") and attempted_on between ? and ? and weightage>0 group by emp_id) as aa");
				pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
				rs = pst.executeQuery();
				while (rs.next()) {
					hmEmpReviewAverage.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("average"))));
				}
				rs.close();
				pst.close();
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select gksrd.*, gd.goal_id,gksrd.emp_id from goal_kra_status_rating_details gksrd, goal_details gd " +
						"where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+tmpempid.toString()+") and gksrd.goal_id in(select goal_id " +
						" from goal_details where measure_kra = 'Measure')");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				Map<String, String> hmEmpwiseRatingAndCount = new HashMap<String, String>();
				while (rs.next()) {
					double totRating = uF.parseToDouble(hmEmpwiseRatingAndCount.get(rs.getString("emp_id")+"_RATING"));
					int totCount = uF.parseToInt(hmEmpwiseRatingAndCount.get(rs.getString("emp_id")+"_COUNT"));
					
					if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
						double strCurrGoalORTargetRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						totRating += strCurrGoalORTargetRating;
						totCount++;
						hmEmpwiseRatingAndCount.put(rs.getString("emp_id")+"_RATING", totRating+"");
						hmEmpwiseRatingAndCount.put(rs.getString("emp_id")+"_COUNT", totCount+"");
					}
				}
				rs.close();
				pst.close();
				
//				System.out.println("hmEmpwiseRatingAndCount ===>> " + hmEmpwiseRatingAndCount);
				
				Map<String, String> hmEmpGKTAnalysis = new HashMap<String, String>();
				
				for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
					double elementAvgScore = 0.0d;
					if(uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_COUNT")) > 0) {
						elementAvgScore = uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_RATING"))/uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_COUNT"));
					}
					hmEmpGKTAnalysis.put(getStrEmpId()[i], elementAvgScore+"");
				}
//				System.out.println("hmEmpGKTAnalysis ===>> " + hmEmpGKTAnalysis);
//				request.setAttribute("hmGKTAnalysisSummary", hmGKTAnalysisSummary);
				
				Map<String, String> hmEmpwiseGKTAnalysis = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gksrd.emp_id from goal_kra_emp_status_rating_details gksrd," +
						" goal_details gd where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+tmpempid.toString()+") and gksrd.goal_id in(select goal_id " +
						"from goal_details where measure_kra = 'Measure') and user_type != '-' group by gksrd.emp_id");
				pst=con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs=pst.executeQuery();
				while (rs.next()) {
					if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
	//						double strCurrGoalORTargetRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
						double elementEmpAvgScore = rs.getDouble("user_rating") / rs.getDouble("cnt");
						hmEmpwiseGKTAnalysis.put(rs.getString("emp_id"), elementEmpAvgScore+"");
					}
				}
				rs.close();
				pst.close();
				
				for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
					double dblTotScore = 0.0d;
					int intTotCount = 0;
//					System.out.println("empID ======>> " + getStrEmpId()[i] + " == dblTotScore ===>> " + dblTotScore);
					if(hmEmpReviewAverage != null && uF.parseToDouble(hmEmpReviewAverage.get(getStrEmpId()[i]))>0) {
						dblTotScore += uF.parseToDouble(hmEmpReviewAverage.get(getStrEmpId()[i]));
						intTotCount++;
//						System.out.println("hmEmpReviewAverage ======>> " + getStrEmpId()[i] + " == dblTotScore ===>> " + dblTotScore + " == intTotCount ====>> " + intTotCount);
					}
					if(hmEmpGKTAnalysis != null && uF.parseToDouble(hmEmpGKTAnalysis.get(getStrEmpId()[i]))>0) {
						dblTotScore += uF.parseToDouble(hmEmpGKTAnalysis.get(getStrEmpId()[i]));
						intTotCount++;
//						System.out.println("hmEmpGKTAnalysis ======>> " + getStrEmpId()[i] + " == dblTotScore ===>> " + dblTotScore + " == intTotCount ====>> " + intTotCount);
					}
					if(hmEmpwiseGKTAnalysis != null && uF.parseToDouble(hmEmpwiseGKTAnalysis.get(getStrEmpId()[i]))>0) {
						dblTotScore += uF.parseToDouble(hmEmpwiseGKTAnalysis.get(getStrEmpId()[i]));
						intTotCount++;
//						System.out.println("hmEmpwiseGKTAnalysis ======>> " + getStrEmpId()[i] + " == dblTotScore ===>> " + dblTotScore + " == intTotCount ====>> " + intTotCount);
					}
//					System.out.println(getStrEmpId()[i] + " == dblTotScore ===>> " + dblTotScore);
					double elementwiseAvgScore = 0.0d;
					if(intTotCount>0) {
						elementwiseAvgScore = dblTotScore / uF.parseToDouble(intTotCount+"");
					}
//					totAverage += elementwiseAvgScore;
					hmEmpListAvg.put(getStrEmpId()[i], elementwiseAvgScore+"");
				}
			}
//			System.out.println("hmEmpListAvg  ::::::::::::::"+hmEmpListAvg);
		} catch (SQLException e) {
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
		return hmEmpListAvg;
	}
	

	private Map<String, String> getEmpOverallReviewRating(Connection con) {
		Map<String, String> hmEmpListAvg = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			StringBuilder tmpempid = new StringBuilder();
			for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
				if(i==0) {
					tmpempid.append(getStrEmpId()[i]);
				} else {
					tmpempid.append(","+getStrEmpId()[i]);
				}
			} 
			if(tmpempid.length() > 0) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select *,((marks*100/weightage)/20) as average from (select sum(marks) as marks, sum(weightage) as weightage," +
					"emp_id from appraisal_details ad, appraisal_question_answer aqa where ad.appraisal_details_id=aqa.appraisal_id " +
					" and emp_id in ("+tmpempid.toString()+") and attempted_on between ? and ? and weightage>0 group by emp_id) as a ");
				pst=con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strStartDate, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
//				System.out.println("pst in getPerformanceReport ===>"+pst);
				rs=pst.executeQuery();
				while(rs.next()) {
					hmEmpListAvg.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("average"))));
				}
				rs.close();
				pst.close();
				
			}
//			System.out.println("hmEmpListAvg  ::::::::::::::"+hmEmpListAvg);
		} catch (SQLException e) {
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
		return hmEmpListAvg;
	}

	public List<List<String>> getAttribsList(Connection con) {
		List<List<String>> attriblist = new ArrayList<List<String>>();
		PreparedStatement pst = null;
		ResultSet rs = null;
//		if(attribids==null) {
//			attribids="";
//		}
//		System.out.println("getAttribParam() :::::::::::::"+getAttribParam());
		try {
			if(getAttribParam() != null && !getAttribParam().equals("") && !getAttribParam().equalsIgnoreCase("null")) {
				pst = con.prepareStatement("select arribute_id,attribute_name from appraisal_attribute where arribute_id in("+ getAttribParam() +") order by attribute_name");
			} else {
				pst = con.prepareStatement("select arribute_id,attribute_name from appraisal_attribute order by attribute_name");
			}
			rs = pst.executeQuery();
			List<String> checkAttribute = new ArrayList<String>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString(1));
				innerList.add(rs.getString(2));
				attriblist.add(innerList);
				checkAttribute.add(rs.getString("arribute_id"));
			}
			rs.close();
			pst.close();
			System.out.println("checkAttribute ::::::::::::::" + checkAttribute);
			request.setAttribute("checkAttribute", checkAttribute);
		} catch (SQLException e) {
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
		return attriblist;
	}

	
	public Map<String, String> getEmpOverallGoalsKRATargetsRating(Connection con) {
		Map<String, String> hmEmpListAvg = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			StringBuilder tmpempid = new StringBuilder();
			for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
				if(i==0) {
					tmpempid.append(getStrEmpId()[i]);
				} else {
					tmpempid.append(","+getStrEmpId()[i]);
				}
			} 
			if(tmpempid.length() > 0) {
				Map<String, String> hmEmpReviewAverage = new HashMap<String, String>();
				pst = con.prepareStatement("select emp_id,sum(marks) as marks, sum(weightage) as weightage from appraisal_question_answer where " +
					" question_id in (select question_bank_id from question_bank where (goal_kra_target_id is not null or kra_id is not null) and " +
					" goal_kra_target_id in (select goal_id from goal_details)) and emp_id in ("+tmpempid.toString()+") and attempted_on between ? and ? group by emp_id");
				pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					double dblAverage = 0;
					if(rs.getDouble("weightage") > 0) {
						dblAverage = ((rs.getDouble("marks")*100) / rs.getDouble("weightage")) / 20;
					}
					hmEmpReviewAverage.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(dblAverage));
				}
				rs.close();
				pst.close();
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select gksrd.*, gd.goal_id,gksrd.emp_id from goal_kra_status_rating_details gksrd, goal_details gd " +
						"where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+tmpempid.toString()+") and gksrd.goal_id in(select goal_id " +
						" from goal_details where measure_kra = '')");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				Map<String, String> hmEmpwiseRatingAndCount = new HashMap<String, String>();
				while (rs.next()) {
					double totRating = uF.parseToDouble(hmEmpwiseRatingAndCount.get(rs.getString("emp_id")+"_RATING"));
					int totCount = uF.parseToInt(hmEmpwiseRatingAndCount.get(rs.getString("emp_id")+"_COUNT"));
					
					if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
						double strCurrGoalORTargetRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						totRating += strCurrGoalORTargetRating;
						totCount++;
						hmEmpwiseRatingAndCount.put(rs.getString("emp_id")+"_RATING", totRating+"");
						hmEmpwiseRatingAndCount.put(rs.getString("emp_id")+"_COUNT", totCount+"");
					}
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmEmpGKTAnalysis = new HashMap<String, String>();
				for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
					double elementAvgScore = 0.0d;
					if(uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_COUNT")) > 0) {
						elementAvgScore = uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_RATING"))/uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_COUNT"));
					}
					hmEmpGKTAnalysis.put(getStrEmpId()[i], elementAvgScore+"");
				}
//				System.out.println("hmEmpGKTAnalysis ===>> " + hmEmpGKTAnalysis);
//				request.setAttribute("hmGKTAnalysisSummary", hmGKTAnalysisSummary);
				
				Map<String, String> hmEmpwiseGKTAnalysis = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gksrd.emp_id from goal_kra_emp_status_rating_details gksrd," +
					" goal_details gd where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+tmpempid.toString()+") and gksrd.goal_id in(select goal_id " +
					"from goal_details) and user_type != '-' ");
				sbQuery.append(" group by gksrd.emp_id");
				pst=con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs=pst.executeQuery();
				while (rs.next()) {
					if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
	//						double strCurrGoalORTargetRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
						double elementEmpAvgScore = rs.getDouble("user_rating") / rs.getDouble("cnt");
						hmEmpwiseGKTAnalysis.put(rs.getString("emp_id"), elementEmpAvgScore+"");
					}
				}
				rs.close();
				pst.close();
				
				for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
					double dblTotScore = 0.0d;
					int intTotCount = 0;
					if(hmEmpReviewAverage != null && uF.parseToDouble(hmEmpReviewAverage.get(getStrEmpId()[i]))>0) {
						dblTotScore += uF.parseToDouble(hmEmpReviewAverage.get(getStrEmpId()[i]));
						intTotCount++;
					}
					if(hmEmpGKTAnalysis != null && uF.parseToDouble(hmEmpGKTAnalysis.get(getStrEmpId()[i]))>0) {
						dblTotScore += uF.parseToDouble(hmEmpGKTAnalysis.get(getStrEmpId()[i]));
						intTotCount++;
					}
					if(hmEmpwiseGKTAnalysis != null && uF.parseToDouble(hmEmpwiseGKTAnalysis.get(getStrEmpId()[i]))>0) {
						dblTotScore += uF.parseToDouble(hmEmpwiseGKTAnalysis.get(getStrEmpId()[i]));
						intTotCount++;
					}
//					System.out.println("dblTotScore ===>> " + dblTotScore);
					double elementwiseAvgScore = 0.0d;
					if(intTotCount>0) {
						elementwiseAvgScore = dblTotScore / uF.parseToDouble(intTotCount+"");
					}
//					totAverage += elementwiseAvgScore;
					hmEmpListAvg.put(getStrEmpId()[i], elementwiseAvgScore+"");
				}
			}
//			System.out.println("hmEmpListAvg  ::::::::::::::"+hmEmpListAvg);
		} catch (SQLException e) {
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
		return hmEmpListAvg;
	}
	
	
	
	public Map<String, String> getEmpOverallGoalsRating(Connection con) {
		Map<String, String> hmEmpListAvg = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			StringBuilder tmpempid = new StringBuilder();
			for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
				if(i==0) {
					tmpempid.append(getStrEmpId()[i]);
				} else {
					tmpempid.append(","+getStrEmpId()[i]);
				}
			} 
			if(tmpempid.length() > 0) {
				Map<String, String> hmEmpReviewAverage = new HashMap<String, String>();
				pst = con.prepareStatement("select emp_id,sum(marks) as marks, sum(weightage) as weightage from appraisal_question_answer where " +
					" question_id in (select question_bank_id from question_bank where goal_kra_target_id is not null and kra_id is null and " +
					" goal_kra_target_id in (select goal_id from goal_details where measure_kra = '')) and emp_id in ("+tmpempid.toString()+") and attempted_on between ? and ? group by emp_id");
				pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					double dblAverage = 0;
					if(rs.getDouble("weightage") > 0) {
						dblAverage = ((rs.getDouble("marks")*100) / rs.getDouble("weightage")) / 20;
					}
					hmEmpReviewAverage.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(dblAverage));
				}
				rs.close();
				pst.close();
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select gksrd.*, gd.goal_id,gksrd.emp_id from goal_kra_status_rating_details gksrd, goal_details gd " +
						"where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+tmpempid.toString()+") and gksrd.goal_id in(select goal_id " +
						" from goal_details where measure_kra = '')");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				Map<String, String> hmEmpwiseRatingAndCount = new HashMap<String, String>();
				while (rs.next()) {
					double totRating = uF.parseToDouble(hmEmpwiseRatingAndCount.get(rs.getString("emp_id")+"_RATING"));
					int totCount = uF.parseToInt(hmEmpwiseRatingAndCount.get(rs.getString("emp_id")+"_COUNT"));
					
					if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
						double strCurrGoalORTargetRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						totRating += strCurrGoalORTargetRating;
						totCount++;
						hmEmpwiseRatingAndCount.put(rs.getString("emp_id")+"_RATING", totRating+"");
						hmEmpwiseRatingAndCount.put(rs.getString("emp_id")+"_COUNT", totCount+"");
					}
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmEmpGKTAnalysis = new HashMap<String, String>();
				for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
					double elementAvgScore = 0.0d;
					if(uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_COUNT")) > 0) {
						elementAvgScore = uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_RATING"))/uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_COUNT"));
					}
					hmEmpGKTAnalysis.put(getStrEmpId()[i], elementAvgScore+"");
				}
//				System.out.println("hmEmpGKTAnalysis ===>> " + hmEmpGKTAnalysis);
//				request.setAttribute("hmGKTAnalysisSummary", hmGKTAnalysisSummary);
				
				Map<String, String> hmEmpwiseGKTAnalysis = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gksrd.emp_id from goal_kra_emp_status_rating_details gksrd," +
					" goal_details gd where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+tmpempid.toString()+") and gksrd.goal_id in(select goal_id from " +
					"goal_details where measure_kra = '') and user_type != '-' ");
				sbQuery.append(" group by gksrd.emp_id");
				pst=con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs=pst.executeQuery();
				while (rs.next()) {
					if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
	//						double strCurrGoalORTargetRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
						double elementEmpAvgScore = rs.getDouble("user_rating") / rs.getDouble("cnt");
						hmEmpwiseGKTAnalysis.put(rs.getString("emp_id"), elementEmpAvgScore+"");
					}
				}
				rs.close();
				pst.close();
				
				for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
					double dblTotScore = 0.0d;
					int intTotCount = 0;
					if(hmEmpReviewAverage != null && uF.parseToDouble(hmEmpReviewAverage.get(getStrEmpId()[i]))>0) {
						dblTotScore += uF.parseToDouble(hmEmpReviewAverage.get(getStrEmpId()[i]));
						intTotCount++;
					}
					if(hmEmpGKTAnalysis != null && uF.parseToDouble(hmEmpGKTAnalysis.get(getStrEmpId()[i]))>0) {
						dblTotScore += uF.parseToDouble(hmEmpGKTAnalysis.get(getStrEmpId()[i]));
						intTotCount++;
					}
					if(hmEmpwiseGKTAnalysis != null && uF.parseToDouble(hmEmpwiseGKTAnalysis.get(getStrEmpId()[i]))>0) {
						dblTotScore += uF.parseToDouble(hmEmpwiseGKTAnalysis.get(getStrEmpId()[i]));
						intTotCount++;
					}
//					System.out.println("dblTotScore ===>> " + dblTotScore);
					double elementwiseAvgScore = 0.0d;
					if(intTotCount>0) {
						elementwiseAvgScore = dblTotScore / uF.parseToDouble(intTotCount+"");
					}
//					totAverage += elementwiseAvgScore;
					hmEmpListAvg.put(getStrEmpId()[i], elementwiseAvgScore+"");
				}
			}
//			System.out.println("hmEmpListAvg  ::::::::::::::"+hmEmpListAvg);
		} catch (SQLException e) {
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
		return hmEmpListAvg;
	}
	
	
	public Map<String, String> getEmpSkillRating(Connection con) {
		Map<String, String> hmEmpSkillsAvg = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			StringBuilder tmpempid = new StringBuilder();
			for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
				if(i==0) {
					tmpempid.append(getStrEmpId()[i]);
				} else {
					tmpempid.append(","+getStrEmpId()[i]);
				}
			} 
			int skillCnt = 0;
			double skillValues = 0.0d;
			if(tmpempid != null && tmpempid.length() > 0) {
				pst = con.prepareStatement("SELECT * FROM skills_description where emp_id in ("+tmpempid.toString()+")");
				rs = pst.executeQuery();
				while (rs.next()) {
					skillValues = uF.parseToDouble(hmEmpSkillsAvg.get(rs.getString("emp_id")+"_VALUE"));
					skillValues += uF.parseToDouble(rs.getString("skills_value"));
					
					skillCnt = uF.parseToInt(hmEmpSkillsAvg.get(rs.getString("emp_id")+"_COUNT"));
					skillCnt++;
					
					double skillAvg = (skillValues / skillCnt) / 2;
					hmEmpSkillsAvg.put(rs.getString("emp_id")+"_VALUE", uF.formatIntoTwoDecimalWithOutComma(skillValues));
					hmEmpSkillsAvg.put(rs.getString("emp_id")+"_AVG", uF.formatIntoTwoDecimalWithOutComma(skillAvg));
					hmEmpSkillsAvg.put(rs.getString("emp_id")+"_COUNT", ""+skillCnt);
				}
				rs.close();
				pst.close();
			}
			
//			System.out.println("hmEmpListAvg  ::::::::::::::"+hmEmpSkillsAvg);
		} catch (SQLException e) {
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
		return hmEmpSkillsAvg;
	}
	
	
	public Map<String, String> getEmpOverallKRARating(Connection con) {
		Map<String, String> hmEmpListAvg = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			StringBuilder tmpempid = new StringBuilder();
			for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
				if(i==0) {
					tmpempid.append(getStrEmpId()[i]);
				} else {
					tmpempid.append(","+getStrEmpId()[i]);
				}
			} 
			if(tmpempid.length() > 0) {
				Map<String, String> hmEmpReviewAverage = new HashMap<String, String>();
				pst = con.prepareStatement("select emp_id,sum(marks) as marks, sum(weightage) as weightage from appraisal_question_answer " +
					" where question_id in (select question_bank_id from question_bank where kra_id is not null and goal_kra_target_id " +
					" in (select goal_id from goal_details where measure_kra = 'KRA')) and emp_id in ("+tmpempid.toString()+") and attempted_on between ? and ? group by emp_id");
				pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
				rs = pst.executeQuery();
				while (rs.next()) {
					double dblAverage = 0;
					if(rs.getDouble("weightage") > 0) {
						dblAverage = ((rs.getDouble("marks")*100) / rs.getDouble("weightage")) / 20;
					}
					hmEmpReviewAverage.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(dblAverage));
				}
				rs.close();
				pst.close();
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select gksrd.*, gd.goal_id,gksrd.emp_id from goal_kra_status_rating_details gksrd, goal_details gd " +
						"where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+tmpempid.toString()+") and gksrd.goal_id in(select goal_id " +
						" from goal_details where measure_kra = 'KRA')");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				Map<String, String> hmEmpwiseRatingAndCount = new HashMap<String, String>();
				while (rs.next()) {
					double totRating = uF.parseToDouble(hmEmpwiseRatingAndCount.get(rs.getString("emp_id")+"_RATING"));
					int totCount = uF.parseToInt(hmEmpwiseRatingAndCount.get(rs.getString("emp_id")+"_COUNT"));
					
					if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
						double strCurrGoalORTargetRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						totRating += strCurrGoalORTargetRating;
						totCount++;
						hmEmpwiseRatingAndCount.put(rs.getString("emp_id")+"_RATING", totRating+"");
						hmEmpwiseRatingAndCount.put(rs.getString("emp_id")+"_COUNT", totCount+"");
					}
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmEmpGKTAnalysis = new HashMap<String, String>();
				for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
					double elementAvgScore = 0.0d;
					if(uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_COUNT")) > 0) {
						elementAvgScore = uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_RATING"))/uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_COUNT"));
					}
					hmEmpGKTAnalysis.put(getStrEmpId()[i], elementAvgScore+"");
				}
//				System.out.println("hmEmpGKTAnalysis ===>> " + hmEmpGKTAnalysis);
//				request.setAttribute("hmGKTAnalysisSummary", hmGKTAnalysisSummary);
				
				Map<String, String> hmEmpwiseGKTAnalysis = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gksrd.emp_id from goal_kra_emp_status_rating_details gksrd," +
					" goal_details gd where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+tmpempid.toString()+") and gksrd.goal_id in(select goal_id " +
					"from goal_details where measure_kra = 'KRA') and user_type != '-' group by gksrd.emp_id");
				pst=con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs=pst.executeQuery();
				while (rs.next()) {
					if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
	//						double strCurrGoalORTargetRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
						double elementEmpAvgScore = rs.getDouble("user_rating") / rs.getDouble("cnt");
						hmEmpwiseGKTAnalysis.put(rs.getString("emp_id"), elementEmpAvgScore+"");
					}
				}
				rs.close();
				pst.close();
				
				for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
					double dblTotScore = 0.0d;
					int intTotCount = 0;
					if(hmEmpReviewAverage != null && uF.parseToDouble(hmEmpReviewAverage.get(getStrEmpId()[i]))>0) {
						dblTotScore += uF.parseToDouble(hmEmpReviewAverage.get(getStrEmpId()[i]));
						intTotCount++;
					}
					if(hmEmpGKTAnalysis != null && uF.parseToDouble(hmEmpGKTAnalysis.get(getStrEmpId()[i]))>0) {
						dblTotScore += uF.parseToDouble(hmEmpGKTAnalysis.get(getStrEmpId()[i]));
						intTotCount++;
					}
					if(hmEmpwiseGKTAnalysis != null && uF.parseToDouble(hmEmpwiseGKTAnalysis.get(getStrEmpId()[i]))>0) {
						dblTotScore += uF.parseToDouble(hmEmpwiseGKTAnalysis.get(getStrEmpId()[i]));
						intTotCount++;
					}
//					System.out.println("dblTotScore ===>> " + dblTotScore);
					double elementwiseAvgScore = 0.0d;
					if(intTotCount>0) {
						elementwiseAvgScore = dblTotScore / uF.parseToDouble(intTotCount+"");
					}
//					totAverage += elementwiseAvgScore;
					hmEmpListAvg.put(getStrEmpId()[i], elementwiseAvgScore+"");
				}
			}
//			System.out.println("hmEmpListAvg  ::::::::::::::"+hmEmpListAvg);
		} catch (SQLException e) {
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
		return hmEmpListAvg;
	}
	
	
	
	public void getEmpPerformanceOverall(Connection con) {
		Map<String, String> hmEmpListOverallAvg = new HashMap<String, String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			StringBuilder tmpempid = new StringBuilder();
			for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
				if(i==0) {
					tmpempid.append(getStrEmpId()[i]);
				} else {
					tmpempid.append(","+getStrEmpId()[i]);
				}
			} 
			if(tmpempid.length() > 0) {
				Map<String, String> hmEmpReviewAverage = new HashMap<String, String>();
				pst = con.prepareStatement("select aqw.emp_id,sum(marks) as marks,sum(weightage) as weightage from appraisal_question_answer aqw " +
					"where aqw.emp_id in ("+tmpempid.toString()+") and attempted_on between ? and ? group by aqw.emp_id");
				pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
				rs = pst.executeQuery();
				while (rs.next()) {
					double dblAverage = 0;
					if(rs.getDouble("weightage") > 0) {
						dblAverage = ((rs.getDouble("marks")*100) / rs.getDouble("weightage")) / 20;
					}
					hmEmpReviewAverage.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(dblAverage));
				}
				rs.close();
				pst.close();
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select gksrd.*, gd.goal_id,gksrd.emp_id from goal_kra_status_rating_details gksrd, goal_details gd " +
						"where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+tmpempid.toString()+")");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				Map<String, String> hmEmpwiseRatingAndCount = new HashMap<String, String>();
				while (rs.next()) {
					double totRating = uF.parseToDouble(hmEmpwiseRatingAndCount.get(rs.getString("emp_id")+"_RATING"));
					int totCount = uF.parseToInt(hmEmpwiseRatingAndCount.get(rs.getString("emp_id")+"_COUNT"));
					
					if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
						double strCurrGoalORTargetRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						totRating += strCurrGoalORTargetRating;
						totCount++;
						hmEmpwiseRatingAndCount.put(rs.getString("emp_id")+"_RATING", totRating+"");
						hmEmpwiseRatingAndCount.put(rs.getString("emp_id")+"_COUNT", totCount+"");
					}
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmEmpGKTAnalysis = new HashMap<String, String>();
				for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
					double elementAvgScore = 0.0d;
					if(uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_COUNT")) > 0) {
						elementAvgScore = uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_RATING"))/uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_COUNT"));
					}
					hmEmpGKTAnalysis.put(getStrEmpId()[i], elementAvgScore+"");
				}
//				System.out.println("hmEmpGKTAnalysis ===>> " + hmEmpGKTAnalysis);
//				request.setAttribute("hmGKTAnalysisSummary", hmGKTAnalysisSummary);
				
				Map<String, String> hmEmpwiseGKTAnalysis = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gksrd.emp_id from goal_kra_emp_status_rating_details gksrd," +
						" goal_details gd where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+tmpempid.toString()+") and user_type != '-' group by gksrd.emp_id");
				pst=con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs=pst.executeQuery();
				while (rs.next()) {
					if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
	//						double strCurrGoalORTargetRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
						double elementEmpAvgScore = rs.getDouble("user_rating") / rs.getDouble("cnt");
						hmEmpwiseGKTAnalysis.put(rs.getString("emp_id"), elementEmpAvgScore+"");
					}
				}
				rs.close();
				pst.close();
				
				for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
					double dblTotScore = 0.0d;
					int intTotCount = 0;
					if(hmEmpReviewAverage != null && uF.parseToDouble(hmEmpReviewAverage.get(getStrEmpId()[i]))>0) {
						dblTotScore += uF.parseToDouble(hmEmpReviewAverage.get(getStrEmpId()[i]));
						intTotCount++;
					}
					if(hmEmpGKTAnalysis != null && uF.parseToDouble(hmEmpGKTAnalysis.get(getStrEmpId()[i]))>0) {
						dblTotScore += uF.parseToDouble(hmEmpGKTAnalysis.get(getStrEmpId()[i]));
						intTotCount++;
					}
					if(hmEmpwiseGKTAnalysis != null && uF.parseToDouble(hmEmpwiseGKTAnalysis.get(getStrEmpId()[i]))>0) {
						dblTotScore += uF.parseToDouble(hmEmpwiseGKTAnalysis.get(getStrEmpId()[i]));
						intTotCount++;
					}
//					System.out.println("dblTotScore ===>> " + dblTotScore);
					double elementwiseAvgScore = 0.0d;
					if(intTotCount>0) {
						elementwiseAvgScore = dblTotScore / uF.parseToDouble(intTotCount+"");
					}
//					totAverage += elementwiseAvgScore;
					hmEmpListOverallAvg.put(getStrEmpId()[i], elementwiseAvgScore+"");
				}
			}
			request.setAttribute("hmEmpListOverallAvg", hmEmpListOverallAvg);
//			System.out.println("hmEmpListOverallAvg :::::::::::::: " + hmEmpListOverallAvg);
		} catch (SQLException e) {
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
	}
	
	
//	public Map<String, String> getEmpPerformanceAttribWise(Connection con) {
//		Map<String, String> hmEmpListAvg = new HashMap<String, String>();
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		UtilityFunctions uF = new UtilityFunctions();
//		try {
//			StringBuilder tmpempid = new StringBuilder();
//			for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
//				if(i==0) {
//					tmpempid.append(getStrEmpId()[i]);
//				} else {
//					tmpempid.append(","+getStrEmpId()[i]);
//				}
//			}
//			if(tmpempid.length() > 0) {
//				Map<String, String> hmEmpReviewAverage = new HashMap<String, String>();
//				pst = con.prepareStatement("select *,((marks*100/weightage)/20) as average from(select aqw.emp_id,aqw.appraisal_attribute,sum(marks) as " +
//					"marks, sum(weightage) as weightage from (select appraisal_element,appraisal_attribute from appraisal_element_attribute group by " +
//					"appraisal_element,appraisal_attribute) as a,appraisal_question_answer aqw where a.appraisal_attribute=aqw.appraisal_attribute " +
//					"and aqw.appraisal_attribute in ("+getAttribParam()+") and aqw.emp_id in ("+tmpempid.toString()+") " +
//					"and aqw.attempted_on between ? and ? and weightage>0 group by aqw.emp_id,aqw.appraisal_attribute ) as aa");
//				pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
//				pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
//				System.out.println("pst ===>> " + pst);
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					hmEmpReviewAverage.put(rs.getString("emp_id")+"_"+rs.getString("appraisal_attribute"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("average"))));
//				}
//				rs.close();
//				pst.close();
//				
//				StringBuilder sbQuery = new StringBuilder();
//				sbQuery.append("select gksrd.*, gd.goal_id,gksrd.emp_id,goal_attribute from goal_kra_status_rating_details gksrd, goal_details gd " +
//					"where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+tmpempid.toString()+") and goal_attribute in("+getAttribParam()+")");
//				pst = con.prepareStatement(sbQuery.toString());
////				System.out.println("pst ===>> " + pst);
//				rs = pst.executeQuery();
//				Map<String, String> hmEmpwiseRatingAndCount = new HashMap<String, String>();
//				while (rs.next()) {
//					double totRating = uF.parseToDouble(hmEmpwiseRatingAndCount.get(rs.getString("emp_id")+"_"+rs.getString("goal_attribute")+"_RATING"));
//					int totCount = uF.parseToInt(hmEmpwiseRatingAndCount.get(rs.getString("emp_id")+"_"+rs.getString("goal_attribute")+"_COUNT"));
//					
//					if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
//						double strCurrGoalORTargetRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
//						if(rs.getString("manager_rating") == null) {
//							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("hr_rating"));
//						} else if(rs.getString("hr_rating") == null) {
//							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("manager_rating"));
//						}
//						totRating += strCurrGoalORTargetRating;
//						totCount++;
//						hmEmpwiseRatingAndCount.put(rs.getString("emp_id")+"_"+rs.getString("goal_attribute")+"_RATING", totRating+"");
//						hmEmpwiseRatingAndCount.put(rs.getString("emp_id")+"_"+rs.getString("goal_attribute")+"_COUNT", totCount+"");
//					}
//				}
//				rs.close();
//				pst.close();
//				
//				List<String> alAttribList = Arrays.asList(getAttribParam().split(","));
//				Map<String, String> hmEmpGKTAnalysis = new HashMap<String, String>();
//				
//				for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
//					for(int j=0; alAttribList!=null && j<alAttribList.size(); j++) {
//						double elementAvgScore = 0.0d;
//						if(uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()+"_COUNT")) > 0) {
//							elementAvgScore = uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()+"_RATING"))/uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()+"_COUNT"));
//						}
//						hmEmpGKTAnalysis.put(getStrEmpId()[i]+"_"+alAttribList.get(j).trim(), elementAvgScore+"");
//					}
//				}
////				System.out.println("hmEmpGKTAnalysis ===>> " + hmEmpGKTAnalysis);
////				request.setAttribute("hmGKTAnalysisSummary", hmGKTAnalysisSummary);
//				
//				Map<String, String> hmEmpwiseGKTAnalysis = new HashMap<String, String>();
//				sbQuery = new StringBuilder();
//				sbQuery.append("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gksrd.emp_id,goal_attribute from goal_kra_emp_status_rating_details gksrd," +
//						" goal_details gd where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+tmpempid.toString()+") and goal_attribute in("+getAttribParam()+")" +
//						" group by gksrd.emp_id,goal_attribute");
//				pst=con.prepareStatement(sbQuery.toString());
////				System.out.println("pst ===>> " + pst);
//				rs=pst.executeQuery();
//				while (rs.next()) {
//					if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
//	//						double strCurrGoalORTargetRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
//						double elementEmpAvgScore = rs.getDouble("user_rating") / rs.getDouble("cnt");
//						hmEmpwiseGKTAnalysis.put(rs.getString("emp_id")+"_"+rs.getString("goal_attribute"), elementEmpAvgScore+"");
//					}
//				}
//				rs.close();
//				pst.close();
//				
//				for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
//					for(int j=0; alAttribList!=null && j<alAttribList.size(); j++) {
//						double dblTotScore = 0.0d;
//						int intTotCount = 0;
//						if(hmEmpReviewAverage != null && uF.parseToDouble(hmEmpReviewAverage.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()))>0) {
//							dblTotScore += uF.parseToDouble(hmEmpReviewAverage.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()));
//							intTotCount++;
//						}
//						if(hmEmpGKTAnalysis != null && uF.parseToDouble(hmEmpGKTAnalysis.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()))>0) {
//							dblTotScore += uF.parseToDouble(hmEmpGKTAnalysis.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()));
//							intTotCount++;
//						}
//						if(hmEmpwiseGKTAnalysis != null && uF.parseToDouble(hmEmpwiseGKTAnalysis.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()))>0) {
//							dblTotScore += uF.parseToDouble(hmEmpwiseGKTAnalysis.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()));
//							intTotCount++;
//						}
//						double elementwiseAvgScore = 0.0d;
//						if(intTotCount>0) {
//							elementwiseAvgScore = dblTotScore / uF.parseToDouble(intTotCount+"");
//						}
//	//					totAverage += elementwiseAvgScore;
//						hmEmpListAvg.put(getStrEmpId()[i]+"_"+alAttribList.get(j).trim(), elementwiseAvgScore+"");
//					}
//				}
//			}
////			System.out.println("hmEmpListAvg :::::::::::::: " + hmEmpListAvg);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			if(rs !=null) {
//				try {
//					rs.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//			if(pst !=null) {
//				try {
//					pst.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		return hmEmpListAvg;
//	}
	
	
	public void getAttributeList1() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select a.appraisal_element,a.appraisal_attribute,aa.attribute_name from " +
					" (select appraisal_element,appraisal_attribute from appraisal_element_attribute " +
					" group by appraisal_element,appraisal_attribute order by appraisal_element) as a, appraisal_attribute aa " +
					" where a.appraisal_attribute=aa.arribute_id");
			rs = pst.executeQuery();
			Map<String,List<List<String>>> hmElementAttribute=new HashMap<String, List<List<String>>>();
			while (rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("appraisal_attribute"));
				innerList.add(rs.getString("attribute_name"));
				List<List<String>> attributeouterList=hmElementAttribute.get(rs.getString("appraisal_element"));
				if(attributeouterList==null) attributeouterList=new ArrayList<List<String>>();
				attributeouterList.add(innerList);
				hmElementAttribute.put(rs.getString("appraisal_element"), attributeouterList);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmElementAttribute", hmElementAttribute);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	public void getElementList() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from appraisal_element order by appraisal_element_id");
			rs = pst.executeQuery();
			List<List<String>> elementouterList=new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList=new ArrayList<String>();
				innerList.add(rs.getString("appraisal_element_id"));
				innerList.add(rs.getString("appraisal_element_name"));
				elementouterList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("elementouterList",elementouterList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void loadLists(UtilityFunctions uF) {
		
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		periodList = new FillPeriod().fillPeriod(1);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		
		request.setAttribute("levelList", levelList);
		getSelectedFilter(uF);
	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		if((strUserType != null && !strUserType.equals(MANAGER)) || (getDataType() != null && getDataType().equals(strBaseUserType))) {
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
					hmFilter.put("ORGANISATION", "All Organisations");
				}
			} else {
				hmFilter.put("ORGANISATION", "All Organisations");
			}
		}
		
		alFilter.add("PERIOD");
		if(getDateParam()!=null && uF.parseToInt(getDateParam())==1) {
			if(getPeriod()!=null && getPeriod().equalsIgnoreCase("T")) {
				hmFilter.put("PERIOD", "Today");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("Y")) {
				hmFilter.put("PERIOD", "Yesterday");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1W")) {
				hmFilter.put("PERIOD", "Last 1 Week");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1M")) {
				hmFilter.put("PERIOD", "Last 1 Month");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L3M")) {
				hmFilter.put("PERIOD", "Last 3 Months");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L6M")) {
				hmFilter.put("PERIOD", "Last 6 Months");
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L1Y")) {
				hmFilter.put("PERIOD", "Last 1 Year");
			}
		} else if(getDateParam()!=null && uF.parseToInt(getDateParam())==2) {
			hmFilter.put("PERIOD", "From: " + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DATE_FORMAT_STR) + "  To: " + uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DATE_FORMAT_STR));
		}
			
		String selectedFilter= CF.getSelectedFilter2(CF, uF, alFilter, hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getDateParam() {
		return dateParam;
	}

	public void setDateParam(String dateParam) {
		this.dateParam = dateParam;
	}

	public List<FillPeriod> getPeriodList() {
		return periodList;
	}

	public void setPeriodList(List<FillPeriod> periodList) {
		this.periodList = periodList;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
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


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}


	public List<FillServices> getServiceList() {
		return serviceList;
	}


	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}


	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public String[] getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String[] strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String[] getFilterParam() {
		return filterParam;
	}

	public void setFilterParam(String[] filterParam) {
		this.filterParam = filterParam;
	}

	public String getAttribParam() {
		return attribParam;
	}

	public void setAttribParam(String attribParam) {
		this.attribParam = attribParam;
	}

	public String getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String getF_department() {
		return f_department;
	}

	public void setF_department(String f_department) {
		this.f_department = f_department;
	}

	public String getF_service() {
		return f_service;
	}

	public void setF_service(String f_service) {
		this.f_service = f_service;
	}

	public String getF_level() {
		return f_level;
	}

	public void setF_level(String f_level) {
		this.f_level = f_level;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillAttribute> getAttributeList() {
		return attributeList;
	}

	public void setAttributeList(List<FillAttribute> attributeList) {
		this.attributeList = attributeList;
	}

	public Map<String, String> getHmEmpPerformanceAvg() {
		return hmEmpPerformanceAvg;
	}

	public void setHmEmpPerformanceAvg(Map<String, String> hmEmpPerformanceAvg) {
		this.hmEmpPerformanceAvg = hmEmpPerformanceAvg;
	}

	public Map<String, String> getHmEmpKRAAvg() {
		return hmEmpKRAAvg;
	}

	public void setHmEmpKRAAvg(Map<String, String> hmEmpKRAAvg) {
		this.hmEmpKRAAvg = hmEmpKRAAvg;
	}

	public Map<String, String> getHmEmpGoalsAvg() {
		return hmEmpGoalsAvg;
	}

	public void setHmEmpGoalsAvg(Map<String, String> hmEmpGoalsAvg) {
		this.hmEmpGoalsAvg = hmEmpGoalsAvg;
	}

	public Map<String, String> getHmEmpSkillAvg() {
		return hmEmpSkillAvg;
	}

	public void setHmEmpSkillAvg(Map<String, String> hmEmpSkillAvg) {
		this.hmEmpSkillAvg = hmEmpSkillAvg;
	}

	public Map<String, String> getHmEmpReviewAvg() {
		return hmEmpReviewAvg;
	}

	public void setHmEmpReviewAvg(Map<String, String> hmEmpReviewAvg) {
		this.hmEmpReviewAvg = hmEmpReviewAvg;
	}

	public Map<String, String> getHmEmpTargetAvg() {
		return hmEmpTargetAvg;
	}

	public void setHmEmpTargetAvg(Map<String, String> hmEmpTargetAvg) {
		this.hmEmpTargetAvg = hmEmpTargetAvg;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getStrEmpIds1() {
		return strEmpIds1;
	}

	public void setStrEmpIds1(String strEmpIds1) {
		this.strEmpIds1 = strEmpIds1;
	}

	public String getFilterParam1() {
		return filterParam1;
	}

	public void setFilterParam1(String filterParam1) {
		this.filterParam1 = filterParam1;
	}

	public Map<String, String> getHmEmpGoalsKRATargetsAvg() {
		return hmEmpGoalsKRATargetsAvg;
	}

	public void setHmEmpGoalsKRATargetsAvg(Map<String, String> hmEmpGoalsKRATargetsAvg) {
		this.hmEmpGoalsKRATargetsAvg = hmEmpGoalsKRATargetsAvg;
	}

	
}
