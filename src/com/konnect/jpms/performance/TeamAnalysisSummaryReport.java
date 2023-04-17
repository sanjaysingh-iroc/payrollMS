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

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TeamAnalysisSummaryReport extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	String strUserType;
	String strSessionEmpId;
	String strSessionOrgId;
//	String checkOrg;
	private String wLocParam;
	private String deptParam;
	private String levelParam;
	private String checkedReview;
	private String checkedGoal;
	private String checkedKRA;
	private String checkedTarget;
	private String checkParam;
	private String dateParam;
	private String period;
	private String strStartDate;
	private String strEndDate;
	
	private String strBaseUserType;
	private String dataType;
	
	public String execute() throws Exception {
		
				
		request.setAttribute(PAGE, "/jsp/performance/TeamAnalysisSummaryReport.jsp");
		request.setAttribute(TITLE, "Report");
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionOrgId = (String) session.getAttribute(ORGID);
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return SUCCESS;
			
		String strD1 = "", strD2 = "";
		UtilityFunctions uF = new UtilityFunctions();
//		System.out.println("getDateParam() ===>> " + getDateParam());
		
		if(getDataType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setDataType("MYTEAM");
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
		
//		System.out.println("getStrStartDate ===>> "+getStrStartDate() + " -- getStrEndDate ===>> " + getStrEndDate());
		//if(getCheckParam()!=null && !getCheckParam().equals("")) {
			getAnalysisSummary();
			getElementList();
			getEmployeeCount();
		//}
		return SUCCESS;

	}
		
	private void getEmployeeCount() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF=new UtilityFunctions();
		ResultSet rs = null;
		try {
//			System.out.println("CHECK PARAM  =================  > "+getCheckParam());
			con = db.makeConnection(con);
			StringBuilder sbEmpIds = null;
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_per_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				" and is_alive= true and emp_per_id>0 ");
			if(strUserType != null && strUserType.equals(MANAGER) && getDataType() != null && !getDataType().equals(strBaseUserType)) {
				sbQuery.append(" and (supervisor_emp_id= "+ uF.parseToInt(strSessionEmpId) +" or emp_per_id= "+ uF.parseToInt(strSessionEmpId) +") ");
			} else {
				if(getwLocParam()!=null && getwLocParam().length()>0) {
					sbQuery.append(" and wlocation_id in ("+getwLocParam()+") ");
	            } else {
	            	sbQuery.append(" and wlocation_id in (0)");
				}
			}
			if(getDeptParam()!=null && getDeptParam().length()>0) {
				sbQuery.append(" and depart_id in ("+getDeptParam()+") ");
            } else {
            	sbQuery.append(" and depart_id in (0) ");
            }
			if(getLevelParam()!=null && getLevelParam().length()>0) {
				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ("+getLevelParam()+") ) ");
            } else {
            	sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in (0) ) ");
            }
			sbQuery.append(" order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(sbEmpIds == null) {
					sbEmpIds = new StringBuilder();
					sbEmpIds.append(rs.getString("emp_per_id"));
				} else {
					sbEmpIds.append(","+rs.getString("emp_per_id"));
				}
			}
			rs.close();
			pst.close();
			if(sbEmpIds == null) {
				sbEmpIds = new StringBuilder();
			}
			
