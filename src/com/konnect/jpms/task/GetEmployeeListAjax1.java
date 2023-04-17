package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class GetEmployeeListAjax1 extends ActionSupport implements IStatements, ServletRequestAware {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String strUserTypeId;
	String strSessionEmpId;
	
	String pro_id;
	String operation;
	String step;
	String actBillingType;
	String strShortCurrency;

	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] skill;
	
	String strLocation;
	String strDepartment;
	String strSkill;
	String strLevel;
	
	String pageType;
	String proType;
	
	String strProOwnerOrTL;
	
	public String execute() {
		
		HttpSession session = request.getSession();
		String strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strUserTypeId = (String)session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		CommonFunctions CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		UtilityFunctions uF = new UtilityFunctions();
	
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_strWLocation(getStrLocation().split(","));
		} else {
			setF_strWLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		if(getStrSkill() != null && !getStrSkill().equals("")) {
			setSkill(getStrSkill().split(","));
		} else {
			setSkill(null);
		}
		
		setStep("3");
	
		getData(CF, uF);
		checkSessionEmpIsProjectOwnerOrTL(uF);
		
		List<String> alEmpId = new ArrayList<String>();
		List<String> alEmpIdExp = new ArrayList<String>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmProInfoDisplay = CF.getProjectInformationDisplay(con);
			request.setAttribute("hmProInfoDisplay", hmProInfoDisplay);
			
			Map<String, String> hmEmpNames = CF.getEmpNameMap(con,null, null);
			Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
			Map<String, String> hmLevel = CF.getLevelMap(con);
			Map<String, String> hmEmpWLocation = CF.getEmpWlocationMap(con);
			Map<String, Map<String, String>> hmWLocation = CF.getWorkLocationMap(con);
			Map<String, String> hmSkillName = CF.getSkillNameMap(con);
			request.setAttribute("hmSkillName", hmSkillName);
			
			Map<String,String> hmLevelDayRateMap = new HashMap<String,String>();
			Map<String,String> hmLevelHourRateMap = new HashMap<String,String>();
			Map<String,String> hmLevelMonthRateMap = new HashMap<String,String>();
			Map<String,String> hmLevelRateCurrIdMap = new HashMap<String,String>();
//			Map<String, String> hmEmpList = new HashMap();
			
			pst = con.prepareStatement("select * from project_emp_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
			StringBuilder sb1 = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();
			int i = 0;
			int j = 0;
			while (rs.next()) {
				if(uF.parseToBoolean(rs.getString("_isteamlead"))) {
					if (i == 0)
						sb1.append(rs.getString("emp_id"));
					else
						sb1.append("," + rs.getString("emp_id"));
					i++;
				} else {
					if (j == 0)
						sb2.append(rs.getString("emp_id"));
					else
						sb2.append("," + rs.getString("emp_id"));
					j++;
				}
			}
			rs.close();
			pst.close();

			request.setAttribute("TL", sb1.toString());

			request.setAttribute("TM", sb2.toString());

			Map<String, String> hmProDetails = CF.getProjectDetailsByProId(con, getPro_id());
			Map<String, String> hmCurrToFromVal = CF.getCurrencyFromIdToIdValue(con);
			Map<String, Map<String, String>> hmCurrData = CF.getCurrencyDetails(con);
			
			request.setAttribute("strProCurrId", hmProDetails.get("PRO_REPORT_CURR_ID"));
			
			Map<String, String> hmCurrInner = hmCurrData.get(hmProDetails.get("PRO_REPORT_CURR_ID"));
			if(hmCurrInner == null) hmCurrInner = new HashMap<String, String>();
			
			setActBillingType(hmProDetails.get("PRO_BILLING_ACTUAL_TYPE"));
			setStrShortCurrency(hmCurrInner.get("SHORT_CURR"));
			
//			pst = con.prepareStatement("SELECT * FROM level_skill_rates where wlocation_id = ?");
			StringBuilder sbskillRateQuery = new StringBuilder();
			sbskillRateQuery.append("SELECT * FROM level_skill_rates ");
			if(getSkill()!=null && getSkill().length>0){
				sbskillRateQuery.append(" where skill_id in ("+StringUtils.join(getSkill(), ",")+") ");
            }
			pst = con.prepareStatement(sbskillRateQuery.toString());
//			System.out.println("=====+>"+pst);
			rs=pst.executeQuery();
			while(rs.next()) {
				hmLevelDayRateMap.put(rs.getString("wlocation_id")+"_"+rs.getString("level_id")+"_"+rs.getString("service_project_id")+"_"+rs.getString("skill_id"), rs.getString("rate_per_day") );
				hmLevelHourRateMap.put(rs.getString("wlocation_id")+"_"+rs.getString("level_id")+"_"+rs.getString("service_project_id")+"_"+rs.getString("skill_id"), rs.getString("rate_per_hour"));
				hmLevelMonthRateMap.put(rs.getString("wlocation_id")+"_"+rs.getString("level_id")+"_"+rs.getString("service_project_id")+"_"+rs.getString("skill_id"), rs.getString("rate_per_month"));
				hmLevelRateCurrIdMap.put(rs.getString("wlocation_id")+"_"+rs.getString("level_id")+"_"+rs.getString("service_project_id")+"_"+rs.getString("skill_id"), rs.getString("curr_id"));
			}
//			System.out.println("hmLevelMonthRateMap ===>> " + hmLevelMonthRateMap);
			rs.close();
			pst.close();
			
//			System.out.println("hmLevelDayRateMap ===>>> " + hmLevelDayRateMap);
//			System.out.println("hmLevelHourRateMap ===>>> " + hmLevelHourRateMap);
			
			
			pst = con.prepareStatement("SELECT * FROM project_emp_details where pro_id = ?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs=pst.executeQuery();
			Map<String,String> mp1=new HashMap<String,String>();
			while(rs.next()) {
				mp1.put(rs.getString("emp_id"), rs.getString("emp_id"));
				mp1.put(rs.getString("emp_id")+"_D", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_rate_per_day")));
				mp1.put(rs.getString("emp_id")+"_H", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_rate_per_hour")));
				mp1.put(rs.getString("emp_id")+"_M", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_rate_per_month")));
			}
			rs.close();
			pst.close();
			request.setAttribute("mp1", mp1);
			
			
			pst = con.prepareStatement("SELECT * FROM project_emp_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs=pst.executeQuery();
			Map<String,String> hmEmpCostAndRate = new HashMap<String,String>();
			Map<String,String> hmEmpAllocatePercentAndBilledUnbilled = new HashMap<String,String>();
			while(rs.next()) {
				if(hmProDetails.get("PRO_BILLING_ACTUAL_TYPE")!=null && hmProDetails.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("D")) {
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_RATE", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_rate_per_day")));
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_COST", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_actual_rate_per_day")));
				} else if(hmProDetails.get("PRO_BILLING_ACTUAL_TYPE")!=null && hmProDetails.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("H")) {
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_RATE", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_rate_per_hour")));
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_COST", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_actual_rate_per_hour")));
				} else if(hmProDetails.get("PRO_BILLING_ACTUAL_TYPE")!=null && hmProDetails.get("PRO_BILLING_ACTUAL_TYPE").equalsIgnoreCase("M")) {
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_RATE", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_rate_per_month")));
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_COST", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_actual_rate_per_month")));
				} else {
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_RATE", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_rate_per_hour")));
					hmEmpCostAndRate.put(rs.getString("emp_id") + "_COST", uF.formatIntoTwoDecimalWithOutComma(rs.getDouble("emp_actual_rate_per_hour")));
				}
				hmEmpAllocatePercentAndBilledUnbilled.put(rs.getString("emp_id") + "_ALLOCATION_PERCENT", uF.showData(rs.getString("allocation_percent"), "0"));
				hmEmpAllocatePercentAndBilledUnbilled.put(rs.getString("emp_id") + "_BILLED_UNBILLED", rs.getString("is_billed"));
				hmEmpAllocatePercentAndBilledUnbilled.put(rs.getString("emp_id") + "_ALLOCATION_DATE", rs.getString("allocation_date")!=null ? uF.getDateFormat(rs.getString("allocation_date"), DBDATE, DATE_FORMAT) : "");
				hmEmpAllocatePercentAndBilledUnbilled.put(rs.getString("emp_id") + "_RELEASE_DATE", rs.getString("release_date")!=null ? uF.getDateFormat(rs.getString("release_date"), DBDATE, DATE_FORMAT) : "");
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpCostAndRate ===>> " + hmEmpCostAndRate);
			request.setAttribute("hmEmpCostAndRate", hmEmpCostAndRate);
			request.setAttribute("hmEmpAllocatePercentAndBilledUnbilled", hmEmpAllocatePercentAndBilledUnbilled);
			
			
