package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.SessionAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectBudgetedSummary extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	int pro_id;
	CommonFunctions CF = null;
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
			
		UtilityFunctions uF = new UtilityFunctions();
		getProjectBudgetedSumary(uF);

		return SUCCESS;
	}

	
	
	public void getProjectBudgetedSumary(UtilityFunctions uF) {

		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from variable_cost where pro_id=?");
			pst.setInt(1, getPro_id());
			List<List<String>> variableCostList = new ArrayList<List<String>>();
			rs = pst.executeQuery();
			double dblVariableCost = 0.0d;
			
			while (rs.next()) {
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("variable_name"));
				alInner.add(rs.getString("variable_cost"));
				variableCostList.add(alInner);
				dblVariableCost += uF.parseToDouble(rs.getString("variable_cost"));
			}
			rs.close();
			pst.close();

			request.setAttribute("variableCostList", variableCostList);

			pst = con.prepareStatement("SELECT * FROM projectmntnc where pro_id = ?");
			pst.setInt(1, getPro_id());
			rs = pst.executeQuery();
			String strActualBilling = null;
			String strProCurrId = "3";
			while(rs.next()) {
				strActualBilling = rs.getString("actual_calculation_type");
				strProCurrId = rs.getString("curr_id");
				request.setAttribute("strActualBilling", strActualBilling);
			}
			rs.close();
			pst.close();
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			Map<String, String> hmCurr = hmCurrency.get(strProCurrId);
			request.setAttribute("SHORT_CURR", hmCurr != null ? hmCurr.get("SHORT_CURR") : "");
			
			Map<String, String> empcostMp = CF.getProjectEmpActualRates(con, uF, getPro_id()+"", strActualBilling);
//			Map<String, String> hmEmpBillRate = CF.getProjectEmpBillRates(con, uF, getPro_id()+"", strActualBilling);
			
			Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, getPro_id()+"");
			
			String strBillingType = "";
			String strProjectName = "";
			double dblIdealTimeTotal = 0;
			double dblBudgetedCostTotal = 0;
			pst = con.prepareStatement("select a.activity_name,a.task_id,a.resource_ids,pm.service,a.idealtime,a.billable_rate,a.billable_amount," +
					"pm.billing_type,pm.billing_amount,pm.actual_calculation_type,pm.pro_name,pm.curr_id from (select * from activity_info where pro_id=? and parent_task_id = 0 order by task_id)" +
					" as a LEFT JOIN projectmntnc pm ON(a.pro_id = pm.pro_id)");
			pst.setInt(1, getPro_id());

			rs = pst.executeQuery();
			List<List<String>> alReport = new ArrayList<List<String>>();
			while (rs.next()) {
				strBillingType = rs.getString("actual_calculation_type");
				strProjectName = rs.getString("pro_name");
				
				double dblEmpRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(empcostMp, rs.getString("resource_ids")));
//				double dblEmpBillRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(hmEmpBillRate, rs.getString("resource_ids")));
				double dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("task_id")); //0
				alInner.add(rs.getString("activity_name")); //1
				alInner.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")); //2
//				alInner.add(uF.showData(CF.getResourcesSkills(con, rs.getString("resource_ids")), "-")); //3
				alInner.add(uF.showData(CF.getProjectServiceNameById(con, rs.getString("service")), "")); //3
				
				alInner.add(rs.getString("idealtime")); //4
				double budgetedAmt = 0;
				if(dblIdealTime > 0) {
					budgetedAmt = dblEmpRate * dblIdealTime;
				}
				dblIdealTimeTotal += dblIdealTime;
				dblBudgetedCostTotal += budgetedAmt;
				
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblEmpRate)); //5
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(budgetedAmt)); //6
				
//				double expectedBillAmt = 0;
//				if(dblIdealTime > 0) {
//					expectedBillAmt = dblEmpBillRate * dblIdealTime;
//				}
				
