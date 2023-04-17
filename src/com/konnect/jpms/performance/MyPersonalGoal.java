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

public class MyPersonalGoal extends ActionSupport implements ServletRequestAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strSessionEmpId;
	String strEmpOrgId;
	String strSessionUserType;
	String strUserTypeId;

	CommonFunctions CF;

	private String del;
	

	private String clevel;
	private String cgrade; 
	private String cemp;
	private String strDesignationUpdate; 
	private String strGoalEmpId;
	private String empselected;
	
	private String priority;
	
	private String operation;
	private String submit;
	
	private String goal_id;
	private  String type;
	private String typeas;
	
	private String superId;
    private String from;
    private String fromPage;

 
    private String policy_id;



	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strEmpOrgId = (String) session.getAttribute(ORGID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID); 

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/MyPersonalGoal.jsp");
		request.setAttribute(TITLE, "Goal Setting");
//		System.out.println("operation==>"+getOperation()+"=>getType()=>"+getType()+"=>typeas=>"+typeas);
//		System.out.println("getSubmit==>"+getSubmit());
		if (getOperation() != null && getOperation().equals("A")) {
			if(getSubmit()!=null && getSubmit().equals("Save")) {
				insertNewGoal();
				if(getType()!=null && getType().equals("type")) {
					if(typeas != null && typeas.equals("target")) {
						return "TSUCCESS";
					} else if(typeas != null && typeas.equals("goal")) {
						return "GSUCCESS";
					} else if(typeas != null && typeas.equals("KRA")) {
						return "KSUCCESS";
					}
//					return "update";
				} else {
					if(typeas != null && typeas.equals("target")) {
						return "TSUCCESS";
					} else if(typeas != null && typeas.equals("goal")) {
						return "GSUCCESS";
					} else if(typeas != null && typeas.equals("KRA")) {
						return "KSUCCESS";
					}
//					return SUCCESS;
				}
			}
		} else if (getOperation() != null && getOperation().equals("D")) {
			deleteGoal();
			if(getType()!=null && getType().equals("type")) {
				return "update";
			} else {
				return SUCCESS;
			}
		}
