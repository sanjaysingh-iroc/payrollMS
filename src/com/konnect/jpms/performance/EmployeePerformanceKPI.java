package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import ChartDirector.AngularMeter;

import com.konnect.jpms.charts.SemiCircleMeter;
import com.konnect.jpms.employee.EmployeePerformance;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeePerformanceKPI extends ActionSupport implements ServletRequestAware, IStatements  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7114035908350012346L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strBaseUserType;
	String strSessionEmpId;
	
	private String submit;
	private String dataType;
	private String filterParam1;
	
	private String f_org;
	
	private String level;

	private String wLocParam;
	private String deptParam;
	private String levelParam;
	private String checkParam;
	private String dateParam;
	
	private String []filterParam;

	private String checkedReview;
	private String checkedGoalKRATarget;
	private String checkedAttribute;
	
	private String period;
	private String strStartDate;
	private String strEndDate;
	private static Logger log = Logger.getLogger(EmployeePerformance.class);
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession(true);
		strUserType = (String)session.getAttribute(USERTYPE);
		strBaseUserType = (String)session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PEmployeePerformance);
		request.setAttribute(TITLE, TEmployeePerformance);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		if(getDataType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setDataType("MYTEAM");
		}
		
		if(getFilterParam1() != null && !getFilterParam1().equals("") && !getFilterParam1().equalsIgnoreCase("null")) {
			setFilterParam(getFilterParam1().split(","));
		}
		
		String []arrEnabledModules = CF.getArrEnabledModules();
		request.setAttribute("arrEnabledModules", arrEnabledModules);
		
		String strD1 = "", strD2 = "";
		for(int i=0; getFilterParam()!=null && i<getFilterParam().length; i++) {
			if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("LH")) {
				request.setAttribute("LH", "LH");
			}
			if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("REVIEW")) {
				request.setAttribute("REVIEW", "REVIEW");
				setCheckedReview("REVIEW");
			}
			if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("GOAL_KRA_TARGET")) {
				request.setAttribute("GOAL_KRA_TARGET", "GOAL_KRA_TARGET");
				setCheckedGoalKRATarget("GOAL_KRA_TARGET");
			}
			if(getFilterParam()[i]!=null && getFilterParam()[i].equalsIgnoreCase("AT")) {
				request.setAttribute("AT", "AT");
				setCheckedAttribute("AT");
			}
		}
		
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
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L2Y")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 730)+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L5Y")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 1825)+"", DBDATE, DATE_FORMAT);
			} else if(getPeriod()!=null && getPeriod().equalsIgnoreCase("L10Y")) {
				strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
				strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 3650)+"", DBDATE, DATE_FORMAT);
			}
		} else if(getDateParam()!=null && uF.parseToInt(getDateParam())==2) {
			strD1 = getStrStartDate();
			strD2 = getStrEndDate();
		} else if(getDateParam()==null) {
			strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
			strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 30)+"", DBDATE, DATE_FORMAT);

			setDateParam("1");
		}
		
		setStrStartDate(strD1);
		setStrEndDate(strD2);
		
		getEmployeeKPI(uF, arrEnabledModules);
		return SUCCESS;
	}
	
	
	private void getPerformanceReport(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
//			System.out.println("emp hmElementAttribute ======>"+hmElementAttribute);
			StringBuilder sbEmpIds = null;
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
            sbQuery.append(" order by empcode, emp_status, emp_fname,emp_lname) ast left join state s on ast.emp_state_id = s.state_id ) aco left join country co on aco.emp_country_id = co.country_id ) aud left join user_details ud on aud.emp_id = ud.emp_id) pr LEFT JOIN city cc on pr.emp_city_id=cast(cc.city_id as text)");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setBoolean(1, true);
			pst.setBoolean(2, true);
//			System.out.println("pst in workring for GHR HR REC CEO CFO ACC ===>> " + pst);
			rs=pst.executeQuery();
			while(rs.next()) {
				if(sbEmpIds ==null) {
					sbEmpIds = new StringBuilder();
					sbEmpIds.append(rs.getString("emp_per_id"));
				} else {
					sbEmpIds.append(","+rs.getString("emp_per_id"));
				}
			}
			rs.close();
			pst.close();

			
			List<String> empList = new ArrayList<String>();
			Map<String,String> hmEmpReviewAnalysis = new HashMap<String,String>();
			if(getCheckedReview() != null && !getCheckedReview().equals("") && sbEmpIds !=null) {
				sbQuery = new StringBuilder();
				sbQuery.append("select *,cast(marks*100/weightage as integer) as average from (select sum(marks) as marks, sum(weightage) as weightage," +
					"emp_id from appraisal_details ad, appraisal_question_answer aqa where ad.appraisal_details_id=aqa.appraisal_id " +
					" and emp_id != "+uF.parseToInt(strSessionEmpId)+" and attempted_on between ? and ? and weightage>0 ");
				if(getCheckedAttribute() != null && !getCheckedAttribute().equals("") && getCheckParam() != null && !getCheckParam().equals("") && !getCheckParam().equalsIgnoreCase("null")) {
					sbQuery.append(" and appraisal_attribute in("+getCheckParam()+") ");
				} else if(getCheckedAttribute() != null && !getCheckedAttribute().equals("") && (getCheckParam() == null || getCheckParam().equals("") || getCheckParam().equalsIgnoreCase("null"))) {
					sbQuery.append(" and appraisal_attribute in(0) ");
				}
				sbQuery.append(" and emp_id in ("+sbEmpIds.toString()+") ");
//				}
				sbQuery.append(" group by emp_id) as a ");
				pst=con.prepareStatement(sbQuery.toString());
				pst.setDate(1, uF.getDateFormat(strStartDate, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
//				System.out.println("pst in getPerformanceReport ===>"+pst);
				rs=pst.executeQuery();
				while(rs.next()) {
					if(!empList.contains(rs.getString("emp_id"))) {
						empList.add(rs.getString("emp_id"));
					}
					hmEmpReviewAnalysis.put(rs.getString("emp_id"), rs.getString("average"));
				}
				rs.close();
				pst.close();
			}
			
			Map<String, String> hmEmpwiseRatingAndCount = new HashMap<String, String>();
			if(sbEmpIds !=null) {
				sbQuery = new StringBuilder();
				sbQuery.append("select gksrd.*, gd.goal_id,gksrd.emp_id from goal_kra_status_rating_details gksrd, goal_details gd " +
						"where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+sbEmpIds.toString()+")  ");
				if(getCheckedAttribute() != null && !getCheckedAttribute().equals("") && getCheckParam() != null && !getCheckParam().equals("") && !getCheckParam().equalsIgnoreCase("null")) {
					sbQuery.append(" and goal_attribute in("+getCheckParam()+") ");
				} else if(getCheckedAttribute() != null && !getCheckedAttribute().equals("") && (getCheckParam() == null || getCheckParam().equals("") || getCheckParam().equalsIgnoreCase("null"))) {
					sbQuery.append(" and goal_attribute in(0) ");
				}
				if(getCheckedGoalKRATarget() != null && !getCheckedGoalKRATarget().equals("")) {
					sbQuery.append(" and gksrd.goal_id in(select goal_id from goal_details) ");
				} else {
					sbQuery.append(" and gksrd.goal_id=0 ");
				}
				pst = con.prepareStatement(sbQuery.toString());
	//				System.out.println("pst ===>> " + pst);
				rs = pst.executeQuery();
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
						
						if(!empList.contains(rs.getString("emp_id"))) {
							empList.add(rs.getString("emp_id"));
						}
					}
				}
				rs.close();
				pst.close();
			}
			
			Map<String, String> hmEmpGKTAnalysis = new HashMap<String, String>();
			for (int i = 0; empList != null && i<empList.size(); i++) {
				double elementAvgScore = 0.0d;
				if(uF.parseToDouble(hmEmpwiseRatingAndCount.get(empList.get(i)+"_COUNT")) > 0) {
					elementAvgScore = (uF.parseToDouble(hmEmpwiseRatingAndCount.get(empList.get(i)+"_RATING"))/uF.parseToDouble(hmEmpwiseRatingAndCount.get(empList.get(i)+"_COUNT"))) * 20;
				}
				hmEmpGKTAnalysis.put(empList.get(i), elementAvgScore+"");
			}
//				System.out.println("hmEmpGKTAnalysis ===>> " + hmEmpGKTAnalysis);
//				request.setAttribute("hmGKTAnalysisSummary", hmGKTAnalysisSummary);
			
			Map<String, String> hmEmpwiseGKTAnalysis = new HashMap<String, String>();
			
			if(sbEmpIds !=null) {
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gksrd.emp_id from goal_kra_emp_status_rating_details gksrd," +
					" goal_details gd where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+sbEmpIds.toString()+") and user_type != '-' ");
				if(getCheckedAttribute() != null && !getCheckedAttribute().equals("") && getCheckParam() != null && !getCheckParam().equals("") && !getCheckParam().equalsIgnoreCase("null")) {
					sbQuery.append(" and goal_attribute in("+getCheckParam()+") ");
				} else if(getCheckedAttribute() != null && !getCheckedAttribute().equals("") && (getCheckParam() == null || getCheckParam().equals("") || getCheckParam().equalsIgnoreCase("null"))) {
					sbQuery.append(" and goal_attribute in(0) ");
				}
				if(getCheckedGoalKRATarget() != null && !getCheckedGoalKRATarget().equals("")) {
					sbQuery.append(" and gksrd.goal_id in(select goal_id from goal_details) ");
				} else {
					sbQuery.append(" and gksrd.goal_id=0 ");
				}
				sbQuery.append(" group by gksrd.emp_id");
				pst=con.prepareStatement(sbQuery.toString());
	//				System.out.println("pst ===>> " + pst);
				rs=pst.executeQuery();
				while (rs.next()) {
					if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
	//						double strCurrGoalORTargetRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
						double elementEmpAvgScore = (rs.getDouble("user_rating") / rs.getDouble("cnt")) * 20;
						hmEmpwiseGKTAnalysis.put(rs.getString("emp_id"), elementEmpAvgScore+"");
						if(!empList.contains(rs.getString("emp_id"))) {
							empList.add(rs.getString("emp_id"));
						}
					}
				}
				rs.close();
				pst.close();
			}
			
