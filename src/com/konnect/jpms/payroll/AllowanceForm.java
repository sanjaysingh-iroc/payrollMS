package com.konnect.jpms.payroll;

import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.export.payroll.ExcelSheetDesign;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillProductionLine;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class AllowanceForm extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpSessionId = null;
	String strUserType = null;
	CommonFunctions CF = null;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	
	String paycycle;
	String f_org;
	String[] f_strWLocation;
	String f_level;
	String[] f_department;
	String[] f_service;
	String f_salaryhead;

	List<FillPayCycles> paycycleList;
	List<FillOrganisation> organisationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillWLocation> wLocationList;
	List<FillSalaryHeads> salaryHeadList;
	
	String formType;
	
	String productionLineId;
	List<FillProductionLine> productionLineList;

	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null) return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		strEmpSessionId = (String) session.getAttribute(EMPID);
		strUserType = (String) session.getAttribute(USERTYPE);
		
		request.setAttribute(PAGE, "/jsp/payroll/AllowanceForm.jsp");
		request.setAttribute(TITLE, "Allowance Form");
		
		boolean isProductionLine = CF.getIsProductionLine();
		request.setAttribute("isProductionLine", ""+isProductionLine);
		
		if(uF.parseToInt(getF_org()) == 0){
			setF_org((String)session.getAttribute(ORGID));
			setF_level(null);
			setF_salaryhead(null);
			setPaycycle(null);
			setProductionLineId(null);
		}
		
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
		if(getStrSbu() != null && !getStrSbu().equals("")) {
			setF_service(getStrSbu().split(","));
		} else {
			setF_service(null);
		}
		
		String[] strPayCycleDates;
		if (getPaycycle() != null && !getPaycycle().trim().equals("") && !getPaycycle().trim().equalsIgnoreCase("NULL")) {
			String str = URLDecoder.decode(getPaycycle());
			strPayCycleDates = str.split("-");
		} else {
			strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(), request);
			setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
		}
		
		if(isProductionLine){
			boolean isSalaryHeadProdLine = checkSalaryHeadProductionLine(uF);
			request.setAttribute("isSalaryHeadProdLine", ""+isSalaryHeadProdLine);
			
			if(isSalaryHeadProdLine && uF.parseToInt(getF_salaryhead()) > 0){
				if(uF.parseToInt(getProductionLineId()) > 0){
					getAllowanceConditionAndLogic(uF, strPayCycleDates);
					
					if(getFormType() != null && getFormType().trim().equalsIgnoreCase("approve")){
						approveAllowanceAmount(uF, strPayCycleDates[0], strPayCycleDates[1], strPayCycleDates[2]);
					} else if(getFormType() != null && getFormType().trim().equalsIgnoreCase("download")){
						downloadAllowance(uF, strPayCycleDates[0], strPayCycleDates[1], strPayCycleDates[2]);
						return null;
					}
					setFormType(null);
					viewAllowanceWithProductionLine(uF,strPayCycleDates);
				}
			} else if(uF.parseToInt(getF_salaryhead()) > 0){
				getAllowanceConditionAndLogic(uF, strPayCycleDates);
				
				if(getFormType() != null && getFormType().trim().equalsIgnoreCase("approve")){
					approveAllowanceAmount(uF, strPayCycleDates[0], strPayCycleDates[1], strPayCycleDates[2]);
				} else if(getFormType() != null && getFormType().trim().equalsIgnoreCase("download")){
					downloadAllowance(uF, strPayCycleDates[0], strPayCycleDates[1], strPayCycleDates[2]);
					return null;
				}
				setFormType(null);
				viewAllowanceWithProductionLine(uF,strPayCycleDates);
			}
		} else {	
			if(uF.parseToInt(getF_salaryhead()) > 0){
				getAllowanceConditionAndLogic(uF, strPayCycleDates);
				
				if(getFormType() != null && getFormType().trim().equalsIgnoreCase("approve")){
					approveAllowanceAmount(uF, strPayCycleDates[0], strPayCycleDates[1], strPayCycleDates[2]);
				} else if(getFormType() != null && getFormType().trim().equalsIgnoreCase("download")){
					downloadAllowance(uF, strPayCycleDates[0], strPayCycleDates[1], strPayCycleDates[2]);
					return null;
				}
				setFormType(null);
				viewAllowance(uF,strPayCycleDates);
			}
		}
		setFormType(null);
		return loadAllowance(uF);
	}
	
	private boolean checkSalaryHeadProductionLine(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		boolean isSalaryHeadProdLine = false;
		try {
			con = db.makeConnection(con);

			pst = con.prepareStatement("select * FROM production_line_details where org_id=? and production_line_id " +
					"in (select production_line_id from production_line_heads where level_id=? and salary_heads like '%,"+getF_salaryhead()+",%')");
			pst.setInt(1, uF.parseToInt(getF_org()));
			pst.setInt(2, uF.parseToInt(getF_level()));
			rs = pst.executeQuery();			
			if(rs.next()){
				isSalaryHeadProdLine = true;
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
		return isSalaryHeadProdLine;		
	}

	private void viewAllowanceWithProductionLine(UtilityFunctions uF, String[] strPayCycleDates) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			if(hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String, String>();
			
			List<String> alEmpIds = (List<String>) request.getAttribute("alEmpIds");
			if(alEmpIds == null) alEmpIds = new ArrayList<String>();
			int nEmp = alEmpIds.size();
			if(alEmpIds.size() > 0){
				String strEmpIds = StringUtils.join(alEmpIds.toArray(),",");
				
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
				
				pst = con.prepareStatement("select * from allowance_individual_details where emp_id in ("+strEmpIds+") and paid_from = ? " +
						"and paid_to=? and pay_paycycle=? and salary_head_id=? and production_line_id=?");
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
				pst.setInt(4, uF.parseToInt(getF_salaryhead()));
				pst.setInt(5, uF.parseToInt(getProductionLineId()));
				rs = pst.executeQuery();
				Map<String, String> hmAllowance = new HashMap<String, String>();
				Map<String, String> hmAllowanceId = new HashMap<String, String>();
				Map<String, String> hmAllowanceValue = new HashMap<String, String>();
				while (rs.next()) {
					hmAllowance.put(rs.getString("emp_id"),rs.getString("is_approved"));
					hmAllowanceId.put(rs.getString("emp_id"),rs.getString("allowance_id"));
					hmAllowanceValue.put(rs.getString("emp_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("pay_amount"))));
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmAllowance", hmAllowance);
				request.setAttribute("hmAllowanceId", hmAllowanceId);
				request.setAttribute("hmAllowanceValue", hmAllowanceValue);				
				
				List<String> alConditionId = (List<String>) request.getAttribute("alConditionId"); 
				if(alConditionId == null) alConditionId = new ArrayList<String>();
				Map<String, List<Map<String, String>>> hmConditionLogic = (Map<String, List<Map<String, String>>>) request.getAttribute("hmConditionLogic");;
				if(hmConditionLogic == null) hmConditionLogic = new HashMap<String, List<Map<String, String>>>();
				
//				System.out.println("hmConditionLogic====>"+hmConditionLogic);
				
				if(alConditionId.size() > 0) {
					List<Map<String, String>> alCondition = (List<Map<String,String>>)request.getAttribute("alCondition");;
					if(alCondition == null) alCondition = new ArrayList<Map<String,String>>();
					
					
					/**
					 * No. of days
					 * */
//					pst = con.prepareStatement("select count(ad.in_out_timestamp) as cnt, rd.emp_id from attendance_details ad, roster_details rd, " +
//							"employee_official_details eod where ad.emp_id=rd.emp_id and ad.service_id = rd.service_id and eod.emp_id = rd.emp_id " +
//							"and eod.emp_id = ad.emp_id and rd._date = to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.in_out = 'OUT' " +
//							"and rd._date between ? and ? and ad.emp_id > 0 and ad.emp_id in ("+strEmpIds+") group by  rd.emp_id");
					
//					pst = con.prepareStatement("select * from approve_attendance where approve_from=? and approve_to=? " +
//							"and paycycle=? and emp_id in ("+strEmpIds+")");
//					pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//					pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//					pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
////					System.out.println("pst====>"+pst);
//					rs = pst.executeQuery();
//					Map<String, String> hmEmpDay = new HashMap<String, String>();
//					while(rs.next()){
//						hmEmpDay.put(rs.getString("emp_id"), rs.getString("present_days"));
//					}
//					rs.close();
//					pst.close();
					
					Map<String, String> hmEmpDay = new HashMap<String, String>();
					for(int i = 0; i < nEmp; i++){
						String strEmpId = alEmpIds.get(i);
						
						pst = con.prepareStatement("select * from attendance_details where emp_id=? " +
								"and to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ? order by to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD'), in_out ");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
						rs = pst.executeQuery();
						Map<String, String> hmHalfDayAttendance = new HashMap<String, String>();
						Map<String,String> hmEmpAttendance = new HashMap<String,String>();
						while(rs.next()){
							hmEmpAttendance.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id")+"_"+rs.getString("emp_id"), "P");
							if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
								double workingHour=rs.getDouble("hours_worked");
								if(workingHour < 5.0d){
									hmHalfDayAttendance.put(rs.getString("emp_id")+"_"+uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBDATE, DATE_FORMAT),""+workingHour);
								}
							}
						}
						rs.close();
						pst.close();
							
						double  dblPresent = hmEmpAttendance.size();
						dblPresent -= hmHalfDayAttendance.size() * 0.5;
						hmEmpDay.put(strEmpId, ""+dblPresent);
					}
					
					/**
					 * No. of Days Absent 
					 * */
					pst = con.prepareStatement("select emp_id,absent_days from approve_attendance where emp_id in ("+strEmpIds+") and approve_from=? " +
							"and approve_to=? and paycycle=?");
					pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
//					System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					Map<String, String> hmEmpAbsentDays = new HashMap<String, String>();
					while(rs.next()){
						hmEmpAbsentDays.put(rs.getString("emp_id"), rs.getString("absent_days"));
					}
					rs.close();
					pst.close();
					
					
					/**
					 * No. of hours
					 * */
//					pst = con.prepareStatement("select sum(hours_worked) as hours_worked, rd.emp_id from attendance_details ad, roster_details rd," +
//							" employee_official_details eod where ad.emp_id=rd.emp_id and ad.service_id = rd.service_id and eod.emp_id = rd.emp_id " +
//							"and eod.emp_id = ad.emp_id and rd._date = to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.in_out = 'OUT' " +
//							"and rd._date between ? and ? and ad.emp_id in ("+strEmpIds+") group by rd.emp_id");
					pst = con.prepareStatement("select * from allowance_hours_details where emp_id in ("+strEmpIds+") and paycycle_from=? and paycycle_to=? and paycycle=?");
					pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
//					System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					Map<String, String> hmEmpHours = new HashMap<String, String>();
					while(rs.next()){
						hmEmpHours.put(rs.getString("emp_id"), rs.getString("allowance_hours"));
					}
					rs.close();
					pst.close();
					
					/**
					 * Allowance Logic
					 * */
					
					Map<String, Map<String, String>> hmConditionDayEmpCal = new HashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmConditionHourEmpCal = new HashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmConditionAchievedEmpCal = new HashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmConditionAbsentDayEmpCal = new HashMap<String, Map<String, String>>();
					
					Map<String, String> hmPaymentLogicAmt = new HashMap<String, String>();
					for(int j = 0; j < alCondition.size(); j++){ 
		    			Map<String,String> hmCondition = (Map<String,String>) alCondition.get(j);
						if(hmCondition == null) hmCondition = new HashMap<String, String>();
						
						List<Map<String, String>> alInner = (List<Map<String, String>>) hmConditionLogic.get(hmCondition.get("ALLOWANCE_CONDITION_ID"));
						if(alInner == null) alInner = new ArrayList<Map<String,String>>();
						
						if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_NO_OF_DAYS_ID && uF.parseToBoolean(hmCondition.get("IS_ADD_ATTENDANCE"))) {
							/**
							 * No. of days
							 * */
							if(hmEmpDay!=null && hmEmpDay.size() > 0){
								Iterator<String> it = hmEmpDay.keySet().iterator();
								Map<String, String> hmEmpDayCnt = new HashMap<String, String>();
								while(it.hasNext()){
									String strEmpId = it.next();
//									int nDayCnt = uF.parseToInt(hmEmpDay.get(strEmpId));
									double dblDayCnt = uF.parseToDouble(hmEmpDay.get(strEmpId));
									
									if(uF.parseToDouble(hmCondition.get("MIN_CONDITION")) <= dblDayCnt && dblDayCnt <= uF.parseToDouble(hmCondition.get("MAX_CONDITION"))){
										hmEmpDayCnt.put(strEmpId, ""+dblDayCnt);
										hmConditionDayEmpCal.put(hmCondition.get("ALLOWANCE_CONDITION_ID"), hmEmpDayCnt);
										
										for(int k = 0; k < alInner.size(); k++){
											Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
											if(hmLogic == null) hmLogic = new HashMap<String, String>();
											
											if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_ONLY_ID){
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")));
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_X_DAYS_ID){
												double dblAmt = uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")) * dblDayCnt;
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblAmt));
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_AND_PER_DAY_ID){
												double nDiff = (dblDayCnt - uF.parseToDouble(hmCondition.get("MIN_CONDITION")));
												nDiff += 1;
												double dblAmt = (uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")) + (uF.parseToDouble(hmLogic.get("PER_HOUR_DAY_AMOUNT")) * nDiff));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblAmt));
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_EQUAL_TO_SALARY_HEAD_ID){
												pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
														"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
														"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
														"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
														"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
														"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
												pst.setInt(1, uF.parseToInt(strEmpId));
												pst.setInt(2, uF.parseToInt(strEmpId));
												pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(4, uF.parseToInt(strEmpId));
												pst.setInt(5, uF.parseToInt(strEmpId));
												pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
												pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
	//											System.out.println("pst ===>> " + pst);
												rs = pst.executeQuery();
												double dblSalaryHeadAmt = 0.0d; 
												while(rs.next()){
													dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
												}
												rs.close();
												pst.close();
												
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
												
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_SALARY_HEAD_X_DAYS_ID){
												pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
														"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
														"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
														"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
														"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
														"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
												pst.setInt(1, uF.parseToInt(strEmpId));
												pst.setInt(2, uF.parseToInt(strEmpId));
												pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(4, uF.parseToInt(strEmpId));
												pst.setInt(5, uF.parseToInt(strEmpId));
												pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
												pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
	//											System.out.println("pst ===>> " + pst);
												rs = pst.executeQuery();
												double dblSalaryHeadAmt = 0.0d; 
												while(rs.next()){
													dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
												}
												rs.close();
												pst.close();
												
												double dblAmt = dblSalaryHeadAmt * dblDayCnt;
												
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblAmt));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
											}
										}
									}
								}
							} else {
								for(String strEmpId : alEmpIds){
									for(int k = 0; k < alInner.size(); k++){
										Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
										if(hmLogic == null) hmLogic = new HashMap<String, String>();
										pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
												"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
												"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
												"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
												"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
												"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
										pst.setInt(1, uF.parseToInt(strEmpId));
										pst.setInt(2, uF.parseToInt(strEmpId));
										pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(4, uF.parseToInt(strEmpId));
										pst.setInt(5, uF.parseToInt(strEmpId));
										pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
										pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
		//											System.out.println("pst ===>> " + pst);
										rs = pst.executeQuery();
										double dblSalaryHeadAmt = 0.0d; 
										while(rs.next()){
											dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
										}
										rs.close();
										pst.close();
										
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "false");
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
									}
								}
							}
						} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_NO_OF_HOURS_ID) {
							/**
							 * No. of Hours
							 * */
							
							if(hmEmpHours!=null && hmEmpHours.size() > 0){
								Iterator<String> it = hmEmpHours.keySet().iterator();
								Map<String, String> hmEmpHourCnt = new HashMap<String, String>();
								while(it.hasNext()){
									String strEmpId = it.next();
									double dblHours = uF.parseToDouble(hmEmpHours.get(strEmpId));
									if(uF.parseToDouble(hmCondition.get("MIN_CONDITION")) <= dblHours && dblHours <= uF.parseToDouble(hmCondition.get("MAX_CONDITION"))){
										hmEmpHourCnt.put(strEmpId, ""+dblHours);
										hmConditionHourEmpCal.put(hmCondition.get("ALLOWANCE_CONDITION_ID"), hmEmpHourCnt);
										
										for(int k = 0; k < alInner.size(); k++){
											Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
											if(hmLogic == null) hmLogic = new HashMap<String, String>();
											
											if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_ONLY_ID){
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmLogic.get("FIXED_AMOUNT"))));
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_X_HOURS_ID){
												double dblAmt = uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")) * dblHours;
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblAmt));
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_AND_PER_HOUR_ID){
												double dblDiff = (dblHours - uF.parseToInt(hmCondition.get("MIN_CONDITION")));
												dblDiff += 1;
												double dblAmt = (uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")) + (uF.parseToDouble(hmLogic.get("PER_HOUR_DAY_AMOUNT")) * dblDiff));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblAmt));
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_EQUAL_TO_SALARY_HEAD_ID){
												pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
														"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
														"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
														"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
														"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
														"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
												pst.setInt(1, uF.parseToInt(strEmpId));
												pst.setInt(2, uF.parseToInt(strEmpId));
												pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(4, uF.parseToInt(strEmpId));
												pst.setInt(5, uF.parseToInt(strEmpId));
												pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
												pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
	//											System.out.println("pst ===>> " + pst);
												rs = pst.executeQuery();
												double dblSalaryHeadAmt = 0.0d; 
												while(rs.next()){
													dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
												}
												rs.close();
												pst.close();
												
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
												
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_SALARY_HEAD_X_HOURS_ID){
												pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
														"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
														"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
														"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
														"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
														"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
												pst.setInt(1, uF.parseToInt(strEmpId));
												pst.setInt(2, uF.parseToInt(strEmpId));
												pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(4, uF.parseToInt(strEmpId));
												pst.setInt(5, uF.parseToInt(strEmpId));
												pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
												pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
	//											System.out.println("pst ===>> " + pst);
												rs = pst.executeQuery();
												double dblSalaryHeadAmt = 0.0d; 
												while(rs.next()){
													dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
												}
												rs.close();
												pst.close();
												
												double dblAmt = dblSalaryHeadAmt * dblHours;
												
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblAmt));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
											}
										}
									}
								}
							} else {
								for(String strEmpId : alEmpIds) {
									for(int k = 0; k < alInner.size(); k++) {
										Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
										if(hmLogic == null) hmLogic = new HashMap<String, String>();
										pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
												"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
												"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
												"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
												"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
												"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
										pst.setInt(1, uF.parseToInt(strEmpId));
										pst.setInt(2, uF.parseToInt(strEmpId));
										pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(4, uF.parseToInt(strEmpId));
										pst.setInt(5, uF.parseToInt(strEmpId));
										pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
										pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
//										System.out.println("pst ===>> " + pst);
										rs = pst.executeQuery();
										double dblSalaryHeadAmt = 0.0d; 
										while(rs.next()) {
											dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
										}
										rs.close();
										pst.close();
										
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "false");
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
									}
								}
							}
							
						} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_CUSTOM_FACTOR_ID) {
							for(String strEmpId : alEmpIds){ 
								for(int k = 0; k < alInner.size(); k++) {
									Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
									if(hmLogic == null) hmLogic = new HashMap<String, String>();
									
									if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_ONLY_ID) {
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")));
									} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_X_CUSTOM_ID){
									
									} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_EQUAL_TO_SALARY_HEAD_ID) {
										pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
												"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
												"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
												"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
												"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
												"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
										pst.setInt(1, uF.parseToInt(strEmpId));
										pst.setInt(2, uF.parseToInt(strEmpId));
										pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(4, uF.parseToInt(strEmpId));
										pst.setInt(5, uF.parseToInt(strEmpId));
										pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
										pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
//										System.out.println("pst ===>> " + pst);
										rs = pst.executeQuery();
										double dblSalaryHeadAmt = 0.0d; 
										while(rs.next()){
											dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
										}
										rs.close();
										pst.close();
										
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+dblSalaryHeadAmt);
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", ""+dblSalaryHeadAmt);
										
									} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_SALARY_HEAD_X_CUSTOM_ID){
										pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
												"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
												"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
												"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
												"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
												"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
										pst.setInt(1, uF.parseToInt(strEmpId));
										pst.setInt(2, uF.parseToInt(strEmpId));
										pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(4, uF.parseToInt(strEmpId));
										pst.setInt(5, uF.parseToInt(strEmpId));
										pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
										pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
//										System.out.println("pst ===>> " + pst);
										rs = pst.executeQuery();
										double dblSalaryHeadAmt = 0.0d; 
										while(rs.next()){
											dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
										}
										rs.close();
										pst.close();
										
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+dblSalaryHeadAmt);
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", ""+dblSalaryHeadAmt);
									}
								}	
							}
						} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_GOAL_KRA_TARGET_ID || uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_KRA_ID) {
							/**
							 * Goal Kra Targets
							 * */
							Map<String, String> hmEmpAchievedPercent = new HashMap<String, String>();
							if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_GOAL_KRA_TARGET_ID) {
								hmEmpAchievedPercent = getEmployeeAchievedPercentage(con, uF, hmCondition.get("ALLOWANCE_CONDITION_ID"));
							} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_KRA_ID) {
								hmEmpAchievedPercent = getEmployeeKRAPercentage(con, uF, hmCondition.get("ALLOWANCE_CONDITION_ID"));
							}
							
							
							if(hmEmpAchievedPercent!=null && hmEmpAchievedPercent.size() > 0) {
								Iterator<String> it = hmEmpAchievedPercent.keySet().iterator();
								Map<String, String> hmEmpAchieved = new HashMap<String, String>();
								while(it.hasNext()) {
									String strEmpId = it.next();
									double dblAchievedPercent = uF.parseToDouble(hmEmpAchievedPercent.get(strEmpId));
									if(uF.parseToDouble(hmCondition.get("MIN_CONDITION")) <= dblAchievedPercent && dblAchievedPercent <= uF.parseToDouble(hmCondition.get("MAX_CONDITION"))) {
										hmEmpAchieved.put(strEmpId, ""+dblAchievedPercent);
										hmConditionAchievedEmpCal.put(hmCondition.get("ALLOWANCE_CONDITION_ID"), hmEmpAchieved);
										
										for(int k = 0; k < alInner.size(); k++) {
											Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
											if(hmLogic == null) hmLogic = new HashMap<String, String>();
											
											if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_ONLY_ID) {
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")));
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_X_ACHIEVED_ID) {
												double dblAmt = uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")) * dblAchievedPercent;
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+dblAmt);
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_EQUAL_TO_SALARY_HEAD_ID) {
												pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
														"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
														"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
														"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
														"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
														"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
												pst.setInt(1, uF.parseToInt(strEmpId));
												pst.setInt(2, uF.parseToInt(strEmpId));
												pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(4, uF.parseToInt(strEmpId));
												pst.setInt(5, uF.parseToInt(strEmpId));
												pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
												pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
	//											System.out.println("pst ===>> " + pst);
												rs = pst.executeQuery();
												double dblSalaryHeadAmt = 0.0d; 
												while(rs.next()) {
													dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
												}
												rs.close();
												pst.close();
												
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+dblSalaryHeadAmt);
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", ""+dblSalaryHeadAmt);
												
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_SALARY_HEAD_X_ACHIEVED_ID) {
												pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
														"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
														"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
														"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
														"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
														"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
												pst.setInt(1, uF.parseToInt(strEmpId));
												pst.setInt(2, uF.parseToInt(strEmpId));
												pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(4, uF.parseToInt(strEmpId));
												pst.setInt(5, uF.parseToInt(strEmpId));
												pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
												pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
	//											System.out.println("pst ===>> " + pst);
												rs = pst.executeQuery();
												double dblSalaryHeadAmt = 0.0d; 
												while(rs.next()) {
													dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
												}
												rs.close();
												pst.close();
												
												double dblAmt = dblSalaryHeadAmt * dblAchievedPercent;
												
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+dblAmt);
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", ""+dblSalaryHeadAmt);
											}
										}
									}
								}
							} else {
								for(String strEmpId : alEmpIds) {
									for(int k = 0; k < alInner.size(); k++){
										Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
										if(hmLogic == null) hmLogic = new HashMap<String, String>();
										pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
												"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
												"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
												"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
												"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
												"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
										pst.setInt(1, uF.parseToInt(strEmpId));
										pst.setInt(2, uF.parseToInt(strEmpId));
										pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(4, uF.parseToInt(strEmpId));
										pst.setInt(5, uF.parseToInt(strEmpId));
										pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
										pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
//										System.out.println("pst ===>> " + pst);
										rs = pst.executeQuery();
										double dblSalaryHeadAmt = 0.0d; 
										while(rs.next()){
											dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
										}
										rs.close();
										pst.close();
										
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "false");
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
									}
								}
							}
							
						} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_NO_OF_DAYS_ABSENT_ID) {
							/**
							 * No. of Days Absent
							 * */
							if(hmEmpAbsentDays!=null && hmEmpAbsentDays.size() > 0){
								Iterator<String> it = hmEmpAbsentDays.keySet().iterator();
								Map<String, String> hmEmpAbsentDayCnt = new HashMap<String, String>();
								while(it.hasNext()){
									String strEmpId = it.next();
//									int nDayCnt = uF.parseToInt(hmEmpDay.get(strEmpId));
									double dblAbsentDayCnt = uF.parseToDouble(hmEmpAbsentDays.get(strEmpId));
									
									if(uF.parseToDouble(hmCondition.get("MIN_CONDITION")) <= dblAbsentDayCnt && dblAbsentDayCnt <= uF.parseToDouble(hmCondition.get("MAX_CONDITION"))){
										hmEmpAbsentDayCnt.put(strEmpId, ""+dblAbsentDayCnt);
										hmConditionAbsentDayEmpCal.put(hmCondition.get("ALLOWANCE_CONDITION_ID"), hmEmpAbsentDayCnt);
										
										for(int k = 0; k < alInner.size(); k++){
											Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
											if(hmLogic == null) hmLogic = new HashMap<String, String>();
											
											if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_ONLY_DEDUCTION_ID){
												pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
														"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
														"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
														"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
														"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
														"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
												pst.setInt(1, uF.parseToInt(strEmpId));
												pst.setInt(2, uF.parseToInt(strEmpId));
												pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(4, uF.parseToInt(strEmpId));
												pst.setInt(5, uF.parseToInt(strEmpId));
												pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(7, uF.parseToInt(getF_salaryhead()));
												pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
	//											System.out.println("pst ===>> " + pst);
												rs = pst.executeQuery();
												double dblSalaryHeadAmt = 0.0d; 
												while(rs.next()){
													dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
												}
												rs.close();
												pst.close();
												
												double dblAmt = 0.0d;
												if(!uF.parseToBoolean(hmLogic.get("IS_DEDUCT_FULL_AMOUNT"))){
													dblAmt = dblSalaryHeadAmt - uF.parseToDouble(hmLogic.get("FIXED_AMOUNT"));
												}
												
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblAmt));
//												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
											}
										}
									}
								}
							} else {
								for(String strEmpId : alEmpIds){
									for(int k = 0; k < alInner.size(); k++){
										Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
										if(hmLogic == null) hmLogic = new HashMap<String, String>();
										pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
												"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
												"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
												"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
												"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
												"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
										pst.setInt(1, uF.parseToInt(strEmpId));
										pst.setInt(2, uF.parseToInt(strEmpId));
										pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(4, uF.parseToInt(strEmpId));
										pst.setInt(5, uF.parseToInt(strEmpId));
										pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(7, uF.parseToInt(getF_salaryhead()));
										pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
		//								System.out.println("pst ===>> " + pst);
										rs = pst.executeQuery();
										double dblSalaryHeadAmt = 0.0d; 
										while(rs.next()){
											dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
										}
										rs.close();
										pst.close();
										
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "false");
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
//										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
									}
								}
							}
						}
					}
					request.setAttribute("hmConditionDayEmpCal", hmConditionDayEmpCal);
					request.setAttribute("hmConditionHourEmpCal", hmConditionHourEmpCal);
					request.setAttribute("hmConditionAchievedEmpCal", hmConditionAchievedEmpCal);
					request.setAttribute("hmPaymentLogicAmt", hmPaymentLogicAmt);
					request.setAttribute("hmConditionAbsentDayEmpCal", hmConditionAbsentDayEmpCal);
