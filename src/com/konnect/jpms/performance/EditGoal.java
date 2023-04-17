package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EditGoal extends ActionSupport  implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;
  
	private String clevel;
	private String cgrade;
	private String cemp;
	private String strDesignationUpdate;
	
	private String month;

	private String strGoalOrgId;
	private String strGoalEmpId;
	private String empselected;
	
	private String priority;
	private String fromPage;
	private String goalTitle;
	private String dataType;
	private String currUserType;
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		//System.out.println("edit goal goalTitle==>"+goalTitle);
		if (CF==null){
			return LOGIN;
		}
		
		editGoal();	
		if(getFromPage() != null && getFromPage().equals("GoalKRA")) {
			return "GKSUCCESS";
		} else if(getFromPage() != null && getFromPage().equals("GoalTarget")) {
			return "GTSUCCESS";
		} else {
			return "success";
		}

	}
	
	
	private void editGoal() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		
		String strOrg = request.getParameter("strOrg");
		String hideOrgid = request.getParameter("hideOrgid");
		if(uF.parseToInt(strOrg) > 0) {
			setStrGoalOrgId(strOrg);
		} else {
			setStrGoalOrgId(hideOrgid);
		}
		String corporateGoal = request.getParameter("corporateGoal");
		String cgoalObjective = request.getParameter("cgoalObjective");
		String cgoalDescription = request.getParameter("cgoalDescription");
		String cgoalAlignAttribute = request.getParameter("cgoalAlignAttribute");
		String goalElements = request.getParameter("goalElements");
		String cmeasurewith = request.getParameter("cmeasurewith");
		String cmeasureDollar = request.getParameter("cmeasureDollar");
		String cmeasureEffortsDays = request.getParameter("cmeasureEffortsDays");
		String cmeasureEffortsHrs = request.getParameter("cmeasureEffortsHrs");
		String cmeasureKra = request.getParameter("cmeasureKra");
		String cAddMKra = request.getParameter("cAddMKra");
//		String[] cKRA = request.getParameterValues("cKRA");
//		String[] cKRAId = request.getParameterValues("cKRAId");
		String cmkwith = request.getParameter("cmkwith");
//		String cmeasurekraDollar = request.getParameter("cmeasurekraDollar");
//		String cmeasurekraEffortsDays = request.getParameter("cmeasurekraEffortsDays");
//		String cmeasurekraEffortsHrs = request.getParameter("cmeasurekraEffortsHrs");
		String cgoalDueDate = request.getParameter("cgoalDueDate");
		String cgoalEffectDate = request.getParameter("cgoalEffectDate");
		String cgoalFeedback = request.getParameter("cgoalFeedback");
		String corientation = request.getParameter("corientation");
		String cgoalWeightage = request.getParameter("cgoalWeightage");
		String strDepart = request.getParameter("strDepart");
		
//		String clevel = request.getParameter("clevel");
//		String cgrade = request.getParameter("cgrade");
//		String cemp = request.getParameter("cemp");
		String cKRACount=request.getParameter("cKRACount");
		String goal_id=request.getParameter("goal_id");
		String goaltype=request.getParameter("goaltype");
		String goal_parent_id=request.getParameter("goal_parent_id");
		
		String frequency = request.getParameter("frequency");
		String strYearType = request.getParameter("strYearType");
		String[] strMonths = request.getParameterValues("strMonths");
		String[] strQuarters = request.getParameterValues("strQuarters");
		String[] strHalfYears = request.getParameterValues("strHalfYears");
		String[] strYears = request.getParameterValues("strYears");
		
		
//		String weekday=request.getParameter("weekday");
//		String annualDay=request.getParameter("annualDay");
//		String annualMonth=request.getParameter("annualMonth");
//		String day=request.getParameter("day");
//		String monthday=request.getParameter("monthday");
		//String month=request.getParameter("month");
				
		String chkWithDepart = request.getParameter("chkWithDepart");
		String chkWithTeam = request.getParameter("chkWithTeam");
		
