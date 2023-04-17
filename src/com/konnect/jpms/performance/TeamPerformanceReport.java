package com.konnect.jpms.performance;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class TeamPerformanceReport  extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	String strUserType;
	String strSessionEmpId;
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
				
		request.setAttribute(PAGE, "/jsp/performance/TeamPerformanceReport.jsp");
		request.setAttribute(TITLE, "Report");
		session = request.getSession();
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return SUCCESS;
		
		/*System.out.println("checkedReview ======>"+getCheckedReview());
		System.out.println("checkedGoal======>"+getCheckedGoal());
		System.out.println("checkedKRA======>"+getCheckedKRA());
		System.out.println("checkedTarget======>"+getCheckedTarget());*/
		if(getDataType()==null && strUserType != null && strUserType.equals(MANAGER)) {
			setDataType("MYTEAM");
		}
		
		String strD1 = "", strD2 = "";
		UtilityFunctions uF = new UtilityFunctions();
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
//			System.out.println("dateParam getDateParam()!=null======>"+getDateParam());
//			System.out.println("strStartDate getDateParam()!=null======>"+getStrStartDate());
//			System.out.println("strEndDate getDateParam()!=null======>"+getStrEndDate());
		} else if(getDateParam()==null) {
			strD2 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 0)+"", DBDATE, DATE_FORMAT);
			strD1 = uF.getDateFormat(uF.getPrevDate(CF.getStrTimeZone(), 30)+"", DBDATE, DATE_FORMAT);

			setDateParam("2");
//			System.out.println("dateParam getDateParam()==null======>"+getDateParam());
//			System.out.println("strStartDate getDateParam()==null======>"+getStrStartDate());
//			System.out.println("strEndDate getDateParam()==null======>"+getStrEndDate());
		}
		setStrStartDate(strD1);
		setStrEndDate(strD2);
		
//		System.out.println("TPR checkParam======>"+getCheckParam());
//		if(getCheckParam()!=null && !getCheckParam().equals("")) {
			getPerformanceReport();
