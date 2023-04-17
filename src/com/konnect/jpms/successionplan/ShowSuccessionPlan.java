package com.konnect.jpms.successionplan;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import com.konnect.jpms.leave.ManagerLeaveApproval;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ShowSuccessionPlan extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	String strSessionEmpId = null; 
	List<FillDesig> desigList; 
	 
	String f_org;
	String designation;
	String[] f_service;
	
	private String strEmpId;
	private String desigId;
	private String planStatusName;
	private String operation;
	
	CommonFunctions CF=null;
	String strAction = null;
	private static Logger log = Logger.getLogger(ShowSuccessionPlan.class);
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType = (String) session.getAttribute(USERTYPE);
		strSessionEmpId= (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/successionplan/ShowSuccessionPlan.jsp");
		request.setAttribute(TITLE, "Succession Plan");
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-line-chart\"></i><a href=\"Analytics.action\" style=\"color: #3c8dbc;\">Performance</a></li>" +
		"<li class=\"active\">Succession Plan</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		if(getOperation() !=null && getOperation().equals("updatePlanStatus")) {
			return updatePlanStatus();
		}
		
		desigList = new FillDesig(request).fillDesig();
		
		viewEmployee(uF);
		getEmpPrevEmployment();
		getElementList();
		getAttributes();
		selectSkills();
		getPlanStatus();
		getLastReviewRating();
		getDesignationwiseSuccessorData();
		getAchievableLevelCount(uF);
		getEmpImage(uF);
		getRemarks(uF);

		getDesignationwiseFinalSuccessPlanData();
		getSelectedFilter(uF);
		
		return LOAD;
		
	}

	
	private String updatePlanStatus() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
//			System.out.println("strRotationOfShift ===>> " + strRotationOfShift);
			String statusColor = "#1bff00";
			if(getPlanStatusName() != null && getPlanStatusName().equals("Better to Prepare")) {
				statusColor = "#f7ff00";
			} else if(getPlanStatusName() != null && getPlanStatusName().equals("Passive Status")) {
				statusColor = "#ff1801";
			}
			pst = con.prepareStatement("UPDATE plan_status_details SET plan_status=?, status_color=?, entry_date=?, added_by=? where emp_id=? and desig_id=?");
			pst.setString(1, getPlanStatusName());
			pst.setString(2, statusColor);
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(4, uF.parseToInt(strSessionEmpId));
			pst.setInt(5, uF.parseToInt(getStrEmpId()));
			pst.setInt(6, uF.parseToInt(getDesigId()));
			int x = pst.executeUpdate();
