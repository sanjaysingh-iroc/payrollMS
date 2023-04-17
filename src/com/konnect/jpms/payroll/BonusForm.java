package com.konnect.jpms.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
 
public class BonusForm extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	CommonFunctions CF = null; 
	 
	private String[] f_wLocation; 
	private String[] f_level;
	private String[] f_department;
	private String[] f_service;
	private String profileEmpId;
	private String paycycle;
	private String f_org;
	
	private String strLocation;
	private String strDepartment;
	private String strService;
	private String strLevel;
	
	private List<FillPayCycles> paycycleList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	private List<FillWLocation> wLocationList;
	private List<FillOrganisation> organisationList;
	
	private static Logger log = Logger.getLogger(BonusForm.class);
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		/*request.setAttribute(PAGE, "/jsp/payroll/BonusForm.jsp");
		request.setAttribute(TITLE, "Bonus");*/
		
		UtilityFunctions uF = new UtilityFunctions();
		
		strEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(USERTYPE);
		
		/*boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}*/
		
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
//		System.out.println("location====>"+getStrLocation());
		
		//System.out.println("Locationsize=====>"+wLocationList.size());
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_wLocation(getStrLocation().split(","));
		} else {
			setF_wLocation(null);
		}
		if(getStrDepartment() != null && !getStrDepartment().equals("")) {
			setF_department(getStrDepartment().split(","));
		} else {
			setF_department(null);
		}
		if(getStrService() != null && !getStrService().equals("")) {
			setF_service(getStrService().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		

		viewBonus(uF);
	
		return loadBonus(uF);
	}

	public String loadBonus(UtilityFunctions uF){
		
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	public String viewBonus(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		

		try {
			
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			String []strPayCycleDates;
			if (getPaycycle() != null && !getPaycycle().equals("") && !getPaycycle().equalsIgnoreCase("null")) {
				strPayCycleDates = getPaycycle().split("-");
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, getF_org());
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
			}
			
			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con);		
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);			
			Map<String, String> hmEmpWlocationMap = CF.getEmpWlocationMap(con);
			Map<String, String> hmEmpJoiningMap = CF.getEmpJoiningDateMap(con, uF);			
			
			String strFinancialYearEnd = null;
			String strFinancialYearStart = null;
			String []strFinancialYear = CF.getFinancialYear(con, strPayCycleDates[1], CF, uF);
			if(strFinancialYear!=null){
				strFinancialYearStart = strFinancialYear[0];
				strFinancialYearEnd = strFinancialYear[1];
			}
			
			String strPrevFinancialYearEnd = null;
			String strPrevFinancialYearStart = null;
			String []strPrevFinancialYear = CF.getPrevFinancialYear(con, strPayCycleDates[1], CF, uF);
			if(strPrevFinancialYear!=null) {
				strPrevFinancialYearStart = strPrevFinancialYear[0];
				strPrevFinancialYearEnd = strPrevFinancialYear[1];
			}
			
			int nPayMonth = uF.parseToInt(uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT, "MM"));
			
			pst = con.prepareStatement("select emp_id from payroll_generation where paid_from = ? and paid_to = ? and paycycle = ? group by emp_id order by emp_id ");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
			rs = pst.executeQuery();
			List<String> ckEmpPayList = new ArrayList<String>();
			while(rs.next()){
				ckEmpPayList.add(rs.getString("emp_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("ckEmpPayList", ckEmpPayList);
			
			pst = con.prepareStatement("select salary_head_id, amount, esd.emp_id from emp_salary_details esd, (select max(entry_date) as max_date, emp_id from emp_salary_details group by emp_id ) as b where esd.entry_date = b.max_date and b.emp_id = esd.emp_id and isdisplay = true and salary_head_id not in ("+GROSS+","+CTC+") order by esd.emp_id, salary_head_id ");
			rs = pst.executeQuery();
			
			Map hmSalaryList = new HashMap();
			List alSalaryList = new ArrayList();
			
			String strEmpIdOld = null;
			String strEmpIdNew = null;
			while(rs.next()){
				strEmpIdNew = rs.getString("emp_id");
				if(strEmpIdNew!=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
					alSalaryList = new ArrayList();
				}
				
				alSalaryList.add(rs.getString("salary_head_id"));
				hmSalaryList.put(strEmpIdNew, alSalaryList);
				
				strEmpIdOld = strEmpIdNew;
			}
			rs.close();
			pst.close();
			

			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true and joining_date<= ? ");
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }else if(getStrLevel()!=null && !getStrLevel().equals("") && !getStrLevel().equalsIgnoreCase("null")) {
            	 sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd, level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+getStrLevel()+") ) ");
            }
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }else if(getStrDepartment()!=null && !getStrDepartment().equals("") && !getStrDepartment().equalsIgnoreCase("null")) {
            	 sbQuery.append(" and depart_id in ("+getStrDepartment()+") ");
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
            
            if(getF_wLocation()!=null && getF_wLocation().length>0){
                sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wLocation(), ",")+") ");
            }else if(getStrLocation()!= null && !getStrLocation().equals("") && !getStrLocation().equalsIgnoreCase("null")) {
            	 sbQuery.append(" and wlocation_id in ("+getStrLocation()+") ");
            }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            
            if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
            sbQuery.append(" and eod.emp_id in ( select esd.emp_id from emp_salary_details esd, (select max(effective_date) as max_date, emp_id " +
            		" from emp_salary_details where isdisplay = true and is_approved=true group by emp_id ) as b where esd.effective_date = b.max_date " +
            		"and b.emp_id = esd.emp_id and isdisplay = true and is_approved=true and esd.salary_head_id=? and esd.effective_date <=?)");
			sbQuery.append(" order by emp_fname, emp_lname");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(2, BONUS);
			pst.setDate(3,  uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			
			List<List<String>> alEmpReport = new ArrayList<List<String>>();
			while(rs.next()){
				
				List<String> alEmpReportInner = new ArrayList<String>();
				alEmpReportInner.add(rs.getString("emp_per_id"));
				
				String strEmpMName = "";
				
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				alEmpReportInner.add(uF.showData(rs.getString("emp_fname"), "")+strEmpMName+" "+uF.showData(rs.getString("emp_lname"), ""));
				alEmpReport.add(alEmpReportInner);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from bonus_individual_details where paid_from = ? and paid_to=? and pay_paycycle = ?");
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
			rs = pst.executeQuery();
			System.out.println("BF/298---pst="+pst);

			Map<String,String> hmBonus = new HashMap<String, String>();
			Map<String,String> hmBonusId = new HashMap<String, String>();
			Map<String,String> hmBonusValue = new HashMap<String, String>();
			Map<String,String> hmBonusPercent = new HashMap<String, String>();
			Map<String,String> hmBonusSalId= new HashMap<String, String>();
			while(rs.next()) {
				hmBonus.put(rs.getString("emp_id"), rs.getString("is_approved"));
				hmBonusId.put(rs.getString("emp_id"), rs.getString("bonus_id"));
				hmBonusValue.put(rs.getString("emp_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(rs.getString("pay_amount"))));
				hmBonusPercent.put(rs.getString("emp_id"), rs.getDouble("percent")>0.0d ? rs.getString("percent") : "");
				hmBonusSalId.put(rs.getString("emp_id"), rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con,strPayCycleDates[0],strPayCycleDates[1], CF, uF,hmWeekEndHalfDates,null);
			
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF,strPayCycleDates[0],strPayCycleDates[1],alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEnds,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
			Map<String, List<String>> hmLevelwiseBonusDetails = new HashMap<String, List<String>>();
			pst = con.prepareStatement("SELECT * FROM bonus_details where date_from =? and date_to=? order by bonus_id desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("BF/326--pst =====> " + pst);
			rs = pst.executeQuery();
			int limitSalaryHeadId = 0;
			double limitSalaryAmt = 0.0d;
	//===start parvez date: 25-07-2022===		
//			if(rs.next()) {
			while(rs.next()) {
	//===end parvez date: 25-07-2022===			
				List<String> alBonusDetails = new ArrayList<String>();
				alBonusDetails.add(rs.getString("limit_salary_head_id"));  //0
				alBonusDetails.add(rs.getString("limit_amount"));  //1
				alBonusDetails.add(rs.getString("bonus_slab_type"));  //2
				alBonusDetails.add(rs.getString("bonus_type"));  //3
				alBonusDetails.add(rs.getString("salary_head_id"));  //4
				alBonusDetails.add(rs.getString("bonus_amount"));  //5 amount or percent
				alBonusDetails.add(rs.getString("salary_calculation"));  //6 '1':'Current Month','2':'Cumulative','3':'Previous Year'
				hmLevelwiseBonusDetails.put(rs.getString("level_id"), alBonusDetails);
//				System.out.println("BF/340--hmLevelwiseBonusDetails="+hmLevelwiseBonusDetails);
			}
			rs.close();
			pst.close();
				
		//===start parvez date: 28-07-2022===	 	
			Map<String, List<String>> hmPaidBonusDetails = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select * from bonus_individual_details where paid_from >= ? and paid_to<=? and is_approved = ?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(3, 1);
			rs = pst.executeQuery();
			while(rs.next()) {
				ArrayList<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("amount"));		//0
				innerList.add(rs.getString("paid_from"));	//1
				innerList.add(rs.getString("paid_to"));		//2
				hmPaidBonusDetails.put(rs.getString("emp_id"), innerList);
			}
			rs.close();
			pst.close();
	//===end parvez date: 28-07-2022=== 
			
			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpYearlyPaidSalary = CF.getYearlyPaidSalaryEarningAndDeduction(con, strPrevFinancialYearStart, strPrevFinancialYearEnd);
//			System.out.println("BF/346--hmEmpYearlyPaidSalary="+hmEmpYearlyPaidSalary);
			
			Map<String,String> hmEmpCalBonus = new HashMap<String, String>();
			for(int i=0;alEmpReport!=null && i<alEmpReport.size();i++) {
				List<String> alEmp = (List<String>) alEmpReport.get(i);
				boolean boolSalCalStatus = CF.getEmpDisableSalaryCalculation(con, uF, alEmp.get(0));
				int level_id =uF.parseToInt(hmEmpLevelId.get(alEmp.get(0)));
				
			//===start parvez date: 29-07-2022===	
				List<String> alPaidBonusDetails = hmPaidBonusDetails.get(alEmp.get(0));
			//===end parvez date: 29-07-2022===	
				
				List<String> alBonusDetails = hmLevelwiseBonusDetails.get(level_id+"");
				if(alBonusDetails!=null && alBonusDetails.size()>0) {
					limitSalaryHeadId = uF.parseToInt(alBonusDetails.get(0));
					limitSalaryAmt = uF.parseToDouble(alBonusDetails.get(1));
				}
				pst = con.prepareStatement("select distinct(esd.salary_head_id),sd.salary_head_name,esd.amount,esd.earning_deduction,effective_date from " +
						"emp_salary_details esd, salary_details sd where effective_date=(SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and level_id ="+level_id+")" +
						" and (sd.level_id = -1 OR sd.level_id = "+level_id+") and sd.salary_head_id = esd.salary_head_id and emp_id = ? order by earning_deduction desc, esd.salary_head_id");
				pst.setInt(1, uF.parseToInt(alEmp.get(0)));
				pst.setInt(2, uF.parseToInt(alEmp.get(0)));
				rs = pst.executeQuery();
//				System.out.println("pst =====> " + pst);
				Set<String> alSHeadList = new HashSet<String>();
				Map<String, String> hmSalaryAmt = new HashMap<String, String>();
				while(rs.next()) {
					alSHeadList.add(rs.getString("salary_head_id"));
					hmSalaryAmt.put(rs.getString("salary_head_id"), rs.getString("amount"));
					
				}
				rs.close();
				pst.close();
				
//				String strLevel = hmEmpLevelMap.get(alEmp.get(0));
				String strLevel = ""+level_id;
				String strLocation = hmEmpWlocationMap.get(alEmp.get(0));
				
				Map<String, String> hmEmpOrgMap = CF.getEmpOrgDetails(con,uF,alEmp.get(0));
				if(hmEmpOrgMap == null) hmEmpOrgMap = new HashMap<String, String>();
				

				String salary_cal_basis = uF.showData(hmEmpOrgMap.get("ORG_SALARY_CAL_BASIS"), "");
				
				String strWLocationId = hmEmpWlocation.get(alEmp.get(0));
				Set<String> weeklyOffEndDate = hmWeekEnds.get(strWLocationId);
				if(weeklyOffEndDate==null) weeklyOffEndDate = new HashSet<String>();
				
				Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(alEmp.get(0));
				if(rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();
				
				
				Map<String, String> hmHolidaysCnt = new HashMap<String, String>();
				Map<String, String> hmHolidayDates = new HashMap<String, String>();
				if(alEmpCheckRosterWeektype!=null && alEmpCheckRosterWeektype.contains(alEmp.get(0))){
					CF.getHolidayListCount(con,request,strPayCycleDates[0], strPayCycleDates[1], CF, hmHolidayDates, hmHolidaysCnt, rosterWeeklyOffSet, false);
				} else {
					CF.getHolidayListCount(con,request,strPayCycleDates[0], strPayCycleDates[1], CF, hmHolidayDates, hmHolidaysCnt, weeklyOffEndDate, false);
				}
				String diffInDays = uF.dateDifference(strPayCycleDates[0], DATE_FORMAT, strPayCycleDates[1], DATE_FORMAT, CF.getStrTimeZone());
				
				
				double nWorkDays = 0.0d;
				if(salary_cal_basis!=null && "AWD".equalsIgnoreCase(salary_cal_basis)){
					int nWeekEnd = (alEmpCheckRosterWeektype!=null && alEmpCheckRosterWeektype.contains(alEmp.get(0))) ? rosterWeeklyOffSet.size() : weeklyOffEndDate.size();
					int nHolidayCnt = uF.parseToInt(hmHolidaysCnt.get(strLocation));
					nWorkDays = (uF.parseToDouble(diffInDays) - nWeekEnd) - nHolidayCnt;
				}else if(salary_cal_basis!=null && "AMD".equalsIgnoreCase(salary_cal_basis)){
					nWorkDays = uF.parseToDouble(diffInDays);
				}else{
					nWorkDays = 26.0d;
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
				
				Map<String, Map<String, String>> hmSalInner = hmSalaryDetails1.get(strLevel);
				if(hmSalInner == null) hmSalInner = new HashMap<String, Map<String, String>>(); 
				
				Map<String, Map<String, String>> hmInnerisDisplay = new HashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> hmEmpSalaryDetails = CF.getSalaryCalculation(con,hmInnerisDisplay,uF.parseToInt(alEmp.get(0)), nWorkDays, 0, 0, ((int)nWorkDays), 0, 0, strLevel, uF, CF, strPayCycleDates[1], hmSalInner, null, boolSalCalStatus+"");
				if(hmEmpSalaryDetails == null) hmEmpSalaryDetails = new HashMap<String, Map<String, String>>();
				
				double dblBonusAmount = CF.getBonusCalculation(con, CF, uF, alEmp.get(0),strPayCycleDates[1], hmEmpLevelMap, hmEmpSalaryDetails, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmEmpJoiningMap);
				
				String strBonusAmt = "";
				if(alBonusDetails!=null && uF.parseToInt(alBonusDetails.get(2))==2) {
					
//					alBonusDetails.add(rs.getString("bonus_type"));  //3
//					alBonusDetails.add(rs.getString("salary_head_id"));  //4
//					alBonusDetails.add(rs.getString("bonus_amount"));  //5 amount or percent
//					alBonusDetails.add(rs.getString("salary_calculation"));  //6 '1':'Current Month','2':'Cumulative','3':'Previous Year'
					if(alBonusDetails.get(3).equals("%")) {
						
						if(uF.parseToInt(alBonusDetails.get(6))==3 && uF.parseToInt(alBonusDetails.get(4))==NET) {
							String paidSalE = hmEmpYearlyPaidSalary.get(alEmp.get(0)+"_E");
							String paidSalD = hmEmpYearlyPaidSalary.get(alEmp.get(0)+"_D");
							
					//===start parvez date: 29-07-2022===		
//							double dblBonusAmt = (uF.parseToDouble(paidSalE)-uF.parseToDouble(paidSalD)) * uF.parseToDouble(alBonusDetails.get(5))/100;
							double dblBonusAmt = 0;
							
							Date paidFromDate = alPaidBonusDetails!=null && alPaidBonusDetails.size()>0? uF.getDateFormatUtil(alPaidBonusDetails.get(1), DBDATE) : null;
							Date paidToDate = alPaidBonusDetails!=null && alPaidBonusDetails.size()>0? uF.getDateFormatUtil(alPaidBonusDetails.get(2), DBDATE) : null;
							
							if(alPaidBonusDetails == null || (alPaidBonusDetails!=null && uF.parseToDouble(alPaidBonusDetails.get(0))==0)
									|| (paidFromDate!=null && paidFromDate.equals(uF.getDateFormatUtil(strPayCycleDates[0], DATE_FORMAT)) && paidToDate!=null && paidToDate.equals(uF.getDateFormatUtil(strPayCycleDates[1], DATE_FORMAT)))){
								dblBonusAmt = (uF.parseToDouble(paidSalE)-uF.parseToDouble(paidSalD)) * uF.parseToDouble(alBonusDetails.get(5))/100;
							}
					//===end parvez date: 29-07-2022===		
							
							strBonusAmt = uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblBonusAmt);
							
						} else {
							
						}
					} else {
						strBonusAmt = alBonusDetails.get(5);
					}
				} else {
					if(alSHeadList.contains(""+limitSalaryHeadId)) {
						if(limitSalaryAmt >= uF.parseToDouble(hmSalaryAmt.get(""+limitSalaryHeadId))) {
							strBonusAmt = uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblBonusAmount);
						}
					} else if(dblBonusAmount>0) {
						strBonusAmt = uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), dblBonusAmount);
					}
				}
				hmEmpCalBonus.put(alEmp.get(0), strBonusAmt);
			}
			
			request.setAttribute("alEmpReport", alEmpReport);
			request.setAttribute("hmSalaryList", hmSalaryList);
			request.setAttribute("hmSalaryHeadsMap", hmSalaryHeadsMap);
			
			request.setAttribute("hmBonus", hmBonus);
			request.setAttribute("hmBonusId", hmBonusId);
			request.setAttribute("hmBonusValue", hmBonusValue);
			request.setAttribute("hmEmpCalBonus", hmEmpCalBonus);
			
			request.setAttribute("hmBonusSalId", hmBonusSalId);
			request.setAttribute("hmBonusPercent", hmBonusPercent);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
			alFilter.add("ORGANISATION");
			if(getF_org()!=null) {
				String strOrg="";
				for(int i=0;organisationList!=null && i<organisationList.size();i++) {
					if(getF_org().equals(organisationList.get(i).getOrgId())) {
						strOrg=organisationList.get(i).getOrgName();
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
			
			alFilter.add("PAYCYCLE");	
			if(getPaycycle()!=null){
				String strPayCycle="";
				int k=0;
				for(int i=0;paycycleList!=null && i<paycycleList.size();i++){
					if(getPaycycle().equals(paycycleList.get(i).getPaycycleId())){
						if(k==0){
							strPayCycle=paycycleList.get(i).getPaycycleName();
						}else{
							strPayCycle+=", "+paycycleList.get(i).getPaycycleName();
						}
						k++;
					}
				}
				if(strPayCycle!=null && !strPayCycle.equals("")){
					hmFilter.put("PAYCYCLE", strPayCycle);
				}else{
					hmFilter.put("PAYCYCLE", "All Paycycle");
				}
			}
//			System.out.println("location====>"+getF_wLocation());
			alFilter.add("LOCATION");
			if(getF_wLocation()!=null) {
				String strLocation="";
				int k=0;
				for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {

					for(int j=0;j<getF_wLocation().length;j++) {
						if(getF_wLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
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
		
			alFilter.add("LEVEL");
			if(getF_level()!=null){
				String strLevel="";
				int k=0;
				for(int i=0;levelList!=null && i<levelList.size();i++){
					for(int j=0;j<getF_level().length;j++){
						if(getF_level()[j].equals(levelList.get(i).getLevelId())){
							if(k==0){
								strLevel=levelList.get(i).getLevelCodeName();
							}else{
								strLevel+=", "+levelList.get(i).getLevelCodeName();
							}
							k++;
						}
					}
				}
				if(strLevel!=null && !strLevel.equals("")){
					hmFilter.put("LEVEL", strLevel);
				}else{
					hmFilter.put("LEVEL", "All Levels");
				}
			}else{
				hmFilter.put("LEVEL", "All Levels");
			}
			
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
    }

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

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

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String[] getF_wLocation() {
		return f_wLocation;
	}

	public void setF_wLocation(String[] f_wLocation) {
		this.f_wLocation = f_wLocation;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
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

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
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

	public String getStrService() {
		return strService;
	}

	public void setStrService(String strService) {
		this.strService = strService;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}
	
	

}
