package com.konnect.jpms.performance;

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

import com.konnect.jpms.employee.EmployeeComparison;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeComparisonData extends ActionSupport implements ServletRequestAware, IStatements {

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
	
	private String f_strWLocation;
	private String f_department;
	private String f_service;
	private String f_level;
	private String f_org;
	
	private Map<String, String> hmEmpPerformanceAvg;
	private Map<String, String> hmEmpSkillAvg;
	private Map<String, String> hmEmpReviewAvg;
	private Map<String, String> hmEmpKRAAvg;
	private Map<String, String> hmEmpTargetAvg;
	private Map<String, String> hmEmpGoalsAvg;
	private Map<String, String> hmEmpGoalsKRATargetsAvg;
	
	private String level;

	private List<List<String>> attriblist;
	
	private String wLocParam;
	private String deptParam;
	private String levelParam;
	private String checkParam;
	private String dateParam;
	private String []filterParam;
	private String attribParam;
	private String filterParam1;
	
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
		
		String []arrEnabledModules = CF.getArrEnabledModules();
		request.setAttribute("arrEnabledModules", arrEnabledModules);
		
		if(getFilterParam1() != null && !getFilterParam1().equals("") && !getFilterParam1().equalsIgnoreCase("null")) {
			setFilterParam(getFilterParam1().split(","));
		} 
		
		compareEmployees(uF);

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
	

	
	private void compareEmployees(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		PreparedStatement pst1 =null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		List<List<String>> alEmpCompareData = new ArrayList<List<String>>(); 
		List<String> alEmployeeNames = new ArrayList<String>();
		List<String> alGrossComp = new ArrayList<String>();
		List<String> alLoggedHours = new ArrayList<String>();
//		List<String> alProjectHours = new ArrayList<String>();
		
		try {
			
			con = db.makeConnection(con);
			
			StringBuilder sbEmpIds = null;
//			List<String> alEmpId = new ArrayList<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from(Select * from ( Select * from ( Select * from ( Select * from employee_personal_details epd, employee_official_details eod WHERE epd.is_alive = ? and eod.emp_id >0 and epd.emp_per_id=eod.emp_id and approved_flag = ? ");
			if(getLevelParam()!=null && getLevelParam().length()>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+getLevelParam()+") ) ");
            }
            if(getDeptParam()!=null && getDeptParam().length()>0){
                sbQuery.append(" and depart_id in ("+getDeptParam()+") ");
            }
            
            if(getwLocParam()!=null && getwLocParam().length()>0) {
                sbQuery.append(" and wlocation_id in ("+getwLocParam()+") ");
            } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and (supervisor_emp_id = "+uF.parseToInt(strSessionEmpId)+" or eod.emp_id = "+uF.parseToInt(strSessionEmpId)+")");
			}
            sbQuery.append(" order by empcode, emp_status, emp_fname,emp_lname) ast left join state s on ast.emp_state_id = s.state_id ) aco left join country co on aco.emp_country_id = co.country_id ) aud left join user_details ud on aud.emp_id = ud.emp_id) pr LEFT JOIN city cc on pr.emp_city_id=cast(cc.city_id as text)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setBoolean(1, true);
			pst.setBoolean(2, true);
//			System.out.println("pst in workring for GHR HR REC CEO CFO ACC ===>> " + pst);
			rs=pst.executeQuery();
			while(rs.next()) {
//				alEmpId.add(rs.getString("emp_per_id"));
				if(sbEmpIds==null) {
					sbEmpIds = new StringBuilder();
					sbEmpIds.append(rs.getString("emp_per_id"));
				} else {
					sbEmpIds.append(","+rs.getString("emp_per_id"));
				}
			}
			rs.close();
			pst.close();
			if(sbEmpIds==null) {
				sbEmpIds = new StringBuilder();
			}
			setStrEmpId(sbEmpIds.toString().split(","));
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
					String strD1 = "", strD2 = "", nPayCycle = "";
//					System.out.println("getDateParam() ===>> " + getDateParam());
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
					
					
					attriblist = getAttribsList(con);
					
					boolean flagGC = false;
					boolean flagLH = false;
					for(int i=0; getFilterParam()!=null && i<getFilterParam().length; i++) {
						if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("GC")) {
							request.setAttribute("GC", "GC");
							flagGC = true;
						}
						
						if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("LH")) {
							request.setAttribute("LH", "LH");
							flagLH = true;
						}
						
						if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("SKILL")) {
							request.setAttribute("SKILL", "SKILL");
							hmEmpSkillAvg = getEmpSkillRating(con);
							request.setAttribute("hmEmpSkillAvg", hmEmpSkillAvg);
						}
						if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("REVIEW")) {
							request.setAttribute("REVIEW", "REVIEW");
							hmEmpReviewAvg = getEmpOverallReviewRating(con);
							request.setAttribute("hmEmpReviewAvg", hmEmpReviewAvg);
						}
						if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("GOAL_KRA_TARGET")) {
							request.setAttribute("GOAL_KRA_TARGET", "GOAL_KRA_TARGET");
							hmEmpGoalsKRATargetsAvg = getEmpOverallGoalsKRATargetsRating(con);
							request.setAttribute("hmEmpGoalsKRATargetsAvg", hmEmpGoalsKRATargetsAvg);
						}
						
						if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("AT")) {
							request.setAttribute("AT", "AT");
							getEmpPerformanceOverall(con);
							if(getAttribParam()!=null && !getAttribParam().equals("")) {
								hmEmpPerformanceAvg = getEmpPerformanceAttribWise(con);
							}
							request.setAttribute("attriblist", attriblist);
							request.setAttribute("hmEmpPerformanceAvg", hmEmpPerformanceAvg);
						}
					}
					Map<String, String> hmLoggedHours = new HashMap<String, String>();
					pst = con.prepareStatement("SELECT sum(hours_worked) as hours_worked, emp_id FROM attendance_details WHERE in_out = 'OUT' and to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ? group by emp_id");
					pst.setDate(1, uF.getDateFormat(strD1, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strD2, DATE_FORMAT));
					rs = pst.executeQuery();
					while(rs.next()) {
						hmLoggedHours.put(rs.getString("emp_id"), rs.getString("hours_worked"));
					}
					rs.close();
					pst.close();
					
					Map<String, String> hmGrossSalary = CF.getEmpGrossSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "H");
					
					for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
						pst = con.prepareStatement(selectEmp);
						pst.setInt(1, uF.parseToInt(getStrEmpId()[i]));
						rs = pst.executeQuery();
						
						while(rs.next()) {
							
							String strEmpMName = "";
							if(flagMiddleName) {
								if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
									strEmpMName = " "+rs.getString("emp_mname");
								}
							}
							
							alEmployeeNames.add("'"+rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname")+"'");
							List<String> alInner = new ArrayList<String>();
							int emp_id = rs.getInt("emp_per_id");
							alInner.add(rs.getString("emp_image"));
							alInner.add(rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
							alInner.add(rs.getString("empcode"));
							alInner.add(rs.getString("designation_name"));
							
							//Supervisor =>
							String supervisorName = hmEmpName.get(rs.getString("supervisor_emp_id"));
							
							alInner.add(supervisorName);	//3
							alInner.add(rs.getString("wlocation_name"));	//4
							alInner.add(rs.getString("dept_name"));	//5
							alInner.add(uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));	//6
							
							
							double dblLoggedEffort = uF.parseToDouble(hmLoggedHours.get(rs.getString("emp_per_id")));
							double dblHOurlyRate = uF.parseToDouble(hmGrossSalary.get(rs.getString("emp_per_id")));
							
							alInner.add(uF.formatIntoOneDecimal(dblLoggedEffort * dblHOurlyRate));
							alInner.add(uF.formatIntoOneDecimal(dblLoggedEffort));
							if(flagGC) {
								alGrossComp.add(uF.formatIntoOneDecimalWithOutComma(dblLoggedEffort * dblHOurlyRate));
							} else {
								alGrossComp.add("");
							}
							if(flagLH) {
								alLoggedHours.add(uF.formatIntoOneDecimalWithOutComma(dblLoggedEffort));
							} else {
								alLoggedHours.add("");
							}

							alInner.add(rs.getString("emp_per_id"));
							alEmpCompareData.add(alInner);
							
						}
						rs.close();
						pst.close();
					}
					
