package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LeaveEncashmentReport extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF = null;
	String strUserType = null;
	String strUserTypeId = null; 
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null;
	
	String strStartDate;
	String strEndDate;
	
	String strMonth;
	String strYear;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String calendarYear;
	
	String f_org;
	String[] f_wLocation; 
	String[] f_level;
	String[] f_department;
	String[] f_service;
	
	List<FillMonth> monthList;
	List<FillYears> yearList;
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillCalendarYears> calendarYearList;
	
	
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
			  
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PLeaveBlanceReport);
		request.setAttribute(TITLE, "Leave Balance Report");
		UtilityFunctions uF = new UtilityFunctions();
		
		if(getF_org()==null || getF_org().trim().equals("")){
			setF_org((String)session.getAttribute(ORGID));
		}
		
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
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
		if(getStrStartDate()!=null && getStrStartDate().equalsIgnoreCase("NULL")){
			setStrStartDate(null);
			setStrEndDate(null);
		}
		
		if(getStrEndDate()!=null && getStrEndDate().equalsIgnoreCase("NULL")){
			setStrStartDate(null);
			setStrEndDate(null);
		}
		
		viewLeaveEncashmentReport(uF);
		
		return loadLeaveEncashmentReport(uF);
	}
	
	private void viewLeaveEncashmentReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;
			String strCurrentMonthDate = null;
	
			if (getCalendarYear() != null) {
				strCalendarYearDates = getCalendarYear().split("-");
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			} else {
				strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
				setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			}
			
			String[] cMonthMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE).split("::::");
			if(uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "yyyy")) == uF.parseToInt(uF.getDateFormat(cMonthMinMaxDate[0], DBDATE, "yyyy"))){
				strCurrentMonthDate = uF.getDateFormat(cMonthMinMaxDate[1], DBDATE, DATE_FORMAT);
				strCalendarYearEnd = strCurrentMonthDate;
			}
			
			System.out.println("strCurrentMonth="+strCurrentMonthDate);
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			if(hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String, String>();
//			Map<String, String> hmDepartment =CF.getDepartmentMap(con, null, null);
//			if(hmDepartment == null) hmDepartment = new HashMap<String, String>();
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			if(hmEmployeeNameMap == null) hmEmployeeNameMap = new HashMap<String, String>();
			
			String[] strFinancialYear = CF.getFinancialYear(strCalendarYearEnd, CF, uF);
			
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, strCalendarYearStart, strCalendarYearEnd, CF, uF,hmWeekEndHalfDates,null);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, strCalendarYearStart, strCalendarYearEnd,alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEnds,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			Map<String,Set<String>> holidaysMp=CF.getHolidayList(con,request, uF,strCalendarYearStart, strCalendarYearEnd);
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			Map<String,List<String>> hmLevelwiseLeaveId = new HashMap<String, List<String>>();
			Map<String,String> hmLeaveTypeMap = new HashMap<String, String>();
			Map<String, String> hmLeaveDetails = new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_leave_type elt,leave_type lt where lt.leave_type_id = elt.leave_type_id and lt.is_leave_encashment=true ");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and elt.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and elt.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_wLocation()!=null && getF_wLocation().length>0) {
	            sbQuery.append(" and elt.wlocation_id in ("+StringUtils.join(getF_wLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and elt.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst="+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				List<String> inner = hmLevelwiseLeaveId.get(rs.getString("level_id")+"_"+rs.getString("wlocation_id"));
				if(inner == null){
					inner = new ArrayList<String>();
				}
				inner.add(rs.getString("leave_type_id"));
				hmLevelwiseLeaveId.put(rs.getString("level_id")+"_"+rs.getString("wlocation_id"), inner);
				hmLeaveTypeMap.put(rs.getString("leave_type_id"), rs.getString("leave_type_name"));
				hmLeaveDetails.put(rs.getString("level_id")+"_"+rs.getString("wlocation_id")+"_"+rs.getString("leave_type_id")+"_BALANCE", rs.getString("max_leave_encash"));
				hmLeaveDetails.put(rs.getString("level_id")+"_"+rs.getString("wlocation_id")+"_"+rs.getString("leave_type_id")+"_SALARY_HEAD", rs.getString("salary_head_id"));
				hmLeaveDetails.put(rs.getString("level_id")+"_"+rs.getString("wlocation_id")+"_"+rs.getString("leave_type_id")+"_PERCENTAGE", rs.getString("percentage"));
				int nTotalNumberOfDays = 0;
				if(rs.getString("effective_date_type").equals("CY")){
					if(strCurrentMonthDate != null){
						nTotalNumberOfDays = uF.parseToInt(uF.dateDifference(strCalendarYearStart, DATE_FORMAT, strCurrentMonthDate, DATE_FORMAT));
					} else{
						nTotalNumberOfDays = uF.parseToInt(uF.dateDifference(strCalendarYearStart, DATE_FORMAT, strCalendarYearEnd, DATE_FORMAT));
					}
					
				} else if(rs.getString("effective_date_type").equals("FY")){
					if(strCurrentMonthDate != null){
						nTotalNumberOfDays = uF.parseToInt(uF.dateDifference(strFinancialYear[0], DATE_FORMAT, strCurrentMonthDate, DATE_FORMAT));
					} else{
						nTotalNumberOfDays = uF.parseToInt(uF.dateDifference(strFinancialYear[0], DATE_FORMAT, strFinancialYear[1], DATE_FORMAT));
					}
				}
				hmLeaveDetails.put(rs.getString("level_id")+"_"+rs.getString("wlocation_id")+"_"+rs.getString("leave_type_id")+"_DAYS", nTotalNumberOfDays+"");
				
				
			}
			rs.close();
			pst.close();
			
			Map<String,String> hmPaidBalnce = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_leave_encashment where leave_encash_id>0 and entry_date between ? and ? ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				double balance = uF.parseToDouble(hmPaidBalnce.get(rs.getString("emp_id")+"_"+rs.getString("leave_type_id")))+uF.parseToDouble(rs.getString("no_days"));
				hmPaidBalnce.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), balance+"");
			}
			rs.close();
			pst.close();
			