//		}
		return SUCCESS;

	}
		
	
	private void getPerformanceReport() {
		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF=new UtilityFunctions();
		
		
		try {
			con = db.makeConnection(con);
			Map<String,String> hmEmpName=CF.getEmpNameMap(con, null,null);	
			 
			/*pst = con.prepareStatement("select appraisal_element,appraisal_attribute from appraisal_element_attribute " +
			"group by appraisal_element,appraisal_attribute order by appraisal_element");*/
			
			Map<String,List<List<String>>> hmElementAttribute=new LinkedHashMap<String, List<List<String>>>();
			
			if(getCheckParam().length()>0) {
				pst = con.prepareStatement("select appraisal_element,appraisal_attribute from appraisal_element_attribute " +
						" where appraisal_attribute in ("+getCheckParam()+") group by appraisal_element,appraisal_attribute order by appraisal_element");
	//			System.out.println("TPR pst =====> " + pst);
				rs=pst.executeQuery();
				while(rs.next()) {
					
					List<List<String>> outerList = hmElementAttribute.get(rs.getString("appraisal_element"));
					if(outerList==null) outerList = new ArrayList<List<String>>();
					
					List<String> innerList=new ArrayList<String>();
					innerList.add(rs.getString("appraisal_element"));
					innerList.add(rs.getString("appraisal_attribute"));
					
					outerList.add(innerList);
					
					hmElementAttribute.put(rs.getString("appraisal_element"), outerList);
					
				}
				rs.close();
				pst.close();
			}
//			System.out.println("emp hmElementAttribute ======>"+hmElementAttribute);
			
			//List<List<String>> outerList=(List<List<String>>)request.getAttribute("elementouterList");
			//List<String> checkboxList=new ArrayList<String>();

//			List<String> empIdList = new ArrayList<String>();
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
			// Created By Dattatray Date : 28-June-21 Note : Start Date and End date wise checked query
			if (getStrStartDate() !=null && getStrStartDate().length() > 0 && getStrEndDate() !=null && getStrEndDate().length() > 0) {
				sbQuery.append(" and joining_date >='"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' AND joining_date < '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"' ");
			}
			sbQuery.append(" order by emp_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("TPR pst ===>> " + pst);
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
			
//			StringBuilder sbEmpID = null;
//			if(strUserType != null && strUserType.equals(MANAGER)) {
//				pst = con.prepareStatement("select emp_per_id from employee_personal_details epd,employee_official_details eod where " +
//					"epd.emp_per_id = eod.emp_id and is_alive= true and emp_per_id>0 and (supervisor_emp_id=? or emp_per_id=?) order by emp_id");
//				pst.setInt(1, uF.parseToInt(strSessionEmpId));
//				pst.setInt(2, uF.parseToInt(strSessionEmpId));
//				rs = pst.executeQuery();
//				while(rs.next()) {
//					if(sbEmpID == null) {
//						sbEmpID = new StringBuilder();
//						sbEmpID.append(rs.getString("emp_per_id"));
//					} else {
//						sbEmpID.append(","+rs.getString("emp_per_id"));
//					}
////					empIdList.add(rs.getString("emp_per_id"));
//				}
//				rs.close();
//				pst.close();
//				if(sbEmpID == null) {
//					sbEmpID = new StringBuilder();
//				}
//			}
			
			Map<String, String> checkboxList = new LinkedHashMap<String, String>();
			Map<String,List<String>> checkselect = new LinkedHashMap<String,List<String>>();
			Iterator<String> it = hmElementAttribute.keySet().iterator();
			while(it.hasNext()) {
				String appraisal_element = it.next();
				List<List<String>> outerList = hmElementAttribute.get(appraisal_element);
				
				StringBuilder sb = new StringBuilder();
				for(int i=0; outerList!=null && i<outerList.size(); i++) {
					List<String> innerList = outerList.get(i);
					//List<String> val=Arrays.asList(request.getParameterValues("check"+innerList.get(0)));  
//					List<String> val=new ArrayList<String>();
					if(i==0) {
						sb.append(innerList.get(1));
					} else {
						sb.append(","+innerList.get(1));
					}
				}
//				checkselect.put(appraisal_element, val);
				checkboxList.put(appraisal_element, sb.toString());
			}
//			System.out.println("checkboxList ========>"+checkboxList);
//			request.setAttribute("checksel?ect",checkselect);
			
			List<Map<String,String>> allData=new ArrayList<Map<String,String>>(2);
			List<String> empList=new ArrayList<String>();
			if(sbEmpIds != null && !sbEmpIds.toString().equals("")) {
				Iterator<String> it1=checkboxList.keySet().iterator();
				while(it1.hasNext()) {
					String appraisal_element=it1.next();
	//			for(int i=0;i<checkboxList.size();i++) {
					String check = checkboxList.get(appraisal_element);
//					System.out.println("CHECK  =================  > "+check);
					Map<String,String> hmEmpReviewAnalysis=new HashMap<String,String>();
					if(getCheckedReview() != null && !getCheckedReview().equals("")) {
						sbQuery = new StringBuilder();
						sbQuery.append("select *,cast(marks*100/weightage as integer) as average from (select sum(marks) as marks, sum(weightage) as weightage," +
							"emp_id from appraisal_details ad, appraisal_question_answer aqa where ad.appraisal_details_id=aqa.appraisal_id " +
							" and emp_id != "+uF.parseToInt(strSessionEmpId)+" and appraisal_attribute in("+check+") and attempted_on between ? and ? and weightage>0 ");
		//				if(strUserType != null && strUserType.equals(MANAGER) && sbEmpID != null && !sbEmpID.toString().equals("")) {
						sbQuery.append(" and emp_id in ("+sbEmpIds.toString()+") ");
		//				}
						sbQuery.append(" group by emp_id) as a ");
						pst=con.prepareStatement(sbQuery.toString());
						pst.setDate(1, uF.getDateFormat(strStartDate, DATE_FORMAT));
						pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
//						System.out.println("TPR pst ===>"+pst);
						rs=pst.executeQuery();
						while(rs.next()) {
							if(!empList.contains(rs.getString("emp_id"))) {
								empList.add(rs.getString("emp_id"));
							}
							hmEmpReviewAnalysis.put(rs.getString("emp_id"), rs.getString("average"));
						}
						rs.close();
						pst.close();
	//					allData.add(empMap);
					}
//					System.out.println("hmEmpReviewAnalysis ===>> " + hmEmpReviewAnalysis);
					
					sbQuery = new StringBuilder();
					sbQuery.append("select gksrd.*, gd.goal_id,gksrd.emp_id from goal_kra_status_rating_details gksrd, goal_details gd " +
							"where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+sbEmpIds.toString()+") and goal_attribute in("+check+") ");
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
					pst = con.prepareStatement(sbQuery.toString());
//					System.out.println("TPR pst ===>> " + pst);
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
							
							if(!empList.contains(rs.getString("emp_id"))) {
								empList.add(rs.getString("emp_id"));
							}
						}
					}
					rs.close();
					pst.close();
					
					Map<String, String> hmEmpGKTAnalysis = new HashMap<String, String>();
					double elementAvgScore = 0.0d;
					for (int i = 0; empList != null && i<empList.size(); i++) {
						if(uF.parseToDouble(hmEmpwiseRatingAndCount.get(empList.get(i)+"_COUNT")) > 0) {
							elementAvgScore = (uF.parseToDouble(hmEmpwiseRatingAndCount.get(empList.get(i)+"_RATING"))/uF.parseToDouble(hmEmpwiseRatingAndCount.get(empList.get(i)+"_COUNT"))) * 20;
						}
						hmEmpGKTAnalysis.put(empList.get(i), elementAvgScore+"");
					}
//					System.out.println("hmEmpGKTAnalysis ===>> " + hmEmpGKTAnalysis);
	//				request.setAttribute("hmGKTAnalysisSummary", hmGKTAnalysisSummary);
					
					Map<String, String> hmEmpwiseGKTAnalysis = new HashMap<String, String>();
					sbQuery = new StringBuilder();
					sbQuery.append("select sum(gksrd.user_rating) as user_rating, count(*) as cnt, gksrd.emp_id from goal_kra_emp_status_rating_details gksrd," +
							" goal_details gd where gksrd.goal_id=gd.goal_id and gksrd.emp_id in ("+sbEmpIds.toString()+") and goal_attribute in("+check+") and user_type != '-' ");
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
					pst=con.prepareStatement(sbQuery.toString());
//					System.out.println("TPR pst ===>> " + pst);
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
//					System.out.println("hmEmpwiseGKTAnalysis ===>> " + hmEmpwiseGKTAnalysis);
	//				request.setAttribute("hmEmpwiseGKTAnalysisSummary",hmEmpwiseGKTAnalysisSummary);
					
//					System.out.println("alElementIds ===>> " + alElementIds);
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
//					System.out.println("hmAnalysisSummaryMap =======>> " + hmAnalysisSummaryMap);
	//				request.setAttribute("hmAnalysisSummaryMap", hmAnalysisSummaryMap);
	//				request.setAttribute("totAverage", totAverage+"");
					allData.add(hmAnalysisSummaryMap);
				}
			}
			
//			System.out.println("empList===>"+empList.toString());
			pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
			rs=pst.executeQuery();
			Map<String,String> empImageMap = new HashMap<String,String>();
			while(rs.next()) {
				empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
			}
			rs.close();
			pst.close();
			
			if(allData != null && !allData.isEmpty()) {
				Map<String,String> potentialMap=allData.get(0);
				Map<String,String> performanceMap = new HashMap<String, String>();
				if(allData.size() > 1) {
					performanceMap = allData.get(1);
				}
				List<String> list1=new ArrayList<String>();
				List<String> list2=new ArrayList<String>();
				List<String> list3=new ArrayList<String>();
				List<String> list4=new ArrayList<String>();
				List<String> list5=new ArrayList<String>();
				List<String> list6=new ArrayList<String>();
				List<String> list7=new ArrayList<String>();
				List<String> list8=new ArrayList<String>();
				List<String> list9=new ArrayList<String>();
				
				StringBuilder builder1 = new StringBuilder();
				StringBuilder builder2 = new StringBuilder();
				StringBuilder builder3 = new StringBuilder();
				StringBuilder builder4 = new StringBuilder();
				StringBuilder builder5 = new StringBuilder();
				StringBuilder builder6 = new StringBuilder();
				StringBuilder builder7 = new StringBuilder();
				StringBuilder builder8 = new StringBuilder();
				StringBuilder builder9 = new StringBuilder();
				
				for(int i=0; i<empList.size(); i++) {
					double potential = uF.parseToDouble(potentialMap.get(empList.get(i)));
					double performance = uF.parseToDouble(performanceMap.get(empList.get(i)));
	//				System.out.println("potential=========>"+potential);
	//				System.out.println("performance====>"+performance);
					String empimg = uF.showData(empImageMap.get(empList.get(i).trim()), "");
					String strImage = "";
					if(CF.getStrDocRetriveLocation()==null) { 
						strImage = "<img class=\"img-circle lazy\" border=\"0\" height=\"24\" width=\"24\" src=\""+DOCUMENT_LOCATION + empimg+"\" />";
				  	} else if(!empimg.equals("") && !empimg.equals("avatar_photo.png") && empimg!=null) {
				  		File f = new File(CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_IMAGE+"/"+empList.get(i).trim()+"/"+I_60x60+"/"+empimg);
				        if (f.exists()) {
				        	strImage = "<img class=\"img-circle lazy\" border=\"0\" height=\"24\" width=\"24\" title=\""+hmEmpName.get(empList.get(i).trim())+"\" src=\""+CF.getStrDocRetriveLocation() +I_PEOPLE+"/"+I_IMAGE+"/"+empList.get(i).trim()+"/"+I_60x60+"/"+empimg+"\" />";
				        } else {
				        	strImage = "<img class=\"img-circle lazy\" border=\"0\" height=\"24\" width=\"24\" title=\""+hmEmpName.get(empList.get(i).trim())+"\" src=\"userImages/avatar_photo.png\" />";
				        }
		            } else {
		            	strImage = "<img class=\"img-circle lazy\" border=\"0\" height=\"24\" width=\"24\" title=\""+hmEmpName.get(empList.get(i).trim())+"\" src=\"userImages/avatar_photo.png\" />";
		            }
					if(performance>60 && performance<=100 && potential<=30) {
						list1.add("<a href=\"javascript:void(0)\" onclick=\"getEmpProfile('"+empList.get(i).trim()+"')\" >" +
	//							"<img class=\"lazy\" src=\"userImages/avatar_photo.png\"  data-original=\"userImages/"+uF.showData(empImageMap.get(empList.get(i).trim()), "avatar_photo.png")+"\" border=\"0\" height=\"16px\" width=\"16px\" title=\""+hmEmpName.get(empList.get(i).trim())+"\"/>" +
								""+ strImage + "</a>");
						builder1.append(empList.get(i)+",");
	//					list1.add("userImages/"+uF.showData(empImageMap.get(empList.get(i)), "avatar_photo.png"));
	//				} else if(potential>60 && potential<=100 && performance>30 && performance<=60) {
					} else if(performance>60 && performance<=100 && potential>30 && potential<=60) {
						list2.add("<a href=\"javascript:void(0)\" onclick=\"getEmpProfile('"+empList.get(i).trim()+"')\" >" +
								""+ strImage + "</a>");
						builder2.append(empList.get(i)+",");
	//				} else if(potential>60 && potential<=100 && performance>60 && performance<=100) {
					} else if(performance>60 && performance<=100 && potential>60 && potential<=100) {
						list3.add("<a href=\"javascript:void(0)\" onclick=\"getEmpProfile('"+empList.get(i).trim()+"')\" >" +
								""+ strImage + "</a>");
						builder3.append(empList.get(i)+",");
	//				} else if(potential>30 && potential<=60  && performance<=30) {
					} else if(performance>30 && performance<=60  && potential<=30) {
						list4.add("<a href=\"javascript:void(0)\" onclick=\"getEmpProfile('"+empList.get(i).trim()+"')\" >" +
								""+ strImage + "</a>");
						builder4.append(empList.get(i)+",");
	//				} else if(potential>30 && potential<=60 &&  performance>30 && performance<=60) {
					} else if(performance>30 && performance<=60 &&  potential>30 && potential<=60) {
						list5.add("<a href=\"javascript:void(0)\" onclick=\"getEmpProfile('"+empList.get(i).trim()+"')\" >" +
								""+ strImage + "</a>");
						builder5.append(empList.get(i)+",");
	//				} else if(potential>30 && potential<=60 && performance>60 && performance<=100) {
					} else if(performance>30 && performance<=60 && potential>60 && potential<=100) {
						list6.add("<a href=\"javascript:void(0)\" onclick=\"getEmpProfile('"+empList.get(i).trim()+"')\" >" +
								""+ strImage + "</a>");
						builder6.append(empList.get(i)+",");
	//				} else if(potential<=30 && performance<=30) {
					} else if(performance<=30 && potential<=30) {
						list7.add("<a href=\"javascript:void(0)\" onclick=\"getEmpProfile('"+empList.get(i).trim()+"')\" >" +
								""+ strImage + "</a>");
						builder7.append(empList.get(i)+",");
	//				} else if(potential<=30 &&  performance>30 && performance<=60) {
					} else if(performance<=30 &&  potential>30 && potential<=60) {
						list8.add("<a href=\"javascript:void(0)\" onclick=\"getEmpProfile('"+empList.get(i).trim()+"')\" >" +
								""+ strImage + "</a>");
						builder8.append(empList.get(i)+",");
	//				} else if(potential<=30 && performance>60 && performance<=100) {
					} else if(performance<=30 && potential>60 && potential<=100) {
						list9.add("<a href=\"javascript:void(0)\" onclick=\"getEmpProfile('"+empList.get(i).trim()+"')\" >" +
								""+ strImage + "</a>");
						builder9.append(empList.get(i)+",");
					}
				}
				
				Map<String, String> hmEmpList = new HashMap<String, String>();
				hmEmpList.put("EMPLIST1", builder1.toString());
				hmEmpList.put("EMPLIST2", builder2.toString());
				hmEmpList.put("EMPLIST3", builder3.toString());
				hmEmpList.put("EMPLIST4", builder4.toString());
				hmEmpList.put("EMPLIST5", builder5.toString());
				hmEmpList.put("EMPLIST6", builder6.toString());
				hmEmpList.put("EMPLIST7", builder7.toString());
				hmEmpList.put("EMPLIST8", builder8.toString());
				hmEmpList.put("EMPLIST9", builder9.toString());
				
				request.setAttribute("hmEmpList", hmEmpList);
				
				request.setAttribute("list1", list1);
				request.setAttribute("list2", list2);
				request.setAttribute("list3", list3);
				request.setAttribute("list4", list4);
				request.setAttribute("list5", list5);
				request.setAttribute("list6", list6);
				request.setAttribute("list7", list7);
				request.setAttribute("list8", list8);
				request.setAttribute("list9", list9);
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
