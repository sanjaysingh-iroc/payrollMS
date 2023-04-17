package com.konnect.jpms.policies;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillAllowanceCondition;
import com.konnect.jpms.select.FillAllowancePaymentLogic;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddAllowanceCondition extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	CommonFunctions CF;
	HttpSession session;
	
	String strOrg;
	String strLevel;
	String strSalaryHeadId;
	
	String conditionId;
	String strAllowanceCondition;
	String strMin;
	String strMax;
	String strType;
	String strAmtPercentage;
	String strCondtionSlab;
	
	String strLevelGoals;
	String strLevelKras;
	
	String btnSubmitPublish;
	String strPublish;
	String strCalculateFrom;
	
	List<FillAllowanceCondition> allowanceConditionList;
	
	List<String> gktValue = new ArrayList<String>();
	List<String> kValue = new ArrayList<String>();
	
	List<FillAllowancePaymentLogic> goalList = new ArrayList<FillAllowancePaymentLogic>();
	List<FillAllowancePaymentLogic> kraList = new ArrayList<FillAllowancePaymentLogic>();
	
	String userscreen;
	String navigationId;
	String toPage;
	
	boolean addDaysAttendance;
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		String operation = request.getParameter("operation");
		String strId = request.getParameter("ID");

		loadAllowanceCondition(uF);
		
		if (operation!=null && operation.equals("D")) {
			return deleteAllowanceCondition(uF,strId);
		} 
		if (operation!=null && operation.equals("E")) {
			return viewAllowanceCondition(uF,strId);
		}

		if (getConditionId()!=null && getConditionId().length()>0 && !getConditionId().equalsIgnoreCase("NULL")) {
			return updateAllowanceCondition(uF);
		}
		if (uF.parseToInt(getStrAllowanceCondition()) > 0 && getStrCondtionSlab()!=null && !getStrCondtionSlab().trim().equals("") && !getStrCondtionSlab().trim().equalsIgnoreCase("NULL")) {
			return insertAllowanceCondition(uF);
		}
		return LOAD;
		
	}

	private void getGoalKraTargetForAllowance(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			List<String> alEmpIds = new ArrayList<String>();
			pst = con.prepareStatement("select emp_id,grade_id from employee_official_details where emp_id>0 and grade_id in (select gd.grade_id from " +
				"grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id " +
				" and ld.level_id = ?)");
			pst.setInt(1, uF.parseToInt(getStrLevel()));
			rs = pst.executeQuery();
			while(rs.next()) {
				alEmpIds.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			
			List<String> goalIds = new ArrayList<String>();
			for (int i = 0; alEmpIds != null && i < alEmpIds.size(); i++) {
				pst = con.prepareStatement("select goal_id, goal_title from goal_details where goal_type in (4,5,6,7) and measure_kra != 'KRA' and is_close = false and emp_ids like '%,"+alEmpIds.get(i)+",%'");
				rs = pst.executeQuery();
				while(rs.next()) {
					if(!goalIds.contains(rs.getString("goal_id"))) {
						goalList.add(new FillAllowancePaymentLogic(rs.getString("goal_id"), rs.getString("goal_title")));
						goalIds.add(rs.getString("goal_id"));
					}
				}
				rs.close();
				pst.close();
			}
			
			List<String> kraIds = new ArrayList<String>();
			for (int i = 0; alEmpIds != null && i < alEmpIds.size(); i++) {
				pst = con.prepareStatement("select goal_kra_id, kra_description from goal_kras where is_close = false and (is_assign = true or is_assign is null) and emp_ids like '%,"+alEmpIds.get(i)+",%'");
				rs = pst.executeQuery();
				while(rs.next()) {
					if(!kraIds.contains(rs.getString("goal_kra_id"))) {
						kraList.add(new FillAllowancePaymentLogic(rs.getString("goal_kra_id"), rs.getString("kra_description")));
						kraIds.add(rs.getString("goal_kra_id"));
					}
				}
				rs.close();
				pst.close();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	private String deleteAllowanceCondition(UtilityFunctions uF, String strId) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("delete from allowance_condition_details where allowance_condition_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Allowance condition deleted successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Allowance condition could not be deleted. Please try again."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	private String viewAllowanceCondition(UtilityFunctions uF, String strId) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from allowance_condition_details where allowance_condition_id=?");
			pst.setInt(1, uF.parseToInt(strId));
			rs = pst.executeQuery();
			while(rs.next()){
				setConditionId(rs.getString("allowance_condition_id"));
				setStrCondtionSlab(uF.showData(rs.getString("condition_slab"),""));
				setStrAllowanceCondition(uF.showData(rs.getString("allowance_condition"),""));
				setStrMin(uF.showData(rs.getString("min_condition"),"0"));
				setStrMax(uF.showData(rs.getString("max_condition"),"0"));
				setStrType(uF.showData(rs.getString("custom_type"), "A"));
				setStrAmtPercentage(uF.showData(rs.getString("custom_amt_percentage"), ""));
				setStrPublish(rs.getString("is_publish"));
				
				List<String> gktValue1 = new ArrayList<String>();
				if (rs.getString("goal_kra_target_ids") == null) {
					gktValue1.add("0");
				} else {
					gktValue1 = Arrays.asList(rs.getString("goal_kra_target_ids").split(","));
				}
				if (gktValue1 != null) {
					for (int i = 0; i < gktValue1.size(); i++) {
						gktValue.add(gktValue1.get(i).trim());
					}
				} else {
					gktValue.add("0");
				}
				
				List<String> kValue1 = new ArrayList<String>();
				if (rs.getString("kra_ids") == null) {
					kValue1.add("0");
				} else {
					kValue1 = Arrays.asList(rs.getString("kra_ids").split(","));
				}
				if (kValue1 != null) {
					for (int i = 0; i < kValue1.size(); i++) {
						kValue.add(kValue1.get(i).trim());
					}
				} else {
					kValue.add("0");
				}
				
				setStrCalculateFrom(uF.showData(rs.getString("calculation_from"),"0"));
				setAddDaysAttendance(uF.parseToBoolean(rs.getString("is_add_days_attendance")));
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
		return LOAD;
	}

	private String updateAllowanceCondition(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			
			StringBuilder sb1 = null;
			if(uF.parseToInt(getStrAllowanceCondition()) == A_GOAL_KRA_TARGET_ID) {
				List<String> goalList = getGoalKraTargetList(con);
				for (int i = 0; goalList != null && i < goalList.size(); i++) {
					if (sb1 == null) {
						sb1 = new StringBuilder();
						sb1.append("," + goalList.get(i).trim()+",");
					} else {
						sb1.append(goalList.get(i).trim()+",");
					}
				}
			} 
			if (sb1 == null) {
				sb1 = new StringBuilder();
			}
			
			StringBuilder sb2 = null;
			if(uF.parseToInt(getStrAllowanceCondition()) == A_KRA_ID) {
				List<String> kraList = getKrasList(con);
				for (int i = 0; kraList != null && i < kraList.size(); i++) {
					if (sb2 == null) {
						sb2 = new StringBuilder();
						sb2.append("," + kraList.get(i).trim()+",");
					} else {
						sb2.append(kraList.get(i).trim()+",");
					}
				}
			}
			if (sb2 == null) {
				sb2 = new StringBuilder();
			}
			
			pst = con.prepareStatement("update allowance_condition_details set allowance_condition=?,min_condition=?,max_condition=?,updated_by=?," +
					"update_date=?,custom_type=?,custom_amt_percentage=?,condition_slab=?,goal_kra_target_ids=?,kra_ids=?,is_publish=?,calculation_from=?," +
					"is_add_days_attendance=? where allowance_condition_id=?");
			pst.setInt(1, uF.parseToInt(getStrAllowanceCondition()));
			pst.setDouble(2, uF.parseToInt(getStrAllowanceCondition()) == A_CUSTOM_FACTOR_ID ? 0 : uF.parseToDouble(getStrMin()));
			pst.setDouble(3, uF.parseToInt(getStrAllowanceCondition()) == A_CUSTOM_FACTOR_ID ? 0 : uF.parseToDouble(getStrMax()));
			pst.setInt(4, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setDate(5, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(6, uF.parseToInt(getStrAllowanceCondition()) == A_CUSTOM_FACTOR_ID ? getStrType() : null);
			pst.setDouble(7, uF.parseToInt(getStrAllowanceCondition()) == A_CUSTOM_FACTOR_ID ?  uF.parseToDouble(getStrAmtPercentage()) : 0.0d);
			pst.setString(8, getStrCondtionSlab());
			pst.setString(9, uF.parseToInt(getStrAllowanceCondition()) == A_GOAL_KRA_TARGET_ID ? sb1.toString(): null);
			pst.setString(10, uF.parseToInt(getStrAllowanceCondition()) == A_KRA_ID ? sb2.toString(): null);
//			if(getBtnSubmitPublish() != null && getBtnSubmitPublish().equals("Submit & Publish")) {
				pst.setBoolean(11, true);
//			} else {
//				pst.setBoolean(11, false);
//			}
			pst.setDouble(12, (uF.parseToInt(getStrAllowanceCondition()) == A_CUSTOM_FACTOR_ID 
					|| uF.parseToInt(getStrAllowanceCondition()) == A_GOAL_KRA_TARGET_ID 
					|| uF.parseToInt(getStrAllowanceCondition()) == A_KRA_ID
					|| uF.parseToInt(getStrAllowanceCondition()) == A_NO_OF_DAYS_ABSENT_ID) ? 0 : uF.parseToDouble(getStrCalculateFrom()));
			pst.setBoolean(13, getAddDaysAttendance());
			pst.setInt(14, uF.parseToInt(getConditionId()));
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Allowance condition updated successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Allowance condition could not be updated. Please try again."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	private String insertAllowanceCondition(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs= null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			
			
			StringBuilder sb1 = null;
			if(uF.parseToInt(getStrAllowanceCondition()) == A_GOAL_KRA_TARGET_ID) {
				List<String> goalList = getGoalKraTargetList(con);
				for (int i = 0; goalList != null && i < goalList.size(); i++) {
					if (sb1 == null) {
						sb1 = new StringBuilder();
						sb1.append("," + goalList.get(i).trim()+",");
					} else {
						sb1.append(goalList.get(i).trim()+",");
					}
				}
			} 
			if (sb1 == null) {
				sb1 = new StringBuilder();
			}
			
			StringBuilder sb2 = null;
			if(uF.parseToInt(getStrAllowanceCondition()) == A_KRA_ID) {
				List<String> kraList = getKrasList(con);
				for (int i = 0; kraList != null && i < kraList.size(); i++) {
					if (sb2 == null) {
						sb2 = new StringBuilder();
						sb2.append("," + kraList.get(i).trim()+",");
					} else {
						sb2.append(kraList.get(i).trim()+",");
					}
				}
			}
			if (sb2 == null) {
				sb2 = new StringBuilder();
			}
			
			pst = con.prepareStatement("insert into allowance_condition_details(allowance_condition,min_condition,max_condition,salary_head_id," +
					"level_id,org_id,added_by,entry_date,custom_type,custom_amt_percentage,condition_slab,goal_kra_target_ids,kra_ids,is_publish," +
					"calculation_from,is_add_days_attendance)values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
			pst.setInt(1, uF.parseToInt(getStrAllowanceCondition()));
			pst.setDouble(2, uF.parseToInt(getStrAllowanceCondition()) == A_CUSTOM_FACTOR_ID ? 0 : uF.parseToDouble(getStrMin()));
			pst.setDouble(3, uF.parseToInt(getStrAllowanceCondition()) == A_CUSTOM_FACTOR_ID ? 0 : uF.parseToDouble(getStrMax()));
			pst.setInt(4, uF.parseToInt(getStrSalaryHeadId()));
			pst.setInt(5, uF.parseToInt(getStrLevel()));
			pst.setInt(6, uF.parseToInt(getStrOrg()));
			pst.setInt(7, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setDate(8, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setString(9, uF.parseToInt(getStrAllowanceCondition()) == A_CUSTOM_FACTOR_ID ? getStrType() : null);
			pst.setDouble(10, uF.parseToInt(getStrAllowanceCondition()) == A_CUSTOM_FACTOR_ID ?  uF.parseToDouble(getStrAmtPercentage()) : 0.0d);
			pst.setString(11, getStrCondtionSlab());
			pst.setString(12, uF.parseToInt(getStrAllowanceCondition()) == A_GOAL_KRA_TARGET_ID ? sb1.toString(): null);
			pst.setString(13, uF.parseToInt(getStrAllowanceCondition()) == A_KRA_ID ? sb2.toString(): null);
//			if(getBtnSubmitPublish() != null && getBtnSubmitPublish().equals("Submit & Publish")) {
				pst.setBoolean(14, true);
//			} else {
//				pst.setBoolean(14, false);
//			}
			pst.setDouble(15, (uF.parseToInt(getStrAllowanceCondition()) == A_CUSTOM_FACTOR_ID 
					|| uF.parseToInt(getStrAllowanceCondition()) == A_GOAL_KRA_TARGET_ID 
					|| uF.parseToInt(getStrAllowanceCondition()) == A_KRA_ID
					|| uF.parseToInt(getStrAllowanceCondition()) == A_NO_OF_DAYS_ABSENT_ID) ? 0 : uF.parseToDouble(getStrCalculateFrom()));
			pst.setBoolean(16, getAddDaysAttendance());
			pst.execute();
			pst.close();
			
			session.setAttribute(MESSAGE, SUCCESSM+"Allowance condition saved successfully."+END);
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute(MESSAGE, ERRORM+"Allowance condition could not be saved. Please try again."+END);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	private List<String> getKrasList(Connection con) {
		List<String> al = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {

			if (getStrLevelKras() != null && getStrLevelKras().length() > 0) {
				List<String> kras = Arrays.asList(getStrLevelKras().split(","));
				for(int i=0; kras!=null && !kras.isEmpty() && i<kras.size(); i++) {
					al.add(kras.get(i).trim());
				}
//				System.out.println("al.add(emp.get(i).trim()) ==========>" + al.toString());
			} else {
			
				List<String> alEmpIds = new ArrayList<String>();
				pst = con.prepareStatement("select emp_id,grade_id from employee_official_details where emp_id>0 and grade_id in (select gd.grade_id from " +
					"grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id " +
					" and ld.level_id = ?)");
				pst.setInt(1, uF.parseToInt(getStrLevel()));
				rs = pst.executeQuery();
				while(rs.next()) {
					alEmpIds.add(rs.getString("emp_id"));
				}
				rs.close();
				pst.close();
				
				for (int i = 0; alEmpIds != null && i < alEmpIds.size(); i++) {
					pst = con.prepareStatement("select goal_kra_id, kra_description from goal_kras where is_close = false and (is_assign = true or is_assign is null) and emp_ids like '%,"+alEmpIds.get(i)+",%'");
					rs = pst.executeQuery();
					while(rs.next()) {
						if(!al.contains(rs.getString("goal_kra_id"))) {
							al.add(rs.getString("goal_kra_id"));
						}
					}
					rs.close();
					pst.close();
				}
		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}

	
	private List<String> getGoalKraTargetList(Connection con) {
		List<String> al = new ArrayList<String>();
		PreparedStatement pst = null;
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {

			if (getStrLevelGoals() != null && getStrLevelGoals().length() > 0) {
				List<String> goals = Arrays.asList(getStrLevelGoals().split(","));
				for(int i=0; goals!=null && !goals.isEmpty() && i<goals.size(); i++) {
					al.add(goals.get(i).trim());
				}
//				System.out.println("al.add(emp.get(i).trim()) ==========>" + al.toString());
			} else {
			
				List<String> alEmpIds = new ArrayList<String>();
				pst = con.prepareStatement("select emp_id,grade_id from employee_official_details where emp_id>0 and grade_id in (select gd.grade_id from " +
					"grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id " +
					" and ld.level_id = ?)");
				pst.setInt(1, uF.parseToInt(getStrLevel()));
				rs = pst.executeQuery();
				while(rs.next()) {
					alEmpIds.add(rs.getString("emp_id"));
				}
				rs.close();
				pst.close();
				
				for (int i = 0; alEmpIds != null && i < alEmpIds.size(); i++) {
					pst = con.prepareStatement("select goal_id, goal_title from goal_details where goal_type in (4,5,6,7) and measure_kra != 'KRA' and is_close = false and emp_ids like '%,"+alEmpIds.get(i)+",%'");
					rs = pst.executeQuery();
					while(rs.next()) {
						if(!al.contains(rs.getString("goal_id"))) {
							al.add(rs.getString("goal_id"));
						}
					}
					rs.close();
					pst.close();
				}
		
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return al;
	}
	
	
	private void loadAllowanceCondition(UtilityFunctions uF) {
		allowanceConditionList = new FillAllowanceCondition().fillAllowanceCondition();
		getGoalKraTargetForAllowance(uF);
	}
	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrOrg() {
		return strOrg;
	}

	public void setStrOrg(String strOrg) {
		this.strOrg = strOrg;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getStrSalaryHeadId() {
		return strSalaryHeadId;
	}

	public void setStrSalaryHeadId(String strSalaryHeadId) {
		this.strSalaryHeadId = strSalaryHeadId;
	}

	public String getStrAllowanceCondition() {
		return strAllowanceCondition;
	}

	public void setStrAllowanceCondition(String strAllowanceCondition) {
		this.strAllowanceCondition = strAllowanceCondition;
	}

	public List<FillAllowanceCondition> getAllowanceConditionList() {
		return allowanceConditionList;
	}

	public void setAllowanceConditionList(List<FillAllowanceCondition> allowanceConditionList) {
		this.allowanceConditionList = allowanceConditionList;
	}

	public String getConditionId() {
		return conditionId;
	}

	public void setConditionId(String conditionId) {
		this.conditionId = conditionId;
	}

	public String getStrMin() {
		return strMin;
	}

	public void setStrMin(String strMin) {
		this.strMin = strMin;
	}

	public String getStrMax() {
		return strMax;
	}

	public void setStrMax(String strMax) {
		this.strMax = strMax;
	}

	public String getStrType() {
		return strType;
	}

	public void setStrType(String strType) {
		this.strType = strType;
	}

	public String getStrAmtPercentage() {
		return strAmtPercentage;
	}

	public void setStrAmtPercentage(String strAmtPercentage) {
		this.strAmtPercentage = strAmtPercentage;
	}

	public String getStrCondtionSlab() {
		return strCondtionSlab;
	}

	public void setStrCondtionSlab(String strCondtionSlab) {
		this.strCondtionSlab = strCondtionSlab;
	}

	public String getStrLevelGoals() {
		return strLevelGoals;
	}

	public void setStrLevelGoals(String strLevelGoals) {
		this.strLevelGoals = strLevelGoals;
	}

	public List<String> getGktValue() {
		return gktValue;
	}

	public void setGktValue(List<String> gktValue) {
		this.gktValue = gktValue;
	}

	public List<FillAllowancePaymentLogic> getGoalList() {
		return goalList;
	}

	public void setGoalList(List<FillAllowancePaymentLogic> goalList) {
		this.goalList = goalList;
	}

	public List<FillAllowancePaymentLogic> getKraList() {
		return kraList;
	}

	public void setKraList(List<FillAllowancePaymentLogic> kraList) {
		this.kraList = kraList;
	}

	public String getStrLevelKras() {
		return strLevelKras;
	}

	public void setStrLevelKras(String strLevelKras) {
		this.strLevelKras = strLevelKras;
	}

	public List<String> getkValue() {
		return kValue;
	}

	public void setkValue(List<String> kValue) {
		this.kValue = kValue;
	}

	public String getBtnSubmitPublish() {
		return btnSubmitPublish;
	}

	public void setBtnSubmitPublish(String btnSubmitPublish) {
		this.btnSubmitPublish = btnSubmitPublish;
	}

	public String getStrPublish() {
		return strPublish;
	}

	public void setStrPublish(String strPublish) {
		this.strPublish = strPublish;
	}

	public String getStrCalculateFrom() {
		return strCalculateFrom;
	}

	public void setStrCalculateFrom(String strCalculateFrom) {
		this.strCalculateFrom = strCalculateFrom;
	}

	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}

	public String getNavigationId() {
		return navigationId;
	}

	public void setNavigationId(String navigationId) {
		this.navigationId = navigationId;
	}

	public String getToPage() {
		return toPage;
	}

	public void setToPage(String toPage) {
		this.toPage = toPage;
	}

	public boolean getAddDaysAttendance() {
		return addDaysAttendance;
	}

	public void setAddDaysAttendance(boolean addDaysAttendance) {
		this.addDaysAttendance = addDaysAttendance;
	}

}
