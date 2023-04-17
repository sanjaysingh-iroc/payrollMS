package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GoalTarget extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = -8347978133635278063L;
	HttpSession session;
	String strSessionEmpId;
	String strEmpOrgId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;
	
	String strUserType = null;

	private String dataType;
	
	private String f_org;
	private String[] f_strWLocation;
	private String[] f_department;
	private String[] f_level;
	
	private List<FillOrganisation> orgList;
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	
	private String exportType;
	
	private String strSearchJob;
	
	private String proPage;
	private String minLimit;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strUserType = (String) session.getAttribute(USERTYPE);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return "login";

		request.setAttribute(PAGE, "/jsp/performance/GoalTarget.jsp");
		request.setAttribute(TITLE, TTargets);
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		UtilityFunctions uF = new UtilityFunctions();		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		if(getDataType() == null || getDataType().trim().equals("") || getDataType().trim().equalsIgnoreCase("NULL")) {
			setDataType("L");
		}
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		if(strSessionUserType!=null && !strSessionUserType.equals(EMPLOYEE)) {
			getSearchAutoCompleteData(uF);		
		}
		
		List<String> empList=getEmployeeList(uF);		
		
		getEmpTargetDetails(uF,empList);
		getTargetRating(uF);
		checkTargetStatus(uF);
		return loadKRAData(uF);
	}
	
	
	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			String strOrgCurrId = CF.getOrgCurrencyIdByOrg(con, getF_org());
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			String strCurrency = "";
			if(uF.parseToInt(strOrgCurrId) > 0) {
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(strOrgCurrId);
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			}
			request.setAttribute("strCurrency",strCurrency);

			SortedSet<String> setEmpList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and is_alive = true ");
			if(strSessionUserType!=null && strSessionUserType.equals(MANAGER)){
				sbQuery.append(" and eod.supervisor_emp_id = " + strSessionEmpId);
			} else {
				if(uF.parseToInt(getF_org())>0){
					sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0){
		            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
		        }
				 if(getF_department()!=null && getF_department().length>0){
		            sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	            }
	            if(getF_level()!=null && getF_level().length>0){
	                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	            }
			}		
			sbQuery.append(" order by epd.emp_fname");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst===> "+ pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				setEmpList.add(rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setEmpList.iterator();
			while (it.hasNext()){
				String strData = it.next();
				if(sbData == null){
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null){
				sbData = new StringBuilder();
			}
			request.setAttribute("sbData", sbData.toString());
				
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
	}
	

	public void checkTargetStatus(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try { 
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from target_details");
			rs = pst.executeQuery();
			List<String> alCheckList = new ArrayList<String>();
			while (rs.next()) {
				if(!alCheckList.contains(rs.getString("goal_id"))){
					alCheckList.add(rs.getString("goal_id"));
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select goal_kra_target_id from question_bank where goal_kra_target_id is not null");
			rs = pst.executeQuery();
			while (rs.next()) {
				if(!alCheckList.contains(rs.getString("goal_kra_target_id"))){
					alCheckList.add(rs.getString("goal_kra_target_id"));
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alCheckList", alCheckList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}


	public void getTargetRating(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try { 
			con = db.makeConnection(con);
			
			/*pst=con.prepareStatement("select aaa.question_bank_id,goal_kra_target_id,avg from (select question_bank_id,sum(average)/count(question_bank_id) as avg " +
					" from(select question_bank_id,(marks*100/weightage) as average from (select question_bank_id,question_text,goal_kra_target_id " +
					" from question_bank where goal_kra_target_id in(select goal_id from goal_details where " + //is_measure_kra = false and 
					" (goal_type = ? or goal_type = ?))) as a,appraisal_question_answer aqa where a.question_bank_id=aqa.question_id) b group by " +
					" question_bank_id) aaa, question_bank qb where aaa.question_bank_id=qb.question_bank_id");*/
			
			pst=con.prepareStatement("select goal_kra_target_id,emp_id,sum(average)/count(goal_kra_target_id) as avg from(select goal_kra_target_id," +
				"emp_id,(marks*100/weightage) as average from (select question_bank_id,question_text,goal_kra_target_id from question_bank where " +
				"goal_kra_target_id in(select goal_id from goal_details where is_measure_kra = true and measure_type !='' and (goal_type = ? or " +
				"goal_type = ?))) as a,appraisal_question_answer aqa where a.question_bank_id=aqa.question_id and weightage>0) b group by goal_kra_target_id,emp_id");
			pst.setInt(1, INDIVIDUAL_GOAL);
			pst.setInt(2, INDIVIDUAL_TARGET);
//			System.out.println("pst ===>> " + pst);
			rs=pst.executeQuery();
			Map<String, String> hmTargetAverage = new HashMap<String, String>();
			while (rs.next()) {
				hmTargetAverage.put(rs.getString("emp_id")+"_"+rs.getString("goal_kra_target_id"), rs.getString("avg"));
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmTargetAverage ===>> " + hmTargetAverage);
			request.setAttribute("hmTargetAverage", hmTargetAverage);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	void getEmpTargetDetails(UtilityFunctions uF,List<String> empList) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
			request.setAttribute("hmEmpCodeName", hmEmpCodeName);
			Map<String, List<List<String>>> hmGoalTarget= new HashMap<String, List<List<String>>>();
			Map<String, List<List<String>>> hmIndividualTarget= new HashMap<String, List<List<String>>>();
			
			for(int i=0;empList!=null && !empList.isEmpty() && i<empList.size();i++){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from goal_details gd where gd.emp_ids like '%,"+empList.get(i)+ ",%' and " +
						" gd.goal_type = ? and is_measure_kra = true and gd.measure_type !='' ");
				if(getDataType() != null && getDataType().equals("L")) {
					sbQuery.append(" and is_close = false ");
				} else if(getDataType() != null && getDataType().equals("C")) {
					sbQuery.append(" and is_close = true ");
				}
				sbQuery.append(" order by gd.goal_id");
				pst=con.prepareStatement(sbQuery.toString());
				pst.setInt(1, INDIVIDUAL_GOAL);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<List<String>> outerList=hmGoalTarget.get(empList.get(i));
					if(outerList==null) outerList=new ArrayList<List<String>>();
					
					List<String> innerList=new ArrayList<String>();
					innerList.add(empList.get(i));
					innerList.add(rs.getString("goal_id"));
					innerList.add(rs.getString("goal_description"));
					innerList.add(rs.getString("goal_title"));
					
					innerList.add(rs.getString("measure_type"));
					String val="",daysHRVal="";
					if(rs.getString("measure_type")!=null && rs.getString("measure_type").equals("Effort")){
						val=" "+rs.getString("measure_effort_days")+" Days and "+rs.getString("measure_effort_hrs")+" Hrs.";
						daysHRVal = (rs.getString("measure_effort_days") != null && !rs.getString("measure_effort_days").equals("") ? rs.getString("measure_effort_days") : "0")+"."
						+(rs.getString("measure_effort_hrs") != null && !rs.getString("measure_effort_hrs").equals("") ? rs.getString("measure_effort_hrs") : "0");
					}else{
//						val= CF.getAmountInCrAndLksFormat(rs.getDouble("measure_currency_value"));
//						val= uF.formatIntoComma(uF.parseToDouble(rs.getString("measure_currency_value")));
						val= uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("measure_currency_value")));
//						val= ""+uF.parseToDouble(rs.getString("measure_currency_value"));
					}
					innerList.add(val);
					
					if(uF.parseToInt(strSessionEmpId)>0 && uF.parseToInt(strSessionEmpId)==uF.parseToInt(rs.getString("user_id"))){
						innerList.add("Self");
					}else{
						innerList.add(hmEmpCodeName.get(rs.getString("user_id")));
					}
					innerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat())); //7
					innerList.add(daysHRVal); //8
					innerList.add(rs.getString("is_close")); //9
					String tGoalId = getPerentGoalId(con, uF, rs.getString("goal_id"));
					innerList.add(tGoalId); // team goal id 10
					String mGoalId = getPerentGoalId(con, uF, tGoalId);
					innerList.add(mGoalId); // manager goal id 11
					String cGoalId = getPerentGoalId(con, uF, mGoalId);
					innerList.add(cGoalId); // Corporate goal id 12
					
					String priority="";
					String pClass="";
					if(rs.getString("priority")!=null && !rs.getString("priority").equals("")){
						if(rs.getString("priority").equals("1")){
							pClass="high";
							priority="High";
						}else if(rs.getString("priority").equals("2")){
							pClass="medium";
							priority="Medium";
						}else if(rs.getString("priority").equals("3")){
							pClass="low";
							priority="Low";
						}
					}
					innerList.add(priority); // Priority 13
					innerList.add(pClass); // Priority class 14
					innerList.add(rs.getString("goal_type")); // 15
					
					outerList.add(innerList);
					hmGoalTarget.put(empList.get(i), outerList);
					
				}
				rs.close();
				pst.close();
				
				StringBuilder sbQuery1 = new StringBuilder();
				sbQuery1.append("select * from goal_details where goal_type = ? and emp_ids like '%,"+empList.get(i)+ ",%'");
				if(getDataType() != null && getDataType().equals("L")) {
					sbQuery1.append(" and is_close = false ");
				} else if(getDataType() != null && getDataType().equals("C")) {
					sbQuery1.append(" and is_close = true ");
				}
				pst=con.prepareStatement(sbQuery1.toString());
				pst.setInt(1, INDIVIDUAL_TARGET);
				rs = pst.executeQuery();
				while (rs.next()) {
					List<List<String>> outerList=hmIndividualTarget.get(empList.get(i));
					if(outerList==null) outerList=new ArrayList<List<String>>();
					
					List<String> innerList=new ArrayList<String>();
					innerList.add(empList.get(i));
					innerList.add(rs.getString("goal_id"));
					innerList.add(rs.getString("goal_description"));
					innerList.add(rs.getString("goal_title"));
					innerList.add(rs.getString("measure_type"));
					String val="",daysHRVal="";
					if(rs.getString("measure_type")!=null && rs.getString("measure_type").equals("Effort")){
						val=" "+rs.getString("measure_effort_days")+" Days and "+rs.getString("measure_effort_hrs")+" Hrs.";
						daysHRVal = (rs.getString("measure_effort_days") != null && !rs.getString("measure_effort_days").equals("") ? rs.getString("measure_effort_days") : "0")+"."
						+(rs.getString("measure_effort_hrs") != null && !rs.getString("measure_effort_hrs").equals("") ? rs.getString("measure_effort_hrs") : "0");
					}else{
//						val= CF.getAmountInCrAndLksFormat(rs.getDouble("measure_currency_value"));
//						val= uF.formatIntoComma(uF.parseToDouble(rs.getString("measure_currency_value")));
						val= ""+uF.parseToDouble(rs.getString("measure_currency_value"));
					}
					
					innerList.add(val);
					if(uF.parseToInt(strSessionEmpId)>0 && uF.parseToInt(strSessionEmpId)==uF.parseToInt(rs.getString("user_id"))){
						innerList.add("Self");
					}else{
						innerList.add(hmEmpCodeName.get(rs.getString("user_id")));
					}
					innerList.add(uF.getDateFormat(rs.getString("due_date"), DBDATE, CF.getStrReportDateFormat())); //7
					innerList.add(daysHRVal); //8
					innerList.add(rs.getString("is_close")); //9
					String tGoalId = getPerentGoalId(con, uF, rs.getString("goal_id"));
					innerList.add(tGoalId); // team goal id 10
					String mGoalId = getPerentGoalId(con, uF, tGoalId);
					innerList.add(mGoalId); // manager goal id 11
					String cGoalId = getPerentGoalId(con, uF, mGoalId);
					innerList.add(cGoalId); // Corporate goal id 12
					
					String priority="";
					String pClass="";
					if(rs.getString("priority")!=null && !rs.getString("priority").equals("")){
						if(rs.getString("priority").equals("1")){
							pClass="high";
							priority="High";
						}else if(rs.getString("priority").equals("2")){
							pClass="medium";
							priority="Medium";
						}else if(rs.getString("priority").equals("3")){
							pClass="low";
							priority="Low";
						}
					}
					innerList.add(priority); // Priority 13
					innerList.add(pClass); // Priority class 14
					innerList.add(rs.getString("goal_type")); // 15
					
					outerList.add(innerList);
					
					hmIndividualTarget.put(empList.get(i), outerList);
					
				}
				rs.close();
				pst.close();
				
			}
			
			Map<String, String> hmTargetValue= new HashMap<String,String>();
			Map<String, String> hmTargetID= new HashMap<String,String>();
			Map<String, String> hmTargetRemark= new HashMap<String,String>();
			Map<String, String> hmTargetTmpValue= new HashMap<String,String>();
			Map<String, String> hmUpdateBy= new HashMap<String,String>();
			pst = con.prepareStatement("select * from target_details where target_id in (select max(target_id) from target_details group by goal_id,emp_id)");
			rs= pst.executeQuery();
			while(rs.next()){
				hmTargetValue.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("amt_percentage"));
				hmTargetID.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("target_id"));
				hmTargetRemark.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("target_remark"));
				hmTargetTmpValue.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), rs.getString("emp_amt_percentage"));
				hmUpdateBy.put(rs.getString("emp_id")+"_"+rs.getString("goal_id"), hmEmpCodeName.get(rs.getString("added_by"))+" on "+uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
			}
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
			rs=pst.executeQuery();
			Map<String,String> empImageMap=new HashMap<String,String>();
			while(rs.next()){
				empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
			}
			rs.close();
			pst.close();
			request.setAttribute("empImageMap", empImageMap);
			request.setAttribute("hmUpdateBy", hmUpdateBy);
			request.setAttribute("hmTargetValue", hmTargetValue);
			request.setAttribute("hmTargetID", hmTargetID);
			request.setAttribute("hmTargetRemark", hmTargetRemark);
			
			request.setAttribute("hmTargetTmpValue", hmTargetTmpValue);
			request.setAttribute("hmGoalTarget", hmGoalTarget);
			request.setAttribute("hmIndividualTarget", hmIndividualTarget);
		
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	
	private String getPerentGoalId(Connection con, UtilityFunctions uF, String goalID) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String goalId = null;
		try {
			String query1 = "select goal_parent_id from goal_details where goal_id = ?";
			pst = con.prepareStatement(query1);
			pst.setInt(1, uF.parseToInt(goalID));
			rs = pst.executeQuery();
			while (rs.next()) {
				goalId = rs.getString("goal_parent_id");
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return goalId;
	}
	
	
	List<String> getEmployeeList(UtilityFunctions uF) {
		List<String> al = new ArrayList<String>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			//if(uF.parseToInt(getF_org())==0 && uF.parseToInt(getF_Location())==0 && uF.parseToInt(getF_department())==0 && uF.parseToInt(getF_level())==0 && uF.parseToInt(getF_desig())==0 ){
			if(strSessionUserType!=null && strSessionUserType.equals(EMPLOYEE)){
				al.add(strSessionEmpId);
			}else{
				String query1 = "select depart_id from employee_official_details where emp_id=?";
				pst = con.prepareStatement(query1);
				pst.setInt(1, uF.parseToInt(strSessionEmpId));
				rs = pst.executeQuery();
				int depart_id = 0;
				while (rs.next()) {
					depart_id = rs.getInt("depart_id");
				}
				rs.close();
				pst.close();
				
				StringBuilder sbQuery=new StringBuilder();
				sbQuery.append("select count(*) as cnt from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and is_alive = true ");
				if(strSessionUserType!=null && strSessionUserType.equals(MANAGER)){
					sbQuery.append(" and eod.supervisor_emp_id = " + strSessionEmpId);
				} else {
					if(uF.parseToInt(getF_org())>0){
						sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
					}
					if(getF_strWLocation()!=null && getF_strWLocation().length>0){
			            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
			        }
					 if(getF_department()!=null && getF_department().length>0){
			            sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
		            }
		            if(getF_level()!=null && getF_level().length>0){
		                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
		            }
				}	
				if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
					sbQuery.append(" and upper(epd.emp_fname)|| ' '||upper(epd.emp_lname) like'%"+getStrSearchJob().trim().toUpperCase()+"%'");
				}
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst======>"+pst);
				rs=pst.executeQuery();
				int proCount = 0;
				int proCnt = 0;
				while(rs.next()) {
					proCnt = rs.getInt("cnt");
					proCount = rs.getInt("cnt")/10;
					if(rs.getInt("cnt")%10 != 0) {
						proCount++;
					}
				}
				rs.close();
				pst.close();
				request.setAttribute("proCount", proCount+"");
				request.setAttribute("proCnt", proCnt+"");
				
				sbQuery=new StringBuilder();
				sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and is_alive = true ");
				if(strSessionUserType!=null && strSessionUserType.equals(MANAGER)){
					sbQuery.append(" and eod.supervisor_emp_id = " + strSessionEmpId);
				} else {
					if(uF.parseToInt(getF_org())>0){
						sbQuery.append(" and eod.org_id="+uF.parseToInt(getF_org()));
					}
					if(getF_strWLocation()!=null && getF_strWLocation().length>0){
			            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
			        }
					 if(getF_department()!=null && getF_department().length>0){
			            sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
		            }
		            if(getF_level()!=null && getF_level().length>0){
		                sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
		            }
				}
				if(getStrSearchJob()!=null && !getStrSearchJob().trim().equals("")){
					sbQuery.append(" and upper(epd.emp_fname)|| ' '||upper(epd.emp_lname) like'%"+getStrSearchJob().trim().toUpperCase()+"%'");
				}		
				sbQuery.append(" order by epd.emp_fname");	
				int intOffset = uF.parseToInt(getMinLimit());
				sbQuery.append(" limit 10 offset "+intOffset+"");		
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst======>"+pst);
				rs = pst.executeQuery();
//				if(uF.parseToInt(getProPage()) == 1) {
//					al.add(strSessionEmpId);
//				}
				while (rs.next()) {
					if(!al.contains(rs.getString("emp_per_id")))
						al.add(rs.getString("emp_per_id"));
				}
				rs.close();
				pst.close();
				
			}
			
			request.setAttribute("empList", al);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);			
		}
		return al;
	}


	private String loadKRAData(UtilityFunctions uF) {
		
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));

		getSelectedFilter(uF);
		
		return SUCCESS;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=orgList.get(i).getOrgName();
					} else {
						strOrg+=", "+orgList.get(i).getOrgName();
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
				hmFilter.put("LEVEL", "All Level's");
			}
		} else {
			hmFilter.put("LEVEL", "All Level's");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
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


	public String[] getF_level() {
		return f_level;
	}


	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}


	public List<FillOrganisation> getOrgList() {
		return orgList;
	}


	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
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


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public String getExportType() {
		return exportType;
	}


	public void setExportType(String exportType) {
		this.exportType = exportType;
	}


	public String getStrSearchJob() {
		return strSearchJob;
	}


	public void setStrSearchJob(String strSearchJob) {
		this.strSearchJob = strSearchJob;
	}


	public String getProPage() {
		return proPage;
	}


	public void setProPage(String proPage) {
		this.proPage = proPage;
	}


	public String getMinLimit() {
		return minLimit;
	}


	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}
	
}