//				if(rs.getString("billing_type")!=null && rs.getString("billing_type").equalsIgnoreCase("F")) {
//					alInner.add("Fixed"); //8
//					alInner.add("Fixed"); //9
//					
//					request.setAttribute("FIXED", uF.formatIntoOneDecimal(uF.parseToDouble(rs.getString("billing_amount"))));
//				} else {
//					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblEmpBillRate)); //8
//					alInner.add(uF.formatIntoTwoDecimalWithOutComma(expectedBillAmt)); //9
//				}
//				
//				alInner.add(rs.getString("resource_ids")); //10
				
				alReport.add(alInner);
			}
			rs.close();
			pst.close();
			request.setAttribute("PROJECT_NAME", strProjectName);
			request.setAttribute("strBillingType", strBillingType);
			
			if(dblVariableCost > 0) {
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("Other project specific expenses");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
//				alInner.add("");
				alInner.add(uF.formatIntoTwoDecimal(dblVariableCost));
//				alInner.add("");
//				alInner.add("");
//				alInner.add("");
				alReport.add(alInner);
			}
			dblBudgetedCostTotal += dblVariableCost;
			
			List<String> alInner1 = new ArrayList<String>();
			alInner1.add("");
			alInner1.add("<b>Total</b>");
			alInner1.add("");
			alInner1.add("");
			alInner1.add(uF.roundOffInTimeInHoursMins(dblIdealTimeTotal));
			alInner1.add("");
			alInner1.add(uF.formatIntoTwoDecimal(dblBudgetedCostTotal));
			alReport.add(alInner1);
			
			
			pst = con.prepareStatement("select a.parent_task_id,a.activity_name,a.task_id,a.resource_ids,pm.service,a.idealtime,a.billable_rate," +
				"a.billable_amount,pm.billing_type,pm.billing_amount,pm.actual_calculation_type from (select * from activity_info where pro_id=? and " +
				"parent_task_id != 0 order by task_id) as a LEFT JOIN projectmntnc pm ON(a.pro_id = pm.pro_id)");
			pst.setInt(1, getPro_id());
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmProjectSummarySubTaskReport = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alProjectSummarySubTaskReport = new ArrayList<List<String>>();
			while (rs.next()) {
				alProjectSummarySubTaskReport = hmProjectSummarySubTaskReport.get(rs.getString("parent_task_id"));
				if(alProjectSummarySubTaskReport == null) alProjectSummarySubTaskReport = new ArrayList<List<String>>();
				double dblEmpRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(empcostMp, rs.getString("resource_ids")));
//				double dblEmpBillRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(hmEmpBillRate, rs.getString("resource_ids")));
				double dblIdealTime = uF.parseToDouble(rs.getString("idealtime"));
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(rs.getString("task_id")); //0
				alInner.add(rs.getString("activity_name")); //1
				alInner.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")); //2
//				alInner.add(uF.showData(CF.getResourcesSkills(con, rs.getString("resource_ids")), "-")); //3
				alInner.add(uF.showData(CF.getProjectServiceNameById(con, rs.getString("service")), "")); //3
				
				alInner.add(rs.getString("idealtime")); //4
				double budgetedAmt = 0;
				if(dblIdealTime > 0) {
					budgetedAmt = dblEmpRate * dblIdealTime;
				}
				
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblEmpRate)); //5
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(budgetedAmt)); //6
				
				alProjectSummarySubTaskReport.add(alInner);
				hmProjectSummarySubTaskReport.put(rs.getString("parent_task_id"), alProjectSummarySubTaskReport);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmProjectSummarySubTaskReport ====>> " + hmProjectSummarySubTaskReport);
			request.setAttribute("hmProjectSummarySubTaskReport", hmProjectSummarySubTaskReport);
			
			request.setAttribute("alReport", alReport);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}
	
	
