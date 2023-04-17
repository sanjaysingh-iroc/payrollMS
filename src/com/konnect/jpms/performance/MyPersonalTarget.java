package com.konnect.jpms.performance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MyPersonalTarget extends ActionSupport implements ServletRequestAware, IStatements {

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
	
	private String priority;
	
	private String operation;
	private String submit;
	
	private String goal_id;
	private String type;

	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);         
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID); 

		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return "login";
		request.setAttribute(PAGE, "/jsp/performance/MyPersonalGoal.jsp");
		request.setAttribute(TITLE, "Goal Setting");
		
		if (getOperation() != null && getOperation().equals("A")) {
			if(getSubmit()!=null && getSubmit().equals("Save")){
				insertNewGoal();
				return SUCCESS;
			}
			
		}else if (getOperation() != null && getOperation().equals("D")) {
			deleteGoal();
			return SUCCESS;
		}
		
		
		return LOAD;

	}

	

	

	private void deleteGoal() {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uf = new UtilityFunctions();

		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from goal_details where goal_id=?");
			pst.setInt(1, uf.parseToInt(getGoal_id()));
			pst.execute();
			pst.close();
			
			pst=con.prepareStatement("delete from goal_kras where goal_id=?");
			pst.setInt(1, uf.parseToInt(getGoal_id()));
			pst.execute();
			pst.close();
				
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
//		UtilityFunctions uf = new UtilityFunctions();
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
		UtilityFunctions uf = new UtilityFunctions();

		String corporateGoal = request.getParameter("corporateGoal");
		String cgoalObjective = request.getParameter("cgoalObjective");
		String cgoalDescription = request.getParameter("cgoalDescription");
		String cgoalAlignAttribute = request
				.getParameter("cgoalAlignAttribute");
		String cmeasurewith = request.getParameter("cmeasurewith");
		String cmeasureDollar = request.getParameter("cmeasureDollar");
		String cmeasureEffortsDays = request
				.getParameter("cmeasureEffortsDays");
		String cmeasureEffortsHrs = request.getParameter("cmeasureEffortsHrs");
		String cmeasureKra = request.getParameter("cmeasureKra");
		String cAddMKra = request.getParameter("cAddMKra");
		String[] cKRA = request.getParameterValues("cKRA");
		String cmkwith = request.getParameter("cmkwith");
		String cmeasurekraDollar = request.getParameter("cmeasurekraDollar");
		String cmeasurekraEffortsDays = request
				.getParameter("cmeasurekraEffortsDays");
		String cmeasurekraEffortsHrs = request
				.getParameter("cmeasurekraEffortsHrs");
		String cgoalDueDate = request.getParameter("cgoalDueDate");
		String cgoalFeedback = request.getParameter("cgoalFeedback");
		String corientation = request.getParameter("corientation");
		String cgoalWeightage = request.getParameter("cgoalWeightage");
		// String clevel = request.getParameter("clevel");
		// String cgrade = request.getParameter("cgrade");
		// String cemp = request.getParameter("cemp");
		String cKRACount = request.getParameter("cKRACount");
		String goal_id = request.getParameter("goal_id");
		String goaltype = request.getParameter("goaltype");
		String goal_parent_id = request.getParameter("goal_parent_id");

		String frequency = request.getParameter("frequency");

		String weekday = request.getParameter("weekday");
		String annualDay = request.getParameter("annualDay");
		String annualMonth = request.getParameter("annualMonth");
		String day = request.getParameter("day");
		String monthday = request.getParameter("monthday");
		String month = request.getParameter("month");
		String cMeasureDesc=request.getParameter("cMeasureDesc");

		try {
			con = db.makeConnection(con);

			if( frequency == null || frequency.equals("")) {
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

			
//			System.out.println("cmeasurewith " + cmeasurewith);
			
			pst = con.prepareStatement("insert into goal_details(goal_type,goal_parent_id,goal_title,goal_objective,"
								+ "goal_description,goal_attribute,measure_type,measure_currency_value,measure_currency_id,measure_effort_days,"
								+ "measure_effort_hrs,due_date,is_feedback,orientation_id,weightage,emp_ids,level_id,grade_id,is_measure_kra,"
								+ "measure_kra,measure_type1,measure_currency_value1,measure_kra_days,measure_kra_hrs,entry_date,user_id,"
								+ "frequency,weekday,frequency_day,frequency_month,priority,measure_desc)"
								+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pst.setInt(1, 6);
			pst.setInt(2, 0);
			pst.setString(3, corporateGoal);
			pst.setString(4, cgoalObjective);
			pst.setString(5, cgoalDescription);
			pst.setInt(6, uf.parseToInt(cgoalAlignAttribute));
			pst.setString(7, cmeasurewith);
			pst.setDouble(8, uf.parseToDouble((cmeasurewith.equals("Value")
						|| cmeasurewith.equals("Amount") || cmeasurewith
						.equals("Percentage")) ? cmeasureDollar : "0"));
			pst.setInt(9, 3);
			pst.setDouble(10, uf.parseToDouble(cmeasurewith
						.equals("Effort") ? cmeasureEffortsDays : "0"));
			pst.setDouble(11, uf.parseToDouble(cmeasurewith
					.equals("Effort") ? cmeasureEffortsHrs : "0"));
			pst.setDate(12, uf.getDateFormat(cgoalDueDate, DATE_FORMAT));
			pst.setBoolean(13, uf.parseToBoolean(cgoalFeedback));
			pst.setInt(14, uf.parseToInt(corientation));
			pst.setDouble(15, uf.parseToDouble(cgoalWeightage));
			pst.setString(16, ","+strSessionEmpId+",");
			pst.setString(17, clevel);
			pst.setString(18, strDesignationUpdate);
			pst.setBoolean(19, uf.parseToBoolean(cmeasureKra));
			pst.setString(20, cAddMKra);
			pst.setString(21, cmkwith);
			pst.setDouble(22,uf.parseToDouble(cmkwith!=null && cmkwith.equals("Amount") ? cmeasurekraDollar : "0"));
			pst.setDouble(23,uf.parseToDouble(cmkwith!=null &&cmkwith.equals("Effort") ? cmeasurekraEffortsDays : "0"));
			pst.setDouble(24,uf.parseToDouble(cmkwith!=null && cmkwith.equals("Effort") ? cmeasurekraEffortsHrs : "0"));
			pst.setDate(25, uf.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(26, uf.parseToInt(strSessionEmpId));
			pst.setInt(27, uf.parseToInt(frequency));
			pst.setString(28, weeklyDay);
			pst.setString(29, frequency_day);
			pst.setString(30, frequency_month);
			pst.setInt(31, uf.parseToInt(getPriority()));
			pst.setString(32, cMeasureDesc);
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
//			System.out.println("individual_goal_id " + individual_goal_id);

//			int ckracountserial = uf.parseToInt(cKRACount);
//
//			for (int i = 0; i < ckracountserial; i++) {
//				if (cKRA[i] != null && !cKRA[i].equals("")) {
//					pst = con.prepareStatement("insert into goal_kras(goal_id,entry_date,effective_date,is_approved,approved_by,kra_order," +
//						"kra_description,goal_type) values(?,?,?,?,?,?,?,?)");
//					pst.setInt(1, individual_goal_id);
//					pst.setDate(2, uf.getCurrentDate(CF.getStrTimeZone()));
//					pst.setDate(3, uf.getCurrentDate(CF.getStrTimeZone()));
//					pst.setBoolean(4, true);
//					pst.setInt(5, uf.parseToInt(strSessionEmpId));
//					pst.setInt(6, 0);
//					pst.setString(7, cKRA[i]);
//					pst.setInt(8, uf.parseToInt(goaltype));
//					pst.execute();
//					pst.close();
//				}
//			}


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
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
	
}