//			System.out.println("update pst ===>> " + pst);
			pst.close();
			
			if(x==0) {
				pst = con.prepareStatement("insert into plan_status_details (plan_status, status_color, emp_id, desig_id, entry_date, added_by) values(?,?,?,?, ?,?)");
				pst.setString(1, getPlanStatusName());
				pst.setString(2, statusColor);
				pst.setInt(3, uF.parseToInt(getStrEmpId()));
				pst.setInt(4, uF.parseToInt(getDesigId()));
				pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(6, uF.parseToInt(strSessionEmpId));
				pst.executeUpdate();
//				System.out.println(strEmpId + " insert --- pst ===>> " + pst);
				pst.close();
				
			}
			
			
			StringBuilder sbSelect = new StringBuilder();
			sbSelect.append("<select style=\"width: 100px !important; background-color: "+statusColor+" \" " +
					"onchange=\"changePlanStatus(this.value, '"+getStrEmpId()+"','"+getDesigId()+"');\" >");
			sbSelect.append("<option value=''>Select Status</option>");
			sbSelect.append("<option value=\"Active Status\"");
			if(getPlanStatusName() !=null && getPlanStatusName().equals("Active Status")) { 
   				sbSelect.append("selected"); 
   			}
   			sbSelect.append(">Active</option>");
   			sbSelect.append("<option value=\"Better to Prepare\"");
			if(getPlanStatusName() !=null && getPlanStatusName().equals("Better to Prepare")) { 
   				sbSelect.append("selected"); 
   			}
   			sbSelect.append(">Better to Prepare</option>");
   			sbSelect.append("<option value=\"Passive Status\"");
			if(getPlanStatusName() !=null && getPlanStatusName().equals("Passive Status")) { 
   				sbSelect.append("selected"); 
   			}
   			sbSelect.append(">Passive</option>");
       		sbSelect.append("</select>");
       
			request.setAttribute("STATUS_MSG", sbSelect.toString());
       		
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "ajax";
	}


	private void getRemarks(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;

		Database db = new Database();
		db.setRequest(request);
		String remark = null;
		String strApprovedBy = null;
		Map<String,String> hmRemark=new HashMap<String, String>();
		boolean flag= false;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			pst = con.prepareStatement("Select sattlement_comment,if_approved,user_id, emp_fname,emp_mname, emp_lname,activity_ids,afs.emp_id,appraisal_id" +
							",_date from appraisal_final_sattlement afs,employee_personal_details epd  where afs.user_id = epd.emp_per_id");
			rs = pst.executeQuery();
			
			while (rs.next()) {
				remark = rs.getString("sattlement_comment");
				if (remark != null) {
					remark = remark.replace("\n", "<br/>");
				}
				flag = uF.parseToBoolean(rs.getString("if_approved"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				strApprovedBy = rs.getString("emp_fname") +strEmpMName+" "+ rs.getString("emp_lname");
				
				hmRemark.put(rs.getString("appraisal_id")+rs.getString("emp_id"), strApprovedBy+" on "+ uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
			}	
			rs.close();
			pst.close();

			/*request.setAttribute("hrremark", remark);
			request.setAttribute("flag", flag);
			request.setAttribute("strApprovedBy", strApprovedBy);*/
			
			request.setAttribute("hmRemark", hmRemark);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	private void getEmpImage(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);

			pst=con.prepareStatement("select emp_image,emp_per_id from employee_personal_details ");
			rs=pst.executeQuery();
			Map<String,String> empImageMap=new HashMap<String,String>();
			while(rs.next()){
				empImageMap.put(rs.getString("emp_per_id"),rs.getString("emp_image"));
			}	
			rs.close();
			pst.close();
			request.setAttribute("empImageMap", empImageMap);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private String getEmpLastPromotion(UtilityFunctions uF, String empId) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		String promotionStatus = "No";
		try {
			con = db.makeConnection(con);
			Date last3YrsDate = uF.getPrevDate(CF.getStrTimeZone(), 1095);
			pst=con.prepareStatement("select emp_activity_id from employee_activity_details where activity_id = 6 and effective_date < ? and emp_id = ? ");
			pst.setDate(1, last3YrsDate);
			pst.setInt(2, uF.parseToInt(empId));
			rs=pst.executeQuery();
//			System.out.println("pst ===> " + pst);
			while(rs.next()){
				promotionStatus = "Yes";
			}	
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return promotionStatus;
	}
	
	
	
	private void getAchievableLevelCount(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			List<String> levelList = new ArrayList<String>();
			pst=con.prepareStatement("select level_id from level_details");
			rs=pst.executeQuery();
			while(rs.next()){
				levelList.add(rs.getString("level_id"));
			}	
			rs.close();
			pst.close();
			
			List<String> levelList1 = new ArrayList<String>();
			pst=con.prepareStatement("select level_id from level_details");
			rs=pst.executeQuery();
			while(rs.next()){
				levelList1.add(rs.getString("level_id"));
			}	
			rs.close();
			pst.close();
			
			Map<String, Map<String, String>> hmMainLevelDiffCount = new HashMap<String, Map<String,String>>();
			for(int i=0; levelList != null && !levelList.isEmpty() && i<levelList.size(); i++){
				
				Map<String, String> hmLevelDiffCount = new HashMap<String, String>();
				for(int j=0; levelList1 != null && !levelList1.isEmpty() && j<levelList1.size(); j++){
				
					pst=con.prepareStatement("select count(level_id) as cnt from level_details where level_id between "+levelList.get(i)+" and "+levelList1.get(j)+"");
					rs=pst.executeQuery();
					int levelCnt = 0;
					while(rs.next()){
						levelCnt = rs.getInt("cnt");
					}	
					rs.close();
					pst.close();
					
					levelCnt--;
					hmLevelDiffCount.put(levelList.get(i)+"_"+levelList1.get(j), ""+levelCnt);
				}
				hmMainLevelDiffCount.put(levelList.get(i), hmLevelDiffCount);
			}
//			System.out.println("hmMainLevelDiffCount =====> " + hmMainLevelDiffCount);
			
			request.setAttribute("hmMainLevelDiffCount", hmMainLevelDiffCount);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
public String getTimeDurationBetweenDates(String strStartDate, String strStartFormat, String strEndDate, String strEndFormat, UtilityFunctions uF, boolean isYear, boolean isMonth, boolean isDays){
		
		StringBuilder sbTimeDuration = new StringBuilder();
		try {
			
			LocalDate joiningDate = new LocalDate(
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strStartDate, strStartFormat, "dd")));
		    LocalDate currentDate = new LocalDate(
		    		uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "yyyy")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "MM")), 
					uF.parseToInt(uF.getDateFormat(strEndDate, strEndFormat, "dd")));

		    Period period = new Period(joiningDate, currentDate, PeriodType.yearMonthDay());
			
		    if(isYear){
				sbTimeDuration.append(period.getYears()+":");
		    }
			
		    if(isMonth){
	    		sbTimeDuration.append(period.getMonths());
		    }
			
			/*if(isDays){
				sbTimeDuration.append(period.getDays());
			}*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbTimeDuration.toString();
	}
	

public Map<String, String> getEmpPrevEmployment() {
	Connection con = null;
	PreparedStatement pst = null;
	Database db = new Database();
	db.setRequest(request);
	ResultSet rs = null;
	UtilityFunctions uF = new UtilityFunctions();
	Map<String, String> hmPrevExpEmpwise = new HashMap<String, String>();
	try {
		con = db.makeConnection(con);
		pst = con.prepareStatement("select * from emp_prev_employment order by emp_id");
		rs = pst.executeQuery();
//		List<List<String>> elementouterList=new ArrayList<List<String>>();
		String empID = "";
		int expYrs =0, expMnth =0 ;
		while (rs.next()) {
//			List<String> innerList=new ArrayList<String>();
//			innerList.add(rs.getString("from_date"));
//			innerList.add(rs.getString("to_date"));
//			innerList.add(rs.getString("emp_id"));
//			elementouterList.add(innerList);
			String durtime = "0:0";
			if(rs.getString("from_date") != null && rs.getString("to_date") != null) {
				durtime = getTimeDurationBetweenDates(rs.getString("from_date"), DBDATE, rs.getString("to_date"), DBDATE, uF, true, true, true);
			}
			String strExpYrs[] = durtime.split(":");
			if(empID.equals(rs.getString("emp_id"))) {
				expYrs += uF.parseToInt(strExpYrs[0]);
				if(strExpYrs.length > 1){
					expMnth += uF.parseToInt(strExpYrs[1]);
				}
			} else if(!empID.equals(rs.getString("emp_id"))) {
				expYrs = uF.parseToInt(strExpYrs[0]);
				if(strExpYrs.length > 1){
					expMnth = uF.parseToInt(strExpYrs[1]);
				}
			}
			
			if(!empID.equals(rs.getString("emp_id"))){
				 empID = rs.getString("emp_id");
			 }
			hmPrevExpEmpwise.put(rs.getString("emp_id"), expYrs+"."+expMnth);
		}	
		rs.close();
		pst.close();
		request.setAttribute("hmPrevExpEmpwise",hmPrevExpEmpwise);

	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		db.closeResultSet(rs);
		db.closeStatements(pst);
		db.closeConnection(con);
	}
	
	return hmPrevExpEmpwise;
}

	

	public void getDesignationwiseFinalSuccessPlanData() {
		UtilityFunctions uF = new UtilityFunctions();
		
		try {
			
		Map<String, Map<String, List<String>>> hmDesignationwiseEmp = (Map<String, Map<String, List<String>>>) request.getAttribute("hmDesignationwiseEmp");
		if(hmDesignationwiseEmp == null) hmDesignationwiseEmp = new HashMap<String, Map<String,List<String>>>();
		
		Map<String, List<List<String>>> hmSkillValue = (Map<String, List<List<String>>>) request.getAttribute("hmSkillValue");
		Map<String, String> hmScoreAggregateMap = (Map<String, String>) request.getAttribute("hmScoreAggregateMap");
		List<List<String>> elementouterList = (List<List<String>>) request.getAttribute("elementouterList");
		
		String potentialId1 ="", performanceId1 = "";
			for (int i = 0; elementouterList != null && i < elementouterList.size(); i++) {
				List<String> innerList1 = elementouterList.get(i);
				if(innerList1.get(1).trim().equalsIgnoreCase("Potential")){
					potentialId1 = innerList1.get(0).trim();
				} else if(innerList1.get(1).trim().equalsIgnoreCase("Performance")){
					performanceId1 = innerList1.get(0).trim();
				}
			}
		
		Iterator<String> it = hmDesignationwiseEmp.keySet().iterator();
		int count=0;
		Map<String, List<String>> hmDesigwiseEmpId = new HashMap<String, List<String>>();
		while(it.hasNext()){
			String strDesgID = it.next();
			Map<String, List<String>> hmEmpwiseData = hmDesignationwiseEmp.get(strDesgID);
				if(hmEmpwiseData != null && !hmEmpwiseData.isEmpty()) {
					
					List<Double> ratingList = new ArrayList<Double>();
					Map<String, String> hmRating = new HashMap<String, String>();
					Iterator<String> it1 = hmEmpwiseData.keySet().iterator();
					while(it1.hasNext()){
						String strEmpID = it1.next();
		//			for(int k=0; empDataListDesigwise != null && k<empDataListDesigwise.size(); k++){
						List<String> empDataList = hmEmpwiseData.get(strEmpID);
//						System.out.println("empDataList =======> " + empDataList);
//						System.out.println("hmSkillValue =======> " + hmSkillValue);
						double allSkillValue = 0;
//						System.out.println("empDataList.get(0) =======> " + empDataList.get(0));
						List<List<String>> skillList = hmSkillValue.get(empDataList.get(0));
						int skillsCnt=0;
						for (int i = 0; skillList != null && !skillList.isEmpty() && i < skillList.size(); i++) {
							List<String> alInner = skillList.get(i);
							allSkillValue += uF.parseToDouble(alInner.get(2));
							skillsCnt++;
						}
						double skillValAvg = allSkillValue / skillsCnt;
						
						double potentialsAvg = uF.parseToDouble(hmScoreAggregateMap.get(empDataList.get(0)+"_"+potentialId1));
						double performancesAvg = uF.parseToDouble(hmScoreAggregateMap.get(empDataList.get(0)+"_"+performanceId1));
						
						double currentRatings = (skillValAvg + (potentialsAvg/10) + (performancesAvg/10)) / 3;
						if((currentRatings+"").equals("NaN")){
							currentRatings = 0;
						}
						ratingList.add(currentRatings);
						hmRating.put(empDataList.get(0), ""+currentRatings);
						//System.out.println("desigwiseInnerList.get(0) ===> " + desigwiseInnerList.get(0)+" currentRatings ===> " + currentRatings);
					}
					
//					System.out.println("ratingList before ===> " + ratingList);
					Collections.sort(ratingList, Collections.reverseOrder());
					//System.out.println("cinnerlist.get(7) === "+cinnerlist.get(7)+" ratingList =====> " + ratingList);
					
//					System.out.println("ratingList after ===> " + ratingList);
					
					List<String> ratingEmpList = new ArrayList<String>();
					for(int j=0; ratingList != null && j<ratingList.size(); j++) {
						Iterator<String> it2 = hmEmpwiseData.keySet().iterator();
						while(it2.hasNext()){
							String strEmpID = it2.next();
							List<String> empDataList = hmEmpwiseData.get(strEmpID);
							double allSkillValue = 0;
							List<List<String>> skillList = hmSkillValue.get(empDataList.get(0));
							int skillsCnt=0;
							for (int i = 0; skillList != null && !skillList.isEmpty() && i < skillList.size(); i++) {
								List<String> alInner = skillList.get(i);
								allSkillValue += uF.parseToDouble(alInner.get(2));
								skillsCnt++;
							}
							double skillValAvg = allSkillValue / skillsCnt;
							
							double potentialsAvg = uF.parseToDouble(hmScoreAggregateMap.get(empDataList.get(0)+"_"+potentialId1));
							double performancesAvg = uF.parseToDouble(hmScoreAggregateMap.get(empDataList.get(0)+"_"+performanceId1));
							
							double currentRatings = (skillValAvg + (potentialsAvg/10) + (performancesAvg/10)) / 3;
							if((currentRatings+"").equals("NaN")){
								currentRatings = 0;
							}
							
							if(ratingList.get(j) == currentRatings && !ratingEmpList.contains(empDataList.get(0))) {
								
								ratingEmpList.add(empDataList.get(0));
								hmRating.put(empDataList.get(0), ""+currentRatings);
//								System.out.println("empDataList.get(0) ===> " + empDataList.get(0)+" currentRatings ===> " + currentRatings);
							}
						}
					}
					hmDesigwiseEmpId.put(strDesgID, ratingEmpList);
				}
		}
//		System.out.println("hmDesigwiseEmpId ===> " + hmDesigwiseEmpId);
		request.setAttribute("hmDesigwiseEmpId", hmDesigwiseEmpId);
		
			} catch (Exception e) {
				e.printStackTrace();
				// TODO: handle exception
			}
	}



	public void getDesignationwiseSuccessorData() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmPrevExpEmpwise = getEmpPrevEmployment();
			
			pst = con.prepareStatement("select * from successionplan_criteria_details order by designation_id");
			rs = pst.executeQuery();
			List<List<String>> planCriteriaList = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("designation_id"));
				innerList.add(rs.getString("qualification_id"));
				innerList.add(rs.getString("qualification_weight"));
				innerList.add(rs.getString("total_exp"));
				innerList.add(rs.getString("precent_org_exp"));
				innerList.add(rs.getString("potential_attribute"));
				innerList.add(rs.getString("performance_attribute"));
				innerList.add(rs.getString("potential_threshold"));
				innerList.add(rs.getString("performance_threshold"));
				innerList.add(rs.getString("skills")); //10
				innerList.add(rs.getString("skills_threshold"));
				innerList.add(rs.getString("sbu_ids")); //11
				innerList.add(rs.getString("geography_ids")); //12
//				innerList.add(rs.getString("level_ids")); //13
				innerList.add(rs.getString("levels_below")); //13
				innerList.add(rs.getString("department_ids")); //14
				
				planCriteriaList.add(innerList);
			}
			rs.close();
			pst.close();
			
			
			
			Map<String, Map<String, List<String>>> hmDesignationwiseEmp = new HashMap<String, Map<String, List<String>>>();
			
			for(int i=0; planCriteriaList != null && !planCriteriaList.isEmpty() && i<planCriteriaList.size(); i++) {
				List<String> planInnerList = planCriteriaList.get(i); 
				if(uF.parseToInt(planInnerList.get(0)) > 0) {
					String allAboveQualiIds = getAboveQualifications(con, planInnerList.get(2));
					String allAboveLevelIds = getAboveLevels(con, planInnerList.get(0), planInnerList.get(13));
					
					StringBuilder sbQuery = new StringBuilder();
					String skills = planInnerList.get(9).length() > 1 ? planInnerList.get(9).substring(1, planInnerList.get(9).length()-1) : "";
					String services = planInnerList.get(11).length() > 1 ? planInnerList.get(11).substring(1, planInnerList.get(11).length()-1) : "";
					String locations = planInnerList.get(12).length() > 1 ? planInnerList.get(12).substring(1, planInnerList.get(12).length()-1) : "";
//					String levels = planInnerList.get(13).length() > 1 ? planInnerList.get(13).substring(1, planInnerList.get(13).length()-1) : "";
					String deprtments = planInnerList.get(14).length() > 1 ? planInnerList.get(14).substring(1, planInnerList.get(14).length()-1) : "";
					f_service = services.split(",");
//					System.out.println("skills===> "+skills);
					StringBuilder totExp = new StringBuilder();
					String temptotExp[] = planInnerList.get(3).split(":");
					totExp.append(temptotExp[0]);
					if(temptotExp.length > 1) {
						totExp.append("."+temptotExp[1]);
					}
					
					StringBuilder presentOrgExp = new StringBuilder();
					String temppresentOrgExp[] = planInnerList.get(4).split(":");
					presentOrgExp.append(temppresentOrgExp[0]);
					if(temppresentOrgExp.length > 1) {
						presentOrgExp.append("."+temppresentOrgExp[1]);
					}
					
					sbQuery.append("select * from(Select * from ( Select * from ( Select * from ( Select * from employee_personal_details epd, employee_official_details eod WHERE epd.is_alive = ? and epd.emp_per_id=eod.emp_id and approved_flag = ? and remove_from_successionplan = 0 " );
					
					 	if(allAboveQualiIds != null && !allAboveQualiIds.equals("")) {
							sbQuery.append(" and epd.emp_per_id in (select emp_id from education_details where education_id in ("+allAboveQualiIds+")) ");
						}
					 
					 	if(allAboveLevelIds != null && !allAboveLevelIds.equals("")) {
			                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+allAboveLevelIds+") ) ");
			            }
					 	
			            if(deprtments!=null && !deprtments.equals("")){
			                sbQuery.append(" and depart_id in ("+deprtments+") ");
			            }
			            
			            if(services != null && !services.equals("")){
			                sbQuery.append(" and (");
			                for(int ii=0; ii<getF_service().length; ii++){
			                    sbQuery.append(" eod.service_id like '%,"+getF_service()[ii]+",%'");
			                    
			                    if(ii<getF_service().length-1){
			                        sbQuery.append(" OR "); 
			                    }
			                }
			                sbQuery.append(" ) ");
			                
			            } 
					 	
		            if(locations != null && !locations.equals("")) {
		                sbQuery.append(" and wlocation_id in ("+locations+") ");
		            }	            	
		            
					/*if(planInnerList.get(0) != null && !planInnerList.get(0).equals("") && uF.parseToInt(planInnerList.get(0)) > 0){
						sbQuery.append(" and grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details " +
								"where designation_id in ("+ planInnerList.get(0) +"))) ");
					}*/
					sbQuery.append(" order by empcode, emp_status, emp_fname,emp_lname) ast ) aco ) aud ) ass");
					pst = con.prepareStatement(sbQuery.toString());
					pst.setBoolean(1, true);
					pst.setBoolean(2, true);
//					System.out.println("pst  =======>"+pst);
					rs = pst.executeQuery();
					String strEmpId = null;
						Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
						Map<String, String> hmEmpCodeDesigId = CF.getEmpDesigMapId(con);
						Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap(con);
						Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
						Map<String, String> hmDeptMap = CF.getDeptMap(con);
						Map<String, List<String>> hmEmpwiseData = new HashMap<String, List<String>>();
						while (rs.next()) {
							
							if(rs.getInt("emp_per_id")<0) {
								continue;
							}
							
							String currDate = uF.getCurrentDate(CF.getStrTimeZone()).toString();
							String curTimeDur = "0:0";
							if(rs.getString("joining_date") != null) {
								curTimeDur = getTimeDurationBetweenDates(rs.getString("joining_date"), DBDATE, currDate, DBDATE, uF, true, true, true);
							}
							StringBuilder curOrgExp = new StringBuilder();
							String tempcurOrgExp[] = curTimeDur.split(":");
							curOrgExp.append(tempcurOrgExp[0]);
							if(tempcurOrgExp.length > 1){
								curOrgExp.append("."+tempcurOrgExp[1]);
							}
							String readiness ="";
							int readinessCnt =0;
							double yrsDiff = 0;
							if(uF.parseToDouble(presentOrgExp.toString()) > uF.parseToDouble(curOrgExp.toString())) {
								readinessCnt++;
								yrsDiff = uF.parseToDouble(presentOrgExp.toString()) - uF.parseToDouble(curOrgExp.toString());
//								readiness = yrsDiff + "years left";
							} else {
//								readiness = "Now";
							}
							
							String prevExp = hmPrevExpEmpwise.get(rs.getInt("emp_per_id"));
							double yrsDiff1 =0;
							if(uF.parseToDouble(totExp.toString()) > uF.parseToDouble(prevExp)) {
								readinessCnt++;
								yrsDiff1 = uF.parseToDouble(totExp.toString()) - uF.parseToDouble(prevExp);
//								readiness = yrsDiff1 + "years left";
							} else {
//								readiness = "Now";
							}
							
							if(readinessCnt > 0 && yrsDiff > yrsDiff1){
								readiness = uF.formatIntoOneDecimal(yrsDiff) + "years left";
							}else if(readinessCnt > 0 && yrsDiff < yrsDiff1){
								readiness = uF.formatIntoOneDecimal(yrsDiff1) + "years left";
							}else if(readinessCnt > 0 && yrsDiff == yrsDiff1){
								readiness = uF.formatIntoOneDecimal(yrsDiff) + "years left";
							} else {
								readiness = "Now";
							}
							
							List<String> alInner = new ArrayList<String>();
							strEmpId = rs.getString("emp_per_id");
							alInner.add(rs.getString("emp_per_id"));
							alInner.add(rs.getString("empcode"));
							alInner.add(rs.getString("emp_fname"));
							
							String strEmpMName = "";
							if(flagMiddleName) {
								if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
									strEmpMName = " "+rs.getString("emp_mname");
								}
							}
							
							alInner.add(strEmpMName);
							alInner.add(rs.getString("emp_lname"));
							
							alInner.add(uF.showData(hmEmpCodeDesig.get(strEmpId),""));
							alInner.add(uF.showData(hmDeptMap.get(hmEmpDepartment.get(strEmpId)), ""));
//							alInner.add(uF.showData(rs.getString("emp_status"), ""));
							alInner.add(uF.showData(hmEmpCodeDesigId.get(strEmpId),""));
//							alInner.add(uF.showData(uF.stringMapping(rs.getString("emptype")), ""));
							alInner.add(uF.showData(hmEmpLevel.get(strEmpId),""));
							alInner.add(uF.showData(readiness, ""));
							
							double prevEmpMentAvgExp = rs.getDouble("previous_empment_avg_exp");
							
							/*String currDate = uF.getCurrentDate(CF.getStrTimeZone()).toString();
							String curTimeDur = "0:0";
							if(rs.getString("joining_date") != null) {
								curTimeDur = getTimeDurationBetweenDates(rs.getString("joining_date"), DBDATE, currDate, DBDATE, uF, true, true, true);
							}
							StringBuilder curOrgExp = new StringBuilder();
							String tempcurOrgExp[] = curTimeDur.split(":");
							curOrgExp.append(tempcurOrgExp[0]);
							if(tempcurOrgExp.length > 1){
								curOrgExp.append("."+tempcurOrgExp[1]);
							}*/
							
							double percentOfRetentionRisk = uF.parseToDouble(curOrgExp.toString()) / prevEmpMentAvgExp * 100;
							alInner.add(uF.showData(""+percentOfRetentionRisk,""));
							String promotionStatus = getEmpLastPromotion(uF, rs.getString("emp_per_id")); 
//							System.out.println(rs.getString("emp_per_id")+" ===> "+promotionStatus);
							alInner.add(promotionStatus);
							
							hmEmpwiseData.put(rs.getString("emp_per_id"), alInner);
						}	
						rs.close();
						pst.close();
						hmDesignationwiseEmp.put(planInnerList.get(0), hmEmpwiseData);
				}
			}
