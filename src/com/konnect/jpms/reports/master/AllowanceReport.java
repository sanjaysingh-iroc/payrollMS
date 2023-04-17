package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AllowanceReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpSessionId = null;
	String strUserType = null;
	CommonFunctions CF = null;
	
	String paycycle;
	String f_org;
	String[] f_strWLocation; 
	String f_level;
	String[] f_department;
	String[] f_service;
	String f_salaryhead;
	

	List<FillPayCycles> paycycleList;
	List<FillOrganisation> orgList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;
	List<FillSalaryHeads> salaryHeadList;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		strEmpSessionId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);
				
		request.setAttribute(PAGE, PReportAllowance); 
		request.setAttribute(TITLE, "Allowance Report");
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
		if(uF.parseToInt(getF_org()) == 0){
			setF_org((String)session.getAttribute(ORGID));
			setF_salaryhead(null);
			setPaycycle(null);
		}
		String[] strPayCycleDates;
		if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
			strPayCycleDates = getPaycycle().split("-");
		} else {
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}
		
		if(uF.parseToInt(getF_salaryhead()) > 0){
			viewAllowanceReport(uF,strPayCycleDates);
		}
			
		return loadAllowanceReport(uF);
	}
	
	
	private String loadAllowanceReport(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
//		salaryHeadList = new FillSalaryHeads(request).fillAllowanceSalaryHeadsByOrg(uF, getF_org());
		salaryHeadList = new FillSalaryHeads(request).fillAllowanceSalaryHeads(getF_level());
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					strOrg=orgList.get(i).getOrgName();
				}
			}
			if(strOrg!=null && !strOrg.equals("")) {
				hmFilter.put("ORGANISATION", strOrg);
			} else {
				hmFilter.put("ORGANISATION", "All Organisation");
			}
			
		} else {
			hmFilter.put("ORGANISATION", "All Organisation");
		}
		
		alFilter.add("LEVEL");
		if(getF_level()!=null) {
			String strLevel="";
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				if(getF_level().equals(levelList.get(i).getLevelId())) {
					strLevel=levelList.get(i).getLevelCodeName();
				}
			}
			if(strLevel!=null && !strLevel.equals("")) {
				hmFilter.put("LEVEL", strLevel);
			} else {
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}
		
		alFilter.add("SALARYHEADS");
		if(getF_salaryhead()!=null) {
			String strSalHead="";
			for(int i=0;salaryHeadList!=null && i<salaryHeadList.size();i++) {
				if(getF_salaryhead().equals(salaryHeadList.get(i).getSalaryHeadId())) {
					strSalHead=salaryHeadList.get(i).getSalaryHeadName();
				}
			}
			if(strSalHead!=null && !strSalHead.equals("")) {
				hmFilter.put("SALARYHEADS", strSalHead);
			} else {
				hmFilter.put("SALARYHEADS", "All Salary Heads");
			}
		} else {
			hmFilter.put("SALARYHEADS", "All Salary Heads");
		}
				
		alFilter.add("LOCATION");
		if(getF_strWLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_strWLocation().length;j++) {
					if(getF_strWLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
						if(k==0) {
							strLocation=wLocationList.get(i).getwLocationName();
						} else {
							strLocation+=", "+wLocationList.get(i).getwLocationName();
						}
						k++;
					}
				}
			}
			if(strLocation!=null && !strLocation.equals("")) {
				hmFilter.put("LOCATION", strLocation);
			} else {
				hmFilter.put("LOCATION", "All Locations");
			}
		} else {
			hmFilter.put("LOCATION", "All Locations");
		}
		
		alFilter.add("DEPARTMENT");
		if(getF_department()!=null) {
			String strDepartment="";
			int k=0;
			for(int i=0;departmentList!=null && i<departmentList.size();i++) {
				for(int j=0;j<getF_department().length;j++) {
					if(getF_department()[j].equals(departmentList.get(i).getDeptId())) {
						if(k==0) {
							strDepartment=departmentList.get(i).getDeptName();
						} else {
							strDepartment+=", "+departmentList.get(i).getDeptName();
						}
						k++;
					}
				}
			}
			if(strDepartment!=null && !strDepartment.equals("")) {
				hmFilter.put("DEPARTMENT", strDepartment);
			} else {
				hmFilter.put("DEPARTMENT", "All Departments");
			}
		} else {
			hmFilter.put("DEPARTMENT", "All Departments");
		}
		
		alFilter.add("SERVICE");
		if(getF_service()!=null) {
			String strService="";
			int k=0;
			for(int i=0;serviceList!=null && i<serviceList.size();i++) {
				for(int j=0;j<getF_service().length;j++) {
					if(getF_service()[j].equals(serviceList.get(i).getServiceId())) {
						if(k==0) {
							strService=serviceList.get(i).getServiceName();
						} else {
							strService+=", "+serviceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strService!=null && !strService.equals("")) {
				hmFilter.put("SERVICE", strService);
			} else {
				hmFilter.put("SERVICE", "All SBUs");
			}
		} else {
			hmFilter.put("SERVICE", "All SBUs");
		}
		
		alFilter.add("PAYCYCLE");
		String strPaycycle = "";
		String[] strPayCycleDates = null;
		if (getPaycycle() != null) {
			strPayCycleDates = getPaycycle().split("-");
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			
			strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
		}
		hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	private void viewAllowanceReport(UtilityFunctions uF, String[] strPayCycleDates) {

		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus =CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmAllowanceCondition = CF.getAllowanceCondition();
			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(getF_level()));
			Map<String, String> hmAllowancePaymentLogic = CF.getAllowancePaymentLogic();
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from payroll_generation pg, employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and pg.emp_id=epd.emp_per_id and pg.emp_id=eod.emp_id and pg.salary_head_id=? and pg.paycycle=? and pg.paid_from=? and pg.paid_to=? and pg.amount > 0 ");
			
			if(uF.parseToInt(getF_level()) >0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id="+uF.parseToInt(getF_level())+") ");
            }
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                	sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            } 
            if(getF_strWLocation()!=null && getF_strWLocation().length>0){
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
        	pst = con.prepareStatement(sbQuery.toString());
            pst.setInt(1, uF.parseToInt(getF_salaryhead()));
            pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
        	pst.setDate(3, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
	
//			System.out.println("pst--"+pst);
			rs = pst.executeQuery();
			List<Map<String, String>> alEmp = new ArrayList<Map<String,String>>();
			List<String> alEmpIds = new ArrayList<String>();
			while(rs.next()){
				Map<String, String> hmEmp = new HashMap<String, String>();
				hmEmp.put("EMP_ID", rs.getString("emp_per_id"));
				hmEmp.put("EMP_CODE", uF.showData(rs.getString("empcode"), ""));
				
			//	String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
			
				String strMiddleName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strMiddleName = " "+rs.getString("emp_mname");
					}
				}
				
				String strEmpName = rs.getString("emp_fname") + uF.showData(strMiddleName, "")+" "+ rs.getString("emp_lname");
			
				
				hmEmp.put("EMP_NAME", uF.showData(strEmpName, ""));
				hmEmp.put("ALLOWANCE_AMOUNT", uF.showData(rs.getString("amount"), "0"));
				hmEmp.put("EMP_PAN_NO", uF.showData(rs.getString("emp_pan_no"), ""));
				alEmp.add(hmEmp);

				if(!alEmpIds.contains(rs.getString("emp_per_id"))){
					alEmpIds.add(rs.getString("emp_per_id"));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("alEmp", alEmp);
			if(alEmpIds.size() > 0){
				String strEmpIds = StringUtils.join(alEmpIds.toArray(),",");
				
				pst = con.prepareStatement("select aid.emp_id,apd.* from allowance_individual_details aid, allowance_pay_details apd " +
						"where aid.allowance_id=apd.allowance_id and aid.emp_id in ("+strEmpIds+") and aid.salary_head_id=? and aid.pay_paycycle=? " +
						"and aid.paid_from=? and aid.paid_to=?");
				pst.setInt(1, uF.parseToInt(getF_salaryhead()));
				pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
				pst.setDate(3, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmAssignConditionAmt = new HashMap<String, String>();
				Map<String, String> hmAssignLogicAmt = new HashMap<String, String>();
				List<String> alConditionId = new ArrayList<String>();
				List<String> alLogicId = new ArrayList<String>();
				while(rs.next()){
					if(uF.parseToInt(rs.getString("condition_id")) > 0){
						hmAssignConditionAmt.put(rs.getString("emp_id")+"_"+rs.getString("condition_id"), uF.parseToDouble(rs.getString("amount")) > 0.0d ? rs.getString("amount") : null);
						if(!alConditionId.contains(rs.getString("condition_id"))){
							alConditionId.add(rs.getString("condition_id"));
						}
					} else if(uF.parseToInt(rs.getString("payment_logic_id")) > 0){
						hmAssignLogicAmt.put(rs.getString("emp_id")+"_"+rs.getString("payment_logic_id"), uF.parseToDouble(rs.getString("amount")) > 0.0d ? rs.getString("amount") : null);
						if(!alLogicId.contains(rs.getString("payment_logic_id"))){
							alLogicId.add(rs.getString("payment_logic_id"));
						}
					}
				}
				rs.close();
				pst.close();
				request.setAttribute("hmAssignConditionAmt", hmAssignConditionAmt);
				request.setAttribute("hmAssignLogicAmt", hmAssignLogicAmt);
				
				String strConditionIds = StringUtils.join(alConditionId.toArray(),",");
				pst = con.prepareStatement("select * from allowance_condition_details where salary_head_id=? and level_id=? and org_id=? and allowance_condition_id in ("+strConditionIds+")");
				pst.setInt(1, uF.parseToInt(getF_salaryhead()));
				pst.setInt(2, uF.parseToInt(getF_level()));
				pst.setInt(3, uF.parseToInt(getF_org()));
				rs = pst.executeQuery();
				List<Map<String, String>> alCondition = new ArrayList<Map<String,String>>();
				while(rs.next()){
					Map<String,String> hmCondition = new HashMap<String, String>();
					hmCondition.put("ALLOWANCE_CONDITION_ID", rs.getString("allowance_condition_id"));
					hmCondition.put("ALLOWANCE_CONDITION_SLAB", uF.showData(rs.getString("condition_slab"),""));
					hmCondition.put("ALLOWANCE_CONDITION_TYPE", rs.getString("allowance_condition"));
					hmCondition.put("ALLOWANCE_CONDITION", uF.showData(hmAllowanceCondition.get(rs.getString("allowance_condition")),""));
					hmCondition.put("MIN_CONDITION", uF.showData(rs.getString("min_condition"),"0"));
					hmCondition.put("MAX_CONDITION", uF.showData(rs.getString("max_condition"),"0"));
					hmCondition.put("CUSTOM_FACTOR_TYPE", uF.showData(rs.getString("custom_type"),"A"));
					hmCondition.put("CUSTOM_AMT_PERCENTAGE", uF.showData(rs.getString("custom_amt_percentage"),"0"));
					
					alCondition.add(hmCondition);
				}
				rs.close(); 
				pst.close();
				request.setAttribute("alCondition", alCondition);
				
				pst = con.prepareStatement("select * from allowance_condition_details");
				rs = pst.executeQuery();
				Map<String, String> hmAllowanceConditionSlab = new HashMap<String, String>();
				while(rs.next()){
					hmAllowanceConditionSlab.put(rs.getString("allowance_condition_id"), uF.showData(rs.getString("condition_slab"),""));
				}
				rs.close();
				pst.close();
				
				String strLogicsIds = StringUtils.join(alLogicId.toArray(),",");
				pst = con.prepareStatement("select * from allowance_payment_logic where salary_head_id=? and level_id=? and org_id=? and payment_logic_id in ("+strLogicsIds+")");
				pst.setInt(1, uF.parseToInt(getF_salaryhead()));
				pst.setInt(2, uF.parseToInt(getF_level()));
				pst.setInt(3, uF.parseToInt(getF_org()));
				rs = pst.executeQuery();
				List<Map<String, String>> alLogic = new ArrayList<Map<String,String>>();
				while(rs.next()){
					Map<String,String> hmLogic = new HashMap<String, String>();
					hmLogic.put("PAYMENT_LOGIC_ID", rs.getString("payment_logic_id"));
					hmLogic.put("PAYMENT_LOGIC_SLAB", uF.showData(rs.getString("payment_logic_slab"),""));
					hmLogic.put("ALLOWANCE_CONDITION_ID", rs.getString("allowance_condition_id"));
					hmLogic.put("ALLOWANCE_CONDITION", uF.showData(hmAllowanceConditionSlab.get(rs.getString("allowance_condition_id")),""));
					
					hmLogic.put("ALLOWANCE_PAYMENT_LOGIC_ID", rs.getString("payment_logic"));
					hmLogic.put("ALLOWANCE_PAYMENT_LOGIC", uF.showData(hmAllowancePaymentLogic.get(rs.getString("payment_logic")),""));
	
					hmLogic.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					hmLogic.put("SALARY_HEAD_NAME", uF.showData(hmSalaryHeadsMap.get(rs.getString("salary_head_id")),""));
					
					hmLogic.put("FIXED_AMOUNT", uF.showData(rs.getString("fixed_amount"),"0"));
					hmLogic.put("CAL_SALARY_HEAD_ID", uF.showData(rs.getString("cal_salary_head_id"),""));
					hmLogic.put("CAL_SALARY_HEAD_NAME", uF.showData(hmSalaryHeadsMap.get(rs.getString("cal_salary_head_id")),""));
					
					alLogic.add(hmLogic);
					
				}
				rs.close();
				pst.close();
				request.setAttribute("alLogic", alLogic);
			}
			
			/*if(alEmpIds.size() > 0){
				
				String strEmpIds = StringUtils.join(alEmpIds.toArray(),",");
				
				pst = con.prepareStatement("select * from allowance_individual_details where emp_id in ("+strEmpIds+") and paid_from = ? and paid_to=? and pay_paycycle=? and salary_head_id=?");
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
				pst.setInt(4, uF.parseToInt(getF_salaryhead()));
				rs = pst.executeQuery();
				Map<String, String> hmAllowance = new HashMap<String, String>();
				Map<String, String> hmAllowanceId = new HashMap<String, String>();
				Map<String, String> hmAllowanceValue = new HashMap<String, String>();
				while (rs.next()) {
					hmAllowance.put(rs.getString("emp_id"),rs.getString("is_approved"));
					hmAllowanceId.put(rs.getString("emp_id"),rs.getString("allowance_id"));
					hmAllowanceValue.put(rs.getString("emp_id"), uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("pay_amount"))));
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmAllowance", hmAllowance);
				request.setAttribute("hmAllowanceId", hmAllowanceId);
				request.setAttribute("hmAllowanceValue", hmAllowanceValue);
				
				pst = con.prepareStatement("select * from allowance_condition_details");
				rs = pst.executeQuery();
				Map<String, String> hmAllowanceConditionSlab = new HashMap<String, String>();
				while(rs.next()){
					hmAllowanceConditionSlab.put(rs.getString("allowance_condition_id"), uF.showData(rs.getString("condition_slab"),""));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from allowance_payment_logic where salary_head_id=? and level_id=? and org_id=?");
				pst.setInt(1, uF.parseToInt(getF_salaryhead()));
				pst.setInt(2, uF.parseToInt(getF_level()));
				pst.setInt(3, uF.parseToInt(getF_org()));
				rs = pst.executeQuery();
				List<Map<String, String>> alLogic = new ArrayList<Map<String,String>>();
				List<String> alConditionId = new ArrayList<String>();
				Map<String, List<Map<String, String>>> hmConditionLogic = new HashMap<String, List<Map<String, String>>>();
				while(rs.next()){
					Map<String,String> hmLogic = new HashMap<String, String>();
					hmLogic.put("PAYMENT_LOGIC_ID", rs.getString("payment_logic_id"));
					hmLogic.put("PAYMENT_LOGIC_SLAB", uF.showData(rs.getString("payment_logic_slab"),""));
					hmLogic.put("ALLOWANCE_CONDITION_ID", rs.getString("allowance_condition_id"));
					hmLogic.put("ALLOWANCE_CONDITION", uF.showData(hmAllowanceConditionSlab.get(rs.getString("allowance_condition_id")),""));
					
					hmLogic.put("ALLOWANCE_PAYMENT_LOGIC_ID", rs.getString("payment_logic"));
					hmLogic.put("ALLOWANCE_PAYMENT_LOGIC", uF.showData(hmAllowancePaymentLogic.get(rs.getString("payment_logic")),""));
	
					hmLogic.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
					hmLogic.put("SALARY_HEAD_NAME", uF.showData(hmSalaryHeadsMap.get(rs.getString("salary_head_id")),""));
					
					hmLogic.put("FIXED_AMOUNT", uF.showData(rs.getString("fixed_amount"),"0"));
					hmLogic.put("CAL_SALARY_HEAD_ID", uF.showData(rs.getString("cal_salary_head_id"),""));
					hmLogic.put("CAL_SALARY_HEAD_NAME", uF.showData(hmSalaryHeadsMap.get(rs.getString("cal_salary_head_id")),""));
					
					alLogic.add(hmLogic);
					
					List<Map<String, String>> alInner = (List<Map<String, String>>) hmConditionLogic.get(rs.getString("allowance_condition_id"));
					if(alInner == null) alInner = new ArrayList<Map<String,String>>();
					alInner.add(hmLogic);
					hmConditionLogic.put(rs.getString("allowance_condition_id"), alInner);
					
					if(!alConditionId.contains(rs.getString("allowance_condition_id"))){
						alConditionId.add(rs.getString("allowance_condition_id"));
					}
				}
				rs.close();
				pst.close();
				request.setAttribute("alLogic", alLogic);
				request.setAttribute("hmConditionLogic", hmConditionLogic);
		
				
				
				if(alConditionId.size() > 0){
					String strConditionIds = StringUtils.join(alConditionId.toArray(),",");
					pst = con.prepareStatement("select * from allowance_condition_details where salary_head_id=? and level_id=? and org_id=? and allowance_condition_id in ("+strConditionIds+")");
					pst.setInt(1, uF.parseToInt(getF_salaryhead()));
					pst.setInt(2, uF.parseToInt(getF_level()));
					pst.setInt(3, uF.parseToInt(getF_org()));
	//				System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					List<Map<String, String>> alCondition = new ArrayList<Map<String,String>>();
					while(rs.next()){
						Map<String,String> hmCondition = new HashMap<String, String>();
						hmCondition.put("ALLOWANCE_CONDITION_ID", rs.getString("allowance_condition_id"));
						hmCondition.put("ALLOWANCE_CONDITION_SLAB", uF.showData(rs.getString("condition_slab"),""));
						hmCondition.put("ALLOWANCE_CONDITION_TYPE", rs.getString("allowance_condition"));
						hmCondition.put("ALLOWANCE_CONDITION", uF.showData(hmAllowanceCondition.get(rs.getString("allowance_condition")),""));
						hmCondition.put("MIN_CONDITION", uF.showData(rs.getString("min_condition"),"0"));
						hmCondition.put("MAX_CONDITION", uF.showData(rs.getString("max_condition"),"0"));
						hmCondition.put("CUSTOM_FACTOR_TYPE", uF.showData(rs.getString("custom_type"),"A"));
						hmCondition.put("CUSTOM_AMT_PERCENTAGE", uF.showData(rs.getString("custom_amt_percentage"),"0"));
						
						alCondition.add(hmCondition);
					}
					rs.close(); 
					pst.close();
					request.setAttribute("alCondition", alCondition);
					
										
			}
			
			
			}*/
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	
	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String getF_level() {
		return f_level;
	}

	public void setF_level(String f_level) {
		this.f_level = f_level;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}

	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public String getF_salaryhead() {
		return f_salaryhead;
	}

	public void setF_salaryhead(String f_salaryhead) {
		this.f_salaryhead = f_salaryhead;
	}

	public List<FillSalaryHeads> getSalaryHeadList() {
		return salaryHeadList;
	}

	public void setSalaryHeadList(List<FillSalaryHeads> salaryHeadList) {
		this.salaryHeadList = salaryHeadList;
	}

}
