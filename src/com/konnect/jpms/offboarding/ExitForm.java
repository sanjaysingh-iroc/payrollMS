package com.konnect.jpms.offboarding;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.payroll.ApprovePayroll;
import com.konnect.jpms.reports.EmpGratuityReport;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.EncryptionUtility;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;

public class ExitForm implements ServletRequestAware, IStatements {
	HttpSession session;
	String strSessionEmpId;
	String strUserType;
	String strBaseUserType = null;
	String strWLocationAccess;
	CommonFunctions CF;
	
	private String id;
	private String resignId;
	private String appId;
	private String operation;
	private boolean flag; 
	private File[] document;
	private String[] documentFileName;
	private String from;
	private String strAction;
	public String execute() {
		session = request.getSession();
		strSessionEmpId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strWLocationAccess = (String)session.getAttribute(WLOCATION_ACCESS);
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return "login";

		request.setAttribute(PAGE, "/jsp/offboarding/ExitForm.jsp");
		request.setAttribute(TITLE, "Exit Form");
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		UtilityFunctions uF = new UtilityFunctions();
		//Created By Dattatray 13-6-2022
		strAction = request.getServletPath();
		if(strAction!=null) {
			strAction = strAction.replace("/","");
		}

//		EncryptionUtility eU = new EncryptionUtility();
//		System.out.println("getId() ===>> " + getId());
		/*if(uF.parseToInt(getId()) > 0) {
			String encodeEmpId = eU.encode(getId());
			setId(encodeEmpId);
		}
		if(uF.parseToInt(getResignId()) > 0) {
			String encodeResignId = eU.encode(getResignId());
			setResignId(encodeResignId);
		}
		
		if(getId() != null && uF.parseToInt(getId()) == 0) {
			String decodeEmpId = eU.decode(getId());
			setId(decodeEmpId);
		}
		if(getResignId() != null && uF.parseToInt(getResignId()) == 0) {
			String decodeResignId = eU.decode(getResignId());
			setResignId(decodeResignId);
		}*/
		
		List<String> accessEmpList = CF.viewEmployeeIdsList(request, uF, strBaseUserType, strSessionEmpId, strWLocationAccess);
		if((strBaseUserType != null && strUserType != null && (strBaseUserType.equals(EMPLOYEE) || strUserType.equals(EMPLOYEE))) || !accessEmpList.contains(getId())) {
			setId(strSessionEmpId);
		}
		
		getEmployeeDetails();

		getApprovalStatus(WORK_FLOW_RESIGN);
		if (getOperation() != null) {
			if (getOperation().equals("A")) {
				insertResignationEntry();
			} else if (getOperation().equals("B")) {
				uploadManagerDocument();
			} else if (getOperation().equals("C")) {
				uploadHRDocument();
			}
			
		}
		Map<String, String> statusMp = getStatus();
//		Map<String, String> statusMp = (Map<String, String> )request.getAttribute("statusMp");
		checkUserAccountStatus(uF);
		getRemark(uF);
		getManagerDocument();
		getHRDocument();
		checkFeedBack(uF);
//		getPreDocument();
		loadPageVisitAuditTrail(uF);//Created By Dattatray 09-06-2022 t0 13-06-2022
		if(statusMp.get("3") != null && uF.parseToBoolean(statusMp.get("3"))){
			getSalaryDetails();
		}
		if (uF.parseToInt(getId()) == uF.parseToInt((String) session.getAttribute(EMPID))) {
			flag = true;
		}
		
		if(getFrom()!=null && getFrom().equalsIgnoreCase("EA")){
			return VIEW;
		}else{
			return "success";
		}
	}

	//Created By Dattatray 10-06-2022 t0 13-06-2022
	private void loadPageVisitAuditTrail(UtilityFunctions uF) {
		Connection con=null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			Map<String, String> hmEmpProfile = CF.getEmpNameMap(con,null,getId());
			StringBuilder builder = new StringBuilder();
			builder.append("Full & final Action clicked: "+hmEmpProfile.get(getId()));
			
			CF.pageVisitAuditTrail(con,CF,uF, strSessionEmpId, strAction, strBaseUserType, builder.toString());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			db.closeConnection(con);
		}
		
	}
	
	private void checkFeedBack(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			boolean feedbackFlag = false;
			pst = con.prepareStatement("select * from form_question_answer where emp_id=? and resign_id=? " +
					"and form_id in (select form_id from form_management_details where node_id="+NODE_EXIT_FORM_ID+") limit 1");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getResignId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				feedbackFlag = true;
			}
			rs.close();
			pst.close();
			request.setAttribute("feedbackFlag", ""+feedbackFlag);
			
			boolean clearanceFlag = false;
			pst = con.prepareStatement("select * from form_question_answer where emp_id=? and resign_id=? " +
					"and form_id in (select form_id from form_management_details where node_id="+NODE_CLEARANCE_FORM_ID+") limit 1");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getResignId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				clearanceFlag = true;
			}
			rs.close();
			pst.close();
			request.setAttribute("clearanceFlag", ""+clearanceFlag);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public List<List<String>> getSalaryDetails() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		List<List<String>> outerList = new ArrayList<List<String>>();

		try {
			
//			System.out.println("EF/173--Called...getSalaryDetails");
			
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];
			
			con = db.makeConnection(con);
			
			String strEmpOrgId = CF.getEmpOrgId(con, uF, ""+getId());
			
			Map<String, String> totalSalry = new LinkedHashMap<String, String>();
			Map<String, String> grossSalry = new HashMap<String, String>();
			Map<String, String> hmPayCycle = new LinkedHashMap<String, String>();
			Map<String, String> paycycleSalry = new HashMap<String, String>();
			Map<String, Boolean> statusSalry = new HashMap<String, Boolean>();
			
			
			String paycycleType = null;
			pst = con.prepareStatement("select paycycle_duration from employee_official_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
//			System.out.println("1 pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				paycycleType = rs.getString(1);
			}
			rs.close();
			pst.close();
			
			
			pst = con.prepareStatement("select paid_from,paid_to,paycycle,is_paid from payroll_generation where emp_id=? and (is_paid=false or is_fullfinal=true) group by paid_from,paid_to,paycycle,is_paid order by paycycle");
			pst.setInt(1,uF.parseToInt(getId()));
//			System.out.println("2 pst==>"+pst);
			rs = pst.executeQuery();
			List<List<String>> outerList1 = new ArrayList<List<String>>();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(uF.getDateFormat(rs.getString(1), DBDATE, DATE_FORMAT));
				innerList.add(uF.getDateFormat(rs.getString(2), DBDATE, DATE_FORMAT));
				innerList.add(rs.getString(3));

				outerList1.add(innerList);
				statusSalry.put(innerList.get(0) + "-" + innerList.get(1) + "-" + innerList.get(2), rs.getBoolean("is_paid"));
			}
			rs.close();
			pst.close();
//			System.out.println("outerList1==>"+outerList1);
//			System.out.println("statusSalry==>"+statusSalry);
			
			for (List<String> innerList : outerList1) {
				double amtPaid = 0.0;
				double amtdeduct = 0.0;
				pst = con.prepareStatement("select sum(amount) from payroll_generation where emp_id=? and paycycle=? and earning_deduction='E'");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(innerList.get(2)));
//				System.out.println(" pst==>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					amtPaid = rs.getDouble(1);
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select sum(amount) from payroll_generation where emp_id=? and paycycle=? and earning_deduction='D'");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setInt(2, uF.parseToInt(innerList.get(2)));
//				System.out.println(" pst==>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					amtdeduct = rs.getDouble(1);
				}
				rs.close();
				pst.close();

				hmPayCycle.put(innerList.get(0) + "-" + innerList.get(1) + "-" + innerList.get(2), innerList.get(0) + "-" + innerList.get(1));
				totalSalry.put(innerList.get(0) + "-" + innerList.get(1) + "-" + innerList.get(2), uF.formatIntoTwoDecimal(amtPaid - amtdeduct));
				grossSalry.put(innerList.get(0) + "-" + innerList.get(1) + "-" + innerList.get(2), uF.formatIntoTwoDecimal(amtPaid));

				paycycleSalry.put(innerList.get(0) + "-" + innerList.get(1) + "-" + innerList.get(2), innerList.get(0) + "-" + innerList.get(1) + "-" + innerList.get(2));
			}
			