//		System.out.println("mypersonal goal java from==>"+getFromPage());
		return LOAD;

	}

	

	private void deleteGoal() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uf = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select goal_title from goal_details where goal_id=?");
			pst.setInt(1, uf.parseToInt(getGoal_id()));
			rs = pst.executeQuery();
			String goaltitle= "";
			while(rs.next()) {
				goaltitle = uf.showData(rs.getString("goal_title"),"");
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("delete from goal_details where goal_id=?");
			pst.setInt(1, uf.parseToInt(getGoal_id()));
			pst.execute();
			pst.close();
			
			pst=con.prepareStatement("delete from goal_kras where goal_id=?");
			pst.setInt(1, uf.parseToInt(getGoal_id()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+goaltitle+" has been deleted successfully."+END);
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

//	private String getGoalID(String gid) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rst = null;
//		String managerID = "";
//
//		try {
//			con = db.makeConnection(con);
//
//			if (gid != null && !gid.equals("")) {
//				pst = con
//						.prepareStatement("select goal_id from goal_details where goal_parent_id in("
//								+ gid + ")");
//				rst = pst.executeQuery();
//				int c = 0;
//				while (rst.next()) {
//					if (c == 0) {
//						managerID = rst.getString(1);
//					} else {
//						managerID += "," + rst.getString(1);
//					}
//					c++;
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rst);
//		}
//		return managerID;
//	}

//	private String getManagerID(String id) {
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rst = null;
//		UtilityFunctions uf = new UtilityFunctions();
//		String managerID = "";
//
//		try {
//			con = db.makeConnection(con);
//
//			pst = con
//					.prepareStatement("select goal_id from goal_details where goal_parent_id=?");
//			pst.setInt(1, uf.parseToInt(id));
//			rst = pst.executeQuery();
//			int a = 0;
//			while (rst.next()) {
//				if (a == 0) {
//					managerID = rst.getString(1);
//				} else {
//					managerID += "," + rst.getString(1);
//				}
//				a++;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rst);
//		}
//		return managerID;
//	}

	private void insertNewGoal() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();

//		String strOrg = request.getParameter("strOrg");
		String hideOrgid = request.getParameter("hideOrgid");
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
//		String[] cKRACount = request.getParameterValues("cKRACount");
//		String[] cKRA = request.getParameterValues("cKRA");
		String cmkwith = request.getParameter("cmkwith");
		String cgoalDueDate = request.getParameter("cgoalDueDate");
		String cgoalEffectDate = request.getParameter("cgoalEffectDate");
		String cgoalFeedback = request.getParameter("cgoalFeedback");
		String corientation = request.getParameter("corientation");
		String cgoalWeightage = request.getParameter("cgoalWeightage");
		// String clevel = request.getParameter("clevel");
		// String cgrade = request.getParameter("cgrade");
		// String cemp = request.getParameter("cemp");
		String cKRACount = request.getParameter("cKRACount");

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
		String monthday = request.getParameter("monthday");
		String month = request.getParameter("month");*/
		
		String teamGoalId = request.getParameter("teamGoalList");
		String goalalignYesno = request.getParameter("goalalignYesno");
		String perspectiveYesno = request.getParameter("perspectiveYesno");
		String strPerspective = request.getParameter("strPerspective");
		
		String createReviewYesno = request.getParameter("createReviewYesno");
		
//		System.out.println("teamGoalId ===>>> " + teamGoalId);
//		System.out.println("goalalignYesno ===>>> " + goalalignYesno);

		String goalCnt = request.getParameter("goalCnt");
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select max(super_id) from goal_details");
			rst = pst.executeQuery();

			while (rst.next()) {
				int intSupId = rst.getInt(1);
				intSupId++;
//				System.out.println("superID==>"+intSupId);
				setSuperId(intSupId+""); 
			}
			rst.close();
			pst.close();
			
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
			
			/*if (frequency == null || frequency.equals("")) {
				frequency = "1";
			}
			StringBuilder kras = null;
			String frequency_day = null;
			String frequency_month = null;
			String weeklyDay = null;
			if (frequency != null && frequency.equals("2")) {
				weeklyDay = weekday;
				frequency_day = null;
				frequency_month = null;
			} else if (frequency != null && frequency.equals("3")) {
				weeklyDay = null;
				frequency_day = day;
				frequency_month = null;
			} else if (frequency != null && frequency.equals("4")) {
				weeklyDay = null;
				frequency_day = monthday;
				frequency_month = month;
			} else if (frequency != null && frequency.equals("5")) {
				weeklyDay = null;
				frequency_day = monthday;
				frequency_month = month;
			} else if (frequency != null && frequency.equals("6")) {
				weeklyDay = null;
				frequency_day = annualDay;
				frequency_month = annualMonth;
			}*/

			
			if(getEmpselected()!=null && !getEmpselected().equals("") && !getEmpselected().equals("0")) {
				List<String> tmpselectEmpList=Arrays.asList(getEmpselected().split(","));
				Set<String> empSet = new HashSet<String>(tmpselectEmpList);
				Iterator<String> itr = empSet.iterator();
				int i=0;
				String empList=null;
				while (itr.hasNext()) {
					String empid = (String) itr.next();
					if(i==0) {
						empList=","+empid.trim()+",";
					} else {
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
			
			pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_objective,goal_description,goal_attribute," +
				"measure_type,measure_currency_value,measure_currency_id,measure_effort_days,measure_effort_hrs,due_date,is_feedback,orientation_id," +
				"weightage,emp_ids,level_id,grade_id,is_measure_kra,measure_kra,measure_type1,measure_currency_value1,measure_kra_days,measure_kra_hrs," +
				"entry_date,user_id,frequency,freq_year_type,recurring_years,frequency_month,priority,effective_date,goalalign_with_teamgoal," +
				"goal_element,org_id,super_id,peer_ids,manager_ids,hr_ids,anyone_ids,align_with_perspective,perspective_id,review_this_goal)" +
				"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?)");
			if(getTypeas()!= null && getTypeas().equals("KRA")) {
				pst.setInt(1, INDIVIDUAL_KRA);
			} else if(getTypeas()!= null && getTypeas().equals("target")) {
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
			pst.setDouble(8, uF.parseToDouble((cmeasurewith != null && (cmeasurewith.equals("Value") || cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage"))) ? cmeasureDollar : "0"));
			pst.setInt(9, 3);
			pst.setDouble(10, uF.parseToDouble(cmeasurewith != null && cmeasurewith.equals("Effort") ? cmeasureEffortsDays : "0"));
			pst.setDouble(11, uF.parseToDouble(cmeasurewith != null && cmeasurewith.equals("Effort") ? cmeasureEffortsHrs : "0"));
			pst.setDate(12, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
			pst.setBoolean(13, uF.parseToBoolean(cgoalFeedback));
			pst.setInt(14, uF.parseToInt(corientation));
			pst.setDouble(15, uF.parseToDouble(cgoalWeightage));
			if(getTypeas() != null && getTypeas().equals("goal")) {
				pst.setString(16, ","+strSessionEmpId+",");
			} else {
				pst.setString(16, getCemp());
			}
			pst.setString(17, clevel);
			pst.setString(18, strDesignationUpdate);
			pst.setBoolean(19, uF.parseToBoolean(cmeasureKra));
			pst.setString(20, cAddMKra);
			/*pst.setString(21, cmkwith);
			pst.setDouble(22,uf.parseToDouble(cmkwith.equals("Amount") ? cmeasurekraDollar : "0"));
			pst.setDouble(23,uf.parseToDouble(cmkwith.equals("Effort") ? cmeasurekraEffortsDays : "0"));
			pst.setDouble(24,uf.parseToDouble(cmkwith.equals("Effort") ? cmeasurekraEffortsHrs : "0"));*/
			pst.setString(21, cmkwith);
			pst.setDouble(22,uF.parseToDouble("0"));
			pst.setDouble(23,uF.parseToDouble("0"));
			pst.setDouble(24,uF.parseToDouble("0"));
			pst.setDate(25, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(26, uF.parseToInt(strSessionEmpId));
			pst.setInt(27, uF.parseToInt(frequency));
			pst.setInt(28, uF.parseToInt(strYearType));
			pst.setString(29, recurringYears.toString());
			pst.setString(30, frequencyMonths.toString());
			pst.setInt(31, uF.parseToInt(getPriority()));
			pst.setDate(32, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
//			System.out.println("uF.parseToBoolean(goalalignYesno) === >>> " + uF.parseToBoolean(goalalignYesno));
			pst.setBoolean(33, uF.parseToBoolean(goalalignYesno));
			pst.setInt(34, uF.parseToInt(goalElements));
//			System.out.println("strEmpOrgId ===> "+strEmpOrgId);
			if(getTypeas() != null && getTypeas().equals("goal")) {
				pst.setInt(35, uF.parseToInt(strEmpOrgId));
			} else {
				pst.setInt(35, uF.parseToInt(hideOrgid));
			}
			if(getTypeas()!= null && getTypeas().equals("KRA") && uF.parseToInt(goalCnt)>0) {
				pst.setInt(36, uF.parseToInt(getSuperId()));
			} else {
				pst.setInt(36, 0);
			}
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
			pst.setBoolean(41, uF.parseToBoolean(perspectiveYesno));
			pst.setInt(42, uF.parseToInt(strPerspective));
			pst.setString(43, createReviewYesno);
			System.out.println("goal pst==>"+pst);
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
			
			if(getTypeas()== null || !getTypeas().equals("KRA") || !getTypeas().equals("target")) {
				//Start By Dattatray Date:27-08-21
				List<String> alManagers = null;
				if (uF.parseToBoolean(CF.getIsWorkFlow())) {
					alManagers = insertApprovalMember(con, pst, rst, uF.parseToInt(individual_goal_id+""), uF);
				}//End By Dattatray Date:27-08-21
			}
			
//			System.out.println("individual_goal_id " + individual_goal_id);

//			***************************** Goal Frequency Start ************************************
			GoalScheduler scheduler = new GoalScheduler(request, session, CF, uF, strSessionEmpId);
			scheduler.updateGoalDetails(individual_goal_id+"");
//			***************************** Goal Frequency End ************************************
			
			
			int ckracountserial = uF.parseToInt(cKRACount);
			StringBuilder sbKras = new StringBuilder();
			for (int i = 0; i <=ckracountserial; i++) {
				String cKRA = request.getParameter("cKRA_"+i+"_0");
				String cKRAWeightage = request.getParameter("cKRAWeightage_"+i+"_0");
				
				if (cKRA != null && !cKRA.equals("")) {
					String[] cKRATask = request.getParameterValues("cKRATask_"+i+"_0");
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
					if(getTypeas().equals("KRA")) {
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
					
					
//					if(getCemp()!=null && !getCemp().equals("")) {
//						String emp=getCemp().substring(1, getCemp().length()-1);
//						List<String> cemplist=Arrays.asList(emp.split(","));
//						Set<String> setCemp=new HashSet<String>(cemplist);
//						Iterator<String> it=setCemp.iterator();
//						List<String> cEmpLEvelList=new ArrayList<String>();
//						Map<String, String> hmEmpLevelMap =CF.getEmpLevelMap(con);
//						while(it.hasNext()) {
//							String val=it.next();
////							System.out.println("val ===> "+val);
//							String levelID = hmEmpLevelMap.get(val.trim());
//							if(levelID != null) {
//								cEmpLEvelList.add(levelID);
//							}
//							
//							pst = con.prepareStatement("insert into emp_kras (emp_id, entry_date, effective_date, is_approved, approved_by, kra_order, kra_description) values (?,?,?,?,?,?,?)");
//							pst.setInt(1, uF.parseToInt(val));
//							pst.setDate(2, uF.getCurrentDate(CF.getStrReportDateFormat()));
//							pst.setDate(3, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
//							pst.setBoolean(4, true);
//							pst.setInt(5, uF.parseToInt(strSessionEmpId));
//							pst.setInt(6, i);
//							pst.setString(7, cKRA[i]);
//							pst.execute();
//						}
//						Set<String> setLvlList = new HashSet<String>(cEmpLEvelList);
//						Iterator<String> it1=setLvlList.iterator();
//						while(it1.hasNext()) {
//							String val=it1.next();
//							
//						pst = con.prepareStatement("insert into kra_details(kra,attribute_id,level_id,measurable,goal_id)"
//										+ "values(?,?,?,?,?)");
//						pst.setString(1, cKRA[i]);
//						pst.setInt(2, uF.parseToInt(cgoalAlignAttribute));
//						pst.setInt(3, uF.parseToInt(val.trim()));
//						pst.setInt(4, 1);
//						pst.setInt(5, individual_goal_id);
//						pst.execute();
//						}
//					}
					
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
			
//			System.out.println("getTypeas() ===>> " + getTypeas());
			if(getTypeas() != null && getTypeas().equals("goal")) {
//				UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//				userAlerts.setStrDomain(strDomain);
//				userAlerts.setStrEmpId(strSessionEmpId);
//				userAlerts.set_type(MY_PERSONAL_GOAL_ALERT);
//				userAlerts.setStatus(INSERT_ALERT);
//				Thread t = new Thread(userAlerts);
//				t.run();
			} else {
//				System.out.println("getEmpselected ===>> " + getEmpselected());
				List<String> selectEmpList = Arrays.asList(getEmpselected().split(","));
				Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
				
//				System.out.println("selectEmpList ===>> " + selectEmpList);
				for(int i=0; selectEmpList!= null && !selectEmpList.isEmpty() && i<selectEmpList.size(); i++) {
					if(!selectEmpList.get(i).equals("") && uF.parseToInt(selectEmpList.get(i)) > 0) {
						String gktType = "";
						if(cAddMKra != null && cAddMKra.equals("KRA")) {
							gktType = "KRA";
						} else if(cAddMKra != null && cAddMKra.equals("Measure")) {
							gktType = "Target";
						}
						String alertData = "<div style=\"float: left;\"> You have received a new "+gktType+" ("+corporateGoal+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
						String alertAction = "MyHR.action?pType=WR";
						
//						System.out.println("selectEmpList.get(i) ===>> " + selectEmpList.get(i));
						UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
						userAlerts.setStrDomain(strDomain);
						userAlerts.setStrEmpId(selectEmpList.get(i));
						userAlerts.setStrData(alertData);
						userAlerts.setStrAction(alertAction);
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(selectEmpList.get(i));
//						if(cAddMKra != null && cAddMKra.equals("KRA")) {
//							userAlerts.set_type(MY_KRA_ALERT);
//						} else if(cAddMKra != null && cAddMKra.equals("Measure")) {
//							userAlerts.set_type(MY_TARGET_ALERT);
//						}
//						userAlerts.setStatus(INSERT_ALERT);
//						Thread t = new Thread(userAlerts);
//						t.run();
					}
				}
				
				sendIndiGoalMailToEmp(con, individual_goal_id, selectEmpList, cAddMKra, sbKras.toString(), cmeasureDollar, cmeasureEffortsDays, cmeasureEffortsHrs);
			}
			
			//System.out.println("kras==>"+kras.toString());
			if(getTypeas() != null && (getTypeas().equalsIgnoreCase("goal") || getTypeas().equalsIgnoreCase("target"))) {
				session.setAttribute(MESSAGE,SUCCESSM +"<b>"+corporateGoal+" </b> "+typeas+" added Successfully."+ END );
			} else if (getTypeas() != null && getTypeas().equalsIgnoreCase("KRA")) {
				session.setAttribute(MESSAGE,SUCCESSM +"<b> KRA  </b> added Successfully."+ END );
			}
			if(createReviewYesno !=null && createReviewYesno.equals("Yes")) {
				createReviewForKRA(con, uF, individual_goal_id);
			}
			
			insertNewKRAGoal(con, uF);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeResultSet(rst);
			db.closeConnection(con);
		}
	}

	
	private void createReviewForKRA(Connection con, UtilityFunctions uF, int individualGoalId) {
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
				
			String corporateGoal = request.getParameter("corporateGoal");
			String cgoalObjective = request.getParameter("cgoalObjective");
			String cgoalDescription = request.getParameter("cgoalDescription");
			String cgoalAlignAttribute = request.getParameter("cgoalAlignAttribute");
			String goalElements = request.getParameter("goalElements");
			String cgoalDueDate = request.getParameter("cgoalDueDate");
			String cgoalEffectDate = request.getParameter("cgoalEffectDate");
//			String cgoalFeedback = request.getParameter("cgoalFeedback");
			String corientation = request.getParameter("corientation");
			String cgoalWeightage = request.getParameter("cgoalWeightage");
			String frequency = request.getParameter("frequency");
			
			List<String> orientMemIds = CF.getOrientationMemberDetails(con, uF.parseToInt(corientation));
			StringBuilder sbMemIds = null;
			if(orientMemIds !=null) {
				for(int i=0; i<orientMemIds.size(); i++) {
					if(sbMemIds==null) {
						sbMemIds = new StringBuilder();
						sbMemIds.append(orientMemIds.get(i));
					} else {
						sbMemIds.append(","+orientMemIds.get(i));
					}
				}
			}
			if(sbMemIds==null) {
				sbMemIds = new StringBuilder();
			}
			
			pst = con.prepareStatement("insert into appraisal_details(appraisal_name,oriented_type,employee_id,self_ids,appraisal_type," +
				"frequency,from_date,to_date,usertype_member,appraisal_description,is_publish,goal_id,added_by,entry_date)"
				+ "values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
			pst.setString(1, corporateGoal);
			pst.setString(2, corientation);
			pst.setString(3, getCemp());
			pst.setString(4, getCemp());
			pst.setString(5, "Feedback");
			pst.setString(6, frequency);
			pst.setDate(7, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
			pst.setString(9, sbMemIds.toString());
			pst.setString(10, cgoalDescription);
			pst.setBoolean(11, true);
			pst.setInt(12, individualGoalId);
			pst.setInt(13, uF.parseToInt(strSessionEmpId));
			pst.setDate(14, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.executeUpdate();
			pst.close();
		
			String reviewId = null;
			pst = con.prepareStatement("select max(appraisal_details_id) from appraisal_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				reviewId = rst.getString(1);
			}
			rst.close();
			pst.close();
			
			if(cgoalEffectDate != null && !cgoalEffectDate.equals("") && cgoalDueDate!= null && !cgoalDueDate.equals("")) {
//				***************************** appraisal Frequency Start ************************************
				AppraisalScheduler scheduler = new AppraisalScheduler(request, session, CF, uF, strSessionEmpId);
				scheduler.updateAppraisalDetails(reviewId);
//				***************************** appraisal Frequency End ************************************
			}
			
			List<String> selfList = new ArrayList<String>();
			if(getCemp() !=null) {
				selfList = Arrays.asList(getCemp().split(","));
			}
			List<String> appFreqIDs = new ArrayList<String>();
			pst = con.prepareStatement("select appraisal_freq_id from appraisal_details_frequency where appraisal_id=? and is_delete=false");
			pst.setInt(1, uF.parseToInt(reviewId));
			rst = pst.executeQuery();
			if(rst.next()) {
				appFreqIDs.add(rst.getString("appraisal_freq_id"));
			}
			
			Map<String, String> hmOrientMemberID = CF.getOrientMemberID(con);
			for(int i=0; appFreqIDs!= null && !appFreqIDs.isEmpty() && i<appFreqIDs.size(); i++) {
				for(int j=0; selfList!= null && !selfList.isEmpty() && j<selfList.size(); j++) {
					if(uF.parseToInt(selfList.get(j))==0) {
						continue;
					}
					Map<String, String> hmManagerIds = CF.getEmpSupervisorIdMap(con);
					Map<String, String> hmHRIds = CF.getEmpHRIdMap(con, uF);
					Map<String, String> hmHODIds = CF.getEmpHODIdMap(con, uF);
					pst = con.prepareStatement("insert into appraisal_reviewee_details(reviewee_id,hr_ids,supervisor_ids," +
						"hod_ids,added_by,entry_date,appraisal_id,appraisal_freq_id) " +
						"values(?,?,?,?, ?,?,?,?)");
					pst.setInt(1, uF.parseToInt(selfList.get(j)));
					if (hmOrientMemberID!=null && hmOrientMemberID.get("HR") != null && orientMemIds.contains(hmOrientMemberID.get("HR"))) {
						pst.setString(2, ","+hmHRIds.get(selfList.get(j))+",");
					} else {
						pst.setString(2, null);
					}
					if (hmOrientMemberID!=null && hmOrientMemberID.get("Manager") != null && orientMemIds.contains(hmOrientMemberID.get("Manager"))) {
						pst.setString(3, ","+hmManagerIds.get(selfList.get(j))+",");
					} else {
						pst.setString(3, null);
					}
					if (hmOrientMemberID!=null && hmOrientMemberID.get("HOD") != null && orientMemIds.contains(hmOrientMemberID.get("HOD"))) {
						pst.setString(4, ","+hmHODIds.get(selfList.get(j))+",");
					} else {
						pst.setString(4, null);
					}
					pst.setInt(5, uF.parseToInt(strSessionEmpId));
					pst.setDate(6, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(7, uF.parseToInt(reviewId));
					pst.setInt(8, uF.parseToInt(appFreqIDs.get(i)));
					int x = pst.executeUpdate();
					pst.close();
	
				}
			}
			
			
//			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			pst = con.prepareStatement("insert into appraisal_main_level_details(level_title,appraisal_id,attribute_id,section_weightage,added_by," +
				"hr,manager,peer,self,subordinate,grouphead,vendor,client,entry_date,ceo,hod,other_peer) " +
				"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
			pst.setString(1, "Section 1");
			pst.setInt(2, uF.parseToInt(reviewId));
			pst.setInt(3, uF.parseToInt(cgoalAlignAttribute));
			pst.setString(4, cgoalWeightage);
			pst.setInt(5, uF.parseToInt(strSessionEmpId));
			if (hmOrientMemberID!=null && hmOrientMemberID.get("HR") != null && orientMemIds.contains(hmOrientMemberID.get("HR"))) {
				pst.setInt(6, 2);
			} else {
				pst.setInt(6, 0);
			}
			if (hmOrientMemberID!=null && hmOrientMemberID.get("Manager") != null && orientMemIds.contains(hmOrientMemberID.get("Manager"))) {
				pst.setInt(7, 2);
			} else {
				pst.setInt(7, 0);
			}
			if (hmOrientMemberID!=null && hmOrientMemberID.get("Peer") != null && orientMemIds.contains(hmOrientMemberID.get("Peer"))) {
				pst.setInt(8, 2);
			} else {
				pst.setInt(8, 0);
			}
			if (hmOrientMemberID!=null && hmOrientMemberID.get("Self") != null && orientMemIds.contains(hmOrientMemberID.get("Self"))) {
				pst.setInt(9, 1);
			} else {
				pst.setInt(9, 0);
			}
			if (hmOrientMemberID!=null && hmOrientMemberID.get("Sub-ordinate") != null && orientMemIds.contains(hmOrientMemberID.get("Sub-ordinate"))) {
				pst.setInt(10, 2);
			} else {
				pst.setInt(10, 0);
			}
			if (hmOrientMemberID!=null && hmOrientMemberID.get("GroupHead") != null && orientMemIds.contains(hmOrientMemberID.get("GroupHead"))) {
				pst.setInt(11, 2);
			} else {
				pst.setInt(11, 0);
			}
			if (hmOrientMemberID!=null && hmOrientMemberID.get("Vendor") != null && orientMemIds.contains(hmOrientMemberID.get("Vendor"))) {
				pst.setInt(12, 2);
			} else {
				pst.setInt(12, 0);
			}
			if (hmOrientMemberID!=null && hmOrientMemberID.get("Client") != null && orientMemIds.contains(hmOrientMemberID.get("Client"))) {
				pst.setInt(13, 2);
			} else {
				pst.setInt(13, 0);
			}
			
			pst.setTimestamp(14, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			
			if (hmOrientMemberID!=null && hmOrientMemberID.get("CEO") != null && orientMemIds.contains(hmOrientMemberID.get("CEO"))) {
				pst.setInt(15, 2);
			} else {
				pst.setInt(15, 0);
			}
			if (hmOrientMemberID!=null && hmOrientMemberID.get("HOD") != null && orientMemIds.contains(hmOrientMemberID.get("HOD"))) {
				pst.setInt(16, 2);
			} else {
				pst.setInt(16, 0);
			}
			
			if (hmOrientMemberID!=null && hmOrientMemberID.get("Other Peer") != null && orientMemIds.contains(hmOrientMemberID.get("Other Peer"))) {
				pst.setInt(17, 2);
			} else {
				pst.setInt(17, 0);
			}
			pst.executeUpdate();
			pst.close();
			
			int main_level_id = 0;
			pst = con.prepareStatement("select max(main_level_id) from appraisal_main_level_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				main_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			pst = con.prepareStatement("insert into appraisal_level_details(appraisal_system,scorecard_type,appraisal_id,main_level_id," +
				"attribute_id,subsection_weightage,added_by,entry_date) values(?,?,?,?, ?,?,?,?)");
			pst.setInt(1, 4);
			pst.setInt(2, 0);
			pst.setInt(3, uF.parseToInt(reviewId));
			pst.setInt(4, main_level_id);
			pst.setInt(5, uF.parseToInt(cgoalAlignAttribute));
			pst.setString(6, "100");
			pst.setInt(7, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(8, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone()) + "" + uF.getCurrentTime(CF.getStrTimeZone()), DBDATE + DBTIME));
			pst.execute();
			pst.close();
			
			int appraisal_level_id = 0;
			pst = con.prepareStatement("select max(appraisal_level_id) from appraisal_level_details where appraisal_id=?");
			pst.setInt(1, uF.parseToInt(reviewId));
			rst = pst.executeQuery();
			while (rst.next()) {
				appraisal_level_id = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			Map<String, List<String>> hmKRAName = new HashMap<String, List<String>>();
			String kraTypes = INDIVIDUAL_GOAL+","+INDIVIDUAL_KRA+","+EMPLOYEE_KRA;
			pst = con.prepareStatement("select * from goal_kras where goal_id=? and goal_type in ("+kraTypes+")");
			pst.setInt(1, individualGoalId);
			rst = pst.executeQuery();
	//		System.out.println("pst ===>> " + pst);
			while (rst.next()) {
				List<String> innerList = hmKRAName.get(rst.getString("goal_kra_id"));
				if(innerList==null) innerList = new ArrayList<String>();
				innerList.add(rst.getString("kra_description"));
				innerList.add(rst.getString("kra_weightage"));
//				innerList.add(rst.getString("kra_description"));
				hmKRAName.put(rst.getString("goal_kra_id"), innerList);
			}
			rst.close();
			pst.close();
			
			Iterator<String> it = hmKRAName.keySet().iterator();
			while(it.hasNext()) {
				String kraId = it.next();
				List<String> innerList = hmKRAName.get(kraId);
	//			System.out.println("GOal ID ===> "+goalId[i]+ "  optiona === "+optiona + "  optiona=b === "+optionb + "  optionc === "+optionc + "  optiond === "+optiond);
				
				pst = con.prepareStatement("insert into question_bank(question_text,option_a,option_b,option_c,option_d,option_e," +
					"rate_option_a,rate_option_b,rate_option_c,rate_option_d,rate_option_e,correct_ans,is_add,question_type," +
					"goal_kra_target_id,app_system_type,kra_id)values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
				pst.setString(1, innerList.get(0));
				pst.setString(2, "");
				pst.setString(3, "");
				pst.setString(4, "");
				pst.setString(5, "");
				pst.setString(6, "");
				pst.setInt(7, 0);
				pst.setInt(8, 0);
				pst.setInt(9, 0);
				pst.setInt(10, 0);
				pst.setInt(11, 0);
				pst.setString(12, "");
				pst.setBoolean(13, false);
				pst.setInt(14, 11); // Grade with Rating
				pst.setInt(15, individualGoalId);
				pst.setInt(16, 4);
				pst.setInt(17, uF.parseToInt(kraId));
				pst.executeUpdate();
				pst.close();
				
				int queId=0;
				pst = con.prepareStatement("select max(question_bank_id) from question_bank");
				rst = pst.executeQuery();
				while (rst.next()) {
					queId = rst.getInt(1);
				}
				rst.close();
				pst.close();
	
				pst = con.prepareStatement("insert into appraisal_question_details(question_id,attribute_id,appraisal_id,appraisal_level_id," +
					"answer_type,goal_kra_target_id,app_system_type,weightage,kra_id) values(?,?,?,?, ?,?,?,?, ?)");
				pst.setInt(1, queId);
				pst.setInt(2, uF.parseToInt(cgoalAlignAttribute));
				pst.setInt(3, uF.parseToInt(reviewId));
				pst.setInt(4, appraisal_level_id);
				pst.setInt(5, 11);
				pst.setInt(6, individualGoalId);
				pst.setInt(7, 4);
				pst.setDouble(8, uF.parseToDouble(innerList.get(1)));
				pst.setInt(9, uF.parseToInt(kraId));
				pst.execute();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @author Dattatray
	 * @since 27-08-21
	 * 
	 * @param con
	 * @param pst
	 * @param rs
	 * @param nRecritmentId
	 * @param uF
	 * @return
	 */
	private List<String> insertApprovalMember(Connection con, PreparedStatement pst, ResultSet rs, int nRecritmentId, UtilityFunctions uF) {
		List<String> alManagers = new ArrayList<String>();
		try {

			Map<String, String> hmEmpUserTypeId = CF.getEmployeeIdUserTypeIdMap(con);
			Map<String, String> hmUserType = CF.getUserTypeMap(con);
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);

			pst = con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where "
					+ " policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
			pst.setInt(1, uF.parseToInt(getPolicy_id()));
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
				// System.out.println("innerList.get(3)+memid====>"+innerList.get(3)+memid+"=====>"+request.getParameter(innerList.get(3)+memid));
				String empid = request.getParameter(innerList.get(3) + memid);

				if (empid != null && !empid.equals("")) {
					int userTypeId = memid;
					if (uF.parseToInt(innerList.get(0)) == 3) {
						userTypeId = uF.parseToInt(hmEmpUserTypeId.get(empid));
					}
					// System.out.println("approval empid====>"+empid);
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
					// System.out.println("pst ===>> " + pst);
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
						alertAction = "GoalKRATargets.action?pType=WR&callFrom=KTDash&strEmpId="+strSessionEmpId+strSubAction;
					} else {
						alertAction = "GoalKRATargets.action?pType=WR&callFrom=KTDash&strEmpId="+strSessionEmpId+strSubAction;
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
	
	
	private void insertNewKRAGoal(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rst = null;

		try {
			String goalCnt = request.getParameter("goalCnt");
			String hideOrgid = request.getParameter("hideOrgid");
//			System.out.println("goalCnt==>"+goalCnt);
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
					String cAddMKra = request.getParameter("cAddMKra_"+a);
					String cmkwith = request.getParameter("cmkwith");
					String cgoalDueDate = request.getParameter("cgoalDueDate_"+a);
					String cgoalEffectDate = request.getParameter("cgoalEffectDate_"+a);
					String cgoalFeedback = request.getParameter("cgoalFeedback");
					String corientation = request.getParameter("corientation_"+a);
					String cgoalWeightage = request.getParameter("cgoalWeightage_"+a);
					
					String cKRACount = request.getParameter("cKRACount_"+a);
		
					String frequency = request.getParameter("frequency_"+a);
		
					String weekday = request.getParameter("weekday_"+a);
					String annualDay = request.getParameter("annualDay_"+a);
					String annualMonth = request.getParameter("annualMonth_"+a);
					String day = request.getParameter("day_"+a);
					String monthday = request.getParameter("monthday_"+a);
					String month = request.getParameter("month_"+a);
					String teamGoalId = request.getParameter("teamGoalList");
					String goalalignYesno = request.getParameter("goalalignYesno");
					String perspectiveYesno = request.getParameter("perspectiveYesno_"+a);
					String strPerspective = request.getParameter("strPerspective_"+a);
		//			System.out.println("teamGoalId ===>>> " + teamGoalId);
		//			System.out.println("goalalignYesno ===>>> " + goalalignYesno);
//					System.out.println("kras==>"+corporateGoal);
					if(frequency == null  || frequency.equals("")) {
						frequency = "1";
					}
					String frequency_day = null;
					String frequency_month = null;
					String weeklyDay = null;
					if (frequency != null && frequency.equals("2")) {
						weeklyDay = weekday;
						frequency_day = null;
						frequency_month = null;
					} else if (frequency != null && frequency.equals("3")) {
						weeklyDay = null;
						frequency_day = day;
						frequency_month = null;
					} else if (frequency != null && frequency.equals("4")) {
						weeklyDay = null;
						frequency_day = monthday;
						frequency_month = month;
					} else if (frequency != null && frequency.equals("5")) {
						weeklyDay = null;
						frequency_day = monthday;
						frequency_month = month;
					} else if (frequency != null && frequency.equals("6")) {
						weeklyDay = null;
						frequency_day = annualDay;
						frequency_month = annualMonth;
					}
		
					
					if(getEmpselected()!=null && !getEmpselected().equals("") && !getEmpselected().equals("0")) {
						List<String> tmpselectEmpList=Arrays.asList(getEmpselected().split(","));
						Set<String> empSet = new HashSet<String>(tmpselectEmpList);
						Iterator<String> itr = empSet.iterator();
						int i=0;
						String empList=null;
						while (itr.hasNext()) {
							String empid = (String) itr.next();
							if(i==0) {
								empList=","+empid.trim()+",";
							} else {
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
					
					pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_objective,goal_description,goal_attribute," +
						"measure_type,measure_currency_value,measure_currency_id,measure_effort_days,measure_effort_hrs,due_date,is_feedback," +
						"orientation_id,weightage,emp_ids,level_id,grade_id,is_measure_kra,measure_kra,measure_type1,measure_currency_value1," +
						"measure_kra_days,measure_kra_hrs,entry_date,user_id,frequency,weekday,frequency_day,frequency_month,priority,effective_date," +
						"goalalign_with_teamgoal,goal_element,org_id,super_id,peer_ids,manager_ids,hr_ids,anyone_ids,align_with_perspective,perspective_id)" +
						"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					if(getTypeas().equals("KRA")) {
						pst.setInt(1, INDIVIDUAL_KRA);
					} else if(getTypeas().equals("target")) {
						pst.setInt(1, INDIVIDUAL_TARGET);
					} else  {
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
					pst.setDouble(8, uF.parseToDouble((cmeasurewith != null && (cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage"))) ? cmeasureDollar : "0"));
					pst.setInt(9, 3);
					pst.setDouble(10, uF.parseToDouble(cmeasurewith != null && cmeasurewith.equals("Effort") ? cmeasureEffortsDays : "0"));
					pst.setDouble(11, uF.parseToDouble(cmeasurewith != null && cmeasurewith.equals("Effort") ? cmeasureEffortsHrs : "0"));
					pst.setDate(12, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
					pst.setBoolean(13, uF.parseToBoolean(cgoalFeedback));
					pst.setInt(14, uF.parseToInt(corientation));
					pst.setDouble(15, uF.parseToDouble(cgoalWeightage));
					if(getTypeas() != null && getTypeas().equals("goal")) {
						pst.setString(16, ","+strSessionEmpId+",");
					} else {
						pst.setString(16, getCemp());
					}
					pst.setString(17, clevel);
					pst.setString(18, strDesignationUpdate);
					pst.setBoolean(19, uF.parseToBoolean(cmeasureKra));
					pst.setString(20, (getTypeas() != null && getTypeas().equals("KRA")) ? "KRA" : cAddMKra);
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
					if(getTypeas() != null && getTypeas().equals("goal")) {
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
					pst.setBoolean(41, uF.parseToBoolean(perspectiveYesno));
					pst.setInt(42, uF.parseToInt(strPerspective));
//					System.out.println("pst2==>"+pst);
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
					

//					***************************** Goal Frequency Start ************************************
					GoalScheduler scheduler = new GoalScheduler(request, session, CF, uF, strSessionEmpId);
					
					scheduler.updateGoalDetails(individual_goal_id+"");
//					***************************** Goal Frequency End ************************************
					
					
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
									" values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
							pst.setInt(1, individual_goal_id);
							pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
							pst.setDate(3, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
							pst.setBoolean(4, true);
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setInt(6, 0);
							pst.setString(7, cKRA);
							if(getTypeas().equals("KRA")) {
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
		
//					System.out.println("getTypeas() ===>> " + getTypeas());
					if(getTypeas() != null && getTypeas().equals("goal")) {
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(strSessionEmpId);
//						userAlerts.set_type(MY_PERSONAL_GOAL_ALERT);
//						userAlerts.setStatus(INSERT_ALERT);
//						Thread t = new Thread(userAlerts);
//						t.run();
					} else {
						List<String> selectEmpList = Arrays.asList(getEmpselected().split(","));
						Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
						
						for(int i=0; selectEmpList!= null && !selectEmpList.isEmpty() && i<selectEmpList.size(); i++) {
							if(!selectEmpList.get(i).equals("") && uF.parseToInt(selectEmpList.get(i)) > 0) {
								String gktType = "";
								if(cAddMKra != null && cAddMKra.equals("KRA")) {
									gktType = "KRA";
								} else if(cAddMKra != null && cAddMKra.equals("Measure")) {
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
								
//								UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//								userAlerts.setStrDomain(strDomain);
//								userAlerts.setStrEmpId(selectEmpList.get(i));
//								if(cAddMKra != null && cAddMKra.equals("KRA")) {
//									userAlerts.set_type(MY_KRA_ALERT);
//								} else if(cAddMKra != null && cAddMKra.equals("Measure")) {
//									userAlerts.set_type(MY_TARGET_ALERT);
//								}
//								userAlerts.setStatus(INSERT_ALERT);
//								Thread t = new Thread(userAlerts);
//								t.run();
							}
						}
						sendIndiGoalMailToEmp(con, individual_goal_id, selectEmpList, cAddMKra, sbKras.toString(), cmeasureDollar, cmeasureEffortsDays, cmeasureEffortsHrs);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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
						if(goalKraTarget != null && goalKraTarget.equals("KRA")) {
							NOTIFICATION_NAME = N_EXECUTIVE_KRA;
						} else if(goalKraTarget != null && goalKraTarget.equals("Measure")) {
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
						if(goalKraTarget != null && goalKraTarget.equals("KRA")) {
							nF.setStrGoalName(goalName);
							nF.setStrKRAName(kras);
						} else if(goalKraTarget != null && goalKraTarget.equals("Measure")) {
							nF.setStrTargetName(goalName);
							if(tAmount != null && !tAmount.equals("")) {
								nF.setStrTargetValue("Rs." + tAmount);
							} else if((tDays != null && !tDays.equals("")) || (tHrs != null && !tHrs.equals(""))) {
								nF.setStrTargetValue(tDays+ " Days " + tHrs + " Hrs ");
							}
						}
						nF.setStrEmpFname(hmEmpInner.get("FNAME"));
						nF.setStrEmpLname(hmEmpInner.get("LNAME"));
						nF.sendNotifications();
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
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

	public String getStrDesignationUpdate() {
		return strDesignationUpdate;
	}

	public void setStrDesignationUpdate(String strDesignationUpdate) {
		this.strDesignationUpdate = strDesignationUpdate;
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

	public String getEmpselected() {
		return empselected;
	}

	public void setEmpselected(String empselected) {
		this.empselected = empselected;
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

	public String getGoal_id() {
		return goal_id;
	}

	public void setGoal_id(String goal_id) {
		this.goal_id = goal_id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}



	public String getFromPage() {
		return fromPage;
	}



	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}



	public String getPolicy_id() {
		return policy_id;
	}



	public void setPolicy_id(String policy_id) {
		this.policy_id = policy_id;
	}
	

	
	
}