//			Map<String, String> hmEmpExtraRateCostAmt = new HashMap<String, String>();
//			pst = con.prepareStatement("select * from variable_cost where pro_id=?");
//			pst.setInt(1, uF.parseToInt(getPro_id()));
//			rs = pst.executeQuery();
////			System.out.println("pst ===>> " + pst);
//			while (rs.next()) {
//				if(rs.getString("amount_type")!= null && rs.getString("amount_type").equals("rate")) {
//					hmEmpExtraRateCostAmt.put(rs.getString("emp_id")+"_EXTRA_RATE", rs.getString("variable_cost"));
//
//				} else if(rs.getString("amount_type")!= null && rs.getString("amount_type").equals("cost")) {
//					hmEmpExtraRateCostAmt.put(rs.getString("emp_id")+"_EXTRA_COST", rs.getString("variable_cost"));
//				
//				}
//			}
//			request.setAttribute("hmEmpExtraRateCostAmt", hmEmpExtraRateCostAmt);

			/*pst = con.prepareStatement("SELECT * FROM employee_personal_details epd, employee_official_details eod,skills_description sd where epd.emp_per_id=sd.emp_id and eod.emp_id = epd.emp_per_id and eod.emp_id = sd.emp_id and is_alive=true and upper(sd.skills_name) in ("+sbSkills.toString()+") and wlocation_id=? and grade_id in (select grade_id from grades_details gd, level_details ld, designation_details dd where ld.level_id = dd.level_id and dd.designation_id = gd.designation_id and dd.level_id in ( "+sbLevelId+") ) order by sd.emp_id");
			pst.setInt(1, nWlocationId);*/
			
//			pst = con.prepareStatement("SELECT * FROM employee_personal_details epd, employee_official_details eod,skills_description sd where epd.emp_per_id=sd.emp_id and eod.emp_id = epd.emp_per_id and eod.emp_id = sd.emp_id and is_alive=true and upper(sd.skills_name) in ("+sbSkills.toString()+") and wlocation_id in ("+sbWLocation.toString()+") and grade_id in (select grade_id from grades_details gd, level_details ld, designation_details dd where ld.level_id = dd.level_id and dd.designation_id = gd.designation_id and dd.level_id in ( "+sbLevel.toString()+") ) order by sd.emp_id");
//			pst.setInt(1, nWlocationId);
			
			pst = con.prepareStatement("select * from projectmntnc where pro_id=? ");
			pst.setInt(1, uF.parseToInt(pro_id));
			rs = pst.executeQuery();
			String proOrgId = null;
			while (rs.next()) {
				proOrgId = rs.getString("org_id");
			}
			rs.close();
			pst.close();
			
			