//			System.out.println("LER/199--hmPaidBalnce="+hmPaidBalnce);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_per_id,empcode,depart_id,grade_id,wlocation_id from employee_personal_details epd, employee_official_details eod WHERE epd.is_alive=true and epd.emp_per_id=eod.emp_id and approved_flag=true ");
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and (supervisor_emp_id = "+uF.parseToInt((String)session.getAttribute(EMPID))+" or eod.emp_id = "+uF.parseToInt((String)session.getAttribute(EMPID))+")");
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_wLocation()!=null && getF_wLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
			sbQuery.append(" order by emp_fname ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst="+pst);
			rs = pst.executeQuery();
			
			while(rs.next()){
				
				String empLevelId = hmEmpLevelMap.get(rs.getString("emp_per_id"));
				List<String> alLeaves = hmLevelwiseLeaveId.get(empLevelId+"_"+rs.getString("wlocation_id"));
				for(int i=0; alLeaves!=null && i<alLeaves.size(); i++){
					
					List<String> innerList = new ArrayList<String>();
					innerList.add(hmEmployeeNameMap.get(rs.getString("emp_per_id")));	//0
					innerList.add(hmLeaveTypeMap.get(alLeaves.get(i)));					//1
					double encashmentBalance = uF.parseToDouble(hmLeaveDetails.get(empLevelId+"_"+rs.getString("wlocation_id")+"_"+alLeaves.get(i)+"_BALANCE"));
//					double paidEnchamentBalance = uF.parseToDouble(hmPaidBalnce.get(rs.getString("emp_per_id")+"_"+alLeaves.get(i)));
//					double totalEncashmentBalance = encashmentBalance - paidEnchamentBalance;
//					innerList.add(uF.formatIntoTwoDecimalWithOutComma(totalEncashmentBalance));
					innerList.add(uF.formatIntoTwoDecimalWithOutComma(encashmentBalance));	//2
					
					String strSalaryHeads = hmLeaveDetails.get(empLevelId+"_"+rs.getString("wlocation_id")+"_"+alLeaves.get(i)+"_SALARY_HEAD");
					
					if(strSalaryHeads!=null){
						
						List<String> alsalaryHeads = Arrays.asList(strSalaryHeads.split(","));
						if(alsalaryHeads == null) alsalaryHeads = new ArrayList<String>();
						
						double dblPercentage = uF.parseToDouble(hmLeaveDetails.get(empLevelId+"_"+rs.getString("wlocation_id")+"_"+alLeaves.get(i)+"_PERCENTAGE"));
						if(dblPercentage == 0.0d) {
							continue;
						}
						
						Set<String> weeklyOffSet = null;
						if(alEmpCheckRosterWeektype.contains(rs.getString("emp_per_id"))) {
							weeklyOffSet = hmRosterWeekEndDates.get(rs.getString("emp_per_id"));
						} else {
							weeklyOffSet = hmWeekEnds.get(rs.getString("wlocation_id"));
						}
						if(weeklyOffSet==null) weeklyOffSet=new HashSet<String>();
						
						Set<String> OriginalholidaysSet=holidaysMp.get(rs.getString("wlocation_id"));	
						if(OriginalholidaysSet==null)OriginalholidaysSet=new HashSet<String>();
						Set<String> holidaysSet=new HashSet<String>(OriginalholidaysSet);	
						
						holidaysSet.removeAll(weeklyOffSet);
						
						int nWeekEnds = weeklyOffSet.size();
						
						int nHolidays = holidaysSet.size();
						
						int nTotalNumberOfDaysForCalc = (uF.parseToInt(hmLeaveDetails.get(empLevelId+"_"+rs.getString("wlocation_id")+"_"+alLeaves.get(i)+"_DAYS"))-nWeekEnds) - nHolidays;
						/*if(uF.parseToInt(rs.getString("emp_per_id")) == 998){
							System.out.println("LER/329---nWeekEnds="+nWeekEnds+"---nHolidays="+nHolidays);
							System.out.println("nTotalNumberOfDaysForCalc="+nTotalNumberOfDaysForCalc);
						}*/
						
						Map<String, Map<String,String>> hmInnerActualCTC = getSalaryCalculation(con, uF.parseToInt(rs.getString("emp_per_id")), encashmentBalance, 0, 0, nTotalNumberOfDaysForCalc, empLevelId, uF, CF, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, DBDATE));
						
						double salaryGross=0;
						double salaryDeduction=0;
						Iterator<String> it = hmInnerActualCTC.keySet().iterator();
						while(it.hasNext()) {
							String strSalaryId = it.next();
							
							Map<String,String> hm = hmInnerActualCTC.get(strSalaryId);
							if(hm.get("EARNING_DEDUCTION").equals("E") && alsalaryHeads.contains(strSalaryId)) {
								salaryGross +=uF.parseToDouble(hm.get("AMOUNT"));
							}else if(hm.get("EARNING_DEDUCTION").equals("D") && alsalaryHeads.contains(strSalaryId)) {
								salaryDeduction +=uF.parseToDouble(hm.get("AMOUNT"));
							}
							
						}
						
						double dblEncashAmount = ((salaryGross - salaryDeduction) * dblPercentage)/100;
						
						innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblEncashAmount));
					} else{
						innerList.add("0.0");
					}
					
					reportList.add(innerList);
				}
				
			}
			rs.close();
			pst.close();
