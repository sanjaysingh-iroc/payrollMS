package com.konnect.jpms.offboarding;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.payroll.ApprovePayroll;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OffboardSalaryPreview extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	HttpSession session;
	HttpServletResponse response;
	String strSessionUserType;
	CommonFunctions CF;
	String emp_id;
	String payCycle;
	int resignId;

	public int getResignId() {
		return resignId;
	}

	public void setResignId(int resignId) {
		this.resignId = resignId;
	}

	public String execute() {

		session = request.getSession();
		if (session == null)
			return LOGIN;
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		strSessionUserType = (String) session.getAttribute(USERTYPE);
		
//		System.out.println("getPayCycle() ===>>> " + getPayCycle());
		
		if (getPayCycle() != null){
			viewSalaryDetails(getEmp_id(), getPayCycle());
		}else {
			fullSalaryPreview(getEmp_id(), CF);
			return "sattlement";
		}
		return "success";

	}
	
	public void fullSalaryPreview(String strEmpId, CommonFunctions CF) {
		System.out.println("fullSalaryPreview method call....");

		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmOrgDetails = CF.getOrgDetails(uF, (String) session.getAttribute(ORGID), request);//Created By Dattatray Date:09-12-21
			Map<String, String> hmCurrencyDetails = CF.getCurrencyDetailsById(con, uF, hmOrgDetails.get("ORG_CURRENCY"));//Created By Dattatray Date:09-12-21
			request.setAttribute("currencyTag", hmCurrencyDetails.get("SHORT_CURR"));//Created By Dattatray Date:09-12-21
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select emp_id,off_board_type,entry_date,approved_1_by,emp_fname,emp_mname, emp_lname,emp_reason,approved_1_reason," +
			"approved_2_reason,last_day_date,approved_1_date from emp_off_board eob,employee_personal_details epd where epd.emp_per_id = eob.approved_1_by and approved_1=? and approved_2=? and emp_id=?");
			pst.setInt(1, 1);
			pst.setInt(2, 1);
			pst.setInt(3, uF.parseToInt(strEmpId));
//			System.out.println("1 pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> empMap = new HashMap<String, String>();
			while (rs.next()) {
				empMap.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat()));
				empMap.put("LAST_DAY_DATE", uF.getDateFormat(rs.getString("last_day_date"), DBDATE, CF.getStrReportDateFormat()));
				empMap.put("OFF_BOARD_TYPE", rs.getString("off_board_type"));
				empMap.put("EMP_ID", rs.getString("emp_id"));
				empMap.put("EMP_RESIGN_REASON", uF.parseToHTML(rs.getString("emp_reason")));
			
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				empMap.put("ACCEPTED_BY", rs.getString("emp_fname") + strEmpMName+" " + rs.getString("emp_lname"));
				empMap.put("ACCEPTED_DATE", uF.getDateFormat(rs.getString("approved_1_date"), DBDATE, CF.getStrReportDateFormat()));
				empMap.put("MANAGER_APPROVE_REASON", rs.getString("approved_1_reason"));
				empMap.put("HR_MANAGER_APPROVE_REASON", rs.getString("approved_2_reason"));
			}
			rs.close();
			pst.close();
			
			viewProfile(con, strEmpId);
			request.setAttribute("empDetailsMp", empMap);
			
			Map<String, String> hmSalaryDetails = new LinkedHashMap<String, String>();
			List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
			List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();

			Map<String, Double> hmSalaryAmt = new HashMap<String, Double>();

//			pst = con.prepareStatement("select sum(amount) as amount,salary_head_id from payroll_generation where emp_id=? and (is_paid=false or(is_paid=true and is_fullfinal=true)) group by salary_head_id");
			pst = con.prepareStatement("select sum(amount) as amount,salary_head_id from payroll_generation where emp_id=? and is_paid=true and is_fullfinal=true group by salary_head_id");
			pst.setInt(1, uF.parseToInt(strEmpId));
//			System.out.println("3 pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				//Created by Dattatray Date:08-12-21
				if(rs.getDouble("amount") > 0) {
					hmSalaryAmt.put(rs.getString("salary_head_id"), rs.getDouble("amount"));
				}
			}
			rs.close();
			pst.close();