//	public void getProjectBudgetedSumary(Connection con, UtilityFunctions uF, CommonFunctions CF) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		try {
//			
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null); 
//			
//			Map<String, String> hmEmpHourlyGrossAmount = CF.getEmpNetSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "H");
//			Map<String, String> hmEmpDailyGrossAmount = CF.getEmpNetSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "D");
//			
//			Map<String, String> hmServices = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from services_project");
//			rs = pst.executeQuery();
//			while(rs.next()) {
//				hmServices.put(rs.getString("service_project_id"), rs.getString("service_name"));
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select sum(variable_cost) as variable_cost from variable_cost where pro_id = ? ");
//			pst.setInt(1, getPro_id());
//			rs = pst.executeQuery();
//			String strVariableCost = null;
//			while(rs.next()) {
//				strVariableCost = rs.getString("variable_cost");
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select pmc.billing_type, pmc.pro_name, ai.emp_id, activity_name, pmc.actual_calculation_type, service, ai.idealtime  from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id and pmc.pro_id = ? ");
//			pst.setInt(1, getPro_id());
//			rs = pst.executeQuery();
//			
//			List<List<String>> alOuter = new ArrayList<List<String>>();
//			List<String> alInner = new ArrayList<String>();
//			
//			double dblIdealTimeTotal = 0;
//			double dblBudgetedCostTotal = 0;
//			String strBillingType = null;
//			String strProjectName = null;
//			
//			while(rs.next()) {
//				
////				String strBillingType = rs.getString("billing_type");
//				strBillingType = rs.getString("actual_calculation_type");
//				strProjectName = rs.getString("pro_name");
//				
//				alInner = new ArrayList<String>();
//				
//				double dblIdealTime = uF.parseToDouble(uF.roundOffInTimeInHoursMins(uF.parseToDouble(rs.getString("idealtime"))));
//				double dblHourlyRate = uF.parseToDouble(((strBillingType != null && strBillingType.equalsIgnoreCase("D")) ? hmEmpDailyGrossAmount.get(rs.getString("emp_id")) : hmEmpHourlyGrossAmount.get(rs.getString("emp_id"))));
//				
//				double dblBudgetedCost = dblIdealTime * dblHourlyRate;
//				
//				dblIdealTimeTotal += dblIdealTime;
//				dblBudgetedCostTotal += dblBudgetedCost;
//				
//				alInner.add(rs.getString("activity_name"));
//				alInner.add(hmEmpName.get(rs.getString("emp_id")));
//				alInner.add(hmServices.get(rs.getString("service")));
//				alInner.add(uF.formatIntoTwoDecimal(dblIdealTime));
//				alInner.add(uF.formatIntoOneDecimal(dblHourlyRate));
//				alInner.add(uF.formatIntoOneDecimal(dblBudgetedCost));
//				
//				alOuter.add(alInner);
//			}	
//			rs.close();
//			pst.close();
//				request.setAttribute("PROJECT_NAME", strProjectName);
//				request.setAttribute("strBillingType", strBillingType);
//			
//			if(strVariableCost != null) {
//				dblBudgetedCostTotal += uF.parseToDouble(strVariableCost);
//				alInner = new ArrayList<String>();
//				alInner.add("<b>Other Project Specific Expenses</b>");
//				alInner.add("");
//				alInner.add("");
//				alInner.add("");
//				alInner.add("");
//				alInner.add(uF.formatIntoOneDecimal(uF.parseToDouble(strVariableCost)));
//				alOuter.add(alInner);
//			}
//			
//			alInner = new ArrayList<String>();
//			alInner.add("<b>Total</b>");
//			alInner.add("");
//			alInner.add("");
//			alInner.add(uF.roundOffInTimeInHoursMins(dblIdealTimeTotal));
//			alInner.add("");
//			alInner.add(uF.formatIntoOneDecimal(dblBudgetedCostTotal));
//			alOuter.add(alInner);
//			
//			request.setAttribute("alOuter", alOuter);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			if(rs !=null){
//				try {
//					rs.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//			if(pst !=null){
//				try {
//					pst.close();
//				} catch (SQLException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
	
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
		
	}

	public int getPro_id() {
		return pro_id;
	}

	public void setPro_id(int pro_id) {
		this.pro_id = pro_id;
	}
}