//			System.out.println("hmPayCycle==>"+hmPayCycle);
//			System.out.println("totalSalry==>"+totalSalry);
//			System.out.println("grossSalry==>"+grossSalry);
//			System.out.println("paycycleSalry==>"+paycycleSalry);

			String last_attendace = null;
			pst = con.prepareStatement("select max(to_date(in_out_timestamp::text,'yyyy-MM-dd'))as last_working_attendance from attendance_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
//			System.out.println("3 pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				last_attendace = rs.getString("last_working_attendance");
			}
			rs.close();
			pst.close();
//			System.out.println("last_attendace==>"+last_attendace);
			
			String last_payroll_date = null;
			pst = con.prepareStatement("select max(paid_to) as paid_to from payroll_generation where emp_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
//			System.out.println("EF/273--4 pst==>"+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				last_payroll_date = rs.getString("paid_to");
			}
			rs.close();
			pst.close(); 
//			System.out.println("EF/280--last_payroll_date==>"+last_payroll_date);
			
			if (last_payroll_date == null) {
				pst = con.prepareStatement("select joining_date from employee_personal_details where emp_per_id=?");
				pst.setInt(1, uF.parseToInt(getId()));
//				System.out.println("4 pst==>"+pst);
				rs = pst.executeQuery();
				while (rs.next()) {
					last_payroll_date = rs.getString("joining_date");
				}
				rs.close();
				pst.close();
//				System.out.println("EF/292--else last_payroll_date==>"+last_payroll_date);
			}

			last_payroll_date = uF.getDateFormat(last_payroll_date, DBDATE, DATE_FORMAT);