//			System.out.println("hmSalaryAmt ===>> " + hmSalaryAmt);
			request.setAttribute("hmSalaryAmt", hmSalaryAmt);

			
			pst = con.prepareStatement("select * from emp_salary_details esd,salary_details sd  where sd.salary_head_id=esd.salary_head_id and (is_contribution is null or is_contribution=false) and emp_id=? ");
			pst.setInt(1, uF.parseToInt(strEmpId));
			System.out.println("2 pst ===>> " + pst);
			rs = pst.executeQuery();

			List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
			List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();

			while (rs.next()) {
				//Created by Dattatray Date:08-12-21
				if(hmSalaryAmt.containsKey(rs.getString("salary_head_id"))) {
					if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
						int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
	
						if (index >= 0) {
							alEmpSalaryDetailsEarning.remove(index);
							alEarningSalaryDuplicationTracer.remove(index);
							// alEmpSalaryDetailsEarning.add(index,
							// rs.getString("salary_head_id"));
							alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
						} else {
							alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
						}
						alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
						
					} else if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("D")) {
						int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
						if (index >= 0) {
							alEmpSalaryDetailsDeduction.remove(index);
							alDeductionSalaryDuplicationTracer.remove(index);
							// alEmpSalaryDetailsDeduction.add(index,
							// rs.getString("salary_head_id"));
							alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
						} else {
							alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
						}
						alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					}
	
					hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
				}
			}
			rs.close();
			pst.close();

//			System.out.println("alEmpSalaryDetailsEarning ===>> " + alEmpSalaryDetailsEarning);
//			System.out.println("alEmpSalaryDetailsDeduction ===>> " + alEmpSalaryDetailsDeduction);
//			System.out.println("alEarningSalaryDuplicationTracer ===>> " + alEarningSalaryDuplicationTracer);
//			System.out.println("alEarningSalaryDuplicationTracer ===>> " + alEarningSalaryDuplicationTracer);
//			System.out.println("hmSalaryDetails ===>> " + hmSalaryDetails);
			
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			
			request.setAttribute("alDeductionSalaryDuplicationTracer", alDeductionSalaryDuplicationTracer);
			request.setAttribute("alDeductionSalaryDuplicationTracer", alDeductionSalaryDuplicationTracer);
			
			request.setAttribute("alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
			request.setAttribute("alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);



			String last_attendace = null;
			pst = con.prepareStatement("select max(to_date(in_out_timestamp::text,'yyyy-MM-dd'))as last_working_attendance from attendance_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
//			System.out.println("4 pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				last_attendace = rs.getString("last_working_attendance");
			}
			rs.close();
			pst.close();

			String last_payroll_date = null;
			pst = con.prepareStatement("select max(paid_to) as paid_to from payroll_generation  where emp_id=? and is_paid=true and (is_fullfinal is null or is_fullfinal=false)");
			pst.setInt(1, uF.parseToInt(strEmpId));
//			System.out.println("5 pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				last_payroll_date = rs.getString("paid_to");
			}
			rs.close();
			pst.close();

			if (last_payroll_date == null) {

				pst = con.prepareStatement("select joining_date from employee_personal_details where emp_per_id=?");
				pst.setInt(1, uF.parseToInt(strEmpId));
//				System.out.println("6 pst ===>> " + pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					last_payroll_date = rs.getString("joining_date");
				}
				rs.close();
				pst.close();

			}

			last_payroll_date = uF.getDateFormat(last_payroll_date, DBDATE, DATE_FORMAT);
			String last_date = last_payroll_date;
			DateFormat format = new SimpleDateFormat(DATE_FORMAT);
			
			Date date = uF.getDateFormat(last_payroll_date, DATE_FORMAT);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DATE, 1);
			
			StringBuilder Months = new StringBuilder();
//			System.out.println("last_payroll_date=====>"+last_payroll_date);
//			System.out.println("last_attendace=====>"+last_attendace);
//			System.out.println("uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE)=====>"+uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE));
//			System.out.println("uF.parseToInt(uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE))=====>"+uF.parseToInt(uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE)));
			while (uF.parseToInt(uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE,CF.getStrTimeZone())) > 1) {
				Months.append(uF.getMonth(cal.get(Calendar.MONTH) + 1) + ",");
				cal.add(Calendar.MONTH, 1);
				date = cal.getTime();
				last_payroll_date = format.format(date);
//				System.out.println("1last_payroll_date=====>"+last_payroll_date);
//				System.out.println("1last_attendace=====>"+last_attendace);
//				System.out.println("1uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE)=====>"+uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE));
//				System.out.println("1uF.parseToInt(uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE))=====>"+uF.parseToInt(uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE)));
			}