//					System.out.println("hmPaymentLogicAmt====>"+hmPaymentLogicAmt);
					
					
					pst = con.prepareStatement("select aid.emp_id,apd.* from allowance_individual_details aid, allowance_pay_details apd " +
							"where aid.allowance_id=apd.allowance_id and aid.emp_id in ("+strEmpIds+") and aid.salary_head_id=? and aid.pay_paycycle=? " +
							"and aid.paid_from=? and aid.paid_to=?");
					pst.setInt(1, uF.parseToInt(getF_salaryhead()));
					pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
					pst.setDate(3, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//					System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					Map<String, String> hmAssignConditionAmt = new HashMap<String, String>();
					Map<String, String> hmAssignLogicAmt = new HashMap<String, String>();
					while(rs.next()){
						if(uF.parseToInt(rs.getString("condition_id")) > 0){
							hmAssignConditionAmt.put(rs.getString("emp_id")+"_"+rs.getString("condition_id"), uF.parseToDouble(rs.getString("amount")) > 0.0d ? rs.getString("amount") : null);
						} else if(uF.parseToInt(rs.getString("payment_logic_id")) > 0){
							hmAssignLogicAmt.put(rs.getString("emp_id")+"_"+rs.getString("payment_logic_id"), uF.parseToDouble(rs.getString("amount")) > 0.0d ? rs.getString("amount") : null);
						}
					}
					rs.close();
					pst.close();
					request.setAttribute("hmAssignConditionAmt", hmAssignConditionAmt);
					request.setAttribute("hmAssignLogicAmt", hmAssignLogicAmt);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void downloadAllowance(UtilityFunctions uF, String strD1, String strD2, String strPC) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			String[] strEmpIds = request.getParameterValues("strEmpIdApplicable");
			if(strEmpIds !=null && strEmpIds.length >0) {
				List<String> alEmp = Arrays.asList(strEmpIds);
				if(alEmp == null) alEmp = new ArrayList<String>();
				int nEmpSize = alEmp.size();
				if(nEmpSize > 0){
					Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(getF_level()));
					List<Map<String, String>> alCondition = (List<Map<String, String>>) request.getAttribute("alCondition");
					if(alCondition == null) alCondition = new ArrayList<Map<String,String>>();
					List<Map<String, String>> alLogic = (List<Map<String, String>>) request.getAttribute("alLogic");
					Map<String, String> hmCalFrom = (Map<String, String>) request.getAttribute("hmCalFrom");
					if(hmCalFrom == null) hmCalFrom = new HashMap<String, String>();
					
					if(nEmpSize > 0 && alCondition.size() > 0  && alLogic.size() > 0){
						HSSFWorkbook workbook = new HSSFWorkbook();
						HSSFSheet sheet = workbook.createSheet(uF.showData(hmSalaryHeadsMap.get(getF_salaryhead()), "Allowance"));
						
						List<DataStyle> header = new ArrayList<DataStyle>();
						header.add(new DataStyle(uF.showData(hmSalaryHeadsMap.get(getF_salaryhead()), "Allowance")+" for paycycle- "+uF.showData(strD1, "")+"-"+uF.showData(strD2, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						
						header.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						header.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						header.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));			
						
						List<List<DataStyle>> reportData = new ArrayList<List<DataStyle>>();
						
						for(int j = 0; j < alCondition.size(); j++){ 
			    			Map<String,String> hmCondition = alCondition.get(j);
							if(hmCondition == null) hmCondition = new HashMap<String, String>();
							
							StringBuilder sbCondition = new StringBuilder();	
							sbCondition.append(hmCondition.get("ALLOWANCE_CONDITION_SLAB")+"\n");
							sbCondition.append(hmCondition.get("ALLOWANCE_CONDITION")+"\n");
							if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_CUSTOM_FACTOR_ID){
								String StrType= hmCondition.get("CUSTOM_FACTOR_TYPE")!=null && hmCondition.get("CUSTOM_FACTOR_TYPE").equals("A") ? "Amount" : "Percentage";
								sbCondition.append("Type: "+StrType);
							} else {
								if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_GOAL_KRA_TARGET_ID || uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_KRA_ID) { 
									sbCondition.append("Achieved %");  
								}
								sbCondition.append("Min: "+hmCondition.get("MIN_CONDITION")+" - ");
								if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_GOAL_KRA_TARGET_ID || uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_KRA_ID) { 
									sbCondition.append("Achieved %");
								}
								sbCondition.append("Max: "+hmCondition.get("MAX_CONDITION"));
							}
							
							header.add(new DataStyle(sbCondition.toString(),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						} 
						
						for(int k = 0; k < alLogic.size(); k++) {
							Map<String,String> hmLogic = alLogic.get(k);
							if(hmLogic == null) hmLogic = new HashMap<String, String>();
						
							StringBuilder sbLogic = new StringBuilder();
							sbLogic.append(hmLogic.get("PAYMENT_LOGIC_SLAB")+"\n");
							sbLogic.append("Condition Name: "+hmLogic.get("ALLOWANCE_CONDITION")+"\n");
							sbLogic.append("Payment Logic: "+hmLogic.get("ALLOWANCE_PAYMENT_LOGIC")+"\n");
							
							if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_ONLY_ID 
									|| uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_X_DAYS_ID 
									|| uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_X_HOURS_ID 
									|| uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_X_CUSTOM_ID 
									|| uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_X_ACHIEVED_ID 
									|| uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_AND_PER_HOUR_ID 
									|| uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_AND_PER_DAY_ID) {
								sbLogic.append("Fixed Amount: "+hmLogic.get("FIXED_AMOUNT"));
								if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_AND_PER_HOUR_ID 
										|| uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_AND_PER_DAY_ID) {
									sbLogic.append("\nPlus amount "+uF.showData(hmLogic.get("PER_HOUR_DAY_AMOUNT"),"0")+" per Extra Hour above "+uF.showData(hmCalFrom.get(hmLogic.get("ALLOWANCE_CONDITION_ID")),"0")+" Extra hours");
								} 
							} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_EQUAL_TO_SALARY_HEAD_ID 
									|| uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_SALARY_HEAD_X_DAYS_ID 
									|| uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_SALARY_HEAD_X_HOURS_ID 
									|| uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_SALARY_HEAD_X_CUSTOM_ID 
									|| uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_SALARY_HEAD_X_ACHIEVED_ID) {
								sbLogic.append("Salary Head: "+hmLogic.get("CAL_SALARY_HEAD_NAME"));
							} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_ONLY_DEDUCTION_ID) {
								if(!uF.parseToBoolean(hmLogic.get("IS_DEDUCT_FULL_AMOUNT"))){
									sbLogic.append("Fixed Amount: "+hmLogic.get("FIXED_AMOUNT"));			
								} 
							}
							header.add(new DataStyle(sbLogic.toString(),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						}
						
						header.add(new DataStyle("Amount",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
						
						for(int i = 0; i < alEmp.size(); i++){
							String strEmpId = alEmp.get(i);
							pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod " +
									"where epd.emp_per_id = eod.emp_id and eod.emp_id=?");
							pst.setInt(1, uF.parseToInt(strEmpId));
							rs = pst.executeQuery();
							String strEmpCode = null;
							String strEmpName = null;
							while(rs.next()){
								strEmpCode = uF.showData(rs.getString("empcode"), "");
								
								//String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
								String strEmpMName = "";
								if(flagMiddleName) {
									if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
										strEmpMName = " "+rs.getString("emp_mname");
									}
								}
							
								
								strEmpName = rs.getString("emp_fname") + strEmpMName+" "+ rs.getString("emp_lname");
								
							}
							rs.close();
							pst.close();
							
							List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
							alInnerExport.add(new DataStyle(uF.showData(""+(i + 1), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							alInnerExport.add(new DataStyle(uF.showData(strEmpCode, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
							alInnerExport.add(new DataStyle(uF.showData(strEmpName, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							
							for(int j = 0; j < alCondition.size(); j++){
				    			Map<String,String> hmCondition = alCondition.get(j);
								if(hmCondition == null) hmCondition = new HashMap<String, String>();
								
								int nConditionId = uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_ID"));
								double dblConditionAmt = 0.0d;
								if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_NO_OF_DAYS_ID) {
									dblConditionAmt = uF.parseToDouble((String) request.getParameter("strDays_"+strEmpId+"_"+nConditionId));
								} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_NO_OF_HOURS_ID) {
									dblConditionAmt = uF.parseToDouble((String) request.getParameter("strHours_"+strEmpId+"_"+nConditionId));
								} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_CUSTOM_FACTOR_ID) {
									dblConditionAmt = uF.parseToDouble((String) request.getParameter("strCustomAmtPercentage_"+strEmpId+"_"+nConditionId));
								} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_GOAL_KRA_TARGET_ID || uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_KRA_ID) {
									dblConditionAmt = uF.parseToDouble((String) request.getParameter("strGoalPercentage_"+strEmpId+"_"+nConditionId));
								} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_NO_OF_DAYS_ABSENT_ID) {
									dblConditionAmt = uF.parseToDouble((String) request.getParameter("strAbsentDays_"+strEmpId+"_"+nConditionId));
								}
								
								alInnerExport.add(new DataStyle(""+dblConditionAmt,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							}
							
							for(int k = 0; k < alLogic.size(); k++){
								Map<String,String> hmLogic = alLogic.get(k);
								if(hmLogic == null) hmLogic = new HashMap<String, String>();
								int nLogicId = uF.parseToInt(hmLogic.get("PAYMENT_LOGIC_ID"));
			        			double dblLogicAmt = uF.parseToDouble((String) request.getParameter("strLogicAmt_"+strEmpId+"_"+nLogicId));
			        			
			        			alInnerExport.add(new DataStyle(""+dblLogicAmt,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							}
							
							String strAmount = (String) request.getParameter("strAllowanceAmt_"+strEmpId);
							alInnerExport.add(new DataStyle(""+uF.parseToDouble(strAmount),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							
							reportData.add(alInnerExport);
						}
						
						ExcelSheetDesign sheetDesign = new ExcelSheetDesign();
						sheetDesign.getExcelSheetDesignData(workbook, sheet, header, reportData);

						String strSalaryHeadName = uF.showData(hmSalaryHeadsMap.get(getF_salaryhead()), "Allowance").replaceAll("\\s+", "");
						String fileName = strSalaryHeadName+"_"+uF.showData(strD1, "")+"_"+uF.showData(strD2, "");
						
						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
						workbook.write(buffer);
						response.setContentType("application/vnd.ms-excel:UTF-8");
						response.setContentLength(buffer.size());
						response.setHeader("Content-Disposition", "attachment; filename="+fileName+".xls");
						ServletOutputStream out = response.getOutputStream();
						buffer.writeTo(out);
						out.flush();
						buffer.close();
						out.close();
					}					
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	private void approveAllowanceAmount(UtilityFunctions uF, String strD1, String strD2, String strPC) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			String[] strEmpIds = request.getParameterValues("strEmpIds");
			if(strEmpIds !=null && strEmpIds.length >0) {
				List<String> alEmp = Arrays.asList(strEmpIds);
				if(alEmp == null) alEmp = new ArrayList<String>();
				int nEmpSize = alEmp.size();
				if(nEmpSize > 0){
					List<Map<String, String>> alCondition = (List<Map<String, String>>) request.getAttribute("alCondition");
					if(alCondition == null) alCondition = new ArrayList<Map<String,String>>();
					List<Map<String, String>> alLogic = (List<Map<String, String>>) request.getAttribute("alLogic");
					
					if(nEmpSize > 0 && alCondition.size() > 0  && alLogic.size() > 0){
						for(int i = 0; i < nEmpSize; i++){
							String strEmpId = alEmp.get(i);
							String strAmount = (String) request.getParameter("strAllowanceAmt_"+strEmpId);
							
							pst = con.prepareStatement("select * from allowance_individual_details where emp_id=? " +
								"and salary_head_id=? and paid_from=? and paid_to=? and pay_paycycle=? and production_line_id=?");
							pst.setInt(1, uF.parseToInt(strEmpId));
							pst.setInt(2, uF.parseToInt(getF_salaryhead()));
							pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
							pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
							pst.setInt(5, uF.parseToInt(strPC));
							pst.setInt(6, uF.parseToInt(getProductionLineId()));
							rs = pst.executeQuery();
							boolean isExist = false;
							if(rs.next()){
								isExist = true;
							}
					        rs.close();
					        pst.close();
					        
					        if(!isExist){
							
								pst = con.prepareStatement("insert into allowance_individual_details (emp_id, pay_paycycle, salary_head_id, amount, pay_amount, added_by," +
										"entry_date, paid_from, paid_to, is_approved,approved_by,approved_date,production_line_id) " +
										"values(?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
								pst.setInt(1, uF.parseToInt(strEmpId));
								pst.setInt(2, uF.parseToInt(strPC));
								pst.setInt(3, uF.parseToInt(getF_salaryhead()));
								pst.setDouble(4, uF.parseToDouble(strAmount));
								pst.setDouble(5, uF.parseToDouble(strAmount));
								pst.setInt(6, uF.parseToInt(strEmpSessionId));
								pst.setDate(7, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setDate(8, uF.getDateFormat(strD1, DATE_FORMAT));
								pst.setDate(9, uF.getDateFormat(strD2, DATE_FORMAT));
								pst.setInt(10, 1);
								pst.setInt(11, uF.parseToInt(strEmpSessionId));
								pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
								pst.setInt(13, uF.parseToInt(getProductionLineId()));
								int x = pst.executeUpdate();
					            pst.close();
					            
					            if(x > 0){
					            	pst = con.prepareStatement("select max(allowance_id) as allowance_id from allowance_individual_details where emp_id=? " +
											"and entry_date=? and paid_from = ? and paid_to=? and pay_paycycle=?");
									pst.setInt(1, uF.parseToInt(strEmpId));
									pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
									pst.setDate(3, uF.getDateFormat(strD1, DATE_FORMAT));
									pst.setDate(4, uF.getDateFormat(strD2, DATE_FORMAT));
									pst.setInt(5, uF.parseToInt(strPC));
									rs = pst.executeQuery();
									int nAllowanceId = 0;
									while(rs.next()){
										nAllowanceId = rs.getInt("allowance_id");
									}
							        rs.close();
							        pst.close();
								
									for(int j = 0; j < alCondition.size(); j++){
						    			Map<String,String> hmCondition = alCondition.get(j);
										if(hmCondition == null) hmCondition = new HashMap<String, String>();
										
										int nConditionId = uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_ID"));
										double dblConditionAmt = 0.0d;
										if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_NO_OF_DAYS_ID) {
											dblConditionAmt = uF.parseToDouble((String) request.getParameter("strDays_"+strEmpId+"_"+nConditionId));
										} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_NO_OF_HOURS_ID) {
											dblConditionAmt = uF.parseToDouble((String) request.getParameter("strHours_"+strEmpId+"_"+nConditionId));
										} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_CUSTOM_FACTOR_ID) {
											dblConditionAmt = uF.parseToDouble((String) request.getParameter("strCustomAmtPercentage_"+strEmpId+"_"+nConditionId));
										} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_GOAL_KRA_TARGET_ID || uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_KRA_ID) {
											dblConditionAmt = uF.parseToDouble((String) request.getParameter("strGoalPercentage_"+strEmpId+"_"+nConditionId));
										} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_NO_OF_DAYS_ABSENT_ID) {
											dblConditionAmt = uF.parseToDouble((String) request.getParameter("strAbsentDays_"+strEmpId+"_"+nConditionId));
										}
										
										pst = con.prepareStatement("insert into allowance_pay_details (allowance_id, condition_id, amount)" +
					        					"values(?,?,?)");
										pst.setInt(1, nAllowanceId);
										pst.setInt(2, nConditionId);
										pst.setDouble(3, dblConditionAmt);
										pst.execute();
							            pst.close();									
									}
									
									for(int k = 0; k < alLogic.size(); k++){
										Map<String,String> hmLogic = alLogic.get(k);
										if(hmLogic == null) hmLogic = new HashMap<String, String>();
										
										
										int nLogicId = uF.parseToInt(hmLogic.get("PAYMENT_LOGIC_ID"));
					        			double dblLogicAmt = uF.parseToDouble((String) request.getParameter("strLogicAmt_"+strEmpId+"_"+nLogicId));
					        			
					        			pst = con.prepareStatement("insert into allowance_pay_details (allowance_id, payment_logic_id, amount)" +
					        					"values(?,?,?)");
										pst.setInt(1, nAllowanceId);
										pst.setInt(2, nLogicId);
										pst.setDouble(3, dblLogicAmt);
										pst.execute();
							            pst.close();
									}								
								}
					        }
						}
					}					
				}
			}
		}catch (Exception e) {
			request.setAttribute("STATUS_MSG", "Could not be updated, Please try again");
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void getAllowanceConditionAndLogic(UtilityFunctions uF, String[] strPayCycleDates) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			Map<String, String> hmAllowanceCondition = CF.getAllowanceCondition();
			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(getF_level())); 
			Map<String, String> hmAllowancePaymentLogic = CF.getAllowancePaymentLogic();
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			if(hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String, String>();
						
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and is_alive = true and joining_date<= ? ");
			if(uF.parseToInt(getF_level()) >0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id="+uF.parseToInt(getF_level())+") ");
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
            sbQuery.append(" and eod.emp_id in (select esd.emp_id from emp_salary_details esd, (select max(effective_date) as max_date, emp_id " +
            		" from emp_salary_details where isdisplay = true and is_approved=true group by emp_id ) as b where esd.effective_date = b.max_date " +
            		"and b.emp_id = esd.emp_id and isdisplay = true and is_approved=true and esd.salary_head_id=? and esd.effective_date <=?)");
//			sbQuery.append(" order by emp_fname, emp_lname");
            sbQuery.append(" order by epd.empcode");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
			pst.setInt(2, uF.parseToInt(getF_salaryhead()));
			pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//			System.out.println("pst===>"+pst); 
			rs = pst.executeQuery();
			List<Map<String, String>> alEmp = new ArrayList<Map<String,String>>();
			List<String> alEmpIds = new ArrayList<String>();
			while(rs.next()){
				Map<String, String> hmEmp = new HashMap<String, String>();
				hmEmp.put("EMP_ID", rs.getString("emp_per_id"));
				hmEmp.put("EMP_CODE", uF.showData(rs.getString("empcode"), ""));
				
				//String strMiddleName=(rs.getString("emp_mname")!=null && !rs.getString("emp_mname").trim().equals("")) ? rs.getString("emp_mname").trim()+" " : "";
				
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				String strEmpName = rs.getString("emp_fname") + strEmpMName+" "+ rs.getString("emp_lname");
				hmEmp.put("EMP_NAME", uF.showData(strEmpName, ""));
				
				alEmp.add(hmEmp);
				
				if(!alEmpIds.contains(rs.getString("emp_per_id"))){
					alEmpIds.add(rs.getString("emp_per_id"));
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("alEmp", alEmp);
			request.setAttribute("alEmpIds", alEmpIds);
			
			if(alEmpIds.size() > 0){
				String strEmpIds = StringUtils.join(alEmpIds.toArray(),",");
				
				pst = con.prepareStatement("select * from allowance_condition_details");
				rs = pst.executeQuery();
				Map<String, String> hmAllowanceConditionSlab = new HashMap<String, String>();
				while(rs.next()){
					hmAllowanceConditionSlab.put(rs.getString("allowance_condition_id"), uF.showData(rs.getString("condition_slab"),""));
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from allowance_payment_logic where salary_head_id=? and level_id=? and org_id=? and " +
						" is_publish = true and allowance_condition_id in (select allowance_condition_id from allowance_condition_details where " +
						" salary_head_id=? and level_id=? and org_id=? and is_publish = true) and effective_date<=? ");
				pst.setInt(1, uF.parseToInt(getF_salaryhead()));
				pst.setInt(2, uF.parseToInt(getF_level()));
				pst.setInt(3, uF.parseToInt(getF_org()));
				pst.setInt(4, uF.parseToInt(getF_salaryhead()));
				pst.setInt(5, uF.parseToInt(getF_level()));
				pst.setInt(6, uF.parseToInt(getF_org()));
				pst.setDate(7, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//				System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				List<Map<String, String>> alLogic = new ArrayList<Map<String,String>>();
				List<String> alConditionId = new ArrayList<String>();
				Map<String, List<Map<String, String>>> hmConditionLogic = new HashMap<String, List<Map<String, String>>>();
				while(rs.next()) {
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
					hmLogic.put("PER_HOUR_DAY_AMOUNT", uF.showData(rs.getString("per_hour_day"),"0"));
					hmLogic.put("IS_DEDUCT_FULL_AMOUNT", ""+uF.parseToBoolean(rs.getString("is_deduct_full_amount")));
					
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
//				System.out.println("hmConditionLogic====>"+hmConditionLogic);
				request.setAttribute("alConditionId", alConditionId);
				if(alConditionId.size() > 0) {
					String strConditionIds = StringUtils.join(alConditionId.toArray(),",");
					pst = con.prepareStatement("select * from allowance_condition_details where salary_head_id=? and level_id=? and org_id=? and is_publish = true and allowance_condition_id in ("+strConditionIds+")");
					pst.setInt(1, uF.parseToInt(getF_salaryhead()));
					pst.setInt(2, uF.parseToInt(getF_level()));
					pst.setInt(3, uF.parseToInt(getF_org()));
//					System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					List<Map<String, String>> alCondition = new ArrayList<Map<String,String>>();
					Map<String, String> hmCalFrom = new HashMap<String, String>();
					boolean isCustomPercentage = false;
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
						hmCondition.put("CALCULATE_FROM", uF.showData(rs.getString("calculation_from"),"0"));
						hmCondition.put("IS_ADD_ATTENDANCE", ""+uF.parseToBoolean(rs.getString("is_add_days_attendance")));
						
						alCondition.add(hmCondition);
						
						hmCalFrom.put(rs.getString("allowance_condition_id"), uF.showData(rs.getString("calculation_from"),"0"));
						
						if(rs.getString("custom_type") != null && rs.getString("custom_type").trim().equalsIgnoreCase("P")){
							isCustomPercentage = true;
						}
					}
					rs.close(); 
					pst.close();
					request.setAttribute("alCondition", alCondition);
					request.setAttribute("hmCalFrom", hmCalFrom);
					request.setAttribute("isCustomPercentage", ""+isCustomPercentage);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void viewAllowance(UtilityFunctions uF, String[] strPayCycleDates) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			con = db.makeConnection(con);
			
//			Map<String, String> hmAllowanceCondition = CF.getAllowanceCondition();
//			Map<String, String> hmSalaryHeadsMap = CF.getSalaryHeadsMap(con, uF.parseToInt(getF_level())); 
//			Map<String, String> hmAllowancePaymentLogic = CF.getAllowancePaymentLogic();
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			if(hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String, String>();
			
			
			List<String> alEmpIds = (List<String>) request.getAttribute("alEmpIds");
			if(alEmpIds == null) alEmpIds = new ArrayList<String>();
			
			int nEmp = alEmpIds.size();
			if(alEmpIds.size() > 0){
				String strEmpIds = StringUtils.join(alEmpIds.toArray(),",");
				
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
					hmAllowanceValue.put(rs.getString("emp_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(rs.getString("pay_amount"))));
				}
				rs.close();
				pst.close();
				
				request.setAttribute("hmAllowance", hmAllowance);
				request.setAttribute("hmAllowanceId", hmAllowanceId);
				request.setAttribute("hmAllowanceValue", hmAllowanceValue);				
				
				List<String> alConditionId = (List<String>) request.getAttribute("alConditionId"); 
				if(alConditionId == null) alConditionId = new ArrayList<String>();
				Map<String, List<Map<String, String>>> hmConditionLogic = (Map<String, List<Map<String, String>>>) request.getAttribute("hmConditionLogic");;
				if(hmConditionLogic == null) hmConditionLogic = new HashMap<String, List<Map<String, String>>>();
				
//				System.out.println("hmConditionLogic====>"+hmConditionLogic);
				
				if(alConditionId.size() > 0) {
					List<Map<String, String>> alCondition = (List<Map<String,String>>)request.getAttribute("alCondition");;
					if(alCondition == null) alCondition = new ArrayList<Map<String,String>>();
					
					
					/**
					 * No. of days
					 * */
//					pst = con.prepareStatement("select count(ad.in_out_timestamp) as cnt, rd.emp_id from attendance_details ad, roster_details rd, " +
//							"employee_official_details eod where ad.emp_id=rd.emp_id and ad.service_id = rd.service_id and eod.emp_id = rd.emp_id " +
//							"and eod.emp_id = ad.emp_id and rd._date = to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.in_out = 'OUT' " +
//							"and rd._date between ? and ? and ad.emp_id > 0 and ad.emp_id in ("+strEmpIds+") group by  rd.emp_id");
					
//					pst = con.prepareStatement("select * from approve_attendance where approve_from=? and approve_to=? " +
//							"and paycycle=? and emp_id in ("+strEmpIds+")");
//					pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
//					pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//					pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
////					System.out.println("pst====>"+pst);
//					rs = pst.executeQuery();
//					Map<String, String> hmEmpDay = new HashMap<String, String>();
//					while(rs.next()){
//						hmEmpDay.put(rs.getString("emp_id"), rs.getString("present_days"));
//					}
//					rs.close();
//					pst.close();
					
					Map<String, String> hmEmpDay = new HashMap<String, String>();
					for(int i = 0; i < nEmp; i++){
						String strEmpId = alEmpIds.get(i);
						
						pst = con.prepareStatement("select * from attendance_details where emp_id=? " +
								"and to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD') between ? and ? order by to_date(in_out_timestamp_actual::text, 'YYYY-MM-DD'), in_out ");
						pst.setInt(1, uF.parseToInt(strEmpId));
						pst.setDate(2, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
						pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
						rs = pst.executeQuery();
						Map<String, String> hmHalfDayAttendance = new HashMap<String, String>();
						Map<String,String> hmEmpAttendance = new HashMap<String,String>();
						while(rs.next()){
							hmEmpAttendance.put(uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBTIMESTAMP, DATE_FORMAT)+"_"+rs.getString("service_id")+"_"+rs.getString("emp_id"), "P");
							if(rs.getString("in_out")!=null && rs.getString("in_out").equalsIgnoreCase("OUT")){
								double workingHour=rs.getDouble("hours_worked");
								if(workingHour < 5.0d){
									hmHalfDayAttendance.put(rs.getString("emp_id")+"_"+uF.getDateFormat(rs.getString("in_out_timestamp_actual"), DBDATE, DATE_FORMAT),""+workingHour);
								}
							}
						}
						rs.close();
						pst.close();
							
						double  dblPresent = hmEmpAttendance.size();
						dblPresent -= hmHalfDayAttendance.size() * 0.5;
						hmEmpDay.put(strEmpId, ""+dblPresent);
					}
					
					/**
					 * No. of Days Absent 
					 * */
					pst = con.prepareStatement("select emp_id,absent_days from approve_attendance where emp_id in ("+strEmpIds+") and approve_from=? " +
							"and approve_to=? and paycycle=?");
					pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
//					System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					Map<String, String> hmEmpAbsentDays = new HashMap<String, String>();
					while(rs.next()){
						hmEmpAbsentDays.put(rs.getString("emp_id"), rs.getString("absent_days"));
					}
					rs.close();
					pst.close();
					
					
					/**
					 * No. of hours
					 * */
//					pst = con.prepareStatement("select sum(hours_worked) as hours_worked, rd.emp_id from attendance_details ad, roster_details rd," +
//							" employee_official_details eod where ad.emp_id=rd.emp_id and ad.service_id = rd.service_id and eod.emp_id = rd.emp_id " +
//							"and eod.emp_id = ad.emp_id and rd._date = to_date(ad.in_out_timestamp::text, 'YYYY-MM-DD') and ad.in_out = 'OUT' " +
//							"and rd._date between ? and ? and ad.emp_id in ("+strEmpIds+") group by rd.emp_id");
					pst = con.prepareStatement("select * from allowance_hours_details where emp_id in ("+strEmpIds+") and paycycle_from=? and paycycle_to=? and paycycle=?");
					pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
//					System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					Map<String, String> hmEmpHours = new HashMap<String, String>();
					while(rs.next()){
						hmEmpHours.put(rs.getString("emp_id"), rs.getString("allowance_hours"));
					}
					rs.close();
					pst.close();
					
					/**
					 * Allowance Logic
					 * */					
					Map<String, Map<String, String>> hmConditionDayEmpCal = new HashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmConditionHourEmpCal = new HashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmConditionAchievedEmpCal = new HashMap<String, Map<String, String>>();
					Map<String, Map<String, String>> hmConditionAbsentDayEmpCal = new HashMap<String, Map<String, String>>();
					
					Map<String, String> hmPaymentLogicAmt = new HashMap<String, String>();
					for(int j = 0; j < alCondition.size(); j++){ 
		    			Map<String,String> hmCondition = (Map<String,String>) alCondition.get(j);
						if(hmCondition == null) hmCondition = new HashMap<String, String>();
						
						List<Map<String, String>> alInner = (List<Map<String, String>>) hmConditionLogic.get(hmCondition.get("ALLOWANCE_CONDITION_ID"));
						if(alInner == null) alInner = new ArrayList<Map<String,String>>();
						
						if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_NO_OF_DAYS_ID && uF.parseToBoolean(hmCondition.get("IS_ADD_ATTENDANCE"))) {
							/**
							 * No. of days 
							 * */
							if(hmEmpDay!=null && hmEmpDay.size() > 0){
								Iterator<String> it = hmEmpDay.keySet().iterator();
								Map<String, String> hmEmpDayCnt = new HashMap<String, String>();
								while(it.hasNext()){
									String strEmpId = it.next();
//									int nDayCnt = uF.parseToInt(hmEmpDay.get(strEmpId));
									double dblDayCnt = uF.parseToDouble(hmEmpDay.get(strEmpId));
									
									if(uF.parseToDouble(hmCondition.get("MIN_CONDITION")) <= dblDayCnt && dblDayCnt <= uF.parseToDouble(hmCondition.get("MAX_CONDITION"))){
										hmEmpDayCnt.put(strEmpId, ""+dblDayCnt);
										hmConditionDayEmpCal.put(hmCondition.get("ALLOWANCE_CONDITION_ID"), hmEmpDayCnt);
										
										for(int k = 0; k < alInner.size(); k++){
											Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
											if(hmLogic == null) hmLogic = new HashMap<String, String>();
											
											if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_ONLY_ID){
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")));
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_X_DAYS_ID){
												double dblAmt = uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")) * dblDayCnt;
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblAmt));
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_AND_PER_DAY_ID){
												double nDiff = (dblDayCnt - uF.parseToDouble(hmCondition.get("MIN_CONDITION")));
												nDiff += 1;
												double dblAmt = (uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")) + (uF.parseToDouble(hmLogic.get("PER_HOUR_DAY_AMOUNT")) * nDiff));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblAmt));
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_EQUAL_TO_SALARY_HEAD_ID){
												pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
														"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
														"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
														"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
														"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
														"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
												pst.setInt(1, uF.parseToInt(strEmpId));
												pst.setInt(2, uF.parseToInt(strEmpId));
												pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(4, uF.parseToInt(strEmpId));
												pst.setInt(5, uF.parseToInt(strEmpId));
												pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
												pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
	//											System.out.println("pst ===>> " + pst);
												rs = pst.executeQuery();
												double dblSalaryHeadAmt = 0.0d; 
												while(rs.next()){
													dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
												}
												rs.close();
												pst.close();
												
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
												
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_SALARY_HEAD_X_DAYS_ID){
												pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
														"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
														"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
														"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
														"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
														"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
												pst.setInt(1, uF.parseToInt(strEmpId));
												pst.setInt(2, uF.parseToInt(strEmpId));
												pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(4, uF.parseToInt(strEmpId));
												pst.setInt(5, uF.parseToInt(strEmpId));
												pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
												pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
	//											System.out.println("pst ===>> " + pst);
												rs = pst.executeQuery();
												double dblSalaryHeadAmt = 0.0d; 
												while(rs.next()){
													dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
												}
												rs.close();
												pst.close();
												
												double dblAmt = dblSalaryHeadAmt * dblDayCnt;
												
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblAmt));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
											}
										}
									}
								}
							} else {
								for(String strEmpId : alEmpIds){
									for(int k = 0; k < alInner.size(); k++){
										Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
										if(hmLogic == null) hmLogic = new HashMap<String, String>();
										pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
												"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
												"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
												"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
												"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
												"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
										pst.setInt(1, uF.parseToInt(strEmpId));
										pst.setInt(2, uF.parseToInt(strEmpId));
										pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(4, uF.parseToInt(strEmpId));
										pst.setInt(5, uF.parseToInt(strEmpId));
										pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
										pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
		//											System.out.println("pst ===>> " + pst);
										rs = pst.executeQuery();
										double dblSalaryHeadAmt = 0.0d; 
										while(rs.next()){
											dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
										}
										rs.close();
										pst.close();
										
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "false");
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
									}
								}
							}
						} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_NO_OF_HOURS_ID) {
							/**
							 * No. of Hours
							 * */
							
							if(hmEmpHours!=null && hmEmpHours.size() > 0){
								Iterator<String> it = hmEmpHours.keySet().iterator();
								Map<String, String> hmEmpHourCnt = new HashMap<String, String>();
								while(it.hasNext()){
									String strEmpId = it.next();
									double dblHours = uF.parseToDouble(hmEmpHours.get(strEmpId));
									if(uF.parseToDouble(hmCondition.get("MIN_CONDITION")) <= dblHours && dblHours <= uF.parseToDouble(hmCondition.get("MAX_CONDITION"))){
										hmEmpHourCnt.put(strEmpId, ""+dblHours);
										hmConditionHourEmpCal.put(hmCondition.get("ALLOWANCE_CONDITION_ID"), hmEmpHourCnt);
										
										for(int k = 0; k < alInner.size(); k++){
											Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
											if(hmLogic == null) hmLogic = new HashMap<String, String>();
											
											if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_ONLY_ID){
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmLogic.get("FIXED_AMOUNT"))));
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_X_HOURS_ID){
												double dblAmt = uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")) * dblHours;
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblAmt));
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_AND_PER_HOUR_ID){
												double dblDiff = (dblHours - uF.parseToInt(hmCondition.get("MIN_CONDITION")));
												dblDiff += 1;
												double dblAmt = (uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")) + (uF.parseToDouble(hmLogic.get("PER_HOUR_DAY_AMOUNT")) * dblDiff));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblAmt));
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_EQUAL_TO_SALARY_HEAD_ID){
												pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
														"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
														"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
														"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
														"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
														"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
												pst.setInt(1, uF.parseToInt(strEmpId));
												pst.setInt(2, uF.parseToInt(strEmpId));
												pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(4, uF.parseToInt(strEmpId));
												pst.setInt(5, uF.parseToInt(strEmpId));
												pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
												pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
	//											System.out.println("pst ===>> " + pst);
												rs = pst.executeQuery();
												double dblSalaryHeadAmt = 0.0d; 
												while(rs.next()){
													dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
												}
												rs.close();
												pst.close();
												
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
												
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_SALARY_HEAD_X_HOURS_ID){
												pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
														"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
														"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
														"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
														"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
														"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
												pst.setInt(1, uF.parseToInt(strEmpId));
												pst.setInt(2, uF.parseToInt(strEmpId));
												pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(4, uF.parseToInt(strEmpId));
												pst.setInt(5, uF.parseToInt(strEmpId));
												pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
												pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
	//											System.out.println("pst ===>> " + pst);
												rs = pst.executeQuery();
												double dblSalaryHeadAmt = 0.0d; 
												while(rs.next()){
													dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
												}
												rs.close();
												pst.close();
												
												double dblAmt = dblSalaryHeadAmt * dblHours;
												
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblAmt));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
											}
										}
									}
								}
							} else {
								for(String strEmpId : alEmpIds) {
									for(int k = 0; k < alInner.size(); k++) {
										Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
										if(hmLogic == null) hmLogic = new HashMap<String, String>();
										pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
												"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
												"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
												"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
												"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
												"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
										pst.setInt(1, uF.parseToInt(strEmpId));
										pst.setInt(2, uF.parseToInt(strEmpId));
										pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(4, uF.parseToInt(strEmpId));
										pst.setInt(5, uF.parseToInt(strEmpId));
										pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
										pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
//										System.out.println("pst ===>> " + pst);
										rs = pst.executeQuery();
										double dblSalaryHeadAmt = 0.0d; 
										while(rs.next()) {
											dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
										}
										rs.close();
										pst.close();
										
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "false");
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
									}
								}
							}
							
						} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_CUSTOM_FACTOR_ID) {
							for(String strEmpId : alEmpIds){ 
								for(int k = 0; k < alInner.size(); k++) {
									Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
									if(hmLogic == null) hmLogic = new HashMap<String, String>();
									
									if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_ONLY_ID) {
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")));
									} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_X_CUSTOM_ID){
									
									} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_EQUAL_TO_SALARY_HEAD_ID) {
										pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
												"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
												"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
												"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
												"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
												"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
										pst.setInt(1, uF.parseToInt(strEmpId));
										pst.setInt(2, uF.parseToInt(strEmpId));
										pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(4, uF.parseToInt(strEmpId));
										pst.setInt(5, uF.parseToInt(strEmpId));
										pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
										pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
//										System.out.println("pst ===>> " + pst);
										rs = pst.executeQuery();
										double dblSalaryHeadAmt = 0.0d; 
										while(rs.next()){
											dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
										}
										rs.close();
										pst.close();
										
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+dblSalaryHeadAmt);
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", ""+dblSalaryHeadAmt);
										
									} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_SALARY_HEAD_X_CUSTOM_ID){
										pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
												"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
												"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
												"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
												"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
												"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
										pst.setInt(1, uF.parseToInt(strEmpId));
										pst.setInt(2, uF.parseToInt(strEmpId));
										pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(4, uF.parseToInt(strEmpId));
										pst.setInt(5, uF.parseToInt(strEmpId));
										pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
										pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
//										System.out.println("pst ===>> " + pst);
										rs = pst.executeQuery();
										double dblSalaryHeadAmt = 0.0d; 
										while(rs.next()){
											dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
										}
										rs.close();
										pst.close();
										
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+dblSalaryHeadAmt);
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", ""+dblSalaryHeadAmt);
									}
								}	
							}
						} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_GOAL_KRA_TARGET_ID || uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_KRA_ID) {
							/**
							 * Goal Kra Targets
							 * */
							Map<String, String> hmEmpAchievedPercent = new HashMap<String, String>();
							if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_GOAL_KRA_TARGET_ID) {
								hmEmpAchievedPercent = getEmployeeAchievedPercentage(con, uF, hmCondition.get("ALLOWANCE_CONDITION_ID"));
							} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_KRA_ID) {
								hmEmpAchievedPercent = getEmployeeKRAPercentage(con, uF, hmCondition.get("ALLOWANCE_CONDITION_ID"));
							}
							
							
							if(hmEmpAchievedPercent!=null && hmEmpAchievedPercent.size() > 0) {
								Iterator<String> it = hmEmpAchievedPercent.keySet().iterator();
								Map<String, String> hmEmpAchieved = new HashMap<String, String>();
								while(it.hasNext()) {
									String strEmpId = it.next();
									double dblAchievedPercent = uF.parseToDouble(hmEmpAchievedPercent.get(strEmpId));
									if(uF.parseToDouble(hmCondition.get("MIN_CONDITION")) <= dblAchievedPercent && dblAchievedPercent <= uF.parseToDouble(hmCondition.get("MAX_CONDITION"))) {
										hmEmpAchieved.put(strEmpId, ""+dblAchievedPercent);
										hmConditionAchievedEmpCal.put(hmCondition.get("ALLOWANCE_CONDITION_ID"), hmEmpAchieved);
										
										for(int k = 0; k < alInner.size(); k++) {
											Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
											if(hmLogic == null) hmLogic = new HashMap<String, String>();
											
											if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_ONLY_ID) {
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")));
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_X_ACHIEVED_ID) {
												double dblAmt = uF.parseToDouble(hmLogic.get("FIXED_AMOUNT")) * dblAchievedPercent;
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+dblAmt);
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_EQUAL_TO_SALARY_HEAD_ID) {
												pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
														"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
														"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
														"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
														"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
														"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
												pst.setInt(1, uF.parseToInt(strEmpId));
												pst.setInt(2, uF.parseToInt(strEmpId));
												pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(4, uF.parseToInt(strEmpId));
												pst.setInt(5, uF.parseToInt(strEmpId));
												pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
												pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
	//											System.out.println("pst ===>> " + pst);
												rs = pst.executeQuery();
												double dblSalaryHeadAmt = 0.0d; 
												while(rs.next()) {
													dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
												}
												rs.close();
												pst.close();
												
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+dblSalaryHeadAmt);
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", ""+dblSalaryHeadAmt);
												
											} else if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_SALARY_HEAD_X_ACHIEVED_ID) {
												pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
														"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
														"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
														"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
														"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
														"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
												pst.setInt(1, uF.parseToInt(strEmpId));
												pst.setInt(2, uF.parseToInt(strEmpId));
												pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(4, uF.parseToInt(strEmpId));
												pst.setInt(5, uF.parseToInt(strEmpId));
												pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
												pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
	//											System.out.println("pst ===>> " + pst);
												rs = pst.executeQuery();
												double dblSalaryHeadAmt = 0.0d; 
												while(rs.next()) {
													dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
												}
												rs.close();
												pst.close();
												
												double dblAmt = dblSalaryHeadAmt * dblAchievedPercent;
												
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), ""+dblAmt);
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", ""+dblSalaryHeadAmt);
											}
										}
									}
								}
							} else {
								for(String strEmpId : alEmpIds) {
									for(int k = 0; k < alInner.size(); k++){
										Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
										if(hmLogic == null) hmLogic = new HashMap<String, String>();
										pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
												"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
												"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
												"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
												"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
												"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
										pst.setInt(1, uF.parseToInt(strEmpId));
										pst.setInt(2, uF.parseToInt(strEmpId));
										pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(4, uF.parseToInt(strEmpId));
										pst.setInt(5, uF.parseToInt(strEmpId));
										pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(7, uF.parseToInt(hmLogic.get("CAL_SALARY_HEAD_ID")));
										pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
//										System.out.println("pst ===>> " + pst);
										rs = pst.executeQuery();
										double dblSalaryHeadAmt = 0.0d; 
										while(rs.next()){
											dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
										}
										rs.close();
										pst.close();
										
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "false");
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
									}
								}
							}
							
						} else if(uF.parseToInt(hmCondition.get("ALLOWANCE_CONDITION_TYPE")) == A_NO_OF_DAYS_ABSENT_ID) {
							/**
							 * No. of Days Absent
							 * */
							if(hmEmpAbsentDays!=null && hmEmpAbsentDays.size() > 0){
								Iterator<String> it = hmEmpAbsentDays.keySet().iterator();
								Map<String, String> hmEmpAbsentDayCnt = new HashMap<String, String>();
								while(it.hasNext()){
									String strEmpId = it.next();
//									int nDayCnt = uF.parseToInt(hmEmpDay.get(strEmpId));
									double dblAbsentDayCnt = uF.parseToDouble(hmEmpAbsentDays.get(strEmpId));
									
									if(uF.parseToDouble(hmCondition.get("MIN_CONDITION")) <= dblAbsentDayCnt && dblAbsentDayCnt <= uF.parseToDouble(hmCondition.get("MAX_CONDITION"))){
										hmEmpAbsentDayCnt.put(strEmpId, ""+dblAbsentDayCnt);
										hmConditionAbsentDayEmpCal.put(hmCondition.get("ALLOWANCE_CONDITION_ID"), hmEmpAbsentDayCnt);
										
										for(int k = 0; k < alInner.size(); k++){
											Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
											if(hmLogic == null) hmLogic = new HashMap<String, String>();
											
											if(uF.parseToInt(hmLogic.get("ALLOWANCE_PAYMENT_LOGIC_ID")) == A_FIXED_ONLY_DEDUCTION_ID){
												pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
														"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
														"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
														"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
														"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
														"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
												pst.setInt(1, uF.parseToInt(strEmpId));
												pst.setInt(2, uF.parseToInt(strEmpId));
												pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(4, uF.parseToInt(strEmpId));
												pst.setInt(5, uF.parseToInt(strEmpId));
												pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
												pst.setInt(7, uF.parseToInt(getF_salaryhead()));
												pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
	//											System.out.println("pst ===>> " + pst);
												rs = pst.executeQuery();
												double dblSalaryHeadAmt = 0.0d; 
												while(rs.next()){
													dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
												}
												rs.close();
												pst.close();
												
												double dblAmt = 0.0d;
												if(!uF.parseToBoolean(hmLogic.get("IS_DEDUCT_FULL_AMOUNT"))){
													dblAmt = dblSalaryHeadAmt - uF.parseToDouble(hmLogic.get("FIXED_AMOUNT"));
												}
												
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "true");
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(dblAmt));
//												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
												hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
											}
										}
									}
								}
							} else {
								for(String strEmpId : alEmpIds){
									for(int k = 0; k < alInner.size(); k++){
										Map<String,String> hmLogic = (Map<String,String>) alInner.get(k);
										if(hmLogic == null) hmLogic = new HashMap<String, String>();
										pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
												"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true) " +
												"AND effective_date <= ? group by salary_head_id) a, emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
												"and a.salary_head_id=esd.salary_head_id and emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
												"WHERE emp_id = ? and is_approved = true) AND effective_date <= ? and esd.salary_head_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
												"WHERE asd.level_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) order by sd.earning_deduction desc, weight");
										pst.setInt(1, uF.parseToInt(strEmpId));
										pst.setInt(2, uF.parseToInt(strEmpId));
										pst.setDate(3, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(4, uF.parseToInt(strEmpId));
										pst.setInt(5, uF.parseToInt(strEmpId));
										pst.setDate(6, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
										pst.setInt(7, uF.parseToInt(getF_salaryhead()));
										pst.setInt(8, uF.parseToInt(hmEmpLevelMap.get(strEmpId)));
		//								System.out.println("pst ===>> " + pst);
										rs = pst.executeQuery();
										double dblSalaryHeadAmt = 0.0d; 
										while(rs.next()){
											dblSalaryHeadAmt = uF.parseToDouble(rs.getString("amount"));
										}
										rs.close();
										pst.close();
										
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_APPLICABLE", "false");
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
//										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID", hmLogic.get("CAL_SALARY_HEAD_ID"));
										hmPaymentLogicAmt.put(strEmpId+"_"+hmLogic.get("PAYMENT_LOGIC_ID")+"_SALARYHEADID_AMT", uF.formatIntoTwoDecimalWithOutComma(dblSalaryHeadAmt));
									}
								}
							}
						}
					}
					request.setAttribute("hmConditionDayEmpCal", hmConditionDayEmpCal);
					request.setAttribute("hmConditionHourEmpCal", hmConditionHourEmpCal);
					request.setAttribute("hmConditionAchievedEmpCal", hmConditionAchievedEmpCal);
					request.setAttribute("hmPaymentLogicAmt", hmPaymentLogicAmt);
					request.setAttribute("hmConditionAbsentDayEmpCal", hmConditionAbsentDayEmpCal);