//			System.out.println("reportList="+reportList);
			request.setAttribute("reportList", reportList);
			
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public Map<String, Map<String, String>> getSalaryCalculation(Connection con, int nEmpId, double dblPresent, double dblAbsent, double dblBreaks,
			int nTotalNumberOfDays, String strLevelId, UtilityFunctions uF, CommonFunctions CF, String strD2) {
		// System.out.println("strD2 =====> "+strD2);
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, Map<String, String>> hmSalaryHeadReCalculatedMap = new LinkedHashMap<String, Map<String, String>>();
		Map<String, Map<String, String>> hmSalaryHeadMap = new LinkedHashMap<String, Map<String, String>>();
		try {
			Map<String, String> hmSalaryEarningDeduction = new HashMap<String, String>();

			// pst =
			// con.prepareStatement("select * from salary_details where level_id = 0 or level_id = -1 or level_id = ? order by salary_head_id, salary_id");
			pst = con.prepareStatement("select * from salary_details where level_id = ? order by salary_head_id, salary_id");
			pst.setInt(1, uF.parseToInt(strLevelId));
			rs = pst.executeQuery();

//			System.out.println("CF/6850--pst===sal==>"+pst);
			Map<String, Map<String, String>> hmSalaryDetails = new HashMap<String, Map<String, String>>();

			while (rs.next()) {

				Map<String, String> hmInnerSal = new HashMap<String, String>();

				hmInnerSal.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
				hmInnerSal.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
				hmInnerSal.put("SALARY_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
				hmInnerSal.put("SUB_SALARY_HEAD_ID", rs.getString("sub_salary_head_id"));
				hmInnerSal.put("SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));

				hmSalaryDetails.put(rs.getString("salary_head_id"), hmInnerSal);

				hmSalaryEarningDeduction.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));

			}
			rs.close();
			pst.close();
			
			pst = con
					.prepareStatement("select * from (	select *, 0 as aa from emp_salary_details where emp_id = ?	and effective_date = (select max(effective_date) from emp_salary_details where emp_id =? and effective_date <= ? and is_approved=true)  and salary_head_id in ("
							+ DA1
							+ ", "
							+ GROSS
							+ " ) order by earning_deduction desc, salary_head_id, emp_salary_id ) ac1 union  select * from (select *, 1 as aa from emp_salary_details where emp_id = ? and effective_date = (select max(effective_date) from emp_salary_details where emp_id =? and effective_date <= ? and is_approved=true)  and salary_head_id not in ("
							+ TDS
							+ ","
							+ DA1
							+ ", "
							+ GROSS
							+ ") order by earning_deduction desc, salary_head_id, emp_salary_id ) ac union (  select *, 2 as aa from emp_salary_details where emp_id = ? and effective_date = (select max(effective_date)  from emp_salary_details where emp_id =? and effective_date <= ? and is_approved=true) and salary_head_id in ("
							+ TDS
							+ ") order by earning_deduction desc, salary_head_id, emp_salary_id	) order by aa, earning_deduction desc, salary_head_id, emp_salary_id");
			pst.setInt(1, nEmpId);
			pst.setInt(2, nEmpId);
			pst.setDate(3, uF.getDateFormat(strD2, DBDATE));
			pst.setInt(4, nEmpId);
			pst.setInt(5, nEmpId);
			pst.setDate(6, uF.getDateFormat(strD2, DBDATE));
			pst.setInt(7, nEmpId);
			pst.setInt(8, nEmpId);
			pst.setDate(9, uF.getDateFormat(strD2, DBDATE));
//			System.out.println("LER/420---pst="+pst);
			rs = pst.executeQuery();
			double dblBasicDA = 0;
			while (rs.next()) {
				Map<String, String> hmSalaryInner = new HashMap<String, String>();
				Map<String, String> hmInner = new HashMap<String, String>();
				double dblAmount = 0;

				if (!rs.getBoolean("isdisplay")) {
					continue;
				}

				String strSalaryHeadId = rs.getString("salary_head_id");
				String strEarningDeduction = hmSalaryEarningDeduction.get(rs.getString("salary_head_id"));

				String strAmount = rs.getString("amount");
				dblAmount = uF.parseToDouble(strAmount) * 12;
				
				if (uF.parseToInt(strSalaryHeadId) == BASIC || uF.parseToInt(strSalaryHeadId) == GROSS || uF.parseToInt(strSalaryHeadId) == DA
						|| uF.parseToInt(strSalaryHeadId) == DA1) {
					dblBasicDA += dblAmount;
				}

				Map<String, String> hmInnerSal = hmSalaryDetails.get(strSalaryHeadId);
				if (hmInnerSal == null)
					hmInnerSal = new HashMap<String, String>();

				String strSubSalAmount = hmInnerSal.get("SALARY_HEAD_AMOUNT");
				String strSubSalAmountType = hmInnerSal.get("SALARY_AMOUNT_TYPE");
				String strSubSalId = hmInnerSal.get("SUB_SALARY_HEAD_ID");

				// boolean isFixed = false;
				if (strSubSalAmountType != null && strSubSalAmountType.equalsIgnoreCase("P")) {

					Map<String, String> hmEmpSalaryInner = hmSalaryHeadMap.get(strSubSalId);

					if (hmEmpSalaryInner == null)
						hmEmpSalaryInner = new HashMap<String, String>();
					String strEmpSalAmount = hmEmpSalaryInner.get("AMOUNT");

					if ((uF.parseToInt(strSubSalId) == BASIC || uF.parseToInt(strSubSalId) == GROSS)
							&& (uF.parseToInt(strSalaryHeadId) != DA && uF.parseToInt(strSalaryHeadId) != DA1)) {
						strEmpSalAmount = dblBasicDA + "";
					}

					double dblSalAmount = uF.parseToDouble(strEmpSalAmount);
					double dblSubSalAmount = uF.parseToDouble(strSubSalAmount);

					double dblTotalAmount = dblSalAmount * dblSubSalAmount / 100;
					
					dblAmount = dblTotalAmount * (dblPresent / nTotalNumberOfDays);
					
					hmInner.put("AMOUNT", uF.formatIntoTwoDecimal(dblAmount));

					if (uF.parseToInt(strSalaryHeadId) == BREAKS) {
						dblAmount = dblTotalAmount * dblBreaks * nTotalNumberOfDays;
						hmInner.put("AMOUNT", uF.formatIntoTwoDecimal(dblAmount));
					}

				} else if (uF.parseToInt(strSalaryHeadId) != BASIC && uF.parseToInt(strSalaryHeadId) != GROSS && uF.parseToInt(strSalaryHeadId) != DA
						&& uF.parseToInt(strSalaryHeadId) != DA1 && !"D".equalsIgnoreCase(rs.getString("earning_deduction"))) {
					
					if (strSubSalAmountType != null && strSubSalAmountType.equalsIgnoreCase("P") && uF.parseToInt(strSubSalId) == BASIC) {
						dblAmount = dblBasicDA * (dblPresent / nTotalNumberOfDays);
					} else {
						dblAmount = dblAmount * (dblPresent / nTotalNumberOfDays);
					}

					hmInner.put("AMOUNT", uF.formatIntoTwoDecimal(dblAmount));
				} else {
					if(nEmpId == 998){
						System.out.println("LER/520--else-->"+strSalaryHeadId+"---Amount="+dblAmount);
						System.out.println("CF/521--else-->dblPresent="+dblPresent+"--nTotalNumberOfDays="+nTotalNumberOfDays);
					}

					if (strEarningDeduction != null && strEarningDeduction.equalsIgnoreCase("E")) {
						dblAmount = dblAmount * (dblPresent / nTotalNumberOfDays);
					}
					hmInner.put("AMOUNT", uF.formatIntoTwoDecimal(dblAmount));
				}
				
				hmInner.put("SALARY_HEAD_ID", strSalaryHeadId);
				hmInner.put("EARNING_DEDUCTION", strEarningDeduction);

				hmSalaryHeadMap.put(strSalaryHeadId, hmInner);
				
				if (strEarningDeduction != null) {
					hmSalaryInner.put("EARNING_DEDUCTION", strEarningDeduction);
					hmSalaryInner.put("AMOUNT", dblAmount + "");
					hmSalaryHeadReCalculatedMap.put(strSalaryHeadId, hmSalaryInner);
				}

			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

		return hmSalaryHeadReCalculatedMap;

	}
	
	public String loadLeaveEncashmentReport(UtilityFunctions uF){	
		monthList = new FillMonth().fillMonth();
		yearList = new FillYears().fillFutureYears(uF.getCurrentDate(CF.getStrTimeZone()), 2, 2);
		calendarYearList = new FillCalendarYears(request).fillCalendarYears(CF);
		
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
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++) {
				if(getF_org().equals(organisationList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=organisationList.get(i).getOrgName();
					} else {
						strOrg+=", "+organisationList.get(i).getOrgName();
					}
					k++;
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
		if(getF_level()!=null) {
			String strLevel="";
			int k=0;
			for(int i=0;levelList!=null && i<levelList.size();i++) {
				for(int j=0;j<getF_level().length;j++) {
					if(getF_level()[j].equals(levelList.get(i).getLevelId())) {
						if(k==0) {
							strLevel=levelList.get(i).getLevelCodeName();
						} else {
							strLevel+=", "+levelList.get(i).getLevelCodeName();
						}
						k++;
					}
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

		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getStrStartDate() {
		return strStartDate;
	}

	public void setStrStartDate(String strStartDate) {
		this.strStartDate = strStartDate;
	}

	public String getStrEndDate() {
		return strEndDate;
	}

	public void setStrEndDate(String strEndDate) {
		this.strEndDate = strEndDate;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
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

	public String getStrSbu() {
		return strSbu;
	}

	public void setStrSbu(String strSbu) {
		this.strSbu = strSbu;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public String getCalendarYear() {
		return calendarYear;
	}

	public void setCalendarYear(String calendarYear) {
		this.calendarYear = calendarYear;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
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

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillYears> getYearList() {
		return yearList;
	}

	public void setYearList(List<FillYears> yearList) {
		this.yearList = yearList;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
	}

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
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

	public List<FillCalendarYears> getCalendarYearList() {
		return calendarYearList;
	}

	public void setCalendarYearList(List<FillCalendarYears> calendarYearList) {
		this.calendarYearList = calendarYearList;
	}
	
}