//			System.out.println("Months.toString()=====>"+Months.toString());
			empMap.put("MONTHS", Months.toString());
			
			String totalWorkingDays = null;
			pst = con.prepareStatement("select count(*) as count_days from(select to_date(in_out_timestamp::text,'yyyy-MM-dd') from attendance_details where to_date(in_out_timestamp::text,'yyyy-MM-dd')>? and emp_id=? group by to_date(in_out_timestamp::text,'yyyy-MM-dd'))as a");
			pst.setDate(1, uF.getDateFormat(last_date, DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(strEmpId));
//			System.out.println("7 pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				totalWorkingDays = rs.getString("count_days");
			}
			rs.close();
			pst.close();

			empMap.put("TOTALWORKINGDAYS", uF.showData(totalWorkingDays, ""));
			String reason = null;
			String notice = null;

			pst = con.prepareStatement("select emp_reason,notice_days from emp_off_board where emp_id=? and off_board_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, getResignId());
//			System.out.println("8 pst ===>> " + pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				reason = rs.getString("emp_reason");
				notice = rs.getString("notice_days");
			}
			rs.close();
			pst.close();

			empMap.put("REASON", uF.showData(reason, ""));
			empMap.put("NOTICE_PERIOD", uF.showData(notice, ""));

			request.setAttribute("empDetailsMp", empMap);

			Map<String, Map<String, String>> hmCurrentsalary = CF.getCurrentSalary(con);
			Map<String, String> hmEmpSalaryDetails = hmCurrentsalary.get(strEmpId);

			pst = con.prepareStatement("select last_day_date from emp_off_board where emp_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
//			System.out.println("9 pst ===>> " + pst);
			rs = pst.executeQuery();
			String strLastDays = null;
			while (rs.next()) {
				strLastDays = rs.getString("last_day_date");
			}
			rs.close();
			pst.close();

			pst = con.prepareStatement("select joining_date from employee_personal_details where emp_per_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
//			System.out.println("10 pst ===>> " + pst);
			rs = pst.executeQuery();
			String strjoiningDays = null;
			while (rs.next()) {
				strjoiningDays = rs.getString("joining_date");
			}
			rs.close();
			pst.close();
			
			String strTotalService = uF.getTimeDurationBetweenDatesNoSpan(strjoiningDays, DBDATE, strLastDays, DBDATE, CF, uF, request);
//			System.out.println("strTotalService======>"+strTotalService);
			request.setAttribute("totalService", strTotalService);
			
			/*
			 * Reimbursement
			 * **/
			double reimbursement = 0.0;
			pst = con.prepareStatement("select reimbursement_amount,is_fullandfinal from emp_reimbursement where emp_id=? and ispaid=true and is_fullandfinal=true");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				reimbursement += rs.getDouble(1);
			}
			rs.close();
			pst.close();
			request.setAttribute("Reimbursement", reimbursement);
			
			/*
			 * Gratuity
			 * **/
			double gratuity = 0.0;
			pst = con.prepareStatement("select gratuity_amount from emp_gratuity_details where emp_id=? and is_fullandfinal=true");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				gratuity += uF.parseToDouble(rs.getString("gratuity_amount"));
			}
			rs.close();
			pst.close();
			request.setAttribute("gratuity", gratuity);

			/*
			 * LTA
			 * **/
			pst = con.prepareStatement("select applied_amount from emp_lta_details where emp_id=? and is_fullandfinal=true");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs=pst.executeQuery();
			double dblLTAAmount = 0.0d;  
			while(rs.next()) {
				dblLTAAmount += rs.getDouble("applied_amount");
			}
			rs.close();
			pst.close();
			request.setAttribute("LTAAmt", dblLTAAmount);
			
			/*
			 * Perk
			 * **/
			pst = con.prepareStatement("select perk_amount from emp_perks where emp_id=? and is_fullandfinal=true");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs=pst.executeQuery();
			double dblPerkPaidAmount = 0.0d;
			while(rs.next()) {
				dblPerkPaidAmount += rs.getDouble("perk_amount");
			}
			rs.close();
			pst.close();
			request.setAttribute("PerkAmt", dblPerkPaidAmount);
			
			/*
			 * Other Deduction
			 * **/
			double deductAmt = 0.0d;
			pst = con.prepareStatement("select deduct_amount from emp_offboard_status where offboard_id=? and section=104");
			pst.setInt(1, getResignId());
			rs = pst.executeQuery();
			while (rs.next()) {
				deductAmt += uF.parseToDouble(rs.getString("deduct_amount"));
			}
			rs.close();
			pst.close();
			request.setAttribute("deductAmt", deductAmt);
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