//					System.out.println("hmPaymentLogicAmt====>"+hmPaymentLogicAmt);
					
					
					pst = con.prepareStatement("select aid.emp_id,apd.* from allowance_individual_details aid, allowance_pay_details apd " +
							"where aid.allowance_id=apd.allowance_id and aid.emp_id in ("+strEmpIds+") and aid.salary_head_id=? and aid.pay_paycycle=? " +
							"and aid.paid_from=? and aid.paid_to=?");
					pst.setInt(1, uF.parseToInt(getF_salaryhead()));
					pst.setInt(2, uF.parseToInt(strPayCycleDates[2]));
					pst.setDate(3, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
//					System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					Map<String, String> hmAssignConditionAmt = new HashMap<String, String>();
					Map<String, String> hmAssignLogicAmt = new HashMap<String, String>();
					while(rs.next()){
						if(uF.parseToInt(rs.getString("condition_id")) > 0){
							hmAssignConditionAmt.put(rs.getString("emp_id")+"_"+rs.getString("condition_id"), uF.parseToDouble(rs.getString("amount")) > 0.0d ? rs.getString("amount") : null);
						} else if(uF.parseToInt(rs.getString("payment_logic_id")) > 0){
							hmAssignLogicAmt.put(rs.getString("emp_id")+"_"+rs.getString("payment_logic_id"), uF.parseToDouble(rs.getString("amount")) > 0.0d ? rs.getString("amount") : null);
						}
					}
					rs.close();
					pst.close();
					request.setAttribute("hmAssignConditionAmt", hmAssignConditionAmt);
					request.setAttribute("hmAssignLogicAmt", hmAssignLogicAmt);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private Map<String, String> getEmployeeKRAPercentage(Connection con, UtilityFunctions uF, String allowanceCoditionId) {
		PreparedStatement pst=null;
		ResultSet rs = null;
		Map<String, String> hmEmpAchievedPercent = new HashMap<String, String>();
		
		try {
			String kIds = null;
			pst = con.prepareStatement("select kra_ids from allowance_condition_details where allowance_condition_id = ? ");
			pst.setInt(1, uF.parseToInt(allowanceCoditionId));
			rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("kra_ids") != null && rs.getString("kra_ids").length()>1) {
					kIds = rs.getString("kra_ids").substring(1, rs.getString("kra_ids").length()-1);
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("goalId ===>> "+ gktIds);
			if(kIds != null && !kIds.equals("")) {
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("SELECT SUM(complete_percent) as complete_percent, kra_id,emp_id FROM goal_kra_status_rating_details where kra_id>0 " +
						"and kra_id in ("+kIds+") and (manager_rating is not null or hr_rating is not null) group by kra_id,emp_id");
//				sbQuery.append("select * from goal_kra_status_rating_details where kra_id in ("+kIds+") and (manager_rating is not null or hr_rating is not null)");
				pst = con.prepareStatement(sbQuery.toString());
	//			System.out.println("pst===>"+pst);
				Map<String, String> hmEmpKRAComplete = new HashMap<String, String>();
				rs = pst.executeQuery();
				while(rs.next()) {
					hmEmpKRAComplete.put(rs.getString("kra_id")+"_"+rs.getString("emp_id"), rs.getString("complete_percent"));
				}
				rs.close();
				pst.close();
//				System.out.println("hmEmpKRAComplete ===>> "+ hmEmpKRAComplete);
				
				sbQuery = new StringBuilder();
				sbQuery.append("select * from goal_kras where goal_kra_id in ("+kIds+")");
				pst = con.prepareStatement(sbQuery.toString());
	//			System.out.println("pst===>"+pst);
				Map<String, String> hmKRAEmpIds = new HashMap<String, String>();
				List<String> alEmpIDs = new ArrayList<String>();
				rs = pst.executeQuery();
				while(rs.next()) {
					hmKRAEmpIds.put(rs.getString("goal_kra_id"), rs.getString("emp_ids"));
					alEmpIDs.add(rs.getString("emp_ids"));
				}
				rs.close();
				pst.close();
//				System.out.println("hmKRAEmpIds ===>> "+ hmKRAEmpIds);
				
				sbQuery = new StringBuilder();
				sbQuery.append("select count(kra_id) as cnt, kra_id from goal_kra_tasks where kra_id in ("+kIds+") group by kra_id");
				pst = con.prepareStatement(sbQuery.toString());
	//			System.out.println("pst===>"+pst);
				Map<String, String> hmKRATaskCnt = new HashMap<String, String>();
				rs = pst.executeQuery();
				while(rs.next()) {
					hmKRATaskCnt.put(rs.getString("kra_id"), rs.getString("cnt"));
				}
				rs.close();
				pst.close();
//				System.out.println("hmKRATaskCnt ===>> "+ hmKRATaskCnt);
				
				Set<String> setEmpIds = new HashSet<String>();
				for(String empId : alEmpIDs) {
					String[] tmpEmpId = empId.split(",");
					List<String> alEmpId = Arrays.asList(tmpEmpId);
					for(String sEmpId : alEmpId) {
						if(uF.parseToInt(sEmpId) > 0) {
							setEmpIds.add(sEmpId);
						}
					}
				}
				
				Map<String, String> hmEmpwiseData = new HashMap<String, String>();
				Iterator<String> itEmp = setEmpIds.iterator();
				while(itEmp.hasNext()) {
					String strEmpId = itEmp.next();
					Iterator<String> it = hmKRAEmpIds.keySet().iterator();
					while(it.hasNext()) {
						String goalId = it.next();
						String empIds = hmKRAEmpIds.get(goalId);
						String[] tmpEmpId = empIds.split(",");
						List<String> alEmpId = Arrays.asList(tmpEmpId);
						if(alEmpId.contains(strEmpId)) {
							int cnt = uF.parseToInt(hmEmpwiseData.get(strEmpId+"CNT"));
							double completePercent = uF.parseToDouble(hmEmpwiseData.get(strEmpId+"PERCENT"));
							completePercent = completePercent + uF.parseToDouble(hmEmpKRAComplete.get(goalId+"_"+strEmpId));
							cnt = cnt + uF.parseToInt(hmKRATaskCnt.get(goalId));
							hmEmpwiseData.put(strEmpId+"CNT", ""+cnt);
							hmEmpwiseData.put(strEmpId+"PERCENT", ""+completePercent);
						}
					}
				}
//				System.out.println("hmEmpwiseData ===>> "+ hmEmpwiseData);
				
				Iterator<String> itEmp1 = setEmpIds.iterator();
				while(itEmp1.hasNext()) {
					String strEmpId = itEmp1.next();
					double empCompletePercent = 0;
					if(uF.parseToInt(hmEmpwiseData.get(strEmpId+"CNT")) > 0) {
						empCompletePercent = uF.parseToDouble(hmEmpwiseData.get(strEmpId+"PERCENT")) / uF.parseToInt(hmEmpwiseData.get(strEmpId+"CNT"));
					}
					hmEmpAchievedPercent.put(strEmpId, uF.formatIntoOneDecimalWithOutComma(empCompletePercent));
				}
//				System.out.println("hmEmpAchievedPercent ===>> "+ hmEmpAchievedPercent);
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
		
		return hmEmpAchievedPercent;
	}



	private Map<String, String> getEmployeeAchievedPercentage(Connection con, UtilityFunctions uF, String allowanceCoditionId) {
		PreparedStatement pst=null;
		ResultSet rs = null;
		Map<String, String> hmEmpAchievedPercent = new HashMap<String, String>();
		
		try {
			String gktIds = null;
			pst = con.prepareStatement("select goal_kra_target_ids from allowance_condition_details where allowance_condition_id = ? ");
			pst.setInt(1, uF.parseToInt(allowanceCoditionId));
			rs = pst.executeQuery();
			while(rs.next()) {
				if(rs.getString("goal_kra_target_ids") != null && rs.getString("goal_kra_target_ids").length()>1) {
					gktIds = rs.getString("goal_kra_target_ids").substring(1, rs.getString("goal_kra_target_ids").length()-1);
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("goalId ===>> "+ gktIds);
			if(gktIds != null && !gktIds.equals("")) {
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from goal_kra_status_rating_details  where goal_id in ("+gktIds+") and (manager_rating is not null or hr_rating is not null)");
				pst = con.prepareStatement(sbQuery.toString());
	//			System.out.println("pst===>"+pst);
				Map<String, String> hmEmpGoalComplete = new HashMap<String, String>();
				rs = pst.executeQuery();
				while(rs.next()) {
					hmEmpGoalComplete.put(rs.getString("goal_id")+"_"+rs.getString("emp_id"), rs.getString("complete_percent"));
				}
				rs.close();
				pst.close();
//				System.out.println("hmEmpGoalComplete ===>> "+ hmEmpGoalComplete);
				
				sbQuery = new StringBuilder();
				sbQuery.append("select * from goal_details where goal_id in ("+gktIds+")");
				pst = con.prepareStatement(sbQuery.toString());
	//			System.out.println("pst===>"+pst);
				Map<String, String> hmGoalTarget = new HashMap<String, String>();
				Map<String, String> hmGoalEmpIds = new HashMap<String, String>();
				List<String> alEmpIDs = new ArrayList<String>();
				rs = pst.executeQuery();
				while(rs.next()) {
					if(rs.getString("measure_kra") != null && rs.getString("measure_kra").equals("Measure")) {
						if(rs.getString("measure_type") != null && rs.getString("measure_type").equals("Effort")) {
							hmGoalTarget.put(rs.getString("goal_id"), rs.getString("measure_effort_days")+"."+rs.getString("measure_effort_hrs"));
						} else {
							hmGoalTarget.put(rs.getString("goal_id"), rs.getString("measure_currency_value"));
						}
					}
					hmGoalEmpIds.put(rs.getString("goal_id"), rs.getString("emp_ids"));
					alEmpIDs.add(rs.getString("emp_ids"));
				}
				rs.close();
				pst.close();
//				System.out.println("hmGoalEmpIds ===>> "+ hmGoalEmpIds);
				
				sbQuery = new StringBuilder();
				sbQuery.append("select max(amt_percentage) as amt_percentage, goal_id, emp_id from target_details where goal_id in ("+gktIds+") " +
						" group by goal_id,emp_id");
				pst = con.prepareStatement(sbQuery.toString());
	//			System.out.println("pst===>"+pst);
				Map<String, String> hmEmpTargetComplete = new HashMap<String, String>();
				rs = pst.executeQuery();
				while(rs.next()) {
					String empTarget = hmGoalTarget.get(rs.getString("goal_id"));
					double dblCompletePercent = (uF.parseToDouble(rs.getString("amt_percentage")) * 100) /uF.parseToDouble(empTarget);
					hmEmpTargetComplete.put(rs.getString("goal_id")+"_"+rs.getString("emp_id"), uF.formatIntoOneDecimalWithOutComma(dblCompletePercent));
				}
				rs.close();
				pst.close();
				
//				System.out.println("hmEmpTargetComplete ===>> "+ hmEmpTargetComplete);
				
				Set<String> setEmpIds = new HashSet<String>();
				for(String empId : alEmpIDs) {
					String[] tmpEmpId = empId.split(",");
					List<String> alEmpId = Arrays.asList(tmpEmpId);
					for(String sEmpId : alEmpId) {
						setEmpIds.add(sEmpId);
					}
				}
				
				Map<String, String> hmEmpwiseData = new HashMap<String, String>();
				Iterator<String> itEmp = setEmpIds.iterator();
				while(itEmp.hasNext()) {
					String strEmpId = itEmp.next();
					Iterator<String> it = hmGoalEmpIds.keySet().iterator();
					while(it.hasNext()) {
						String goalId = it.next();
						String empIds = hmGoalEmpIds.get(goalId);
						String[] tmpEmpId = empIds.split(",");
						List<String> alEmpId = Arrays.asList(tmpEmpId);
						if(alEmpId.contains(strEmpId)) {
							int cnt = uF.parseToInt(hmEmpwiseData.get(strEmpId+"CNT"));
							double completePercent = uF.parseToDouble(hmEmpwiseData.get(strEmpId+"PERCENT"));
							if(hmEmpTargetComplete.get(goalId+"_"+strEmpId) != null && uF.parseToDouble(hmEmpTargetComplete.get(goalId+"_"+strEmpId))>0) {
								completePercent = completePercent + uF.parseToDouble(hmEmpTargetComplete.get(goalId+"_"+strEmpId));
							} else {
								completePercent = completePercent + uF.parseToDouble(hmEmpGoalComplete.get(goalId+"_"+strEmpId));
							}
							cnt++;
							hmEmpwiseData.put(strEmpId+"CNT", ""+cnt);
							hmEmpwiseData.put(strEmpId+"PERCENT", ""+completePercent);
						}
					}
				}
				
//				System.out.println("hmEmpwiseData ===>> "+ hmEmpwiseData);
				
				Iterator<String> itEmp1 = setEmpIds.iterator();
				while(itEmp1.hasNext()) {
					String strEmpId = itEmp1.next();
					double empCompletePercent = 0;
					if(uF.parseToInt(hmEmpwiseData.get(strEmpId+"CNT")) > 0) {
						empCompletePercent = uF.parseToDouble(hmEmpwiseData.get(strEmpId+"PERCENT")) / uF.parseToInt(hmEmpwiseData.get(strEmpId+"CNT"));
					}
					hmEmpAchievedPercent.put(strEmpId, uF.formatIntoOneDecimalWithOutComma(empCompletePercent));
				}
				
//				System.out.println("hmEmpAchievedPercent ===>> "+ hmEmpAchievedPercent);
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
		return hmEmpAchievedPercent;
	}
	


	private String loadAllowance(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
//		salaryHeadList = new FillSalaryHeads(request).fillAllowanceSalaryHeadsByOrg(uF, getF_org());
		salaryHeadList = new FillSalaryHeads(request).fillAllowanceSalaryHeads(getF_level());
		
		productionLineList = new FillProductionLine(request).fillProductionLineBySalaryHead(getF_org(), getF_level(),getF_salaryhead());
		
		getSelectedFilter(uF);
		
		return LOAD;
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
		
		alFilter.add("LEVEL");
		if(getF_level()!=null){
			String strLevel="";
			for(int i=0;levelList!=null && i<levelList.size();i++){
				if(getF_level().equals(levelList.get(i).getLevelId())){
					strLevel=levelList.get(i).getLevelCodeName();
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
		
		alFilter.add("ALLOWHEAD");
		if(getF_salaryhead()!=null){
			String strSalaryHead="";
			for(int i=0;salaryHeadList!=null && i<salaryHeadList.size();i++){
				if(getF_salaryhead().equals(salaryHeadList.get(i).getSalaryHeadId())){
					strSalaryHead=salaryHeadList.get(i).getSalaryHeadName();
				}
			}
			if(strSalaryHead!=null && !strSalaryHead.equals("")){
				hmFilter.put("ALLOWHEAD", strSalaryHead);
			}else{
				hmFilter.put("ALLOWHEAD", "Select Head");
			}
		}else{
			hmFilter.put("ALLOWHEAD", "Select Head");
		}
		
		boolean isSalaryHeadProdLine = checkSalaryHeadProductionLine(uF);
		if(isSalaryHeadProdLine){
			alFilter.add("PRODUCTIONLINE");
			if(getProductionLineId()!=null){
				String strProductionLine="";
				for(int i=0;productionLineList!=null && i<productionLineList.size();i++){
					if(getProductionLineId().equals(productionLineList.get(i).getProductionLineId())){
						strProductionLine=productionLineList.get(i).getProductionLineName();
					}
				}
				if(strProductionLine!=null && !strProductionLine.trim().equals("")){
					hmFilter.put("PRODUCTIONLINE", strProductionLine);
				}else{
					hmFilter.put("PRODUCTIONLINE", "Select Production Line");
				}
			}else{
				hmFilter.put("PRODUCTIONLINE", "Select Production Line");
			}
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
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
    }
	
	
	private HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	private HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
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

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
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

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public String getProductionLineId() {
		return productionLineId;
	}

	public void setProductionLineId(String productionLineId) {
		this.productionLineId = productionLineId;
	}

	public List<FillProductionLine> getProductionLineList() {
		return productionLineList;
	}

	public void setProductionLineList(List<FillProductionLine> productionLineList) {
		this.productionLineList = productionLineList;
	}
}