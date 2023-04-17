package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UserAlerts;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EditMyPersonalGoal extends ActionSupport  implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	String strEmpOrgId;
	
	CommonFunctions CF;
  
	private String clevel;
	private String cgrade;
	private String cemp;
	private String strDesignationUpdate;
	
	private String month;
	private String strGoalEmpId;
	private String priority;
	
	private String operation;
	private String submit;
	private String empselected;
	private String type;
	private String typeas;
	
	private String superId;
	
	private String proPage;
	private String minLimit;
	private String empId;
	private String goalTitle;
	
	private String fromPage;
	private String from;
	private String currUserType;
	
	private String dataType;		//add by Parvez date: 29-12-2021
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
	
//		System.out.println("EMPG/81--dataType="+getDataType());

//		System.out.println("getDataType() ===>> " + getDataType());
	//===start parvez date: 29-12-2021===	
//		editGoal();
		if(getDataType() == null || getDataType().equalsIgnoreCase("null")){
			editGoal();
		} else {
			editNewGoal();
			return "NGSUCCESS";
		}
	//===end parvez date: 29-12-2021===	
			
		if(getType()!=null && getType().equals("type")) {
			if(typeas != null && typeas.equalsIgnoreCase("target")) {
				return "TSUCCESS";
			} else if(typeas != null && typeas.equalsIgnoreCase("goal")) {
				if(getFromPage() != null && getFromPage().equalsIgnoreCase("GKT")) {
					return "TSUCCESS";
				} else {
					return "GSUCCESS";
				}
			} else if(typeas != null && typeas.equalsIgnoreCase("KRA")) {
				return "KSUCCESS";
			}
//			return "update";
		} else {
			if(typeas != null && typeas.equalsIgnoreCase("target")){
				return "TSUCCESS";
			} else if(typeas != null && typeas.equalsIgnoreCase("goal")){
				if(getFromPage() != null && getFromPage().equalsIgnoreCase("GKT")) {
					return "TSUCCESS";
				} else {
					return "GSUCCESS";
				}
			} else if(typeas != null && typeas.equalsIgnoreCase("KRA")){
				return "KSUCCESS";
			}
//			return "success";
		}
		return LOAD;
	}
	
	
	private void editGoal() {
//		System.out.println(" 1 in editGoal() ......................");
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		
//		System.out.println("in editGoal() ......................");
		String hideOrgid = request.getParameter("hideOrgid");
		String corporateGoal = request.getParameter("corporateGoal");
		//System.out.println("corporateGoal==>"+corporateGoal);
		String cgoalObjective = request.getParameter("cgoalObjective");
		String cgoalDescription = request.getParameter("cgoalDescription");
		String cgoalAlignAttribute = request.getParameter("cgoalAlignAttribute");
		String goalElements = request.getParameter("goalElements");
		String cmeasurewith = request.getParameter("cmeasurewith");
//		System.out.println("EMPG/126---dataType="+request.getParameter("dataType"));
		
		String cmeasureDollar = request.getParameter("cmeasureDollar");
		String cmeasureEffortsDays = request.getParameter("cmeasureEffortsDays");
		String cmeasureEffortsHrs = request.getParameter("cmeasureEffortsHrs");
		String cmeasureKra = request.getParameter("cmeasureKra");
		String cAddMKra = request.getParameter("cAddMKra");
		
		/*
		if(request.getParameter("cmeasureKra")!=null && request.getParameter("cmeasureKra").equals("KRA")){
			cmeasureKra = "KRA";
		}
		
		if(request.getParameter("cmeasureKra")!=null && request.getParameter("cmeasureKra").equals("Measure")){
			cmeasureKra = "Target";
		}*/
		
//		String[] cKRA = request.getParameterValues("cKRA");
		String goalCnt = request.getParameter("goalCnt");
		String cmkwith = request.getParameter("cmkwith");
//		String cmeasurekraDollar = request.getParameter("cmeasurekraDollar");
//		String cmeasurekraEffortsDays = request.getParameter("cmeasurekraEffortsDays");
//		String cmeasurekraEffortsHrs = request.getParameter("cmeasurekraEffortsHrs");
		String cgoalDueDate = request.getParameter("cgoalDueDate");
		String cgoalEffectDate = request.getParameter("cgoalEffectDate");
		String cgoalFeedback = request.getParameter("cgoalFeedback");
		String corientation = request.getParameter("corientation");
		String cgoalWeightage = request.getParameter("cgoalWeightage");
//		String clevel = request.getParameter("clevel");
//		String cgrade = request.getParameter("cgrade");
//		String cemp = request.getParameter("cemp");
		String cKRACount = request.getParameter("cKRACount");
		String goal_id = request.getParameter("goal_id");
		String goaltype = request.getParameter("goaltype");
//		String goal_parent_id=request.getParameter("goal_parent_id");
		
		String frequency = request.getParameter("frequency");
		String strYearType = request.getParameter("strYearType");
		String[] strMonths = request.getParameterValues("strMonths");
		String[] strQuarters = request.getParameterValues("strQuarters");
		String[] strHalfYears = request.getParameterValues("strHalfYears");
		String[] strYears = request.getParameterValues("strYears");
		
		/*String weekday = request.getParameter("weekday");
		String annualDay = request.getParameter("annualDay");
		String annualMonth = request.getParameter("annualMonth");
		String day = request.getParameter("day");
		String monthday = request.getParameter("monthday");*/

		String teamGoalId = request.getParameter("teamGoalList");
		String goalalignYesno = request.getParameter("goalalignYesno");
		
		String strPerspective = request.getParameter("strPerspective");
		String perspectiveYesno = request.getParameter("perspectiveYesno");
			
		System.out.println("EMPG/179--strPerspective::"+strPerspective+"---perspectiveYesno::"+perspectiveYesno);
		System.out.println("EMPG/180--teamGoalId="+teamGoalId+"--goalalignYesno="+goalalignYesno);
		try {
			con = db.makeConnection(con);
			
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

			Map<String,String> hmEmpMap = CF.getEmpNameMap(con, null, null);
			
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
//					System.out.println("frequencyMonths ===>> " + frequencyMonths.toString());
				}
				String strDt = "01/"+ (uF.parseToInt(firstMonth)<10 ? "0"+firstMonth : firstMonth) +"/"+firstRecurrYear;
				String strMinMaxDates = uF.getCurrentMonthMinMaxDate(strDt, DATE_FORMAT);
				String[] tmpMinMaxDt = strMinMaxDates.split("::::");
				cgoalEffectDate = tmpMinMaxDt[0];
				cgoalDueDate = tmpMinMaxDt[1];
				
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
				
			}
			
			if(frequencyMonths==null) {
				frequencyMonths = new StringBuilder();
			}
			
			/*if(frequency == null || frequency.equals("")) {
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
			
//			System.out.println("getEmpselected()======> " + getEmpselected());
			StringBuilder sbEmpName = new StringBuilder();
			if(getEmpselected()!=null && !getEmpselected().equals("") && !getEmpselected().equals("0")) {
				List<String> tmpselectEmpList=Arrays.asList(getEmpselected().split(","));
				Set<String> empSet = new HashSet<String>(tmpselectEmpList);
				Iterator<String> itr = empSet.iterator();
				int i=0;
				String empList=null;
				
				while (itr.hasNext()) {
					String empid = (String) itr.next();
					if(i==0) {
						sbEmpName.append(hmEmpMap.get(empid));
						empList=","+empid.trim()+",";
					} else {
						sbEmpName.append(", "+hmEmpMap.get(empid));
						empList+=empid.trim()+",";
					}
					i++;
				}
				setCemp(empList);
//				System.out.println("empList======> " + getCemp());
			} else {
				setCemp(null);
				sbEmpName.append(hmEmpMap.get(strSessionEmpId));
			}
			
			//updated by kalpana on 15/10/2016
			/**
			 * start
			 */
			if(uF.parseToInt(goalCnt) > 0 && uF.parseToInt(getSuperId()) == 0) {
				pst = con.prepareStatement("select max(super_id) from goal_details");
				rst = pst.executeQuery();
				while (rst.next()) {
					int intSupId = rst.getInt(1);
					intSupId++;
					setSuperId(intSupId+""); 
				}
				rst.close();
				pst.close();
			}
			/**
			 * end
			 */
			
