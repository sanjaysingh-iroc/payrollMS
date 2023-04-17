package com.konnect.jpms.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class CommonFunctionsNew implements IStatements{
    
	public Map<String,Set<String>> getHolidayList(Connection con,HttpServletRequest request,UtilityFunctions uF, String strD1, String strD2) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,Set<String>> holidaysMp=new HashMap<String,Set<String>>();
		try {
				
				pst = con.prepareStatement(selectHolidaysR2);
				pst.setDate(1, uF.getDateFormat(strD1, DBDATE));
				pst.setDate(2, uF.getDateFormat(strD2, DBDATE));
				
			
			rs = pst.executeQuery();
			
			while (rs.next()) {
				
				Set<String> list=holidaysMp.get(rs.getString("wlocation_id"));
				if(list==null)list=new HashSet<String>();
				
				list.add(rs.getString("_date"));
				holidaysMp.put(rs.getString("wlocation_id"),list);

			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return holidaysMp;
	}
	
	public Map<String, Set<String>> getWeekEndDateList(Connection con, String strD1, String strD2, CommonFunctions CF, UtilityFunctions uF,Map<String, Set<String>> hmWeekEndHalfDates,String wlocation) {

		Map<String, Set<String>> hmWeekEndDates = new HashMap<String, Set<String>>();
		

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			int diff=uF.parseToInt(uF.dateDifference(strD1, DBDATE, strD2, DBDATE,CF.getStrTimeZone()));
			SimpleDateFormat smft = new SimpleDateFormat(DBDATE);
			Calendar cal1 = GregorianCalendar.getInstance();
			cal1.setTime(uF.getDateFormatUtil(strD1, DBDATE));
			Map<String,String> aaa=new HashMap<String,String>();
			for(int i=0;i<diff;i++){
				
				aaa.put(smft.format(cal1.getTime()), uF.getDay(cal1.get(Calendar.DAY_OF_WEEK)));
				cal1.add(Calendar.DATE,1);
			}
			cal1.setTime(uF.getDateFormatUtil(strD1, DBDATE));
			
			if(wlocation==null)
			pst = con.prepareStatement("select * from work_location_info");
			else{
				pst = con.prepareStatement("select * from work_location_info where wlocation_id=?");
				pst.setInt(1,uF.parseToInt(wlocation));
			}
			rs = pst.executeQuery();
			while (rs.next()) {
				String weeklyofftype1=rs.getString("wlocation_weeklyofftype1");
				String weeklyofftype2=rs.getString("wlocation_weeklyofftype2");
				String weeklyofftype3=rs.getString("wlocation_weeklyofftype3");
				
				String weeklyoff1=rs.getString("wlocation_weeklyoff1");
				String weeklyoff2=rs.getString("wlocation_weeklyoff2");
				String weeklyoff3=rs.getString("wlocation_weeklyoff3");
				
				String weeklyoffno3=rs.getString("wlocation_weeknos3");
				List<String> aaaasa=Arrays.asList(weeklyoffno3.split(","));

				for(int i=0;i<diff;i++){
					
					Set<String> list=hmWeekEndDates.get(rs.getString("wlocation_id"));
					if(list==null)list=new HashSet<String>();
					String day=aaa.get(smft.format(cal1.getTime()));
					if(weeklyofftype1!=null && weeklyofftype1.equalsIgnoreCase("FD")  && weeklyoff1.equalsIgnoreCase(day) ){
						list.add(smft.format(cal1.getTime()));
					}else if(weeklyofftype2!=null &&  weeklyofftype2.equalsIgnoreCase("FD")  && weeklyoff2.equalsIgnoreCase(day) ){
						list.add(smft.format(cal1.getTime()));
					}else if(weeklyofftype3!=null &&  weeklyofftype3.equalsIgnoreCase("FD")  && weeklyoff3.equalsIgnoreCase(day) ){
						if(aaaasa.contains(cal1.get(Calendar.WEEK_OF_MONTH )+""))
							list.add(smft.format(cal1.getTime()));
						
					}
					
					if(hmWeekEndHalfDates!=null){
					Set<String> halflist=hmWeekEndHalfDates.get(rs.getString("wlocation_id"));
					if(halflist==null)halflist=new HashSet<String>();
					if(weeklyofftype1!=null && weeklyofftype1.equalsIgnoreCase("HD")  && weeklyoff1.equalsIgnoreCase(day) ){
						if(!list.contains(smft.format(cal1.getTime())))
							halflist.add(smft.format(cal1.getTime()));
					}else if(weeklyofftype2!=null &&  weeklyofftype2.equalsIgnoreCase("HD")  && weeklyoff2.equalsIgnoreCase(day) ){
						if(!list.contains(smft.format(cal1.getTime())))
							halflist.add(smft.format(cal1.getTime()));
					}else if(weeklyofftype3!=null &&  weeklyofftype3.equalsIgnoreCase("HD")  && weeklyoff3.equalsIgnoreCase(day) ){
						if(aaaasa.contains(cal1.get(Calendar.WEEK_OF_MONTH )+"")){
							if(!list.contains(smft.format(cal1.getTime())))
								halflist.add(smft.format(cal1.getTime()));
						}
						
					}
					hmWeekEndHalfDates.put(rs.getString("wlocation_id"), halflist);
					}
					hmWeekEndDates.put(rs.getString("wlocation_id"), list);
					
					cal1.add(Calendar.DATE,1);
				}
				cal1.setTime(uF.getDateFormatUtil(strD1, DBDATE));
				rs.close();
				pst.close();
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmWeekEndDates;
	}
	
	
	
	public Map<String, Set<String>> getLeaveDates(Connection con, String strD1, String strD2, CommonFunctions CF,Map<String, Set<String>> leaveCntMap,String empIds) {

		UtilityFunctions uF = new UtilityFunctions();

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String, Set<String>> empleavemp = new HashMap<String, Set<String>>();
		try {
			if(empIds!=null)
				pst = con.prepareStatement("select * from leave_application_register where _date between ? and ? and is_paid=true and emp_id in("+empIds+")");
			else 
				pst = con.prepareStatement("select * from leave_application_register where _date between ? and ? and is_paid=true ");

			 pst.setDate(1, uF.getDateFormat(strD1, DBDATE));
			pst.setDate(2, uF.getDateFormat(strD2, DBDATE));

			rs = pst.executeQuery();
			
			while (rs.next()) {
				
				if(uF.parseToDouble(rs.getString("leave_no"))<1){
					
					Set<String> list=leaveCntMap.get(rs.getString("emp_id"));
					if(list==null)list=new HashSet<String>();
					list.add(rs.getString("_date"));
					leaveCntMap.put(rs.getString("emp_id"), list);
				}else{
					Set<String> list=empleavemp.get(rs.getString("emp_id"));
					if(list==null)list=new HashSet<String>();
					list.add(rs.getString("_date"));
					empleavemp.put(rs.getString("emp_id"), list);
				}
				
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

		return empleavemp;
	}
	
	
	public Map<String, List<String>> getHalfDayRosterPolicy(String strDate, UtilityFunctions uF, Connection con){
		PreparedStatement pst=null;
		ResultSet rs = null;
		Map<String, List<String>> halfDayRosterPolicymap = new HashMap<String, List<String>>();
		try {
			
				pst = con.prepareStatement("select rhp.* from (select max(effective_date)as effective_date,wlocation_id,_mode from roster_halfday_policy where policy_status=true and effective_date <= ?   group by wlocation_id,_mode) as a ,roster_halfday_policy rhp where a.effective_date=rhp.effective_date and a.wlocation_id=rhp.wlocation_id and a._mode=rhp._mode");
				pst.setDate(1, uF.getDateFormat(strDate, DBDATE));
				
				rs = pst.executeQuery();
				while(rs.next()){
					List<String> list=new ArrayList<String>();
					list.add(rs.getString("time_value"));
					list.add(rs.getString("days"));
	
					halfDayRosterPolicymap.put(rs.getString("wlocation_id")+"_"+rs.getString("_mode"), list);
				}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return halfDayRosterPolicymap;
	}
	
	
	public Map<String,List<List<Double>>> getAttendanceRosterPolicy(Connection con) {


		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,List<List<Double>>> attendanceRosterPolicyWlocation=new HashMap<String,List<List<Double>>>();
		
		try {
			
			
			
			pst = con.prepareStatement("select * from attendance_roster_policy");
		
			rs=pst.executeQuery();
			while(rs.next()){
				List<List<Double>> list=attendanceRosterPolicyWlocation.get(rs.getString("wlocation_id"));
				if(list==null)list=new ArrayList<List<Double>>();
				List<Double> innerList=new ArrayList<Double>();
				innerList.add(rs.getDouble("from_hours"));
				innerList.add(rs.getDouble("to_hours"));
				innerList.add(rs.getDouble("deduction_days"));
				list.add(innerList);
				attendanceRosterPolicyWlocation.put(rs.getString("wlocation_id"), list);
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}

		return attendanceRosterPolicyWlocation;
	}
	
	public Map<String,String> getSalaryHeadDetails(Connection con,List<String> salaryHeadList,Map<String,String> salaryHeadsEarningDeductionDetails){
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> salaryHeadsDetails=new LinkedHashMap<String,String>();
		

try {
			
			
			
			pst = con.prepareStatement("select distinct(salary_head_name),salary_head_id,earning_deduction,weight from salary_details order by   earning_deduction desc,weight");
		
			rs=pst.executeQuery();
			while(rs.next()){
				salaryHeadList.add(rs.getString("salary_head_id"));
				salaryHeadsDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
				salaryHeadsEarningDeductionDetails.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
			}
			rs.close();
			pst.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return salaryHeadsDetails;
	}
	
	public Map<String,Map<String,Map<String,String>>> getLevelSalaryDetails(Connection con) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,Map<String,Map<String,String>>> levelhmSalaryDetails=new LinkedHashMap<String,Map<String,Map<String,String>>>();
		try {
			pst = con.prepareStatement("select * from salary_details  order by level_id,earning_deduction desc,salary_head_amount_type,salary_head_id, salary_id");
			
		
		rs = pst.executeQuery(); 

		
		
		while (rs.next()) {

			Map<String, String> hmInnerSal = new LinkedHashMap<String, String>();

			hmInnerSal.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
			hmInnerSal.put("SALARY_AMOUNT_TYPE", rs.getString("salary_head_amount_type"));
			hmInnerSal.put("SUB_SALARY_HEAD_ID", rs.getString("sub_salary_head_id"));
			hmInnerSal.put("SALARY_HEAD_AMOUNT", rs.getString("salary_head_amount"));
			hmInnerSal.put("IS_CTC_VARIABLE", rs.getString("is_ctc_variable"));
			hmInnerSal.put("SALARY_TYPE", rs.getString("salary_type"));
			hmInnerSal.put("IS_VARIABLE", rs.getString("is_variable"));

			Map<String,Map<String,String>> hmSalaryDetails = levelhmSalaryDetails.get(rs.getString("level_id"));
			if(hmSalaryDetails==null)hmSalaryDetails= new LinkedHashMap<String,Map<String,String>>();
			hmSalaryDetails.put(rs.getString("salary_head_id"), hmInnerSal);
			levelhmSalaryDetails.put(rs.getString("level_id"), hmSalaryDetails);

			
		}
		rs.close();
		pst.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return levelhmSalaryDetails;
	}
	
	
	public Map<String,Map<String,Map<String,String>>> getEmpSalaryDetails(Connection con,UtilityFunctions uF,String strDate,String empIds) {

		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,Map<String,Map<String,String>>> emphmSalaryDetails=new HashMap<String,Map<String,Map<String,String>>>();
		try {
			if(empIds!=null)
				pst = con.prepareStatement("select * from emp_salary_details esd,(select max(effective_date) as effective_date,emp_id from emp_salary_details where effective_date <= ? and is_approved=true and emp_id in("+empIds+") group by emp_id) as a  where a.effective_date=esd.effective_date and a.emp_id=esd.emp_id and esd.emp_id in("+empIds+") order by esd.emp_id");
			else
				pst = con.prepareStatement("select * from emp_salary_details esd,(select max(effective_date) as effective_date,emp_id from emp_salary_details where effective_date <= ? and is_approved=true group by emp_id) as a  where a.effective_date=esd.effective_date and a.emp_id=esd.emp_id order by esd.emp_id");

			pst.setDate(1, uF.getDateFormat(strDate, DBDATE));
		
		rs = pst.executeQuery(); 

		
		
		while (rs.next()) {

			Map<String, String> hmInnerSal = new HashMap<String, String>();
			hmInnerSal.put("AMOUNT", rs.getString("amount"));
			hmInnerSal.put("ISDISPLAY", rs.getString("isdisplay"));
			hmInnerSal.put("EARNING_DEDUCTION", rs.getString("earning_deduction"));
			hmInnerSal.put("SALARY_TYPE", rs.getString("salary_type"));
			
			Map<String,Map<String,String>> hmEmpSalaryDetails = emphmSalaryDetails.get(rs.getString("emp_id"));
			if(hmEmpSalaryDetails==null)hmEmpSalaryDetails= new HashMap<String,Map<String,String>>();
			hmEmpSalaryDetails.put(rs.getString("salary_head_id"), hmInnerSal);
			emphmSalaryDetails.put(rs.getString("emp_id"), hmEmpSalaryDetails);

			
		}
		rs.close();
		pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return emphmSalaryDetails;
	}
	
	
	public Map<String,String> getSalaryCalculation(CommonFunctions CF, UtilityFunctions uF,String empId,String level,double totalDays,double presentDays,Map<String,Map<String,String>> hmSalaryDetails,Map<String,Map<String,String>> hmEmpSalaryDetails ,Map<String, String> hmAttendanceBonusLevelMap,Map<String,String> levelBonusDetails,Map<String, String> hmVariables ,Map<String,String> lateComingPolicy,String hourslate,Map<String,String> hmArearMap,String stateId,String empServiceTax,Map<String, String> hmOtherTaxDetails,Map<String,String> EEPFMap,Map<String,String> ERPFMap,Map<String,String> ERESI,Map<String,String> EESI,Map<String,String> isDisplayempSalaryCalculatedMp,double overTimehours){
		Set<String> set=hmSalaryDetails.keySet();
		
		Iterator<String> it=set.iterator();
		double dblNETAmount=0;
		double dblGROSSAmount=0;
		Map<String,String> empSalaryCalculatedMp=new HashMap<String,String>();
//		Map<String,String> isDisplayempSalaryCalculatedMp=new HashMap<String,String>();

		while(it.hasNext()){
			
			String SalaryHead=it.next();
			
			Map<String,String> salaryDetails=hmSalaryDetails.get(SalaryHead);
			Map<String,String> empSalaryDetails=hmEmpSalaryDetails.get(SalaryHead);
			if(empSalaryDetails==null)continue;
			String strSubSalAmount =  salaryDetails.get("SALARY_HEAD_AMOUNT");
			String strSubSalAmountType =  salaryDetails.get("SALARY_AMOUNT_TYPE");
			String strSubSalId =  salaryDetails.get("SUB_SALARY_HEAD_ID");
			String isCTCVariable=salaryDetails.get("IS_CTC_VARIABLE");

			String strSalaryType =  empSalaryDetails.get("SALARY_TYPE");
			String str_E_OR_D =  empSalaryDetails.get("EARNING_DEDUCTION");

			String isDisplay=empSalaryDetails.get("ISDISPLAY");
			double dblAmount=0;
			
			if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("E")){
			switch(uF.parseToInt(SalaryHead)){
			/**********  Attendance Bonus   *************/
			case ATTENDANCE_BONUS:  
				 
				double daysDiff=totalDays-presentDays;
				if(daysDiff<0){
					daysDiff=0;
				}
				if(empId.equals("211")){
					System.out.println("totalDays=>>>"+totalDays);
					System.out.println("presentDays=>>>"+presentDays);

					System.out.println("daysDiff=>>>"+daysDiff);

				}
				double dblAttendanceBonus=uF.parseToDouble(hmAttendanceBonusLevelMap.get(level+"_"+((int)daysDiff)));
				
					dblAmount= dblAttendanceBonus;

			break;
			case OVER_TIME:  
			
				
				
			break;
			case BONUS:
				// Bonus is paid independent of paycycle -- 
				
				if(uF.parseToBoolean(CF.getIsBonusPaidWithPayroll())){
				}
				
			break;
			
			case AREARS:

				double dblArearAmount = getArearCalculation( uF,  hmArearMap);
				dblAmount=dblArearAmount;
				
			break;
			
//			case INCENTIVES:
//				isDefinedEarningDeduction = true;
//				double dblIncentiveAmount = getIncentivesCalculation(con, uF, empId, level, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIncentives, CF);
//				hmTotalisDisplay.put(SalaryHead, uF.formatIntoTwoDecimal(Math.round(dblIncentiveAmount)));
//				dblIncentiveAmount = Math.round(dblIncentiveAmount);
//				dblGross += dblIncentiveAmount;
//				dblGrossTDS += dblIncentiveAmount;
//			break;
			
//			case REIMBURSEMENT:
//				isDefinedEarningDeduction = true;
//				double dblReimbursementAmount = getReimbursementCalculation(con, uF, empId, level, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmReimbursement, CF);
//				hmTotalisDisplay.put(SalaryHead, uF.formatIntoTwoDecimal(Math.round(dblReimbursementAmount)));
//				dblReimbursementAmount = Math.round(dblReimbursementAmount);
//				dblGross += dblReimbursementAmount;
//			break;
			
//			case TRAVEL_REIMBURSEMENT:
//				isDefinedEarningDeduction = true;
//				double dblTravelReimbursementAmount = getMobileReimbursementCalculation(con, uF, empId, level, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualTravelReimbursement, CF);
//				hmTotalisDisplay.put(SalaryHead, uF.formatIntoTwoDecimal(Math.round(dblTravelReimbursementAmount)));
//				dblTravelReimbursementAmount = Math.round(dblTravelReimbursementAmount);
//				dblGross += dblTravelReimbursementAmount;
//			break;
			
//			case MOBILE_REIMBURSEMENT:
//				isDefinedEarningDeduction = true;
//				double dblMobileReimbursementAmount = getMobileReimbursementCalculation(con, uF, empId, level, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualMobileReimbursement, CF);
//				hmTotalisDisplay.put(SalaryHead, uF.formatIntoTwoDecimal(Math.round(dblMobileReimbursementAmount)));
//				dblMobileReimbursementAmount = Math.round(dblMobileReimbursementAmount);
//				dblGross += dblMobileReimbursementAmount;
//			break;
			
//			case OTHER_REIMBURSEMENT:
//				isDefinedEarningDeduction = true;
//				double dblOtherReimbursementAmount = getOtherReimbursementCalculation(con, uF, empId, level, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherReimbursement, CF);
//				hmTotalisDisplay.put(SalaryHead, uF.formatIntoTwoDecimal(Math.round(dblOtherReimbursementAmount)));
//				dblOtherReimbursementAmount = Math.round(dblOtherReimbursementAmount);
//				dblGross += dblOtherReimbursementAmount;
//			break;
			
//			case OTHER_EARNING:
//				isDefinedEarningDeduction = true;
//				double dblOtherEarningAmount = getIndividualOtherEarningCalculation(con, uF, empId, level, hmTotalisDisplay, strFinancialYearStart, strFinancialYearEnd, nPayMonth, hmIndividualOtherEarning, CF);
//				hmTotalisDisplay.put(SalaryHead, uF.formatIntoTwoDecimal(Math.round(dblOtherEarningAmount)));
//				dblOtherEarningAmount = Math.round(dblOtherEarningAmount);
//				dblGross += dblOtherEarningAmount;
//			break;
			
			case SERVICE_TAX:
				
				if(empServiceTax!=null){
					double dblServiceTaxAmount = calculateServiceTax( uF,  dblGROSSAmount,stateId,hmOtherTaxDetails);
					dblAmount=dblServiceTaxAmount;
					}

				
				  
				break;	
			case GRINDING_ALLOWANCE:
				

				double dblGrinddingAmount =  uF.parseToDouble(empSalaryDetails.get("AMOUNT"));
				if(strSalaryType!=null && strSalaryType.equalsIgnoreCase("F")){
					dblAmount = dblGrinddingAmount;
				}else if(strSalaryType!=null && strSalaryType.equalsIgnoreCase("D")){
					dblAmount = dblGrinddingAmount * presentDays;
				}else{
					dblAmount = dblGrinddingAmount * (presentDays / totalDays);
				}
				
				if(overTimehours>0){
					dblAmount+=(dblGrinddingAmount/8)*overTimehours;
				}
				  
				break;	
			case LAPPING_ALLOWANCE:
				

				double dblLappingAmount =  uF.parseToDouble(empSalaryDetails.get("AMOUNT"));
				if(strSalaryType!=null && strSalaryType.equalsIgnoreCase("F")){
					dblAmount = dblLappingAmount;
				}else if(strSalaryType!=null && strSalaryType.equalsIgnoreCase("D")){
					dblAmount = dblLappingAmount * presentDays;
				}else{
					dblAmount = dblLappingAmount * (presentDays / totalDays);
				}
				
				if(overTimehours>0){
					dblAmount+=(dblLappingAmount/8)*overTimehours;
				}
				  
				break;	
				case CARBON_ALLOWANCE:
					

					double dblCarbonAmount =  uF.parseToDouble(empSalaryDetails.get("AMOUNT"));
					if(strSalaryType!=null && strSalaryType.equalsIgnoreCase("F")){
						dblAmount = dblCarbonAmount;
					}else if(strSalaryType!=null && strSalaryType.equalsIgnoreCase("D")){
						dblAmount = dblCarbonAmount * presentDays;
					}else{
						dblAmount = dblCarbonAmount * (presentDays / totalDays);
					}
					
					if(overTimehours>0){
						dblAmount+=(dblCarbonAmount/8)*overTimehours;
					}
					  
					break;	
			default:
				if(hmVariables.get(empId+"_"+SalaryHead+"_"+"E")!=null){
					dblAmount=uF.parseToDouble((String)hmVariables.get(empId+"_"+SalaryHead+"_E"));
					
				
				}else if("A".equalsIgnoreCase(strSubSalAmountType)){
					double dblTotalAmount =  uF.parseToDouble(empSalaryDetails.get("AMOUNT"));
					if(strSalaryType!=null && strSalaryType.equalsIgnoreCase("F")){
						dblAmount = dblTotalAmount;
					}else if(strSalaryType!=null && strSalaryType.equalsIgnoreCase("D")){
						dblAmount = dblTotalAmount * presentDays;
					}else{
						dblAmount = dblTotalAmount * (presentDays / totalDays);
					}
					
				}else if("P".equalsIgnoreCase(strSubSalAmountType)){
					
					double dblTotalAmount = uF.parseToDouble(empSalaryCalculatedMp.get(strSubSalId));
					
					dblAmount=dblTotalAmount*uF.parseToDouble(strSubSalAmount)/100;
					
				}
				
				break;
			}
			}else if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D")){
				

				
				
				/**
				 * 			TAX CALCULATION STARTS HERE
				 * 
				 * */
				
				switch(uF.parseToInt(SalaryHead)){
				/**********  	 TAX   *************/
				
				case LATE_COMING:
					
												
					double dblLatecoming = getLateCalculation(uF, empSalaryCalculatedMp,    presentDays,lateComingPolicy,uF.parseToDouble(hourslate));
					dblAmount=dblLatecoming;
					
				break;
				
				case PROFESSIONAL_TAX:
////					
						     
//						double dblPt = calculateProfessionalTax(con, uF, dblGrossPT, strFinancialYearEnd, nPayMonth, (String)hmEmpStateMap.get(empId));
					
					break;
				/**********  EPF EMPLOYEE CONTRIBUTION   *************/
				case EMPLOYEE_EPF:
//					if(EEPFMap!=null){
//						if(empId.equals("343")){
//							System.out.println("EEPFMap==="+EEPFMap);
//							System.out.println("empSalaryCalculatedMp==="+empSalaryCalculatedMp);
//						}
//						double dblEEPF = calculateEEPF(uF,EEPFMap,empSalaryCalculatedMp);
//						dblAmount=dblEEPF;
//					}
					break;
					
//				case VOLUNTARY_EPF:
					

//					break;
					
				/**********  EPF EMPLOYER CONTRIBUTION   *************/
				case EMPLOYER_EPF:
//					if(ERPFMap!=null){
//						double dblERPF = calculateERPF( uF,CF,ERPFMap,empSalaryCalculatedMp);
//						dblAmount=dblERPF;
//					}
					break;
					
				
				/**********  ESI EMPLOYER CONTRIBUTION   *************/
				case EMPLOYER_ESI:
						if(ERESI!=null){
						double dblESI = calculateERESI(uF,ERESI,empSalaryCalculatedMp);
						dblAmount=dblESI;
						}
					break;
					
					
					/**********  /ESI EMPLOYER CONTRIBUTION   *************/
					
					/**********  ESI EMPLOYEE CONTRIBUTION   *************/
				case EMPLOYEE_ESI:
						if(EESI!=null){
					double dblEESI = calculateEEESI( uF,empId,EESI,empSalaryCalculatedMp,  hmVariables);
					dblAmount=dblEESI;
						}
					
					break;
					
					
					/**********  /ESI EMPLOYEE CONTRIBUTION   *************/	
					
					/**********  LWF EMPLOYER CONTRIBUTION   *************/
				case EMPLOYER_LWF:
					
					break;
					
					
					/**********  /LWF EMPLOYER CONTRIBUTION   *************/
					
					/**********  LWF EMPLOYEE CONTRIBUTION   *************/
				case EMPLOYEE_LWF:
					
					break;
					
					/**********  /LWF EMPLOYEE CONTRIBUTION   *************/	
					
				case LOAN:
					
					
						
						
					
					break;  
					
//				case OTHER_DEDUCTION:
					 
						
					
					
//				case MOBILE_RECOVERY:
					
					
//					break;		
					
				/**********  TDS   *************/
				case TDS:
					
					break;
					
				default:
					
					if(hmVariables.get(empId+"_"+SalaryHead+"_"+"D")!=null){
						
						dblAmount=uF.parseToDouble(hmVariables.get(empId+"_"+SalaryHead+"_D"));
						
					
					}else if("A".equalsIgnoreCase(strSubSalAmountType)){
						double dblTotalAmount =  uF.parseToDouble(empSalaryDetails.get("AMOUNT"));
						if(strSalaryType!=null && strSalaryType.equalsIgnoreCase("F")){
							dblAmount = dblTotalAmount;
						}else if(strSalaryType!=null && strSalaryType.equalsIgnoreCase("D")){
							dblAmount = dblTotalAmount * presentDays;
						}else{
							dblAmount = dblTotalAmount * (presentDays / totalDays);
						}
						
					}else if("P".equalsIgnoreCase(strSubSalAmountType)){
						
						double dblTotalAmount = uF.parseToDouble(empSalaryCalculatedMp.get(strSubSalId));
						dblAmount=dblTotalAmount*uF.parseToDouble(strSubSalAmount)/100;
						
					}
					
					
					break;
				}
			
				
				
				
			}
			
			
			
			
			
			if (!uF.parseToBoolean(isDisplay) && uF.parseToBoolean(isCTCVariable)) {
//				continue;
				isDisplayempSalaryCalculatedMp.put(SalaryHead, dblAmount+"");
			}else  if (uF.parseToBoolean(isDisplay)){
				if(str_E_OR_D!=null && str_E_OR_D.equalsIgnoreCase("D")){
					dblNETAmount-=dblAmount;
				}else{
					dblNETAmount+=dblAmount;
					dblGROSSAmount+=dblAmount;

				}
				
				empSalaryCalculatedMp.put(SalaryHead, dblAmount+"");
				
//				hmSalaryHeadReCalculatedMap.put(strSalaryHeadId, hmSalaryInner);
			}
			
//			empSalaryCalculatedMp.put(SalaryHead, dblAmount+"");
			
			
		}
		
		empSalaryCalculatedMp.put("NET", dblNETAmount+"");
		empSalaryCalculatedMp.put("GROSS", dblGROSSAmount+"");
		return empSalaryCalculatedMp;
	}
	public double getOverTimeCalculationHours(UtilityFunctions uF,Map<String,String> hmOvertimePolicy,Map<String,String> empSalaryCalculatedMp,double workingDays,double overTimehours) {
		double dblTotalOverTimeAmount = 0.0d;

		String salaryHeadId=hmOvertimePolicy.get("SALARY_HEAD_ID");
		List<String> salaryHeadList=null;
		if(salaryHeadId!=null){
			salaryHeadList=Arrays.asList(salaryHeadId.split(","));
		}
		double salaryHeadamount=0;
		for(int i=0;salaryHeadList!=null && i<salaryHeadList.size();i++){
			salaryHeadamount+=uF.parseToDouble( empSalaryCalculatedMp.get(salaryHeadList.get(i)));

		}
		

		dblTotalOverTimeAmount=(((salaryHeadamount/workingDays)/8)*overTimehours)*2;
		
		return dblTotalOverTimeAmount;
	}
	
	
	
	public Map<String,String> getOverTimehours(Connection con,UtilityFunctions uF, String strD1, String strD2,String empIds){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,String> empOvertimeMp=new HashMap<String,String>();
		try {
			if(empIds!=null)
				pst = con.prepareStatement("select sum(approved_ot_hours) as approved_ot_hours,emp_id from overtime_hours where _date between ? and ? and emp_id in("+empIds+") group by emp_id");
			else
				pst = con.prepareStatement("select sum(approved_ot_hours) as approved_ot_hours,emp_id from overtime_hours where _date between ? and ? group by emp_id");

				pst.setDate(1, uF.getDateFormat(strD1, DBDATE));
				pst.setDate(2, uF.getDateFormat(strD2, DBDATE));
			
			rs = pst.executeQuery();
			
			while (rs.next()) {
				
				empOvertimeMp.put(rs.getString("emp_id"),rs.getString("approved_ot_hours"));

			}
			rs.close();
			pst.close();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return empOvertimeMp;
	}

	public Map<String, Map<String, String>> getEmpOverTimeLevelPolicy(Connection con,CommonFunctions CF,UtilityFunctions uF, String strD1, String strD2, String strPC) {
		PreparedStatement pst=null;
		ResultSet rs=null;
		Map<String, Map<String, String>> hmEmpOverTimeLevelPolicy=new HashMap<String, Map<String,String>>();
		try{
					
			pst = con.prepareStatement("select * from overtime_details where overtime_type='EH' and date_from <=?");
			pst.setDate(1, uF.getDateFormat(strD1, DBDATE));
			rs = pst.executeQuery();
			while(rs.next()){
				Map<String,String> hmEmpOverTimePolicy=new HashMap<String,String>();
				
				hmEmpOverTimePolicy.put("OVERTIME_ID",rs.getString("overtime_id"));
				hmEmpOverTimePolicy.put("OVERTIME_CODE",rs.getString("overtime_code"));
				hmEmpOverTimePolicy.put("OVERTIME_DESCRIPTION",rs.getString("overtime_description"));
				hmEmpOverTimePolicy.put("LEVEL_ID",rs.getString("level_id"));
				hmEmpOverTimePolicy.put("GRADE_ID",rs.getString("grade_id"));
				hmEmpOverTimePolicy.put("OVERTIME_TYPE",rs.getString("overtime_type"));
				hmEmpOverTimePolicy.put("OVERTIME_PAYMENT_TYPE",rs.getString("overtime_payment_type"));
				hmEmpOverTimePolicy.put("DATE_FROM",rs.getString("date_from"));
				hmEmpOverTimePolicy.put("OVERTIME_PAYMENT_AMOUNT",rs.getString("overtime_payment_amount"));
				hmEmpOverTimePolicy.put("DAY_CALCULATION",rs.getString("day_calculation"));
				hmEmpOverTimePolicy.put("FIXED_DAY_CALCULATION",rs.getString("fixed_day_calculation"));
				hmEmpOverTimePolicy.put("STANDARD_WKG_HOURS",rs.getString("standard_wkg_hours"));
				hmEmpOverTimePolicy.put("FIXED_STWKG_HOURS",rs.getString("fixed_stwkg_hrs"));
				hmEmpOverTimePolicy.put("STANDARD_TIME",rs.getString("standard_time"));
				hmEmpOverTimePolicy.put("BUFFER_STANDARD_TIME",rs.getString("buffer_standard_time"));
				hmEmpOverTimePolicy.put("OVERTIME_HOURS",rs.getString("over_time_hrs"));
				hmEmpOverTimePolicy.put("FIXED_OVERTIME_HOURS",rs.getString("fixed_overtime_hrs"));
				hmEmpOverTimePolicy.put("MIN_OVER_TIME", rs.getString("min_over_time"));				
				hmEmpOverTimePolicy.put("OVERTIME_TYPE", rs.getString("overtime_type"));
				hmEmpOverTimePolicy.put("ORG_ID", rs.getString("org_id")); 
				hmEmpOverTimePolicy.put("SALARY_HEAD_ID", rs.getString("salaryhead_id"));
				hmEmpOverTimePolicy.put("CAL_BASIS",rs.getString("calculation_basis"));
				
				hmEmpOverTimeLevelPolicy.put(rs.getString("level_id"), hmEmpOverTimePolicy);				
				
			}	
			rs.close();
			pst.close();
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return hmEmpOverTimeLevelPolicy;
	}

	
	public Map<String,Map<String,String>> getBonusDetails(Connection con, UtilityFunctions uF,String strFinancialYearStart,String strFinancialYearEnd){
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		Map<String,Map<String,String>> bonusDetails=new HashMap<String,Map<String,String>>();
		try {
			pst = con.prepareStatement("SELECT * FROM bonus_details where date_from =? and date_to=? order by bonus_id desc");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
			rs = pst.executeQuery();
			if(rs.next()){
				Map<String,String> bonusMap=new HashMap<String,String>();
				bonusMap.put("BONUS_MINIMUM", rs.getString("bonus_minimum"));
				bonusMap.put("BONUS_MAXIMUM", rs.getString("bonus_maximum"));
				bonusMap.put("BONUS_AMOUNT", rs.getString("bonus_amount"));
				bonusMap.put("BONUS_MINIMUM_DAYS", rs.getString("bonus_minimum_days"));
				bonusMap.put("BONUS_TYPE", rs.getString("bonus_type"));
				bonusMap.put("BONUS_PERIOD", rs.getString("bonus_period"));
				bonusMap.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
				bonusMap.put("SALARY_EFFECTIVE_YEAR", rs.getString("salary_effective_year"));
				bonusMap.put("SALARY_CALCULATION", rs.getString("salary_calculation"));
				bonusDetails.put(rs.getString("level_id"), bonusMap);

			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return bonusDetails;
	}
	

	
	public double getLateCalculation(UtilityFunctions uF, Map<String, String> hmActualSalaryCTC,double  dblPresent,Map<String,String> lateComingPolicy,double  lateMinutes){
		
		double dblOverTime = 0.0d;
		try {
			
					
			double dblSubSalaryAmountActualCTC = 0;

			if(lateComingPolicy==null)lateComingPolicy=new HashMap<String,String>();
			if(lateComingPolicy.get("SALARY_HEAD_ID")!=null){
			List<String> list=Arrays.asList(lateComingPolicy.get("SALARY_HEAD_ID").split(","));
						for(String salaryHead:list){
				dblSubSalaryAmountActualCTC +=uF.parseToDouble( hmActualSalaryCTC.get(salaryHead));
			}
			}
			dblOverTime=dblSubSalaryAmountActualCTC/dblPresent/480*lateMinutes;
			
			

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return dblOverTime;
	}
	
public String[] getFinancialYear(Connection con, String strDate, CommonFunctions CF, UtilityFunctions uF) {
		
		String []arr = new String[2];
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
		
			pst = con.prepareStatement("select * from financial_year_details where financial_year_from <= ? and financial_year_to >= ? ");
			pst.setDate(1, uF.getDateFormat(strDate, DBDATE));
			pst.setDate(2, uF.getDateFormat(strDate, DBDATE));
			rs = pst.executeQuery();
			while(rs.next()){
				arr[0] =rs.getString("financial_year_from");
				arr[1] = rs.getString("financial_year_to");
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return arr;
	}

public Map<String, String> getVariableAmount(Connection con, UtilityFunctions uF,  String strPC,String empIds){
	
	PreparedStatement pst = null;
	ResultSet rs  = null;
	Map<String, String> hmVariables  = new HashMap<String, String>();
	try {
		if(empIds!=null)
		pst = con.prepareStatement("select * from otherearning_individual_details where pay_paycycle = ? and is_approved = 1 and emp_id in("+empIds+")");
		else
			pst = con.prepareStatement("select * from otherearning_individual_details where pay_paycycle = ? and is_approved = 1");

		pst.setInt(1, uF.parseToInt(strPC)); 
		rs = pst.executeQuery();
		while(rs.next()){
			hmVariables.put(rs.getString("emp_id")+"_"+rs.getString("salary_head_id")+"_"+rs.getString("earning_deduction"), rs.getString("pay_amount"));
		}
		
		rs.close();
		pst.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return hmVariables;
}
public Map<String, List<Map<String, String>>> getLoanDetails(Connection con, UtilityFunctions uF,  String strDate,String empIds){
	
	PreparedStatement pst = null;
	ResultSet rs  = null;
	Map<String, List<Map<String, String>>> hmLOANVariables  = new HashMap<String, List<Map<String, String>>>();
	try {
		if(empIds!=null)
		pst = con.prepareStatement("select * from loan_applied_details lad, loan_details ld where lad.loan_id = ld.loan_id and is_approved=1 and is_completed = false and applied_date<=? and emp_id in("+empIds+")");
		else
			pst = con.prepareStatement("select * from loan_applied_details lad, loan_details ld where lad.loan_id = ld.loan_id and is_approved=1 and is_completed = false and applied_date<=?");

		pst.setDate(1, uF.getDateFormat(strDate, DBDATE));
		rs = pst.executeQuery();
		while(rs.next()){
			
			
			Map<String, String> hmEmpLoanInner = new HashMap<String, String>();
			hmEmpLoanInner.put("AMOUNT_PAID", rs.getString("amount_paid"));
			hmEmpLoanInner.put("TDS_AMOUNT", rs.getString("tds_amount"));
			hmEmpLoanInner.put("BALANCE_AMOUNT", rs.getString("balance_amount"));
			hmEmpLoanInner.put("LOAN_INTEREST", rs.getString("loan_interest"));
			hmEmpLoanInner.put("DURATION_MONTHS", rs.getString("duration_months"));
			hmEmpLoanInner.put("APPROVED_DATE", rs.getString("approved_date"));
			hmEmpLoanInner.put("LOAN_APPLIED_ID", rs.getString("loan_applied_id"));
			hmEmpLoanInner.put("LOAN_ID", rs.getString("loan_id"));
			
			hmEmpLoanInner.put("LOAN_EMI", rs.getString("loan_emi"));
			

			List<Map<String, String>> loanEmplist=hmLOANVariables.get(rs.getString("emp_id"));
			if(loanEmplist==null)loanEmplist=new ArrayList<Map<String, String>>();
			loanEmplist.add(hmEmpLoanInner);
			hmLOANVariables.put(rs.getString("emp_id"), loanEmplist);
		}
		rs.close();
		pst.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return hmLOANVariables;
}

public double calculateLOAN(List<Map<String, String>> loanEmplist, UtilityFunctions uF, double dblGross, String strEmpId, CommonFunctions CF,  Map<String,Map<String,String>> hmEmpLoan){
	
	double dblCalculatedAmount = 0;
	double dblTotalCalculatedAmount = 0;
	
	
	try {
		
		
		double dblBalAmt = 0;
		
		
		for(Map<String,String> loanMp:loanEmplist){
			
			dblBalAmt=uF.parseToDouble(loanMp.get("BALANCE_AMOUNT"));
			dblCalculatedAmount=uF.parseToDouble(loanMp.get("LOAN_EMI"));
			
			
				
				
				if(dblCalculatedAmount>=dblBalAmt){
					dblCalculatedAmount = dblBalAmt;
				}
				if(dblCalculatedAmount>dblGross){
					dblCalculatedAmount = dblGross;
				}
				
				dblGross=dblGross-dblCalculatedAmount;
				
				dblTotalCalculatedAmount +=dblCalculatedAmount;
				
				
				Map<String,String> hmEmpLoanInner = hmEmpLoan.get(strEmpId);
				if(hmEmpLoanInner==null)hmEmpLoanInner=new HashMap<String,String>();
				dblCalculatedAmount+=uF.parseToDouble(hmEmpLoanInner.get(loanMp.get("LOAN_ID")));
				
				hmEmpLoanInner.put(loanMp.get("LOAN_ID"), uF.formatIntoTwoDecimal(dblCalculatedAmount));
				hmEmpLoan.put(strEmpId, hmEmpLoanInner);
				
				
			}
		 
	} catch (Exception e) {
		e.printStackTrace();
		
	}
	return dblTotalCalculatedAmount;
	
}

public Map<String, String> getLoanPoliciesMap(Connection con, UtilityFunctions uF, String strOrgId,List<String> loanList) {
	Map<String, String> hmLoanPoliciesMap = new HashMap<String, String>();

	PreparedStatement pst = null;
	ResultSet rs = null;

	try {

		pst = con.prepareStatement(selectLoanDetails2);
		pst.setInt(1, uF.parseToInt(strOrgId));
		rs = pst.executeQuery();
		while (rs.next()) {
			loanList.add(rs.getString("loan_id"));
			hmLoanPoliciesMap.put(rs.getString("loan_id"), rs.getString("loan_code"));
		}
		
		rs.close();
		pst.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return hmLoanPoliciesMap;  
}

public double getArearCalculation(UtilityFunctions uF, Map<String,String> hmArearMap){
	
	
	double dblMonthlyAmount = 0;
	
	try {
		

		if(hmArearMap==null)hmArearMap=new HashMap<String, String>();
		
		
		double dblBalanceAmount = uF.parseToDouble((String)hmArearMap.get("AMOUNT_BALANCE"));
		dblMonthlyAmount = uF.parseToDouble((String)hmArearMap.get("MONTHLY_AREAR"));
		
		if((dblBalanceAmount-dblMonthlyAmount) >0 && (dblBalanceAmount-dblMonthlyAmount) < 1){
			dblMonthlyAmount = dblBalanceAmount;
		}
			
		
		
		
	} catch (Exception e) {
		e.printStackTrace();
	}
	return dblMonthlyAmount;
}
public double calculateServiceTax(UtilityFunctions uF, double dblGross, String strStateId, Map<String, String> hmOtherTaxDetails){
	
	double dblServiceTaxAmount = 0;
	double dblCess1Amount = 0;
	double dblCess2Amount = 0;
	
	try {
		
		double dblServiceTax = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_SERVICE_TAX"));
		double dblEduTax = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_EDU_TAX"));
		double dblSTDTax = uF.parseToDouble(hmOtherTaxDetails.get(strStateId+"_STD_TAX"));
		
		dblServiceTaxAmount = (dblGross * dblServiceTax)/100;
		dblCess1Amount = (dblServiceTaxAmount * dblEduTax)/100;
		dblCess2Amount = (dblServiceTaxAmount * dblSTDTax)/100;
		
		dblServiceTaxAmount = dblServiceTaxAmount + dblCess1Amount + dblCess2Amount;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return dblServiceTaxAmount;
}
public Map<String,String> getEmpWlocationMap(Connection con,Map<String,String> hmEmpMertoMap) {
	PreparedStatement pst = null;
	ResultSet rs = null;
	Map<String,String> wlocationMp=new HashMap<String,String>();
	try {
		pst = con.prepareStatement("select wlocation_state_id,wlocation_id,ismetro from work_location_info");
		rs = pst.executeQuery();
		
		while (rs.next()) {
			wlocationMp.put(rs.getString("wlocation_id"), rs.getString("wlocation_state_id"));
			hmEmpMertoMap.put(rs.getString("wlocation_id"), rs.getString("ismetro"));
		}
		
		rs.close();
		pst.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return wlocationMp;

} 

public Map<String,String> getEEPF(Connection con,  UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	Map<String,String> EEPFMap=new HashMap<String,String>();
	try {
		
		pst = con.prepareStatement(selectEEPF);
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
		
		rs = pst.executeQuery();

		
		while(rs.next()){
			EEPFMap.put("EEPF_CONTRIBUTION", rs.getString("eepf_contribution"));
			EEPFMap.put("EPF_MAX_LIMIT", rs.getString("epf_max_limit"));
			EEPFMap.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
		}
		
		rs.close();
		pst.close();
	} catch (Exception e) {
		e.printStackTrace();
	
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return EEPFMap;
	
}
public double calculateEEPF(UtilityFunctions uF,Map<String,String> EEPFMap,Map<String,String> hmTotal){
	

	double dblCalculatedAmount = 0;
	
	try {
		
		double dblEEPFAmount = uF.parseToDouble(EEPFMap.get("EEPF_CONTRIBUTION"));
		double dblMaxAmount =  uF.parseToDouble(EEPFMap.get("EPF_MAX_LIMIT"));
		String strSalaryHeads = EEPFMap.get("SALARY_HEAD_ID");
		
		
		String []arrSalaryHeads = null;
		if(strSalaryHeads!=null){
			arrSalaryHeads = strSalaryHeads.split(",");
		}
		
		
		double dblAmount = 0;
		for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
			dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
		}
		
		
		if(dblAmount>=dblMaxAmount){
			dblAmount = dblMaxAmount;
			
		}
		dblCalculatedAmount = ( dblEEPFAmount * dblAmount ) / 100;
		
		
		
	} catch (Exception e) {
		e.printStackTrace();
	
	}
	return dblCalculatedAmount;
	
}

public Map<String,String> getERPF(Connection con,UtilityFunctions uF,  String strFinancialYearStart, String strFinancialYearEnd){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	Map<String,String> ERPFMap=new HashMap<String,String>();
	
	
	try {
		
		pst = con.prepareStatement(selectERPF);
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
		
		rs = pst.executeQuery();
		
		
		while(rs.next()){
			
			ERPFMap.put("ERPF_CONTRIBUTION", rs.getString("erpf_contribution"));
			ERPFMap.put("ERPS_CONTRIBUTION", rs.getString("erps_contribution"));
			ERPFMap.put("ERDLI_CONTRIBUTION", rs.getString("erdli_contribution"));
			
			ERPFMap.put("PF_ADMIN_CHARGES", rs.getString("pf_admin_charges"));
			ERPFMap.put("EDLI_ADMIN_CHARGES", rs.getString("edli_admin_charges"));
			ERPFMap.put("ERPF_MAX_LIMIT", rs.getString("erpf_max_limit"));
			ERPFMap.put("EPF_MAX_LIMIT", rs.getString("epf_max_limit"));
			ERPFMap.put("EPS_MAX_LIMIT", rs.getString("eps_max_limit"));
			ERPFMap.put("EDLI_MAX_LIMIT", rs.getString("edli_max_limit"));
			ERPFMap.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
			ERPFMap.put("IS_ERPF_CONTRIBUTION", rs.getString("is_erpf_contribution"));
			ERPFMap.put("IS_ERPS_CONTRIBUTION", rs.getString("is_erps_contribution"));
			ERPFMap.put("IS_PF_ADMIN_CHARGES", rs.getString("is_pf_admin_charges"));
			ERPFMap.put("IS_EDLI_ADMIN_CHARGES", rs.getString("is_edli_admin_charges"));
			ERPFMap.put("IS_ERDLI_CONTRIBUTION", rs.getString("is_erdli_contribution"));
			
		}
		
		rs.close();
		pst.close();
	
		
	} catch (Exception e) {
		e.printStackTrace();
		
	} finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return ERPFMap;
	
}

public double calculateERPF(UtilityFunctions uF,CommonFunctions CF, Map<String,String> ERPFMap, Map<String,String> hmTotal){
	
	double dblEPS1 = 0;
	double dblEPS = 0;
	double dblEPF = 0;
	double dblEDLI = 0;
	
	double dblEPFAdmin = 0;
	double dblEDLIAdmin = 0;
	
	double dblTotalEPF = 0;
	double dblTotalEDLI = 0;
	
	
	try {
		
			double	dblERPFAmount = uF.parseToDouble(ERPFMap.get("ERPF_CONTRIBUTION"));
			double	dblERPSAmount = uF.parseToDouble(ERPFMap.get("ERPS_CONTRIBUTION"));
			double	dblERDLIAmount =uF.parseToDouble(ERPFMap.get("ERDLI_CONTRIBUTION"));
			double	dblPFAdminAmount = uF.parseToDouble(ERPFMap.get("PF_ADMIN_CHARGES"));
			double	dblEDLIAdminAmount = uF.parseToDouble(ERPFMap.get("EDLI_ADMIN_CHARGES"));
			
			double	dblEPRMaxAmount = uF.parseToDouble(ERPFMap.get("ERPF_MAX_LIMIT"));
			double	dblEPFMaxAmount = uF.parseToDouble(ERPFMap.get("EPF_MAX_LIMIT"));
			double	dblEPSMaxAmount = uF.parseToDouble(ERPFMap.get("EPS_MAX_LIMIT"));
			double	dblEDLIMaxAmount = uF.parseToDouble(ERPFMap.get("EDLI_MAX_LIMIT"));
			
			String strSalaryHeads = ERPFMap.get("SALARY_HEAD_ID");
			
			
			boolean	erpfContributionchbox = uF.parseToBoolean(ERPFMap.get("IS_ERPF_CONTRIBUTION"));
			boolean	erpsContributionchbox = uF.parseToBoolean(ERPFMap.get("IS_ERPS_CONTRIBUTION"));
			boolean	pfAdminChargeschbox = uF.parseToBoolean(ERPFMap.get("IS_PF_ADMIN_CHARGES"));
			boolean	edliAdminChargeschbox = uF.parseToBoolean(ERPFMap.get("IS_EDLI_ADMIN_CHARGES"));
			boolean	erdliContributionchbox = uF.parseToBoolean(ERPFMap.get("IS_ERDLI_CONTRIBUTION"));
//		}

		String []arrSalaryHeads = null;
		if(strSalaryHeads!=null){
			arrSalaryHeads = strSalaryHeads.split(",");
		}
		
		
		double dblAmount = 0;
		double dblAmountERPF = 0;
		double dblAmountEEPF = 0;
		double dblAmountERPS = 0;
		double dblAmountERPS1 = 0;
		double dblAmountEREDLI = 0;
		for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
			dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
		}
		
		
			
		if(dblAmount>=dblEPRMaxAmount){
			dblAmountERPF = dblEPRMaxAmount;
		}else{
			dblAmountERPF = dblAmount;
		}
		
		if(dblAmount>=dblEPFMaxAmount){
			dblAmountEEPF = dblEPFMaxAmount;
		}else{
			dblAmountEEPF = dblAmount;
		}
		
		
		dblAmountERPS1 = dblAmount;
		if(dblAmount>=dblEPSMaxAmount){
			dblAmountERPS = dblEPSMaxAmount;
		}else{
			dblAmountERPS = dblAmount;
		}
		
		if(dblAmount>=dblEDLIMaxAmount){
			dblAmountEREDLI = dblEDLIMaxAmount;
		}else{
			dblAmountEREDLI = dblAmount;
		}
		
		
		
		
			if(erpfContributionchbox){
				dblEPF = (( dblERPFAmount * dblAmountERPF ) / 100);
			}
			if(erpsContributionchbox){
				dblEPS = (( dblERPSAmount * dblAmountERPS ) / 100);
				dblEPS1 = (( dblERPSAmount * dblAmountERPS1 ) / 100);
			}
				
			if(erdliContributionchbox){
				dblEDLI = (( dblERDLIAmount * dblAmountEREDLI ) / 100);
			}
			
			if(edliAdminChargeschbox){
				dblEDLIAdmin = (( dblEDLIAdminAmount * dblAmountEREDLI ) / 100);
			}
			if(pfAdminChargeschbox){
				dblEPFAdmin = (( dblPFAdminAmount * dblAmountEEPF ) / 100);
			}
		
		
		
		if(CF.isEPF_Condition1()){
			dblEPF += dblEPS1 - dblEPS;
		}
		
		
		
		dblTotalEDLI = dblEDLI + dblEDLIAdmin;
		dblTotalEPF = dblEPF + dblEPS + dblEPFAdmin;
		
		
		
		
	} catch (Exception e) {
		e.printStackTrace();
		
	}
	return (dblTotalEPF + dblTotalEDLI);
	
}

public Map<String,Map<String,String>> getERESI(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	
	Map<String,Map<String,String>> ERESIMap=new HashMap<String,Map<String,String>>();
	try {
		
		pst = con.prepareStatement("select * from esi_details where financial_year_start= ? and financial_year_end = ?");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
		rs = pst.executeQuery();
		
		while(rs.next()){
			Map<String,String> ERESI=new HashMap<String,String>();
			ERESI.put("ERSI_CONTRIBUTION",rs.getString("ersi_contribution"));
			ERESI.put("MAX_LIMIT",rs.getString("max_limit"));
			ERESI.put("SALARY_HEAD_ID",rs.getString("salary_head_id"));
			ERESIMap.put(rs.getString("org_id")+"_"+rs.getString("state_id"), ERESI);
		}

		rs.close();
		pst.close();
		
	} catch (Exception e) {
		e.printStackTrace();
		
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return ERESIMap;
}
public double calculateERESI(UtilityFunctions uF,Map<String,String> ERESI, Map<String,String> hmTotal){
	
	
	double dblCalculatedAmount = 0;
	
	
	try {
		
			double	dblERESIAmount = uF.parseToDouble(ERESI.get("ERSI_CONTRIBUTION"));
			double	dblESIMaxAmount =uF.parseToDouble(ERESI.get("MAX_LIMIT"));
			String	strSalaryHeads = ERESI.get("SALARY_HEAD_ID");

		String []arrSalaryHeads = null;
		if(strSalaryHeads!=null){
			arrSalaryHeads = strSalaryHeads.split(",");
		}
		
		
		double dblAmount = 0;
		for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
			dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
		}
		
		if(dblAmount<dblESIMaxAmount){
			dblCalculatedAmount = (( dblERESIAmount * dblAmount ) / 100);
		}
		
	} catch (Exception e) {
		e.printStackTrace();
		
	}
	return dblCalculatedAmount;
}
public Map<String,Map<String,String>> getEEESI(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	Map<String,Map<String,String>> EESIMap=new HashMap<String,Map<String,String>>();

	try {
		
		pst = con.prepareStatement("select * from esi_details where financial_year_start= ? and financial_year_end = ?");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
		rs = pst.executeQuery();
		
		
		while(rs.next()){
			Map<String,String> EESI=new HashMap<String,String>();
			EESI.put("EESI_CONTRIBUTION",rs.getString("eesi_contribution"));
			EESI.put("MAX_LIMIT",rs.getString("max_limit"));
			EESI.put("SALARY_HEAD_ID",rs.getString("salary_head_id"));
			EESIMap.put(rs.getString("state_id"), EESI);
			
			
		}
		rs.close();
		pst.close();
	} catch (Exception e) {
		e.printStackTrace();
		
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return EESIMap;
	
}

public double calculateEEESI(UtilityFunctions uF,String strEmpId, Map<String,String> EESI, Map<String,String> hmTotal, Map<String,String> hmVariables){
	
	double dblCalculatedAmount = 0;
	
	try {
		
		double dblEEESIAmount =uF.parseToDouble(EESI.get("EESI_CONTRIBUTION"));
		double dblESIMaxAmount = uF.parseToDouble(EESI.get("MAX_LIMIT"));
		String strSalaryHeads = EESI.get("SALARY_HEAD_ID");

		String []arrSalaryHeads = null;
		if(strSalaryHeads!=null){
			arrSalaryHeads = strSalaryHeads.split(",");
		}
		
		double dblAmount = 0;
		double dblAmountEligibility = 0; 
		for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
			if(uF.parseToInt(arrSalaryHeads[i])!=OVER_TIME && hmVariables!=null && !hmVariables.containsKey(strEmpId+"_"+arrSalaryHeads[i]+"_E")){
				dblAmountEligibility += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));
			}
			dblAmount += uF.parseToDouble((String)hmTotal.get(arrSalaryHeads[i]));	
		}
		
		if(dblAmountEligibility<dblESIMaxAmount){
			dblCalculatedAmount = (( dblEEESIAmount * dblAmount ) / 100);
		}
		
		
	} catch (Exception e) {
		e.printStackTrace();
		
	}
	return dblCalculatedAmount;
	
}


public Map<String,Map<String,String>> getERLWF(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd, String orgId){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	
	Map<String,Map<String,String>> ERLWFMap=new HashMap<String,Map<String,String>>();

	try {
		
		pst = con.prepareStatement("select * from lwf_details where financial_year_start=? and financial_year_end=? and org_id=?");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
		pst.setInt(3, uF.parseToInt(orgId));
		rs = pst.executeQuery();
		while(rs.next()){
			Map<String,String> ERLWF=new HashMap<String,String>();

			ERLWF.put("SALARY_HEAD_ID",rs.getString("salary_head_id"));
			ERLWF.put("MONTHS",rs.getString("months"));
			ERLWFMap.put(rs.getString("state_id"), ERLWF);
		}

		rs.close();
		pst.close();
		
		
		
	} catch (Exception e) {
		e.printStackTrace();
		
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return ERLWFMap;
}

public double calculateERLWF(Connection con, UtilityFunctions uF,Map<String,String> ERLWF, String strFinancialYearStart, String strFinancialYearEnd, Map<String,String> hmTotal, String strWLocationStateId, int nPayMonth, String strOrgId){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	double dblCalculatedAmount = 0;
	
	
	try {
		
		String strSalaryHeads = ERLWF.get("SALARY_HEAD_ID");
		String strMonths = ERLWF.get("MONTHS");

		String []arrMonths = null;
		if(strMonths!=null){
			arrMonths = strMonths.split(",");
		}
		
		if(ArrayUtils.contains(arrMonths, nPayMonth+"")>=0){
			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null){
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			
			double dblAmount = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
			}
			
			
			pst = con.prepareStatement(selectERLWFC);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
			pst.setInt(3, uF.parseToInt(strWLocationStateId));
			pst.setDouble(4, dblAmount);
			pst.setDouble(5, dblAmount);
			pst.setInt(6, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				dblCalculatedAmount = uF.parseToDouble(rs.getString("erlfw_contribution"));
			}
			
			rs.close();
			pst.close();
			
		}
		
		
		
	} catch (Exception e) {
		e.printStackTrace();
		
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return dblCalculatedAmount;
}

public Map<String,Map<String,String>> getEELWF(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd, String orgId){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	Map<String,Map<String,String>> EELWFMap=new HashMap<String,Map<String,String>>();

	try {
		
		pst = con.prepareStatement("select * from lwf_details where financial_year_start=? and financial_year_end=? and org_id=?");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
//		pst.setInt(3, uF.parseToInt(strWLocationStateId));
		pst.setInt(3, uF.parseToInt(orgId));
		rs = pst.executeQuery();
		
//		double dblERLWFAmount = 0;
//		double dblLWFMaxAmount = 0;
//		String strSalaryHeads = null;
//		String strMonths = null;
		while(rs.next()){
			Map<String,String> EELWF=new HashMap<String,String>();
//			ERLWF.put("ERLFW_CONTRIBUTION",rs.getString("eelfw_contribution"));
//			ERLWF.put("MAX_LIMIT",rs.getString("max_limit"));

			EELWF.put("SALARY_HEAD_ID",rs.getString("salary_head_id"));
			EELWF.put("MONTHS",rs.getString("months"));
			EELWFMap.put(rs.getString("state_id"), EELWF);
			
//			dblERLWFAmount = rs.getDouble("eelfw_contribution");
//			dblLWFMaxAmount = rs.getDouble("max_limit");
//			strSalaryHeads = rs.getString("salary_head_id");
//			strMonths  = rs.getString("months");
		}
		rs.close();
		pst.close();
		
		
//		}
		
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return EELWFMap;
}

public double calculateEELWF(Connection con, UtilityFunctions uF,Map<String,String> ERLWF, String strFinancialYearStart, String strFinancialYearEnd, Map<String,String> hmTotal, String strWLocationStateId, int nPayMonth, String strOrgId){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	double dblCalculatedAmount = 0;
	
	
	try {
		
		String strSalaryHeads = ERLWF.get("SALARY_HEAD_ID");
		String strMonths = ERLWF.get("MONTHS");

		String []arrMonths = null;
		if(strMonths!=null){
			arrMonths = strMonths.split(",");
		}
		
		if(ArrayUtils.contains(arrMonths, nPayMonth+"")>=0){
			String []arrSalaryHeads = null;
			if(strSalaryHeads!=null){
				arrSalaryHeads = strSalaryHeads.split(",");
			}
			
			
			double dblAmount = 0;
			for(int i=0; arrSalaryHeads!=null && i<arrSalaryHeads.length; i++){
				dblAmount += uF.parseToDouble(hmTotal.get(arrSalaryHeads[i]));
			}
			
			
			pst = con.prepareStatement(selectERLWFC);
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
			pst.setInt(3, uF.parseToInt(strWLocationStateId));
			pst.setDouble(4, dblAmount);
			pst.setDouble(5, dblAmount);
			pst.setInt(6, uF.parseToInt(strOrgId));
			rs = pst.executeQuery();
			
			while(rs.next()){
				dblCalculatedAmount = uF.parseToDouble(rs.getString("erlfw_contribution"));
			}
			rs.close();
			pst.close();
		}
		
		
		
	} catch (Exception e) {
		e.printStackTrace();
		
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return dblCalculatedAmount;
}

	public double calculateTDS(Connection con, UtilityFunctions uF, double dblGross, double dblCess1, double dblCess2, double dblFlatTDS, double dblDeclaredInvestmentExemption, double dblHRA, double dblBasicDA,
		int nPayMonth, String strPaycycleStart, String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, String strGender, String strAge, String strWLocationStateId,
		Map<String,String> hmEmpExemptionsMap, Map<String,String> hmEmpHomeLoanMap, Map<String,String> hmFixedExemptions, String isMetro, String EmpRentPaid, Map<String,String> hmPaidSalaryDetails,
		Map<String,String> hmTotal, Map<String,String> hmSalaryDetails,Map<String,String> hmEmpLevelMap, CommonFunctions CF,Map<String, String> hmPrevEmpTdsAmount,Map<String, String> hmPrevEmpGrossAmount,Map<String,String> hmEmpIncomeOtherSourcesMap,String strPaycycleEnd){

		PreparedStatement pst = null, pst1 = null;
		ResultSet rst = null, rs = null;
		double dblTDSMonth = 0;
		
		
		try {
			if(hmPaidSalaryDetails==null)hmPaidSalaryDetails=new HashMap<String,String>();
			
			pst = con.prepareStatement("select * from tds_projections where emp_id =? and month=? and fy_year_from=? and fy_year_end=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, nPayMonth);
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DBDATE));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DBDATE));
			rs = pst.executeQuery();
			if(rs.next()){
			dblTDSMonth = rs.getDouble("amount");
			return dblTDSMonth;
			}
			rs.close();
			pst.close();
			
			if(uF.parseToBoolean((String)hmEmpLevelMap.get(strEmpId+"_FLAT_TDS_DEDEC"))){
			
			dblTDSMonth = dblGross * dblFlatTDS / 100;
			//dblTDSMonth += (dblCess1 * 0.01 * dblTDSMonth) + (dblCess2 * 0.01 * dblTDSMonth); 
		
			}else{
			
				String strSlabType = CF.getEmpIncomeTaxSlabType(con, CF, strEmpId, strFinancialYearStart, strFinancialYearEnd);
				int slabType = uF.parseToInt(strSlabType);
				
			pst = con.prepareStatement(selectTDS);
			pst.setInt(1, TDS);
			pst.setInt(2, uF.parseToInt(strEmpId));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DBDATE));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DBDATE));
			rs = pst.executeQuery();
			double dblTDSPaidAmount = 0;
			while(rs.next()){
				dblTDSPaidAmount = rs.getDouble("tds");
			}
			
			rs.close();
			pst.close();
			dblTDSPaidAmount+= uF.parseToDouble(hmPrevEmpTdsAmount.get(strEmpId));
			
			
			//pst = con.prepareStatement("select sum(amount) as amount from payroll_generation pg, salary_details sd where pg.salary_head_id=sd.salary_head_id and emp_id = ? and earning_deduction = 'E' and financial_year_from_date = ? and financial_year_to_date = ?");
			pst = con.prepareStatement("select sum(amount) as amount from payroll_generation where emp_id = ? and earning_deduction = 'E' and financial_year_from_date = ? and financial_year_to_date = ? and salary_head_id not in ("+REIMBURSEMENT+","+OTHER_REIMBURSEMENT+","+MOBILE_REIMBURSEMENT+","+TRAVEL_REIMBURSEMENT+","+SERVICE_TAX+","+SWACHHA_BHARAT_CESS+","+KRISHI_KALYAN_CESS+")");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DBDATE));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DBDATE));
			
			rs = pst.executeQuery(); 
			
			double dblGrossPaidAmount = 0;
			while(rs.next()){
				dblGrossPaidAmount = rs.getDouble("amount");
			}
			dblGrossPaidAmount+= uF.parseToDouble(hmPrevEmpGrossAmount.get(strEmpId));
			rs.close();
			pst.close();
			
			String strMonthsLeft = uF.dateDifference(strPaycycleStart, DBDATE, strFinancialYearEnd, DBDATE,CF.getStrTimeZone());
			int nMonthsLeft = (int) Math.round(uF.parseToInt(strMonthsLeft) / 30);
			
			
			double dblInvestment = uF.parseToDouble((String)hmEmpExemptionsMap.get(strEmpId));
			double dblHomeLoanExemtion = uF.parseToDouble((String)hmEmpHomeLoanMap.get(strEmpId));
			
			
			double dblEEEPFPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(EMPLOYEE_EPF+""));
			double dblVOLEEPFPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(VOLUNTARY_EPF+""));
			double dblEEEPFToBePaid = nMonthsLeft * uF.parseToDouble((String)hmTotal.get(EMPLOYEE_EPF+""));
			
			double dblEREPFPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(EMPLOYER_EPF+""));
			double dblEREPFToBePaid = nMonthsLeft * uF.parseToDouble((String)hmTotal.get(EMPLOYER_EPF+""));
			
			double dbl80CC_New = 0;
			double dbl80CC_Old = uF.parseToDouble((String)hmEmpExemptionsMap.get(strEmpId+"_3")); 
			dbl80CC_New = dbl80CC_Old +  dblEEEPFPaid + dblVOLEEPFPaid + dblEEEPFToBePaid;
			
			if(dbl80CC_New>=dblDeclaredInvestmentExemption){
				dbl80CC_New = dblDeclaredInvestmentExemption;
			}
			
			double dblTotalInvestment = dblInvestment - dbl80CC_Old + dbl80CC_New;
			
			//double dblTotalInvestment = dblInvestment + dblEEEPFPaid + dblEEEPFToBePaid+ dblEREPFPaid + dblEREPFToBePaid;
			//double dblTotalInvestment = dblInvestment + dblEREPFPaid + dblEREPFToBePaid;
			//double dblTotalInvestment = dblInvestment + dblEEEPFPaid + dblVOLEEPFPaid + dblEEEPFToBePaid;
			
			double dblHRAExemptions = getHRAExemptionCalculation(con, uF, hmPaidSalaryDetails, strFinancialYearStart, strFinancialYearEnd, strEmpId, dblHRA, dblBasicDA, isMetro, EmpRentPaid,strPaycycleStart,strPaycycleEnd);
			
			double dblExemptions = dblHomeLoanExemtion + dblTotalInvestment + dblHRAExemptions; 
			
			
			Set set = hmSalaryDetails.keySet();
			Iterator it = set.iterator();
			while(it.hasNext()){
				String strSalaryHeadId = (String)it.next();
				String strSalaryHeadName = (String)hmSalaryDetails.get(strSalaryHeadId);
				
				
				if(hmFixedExemptions.containsKey(strSalaryHeadName)){
					
					double dblIndividualExemption = uF.parseToDouble((String)hmFixedExemptions.get(strSalaryHeadName));
					
					double dblTotalToBePaid = 0;
					if(uF.parseToInt(strSalaryHeadId) == PROFESSIONAL_TAX){
						int nLastPayMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DBDATE, "MM"));
						double dblCurrentMonthGross = uF.parseToDouble((String)hmTotal.get("GROSS"));
						dblTotalToBePaid = (nMonthsLeft-1) * uF.parseToDouble((String)hmTotal.get(strSalaryHeadId));
						dblTotalToBePaid += calculateProfessionalTax(con, uF, dblCurrentMonthGross, strFinancialYearEnd, nLastPayMonth, strWLocationStateId);
					}else{
						dblTotalToBePaid = (nMonthsLeft) * uF.parseToDouble((String)hmTotal.get(strSalaryHeadId));
					}
					
					double dblTotalPaid = uF.parseToDouble((String)hmPaidSalaryDetails.get(strSalaryHeadId));
					double dblTotalPaidAmount = dblTotalToBePaid + dblTotalPaid;  
					double dblExmp = 0;
					if(dblTotalPaidAmount >= dblIndividualExemption){
						dblExemptions += dblIndividualExemption;
						dblExmp = dblIndividualExemption;
					}else{
						dblExemptions += dblTotalPaidAmount;
						dblExmp = dblTotalPaidAmount;
					}
					
				}
			}
			
			//double dblTotalGrossSalary = dblGrossPaidAmount + ((nMonthsLeft) * dblGross); 
			double dblTotalGrossSalary = dblGrossPaidAmount + ((nMonthsLeft) * dblGross) + uF.parseToDouble(""+hmEmpIncomeOtherSourcesMap.get(strEmpId)); 
			
			double dblTotalTaxableSalary = 0;
			if(dblTotalGrossSalary>dblExemptions){
				dblTotalTaxableSalary = dblTotalGrossSalary - dblExemptions;
			}else if(dblTotalGrossSalary>0 && dblExemptions>0 && dblTotalGrossSalary<=dblExemptions){
				dblTotalTaxableSalary = 0;
			}
			
			int countBug = 0;
			double dblTotalTDSPayable = 0.0d;
			double dblUpperDeductionSlabLimit = 0;
			double dblLowerDeductionSlabLimit = 0;
			double dblTotalNetTaxableSalary = 0; 
				
			do{
				
				pst = con.prepareStatement(selectDeduction);
				pst.setDouble(1, uF.parseToDouble(strAge));
				pst.setDouble(2, uF.parseToDouble(strAge));
				pst.setString(3, strGender);
				pst.setDate(4, uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(CF.getStrFinancialYearTo(), DATE_FORMAT));
				pst.setDouble(6, dblTotalTaxableSalary);
				pst.setDouble(7, dblUpperDeductionSlabLimit);
				pst.setInt(8, slabType);
				rs = pst.executeQuery();
				
				double dblDeductionAmount = 0;
				String strDeductionType = null;
				
				if(rs.next()){
					dblDeductionAmount = rs.getDouble("deduction_amount");
					strDeductionType = rs.getString("deduction_type");
					dblUpperDeductionSlabLimit = rs.getDouble("_to");
					dblLowerDeductionSlabLimit = rs.getDouble("_from");
				}
				rs.close();
				pst.close();
				
				if(countBug==0){
					dblTotalNetTaxableSalary = dblTotalTaxableSalary;
				}
				
				if(dblTotalTaxableSalary>=dblUpperDeductionSlabLimit){
					dblTotalTDSPayable += ((dblDeductionAmount /100) *  (dblUpperDeductionSlabLimit - dblLowerDeductionSlabLimit) );
					
			//		log.debug("dblTotalTaxableSalary 1 ="+((dblDeductionAmount /100) *  dblUpperDeductionSlabLimit ));
					
				}else{
					
					if(countBug==0){
						dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblLowerDeductionSlabLimit;
					}
					
					dblTotalTDSPayable += ((dblDeductionAmount /100) *  dblTotalNetTaxableSalary );
				}
				
				dblTotalNetTaxableSalary = dblTotalTaxableSalary - dblUpperDeductionSlabLimit;
				
				if(countBug==15)break;		// in case of any bug, this condition is used to avoid any stoppage 
				countBug++;
				
			}while(dblTotalNetTaxableSalary>0);
			
			// Service tax + Education cess
			
			
			double dblRebate = 0;
			if(dblTotalTaxableSalary<500000){
				if(dblTotalTDSPayable>=2000){
					dblRebate = 2000;
				}else if(dblTotalTDSPayable>0 && dblTotalTDSPayable<2000){
					dblRebate = dblTotalTDSPayable;
				}
			}
			dblTotalTDSPayable = dblTotalTDSPayable - dblRebate;
			
			
			
			double dblCess = dblTotalTDSPayable * ( dblCess1/100);
			dblCess += dblTotalTDSPayable * ( dblCess2/100);
			
			dblTotalTDSPayable += dblCess;   
			
			
			
			dblTDSMonth = dblTotalTDSPayable - dblTDSPaidAmount;
			dblTDSMonth = dblTDSMonth/(nMonthsLeft);
			
			if(dblTDSMonth<0){
				dblTDSMonth = 0;
			}
			
			}
		
		} catch (Exception e) {
		e.printStackTrace();
		}finally {
			if(rs != null) {
				try {
					rs.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if(pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		return dblTDSMonth;
	}
	
	
public double getHRAExemptionCalculation(Connection con, UtilityFunctions uF, Map hmPaidSalaryDetails, String strFinancialYearStart, String strFinancialYearEnd, String strEmpId, double dblHRA, double dblBasicDA, String strIsMetro, String EmpRentPaid,String strPaycycleEnd,String strPaycycleStart){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	double dblHRAExemption = 0;
	
	try {
		
		boolean isMetro = uF.parseToBoolean(strIsMetro);
		
		String strBasicPaidAmount = (String)hmPaidSalaryDetails.get(BASIC+"");
		String strHRAPaidAmount = (String)hmPaidSalaryDetails.get(HRA+"");
		
		
		
		
		
		String strMonthsLeft = uF.dateDifference(strPaycycleStart, DBDATE, strFinancialYearEnd, DBDATE);
		int nMonthsLeft = uF.parseToInt(strMonthsLeft) / 30;
		
		double dblBasicToBePaidAmount = nMonthsLeft * dblBasicDA;
		double dblHRAToBePaidAmount = nMonthsLeft * dblHRA;
		
		
		double dblTotalBasicDAAmount = uF.parseToDouble(strBasicPaidAmount) + dblBasicToBePaidAmount;
		double dblTotalHRAAmount = uF.parseToDouble(strHRAPaidAmount) + dblHRAToBePaidAmount;
		
		double dblTotalRentPaid = uF.parseToDouble(EmpRentPaid);
		
		
		
		pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from = ? and financial_year_to =? ");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
		rs = pst.executeQuery();
		
		double dblCondition1= 0;
		double dblCondition2= 0;
		double dblCondition3= 0;
		
		while (rs.next()) {
			dblCondition1= rs.getDouble("condition1");
			dblCondition2= rs.getDouble("condition2");
			dblCondition3= rs.getDouble("condition3");
		}
		rs.close();
		pst.close();
//		double dblRentPaidGreaterThanCondition1 = dblCondition1 * dblTotalBasicDAAmount /100;
		double dblRentPaidGreaterThanCondition1 = 0;
		
		
		
		if(dblTotalRentPaid>dblRentPaidGreaterThanCondition1){
			
			dblRentPaidGreaterThanCondition1 = dblCondition1 * dblTotalBasicDAAmount /100;
			
			
			
			dblRentPaidGreaterThanCondition1 = dblTotalRentPaid - dblRentPaidGreaterThanCondition1;
			
			
		}else if(dblTotalRentPaid>0){
			dblRentPaidGreaterThanCondition1 = dblCondition1 * dblTotalBasicDAAmount /100;
			
			
		}
		
		
		double dblRentPaidCondition23 = 0;
		
		if(isMetro){
			dblRentPaidCondition23 = dblCondition2 * dblTotalBasicDAAmount /100;
		}else{
			dblRentPaidCondition23 = dblCondition3 * dblTotalBasicDAAmount /100;
		}
		
		dblHRAExemption = Math.min(dblTotalHRAAmount, dblRentPaidGreaterThanCondition1);
		dblHRAExemption = Math.min(dblHRAExemption, dblRentPaidCondition23);
		
		
		
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return dblHRAExemption;

}

public double calculateProfessionalTax(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearEnd, int nPayMonth, String strWLocationStateId){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	double dblDeductionPayMonth = 0;
	
	
	try {
		
		pst = con.prepareStatement("select * from deduction_details_india where income_from<= ? and income_to>= ? and state_id=? and financial_year_from = (select max(financial_year_from) from deduction_details_india) limit 1");
		
		pst.setDouble(1, dblGross);
		pst.setDouble(2, dblGross);
		pst.setInt(3, uF.parseToInt(strWLocationStateId));
		
		rs = pst.executeQuery();
		
		
		
		
		double dblDeductionAmount = 0;
		double dblDeductionPaycycleAmount = 0;
		while(rs.next()){
			dblDeductionAmount = rs.getDouble("deduction_amount");
			dblDeductionPaycycleAmount = rs.getDouble("deduction_paycycle");
		}
//		nPayMonth = uF.parseToInt(uF.getDateFormat(strPaycycleEnd, DATE_FORMAT, "MM"));
		
		int nFinancialYearEndMonth = uF.parseToInt(uF.getDateFormat(strFinancialYearEnd, DBDATE, "MM"));
		nFinancialYearEndMonth = nFinancialYearEndMonth - 1;

		if(nFinancialYearEndMonth==nPayMonth){
			dblDeductionPayMonth = dblDeductionAmount - (11*dblDeductionPaycycleAmount);
		}else{
			dblDeductionPayMonth = dblDeductionPaycycleAmount;
		}

		
		rs.close();
		pst.close();
		
		
		
	} catch (Exception e) {
		e.printStackTrace();
		
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return dblDeductionPayMonth;
	
}
public Map<String, Map<String, String>> getArearDetails(Connection con, UtilityFunctions uF, CommonFunctions CF, String strD2,String empIds) {

	PreparedStatement pst = null;
	ResultSet rs = null;		
	Map<String, Map<String, String>> hmArearAmountMap = new HashMap<String, Map<String, String>>();

	try {
		if(empIds!=null)
		pst = con.prepareStatement("select * from arear_details where effective_date <= ? and is_paid = false and emp_id in("+empIds+")");
		else
			pst = con.prepareStatement("select * from arear_details where effective_date <= ? and is_paid = false");

		pst.setDate(1, uF.getDateFormat(strD2, DBDATE));
		rs = pst.executeQuery();

		while (rs.next()) {
			Map<String, String> hmInner = new HashMap<String, String>();

			hmInner.put("AMOUNT_PAID", rs.getString("total_amount_paid"));
			hmInner.put("AMOUNT_BALANCE", rs.getString("arear_amount_balance"));
			hmInner.put("TOTAL_AMOUNT", rs.getString("arear_amount"));
			hmInner.put("DURATION", rs.getString("duration_months"));
			hmInner.put("MONTHLY_AREAR", rs.getString("monthly_arear"));
			hmInner.put("AREAR_ID", rs.getString("arear_id"));

			hmArearAmountMap.put(rs.getString("emp_id"), hmInner);

		}
		
		rs.close();
		pst.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return hmArearAmountMap;
}

public Map<String, String> getLICDetails(Connection con,String empIds) {

	PreparedStatement pst = null;
	ResultSet rs = null;		
	 Map<String, String> hmLICAmountMap = new HashMap<String, String>();

	try {
		if(empIds!=null)
		pst = con.prepareStatement("select sum(amount) as amount,emp_id from lic_details where status=1 and emp_id in("+empIds+") group by emp_id");
		else
			pst = con.prepareStatement("select sum(amount) as amount,emp_id from lic_details where status=1 group by emp_id");

		rs = pst.executeQuery();

		while (rs.next()) {

			hmLICAmountMap.put( rs.getString("emp_id"), rs.getString("amount"));

		}

		rs.close();
		pst.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return hmLICAmountMap;
}
public Map<String, String> getOUTDOORLoanDetails(Connection con,String empIds) {

	PreparedStatement pst = null;
	ResultSet rs = null;		
	 Map<String, String> hmLICAmountMap = new HashMap<String, String>();

	try {
		if(empIds!=null)
		pst = con.prepareStatement("select sum(amount) as amount,emp_id from outdoorloan_details where status=true and emp_id in("+empIds+") group by emp_id");
		else
			pst = con.prepareStatement("select sum(amount) as amount,emp_id from outdoorloan_details where status=true group by emp_id");

		rs = pst.executeQuery();

		while (rs.next()) {

			hmLICAmountMap.put( rs.getString("emp_id"), rs.getString("amount"));

		}
		
		rs.close();
		pst.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return hmLICAmountMap;
}

public Map<String, String> getEmpLevelMap(Connection con,String empIds) {

	PreparedStatement pst = null;
	ResultSet rs = null;
	Map<String, String> hmEmpLevelMap = new HashMap<String, String>();
	
	try {
		
		if(empIds!=null)
			pst = con.prepareStatement("select emp_id,ld.level_id,standard_working_hours,standard_overtime_hours,flat_deduction,ld.salary_cal_basis from level_details ld right join (select * from designation_details dd right join (select *, gd.designation_id as designationid from employee_official_details eod, grades_details gd where gd.grade_id=eod.grade_id) a on a.designationid=dd.designation_id) a on a.level_id=ld.level_id and emp_id in("+empIds+")");
		else
			pst = con.prepareStatement("select emp_id,ld.level_id,standard_working_hours,standard_overtime_hours,flat_deduction,ld.salary_cal_basis from level_details ld right join (select * from designation_details dd right join (select *, gd.designation_id as designationid from employee_official_details eod, grades_details gd where gd.grade_id=eod.grade_id) a on a.designationid=dd.designation_id) a on a.level_id=ld.level_id");

		rs = pst.executeQuery();

		while (rs.next()) {
			hmEmpLevelMap.put(rs.getString("emp_id"), rs.getString("level_id"));
			hmEmpLevelMap.put(rs.getString("emp_id") + "_SWH", rs.getString("standard_working_hours"));
			hmEmpLevelMap.put(rs.getString("emp_id") + "_SOH", rs.getString("standard_overtime_hours"));
			hmEmpLevelMap.put(rs.getString("emp_id") + "_FLAT_TDS_DEDEC", rs.getString("flat_deduction"));
			hmEmpLevelMap.put(rs.getString("emp_id") + "_SALARY_CAL_BASIS", rs.getString("salary_cal_basis"));
		}

		rs.close();
		pst.close();
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return hmEmpLevelMap;
}

public Map<String,String> getEmpInvestmentExemptions(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd, double dblDeclaredInvestmentExemption,String empIds){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	Map<String,String> hmEmpExemptionsMap = new HashMap<String,String>();
	
	try {
		
		Map<String, String> hmSectionLimitA = new HashMap<String, String>();
		Map<String, String> hmSectionLimitP = new HashMap<String, String>();
		
//		Map<String, String> hmSectionLimitEmp = new HashMap<String, String>();
		
		//pst = con.prepareStatement(selectSection);
		pst = con.prepareStatement("SELECT * FROM section_details where isdisplay=true order by section_code");
		rs = pst.executeQuery();
		
		
		while (rs.next()) {
			
			if(rs.getString("section_limit_type").equalsIgnoreCase("A")){
				hmSectionLimitA.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
			}else{
				hmSectionLimitP.put(rs.getString("section_id"), rs.getString("section_exemption_limit"));
			}
		}
		
		rs.close();
		pst.close();
		
		if(empIds!=null)
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd  where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = true and status = true and section_code not in ('HRA') and sd.section_id not in (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') and isdisplay=true and emp_id in("+empIds+") group by emp_id, sd.section_id order by emp_id ");
		else
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd  where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = true and status = true and section_code not in ('HRA') and sd.section_id not in (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') and isdisplay=true group by emp_id, sd.section_id order by emp_id ");

		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
		
		rs = pst.executeQuery();
		
		
		double dblInvestmentLimit = 0;
		double dblInvestmentEmp = 0;
		
		while (rs.next()) {

			
			String strSectionId = rs.getString("section_id");
			double dblInvestment = rs.getDouble("amount_paid");
			
			
			
			if(hmSectionLimitA.containsKey(strSectionId)){
				dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitA.get(strSectionId));
			}else{
				dblInvestmentLimit = uF.parseToDouble((String)hmSectionLimitP.get(strSectionId));
				dblInvestmentLimit = dblInvestment * dblInvestmentLimit / 100;
			}
			
			

			
			if(uF.parseToInt(strSectionId)==3){
				hmEmpExemptionsMap.put(rs.getString("emp_id")+"_"+strSectionId, dblInvestment+"");
			}
			
			
			
			if(dblInvestment>=dblInvestmentLimit){
				dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsMap.get(rs.getString("emp_id"))) + dblInvestmentLimit; 
				hmEmpExemptionsMap.put(rs.getString("emp_id"), dblInvestmentEmp+"");
			}else{
				dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsMap.get(rs.getString("emp_id"))) + dblInvestment;
				hmEmpExemptionsMap.put(rs.getString("emp_id"), dblInvestmentEmp+"");
			}
			
		}
		
		rs.close();
		pst.close();
		
		
		
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return hmEmpExemptionsMap;

}
public Map<String,String> getEmpHomeLoanExemptions(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd,String empIds){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	Map<String,String> hmEmpHomeLoanMap = new HashMap<String,String>();
	
	try {
		
		pst = con.prepareStatement("select * from section_details where section_id = (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') and isdisplay=true");
		rs = pst.executeQuery();
		double dblLoanExemptionLimit = 0;
		while (rs.next()) {
			dblLoanExemptionLimit = rs.getDouble("section_exemption_limit");
		}
		rs.close();
		pst.close();
		
		if(empIds!=null)
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from = ? and fy_to = ? and status = true and  section_id = (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%' and isdisplay=true)  group by emp_id");
		else
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details where fy_from = ? and fy_to = ? and status = true and  section_id = (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%' and isdisplay=true)  group by emp_id");

		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
		rs = pst.executeQuery();
		while (rs.next()) {
			
			if(uF.parseToDouble(rs.getString("amount_paid"))>dblLoanExemptionLimit){
				hmEmpHomeLoanMap.put(rs.getString("emp_id"), dblLoanExemptionLimit+"");
			}else{
				hmEmpHomeLoanMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
			}
		}
		rs.close();
		pst.close();
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return hmEmpHomeLoanMap;

}
public Map<String,String> getFixedExemption(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	Map<String,String> hmFixedExemptions = new HashMap<String,String>();
	
	try {
		
		pst = con.prepareStatement("select * from exemption_details where exemption_from = ? and exemption_to =? ");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
		rs = pst.executeQuery();
		
		
		while (rs.next()) {
			hmFixedExemptions.put(rs.getString("exemption_name"), rs.getString("exemption_limit"));
		}
		rs.close();
		pst.close();
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return hmFixedExemptions;

}

public Map<String,String> getEmpRentPaid(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd,String empIds){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	Map<String,String> hmEmpRentPaidMap = new HashMap<String,String>();
	
	try {
		if(empIds!=null)
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, section_details sd  where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and agreed_date between ? and ? and section_code in ('HRA') and isdisplay=true and status=true and emp_id in("+empIds+") group by emp_id ");
		else
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, emp_id from investment_details id, section_details sd  where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and agreed_date between ? and ? and section_code in ('HRA') and isdisplay=true and status=true group by emp_id ");

		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DBDATE));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DBDATE));
		
		rs = pst.executeQuery();          
		while (rs.next()) {
			hmEmpRentPaidMap.put(rs.getString("emp_id"), rs.getString("amount_paid"));
		}
		rs.close();
		pst.close();
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return hmEmpRentPaidMap;

}
public Map<String,String> getEmpIncomeOtherSources(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd,String empIds) {
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	Map<String,String> hmEmpExemptionsMap = new HashMap<String,String>();
	
	try {
		if(empIds!=null)
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd  where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = true and status = true and section_code not in ('HRA') and sd.section_id not in (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') and isdisplay=false and emp_id in("+empIds+") group by emp_id, sd.section_id order by emp_id ");
		else 
			pst = con.prepareStatement("select sum(amount_paid) as amount_paid, sd.section_id, emp_id from investment_details id, section_details sd  where sd.section_id=id.section_id and id.fy_from = ? and id.fy_to = ? and trail_status = true and status = true and section_code not in ('HRA') and sd.section_id not in (select section_id from section_details where upper(section_code) like '%HOME% %INTEREST%') and isdisplay=false group by emp_id, sd.section_id order by emp_id ");

		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
		rs = pst.executeQuery();
//		double dblInvestmentLimit = 0;
		double dblInvestmentEmp = 0;
		while (rs.next()) {
//			String strSectionId = rs.getString("section_id");
			double dblInvestment = rs.getDouble("amount_paid");
			
			dblInvestmentEmp = uF.parseToDouble(hmEmpExemptionsMap.get(rs.getString("emp_id"))) + dblInvestment;
			hmEmpExemptionsMap.put(rs.getString("emp_id"), dblInvestmentEmp+"");
		}
		rs.close();
		pst.close();
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return hmEmpExemptionsMap;

}

public Map<String, String> getPrevEmpTdsAmount(Connection con, UtilityFunctions uF,String strFinancialYearStart, String strFinancialYearEnd,Map<String, String> hmPrevEmpGrossAmount,String empIds) {
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	Map<String, String> hmPrevEmpTdsAmount=new HashMap<String, String>();
	try {
		if(empIds!=null)
			pst = con.prepareStatement("select * from prev_earn_deduct_details where financial_start=? and financial_end=? and emp_id in("+empIds+")");
		else 
			pst = con.prepareStatement("select * from prev_earn_deduct_details where financial_start=? and financial_end=?");

		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
		rs = pst.executeQuery();
		if(rs.next()){
			double dblTDSAmt = rs.getDouble("tds_amount");
			double dblGrossAmt = rs.getDouble("gross_amount");
			
			hmPrevEmpGrossAmount.put(rs.getString("emp_id"), ""+dblGrossAmt);
			hmPrevEmpTdsAmount.put(rs.getString("emp_id"), ""+dblTDSAmt);
		}
		rs.close();
		pst.close();
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	return hmPrevEmpTdsAmount;
}
public Map<String, Map<String, String>> getEmpPaidAmountDetails(Connection con, UtilityFunctions uF, String strFinancialYearStart, String strFinancialYearEnd){
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	Map<String, Map<String, String>> hmEmpPaidAmountDetails = new HashMap<String, Map<String, String>>();
	
	try {
		pst = con.prepareStatement("select sum(amount) as amount, emp_id, salary_head_id from payroll_generation where financial_year_from_date = ? and financial_year_to_date = ? group by emp_id, salary_head_id order by emp_id");
		pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DBDATE));
		pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DBDATE));
		rs = pst.executeQuery();
		
		
		String strEmpIdNew = null;
		String strEmpIdOld = null;
		Map<String, String> hmInner = new HashMap<String, String>();
		while (rs.next()) {
			strEmpIdNew = rs.getString("emp_id");
			
			
			if(strEmpIdNew !=null && !strEmpIdNew.equalsIgnoreCase(strEmpIdOld)){
				hmInner = new HashMap<String, String>();
			}
			hmInner.put(rs.getString("salary_head_id"), rs.getString("amount"));
			
			hmEmpPaidAmountDetails.put(rs.getString("emp_id"), hmInner);
			
			strEmpIdOld  = strEmpIdNew;
			
		}
		rs.close();
		pst.close();
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return hmEmpPaidAmountDetails;

}
public Map<String, String> getEmpServiceTax(Connection con, UtilityFunctions uF, CommonFunctions CF,String empIds) {

	PreparedStatement pst = null;
	ResultSet rs = null;		
	Map<String, String> hmEmpServiceTaxMap = new HashMap<String, String>();

	try {
		if(empIds!=null)
		pst = con.prepareStatement("select is_service_tax, emp_id from employee_official_details where is_service_tax = true and emp_id in("+empIds+")");
		else
		pst = con.prepareStatement("select is_service_tax, emp_id from employee_official_details where is_service_tax = true");

		rs = pst.executeQuery();

		while (rs.next()) {
			hmEmpServiceTaxMap.put(rs.getString("emp_id"), rs.getString("is_service_tax"));
		}
		rs.close();
		pst.close();
	} catch (Exception e) {
		e.printStackTrace();
	}finally {
		if(rs != null) {
			try {
				rs.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		if(pst != null) {
			try {
				pst.close();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
	}
	return hmEmpServiceTaxMap;
}
}

