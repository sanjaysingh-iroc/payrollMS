package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AddtoProject extends ActionSupport implements ServletRequestAware,IStatements{
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF; 
	
	String strEmpId; 
	List<FillProjectList> projectList;
	
	String operation;
	String strProject;

	public String execute() {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)	return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		projectList = new FillProjectList(request).fillAllProjectDetails(false, false);
		
		if(getOperation()!=null && getOperation().equals("E")){
			addResourceInProject(uF);
			return SUCCESS;
		}
		return LOAD;
	}

	private void addResourceInProject(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
			Map<String, Map<String, String>> hmWLocation = CF.getWorkLocationMap(con);
			
			Map<String, String> hmProDetails = CF.getProjectDetailsByProId(con, getStrProject());
			Map<String, String> hmCurrToFromVal = CF.getCurrencyFromIdToIdValue(con);
			Map<String, Map<String, String>> hmCurrData = CF.getCurrencyDetails(con);
			
			String strActualBillingType = hmProDetails.get("PRO_BILLING_ACTUAL_TYPE");
			
			pst = con.prepareStatement("select * from project_emp_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(getStrProject()));
			rs = pst.executeQuery();
			List<String> proEmpIdList = new ArrayList<String>();
			while (rs.next()) {
				if(!proEmpIdList.contains(rs.getString("emp_id"))) {
					proEmpIdList.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			
			
			Map<String,String> hmLevelDayRateMap = new HashMap<String,String>();
			Map<String,String> hmLevelHourRateMap = new HashMap<String,String>();
			Map<String,String> hmLevelMonthRateMap = new HashMap<String,String>();
			Map<String,String> hmLevelRateCurrIdMap = new HashMap<String,String>();			
			StringBuilder sbskillRateQuery = new StringBuilder();
			sbskillRateQuery.append("SELECT * FROM level_skill_rates ");
			pst = con.prepareStatement(sbskillRateQuery.toString());
//			System.out.println("=====+>"+pst);
			rs=pst.executeQuery();
			while(rs.next()) {
				hmLevelDayRateMap.put(rs.getString("wlocation_id")+"_"+rs.getString("level_id")+"_"+rs.getString("service_project_id")+"_"+rs.getString("skill_id"), rs.getString("rate_per_day") );
				hmLevelHourRateMap.put(rs.getString("wlocation_id")+"_"+rs.getString("level_id")+"_"+rs.getString("service_project_id")+"_"+rs.getString("skill_id"), rs.getString("rate_per_hour"));
				hmLevelMonthRateMap.put(rs.getString("wlocation_id")+"_"+rs.getString("level_id")+"_"+rs.getString("service_project_id")+"_"+rs.getString("skill_id"), rs.getString("rate_per_month"));
				hmLevelRateCurrIdMap.put(rs.getString("wlocation_id")+"_"+rs.getString("level_id")+"_"+rs.getString("service_project_id")+"_"+rs.getString("skill_id"), rs.getString("curr_id"));
			}
			rs.close();
			pst.close();
			
			
			Map<String, String> hmEmpSkills = new HashMap<String, String>();
			Map<String, String> hmEmpSkillsDayRate = new HashMap<String, String>();
			Map<String, String> hmEmpSkillsHourRate = new HashMap<String, String>();
			Map<String, String> hmEmpSkillsMonthRate = new HashMap<String, String>();
			Map<String, String> hmEmpSkillDayCount = new HashMap<String, String>();
			Map<String, String> hmEmpSkillHourCount = new HashMap<String, String>();
			Map<String, String> hmEmpSkillMonthCount = new HashMap<String, String>();
			if(getStrEmpId() != null && !getStrEmpId().trim().equals("")){
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("SELECT eod.emp_id, skill_id FROM employee_official_details eod left join skills_description sd on eod.emp_id = sd.emp_id" +
						" where eod.emp_id in (select emp_per_id from employee_personal_details where is_alive=true) and eod.emp_id in ("+getStrEmpId()+")");
				sbQuery.append(" order by eod.emp_id");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst=====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					double dblSkillDayRate = uF.parseToDouble(hmEmpSkillsDayRate.get(rs.getString("emp_id")));
					double dblSkillHourRate = uF.parseToDouble(hmEmpSkillsHourRate.get(rs.getString("emp_id")));
					double dblSkillMonthRate = uF.parseToDouble(hmEmpSkillsMonthRate.get(rs.getString("emp_id")));
					int intSkillDayCnt = uF.parseToInt(hmEmpSkillDayCount.get(rs.getString("emp_id")));
					int intSkillHourCnt = uF.parseToInt(hmEmpSkillHourCount.get(rs.getString("emp_id")));
					int intSkillMonthCnt = uF.parseToInt(hmEmpSkillMonthCount.get(rs.getString("emp_id")));
					
					String strLevelId = (String)hmEmpLevel.get(rs.getString("emp_id"));
					String strwLocId = (String)hmEmpWLocation.get(rs.getString("emp_id"));
	
					String strRateCurrId = (String)hmLevelRateCurrIdMap.get(strwLocId+"_"+strLevelId+"_"+hmProDetails.get("PRO_SERVICE_ID")+"_"+rs.getString("skill_id"));
					String strDayRate = (String)hmLevelDayRateMap.get(strwLocId+"_"+strLevelId+"_"+hmProDetails.get("PRO_SERVICE_ID")+"_"+rs.getString("skill_id"));
					String strHourRate = (String)hmLevelHourRateMap.get(strwLocId+"_"+strLevelId+"_"+hmProDetails.get("PRO_SERVICE_ID")+"_"+rs.getString("skill_id"));
					String strMonthRate = (String)hmLevelMonthRateMap.get(strwLocId+"_"+strLevelId+"_"+hmProDetails.get("PRO_SERVICE_ID")+"_"+rs.getString("skill_id"));
					
					if(uF.parseToDouble(strDayRate) > 0) {
						intSkillDayCnt++;
					}
					
					if(uF.parseToDouble(strHourRate) > 0) {
						intSkillHourCnt++;
					}
					
					if(uF.parseToDouble(strMonthRate) > 0) {
						intSkillMonthCnt++;
					}
					
					if(hmProDetails.get("PRO_REPORT_CURR_ID") != null && strRateCurrId != null && !hmProDetails.get("PRO_REPORT_CURR_ID").equals(strRateCurrId)) {
						String strNewCurrValue = hmCurrToFromVal.get(strRateCurrId+"_"+hmProDetails.get("PRO_REPORT_CURR_ID"));
	//					System.out.println("strNewCurrValue ===>>> " + strNewCurrValue);
						double dblDayRate = 0;
						double dblHourRate = 0;
						double dblMonthRate = 0;
						if(uF.parseToDouble(strNewCurrValue) > 0) {
							dblDayRate = uF.parseToDouble(strDayRate) * uF.parseToDouble(strNewCurrValue);
							dblHourRate = uF.parseToDouble(strHourRate) * uF.parseToDouble(strNewCurrValue);
							dblMonthRate = uF.parseToDouble(strMonthRate) * uF.parseToDouble(strNewCurrValue);
						}
						dblSkillDayRate += dblDayRate;
						dblSkillHourRate += dblHourRate;
						dblSkillMonthRate += dblMonthRate;
					} else {
						dblSkillDayRate += uF.parseToDouble(strDayRate);
						dblSkillHourRate += uF.parseToDouble(strHourRate);
						dblSkillMonthRate += uF.parseToDouble(strMonthRate);
					}
					
					hmEmpSkillsDayRate.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(dblSkillDayRate));
					hmEmpSkillDayCount.put(rs.getString("emp_id"), intSkillDayCnt+"");
					
					hmEmpSkillsHourRate.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(dblSkillHourRate));
					hmEmpSkillHourCount.put(rs.getString("emp_id"), intSkillHourCnt+"");
					
					hmEmpSkillsMonthRate.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(dblSkillMonthRate));
					hmEmpSkillMonthCount.put(rs.getString("emp_id"), intSkillMonthCnt+"");
				}
				rs.close();
				pst.close();
			}
			
			/**
			 * insert To Project
			 * */
			GetTeamInfoAjax teamInfoAjax = new GetTeamInfoAjax();
			teamInfoAjax.session = session;
			teamInfoAjax.request = request;
			teamInfoAjax.CF = CF;
			teamInfoAjax.setProId(getStrProject());
			teamInfoAjax.setStrActualBillingType(strActualBillingType);
			
			List<String> alEmp = Arrays.asList(getStrEmpId().split(","));
			for(int i = 0; alEmp!=null && i<alEmp.size(); i++) {
				String strEmpid = alEmp.get(i);
				if(proEmpIdList.contains(strEmpid)){
					continue;
				}
				
				String empOrgId = CF.getEmpOrgId(con, uF, strEmpid);
				pst = con.prepareStatement("select org_id, calculation_type, days from cost_calculation_settings where org_id = ?");
				pst.setInt(1, uF.parseToInt(empOrgId));
//				System.out.println("pst ===> " + pst);
				rs = pst.executeQuery();
				Map<String, String> hmOrgCalType = new HashMap<String, String>();
				while (rs.next()) {
					hmOrgCalType.put("CAL_TYPE", rs.getString("calculation_type"));
					hmOrgCalType.put("DAYS", rs.getString("days"));
				}
				rs.close();
				pst.close();
				
				double nTotalNumberOfDays = 30;
				if(uF.parseToInt(hmOrgCalType.get("CAL_TYPE")) == 3) {
					nTotalNumberOfDays = uF.parseToDouble(hmOrgCalType.get("DAYS"));
				}
				
				String strLevel = hmEmpLevel.get(strEmpid);
				
				String rateDayAmount = "0";
				String rateHourAmount = "0";
				String rateMonthAmount = "0";
				
				String rateAmount = "0";
				String costAmount = "0";
				
				
				double dblRateDayAmount = 0;
				if(uF.parseToDouble(hmEmpSkillsDayRate.get(strEmpid)) > 0) {
					dblRateDayAmount = uF.parseToDouble(hmEmpSkillsDayRate.get(strEmpid)) / uF.parseToDouble(hmEmpSkillDayCount.get(strEmpid));
				}
				rateDayAmount = uF.formatIntoTwoDecimalWithOutComma(dblRateDayAmount);
				double dblRateHourAmount = 0;
				if(uF.parseToDouble(hmEmpSkillsDayRate.get(strEmpid)) > 0) {
					dblRateHourAmount = uF.parseToDouble(hmEmpSkillsHourRate.get(strEmpid)) / uF.parseToDouble(hmEmpSkillHourCount.get(strEmpid));
				}
				rateHourAmount = uF.formatIntoTwoDecimalWithOutComma(dblRateHourAmount);
				
				double dblRateMonthAmount = 0;
				if(uF.parseToDouble(hmEmpSkillsMonthRate.get(strEmpid)) > 0) {
					dblRateMonthAmount = uF.parseToDouble(hmEmpSkillsMonthRate.get(strEmpid)) / uF.parseToDouble(hmEmpSkillMonthCount.get(strEmpid));
				}
				rateMonthAmount = uF.formatIntoTwoDecimalWithOutComma(dblRateMonthAmount);
				
				
				if(strActualBillingType != null && strActualBillingType.equals("H")) {
					rateAmount = rateHourAmount;
				} else if(strActualBillingType != null && strActualBillingType.equals("M")) {
					rateAmount = rateMonthAmount;
				}  else {
					rateAmount = rateDayAmount;
				}
				
				Map<String,Map<String,Map<String,String>>> hmSalaryDetails1 = new HashMap<String,Map<String,Map<String,String>>>();
				pst = con.prepareStatement("select * from salary_details where level_id in (select level_id from level_details where " +
						"level_id =?) and (is_delete is null or is_delete=false) order by level_id, earning_deduction desc, salary_head_id, weight");
				pst.setInt(1, uF.parseToInt(strLevel));
				rs = pst.executeQuery(); 
				while (rs.next()) {
					
					Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(rs.getString("level_id"));
					if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>(); 
					
					Map<String, String> hmInnerSal = new HashMap<String, String>();
					hmInnerSal.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					hmInnerSal.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
					hmInnerSal.put("SALARY_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
					hmInnerSal.put("SUB_SALARY_HEAD_ID", rs.getString("sub_salary_head_id"));
					hmInnerSal.put("SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));
					hmInnerSal.put("IS_CTC_VARIABLE", ""+uF.parseToBoolean(rs.getString("is_ctc_variable")));
					hmInnerSal.put("MULTIPLE_CALCULATION", rs.getString("multiple_calculation"));
					hmInnerSal.put("IS_ALIGN_WITH_PERK", ""+uF.parseToBoolean(rs.getString("is_align_with_perk")));
					hmInnerSal.put("IS_DEFAULT_CAL_ALLOWANCE", ""+uF.parseToBoolean(rs.getString("is_default_cal_allowance")));
					hmInnerSal.put("SALARY_TYPE", rs.getString("salary_type"));
					
					hmSalInner.put(rs.getString("salary_head_id"), hmInnerSal);
					
					hmSalaryDetails1.put(rs.getString("level_id"), hmSalInner);
				}
				rs.close();
				pst.close();
				
				Map<String,Map<String,String>> hmSalInner = hmSalaryDetails1.get(strLevel);
				if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String,String>>(); 
				
				Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmEmpSalaryDetails = CF.getSalaryCalculation(con,hmInnerisDisplay,uF.parseToInt(strEmpid), 30.0d, 0, 0, 30, 0, 0, strLevel, uF, CF, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT),hmSalInner, null, null);
				if(hmEmpSalaryDetails == null) hmEmpSalaryDetails = new HashMap<String, Map<String,String>>();
				
				Iterator<String> it = hmEmpSalaryDetails.keySet().iterator();
				double dblEmpEarningTotal = 0.0d;
				double dblEmpDeductionTotal = 0.0d;
				double dblEmpNetSalTotal = 0.0d;
				while(it.hasNext()) {
					String strSalHeadId = it.next();
					Map<String, String> hmInnerSal = hmEmpSalaryDetails.get(strSalHeadId);
					if(hmInnerSal.get("EARNING_DEDUCTION").equals("E")) {
						dblEmpEarningTotal += uF.parseToDouble(hmInnerSal.get("AMOUNT")); 
					} else if(hmInnerSal.get("EARNING_DEDUCTION").equals("D")) {
						dblEmpDeductionTotal += uF.parseToDouble(hmInnerSal.get("AMOUNT")); 
					}
					
				}
				
				dblEmpNetSalTotal = dblEmpEarningTotal - dblEmpDeductionTotal;
				
				if (hmProDetails.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProDetails.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("D")) {
					dblEmpNetSalTotal = CF.getDailyAmount(dblEmpNetSalTotal, nTotalNumberOfDays, CF);
				} else if (hmProDetails.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProDetails.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("H")) {
					dblEmpNetSalTotal = CF.getHourlyAmount(dblEmpNetSalTotal, nTotalNumberOfDays, CF);
				} else if (hmProDetails.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProDetails.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("M")) {
					dblEmpNetSalTotal = dblEmpNetSalTotal;
				} else {
					dblEmpNetSalTotal = CF.getHourlyAmount(dblEmpNetSalTotal, nTotalNumberOfDays, CF);
				}
				
				double dblEmpSapTotal = 0;
				if(hmProDetails.get("PRO_REPORT_CURR_ID") != null && INR_CURR_ID != null && !hmProDetails.get("PRO_REPORT_CURR_ID").equals(INR_CURR_ID)) {
					String strNewCurrValue = hmCurrToFromVal.get(INR_CURR_ID+"_"+hmProDetails.get("PRO_REPORT_CURR_ID"));
					if(uF.parseToDouble(strNewCurrValue) > 0) {
						dblEmpSapTotal = dblEmpNetSalTotal * uF.parseToDouble(strNewCurrValue);
					}
				} else {
					dblEmpSapTotal = dblEmpNetSalTotal;
				}
				
				costAmount = uF.formatIntoTwoDecimalWithOutComma(dblEmpSapTotal);
				
				teamInfoAjax.setEmpId(strEmpid);
				teamInfoAjax.setRateHourAmount(rateHourAmount);
				teamInfoAjax.setRateDayAmount(rateDayAmount);
				teamInfoAjax.setRateMonthAmount(rateMonthAmount);
				teamInfoAjax.setCostAmount(costAmount);
				teamInfoAjax.setStrActualBillingType(strActualBillingType);
				teamInfoAjax.insertEmployee(CF);
				
			}
			
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
	public String getStrEmpId() {
		return strEmpId;
	}
	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}
	public List<FillProjectList> getProjectList() {
		return projectList;
	}
	public void setProjectList(List<FillProjectList> projectList) {
		this.projectList = projectList;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getStrProject() {
		return strProject;
	}
	public void setStrProject(String strProject) {
		this.strProject = strProject;
	}
	
}
