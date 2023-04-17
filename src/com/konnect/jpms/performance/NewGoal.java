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

public class NewGoal implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
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
	private String goalTitle;
	private String priority;
	private String dataType;
	private String currUserType;
	
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/NewGoal.jsp");
		request.setAttribute(TITLE, "Goal Setting");
		if (del == null) {
			insertNewGoal();
		} else if (del.equals("del")) {
			//System.out.println("in delete goalTitle==>"+goalTitle);
			deleteGoal1();
		}

		return "success";
 
	}

	private void deleteGoal1() {

		String type = request.getParameter("type");
		String id = request.getParameter("id");

		if (type != null) {
			if (type.equals("corporate")) {
				deleteCorporate(id);
			} else if (type.equals("manager")) {
				deleteManager(id);
			} else if (type.equals("team")) {
				deleteTeam(id);
			} else if (type.equals("individual")) {
				deleteIndividual(id);
			}
		}
		
	}

	private void deleteIndividual(String id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uf = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from goal_details where goal_id=?");
			pst.setInt(1, uf.parseToInt(id));
			pst.execute();
			pst.close();
			
			pst = con.prepareStatement("delete from goal_kras where goal_id=?");
			pst.setInt(1, uf.parseToInt(id));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE,SUCCESSM +"<b>"+getGoalTitle()+" </b>  goal deleted Successfully."+ END );
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void deleteTeam(String id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			String individualID = getGoalID(con, id);

			StringBuilder sbID = new StringBuilder();
			sbID.append(id);

			if (individualID != null && !individualID.equals("")) {
				sbID.append("," + individualID);
				
				pst = con.prepareStatement("delete from goal_kras where goal_id in ("+ individualID + ")");
				pst.execute();
				pst.close();
			}

			pst = con.prepareStatement("delete from goal_details where goal_id in ("+ sbID.toString() + ")");
			pst.execute();
			pst.close();
			session.setAttribute(MESSAGE,SUCCESSM +"<b>"+getGoalTitle()+" </b>  goal deleted Successfully."+ END );
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void deleteManager(String id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			String teamID = getGoalID(con, id);

			String individualID = getGoalID(con, teamID);

			StringBuilder sbID = new StringBuilder();
			sbID.append(id);

			if (teamID != null && !teamID.equals("")) {
				sbID.append("," + teamID);
			}
			if (individualID != null && !individualID.equals("")) {
				sbID.append("," + individualID);
				
				pst = con.prepareStatement("delete from goal_kras where goal_id in ("+ individualID + ")");
				pst.execute();
				pst.close();
			}

			pst = con.prepareStatement("delete from goal_details where goal_id in ("+ sbID.toString() + ")");
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE,SUCCESSM +"<b>"+getGoalTitle()+" </b>  goal deleted Successfully."+ END );
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void deleteCorporate(String id) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			String managerID = getManagerID(con, id);

			String teamID = getGoalID(con, managerID);

			String individualID = getGoalID(con, teamID);

			StringBuilder sbID = new StringBuilder();
			sbID.append(id);

			if (managerID != null && !managerID.equals("")) {
				sbID.append("," + managerID);
			}

			if (teamID != null && !teamID.equals("")) {
				sbID.append("," + teamID);
			}
			if (individualID != null && !individualID.equals("")) {
				sbID.append("," + individualID);
				
				pst = con.prepareStatement("delete from goal_kras where goal_id in ("+ individualID + ")");
				pst.execute();
				pst.close();
			}

			pst = con.prepareStatement("delete from goal_details where goal_id in ("+ sbID.toString() + ")");
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE,SUCCESSM +"<b>"+getGoalTitle()+" </b>  goal deleted Successfully."+ END );
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