//				System.out.println("hmEmpwiseGKTAnalysis ===>> " + hmEmpwiseGKTAnalysis);
//				request.setAttribute("hmEmpwiseGKTAnalysisSummary",hmEmpwiseGKTAnalysisSummary);
			
//				System.out.println("alElementIds ===>> " + alElementIds);
			Map<String, String> hmAnalysisSummaryMap = new HashMap<String, String>();
//				double totAverage = 0.0d;
			for (int i = 0; empList != null && i<empList.size(); i++) {
				double dblTotScore = 0.0d;
				int intTotCount = 0;
				if(hmEmpReviewAnalysis != null && uF.parseToDouble(hmEmpReviewAnalysis.get(empList.get(i)))>0) {
					dblTotScore += uF.parseToDouble(hmEmpReviewAnalysis.get(empList.get(i)));
					intTotCount++;
				}
				if(hmEmpGKTAnalysis != null && uF.parseToDouble(hmEmpGKTAnalysis.get(empList.get(i)))>0) {
					dblTotScore += uF.parseToDouble(hmEmpGKTAnalysis.get(empList.get(i)));
					intTotCount++;
				}
				if(hmEmpwiseGKTAnalysis != null && uF.parseToDouble(hmEmpwiseGKTAnalysis.get(empList.get(i)))>0) {
					dblTotScore += uF.parseToDouble(hmEmpwiseGKTAnalysis.get(empList.get(i)));
					intTotCount++;
				}
//					System.out.println("dblTotScore ===>> " + dblTotScore);
				double elementwiseAvgScore = 0.0d;
				if(intTotCount>0) {
					elementwiseAvgScore = dblTotScore / uF.parseToDouble(intTotCount+"");
				}
//					totAverage += elementwiseAvgScore;
				hmAnalysisSummaryMap.put(empList.get(i), elementwiseAvgScore+"");
			}

