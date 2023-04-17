package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

public class ProjectCostSummary extends ActionSupport implements
		ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	
	int pro_id;
	
	String pageType;
	
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		
		getProjectBudgetedSumary(uF, CF);

		return SUCCESS;
	}

	
	
	public void getProjectBudgetedSumary(UtilityFunctions uF, CommonFunctions CF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmProInfoDisplay = CF.getProjectInformationDisplay(con);
			request.setAttribute("hmProInfoDisplay", hmProInfoDisplay);
			
//			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null); 
//			
//			Map<String, String> hmEmpHourlyGrossAmount = CF.getEmpGrossSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "H");
//			Map<String, String> hmEmpDailyGrossAmount = CF.getEmpGrossSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", "D");
			
			Map<String, String> hmServices = new HashMap<String, String>();
			pst = con.prepareStatement("select * from services_project");
			rs = pst.executeQuery();
			while(rs.next()) {
				hmServices.put(rs.getString("service_project_id"), rs.getString("service_name"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select sum(variable_cost) as variable_cost from variable_cost where pro_id = ? ");
			pst.setInt(1, getPro_id());
			rs = pst.executeQuery();
			String strVariableCost = null;
			while(rs.next()) {
				strVariableCost = rs.getString("variable_cost");
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, getPro_id()+"");
			Map<String, String> hmTeamLead = CF.getProjectTeamLeads(con, uF, getPro_id()+"");
			
			String currId = hmProjectData.get("PRO_REPORT_CURR_ID");
			Map<String, Map<String, String>> hmCurrency = CF.getCurrencyDetails(con);
			Map<String, String> hmCurr = hmCurrency.get(currId);
			
			request.setAttribute("SHORT_CURR", hmCurr != null ? hmCurr.get("SHORT_CURR") : "");
			
//			pst = con.prepareStatement("select sum(reimbursement_amount) as amount from emp_reimbursement where reimbursement_type1 = 'P' and approval_2_emp_id>0 and reimbursement_type::integer = ? ");
			pst = con.prepareStatement("select sum(reimbursement_amount) as amount from emp_reimbursement where reimbursement_type1 = 'P' and approval_1 =1 and approval_2=1 and pro_id = ? ");
			pst.setInt(1, getPro_id());
			rs = pst.executeQuery();
			
			String strReimbursementCost = null;
			while(rs.next()) {
				strReimbursementCost = uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("amount")));
			}
			rs.close();
			pst.close();
			
//			pst = con.prepareStatement("select * from ( select pmc.billing_type, pmc.pro_name, ai.emp_id,activity_name, service, ai.idealtime, pmc.pro_id,ai.billable_rate  from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id ) a, project_cost pc where pc.pro_id = a.pro_id and  a.pro_id = ? ");
			
			pst = con.prepareStatement("select pmc.billing_type,pmc.actual_calculation_type,pmc.pro_name,ai.resource_ids,ai.emp_id,activity_name, " +
					" service,ai.task_id,ai.idealtime,pmc.pro_id,ai.billable_rate,ai.already_work,ai.already_work_days,ai.billable_amount,pmc.billing_amount " +
					"from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id and pmc.pro_id = ? and parent_task_id = 0 and task_accept_status >=0 order by task_id");
			
			pst.setInt(1, getPro_id());
			rs = pst.executeQuery();
			
			List<List<String>> alOuter = new ArrayList<List<String>>();
			double dblActualTimeTotal = 0;
			double dblIdealTimeTotal = 0;
			double dblActualCostTotal = 0;
			double dblBudgetedCostTotal = 0;
			double dblBillableCostTotal = 0;
			String strBillingType = "";
			while(rs.next()) {
				
				strBillingType = rs.getString("actual_calculation_type");
				String strBillType = rs.getString("billing_type");
				
				List<String> alInner = new ArrayList<String>();
				Map<String, String> hmEmpCost = CF.getProjectEmpActualRates(con, uF, getPro_id()+"", strBillingType);
				Map<String, String> hmEmpBillRate = CF.getProjectEmpBillRates(con, uF, getPro_id()+"", strBillingType);
				
				double dblActualTime = 0;
				
//				Map<String, String> hmTaskAWDaysAndHrs = CF.getProjectTaskActualWorkedDaysAndHrs(con, rs.getString("task_id"), hmProjectData);
				Map<String, String> hmTaskAWDaysHrsMonthsCost = new HashMap<String, String>();
				Map<String, String> hmTaskAWDaysHrsMonthsRate = new HashMap<String, String>();
				if(strBillingType != null && strBillingType.equals("M")) {
					hmTaskAWDaysHrsMonthsCost = CF.getProjectTaskActualWorkedMonthsCostOrRate(con,request, CF, rs.getString("task_id"), rs.getString("resource_ids"), hmEmpCost, hmProjectData);
					hmTaskAWDaysHrsMonthsRate = CF.getProjectTaskActualWorkedMonthsCostOrRate(con,request, CF, rs.getString("task_id"), rs.getString("resource_ids"), hmEmpBillRate, hmProjectData);
				} else {
					hmTaskAWDaysHrsMonthsCost = CF.getProjectTaskActualWorkedDaysHrsMonthsCostOrRate(con,request, CF, rs.getString("task_id"), rs.getString("resource_ids"), hmEmpCost, hmProjectData);
					hmTaskAWDaysHrsMonthsRate = CF.getProjectTaskActualWorkedDaysHrsMonthsCostOrRate(con,request, CF, rs.getString("task_id"), rs.getString("resource_ids"), hmEmpBillRate, hmProjectData);
				}
//				System.out.println("hmTaskAWDaysHrsMonthsCost ===>> " + hmTaskAWDaysHrsMonthsCost);
//				System.out.println("hmTaskAWDaysHrsMonthsRate ===>> " + hmTaskAWDaysHrsMonthsRate);
				
//				if(strBillingType!=null && strBillingType.equalsIgnoreCase("D")) {
////					dblActualTime = uF.parseToDouble(rs.getString("already_work_days"));
//					dblActualTime = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_DAYS"));
//				} else {
////					dblActualTime = uF.parseToDouble(rs.getString("already_work"));
//					dblActualTime = uF.parseToDouble(hmTaskAWDaysAndHrs.get("ACTUAL_HRS"));
//				}
				
				dblActualTime = uF.parseToDouble(hmTaskAWDaysHrsMonthsCost.get("ACTUAL_WORKING"));
				
				double dblIdealTime = uF.parseToDouble(uF.roundOffInTimeInHoursMins(uF.parseToDouble(rs.getString("idealtime"))));
//				double dblHourlyRate = uF.parseToDouble(((strBillingType!=null && strBillingType.equalsIgnoreCase("D")) ? hmEmpDailyGrossAmount.get(rs.getString("emp_id")):hmEmpHourlyGrossAmount.get(rs.getString("emp_id"))));
				double dblEmpRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(hmEmpCost, rs.getString("resource_ids")));
//				double dblEmpBillRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(hmEmpBillRate, rs.getString("resource_ids")));
//				double dblHourlyRate = uF.parseToDouble(hmEmpCost.get(rs.getString("emp_id")));
//				double dblBillableRate = uF.parseToDouble(uF.roundOffInTimeInHoursMins(uF.parseToDouble(rs.getString("billable_rate"))));
//				double dblBillableRate = uF.parseToDouble(hmEmpBillRate.get(rs.getString("emp_id")));
				
				double dblBudgetedCost = dblIdealTime * dblEmpRate;
//				double dblActualCost = dblActualTime * dblEmpRate;
				double dblActualCost = uF.parseToDouble(hmTaskAWDaysHrsMonthsCost.get("ACTUAL_COST"));
				
//				double dblBillableAmount = uF.parseToDouble(rs.getString("billable_amount"));
//				double dblBillableAmount = dblActualTime * dblEmpBillRate;
				double dblBillableAmount = uF.parseToDouble(hmTaskAWDaysHrsMonthsRate.get("BILLABLE_COST"));
//				double dblBillableAmountF = uF.parseToDouble(rs.getString("billable_amount"));
				double dblBillingAmountF = uF.parseToDouble(rs.getString("billing_amount"));
				
				
				dblActualTimeTotal += dblActualTime;
				dblIdealTimeTotal +=dblIdealTime;
				dblBudgetedCostTotal +=dblBudgetedCost;
				dblActualCostTotal +=dblActualCost;
				
				if(strBillType!=null && strBillType.equalsIgnoreCase("F")) {
					dblBillableCostTotal = dblBillingAmountF;
				} else {
					dblBillableCostTotal += dblBillableAmount;
				}
				alInner.add(rs.getString("task_id")); //0
				alInner.add(rs.getString("activity_name")); //1
				alInner.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")); //2
				alInner.add(uF.showData(CF.getProjectServiceNameById(con, rs.getString("service")), "")); //3
//				alInner.add(hmEmpName.get(rs.getString("emp_id")));
//				alInner.add(hmServices.get(rs.getString("service")));
//				alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblEmpRate)); //4
				alInner.add(hmTaskAWDaysHrsMonthsCost.get("RESOURCE_COST_RATE")); //4
				if(strBillingType != null && strBillingType.equals("H")) {
					alInner.add(uF.getTotalTimeMinutes100To60(""+dblActualTime)); //5
				} else {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblActualTime)); //5
				}
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblActualCost)); //6
				if(strBillingType != null && strBillingType.equals("H")) {
					alInner.add(uF.getTotalTimeMinutes100To60(""+dblIdealTime)); //7
				} else {
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblIdealTime)); //7
				}
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblBudgetedCost)); //8
				
				if(strBillType!=null && strBillType.equalsIgnoreCase("F")) {
					alInner.add("Fixed"); //9
					alInner.add("Fixed"); //10
				} else {
//					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblEmpBillRate)); //9
					alInner.add(hmTaskAWDaysHrsMonthsRate.get("RESOURCE_COST_RATE")); //9
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblBillableAmount)); //10
				}
				
				alOuter.add(alInner);
				
				request.setAttribute("PROJECT_NAME", rs.getString("pro_name"));
			}
			rs.close();
			pst.close();
			//if(strVariableCost!=null){
				dblBudgetedCostTotal += uF.parseToDouble(strVariableCost);
				dblActualCostTotal += uF.parseToDouble(strReimbursementCost);
				List<String> alInner = new ArrayList<String>();
				alInner.add("");
				alInner.add("<b>Other project specific expenses</b>");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("");
				alInner.add("<a href=\"javascript:void(0);\" onclick=\"getReimbursementDetails("+getPro_id()+",-1)\">"+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(strReimbursementCost))+"</a>");
				alInner.add("");
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(strVariableCost)));
				alInner.add("");
				alInner.add("");
				alOuter.add(alInner);
			//}
			
			alInner = new ArrayList<String>();
			alInner.add("");
			alInner.add("<b>Total</b>");
			alInner.add("");
			alInner.add("");
			alInner.add("");