//					log.debug("alEmpCompareData==>"+alEmpCompareData);
					
					request.setAttribute("alEmpCompareData", alEmpCompareData);
					request.setAttribute("alEmployeeNames", alEmployeeNames);
					request.setAttribute("alGrossComp", alGrossComp);
					request.setAttribute("alLoggedHours", alLoggedHours);
//					request.setAttribute("alProjectHours", alProjectHours);
			
		} catch (Exception e) {
				e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeStatements(pst1);
			db.closeConnection(con);
		}
			
		
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
					" goal_details gd where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+tmpempid.toString()+") and gksrd.goal_id in(select goal_id " +
					"from goal_details where measure_kra = '') and user_type != '-' ");
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
	
	
	public Map<String, String> getEmpPerformanceAttribWise(Connection con) {
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
				pst = con.prepareStatement("select *,((marks*100/weightage)/20) as average from(select aqw.emp_id,aqw.appraisal_attribute,sum(marks) as " +
					"marks, sum(weightage) as weightage from (select appraisal_element,appraisal_attribute from appraisal_element_attribute group by " +
					"appraisal_element,appraisal_attribute) as a,appraisal_question_answer aqw where a.appraisal_attribute=aqw.appraisal_attribute " +
					"and aqw.appraisal_attribute in ("+getAttribParam()+") and aqw.emp_id in ("+tmpempid.toString()+") " +
					"and aqw.attempted_on between ? and ? and weightage>0 group by aqw.emp_id,aqw.appraisal_attribute ) as aa");
				pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					hmEmpReviewAverage.put(rs.getString("emp_id")+"_"+rs.getString("appraisal_attribute"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("average"))));
				}
				rs.close();
				pst.close();
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select gksrd.*, gd.goal_id,gksrd.emp_id,goal_attribute from goal_kra_status_rating_details gksrd, goal_details gd " +
					"where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+tmpempid.toString()+") and goal_attribute in("+getAttribParam()+")");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
				Map<String, String> hmEmpwiseRatingAndCount = new HashMap<String, String>();
				while (rs.next()) {
					double totRating = uF.parseToDouble(hmEmpwiseRatingAndCount.get(rs.getString("emp_id")+"_"+rs.getString("goal_attribute")+"_RATING"));
					int totCount = uF.parseToInt(hmEmpwiseRatingAndCount.get(rs.getString("emp_id")+"_"+rs.getString("goal_attribute")+"_COUNT"));
					
					if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
						double strCurrGoalORTargetRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						totRating += strCurrGoalORTargetRating;
						totCount++;
						hmEmpwiseRatingAndCount.put(rs.getString("emp_id")+"_"+rs.getString("goal_attribute")+"_RATING", totRating+"");
						hmEmpwiseRatingAndCount.put(rs.getString("emp_id")+"_"+rs.getString("goal_attribute")+"_COUNT", totCount+"");
					}
				}
				rs.close();
				pst.close();
				
				List<String> alAttribList = Arrays.asList(getAttribParam().split(","));
				Map<String, String> hmEmpGKTAnalysis = new HashMap<String, String>();
				
				for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
					for(int j=0; alAttribList!=null && j<alAttribList.size(); j++) {
						double elementAvgScore = 0.0d;
						if(uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()+"_COUNT")) > 0) {
							elementAvgScore = uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()+"_RATING"))/uF.parseToDouble(hmEmpwiseRatingAndCount.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()+"_COUNT"));
						}
						hmEmpGKTAnalysis.put(getStrEmpId()[i]+"_"+alAttribList.get(j).trim(), elementAvgScore+"");
					}
				}