//			System.out.println("hmAnalysisSummaryMap ===>> " + hmAnalysisSummaryMap);
			request.setAttribute("hmAnalysisSummaryMap", hmAnalysisSummaryMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void getEmployeeKPI(UtilityFunctions uF, String []arrEnabledModules) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		Connection con = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			
			if(arrEnabledModules!=null && ArrayUtils.contains(arrEnabledModules, IConstants.MODULE_PERFORMANCE_MANAGEMENT+"")>=0) {
				getPerformanceReport(con, uF);
			}
			
			Map<String, String> hmLoggedHours = new HashMap<String, String>();
			pst = con.prepareStatement("SELECT sum(hours_worked) as hours_worked, emp_id FROM attendance_details WHERE in_out = 'OUT' and to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ? group by emp_id");
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmLoggedHours.put(rs.getString("emp_id"), rs.getString("hours_worked"));
			}
			rs.close();
			pst.close();
			
			
			Map<String, String> hmRosterHours = new HashMap<String, String>();
			pst = con.prepareStatement("SELECT sum(actual_hours) as actual_hours, emp_id FROM roster_details WHERE _date between ? and ? group by emp_id");
			pst.setDate(1, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()) {
				hmRosterHours.put(rs.getString("emp_id"), rs.getString("actual_hours"));
			}
			rs.close();
			pst.close();
			
			
			
			List<String> alEmpId = new ArrayList<String>();
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
				alEmpId.add(rs.getString("emp_per_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpDesigMap = CF.getEmpDesigMap(con);
			double []HOURS_DATA  = new double[2];
			String []HOURS_LABEL  = new String[2];
			
			Map<String, AngularMeter> hmKPI = new HashMap<String, AngularMeter>();
//			Map<String, AngularMeter> hmProKPI = new HashMap<String, AngularMeter>();
			Map<String, String> hmAnalysisSummaryMap = (Map<String, String>) request.getAttribute("hmAnalysisSummaryMap");
			Map<String, String> hmKPIData = new HashMap<String, String>();
			Map<String, String> hmAttendanceData = new HashMap<String, String>();
			for (int i = 0; alEmpId!=null && i<alEmpId.size(); i++) {
				if(hmKPI!=null && !hmKPI.containsKey(alEmpId.get(i))) {
					hmAttendanceData.put(alEmpId.get(i)+"_LOG_HR", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmLoggedHours.get(alEmpId.get(i)))));
					hmAttendanceData.put(alEmpId.get(i)+"_ROSTER_HR", uF.formatIntoTwoDecimalWithOutComma((uF.parseToDouble(hmRosterHours.get(alEmpId.get(i))) + (0.25 * uF.parseToDouble(hmRosterHours.get(alEmpId.get(i)))))));
					double attendencePercent = 0.0d; 
					double performancePercent = 0.0d;
					double totAvgPercent = 0.0d;
					if(hmAttendanceData != null && hmAttendanceData.get(alEmpId.get(i)+"_LOG_HR") != null && hmAttendanceData.get(alEmpId.get(i)+"_ROSTER_HR") != null) {
						if(uF.parseToDouble(hmAttendanceData.get(alEmpId.get(i)+"_ROSTER_HR")) > 0) {
							attendencePercent = (uF.parseToDouble(hmAttendanceData.get(alEmpId.get(i)+"_LOG_HR")) / uF.parseToDouble(hmAttendanceData.get(alEmpId.get(i)+"_ROSTER_HR"))) *100;
							totAvgPercent = attendencePercent;
						}
//						System.out.println("alEmpId.get(i) ===>> " + alEmpId.get(i) + " -- attendencePercent ===>> " + attendencePercent);
					}
					if(hmAnalysisSummaryMap != null && hmAnalysisSummaryMap.get(alEmpId.get(i)) != null) {
						performancePercent = uF.parseToDouble(hmAnalysisSummaryMap.get(alEmpId.get(i)));
//						System.out.println("alEmpId.get(i) ===>> " + alEmpId.get(i) + " -- performancePercent ===>> " + performancePercent);
						totAvgPercent = (attendencePercent + performancePercent) / 2;
					}
//					System.out.println("alEmpId.get(i) ===>> " +alEmpId.get(i) + " -- totAvgPercent ===>> "+ totAvgPercent);
					HOURS_DATA  = new double[2];
					HOURS_DATA [0] = totAvgPercent;
					HOURS_DATA [1] = 100.00;
					hmKPI.put(alEmpId.get(i), new SemiCircleMeter().getSemiCircleChart(HOURS_DATA, HOURS_LABEL, "Performance"));
					
					hmKPIData.put(alEmpId.get(i), uF.formatIntoTwoDecimalWithOutComma(totAvgPercent));
				}
			}
				
			request.setAttribute("hmKPI", hmKPI);
//			request.setAttribute("hmProKPI", hmProKPI);
			request.setAttribute("hmEmpName", hmEmpName);
			request.setAttribute("hmEmpDesigMap", hmEmpDesigMap);
			
			request.setAttribute("hmKPIData", hmKPIData);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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
	
	public String[] getFilterParam() {
		return filterParam;
	}
	
	public void setFilterParam(String[] filterParam) {
		this.filterParam = filterParam;
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

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public String getFilterParam1() {
		return filterParam1;
	}

	public void setFilterParam1(String filterParam1) {
		this.filterParam1 = filterParam1;
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

	public String getCheckedReview() {
		return checkedReview;
	}

	public void setCheckedReview(String checkedReview) {
		this.checkedReview = checkedReview;
	}

	public String getCheckedGoalKRATarget() {
		return checkedGoalKRATarget;
	}

	public void setCheckedGoalKRATarget(String checkedGoalKRATarget) {
		this.checkedGoalKRATarget = checkedGoalKRATarget;
	}

	public String getCheckedAttribute() {
		return checkedAttribute;
	}

	public void setCheckedAttribute(String checkedAttribute) {
		this.checkedAttribute = checkedAttribute;
	}
	
}