//			System.out.println("TAS empcnt sbEmpIds ===>> "  +sbEmpIds.toString());
			if(sbEmpIds != null && !sbEmpIds.toString().equals("")) {
				List<String> alEmpIds= new ArrayList<String>();
				if(getCheckedReview() != null && !getCheckedReview().equals("")) {
					sbQuery = new StringBuilder();
					sbQuery.append("select emp_id from appraisal_details ad, appraisal_question_answer aqa where ad.appraisal_details_id=aqa.appraisal_id " +
						" and emp_id != "+uF.parseToInt(strSessionEmpId)+" ");
					if(getCheckParam() != null && !getCheckParam().equals("")) {
						sbQuery.append(" and appraisal_attribute in ("+getCheckParam()+") ");
					} else {
						sbQuery.append(" and appraisal_attribute in (0) ");
					}
					sbQuery.append(" and emp_id in ("+sbEmpIds.toString()+") and attempted_on between ? and ? group by emp_id");
					pst = con.prepareStatement(sbQuery.toString());
		//			pst=con.prepareStatement("select count(*)as count from (select emp_id from appraisal_question_answer" +
		//					" where appraisal_attribute in ("+getCheckParam()+") and attempted_on between ? and ? group by emp_id) as a");
					pst.setDate(1, uF.getDateFormat(strStartDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
//					System.out.println("TAS PST ===========>>> "+pst);
					rs = pst.executeQuery();
					while(rs.next()) {
						if(rs.getInt("emp_id") > 0 && !alEmpIds.contains(rs.getString("emp_id"))) {
							alEmpIds.add(rs.getString("emp_id"));
						}
					}
					rs.close();
					pst.close();
				}
				
				sbQuery = new StringBuilder();
				sbQuery.append("select gksrd.emp_id from goal_kra_status_rating_details gksrd, goal_details gd where " +
					"gksrd.goal_id = gd.goal_id and gksrd.emp_id in ("+sbEmpIds.toString()+") and (manager_rating is not null or hr_rating is not null) ");
				if(getCheckParam() != null && !getCheckParam().equals("")) {
					sbQuery.append("and goal_attribute in ("+getCheckParam()+") ");
				} else {
					sbQuery.append(" and goal_attribute in (0) ");
				}
				if((getCheckedGoal() != null && !getCheckedGoal().equals("")) || (getCheckedKRA() != null && !getCheckedKRA().equals("")) || (getCheckedTarget() != null && !getCheckedTarget().equals(""))) {
					sbQuery.append(" and (");
				}
				if(getCheckedGoal() != null && !getCheckedGoal().equals("")) {
					sbQuery.append("gksrd.goal_id in(select goal_id from goal_details where measure_kra = '') ");
				}
				if(getCheckedGoal() != null && !getCheckedGoal().equals("") && ((getCheckedKRA() != null && !getCheckedKRA().equals("")) || getCheckedTarget() != null && !getCheckedTarget().equals("")) ) {
					sbQuery.append(" or ");
				}
				if(getCheckedKRA() != null && !getCheckedKRA().equals("")) {
					sbQuery.append("gksrd.goal_id in(select goal_id from goal_details where measure_kra = 'KRA') ");
				}
				if(getCheckedKRA() != null && !getCheckedKRA().equals("") && getCheckedTarget() != null && !getCheckedTarget().equals("")) {
					sbQuery.append(" or ");
				}
				if(getCheckedTarget() != null && !getCheckedTarget().equals("")) {
					sbQuery.append("gksrd.goal_id in(select goal_id from goal_details where measure_kra = 'Measure') ");
				}
				if((getCheckedGoal() != null && !getCheckedGoal().equals("")) || (getCheckedKRA() != null && !getCheckedKRA().equals("")) || (getCheckedTarget() != null && !getCheckedTarget().equals(""))) {
					sbQuery.append(" ) ");
				} else {
					sbQuery.append(" and gksrd.goal_id=0 ");
				}
				sbQuery.append(" group by gksrd.emp_id");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("TAS pst ===>> " + pst);
				rs=pst.executeQuery();
				while (rs.next()) {
					if(rs.getInt("emp_id") > 0 && !alEmpIds.contains(rs.getString("emp_id"))) {
						alEmpIds.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select gksrd.emp_id from goal_kra_emp_status_rating_details gksrd, goal_details gd where " +
					"gksrd.goal_id = gd.goal_id and gksrd.emp_id in ("+sbEmpIds.toString()+") and user_type != '-' ");
				if(getCheckParam() != null && !getCheckParam().equals("")) {
					sbQuery.append("and goal_attribute in ("+getCheckParam()+") ");
				} else {
					sbQuery.append(" and goal_attribute in (0) ");
				}
				if((getCheckedGoal() != null && !getCheckedGoal().equals("")) || (getCheckedKRA() != null && !getCheckedKRA().equals("")) || (getCheckedTarget() != null && !getCheckedTarget().equals(""))) {
					sbQuery.append(" and (");
				}
				if(getCheckedGoal() != null && !getCheckedGoal().equals("")) {
					sbQuery.append("gksrd.goal_id in(select goal_id from goal_details where measure_kra = '') ");
				}
				if(getCheckedGoal() != null && !getCheckedGoal().equals("") && ((getCheckedKRA() != null && !getCheckedKRA().equals("")) || getCheckedTarget() != null && !getCheckedTarget().equals("")) ) {
					sbQuery.append(" or ");
				}
				if(getCheckedKRA() != null && !getCheckedKRA().equals("")) {
					sbQuery.append("gksrd.goal_id in(select goal_id from goal_details where measure_kra = 'KRA') ");
				}
				if(getCheckedKRA() != null && !getCheckedKRA().equals("") && getCheckedTarget() != null && !getCheckedTarget().equals("")) {
					sbQuery.append(" or ");
				}
				if(getCheckedTarget() != null && !getCheckedTarget().equals("")) {
					sbQuery.append("gksrd.goal_id in(select goal_id from goal_details where measure_kra = 'Measure') ");
				}
				if((getCheckedGoal() != null && !getCheckedGoal().equals("")) || (getCheckedKRA() != null && !getCheckedKRA().equals("")) || (getCheckedTarget() != null && !getCheckedTarget().equals(""))) {
					sbQuery.append(" ) ");
				} else {
					sbQuery.append(" and gksrd.goal_id=0 ");
				}
				sbQuery.append(" group by gksrd.emp_id");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("TAS pst ===>> " + pst);
				rs=pst.executeQuery();
				while (rs.next()) {
					if(rs.getInt("emp_id") > 0 && !alEmpIds.contains(rs.getString("emp_id"))) {
						alEmpIds.add(rs.getString("emp_id"));
					}
				}
				rs.close();
				pst.close();

//				System.out.println("alEmpIds ===>> " + alEmpIds);
				request.setAttribute("empCount", alEmpIds.size()+"");
			}
//			System.out.println("empCount is =========== >"+ empCount);
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
			request.setAttribute("elementouterList", elementouterList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getAnalysisSummary() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
//		double totAverage=0;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbEmpIds = null;
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select emp_per_id from employee_personal_details epd,employee_official_details eod where epd.emp_per_id = eod.emp_id " +
				" and is_alive= true and emp_per_id>0 ");
			if(strUserType != null && strUserType.equals(MANAGER) && getDataType() != null && !getDataType().equals(strBaseUserType)) {
				sbQuery.append(" and (supervisor_emp_id= "+ uF.parseToInt(strSessionEmpId) +" or emp_per_id= "+ uF.parseToInt(strSessionEmpId) +") ");
			} else {
				if(getwLocParam()!=null && getwLocParam().length()>0) {
					sbQuery.append(" and wlocation_id in ("+getwLocParam()+") ");
	            } else {
	            	sbQuery.append(" and wlocation_id in (0)");
				}
			}
			if(getDeptParam()!=null && getDeptParam().length()>0) {
				sbQuery.append(" and depart_id in ("+getDeptParam()+") ");
            } else {
            	sbQuery.append(" and depart_id in (0) ");
            }
			if(getLevelParam()!=null && getLevelParam().length()>0) {
				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ("+getLevelParam()+") ) ");
            } else {
            	sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in (0) ) ");
            }
			sbQuery.append(" order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("TAS pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(sbEmpIds == null) {
					sbEmpIds = new StringBuilder();
					sbEmpIds.append(rs.getString("emp_per_id"));
				} else {
					sbEmpIds.append(","+rs.getString("emp_per_id"));
				}
			}
			rs.close();
			pst.close();
			if(sbEmpIds == null) {
				sbEmpIds = new StringBuilder();
			}
//			System.out.println("TAS sbEmpIds ===>> "  +sbEmpIds.toString());
			
			if(sbEmpIds != null && !sbEmpIds.toString().equals("")) {
				List<String> alElementIds = new ArrayList<String>();
				Map<String, String> hmAnalysisSummaryMap = new HashMap<String, String>();
				Map<String, String> hmReviewAnalysisSummary = new HashMap<String, String>();
				if(getCheckedReview() != null && !getCheckedReview().equals("")) {
					sbQuery = new StringBuilder();
					sbQuery.append("select *,((marks*100/weightage)) as average from (select sum(marks) as marks, sum(weightage) as weightage,a.appraisal_element" +
						" from (select appraisal_element,appraisal_attribute from appraisal_element_attribute where appraisal_attribute in (select " +
						"aa.arribute_id from appraisal_attribute aa, appraisal_attribute_level aal where aal.attribute_id = aa.arribute_id) group by " +
						"appraisal_element,appraisal_attribute) as a,appraisal_question_answer aqw where aqw.emp_id != "+uF.parseToInt(strSessionEmpId)+" " +
						" and a.appraisal_attribute=aqw.appraisal_attribute ");
					if(getCheckParam() != null && !getCheckParam().equals("")) {
						sbQuery.append(" and aqw.appraisal_attribute in ("+getCheckParam()+") ");
					} else {
						sbQuery.append(" and aqw.appraisal_attribute in (0) ");
					}
					sbQuery.append("and aqw.emp_id in ("+sbEmpIds.toString()+") ");
					sbQuery.append(" and aqw.appraisal_attribute >0 and aqw.attempted_on between ? and ? and weightage>0 group by a.appraisal_element) as aa order by aa.appraisal_element");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setDate(1, uF.getDateFormat(strStartDate, DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
//					System.out.println("TAS PST =========== >" + pst);
					rs = pst.executeQuery();
					while (rs.next()) {
//						totAverage += uF.parseToDouble(rs.getString("average"));
						hmReviewAnalysisSummary.put(rs.getString("appraisal_element"), rs.getString("average"));
						if(rs.getInt("appraisal_element")>0 && !alElementIds.contains(rs.getString("appraisal_element"))) {
							alElementIds.add(rs.getString("appraisal_element"));
						}
					}
					rs.close();
					pst.close();
				}
//				System.out.println("hmReviewAnalysisSummary ===>> " + hmReviewAnalysisSummary);
				
				sbQuery = new StringBuilder();
				sbQuery.append("select gksrd.*, gd.goal_id,gd.goal_element from goal_kra_status_rating_details gksrd, goal_details gd " +
						"where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+sbEmpIds.toString()+") ");
				if(getCheckParam() != null && !getCheckParam().equals("")) {
					sbQuery.append("and goal_attribute in ("+getCheckParam()+") ");
				} else {
					sbQuery.append(" and goal_attribute in (0) ");
				}
				if((getCheckedGoal() != null && !getCheckedGoal().equals("")) || (getCheckedKRA() != null && !getCheckedKRA().equals("")) || (getCheckedTarget() != null && !getCheckedTarget().equals(""))) {
					sbQuery.append(" and (");
				}
				if(getCheckedGoal() != null && !getCheckedGoal().equals("")) {
					sbQuery.append("gksrd.goal_id in(select goal_id from goal_details where measure_kra = '') ");
				}
				if(getCheckedGoal() != null && !getCheckedGoal().equals("") && ((getCheckedKRA() != null && !getCheckedKRA().equals("")) || getCheckedTarget() != null && !getCheckedTarget().equals("")) ) {
					sbQuery.append(" or ");
				}
				if(getCheckedKRA() != null && !getCheckedKRA().equals("")) {
					sbQuery.append("gksrd.goal_id in(select goal_id from goal_details where measure_kra = 'KRA') ");
				}
				if(getCheckedKRA() != null && !getCheckedKRA().equals("") && getCheckedTarget() != null && !getCheckedTarget().equals("")) {
					sbQuery.append(" or ");
				}
				if(getCheckedTarget() != null && !getCheckedTarget().equals("")) {
					sbQuery.append("gksrd.goal_id in(select goal_id from goal_details where measure_kra = 'Measure') ");
				}
				if((getCheckedGoal() != null && !getCheckedGoal().equals("")) || (getCheckedKRA() != null && !getCheckedKRA().equals("")) || (getCheckedTarget() != null && !getCheckedTarget().equals(""))) {
					sbQuery.append(" ) ");
				} else {
					sbQuery.append(" and gksrd.goal_id=0 ");
				}
				pst=con.prepareStatement(sbQuery.toString());
//				System.out.println("TAS pst ===>> " + pst);
				rs=pst.executeQuery();
				Map<String, String> hmElementwiseRatingAndCount = new HashMap<String, String>();
				while (rs.next()) {
					double totRating = uF.parseToDouble(hmElementwiseRatingAndCount.get(rs.getString("goal_element")+"_RATING"));
					int totCount = uF.parseToInt(hmElementwiseRatingAndCount.get(rs.getString("goal_element")+"_COUNT"));
					
					if((rs.getString("manager_rating") != null && !rs.getString("manager_rating").equals("")) || (rs.getString("hr_rating") != null && !rs.getString("hr_rating").equals("")) ) {
						double strCurrGoalORTargetRating = (uF.parseToDouble(rs.getString("manager_rating")) + uF.parseToDouble(rs.getString("hr_rating"))) / 2;
						if(rs.getString("manager_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("hr_rating"));
						} else if(rs.getString("hr_rating") == null) {
							strCurrGoalORTargetRating = uF.parseToDouble(rs.getString("manager_rating"));
						}
						totRating += strCurrGoalORTargetRating;
						totCount++;
						hmElementwiseRatingAndCount.put(rs.getString("goal_element")+"_RATING", totRating+"");
						hmElementwiseRatingAndCount.put(rs.getString("goal_element")+"_COUNT", totCount+"");
					}
					if(rs.getInt("goal_element")>0 && !alElementIds.contains(rs.getString("goal_element"))) {
						alElementIds.add(rs.getString("goal_element"));
					}
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmGKTAnalysisSummary = new HashMap<String, String>();
				for (int i = 0; alElementIds != null && i<alElementIds.size(); i++) {
					double elementAvgScore = 0.0d;
					if(uF.parseToDouble(hmElementwiseRatingAndCount.get(alElementIds.get(i)+"_COUNT")) > 0) {
						elementAvgScore = (uF.parseToDouble(hmElementwiseRatingAndCount.get(alElementIds.get(i)+"_RATING"))/uF.parseToDouble(hmElementwiseRatingAndCount.get(alElementIds.get(i)+"_COUNT"))) * 20;
					}
					hmGKTAnalysisSummary.put(alElementIds.get(i), elementAvgScore+"");
				}
//				System.out.println("TAS hmGKTAnalysisSummary ===>> " + hmGKTAnalysisSummary);
//				request.setAttribute("hmGKTAnalysisSummary", hmGKTAnalysisSummary);
				
				Map<String, String> hmEmpwiseGKTAnalysisSummary = new HashMap<String, String>();
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gd.goal_element from goal_kra_emp_status_rating_details gksrd," +
						" goal_details gd where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+sbEmpIds.toString()+") and user_type != '-' ");
				if(getCheckParam() != null && !getCheckParam().equals("")) {
					sbQuery.append("and goal_attribute in ("+getCheckParam()+") ");
				} else {
					sbQuery.append(" and goal_attribute in (0) ");
				}
				if((getCheckedGoal() != null && !getCheckedGoal().equals("")) || (getCheckedKRA() != null && !getCheckedKRA().equals("")) || (getCheckedTarget() != null && !getCheckedTarget().equals(""))) {
					sbQuery.append(" and (");
				}
				if(getCheckedGoal() != null && !getCheckedGoal().equals("")) {
					sbQuery.append("gksrd.goal_id in(select goal_id from goal_details where measure_kra = '') ");
				}
				if(getCheckedGoal() != null && !getCheckedGoal().equals("") && ((getCheckedKRA() != null && !getCheckedKRA().equals("")) || getCheckedTarget() != null && !getCheckedTarget().equals("")) ) {
					sbQuery.append(" or ");
				}
				if(getCheckedKRA() != null && !getCheckedKRA().equals("")) {
					sbQuery.append("gksrd.goal_id in(select goal_id from goal_details where measure_kra = 'KRA') ");
				}
				if(getCheckedKRA() != null && !getCheckedKRA().equals("") && getCheckedTarget() != null && !getCheckedTarget().equals("")) {
					sbQuery.append(" or ");
				}
				if(getCheckedTarget() != null && !getCheckedTarget().equals("")) {
					sbQuery.append("gksrd.goal_id in(select goal_id from goal_details where measure_kra = 'Measure') ");
				}
				if((getCheckedGoal() != null && !getCheckedGoal().equals("")) || (getCheckedKRA() != null && !getCheckedKRA().equals("")) || (getCheckedTarget() != null && !getCheckedTarget().equals(""))) {
					sbQuery.append(" ) ");
				} else {
					sbQuery.append(" and gksrd.goal_id=0 ");
				}
				sbQuery.append(" group by gd.goal_element");
				pst=con.prepareStatement(sbQuery.toString());
//				System.out.println("TAS pst ===>> " + pst);
				rs=pst.executeQuery();
				while (rs.next()) {
					if((rs.getString("user_rating") != null && !rs.getString("user_rating").equals("")) ) {
	//						double strCurrGoalORTargetRating = rs.getDouble("user_rating")/ rs.getInt("cnt");
						double elementEmpAvgScore = (rs.getDouble("user_rating") / rs.getDouble("cnt")) * 20;
						hmEmpwiseGKTAnalysisSummary.put(rs.getString("goal_element"), elementEmpAvgScore+"");
						if(rs.getInt("goal_element")>0 && !alElementIds.contains(rs.getString("goal_element"))) {
							alElementIds.add(rs.getString("goal_element"));
						}
					}
				}
				rs.close();
				pst.close();
//				System.out.println("TAS hmEmpwiseGKTAnalysisSummary ===>> " + hmEmpwiseGKTAnalysisSummary);
//				request.setAttribute("hmEmpwiseGKTAnalysisSummary",hmEmpwiseGKTAnalysisSummary);
				
//				System.out.println("TAS alElementIds ===>> " + alElementIds);
				double totAverage = 0.0d;
				for (int i = 0; alElementIds != null && i<alElementIds.size(); i++) {
					double dblTotScore = 0.0d;
					int intTotCount = 0;
					if(hmReviewAnalysisSummary != null && uF.parseToDouble(hmReviewAnalysisSummary.get(alElementIds.get(i)))>0) {
						dblTotScore += uF.parseToDouble(hmReviewAnalysisSummary.get(alElementIds.get(i)));
						intTotCount++;
					}
					if(hmGKTAnalysisSummary != null && uF.parseToDouble(hmGKTAnalysisSummary.get(alElementIds.get(i)))>0) {
						dblTotScore += uF.parseToDouble(hmGKTAnalysisSummary.get(alElementIds.get(i)));
						intTotCount++;
					}
					if(hmEmpwiseGKTAnalysisSummary != null && uF.parseToDouble(hmEmpwiseGKTAnalysisSummary.get(alElementIds.get(i)))>0) {
						dblTotScore += uF.parseToDouble(hmEmpwiseGKTAnalysisSummary.get(alElementIds.get(i)));
						intTotCount++;
					}
//					System.out.println("dblTotScore ===>> " + dblTotScore);
					double elementwiseAvgScore = 0.0d;
					if(intTotCount>0) {
						elementwiseAvgScore = dblTotScore / uF.parseToDouble(intTotCount+"");
					}
					totAverage += elementwiseAvgScore;
					hmAnalysisSummaryMap.put(alElementIds.get(i), elementwiseAvgScore+"");
				}
				request.setAttribute("hmAnalysisSummaryMap", hmAnalysisSummaryMap);
				request.setAttribute("totAverage", totAverage+"");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public String getCheckParam() {
		return checkParam;
	}

	public void setCheckParam(String checkParam) {
		this.checkParam = checkParam;
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

	public String getCheckedReview() {
		return checkedReview;
	}

	public void setCheckedReview(String checkedReview) {
		this.checkedReview = checkedReview;
	}

	public String getCheckedGoal() {
		return checkedGoal;
	}

	public void setCheckedGoal(String checkedGoal) {
		this.checkedGoal = checkedGoal;
	}

	public String getCheckedKRA() {
		return checkedKRA;
	}

	public void setCheckedKRA(String checkedKRA) {
		this.checkedKRA = checkedKRA;
	}

	public String getCheckedTarget() {
		return checkedTarget;
	}

	public void setCheckedTarget(String checkedTarget) {
		this.checkedTarget = checkedTarget;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	

}