//			System.out.println("EF/296--last_payroll_date======>"+last_payroll_date);

			List<String[]> payCycleList = new ArrayList<String[]>(); // not precessed yet
			
	//===start parvez date: 27-10-2021===
			pst = con.prepareStatement("select last_day_date from emp_off_board where approved_1=1 and approved_2=1 and emp_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			String empResignDate = null;
//			String strEmpEndDate = null;		//created by parvez date: 14-04-2022
			while (rs.next()) {
				empResignDate = uF.getDateFormat(rs.getString("last_day_date"), DBDATE, "MM");
			//===start parvez date: 14-04-2022===		
//				strEmpEndDate = uF.getDateFormat(rs.getString("last_day_date"), DBDATE, DATE_FORMAT);
			//===end parvez date: 14-04-2022===	
			}
			rs.close();
			pst.close();
			
	//===end parvez date: 27-10-2021===

			while (uF.parseToInt(uF.dateDifference(last_payroll_date, DATE_FORMAT, last_attendace, DBDATE)) > 1) {
//				String[] pay = getNextPayCycle(con,last_payroll_date, CF.getStrTimeZone(), CF, paycycleType);
//				System.out.println("ExitForm/302--empResignDate="+empResignDate);
				
		//===start parvez date: 27-10-2021===
				if(empResignDate != null && empResignDate.equals(uF.getDateFormat(last_payroll_date, DATE_FORMAT, "MM"))){
					break;
				}
		//===end parvez date: 27-10-2021===
				
				
				String[] pay = CF.getNextPayCycleByOrg(con, last_payroll_date,  CF.getStrTimeZone(), CF, strEmpOrgId);
				last_payroll_date = pay[1];
				payCycleList.add(pay);
			}
			
//			System.out.println("payCycleList======>"+payCycleList.size());
			
	//===start parvez date: 14-04-2022===
			
//			String[] empLastPay = CF.getPayCycleByOrg(con, strEmpEndDate,  CF.getStrTimeZone(), strEmpOrgId);
//			System.out.println("empLastPay[0]="+empLastPay[0]+"---empLastPay[1]="+empLastPay[1]+"---empLastPay[2]="+empLastPay[2]);
	//===end parvez date: 14-04-2022===		

			for (String[] payCycle : payCycleList) {
//				System.out.println("EF/334---"+payCycle[0] + "-" + payCycle[1] + "-" + payCycle[2]);
//				System.out.println(payCycle.toString());
				statusSalry.put(payCycle[0] + "-" + payCycle[1] + "-" + payCycle[2], false);

			//===start parvez date: 27-10-2021===	
				if(payCycle[0] != null && payCycle[1] != null && payCycle[2] != null){
					ApprovePayroll objApprovePayroll = new ApprovePayroll();
					objApprovePayroll.setServletRequest(request);
					objApprovePayroll.session=session;
					objApprovePayroll.CF=CF;
					objApprovePayroll.setPaycycle(payCycle[0] + "-" + payCycle[1] + "-" + payCycle[2]);
					objApprovePayroll.setStrPaycycleDuration(paycycleType);
					objApprovePayroll.setF_org(strEmpOrgId);
//					objApprovePayroll.viewClockEntriesForPayrollApproval(CF, getId(), payCycle[0], payCycle[1],payCycle[2]);
					objApprovePayroll.viewClockEntriesForPayrollApprovalExitForm(CF, getId(), payCycle[0], payCycle[1],payCycle[2]);
					Map hmTotalSalary = (Map) request.getAttribute("hmTotalSalary");

					Map a = (Map) hmTotalSalary.get(String.valueOf(getId()));

					String net = "0.0";
					String gross = "0.0";
					if (a != null) {
						net = (String) a.get("NET");
//						System.out.println("net====" + net);
						if (net.contains("</span>")){
							net = net.substring(net.indexOf("</span>"), net.length()).replace("</span>", "");
						}
//						System.out.println("ExF/358--net====" + net);
						gross = (String) a.get("GROSS");
					}
					hmPayCycle.put(payCycle[0] + "-" + payCycle[1] + "-" + payCycle[2], payCycle[0] + "-" + payCycle[1]);
					totalSalry.put(payCycle[0] + "-" + payCycle[1] + "-" + payCycle[2], net);
					grossSalry.put(payCycle[0] + "-" + payCycle[1] + "-" + payCycle[2], gross);

				}
		//===end parvez date: 27-10-2021===
				
			}
			
			
//			System.out.println("hmPayCycle==>"+hmPayCycle);
//			System.out.println("totalSalry==>"+totalSalry);
//			System.out.println("grossSalry==>"+grossSalry);

			request.setAttribute("hmPayCycle", hmPayCycle);
			request.setAttribute("grossSalry", grossSalry);
			request.setAttribute("totalSalry", totalSalry);
			request.setAttribute("statusSalry", statusSalry);
			request.setAttribute("paycycleSalry", paycycleSalry);
			
			Map<String, Boolean> exemptionStatusMp = new HashMap<String, Boolean>();
			/*
			 * Reimbursment
			 * **/
			double reimbursement = 0.0d;
			boolean reimbursementFlag = false;
			pst = con.prepareStatement("select reimbursement_amount from emp_reimbursement where emp_id=? and ispaid=true and is_fullandfinal=true");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				reimbursement += rs.getDouble(1);
				reimbursementFlag = true;
				exemptionStatusMp.put("REIMBURSEMENT", true);
			}
			rs.close();
			pst.close();
			
			if(!reimbursementFlag){
				reimbursement = 0.0d;
				pst = con.prepareStatement("select reimbursement_amount,is_fullandfinal from emp_reimbursement where emp_id=? and ispaid=false and is_fullandfinal=false");
				pst.setInt(1, uF.parseToInt(getId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					reimbursement += rs.getDouble(1);
				}
				rs.close();
				pst.close();
			}
			request.setAttribute("reimbursement", reimbursement);
			
			/*
			 * Gratuity
			 * **/
			
			double gratuity = 0.0;
			boolean gratuiityFlag = false;
			pst = con.prepareStatement("select gratuity_amount from emp_gratuity_details where emp_id=? and is_fullandfinal=true");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				gratuity += uF.parseToDouble(rs.getString("gratuity_amount"));
				gratuiityFlag = true;
				exemptionStatusMp.put("GRATUITY", true);
			}
			rs.close();
			pst.close();
			
			if(!gratuiityFlag){
				String orgId = CF.getEmpOrgId(con, uF, ""+getId());
				EmpGratuityReport gratuityReport = new EmpGratuityReport();
				gratuityReport.session = session;
				gratuityReport.request = request;
				gratuityReport.CF = CF;
				String[] strPayCycleDates = CF.getCurrentPayCycleByOrg(con, CF.getStrTimeZone(), CF, orgId);
				gratuityReport.setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				gratuity = gratuityReport.getGratuityAmount(con, uF, uF.parseToInt(getId()));
			}
			request.setAttribute("gratuity", gratuity);
			
			/*
			 * LTA
			 * **/
			boolean ltaFlag = false;
			pst = con.prepareStatement("select applied_amount from emp_lta_details where emp_id=? and is_fullandfinal=true");
			pst.setInt(1, uF.parseToInt(getId()));
			rs=pst.executeQuery();
			double dblLTA = 0.0d;  
			while(rs.next()) {
				dblLTA += rs.getDouble("applied_amount");
				ltaFlag = true;
				exemptionStatusMp.put("LTA", true);
			}
			rs.close();
			pst.close();
			
			if(!ltaFlag){
				pst = con.prepareStatement("select sum(amount) as amount from payroll_generation_lta  where emp_id=? group by emp_id");
				pst.setInt(1, uF.parseToInt(getId()));
				rs=pst.executeQuery();
				double dblLTAAmount = 0.0d;
				while(rs.next()){
					dblLTAAmount = rs.getDouble("amount");
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select sum(applied_amount) as applied_amount from emp_lta_details where emp_id=? group by emp_id");
				pst.setInt(1, uF.parseToInt(getId()));
				rs=pst.executeQuery();
				double dblAppliedAmount = 0.0d;
				while(rs.next()){
					dblAppliedAmount = rs.getDouble("applied_amount");
				}
				rs.close();
				pst.close();
				
				dblLTA = dblLTAAmount - dblAppliedAmount;
				if(dblLTA < 0.0d){
					dblLTA = 0.0d;
				}
			}
			request.setAttribute("dblLTA", dblLTA);
			
			
			/*
			 * Perk
			 * **/
			boolean perkFlag = false;
			pst = con.prepareStatement("select perk_amount from emp_perks where emp_id=? and is_fullandfinal=true");
			pst.setInt(1, uF.parseToInt(getId()));
			rs=pst.executeQuery();
			double dblPerk = 0.0d;
			while(rs.next()) {
				dblPerk += rs.getDouble("perk_amount");
				perkFlag = true;
				exemptionStatusMp.put("PERK", true);
			}
			rs.close();
			pst.close();
			
			if(!perkFlag){
				String strLevelId =  CF.getEmpLevelId(con, ""+getId());	
				
				pst = con.prepareStatement("select * from perk_details where financial_year_start=? and financial_year_end=? and level_id=?");
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strLevelId));
	//			System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				Map<String, List<Map<String, String>>> hmPerkLevel = new HashMap<String, List<Map<String,String>>>();
				while(rs.next()){
					List<Map<String, String>> al = (List<Map<String, String>>) hmPerkLevel.get(rs.getString("level_id"));
					if(al == null) al = new ArrayList<Map<String,String>>();
					
					Map<String, String> hmPerk = new HashMap<String, String>();
					hmPerk.put("PERK_ID", rs.getString("perk_id"));
					hmPerk.put("PERK_CODE", uF.showData(rs.getString("perk_code"), ""));
					hmPerk.put("PERK_NAME", uF.showData(rs.getString("perk_name"), ""));
					hmPerk.put("PERK_DESCRIPTION", uF.showData(rs.getString("perk_description"), ""));
					hmPerk.put("PERK_PAYMENT_CYCLE", rs.getString("perk_payment_cycle"));
					hmPerk.put("PERK_LEVEL_ID", rs.getString("level_id"));
					hmPerk.put("PERK_MAX_AMOUNT", rs.getString("max_amount"));
					hmPerk.put("PERK_ORG_ID", rs.getString("org_id"));
					
					al.add(hmPerk);
					
					hmPerkLevel.put(rs.getString("level_id"), al);
				}
				rs.close();
				pst.close();
				
				/*
				 * Annual perk Data
				 * **/
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount, emp_id, perk_type_id from emp_perks where " +
						" financial_year_start=? and financial_year_end=? and emp_id in("+getId()+") and perk_type_id in (select perk_id " +
						"from perk_details where financial_year_start=? and financial_year_end=? and perk_payment_cycle='A') group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
	//			System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmAppliedPerk = new HashMap<String, String>();
				while(rs.next()){
					hmAppliedPerk.put(rs.getString("emp_id")+"_"+rs.getString("perk_type_id"), rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount, emp_id, perk_type_id from emp_perks where approval_1=1 and approval_2=1 and ispaid=false " +
						"and financial_year_start=? and financial_year_end=? and emp_id in("+getId()+") and perk_type_id in (select perk_id " +
						"from perk_details where financial_year_start=? and financial_year_end=? and perk_payment_cycle='A') group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
	//			System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				Map<String, String> hmApprovedPerk = new HashMap<String, String>();
				while(rs.next()){
					hmApprovedPerk.put(rs.getString("emp_id")+"_"+rs.getString("perk_type_id"), rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				
				/*
				 * Annual perk  Data end
				 * **/
				
				/*
				 * Month perk Data
				 * **/
				String strMonth = ""+uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"));
				
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount, emp_id, perk_type_id from emp_perks where " +
						" financial_year_start=? and financial_year_end=? and emp_id in("+getId()+") and perk_type_id in (select perk_id " +
						"from perk_details where financial_year_start=? and financial_year_end=? and perk_payment_cycle='M') and perk_month=? group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strMonth));
	//			System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					hmAppliedPerk.put(rs.getString("emp_id")+"_"+rs.getString("perk_type_id"), rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select sum(perk_amount) as perk_amount, emp_id, perk_type_id from emp_perks where approval_1=1 and approval_2=1 and ispaid=false " +
						"and financial_year_start=? and financial_year_end=? and emp_id in("+getId()+") and perk_type_id in (select perk_id " +
						"from perk_details where financial_year_start=? and financial_year_end=? and perk_payment_cycle='M') and perk_month=? group by emp_id, perk_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				pst.setDate(1,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(2,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDate(3,  uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(4,  uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setInt(5, uF.parseToInt(strMonth));
	//			System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				while(rs.next()){
					hmApprovedPerk.put(rs.getString("emp_id")+"_"+rs.getString("perk_type_id"), rs.getString("perk_amount"));
				}
				rs.close();
				pst.close();
				/*
				 * Month perk Data end
				 * **/
				
				dblPerk = 0.0d;
				List<Map<String, String>> alPerkList = (List<Map<String, String>>) hmPerkLevel.get(strLevelId);
	    		if(alPerkList == null) alPerkList = new ArrayList<Map<String, String>>();
				for(int j = 0; j < alPerkList.size(); j++){
					Map<String, String> hmPerk = (Map<String, String>) alPerkList.get(j);
	    			if(hmPerk == null) hmPerk = new HashMap<String, String>();
	    			
	    			double dblRemainingAmt = uF.parseToDouble(hmPerk.get("PERK_MAX_AMOUNT")) - uF.parseToDouble(hmAppliedPerk.get(getId()+"_"+hmPerk.get("PERK_ID")));
	    			dblPerk +=uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblRemainingAmt));
				}
			}
			request.setAttribute("dblPerk", dblPerk);
			
			/*
			 * Other Deduction
			 * **/
			double deductAmt = 0.0d;
			pst = con.prepareStatement("select deduct_amount from emp_offboard_status where offboard_id=? and section=104");
			pst.setInt(1, uF.parseToInt(getResignId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				deductAmt += uF.parseToDouble(rs.getString("deduct_amount"));
				exemptionStatusMp.put("OTHERDEDUCT", true);
			}
			rs.close();
			pst.close();
			request.setAttribute("deductAmt", deductAmt);
			
			request.setAttribute("exemptionStatusMp", exemptionStatusMp);
			

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		return outerList;
	}

	public Map<String, String> getStatus() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF = new UtilityFunctions();
		Map<String, String> statusMp = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);

			
			pst = con.prepareStatement("select * from emp_offboard_status where offboard_id=?");
			pst.setInt(1, uF.parseToInt(getResignId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				statusMp.put(rs.getString("section"), rs.getString("status"));
				statusMp.put(rs.getString("section")+"_REMARK", rs.getString("approved_remark"));
				statusMp.put(rs.getString("section")+"_RATING", rs.getString("approved_rating"));
				statusMp.put(rs.getString("section")+"_APPROVED_BY", uF.showData(CF.getEmpNameMapByEmpId(con, rs.getString("approved_by")), "-"));
			}
			rs.close();
			pst.close();
			request.setAttribute("statusMp", statusMp);
//			System.out.println("statusMp=====>"+statusMp);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return statusMp;

	}

	public void getHRDocument() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		List<List<String>> outerList = new ArrayList<List<String>>();
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from emp_off_board_document_details where off_board_id=? and document_type=? order by off_board_document_id ");
			pst.setInt(1, uF.parseToInt(getResignId()));
			pst.setString(2, "HR");
			rs = pst.executeQuery();

			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("off_board_document_id"));
				innerList.add(rs.getString("document_name"));
				innerList.add(rs.getString("document_comment"));
				innerList.add(rs.getString("file_path"));
				if (rs.getBoolean("approved"))
					innerList.add("1");
				else
					innerList.add("0");
				innerList.add(rs.getString("emp_id"));
				innerList.add(rs.getString("off_board_id"));
				innerList.add(rs.getString("document_uploaded_by"));

				outerList.add(innerList);
			}
			rs.close();
			pst.close();
			request.setAttribute("HRDocumentList", outerList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void getManagerDocument() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		UtilityFunctions uF= new UtilityFunctions();
		List<List<String>> outerList = new ArrayList<List<String>>();
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * from emp_off_board_document_details where off_board_id=? and document_type=? order by off_board_document_id ");
			pst.setInt(1, uF.parseToInt(getResignId()));
			pst.setString(2, "MANAGER");
			rs = pst.executeQuery();
			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("off_board_document_id"));
				innerList.add(rs.getString("document_name"));
				innerList.add(rs.getString("document_comment"));
				innerList.add(rs.getString("file_path"));
				if (rs.getBoolean("approved"))
					innerList.add("1");
				else
					innerList.add("0");
				innerList.add(rs.getString("emp_id"));
				innerList.add(rs.getString("off_board_id"));
				innerList.add(rs.getString("document_uploaded_by"));

				outerList.add(innerList);
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from emp_off_board_document_comment  where comment_made_by =?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			//System.out.println("pst ===>>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmDocumentComment = new HashMap<String, String>();
			while (rs.next()) {
				hmDocumentComment.put(rs.getString("document_id"), rs.getString("comment_text"));
			}
			rs.close();
			pst.close();
			
			request.setAttribute("hmDocumentComment", hmDocumentComment);
			request.setAttribute("outerList", outerList);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	public void uploadHRDocument() {

		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			String[] documentName = request.getParameterValues("documentname");
			String[] status = request.getParameterValues("status");
			String[] documentId = request.getParameterValues("documentId");

			int j = 0;
			int k = 0;
			for (int i = 0; status != null && i < status.length; i++) {
				String filename = null;
				if (uF.parseToInt(status[i]) == 1) {
					if (document[j] != null) {
//						filename = uF.uploadFile(request, DOCUMENT_LOCATION, document[j], documentFileName[j]);
						if(CF.getStrDocSaveLocation()==null) {
							System.out.println("if in c upload");
//							filename = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, document[j], documentFileName[j], documentFileName[j], CF);
							filename = uF.uploadProjectDocuments(request, DOCUMENT_LOCATION, document[j], documentFileName[j], documentFileName[j], CF);
						} else {
							System.out.println("else in c upload=====>"+CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_OFFBOARD+"/"+getId());
//							filename = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_OFFBOARD+"/"+getId(), document[j], documentFileName[j], documentFileName[j], CF);
							filename = uF.uploadProjectDocuments(request, CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_OFFBOARD+"/"+getId(), document[j], documentFileName[j], documentFileName[j], CF);
						}
					}
					j++; 
				}

				if (documentName == null || (status != null && (documentName.length + k) < status.length)) {

					if (filename != null) {
						pst = con.prepareStatement("update emp_off_board_document_details set file_path=?,document_uploaded_by=? where off_board_document_id=?");
						pst.setString(1, filename);
						pst.setInt(2, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setInt(3, uF.parseToInt(documentId[k]));
						pst.execute();
						pst.close();
					}
					k++;

				} else {

					pst = con.prepareStatement("insert into emp_off_board_document_details(document_name,document_comment,file_path,approved,emp_id,off_board_id,document_uploaded_by,document_type) values(?,?,?,?,?,?,?,?) ");
					pst.setString(1, documentName[i - k]);
					pst.setString(2, null);
					pst.setString(3, filename);
					pst.setBoolean(4, false);
					pst.setInt(5, uF.parseToInt(getId()));
					pst.setInt(6, uF.parseToInt(getResignId()));
					pst.setInt(7, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setString(8, "HR");
					pst.execute();
					pst.close();

				}
				
			}

			pst = con.prepareStatement("update emp_offboard_status set status=?, approved_by=?, approved_date=to_date(CURRENT_TIMESTAMP::text,'yyyy-MM-dd') " +
					"where offboard_id=? and section=?");
			pst.setBoolean(1, true);
			pst.setInt(2, uF.parseToInt((String) session.getAttribute(EMPID)));
			pst.setInt(3, uF.parseToInt(getResignId()));
			pst.setInt(4, OFFBOARD_HRMANAGER_DOC_SECTION);
			int flag = pst.executeUpdate();
			pst.close();
	
			if (flag == 0) {
				pst = con.prepareStatement("insert into emp_offboard_status(status,offboard_id,section,approved_by,approved_date) " +
						"values(?,?,?,?, to_date(CURRENT_TIMESTAMP::text,'yyyy-MM-dd'))");
				pst.setBoolean(1, true);
				pst.setInt(2, uF.parseToInt(getResignId())); 
				pst.setInt(3, OFFBOARD_HRMANAGER_DOC_SECTION);
				pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.execute();
				pst.close();
			}
			
			String strReason = "HR documents save and approved";
			insertEmpActivity(con, ""+getId(), CF, strSessionEmpId, ACTIVITY_HR_DOCUMENTS_SAVE_APPROVAL_ID, strReason);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}

	}

	
	public void uploadManagerDocument() {

		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		Map<String, String> ansMap = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			String[] documentName = request.getParameterValues("documentname");
			String[] status = request.getParameterValues("status");
			String[] documentId = request.getParameterValues("documentId");

			int j = 0;
			int k = 0;
			for (int i = 0; i < status.length; i++) {
				// String path = null;
				String filename = null;
				if (uF.parseToInt(status[i]) == 1) {
					if (document[j] != null) {
//						filename = uF.uploadFile(request, DOCUMENT_LOCATION, document[j], documentFileName[j]);
						
						if(CF.getStrDocSaveLocation()==null) {
//							System.out.println("if in b upload");
//							filename = uF.uploadImageDocuments(request, DOCUMENT_LOCATION, document[j], documentFileName[j], documentFileName[j], CF);
							filename = uF.uploadProjectDocuments(request, DOCUMENT_LOCATION, document[j], documentFileName[j], documentFileName[j], CF);
						} else {
//							System.out.println("else in b upload====>"+CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_OFFBOARD+"/"+getId());
//							filename = uF.uploadImageDocuments(request, CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_OFFBOARD+"/"+getId(), document[j], documentFileName[j], documentFileName[j], CF);
							filename = uF.uploadProjectDocuments(request, CF.getStrDocSaveLocation()+I_PEOPLE+"/"+I_OFFBOARD+"/"+getId(), document[j], documentFileName[j], documentFileName[j], CF);
						}
					}
					j++;
				}
				if (documentName == null || (documentName.length + k) < status.length) {

					if (filename != null) {
						pst = con.prepareStatement("update emp_off_board_document_details set file_path=?,document_uploaded_by=? where off_board_document_id=?");
						pst.setString(1, filename);
						pst.setInt(2, uF.parseToInt((String) session.getAttribute(EMPID)));
						pst.setInt(3, uF.parseToInt(documentId[k]));
						pst.execute();
						pst.close();
					}
					k++;

				} else {

					pst = con.prepareStatement("insert into emp_off_board_document_details(document_name,document_comment,file_path,approved,emp_id,off_board_id,document_uploaded_by,document_type) values(?,?,?,?,?,?,?,?) ");
					pst.setString(1, documentName[i - k]);
					pst.setString(2, null);
					pst.setString(3, filename);
					pst.setBoolean(4, false);
					pst.setInt(5, uF.parseToInt(getId()));
					pst.setInt(6, uF.parseToInt(getResignId()));
					pst.setInt(7, uF.parseToInt((String) session.getAttribute(EMPID)));
					pst.setString(8, "MANAGER");
					pst.execute();
					pst.close();

				}
			}
			
			if(((String) session.getAttribute(USERTYPE)).equalsIgnoreCase(EMPLOYEE)) {
				pst = con.prepareStatement("delete from emp_offboard_status where emp_id=? and offboard_id=? and section=2");
				pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.setInt(2, uF.parseToInt(getResignId()));
				pst.execute();
				pst.close();
	
				pst = con.prepareStatement("insert into emp_offboard_status(status,offboard_id,section,emp_id,emp_status) values(?,?,?,?,?)");
				pst.setBoolean(1, false);
				pst.setInt(2, uF.parseToInt(getResignId()));
				pst.setInt(3, 2);
				pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.setBoolean(5, true);
				pst.execute();
				pst.close();
			}

			String strReason = "Handover documents submited";
			insertEmpActivity(con, getId(), CF, strSessionEmpId, ACTIVITY_HANDOVER_DOCUMENTS_SUBMIT_ID, strReason);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
			db.closeStatements(pst);
			db.closeConnection(con);
		}

		request.setAttribute("ansMap", ansMap);

	}

	public void checkUserAccountStatus(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		boolean userAccStatus = true;
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select is_alive from employee_personal_details where emp_per_id = ? ");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				userAccStatus = rs.getBoolean("is_alive");
			}
			rs.close();
			pst.close();
			//System.out.println("userAccStatus ===>> " + userAccStatus);
			request.setAttribute("userAccStatus", userAccStatus);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getRemark(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		ResultSet rs = null;
		Map<String, String> ansMap = new HashMap<String, String>();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("select * from emp_off_board_feedback where emp_id=? ");
			pst.setInt(1, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				if(((String) session.getAttribute(USERTYPE)).equalsIgnoreCase(EMPLOYEE)){
					ansMap.put(rs.getString("question"), rs.getString("question_answer"));
				}else{
					ansMap.put(rs.getString("question"), uF.parseToHTML(rs.getString("question_answer")));
				}
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
		request.setAttribute("ansMap", ansMap);
	}

	
	public String insertResignationEntry() {

		Connection con = null;
		PreparedStatement pst = null;
		Database db = new Database();
		db.setRequest(request);
		UtilityFunctions uF = new UtilityFunctions();
		try {
			con = db.makeConnection(con);
			pst = con.prepareStatement("delete from emp_off_board_feedback where emp_id=? ");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.execute();
			pst.close();
//			String empPerformanceReason[] = request.getParameterValues("empPerformanceReason");
//			int i = 0;
			/*for (String question : questionList) {
				pst = con.prepareStatement("insert into emp_off_board_feedback(offboard_id,question,question_answer,emp_id) values(?,?,?,?) ");
				pst.setInt(1, uF.parseToInt(getResignId()));
				pst.setString(2, question);
				pst.setString(3, empPerformanceReason[i]);
				pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
				pst.execute();
				i++;
			}*/
			
			insertMarks(con, uF);
			
			pst = con.prepareStatement("delete from emp_offboard_status where emp_id=? and offboard_id=? and section=1");
			pst.setInt(1, uF.parseToInt((String) session.getAttribute(EMPID)));
			pst.setInt(2, uF.parseToInt(getResignId()));
			pst.execute();
			pst.close();

			pst = con.prepareStatement("insert into  emp_offboard_status(status,offboard_id,section,emp_id,emp_status) values(?,?,?,?,?)");
			pst.setBoolean(1, false);
			pst.setInt(2, uF.parseToInt(getResignId()));
			pst.setInt(3, 1);
			pst.setInt(4, uF.parseToInt((String) session.getAttribute(EMPID)));
			pst.setBoolean(5, true);
			pst.execute();
			pst.close();
			
			String strReason = "Exit feedback form submited";
			insertEmpActivity(con, getId(), CF, strSessionEmpId, ACTIVITY_EXIT_FEEDBACK_FORM_SUBMIT_ID, strReason);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return "success";
	}
	
	
	private void insertMarks(Connection con, UtilityFunctions uF) {
		PreparedStatement pst = null;
		ResultSet rs = null;
//		Map<String,List<List<String>>> hmLevelQuestion = (Map<String,List<List<String>>>)request.getAttribute("hmLevelQuestion");
		List<List<String>> feedbackQueList = (List<List<String>>) request.getAttribute("feedbackQueList");
		Map<String, List<String>> hmQuestion = (Map<String, List<String>>) request.getAttribute("hmQuestion");
//		System.out.println("hmLevelQuestion in insert ============= > "+hmLevelQuestion);
		try {
			pst = con.prepareStatement("delete from appraisal_question_answer where emp_id=? and appraisal_id=? and user_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getAppId()));
			pst.setInt(3, uF.parseToInt(getId()));
			pst.execute();
			pst.close();
			
//			Set keys=hmLevelQuestion.keySet();
//			Iterator it=keys.iterator();
//			while(it.hasNext()){
//				String key=(String)it.next();
//				List<List<String>> outerList =hmLevelQuestion.get(key);
			for (int i = 0; feedbackQueList != null && i < feedbackQueList.size(); i++) {
				List<String> innerlist = (List<String>) feedbackQueList.get(i);
				List<String> questioninnerList = hmQuestion.get(innerlist.get(1));
				String weightage = innerlist.get(2);
				String appraisal_level_id = innerlist.get(13);
				String scorecard_id = innerlist.get(14);
				String attribute = innerlist.get(11);
				String givenAnswer = null;
				String other_id = innerlist.get(15);
				double marks = 0;
				String remark = null;
				String ansComment = null;
				if (uF.parseToInt(questioninnerList.get(8)) == 1) {
					ansComment = request.getParameter("anscomment" + innerlist.get(1)+"_"+questioninnerList.get(9));
					String[] correct = request.getParameterValues("correct" + innerlist.get(1)+"_"+questioninnerList.get(9));
					remark = request.getParameter("" + innerlist.get(1)+"_"+questioninnerList.get(9));
					String correctanswer = questioninnerList.get(6);
					for (int k = 0; correct != null && k < correct.length; k++) {
						if (k == 0) {
							givenAnswer = correct[k] + ",";
						} else {
							givenAnswer += correct[k] + ",";
						}
					}
					if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
						marks = uF.parseToDouble(weightage);
					}

				} else if (uF.parseToInt(questioninnerList.get(8)) == 2) {
					ansComment = request.getParameter("anscomment" + innerlist.get(1)+"_"+questioninnerList.get(9));
					String[] correct = request.getParameterValues("correct" + innerlist.get(1)+"_"+questioninnerList.get(9));
					for (int k = 0; correct != null && k < correct.length; k++) {
						if (k == 0) {
							givenAnswer = correct[k] + ",";
						} else {
							givenAnswer += correct[k] + ",";
						}
					}
					String correctanswer = questioninnerList.get(6);

					if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
						marks = uF.parseToDouble(weightage);
					} else {
						marks = 0;
					}

				} else if (uF.parseToInt(questioninnerList.get(8)) == 3) { 
					ansComment = request.getParameter("anscomment" + innerlist.get(1)+"_"+questioninnerList.get(9));
					marks = uF.parseToDouble(request.getParameter("marks" + innerlist.get(1)+"_"+questioninnerList.get(9)));
					
				} else if (uF.parseToInt(questioninnerList.get(8)) == 4) {
					ansComment = request.getParameter("anscomment" + innerlist.get(1)+"_"+questioninnerList.get(9));
					givenAnswer = request.getParameter("" + innerlist.get(1)+"_"+questioninnerList.get(9));
					marks = (uF.parseToDouble(givenAnswer) * uF.parseToDouble(weightage)) / 100;

				} else if (uF.parseToInt(questioninnerList.get(8)) == 5) {
					ansComment = request.getParameter("anscomment" + innerlist.get(1)+"_"+questioninnerList.get(9));
					givenAnswer = request.getParameter("" + innerlist.get(1)+"_"+questioninnerList.get(9)) + ",";
					String answer = questioninnerList.get(6);
					if (givenAnswer != null && answer != null && answer.contains(givenAnswer)) {
						marks = uF.parseToDouble(weightage);
					}
					
				} else if (uF.parseToInt(questioninnerList.get(8)) == 6) {
					ansComment = request.getParameter("anscomment" + innerlist.get(1)+"_"+questioninnerList.get(9));
					givenAnswer = request.getParameter("" + innerlist.get(1)+"_"+questioninnerList.get(9)) + ",";
					String answer = questioninnerList.get(6);
					if (givenAnswer != null && answer != null && answer.contains(givenAnswer)) {
						marks = uF.parseToDouble(weightage);
					}

				} else if (uF.parseToInt(questioninnerList.get(8)) == 7) {
					ansComment = request.getParameter("anscomment" + innerlist.get(1)+"_"+questioninnerList.get(9));
					givenAnswer = request.getParameter("" + innerlist.get(1)+"_"+questioninnerList.get(9));
					marks = uF.parseToDouble(request.getParameter("marks" + innerlist.get(1)+"_"+questioninnerList.get(9)));
					weightage = request.getParameter("outofmarks" + innerlist.get(1)+"_"+questioninnerList.get(9));

				} else if (uF.parseToInt(questioninnerList.get(8)) == 8) {
					ansComment = request.getParameter("anscomment" + innerlist.get(1)+"_"+questioninnerList.get(9));
					givenAnswer = request.getParameter("correct" + innerlist.get(1)+"_"+questioninnerList.get(9)) + ",";
					String correctanswer = questioninnerList.get(6);
					if (givenAnswer != null && correctanswer != null && correctanswer.contains(givenAnswer)) {
						marks = uF.parseToDouble(weightage);
					}

				} else if (uF.parseToInt(questioninnerList.get(8)) == 9) {
					ansComment = request.getParameter("anscomment" + innerlist.get(1)+"_"+questioninnerList.get(9));
					String[] correct = request.getParameterValues("correct" + innerlist.get(1)+"_"+questioninnerList.get(9));
					for (int k = 0; correct != null && k < correct.length; k++) {
						if (k == 0) {
							givenAnswer = correct[k] + ",";
						} else {
							givenAnswer += correct[k] + ",";
						}
					}
					String correctanswer = questioninnerList.get(6);
					if (correctanswer != null && givenAnswer != null && givenAnswer.contains(correctanswer)) {
						marks = uF.parseToDouble(weightage);
					}
				} else if (uF.parseToInt(questioninnerList.get(8)) == 10) {
					ansComment = request.getParameter("anscomment" + innerlist.get(1)+"_"+questioninnerList.get(9));
					marks = uF.parseToDouble(request.getParameter("marks" + innerlist.get(1)+"_"+questioninnerList.get(9)));
					String a = request.getParameter("a" + innerlist.get(1)+"_"+questioninnerList.get(9));
					String b = request.getParameter("b" + innerlist.get(1)+"_"+questioninnerList.get(9));
					String c = request.getParameter("c" + innerlist.get(1)+"_"+questioninnerList.get(9));
					String d = request.getParameter("d" + innerlist.get(1)+"_"+questioninnerList.get(9));

					givenAnswer = uF.showData(a, "") + " :_:" + uF.showData(b, "") + " :_:" + uF.showData(c, "") + " :_: " + uF.showData(d, "");

				} else if (uF.parseToInt(questioninnerList.get(8)) == 11) {
					ansComment = request.getParameter("anscomment" + innerlist.get(1)+"_"+questioninnerList.get(9));
					String rating = request.getParameter("gradewithrating" + innerlist.get(1)+"_"+questioninnerList.get(9));
					marks = uF.parseToDouble(rating) * uF.parseToDouble(weightage) / 5;

				}else if (uF.parseToInt(questioninnerList.get(8)) == 12) {
					ansComment = request.getParameter("anscomment" + innerlist.get(1)+"_"+questioninnerList.get(9));
					givenAnswer = request.getParameter("" + innerlist.get(1)+"_"+questioninnerList.get(9));
				}
 
				pst = con.prepareStatement("insert into appraisal_question_answer(emp_id,answer,appraisal_id,question_id,user_id,user_type_id," +
						"attempted_on,weightage,marks,appraisal_level_id,scorecard_id,appraisal_attribute,remark,other_id," +
						"appraisal_question_details_id,section_id,answers_comment)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getId()));
				pst.setString(2, givenAnswer);
				pst.setInt(3, uF.parseToInt(getAppId()));
				pst.setInt(4, uF.parseToInt(innerlist.get(1)));
				pst.setInt(5, uF.parseToInt(strSessionEmpId));
				pst.setInt(6, 0);
				pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDouble(8, uF.parseToDouble(weightage));
				pst.setDouble(9, marks);
				pst.setInt(10, uF.parseToInt(appraisal_level_id));
				pst.setInt(11, uF.parseToInt(scorecard_id));
				pst.setInt(12, uF.parseToInt(attribute));
				pst.setString(13, remark);
				pst.setInt(14, uF.parseToInt(other_id));
				pst.setInt(15, uF.parseToInt(innerlist.get(0)));
				pst.setInt(16, 0);
				pst.setString(17, ansComment);
				pst.execute();
				pst.close();

			}
//		}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	

	public void getEmployeeDetails() {

		UtilityFunctions uF = new UtilityFunctions();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		con = db.makeConnection(con);

		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select emp_id,off_board_type,entry_date,approved_1_by,emp_fname,emp_mname,emp_lname,emp_reason,approved_1_reason," +
					"approved_2_reason,notice_days,last_day_date,emp_status from emp_off_board eob,employee_personal_details epd where epd.emp_per_id=eob.approved_1_by and approved_1=? and approved_2=? and emp_id=?");
			pst.setInt(1, 1);
			pst.setInt(2, 1);
			pst.setInt(3, uF.parseToInt(getId()));
//			System.out.println("pst==>"+pst);
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
			
				
				empMap.put("ACCEPTED_BY", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
				empMap.put("MANAGER_APPROVE_REASON", rs.getString("approved_1_reason"));
				empMap.put("HR_MANAGER_APPROVE_REASON", rs.getString("approved_2_reason"));
				
//				System.out.println("notice_days==>"+ rs.getString("notice_days"));
				empMap.put("NOTICE_PERIOD", rs.getString("notice_days"));
				empMap.put("EMP_STATUS", rs.getString("emp_status"));
			}
			rs.close();
			pst.close();
			viewProfile(con, getId());
			/*pst = con.prepareStatement("select emp_fname,emp_lname,dept_name,wlocation_name,designation_name,emp_per_id,empcode from  employee_personal_details epd,employee_official_details eod,department_info di,work_location_info wli,grades_details gd,designation_details dd where gd.grade_id=(select grade_id from employee_official_details where emp_id=?) and  gd.designation_id=dd.designation_id and epd.emp_per_id=? and wli.wlocation_id=di.wlocation_id and eod.depart_id=di.dept_id and emp_id=?");
			pst.setInt(1, uF.parseToInt(getId()));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getId()));
			rs = pst.executeQuery();
			while (rs.next()) {
				empMap.put("EMP_FNAME", rs.getString("emp_fname"));
				empMap.put("EMP_LNAME", rs.getString("emp_lname"));
				empMap.put("DEPART_NAME", rs.getString("dept_name"));
				empMap.put("WLOCATION_NAME", rs.getString("wlocation_name"));
				empMap.put("DESIGNATION_NAME", rs.getString("designation_name"));
				empMap.put("EMP_CODE", rs.getString("empcode"));
			}*/
			
			request.setAttribute("empDetailsMp", empMap);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			
			db.closeStatements(pst);
			db.closeResultSet(rs);
			db.closeConnection(con);
		}

	}

	
	public void viewProfile(Connection con, String strEmpIdReq) {

		UtilityFunctions uF = new UtilityFunctions();

		try {
			
			CF.getEmpProfileDetail(con, request, session, CF, uF, strUserType, strEmpIdReq);
			request.setAttribute(TITLE, "Exit Form");

			List<List<String>> alSkills = new ArrayList<List<String>>();
			alSkills = CF.selectSkills(con, uF.parseToInt(strEmpIdReq));
			request.setAttribute("alSkills", alSkills);
			
//			request.setAttribute("alActivityDetails", alActivityDetails);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
//		return SUCCESS;
	}

	
	public boolean getExitFeedbackQueData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean flag = false;
		try {
			con = db.makeConnection(con);
//			StringBuilder sb = new StringBuilder("select * from appraisal_question_details where appraisal_id=? and appraisal_level_id=?");
			StringBuilder sb = new StringBuilder("select appraisal_details_id from appraisal_details where form_type = "+EXIT_FEEDBACK_FORM+" and is_publish = true");
			pst = con.prepareStatement(sb.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				setAppId(rs.getString("appraisal_details_id"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbQuery = new StringBuilder("select * from appraisal_question_details where appraisal_id = ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(getAppId()));
//			System.out.println("pst ====>>>>> "  + pst);
			rs = pst.executeQuery();
//			Map<String,List<List<String>>> hmLevelQuestion=new HashMap<String,List<List<String>>>();
			List<List<String>> feedbackQueList = new ArrayList<List<String>>();
			while (rs.next()) {
//				List<List<String>> outerList =hmLevelQuestion.get(rs.getString("appraisal_level_id"));
//				if(outerList==null)outerList=new ArrayList<List<String>>();
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("appraisal_question_details_id"));
				innerList.add(rs.getString("question_id"));
				innerList.add(rs.getString("weightage"));
				innerList.add(rs.getString("client"));
				innerList.add(rs.getString("appraisal_id"));
				innerList.add(rs.getString("plan_id"));
				innerList.add(rs.getString("hr")); 
				innerList.add(rs.getString("manager"));
				innerList.add(rs.getString("peer"));
				innerList.add(rs.getString("self"));
				innerList.add(rs.getString("measure_id"));
				innerList.add(rs.getString("attribute_id"));
				innerList.add(rs.getString("other_short_description"));
				innerList.add(rs.getString("appraisal_level_id"));
				innerList.add(rs.getString("scorecard_id"));
				innerList.add(rs.getString("other_id"));
				feedbackQueList.add(innerList);
//				hmLevelQuestion.put(rs.getString("appraisal_level_id"), outerList);
			}
			rs.close();
			pst.close();
			
//			System.out.println("feedbackQueList ====>>> " + feedbackQueList);
			
//			request.setAttribute("hmLevelQuestion", hmLevelQuestion);
			request.setAttribute("feedbackQueList", feedbackQueList);
			
			getExitFeedbackQuestionMap(con, uF, getAppId());
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return flag;
	}
	
	
	public void getExitFeedbackQuestionMap(Connection con, UtilityFunctions uF, String appId) {

		PreparedStatement pst = null;
		ResultSet rs = null;
//		Map<String, String> AppraisalQuestion = new HashMap<String, String>();
		Map<String, List<String>> hmQuestion = new HashMap<String, List<String>>();
		try {

			pst = con.prepareStatement("select * from question_bank qb, appraisal_question_details aqd where qb.question_bank_id=aqd.question_id and appraisal_id=?");
			pst.setInt(1, uF.parseToInt(appId));
			rs = pst.executeQuery();
			List<List<String>> outerList = new ArrayList<List<String>>();

			while (rs.next()) {
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("question_bank_id"));
				innerList.add(rs.getString("question_text"));
				innerList.add(rs.getString("option_a"));
				innerList.add(rs.getString("option_b"));
				innerList.add(rs.getString("option_c"));
				innerList.add(rs.getString("option_d"));
				innerList.add(rs.getString("correct_ans"));
				innerList.add(rs.getString("is_add"));
				innerList.add(rs.getString("question_type"));
				innerList.add(rs.getString("appraisal_question_details_id"));
				
				outerList.add(innerList);
				hmQuestion.put(rs.getString("question_bank_id"), innerList);

//				AppraisalQuestion.put(rs.getString("question_bank_id"), rs.getString("question_text"));

			}
			rs.close();
			pst.close();
//			System.out.println("hmQuestion ===>> " + hmQuestion);
			request.setAttribute("hmQuestion", hmQuestion);
			
			getQuestionsAnswer(con, uF, appId);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
//		return AppraisalQuestion;
	}
	
	
	public void getQuestionsAnswer(Connection con, UtilityFunctions uF, String appId) {

		PreparedStatement pst = null;
		ResultSet rs = null;

		try {

			pst = con.prepareStatement("select * from appraisal_question_answer  where appraisal_id = ? and emp_id = ? and user_id = ?");
			pst.setInt(1, uF.parseToInt(appId));
			pst.setInt(2, uF.parseToInt(getId()));
			pst.setInt(3, uF.parseToInt(getId()));
//			pst.setInt(5, uF.parseToInt(getUserType()));
			rs = pst.executeQuery();
			Map<String, Map<String, String>> questionanswerMp = new HashMap<String, Map<String, String>>();
			Map<String, String> hmFilledDate = new HashMap<String, String>();
			while (rs.next()) {

				Map<String, String> innerMp = new HashMap<String, String>();
				innerMp.put("ANSWER", rs.getString("answer"));
				innerMp.put("REMARK", rs.getString("remark"));
				innerMp.put("MARKS", rs.getString("marks"));
				innerMp.put("APP_QUE_ANS_ID", rs.getString("appraisal_question_answer_id"));
				innerMp.put("WEIGHTAGE", rs.getString("weightage"));
				innerMp.put("ANSWERCOMMENT", rs.getString("answers_comment"));
				hmFilledDate.put("FILLED_DATE", uF.getDateFormat(rs.getString("attempted_on"), DBDATE, CF.getStrReportDateFormat()));
				if (uF.parseToInt(rs.getString("scorecard_id")) != 0)
					questionanswerMp.put(rs.getString("scorecard_id") + "question" + rs.getString("question_id"), innerMp);
				else {
					questionanswerMp.put(rs.getString("other_id") + "question" + rs.getString("question_id"), innerMp);
				}
			}
			rs.close();
			pst.close();
//			System.out.println("hmFilledDate ===>> " + hmFilledDate);
			request.setAttribute("questionanswerMp", questionanswerMp);
			request.setAttribute("hmFilledDate", hmFilledDate);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
	}
	
	
	private void getQuestionSubType(UtilityFunctions uF) {
		Map<String, List<List<String>>> answertypeSub = new HashMap<String, List<List<String>>>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);

		con = db.makeConnection(con);

		try {

			pst = con.prepareStatement("select * from appraisal_answer_type_sub order by appraisal_answer_type_sub_id");
			rst = pst.executeQuery();
			while (rst.next()) {
				List<List<String>> outerList = answertypeSub.get(rst.getString("answer_type_id"));
				if (outerList == null)
					outerList = new ArrayList<List<String>>();

				List<String> innerList = new ArrayList<String>();
				innerList.add(rst.getString("score"));
				innerList.add(rst.getString("score_label"));
				outerList.add(innerList);
				answertypeSub.put(rst.getString("answer_type_id"), outerList);
			}
			rst.close();
			pst.close();
//			System.out.println("answertypeSub ===>> " + answertypeSub);
			request.setAttribute("answertypeSub", answertypeSub);

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {

			
			db.closeStatements(pst);
			db.closeResultSet(rst);
			db.closeConnection(con);
		}
	}
	

	
public void insertEmpActivity(Connection con,String strEmpId, CommonFunctions CF, String strSessionEmpId, String activity_id, String strReason) {
		
		UtilityFunctions uF = new UtilityFunctions();
		PreparedStatement pst = null;
		
		try {
			String strOrg = CF.getEmpOrgId(con, uF, strEmpId);
			String strWLocation = CF.getEmpWlocationMap(con).get(strEmpId); 
			String strDepartment = CF.getEmpDepartmentMap(con).get(strEmpId);
			String strLevel = CF.getEmpLevelMap(con).get(strEmpId);
			String strService = CF.getEmpServiceMap(con).get(strEmpId);
			String strDesignation = CF.getEmpDesigMapId(con).get(strEmpId);
			String strGrade = CF.getEmpGenderMap(con).get(strEmpId);
			String strNewStatus = CF.getEmpEmploymentStatusMap(con).get(strEmpId);

//			pst = con.prepareStatement(insertEmpActivity);
			pst = con.prepareStatement("insert into employee_activity_details (wlocation_id, department_id, level_id, desig_id, grade_id, " +
					"emp_status_code, activity_id, reason, effective_date, entry_date, user_id, emp_id, notice_period, probation_period, appraisal_id, " +
					"extend_probation_period, org_id,service_id,increment_type,increment_percent) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?)");
			pst.setInt(1, uF.parseToInt(strWLocation));
			pst.setInt(2, uF.parseToInt(strDepartment));
			pst.setInt(3, uF.parseToInt(strLevel));
			pst.setInt(4, uF.parseToInt(strDesignation));
			pst.setInt(5, uF.parseToInt(strGrade));
			pst.setString(6, strNewStatus);
			pst.setInt(7, uF.parseToInt(activity_id));
			pst.setString(8, strReason);
			pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(11, uF.parseToInt(strSessionEmpId));
			pst.setInt(12,  uF.parseToInt(strEmpId));
			pst.setInt(13,  0);
			pst.setInt(14,  0);
			pst.setInt(15, 0);
			pst.setInt(16, 0);
			pst.setInt(17, uF.parseToInt(strOrg));
			pst.setString(18, strService);
			pst.setInt(19, 0);
			pst.setDouble(20, 0);
			pst.execute();
			pst.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		} finally {
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

private void getApprovalStatus(String effectivetype) {

	Connection con = null;
	PreparedStatement pst = null;
	ResultSet rs = null;
	Database db = new Database();
	db.setRequest(request);
	UtilityFunctions uF = new UtilityFunctions();

	try {
		con = db.makeConnection(con);
		
		Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
		Map<String, String> hmUserTypeMap = CF.getUserTypeMap(con);
		if (hmUserTypeMap == null) hmUserTypeMap = new HashMap<String, String>();
		Map<String, String> hmEmpUserId = CF.getEmpUserIdMap(con);
		if(hmEmpUserId == null) hmEmpUserId = new HashMap<String, String>();
		
		pst = con.prepareStatement("select * from work_flow_details where effective_id=? and (effective_type='"+WORK_FLOW_RESIGN+"' or effective_type='"+WORK_FLOW_TERMINATION+"') order by member_position");
		pst.setInt(1, uF.parseToInt(getResignId()));
//		pst.setString(2, effectivetype);
//		System.out.println("getApproval status pst====>"+pst);
		rs = pst.executeQuery();
		Map<String, String> hmApprovalStatus = new HashMap<String, String>();
		String strMemberPosition = null;
		int step = 1;
		boolean flag = false;
		StringBuilder resigAcceptedBy = null;
		while (rs.next()) {

			String memberStep = hmApprovalStatus.get(rs.getString("effective_id"));
			String strUserTypeName = uF.parseToInt(rs.getString("user_type_id")) > 0 ?  uF.showData(hmUserTypeMap.get(rs.getString("user_type_id")), "") : "";
			String strApproveMemTick = "";
			String strApproveMemReason = "";
		
			
			if (uF.parseToInt(rs.getString("is_approved")) == 1 || uF.parseToInt(rs.getString("is_approved")) == -1) {
				if (uF.parseToInt(rs.getString("is_approved")) == 1){
					 /*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
					strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\"  title=\"Approved\"></i>";
					
				} else if (uF.parseToInt(rs.getString("is_approved")) == -1){
					/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
					strApproveMemTick = "<i class=\"fa fa-times cross\" aria-hidden=\"true\"  title=\"Denied\"></i>";
				}
				if(rs.getString("reason")!=null && !rs.getString("reason").trim().equals("") && !rs.getString("reason").trim().equalsIgnoreCase("NULL")){
					strApproveMemReason = "[Reason: "+rs.getString("reason").trim()+"]";
				}
			} else if (uF.parseToInt(rs.getString("is_approved")) == 0){
				flag = true;
			}
			
//			System.out.println("strApproveMemReason==>"+strApproveMemReason);

			if (memberStep == null) {
				memberStep = "<strong>" + strUserTypeName + ":</strong> " + hmEmployeeNameMap.get(rs.getString("emp_id").trim())+strApproveMemReason+" "+ strApproveMemTick;
				strMemberPosition = rs.getString("member_position");
				
				
			} else {
				if (strMemberPosition != null && strMemberPosition.equals(rs.getString("member_position"))) {
					memberStep += ", "+ strApproveMemTick  + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strUserTypeName + strApproveMemReason;
					
				} else {
					step++;
					strMemberPosition = rs.getString("member_position");
				
					// memberStep+=" ======> "+hmEmployeeNameMap.get(rs.getString("emp_id").trim())+strUserTypeName;
					//memberStep += "<br/><strong>Step " + step + ".</strong> "+ strApproveMemTick + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strUserTypeName + strApproveMemReason;
					memberStep += "<br/><strong>"+strUserTypeName +  ":</strong> " + hmEmployeeNameMap.get(rs.getString("emp_id").trim()) + strApproveMemReason+" "+ strApproveMemTick;
				}
			}
			if (uF.parseToInt(rs.getString("is_approved")) == 1 || uF.parseToInt(rs.getString("is_approved")) == -1) {
				if(resigAcceptedBy == null) {
					resigAcceptedBy = new StringBuilder();
					resigAcceptedBy.append(hmEmployeeNameMap.get(rs.getString("emp_id").trim()));
				} else {
					resigAcceptedBy.append(", "+hmEmployeeNameMap.get(rs.getString("emp_id").trim()));
				}
			
			}
//			System.out.println("effective_id==>"+rs.getString("effective_id")+"==>memberStep==>"+memberStep+"==>flag==>"+flag);
			hmApprovalStatus.put(rs.getString("work_flow_id"), memberStep);
		}
		rs.close();
		pst.close();
		request.setAttribute("hmApprovalStatus", hmApprovalStatus);

		StringBuilder sbReason = new StringBuilder();;
		if(flag){
			
		//	if(effectivetype.equals(WORK_FLOW_RESIGN)  || effectivetype.equals(WORK_FLOW_TERMINATION) ) {
				if(resigAcceptedBy == null) {
					resigAcceptedBy = new StringBuilder();
				}
				pst = con.prepareStatement("select * from emp_off_board where off_board_id=? and approved_1 > 0 and approved_1_by > 0");
				pst.setInt(1, uF.parseToInt(getResignId()));
				rs = pst.executeQuery();
				while (rs.next()) {
					String strApproveMemTick = "";
					String strApproveMemReason = "";
					if (uF.parseToInt(rs.getString("approved_1")) == 1){
						 /*strApproveMemTick = "<img border=\"0\" src=\"images1/tick.png\" title=\"Approved\"/>";*/
						strApproveMemTick = "<i class=\"fa fa-check checknew\" aria-hidden=\"true\"  title=\"Approved\"></i>";
						
					} else if (uF.parseToInt(rs.getString("approved_1")) == -1){
						/*strApproveMemTick = "<img border=\"0\" src=\"images1/cross.png\" title=\"Denied\"/>";*/
						strApproveMemTick = "<i class=\"fa fa-times-circle cross\" aria-hidden=\"true\"  title=\"Denied\"></i>";
					}
					if(rs.getString("approved_1_reason")!=null && !rs.getString("approved_1_reason").trim().equals("") && !rs.getString("approved_1_reason").trim().equalsIgnoreCase("NULL")){
						strApproveMemReason = "[Reason: "+rs.getString("approved_1_reason").trim()+"]";
					}
					
					
					resigAcceptedBy.append(uF.showData(hmEmployeeNameMap.get(rs.getString("approved_1_by").trim()), "-"));
					
					String strUserTypeName = uF.parseToInt(hmEmpUserId.get(rs.getString("approved_1_by"))) > 0 ? " (<strong>"+ uF.showData(hmUserTypeMap.get(hmEmpUserId.get(rs.getString("approved_1_by"))), "") + "</strong>)" : "";
					
					//sbReason.append(strApproveMemTick+uF.showData(hmEmployeeNameMap.get(rs.getString("approved_1_by").trim()), "-")+ strUserTypeName +strApproveMemReason);
					sbReason.append(strUserTypeName+":"+uF.showData(hmEmployeeNameMap.get(rs.getString("approved_1_by").trim()), "-")+strApproveMemReason+" "+strApproveMemTick);
				}	
		}
		if(resigAcceptedBy == null) resigAcceptedBy = new StringBuilder();
	
		request.setAttribute("resigAcceptedBy", resigAcceptedBy.toString());
		request.setAttribute("strReason", sbReason.toString());

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

public File[] getDocument() {
	return document;
}

public void setDocument(File[] document) {
	this.document = document;
}

public String[] getDocumentFileName() {
	return documentFileName;
}

public void setDocumentFileName(String[] documentFileName) {
	this.documentFileName = documentFileName;
}

public boolean isFlag() {
	return flag;
}

public void setFlag(boolean flag) {
	this.flag = flag;
}

public String getOperation() {
	return operation;
}

public void setOperation(String operation) {
	this.operation = operation;
}

public String getResignId() {
	return resignId;
}

public void setResignId(String resignId) {
	this.resignId = resignId;
}

public String getId() {
	return id;
}

public void setId(String id) {
	this.id = id;
}

public String getAppId() {
	return appId;
}

public void setAppId(String appId) {
	this.appId = appId;
}

public String getFrom() {
	return from;
}

public void setFrom(String from) {
	this.from = from;
}

}