//			System.out.println("hmDesignationwiseEmp ===> " + hmDesignationwiseEmp);
			request.setAttribute("hmDesignationwiseEmp", hmDesignationwiseEmp);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private String getAboveLevels(Connection con, String desigId, String levelBelow) {

		PreparedStatement pst = null;
		ResultSet rs=null;
		UtilityFunctions uF = new UtilityFunctions();
		StringBuilder sbLevelIds = null;
		try {
			pst = con.prepareStatement("select weightage from designation_details dd, level_details ld where dd.level_id = ld.level_id and dd.designation_id=?");
			pst.setInt(1, uF.parseToInt(desigId));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			String levelWeight = "";
			while (rs.next()) {
				levelWeight = rs.getString("weightage");
			}
			rs.close();
			pst.close();
			
			int belowWeight = uF.parseToInt(levelWeight) - uF.parseToInt(levelBelow);
			
			if(belowWeight < 0) {
				belowWeight = 0;
			}
			
			pst = con.prepareStatement("select level_id from level_details where weightage >= ?");
			pst.setInt(1, belowWeight);
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while (rs.next()) {
				if(sbLevelIds == null) {
					sbLevelIds = new StringBuilder();
					sbLevelIds.append(rs.getString("level_id"));
				} else {
					sbLevelIds.append(","+rs.getString("level_id"));
				}
			}
			if(sbLevelIds == null) {
				sbLevelIds = new StringBuilder();
			}
			rs.close();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbLevelIds.toString();
	}


	private String getAboveQualifications(Connection con, String qualiWeight) {

		PreparedStatement pst = null;
		ResultSet rs=null;
		UtilityFunctions uF = new UtilityFunctions();
		StringBuilder sbEduIds = null;
		try {
			pst = con.prepareStatement("select edu_id from educational_details where weightage >= ? ");
			pst.setInt(1, uF.parseToInt(qualiWeight));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " + pst);
			while (rs.next()) {
				if(sbEduIds == null) {
					sbEduIds = new StringBuilder();
					sbEduIds.append(rs.getString("edu_id"));
				} else {
					sbEduIds.append(","+rs.getString("edu_id"));
				}
			}
			if(sbEduIds == null) {
				sbEduIds = new StringBuilder();
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return sbEduIds.toString();
	}


	public void getPlanStatus() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			Map<String, List<String>> hmPlanStatus = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select plan_status,emp_id,status_color from plan_status_details order by emp_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("plan_status"));
				innerList.add(rs.getString("status_color"));
				hmPlanStatus.put(rs.getString("emp_id"), innerList);
			}	
			rs.close();
			pst.close();
			request.setAttribute("hmPlanStatus", hmPlanStatus);

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
	
	
	public void selectSkills() {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("SELECT * FROM skills_description ORDER BY skills_id");
//			pst.setInt(1, EmpId);
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmSkillValue = new HashMap<String, List<List<String>>>();
			List<List<String>> alSkills = new ArrayList<List<String>>();
			while (rs.next()) {
				alSkills = hmSkillValue.get(rs.getString("emp_id"));
				if(alSkills == null) alSkills = new ArrayList<List<String>>();
				
				List<String> alInner1 = new ArrayList<String>();
				alInner1.add(rs.getInt("skills_id") + "");
				alInner1.add(CF.getSkillNameBySkillId(con, rs.getString("skill_id")));
				alInner1.add(rs.getString("skills_value"));
				alSkills.add(alInner1);
				hmSkillValue.put(rs.getString("emp_id"), alSkills);
			}	
			rs.close();
			pst.close();
//			System.out.println("hmSkillValue in fun =======> " + hmSkillValue);
			request.setAttribute("hmSkillValue", hmSkillValue);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " + e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
private void getLastReviewRating() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmLastReviewRating = new HashMap<String, String>();

			pst = con.prepareStatement("select *,(marks*100/weightage) as average from (select sum(marks) as marks, sum(weightage) as weightage,aqa.appraisal_id,aqa.emp_id from appraisal_question_answer aqa,(" +
					"select a.*,aqa.appraisal_id from appraisal_question_answer aqa,(select max(appraisal_question_answer_id) as aqai,emp_id from appraisal_question_answer group by emp_id) as a where " +
					"a.aqai=aqa.appraisal_question_answer_id and a.emp_id=aqa.emp_id) as a where a.appraisal_id=aqa.appraisal_id and a.emp_id=aqa.emp_id and weightage>0 group by aqa.emp_id,aqa.appraisal_id order by emp_id" +
					") as aaa"); //and aqw.appraisal_attribute = ?
			rs = pst.executeQuery();
				while (rs.next()) {
					hmLastReviewRating.put(rs.getString("emp_id"),uF.showData(rs.getString("average"), "0"));
				}	
				rs.close();
				pst.close();
//				System.out.println("hmLastReviewRating ===> " + hmLastReviewRating);
			request.setAttribute("hmLastReviewRating", hmLastReviewRating);
			
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}



private void getAttributes() {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			Map<String, String> hmScoreAggregateMap = new HashMap<String, String>();

			pst = con.prepareStatement("select *,(marks*100/weightage) as average from (select sum(marks) as marks, sum(weightage) as weightage," +
					"appraisal_element,emp_id from (select aqa.*,appraisal_element from appraisal_question_answer aqa,appraisal_element_attribute aea " +
					"where aqa.appraisal_attribute=aea.appraisal_attribute) as a where weightage>0 group by appraisal_element,emp_id) as aa"); //and aqw.appraisal_attribute = ?
//			pst.setInt(1, uF.parseToInt(empId));
//			pst.setInt(2, uF.parseToInt(appraisalAttributeList.get(i)));
			rs = pst.executeQuery();
//			System.out.println("pst ===> "+pst);
				while (rs.next()) {
//					totAttributeAvg += uF.parseToDouble(uF.showData(rs.getString("average"), "0"));
					hmScoreAggregateMap.put(rs.getString("emp_id")+"_"+rs.getString("appraisal_element"),uF.showData(rs.getString("average"), "0"));
				}	
				rs.close();
				pst.close();
//			request.setAttribute("hmElementAttribute", hmElementAttribute);
			request.setAttribute("hmScoreAggregateMap", hmScoreAggregateMap);
			
		} catch (Exception e) {
			e.printStackTrace();
		}  finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	} 
	
	public String viewEmployee(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			List<List<String>> empDataList = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			Map<String, List<String>> hm = new HashMap<String, List<String>>();
			con = db.makeConnection(con); 
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map hmWorkLocationMap = CF.getWorkLocationMap(con); 
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select empcode,emp_fname,emp_mname,emp_lname,emp_per_id,wlocation_id,emp_status,emptype,emp_off_id,joining_date," +
					"previous_empment_avg_exp from employee_official_details eod, employee_personal_details epd where epd.emp_per_id=eod.emp_id " );
			if(designation != null && !designation.equals("") && uF.parseToInt(designation) > 0){
				sbQuery.append("and grade_id in(SELECT grade_id FROM grades_details where designation_id in(SELECT designation_id FROM designation_details " +
						"where designation_id in ("+ getDesignation() +")))");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=======>"+pst);
			rs = pst.executeQuery();
			String strEmpId = null;
				Map<String, String> hmEmpCodeDesig = CF.getEmpDesigMap(con);
				Map<String, String> hmEmpCodeDesigId = CF.getEmpDesigMapId(con);
				Map<String, String> hmEmpDepartment = CF.getEmpDepartmentMap(con);
				Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
				Map<String, String> hmDeptMap = CF.getDeptMap(con);
				
				while (rs.next()) {
					
					if(rs.getInt("emp_per_id")<0){
						continue;
					}
					alInner = new ArrayList<String>();
					strEmpId = rs.getString("emp_per_id");
					alInner.add(rs.getString("emp_per_id"));
					alInner.add(rs.getString("empcode"));
					alInner.add(rs.getString("emp_fname"));
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					alInner.add(strEmpMName);
					alInner.add(rs.getString("emp_lname"));
					
					Map<String, String> hmWLocation = (Map)hmWorkLocationMap.get(rs.getString("wlocation_id"));
					if(hmWLocation==null)hmWLocation=new HashMap<String, String>();
					
					alInner.add(uF.showData(hmEmpCodeDesig.get(strEmpId),""));
					alInner.add(uF.showData(hmDeptMap.get(hmEmpDepartment.get(strEmpId)), ""));
					alInner.add(uF.showData(hmEmpCodeDesigId.get(strEmpId),""));
					alInner.add(uF.showData(hmEmpLevel.get(strEmpId),""));
					
					
					double prevEmpMentAvgExp = rs.getDouble("previous_empment_avg_exp");
					
					String currDate = uF.getCurrentDate(CF.getStrTimeZone()).toString();
					String curTimeDur = "0:0";
					if(rs.getString("joining_date") != null) {
						curTimeDur = getTimeDurationBetweenDates(rs.getString("joining_date"), DBDATE, currDate, DBDATE, uF, true, true, true);
					}
					StringBuilder curOrgExp = new StringBuilder();
					String tempcurOrgExp[] = curTimeDur.split(":");
					curOrgExp.append(tempcurOrgExp[0]);
					if(tempcurOrgExp.length > 1){
						curOrgExp.append("."+tempcurOrgExp[1]);
					}
					
					double percentOfRetentionRisk = uF.parseToDouble(curOrgExp.toString()) / prevEmpMentAvgExp * 100;
					alInner.add(uF.showData(""+percentOfRetentionRisk,""));
					String promotionStatus = getEmpLastPromotion(uF,rs.getString("emp_per_id")); 
//					System.out.println(rs.getString("emp_per_id")+" ===> "+promotionStatus);
					alInner.add(promotionStatus);
					
//					log.debug(rs.getString("wlocation_id")+" hmWorkLocationMap=="+hmWorkLocationMap);
//					log.debug("hmWLocation=="+hmWLocation);
					
					String wlocation = hmWLocation.get("WL_NAME");
					
					alInner.add(uF.showData(wlocation, ""));
					alInner.add(uF.showData(rs.getString("emp_status"), ""));
					alInner.add(uF.showData(uF.stringMapping(rs.getString("emptype")), ""));
					
					/*if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER)) ){
						alInner.add("<a class=\"factsheet\" href=\"MyProfile.action?empId=" + strEmpId + "\" > </a>");
					}else{
						alInner.add("");
					}*/
					empDataList.add(alInner);
					hm.put(rs.getString("emp_off_id"), alInner);
				}	
				rs.close();
				pst.close();

//			log.debug(empDataList.size()+" al===>"+empDataList);
			
			request.setAttribute("empDataList", empDataList);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("DESIG");
		if(getDesignation()!=null) {
			String strDesig="";
			int k=0;
			for(int i=0; desigList!=null && i<desigList.size();i++) {
				if(getDesignation().equals(desigList.get(i).getDesigId())) {
					strDesig = desigList.get(i).getDesigCodeName();
				}
			}
			if(strDesig!=null && !strDesig.equals("")) {
				hmFilter.put("DESIG", strDesig);
			} else {
				hmFilter.put("DESIG", "All Designations");
			}
		} else {
			hmFilter.put("DESIG", "All Designations");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getDesignation() {
		return designation;
	}

	public void setDesignation(String designation) {
		this.designation = designation;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getDesigId() {
		return desigId;
	}

	public void setDesigId(String desigId) {
		this.desigId = desigId;
	}


	public String getOperation() {
		return operation;
	}


	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getPlanStatusName() {
		return planStatusName;
	}

	public void setPlanStatusName(String planStatusName) {
		this.planStatusName = planStatusName;
	}


	
}