//		System.out.println("chkWithDepart ===>> " + chkWithDepart);
//		System.out.println("chkWithTeam ===>> " + chkWithTeam);
		
		String strWithDepartAndTeam = null;
		if (goaltype.equals("1")) {
			strWithDepartAndTeam = "0,0";
			if(uF.parseToBoolean(chkWithDepart) && uF.parseToBoolean(chkWithTeam)) {
				strWithDepartAndTeam = "1,1";
			}
			if(uF.parseToBoolean(chkWithDepart) && !uF.parseToBoolean(chkWithTeam)) {
				strWithDepartAndTeam = "1,0";
			}
			if(!uF.parseToBoolean(chkWithDepart) && uF.parseToBoolean(chkWithTeam)) {
				strWithDepartAndTeam = "0,1";
			}
		}
		
		String cMeasureDesc = request.getParameter("cMeasureDesc");
		String cMeasureVal = request.getParameter("cMeasureVal");
		String teamgoalRedio = request.getParameter("teamgoalRedio");
		String responsibleEmpID = request.getParameter("teamEmpID");
		
		try {
			con = db.makeConnection(con);
			
			
			String firstMonth = null;
			String firstQuarter = null;
			String firstHalf = null;
			String firstRecurrYear = null;
			if( frequency == null || frequency.equals("")) {
				frequency = "1";
			}
			
			StringBuilder recurringYears = null;
			if(strYears!=null) {
				for(int i=0; i<strYears.length; i++) {
					if(recurringYears==null) {
						recurringYears = new StringBuilder();
						recurringYears.append(","+strYears[i]+",");
						firstRecurrYear = strYears[i];
					} else {
						recurringYears.append(strYears[i]+",");
					}
				}
			}
			
			if(recurringYears==null) {
				recurringYears = new StringBuilder();
			}
			
			StringBuilder frequencyMonths = null;
//			String weeklyDay = null;
			/*if (frequency != null && frequency.equals("2")) {
				weeklyDay = weekday;
				frequency_day = null;
				frequency_month = null;
			} else */
			if (frequency != null && frequency.equals("3")) {
				if(strMonths!=null) {
					for(int i=0; i<strMonths.length; i++) {
						if(frequencyMonths==null) {
							frequencyMonths = new StringBuilder();
							frequencyMonths.append(","+strMonths[i]+",");
							firstMonth = strMonths[i];
						} else {
							frequencyMonths.append(strMonths[i]+",");
						}
					}
				}
				String strDt = "01/"+ (uF.parseToInt(firstMonth)<10 ? "0"+firstMonth : firstMonth) +"/"+firstRecurrYear;
				String strMinMaxDates = uF.getCurrentMonthMinMaxDate(strDt, DATE_FORMAT);
				String[] tmpMinMaxDt = strMinMaxDates.split("::::");
				cgoalEffectDate = tmpMinMaxDt[0];
				cgoalDueDate = tmpMinMaxDt[1];
				
//				weeklyDay = null;
//				frequency_day = day;
//				frequency_month = null;
			} else if (frequency != null && frequency.equals("4")) {
				if(strQuarters!=null) {
					for(int i=0; i<strQuarters.length; i++) {
						if(frequencyMonths==null) {
							frequencyMonths = new StringBuilder();
							frequencyMonths.append(","+strQuarters[i]+",");
							firstQuarter = strQuarters[i];
						} else {
							frequencyMonths.append(strQuarters[i]+",");
						}
					}
				}
				int intFirstRecurrYear = uF.parseToInt(firstRecurrYear);
				if(uF.parseToInt(strYearType)==2 && uF.parseToInt(firstQuarter)==4) {
					intFirstRecurrYear = intFirstRecurrYear+1;
				}
				List<String> alMonths = uF.getQuarterFirstAndLastMonth(uF.parseToInt(firstQuarter), uF.parseToInt(strYearType));
				cgoalEffectDate = "01/"+ (uF.parseToInt(alMonths.get(0))<10 ? "0"+alMonths.get(0) : alMonths.get(0)) +"/"+intFirstRecurrYear;
				
				String strDt = "01/"+ (uF.parseToInt(alMonths.get(1))<10 ? "0"+alMonths.get(1) : alMonths.get(1)) +"/"+intFirstRecurrYear;
				String strMinMaxDates = uF.getCurrentMonthMinMaxDate(strDt, DATE_FORMAT);
				String[] tmpMinMaxDt = strMinMaxDates.split("::::");
				cgoalDueDate = tmpMinMaxDt[1];

//				weeklyDay = null;
//				frequency_day = monthday;
//				frequency_month = month;
			} else if (frequency != null && frequency.equals("5")) {
				if(strHalfYears!=null) {
					for(int i=0; i<strHalfYears.length; i++) {
						if(frequencyMonths==null) {
							frequencyMonths = new StringBuilder();
							frequencyMonths.append(","+strHalfYears[i]+",");
							firstHalf = strHalfYears[i];
						} else {
							frequencyMonths.append(strHalfYears[i]+",");
						}
					}
				}
				int intFirstRecurrYear = uF.parseToInt(firstRecurrYear);
				if(uF.parseToInt(strYearType)==2 && uF.parseToInt(firstHalf)==2) {
					intFirstRecurrYear = intFirstRecurrYear+1;
				}
				List<String> alMonths = uF.getHalfYearFirstAndLastMonth(uF.parseToInt(firstHalf), uF.parseToInt(strYearType));
				cgoalEffectDate = "01/"+ (uF.parseToInt(alMonths.get(0))<10 ? "0"+alMonths.get(0) : alMonths.get(0)) +"/"+firstRecurrYear;
				
				String strDt = "01/"+ (uF.parseToInt(alMonths.get(1))<10 ? "0"+alMonths.get(1) : alMonths.get(1)) +"/"+intFirstRecurrYear;
				String strMinMaxDates = uF.getCurrentMonthMinMaxDate(strDt, DATE_FORMAT);
				String[] tmpMinMaxDt = strMinMaxDates.split("::::");
				cgoalDueDate = tmpMinMaxDt[1];
				
				
//				weeklyDay = null;
//				frequency_day = monthday;
//				frequency_month = month;
			} else if (frequency != null && frequency.equals("6")) {
				int intFirstRecurrYear = uF.parseToInt(firstRecurrYear);
				if(uF.parseToInt(strYearType)==2) {
					intFirstRecurrYear = intFirstRecurrYear+1;
					cgoalEffectDate = "01/04/"+firstRecurrYear;
					cgoalDueDate = "31/03/"+intFirstRecurrYear;
				} else {
					cgoalEffectDate = "01/01/"+firstRecurrYear;
					cgoalDueDate = "31/12/"+firstRecurrYear;
				}
				
//				weeklyDay = null;
//				frequency_day = annualDay;
//				frequency_month = annualMonth;
			}
			
			if(frequencyMonths==null) {
				frequencyMonths = new StringBuilder();
			}
			
			/*if( frequency == null || frequency.equals("")) {
				frequency = "1";
			}
			String frequency_day=null;
			String frequency_month=null;
			String weeklyDay=null;
			if(frequency!=null && frequency.equals("2")){ 
				weeklyDay=weekday;
				frequency_day=null;
				frequency_month=null;
			}else if(frequency!=null && frequency.equals("3")){
				weeklyDay=null;
				frequency_day=day;
				frequency_month=null;
			}else if(frequency!=null && frequency.equals("4")){
				weeklyDay=null;
				frequency_day=monthday;
				frequency_month=month;
			}else if(frequency!=null && frequency.equals("5")){
				weeklyDay=null;
				frequency_day=monthday;
				frequency_month=month;
			}else if(frequency!=null && frequency.equals("6")){
				weeklyDay=null;
				frequency_day=annualDay;
				frequency_month=annualMonth;
			}*/
			
			
			if(getEmpselected()!=null && !getEmpselected().equals("") && !getEmpselected().equals("0")){
				List<String> tmpselectEmpList=Arrays.asList(getEmpselected().split(","));
				Set<String> empSet = new HashSet<String>(tmpselectEmpList);
				Iterator<String> itr = empSet.iterator();
				int i=0;
				String empList=null;
				while (itr.hasNext()) {
					String empid = (String) itr.next();
					if(i==0){
						empList=","+empid.trim()+",";
					}else{
						empList+=empid.trim()+",";
					}
					i++;
				}
				setCemp(empList);
				
			}else{
				setCemp(null);
			}
			
//			System.out.println("getStrGoalEmpId======> " + getStrGoalEmpId());
//			if(getStrGoalEmpId()!=null && !getStrGoalEmpId().equals("")){
//				List<String> tmpselectEmpList=Arrays.asList(getStrGoalEmpId().split(","));
//				Set<String> empSet = new HashSet<String>(tmpselectEmpList);
//				Iterator<String> itr = empSet.iterator();
//				int i=0;
//				String empList=null;
//				while (itr.hasNext()) {
//					String empid = (String) itr.next();
//					if(i==0){
//						empList=","+empid.trim()+",";
//					}else{
//						empList+=empid.trim()+",";
//					}
//					i++;
//				}
//				setCemp(empList);
//				System.out.println("empList======> " + getCemp());
//			}else{
//				setCemp(null);
//			}
			String goalCreaterID = "";
			if(teamgoalRedio != null && teamgoalRedio.equals("manager")){
				goalCreaterID = request.getParameter("managers");
			}else if(teamgoalRedio != null && teamgoalRedio.equals("self")){
				goalCreaterID = strSessionEmpId;
			}
			
			StringBuilder strQuery = new StringBuilder();
			strQuery.append("select * from goal_details where goal_id = ?");
			pst = con.prepareStatement(strQuery.toString());
			pst.setInt(1, uF.parseToInt(goal_id));
//			System.out.println("\npst1 in proId====>"+pst1);
			rst = pst.executeQuery();
			List<String> innerList = new ArrayList<String>();
			while (rst.next()) {
				innerList.add(uF.getDateFormat(rst.getString("effective_date"), DBTIMESTAMP, DATE_FORMAT));//0
				innerList.add(uF.getDateFormat(rst.getString("due_date"), DBDATE, DATE_FORMAT));//1
				innerList.add(rst.getString("frequency"));//2
				innerList.add(rst.getString("user_id"));//3
				innerList.add(rst.getString("frequency_day")); //4
				innerList.add(rst.getString("weekday"));//5
			}
			rst.close();
			pst.close();
			
			if (goaltype.equals(INDIVIDUAL_GOAL+"") || goaltype.equals(INDIVIDUAL_KRA+"") || goaltype.equals(INDIVIDUAL_TARGET+"")) {
				pst = con.prepareStatement("update goal_details set goal_type=?,goal_parent_id=?,goal_title=?,goal_objective=?," +
					"goal_description=?,goal_attribute=?,measure_type=?,measure_currency_value=?,measure_currency_id=?," +
					"measure_effort_days=?,measure_effort_hrs=?,due_date=?,is_feedback=?,orientation_id=?,weightage=?,emp_ids=?," +
					"level_id=?,grade_id=?,is_measure_kra=?,measure_kra=?,measure_type1=?,measure_currency_value1=?," +
					"measure_kra_days=?,measure_kra_hrs=?,frequency=?,freq_year_type=?,recurring_years=?,frequency_month=?,priority=?," +
					"measure_desc=?,measure_val=?,effective_date=?,goal_creater_id=?,goal_creater_type=?,responsible_emp_id=?," +
					"goal_element=?,org_id=?,depart_id=? where goal_id=?");
				pst.setInt(1, uF.parseToInt(goaltype));
				pst.setInt(2, uF.parseToInt(goal_parent_id));
				pst.setString(3, corporateGoal);
				pst.setString(4, cgoalObjective);  
				pst.setString(5, cgoalDescription);
				pst.setInt(6, uF.parseToInt(cgoalAlignAttribute));
			//	pst.setString(7, cAddMKra != null && cAddMKra.equals("Measure") ? cmeasurewith : "");
				if(cAddMKra != null && cAddMKra.equals("Measure")){
					pst.setString(7, cmeasurewith );
				}else{
					pst.setString(7, "" );
				}
				if((cmeasurewith != null)&& (cmeasurewith.equals("Value") || cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage"))){
					pst.setDouble(8,uF.parseToDouble(cmeasureDollar));
				}else{
					pst.setDouble(8,0.0);
				}
				//pst.setDouble(8,uF.parseToDouble((cmeasurewith.equals("Value") || cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage")) ? cmeasureDollar : "0"));
				pst.setInt(9, 3);
				if(cmeasurewith!=null && cmeasurewith.equals("Effort")){
					pst.setDouble(10, uF.parseToDouble(cmeasureEffortsDays ));
				}else{
					pst.setDouble(10, 0.0);
				}
				
				if(cmeasurewith!=null && cmeasurewith.equals("Effort")){
					pst.setDouble(11, uF.parseToDouble(cmeasureEffortsHrs ));
				}else{
					pst.setDouble(11, 0.0);
				}
				
				
				//pst.setDouble(11, uF.parseToDouble(cmeasurewith.equals("Effort") ? cmeasureEffortsHrs : "0"));
				pst.setDate(12, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
				pst.setBoolean(13, uF.parseToBoolean(cgoalFeedback));
				pst.setInt(14, uF.parseToInt(corientation));
				pst.setDouble(15, uF.parseToDouble(cgoalWeightage));
				pst.setString(16, cemp);
				pst.setString(17, clevel);
				pst.setString(18, strDesignationUpdate);
				pst.setBoolean(19, uF.parseToBoolean(cmeasureKra));
				pst.setString(20, cAddMKra);
				pst.setString(21, cmkwith);
				pst.setDouble(22,uF.parseToDouble("0"));
				pst.setDouble(23, uF.parseToDouble("0"));
				pst.setDouble(24, uF.parseToDouble("0"));
				
				pst.setInt(25,uF.parseToInt(frequency));
				pst.setInt(26, uF.parseToInt(strYearType));
				pst.setString(27, recurringYears.toString());
				pst.setString(28, frequencyMonths.toString());
				pst.setInt(29, uF.parseToInt(getPriority()));
				pst.setString(30, cMeasureDesc);
				pst.setDouble(31, uF.parseToDouble(cMeasureVal));
				pst.setDate(32, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
				pst.setInt(33, uF.parseToInt(goalCreaterID));
				pst.setString(34, teamgoalRedio);
				pst.setInt(35, uF.parseToInt(responsibleEmpID));
				pst.setInt(36, uF.parseToInt(goalElements));
				
				if(strOrg != null && !strOrg.equals("")){
					pst.setInt(37, uF.parseToInt(strOrg));
				}else{
					pst.setInt(37, uF.parseToInt(hideOrgid));
				}
				pst.setInt(38, uF.parseToInt(strDepart));
				//pst.setInt(37, uF.parseToInt(strOrg != null && !strOrg.equals("") ? strOrg : hideOrgid));
				pst.setInt(39, uF.parseToInt(goal_id));
				pst.execute();
				pst.close();
				
			
				
				//updated by kalpana on 18/10/2016
              
//				***************************** Goal Frequency Start ************************************
				GoalScheduler scheduler = new GoalScheduler(request, session, CF, uF, strSessionEmpId);
				
				/*if(uF.parseToInt(innerList.get(2)) != uF.parseToInt(frequency)) {
					scheduler.updateGoalDetails(goal_id);
				} else if(uF.parseToInt(innerList.get(2)) == 2 && uF.parseToInt(frequency) == 2) {  // Weekly
					if(!innerList.get(5).equals(weeklyDay)) {
						scheduler.updateGoalDetails(goal_id);
					} else if(!innerList.get(0).equals(cgoalEffectDate)) { 
						scheduler.updateGoalDetails(goal_id);
					} else if(!innerList.get(1).equals(cgoalDueDate)) {
						scheduler.updateGoalDetails(goal_id);
					}
				} else if(uF.parseToInt(innerList.get(2)) == 3 && uF.parseToInt(frequency) == 3) { // Monthly
					if(!innerList.get(4).equals(frequency_day)) {
						scheduler.updateGoalDetails(goal_id);
					} else if(!innerList.get(0).equals(cgoalEffectDate)) { 
						scheduler.updateGoalDetails(goal_id);
					} else if(!innerList.get(1).equals(cgoalDueDate)) {
						scheduler.updateGoalDetails(goal_id);
					}
				} else if(!innerList.get(0).equals(cgoalEffectDate)) { 
					scheduler.updateGoalDetails(goal_id);
				} else if(!innerList.get(1).equals(cgoalDueDate)) {
					scheduler.updateGoalDetails(goal_id);
				}*/
				
				
//				***************************** Goal Frequency End ************************************

				//updated by kalpana on 18/10/2016
				/**
				 * start
				 */
				String cDeleteKRAIds = request.getParameter("cDeleteKRAIds");
//				System.out.println("cDeleteKRAIds==>"+cDeleteKRAIds);
				if(cDeleteKRAIds.length()>0) {
					pst=con.prepareStatement("delete from goal_kras where goal_kra_id in ("+cDeleteKRAIds+")");
//					System.out.println("pst1==>"+pst);
					pst.execute();
					pst.close();
					
					pst=con.prepareStatement("delete from activity_info where kra_id in ("+cDeleteKRAIds+")");
//					System.out.println("pst2==>"+pst);
					pst.execute();
					pst.close();
					
					
					pst=con.prepareStatement("delete from goal_kra_tasks where kra_id in ("+cDeleteKRAIds+")");
//					System.out.println("pst2==>"+pst);
					pst.execute();
					pst.close();
					
				}
				
				String cDeleteKRATaskIds = request.getParameter("cDeleteKRATaskIds");
//				System.out.println("cDeleteKRATaskIds==>"+cDeleteKRATaskIds);
				if(cDeleteKRATaskIds != null && cDeleteKRATaskIds.length()>0) {
					
					pst=con.prepareStatement("delete from activity_info where goal_kra_task_id in ("+cDeleteKRATaskIds+")");
					pst.execute();
					pst.close();
					
					pst=con.prepareStatement("delete from goal_kra_tasks where goal_kra_task_id in ("+cDeleteKRATaskIds+")");
					pst.execute();
					pst.close();
					
				}
				
				
				
				int ckracountserial =uF.parseToInt(cKRACount);
//				System.out.println("cKRACount === > " + cKRACount);
//				System.out.println("cKRA === > " + cKRA.length);
				for(int i=0; i<=ckracountserial; i++) {
					String cKRA = request.getParameter("cKRA_"+i);
					String hideKRAId = request.getParameter("hideKRAId_"+i);
					String cKRAWeightage = request.getParameter("cKRAWeightage_"+i);
					
					if(cKRA != null && !cKRA.equals("")) {
						String[] cKRATask = request.getParameterValues("cKRATask_"+i);
						String[] hideKRATaskId = request.getParameterValues("hideKRATask_"+i);
						int tasksCount = hideKRATaskId.length;
						/*System.out.println("cKRATask==>"+Arrays.asList(cKRATask));
						System.out.println("hideKRATaskId==>"+Arrays.asList(hideKRATaskId));*/
						if(hideKRAId != null && uF.parseToInt(hideKRAId) > 0) {
							pst = con.prepareStatement("update goal_kras set goal_id=?,update_date=?,effective_date=?,is_approved=?,approved_by=?,kra_order=?," +
								"kra_description=?,goal_type=?,element_id=?,attribute_id=?,emp_ids=?,updated_by=?,kra_weightage=? where goal_kra_id=?");
							pst.setInt(1, uF.parseToInt(goal_id));
							pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setDate(3, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
							pst.setBoolean(4, true);
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setInt(6, 0);
							pst.setString(7, cKRA);
							pst.setInt(8, uF.parseToInt(goaltype));
							pst.setInt(9, uF.parseToInt(goalElements));
							pst.setInt(10, uF.parseToInt(cgoalAlignAttribute));
							pst.setString(11, getCemp());
							pst.setInt(12, uF.parseToInt(strSessionEmpId));
							pst.setDouble(13, uF.parseToDouble(cKRAWeightage));
							pst.setInt(14, uF.parseToInt(hideKRAId));
//							System.out.println("pst kra update==>"+ pst);
							pst.execute();
							pst.close();
							
							//updated by kalpana on 18/10/2016
							/**
							 * start
							 */
							for(int j=0; cKRATask != null && j<cKRATask.length; j++) {
								
								 if(getEmpselected()!=null && !getEmpselected().equals("") && !getEmpselected().equals("0") && hideKRATaskId!=null && hideKRATaskId.length > 0 && uF.parseToInt(hideKRATaskId[j]) > 0) {
										
									 	String empIds = ""; 
									    pst = con.prepareStatement("select emp_ids from goal_kra_tasks where goal_kra_task_id = ?");
										
										pst.setInt(1, uF.parseToInt(hideKRATaskId[j]));
										rst = pst.executeQuery();
										while(rst.next()) {
											empIds = rst.getString("emp_ids");
										}
										rst.close();
										pst.close();
										
										pst = con.prepareStatement("update goal_kra_tasks set task_name = ?,emp_ids = ? where goal_kra_task_id = ?");
										pst.setString(1, cKRATask[j]);
										pst.setString(2, getCemp());
										pst.setInt(3, uF.parseToInt(hideKRATaskId[j]));
										pst.executeUpdate();
										pst.close();
										
										List<String> empList = Arrays.asList(empIds.split(","));
										List<String> tmpselectEmpList1 = Arrays.asList(getEmpselected().split(","));
										Set<String> empSet1 = new HashSet<String>(tmpselectEmpList1);
										Iterator<String> itr1 = empSet1.iterator();
										while(itr1.hasNext()) {
											String empId = itr1.next();
											if(empList.contains(empId)) {
												pst = con.prepareStatement("update activity_info set activity_name = ?,priority = ?,start_date = ?,deadline = ?" +
												" where resource_ids like '%,"+empId+",%' and goal_kra_task_id = ?");
													
												pst.setString(1, cKRATask[j]);
												pst.setInt(2, uF.parseToInt(getPriority()));
												pst.setDate(3, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
												pst.setDate(4, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
												pst.setInt(5, uF.parseToInt(hideKRATaskId[j]));
//												System.out.println("update task activity pst==>"+pst);
												pst.executeUpdate();
												pst.close();
											} else {
												pst = con.prepareStatement("insert into activity_info(pro_id,activity_name,priority,start_date,deadline,taskstatus," +
												"task_accept_status,kra_id,resource_ids,entry_date,added_by,goal_kra_task_id) values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
												pst.setInt(1, 0);
												pst.setString(2, cKRATask[j]);
												pst.setInt(3, uF.parseToInt(getPriority()));
												pst.setDate(4, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
												pst.setDate(5, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
												pst.setString(6, "New Task");
												pst.setInt(7, 1);
												pst.setInt(8, uF.parseToInt(hideKRAId));
												pst.setString(9, ","+empId+",");
												pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
												pst.setInt(11, uF.parseToInt(strSessionEmpId));
												pst.setInt(12, uF.parseToInt(hideKRATaskId[j]));
//												System.out.println("insert activity_info pst==>"+pst);
												pst.execute();
												pst.close();
											}
										}
										
																				
								} else {
										
									if(getEmpselected()!=null && !getEmpselected().equals("") && !getEmpselected().equals("0") && cKRATask[j] != null && !cKRATask[j].equals("") && uF.parseToInt(hideKRATaskId[j]) == 0) {
										pst = con.prepareStatement("insert into goal_kra_tasks(goal_id,kra_id,task_name,emp_ids,entry_date,added_by) " +
												"values(?,?,?,?, ?,?)");
										pst.setInt(1, uF.parseToInt(goal_id));
										pst.setInt(2, uF.parseToInt(hideKRAId));
										pst.setString(3, cKRATask[j]);
										pst.setString(4, getCemp());
										pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
										pst.setInt(6, uF.parseToInt(strSessionEmpId));
										pst.execute();
										pst.close();
										
										
										int newKRATaskId = 0;
										pst = con.prepareStatement("select max(goal_kra_task_id) from goal_kra_tasks");
										rst = pst.executeQuery();
										while (rst.next()) {
											newKRATaskId = rst.getInt(1);
										}
										rst.close();
										pst.close();
									
										List<String> tmpselectEmpList = Arrays.asList(getEmpselected().split(","));
										Set<String> empSet = new HashSet<String>(tmpselectEmpList);
										Iterator<String> itr = empSet.iterator();
										while (itr.hasNext()) {
											String empId = itr.next();
											pst = con.prepareStatement("insert into activity_info(pro_id,activity_name,priority,start_date,deadline,taskstatus," +
												"task_accept_status,kra_id,resource_ids,entry_date,added_by,goal_kra_task_id) values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
											pst.setInt(1, 0);
											pst.setString(2, cKRATask[j]);
											pst.setInt(3, uF.parseToInt(getPriority()));
											pst.setDate(4, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
											pst.setDate(5, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
											pst.setString(6, "New Task");
											pst.setInt(7, 1);
											pst.setInt(8, uF.parseToInt(hideKRAId));
											pst.setString(9, ","+empId+",");
											pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
											pst.setInt(11, uF.parseToInt(strSessionEmpId));
											pst.setInt(12, newKRATaskId);
//											System.out.println("insert activity_info pst==>"+pst);
											pst.execute();
											pst.close();
									    }
							        }
								  }
							  }
							/**
							 * end
							 */
							
						} else {
							pst = con.prepareStatement("insert into goal_kras(goal_id,entry_date,effective_date,is_approved,approved_by,kra_order," +
								"kra_description,goal_type,element_id,attribute_id,emp_ids,added_by) values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
							pst.setInt(1, uF.parseToInt(goal_id));
							pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setBoolean(4, true);
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setInt(6, 0);
							pst.setString(7, cKRA);
							pst.setInt(8, uF.parseToInt(goaltype));
							pst.setInt(9, uF.parseToInt(goalElements));
							pst.setInt(10, uF.parseToInt(cgoalAlignAttribute));
							pst.setString(11, getCemp());
							pst.setInt(12, uF.parseToInt(strSessionEmpId));
//							System.out.println("insert pst kra==>"+pst);
							pst.execute();
							pst.close();
							
							int newKRAId = 0;
							pst = con.prepareStatement("select max(goal_kra_id) from goal_kras");
							rst = pst.executeQuery();
							while (rst.next()) {
								newKRAId = rst.getInt(1);
							}
							rst.close();
							pst.close();
							
							for(int j=0; cKRATask != null && j<cKRATask.length; j++) {
								if(getEmpselected()!=null && !getEmpselected().equals("") && !getEmpselected().equals("0") && cKRATask[j] != null && !cKRATask[j].equals("")) {
									pst = con.prepareStatement("insert into goal_kra_tasks(goal_id,kra_id,task_name,emp_ids,entry_date,added_by) " +
										"values(?,?,?,?, ?,?)");
									pst.setInt(1, uF.parseToInt(goal_id));
									pst.setInt(2, newKRAId);
									pst.setString(3, cKRATask[j]);
									pst.setString(4, getCemp());
									pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setInt(6, uF.parseToInt(strSessionEmpId));
									pst.execute();
									pst.close();
									
									int newKRATaskId = 0;
									pst = con.prepareStatement("select max(goal_kra_task_id) from goal_kra_tasks");
									rst = pst.executeQuery();
									while (rst.next()) {
										newKRATaskId = rst.getInt(1);
									}
									rst.close();
									pst.close();
								
									List<String> tmpselectEmpList = Arrays.asList(getEmpselected().split(","));
									Set<String> empSet = new HashSet<String>(tmpselectEmpList);
									Iterator<String> itr = empSet.iterator();
									while (itr.hasNext()) {
										String empId = itr.next();
										pst = con.prepareStatement("insert into activity_info(pro_id,activity_name,priority,start_date,deadline,taskstatus," +
											"task_accept_status,kra_id,resource_ids,entry_date,added_by,goal_kra_task_id) values(?,?,?,?, ?,?,?,?, ?,?,?,?)");
										pst.setInt(1, 0);
										pst.setString(2, cKRATask[j]);
										pst.setInt(3, uF.parseToInt(getPriority()));
										pst.setDate(4, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
										pst.setDate(5, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
										pst.setString(6, "New Task");
										pst.setInt(7, 1);
										pst.setInt(8, newKRAId);
										pst.setString(9, ","+empId+",");
										pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
										pst.setInt(11, uF.parseToInt(strSessionEmpId));
										pst.setInt(12, newKRATaskId);
										pst.execute();
										pst.close();
									}
								}
							}
						}
						
//						pst = con.prepareStatement("insert into goal_kras(goal_id,entry_date,effective_date,is_approved,approved_by,kra_order," +
//								"kra_description,goal_type) values(?,?,?,?,?,?,?,?)");
//						pst.setInt(1, uf.parseToInt(goal_id));
//						pst.setDate(2,uf.getCurrentDate(CF.getStrTimeZone()));
//						pst.setDate(3,uf.getCurrentDate(CF.getStrTimeZone()));
//						pst.setBoolean(4,true);
//						pst.setInt(5,uf.parseToInt(strSessionEmpId));
//						pst.setInt(6,0);
//						pst.setString(7,cKRA[i]);
//						pst.setInt(8, uf.parseToInt(goaltype));
//						pst.execute();
//					}
					
						/*if(cKRAId.length > i) {
							pst = con.prepareStatement("update kra_details set kra = ? ,attribute_id=?, level_id=?, measurable=?, goal_id=? where kra_id = ?");
							pst.setString(1, cKRA[i]);
							pst.setInt(2, uf.parseToInt(cgoalAlignAttribute));
							pst.setInt(3, uf.parseToInt("0"));
							pst.setInt(4, 1);
							pst.setInt(5, uf.parseToInt(goal_id));
							pst.setInt(6, uf.parseToInt(cKRAId[i]));
							pst.executeUpdate();
						} else {
							pst = con.prepareStatement("insert into kra_details(kra,attribute_id,level_id,measurable,goal_id)"
									+ "values(?,?,?,?,?)");
							pst.setString(1, cKRA[i]);
							pst.setInt(2, uf.parseToInt(cgoalAlignAttribute));
							pst.setInt(3, uf.parseToInt("0"));
							pst.setInt(4, 1);
							pst.setInt(5, uf.parseToInt(goal_id));
							pst.execute();
						}*/
//					if(getCemp()!=null && !getCemp().equals("")) {
//						String emp=getCemp().substring(1, getCemp().length()-1);
//						List<String> cemplist=Arrays.asList(emp.split(","));
//						Set<String> setCemp=new HashSet<String>(cemplist);
//						Iterator<String> it=setCemp.iterator();
//						List<String> cEmpLEvelList=new ArrayList<String>();
//						Map<String, String> hmEmpLevelMap =CF.getEmpLevelMap();
//						while(it.hasNext()){
//							String val=it.next();
//							String levelID = hmEmpLevelMap.get(val.trim());
//							if(levelID != null){
//								cEmpLEvelList.add(levelID);
//							}
//						}
//						
//						Set<String> setLvlList = new HashSet<String>(cEmpLEvelList);
//						Iterator<String> it1=setLvlList.iterator();
////						Map<String, String> hmEmpLevelMap =CF.getEmpLevelMap();
//						while(it1.hasNext()){
//							String val=it1.next();
////							String levelID = hmEmpLevelMap.get(val.trim());
////							cEmpLEvelList.add(levelID);
//						
//						pst = con.prepareStatement("insert into kra_details(kra,attribute_id,level_id,measurable,goal_id)"
//										+ "values(?,?,?,?,?)");
//						pst.setString(1, cKRA[i]);
//						pst.setInt(2, uf.parseToInt(cgoalAlignAttribute));
//						pst.setInt(3, uf.parseToInt(val.trim()));
//						pst.setInt(4, 1);
//						pst.setInt(5, uf.parseToInt(goal_id));
//						pst.execute();
//						}
//					}
				}
			}/**
				 * end
				 */
				
			} else {
						
				/*pst = con.prepareStatement("update goal_details set goal_type=?,goal_parent_id=?,goal_title=?,goal_objective=?," +
						"goal_description=?,goal_attribute=?,measure_type=?,measure_currency_value=?,measure_currency_id=?,measure_effort_days=?," +
						"measure_effort_hrs=?,due_date=?,is_feedback=?,orientation_id=?,weightage=?,emp_ids=?,level_id=?,grade_id=?," +
						"frequency=?,weekday=?,frequency_day=?,frequency_month=?,priority=?,measure_desc=?,measure_val=?,effective_date=?" +
						",is_measure_kra=?,goal_creater_id=?,goal_creater_type=?,responsible_emp_id=?,goal_element=?,org_id=? where goal_id=?");
				pst.setInt(1, uF.parseToInt(goaltype));
				pst.setInt(2, uF.parseToInt(goal_parent_id));
				pst.setString(3,corporateGoal);
				pst.setString(4,cgoalObjective);
				pst.setString(5,cgoalDescription);
				pst.setInt(6,uF.parseToInt(cgoalAlignAttribute));
				pst.setString(7,cmeasurewith);
				pst.setDouble(8,uF.parseToDouble(((cmeasurewith!=null) && (cmeasurewith.equals("Value") || cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage"))) ? cmeasureDollar:"0"));
				pst.setInt(9,3);
				pst.setDouble(10,uF.parseToDouble((cmeasurewith!=null) && (cmeasurewith.equals("Effort")) ? cmeasureEffortsDays:"0"));
				pst.setDouble(11,uF.parseToDouble((cmeasurewith!=null) && (cmeasurewith.equals("Effort")) ? cmeasureEffortsHrs:"0"));
				pst.setDate(12,uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
				pst.setBoolean(13,uF.parseToBoolean(cgoalFeedback));
				pst.setInt(14,uF.parseToInt(corientation));
				pst.setDouble(15,uF.parseToDouble(cgoalWeightage));
				pst.setString(16,cemp);
				pst.setString(17,clevel);
				pst.setString(18,strDesignationUpdate);
				
				pst.setInt(19,uF.parseToInt(frequency));
				pst.setString(20, weeklyDay);
				pst.setString(21, frequency_day);
				pst.setString(22, frequency_month);
				pst.setInt(23, uF.parseToInt(getPriority()));
				pst.setString(24, cMeasureDesc);
				pst.setDouble(25, uF.parseToDouble(cMeasureVal));
				pst.setDate(26, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
				pst.setBoolean(27, uF.parseToBoolean(cmeasureKra));
				pst.setInt(28, uF.parseToInt(goalCreaterID));
				pst.setString(29, teamgoalRedio);
				pst.setInt(30, uF.parseToInt(responsibleEmpID));
				pst.setInt(31, uF.parseToInt(goalElements));
				pst.setInt(32, uF.parseToInt(strOrg != null && !strOrg.equals("") ? strOrg : hideOrgid));
				pst.setInt(33, uF.parseToInt(goal_id));
				pst.execute();
				pst.close();*/
				
				
				
				pst = con.prepareStatement("update goal_details set goal_type=?,goal_parent_id=?,goal_title=?,goal_objective=?," +
					"goal_description=?,goal_attribute=?,measure_type=?,measure_currency_value=?,measure_currency_id=?," +
					"measure_effort_days=?,measure_effort_hrs=?,due_date=?,is_feedback=?,orientation_id=?,weightage=?,emp_ids=?," +
					"level_id=?,grade_id=?,is_measure_kra=?,measure_kra=?,measure_type1=?,measure_currency_value1=?," +
					"measure_kra_days=?,measure_kra_hrs=?,frequency=?,freq_year_type=?,recurring_years=?,frequency_month=?,priority=?," +
					"measure_desc=?,measure_val=?,effective_date=?,goal_creater_id=?,goal_creater_type=?,responsible_emp_id=?," +
					"goal_element=?,org_id=?,depart_id=?,with_depart_with_team=? where goal_id=?");
				pst.setInt(1, uF.parseToInt(goaltype));
				pst.setInt(2, uF.parseToInt(goal_parent_id));
				pst.setString(3, corporateGoal);
				pst.setString(4, cgoalObjective);  
				pst.setString(5, cgoalDescription);
				pst.setInt(6, uF.parseToInt(cgoalAlignAttribute));
			//	pst.setString(7, cAddMKra != null && cAddMKra.equals("Measure") ? cmeasurewith : "");
				if(cAddMKra != null && cAddMKra.equals("Measure")) {
					pst.setString(7, cmeasurewith );
				} else {
					pst.setString(7, "" );
				}
				if((cmeasurewith != null)&& (cmeasurewith.equals("Value") || cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage"))) {
					pst.setDouble(8,uF.parseToDouble(cmeasureDollar));
				} else {
					pst.setDouble(8,0.0);
				}
				//pst.setDouble(8,uF.parseToDouble((cmeasurewith.equals("Value") || cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage")) ? cmeasureDollar : "0"));
				pst.setInt(9, 3);
				if(cmeasurewith!=null && cmeasurewith.equals("Effort")) {
					pst.setDouble(10, uF.parseToDouble(cmeasureEffortsDays ));
				}else{
					pst.setDouble(10, 0.0);
				}
				
				if(cmeasurewith!=null && cmeasurewith.equals("Effort")) {
					pst.setDouble(11, uF.parseToDouble(cmeasureEffortsHrs ));
				} else {
					pst.setDouble(11, 0.0);
				}
				pst.setDate(12, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
				pst.setBoolean(13, uF.parseToBoolean(cgoalFeedback));
				pst.setInt(14, uF.parseToInt(corientation));
				pst.setDouble(15, uF.parseToDouble(cgoalWeightage));
				pst.setString(16, cemp);
				pst.setString(17, clevel);
				pst.setString(18, strDesignationUpdate);
				pst.setBoolean(19, uF.parseToBoolean(cmeasureKra));
				pst.setString(20, cAddMKra);
				pst.setString(21, cmkwith);
				pst.setDouble(22, uF.parseToDouble("0"));
				pst.setDouble(23, uF.parseToDouble("0"));
				pst.setDouble(24, uF.parseToDouble("0"));
				
				pst.setInt(25, uF.parseToInt(frequency));
				pst.setInt(26, uF.parseToInt(strYearType));
				pst.setString(27, recurringYears.toString());
				pst.setString(28, frequencyMonths.toString());
				pst.setInt(29, uF.parseToInt(getPriority()));
				pst.setString(30, cMeasureDesc);
				pst.setDouble(31, uF.parseToDouble(cMeasureVal));
				pst.setDate(32, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
				pst.setInt(33, uF.parseToInt(goalCreaterID));
				pst.setString(34, teamgoalRedio);
				pst.setInt(35, uF.parseToInt(responsibleEmpID));
				pst.setInt(36, uF.parseToInt(goalElements));
				
				if(strOrg != null && !strOrg.equals("")) {
					pst.setInt(37, uF.parseToInt(strOrg));
				} else {
					pst.setInt(37, uF.parseToInt(hideOrgid));
				}
				pst.setInt(38, uF.parseToInt(strDepart));
				pst.setString(39, strWithDepartAndTeam);
				
				//pst.setInt(37, uF.parseToInt(strOrg != null && !strOrg.equals("") ? strOrg : hideOrgid));
				pst.setInt(40, uF.parseToInt(goal_id));
				pst.execute();
				pst.close();
				
			
				
				//updated by kalpana on 18/10/2016
              
//				***************************** Goal Frequency Start ************************************
//				GoalScheduler scheduler = new GoalScheduler(request, session, CF, uF, strSessionEmpId);
//				
//				if(uF.parseToInt(innerList.get(2)) != uF.parseToInt(frequency)) {
//					scheduler.updateGoalDetails(goal_id);
//				} else if(uF.parseToInt(innerList.get(2)) == 2 && uF.parseToInt(frequency) == 2) {  // Weekly
//					if(!innerList.get(5).equals(weeklyDay)) {
//						scheduler.updateGoalDetails(goal_id);
//					} else if(!innerList.get(0).equals(cgoalEffectDate)) { 
//						scheduler.updateGoalDetails(goal_id);
//					} else if(!innerList.get(1).equals(cgoalDueDate)) {
//						scheduler.updateGoalDetails(goal_id);
//					}
//				} else if(uF.parseToInt(innerList.get(2)) == 3 && uF.parseToInt(frequency) == 3) { // Monthly
//					if(!innerList.get(4).equals(frequency_day)) {
//						scheduler.updateGoalDetails(goal_id);
//					} else if(!innerList.get(0).equals(cgoalEffectDate)) { 
//						scheduler.updateGoalDetails(goal_id);
//					} else if(!innerList.get(1).equals(cgoalDueDate)) {
//						scheduler.updateGoalDetails(goal_id);
//					}
//				} else if(!innerList.get(0).equals(cgoalEffectDate)) { 
//					scheduler.updateGoalDetails(goal_id);
//				} else if(!innerList.get(1).equals(cgoalDueDate)) {
//					scheduler.updateGoalDetails(goal_id);
//				}
				
				
//				***************************** Goal Frequency End ************************************

				//updated by kalpana on 18/10/2016
				/**
				 * start
				 */
				String cDeleteKRAIds = request.getParameter("cDeleteKRAIds");
//				System.out.println("cDeleteKRAIds==>"+cDeleteKRAIds);
				if(cDeleteKRAIds.length()>0) {
					pst=con.prepareStatement("delete from goal_kras where goal_kra_id in ("+cDeleteKRAIds+")");
//					System.out.println("pst1==>"+pst);
					pst.execute();
					pst.close();
					
				}
				
				int ckracountserial =uF.parseToInt(cKRACount);
//				System.out.println("cKRACount === > " + cKRACount);
//				System.out.println("cKRA === > " + cKRA.length);
				for(int i=0; i<=ckracountserial; i++) {
					String cKRA = request.getParameter("cKRA_"+i);
					String hideKRAId = request.getParameter("hideKRAId_"+i);
					String cKRAWeightage = request.getParameter("cKRAWeightage_"+i);
					
					if(cKRA != null && !cKRA.equals("")) {
//						String[] cKRATask = request.getParameterValues("cKRATask_"+i);
//						String[] hideKRATaskId = request.getParameterValues("hideKRATask_"+i);
//						int tasksCount = hideKRATaskId.length;
						/*System.out.println("cKRATask==>"+Arrays.asList(cKRATask));
						System.out.println("hideKRATaskId==>"+Arrays.asList(hideKRATaskId));*/
						if(hideKRAId != null && uF.parseToInt(hideKRAId) > 0) {
							pst = con.prepareStatement("update goal_kras set goal_id=?,update_date=?,effective_date=?,is_approved=?,approved_by=?,kra_order=?," +
								"kra_description=?,goal_type=?,element_id=?,attribute_id=?,emp_ids=?,updated_by=?,kra_weightage=? where goal_kra_id=?");
							pst.setInt(1, uF.parseToInt(goal_id));
							pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setDate(3, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
							pst.setBoolean(4, true);
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setInt(6, 0);
							pst.setString(7, cKRA);
							pst.setInt(8, uF.parseToInt(goaltype));
							pst.setInt(9, uF.parseToInt(goalElements));
							pst.setInt(10, uF.parseToInt(cgoalAlignAttribute));
							pst.setString(11, getCemp());
							pst.setInt(12, uF.parseToInt(strSessionEmpId));
							pst.setDouble(13, uF.parseToDouble(cKRAWeightage));
							pst.setInt(14, uF.parseToInt(hideKRAId));
//							System.out.println("pst kra update==>"+ pst);
							pst.execute();
							pst.close();
							
							//updated by kalpana on 18/10/2016
							/**
							 * start
							 */
						} else {
							pst = con.prepareStatement("insert into goal_kras(goal_id,entry_date,effective_date,is_approved,approved_by,kra_order," +
								"kra_description,goal_type,element_id,attribute_id,emp_ids,added_by,kra_weightage) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
							pst.setInt(1, uF.parseToInt(goal_id));
							pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setDate(3, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
							pst.setBoolean(4, true);
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setInt(6, 0);
							pst.setString(7, cKRA);
							pst.setInt(8, uF.parseToInt(goaltype));
							pst.setInt(9, uF.parseToInt(goalElements));
							pst.setInt(10, uF.parseToInt(cgoalAlignAttribute));
							pst.setString(11, getCemp());
							pst.setInt(12, uF.parseToInt(strSessionEmpId));
							pst.setDouble(13, uF.parseToDouble(cKRAWeightage));
//							System.out.println("insert pst kra==>"+pst);
							pst.execute();
							pst.close();
							
						}
						
					}
				}	
				
			}
			
			session.setAttribute(MESSAGE,SUCCESSM +"<b>"+getGoalTitle()+"</b>  goal updated Successfully."+ END );
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		}
	
	public String getClevel() {
		return clevel;
	}
	
	public void setClevel(String clevel) {
		this.clevel = clevel;
	}
	
	public String getCgrade() {
		return cgrade;
	}
	
	public void setCgrade(String cgrade) {
		this.cgrade = cgrade;
	}
	
	public String getCemp() {
		return cemp;
	}
	
	public void setCemp(String cemp) {
		this.cemp = cemp;
	}

	public String getEmpselected() {
		return empselected;
	}
	
	public void setEmpselected(String empselected) {
		this.empselected = empselected;
	}
	
	public String getStrDesignationUpdate() {
		return strDesignationUpdate;
	}
	
	public void setStrDesignationUpdate(String strDesignationUpdate) {
		this.strDesignationUpdate = strDesignationUpdate;
	}
	
	public String getMonth() {
		return month;
	}
	
	public void setMonth(String month) {
		this.month = month;
	}

	public String getStrGoalEmpId() {
		return strGoalEmpId;
	}
	
	public void setStrGoalEmpId(String strGoalEmpId) {
		this.strGoalEmpId = strGoalEmpId;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	public String getPriority() {
		return priority;
	}
	public void setPriority(String priority) {
		this.priority = priority;
	}
	public String getGoalTitle() {
		return goalTitle;
	}
	public void setGoalTitle(String goalTitle) {
		this.goalTitle = goalTitle;
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	public String getCurrUserType() {
		return currUserType;
	}
	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}
	public String getStrGoalOrgId() {
		return strGoalOrgId;
	}
	public void setStrGoalOrgId(String strGoalOrgId) {
		this.strGoalOrgId = strGoalOrgId;
	}
	
	
}