//			pst = con.prepareStatement("SELECT p.pro_id, p.pro_name, pf.freq_end_date, ped.emp_id,ped.is_billed,ped.allocation_percent from projectmntnc p, projectmntnc_frequency pf, project_emp_details ped where p.pro_id=pf.pro_id " +
//				" and p.pro_id=ped.pro_id and (freq_start_date between ? and ? or freq_end_date between ? and ?) order by ped.emp_id,freq_start_date"); //and p.pro_id not in ("+getPro_id()+")
			pst = con.prepareStatement("SELECT p.pro_id, p.pro_name, p.deadline, ped.emp_id,ped.is_billed,ped.allocation_percent,ped.release_date from projectmntnc p, project_emp_details ped " +
				"where p.pro_id=ped.pro_id and (? between allocation_date and release_date or ? between allocation_date and release_date or allocation_date between ? and ? or release_date between ? and ?) and approve_status != 'approved' order by ped.emp_id,start_date");
			pst.setDate(1, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println("liveProEmpIds pst ===>>"+pst);
			Map<String, Map<String, List<String>>> hmEmpProDetails = new HashMap<String, Map<String,List<String>>>();
			Map<String, String> hmEmpAllocationPercent = new HashMap<String, String>();
			while(rs.next()) {
				Map<String, List<String>> hmProData = hmEmpProDetails.get(rs.getString("emp_id"));
				if(hmProData==null)hmProData = new HashMap<String, List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("pro_name"));
				innerList.add(uF.getDateFormat(rs.getString("release_date"), DBDATE, DATE_FORMAT)); //deadline
				innerList.add(rs.getString("is_billed"));
				innerList.add(rs.getString("allocation_percent"));
				hmProData.put(rs.getString("pro_id"), innerList);
				hmEmpProDetails.put(rs.getString("emp_id"), hmProData);
				
//				if(rs.getInt("pro_id") != uF.parseToInt(getPro_id())) {
					double dblAllocationPercent = uF.parseToDouble(hmEmpAllocationPercent.get(rs.getString("emp_id")));
					dblAllocationPercent += rs.getDouble("allocation_percent");
					hmEmpAllocationPercent.put(rs.getString("emp_id"), dblAllocationPercent+"");
//				}
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpProDetails ===>> " + hmEmpProDetails);
			request.setAttribute("hmEmpProDetails", hmEmpProDetails);
			request.setAttribute("hmEmpAllocationPercent", hmEmpAllocationPercent);
//			System.out.println("hmEmpAllocationPercent ===>> " + hmEmpAllocationPercent);
			
		
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_prev_employment epe, employee_personal_details epd, employee_official_details eod ");
//			sbQuery.append(" left join skills_description sd on eod.emp_id = sd.emp_id ");
			sbQuery.append(" where epd.emp_per_id = eod.emp_id and epe.emp_id=eod.emp_id and is_alive=true and from_date is not null and to_date is not null ");
			
			if(hmProInfoDisplay != null && uF.parseToBoolean(hmProInfoDisplay.get("ONLY_TEAM"))) {
				if(strUserType != null && strUserType.equals(MANAGER)) {
					sbQuery.append(" and (eod.supervisor_emp_id = "+uF.parseToInt(strSessionEmpId)+" or eod.emp_id = "+uF.parseToInt(strSessionEmpId)+")");
				}
			}
			if(uF.parseToInt(proOrgId) > 0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(proOrgId)+" ");
			}
			
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
				sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
			}
			
			if(getF_department()!=null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			
			if(getF_level()!=null && getF_level().length>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from grades_details gd, level_details ld, designation_details dd where ld.level_id = dd.level_id and dd.designation_id = gd.designation_id and dd.level_id in ("+StringUtils.join(getF_level(), ",")+") ) ");
			}