//	private void deleteGoal() {
//		Connection con = null;
//		PreparedStatement pst = null;
//		Database db = new Database();
//		db.setRequest(request);
//		ResultSet rst = null;
//		UtilityFunctions uf = new UtilityFunctions();
//
//		try {
//			con = db.makeConnection(con);
//			String type = request.getParameter("type");
//			String id = request.getParameter("id");
//
//			if (type != null) {
//				if (type.equals("corporate")) {
////					System.out.println("corporate type====>" + type);
////					System.out.println("corporate id====>" + id);
//					con.setAutoCommit(false);
//					String managerID = getManagerID(con, id);
//
//					String teamID = getGoalID(con, managerID);
//
//					String individualID = getGoalID(con, teamID);
//
//					pst = con
//							.prepareStatement("delete from goal_details where goal_id=?");
//					pst.setInt(1, uf.parseToInt(id));
//					pst.execute();
//
//					if (managerID != null && !managerID.equals("")) {
//						pst = con
//								.prepareStatement("delete from goal_details where goal_id in ("
//										+ managerID + ")");
//						pst.execute();
//					}
//
//					if (teamID != null && !teamID.equals("")) {
//						pst = con
//								.prepareStatement("delete from goal_details where goal_id in ("
//										+ teamID + ")");
//						pst.execute();
//					}
//					if (individualID != null && !individualID.equals("")) {
//						pst = con
//								.prepareStatement("delete from goal_details where goal_id in ("
//										+ individualID + ")");
//						pst.execute();
//					}
//					con.commit();
//				} else if (type.equals("manager")) {
////					System.out.println("manager type====>" + type);
////					System.out.println("manager id====>" + id);
//					con.setAutoCommit(false);
//
//					String teamID = getGoalID(con, id);
//
//					String individualID = getGoalID(con, teamID);
//
//					pst = con
//							.prepareStatement("delete from goal_details where goal_id=?");
//					pst.setInt(1, uf.parseToInt(id));
//					pst.execute();
//
//					if (teamID != null && !teamID.equals("")) {
//						pst = con
//								.prepareStatement("delete from goal_details where goal_id in ("
//										+ teamID + ")");
//						pst.execute();
//					}
//					if (individualID != null && !individualID.equals("")) {
//						pst = con
//								.prepareStatement("delete from goal_details where goal_id in ("
//										+ individualID + ")");
//						pst.execute();
//					}
//
//					con.commit();
//				} else if (type.equals("team")) {
//					con.setAutoCommit(false);
////					System.out.println("team type====>" + type);
////					System.out.println("team id====>" + id);
//
//					String individualID = getGoalID(con, id);
//
//					pst = con
//							.prepareStatement("delete from goal_details where goal_id=?");
//					pst.setInt(1, uf.parseToInt(id));
//					pst.execute();
//
//					if (individualID != null && !individualID.equals("")) {
//						pst = con
//								.prepareStatement("delete from goal_details where goal_id in ("
//										+ individualID + ")");
//						pst.execute();
//					}
//
//					con.commit();
//				} else if (type.equals("individual")) {
////					System.out.println("individual type====>" + type);
////					System.out.println("individual id====>" + id);
//
//					con.setAutoCommit(false);
//
//					pst = con.prepareStatement("delete from goal_details where goal_id=?");
//					pst.setInt(1, uf.parseToInt(id));
//					pst.execute();
//
//					con.commit();
//				}
//			}
//		} catch (Exception e) {
//			try {
//				con.rollback();
//			} catch (SQLException e1) {
//				e1.printStackTrace();
//			}
//			e.printStackTrace();
//		} finally {
//			db.closeConnection(con);
//			db.closeStatements(pst);
//			db.closeResultSet(rst);
//		}
//	}

	private String getGoalID(Connection con, String gid) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		String managerID = "";

		try {
			if (gid != null && !gid.equals("")) {
				pst = con.prepareStatement("select goal_id from goal_details where goal_parent_id in(" + gid + ")");
				rst = pst.executeQuery();
				int c = 0;
				while (rst.next()) {
					if (c == 0) {
						managerID = rst.getString(1);
					} else {
						managerID += "," + rst.getString(1);
					}
					c++;
				}
				rst.close();
				pst.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return managerID;
	}

	private String getManagerID(Connection con, String id) {
		PreparedStatement pst = null;
		ResultSet rst = null;
		UtilityFunctions uf = new UtilityFunctions();
		String managerID = "";

		try {
			pst = con
					.prepareStatement("select goal_id from goal_details where goal_parent_id=?");
			pst.setInt(1, uf.parseToInt(id));
			rst = pst.executeQuery();
			int a = 0;
			while (rst.next()) {
				if (a == 0) {
					managerID = rst.getString(1);
				} else {
					managerID += "," + rst.getString(1);
				}
				a++;
			}
			rst.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return managerID;
	}

	
	private void insertNewGoal() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uF = new UtilityFunctions();

		String strOrg = request.getParameter("strOrg");
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
//		String[] cKRA = request.getParameterValues("cKRA");
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
		// String clevel = request.getParameter("clevel");
		// String cgrade = request.getParameter("cgrade");
		// String cemp = request.getParameter("cemp");
		String cKRACount = request.getParameter("cKRACount");
		String goal_id = request.getParameter("goal_id");
		String goaltype = request.getParameter("goaltype");
		String goal_parent_id = request.getParameter("goal_parent_id");
//		System.out.println("cemp " + cemp);

		String frequency = request.getParameter("frequency");
		String strYearType = request.getParameter("strYearType");
		String[] strMonths = request.getParameterValues("strMonths");
		String[] strQuarters = request.getParameterValues("strQuarters");
		String[] strHalfYears = request.getParameterValues("strHalfYears");
		String[] strYears = request.getParameterValues("strYears");
		
//		System.out.println("strMonths ===>> " + strMonths.length);
//		System.out.println("strQuarters ===>> " + (strQuarters!=null ? strQuarters.length : 0));
//		System.out.println("strHalfYears ===>> " + (strHalfYears!=null ? strHalfYears.length : 0));
		
		
//		String weekday = request.getParameter("weekday");
//		String annualDay = request.getParameter("annualDay");
//		String annualMonth = request.getParameter("annualMonth");
//		String day = request.getParameter("day");
//		String monthday = request.getParameter("monthday");
//		String month = request.getParameter("month");
		
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
			
			System.out.println("cgoalEffectDate ===>> " + cgoalEffectDate + " -- cgoalDueDate ===>> " + cgoalDueDate);
			

			/*if(getStrGoalEmpId()!=null && !getStrGoalEmpId().equals("")){
				List<String> tmpselectEmpList=Arrays.asList(getStrGoalEmpId().split(","));
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
				System.out.println("empList======> " + getCemp());
			}else{
				setCemp(null);
			}*/
			
			List<String> managerIdList = new ArrayList<String>();
			if(getEmpselected()!=null && !getEmpselected().equals("") && !getEmpselected().equals("0")) {
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
					managerIdList.add(empid.trim());
				}
				setCemp(empList);
//				System.out.println("empList======> " + getCemp());
			}else{
				setCemp(null);
			}
			
			String goalCreaterID = "";
			if(teamgoalRedio != null && teamgoalRedio.equals("manager")){
				goalCreaterID = request.getParameter("managers");
			}else if(teamgoalRedio != null && teamgoalRedio.equals("self")){
				goalCreaterID = strSessionEmpId;
			} 
//			System.out.println("teamgoalRedio ===> "+teamgoalRedio);
//			System.out.println("goalCreaterID ===> "+goalCreaterID);
//			System.out.println("cmeasurewith " + cmeasurewith);
			if (goaltype.equals("4")) {
				pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_objective,goal_description," +
					"goal_attribute,measure_type,measure_currency_value,measure_currency_id,measure_effort_days," +
					"measure_effort_hrs,due_date,is_feedback,orientation_id,weightage,emp_ids,level_id,grade_id,is_measure_kra," +
					"measure_kra,measure_type1,measure_currency_value1,measure_kra_days,measure_kra_hrs,entry_date,user_id," +
					"frequency,freq_year_type,recurring_years,frequency_month,priority,measure_desc,measure_val,effective_date," +
					"goal_creater_id,goal_creater_type,responsible_emp_id,goal_element,org_id, depart_id)" +
					"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(goaltype));
				pst.setInt(2, uF.parseToInt(goal_parent_id));
				pst.setString(3, corporateGoal);
				pst.setString(4, cgoalObjective);
				pst.setString(5, cgoalDescription);
				pst.setInt(6, uF.parseToInt(cgoalAlignAttribute));
				pst.setString(7, cAddMKra != null && cAddMKra.equals("Measure") ? cmeasurewith : "");
				pst.setDouble(8, uF.parseToDouble((cmeasurewith.equals("Value")|| cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage")) ? cmeasureDollar : "0"));
				pst.setInt(9, 3);
				pst.setDouble(10, uF.parseToDouble(cmeasurewith.equals("Effort") ? cmeasureEffortsDays : "0"));
				pst.setDouble(11, uF.parseToDouble(cmeasurewith.equals("Effort") ? cmeasureEffortsHrs : "0"));
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
				pst.setDate(25, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(26, uF.parseToInt(strSessionEmpId));
				pst.setInt(27, uF.parseToInt(frequency));
				pst.setInt(28, uF.parseToInt(strYearType));
				pst.setString(29, recurringYears.toString());
				pst.setString(30, frequencyMonths.toString());
				pst.setInt(31, uF.parseToInt(getPriority()));
				pst.setString(32, cMeasureDesc);
				pst.setDouble(33, uF.parseToDouble(cMeasureVal));
				pst.setDate(34, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
				pst.setInt(35, uF.parseToInt(goalCreaterID));
				pst.setString(36, teamgoalRedio);
				pst.setInt(37, uF.parseToInt(responsibleEmpID));
				pst.setInt(38, uF.parseToInt(goalElements));
				pst.setInt(39, uF.parseToInt(strOrg != null && !strOrg.equals("") ? strOrg : hideOrgid));
				pst.setInt(40, uF.parseToInt(strDepart));
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
				
//				System.out.println("individual_goal_id " + individual_goal_id);

//				System.out.println("effective date==>"+cgoalEffectDate+"==>dueDate==>"+cgoalDueDate);
//				***************************** Goal Frequency Start ************************************
				GoalScheduler scheduler = new GoalScheduler(request, session, CF, uF, strSessionEmpId);
				scheduler.updateGoalDetails(individual_goal_id+"");
//				***************************** Goal Frequency End ************************************
				
				
				int ckracountserial = uF.parseToInt(cKRACount);
				StringBuilder sbKras = new StringBuilder();
				
				for (int i = 0; i <=ckracountserial; i++) {
					String cKRA = request.getParameter("cKRA_"+i);
					String cKRAWeightage = request.getParameter("cKRAWeightage_"+i);
					if (cKRA != null && !cKRA.equals("")) {
						String[] cKRATask = request.getParameterValues("cKRATask_"+i);
						if(sbKras == null || sbKras.toString().equals("")) {
							sbKras.append(cKRA);
						} else {
							sbKras.append(", "+cKRA);
						}
						
						pst = con.prepareStatement("insert into goal_kras(goal_id,entry_date,effective_date,is_approved,approved_by,kra_order," +
						"kra_description,goal_type,element_id,attribute_id,emp_ids,added_by,kra_weightage) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
						pst.setInt(1, individual_goal_id);
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
						
//						pst = con.prepareStatement("insert into goal_kras(goal_id,entry_date,effective_date,"
//										+ "is_approved,approved_by,kra_order,kra_description,goal_type)"
//										+ "values(?,?,?,?,?,?,?,?)");
//						pst.setInt(1, individual_goal_id);
//						pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//						pst.setBoolean(4, true);
//						pst.setInt(5, uF.parseToInt(strSessionEmpId));
//						pst.setInt(6, 0);
//						pst.setString(7, cKRA[i]);
//						pst.setInt(8, uF.parseToInt(goaltype));
//
//						pst.execute();

//					}
				
//					if(getCemp()!=null && !getCemp().equals("")){
////						System.out.println("getCemp() ===> "+getCemp());
//						String emp=getCemp().substring(1, getCemp().length()-1);
////						System.out.println("emp ===> "+emp);
//						List<String> cemplist=Arrays.asList(emp.split(","));
//						Set<String> setCemp=new HashSet<String>(cemplist);
//						Iterator<String> it=setCemp.iterator();
//						List<String> cEmpLEvelList=new ArrayList<String>();
//						Map<String, String> hmEmpLevelMap =CF.getEmpLevelMap(con);
////						System.out.println("hmEmpLevelMap ===> "+hmEmpLevelMap);
////						System.out.println("it ===> "+it);
//						while(it.hasNext()){
//							String val=it.next();
//							String levelID = hmEmpLevelMap.get(val.trim());
////							System.out.println("levelID ===> "+levelID);
//							if(levelID != null){
//								cEmpLEvelList.add(levelID);
//							}
//						}
////						System.out.println("cEmpLEvelList ===> "+cEmpLEvelList);
//						Set<String> setLvlList = new HashSet<String>(cEmpLEvelList);
////						System.out.println("setLvlList ===> "+setLvlList);
//						Iterator<String> it1=setLvlList.iterator();
////						System.out.println("it1 ===> "+it1);
////						Map<String, String> hmEmpLevelMap =CF.getEmpLevelMap();
//						while(it1.hasNext()) {
//							String val=it1.next();
////							String levelID = hmEmpLevelMap.get(val.trim());
////							cEmpLEvelList.add(levelID);
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

				List<String> selectEmpList = Arrays.asList(getEmpselected().split(","));
				Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
				
				for(int i=0; selectEmpList!= null && !selectEmpList.isEmpty() && i<selectEmpList.size(); i++) {
					if(!selectEmpList.get(i).equals("") && uF.parseToInt(selectEmpList.get(i)) > 0) {
						String strDomain = request.getServerName().split("\\.")[0];
						String gktType = "";
						if(cAddMKra == null || cAddMKra.equals("")) {
							gktType = "goal";
						} else if(cAddMKra != null && cAddMKra.equals("KRA")) {
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
						userAlerts.setCurrUserTypeID(hmUserTypeId.get(EMPLOYEE));
						userAlerts.setStatus(INSERT_WR_ALERT);
						Thread t = new Thread(userAlerts);
						t.run();
						
						
//						UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//						userAlerts.setStrDomain(strDomain);
//						userAlerts.setStrEmpId(selectEmpList.get(i));
//						if(cAddMKra == null || cAddMKra.equals("")) {
//							userAlerts.set_type(MY_GOAL_ALERT);
//						} else if(cAddMKra != null && cAddMKra.equals("KRA")) {
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
				
				
			} else {

				/*pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_objective,"
					+ "goal_description,goal_attribute,measure_type,measure_currency_value,measure_currency_id,measure_effort_days,"
					+ "measure_effort_hrs,due_date,is_feedback,orientation_id,weightage,emp_ids,level_id,grade_id,entry_date,user_id,"
					+ "frequency,weekday,frequency_day,frequency_month,priority,measure_desc,measure_val,effective_date,is_measure_kra," +
					"goal_creater_id,goal_creater_type,responsible_emp_id,goal_element,org_id)"
					+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(goaltype));
				pst.setInt(2, uF.parseToInt(goal_parent_id));
				pst.setString(3, corporateGoal);
				pst.setString(4, cgoalObjective);
				pst.setString(5, cgoalDescription);
				pst.setInt(6, uF.parseToInt(cgoalAlignAttribute));
				pst.setString(7, cmeasurewith);
				pst.setDouble(8, uF.parseToDouble((cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage")) ? cmeasureDollar : "0"));
				pst.setInt(9, 3);
				pst.setDouble(10, uF.parseToDouble(cmeasurewith.equals("Effort") ? cmeasureEffortsDays : "0"));
				pst.setDouble(11, uF.parseToDouble(cmeasurewith.equals("Effort") ? cmeasureEffortsHrs : "0"));
				pst.setDate(12, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
				pst.setBoolean(13, uF.parseToBoolean(cgoalFeedback));
				pst.setInt(14, uF.parseToInt(corientation));
				pst.setDouble(15, uF.parseToDouble(cgoalWeightage));
				pst.setString(16, cemp);
				pst.setString(17, clevel);
				pst.setString(18, strDesignationUpdate);
				pst.setDate(19, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(20, uF.parseToInt(strSessionEmpId));
				pst.setInt(21, uF.parseToInt(frequency));
				pst.setString(22, weeklyDay);
				pst.setString(23, frequency_day);
				pst.setString(24, frequency_month);
				pst.setInt(25, uF.parseToInt(getPriority()));
				pst.setString(26, cMeasureDesc);
				pst.setDouble(27, uF.parseToDouble(cMeasureVal));
				pst.setDate(28, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
				pst.setBoolean(29, uF.parseToBoolean(cmeasureKra));
				pst.setInt(30, uF.parseToInt(goalCreaterID));
				pst.setString(31, teamgoalRedio);
				pst.setInt(32, uF.parseToInt(responsibleEmpID));
				pst.setInt(33, uF.parseToInt(goalElements));
				pst.setInt(34, uF.parseToInt(strOrg != null && !strOrg.equals("") ? strOrg : hideOrgid));
				pst.execute();
				pst.close();*/

				
				pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_objective,goal_description," +
					"goal_attribute,measure_type,measure_currency_value,measure_currency_id,measure_effort_days," +
					"measure_effort_hrs,due_date,is_feedback,orientation_id,weightage,emp_ids,level_id,grade_id,is_measure_kra," +
					"measure_kra,measure_type1,measure_currency_value1,measure_kra_days,measure_kra_hrs,entry_date,user_id," +
					"frequency,freq_year_type,recurring_years,frequency_month,priority,measure_desc,measure_val,effective_date," +
					"goal_creater_id,goal_creater_type,responsible_emp_id,goal_element,org_id,depart_id,with_depart_with_team) " +
					"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
				pst.setInt(1, uF.parseToInt(goaltype));
				pst.setInt(2, uF.parseToInt(goal_parent_id));
				pst.setString(3, corporateGoal);
				pst.setString(4, cgoalObjective);
				pst.setString(5, cgoalDescription);
				pst.setInt(6, uF.parseToInt(cgoalAlignAttribute));
				pst.setString(7, cAddMKra != null && cAddMKra.equals("Measure") ? cmeasurewith : "");
				pst.setDouble(8, uF.parseToDouble((cmeasurewith.equals("Value")|| cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage")) ? cmeasureDollar : "0"));
				pst.setInt(9, 3);
				pst.setDouble(10, uF.parseToDouble(cmeasurewith.equals("Effort") ? cmeasureEffortsDays : "0"));
				pst.setDouble(11, uF.parseToDouble(cmeasurewith.equals("Effort") ? cmeasureEffortsHrs : "0"));
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
				pst.setDouble(23,uF.parseToDouble("0"));
				pst.setDouble(24,uF.parseToDouble("0"));
				pst.setDate(25, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(26, uF.parseToInt(strSessionEmpId));
				pst.setInt(27, uF.parseToInt(frequency));
				pst.setInt(28, uF.parseToInt(strYearType));
				pst.setString(29, recurringYears.toString());
				pst.setString(30, frequencyMonths.toString());
				pst.setInt(31, uF.parseToInt(getPriority()));
				pst.setString(32, cMeasureDesc);
				pst.setDouble(33, uF.parseToDouble(cMeasureVal));
				pst.setDate(34, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
				pst.setInt(35, uF.parseToInt(goalCreaterID));
				pst.setString(36, teamgoalRedio);
				pst.setInt(37, uF.parseToInt(responsibleEmpID));
				pst.setInt(38, uF.parseToInt(goalElements));
				pst.setInt(39, uF.parseToInt(strOrg != null && !strOrg.equals("") ? strOrg : hideOrgid));
				pst.setInt(40, uF.parseToInt(strDepart));
				pst.setString(41, strWithDepartAndTeam);
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
				
//				System.out.println("individual_goal_id " + individual_goal_id);

//				System.out.println("effective date==>"+cgoalEffectDate+"==>dueDate==>"+cgoalDueDate);
//				***************************** Goal Frequency Start ************************************
//				GoalScheduler scheduler = new GoalScheduler(request, session, CF, uF, strSessionEmpId);
//				scheduler.updateGoalDetails(individual_goal_id+"");
//				***************************** Goal Frequency End ************************************
				
				int ckracountserial = uF.parseToInt(cKRACount);
				StringBuilder sbKras = new StringBuilder();
				for (int i = 0; i <=ckracountserial; i++) {
					String cKRA = request.getParameter("cKRA_"+i);
					String cKRAWeightage = request.getParameter("cKRAWeightage_"+i);
					if (cKRA != null && !cKRA.equals("")) {
//						String[] cKRATask = request.getParameterValues("cKRATask_"+i);
						if(sbKras == null || sbKras.toString().equals("")) {
							sbKras.append(cKRA);
						} else {
							sbKras.append(", "+cKRA);
						}
						
						pst = con.prepareStatement("insert into goal_kras(goal_id,entry_date,effective_date,is_approved,approved_by,kra_order," +
						"kra_description,goal_type,element_id,attribute_id,emp_ids,added_by,kra_weightage) values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
						pst.setInt(1, individual_goal_id);
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
						pst.execute();
						pst.close();
						
					}
				}
			}
			
			int newGoalId = 0;
			pst = con.prepareStatement("select max(goal_id) from goal_details");
			rst = pst.executeQuery();
			while (rst.next()) {
				newGoalId = rst.getInt(1);
			}
			rst.close();
			pst.close();
			
			
			List<String> alWithDepartWithTeam = new ArrayList<String>();
			if(uF.parseToInt(goaltype)==2) {
				pst = con.prepareStatement("select with_depart_with_team from goal_details where goal_id=?");
				pst.setInt(1, uF.parseToInt(goal_parent_id));
				rst = pst.executeQuery();
				System.out.println("pst ===>>>> " + pst);
				while (rst.next()) {
					if(rst.getString("with_depart_with_team")!=null) {
						alWithDepartWithTeam = Arrays.asList(rst.getString("with_depart_with_team").split(","));
					}
				}
				rst.close();
				pst.close();
			}
			
			
			if(goaltype.equals("1") && !uF.parseToBoolean(chkWithDepart)) {
				pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_objective,goal_description," +
					"goal_attribute,measure_type,measure_currency_value,measure_currency_id,measure_effort_days," +
					"measure_effort_hrs,due_date,is_feedback,orientation_id,weightage,emp_ids,level_id,grade_id,is_measure_kra," +
					"measure_kra,measure_type1,measure_currency_value1,measure_kra_days,measure_kra_hrs,entry_date,user_id," +
					"frequency,freq_year_type,recurring_years,frequency_month,priority,measure_desc,measure_val,effective_date," +
					"goal_creater_id,goal_creater_type,responsible_emp_id,goal_element,org_id,depart_id,with_depart_with_team) " +
					"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
				pst.setInt(1, 2);
				pst.setInt(2, newGoalId);
				pst.setString(3, corporateGoal);
				pst.setString(4, cgoalObjective);
				pst.setString(5, cgoalDescription);
				pst.setInt(6, uF.parseToInt(cgoalAlignAttribute));
				pst.setString(7, cAddMKra != null && cAddMKra.equals("Measure") ? cmeasurewith : "");
				pst.setDouble(8, uF.parseToDouble((cmeasurewith.equals("Value")|| cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage")) ? cmeasureDollar : "0"));
				pst.setInt(9, 3);
				pst.setDouble(10, uF.parseToDouble(cmeasurewith.equals("Effort") ? cmeasureEffortsDays : "0"));
				pst.setDouble(11, uF.parseToDouble(cmeasurewith.equals("Effort") ? cmeasureEffortsHrs : "0"));
				pst.setDate(12, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
				pst.setBoolean(13, uF.parseToBoolean(cgoalFeedback));
				pst.setInt(14, uF.parseToInt(corientation));
				pst.setDouble(15, 100);
				pst.setString(16, cemp);
				pst.setString(17, clevel);
				pst.setString(18, strDesignationUpdate);
				pst.setBoolean(19, uF.parseToBoolean(cmeasureKra));
				pst.setString(20, cAddMKra);
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
				pst.setString(32, cMeasureDesc);
				pst.setDouble(33, uF.parseToDouble(cMeasureVal));
				pst.setDate(34, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
				pst.setInt(35, uF.parseToInt(goalCreaterID));
				pst.setString(36, teamgoalRedio);
				pst.setInt(37, uF.parseToInt(responsibleEmpID));
				pst.setInt(38, uF.parseToInt(goalElements));
				pst.setInt(39, uF.parseToInt(strOrg != null && !strOrg.equals("") ? strOrg : hideOrgid));
				pst.setInt(40, uF.parseToInt(strDepart));
				pst.setString(41, null);
				pst.execute();
				pst.close();
				
			}
			
//			System.out.println("goaltype ==>>> " + goaltype + " -- alWithDepartWithTeam ==>>> " + alWithDepartWithTeam);
			if((goaltype.equals("1") && !uF.parseToBoolean(chkWithDepart) && !uF.parseToBoolean(chkWithTeam)) || (goaltype.equals("2") && alWithDepartWithTeam!=null && alWithDepartWithTeam.size()>0 && !uF.parseToBoolean(alWithDepartWithTeam.get(1)))) {
				
				int departGoalId = 0;
				pst = con.prepareStatement("select max(goal_id) from goal_details");
				rst = pst.executeQuery();
				while (rst.next()) {
					departGoalId = rst.getInt(1);
				}
				rst.close();
				pst.close();
				
				pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_objective,goal_description," +
					"goal_attribute,measure_type,measure_currency_value,measure_currency_id,measure_effort_days," +
					"measure_effort_hrs,due_date,is_feedback,orientation_id,weightage,emp_ids,level_id,grade_id,is_measure_kra," +
					"measure_kra,measure_type1,measure_currency_value1,measure_kra_days,measure_kra_hrs,entry_date,user_id," +
					"frequency,freq_year_type,recurring_years,frequency_month,priority,measure_desc,measure_val,effective_date," +
					"goal_creater_id,goal_creater_type,responsible_emp_id,goal_element,org_id,depart_id,with_depart_with_team) " +
					"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
				pst.setInt(1, 3);
				pst.setInt(2, departGoalId);
				pst.setString(3, corporateGoal);
				pst.setString(4, cgoalObjective);
				pst.setString(5, cgoalDescription);
				pst.setInt(6, uF.parseToInt(cgoalAlignAttribute));
				pst.setString(7, cAddMKra != null && cAddMKra.equals("Measure") ? cmeasurewith : "");
				pst.setDouble(8, uF.parseToDouble((cmeasurewith.equals("Value")|| cmeasurewith.equals("Amount") || cmeasurewith.equals("Percentage")) ? cmeasureDollar : "0"));
				pst.setInt(9, 3);
				pst.setDouble(10, uF.parseToDouble(cmeasurewith.equals("Effort") ? cmeasureEffortsDays : "0"));
				pst.setDouble(11, uF.parseToDouble(cmeasurewith.equals("Effort") ? cmeasureEffortsHrs : "0"));
				pst.setDate(12, uF.getDateFormat(cgoalDueDate, DATE_FORMAT));
				pst.setBoolean(13, uF.parseToBoolean(cgoalFeedback));
				pst.setInt(14, uF.parseToInt(corientation));
				pst.setDouble(15, 100); //uF.parseToDouble(cgoalWeightage)
				pst.setString(16, cemp);
				pst.setString(17, clevel);
				pst.setString(18, strDesignationUpdate);
				pst.setBoolean(19, uF.parseToBoolean(cmeasureKra));
				pst.setString(20, cAddMKra);
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
				pst.setString(32, cMeasureDesc);
				pst.setDouble(33, uF.parseToDouble(cMeasureVal));
				pst.setDate(34, uF.getDateFormat(cgoalEffectDate, DATE_FORMAT));
				pst.setInt(35, uF.parseToInt(goalCreaterID));
				pst.setString(36, teamgoalRedio);
				pst.setInt(37, uF.parseToInt(responsibleEmpID));
				pst.setInt(38, uF.parseToInt(goalElements));
				pst.setInt(39, uF.parseToInt(strOrg != null && !strOrg.equals("") ? strOrg : hideOrgid));
				pst.setInt(40, uF.parseToInt(strDepart));
				pst.setString(41, null);
				pst.execute();
				pst.close();
				
			}
			
			
			
			Map<String, String> hmUserTypeId = CF.getUserTypeIdMap(con);
			if(goaltype != null && goaltype.equals("2")) {
				for(int i=0; managerIdList != null && !managerIdList.isEmpty() && i<managerIdList.size(); i++) {
					String strDomain = request.getServerName().split("\\.")[0];
					String alertData = "<div style=\"float: left;\"> Received a new Manager Goal ("+corporateGoal+") by <b>"+CF.getEmpNameMapByEmpId(con, strSessionEmpId)+"</b>. </div>";
					String alertAction = "GoalKRATargets.action?pType=WR";
					
					UserAlerts userAlerts = new UserAlerts(con, uF, CF, request);
					userAlerts.setStrDomain(strDomain);
					userAlerts.setStrEmpId(managerIdList.get(i));
					userAlerts.setStrData(alertData);
					userAlerts.setStrAction(alertAction);
					userAlerts.setCurrUserTypeID(hmUserTypeId.get(MANAGER));
					userAlerts.setStatus(INSERT_WR_ALERT);
					Thread t = new Thread(userAlerts);
					t.run();
					
//					UserAlerts userAlerts=new UserAlerts(con, uF, CF, request);
//					userAlerts.setStrDomain(strDomain);
//					userAlerts.setStrEmpId(managerIdList.get(i));
//					userAlerts.set_type(MANAGER_GOALS_ALERT);
//					userAlerts.setStatus(INSERT_ALERT);
//					Thread t = new Thread(userAlerts);
//					t.run();
				}
			
				sendMailToManager(con, newGoalId, managerIdList);
			}
			
			Map<String,String> hmGoalType = getGoalTypeDetails();
			
			session.setAttribute(MESSAGE,SUCCESSM +"<b>"+corporateGoal+" ("+hmGoalType.get(goaltype)+")</b> added Successfully."+ END );
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private Map<String,String> getGoalTypeDetails() {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> hmGoalType = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from goal_type_details order by goal_type_id");
			rs = pst.executeQuery();
			while (rs.next()) {
				hmGoalType.put(rs.getString("goal_type_id"), rs.getString("goal_type_name"));
			}
			rs.close();
			pst.close();
			//request.setAttribute("hmGoalType", hmGoalType);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return hmGoalType;
	}
	
	public void sendMailToManager(Connection con, int newGoalId, List<String> managerIdList) {

		ResultSet rst = null;
		PreparedStatement pst = null;

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
				for(int i=0; managerIdList != null && !managerIdList.isEmpty() && i<managerIdList.size(); i++) {
				Map<String, String> hmEmpInner = hmEmpInfo.get(managerIdList.get(i));
					String strDomain = request.getServerName().split("\\.")[0];
					Notifications nF = new Notifications(N_MANAGER_GOAL, CF); 
		//			System.out.println("Emp ID is ========= "+rst1.getString("panel_emp_id"));
					 nF.setDomain(strDomain);
					 nF.request = request;
					 nF.setStrEmpId(managerIdList.get(i));
					 nF.setStrHostAddress(CF.getStrEmailLocalHost());
					 nF.setStrHostPort(CF.getStrHostPort());
					 nF.setStrContextPath(request.getContextPath());
					 nF.setStrGoalAssignerName(sbGoalAssignerName.toString());
					 nF.setStrGoalName(goalName);
					 nF.setStrEmpFname(hmEmpInner.get("FNAME"));
					 nF.setStrEmpLname(hmEmpInner.get("LNAME"));
					 nF.setEmailTemplate(true);
					 nF.sendNotifications();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void sendIndiGoalMailToEmp(Connection con, int newGoalId, List<String> empIdList, String goalKraTarget, String kras, String tAmount, String tDays, String tHrs) {

		ResultSet rst = null;
		PreparedStatement pst = null;

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
				Map<String, String> hmEmpInner = hmEmpInfo.get(empIdList.get(i));
					String strDomain = request.getServerName().split("\\.")[0];
					int NOTIFICATION_NAME = N_EXECUTIVE_GOAL; 
					if(goalKraTarget == null || goalKraTarget.equals("")) {
						NOTIFICATION_NAME = N_EXECUTIVE_GOAL;
					} else if(goalKraTarget != null && goalKraTarget.equals("KRA")) {
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
					 if(goalKraTarget == null || goalKraTarget.equals("")) {
						 nF.setStrGoalName(goalName);
						} else if(goalKraTarget != null && goalKraTarget.equals("KRA")) {
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
					 nF.setEmailTemplate(true);
					 nF.sendNotifications();
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

	
	public String getGoalTitle() {
		return goalTitle;
	}

	public void setGoalTitle(String goalTitle) {
		this.goalTitle = goalTitle;
	}


	String strDomain;
	public void setDomain(String strDomain) {
		this.strDomain=strDomain;
	}

	public String getStrDomain() {
		return strDomain;
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
	
}