//				System.out.println("hmEmpGKTAnalysis ===>> " + hmEmpGKTAnalysis);
//				request.setAttribute("hmGKTAnalysisSummary", hmGKTAnalysisSummary);
				
				Map<String, String> hmEmpwiseGKTAnalysis = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gksrd.emp_id,goal_attribute from goal_kra_emp_status_rating_details gksrd," +
						" goal_details gd where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+tmpempid.toString()+") and goal_attribute in("+getAttribParam()+") and user_type != '-' " +
						" group by gksrd.emp_id,goal_attribute");
				pst=con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===>> " + pst);
				rs=pst.executeQuery();
				while (rs.next()) {
					if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
	//						double strCurrGoalORTargetRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
						double elementEmpAvgScore = rs.getDouble("user_rating") / rs.getDouble("cnt");
						hmEmpwiseGKTAnalysis.put(rs.getString("emp_id")+"_"+rs.getString("goal_attribute"), elementEmpAvgScore+"");
					}
				}
				rs.close();
				pst.close();
				
				for(int i=0; getStrEmpId()!=null && i<getStrEmpId().length; i++) {
					for(int j=0; alAttribList!=null && j<alAttribList.size(); j++) {
						double dblTotScore = 0.0d;
						int intTotCount = 0;
						if(hmEmpReviewAverage != null && uF.parseToDouble(hmEmpReviewAverage.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()))>0) {
							dblTotScore += uF.parseToDouble(hmEmpReviewAverage.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()));
							intTotCount++;
						}
						if(hmEmpGKTAnalysis != null && uF.parseToDouble(hmEmpGKTAnalysis.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()))>0) {
							dblTotScore += uF.parseToDouble(hmEmpGKTAnalysis.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()));
							intTotCount++;
						}
						if(hmEmpwiseGKTAnalysis != null && uF.parseToDouble(hmEmpwiseGKTAnalysis.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()))>0) {
							dblTotScore += uF.parseToDouble(hmEmpwiseGKTAnalysis.get(getStrEmpId()[i]+"_"+alAttribList.get(j).trim()));
							intTotCount++;
						}
						double elementwiseAvgScore = 0.0d;
						if(intTotCount>0) {
							elementwiseAvgScore = dblTotScore / uF.parseToDouble(intTotCount+"");
						}
	//					totAverage += elementwiseAvgScore;
						hmEmpListAvg.put(getStrEmpId()[i]+"_"+alAttribList.get(j).trim(), elementwiseAvgScore+"");
					}
				}
			}
//			System.out.println("hmEmpListAvg :::::::::::::: " + hmEmpListAvg);
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
	
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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

	public String getwLocParam() {
		return wLocParam;
	}

	public void setwLocParam(String wLocParam) {
		this.wLocParam = wLocParam;
	}

	public String getDeptParam() {
		return deptParam;
	}

	public void setDeptParam(String deptParam) {
		this.deptParam = deptParam;
	}

	public String getLevelParam() {
		return levelParam;
	}

	public void setLevelParam(String levelParam) {
		this.levelParam = levelParam;
	}

	public String getCheckParam() {
		return checkParam;
	}

	public void setCheckParam(String checkParam) {
		this.checkParam = checkParam;
	}

	public String[] getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String[] strEmpId) {
		this.strEmpId = strEmpId;
	}

	
}