//			if(getSkill()!=null && getSkill().length>0) {
//				sbQuery.append(" and sd.skill_id in ("+StringUtils.join(getSkill(), ",")+") ");
//			}
            sbQuery.append(" and joining_date <=? order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst =======>> " + pst);
			rs = pst.executeQuery();
			Map<String,List<List<String>>>hmEmpStEndMonth = new LinkedHashMap<String, List<List<String>>>();
			while(rs.next()){
				List<List<String>>alOuterEmpStEndMonth = hmEmpStEndMonth.get(rs.getString("emp_id"));
				if(alOuterEmpStEndMonth==null)alOuterEmpStEndMonth = new ArrayList<List<String>>();
				
				List<String>alInnerEmpStEndMonth=new ArrayList<String>();
				alInnerEmpStEndMonth.add(uF.showData(rs.getString("from_date"), ""));
				alInnerEmpStEndMonth.add(uF.showData(rs.getString("to_date"), ""));
				alOuterEmpStEndMonth.add(alInnerEmpStEndMonth);
				hmEmpStEndMonth.put(rs.getString("emp_id"), alOuterEmpStEndMonth);
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive=true ");
			if(hmProInfoDisplay != null && uF.parseToBoolean(hmProInfoDisplay.get("ONLY_TEAM"))) {
				if(strUserType != null && strUserType.equals(MANAGER)) {
					sbQuery.append(" and (eod.supervisor_emp_id = "+uF.parseToInt(strSessionEmpId)+" or eod.emp_id = "+uF.parseToInt(strSessionEmpId)+")");
				}
			}
			if(uF.parseToInt(proOrgId) > 0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(proOrgId)+" ");
			}
			
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
				sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
			}
			
			if(getF_department()!=null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			
			if(getF_level()!=null && getF_level().length>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from grades_details gd, level_details ld, designation_details dd where ld.level_id = dd.level_id and dd.designation_id = gd.designation_id and dd.level_id in ("+StringUtils.join(getF_level(), ",")+") ) ");
			}
            sbQuery.append(" and joining_date <=? order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			Map<String, String> hmEmpTotRelExp = new HashMap<String, String>();
			while(rs.next()){
				hmEmpTotRelExp.put(rs.getString("emp_id")+"_TOT_EXP", rs.getString("total_experience"));
				hmEmpTotRelExp.put(rs.getString("emp_id")+"_REL_EXP", rs.getString("relevant_experience"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmEmpTotRelExp ===>> " + hmEmpTotRelExp);
			
			Map<String, String> hmEmpJoiningDate = CF.getEmpJoiningDateMap(con, uF);
			sbQuery = new StringBuilder();
			sbQuery.append("SELECT eod.emp_id, sd.skill_id,sd.skills_value FROM employee_official_details eod left join skills_description sd on eod.emp_id = sd.emp_id " +
				" left join skills_details sd1 on sd.skill_id=sd1.skill_id where eod.emp_id in (select emp_per_id from employee_personal_details where is_alive=true) ");
			sbQuery.append(" and sd.skills_value is not null and sd.skills_value !='' ");
			if(hmProInfoDisplay != null && uF.parseToBoolean(hmProInfoDisplay.get("ONLY_TEAM"))) {
				if(strUserType != null && strUserType.equals(MANAGER)) {
					sbQuery.append(" and (eod.supervisor_emp_id = "+uF.parseToInt(strSessionEmpId)+" or eod.emp_id = "+uF.parseToInt(strSessionEmpId)+")");
				}
			}
			if(uF.parseToInt(proOrgId) > 0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(proOrgId)+" ");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
				sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
				
			}
			if(getF_department()!=null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_level()!=null && getF_level().length>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from grades_details gd, level_details ld, designation_details dd where ld.level_id = dd.level_id and dd.designation_id = gd.designation_id and dd.level_id in ("+StringUtils.join(getF_level(), ",")+") ) ");
			}
			if(getSkill()!=null && getSkill().length>0) {
				sbQuery.append(" and sd.skill_id in ("+StringUtils.join(getSkill(), ",")+") ");
			}
			sbQuery.append(" order by sd.skills_value desc,eod.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
//				System.out.println("emp_id ===>> "+rs.getString("emp_id"));
				if(uF.parseToDouble(hmEmpAllocationPercent.get(rs.getString("emp_id")))<100) {
					if(!alEmpIdExp.contains(rs.getString("emp_id"))) {
						alEmpIdExp.add(rs.getString("emp_id"));
					}
				}
			}
			rs.close();
			pst.close();
			
			
			Map<String,String> hmEmployeeExperience=new LinkedHashMap<String, String>();
			Map<String, String> hmEmpTotExp = new HashMap<String, String>();
			for(int a=0; alEmpIdExp!=null && a<alEmpIdExp.size(); a++) {
				String empid = alEmpIdExp.get(a);
				List<List<String>>alOuterEmpStEndMonth = hmEmpStEndMonth.get(empid);
				long datediffOuter=0;
				long datediffInner=0;
				long datediff=0;
				int noyear = 0,nomonth = 0,nodays = 0;
				for(i=0;alOuterEmpStEndMonth!=null && i<alOuterEmpStEndMonth.size();i++) {
					List<String>alInnerEmpStEndMonth=alOuterEmpStEndMonth.get(i);
					String stdt=alInnerEmpStEndMonth.get(0);
					String endDt=alInnerEmpStEndMonth.get(1);
					if(stdt!=null && endDt!=null && !stdt.equals("") && !endDt.equals("")) {
						String datedif = uF.dateDifference(uF.showData(stdt, ""), DBDATE, uF.showData(endDt, ""), DBDATE);
						datediff = uF.parseToLong(datedif);
						datediffInner = datediff+datediffInner;
					}/* else {
						datediff=0;
						datediffInner=0;
					}*/
				}
//				if(uF.parseToInt(empid) == 18) {
//					System.out.println("alOuterEmpStEndMonth ===>> "+alOuterEmpStEndMonth);
//				}
				if(alOuterEmpStEndMonth==null || alOuterEmpStEndMonth.size()==0) {
					datediffInner = (uF.parseToLong(hmEmpTotRelExp.get(empid+"_TOT_EXP")) * 365);
				}
//				if(uF.parseToInt(empid) == 18) {
//					System.out.println("datediffInner ===>> "+datediffInner);
//				}
//				System.out.println(empid+ " -- hmEmpJoiningDate ===>> " + hmEmpJoiningDate);
				if(hmEmpJoiningDate!=null && hmEmpJoiningDate.get(empid)!=null && !hmEmpJoiningDate.get(empid).equals("")) {
					String datedif = uF.dateDifference(hmEmpJoiningDate.get(empid), DATE_FORMAT, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
					datediff = uF.parseToLong(datedif);
//					if(uF.parseToInt(empid) == 18) {
//						System.out.println("if datediffInner ===>> "+datediffInner);
//					}
					datediffInner = datediff+datediffInner;
					/*if(uF.parseToInt(empid) == 18) {
						System.out.println("if 1 datediffInner ===>> "+datediffInner);
					}*/
				}
				
				datediffOuter = datediffInner;
				datediffInner=0;
				noyear+=(int) (datediffOuter/365);
		    	nomonth+=(int) ((datediffOuter%365)/30);
		    	nodays+=(int) ((datediffOuter%365)%30);
		     
		    	/*if(uF.parseToInt(empid) == 18) {
					System.out.println("if 1 noyear ===>> "+noyear+" -- nomonth ===>> " + nomonth);
				}*/
		    	
		    	if(nodays>30){
		    		nomonth=nomonth+1;
		    	}
		    	if(nomonth>12){
		    		nomonth=nomonth-12;
		    		noyear=noyear+1;
		    	}
		    	
		    	String yearsLbl = " yrs ";
		    	if(noyear == 1) {
		    		yearsLbl = " yr ";
		    	}
		    	
		    	String monthLbl = " Months ";
		    	if(nomonth == 1) {
		    		monthLbl = " Month ";
		    	}
		    	
		    	hmEmployeeExperience.put(empid, ""+noyear+"."+nomonth+yearsLbl);
		    	hmEmpTotExp.put(empid, noyear+"."+nomonth);
//					System.out.println(empid+"--"+uF.showData((String)hmEmployeeExperience.get(empid), "N/A"));
			}
//			System.out.println("hmEmpTotExp ===>> " + hmEmpTotExp);
//			System.out.println("hmEmployeeExperience ===>> " + hmEmployeeExperience);
			request.setAttribute("hmEmployeeExperience", hmEmployeeExperience);
			request.setAttribute("hmEmpTotExp", hmEmpTotExp);
			
			pst = con.prepareStatement("select * from project_resource_req_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(pro_id));
			rs = pst.executeQuery();
			StringBuilder sbExistWLocIds = null;
			StringBuilder sbExistSkillIds = null;
			Map<String, List<List<String>>> hmSkillMinMaxExp = new HashMap<String, List<List<String>>>();
			List<List<String>> alResReqData = new ArrayList<List<String>>();
			while (rs.next()) {
				if(uF.parseToInt(rs.getString("skill_id"))>0) {
					if(sbExistSkillIds==null) {
						sbExistSkillIds = new StringBuilder();
						sbExistSkillIds.append(rs.getString("skill_id"));
					} else {
						sbExistSkillIds.append(","+rs.getString("skill_id"));
					}
				}
				List<List<String>> alResReqData1 = hmSkillMinMaxExp.get(rs.getString("skill_id"));
				if(alResReqData1==null) alResReqData1 = new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				if(strUserType!=null && strUserType.equals(RECRUITER)) {
					if(rs.getString("wloc_ids_filter")!=null && !rs.getString("wloc_ids_filter").equals("")) {
						if(sbExistWLocIds==null) {
							sbExistWLocIds = new StringBuilder();
							sbExistWLocIds.append(rs.getString("wloc_ids_filter"));
						} else {
							sbExistWLocIds.append(","+rs.getString("wloc_ids_filter"));
						}
					} else {
						if(rs.getString("wloc_ids")!=null && !rs.getString("wloc_ids").equals("")) {
							if(sbExistWLocIds==null) {
								sbExistWLocIds = new StringBuilder();
								sbExistWLocIds.append(rs.getString("wloc_ids"));
							} else {
								sbExistWLocIds.append(","+rs.getString("wloc_ids"));
							}
						}
					}
					if(uF.parseToDouble(rs.getString("min_exp_filter"))>0 || uF.parseToDouble(rs.getString("max_exp_filter"))>0) {
						innerList.add(rs.getString("min_exp_filter"));
						innerList.add(rs.getString("max_exp_filter"));
					} else {
						innerList.add(rs.getString("min_exp"));
						innerList.add(rs.getString("max_exp"));
					}
				} else {
					if(rs.getString("wloc_ids")!=null && !rs.getString("wloc_ids").equals("")) {
						if(sbExistWLocIds==null) {
							sbExistWLocIds = new StringBuilder();
							sbExistWLocIds.append(rs.getString("wloc_ids"));
						} else {
							sbExistWLocIds.append(","+rs.getString("wloc_ids"));
						}
					}
					innerList.add(rs.getString("min_exp"));
					innerList.add(rs.getString("max_exp"));	
				}
				
				innerList.add(rs.getString("req_resource"));
				innerList.add(rs.getString("resource_gap"));
				innerList.add(rs.getString("skill_id")); //4
				innerList.add(rs.getString("project_resource_req_id")); //5
				alResReqData.add(innerList);
				alResReqData1.add(innerList);
				hmSkillMinMaxExp.put(rs.getString("skill_id"), alResReqData1);
			}
			rs.close();
			pst.close();
			request.setAttribute("alResReqData", alResReqData);
			
			if(sbExistWLocIds != null && !sbExistWLocIds.toString().equals("")) {
				setF_strWLocation(sbExistWLocIds.toString().split(","));
			} else {
				setF_strWLocation(null);
			}
			if(sbExistSkillIds != null && !sbExistSkillIds.toString().equals("")) {
				setSkill(sbExistSkillIds.toString().split(","));
			} else {
				setSkill(null);
			}
			
			
//			System.out.println("sbWLocation ==>> " + sbWLocation.toString());
//			System.out.println("sbDepartment ==>> " + sbDepartment.toString());
//			System.out.println("sbLevel ==>> " + sbLevel.toString());
//			System.out.println("sbSkills ==>> " + sbSkills.toString());
			
			sbQuery = new StringBuilder();
			//sbQuery.append("SELECT * FROM employee_personal_details epd, employee_official_details eod,skills_description sd where epd.emp_per_id=sd.emp_id and eod.emp_id = epd.emp_per_id and eod.emp_id = sd.emp_id and is_alive=true ");
//			sbQuery.append("SELECT eod.emp_id, skill_id FROM employee_official_details eod left join skills_description sd on eod.emp_id = sd.emp_id where eod.emp_id in (select emp_per_id from employee_personal_details where is_alive=true) ");
			sbQuery.append("SELECT eod.emp_id, sd.skill_id,sd.skills_value FROM employee_official_details eod left join skills_description sd on eod.emp_id = sd.emp_id " +
				" left join skills_details sd1 on sd.skill_id=sd1.skill_id where eod.emp_id in (select emp_per_id from employee_personal_details where is_alive=true) ");
//			sbQuery.append(" and sd.skills_value is not null and sd.skills_value !='' ");
//			System.out.println("ONLY_TEAM ===>> " + uF.parseToBoolean(hmProInfoDisplay.get("ONLY_TEAM")));
			if(hmProInfoDisplay != null && uF.parseToBoolean(hmProInfoDisplay.get("ONLY_TEAM"))) {
				if(strUserType != null && strUserType.equals(MANAGER)) {
					sbQuery.append(" and (eod.supervisor_emp_id = "+uF.parseToInt(strSessionEmpId)+" or eod.emp_id = "+uF.parseToInt(strSessionEmpId)+")");
				}
			}
			
			if(uF.parseToInt(proOrgId) > 0) {
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(proOrgId)+" ");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
				sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
			}
			if(getF_department()!=null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_level()!=null && getF_level().length>0) {
				sbQuery.append(" and eod.grade_id in (select grade_id from grades_details gd, level_details ld, designation_details dd where ld.level_id = dd.level_id and dd.designation_id = gd.designation_id and dd.level_id in ("+StringUtils.join(getF_level(), ",")+") ) ");
			}
			if(getSkill()!=null && getSkill().length>0) {
				sbQuery.append(" and sd.skill_id in ("+StringUtils.join(getSkill(), ",")+") ");
			}
//			sbQuery.append(" order by eod.emp_id");
			sbQuery.append(" order by sd.skills_value desc,eod.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			
			Map<String, String> hmEmpSkills = new HashMap<String, String>();
			Map<String, String> hmEmpSkillId = new HashMap<String, String>();
			Map<String, String> hmEmpSkillsDayRate = new HashMap<String, String>();
			Map<String, String> hmEmpSkillsHourRate = new HashMap<String, String>();
			Map<String, String> hmEmpSkillsMonthRate = new HashMap<String, String>();
			Map<String, String> hmEmpSkillDayCount = new HashMap<String, String>();
			Map<String, String> hmEmpSkillHourCount = new HashMap<String, String>();
			Map<String, String> hmEmpSkillMonthCount = new HashMap<String, String>();
			
			String empSkills = "";
			while(rs.next()) {
//				boolean flag = true;
				List<List<String>> alResReqData1 = hmSkillMinMaxExp.get(rs.getString("skill_id"));
				String empTotExp = hmEmpTotExp.get(rs.getString("emp_id"));
				int cnt=0;
				for(int k=0; alResReqData1!=null && k<alResReqData1.size(); k++) {
					List<String> innList = alResReqData1.get(k);
					if(innList!=null && (uF.parseToDouble(innList.get(0))> uF.parseToDouble(empTotExp) || uF.parseToDouble(innList.get(1)) < uF.parseToDouble(empTotExp))) {
						cnt++;
					}
				}
				if(alResReqData1!=null && alResReqData1.size()==cnt) {
					continue;
				}
//				System.out.println("strActualBillingType 111 ===>>>> " + strActualBillingType);
				double dblSkillDayRate = uF.parseToDouble(hmEmpSkillsDayRate.get(rs.getString("emp_id")));
				double dblSkillHourRate = uF.parseToDouble(hmEmpSkillsHourRate.get(rs.getString("emp_id")));
				double dblSkillMonthRate = uF.parseToDouble(hmEmpSkillsMonthRate.get(rs.getString("emp_id")));
				int intSkillDayCnt = uF.parseToInt(hmEmpSkillDayCount.get(rs.getString("emp_id")));
				int intSkillHourCnt = uF.parseToInt(hmEmpSkillHourCount.get(rs.getString("emp_id")));
				int intSkillMonthCnt = uF.parseToInt(hmEmpSkillMonthCount.get(rs.getString("emp_id")));
				
				
				empSkills = hmEmpSkills.get(rs.getString("emp_id"));
				if(empSkills == null) empSkills = "";
				
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
//					System.out.println("dblDayRate ===>>> " + dblDayRate);
//					System.out.println("dblHourRate ===>>> " + dblHourRate);
					
					dblSkillDayRate += dblDayRate;
					dblSkillHourRate += dblHourRate;
					dblSkillMonthRate += dblMonthRate;
				} else {
					dblSkillDayRate += uF.parseToDouble(strDayRate);
					dblSkillHourRate += uF.parseToDouble(strHourRate);
					dblSkillMonthRate += uF.parseToDouble(strMonthRate);
				}
				
				double skillVal = (uF.parseToDouble(rs.getString("skills_value"))/2);
				if(rs.getInt("skill_id")>0) {
					if(empSkills != null && empSkills.equals("")) {
						empSkills = uF.showData(hmSkillName.get(rs.getString("skill_id")),"")+" ("+uF.formatIntoOneDecimalIfDecimalValIsThere(skillVal)+")";
					} else {
						empSkills = empSkills + "<br/>"+ uF.showData(hmSkillName.get(rs.getString("skill_id")),"")+" ("+uF.formatIntoOneDecimalIfDecimalValIsThere(skillVal)+")";
					}
				}
				String empSkillId = hmEmpSkillId.get(rs.getString("emp_id"));
				if(empSkillId==null && rs.getInt("skill_id")>0) {
					hmEmpSkillId.put(rs.getString("emp_id"), rs.getString("skill_id"));
				}
				hmEmpSkills.put(rs.getString("emp_id"), empSkills);
				
				hmEmpSkillsDayRate.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(dblSkillDayRate));
				hmEmpSkillDayCount.put(rs.getString("emp_id"), intSkillDayCnt+"");
				
				hmEmpSkillsHourRate.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(dblSkillHourRate));
				hmEmpSkillHourCount.put(rs.getString("emp_id"), intSkillHourCnt+"");
				
				hmEmpSkillsMonthRate.put(rs.getString("emp_id"), uF.formatIntoTwoDecimalWithOutComma(dblSkillMonthRate));
				hmEmpSkillMonthCount.put(rs.getString("emp_id"), intSkillMonthCnt+"");
				
				if(rs.getInt("emp_id") == 619) {
//					System.out.println("emp_id ===>> "+rs.getString("emp_id") + " -- hmEmpAllocationPercent ===>> " + hmEmpAllocationPercent.get(rs.getString("emp_id")));
				}
				if(uF.parseToDouble(hmEmpAllocationPercent.get(rs.getString("emp_id")))<100) {
					if(!alEmpId.contains(rs.getString("emp_id"))) {
						alEmpId.add(rs.getString("emp_id"));
					}
				}
				
//				count++;
//				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			
			
//			System.out.println("hmEmployeeExperience ===>>>> " + hmEmployeeExperience);
//			System.out.println("hmEmpSkills ===>>>> " + hmEmpSkills);
//			System.out.println("hmEmpSkillsDayRate ===>>> " + hmEmpSkillsDayRate);
			
//			System.out.println("hmEmpSkillsMonthRate ===>>> " + hmEmpSkillsMonthRate);
//			System.out.println("hmEmpSkillMonthCount ===>>> " + hmEmpSkillMonthCount);
			
			
			List<String> liveProEmpIds = new ArrayList<String>();
			pst = con.prepareStatement("select resource_ids from activity_info ai, projectmntnc p where ai.pro_id = p.pro_id and p.approve_status = 'n' " +
				"and (ai.completed < 100 or ai.completed is null) and ((ai.start_date >= ? and ai.deadline <= ?) or (ai.start_date <= ? and ai.deadline >= ?) or (ai.start_date >= ? and ai.start_date <= ?))");
			pst.setDate(1, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
			pst.setDate(6, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println("liveProEmpIds pst === >> " +pst);
			
			while(rs.next()) {
				if(rs.getString("resource_ids") != null && !rs.getString("resource_ids").equals("")) {
					List<String> empIdList = Arrays.asList(rs.getString("resource_ids").split(","));
					 for(String empId : empIdList) {
						 if(empId!=null && !empId.equals("")) {
							 if(!liveProEmpIds.contains(empId)) {
								 liveProEmpIds.add(empId);
							 }
						 }
					 }  
				}
			}
			rs.close();
			pst.close();
//			System.out.println("liveProEmpIds ===>> " + liveProEmpIds);
			
			Map<String, String> hmTaskAllocation  = new HashMap<String, String>();
			for(int a=0; liveProEmpIds != null && a< liveProEmpIds.size(); a++) {
//				pst = con.prepareStatement("select count(*) as task_no from activity_info ai, projectmntnc p where ai.pro_id = p.pro_id and p.approve_status = 'n' " +
//						" and ai.completed < 100  and ai.start_date>=? and ai.deadline <=? and ai.resource_ids like '%,"+liveProEmpIds.get(a)+",%' ");
				pst = con.prepareStatement("select count(a.*) as task_no from (select task_id,activity_name,parent_task_id,pro_id,start_date,deadline," +
					"completed from activity_info where resource_ids like '%,"+liveProEmpIds.get(a)+",%' and task_id not in (select parent_task_id " +
					" from activity_info where resource_ids like '%,"+liveProEmpIds.get(a)+",%' and parent_task_id is not null)) a, projectmntnc pmc " +
					" where pmc.pro_id=a.pro_id and pmc.approve_status='n' and (parent_task_id in (select task_id from activity_info where resource_ids " +
					" like '%,"+liveProEmpIds.get(a)+",%') or parent_task_id = 0) and (a.completed < 100 or a.completed is null) and ((a.start_date >= ? and a.deadline <= ?) or " +
					" (a.start_date <= ? and a.deadline >= ?) or (a.start_date >= ? and a.start_date <= ?))");
				pst.setDate(1, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(hmProDetails.get("PRO_START_DATE"), DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(hmProDetails.get("PRO_END_DATE"), DATE_FORMAT));
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " + pst);
				while(rs.next()) {
					if(uF.parseToInt(rs.getString("task_no")) > 5) {
						hmTaskAllocation.put(liveProEmpIds.get(a), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: red;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation('"+liveProEmpIds.get(a)+"','"+hmProDetails.get("PRO_START_DATE")+"','"+hmProDetails.get("PRO_END_DATE")+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
					} else if(uF.parseToInt(rs.getString("task_no")) >= 2) {
						hmTaskAllocation.put(liveProEmpIds.get(a), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: yellow;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation('"+liveProEmpIds.get(a)+"','"+hmProDetails.get("PRO_START_DATE")+"','"+hmProDetails.get("PRO_END_DATE")+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
					} else {
						hmTaskAllocation.put(liveProEmpIds.get(a), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\"><a href=\"javascript:void(0);\" onclick=\"viewWorkAllocation('"+liveProEmpIds.get(a)+"','"+hmProDetails.get("PRO_START_DATE")+"','"+hmProDetails.get("PRO_END_DATE")+"')\">"+uF.parseToInt(rs.getString("task_no"))+"</a></div>");
					}
				}
				rs.close();
				pst.close();
			}
			
//			Map<String, String> hmEmpSalaryMap = CF.getEmpNetSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", strActualBillingType);
			Map<String, String> hmEmpSalaryMap = new HashMap<String, String>();
			
			Map<String, Map<String, String>> hmLeaveDatesType = new HashMap<String, Map<String, String>>();
//			Map<String, Map<String, String>> hmLeaves = CF.getLeaveDates(con, hmProDetails.get("PRO_START_DATE"), hmProDetails.get("PRO_END_DATE"), CF, hmLeaveDatesType, false, null);
			Map<String, Map<String, String>> hmLeaves = CF.getActualLeaveDates(con, CF, uF, hmProDetails.get("PRO_START_DATE"), hmProDetails.get("PRO_END_DATE"), hmLeaveDatesType, false, null);
			
//			Map<String, String> hmEmpGrossSalaryMap = CF.getEmpGrossSalary(uF, CF, con, uF.getCurrentDate(CF.getStrTimeZone())+"", strActualBillingType);
			
			for(int a=0; alEmpId != null && a< alEmpId.size(); a++) {
				String strLevel = hmEmpLevel.get(alEmpId.get(a));
				
//				Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
//				Map<String, Map<String, String>> hmEmpSalaryDetails = CF.getSalaryCalculation(con,hmInnerisDisplay,uF.parseToInt(alEmpId.get(a)), 30.0d, 0, 0, 30, 0, 0, strLevel, uF, CF, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT));
//				if(hmEmpSalaryDetails == null) hmEmpSalaryDetails = new HashMap<String, Map<String,String>>();
//				
//				Iterator<String> it = hmEmpSalaryDetails.keySet().iterator();
//				double dblEmpEarningTotal = 0.0d;
//				double dblEmpDeductionTotal = 0.0d;
//				double dblEmpNetSalTotal = 0.0d;
//				while(it.hasNext()) {
//					String strSalHeadId = it.next();
//					Map<String, String> hmInnerSal = hmEmpSalaryDetails.get(strSalHeadId);
//					if(hmInnerSal.get("EARNING_DEDUCTION").equals("E")) {
//						dblEmpEarningTotal += uF.parseToDouble(hmInnerSal.get("AMOUNT")); 
//					} else if(hmInnerSal.get("EARNING_DEDUCTION").equals("D")) {
//						dblEmpDeductionTotal += uF.parseToDouble(hmInnerSal.get("AMOUNT")); 
//					}
//					
//				}
//				dblEmpNetSalTotal = dblEmpEarningTotal - dblEmpDeductionTotal;
				
				double dblEmpNetSalTotal = 0.0d;
				int empOrgId = 0;
				pst = con.prepareStatement("select month_ctc, org_id from employee_official_details where emp_id=?");
				pst.setInt(1, uF.parseToInt(alEmpId.get(a)));
				rs = pst.executeQuery();
				while(rs.next()) {
					dblEmpNetSalTotal = uF.parseToDouble(rs.getString("month_ctc"));
					empOrgId = rs.getInt("org_id");
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select org_id, calculation_type, days from cost_calculation_settings where org_id = ?");
				pst.setInt(1, empOrgId);
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
				if(uF.parseToInt(hmOrgCalType.get("CAL_TYPE")) == 3 && uF.parseToDouble(hmOrgCalType.get("DAYS"))>0) {
					nTotalNumberOfDays = uF.parseToDouble(hmOrgCalType.get("DAYS"));
				}
				
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
//						System.out.println("strNewCurrValue ===>>> " + strNewCurrValue);
					if(uF.parseToDouble(strNewCurrValue) > 0) {
						dblEmpSapTotal = dblEmpNetSalTotal * uF.parseToDouble(strNewCurrValue);
					}
//						System.out.println("dblEmpSapTotal ===>>> " + dblEmpSapTotal);
				} else {
					dblEmpSapTotal = dblEmpNetSalTotal;
				}
				
				hmEmpSalaryMap.put(alEmpId.get(a), uF.formatIntoTwoDecimalWithOutComma(dblEmpSapTotal));
			}
			
//			System.out.println("hmEmpSalaryMap========>"+hmEmpSalaryMap);
			
//			Map<String, String> hmEmpGrossSalaryMap = new HashMap<String, String>();
			
			request.setAttribute("strActualBillingType", hmProDetails.get("PRO_BILLING_ACTUAL_TYPE"));
			request.setAttribute("hmLeaves", hmLeaves);
			request.setAttribute("hmEmpSalaryMap", hmEmpSalaryMap);
			
			request.setAttribute("hmEmpSkills", hmEmpSkills);
			request.setAttribute("hmEmpSkillId", hmEmpSkillId);
			request.setAttribute("hmEmpSkillsDayRate", hmEmpSkillsDayRate);
			request.setAttribute("hmEmpSkillDayCount", hmEmpSkillDayCount);
			
			request.setAttribute("hmEmpSkillsHourRate", hmEmpSkillsHourRate);
			request.setAttribute("hmEmpSkillHourCount", hmEmpSkillHourCount);
			
			request.setAttribute("hmEmpSkillsMonthRate", hmEmpSkillsMonthRate);
			request.setAttribute("hmEmpSkillMonthCount", hmEmpSkillMonthCount);
			
			request.setAttribute("hmTaskAllocation", hmTaskAllocation);
			
			request.setAttribute("alEmpId", alEmpId);
//			request.setAttribute("hmEmpList", hmEmpList);
			request.setAttribute("hmEmpNames", hmEmpNames);
			request.setAttribute("hmEmpLevel", hmEmpLevel);
			request.setAttribute("hmLevel", hmLevel);
			request.setAttribute("hmEmpWLocation", hmEmpWLocation);
			request.setAttribute("hmWLocation", hmWLocation);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return SUCCESS;
	}
	
	
	private void checkSessionEmpIsProjectOwnerOrTL(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			pst = con.prepareStatement("select * from project_emp_details where _isteamlead=true and pro_id=? and emp_id=? ");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			pst.setInt(2, uF.parseToInt(strSessionEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				setStrProOwnerOrTL("2");
			}
			rs.close();
			pst.close();
			
		//===start parvez date: 13-10-2022===	
//			pst = con.prepareStatement("select * from projectmntnc where pro_id=? and project_owner=? ");
			pst = con.prepareStatement("select * from projectmntnc where pro_id=? and project_owners like '%,"+strSessionEmpId+",%' ");
			pst.setInt(1, uF.parseToInt(getPro_id()));
//			pst.setInt(2, uF.parseToInt(strSessionEmpId));
		//===end parvez date: 13-10-2022===	
			rs = pst.executeQuery();
			while (rs.next()) {
				setStrProOwnerOrTL("1");
			}
			rs.close();
			pst.close();
//			System.out.println("getStrProOwnerOrTL() ===>> " + getStrProOwnerOrTL());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getData(CommonFunctions CF, UtilityFunctions uF) {
		
		List<String> alExistEmpId = new ArrayList<String>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from project_emp_details where pro_id=?");
			pst.setInt(1, uF.parseToInt(getPro_id()));
			rs = pst.executeQuery();
//			System.out.println("pst ===>> " +pst);
			
			Map<String, String> hmTLMembEmp = new HashMap<String, String>();
			StringBuilder existEmpIds = null;
			List<String> proEmpIdList = new ArrayList<String>();
			while (rs.next()) {
				if(existEmpIds == null) {
					existEmpIds = new StringBuilder();
					existEmpIds.append(rs.getString("emp_id"));
				} else {
					existEmpIds.append("," + rs.getString("emp_id"));
				}
				
				if(uF.parseToBoolean(rs.getString("_isteamlead"))) {
					hmTLMembEmp.put(rs.getString("emp_id")+"_T", "TL");
				} else {
					hmTLMembEmp.put(rs.getString("emp_id")+"_M", "MEMB");
				}
				if(!proEmpIdList.contains(rs.getString("emp_id"))) {
					proEmpIdList.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpTaskCount = new HashMap<String, String>();
			for(int i=0; proEmpIdList != null && i<proEmpIdList.size(); i++) {
				pst = con.prepareStatement("select count(task_id) as taskCnt from activity_info where pro_id = ? and resource_ids like '%,"+proEmpIdList.get(i)+",%' ");
				pst.setInt(1, uF.parseToInt(getPro_id()));
				rs = pst.executeQuery();
//				System.out.println("pst ===>> " +pst);
				while (rs.next()) {
					hmEmpTaskCount.put(proEmpIdList.get(i), rs.getString("taskCnt"));
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("hmEmpTaskCount", hmEmpTaskCount);
			
			if(existEmpIds == null) {
				existEmpIds = new StringBuilder();
				existEmpIds.append("0");
			}
			
//			System.out.println("existEmpIds ===>>>> " + existEmpIds);
//			System.out.println("hmTLMembEmp ===>>>> " + hmTLMembEmp);
			
			request.setAttribute("hmTLMembEmp", hmTLMembEmp);
			
			StringBuilder sbQuery = new StringBuilder();
			
			sbQuery.append("SELECT eod.emp_id, skill_id FROM employee_official_details eod left join skills_description sd on eod.emp_id = sd.emp_id where eod.emp_id > 0 and eod.emp_id in ("+existEmpIds.toString()+") ");
			sbQuery.append(" order by eod.emp_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			
			while(rs.next()) {
				if(!alExistEmpId.contains(rs.getString("emp_id"))) {
					alExistEmpId.add(rs.getString("emp_id"));
				}
			}
			rs.close();
			pst.close();
//			System.out.println("alExistEmpId ===>> " + alExistEmpId);
			
			request.setAttribute("alExistEmpId", alExistEmpId);
			
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
	public String getPro_id() {
		return pro_id;
	}
	public void setPro_id(String pro_id) {
		this.pro_id = pro_id;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	public String getStep() {
		return step;
	}
	public void setStep(String step) {
		this.step = step;
	}

	public String getActBillingType() {
		return actBillingType;
	}

	public void setActBillingType(String actBillingType) {
		this.actBillingType = actBillingType;
	}

	public String getStrShortCurrency() {
		return strShortCurrency;
	}

	public void setStrShortCurrency(String strShortCurrency) {
		this.strShortCurrency = strShortCurrency;
	}

	public String getPageType() {
		return pageType;
	}

	public void setPageType(String pageType) {
		this.pageType = pageType;
	}

	public String getStrLocation() {
		return strLocation;
	}

	public void setStrLocation(String strLocation) {
		this.strLocation = strLocation;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public String getStrSkill() {
		return strSkill;
	}

	public void setStrSkill(String strSkill) {
		this.strSkill = strSkill;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getSkill() {
		return skill;
	}

	public void setSkill(String[] skill) {
		this.skill = skill;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public String getStrProOwnerOrTL() {
		return strProOwnerOrTL;
	}

	public void setStrProOwnerOrTL(String strProOwnerOrTL) {
		this.strProOwnerOrTL = strProOwnerOrTL;
	}

}