//			alInner.add(uF.roundOffInTimeInHoursMins(dblActualTimeTotal));
			if(strBillingType != null && strBillingType.equals("H")) {
				alInner.add(uF.getTotalTimeMinutes100To60(""+dblActualTimeTotal));
			} else {
				alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblActualTimeTotal));
			}
			alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblActualCostTotal));
			if(strBillingType != null && strBillingType.equals("H")) {
				alInner.add(uF.getTotalTimeMinutes100To60(""+dblIdealTimeTotal));
			} else {
				alInner.add(uF.roundOffInTimeInHoursMins(dblIdealTimeTotal));
			}
			alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblBudgetedCostTotal));
			alInner.add("");
			alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblBillableCostTotal));
			alOuter.add(alInner);
			
			
			pst = con.prepareStatement("select a.parent_task_id,a.activity_name,a.task_id,a.resource_ids,pm.service,a.idealtime,a.billable_rate,a.billable_amount," +
					"pm.billing_type,pm.billing_amount,pm.actual_calculation_type from (select * from activity_info where pro_id=? and " +
					"parent_task_id != 0 and task_accept_status >=0 order by task_id) as a LEFT JOIN projectmntnc pm ON(a.pro_id = pm.pro_id)");
			pst.setInt(1, getPro_id());
			rs = pst.executeQuery();
			Map<String, List<List<String>>> hmProjectCostSubTaskReport = new LinkedHashMap<String, List<List<String>>>();
			List<List<String>> alProjectCostSubTaskReport = new ArrayList<List<String>>();
			while (rs.next()) {
				
				alProjectCostSubTaskReport = hmProjectCostSubTaskReport.get(rs.getString("parent_task_id"));
				if(alProjectCostSubTaskReport == null) alProjectCostSubTaskReport = new ArrayList<List<String>>();
				
				alInner = new ArrayList<String>();
				alInner.add(rs.getString("task_id")); //0
				alInner.add(rs.getString("activity_name")); //1
				alInner.add(uF.showData(CF.getResourcesName(con, rs.getString("resource_ids"), hmTeamLead), "-")); //2
				alInner.add(uF.showData(CF.getResourcesSkills(con, rs.getString("resource_ids")), "-")); //3
				alInner.add(uF.showData(CF.getProjectServiceNameById(con, rs.getString("service")), "")); //4

				alProjectCostSubTaskReport.add(alInner);
				hmProjectCostSubTaskReport.put(rs.getString("parent_task_id"), alProjectCostSubTaskReport);
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmProjectCostSubTaskReport ====>> " + hmProjectCostSubTaskReport);
			request.setAttribute("hmProjectCostSubTaskReport", hmProjectCostSubTaskReport);
			
			List alProfitSummary = new ArrayList();
			
			alProfitSummary.add((String)request.getAttribute("SHORT_CURR")+" "+uF.formatIntoTwoDecimalWithOutComma(dblBillableCostTotal - dblActualCostTotal));
			alProfitSummary.add((String)request.getAttribute("SHORT_CURR")+" "+uF.formatIntoTwoDecimalWithOutComma(dblBillableCostTotal - dblBudgetedCostTotal));
			if(dblBillableCostTotal>0){
//				alProfitSummary.add(uF.formatIntoOneDecimal((dblBillableCostTotal - dblBudgetedCostTotal)/dblBudgetedCostTotal)+"%");
				alProfitSummary.add(uF.formatIntoOneDecimalWithOutComma(((dblBillableCostTotal - dblActualCostTotal) * 100 )/dblBillableCostTotal)+"%");
				alProfitSummary.add(uF.formatIntoOneDecimalWithOutComma(((dblBillableCostTotal - dblBudgetedCostTotal) * 100 )/dblBillableCostTotal)+"%");
			}else{
				alProfitSummary.add("N/A");
				alProfitSummary.add("N/A");
			}
			
			request.setAttribute("strBillingType", strBillingType);
			request.setAttribute("alOuter", alOuter);
			request.setAttribute("alProfitSummary", alProfitSummary);
			
		} catch (Exception e) { 
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
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

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}
	
}