//	public void fullSalaryPreview(String strEmpId, CommonFunctions CF) {
//
//		UtilityFunctions uF = new UtilityFunctions();
//		Connection con = null;
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		Database db = new Database();
//		db.setRequest(request);
//		
//		try {
//
//			con = db.makeConnection(con);
//			
//			pst = con.prepareStatement("select emp_id,off_board_type,entry_date,approved_1_by,emp_fname,emp_lname,emp_reason,approved_1_reason," +
//			"approved_2_reason,last_day_date,approved_1_date from emp_off_board eob,employee_personal_details epd where epd.emp_per_id = eob.approved_1_by and approved_1=? and approved_2=? and emp_id=?");
//			pst.setInt(1, 1);
//			pst.setInt(2, 1);
//			pst.setInt(3, uF.parseToInt(strEmpId));
////			System.out.println("1 pst ===>> " + pst);
//			rs = pst.executeQuery();
//			Map<String, String> empMap = new HashMap<String, String>();
//			while (rs.next()) {
//				empMap.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBTIMESTAMP, CF.getStrReportDateFormat()));
//				empMap.put("LAST_DAY_DATE", uF.getDateFormat(rs.getString("last_day_date"), DBDATE, CF.getStrReportDateFormat()));
//				empMap.put("OFF_BOARD_TYPE", rs.getString("off_board_type"));
//				empMap.put("EMP_ID", rs.getString("emp_id"));
//				empMap.put("EMP_RESIGN_REASON", uF.parseToHTML(rs.getString("emp_reason")));
//				empMap.put("ACCEPTED_BY", rs.getString("emp_fname") + " " + rs.getString("emp_lname"));
//				empMap.put("ACCEPTED_DATE", uF.getDateFormat(rs.getString("approved_1_date"), DBDATE, CF.getStrReportDateFormat()));
//				empMap.put("MANAGER_APPROVE_REASON", rs.getString("approved_1_reason"));
//				empMap.put("HR_MANAGER_APPROVE_REASON", rs.getString("approved_2_reason"));
//			}
//			rs.close();
//			pst.close();
//			
//			viewProfile(con, strEmpId);
//			request.setAttribute("empDetailsMp", empMap);
//			
//			Map<String, String> hmSalaryDetails = new LinkedHashMap<String, String>();
//			List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
//			List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
//
//			pst = con.prepareStatement("select * from emp_salary_details esd,salary_details sd  where sd.salary_head_id=esd.salary_head_id and emp_id=? ");
//			pst.setInt(1, uF.parseToInt(strEmpId));
////			System.out.println("2 pst ===>> " + pst);
//			rs = pst.executeQuery();
//
//			List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
//			List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
//
//			while (rs.next()) {
//				if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
//					int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
//
//					if (index >= 0) {
//						alEmpSalaryDetailsEarning.remove(index);
//						alEarningSalaryDuplicationTracer.remove(index);
//						// alEmpSalaryDetailsEarning.add(index,
//						// rs.getString("salary_head_id"));
//						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
//					} else {
//						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
//					}
//					alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
//					
//				} else if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("D")) {
//					int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
//					if (index >= 0) {
//						alEmpSalaryDetailsDeduction.remove(index);
//						alDeductionSalaryDuplicationTracer.remove(index);
//						// alEmpSalaryDetailsDeduction.add(index,
//						// rs.getString("salary_head_id"));
//						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
//					} else {
//						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
//					}
//					alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
//				}
//
//				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
//			}
//			rs.close();
//			pst.close();
//
////			System.out.println("alEmpSalaryDetailsEarning ===>> " + alEmpSalaryDetailsEarning);
////			System.out.println("alEmpSalaryDetailsDeduction ===>> " + alEmpSalaryDetailsDeduction);
//			
////			System.out.println("alEarningSalaryDuplicationTracer ===>> " + alEarningSalaryDuplicationTracer);
////			System.out.println("alEarningSalaryDuplicationTracer ===>> " + alEarningSalaryDuplicationTracer);
//			
////			System.out.println("hmSalaryDetails ===>> " + hmSalaryDetails);
//			
//			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
//			
//			request.setAttribute("alDeductionSalaryDuplicationTracer", alDeductionSalaryDuplicationTracer);
//			request.setAttribute("alDeductionSalaryDuplicationTracer", alDeductionSalaryDuplicationTracer);
//			
//			request.setAttribute("alEmpSalaryDetailsEarning", alEmpSalaryDetailsEarning);
//			request.setAttribute("alEmpSalaryDetailsDeduction", alEmpSalaryDetailsDeduction);
//
//			Map<String, Double> hmSalaryAmt = new HashMap<String, Double>();
//
////			pst = con.prepareStatement("select sum(amount) as amount,salary_head_id from payroll_generation where emp_id=? and (is_paid=false or(is_paid=true and is_fullfinal=true)) group by salary_head_id");
//			pst = con.prepareStatement("select sum(amount) as amount,salary_head_id from payroll_generation where emp_id=? and is_paid=true and is_fullfinal=true group by salary_head_id");
//			pst.setInt(1, uF.parseToInt(strEmpId));
////			System.out.println("3 pst ===>> " + pst);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				hmSalaryAmt.put(rs.getString("salary_head_id"), rs.getDouble("amount"));
//			}
//			rs.close();
//			pst.close();
//
//			request.setAttribute("hmSalaryAmt", hmSalaryAmt);
//
//			String last_attendace = null;
//			pst = con.prepareStatement("select max(to_date(in_out_timestamp::text,'yyyy-MM-dd'))as last_working_attendance from attendance_details where emp_id=?");
//			pst.setInt(1, uF.parseToInt(strEmpId));
////			System.out.println("4 pst ===>> " + pst);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				last_attendace = rs.getString("last_working_attendance");
//			}
//			rs.close();
//			pst.close();
//
//			String last_payroll_date = null;
//			pst = con.prepareStatement("select max(paid_to) as paid_to from payroll_generation  where emp_id=? and is_paid=true and (is_fullfinal is null or is_fullfinal=false)");
//			pst.setInt(1, uF.parseToInt(strEmpId));
////			System.out.println("5 pst ===>> " + pst);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				last_payroll_date = rs.getString("paid_to");
//			}
//			rs.close();
//			pst.close();
//
//			if (last_payroll_date == null) {
//
//				pst = con.prepareStatement("select joining_date from employee_personal_details where emp_per_id=?");
//				pst.setInt(1, uF.parseToInt(strEmpId));
////				System.out.println("6 pst ===>> " + pst);
//				rs = pst.executeQuery();
//				while (rs.next()) {
//					last_payroll_date = rs.getString("joining_date");
//				}
//				rs.close();
//				pst.close();
//
//			}
//
//			last_payroll_date = uF.getDateFormat(last_payroll_date, DBDATE, DATE_FORMAT);
//			String last_date = last_payroll_date;
//			DateFormat format = new SimpleDateFormat(DATE_FORMAT);
//			
//			Date date = uF.getDateFormat(last_payroll_date, DATE_FORMAT);
//			Calendar cal = Calendar.getInstance();
//			cal.setTime(date);
//			cal.add(Calendar.DATE, 1);
//			
//			StringBuilder Months = new StringBuilder();
////			System.out.println("last_payroll_date=====>"+last_payroll_date);
////			System.out.println("last_attendace=====>"+last_attendace);
////			System.out.println("uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE)=====>"+uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE));
////			System.out.println("uF.parseToInt(uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE))=====>"+uF.parseToInt(uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE)));
//			while (uF.parseToInt(uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE)) > 1) {
//				Months.append(uF.getMonth(cal.get(Calendar.MONTH) + 1) + ",");
//				cal.add(Calendar.MONTH, 1);
//				date = cal.getTime();
//				last_payroll_date = format.format(date);
////				System.out.println("1last_payroll_date=====>"+last_payroll_date);
////				System.out.println("1last_attendace=====>"+last_attendace);
////				System.out.println("1uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE)=====>"+uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE));
////				System.out.println("1uF.parseToInt(uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE))=====>"+uF.parseToInt(uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE)));
//			}
////			System.out.println("Months.toString()=====>"+Months.toString());
//			empMap.put("MONTHS", Months.toString());
//			
//			String totalWorkingDays = null;
//			pst = con.prepareStatement("select count(*) as count_days from(select to_date(in_out_timestamp::text,'yyyy-MM-dd') from attendance_details where to_date(in_out_timestamp::text,'yyyy-MM-dd')>? and emp_id=? group by to_date(in_out_timestamp::text,'yyyy-MM-dd'))as a");
//			pst.setDate(1, uF.getDateFormat(last_date, DATE_FORMAT));
//			pst.setInt(2, uF.parseToInt(strEmpId));
////			System.out.println("7 pst ===>> " + pst);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				totalWorkingDays = rs.getString("count_days");
//			}
//			rs.close();
//			pst.close();
//
//			empMap.put("TOTALWORKINGDAYS", uF.showData(totalWorkingDays, ""));
//			String reason = null;
//			String notice = null;
//
//			pst = con.prepareStatement("select emp_reason,notice_days from emp_off_board where emp_id=? and off_board_id=?");
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setInt(2, getResignId());
////			System.out.println("8 pst ===>> " + pst);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				reason = rs.getString("emp_reason");
//				notice = rs.getString("notice_days");
//			}
//			rs.close();
//			pst.close();
//
//			empMap.put("REASON", uF.showData(reason, ""));
//			empMap.put("NOTICE_PERIOD", uF.showData(notice, ""));
//
//			request.setAttribute("empDetailsMp", empMap);
//
//			Map<String, Map<String, String>> hmCurrentsalary = CF.getCurrentSalary(con);
//			Map<String, String> hmEmpSalaryDetails = hmCurrentsalary.get(strEmpId);
//
//			pst = con.prepareStatement("select last_day_date from emp_off_board where emp_id=?");
//			pst.setInt(1, uF.parseToInt(strEmpId));
////			System.out.println("9 pst ===>> " + pst);
//			rs = pst.executeQuery();
//			String strLastDays = null;
//			while (rs.next()) {
//				strLastDays = rs.getString("last_day_date");
//			}
//			rs.close();
//			pst.close();
//
//			pst = con.prepareStatement("select joining_date from employee_personal_details where emp_per_id=?");
//			pst.setInt(1, uF.parseToInt(strEmpId));
////			System.out.println("10 pst ===>> " + pst);
//			rs = pst.executeQuery();
//			String strjoiningDays = null;
//			while (rs.next()) {
//				strjoiningDays = rs.getString("joining_date");
//			}
//			rs.close();
//			pst.close();
//			
//			String strTotalService = uF.getTimeDurationBetweenDatesNoSpan(strjoiningDays, DBDATE, strLastDays, DBDATE, CF, uF, request);
////			System.out.println("strTotalService======>"+strTotalService);
//			request.setAttribute("totalService", strTotalService);
//			
//			strjoiningDays = uF.dateDifference(strjoiningDays, DBDATE, strLastDays, DBDATE);
// 
//			int nTotalNoOfDays = uF.parseToInt(strjoiningDays);   
//			int nNoOfYears = nTotalNoOfDays / 365;
//			int nNoOfMonths = (nTotalNoOfDays % 365) / 30;
//			int nNoOfDays = (nTotalNoOfDays % 365) % 30;
//			
////			String strTotalService = "";
////			if(nNoOfYears >0 && nNoOfYears==1){
////				strTotalService += nNoOfYears+" year ";
////			} else if(nNoOfYears >0){
////				strTotalService += nNoOfYears+" years ";
////			}
////			if(nNoOfYears > 0){
////				strTotalService += " and ";
////			}
////			
////			if(nNoOfMonths >0 && nNoOfMonths==1){
////				strTotalService += nNoOfMonths+" month ";
////			} else if(nNoOfMonths >0){
////				strTotalService += nNoOfMonths+" months ";
////			}
////			 
////			if(nNoOfMonths > 0){
////				strTotalService += " and ";
////			}
////			
////			if(nNoOfDays >0 && nNoOfDays==1){
////				strTotalService += nNoOfDays+" day ";
////			} else if(nNoOfDays >0){
////				strTotalService += nNoOfDays+" days ";
////			}
////			
////			System.out.println("strTotalService======>"+strTotalService);
////			request.setAttribute("totalService", strTotalService);
//
//			double gratuity = 0.0;
//			if (hmEmpSalaryDetails != null) {
//				gratuity = CF.getCalculatedGratuityAmount(con, hmEmpSalaryDetails, nNoOfYears, nNoOfMonths);
//			}
//			request.setAttribute("gratuity", gratuity);
//			
//			
//			
//			double reimbursement = 0.0;
////			pst = con.prepareStatement("select reimbursement_amount,is_fullandfinal from emp_reimbursement where emp_id=? and ((ispaid=false and is_fullandfinal=false ) or (ispaid=true and is_fullandfinal=true))");
//			pst = con.prepareStatement("select reimbursement_amount,is_fullandfinal from emp_reimbursement where emp_id=? and ispaid=true and is_fullandfinal=true");
//			pst.setInt(1, uF.parseToInt(strEmpId));
////			System.out.println("11 pst ===>> " + pst); 
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				reimbursement += rs.getDouble(1);
//			}
//			rs.close();
//			pst.close();
//			request.setAttribute("Reimbursement", reimbursement);
//
//			double leaveAccrue = 0.0;
//			pst = con.prepareStatement("select sum(leave_carryforward) as leave_carryforward,sum(accrued_leaves) as accrued_leaves,sum(taken_leaves) as taken_leaves,sum(assigned_leaves) as assigned_leaves from leave_register where emp_id=?");
//			pst.setInt(1, uF.parseToInt(strEmpId));
////			System.out.println("12 pst ===>> " + pst);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				leaveAccrue = rs.getDouble("leave_carryforward") + rs.getDouble("accrued_leaves") - rs.getDouble("taken_leaves");
//			}
//			rs.close();
//			pst.close();
//
//			pst = con.prepareStatement("select * from emp_salary_details where emp_id=? and is_approved=true and  effective_date=(select max(effective_date) from emp_salary_details where emp_id=?)");
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setInt(2, uF.parseToInt(strEmpId));
////			System.out.println("13 pst ===>> " + pst);
//			rs = pst.executeQuery();
//			double grossSal = 0.0;
//			while (rs.next()) {
//
//				if (rs.getString("earning_deduction") != null && rs.getString("earning_deduction").equalsIgnoreCase("E")) {
//					grossSal += rs.getDouble("amount");
//				} else {
//					grossSal -= rs.getDouble("amount");
//				}
//			}
//			rs.close();
//			pst.close();
//			
//			double leaveEncash = grossSal * leaveAccrue / 31;
//			request.setAttribute("leaveAccrue", leaveAccrue);
//			request.setAttribute("leaveEncash", leaveEncash);
//			
//			double accrued_salary=0.0;
//			double deduction_salary=0.0;
//
//			pst = con.prepareStatement("select * from payroll_generation where emp_id=? and salary_head_id=?");
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setInt(2, -2);
////			System.out.println("14 pst ===>> " + pst);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				accrued_salary=rs.getDouble("amount");
//			}
//			rs.close();
//			pst.close();
//
//			pst = con.prepareStatement("select * from payroll_generation where emp_id=? and salary_head_id=?");
//			pst.setInt(1, uF.parseToInt(strEmpId));
//			pst.setInt(2, -1);
////			System.out.println("15 pst ===>> " + pst);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				deduction_salary=rs.getDouble("amount");
//			}
//			rs.close();
//			pst.close();
//			request.setAttribute("accrued_salary",accrued_salary);
//			request.setAttribute("deduction_salary",deduction_salary);
//			
//			double loanAmt = 0.0;
//			pst = con.prepareStatement("select balance_amount from loan_applied_details where emp_id=? and is_approved=1");
//			pst.setInt(1, uF.parseToInt(strEmpId));
////			System.out.println("16 pst ===>> " + pst);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				loanAmt += rs.getDouble(1);
//			}
//			rs.close();
//			pst.close();
//
//			pst = con.prepareStatement("select amount_paid from loan_payments where emp_id=? and is_fullandfinal=true");
//			pst.setInt(1, uF.parseToInt(strEmpId));
////			System.out.println("17 pst ===>> " + pst);
//			rs = pst.executeQuery();
//			while (rs.next()) {
//				loanAmt += rs.getDouble(1);
//			}
//			rs.close();
//			pst.close();
//			request.setAttribute("loanAmt", loanAmt);
//			
//			double travellingAllowance=0.0;
//			request.setAttribute("travellingAllowance", travellingAllowance);
//
//			
//			pst = con.prepareStatement("select sum(amount) as amount from payroll_generation_lta  where emp_id=? group by emp_id");
//			pst.setInt(1, uF.parseToInt(strEmpId));
////			System.out.println("18 pst ===>> " + pst);
//			rs=pst.executeQuery();
//			double dblLTAAmount = 0.0d;  
//			while(rs.next()) {
//				dblLTAAmount = rs.getDouble("amount");
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select sum(applied_amount) as applied_amount from emp_lta_details where emp_id=? and is_paid = true group by emp_id");
//			pst.setInt(1, uF.parseToInt(strEmpId));
////			System.out.println("19 pst ===>> " + pst);
//			rs=pst.executeQuery();
//			double dblLTAPaidAmount = 0.0d;
//			while(rs.next()) {
//				dblLTAPaidAmount = rs.getDouble("applied_amount");
//			}
//			rs.close();
//			pst.close();
//			
//			double amt = dblLTAAmount - dblLTAPaidAmount;
//			if(amt < 0.0d) {
//				amt = 0.0d;
//			}
//			
//			request.setAttribute("LTAAmt", amt);
//			
//			Map<String, String> hmEmpLevelId = CF.getEmpLevelMap(con);
//			pst = con.prepareStatement("select max_amount from perk_details where level_id=?");
//			pst.setInt(1, uF.parseToInt(hmEmpLevelId.get(strEmpId)));
////			System.out.println("20 pst ===>> " + pst);
//			rs=pst.executeQuery();
//			double dblPerksAmount = 0.0d;  
//			while(rs.next()) {
//				dblPerksAmount = rs.getDouble("max_amount");
//			}
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select sum(perk_amount) as perk_amount from emp_perks where emp_id=? and ispaid = true group by emp_id");
//			pst.setInt(1, uF.parseToInt(strEmpId));
////			System.out.println("21 pst ===>> " + pst);
//			rs=pst.executeQuery();
//			double dblPerkPaidAmount = 0.0d;
//			while(rs.next()) {
//				dblPerkPaidAmount = rs.getDouble("perk_amount");
//			}
//			rs.close();
//			pst.close();
//			
//			double dblPerkRemainAmount = dblPerksAmount - dblPerkPaidAmount;
//			if(dblPerkRemainAmount < 0.0d) {
//				dblPerkRemainAmount = 0.0d;
//			}
//			
//			request.setAttribute("PerkAmt", dblPerkRemainAmount);
//			
//			
//		} catch (SQLException e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}

	public void viewProfile(Connection con, String strEmpIdReq) {

		UtilityFunctions uF = new UtilityFunctions();

		try {
//			System.out.println("CF ====> " + CF + " session ======> " + session + " strSessionUserType =====> " + strSessionUserType);
			CF.getEmpProfileDetail(con, request, session, CF, uF, strSessionUserType, strEmpIdReq);
			request.setAttribute(TITLE, "Salary Preview");

			List<List<String>> alSkills = new ArrayList<List<String>>();
			alSkills = CF.selectSkills(con, uF.parseToInt(strEmpIdReq));
			request.setAttribute("alSkills", alSkills);
			
//			request.setAttribute("alActivityDetails", alActivityDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
//		return SUCCESS;
	}
	
	public void viewSalaryDetails(String strEmpId, String payCycle) {

		
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			//UtilityFunctions uF = new UtilityFunctions();
			String[] arrPaycycle = payCycle.split("-");
			String strD1 = arrPaycycle[0];
			String strD2 = arrPaycycle[1];

			String strEmpOrgId = CF.getEmpOrgId(con, uF, strEmpId);
			
			String paycycleType = null;
			pst = con.prepareStatement("select paycycle_duration from employee_official_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			rs = pst.executeQuery();
			while (rs.next()) {
				paycycleType = rs.getString(1);
			}
			rs.close();
			pst.close();

			ApprovePayroll objApprovePayroll = new ApprovePayroll();
			objApprovePayroll.setServletRequest(request);
			objApprovePayroll.session=session;
			objApprovePayroll.CF=CF;
//			objApprovePayroll.setPaycycle(strD1 + "-" + strD2 + "-" + paycycleType);
			objApprovePayroll.setPaycycle(payCycle);
			objApprovePayroll.setStrPaycycleDuration(paycycleType);
			objApprovePayroll.setF_org(strEmpOrgId);
			objApprovePayroll.viewClockEntriesForPayrollApproval(CF, strEmpId, strD1, strD2, arrPaycycle[2]);
//			objApprovePayroll.viewClockEntriesForPayrollApproval(CF, null, strD1, strD2, arrPaycycle[2]);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public String getEmp_id() {
		return emp_id;
	}

	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}

	public String getPayCycle() {
		return payCycle;
	}

	public void setPayCycle(String payCycle) {
		this.payCycle = payCycle;
	}

	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

}