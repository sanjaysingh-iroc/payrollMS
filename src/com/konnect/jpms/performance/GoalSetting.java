package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class GoalSetting implements ServletRequestAware, IStatements {

	HttpSession session;
	String strSessionEmpId;
	String strSessionUserType;
	String strUserTypeId;
	CommonFunctions CF;
  
	private List<FillOrientation> orientationList;
	private List<FillLevel> levelList;
	private List<FillGrade> gradeList;
	private List<FillEmployee> empList; 
	
	private List<FillAttribute> attributeList;

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/GoalSetting.jsp");
		request.setAttribute(TITLE, "Goal Setting");
		orientationList = new FillOrientation(request).fillOrientation();
		getOrientationDetailsList();

		levelList = new FillLevel(request).fillLevel();
		getLevelDetailsList();

		gradeList = new FillGrade(request).fillGrade();
		getgradeDetailsList();

		empList = new FillEmployee(request).fillEmployeeName();
		getEmpDetailsList();
		
		attributeList = new FillAttribute(request).fillAttribute();
		getattribute();
		
		String submit=request.getParameter("submit");
		
		if(submit!=null && submit.equals("Save")){
			addGoalSetting();
		}
		

		return "success";

	}
	private void addGoalSetting() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rst = null;
		UtilityFunctions uf=new UtilityFunctions();
		
		String[] corporateGoal = request.getParameterValues("corporateGoal");
		String[] cgoalObjective = request.getParameterValues("cgoalObjective");
		String[] cgoalDescription = request.getParameterValues("cgoalDescription");
		String[] cgoalAlignAttribute = request.getParameterValues("cgoalAlignAttribute");
		String[] cmeasurewith = request.getParameterValues("cmeasurewith");
		String[] cmeasureDollar = request.getParameterValues("cmeasureDollar");
		String[] cmeasureEffortsDays = request.getParameterValues("cmeasureEffortsDays");
		String[] cmeasureEffortsHrs = request.getParameterValues("cmeasureEffortsHrs");
		String[] cmeasureKra = request.getParameterValues("cmeasureKra");
		String[] cAddMKra = request.getParameterValues("cAddMKra");
		String[] cKRA = request.getParameterValues("cKRA");
		String[] cmkwith = request.getParameterValues("cmkwith");
		String[] cmeasurekraDollar = request.getParameterValues("cmeasurekraDollar");
		String[] cmeasurekraEffortsDays = request.getParameterValues("cmeasurekraEffortsDays");
		String[] cmeasurekraEffortsHrs = request.getParameterValues("cmeasurekraEffortsHrs");
		String[] cgoalDueDate = request.getParameterValues("cgoalDueDate");
		String[] cgoalFeedback = request.getParameterValues("cgoalFeedback");
		String[] corientation = request.getParameterValues("corientation");
		String[] cgoalWeightage = request.getParameterValues("cgoalWeightage");
		String[] clevel = request.getParameterValues("clevel");
		String[] cgrade = request.getParameterValues("cgrade");
		String[] cemp = request.getParameterValues("cemp");
		String[] cKRACount=request.getParameterValues("cKRACount");
		
		String[] managerGoal = request.getParameterValues("managerGoal");
		String[] mgoalObjective = request.getParameterValues("mgoalObjective");
		String[] mgoalDescription = request.getParameterValues("mgoalDescription");
		String[] mgoalAlignAttribute = request.getParameterValues("mgoalAlignAttribute");
		String[] mmeasurewith = request.getParameterValues("mmeasurewith");
		String[] mmeasureDollar = request.getParameterValues("mmeasureDollar");
		String[] mmeasureEffortsDays = request.getParameterValues("mmeasureEffortsDays");
		String[] mmeasureEffortsHrs = request.getParameterValues("mmeasureEffortsHrs");
		String[] mmeasureKra = request.getParameterValues("mmeasureKra");
		String[] mAddMKra = request.getParameterValues("mAddMKra");
		String[] mKRA = request.getParameterValues("mKRA");
		String[] mmkwith = request.getParameterValues("mmkwith");
		String[] mmeasurekraDollar = request.getParameterValues("mmeasurekraDollar");
		String[] mmeasurekraEffortsDays = request.getParameterValues("mmeasurekraEffortsDays");
		String[] mmeasurekraEffortsHrs = request.getParameterValues("mmeasurekraEffortsHrs");
		String[] mgoalDueDate = request.getParameterValues("mgoalDueDate");
		String[] mgoalFeedback = request.getParameterValues("mgoalFeedback");
		String[] morientation = request.getParameterValues("morientation");
		String[] mgoalWeightage = request.getParameterValues("mgoalWeightage");
		String[] mlevel = request.getParameterValues("mlevel");
		String[] mgrade = request.getParameterValues("mgrade");
		String[] memp = request.getParameterValues("memp");
		String[] managercount=request.getParameterValues("managercount");
		String[] mKRACount=request.getParameterValues("mKRACount");
		
		
		
		String[] teamGoal = request.getParameterValues("teamGoal");
		String[] tgoalObjective = request.getParameterValues("tgoalObjective");
		String[] tgoalDescription = request.getParameterValues("tgoalDescription");
		String[] tgoalAlignAttribute = request.getParameterValues("tgoalAlignAttribute");
		String[] tmeasurewith = request.getParameterValues("tmeasurewith");
		String[] tmeasureDollar = request.getParameterValues("tmeasureDollar");
		String[] tmeasureEffortsDays = request.getParameterValues("tmeasureEffortsDays");
		String[] tmeasureEffortsHrs = request.getParameterValues("tmeasureEffortsHrs");
		String[] tmeasureKra = request.getParameterValues("tmeasureKra");
		String[] tAddMKra = request.getParameterValues("tAddMKra");
		String[] tKRA = request.getParameterValues("tKRA");
		String[] tmkwith = request.getParameterValues("tmkwith");
		String[] tmeasurekraDollar = request.getParameterValues("tmeasurekraDollar");
		String[] tmeasurekraEffortsDays = request.getParameterValues("tmeasurekraEffortsDays");
		String[] tmeasurekraEffortsHrs = request.getParameterValues("tmeasurekraEffortsHrs");
		String[] tgoalDueDate = request.getParameterValues("tgoalDueDate");
		String[] tgoalFeedback = request.getParameterValues("tgoalFeedback");
		String[] torientation = request.getParameterValues("torientation");
		String[] tgoalWeightage = request.getParameterValues("tgoalWeightage");
		String[] tlevel = request.getParameterValues("tlevel");
		String[] tgrade = request.getParameterValues("tgrade");
		String[] temp = request.getParameterValues("temp");
		String[] teamcount=request.getParameterValues("teamcount");
		String[] tKRACount=request.getParameterValues("tKRACount");
		
		String[] individualGoal = request.getParameterValues("individualGoal");
		String[] igoalObjective = request.getParameterValues("igoalObjective");
		String[] igoalDescription = request.getParameterValues("igoalDescription");
		String[] igoalAlignAttribute = request.getParameterValues("igoalAlignAttribute");
		String[] imeasurewith = request.getParameterValues("imeasurewith");
		String[] imeasureDollar = request.getParameterValues("imeasureDollar");
		String[] imeasureEffortsDays = request.getParameterValues("imeasureEffortsDays");
		String[] imeasureEffortsHrs = request.getParameterValues("imeasureEffortsHrs");
		String[] imeasureKra = request.getParameterValues("imeasureKra");
		String[] iAddMKra = request.getParameterValues("iAddMKra");
		String[] iKRA = request.getParameterValues("iKRA");
		String[] imkwith = request.getParameterValues("imkwith");
		String[] imeasurekraDollar = request.getParameterValues("imeasurekraDollar");
		String[] imeasurekraEffortsDays = request.getParameterValues("imeasurekraEffortsDays");
		String[] imeasurekraEffortsHrs = request.getParameterValues("imeasurekraEffortsHrs");
		String[] igoalDueDate = request.getParameterValues("igoalDueDate");
		String[] igoalFeedback = request.getParameterValues("igoalFeedback");
		String[] iorientation = request.getParameterValues("iorientation");
		String[] igoalWeightage = request.getParameterValues("igoalWeightage");
		String[] ilevel = request.getParameterValues("ilevel");
		String[] igrade = request.getParameterValues("igrade");
		String[] iemp = request.getParameterValues("iemp");
		String[] individualcount=request.getParameterValues("individualcount");
		String[] iKRACount=request.getParameterValues("iKRACount");
		
		try {
			con = db.makeConnection(con);
			
			/*goal_id,goal_type,goal_parent_id,goal_title,goal_objective,goal_description,goal_attribute,measure_type,
			measure_currency_value,measure_currency_id,measure_effort_days,measure_effort_hrs,measure_type1,measure_kra,
			measure_currency_value1,measure_currency1_id,due_date,is_feedback,orientation_id,weightage,emp_ids,
			entry_date,user_id,
			
			is_measure_kra,measure_kra_days,measure_kra_hrs,level_id,grade_id,emp_id
			*/
			con.setAutoCommit(false);
			int managerserial = 0;
			int teamserial = 0;
			int individualserial = 0;
			
			int ckracountserial=0;
			int mkracountserial=0;
			int tkracountserial=0;
			int ikracountserial=0;
			
			for (int i = 0; i < corporateGoal.length; i++) {
				/*System.out.println("cKRACount[countid]====>"+cKRACount[i]);
				int ckracount=uf.parseToInt(cKRACount[i]);
				for(int b=0;b<ckracount;b++){
					System.out.println(b+" count "+cKRA[ckracountserial]);
					ckracountserial++;
				}*/
				
				
				pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_objective," +
						"goal_description,goal_attribute,measure_type,measure_currency_value,measure_currency_id,measure_effort_days," +
						"measure_effort_hrs,measure_type1,measure_kra,measure_currency_value1,measure_currency1_id,due_date,is_feedback," +
						"orientation_id,weightage,emp_ids,entry_date,user_id,is_measure_kra,measure_kra_days,measure_kra_hrs,level_id,grade_id)" +
						"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, 1);
				pst.setInt(2, 0);
				pst.setString(3,corporateGoal[i]);
				pst.setString(4,cgoalObjective[i]);
				pst.setString(5,cgoalDescription[i]);
				pst.setInt(6,uf.parseToInt(cgoalAlignAttribute[i]));
				pst.setString(7,cmeasurewith[i]);
				pst.setDouble(8,uf.parseToDouble(cmeasureDollar[i]));
				pst.setInt(9,3);
				pst.setDouble(10,uf.parseToDouble(cmeasureEffortsDays[i]));
				pst.setDouble(11,uf.parseToDouble(cmeasureEffortsHrs[i]));
				pst.setString(12,cmkwith[i]);
				pst.setString(13,cAddMKra[i]);
				pst.setDouble(14,uf.parseToDouble(cmeasurekraDollar[i]));
				pst.setInt(15,3);
				pst.setDate(16,uf.getDateFormat(cgoalDueDate[i], DATE_FORMAT));
				pst.setBoolean(17,uf.parseToBoolean(cgoalFeedback[i]));
				pst.setInt(18,uf.parseToInt(corientation[i]));
				pst.setDouble(19,uf.parseToDouble(cgoalWeightage[i]));
				pst.setString(20,cemp[i]);
				pst.setDate(21,uf.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(22,uf.parseToInt(strSessionEmpId));
				pst.setBoolean(23,uf.parseToBoolean(cmeasureKra[i]));
				pst.setDouble(24,uf.parseToDouble(cmeasurekraEffortsDays[i]));
				pst.setDouble(25,uf.parseToDouble(cmeasurekraEffortsHrs[i]));
				pst.setString(26,clevel[i]);
				pst.setString(27,cgrade[i]);
				pst.execute();
				pst.close();
				
				int corporate_id = 0;
				pst = con
						.prepareStatement("select max(goal_id) from goal_details");
				rst = pst.executeQuery();
				while (rst.next()) {
					corporate_id=rst.getInt(1);
				}
				rst.close();
				pst.close();
//				System.out.println("corporate_id "+corporate_id);
				
//				System.out.println("cKRACount[countid]====>"+cKRACount[i]);
				int ckracount=uf.parseToInt(cKRACount[i]);
				for(int b=0;b<ckracount;b++){
//					System.out.println(b+" count "+cKRA[ckracountserial]);
					//goal_kra_id,goal_id,entry_date,effective_date,is_approved,approved_by,kra_order,kra_description,goal_type,
									
					if(cKRA[ckracountserial]!=null && !cKRA[ckracountserial].equals("")){
						pst = con.prepareStatement("insert into goal_kras(goal_id,entry_date,effective_date,is_approved,approved_by,kra_order,kra_description,goal_type)" +
								"values(?,?,?,?,?,?,?,?)");
						pst.setInt(1, corporate_id);
						pst.setDate(2,uf.getCurrentDate(CF.getStrTimeZone()));
						pst.setDate(3,uf.getCurrentDate(CF.getStrTimeZone()));
						pst.setBoolean(4,true);
						pst.setInt(5,uf.parseToInt(strSessionEmpId));
						pst.setInt(6,0);
						pst.setString(7,cKRA[ckracountserial]);
						pst.setInt(8, 1);
						pst.execute();
						pst.close();
					
					}
					
					ckracountserial++;
				}
				
				int manager = uf.parseToInt(managercount[i]);
				for (int j = 0; j < manager; j++) {
					pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_objective," +
							"goal_description,goal_attribute,measure_type,measure_currency_value,measure_currency_id,measure_effort_days," +
							"measure_effort_hrs,measure_type1,measure_kra,measure_currency_value1,measure_currency1_id,due_date,is_feedback," +
							"orientation_id,weightage,emp_ids,entry_date,user_id,is_measure_kra,measure_kra_days,measure_kra_hrs,level_id,grade_id)" +
							"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					pst.setInt(1, 2);
					pst.setInt(2, corporate_id);
					pst.setString(3,managerGoal[managerserial]);
					pst.setString(4,mgoalObjective[managerserial]);
					pst.setString(5,mgoalDescription[managerserial]);
					pst.setInt(6,uf.parseToInt(mgoalAlignAttribute[managerserial]));
					pst.setString(7,mmeasurewith[managerserial]);
					pst.setDouble(8,uf.parseToDouble(mmeasureDollar[managerserial]));
					pst.setInt(9,3);
					pst.setDouble(10,uf.parseToDouble(mmeasureEffortsDays[managerserial]));
					pst.setDouble(11,uf.parseToDouble(mmeasureEffortsHrs[managerserial]));
					pst.setString(12,mmkwith[managerserial]);
					pst.setString(13,mAddMKra[managerserial]);
					pst.setDouble(14,uf.parseToDouble(mmeasurekraDollar[managerserial]));
					pst.setInt(15,3);
					pst.setDate(16,uf.getDateFormat(mgoalDueDate[managerserial], DATE_FORMAT));
					pst.setBoolean(17,uf.parseToBoolean(mgoalFeedback[managerserial]));
					pst.setInt(18,uf.parseToInt(morientation[managerserial]));
					pst.setDouble(19,uf.parseToDouble(mgoalWeightage[managerserial]));
					pst.setString(20,memp[managerserial]);
					pst.setDate(21,uf.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(22,uf.parseToInt(strSessionEmpId));
					pst.setBoolean(23,uf.parseToBoolean(mmeasureKra[managerserial]));
					pst.setDouble(24,uf.parseToDouble(mmeasurekraEffortsDays[managerserial]));
					pst.setDouble(25,uf.parseToDouble(mmeasurekraEffortsHrs[managerserial]));
					pst.setString(26,mlevel[managerserial]);
					pst.setString(27,mgrade[managerserial]);
					pst.execute();
					pst.close();
					
					int manager_id = 0;
					pst = con
							.prepareStatement("select max(goal_id) from goal_details");
					rst = pst.executeQuery();
			
					while (rst.next()) {
						manager_id=rst.getInt(1);
					}
					rst.close();
					pst.close();
					
//					System.out.println("mKRACount[countid]====>"+mKRACount[managerserial]);
					int mkracount=uf.parseToInt(mKRACount[managerserial]);
					for(int b=0;b<mkracount;b++){
//						System.out.println(b+" count "+mKRA[mkracountserial]);
						//goal_kra_id,goal_id,entry_date,effective_date,is_approved,approved_by,kra_order,kra_description,goal_type,
										
						if(mKRA[mkracountserial]!=null && !mKRA[mkracountserial].equals("")){
							pst = con.prepareStatement("insert into goal_kras(goal_id,entry_date,effective_date,is_approved,approved_by,kra_order,kra_description,goal_type)" +
									"values(?,?,?,?,?,?,?,?)");
							pst.setInt(1, manager_id);
							pst.setDate(2,uf.getCurrentDate(CF.getStrTimeZone()));
							pst.setDate(3,uf.getCurrentDate(CF.getStrTimeZone()));
							pst.setBoolean(4,true);
							pst.setInt(5,uf.parseToInt(strSessionEmpId));
							pst.setInt(6,0);
							pst.setString(7,mKRA[mkracountserial]);
							pst.setInt(8, 2);
							pst.execute();
							pst.close();
						}
						
						mkracountserial++;
					}
					
					int team = uf.parseToInt(teamcount[managerserial]);
					managerserial++;
					
					for (int m = 0; m < team; m++) {
						pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_objective," +
								"goal_description,goal_attribute,measure_type,measure_currency_value,measure_currency_id,measure_effort_days," +
								"measure_effort_hrs,measure_type1,measure_kra,measure_currency_value1,measure_currency1_id,due_date,is_feedback," +
								"orientation_id,weightage,emp_ids,entry_date,user_id,is_measure_kra,measure_kra_days,measure_kra_hrs,level_id,grade_id)" +
								"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
						pst.setInt(1, 3);
						pst.setInt(2, manager_id);
						pst.setString(3,teamGoal[teamserial]);
						pst.setString(4,tgoalObjective[teamserial]);
						pst.setString(5,tgoalDescription[teamserial]);
						pst.setInt(6,uf.parseToInt(tgoalAlignAttribute[teamserial]));
						pst.setString(7,tmeasurewith[teamserial]);
						pst.setDouble(8,uf.parseToDouble(tmeasureDollar[teamserial]));
						pst.setInt(9,3);
						pst.setDouble(10,uf.parseToDouble(tmeasureEffortsDays[teamserial]));
						pst.setDouble(11,uf.parseToDouble(tmeasureEffortsHrs[teamserial]));
						pst.setString(12,tmkwith[teamserial]);
						pst.setString(13,tAddMKra[teamserial]);
						pst.setDouble(14,uf.parseToDouble(tmeasurekraDollar[teamserial]));
						pst.setInt(15,3);
						pst.setDate(16,uf.getDateFormat(tgoalDueDate[teamserial], DATE_FORMAT));
						pst.setBoolean(17,uf.parseToBoolean(tgoalFeedback[teamserial]));
						pst.setInt(18,uf.parseToInt(torientation[teamserial]));
						pst.setDouble(19,uf.parseToDouble(tgoalWeightage[teamserial]));
						pst.setString(20,temp[teamserial]);
						pst.setDate(21,uf.getCurrentDate(CF.getStrTimeZone()));
						pst.setInt(22,uf.parseToInt(strSessionEmpId));
						pst.setBoolean(23,uf.parseToBoolean(tmeasureKra[teamserial]));
						pst.setDouble(24,uf.parseToDouble(tmeasurekraEffortsDays[teamserial]));
						pst.setDouble(25,uf.parseToDouble(tmeasurekraEffortsHrs[teamserial]));
						pst.setString(26,tlevel[teamserial]);
						pst.setString(27,tgrade[teamserial]);
						pst.execute();
						pst.close();
				
						int team_id = 0;
						pst = con
								.prepareStatement("select max(goal_id) from goal_details");
						rst = pst.executeQuery();
				
						while (rst.next()) {
							team_id=rst.getInt(1);
						}
						rst.close();
						pst.close();
						
//						System.out.println("tKRACount[countid]====>"+tKRACount[teamserial]);
						int tkracount=uf.parseToInt(tKRACount[teamserial]);
						for(int b=0;b<tkracount;b++){
//							System.out.println(b+" count "+tKRA[tkracountserial]);
							//goal_kra_id,goal_id,entry_date,effective_date,is_approved,approved_by,kra_order,kra_description,goal_type,
											
							if(tKRA[ckracountserial]!=null && !tKRA[tkracountserial].equals("")){
								pst = con.prepareStatement("insert into goal_kras(goal_id,entry_date,effective_date,is_approved,approved_by,kra_order,kra_description,goal_type)" +
										"values(?,?,?,?,?,?,?,?)");
								pst.setInt(1, team_id);
								pst.setDate(2,uf.getCurrentDate(CF.getStrTimeZone()));
								pst.setDate(3,uf.getCurrentDate(CF.getStrTimeZone()));
								pst.setBoolean(4,true);
								pst.setInt(5,uf.parseToInt(strSessionEmpId));
								pst.setInt(6,0);
								pst.setString(7,tKRA[tkracountserial]);
								pst.setInt(8, 3);
								pst.execute();
								pst.close();
							}
							tkracountserial++;
						}
						
						
						int individual = uf.parseToInt(individualcount[teamserial]);
						teamserial++;
						
						for (int k = 0; k < individual; k++) {
							pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_objective," +
									"goal_description,goal_attribute,measure_type,measure_currency_value,measure_currency_id,measure_effort_days," +
									"measure_effort_hrs,measure_type1,measure_kra,measure_currency_value1,measure_currency1_id,due_date,is_feedback," +
									"orientation_id,weightage,emp_ids,entry_date,user_id,is_measure_kra,measure_kra_days,measure_kra_hrs,level_id,grade_id)" +
									"values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
							pst.setInt(1, 4);
							pst.setInt(2, team_id);
							pst.setString(3,individualGoal[individualserial]);
							pst.setString(4,igoalObjective[individualserial]);
							pst.setString(5,igoalDescription[individualserial]);
							pst.setInt(6,uf.parseToInt(igoalAlignAttribute[individualserial]));
							pst.setString(7,imeasurewith[individualserial]);
							pst.setDouble(8,uf.parseToDouble(imeasureDollar[individualserial]));
							pst.setInt(9,3);
							pst.setDouble(10,uf.parseToDouble(imeasureEffortsDays[individualserial]));
							pst.setDouble(11,uf.parseToDouble(imeasureEffortsHrs[individualserial]));
							pst.setString(12,imkwith[individualserial]);
							pst.setString(13,iAddMKra[individualserial]);
							pst.setDouble(14,uf.parseToDouble(imeasurekraDollar[individualserial]));
							pst.setInt(15,3);
							pst.setDate(16,uf.getDateFormat(igoalDueDate[individualserial], DATE_FORMAT));
							pst.setBoolean(17,uf.parseToBoolean(igoalFeedback[individualserial]));
							pst.setInt(18,uf.parseToInt(iorientation[individualserial]));
							pst.setDouble(19,uf.parseToDouble(igoalWeightage[individualserial]));
							pst.setString(20,iemp[individualserial]);
							pst.setDate(21,uf.getCurrentDate(CF.getStrTimeZone()));
							pst.setInt(22,uf.parseToInt(strSessionEmpId));
							pst.setBoolean(23,uf.parseToBoolean(imeasureKra[individualserial]));
							pst.setDouble(24,uf.parseToDouble(imeasurekraEffortsDays[individualserial]));
							pst.setDouble(25,uf.parseToDouble(imeasurekraEffortsHrs[individualserial]));
							pst.setString(26,ilevel[individualserial]);
							pst.setString(27,igrade[individualserial]);
							pst.execute();
							pst.close();
					
							int individual_id = 0;
							pst = con
									.prepareStatement("select max(goal_id) from goal_details");
							rst = pst.executeQuery();
					
							while (rst.next()) {
								individual_id=rst.getInt(1);
							}
							rst.close();
							pst.close();
							
//							System.out.println("iKRACount[countid]====>"+iKRACount[individualserial]);
							int ikracount=uf.parseToInt(iKRACount[individualserial]);
							for(int b=0;b<ikracount;b++){
//								System.out.println(b+" count "+iKRA[ikracountserial]);
								//goal_kra_id,goal_id,entry_date,effective_date,is_approved,approved_by,kra_order,kra_description,goal_type,
												
								if(iKRA[ikracountserial]!=null && !iKRA[ikracountserial].equals("")){
									pst = con.prepareStatement("insert into goal_kras(goal_id,entry_date,effective_date,is_approved,approved_by,kra_order,kra_description,goal_type)" +
											"values(?,?,?,?,?,?,?,?)");
									pst.setInt(1, individual_id);
									pst.setDate(2,uf.getCurrentDate(CF.getStrTimeZone()));
									pst.setDate(3,uf.getCurrentDate(CF.getStrTimeZone()));
									pst.setBoolean(4,true);
									pst.setInt(5,uf.parseToInt(strSessionEmpId));
									pst.setInt(6,0);
									pst.setString(7,iKRA[ikracountserial]);
									pst.setInt(8, 4);
									pst.execute();
									pst.close();
								}
								ikracountserial++;
							}
							
							
							individualserial++;
						}
					}
				}
				
			}
			
			con.commit();	
		} catch (Exception e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeResultSet(rst);
			db.closeConnection(con);
		}
		}
	public void getattribute() {

		StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < attributeList.size(); i++) {
			FillAttribute fillAttribute = attributeList.get(i);

			sb.append("<option value='" + fillAttribute.getId() + "'>"
					+ fillAttribute.getName() + "</option>");

		}
		request.setAttribute("attribute", sb.toString());

	}

	private void getEmpDetailsList() {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < empList.size(); i++) {
			FillEmployee fillEmployee = empList.get(i);

			sb.append("<option value='" + fillEmployee.getEmployeeId() + "'>"
					+ fillEmployee.getEmployeeCode() + "</option>");

		}
		request.setAttribute("empListOption", sb.toString()); 

	}

	private void getgradeDetailsList() {
		StringBuilder sb = new StringBuilder("");
		for (int i = 0; i < gradeList.size(); i++) {
			FillGrade fillGrade = gradeList.get(i);

			sb.append("<option value='" + fillGrade.getGradeId() + "'>"
					+ fillGrade.getGradeCode() + "</option>");

		}
		request.setAttribute("gradeListOption", sb.toString()); 

	}

	private void getLevelDetailsList() {
		StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < levelList.size(); i++) {
			FillLevel fillLevel = levelList.get(i);

			sb.append("<option value='" + fillLevel.getLevelId() + "'>"
					+ fillLevel.getLevelCodeName() + "</option>");

		}
		request.setAttribute("levelListOption", sb.toString());
	}

	public void getOrientationDetailsList() {

		StringBuilder sb = new StringBuilder("");

		for (int i = 0; i < orientationList.size(); i++) {
			FillOrientation fillOrientation = orientationList.get(i);

			sb.append("<option value=\"" + fillOrientation.getId() + "\">"
					+ fillOrientation.getName() + "</option>");

		}
		request.setAttribute("orientation", sb.toString());

	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {

		this.request = request;
	}

	public List<FillOrientation> getOrientationList() {
		return orientationList;
	}

	public void setOrientationList(List<FillOrientation> orientationList) {
		this.orientationList = orientationList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillGrade> getGradeList() {
		return gradeList;
	}

	public void setGradeList(List<FillGrade> gradeList) {
		this.gradeList = gradeList;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

}