//			System.out.println("superID==>"+getSuperId());
			String peerIds = getEmployeeList(getCemp(), 4);
			String managerIds = getEmployeeList(getCemp(), 2);
			String hrIds = getEmployeeList(getCemp(), 7);
			String anyoneIds = getEmployeeList(getCemp(), 0);
			
			
					
//				System.out.println("typeas==>"+getTypeas());
				pst = con.prepareStatement("update goal_details set goal_type=?,goal_parent_id=?,goal_title=?,goal_objective=?,goal_description=?," +
						"goal_attribute=?,measure_type=?,measure_currency_value=?,measure_currency_id=?,measure_effort_days=?,measure_effort_hrs=?," +
						"due_date=?,is_feedback=?,orientation_id=?,weightage=?,emp_ids=?,level_id=?,grade_id=?,is_measure_kra=?,measure_kra=?," +
						"measure_type1=?,measure_currency_value1=?,measure_kra_days=?,measure_kra_hrs=?,frequency=?,freq_year_type=?,recurring_years=?," +
						"frequency_month=?,priority=?,effective_date=?,goalalign_with_teamgoal=?,goal_element=?,peer_ids=?,manager_ids=?,hr_ids=?," +
						"anyone_ids=?, super_id=?, align_with_perspective = ?, perspective_id = ? where goal_id=?");
				if(getTypeas().equalsIgnoreCase("KRA")){
					pst.setInt(1, INDIVIDUAL_KRA);
				}else if(getTypeas().equalsIgnoreCase("Target")){
					pst.setInt(1, INDIVIDUAL_TARGET);
				}else {
					pst.setInt(1, PERSONAL_GOAL);
				}
				if(teamGoalId != null && !teamGoalId.equals("")){
					pst.setInt(2, uF.parseToInt(teamGoalId));
				}else{
					pst.setInt(2, 0);
				}
				pst.setString(3, corporateGoal);
				pst.setString(4, cgoalObjective);
				pst.setString(5, cgoalDescription);
				pst.setInt(6, uF.parseToInt(cgoalAlignAttribute));
				
				pst.setString(7, (cmeasureKra != null && cmeasureKra.equals("Yes")) ? cmeasurewith : "");
				
				pst.setDouble(8, uF.parseToDouble((cmeasurewith != null && (cmeasurewith.equals("Value") || cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage"))) ? cmeasureDollar : "0"));
				pst.setInt(9, 3);
				pst.setDouble(10, uF.parseToDouble((cmeasurewith != null && cmeasurewith.equals("Effort")) ? cmeasureEffortsDays : "0"));
				pst.setDouble(11, uF.parseToDouble((cmeasurewith != null && cmeasurewith.equals("Effort")) ? cmeasureEffortsHrs : "0"));
				pst.setDate(12, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
				pst.setBoolean(13, uF.parseToBoolean(cgoalFeedback));
				pst.setInt(14, uF.parseToInt(corientation));
				pst.setDouble(15, uF.parseToDouble(cgoalWeightage));
				/*if(getTypeas().equalsIgnoreCase("goal")){
					pst.setString(16,  ","+strSessionEmpId+",");
				}else{
					pst.setString(16,  getCemp());
				}*/
		//===start parvez date: 08-12-2021===
				
				if(getTypeas().equalsIgnoreCase("goal") && (strSessionUserType.equals(MANAGER) || strSessionUserType.equals(ADMIN) || strSessionUserType.equals(HRMANAGER))){
					pst.setString(16,  getEmpId());
				} else if(getTypeas().equalsIgnoreCase("goal")){
					pst.setString(16,  ","+strSessionEmpId+",");
				}else{
					pst.setString(16,  getCemp());
				}
		//===end parvez date: 08-12-2021===
				pst.setString(17, clevel);
				pst.setString(18, strDesignationUpdate);
				pst.setBoolean(19, (getTypeas() != null && getTypeas().equalsIgnoreCase("KRA")) ? true : uF.parseToBoolean(cmeasureKra));
				pst.setString(20, (getTypeas() != null && getTypeas().equalsIgnoreCase("KRA")) ? "KRA" : cAddMKra);
				pst.setString(21, cmkwith);
				pst.setDouble(22,uF.parseToDouble("0"));
				pst.setDouble(23, uF.parseToDouble("0"));
				pst.setDouble(24, uF.parseToDouble("0"));
				
				pst.setInt(25,uF.parseToInt(frequency));
				pst.setInt(26, uF.parseToInt(strYearType));
				pst.setString(27, recurringYears.toString());
				pst.setString(28, frequencyMonths.toString());
//				pst.setString(26, weeklyDay);
//				pst.setString(27, frequency_day);
//				pst.setString(28, frequency_month);
				pst.setInt(29, uF.parseToInt(getPriority()));
				pst.setDate(30, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
				pst.setBoolean(31, uF.parseToBoolean(goalalignYesno));
				pst.setInt(32, uF.parseToInt(goalElements));
				if(uF.parseToInt(corientation)== 3 || uF.parseToInt(corientation)== 4) {
					pst.setString(33, peerIds);
				} else {
					pst.setString(33, "");
				}
				
				if(uF.parseToInt(corientation)!= 1 && uF.parseToInt(corientation)!= 5) {
					pst.setString(34, managerIds);
				} else {
					pst.setString(34, "");
				}
				pst.setString(35, hrIds);
				
				if(uF.parseToInt(corientation) == 5 && anyoneIds != null && !anyoneIds.equals("")) {
					pst.setString(36, anyoneIds);
				} else {
					pst.setString(36, null);
				}
				
				pst.setInt(37, uF.parseToInt(getSuperId()));
				
				if(perspectiveYesno != null && perspectiveYesno.equalsIgnoreCase("YES")){
					pst.setBoolean(38, true);	
				} else {
					pst.setBoolean(38, false);	
				}
				pst.setInt(39,  uF.parseToInt(strPerspective));
				pst.setInt(40, uF.parseToInt(goal_id));
				//System.out.println("EMPG/484--pst====>"+pst);
				pst.execute();
				 
				pst.close();
				
		//===Start Parvez Date:10-12-2021===
//				System.out.println("EMPG/501--getTypeas="+getTypeas());
				/*if(getTypeas()==null || !getTypeas().equalsIgnoreCase("KRA") || !getTypeas().equalsIgnoreCase("target")){
					List<String> alManagers = null;
					if (uF.parseToBoolean(CF.getIsWorkFlow())) {
						alManagers = insertApprovalMember(con, pst, rst, uF.parseToInt(goal_id+""), uF);
					}
				}*/
				if((getTypeas()==null || !getTypeas().equalsIgnoreCase("KRA") || !getTypeas().equalsIgnoreCase("target")) && (!strSessionUserType.equals(MANAGER) || !strSessionUserType.equals(ADMIN) || !strSessionUserType.equals(HRMANAGER))){
					List<String> alManagers = null;
					if (uF.parseToBoolean(CF.getIsWorkFlow())) {
						alManagers = insertApprovalMember(con, pst, rst, uF.parseToInt(goal_id+""), uF);
					}
				}
		//===End Parvez Date:10-12-2021===
				
//				***************************** Goal Frequency Start ************************************
//				GoalScheduler scheduler = new GoalScheduler(request, session, CF, uF, strSessionEmpId);
				
//				System.out.println("frequency==>"+frequency);
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
				}*/
				/*if(innerList.get(2).equals("B") && getStrBillingKind().equals("B")){
					if(innerList.get(4).equals(getDayCycle())==false){
						scheduler.updateProjectDetails(goal_id);
					}
				} */
				/*else if(uF.parseToInt(innerList.get(2)) == 3 && uF.parseToInt(frequency) == 3) { // Monthly
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
				
				
				String cDeleteKRAIds = request.getParameter("cDeleteKRAIds");
//				String cDeleteKRATaskIds = request.getParameter("cDeleteKRATaskIds");
//				System.out.println("cDeleteKRAIds==>"+cDeleteKRAIds);
				
				//updated by kalpana on 15/10/2016
				/**
				 * start
				 */
				if(cDeleteKRAIds!=null && cDeleteKRAIds.length()>0 ) {
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
				
				if(cDeleteKRATaskIds != null && cDeleteKRATaskIds.length()>0) {
					
					pst=con.prepareStatement("delete from activity_info where goal_kra_task_id in ("+cDeleteKRATaskIds+")");
					pst.execute();
					pst.close();
					
					pst=con.prepareStatement("delete from goal_kra_tasks where goal_kra_task_id in ("+cDeleteKRATaskIds+")");
					pst.execute();
					pst.close();
					
				}
				
				/**
				 * start
				 */
				
				int ckracountserial = uF.parseToInt(cKRACount);
//				System.out.println("ckracountserial ===>> " + ckracountserial);
				for(int i=0; i<=ckracountserial; i++) {
					String cKRA = request.getParameter("cKRA_"+i+"_0");
					String hideKRAId = request.getParameter("hideKRAId_"+i+"_0");
					String cKRAWeightage = request.getParameter("cKRAWeightage_"+i+"_0");
				
					if (cKRA != null && !cKRA.equals("")) {
						String[] cKRATask = request.getParameterValues("cKRATask_"+i+"_0");
						String[] hideKRATaskId = request.getParameterValues("hideKRATaskId_"+i+"_0");
						int tasksCount = hideKRATaskId.length;
						if(hideKRAId!=null && uF.parseToInt(hideKRAId) > 0 ) {
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
							pst.execute();
//							System.out.println("pst ===>> " + pst);
							pst.close();
						   
							//updated by kalpana on 15/10/2016
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
									"kra_description,goal_type,element_id,attribute_id,emp_ids,added_by,kra_weightage) " +
									"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
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
								pst.setDouble(13, uF.parseToDouble(cKRAWeightage));
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
						
//						pst = con.prepareStatement("insert into goal_kras(goal_id,entry_date,effective_date,"
//											+ "is_approved,approved_by,kra_order,kra_description,goal_type)"
//											+ "values(?,?,?,?,?,?,?,?)");
//						pst.setInt(1, uF.parseToInt(goal_id));
//						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst.setBoolean(4, true);
//						pst.setInt(5, uF.parseToInt(strSessionEmpId));
//						pst.setInt(6, 0);
//						pst.setString(7, cKRA[i]);
//						pst.setInt(8, uF.parseToInt(goaltype));
//						pst.execute();
						
//						if(getCemp()!=null && !getCemp().equals("")) {
//							String emp=getCemp().substring(1, getCemp().length()-1);
//							List<String> cemplist=Arrays.asList(emp.split(","));
//							Set<String> setCemp=new HashSet<String>(cemplist);
//							Iterator<String> it=setCemp.iterator();
//							List<String> cEmpLEvelList=new ArrayList<String>();
//							Map<String, String> hmEmpLevelMap =CF.getEmpLevelMap(con);
//							while(it.hasNext()){
//								String val=it.next();
//								String levelID = hmEmpLevelMap.get(val.trim());
//								if(levelID != null){
//									cEmpLEvelList.add(levelID);
//								}
//							}
//							Set<String> setLvlList = new HashSet<String>(cEmpLEvelList);
//							Iterator<String> it1=setLvlList.iterator();
//							while(it1.hasNext()){
//								String val=it1.next();
//							pst = con.prepareStatement("insert into kra_details(kra,attribute_id,level_id,measurable,goal_id)"
//											+ "values(?,?,?,?,?)");
//							pst.setString(1, cKRA[i]);
//							pst.setInt(2, uF.parseToInt(cgoalAlignAttribute));
//							pst.setInt(3, uF.parseToInt(val.trim()));
//							pst.setInt(4, 1);
//							pst.setInt(5, uF.parseToInt(goal_id));
//							pst.execute();
//							}
//						}
						
						}
					}
				
					editMultiGoals(con, uF);
					session.setAttribute(MESSAGE,SUCCESSM +""+sbEmpName.toString() +"'s <b> "+corporateGoal+"</b> "+typeas+" updated Successfully."+ END );
				 
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rst);
			db.closeConnection(con);
		}
	}
	
	
	private void editMultiGoals(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			String goalCnt = request.getParameter("goalCnt");
			String hideOrgid = request.getParameter("hideOrgid");
			Map<String,String> hmEmpMap = CF.getEmpNameMap(con, null, null);
//			System.out.println("inside edit multigoals");
			for(int a=1; a<=uF.parseToInt(goalCnt); a++) {
				
				String corporateGoal = request.getParameter("corporateGoal_"+a);
				if(corporateGoal != null && !corporateGoal.equals("")) {
					
					String cgoalObjective = request.getParameter("cgoalObjective_"+a);
					String cgoalDescription = request.getParameter("cgoalDescription_"+a);
					String cgoalAlignAttribute = request.getParameter("cgoalAlignAttribute_"+a);
					String goalElements = request.getParameter("goalElements_"+a);
					String cmeasurewith = request.getParameter("cmeasurewith");
					String cmeasureDollar = request.getParameter("cmeasureDollar");
					String cmeasureEffortsDays = request.getParameter("cmeasureEffortsDays");
					String cmeasureEffortsHrs = request.getParameter("cmeasureEffortsHrs");
					String cmeasureKra = request.getParameter("cmeasureKra");
					String cAddMKra = request.getParameter("cAddMKra");
	//				String[] cKRA = request.getParameterValues("cKRA");
//					System.out.println("edit multigoal cmeasureKra==>"+cmeasureKra+"==>cAddMKra==>"+cAddMKra );
					String cmkwith = request.getParameter("cmkwith");
					String cgoalDueDate = request.getParameter("cgoalDueDate_"+a);
					String cgoalEffectDate = request.getParameter("cgoalEffectDate_"+a);
					String cgoalFeedback = request.getParameter("cgoalFeedback_"+a);
					String corientation = request.getParameter("corientation_"+a);
					String cgoalWeightage = request.getParameter("cgoalWeightage_"+a);
	
					String cKRACount = request.getParameter("cKRACount_"+a);
					String goal_id = request.getParameter("goalId_"+a);
					String goaltype = request.getParameter("goaltype");
					
					String frequency = request.getParameter("frequency_"+a);
					
					String weekday = request.getParameter("weekday_"+a);
					String annualDay = request.getParameter("annualDay_"+a);
					String annualMonth = request.getParameter("annualMonth_"+a);
					String day = request.getParameter("day_"+a);
					String monthday = request.getParameter("monthday_"+a);
					//String month=request.getParameter("month");
					String teamGoalId = request.getParameter("teamGoalList");
					String goalalignYesno = request.getParameter("goalalignYesno");
					
					String strPerspective = request.getParameter("strPerspective");
					String perspectiveYesno = request.getParameter("perspectiveYesno");
					
//					System.out.println("strPerspective::"+strPerspective+"perspectiveYesno:"+perspectiveYesno);
					StringBuilder strQuery = new StringBuilder();
					strQuery.append("select * from goal_details where goal_id = ?");
					pst = con.prepareStatement(strQuery.toString());
					pst.setInt(1, uF.parseToInt(goal_id));
//					System.out.println("\npst1 in proId====>"+pst1);
					rst = pst.executeQuery();
					List<String> innerList = new ArrayList<String>();
					while (rst.next()) {
						innerList.add(uF.getDateFormat(rst.getString("effective_date"), DBTIMESTAMP, DATE_FORMAT));
						innerList.add(uF.getDateFormat(rst.getString("due_date"), DBDATE, DATE_FORMAT));
						innerList.add(rst.getString("frequency"));
						innerList.add(rst.getString("user_id"));
						innerList.add(rst.getString("frequency_day")); //4
						innerList.add(rst.getString("weekday"));
					}
					rst.close();
					pst.close();
					
				if(frequency == null || frequency.equals("")) {
					frequency = "1";
				}	
				
				String frequency_day=null;
				String frequency_month=null;
				String weeklyDay=null;
				if(frequency!=null && frequency.equals("2")) { 
					weeklyDay=weekday;
					frequency_day=null;
					frequency_month=null;
				} else if(frequency!=null && frequency.equals("3")) {
					weeklyDay=null;
					frequency_day=day;
					frequency_month=null;
				} else if(frequency!=null && frequency.equals("4")) {
					weeklyDay=null;
					frequency_day=monthday;
					frequency_month=month;
				} else if(frequency!=null && frequency.equals("5")) {
					weeklyDay=null;
					frequency_day=monthday;
					frequency_month=month;
				} else if(frequency!=null && frequency.equals("6")) {
					weeklyDay=null;
					frequency_day=annualDay;
					frequency_month=annualMonth;
				}
				
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
	//				System.out.println("empList======> " + getCemp());
				} else {
					setCemp(null);
				}
				
				String peerIds = getEmployeeList(getCemp(), 4);
				String managerIds = getEmployeeList(getCemp(), 2);
				String hrIds = getEmployeeList(getCemp(), 7);
				String anyoneIds = getEmployeeList(getCemp(), 0);
				
				
//				System.out.println("goal_id ===>> " + goal_id);
					if(uF.parseToInt(goal_id) > 0) {
						pst = con.prepareStatement("update goal_details set goal_type=?,goal_parent_id=?,goal_title=?,goal_objective=?,goal_description=?," +
							"goal_attribute=?,measure_type=?,measure_currency_value=?,measure_currency_id=?,measure_effort_days=?,measure_effort_hrs=?," +
							"due_date=?,is_feedback=?,orientation_id=?,weightage=?,emp_ids=?,level_id=?,grade_id=?,is_measure_kra=?,measure_kra=?," +
							"measure_type1=?,measure_currency_value1=?,measure_kra_days=?,measure_kra_hrs=?,frequency=?,weekday=?,frequency_day=?," +
							"frequency_month=?,priority=?,effective_date=?,goalalign_with_teamgoal=?,goal_element=?,peer_ids=?,manager_ids=?,hr_ids=?," +
							"anyone_ids=?,super_id = ?,align_with_perspective = ?,perspective_id = ?  where goal_id=?");
						if(getTypeas().equalsIgnoreCase("KRA")) {
							pst.setInt(1, INDIVIDUAL_KRA);
						} else if(getTypeas().equalsIgnoreCase("target")) {
							pst.setInt(1, INDIVIDUAL_TARGET);
						} else {
							pst.setInt(1, PERSONAL_GOAL);
						}
						if(teamGoalId != null && !teamGoalId.equals("")) {
							pst.setInt(2, uF.parseToInt(teamGoalId));
						} else {
							pst.setInt(2, 0);
						}
						pst.setString(3, corporateGoal);
						pst.setString(4, cgoalObjective);
						pst.setString(5, cgoalDescription);
						pst.setInt(6, uF.parseToInt(cgoalAlignAttribute));
						pst.setString(7, cmeasureKra != null && cmeasureKra.equals("Yes") ? cmeasurewith : "");
						
						pst.setDouble(8,uF.parseToDouble((cmeasurewith != null && (cmeasurewith.equals("Value") || cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage"))) ? cmeasureDollar : "0"));
						pst.setInt(9, 3);
						pst.setDouble(10, uF.parseToDouble((cmeasurewith != null && cmeasurewith.equals("Effort")) ? cmeasureEffortsDays : "0"));
						pst.setDouble(11, uF.parseToDouble((cmeasurewith != null && cmeasurewith.equals("Effort")) ? cmeasureEffortsHrs : "0"));
						pst.setDate(12, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
						pst.setBoolean(13, uF.parseToBoolean(cgoalFeedback));
						pst.setInt(14, uF.parseToInt(corientation));
						pst.setDouble(15, uF.parseToDouble(cgoalWeightage));
						if(getTypeas().equalsIgnoreCase("goal")){
							pst.setString(16,  ","+strSessionEmpId+",");
						}else{
							pst.setString(16,  getCemp());
						}
						pst.setString(17, clevel);
						pst.setString(18, strDesignationUpdate);
						pst.setBoolean(19, (getTypeas() != null && getTypeas().equalsIgnoreCase("KRA")) ? true : uF.parseToBoolean(cmeasureKra));
						pst.setString(20, (getTypeas() != null && getTypeas().equalsIgnoreCase("KRA")) ? "KRA" : cAddMKra);
						pst.setString(21, cmkwith);
						pst.setDouble(22,uF.parseToDouble("0"));
						pst.setDouble(23, uF.parseToDouble("0"));
						pst.setDouble(24, uF.parseToDouble("0"));
						
						pst.setInt(25,uF.parseToInt(frequency));
						pst.setString(26, weeklyDay);
						pst.setString(27, frequency_day);
						pst.setString(28, frequency_month);
						pst.setInt(29, uF.parseToInt(getPriority()));
						pst.setDate(30, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
						pst.setBoolean(31, uF.parseToBoolean(goalalignYesno));
						pst.setInt(32, uF.parseToInt(goalElements));
						if(uF.parseToInt(corientation)== 3 || uF.parseToInt(corientation)== 4) {
							pst.setString(33, peerIds);
						} else {
							pst.setString(33, "");
						}
						if(uF.parseToInt(corientation)!= 1 && uF.parseToInt(corientation)!= 5) {
							pst.setString(34, managerIds);
						} else {
							pst.setString(34, "");
						}
						pst.setString(35, hrIds);
						if(uF.parseToInt(corientation) == 5 && anyoneIds != null && !anyoneIds.equals("")) {
							pst.setString(36, anyoneIds);
						} else {
							pst.setString(36, null);
						}
						
						pst.setInt(37, uF.parseToInt(getSuperId()));
						
						if(perspectiveYesno != null && perspectiveYesno.equalsIgnoreCase("YES")){
							pst.setBoolean(38, true);	
						}
						if(perspectiveYesno != null && perspectiveYesno.equalsIgnoreCase("NO")){
							pst.setBoolean(38, false);	
						}
						pst.setInt(39,  uF.parseToInt(strPerspective));
						pst.setInt(40, uF.parseToInt(goal_id));
//						System.out.println("EMPG/1078--pst ===>> " + pst);
						pst.execute();
						pst.close();
					

//						***************************** Goal Frequency Start ************************************
						GoalScheduler scheduler = new GoalScheduler(request, session, CF, uF, strSessionEmpId);
						
						if(uF.parseToInt(innerList.get(2)) != uF.parseToInt(frequency)) {
							scheduler.updateGoalDetails(goal_id);
						} else if(uF.parseToInt(innerList.get(2)) == 2 && uF.parseToInt(frequency) == 2) {  // Weekly
							if(!innerList.get(5).equals(weeklyDay)) {
								scheduler.updateGoalDetails(goal_id);
							} else if(!innerList.get(0).equals(cgoalEffectDate)) { 
								scheduler.updateGoalDetails(goal_id);
							} else if(!innerList.get(1).equals(cgoalDueDate)) {
								scheduler.updateGoalDetails(goal_id);
							}
						}
						/*if(innerList.get(2).equals("B") && getStrBillingKind().equals("B")){
							if(innerList.get(4).equals(getDayCycle())==false){
								scheduler.updateProjectDetails(goal_id);
							}
						} */
						else if(uF.parseToInt(innerList.get(2)) == 3 && uF.parseToInt(frequency) == 3) { // Monthly
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
						}
						
//						***************************** Goal Frequency End ************************************
						
						
	//				String cDeleteKRATaskIds = request.getParameter("cDeleteKRATaskIds"); ---------------- Remaininig Point --------------------
					
						int ckracountserial = uF.parseToInt(cKRACount);
					
						for(int i=0; i<=ckracountserial; i++) {
							String cKRA = request.getParameter("cKRA_"+i+"_"+a);
							String hideKRAId = request.getParameter("hideKRAId_"+i+"_"+a);
							String cKRAWeightage = request.getParameter("cKRAWeightage_"+i+"_"+a);
//							System.out.println("cKRA ===>> " + cKRA);
//							System.out.println("hideKRAId ===>> " + hideKRAId);
							if (cKRA != null && !cKRA.equals("")) {
								String[] cKRATask = request.getParameterValues("cKRATask_"+i+"_"+a);
								String[] hideKRATaskId = request.getParameterValues("hideKRATaskId_"+i+"_"+a);
								
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
									setGoalTitle(cKRA);
									pst.setInt(8, uF.parseToInt(goaltype));
									pst.setInt(9, uF.parseToInt(goalElements));
									pst.setInt(10, uF.parseToInt(cgoalAlignAttribute));
									pst.setString(11, getCemp());
									pst.setInt(12, uF.parseToInt(strSessionEmpId));
									pst.setDouble(13, uF.parseToDouble(cKRAWeightage));
									pst.setInt(14, uF.parseToInt(hideKRAId));
									pst.execute();
//									System.out.println("pst ===>> " + pst);
									pst.close();
									
									//updated by kalpana on 15/10/2016
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
//													System.out.println("update task activity pst==>"+pst);
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
//													System.out.println("insert activity_info pst==>"+pst);
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
												pst.execute();
												pst.close();
										    }
											/*pst = con.prepareStatement("insert into goal_kra_tasks(goal_id,kra_id,task_name,emp_ids,entry_date,added_by) " +
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
												pst.execute();
												pst.close();
											}*/
										}
									 }
								 }
								
								/**
									 * end
								 */	
									session.setAttribute(MESSAGE,SUCCESSM +""+hmEmpMap.get(empId)+"'s<b>"+goalTitle+"</b> "+typeas+" updated Successfully."+ END );
									
								} else {
									pst = con.prepareStatement("insert into goal_kras(goal_id,entry_date,effective_date,is_approved,approved_by,kra_order," +
										"kra_description,goal_type,element_id,attribute_id,emp_ids,added_by,kra_weightage) " +
										"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
									pst.setInt(1, uF.parseToInt(goal_id));
									pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setBoolean(4, true);
									pst.setInt(5, uF.parseToInt(strSessionEmpId));
									pst.setInt(6, 0);
									pst.setString(7, cKRA);
									setGoalTitle(cKRA);
									pst.setInt(8, uF.parseToInt(goaltype));
									pst.setInt(9, uF.parseToInt(goalElements));
									pst.setInt(10, uF.parseToInt(cgoalAlignAttribute));
									pst.setString(11, getCemp());
									pst.setInt(12, uF.parseToInt(strSessionEmpId));
									pst.setDouble(13, uF.parseToDouble(cKRAWeightage));
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
									
									session.setAttribute(MESSAGE,SUCCESSM +""+hmEmpMap.get(empId)+"'s<b>"+goalTitle+"</b> "+typeas+" updated Successfully."+ END );
									
									for(int j=0; cKRATask != null && j<cKRATask.length; j++) {
										if(getEmpselected()!=null && !getEmpselected().equals("") && !getEmpselected().equals("0") && cKRATask[j] != null && !cKRATask[j].equals("")) {
											
											pst = con.prepareStatement("insert into goal_kra_tasks(goal_id,kra_id,task_name,emp_ids,entry_date,added_by) " +
												" values(?,?,?,?, ?,?)");
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
								
							}
						}
					} else {
						
						/*pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_objective,goal_description,goal_attribute," +
								"measure_type,measure_currency_value,measure_currency_id,measure_effort_days,measure_effort_hrs,due_date,is_feedback," +
								"orientation_id,weightage,emp_ids,level_id,grade_id,is_measure_kra,measure_kra,measure_type1,measure_currency_value1," +
								"measure_kra_days,measure_kra_hrs,entry_date,user_id,frequency,weekday,frequency_day,frequency_month,priority,effective_date," +
								"goalalign_with_teamgoal,goal_element,org_id,super_id,peer_ids,manager_ids,hr_ids,anyone_ids)" +
								"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");*/
						
						pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_objective,goal_description,goal_attribute," +
								"measure_type,measure_currency_value,measure_currency_id,measure_effort_days,measure_effort_hrs,due_date,is_feedback," +
								"orientation_id,weightage,emp_ids,level_id,grade_id,is_measure_kra,measure_kra,measure_type1,measure_currency_value1," +
								"measure_kra_days,measure_kra_hrs,entry_date,user_id,frequency,weekday,frequency_day,frequency_month,priority,effective_date," +
								"goalalign_with_teamgoal,goal_element,org_id,super_id,peer_ids,manager_ids,hr_ids,anyone_ids)" +
								"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
						if(getTypeas().equalsIgnoreCase("KRA")) {
							pst.setInt(1, INDIVIDUAL_KRA);
						} else if(getTypeas().equalsIgnoreCase("target")) {
							pst.setInt(1, INDIVIDUAL_TARGET);
						} else if(getTypeas().equalsIgnoreCase("goal")) {
							pst.setInt(1, PERSONAL_GOAL);
						}
						if(teamGoalId != null && !teamGoalId.equals("")) {
							pst.setInt(2, uF.parseToInt(teamGoalId));
						} else {
							pst.setInt(2, 0);
						}
						pst.setString(3, corporateGoal);
						pst.setString(4, cgoalObjective);
						pst.setString(5, cgoalDescription);
						pst.setInt(6, uF.parseToInt(cgoalAlignAttribute));
						pst.setString(7, cmeasureKra != null && cmeasureKra.equals("Yes") ? cmeasurewith : "");
						
						pst.setDouble(8, uF.parseToDouble((cmeasurewith != null && (cmeasurewith.equals("Value") || cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage"))) ? cmeasureDollar : "0"));
						pst.setInt(9, 3);
						pst.setDouble(10, uF.parseToDouble(cmeasurewith != null && cmeasurewith.equals("Effort") ? cmeasureEffortsDays : "0"));
						pst.setDouble(11, uF.parseToDouble(cmeasurewith != null && cmeasurewith.equals("Effort") ? cmeasureEffortsHrs : "0"));
						pst.setDate(12, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
						pst.setBoolean(13, uF.parseToBoolean(cgoalFeedback));
						pst.setInt(14, uF.parseToInt(corientation));
						pst.setDouble(15, uF.parseToDouble(cgoalWeightage));
						if(getTypeas() != null && getTypeas().equalsIgnoreCase("goal")) {
							pst.setString(16, ","+strSessionEmpId+",");
						} else {
							pst.setString(16, getCemp());
						}
						pst.setString(17, clevel);
						pst.setString(18, strDesignationUpdate);
						pst.setBoolean(19, uF.parseToBoolean(cmeasureKra));
						pst.setString(20, (getTypeas() != null && getTypeas().equalsIgnoreCase("KRA")) ? "KRA" : cAddMKra);
						pst.setString(21, cmkwith);
						pst.setDouble(22,uF.parseToDouble("0"));
						pst.setDouble(23,uF.parseToDouble("0"));
						pst.setDouble(24,uF.parseToDouble("0"));
						pst.setDate(25, uF.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(26, uF.parseToInt(strSessionEmpId));
						pst.setInt(27, uF.parseToInt(frequency));
						pst.setString(28, weeklyDay);
						pst.setString(29, frequency_day);
						pst.setString(30, frequency_month);
						pst.setInt(31, uF.parseToInt(getPriority()));
						pst.setDate(32, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
						pst.setBoolean(33, uF.parseToBoolean(goalalignYesno));
						pst.setInt(34, uF.parseToInt(goalElements));
						if(getTypeas() != null && getTypeas().equalsIgnoreCase("goal")) {
							pst.setInt(35, uF.parseToInt(strEmpOrgId));
						} else {
							pst.setInt(35, uF.parseToInt(hideOrgid));
						}
						
						pst.setInt(36, uF.parseToInt(getSuperId()));
						if(uF.parseToInt(corientation)== 3 || uF.parseToInt(corientation)== 4) {
							pst.setString(37, peerIds);
						} else {
							pst.setString(37, "");
						}
						if(uF.parseToInt(corientation)!= 1 && uF.parseToInt(corientation)!= 5) {
							pst.setString(38, managerIds);
						} else {
							pst.setString(38, "");
						}
						pst.setString(39, hrIds);
						if(uF.parseToInt(corientation) == 5 && anyoneIds != null && !anyoneIds.equals("")) {
							pst.setString(40, anyoneIds);
						} else {
							pst.setString(40, null);
						}
						pst.execute();
						pst.close();
						
						int individual_goal_id = 0;
						pst = con.prepareStatement("select max(goal_id) from goal_details");
						rst = pst.executeQuery();
			
						while (rst.next()) {
							individual_goal_id = rst.getInt(1);
						}
						rst.close();
						pst.close();
						
						


//						***************************** Goal Frequency Start ************************************
						GoalScheduler scheduler = new GoalScheduler(request, session, CF, uF, strSessionEmpId);
						scheduler.updateGoalDetails(individual_goal_id+"");
//						***************************** Goal Frequency End ************************************
						
						
						int ckracountserial = uF.parseToInt(cKRACount);
						StringBuilder sbKras = new StringBuilder();
						for (int i = 0; i <=ckracountserial; i++) {
							String cKRA = request.getParameter("cKRA_"+i+"_"+a);
							String cKRAWeightage = request.getParameter("cKRAWeightage_"+i+"_"+a);
							if (cKRA != null && !cKRA.equals("")) {
								String[] cKRATask = request.getParameterValues("cKRATask_"+i+"_"+a);
								if(sbKras == null || sbKras.toString().equals("")) {
									sbKras.append(cKRA);
								} else {
									sbKras.append(", "+cKRA);
								}
								pst = con.prepareStatement("insert into goal_kras(goal_id,entry_date,effective_date,is_approved,approved_by,kra_order," +
										"kra_description,goal_type,element_id,attribute_id,emp_ids,added_by,kra_weightage) " +
										"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
								pst.setInt(1, individual_goal_id);
								pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setDate(3, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
								pst.setBoolean(4, true);
								pst.setInt(5, uF.parseToInt(strSessionEmpId));
								pst.setInt(6, 0);
								pst.setString(7, cKRA);
								if(getTypeas().equalsIgnoreCase("KRA")) {
									pst.setInt(8, INDIVIDUAL_KRA);
								} else {
									pst.setInt(8, 0);
								}
								pst.setInt(9, uF.parseToInt(goalElements));
								pst.setInt(10, uF.parseToInt(cgoalAlignAttribute));
								pst.setString(11, getCemp());
								pst.setInt(12, uF.parseToInt(strSessionEmpId));
								pst.setDouble(13, uF.parseToDouble(cKRAWeightage));
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
											pst.setInt(1, individual_goal_id);
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
							}
			
						if(getTypeas() != null && getTypeas().equalsIgnoreCase("goal")) {
//							UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//							userAlerts.setStrDomain(strDomain);
//							userAlerts.setStrEmpId(strSessionEmpId);
//							userAlerts.set_type(MY_PERSONAL_GOAL_ALERT);
//							userAlerts.setStatus(INSERT_ALERT);
//							Thread t = new Thread(userAlerts);
//							t.run();
						} else {
							List<String> selectEmpList = Arrays.asList(getEmpselected().split(","));
							Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
							
							for(int i=0; selectEmpList!= null && !selectEmpList.isEmpty() && i<selectEmpList.size(); i++) {
								if(!selectEmpList.get(i).equals("") && uF.parseToInt(selectEmpList.get(i)) > 0) {
									
									String gktType = "";
									if(cAddMKra != null && cAddMKra.equalsIgnoreCase("KRA")) {
										gktType = "KRA";
									} else if(cAddMKra != null && cAddMKra.equalsIgnoreCase("Measure")) {
										gktType = "Target";
									}
									String alertData = "<div style=\"float: left;\"> You have received a new "+gktType+" ("+corporateGoal+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
									String alertAction = "MyHR.action?pType=WR";
									
									UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
									userAlerts.setStrDomain(strDomain);
									userAlerts.setStrEmpId(selectEmpList.get(i));
									userAlerts.setStrData(alertData);
									userAlerts.setStrAction(alertAction);
									userAlerts.setCurrUserTypeID(hmUserTypeId.get(ADMIN));
									userAlerts.setStatus(INSERT_WR_ALERT);
									Thread t = new Thread(userAlerts);
									t.run();
									
//									UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//									userAlerts.setStrDomain(strDomain);
//									userAlerts.setStrEmpId(selectEmpList.get(i));
//									userAlerts.set_type(MY_KRA_ALERT);
//									userAlerts.setStatus(INSERT_ALERT);
//									Thread t = new Thread(userAlerts);
//									t.run();
								}
							}
							sendIndiGoalMailToEmp(con, individual_goal_id, selectEmpList, cAddMKra, sbKras.toString(), cmeasureDollar, cmeasureEffortsDays, cmeasureEffortsHrs);
						}
						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String getEmployeeList(String self, int type) {
		StringBuilder sb = null; 
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);
		try {
			List<String> empList = new ArrayList<String>();
			if(self != null && self.trim().length()>0) {
			 self=self.substring(1,self.length()-1);
			}
//			System.out.println("self=====>"+self);
//			System.out.println("type=====>"+type);
			if(self != null && self.length()>1) {
				if (type == 2) {
					pst = con.prepareStatement("select supervisor_emp_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in ("+ self+ ") and supervisor_emp_id!=0");
					rs = pst.executeQuery();
					
					while(rs.next()) {
						if(!empList.contains(rs.getString("supervisor_emp_id").trim())) {
							if(sb == null) {
								sb = new StringBuilder();
								sb.append(","+rs.getString("supervisor_emp_id").trim()+",");
							} else {
								sb.append(rs.getString("supervisor_emp_id").trim()+",");
							}
							empList.add(rs.getString("supervisor_emp_id").trim());
						}
					}
					rs.close();
					pst.close();
					
					if(sb == null) {
						sb = new StringBuilder();
					}
					return sb.toString();
			
				} else if (type == 3) {
	
				} else if (type == 4) {
					
	//				pst=con.prepareStatement("select grade_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in("+self+") group by grade_id");
	//				rs=pst.executeQuery();
	//				StringBuilder sb4=new StringBuilder();
	//				int cnt=0;
	//				while(rs.next()){
	//					if(cnt==0){
	//						sb4.append(rs.getString("grade_id").trim());
	//					}else{
	//						sb4.append(","+rs.getString("grade_id").trim());
	//					}
	//					cnt++;
	//				}
	//				rs.close();
	//				pst.close();
	//				
	//				pst=con.prepareStatement("select wlocation_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in("+self+") group by wlocation_id");
	//				rs=pst.executeQuery();
	//				StringBuilder sb5=new StringBuilder();
	//				 cnt=0;
	//				while(rs.next()){
	//					if(cnt==0){
	//						sb5.append(","+rs.getString("wlocation_id").trim()+",");
	//					}else{
	//						sb5.append(rs.getString("wlocation_id").trim()+",");
	//					}
	//					cnt++;
	//				}
	//				rs.close();
	//				pst.close();
					
	//				String strsb5 = (sb5 != null ? sb5.toString().substring(1, sb5.toString().length()-1) : "");
					//String strsb4 = (sb4 != null ? sb4.toString().substring(1, sb4.toString().length()-1) : "");
					pst = con.prepareStatement("select emp_id from employee_official_details where supervisor_emp_id in " +
						" (select supervisor_emp_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = " +
						" eod.emp_id and is_alive = true and emp_id in ("+self+") and supervisor_emp_id!=0) and emp_id not in ("+self+") and emp_id >0");
	//				pst=con.prepareStatement("select emp_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and wlocation_id in("+strsb5+") and grade_id in("+sb4.toString()+") group by emp_id");
					rs = pst.executeQuery();
					
					while(rs.next()) {
						if(!empList.contains(rs.getString("emp_id").trim())) {
							if(sb == null) {
								sb = new StringBuilder();
								sb.append(","+rs.getString("emp_id").trim()+",");
							}else{
								sb.append(rs.getString("emp_id").trim()+",");
							}
						empList.add(rs.getString("emp_id").trim());	
						}
					}
					rs.close();
					pst.close();
					
					if(sb == null) {
						sb = new StringBuilder();
					}
					return sb.toString();
	
				} else if (type == 5) {
	
				} else if (type == 6) {
	
				} else if (type == 7) {
					
					/*pst=con.prepareStatement("select wlocation_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in("+self+") group by wlocation_id");
					rs=pst.executeQuery();
					StringBuilder sb5=new StringBuilder();
					int cnt=0;
					while(rs.next()){
						if(cnt==0){
							sb5.append(rs.getString("wlocation_id").trim());
						}else{
							sb5.append(","+rs.getString("wlocation_id").trim());
	
						}
						cnt++;
					}
					rs.close();
					pst.close();*/
					
					//String strsb5 = (sb5 != null ? sb5.toString().substring(1, sb5.toString().length()-1) : "");
					pst = con.prepareStatement("select emp_hr from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in ("+ self+ ") and emp_hr >0");
	//				pst=con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=? and wlocation_id in("+sb5.toString()+")");
	//				pst.setInt(1, 7);
					rs = pst.executeQuery();
					while(rs.next()) {
						if(!empList.contains(rs.getString("emp_hr").trim())) {
							if(sb == null) {
								sb = new StringBuilder();
								sb.append(","+rs.getString("emp_hr").trim()+",");
							} else {
								sb.append(rs.getString("emp_hr").trim()+",");
							}
						empList.add(rs.getString("emp_hr").trim());	
						}
					}
					rs.close();
					pst.close();
					
					if(sb == null) {
						sb = new StringBuilder();
					}
					return sb.toString();
				} else if (type == 8) {
	
				} else if (type == 9) {
	
				} else if (type == 0) {
					
					pst = con.prepareStatement("select wlocation_id from employee_official_details eod, employee_personal_details epd where epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in("+self+") group by wlocation_id");
					rs = pst.executeQuery();
					StringBuilder sb5 = new StringBuilder();
					int cnt = 0;
					while(rs.next()) {
						if(cnt == 0) {
							sb5.append(rs.getString("wlocation_id").trim());
						} else {
							sb5.append(","+rs.getString("wlocation_id").trim());
						}
						cnt++;
					}
					rs.close();
					pst.close();
					
					//String strsb5 = (sb5 != null ? sb5.toString().substring(1, sb5.toString().length()-1) : "");
					pst=con.prepareStatement("select emp_id from employee_official_details eod, employee_personal_details epd where " +
						" epd.emp_per_id = eod.emp_id and is_alive = true and emp_id in ("+ self+ ") and emp_id>0 ");
	//				pst=con.prepareStatement("select emp_per_id from employee_official_details eod,user_details ud, employee_personal_details epd where epd.emp_per_id=eod.emp_id and ud.emp_id=eod.emp_id and is_alive = true and ud.usertype_id=? and wlocation_id in("+sb5.toString()+")");
	//				pst.setInt(1, 7);
					rs=pst.executeQuery();
					while(rs.next()) {
						if(!empList.contains(rs.getString("emp_id").trim())) {
							if(sb == null) {
								sb = new StringBuilder();
								sb.append(","+rs.getString("emp_id").trim()+",");
							} else {
								sb.append(rs.getString("emp_id").trim()+",");
							}
						empList.add(rs.getString("emp_id").trim());	
						}
					}
					rs.close();
					pst.close();
					
					if(sb == null) {
						sb = new StringBuilder();
					}
					return sb.toString();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return null;
	}
	
	
	public void sendIndiGoalMailToEmp(Connection con, int newGoalId, List<String> empIdList, String goalKraTarget, String kras, String tAmount, String tDays, String tHrs) {

		ResultSet rst = null;
		PreparedStatement pst = null;
		UtilityFunctions uF = new UtilityFunctions();

		try {
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
//			System.out.println("Req id is ========= "+getStrId());
			String goalName = "";
			pst = con.prepareStatement("select goal_title from goal_details where goal_id = ?");
			pst.setInt(1, newGoalId);
			rst = pst.executeQuery();
			while (rst.next()) {
				goalName = rst.getString("goal_title");
			}
			rst.close();
			pst.close();
			
			if(goalName != null && !goalName.equals("")) {
				Map<String, String> hmEmpInner1 = hmEmpInfo.get(strSessionEmpId);
				StringBuilder sbGoalAssignerName = new StringBuilder();
				sbGoalAssignerName.append(hmEmpInner1.get("FNAME")+" " +hmEmpInner1.get("LNAME"));
				for(int i=0; empIdList != null && !empIdList.isEmpty() && i<empIdList.size(); i++) {
					if(!empIdList.get(i).equals("")) {
						Map<String, String> hmEmpInner = hmEmpInfo.get(empIdList.get(i));
						if(hmEmpInner == null) hmEmpInner = new HashMap<String, String>();
						String strDomain = request.getServerName().split("\\.")[0];
						int NOTIFICATION_NAME = N_EXECUTIVE_TARGET; 
						/*if(goalKraTarget == null || goalKraTarget.equals("")) {
							NOTIFICATION_NAME = N_EXECUTIVE_GOAL;
						} else*/
						if(goalKraTarget != null && goalKraTarget.equalsIgnoreCase("KRA")) {
							NOTIFICATION_NAME = N_EXECUTIVE_KRA;
						} else if(goalKraTarget != null && goalKraTarget.equalsIgnoreCase("Measure")) {
							NOTIFICATION_NAME = N_EXECUTIVE_TARGET;
						}
						Notifications nF = new Notifications(NOTIFICATION_NAME, CF); 
			//			System.out.println("Emp ID is ========= "+rst1.getString("panel_emp_id"));
						nF.setDomain(strDomain);
						nF.request = request;
						nF.setStrEmpId(empIdList.get(i));
						nF.setStrHostAddress(CF.getStrEmailLocalHost());
						nF.setStrHostPort(CF.getStrHostPort());
						nF.setStrContextPath(request.getContextPath());
						nF.setStrGoalAssignerName(sbGoalAssignerName.toString());
						/*if(goalKraTarget == null || goalKraTarget.equals("")) {
							 nF.setStrGoalName(goalName);
						} else*/
						if(goalKraTarget != null && goalKraTarget.equalsIgnoreCase("KRA")) {
							nF.setStrGoalName(goalName);
							nF.setStrKRAName(kras);
						} else if(goalKraTarget != null && goalKraTarget.equalsIgnoreCase("Measure")) {
							nF.setStrTargetName(goalName);
							if(tAmount != null && !tAmount.equals("")) {
								nF.setStrTargetValue("Rs." + tAmount);
							} else if((tDays != null && !tDays.equals("")) || (tHrs != null && !tHrs.equals(""))) {
								nF.setStrTargetValue(tDays+ " Days " + tHrs + " Hrs ");
							}
						}
						nF.setStrEmpFname(hmEmpInner.get("FNAME"));
						nF.setStrEmpLname(hmEmpInner.get("LNAME"));
						nF.setEmailTemplate(true);
						nF.sendNotifications();
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	//===start parvez date: 04-09-2021===
	private List<String> insertApprovalMember(Connection con, PreparedStatement pst, ResultSet rs, int nRecritmentId, UtilityFunctions uF) {
		List<String> alManagers = new ArrayList<String>();
		try {

			String policy_id = request.getParameter("policy_id");
//			System.out.println("EMPG/1947--policy_id="+policy_id);
			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);

			pst = con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where "
					+ " policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
			pst.setInt(1, uF.parseToInt(policy_id));
			// System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, List<String>> hmMemberMap = new LinkedHashMap<String, List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("member_type"));
				innerList.add(rs.getString("member_id"));
				innerList.add(rs.getString("member_position"));
				innerList.add(rs.getString("work_flow_mem"));
				innerList.add(rs.getString("work_flow_member_id"));

				hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("delete from work_flow_details where effective_id=? and effective_type=?");
			pst.setInt(1, nRecritmentId);
			pst.setString(2, WORK_FLOW_PERSONAL_GOAL);
			pst.executeUpdate();
			pst.close();

			String strDomain = request.getServerName().split("\\.")[0];
			Iterator<String> it = hmMemberMap.keySet().iterator();
			while (it.hasNext()) {
				String work_flow_member_id = it.next();
				List<String> innerList = hmMemberMap.get(work_flow_member_id);

				int memid = uF.parseToInt(innerList.get(1));
				 //System.out.println("EMPG/1987--innerList.get(3)+memid====>"+innerList.get(3)+memid+"=====>"+request.getParameter(innerList.get(3)+memid));
				String empid = request.getParameter(innerList.get(3) + memid);

//				System.out.println("EMPG/1990--empId="+empid);
				if (empid != null && !empid.equals("")) {
					int userTypeId = memid;
					if (uF.parseToInt(innerList.get(0)) == 3) {
						userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
					}
//					 System.out.println("approval empid====>"+empid);
//					pst = con.prepareStatement("update work_flow_details set emp_id=?,member_type=?,member_position=?,"
//							+ "work_flow_mem_id=?,is_approved=?,status=?,user_type_id=? where effective_id=? and effective_type=?");
					pst = con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position,"
								+ "work_flow_mem_id,is_approved,status,user_type_id) values(?,?,?,?, ?,?,?,?, ?)");
					pst.setInt(1, uF.parseToInt(empid));
					pst.setInt(2, nRecritmentId);
					pst.setString(3, WORK_FLOW_PERSONAL_GOAL);
					pst.setInt(4, uF.parseToInt(innerList.get(0)));
					pst.setInt(5, (int) uF.parseToDouble(innerList.get(2)));
					pst.setInt(6, uF.parseToInt(innerList.get(4)));
					pst.setInt(7, 0);
					pst.setInt(8, 0);
					pst.setInt(9, userTypeId);
//					 System.out.println("EMPG/1180---pst ===>> " + pst);
					pst.execute();
					pst.close();

					String alertData = "<div style=\"float: left;\"> Received a new Request for Personal Goal from <b>"
							+ CF.getEmpNameMapByEmpId(con, strSessionEmpId) + "</b>. [" + hmUserType.get(userTypeId + "") + "] </div>";
					String strSubAction = "";
					String alertAction = "";
					if (userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))
							|| userTypeId == uF.parseToInt(hmUserTypeId.get(MANAGER))) {
						if (userTypeId == uF.parseToInt(hmUserTypeId.get(CEO)) || userTypeId == uF.parseToInt(hmUserTypeId.get(HOD))) {
							strSubAction = "&currUserType=" + hmUserType.get(userTypeId + "");
						}
//						System.out.println("MPG/1191--strSubAction="+strSubAction);
						alertAction = "GoalKRATargets.action?pType=WR&callFrom=KTDash&strEmpId="+strSessionEmpId+ strSubAction;
					} else {
//						System.out.println("MPG/1196--strSubAction="+strSubAction);
						alertAction = "GoalKRATargets.action?pType=WR&callFrom=KTDash&strEmpId="+strSessionEmpId+ strSubAction;
					}

					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(empid);
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(userTypeId + "");
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();


					if (!alManagers.contains(empid)) {
						alManagers.add(empid);
					}
//					System.out.println("MPG/1212---alManagers="+alManagers);
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return alManagers;
	}
	
	//===end parvez date: 04-09-2021===
	
	
	//===created by parvez date: 29-12-2021===
	//===start===
	private void editNewGoal() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();
		
		String hideOrgid = request.getParameter("hideOrgid");
		String corporateGoal = request.getParameter("corporateGoal");
		String cgoalObjective = request.getParameter("cgoalObjective");
		String cgoalDescription = request.getParameter("cgoalDescription");
		String cmeasurewith = request.getParameter("cmeasurewith");
//		System.out.println("EMPG/126---dataType="+request.getParameter("dataType"));
		
		String cmeasureDollar = request.getParameter("cmeasureDollar");
		String cmeasureEffortsDays = request.getParameter("cmeasureEffortsDays");
		String cmeasureEffortsHrs = request.getParameter("cmeasureEffortsHrs");
		String cmeasureKra = request.getParameter("cmeasureKra");
		String cgoalDueDate = request.getParameter("cgoalDueDate");
		String cgoalEffectDate = request.getParameter("cgoalEffectDate");
		String cgoalWeightage = request.getParameter("cgoalWeightage");
		String goal_id = request.getParameter("goal_id");
		
		
			
		try {
			con = db.makeConnection(con);
			
			
			pst = con.prepareStatement("update goal_details set goal_title=?,goal_objective=?,goal_description=?," +
						"measure_type=?,measure_currency_value=?,measure_currency_id=?,measure_effort_days=?,measure_effort_hrs=?," +
						"due_date=?,weightage=?,emp_ids=?,priority=?,effective_date=? where goal_id=?");
				
			
			pst.setString(1, corporateGoal);		//goal_title
			pst.setString(2, cgoalObjective);		//goal_objective
			pst.setString(3, cgoalDescription);		//goal_description
			pst.setString(4, (cmeasureKra != null && cmeasureKra.equals("Yes")) ? cmeasurewith : "");		//measure_type
			pst.setDouble(5, uF.parseToDouble((cmeasurewith != null && (cmeasurewith.equals("Value") || cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage"))) ? cmeasureDollar : "0"));	//measure_currency_value
			pst.setInt(6, 3);		//measure_currency_id
			pst.setDouble(7, uF.parseToDouble((cmeasurewith != null && cmeasurewith.equals("Effort")) ? cmeasureEffortsDays : "0"));	//measure_effort_days
			pst.setDouble(8, uF.parseToDouble((cmeasurewith != null && cmeasurewith.equals("Effort")) ? cmeasureEffortsHrs : "0"));	//measure_effort_hrs
			pst.setDate(9, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));		//due_date
			
			pst.setDouble(10, uF.parseToDouble(cgoalWeightage));
			pst.setString(11,  ","+getEmpId()+",");
			pst.setInt(12, uF.parseToInt(getPriority()));
			pst.setDate(13, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
			pst.setInt(14, uF.parseToInt(goal_id));	
				
			pst.execute();
			pst.close();
			
			goalId = goal_id; 
			setGoalId(goal_id);
			setGoalTitle(corporateGoal);
			request.setAttribute("goalTitle", corporateGoal);
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rst);
			db.closeConnection(con);
		}
		
		
	}
	//===end===
	
	
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
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getSubmit() {
		return submit;
	}
	public void setSubmit(String submit) {
		this.submit = submit;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getEmpselected() {
		return empselected;
	}
	public void setEmpselected(String empselected) {
		this.empselected = empselected;
	}
	public String getTypeas() {
		return typeas;
	}
	public void setTypeas(String typeas) {
		this.typeas = typeas;
	}

	public String getSuperId() {
		return superId;
	}

	public void setSuperId(String superId) {
		this.superId = superId;
	}
	
	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}

	public String getStrDomain() {
		return strDomain;
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

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getGoalTitle() {
		return goalTitle;
	}

	public void setGoalTitle(String goalTitle) {
		this.goalTitle = goalTitle;
	}

	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}


	public String getFrom() {
		return from;
	}


	public void setFrom(String from) {
		this.from = from;
	}


	public String getCurrUserType() {
		return currUserType;
	}


	public void setCurrUserType(String currUserType) {
		this.currUserType = currUserType;
	}

//===start parvez date: 29-12-2021===
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	
	String goalId;


	public String getGoalId() {
		return goalId;
	}


	public void setGoalId(String goalId) {
		this.goalId = goalId;
	}
	
//===end parvez date: 29-12-2021===	
	
}