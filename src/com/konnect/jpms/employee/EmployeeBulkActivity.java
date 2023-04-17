package com.konnect.jpms.employee;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.html.simpleparser.HTMLWorker;
import com.itextpdf.text.pdf.PdfWriter;
import com.konnect.jpms.document.HeaderFooterPageEvent;
import com.konnect.jpms.leave.AssignLeaveCron;
import com.konnect.jpms.reports.MyProfile;
import com.konnect.jpms.roster.FillShift;
import com.konnect.jpms.salary.EmpSalaryApproval;
import com.konnect.jpms.salary.EmployeeSalaryDetails;
import com.konnect.jpms.select.FillActivity;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillDesig;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillEmploymentType;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillGrade;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillSalaryHeads;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IConstants;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.Notifications;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class EmployeeBulkActivity extends ActionSupport implements ServletRequestAware, IStatements, ServletResponseAware {

	private static final long serialVersionUID = 1L;

	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId;
	String strSessionOrgId;
	
	private String dataType;
		
	private String f_org;
	private String[] f_strWLocation;
	private String[] f_department; 
	private String[] f_level;
	private String[] f_service;
	
	private List<FillOrganisation> orgList;
	private List<FillWLocation> wLocationList;
	private List<FillDepartment> departmentList;
	private List<FillLevel> levelList;
	private List<FillServices> serviceList;
	
	private List<String> strIsAssigActivity;
	private String strEmpIds; 
	
	private String strActivity;
	private String effectiveDate;
	private String strExtendProbationDays;
	private String strTransferType;
	private String strIncrementType;
	private String strOrganisation;
	private String strWLocation;
	private String strSBU;
	private String strDepartment;
	private String strLevel;
	private String strDesignation;
	private String empGrade;
	private String empChangeGrade;
	private String strNoticePeriod;
	private String strProbationPeriod;
	private String strIncrementPercentage;
	private String strReason;
	private String strUpdate;
	private String strUpdateDocument;
	
	private String strNewStatus;
	
	private List<FillActivity> activityList;
	private List<FillOrganisation> organisationList1;
	private List<FillEmploymentType> empTypeList;
	private List<FillWLocation> wLocationList1;
	private List<FillServices> serviceList1;
	private List<FillDepartment> departmentList1;
	private List<FillLevel> levelList1;
	private List<FillDesig> desigList;
	private List<FillGrade> gradeChangeList;
	
	private String employmentType;
	private String empIds;
	
	private String Submit;
	private String[] strSelectedEmpId;
	private List<FillEmployee> empList;
	
	private String remEmpId;
		
	public String execute() throws Exception {
  
		request.setAttribute(PAGE, "/jsp/employee/EmployeeBulkActivity.jsp");
		request.setAttribute(TITLE, "Employee Bulk Activity");
		session = request.getSession(); 
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null) return LOGIN;
		 
		strSessionEmpId = (String)session.getAttribute(EMPID); 
		strUserType = (String)session.getAttribute(USERTYPE);
		strSessionOrgId = (String)session.getAttribute(ORGID);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		StringBuilder sbpageTitleNaviTrail = new StringBuilder();
		sbpageTitleNaviTrail.append("<li><i class=\"fa fa-group\"></i><a href=\"People.action\" style=\"color: #3c8dbc;\"> People</a></li>" +
			"<li class=\"active\">Bulk Employee Activity</li>");
		request.setAttribute("PAGETITLE_NAVITRAIL", sbpageTitleNaviTrail.toString());
		
		if(getEmpIds()!=null && !getEmpIds().equals("")){
			insertEmpActivity(uF);
			return SUCCESS;
		}
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		loadValidateEmpActivity(uF);
		
		String status = (String) request.getParameter("status");
		if(uF.parseToInt(status) == 1){
			clearAllEmployees(uF);
		}
		
		getEmployeeList(uF);
		return LOAD;
		
	}

	private void clearAllEmployees(UtilityFunctions uF) {
		setEmpIds(null);
	}

	private void insertEmpActivity(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			List<String> alEmpList = Arrays.asList(getEmpIds().split(","));
			if(alEmpList == null) alEmpList = new ArrayList<String>();
			//System.out.println("alEmpList==>"+alEmpList);
			for(int i=0; alEmpList!=null && i < alEmpList.size(); i++){
				if(uF.parseToInt(alEmpList.get(i)) == 0){
					continue;
				}
//				System.out.println("alEmpList====>"+alEmpList.get(i));
				pst = con.prepareStatement("insert into employee_activity_details (wlocation_id, department_id, level_id, desig_id, grade_id, " +
						"emp_status_code, activity_id, reason, effective_date, entry_date, user_id, emp_id, notice_period, probation_period, appraisal_id, " +
						"extend_probation_period, org_id,service_id,increment_type,increment_percent,transfer_type,emptype) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
				pst.setInt(1, uF.parseToInt(getStrWLocation()));
				pst.setInt(2, uF.parseToInt(getStrDepartment()));
				pst.setInt(3, uF.parseToInt(getStrLevel()));
				pst.setInt(4, uF.parseToInt(getStrDesignation()));
				if(uF.parseToInt(getStrActivity()) == uF.parseToInt(ACTIVITY_GRADE_CHANGE_ID)){
					pst.setInt(5, uF.parseToInt(getEmpChangeGrade()));
				}else{
					pst.setInt(5, uF.parseToInt(getEmpGrade()));
				}
				pst.setString(6, getStrNewStatus());
				pst.setInt(7, uF.parseToInt(getStrActivity()));
				pst.setString(8, getStrReason());
				pst.setDate(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setInt(11, uF.parseToInt(strSessionEmpId));
				pst.setInt(12, uF.parseToInt(alEmpList.get(i)));
				pst.setInt(13, uF.parseToInt(getStrNoticePeriod()));
				pst.setInt(14, uF.parseToInt(getStrProbationPeriod()));
				pst.setInt(15, 0);
				pst.setInt(16, uF.parseToInt(getStrExtendProbationDays()));
				pst.setInt(17, uF.parseToInt(getStrOrganisation()));
				pst.setString(18, getStrSBU()!=null && !getStrSBU().equals("") ? ","+getStrSBU()+"," : null);
				pst.setInt(19, uF.parseToInt(getStrIncrementType()));
				pst.setDouble(20, uF.parseToDouble(getStrIncrementPercentage()));
				pst.setString(21, getStrTransferType());
				pst.setString(22, getEmploymentType());
				int x = pst.executeUpdate();
				
				String strDate = uF.getCurrentDate(CF.getStrTimeZone())+"";
				if(x > 0) {
					pst = con.prepareStatement("select max(emp_activity_id) as emp_activity_id from employee_activity_details");
					rs = pst.executeQuery();
					int nEmpActivityId = 0;
					while(rs.next()){
						nEmpActivityId = uF.parseToInt(rs.getString("emp_activity_id"));
					}
				
					Map<String, String>  hmActivity = CF.getActivityName(con);
					if(hmActivity == null) hmActivity = new HashMap<String, String>();
					
					int activityType = uF.parseToInt(getStrActivity());
					processActivity(con, activityType, uF.parseToInt(alEmpList.get(i)), strDate, CF, uF, uF.showData(hmActivity.get(""+activityType), ""), nEmpActivityId);
					
					session.setAttribute(MESSAGE, SUCCESSM+"Employee activity successfully updated."+END);
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
	
	public void processActivity(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		
		this.CF= CF;
		switch(activityType){
		
			case 1:
					processOfferLetter(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 2:
					processAppointmentLetter(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 3:
					processProbation(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 4:
					processExtendProbation(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 5:
					processConfirmation(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
					
			case 6:
					processTemporary(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
					
			case 7:
					processPermanent(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
					
			case 8:
					processTransfer(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 9:
					processPromotion(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 10:
					processIncrement(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 11:
					processGradeChange(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 12:
					processTerminate(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 13:
					processNoticePeriod(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
				
			case 14:
					processWithdrawnResignation(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
					
			case 15:
//					processFullAndFinal(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
			
	//===start parvez date: 17-08-2022===				
			case 17:
				processResignation(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
				break;		
	//===end parvez date: 17-08-2022===	
				
			case 29:
					processRelieving(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
			
			case 30:
					processExperience(con,activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
					break;
					
			case 32:
				processLifeEventIncrement(con,activityType, nEmpId, strDate, CF, uF, strActivityName,nEmpActivityId);
				break;
		}
	}

	private void processLifeEventIncrement(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName, int nEmpActivityId) {
		updateEmployeeSalaryDetails(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}
	
	private void processExperience(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}

	private void processRelieving(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}

	private void processPromotion(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		insertEmpSalaryDetails(con, activityType, nEmpId, uF, strActivityName, nEmpActivityId);
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}

	private void insertEmpSalaryDetails(Connection con, int activityType, int nEmpId, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
	
			pst = con.prepareStatement("select * from salary_details where level_id=?");
			pst.setInt(1, uF.parseToInt(getStrLevel()));
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			Map<String, String> hmSalaryTypeMap = new HashMap<String, String>();
			while (rs.next()) {
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
				hmSalaryTypeMap.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
			}
			rs.close();
			pst.close();
	
			pst = con.prepareStatement("UPDATE employee_official_details SET grade_id=? where emp_id=?");
			pst.setInt(1, uF.parseToInt(getEmpGrade()));
			pst.setInt(2, nEmpId);
			int x = pst.executeUpdate();
			pst.close();
	
			if (x > 0) {
	
				List<FillSalaryHeads> salaryHeadList = new ArrayList<FillSalaryHeads>();
				// salaryHeadList = new
				// FillSalaryHeads(request).fillSalaryHeads();
				salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads(getStrLevel());
	
				EmployeeSalaryDetails salaryDetails = new EmployeeSalaryDetails();
				salaryDetails.CF = CF;
				salaryDetails.request = request;
				salaryDetails.empId = "" + nEmpId;
				salaryDetails.salaryHeadList = salaryHeadList;
				salaryDetails.viewEmployeeSalaryDetails();
				// System.out.println("reportList======>"+request.getAttribute("reportList"));
				List<List<String>> reportList = (List<List<String>>) request.getAttribute("reportList");
				if (reportList == null)
					reportList = new ArrayList<List<String>>();
	
				if (reportList != null && !reportList.isEmpty()) {
					Map<String, Map<String, String>> hmEmpSalaryMap = new HashMap<String, Map<String, String>>();
					for (int i = 0; i < reportList.size(); i++) {
						List<String> alInner = (List<String>) reportList.get(i);
						Map<String, String> hmInner = new HashMap<String, String>();
						hmInner.put("SALARY_HEAD_ID", alInner.get(0));
						hmInner.put("SALARY_HEAD_NAME", alInner.get(1));
						hmInner.put("EARNING_DEDUCTION", alInner.get(2));
						hmInner.put("SALARY_HEAD_AMOUNT_TYPE", alInner.get(3));
						hmInner.put("SUB_SALARY_HEAD_ID", alInner.get(4));
						hmInner.put("SUB_SALARY_HEAD_NAME", alInner.get(5));
						hmInner.put("SALARY_HEAD_AMOUNT", alInner.get(6));
	
						hmEmpSalaryMap.put(alInner.get(0), hmInner);
					}
					// System.out.println("hmEmpSalaryMap======>"+hmEmpSalaryMap);
	
					if (hmEmpSalaryMap != null && !hmEmpSalaryMap.isEmpty()) {
						Map<String, Map<String, String>> hmCalSalaryMap = new HashMap<String, Map<String, String>>();
						Iterator<String> it = hmEmpSalaryMap.keySet().iterator();
						while (it.hasNext()) {
							String slaryHeadId = (String) it.next();
							Map<String, String> hmInner = hmEmpSalaryMap.get(slaryHeadId);
							if (hmInner.get("EARNING_DEDUCTION").equals("E")) {
								// if(hmInner.get("SALARY_HEAD_AMOUNT_TYPE").equals("P")){
								// double dblSalaryHeadPercentage =
								// uF.parseToDouble(hmInner.get("SALARY_HEAD_AMOUNT"));
								// double dblPerAmt = 0.00;
								// Map<String, String> hmSalaryHead=
								// hmEmpSalaryMap.get(hmInner.get("SUB_SALARY_HEAD_ID"));
								//
								// double dblSumSalAmt =
								// uF.parseToDouble(hmSalaryHead.get("AMOUNT"));
								// dblPerAmt = (dblSumSalAmt *
								// dblSalaryHeadPercentage)/100;
								//
								// Map<String, String> hm = new HashMap<String,
								// String>();
								// hm.put("SALARY_HEAD_ID", slaryHeadId);
								// hm.put("EARNING_DEDUCTION",
								// hmInner.get("EARNING_DEDUCTION"));
								// hm.put("AMOUNT",
								// uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
								//
								// hmCalSalaryMap.put(slaryHeadId, hm);
								// } else {
								Map<String, String> hm = new HashMap<String, String>();
								hm.put("SALARY_HEAD_ID", slaryHeadId);
								hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
								hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
	
								hmCalSalaryMap.put(slaryHeadId, hm);
								// }
							} else if (hmInner.get("EARNING_DEDUCTION").equals("D")) {
								// if(hmInner.get("SALARY_HEAD_AMOUNT_TYPE").equals("P")){
								// double dblSalaryHeadPercentage =
								// uF.parseToDouble(hmInner.get("SALARY_HEAD_AMOUNT"));
								// double dblPerAmt = 0.00;
								// Map<String, String> hmSalaryHead=
								// hmEmpSalaryMap.get(hmInner.get("SUB_SALARY_HEAD_ID"));
								//
								// double dblSumSalAmt =
								// uF.parseToDouble(hmSalaryHead.get("AMOUNT"));
								// dblPerAmt = (dblSumSalAmt *
								// dblSalaryHeadPercentage)/100;
								//
								// Map<String, String> hm = new HashMap<String,
								// String>();
								// hm.put("SALARY_HEAD_ID", slaryHeadId);
								// hm.put("EARNING_DEDUCTION",
								// hmInner.get("EARNING_DEDUCTION"));
								// hm.put("AMOUNT",
								// uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
								//
								// hmCalSalaryMap.put(slaryHeadId, hm);
								// } else {
								Map<String, String> hm = new HashMap<String, String>();
								hm.put("SALARY_HEAD_ID", slaryHeadId);
								hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
								hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
	
								hmCalSalaryMap.put(slaryHeadId, hm);
								// }
							}
						}
	 
						// System.out.println("hmCalSalaryMap======>"+hmCalSalaryMap);
						Iterator<String> it1 = hmCalSalaryMap.keySet().iterator();
						while (it1.hasNext()) {
							String strSalaryHeadId = (String) it1.next();
							Map<String, String> hm = hmCalSalaryMap.get(strSalaryHeadId);
	
							pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id, salary_head_id, " +
									"amount, entry_date, user_id, pay_type, isdisplay, service_id, effective_date, " +
									"earning_deduction, salary_type,is_approved,level_id) " +
									"VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
							pst.setInt(1, nEmpId);
							pst.setInt(2, uF.parseToInt(strSalaryHeadId));
							pst.setDouble(3, uF.parseToDouble(hm.get("AMOUNT")));
							pst.setDate(4, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE));
							// pst.setDate(4,
							// uF.getDateFormat(getEffectiveDate(),
							// DATE_FORMAT));
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setString(6, "M");
							pst.setBoolean(7, true);
							pst.setInt(8, 0);
							pst.setDate(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
							pst.setString(10, hm.get("EARNING_DEDUCTION"));
							pst.setString(11, hmSalaryTypeMap.get(strSalaryHeadId));
							pst.setBoolean(12, true);
							pst.setInt(13, uF.parseToInt(getStrLevel()));
							pst.execute();
							pst.close();
	
						}
						
						CF.updateNextEmpSalaryEffectiveDate(con, uF, nEmpId, getEffectiveDate(), DATE_FORMAT);
	
						/**
						 * Calaculate CTC
						 * */
						Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, "" + nEmpId);
	
						MyProfile myProfile = new MyProfile();
						myProfile.session = session;
						myProfile.request = request;
						myProfile.CF = CF;
						myProfile.getSalaryHeadsforEmployee(con, uF, nEmpId, hmEmpProfile);
	
						double grossAmount = 0.0d;
						double grossYearAmount = 0.0d;
						double deductAmount = 0.0d;
						double deductYearAmount = 0.0d;
						double netAmount = 0.0d;
						double netYearAmount = 0.0d;
						List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
						for (int i = 0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i < salaryHeadDetailsList.size(); i++) {
							List<String> innerList = salaryHeadDetailsList.get(i);
							if (innerList.get(1).equals("E")) {
								grossAmount += uF.parseToDouble(innerList.get(2));
								grossYearAmount += uF.parseToDouble(innerList.get(3));
							} else if (innerList.get(1).equals("D")) {
								double dblDeductMonth = 0.0d;
								double dblDeductAnnual = 0.0d;
								if (uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI) {
									dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
									dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
								} else if (uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI) {
									dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
									dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
								} else {
									dblDeductMonth += Math.round(uF.parseToDouble(innerList.get(2)));
									dblDeductAnnual += Math.round(uF.parseToDouble(innerList.get(3)));
								}
								deductAmount += dblDeductMonth;
								deductYearAmount += dblDeductAnnual;
							}
						}
	
						Map<String, String> hmContribution = (Map<String, String>) request.getAttribute("hmContribution");
						if (hmContribution == null)
							hmContribution = new HashMap<String, String>();
						double dblMonthContri = 0.0d;
						double dblAnnualContri = 0.0d;
						boolean isEPF = uF.parseToBoolean((String) request.getAttribute("isEPF"));
						boolean isESIC = uF.parseToBoolean((String) request.getAttribute("isESIC"));
						boolean isLWF = uF.parseToBoolean((String) request.getAttribute("isLWF"));
						if (isEPF || isESIC || isLWF) {
							if (isEPF) {
								double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
								double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
								dblMonthContri += dblEPFMonth;
								dblAnnualContri += dblEPFAnnual;
							}
							if (isESIC) {
								double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
								double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
								dblMonthContri += dblESIMonth;
								dblAnnualContri += dblESIAnnual;
							}
							if (isLWF) {
								double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
								double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
								dblMonthContri += dblLWFMonth;
								dblAnnualContri += dblLWFAnnual;
							}
						}
	
						double dblCTCMonthly = grossAmount + dblMonthContri;
						double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
	
						List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>) request.getAttribute("salaryAnnualVariableDetailsList");
						if (salaryAnnualVariableDetailsList == null)
							salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
						int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
						if (nAnnualVariSize > 0) {
							double grossAnnualAmount = 0.0d;
							double grossAnnualYearAmount = 0.0d;
							for (int i = 0; i < nAnnualVariSize; i++) {
								List<String> innerList = salaryAnnualVariableDetailsList.get(i);
								double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
								double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
								grossAnnualAmount += dblEarnMonth;
								grossAnnualYearAmount += dblEarnAnnual;
							}
							dblCTCMonthly += grossAnnualAmount;
							dblCTCAnnualy += grossAnnualYearAmount;
						}
	
						netAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCMonthly));
						netYearAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCAnnualy));
	
						EmpSalaryApproval salaryApproval = new EmpSalaryApproval();
						salaryApproval.request = request;
						salaryApproval.session = session;
						salaryApproval.CF = CF;
						Map<String, String> hmPrevCTC = salaryApproval.getPrevCTCDetails(con, uF, "" + nEmpId);
	
						if (hmPrevCTC == null)
							hmPrevCTC = new HashMap<String, String>();
						double dblIncrementMonthAmt = netAmount - uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC"));
						double dblIncrementAnnualAmt = netYearAmount - uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC"));
	
						pst = con.prepareStatement("update employee_official_details set month_ctc=?,annual_ctc=?,prev_month_ctc=?,"
								+ "prev_annual_ctc=?,incre_month_amount=?,incre_annual_amount=? where emp_id=?");
						pst.setDouble(1, netAmount);
						pst.setDouble(2, netYearAmount);
						pst.setDouble(3, uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC")));
						pst.setDouble(4, uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC")));
						pst.setDouble(5, dblIncrementMonthAmt);
						pst.setDouble(6, dblIncrementAnnualAmt);
						pst.setInt(7, nEmpId);
						pst.execute();
						pst.close();
					}
				}
			}
	
			// for(int i=0; i<getSalary_head_id().length; i++) {
			//
			// String isDiplaySalaryHead = (String)
			// request.getParameter("isDisplay_"+getSalary_head_id()[i]);
			//
			// pst = con.prepareStatement(insertEmpSalaryDetails);
			// pst.setInt(1, nEmpId);
			// pst.setInt(2, uF.parseToInt(getSalary_head_id()[i]));
			// pst.setDouble(3, uF.parseToDouble(getSalary_head_value()[i]));
			// pst.setDate (4,
			// uF.getDateFormat(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"",
			// DBDATE, DATE_FORMAT), DATE_FORMAT));
			// pst.setInt(5, 1);
			// pst.setString(6, "M");
			//
			// // pst.setBoolean(7, true); //Need
			// pst.setBoolean(7, uF.parseToBoolean(isDiplaySalaryHead));
			//
			// pst.setInt(8, 0);
			// pst.setDate (9,
			// uF.getDateFormat(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"",
			// DBDATE, DATE_FORMAT), DATE_FORMAT));
			// pst.setString(10,
			// hmEarningDeductionMap.get(getSalary_head_id()[i]));
			// pst.setString(11, hmSalaryTypeMap.get(getSalary_head_id()[i]));
			//
			// // System.out.println("pst ==>"+pst);
			// pst.execute();
			// pst.close();
			// }
	
			int ServiceNo = uF.parseToInt((String) session.getAttribute("ServicesLinkNo"));
			// session.setAttribute("ServicesLinkNo", (ServiceNo-1)+""); //
			// Uncomment this code if you wish to use salary cost center wise.
			session.setAttribute("ServicesLinkNo", 1 + "");
	
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
	
	private void processIncrement(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		updateEmployeeSalaryDetails(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}
	
	private void processConfirmation(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
				pst = con.prepareStatement("update employee_personal_details set emp_status =?  where emp_per_id=?");
				pst.setString(1, PERMANENT);
				pst.setInt(2, nEmpId);
				pst.executeUpdate();
				pst.close();
				
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs !=null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst !=null) {
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		updateEmployeeSalaryDetails(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}

	private void updateEmployeeSalaryDetails(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		try {
	
			// code for getting userid
			// System.out.println("updateEmployeeSalaryDetails: getEmpId==>"+getEmpId());
			String nEmpLevelId = CF.getEmpLevelId(con, "" + nEmpId);
	
			pst = con.prepareStatement("select * from salary_details where level_id=?");
			pst.setInt(1, uF.parseToInt(nEmpLevelId));
			rs = pst.executeQuery();
			Map<String, String> hmEarningDeductionMap = new HashMap<String, String>();
			Map<String, String> hmSalaryTypeMap = new HashMap<String, String>();
			while (rs.next()) {
				hmEarningDeductionMap.put(rs.getString("salary_head_id"), rs.getString("earning_deduction"));
				hmSalaryTypeMap.put(rs.getString("salary_head_id"), rs.getString("salary_type"));
			}
			rs.close();
			pst.close();
	
			pst = con.prepareStatement("select * from emp_salary_details where effective_date = ? and emp_id = ?");
			pst.setDate(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE));
			pst.setInt(2, nEmpId);
			rs = pst.executeQuery();
			boolean isCurrentDateExist = false;
			while (rs.next()) {
				isCurrentDateExist = true;
			}
			rs.close();
			pst.close();
	
			if (isCurrentDateExist) {
				pst = con.prepareStatement("delete from emp_salary_details where effective_date = ? and emp_id = ?");
				pst.setDate(1, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE));
				pst.setInt(2, nEmpId);
				pst.execute();
				pst.close();
			}
	
			List<FillSalaryHeads> salaryHeadList = new ArrayList<FillSalaryHeads>();
			// salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads();
			salaryHeadList = new FillSalaryHeads(request).fillSalaryHeads("" + nEmpLevelId);
	
			int strIncrementType = uF.parseToInt(getStrIncrementType());
			double dblIncremnetPercent = 0.00;
			if (strIncrementType == 2) {
				dblIncremnetPercent = uF.parseToDouble(getStrIncrementPercentage()) * 2;
			} else {
				dblIncremnetPercent = uF.parseToDouble(getStrIncrementPercentage());
			}
	
			if (dblIncremnetPercent > 0.0) {
				EmployeeSalaryDetails salaryDetails = new EmployeeSalaryDetails();
				salaryDetails.CF = CF;
				salaryDetails.request = request;
				salaryDetails.empId = "" + nEmpId;
				salaryDetails.salaryHeadList = salaryHeadList;
				salaryDetails.viewUpdateEmployeeSalaryDetails();
				// System.out.println("reportList======>"+request.getAttribute("reportList"));
				List<List<String>> reportList = (List<List<String>>) request.getAttribute("reportList");
				if (reportList == null)
					reportList = new ArrayList<List<String>>();
	
				if (reportList != null && !reportList.isEmpty()) {
					Map<String, Map<String, String>> hmEmpSalaryMap = new HashMap<String, Map<String, String>>();
					for (int i = 0; i < reportList.size(); i++) {
						List<String> alInner = (List<String>) reportList.get(i);
						Map<String, String> hmInner = new HashMap<String, String>();
						hmInner.put("EMP_SALARY_ID", alInner.get(0));
						hmInner.put("SALARY_HEAD_ID", alInner.get(1));
						hmInner.put("SALARY_HEAD_NAME", alInner.get(2));
						hmInner.put("EARNING_DEDUCTION", alInner.get(3));
						hmInner.put("SALARY_HEAD_AMOUNT_TYPE", alInner.get(4));
						hmInner.put("SUB_SALARY_HEAD_ID", alInner.get(5));
						hmInner.put("SUB_SALARY_HEAD_NAME", alInner.get(6));
						hmInner.put("SALARY_HEAD_AMOUNT", alInner.get(7));
						hmInner.put("AMOUNT", alInner.get(8));
						hmInner.put("ENTRY_DATE", alInner.get(9));
						hmInner.put("USER_ID", alInner.get(10));
						hmInner.put("PAY_TYPE", alInner.get(11));
						hmInner.put("IS_DISPLAY", alInner.get(12));
						hmInner.put("WEIGHT", alInner.get(13));
	
						hmEmpSalaryMap.put(alInner.get(1), hmInner);
					}
					// System.out.println("hmEmpSalaryMap======>"+hmEmpSalaryMap);
	
					if (hmEmpSalaryMap != null && !hmEmpSalaryMap.isEmpty()) {
	
						Map<String, Map<String, String>> hmCalSalaryMap = new HashMap<String, Map<String, String>>();
	
						Map<String, String> hmSalaryHeadId1 = hmEmpSalaryMap.get("" + 1);
						if (hmSalaryHeadId1 != null) {
							double txt_amount = uF.parseToDouble(hmSalaryHeadId1.get("AMOUNT"));
							double newBasic = (txt_amount * dblIncremnetPercent) / 100;
							double finalBasic = txt_amount + newBasic;
							// System.out.println("txt_amount======>"+txt_amount);
							// System.out.println("newBasic======>"+newBasic);
							// System.out.println("finalBasic======>"+finalBasic);
	
							Iterator<String> it = hmEmpSalaryMap.keySet().iterator();
							while (it.hasNext()) {
								String slaryHeadId = (String) it.next();
								Map<String, String> hmInner = hmEmpSalaryMap.get(slaryHeadId);
	
								if (hmInner.get("EARNING_DEDUCTION").equals("E")) {
									if (hmInner.get("SALARY_HEAD_AMOUNT_TYPE").equals("P")) {
										// if(uF.parseToBoolean(hmInner.get("IS_DISPLAY"))){
										// double dblSalaryHeadPercentage =
										// uF.parseToDouble(hmInner.get("SALARY_HEAD_AMOUNT"));
										// double dblPerAmt = 0.00;
										// Map<String, String> hmSalaryHead=
										// hmEmpSalaryMap.get(hmInner.get("SUB_SALARY_HEAD_ID"));
										// if(uF.parseToInt(hmSalaryHead.get("SALARY_HEAD_ID"))
										// == 1){
										// dblPerAmt = (finalBasic *
										// dblSalaryHeadPercentage)/100;
										// } else {
										// double dblSumSalAmt =
										// uF.parseToDouble(hmSalaryHead.get("AMOUNT"));
										// dblPerAmt = (dblSumSalAmt *
										// dblSalaryHeadPercentage)/100;
										// }
										//
										// Map<String, String> hm = new
										// HashMap<String, String>();
										// hm.put("SALARY_HEAD_ID",
										// slaryHeadId);
										// hm.put("EARNING_DEDUCTION",
										// hmInner.get("EARNING_DEDUCTION"));
										// hm.put("AMOUNT",
										// uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
										// hm.put("IS_DISPLAY",
										// hmInner.get("IS_DISPLAY"));
										//
										// hmCalSalaryMap.put(slaryHeadId, hm);
										// } else {
										Map<String, String> hm = new HashMap<String, String>();
										hm.put("SALARY_HEAD_ID", slaryHeadId);
										hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
										hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
										hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
	
										hmCalSalaryMap.put(slaryHeadId, hm);
										// }
									} else {
	
										if (uF.parseToInt(slaryHeadId) == 1) {
											Map<String, String> hm = new HashMap<String, String>();
											hm.put("SALARY_HEAD_ID", slaryHeadId);
											hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
											hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(finalBasic));
											hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
	
											hmCalSalaryMap.put(slaryHeadId, hm);
	
										} else {
											Map<String, String> hm = new HashMap<String, String>();
											hm.put("SALARY_HEAD_ID", slaryHeadId);
											hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
											hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
											hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
	
											hmCalSalaryMap.put(slaryHeadId, hm);
										}
									}
								} else if (hmInner.get("EARNING_DEDUCTION").equals("D")) {
									if (hmInner.get("SALARY_HEAD_AMOUNT_TYPE").equals("P")) {
										// if(uF.parseToBoolean(hmInner.get("IS_DISPLAY"))){
										// double dblSalaryHeadPercentage =
										// uF.parseToDouble(hmInner.get("SALARY_HEAD_AMOUNT"));
										// double dblPerAmt = 0.00;
										// Map<String, String> hmSalaryHead=
										// hmEmpSalaryMap.get(hmInner.get("SUB_SALARY_HEAD_ID"));
										// if(uF.parseToInt(hmSalaryHead.get("SALARY_HEAD_ID"))
										// == 1){
										// dblPerAmt = (finalBasic *
										// dblSalaryHeadPercentage)/100;
										// } else {
										// double dblSumSalAmt =
										// uF.parseToDouble(hmSalaryHead.get("AMOUNT"));
										// dblPerAmt = (dblSumSalAmt *
										// dblSalaryHeadPercentage)/100;
										// }
										//
										// Map<String, String> hm = new
										// HashMap<String, String>();
										// hm.put("SALARY_HEAD_ID",
										// slaryHeadId);
										// hm.put("EARNING_DEDUCTION",
										// hmInner.get("EARNING_DEDUCTION"));
										// hm.put("AMOUNT",
										// uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
										// hm.put("IS_DISPLAY",
										// hmInner.get("IS_DISPLAY"));
										//
										// hmCalSalaryMap.put(slaryHeadId, hm);
										// } else {
										Map<String, String> hm = new HashMap<String, String>();
										hm.put("SALARY_HEAD_ID", slaryHeadId);
										hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
										hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
										hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
	
										hmCalSalaryMap.put(slaryHeadId, hm);
										// }
									} else {
										Map<String, String> hm = new HashMap<String, String>();
										hm.put("SALARY_HEAD_ID", slaryHeadId);
										hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
										hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
										hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
	
										hmCalSalaryMap.put(slaryHeadId, hm);
									}
								}
							}
	
						} else {
	
							Iterator<String> it = hmEmpSalaryMap.keySet().iterator();
							while (it.hasNext()) {
								String slaryHeadId = (String) it.next();
								Map<String, String> hmInner = hmEmpSalaryMap.get(slaryHeadId);
	
								if (hmInner.get("EARNING_DEDUCTION").equals("E")) {
									if (hmInner.get("SALARY_HEAD_AMOUNT_TYPE").equals("P")) {
										// if(uF.parseToBoolean(hmInner.get("IS_DISPLAY"))){
										// double dblSalaryHeadPercentage =
										// uF.parseToDouble(hmInner.get("SALARY_HEAD_AMOUNT"));
										// double dblPerAmt = 0.00;
										// Map<String, String> hmSalaryHead=
										// hmEmpSalaryMap.get(hmInner.get("SUB_SALARY_HEAD_ID"));
										// double txt_amount =
										// uF.parseToDouble(hmSalaryHead.get("AMOUNT"));
										// double dblNew = (txt_amount *
										// dblIncremnetPercent) / 100;
										// double dblFinal = txt_amount +
										// dblNew;
										// dblPerAmt = (dblFinal *
										// dblSalaryHeadPercentage)/100;
										//
										// Map<String, String> hm = new
										// HashMap<String, String>();
										// hm.put("SALARY_HEAD_ID",
										// slaryHeadId);
										// hm.put("EARNING_DEDUCTION",
										// hmInner.get("EARNING_DEDUCTION"));
										// hm.put("AMOUNT",
										// uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
										// hm.put("IS_DISPLAY",
										// hmInner.get("IS_DISPLAY"));
										//
										// hmCalSalaryMap.put(slaryHeadId, hm);
										// } else {
	
										Map<String, String> hm = new HashMap<String, String>();
										hm.put("SALARY_HEAD_ID", slaryHeadId);
										hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
										hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
										hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
	
										hmCalSalaryMap.put(slaryHeadId, hm);
										// }
									} else {
										double dblPerAmt = 0.00;
										double txt_amount = uF.parseToDouble(hmInner.get("AMOUNT"));
										double dblNew = (txt_amount * dblIncremnetPercent) / 100;
										double dblFinal = txt_amount + dblNew;
										dblPerAmt = dblFinal;
	
										Map<String, String> hm = new HashMap<String, String>();
										hm.put("SALARY_HEAD_ID", slaryHeadId);
										hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
										hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
										hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
	
										hmCalSalaryMap.put(slaryHeadId, hm);
									}
								} else if (hmInner.get("EARNING_DEDUCTION").equals("D")) {
									if (hmInner.get("SALARY_HEAD_AMOUNT_TYPE").equals("P")) {
										// if(uF.parseToBoolean(hmInner.get("IS_DISPLAY"))){
										// double dblSalaryHeadPercentage =
										// uF.parseToDouble(hmInner.get("SALARY_HEAD_AMOUNT"));
										// double dblPerAmt = 0.00;
										// Map<String, String> hmSalaryHead=
										// hmEmpSalaryMap.get(hmInner.get("SUB_SALARY_HEAD_ID"));
										// double txt_amount =
										// uF.parseToDouble(hmSalaryHead.get("AMOUNT"));
										// double dblNew = (txt_amount *
										// dblIncremnetPercent) / 100;
										// double dblFinal = txt_amount +
										// dblNew;
										// dblPerAmt = (dblFinal *
										// dblSalaryHeadPercentage)/100;
										//
										// Map<String, String> hm = new
										// HashMap<String, String>();
										// hm.put("SALARY_HEAD_ID",
										// slaryHeadId);
										// hm.put("EARNING_DEDUCTION",
										// hmInner.get("EARNING_DEDUCTION"));
										// hm.put("AMOUNT",
										// uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
										// hm.put("IS_DISPLAY",
										// hmInner.get("IS_DISPLAY"));
										//
										// hmCalSalaryMap.put(slaryHeadId, hm);
										// } else {
	
										Map<String, String> hm = new HashMap<String, String>();
										hm.put("SALARY_HEAD_ID", slaryHeadId);
										hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
										hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmInner.get("AMOUNT"))));
										hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
	
										hmCalSalaryMap.put(slaryHeadId, hm);
										// }
									} else {
										double dblPerAmt = 0.00;
										double txt_amount = uF.parseToDouble(hmInner.get("AMOUNT"));
										double dblNew = (txt_amount * dblIncremnetPercent) / 100;
										double dblFinal = txt_amount + dblNew;
										dblPerAmt = dblFinal;
	
										Map<String, String> hm = new HashMap<String, String>();
										hm.put("SALARY_HEAD_ID", slaryHeadId);
										hm.put("EARNING_DEDUCTION", hmInner.get("EARNING_DEDUCTION"));
										hm.put("AMOUNT", uF.formatIntoTwoDecimalWithOutComma(dblPerAmt));
										hm.put("IS_DISPLAY", hmInner.get("IS_DISPLAY"));
	
										hmCalSalaryMap.put(slaryHeadId, hm);
									}
								}
							}
						}
	
						// System.out.println("hmCalSalaryMap======>"+hmCalSalaryMap);
						Iterator<String> it = hmCalSalaryMap.keySet().iterator();
						while (it.hasNext()) {
							String strSalaryHeadId = (String) it.next();
							Map<String, String> hm = hmCalSalaryMap.get(strSalaryHeadId);
	
							pst = con.prepareStatement("INSERT INTO emp_salary_details (emp_id , salary_head_id, amount, " +
									"entry_date, user_id, pay_type, isdisplay, service_id, effective_date, earning_deduction, " +
									"salary_type,is_approved,level_id) VALUES (?,?,?,?, ?,?,?,?, ?,?,?,?, ?)");
							pst.setInt(1, nEmpId);
							pst.setInt(2, uF.parseToInt(strSalaryHeadId));
							pst.setDouble(3, uF.parseToDouble(hm.get("AMOUNT")));
							pst.setDate(4, uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone()) + "", DBDATE));
							pst.setInt(5, uF.parseToInt(strSessionEmpId));
							pst.setString(6, "M");
							pst.setBoolean(7, uF.parseToBoolean(hm.get("IS_DISPLAY")));
							pst.setInt(8, 0);
							pst.setDate(9, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
							pst.setString(10, hm.get("EARNING_DEDUCTION"));
							pst.setString(11, hmSalaryTypeMap.get(strSalaryHeadId));
							pst.setBoolean(12, true);
							pst.setInt(13, uF.parseToInt(nEmpLevelId));
							// System.out.println("pst2==>");
							pst.execute();
							pst.close();
						}
						
						CF.updateNextEmpSalaryEffectiveDate(con, uF, nEmpId, getEffectiveDate(), DATE_FORMAT);
	
						/**
						 * Calaculate CTC
						 * */
						Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, null, "" + nEmpId);
	
						MyProfile myProfile = new MyProfile();
						myProfile.session = session;
						myProfile.request = request;
						myProfile.CF = CF;
						myProfile.getSalaryHeadsforEmployee(con, uF, nEmpId, hmEmpProfile);
	
						double grossAmount = 0.0d;
						double grossYearAmount = 0.0d;
						double deductAmount = 0.0d;
						double deductYearAmount = 0.0d;
						double netAmount = 0.0d;
						double netYearAmount = 0.0d;
						List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
						for (int i = 0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i < salaryHeadDetailsList.size(); i++) {
							List<String> innerList = salaryHeadDetailsList.get(i);
							if (innerList.get(1).equals("E")) {
								grossAmount += uF.parseToDouble(innerList.get(2));
								grossYearAmount += uF.parseToDouble(innerList.get(3));
							} else if (innerList.get(1).equals("D")) {
								double dblDeductMonth = 0.0d;
								double dblDeductAnnual = 0.0d;
								if (uF.parseToInt(innerList.get(4)) == EMPLOYEE_ESI) {
									dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
									dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
								} else if (uF.parseToInt(innerList.get(4)) == EMPLOYER_ESI) {
									dblDeductMonth = Math.ceil(uF.parseToDouble(innerList.get(2)));
									dblDeductAnnual = Math.ceil(uF.parseToDouble(innerList.get(3)));
								} else {
									dblDeductMonth += Math.round(uF.parseToDouble(innerList.get(2)));
									dblDeductAnnual += Math.round(uF.parseToDouble(innerList.get(3)));
								}
								deductAmount += dblDeductMonth;
								deductYearAmount += dblDeductAnnual;
							}
						}
	
						Map<String, String> hmContribution = (Map<String, String>) request.getAttribute("hmContribution");
						if (hmContribution == null)
							hmContribution = new HashMap<String, String>();
						double dblMonthContri = 0.0d;
						double dblAnnualContri = 0.0d;
						boolean isEPF = uF.parseToBoolean((String) request.getAttribute("isEPF"));
						boolean isESIC = uF.parseToBoolean((String) request.getAttribute("isESIC"));
						boolean isLWF = uF.parseToBoolean((String) request.getAttribute("isLWF"));
						if (isEPF || isESIC || isLWF) {
							if (isEPF) {
								double dblEPFMonth = Math.round(uF.parseToDouble(hmContribution.get("EPF_MONTHLY")));
								double dblEPFAnnual = Math.round(uF.parseToDouble(hmContribution.get("EPF_ANNUALY")));
								dblMonthContri += dblEPFMonth;
								dblAnnualContri += dblEPFAnnual;
							}
							if (isESIC) {
								double dblESIMonth = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_MONTHLY")));
								double dblESIAnnual = Math.ceil(uF.parseToDouble(hmContribution.get("ESI_ANNUALY")));
								dblMonthContri += dblESIMonth;
								dblAnnualContri += dblESIAnnual;
							}
							if (isLWF) {
								double dblLWFMonth = Math.round(uF.parseToDouble(hmContribution.get("LWF_MONTHLY")));
								double dblLWFAnnual = Math.round(uF.parseToDouble(hmContribution.get("LWF_ANNUALY")));
								dblMonthContri += dblLWFMonth;
								dblAnnualContri += dblLWFAnnual;
							}
						}
	
						double dblCTCMonthly = grossAmount + dblMonthContri;
						double dblCTCAnnualy = grossYearAmount + dblAnnualContri;
	
						List<List<String>> salaryAnnualVariableDetailsList = (List<List<String>>) request.getAttribute("salaryAnnualVariableDetailsList");
						if (salaryAnnualVariableDetailsList == null)
							salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
						int nAnnualVariSize = salaryAnnualVariableDetailsList.size();
						if (nAnnualVariSize > 0) {
							double grossAnnualAmount = 0.0d;
							double grossAnnualYearAmount = 0.0d;
							for (int i = 0; i < nAnnualVariSize; i++) {
								List<String> innerList = salaryAnnualVariableDetailsList.get(i);
								double dblEarnMonth = Math.round(uF.parseToDouble(innerList.get(2)));
								double dblEarnAnnual = Math.round(uF.parseToDouble(innerList.get(3)));
								grossAnnualAmount += dblEarnMonth;
								grossAnnualYearAmount += dblEarnAnnual;
							}
							dblCTCMonthly += grossAnnualAmount;
							dblCTCAnnualy += grossAnnualYearAmount;
						}
	
						netAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCMonthly));
						netYearAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblCTCAnnualy));
	
						EmpSalaryApproval salaryApproval = new EmpSalaryApproval();
						salaryApproval.request = request;
						salaryApproval.session = session;
						salaryApproval.CF = CF;
						Map<String, String> hmPrevCTC = salaryApproval.getPrevCTCDetails(con, uF, "" + nEmpId);
	
						if (hmPrevCTC == null)
							hmPrevCTC = new HashMap<String, String>();
						double dblIncrementMonthAmt = netAmount - uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC"));
						double dblIncrementAnnualAmt = netYearAmount - uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC"));
	
						pst = con.prepareStatement("update employee_official_details set month_ctc=?,annual_ctc=?,prev_month_ctc=?,"
								+ "prev_annual_ctc=?,incre_month_amount=?,incre_annual_amount=? where emp_id=?");
						pst.setDouble(1, netAmount);
						pst.setDouble(2, netYearAmount);
						pst.setDouble(3, uF.parseToDouble(hmPrevCTC.get("PREV_MONTH_CTC")));
						pst.setDouble(4, uF.parseToDouble(hmPrevCTC.get("PREV_ANNUAL_CTC")));
						pst.setDouble(5, dblIncrementMonthAmt);
						pst.setDouble(6, dblIncrementAnnualAmt);
						pst.setInt(7, nEmpId);
						pst.execute();
						pst.close();
					}
				}
			}
	
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
	
	private void processTemporary(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement(updateUserStatus);
			pst.setString(1, IConstants.TEMPORARY);
			pst.setInt(2, nEmpId);
			pst.execute();
			pst.close();
			
			sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		} catch (Exception e) {
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
	
	private void processPermanent(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement(updateUserStatus);
			pst.setString(1, IConstants.PERMANENT);
			pst.setInt(2, nEmpId);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x > 0){
				String levelId = CF.getEmpLevelId(con, ""+nEmpId);
				List<List<String>> leaveTypeListWithBalance = CF.getLevelLeaveTypeBalanceForEmp(con, levelId, ""+nEmpId, CF);
				for(int i=0; leaveTypeListWithBalance != null && !leaveTypeListWithBalance.isEmpty() && i<leaveTypeListWithBalance.size(); i++) {
					List<String> innerList = leaveTypeListWithBalance.get(i);
					String leaveTypeId = innerList.get(0);
					String strLeaveBalance = innerList.get(2);
			//===start parvez date: 19-11-2021===		
	//				pst=con.prepareStatement("delete from leave_register1 where emp_id=? and _date=? and leave_type_id=? and _type=?");
					pst=con.prepareStatement("delete from leave_register1 where emp_id=? and _date>=? and leave_type_id=? and _type=?");
			//===end parvez date: 19-11-2021===		
					pst.setInt(1, nEmpId);
					pst.setDate(2, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
					pst.setInt(3, uF.parseToInt(leaveTypeId));
					pst.setString(4, "C");
					pst.execute();
					pst.close();
					
					pst=con.prepareStatement("insert into leave_register1(emp_id,_date,balance,leave_type_id,_type)values(?,?,?,?,?)");
					pst.setInt(1, nEmpId);
					pst.setDate(2, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
					pst.setDouble(3, uF.parseToDouble(strLeaveBalance));
					pst.setInt(4, uF.parseToInt(leaveTypeId));
					pst.setString(5, "C");
					pst.execute();
					pst.close();
				}
			}
			
			sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
		} catch (Exception e) {
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
	
	private void processOfferLetter(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
	}
	
	private void processGradeChange(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		
		PreparedStatement pst = null;
		
		try {
			
			pst = con.prepareStatement("UPDATE employee_official_details SET grade_id=? where emp_id=?");
			pst.setInt(1, uF.parseToInt(getEmpChangeGrade()));
			pst.setInt(2, nEmpId);
			pst.execute();
			pst.close();
			
			sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
			
		} catch (Exception e) {
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
	
	private void processAppointmentLetter(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);		
	}
	
	private void processWithdrawnResignation(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		
		PreparedStatement pst = null;
		
		try {
			
			Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
			Date effectDate = uF.getDateFormat(getEffectiveDate(), DATE_FORMAT);
			int dateResult = effectDate.compareTo(currDate);
			
	//		System.out.println("dateResult ===>> " + dateResult);
			
			if(dateResult < 1) {
				pst = con.prepareStatement(updateUserDetailsStatus);
				pst.setString(1, "ACTIVE");
				pst.setInt(2, nEmpId);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement(updateUserStatus2);
				pst.setString(1, PERMANENT);
				pst.setBoolean(2, true);
				pst.setDate(3, null);
				pst.setInt(4, nEmpId);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("delete from emp_off_board where emp_id=?");
				pst.setInt(1, nEmpId);
				pst.execute();
				pst.close();
				
				sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
			}
			
		} catch (Exception e) {
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
	
	private void processTransfer(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
			Date effectDate = uF.getDateFormat(getEffectiveDate(), DATE_FORMAT);
			int dateResult = effectDate.compareTo(currDate);
			
			if(dateResult < 1) {
				int departHod = 0;
				pst = con.prepareStatement("select emp_id from employee_official_details where depart_id = ? and is_hod = true limit 1");
				pst.setInt(1, uF.parseToInt(getStrDepartment()));
				rs = pst.executeQuery();
				while(rs.next()) {
					departHod = rs.getInt("emp_id");
				}
				rs.close();
				pst.close();
				
				StringBuilder sb = new StringBuilder();
				sb.append("UPDATE employee_official_details SET emp_id = "+nEmpId+"");
				if(uF.parseToInt(getStrDepartment()) > 0) {
					sb.append(", depart_id= "+uF.parseToInt(getStrDepartment())+"");
				}
				if(uF.parseToInt(getStrSBU()) > 0) {
					sb.append(", service_id= ',"+uF.parseToInt(getStrSBU())+",'");
				}
				if(uF.parseToInt(getStrWLocation()) > 0) {
					sb.append(", wlocation_id= "+uF.parseToInt(getStrWLocation())+" ");
				}
				
				if(uF.parseToInt(getEmpGrade()) > 0) {
					sb.append(", grade_id= "+uF.parseToInt(getEmpGrade())+" ");
				}
				
				if(uF.parseToInt(getStrOrganisation()) > 0) {
					sb.append(", org_id= "+uF.parseToInt(getStrOrganisation())+" ");
				}
				if(departHod > 0) {
					sb.append(", hod_emp_id= "+departHod+" ");
				}
				if(getEmploymentType() != null) {
					sb.append(", emptype= '"+getEmploymentType()+"' ");
				}
				sb.append(" WHERE emp_id=?");
				pst = con.prepareStatement(sb.toString());
				pst.setInt(1, nEmpId);
				pst.execute();
				pst.close();
				
				if(uF.parseToInt(getStrWLocation()) > 0 && getStrTransferType() !=null && getStrTransferType().trim().equalsIgnoreCase("WL")){
					String strDomain = request.getServerName().split("\\.")[0];
					AssignLeaveCron leaveCron = new AssignLeaveCron();
					leaveCron.request = request;
					leaveCron.session = session;
					leaveCron.CF = CF;
					leaveCron.strDomain = strDomain;
					leaveCron.strWlocationId = getStrWLocation();
					leaveCron.strEmpId = ""+nEmpId;
					leaveCron.setCronData();
				} else if(uF.parseToInt(getStrLevel()) > 0 && getStrTransferType() !=null && getStrTransferType().trim().equalsIgnoreCase("LE")){
					String strDomain = request.getServerName().split("\\.")[0];
					AssignLeaveCron leaveCron = new AssignLeaveCron();
					leaveCron.request = request;
					leaveCron.session = session;
					leaveCron.CF = CF;
					leaveCron.strDomain = strDomain;
					leaveCron.strLevelId = getStrLevel();
					leaveCron.strEmpId = ""+nEmpId;
					leaveCron.setCronData();
				}
				
				sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
			}
			
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
	
	private void processTerminate(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			if(CF.getIsTerminateWithoutFullAndFinal()) {
				pst = con.prepareStatement("UPDATE user_details SET status=? where emp_id=?");
				pst.setString(1, "INACTIVE");
				pst.setInt(2, nEmpId);
	//			System.out.println("pst1==>"+pst);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("UPDATE employee_personal_details SET emp_status=?, is_alive=?, employment_end_date=? where emp_per_id=?");
				pst.setString(1, TERMINATED);
				pst.setBoolean(2, false);
				pst.setDate(3, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setInt(4, nEmpId);
	//			System.out.println("pst2==>"+pst);
				int y = pst.executeUpdate();
				pst.close();
				
				if(y > 0){
					pst = con.prepareStatement("delete from leave_register1 where emp_id=? and _date>?");
					pst.setInt(1, nEmpId);
					pst.setDate(2, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
					pst.executeUpdate();
					pst.close();
				}
				
			} else {
			Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
			Date effectDate = uF.getDateFormat(getEffectiveDate(), DATE_FORMAT);
			int dateResult = effectDate.compareTo(currDate);
			
			pst = con.prepareStatement("select emp_status from employee_personal_details where emp_per_id = ?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			String empStatus = "";
			while(rs.next()) {
				empStatus = rs.getString("emp_status");
			}
			rs.close();
			pst.close();
			
			if(dateResult < 1) {
				pst = con.prepareStatement(updateUserDetailsStatus);
				pst.setString(1, "INACTIVE");
				pst.setInt(2, nEmpId);
				pst.execute();
				pst.close();
				
				pst = con.prepareStatement("UPDATE employee_personal_details SET emp_status=?, employment_end_date=? where emp_per_id=?");
				pst.setString(1, TERMINATED);
				pst.setDate(2, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setInt(3, nEmpId);
				int y = pst.executeUpdate();
				pst.close();
				
				if(y > 0){
					pst = con.prepareStatement("delete from leave_register1 where emp_id=? and _date>?");
					pst.setInt(1, nEmpId);
					pst.setDate(2, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
					pst.executeUpdate();
					pst.close();
				}
				
				if(strUserType!=null && strUserType.equals(ADMIN)) {
					pst = con.prepareStatement("insert into emp_off_board (emp_id, off_board_type, emp_reason, entry_date, notice_days, last_day_date,approved_1_by,approved_2_by, approved_1, approved_2,approved_1_date,approved_2_date,approved_1_reason,previous_emp_status) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
					pst.setInt(1, nEmpId);
					pst.setString(2, IConstants.TERMINATED);
					pst.setString(3, "Direct termination");
					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(5, 0);
					pst.setDate(6, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
					pst.setInt(7,uF.parseToInt(strSessionEmpId));
					pst.setInt(8, uF.parseToInt(strSessionEmpId));
					pst.setInt(9, 1);
					pst.setInt(10, 1);
					pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(13,getStrReason());
					pst.setString(14, empStatus);
				}else {
					pst = con.prepareStatement("insert into emp_off_board (emp_id, off_board_type, emp_reason, entry_date, notice_days, last_day_date,approved_1_by, approved_1, approved_2,approved_1_date,approved_1_reason,previous_emp_status) values (?,?,?,?, ?,?,?,?, ?,?,?,?)");
					pst.setInt(1, nEmpId);
					pst.setString(2, IConstants.TERMINATED);
					pst.setString(3, "Direct termination");
					pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setInt(5, 0);
					pst.setDate(6, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
					pst.setInt(7,uF.parseToInt(strSessionEmpId));
					pst.setInt(8, 1);
					pst.setInt(9, 1);
					pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
					pst.setString(11,getStrReason());
					pst.setString(12, empStatus);
				}
				int x = pst.executeUpdate();
				pst.close();
				
				if(x > 0) {
					pst = con.prepareStatement("select max(off_board_id) as off_board_id from emp_off_board");
					rs = pst.executeQuery();
					int nOffBoardId = 0;
					while(rs.next()) {
						nOffBoardId = uF.parseToInt(rs.getString("off_board_id"));
					}
					
					rs.close();
					pst.close();
					
					if(nOffBoardId > 0) {
						String policy_id=null;
						int empId = 0;
						int userTypeId = 0;
						Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
						String empLevelId=hmEmpLevelMap.get(""+nEmpId);
						Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
						String locationID=hmEmpWlocationMap.get(""+nEmpId);
						
						Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
					
						pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_TERMINATION+"' and level_id=? and wlocation_id=?");
						pst.setInt(1, uF.parseToInt(empLevelId));
						pst.setInt(2, uF.parseToInt(locationID));
	//					System.out.println("pst====>"+pst);
						rs = pst.executeQuery();
						while(rs.next()){
							policy_id=rs.getString("policy_id");
						}
						rs.close();
						pst.close();
						
						if(uF.parseToInt(policy_id) == 0){
							pst = con.prepareStatement("select policy_count from work_flow_member wfm,work_flow_policy wfp where wfp.group_id=wfm.group_id " +
									"and wfp.work_flow_member_id=wfm.work_flow_member_id and wfm.wlocation_id=? and wfm.is_default = true");
							pst.setInt(1, uF.parseToInt(locationID));
							rs = pst.executeQuery();
							while(rs.next()){
								policy_id=rs.getString("policy_count");
							}
							rs.close();
							pst.close();
						}
	//					System.out.println("policyId == >"+ policy_id);
						if(uF.parseToInt(policy_id) > 0){
							pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
							" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
							pst.setInt(1,uF.parseToInt(policy_id));
	//						System.out.println("pst 1==>"+pst);
							rs=pst.executeQuery();
							Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
							while(rs.next()){
								List<String> innerList=new ArrayList<String>();
								innerList.add(rs.getString("member_type"));
								innerList.add(rs.getString("member_id"));
								innerList.add(rs.getString("member_position"));
								innerList.add(rs.getString("work_flow_mem"));
								innerList.add(rs.getString("work_flow_member_id"));
								
								hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
							}
							rs.close();
							pst.close();
					
							String strDomain = request.getServerName().split("\\.")[0];
							Map<String,String> hmMemberOption=new LinkedHashMap<String,String>();
							
							Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
							if(hmUserTypeIdMap==null) hmUserTypeIdMap = new HashMap<String, String>();
							Iterator<String> it=hmMemberMap.keySet().iterator();
							
							while(it.hasNext()){
								String work_flow_member_id=it.next();
								List<String> innerList=hmMemberMap.get(work_flow_member_id);
								
								if(uF.parseToInt(innerList.get(0))==1){
									int memid=uF.parseToInt(innerList.get(1));
									switch(memid){
										case 1:
												pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
																+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
																+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and eod.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
																+ " and ud.emp_id not in(?) and epd.is_alive=true limit 1");
												pst.setInt(1, nEmpId);
												rs = pst.executeQuery();
												
												while (rs.next()) {
													empId = rs.getInt("emp_id");
													userTypeId = rs.getInt("usertype_id");
												
												}
												rs.close();
												pst.close();
												
												pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
														"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
														"values(?,?,?,?, ?,?,?,?, ?,?)");
												pst.setInt(1,empId);
												pst.setInt(2,nOffBoardId);
												pst.setString(3,WORK_FLOW_TERMINATION);
												pst.setInt(4,uF.parseToInt(innerList.get(0)));
												pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
												pst.setInt(6,uF.parseToInt(innerList.get(4)));
												pst.setInt(7,1);
												pst.setInt(8,1);
												pst.setInt(9,userTypeId);
												pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
												pst.execute();
												pst.close();
												break;
											
										case 2:
												pst = con.prepareStatement("select * from (select supervisor_emp_id from employee_official_details where emp_id= ? and supervisor_emp_id!=0) as a," +
														"employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" +
														" and epd.is_alive=true limit 1");
												pst.setInt(1, nEmpId);
												rs = pst.executeQuery();
											
												while (rs.next()) {
													empId = rs.getInt("emp_id");
													userTypeId = rs.getInt("usertype_id");
																		
												}
												rs.close();
												pst.close();
												
												pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
														"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
														"values(?,?,?,?, ?,?,?,?, ?,?)");
												pst.setInt(1,empId);
												pst.setInt(2,nOffBoardId);
												pst.setString(3,WORK_FLOW_TERMINATION);
												pst.setInt(4,uF.parseToInt(innerList.get(0)));
												pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
												pst.setInt(6,uF.parseToInt(innerList.get(4)));
												pst.setInt(7,1);
												pst.setInt(8,1);
												pst.setInt(9,userTypeId);
												pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
												pst.execute();
												pst.close();
												break;
											
										case 3:
												pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
																+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 "
																+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and eod.emp_id = epd.emp_per_id " 
																+" and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true limit 1");
												pst.setInt(1, uF.parseToInt(locationID));
												pst.setInt(2, nEmpId);
												rs = pst.executeQuery();
												while (rs.next()) {
													empId = rs.getInt("emp_id");
													userTypeId = rs.getInt("usertype_id");
												
												}
												rs.close();
												pst.close();
												
												pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
														"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
														"values(?,?,?,?, ?,?,?,?, ?,?)");
												pst.setInt(1,empId);
												pst.setInt(2,nOffBoardId);
												pst.setString(3,WORK_FLOW_TERMINATION);
												pst.setInt(4,uF.parseToInt(innerList.get(0)));
												pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
												pst.setInt(6,uF.parseToInt(innerList.get(4)));
												pst.setInt(7,1);
												pst.setInt(8,1);
												pst.setInt(9,userTypeId);
												pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
												pst.execute();
												pst.close();
												break;
										
										case 4:
												pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
														+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 "
														+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id "
														+"  and eod.emp_id=epd.emp_per_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true limit 1");
												
												pst.setInt(1, nEmpId);
												rs = pst.executeQuery();
												while (rs.next()) {
													empId = rs.getInt("emp_id");
													userTypeId = rs.getInt("usertype_id");
																	
												}
												rs.close();
												pst.close();
												
												pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
														"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
														"values(?,?,?,?, ?,?,?,?, ?,?)");
												pst.setInt(1,empId);
												pst.setInt(2,nOffBoardId);
												pst.setString(3,WORK_FLOW_TERMINATION);
												pst.setInt(4,uF.parseToInt(innerList.get(0)));
												pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
												pst.setInt(6,uF.parseToInt(innerList.get(4)));
												pst.setInt(7,1);
												pst.setInt(8,1);
												pst.setInt(9,userTypeId);
												pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
												pst.execute();
												pst.close();
												break;
										
										case 5:
												pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
														+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=5 "
														+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id "
														+ " and eod.emp_id=epd.emp_per_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true limit 1");
												
												pst.setInt(1, nEmpId);
												rs = pst.executeQuery();
												
												while (rs.next()) {
													empId = rs.getInt("emp_id");
													userTypeId = rs.getInt("usertype_id");
																			
												}
												rs.close();
												pst.close();
												
												pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
														"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
														"values(?,?,?,?, ?,?,?,?, ?,?)");
												pst.setInt(1,empId);
												pst.setInt(2,nOffBoardId);
												pst.setString(3,WORK_FLOW_TERMINATION);
												pst.setInt(4,uF.parseToInt(innerList.get(0)));
												pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
												pst.setInt(6,uF.parseToInt(innerList.get(4)));
												pst.setInt(7,1);
												pst.setInt(8,1);
												pst.setInt(9,userTypeId);
												pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
												pst.execute();
												pst.close();
												break;
											
										case 6:
												pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
														+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=6 "
														+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id "
														+ " and eod.emp_id=epd.emp_per_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true limit 1");
												pst.setInt(1, uF.parseToInt(locationID));
												pst.setInt(2, nEmpId);
												rs = pst.executeQuery();
												
												while (rs.next()) {
													empId = rs.getInt("emp_id");
													userTypeId = rs.getInt("usertype_id");
																			
												}
												rs.close();
												pst.close();
												pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
														"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
														"values(?,?,?,?, ?,?,?,?, ?,?)");
												pst.setInt(1,empId);
												pst.setInt(2,nOffBoardId);
												pst.setString(3,WORK_FLOW_TERMINATION);
												pst.setInt(4,uF.parseToInt(innerList.get(0)));
												pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
												pst.setInt(6,uF.parseToInt(innerList.get(4)));
												pst.setInt(7,1);
												pst.setInt(8,1);
												pst.setInt(9,userTypeId);
												pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
												pst.execute();
												pst.close();
												break;
											
										case 7:
											pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud, employee_official_details eod," +
													"employee_personal_details epd where ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id " +
													"and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true and ud.emp_id in (select eod.emp_hr from employee_official_details eod," +
													"employee_personal_details epd where epd.emp_per_id=eod.emp_id and eod.emp_id=?)" +
													" union " +
													"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud, employee_official_details eod," +
													"employee_personal_details epd where ud.usertype_id=7 and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' " +
													"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true " +
													" union " +
													"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud, employee_official_details eod," +
													"employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and " +
													"epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true limit 1");
												pst.setInt(1, nEmpId);
												pst.setInt(2, nEmpId);
												pst.setInt(3, nEmpId);
												pst.setInt(4, nEmpId);
												rs = pst.executeQuery();
												while (rs.next()) {
													empId = rs.getInt("emp_id");
													userTypeId = rs.getInt("usertype_id");
																			
												}
												rs.close();
												pst.close();
												
												pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
														"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
														"values(?,?,?,?, ?,?,?,?, ?,?)");
												pst.setInt(1,empId);
												pst.setInt(2,nOffBoardId);
												pst.setString(3,WORK_FLOW_TERMINATION);
												pst.setInt(4,uF.parseToInt(innerList.get(0)));
												pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
												pst.setInt(6,uF.parseToInt(innerList.get(4)));
												pst.setInt(7,1);
												pst.setInt(8,1);
												pst.setInt(9,userTypeId);
												pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
												pst.execute();
												pst.close();
												break;		
											
										case 13:
											pst = con.prepareStatement("select * from (select distinct(hod_emp_id) as hod_emp_id from employee_official_details where " +
													"emp_id=? and hod_emp_id!=0) as a,employee_personal_details epd,user_details ud where a.hod_emp_id=epd.emp_per_id " +
													"and ud.emp_id=epd.emp_per_id  and ud.status='ACTIVE' and epd.is_alive=true order by epd.emp_fname limit 1");
											pst.setInt(1, nEmpId);
											rs = pst.executeQuery();
											
											while (rs.next()) {
												empId = rs.getInt("emp_id");
												userTypeId = rs.getInt("usertype_id");
																			
											}
											rs.close();
											pst.close();
											
											pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
													"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
													"values(?,?,?,?, ?,?,?,?, ?,?)");
											pst.setInt(1,empId);
											pst.setInt(2,nOffBoardId);
											pst.setString(3,WORK_FLOW_TERMINATION);
											pst.setInt(4,uF.parseToInt(innerList.get(0)));
											pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
											pst.setInt(6,uF.parseToInt(innerList.get(4)));
											pst.setInt(7,1);
											pst.setInt(8,1);
											pst.setInt(9,userTypeId);
											pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
											pst.execute();
											pst.close();
											break;
										
									}
									
								}
							}
						}
						
					}
					sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
				}
			}
		 }
			
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
	
//===created by parvez date: 17-08-2022===
	//===start===
	private void processResignation(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
			Date effectDate = uF.getDateFormat(getEffectiveDate(), DATE_FORMAT);
			int dateResult = effectDate.compareTo(currDate);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			pst = con.prepareStatement("select * from employee_personal_details epd,probation_policy p where epd.emp_per_id = p.emp_id and emp_id = ?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			int nNoticeDays = 0;
			while(rs.next()){
				if(rs.getString("emp_status")!=null && !rs.getString("emp_status").equalsIgnoreCase("TERMINATED")){
					nNoticeDays = rs.getInt("notice_duration");
					
				}
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select emp_status from employee_personal_details where emp_per_id = ?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			String empStatus = "";
			while(rs.next()) {
				empStatus = rs.getString("emp_status");
			}
			rs.close();
			pst.close();
			
			/*pst = con.prepareStatement("insert into emp_off_board (emp_id, off_board_type, emp_reason, entry_date, notice_days, last_day_date,previous_emp_status) values (?,?,?,?,?,?,?)");
			pst.setInt(1, nEmpId);
			pst.setString(2, RESIGNED);
			pst.setString(3, "");
			pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			
			pst.setInt(5, nNoticeDays);
			pst.setDate(6, uF.getFutureDate(uF.getDateFormat(getEffectiveDate(), DATE_FORMAT), nNoticeDays));
//			pst.setDate(6, uF.getFutureDate(CF.getStrTimeZone(), nNoticeDays));
			pst.setString(7, empStatus);
			System.out.println("new pst==>"+pst);
			int x = pst.executeUpdate();
			pst.close();*/
			
			pst = con.prepareStatement(updateUserDetailsStatus);
			pst.setString(1, "INACTIVE");
			pst.setInt(2, nEmpId);
			pst.execute();
			pst.close();
			
//			pst = con.prepareStatement("UPDATE employee_personal_details SET emp_status=? where emp_per_id=?");
			pst = con.prepareStatement("UPDATE employee_personal_details SET emp_status=?, is_alive=?, employment_end_date=? where emp_per_id=?");
			pst.setString(1, RESIGNED);
			pst.setBoolean(2, false);
			pst.setDate(3, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setInt(4, nEmpId);
			int y = pst.executeUpdate();
			pst.close();
			
			if(y > 0){
				pst = con.prepareStatement("delete from leave_register1 where emp_id=? and _date>?");
				pst.setInt(1, nEmpId);
				pst.setDate(2, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.executeUpdate();
				pst.close();
			}
				
			if(strUserType!=null && strUserType.equals(ADMIN)) {
				pst = con.prepareStatement("insert into emp_off_board (emp_id, off_board_type, emp_reason, entry_date, notice_days, last_day_date,approved_1_by,approved_2_by, approved_1, approved_2,approved_1_date,approved_2_date,approved_1_reason,previous_emp_status) values (?,?,?,?, ?,?,?,?, ?,?,?,?, ?,?)");
				pst.setInt(1, nEmpId);
				pst.setString(2, IConstants.RESIGNED);
//				pst.setString(3, "Bulk Resignation");
				pst.setString(3, getStrReason());
				pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
//				pst.setInt(5, nNoticeDays);
				pst.setInt(5, 0);
//				pst.setDate(6, uF.getFutureDate(uF.getDateFormat(getEffectiveDate(), DATE_FORMAT), nNoticeDays));
				pst.setDate(6, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setInt(7,uF.parseToInt(strSessionEmpId));
				pst.setInt(8, uF.parseToInt(strSessionEmpId));
				pst.setInt(9, 1);
				pst.setInt(10, 1);
				pst.setDate(11, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setDate(12, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(13,getStrReason());
				pst.setString(14, empStatus);
			}else {
				pst = con.prepareStatement("insert into emp_off_board (emp_id, off_board_type, emp_reason, entry_date, notice_days, last_day_date,approved_1_by, approved_1, approved_2,approved_1_date,approved_1_reason,previous_emp_status) values (?,?,?,?, ?,?,?,?, ?,?,?,?)");
				pst.setInt(1, nEmpId);
				pst.setString(2, IConstants.RESIGNED);
//				pst.setString(3, "Bulk Resignation");
				pst.setString(3, getStrReason());
				pst.setTimestamp(4, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
				pst.setInt(5, nNoticeDays);
//				pst.setDate(6, uF.getFutureDate(uF.getDateFormat(getEffectiveDate(), DATE_FORMAT), nNoticeDays));
				pst.setDate(6, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
				pst.setInt(7,uF.parseToInt(strSessionEmpId));
				pst.setInt(8, 1);
				pst.setInt(9, 1);
				pst.setDate(10, uF.getCurrentDate(CF.getStrTimeZone()));
				pst.setString(11,getStrReason());
				pst.setString(12, empStatus);
			}
			int x = pst.executeUpdate();
			pst.close();
				
			if(x > 0) {
				pst = con.prepareStatement("select max(off_board_id) as off_board_id from emp_off_board");
				rs = pst.executeQuery();
				int nOffBoardId = 0;
				while(rs.next()) {
					nOffBoardId = uF.parseToInt(rs.getString("off_board_id"));
				}
				rs.close();
				pst.close();
					
				if(nOffBoardId > 0) {
					String policy_id=null;
					int empId = 0;
					int userTypeId = 0;
					Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
					String empLevelId=hmEmpLevelMap.get(""+nEmpId);
					Map<String, String> hmEmpWlocationMap =CF.getEmpWlocationMap(con);
					String locationID=hmEmpWlocationMap.get(""+nEmpId);
						
					Map<String, String> hmEmpCodeName =CF.getEmpNameMap(con, null, null);
					
					pst = con.prepareStatement("select policy_id from work_flow_policy_details where type='"+WORK_FLOW_RESIGN+"' and level_id=? and wlocation_id=?");
					pst.setInt(1, uF.parseToInt(empLevelId));
					pst.setInt(2, uF.parseToInt(locationID));
	//				System.out.println("pst====>"+pst);
					rs = pst.executeQuery();
					while(rs.next()){
						policy_id=rs.getString("policy_id");
					}
					rs.close();
					pst.close();
						
					if(uF.parseToInt(policy_id) == 0){
						pst = con.prepareStatement("select policy_count from work_flow_member wfm,work_flow_policy wfp where wfp.group_id=wfm.group_id " +
								"and wfp.work_flow_member_id=wfm.work_flow_member_id and wfm.wlocation_id=? and wfm.is_default = true");
						pst.setInt(1, uF.parseToInt(locationID));
						rs = pst.executeQuery();
						while(rs.next()){
							policy_id=rs.getString("policy_count");
						}
						rs.close();
						pst.close();
					}
	//				System.out.println("policyId == >"+ policy_id);
					
					if(uF.parseToInt(policy_id) > 0){
						pst=con.prepareStatement("select * from work_flow_member as a,work_flow_policy wfp where " +
							" policy_count=? and policy_type='1' and wfp.work_flow_member_id=a.work_flow_member_id order by member_position");
						pst.setInt(1,uF.parseToInt(policy_id));
	//					System.out.println("pst 1==>"+pst);
						rs=pst.executeQuery();
						Map<String,List<String>> hmMemberMap=new LinkedHashMap<String, List<String>>();
						while(rs.next()){
							List<String> innerList=new ArrayList<String>();
							innerList.add(rs.getString("member_type"));
							innerList.add(rs.getString("member_id"));
							innerList.add(rs.getString("member_position"));
							innerList.add(rs.getString("work_flow_mem"));
							innerList.add(rs.getString("work_flow_member_id"));
								
							hmMemberMap.put(rs.getString("work_flow_member_id"), innerList);
						}
						rs.close();
						pst.close();
					
						String strDomain = request.getServerName().split("\\.")[0];
						Map<String,String> hmMemberOption=new LinkedHashMap<String,String>();
							
						Map<String, String> hmUserTypeIdMap = CF.getUserTypeIdMap(con);
						if(hmUserTypeIdMap==null) hmUserTypeIdMap = new HashMap<String, String>();
						Iterator<String> it=hmMemberMap.keySet().iterator();
							
						while(it.hasNext()){
							String work_flow_member_id=it.next();
							List<String> innerList=hmMemberMap.get(work_flow_member_id);
								
							if(uF.parseToInt(innerList.get(0))==1){
								int memid=uF.parseToInt(innerList.get(1));
								switch(memid){
									case 1:
											pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
															+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=1 "
															+ " and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and eod.emp_id=epd.emp_per_id and ud.status='ACTIVE'" 
															+ " and ud.emp_id not in(?) and epd.is_alive=true limit 1");
											pst.setInt(1, nEmpId);
											rs = pst.executeQuery();
												
											while (rs.next()) {
												empId = rs.getInt("emp_id");
												userTypeId = rs.getInt("usertype_id");
												
											}
											rs.close();
											pst.close();
												
											pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
														"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
														"values(?,?,?,?, ?,?,?,?, ?,?)");
											pst.setInt(1,empId);
											pst.setInt(2,nOffBoardId);
											pst.setString(3,WORK_FLOW_RESIGN);
											pst.setInt(4,uF.parseToInt(innerList.get(0)));
											pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
											pst.setInt(6,uF.parseToInt(innerList.get(4)));
											pst.setInt(7,1);
											pst.setInt(8,1);
											pst.setInt(9,userTypeId);
											pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
											pst.execute();
											pst.close();
											break;
											
									case 2:
											pst = con.prepareStatement("select * from (select supervisor_emp_id from employee_official_details where emp_id= ? and supervisor_emp_id!=0) as a," +
														"employee_personal_details epd,user_details ud where a.supervisor_emp_id=epd.emp_per_id and ud.emp_id=epd.emp_per_id and ud.status='ACTIVE'" +
														" and epd.is_alive=true limit 1");
											pst.setInt(1, nEmpId);
											rs = pst.executeQuery();
											
											while (rs.next()) {
												empId = rs.getInt("emp_id");
												userTypeId = rs.getInt("usertype_id");
																		
											}
											rs.close();
											pst.close();
												
											pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
														"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
														"values(?,?,?,?, ?,?,?,?, ?,?)");
											pst.setInt(1,empId);
											pst.setInt(2,nOffBoardId);
											pst.setString(3,WORK_FLOW_RESIGN);
											pst.setInt(4,uF.parseToInt(innerList.get(0)));
											pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
											pst.setInt(6,uF.parseToInt(innerList.get(4)));
											pst.setInt(7,1);
											pst.setInt(8,1);
											pst.setInt(9,userTypeId);
											pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
											pst.execute();
											pst.close();
											break;
											
									case 3:
											pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
																+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=3 "
																+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id and eod.emp_id = epd.emp_per_id " 
																+" and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true limit 1");
											pst.setInt(1, uF.parseToInt(locationID));
											pst.setInt(2, nEmpId);
											rs = pst.executeQuery();
											while (rs.next()) {
												empId = rs.getInt("emp_id");
												userTypeId = rs.getInt("usertype_id");
												
											}
											rs.close();
											pst.close();
												
											pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
														"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
														"values(?,?,?,?, ?,?,?,?, ?,?)");
											pst.setInt(1,empId);
											pst.setInt(2,nOffBoardId);
											pst.setString(3,WORK_FLOW_RESIGN);
											pst.setInt(4,uF.parseToInt(innerList.get(0)));
											pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
											pst.setInt(6,uF.parseToInt(innerList.get(4)));
											pst.setInt(7,1);
											pst.setInt(8,1);
											pst.setInt(9,userTypeId);
											pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
											pst.execute();
											pst.close();
											break;
										
									case 4:
											pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
													+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=4 "
													+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id "
													+"  and eod.emp_id=epd.emp_per_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true limit 1");
												
											pst.setInt(1, nEmpId);
											rs = pst.executeQuery();
											while (rs.next()) {
												empId = rs.getInt("emp_id");
												userTypeId = rs.getInt("usertype_id");
																	
											}
											rs.close();
											pst.close();
												
											pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
														"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
														"values(?,?,?,?, ?,?,?,?, ?,?)");
											pst.setInt(1,empId);
											pst.setInt(2,nOffBoardId);
											pst.setString(3,WORK_FLOW_RESIGN);
											pst.setInt(4,uF.parseToInt(innerList.get(0)));
											pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
											pst.setInt(6,uF.parseToInt(innerList.get(4)));
											pst.setInt(7,1);
											pst.setInt(8,1);
											pst.setInt(9,userTypeId);
											pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
											pst.execute();
											pst.close();
											break;
										
									case 5:
											pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
														+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=5 "
														+ " and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' and ud.emp_id=epd.emp_per_id "
														+ " and eod.emp_id=epd.emp_per_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true limit 1");
												
											pst.setInt(1, nEmpId);
											rs = pst.executeQuery();
												
											while (rs.next()) {
												empId = rs.getInt("emp_id");
												userTypeId = rs.getInt("usertype_id");
																			
											}
											rs.close();
											pst.close();
												
											pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
														"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
														"values(?,?,?,?, ?,?,?,?, ?,?)");
											pst.setInt(1,empId);
											pst.setInt(2,nOffBoardId);
											pst.setString(3,WORK_FLOW_RESIGN);
											pst.setInt(4,uF.parseToInt(innerList.get(0)));
											pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
											pst.setInt(6,uF.parseToInt(innerList.get(4)));
											pst.setInt(7,1);
											pst.setInt(8,1);
											pst.setInt(9,userTypeId);
											pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
											pst.execute();
											pst.close();
											break;
											
									case 6:
											pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud,"
														+ " employee_official_details eod,employee_personal_details epd where ud.usertype_id=6 "
														+ " and ud.emp_id=eod.emp_id and eod.wlocation_id=? and ud.emp_id=epd.emp_per_id "
														+ " and eod.emp_id=epd.emp_per_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true limit 1");
											pst.setInt(1, uF.parseToInt(locationID));
											pst.setInt(2, nEmpId);
											rs = pst.executeQuery();
												
											while (rs.next()) {
												empId = rs.getInt("emp_id");
												userTypeId = rs.getInt("usertype_id");
																			
											}
											rs.close();
											pst.close();
											pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
														"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
														"values(?,?,?,?, ?,?,?,?, ?,?)");
											pst.setInt(1,empId);
											pst.setInt(2,nOffBoardId);
											pst.setString(3,WORK_FLOW_RESIGN);
											pst.setInt(4,uF.parseToInt(innerList.get(0)));
											pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
											pst.setInt(6,uF.parseToInt(innerList.get(4)));
											pst.setInt(7,1);
											pst.setInt(8,1);
											pst.setInt(9,userTypeId);
											pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
											pst.execute();
											pst.close();
											break;
											
									case 7:
											pst = con.prepareStatement("select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud, employee_official_details eod," +
													"employee_personal_details epd where ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id " +
													"and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true and ud.emp_id in (select eod.emp_hr from employee_official_details eod," +
													"employee_personal_details epd where epd.emp_per_id=eod.emp_id and eod.emp_id=?)" +
													" union " +
													"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud, employee_official_details eod," +
													"employee_personal_details epd where ud.usertype_id=7 and ud.emp_id=eod.emp_id and ud.wlocation_id_access like '%,"+locationID+",%' " +
													"and ud.emp_id=epd.emp_per_id and epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true " +
													" union " +
													"select ud.emp_id,ud.usertype_id,epd.emp_fname,epd.emp_lname from user_details ud, employee_official_details eod," +
													"employee_personal_details epd where ud.usertype_id=1 and ud.emp_id=eod.emp_id and ud.emp_id=epd.emp_per_id and " +
													"epd.emp_per_id=eod.emp_id and ud.status='ACTIVE' and ud.emp_id not in(?) and epd.is_alive=true limit 1");
											pst.setInt(1, nEmpId);
											pst.setInt(2, nEmpId);
											pst.setInt(3, nEmpId);
											pst.setInt(4, nEmpId);
											rs = pst.executeQuery();
											while (rs.next()) {
												empId = rs.getInt("emp_id");
												userTypeId = rs.getInt("usertype_id");
																			
											}
											rs.close();
											pst.close();
												
											pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
														"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
														"values(?,?,?,?, ?,?,?,?, ?,?)");
											pst.setInt(1,empId);
											pst.setInt(2,nOffBoardId);
											pst.setString(3,WORK_FLOW_RESIGN);
											pst.setInt(4,uF.parseToInt(innerList.get(0)));
											pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
											pst.setInt(6,uF.parseToInt(innerList.get(4)));
											pst.setInt(7,1);
											pst.setInt(8,1);
											pst.setInt(9,userTypeId);
											pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
											pst.execute();
											pst.close();
											break;		
											
									case 13:
											pst = con.prepareStatement("select * from (select distinct(hod_emp_id) as hod_emp_id from employee_official_details where " +
													"emp_id=? and hod_emp_id!=0) as a,employee_personal_details epd,user_details ud where a.hod_emp_id=epd.emp_per_id " +
													"and ud.emp_id=epd.emp_per_id  and ud.status='ACTIVE' and epd.is_alive=true order by epd.emp_fname limit 1");
										pst.setInt(1, nEmpId);
										rs = pst.executeQuery();
											
										while (rs.next()) {
											empId = rs.getInt("emp_id");
											userTypeId = rs.getInt("usertype_id");
																			
										}
										rs.close();
										pst.close();
											
										pst=con.prepareStatement("insert into work_flow_details(emp_id,effective_id,effective_type,member_type,member_position," +
													"work_flow_mem_id,is_approved,status,user_type_id,approve_date)" +
													"values(?,?,?,?, ?,?,?,?, ?,?)");
										pst.setInt(1,empId);
										pst.setInt(2,nOffBoardId);
										pst.setString(3,WORK_FLOW_RESIGN);
										pst.setInt(4,uF.parseToInt(innerList.get(0)));
										pst.setInt(5,(int)uF.parseToDouble(innerList.get(2)));
										pst.setInt(6,uF.parseToInt(innerList.get(4)));
										pst.setInt(7,1);
										pst.setInt(8,1);
										pst.setInt(9,userTypeId);
										pst.setDate(10,uF.getCurrentDate(CF.getStrTimeZone()));
										pst.execute();
										pst.close();
										break;
										
								}
									
							}
						}
					}
						
				}
					sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
			}
			
		 
			
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
	//===end===
	

	private void processProbation(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
	
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			String joiningDate = null;
			pst = con.prepareStatement(" select emp_per_id,joining_date from employee_personal_details where emp_per_id = ?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			
			while (rs.next()) {
				joiningDate = rs.getString("joining_date");
				
			}
			rs.close();
			pst.close();
			
			Date lastProbationEndDate = null;
			if(joiningDate != null && !joiningDate.equals("")) {
				lastProbationEndDate = uF.getFutureDate(uF.getDateFormat(joiningDate, DBDATE),uF.parseToInt(getStrProbationPeriod()));
			
			
				Date currentProbationEndDate = uF.getFutureDate(lastProbationEndDate != null ? lastProbationEndDate : uF.getDateFormat(getEffectiveDate(), DATE_FORMAT) , uF.parseToInt(getStrExtendProbationDays())-1);
			//	Date currentProbationEndDate = uF.getFutureDate(uF.getDateFormat(joiningDate, DBDATE) , uF.parseToInt(getStrProbationPeriod())-1);
								
				pst = con.prepareStatement("update probation_policy set is_probation = true, probation_duration = ?, probation_end_date = ?,extend_probation_duration=?  where emp_id=?");
				pst.setInt(1, uF.parseToInt(getStrProbationPeriod()));
				pst.setDate(2, currentProbationEndDate);
				pst.setInt(3,0);
				pst.setInt(4, nEmpId);
				
				int x = pst.executeUpdate();
				pst.close();
				boolean flag = false;
				if(x==0) {
					pst = con.prepareStatement("insert into probation_policy (is_probation, probation_duration, probation_end_date, emp_id) values (?,?,?,?)");
					pst.setBoolean(1, true);
					pst.setInt(2, uF.parseToInt(getStrProbationPeriod()));
					pst.setDate(3, currentProbationEndDate);
					pst.setInt(4, nEmpId);
					pst.execute();
					pst.close();
					
					flag = true;
				} else {
					flag = true;
				}
				
				if(flag){
					
					pst = con.prepareStatement("update employee_personal_details set emp_status =?  where emp_per_id=?");
					pst.setString(1, PROBATION);
					pst.setInt(2, nEmpId);
					pst.executeUpdate();
					pst.close();
				}
				
				sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
			}
			
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
	
	private void processNoticePeriod(Connection con, int activityType,int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
	
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			pst = con.prepareStatement("update probation_policy set notice_duration = ? where emp_id=?");
			pst.setInt(1, uF.parseToInt(getStrNoticePeriod()));
			pst.setInt(2, nEmpId);
			int x = pst.executeUpdate();
			pst.close();
			
			if(x==0) {
				pst = con.prepareStatement("insert into probation_policy (notice_duration, emp_id) values (?,?)");
				pst.setInt(1, uF.parseToInt(getStrNoticePeriod()));
				pst.setInt(2, nEmpId);
				pst.execute();
				pst.close();
			}
			
			String resignationDate = null;
			pst = con.prepareStatement("select * from emp_off_board where emp_id = ?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			while(rs.next()) {
				resignationDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT);
			}
			rs.close();
			pst.close();
			
			
			
			if(resignationDate != null && !resignationDate.equals("")) {
				java.sql.Date lastDate = uF.getFutureDate(resignationDate, uF.parseToInt(getStrNoticePeriod()));
				
				pst = con.prepareStatement("update emp_off_board set notice_days = ?, last_day_date = ? where emp_id = ?");
				pst.setInt(1, uF.parseToInt(getStrNoticePeriod()));
				pst.setDate(2,lastDate);
				pst.setInt(3, nEmpId);
	//			System.out.println("pst==>"+pst);
				pst.executeUpdate();
				pst.close();
			}
			
			
			sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
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
	
	private void processExtendProbation(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
	
		PreparedStatement pst = null;
		ResultSet rst = null;
		
		try {
			String effectiveDate = null;
			int probationPeriod = 0;
			String joiningDate = null;
			pst = con.prepareStatement("select * from employee_personal_details epd,probation_policy p where epd.emp_per_id = p.emp_id and emp_id=?");
			pst.setInt(1, nEmpId);
			rst = pst.executeQuery();
			
			while (rst.next()) {
				probationPeriod = rst.getInt("probation_duration");
				joiningDate = rst.getString("joining_date");
			}
			rst.close();
			pst.close();
						
			Date lastProbationEndDate = null;
			if(joiningDate != null && !joiningDate.equals("")) {
				lastProbationEndDate = uF.getFutureDate(uF.getDateFormat(joiningDate, DBDATE),(probationPeriod));
		        Date currentProbationEndDate = uF.getFutureDate(lastProbationEndDate != null ? lastProbationEndDate : uF.getDateFormat(getEffectiveDate(), DATE_FORMAT) , uF.parseToInt(getStrExtendProbationDays())-1);
				pst = con.prepareStatement("update probation_policy set is_probation = true, extend_probation_duration = ?, probation_end_date = ? where emp_id=?");
				pst.setInt(1, uF.parseToInt(getStrExtendProbationDays()));
				pst.setDate(2, currentProbationEndDate);
				pst.setInt(3, nEmpId);
	
				int x = pst.executeUpdate();
				pst.close();
				boolean flag = false;
				if(x==0) {
					pst = con.prepareStatement("insert into probation_policy (is_probation, extend_probation_duration, probation_end_date, emp_id) values (?,?,?,?)");
					pst.setBoolean(1, true);
					pst.setInt(2, uF.parseToInt(getStrExtendProbationDays()));
					pst.setDate(3, currentProbationEndDate);
					pst.setInt(4, nEmpId);
	
					pst.execute();
					pst.close();
					
					flag = true;
				}else{
					flag = true;
				}
				
				if(flag){
					pst = con.prepareStatement("update employee_personal_details set emp_status =?  where emp_per_id=?");
					pst.setString(1, PROBATION);
					pst.setInt(2, nEmpId);
					pst.executeUpdate();
					pst.close();
				}
				
				sendAttachDocument(con, activityType, nEmpId, strDate, CF, uF, strActivityName, nEmpActivityId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
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
	
	private void sendAttachDocument(Connection con, int activityType, int nEmpId, String strDate, CommonFunctions CF, UtilityFunctions uF, String strActivityName,int nEmpActivityId) {
	
	
		ResultSet rst = null;
		PreparedStatement pst = null;
	
		try {
			Map<String, Map<String, String>> hmEmpInfo = CF.getEmpInfoMap(con, false);
			Map<String, String> hmEmpInner = hmEmpInfo.get(""+nEmpId);
			
			StringBuilder sbEmpSalTable = CF.getEmployeeSalaryDetails(con, CF, uF, ""+nEmpId, request, session);
			if(sbEmpSalTable == null) sbEmpSalTable = new StringBuilder();
			
			String empOrgId = CF.getEmpOrgId(con, uF, ""+nEmpId);
			
			Map<String, Map<String, String>> hmHeader=new HashMap<String, Map<String, String>>();
			Map<String, Map<String, String>> hmFooter=new HashMap<String, Map<String, String>>();
			pst = con.prepareStatement("select * from document_collateral");
			rst = pst.executeQuery();
	//		System.out.println("new Date ===> " + new Date());
			while(rst.next()){
				if(rst.getString("_type").equals("H")){
					Map<String, String> hmInner=new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rst.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rst.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rst.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rst.getString("collateral_text"),""));
					
					hmHeader.put(rst.getString("collateral_id"), hmInner);
				}else{
					Map<String, String> hmInner=new HashMap<String, String>();
					hmInner.put("COLLATERAL_ID", rst.getString("collateral_id"));
					hmInner.put("COLLATERAL_PATH", rst.getString("collateral_image"));
					hmInner.put("COLLATERAL_IMG_ALIGN", rst.getString("image_align"));
					hmInner.put("COLLATERAL_TEXT", uF.showData(rst.getString("collateral_text"),""));
					
					hmFooter.put(rst.getString("collateral_id"), hmInner);
				}
			}
			rst.close();
			pst.close();
			
			pst=con.prepareStatement("select * from nodes");
			rst = pst.executeQuery();
			Map<String, String> hmMapActivityNode = new HashMap<String, String>();
			while(rst.next()){
				hmMapActivityNode.put(rst.getString("mapped_activity_id"), rst.getString("node_id"));
			}
			rst.close();
			pst.close();
			int nTriggerNode = uF.parseToInt(hmMapActivityNode.get(""+activityType));
			
			String strDocumentName = null;
			String strDocumentContent = null;
			String strDocumentHeader = null;
			String strDocumentFooter = null;
			String strHeader = null;
			String strFooter = null;
			String strHeaderImageAlign="";
			String strHeaderCollateralText="";
			String strHeaderTextAlign="";
			String strFooterImageAlign="";
			String strFooterCollateralText="";
			String strFooterTextAlign="";
			
			if(nTriggerNode > 0){
				pst = con.prepareStatement("select * from document_comm_details where trigger_nodes like '%,"+nTriggerNode+",%' and status=1 and org_id=? order by document_id desc limit 1");
				pst.setInt(1, uF.parseToInt(empOrgId));
				rst = pst.executeQuery();
		//		System.out.println("new Date ===> " + new Date());
				while (rst.next()) {  
					strDocumentName = rst.getString("document_name");
					strDocumentContent = rst.getString("document_text");
					
					if(rst.getString("collateral_header")!=null && !rst.getString("collateral_header").equals("") && hmHeader.get(rst.getString("collateral_header"))!=null){
						Map<String, String> hmInner=hmHeader.get(rst.getString("collateral_header"));
						strHeader = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
						strHeaderImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
						strHeaderCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
						strHeaderTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
					}
					if(rst.getString("collateral_footer")!=null && !rst.getString("collateral_footer").equals("") && hmFooter.get(rst.getString("collateral_footer"))!=null){
						Map<String, String> hmInner=hmFooter.get(rst.getString("collateral_footer"));
						strFooter = uF.showData(hmInner.get("COLLATERAL_PATH"),"");
						strFooterImageAlign=uF.showData(hmInner.get("COLLATERAL_IMG_ALIGN"),"");
						strFooterCollateralText=uF.showData(hmInner.get("COLLATERAL_TEXT"),"");
						strFooterTextAlign=uF.showData(hmInner.get("COLLATERAL_TEXT_ALIGN"),"");
					}
				}
				rst.close();
				pst.close();
			}
			if(strDocumentName!=null){
	//			strDocumentName = strDocumentName.replace(" ", "");
				strDocumentName = strDocumentName!=null ? strDocumentName.trim() : "";
			}
			
			String strDomain = request.getServerName().split("\\.")[0];
			
			Notifications nF = new Notifications(N_NEW_ACTIVITY, CF);
			nF.setDomain(strDomain);
			nF.request = request;
			nF.session = session;
			nF.setStrEmpId(""+nEmpId);  
			nF.setStrHostAddress(CF.getStrEmailLocalHost());
			nF.setStrHostPort(CF.getStrHostPort());
	//		nF.setStrContextPath(request.getServletPath());
			nF.setStrContextPath(request.getContextPath());
			 
			nF.setStrEmpFname(hmEmpInner.get("FNAME"));
			nF.setStrEmpLname(hmEmpInner.get("LNAME"));
			nF.setStrSalaryStructure(sbEmpSalTable.toString());
			nF.setStrActivityName(strActivityName);
			
			if(getEffectiveDate() != null && !getEffectiveDate().equals("")) {
				nF.setStrEffectiveDate(uF.getDateFormat(getEffectiveDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
				nF.setStrPromotionDate(uF.getDateFormat(getEffectiveDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
			}
			nF.setStrIncrementPercent(""+uF.parseToDouble(getStrIncrementPercentage()));
			nF.setStrSalaryStructure(sbEmpSalTable.toString());
			
			Map<String, String> hmParsedContent = null;
	
	//		Document document = new Document(PageSize.A4);
			Document document = new Document(PageSize.A4,40, 40, 10, 60); 
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			StringBuilder sbHeader = new StringBuilder();
			StringBuilder sbFooter = new StringBuilder();
			String strDocName = null;
			String strDocContent = null;
			if(strDocumentContent!=null){
				
	//			hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				hmParsedContent  = nF.parseContent(strDocumentContent, "", "");
				
				
				strDocName = strDocumentName;
				strDocContent = hmParsedContent.get("MAIL_BODY");
				String strDocument = hmParsedContent.get("MAIL_BODY");
				if(strDocument!=null) {
	//				strDocument = strDocument.replaceAll("<br/>", "");
					
					//Satrt Dattatray Date : 31-07-21  
					if (strDocument.contains("<pre style=\"text-align:justify\">") || strDocument.contains("<pre style=\"text-align:justify;\">") || strDocument.contains("<pre style=\"text-align: justify;\">") || strDocument.contains("<pre style=\"text-align: justify\">")) {
	//					System.out.println("if");
						if (strDocument.contains("<pre ")) {
							strDocument = strDocument.replaceAll("<pre ", "<p ");
						}
						 if(strDocument.contains("<pre>") ){
							 strDocument = strDocument.replaceAll("<pre>", "<p>");
						 }
						//
						/*if (strDocument.contains("><span")) {
							strDocument = strDocument.replaceAll("><span ", "><p style=\"text-align: justify\"><span ");
						}
						if (strDocument.contains("</span>")) {
							strDocument = strDocument.replaceAll("</span>", "</span></p>");
						}*/
	
						if (strDocument.contains("</pre>")) {
							strDocument = strDocument.replaceAll("</pre>", "</p>");
						}
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p>", "<br/><p style=\"text-align: justify\">", true, true, "<p>");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/><p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<br/><p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: center;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: center\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align:center\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:center\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: right;\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right;\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align: right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align: right\">");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	<p style=\"text-align:right\">", "<br/><p style=\"text-align: justify\">", true, true, "<br/><p style=\"text-align:right\">");
						
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<li>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
	//					strDocument = replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>", "<p style=\"text-align: justify\">", true, true, "<p style=\"text-align: justify\">");
	//					strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
	//					strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</p>", true, true, "<p>");
	//					strDocument = replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
					}else {
						System.out.println("Else");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<li>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<p>", true, true, "<p>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", " </li>	", true, true, "</li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<li>", "<br/>", true, true, "<li>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "<pre>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<pre>", "<br/>", true, true, "<pre>");
						strDocument = uF.replaceBetweenTwoString(strDocument, "<br/>	", "</li>", true, true, "</li>");
					}
					//End Dattatray Date : 31-07-21 
				}
				
				String headerPath="";
				if(strHeader!=null && !strHeader.equals("")){
	//				headerPath=CF.getStrDocRetriveLocation()+strHeader;
					if(CF.getStrDocRetriveLocation()==null) { 
						headerPath =  DOCUMENT_LOCATION + strHeader;
					} else { 
						headerPath = CF.getStrDocSaveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strHeader;
					}
				}
				
				
	//			if(headerPath != null && !headerPath.equals("")) {
	//				//sbHeader.append("<table><tr><td><img height=\"60\" src=\""+strDocumentHeader+"\"></td></tr></table>");
	//				sbHeader.append("<table style=\"width: 100%;\"><tr>");
	//				if(strHeaderImageAlign!=null && strHeaderImageAlign.equals("R")) { 
	//					sbHeader.append("<td width=\"70%\" valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>" +
	//							"<td align=\"right\">");
	//					if(headerPath != null && !headerPath.equals("")) {
	//						sbHeader.append("<img height=\"30\" src=\""+headerPath+"\">");
	//					}
	//					sbHeader.append("</td>");						
	//				
	//				} else if(strHeaderImageAlign !=null && strHeaderImageAlign.equals("C")) { 
	//					sbHeader.append("<td colspan=\"2\" align=\"Center\">");
	//					if(headerPath != null && !headerPath.equals("")) {
	//						sbHeader.append("<img height=\"30\" src=\""+headerPath+"\"><br/>");
	//					}
	//					sbHeader.append(""+strHeaderCollateralText+"</td>");
	//				} else {
	//					sbHeader.append("<td>");
	//					if(headerPath != null && !headerPath.equals("")) {
	//						sbHeader.append("<img height=\"30\" src=\""+headerPath+"\">");
	//					}
	//					sbHeader.append("</td> <td valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>");
	//				}
	//				sbHeader.append("</tr></table>");
	//				
	//			} else {
	//				
	//				sbHeader.append("<table style=\"width: 100%;\"><tr>");
	//				if(strHeaderTextAlign!=null && strHeaderTextAlign.equals("R")) { 
	//					sbHeader.append("<td colspan=\"2\" align=\"right\" valign=\"middle\" style=\"padding-right: 50px;\">"+strHeaderCollateralText+"</td>");
	//					
	//				} else if(strHeaderTextAlign!=null && strHeaderTextAlign.equals("C")) { 
	//					sbHeader.append("<td colspan=\"2\" align=\"center\" valign=\"middle\">"+strHeaderCollateralText+"</td>");
	//				} else { 
	//					sbHeader.append("<td colspan=\"2\" valign=\"middle\" style=\"padding-left: 50px;\">"+strHeaderCollateralText+"</td>");
	//				}
	//				sbHeader.append("</tr></table>");
	//			
	//			}
				
				if(headerPath != null && !headerPath.equals("")) {
					sbHeader.append("<table style=\"width: 100%;\"><tr>");
					sbHeader.append("<td>");
					if(strHeader!=null && !strHeader.equals("")) {
						sbHeader.append("<img src=\""+headerPath+"\">");
					}
					sbHeader.append("</td>");	
					sbHeader.append("</tr></table>");
				}
				
				
	//			String footerPath="";   
	//			if(strFooter!=null && !strFooter.equals("")){
	////				footerPath=CF.getStrDocRetriveLocation()+strFooter;
	//				if(CF.getStrDocRetriveLocation()==null) { 
	//					footerPath =  DOCUMENT_LOCATION + strFooter;
	//				} else { 
	//					footerPath = CF.getStrDocRetriveLocation() +I_COLLATERAL+"/"+I_IMAGE+"/"+strFooter;
	//				}
	//			}
	//			
	//			if(footerPath != null && !footerPath.equals("")) {
	//				sbFooter.append("<table><tr>");
	//				if(strFooterImageAlign!=null && strFooterImageAlign.equals("R")) { 
	//					sbFooter.append("<td width=\"70%\" valign=\"middle\" style=\"padding-left: 50px;\">"+strFooterCollateralText+"</td> <td align=\"right\">");
	//					if(footerPath != null && !footerPath.equals("")) {
	//						sbFooter.append("<img height=\"60\" src=\""+footerPath+"\">");
	//					}
	//					sbFooter.append("</td>");						
	//				
	//				} else if(strFooterImageAlign!=null && strFooterImageAlign.equals("C")) { 
	//					sbFooter.append("<td align=\"Center\">");
	//					if(footerPath != null && !footerPath.equals("")) {
	//						sbFooter.append("<img height=\"60\" src=\""+footerPath+"\"><br/>");
	//					}
	//					sbFooter.append(""+strFooterCollateralText+"</td>");
	//				} else { 
	//					sbFooter.append("<td>");
	//					if(footerPath != null && !footerPath.equals("")) {
	//						sbFooter.append("<img height=\"60\" src=\""+footerPath+"\">");
	//					}
	//					sbFooter.append("</td> <td valign=\"middle\" style=\"padding-left: 50px;\">"+strFooterCollateralText+"</td>");
	//				}
	//				sbFooter.append("</tr></table>");
	//			} else {
	//
	//				sbFooter.append("<table><tr>");
	//				if(strFooterTextAlign!=null && strFooterTextAlign.equals("R")) { 
	//					sbFooter.append("<td colspan=\"2\" align=\"right\" valign=\"middle\" style=\"padding-right: 50px;\">"+strFooterCollateralText+"</td>");
	//				
	//				} else if(strFooterTextAlign!=null && strFooterTextAlign.equals("C")) { 
	//					sbFooter.append("<td colspan=\"2\" align=\"center\" valign=\"middle\">"+strFooterCollateralText+"</td>");
	//				} else { 
	//					sbFooter.append("<td colspan=\"2\" valign=\"middle\" style=\"padding-right: 50px;\">"+strFooterCollateralText+"</td>");
	//				}
	//				sbFooter.append("</tr></table>");
	//			
	//			}
				
				PdfWriter writer = PdfWriter.getInstance(document, buffer);
				HeaderFooterPageEvent event = new HeaderFooterPageEvent(sbHeader.toString(),strFooterCollateralText);
			    writer.setPageEvent(event);
				
				document.open();
				
	//			System.out.println("strDocument ====> " +strDocument);
				HTMLWorker hw = new HTMLWorker(document);
	//			hw.parse(new StringReader(sbHeader.toString())); 
				hw.parse(new StringReader(strDocument));
	//			hw.parse(new StringReader(sbFooter.toString()));
				document.close();  
			
			}
			
			byte[] bytes = buffer.toByteArray();			
			if(strDocumentContent!=null && !strDocumentContent.trim().equals("") && !strDocumentContent.trim().equalsIgnoreCase("NULL") 
					&& getStrUpdate() == null && getStrUpdateDocument()!=null && !getStrUpdateDocument().trim().equals("") && !getStrUpdateDocument().trim().equalsIgnoreCase("NULL")) {
				
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName(strDocumentName+".pdf");
			}
			
			/*if(strDocumentContent!=null && getStrUpdateDocument()!=null) {
				nF.setPdfData(bytes);
				nF.setStrAttachmentFileName(strDocumentName+".pdf");
			}*/
			String strMailSubject = nF.getStrEmailSubject();
			String strMailBody = nF.getStrNewEmailBody();
	
			nF.setEmailTemplate(true);
			nF.sendNotifications();
			
	//		if(strDocumentContent!=null && getStrUpdateDocument()!=null) {
				saveDocumentActivity(con, uF, CF,nEmpId, strDocName, sbHeader.toString(), strDocContent, strFooterCollateralText, strMailSubject, strMailBody, nEmpActivityId);
	//		}
			 
		} catch (Exception e) {  
			e.printStackTrace();
		} finally {
			if(rst !=null){
				try {
					rst.close();
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
	
	private void saveDocumentActivity(Connection con, UtilityFunctions uF,CommonFunctions CF, int nEmpId, String strDocumentName, String strDocumentHeader, String strDocumentContent, String strDocumentFooter, String strMailSubject, String strMailBody,int nEmpActivityId){
		
		PreparedStatement pst = null;
		
		try {
		
			pst = con.prepareStatement("insert into document_activities (document_name, document_content, effective_date, entry_date, user_id, emp_id, " +
					"mail_subject, mail_body, document_header, document_footer,emp_activity_id) values (?,?,?,?, ?,?,?,?, ?,?,?)");
			pst.setString(1, strDocumentName);
			pst.setString(2, strDocumentContent);
			pst.setDate(3, uF.getDateFormat(getEffectiveDate(), DATE_FORMAT));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt((String)session.getAttribute(EMPID)));
			pst.setInt(6, nEmpId);
			pst.setString(7, strMailSubject);
			pst.setString(8, strMailBody);
			pst.setString(9, strDocumentHeader);
			pst.setString(10, strDocumentFooter);
			pst.setInt(11, nEmpActivityId);
			pst.execute();
			pst.close();
			
		} catch (Exception e) {
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
	

	private void getEmployeeList(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbEmp = null;
			
			if(getStrEmpIds()!=null && !getStrEmpIds().trim().equals("") && !getStrEmpIds().trim().equalsIgnoreCase("NULL")){
				sbEmp = new StringBuilder();
				sbEmp.append(getStrEmpIds());
			} else if(strIsAssigActivity!=null){
				for(int i = 0; i<strIsAssigActivity.size(); i++){
					if(sbEmp == null){
						sbEmp = new StringBuilder();
						sbEmp.append(","+strIsAssigActivity.get(i)+",");
					} else {
						sbEmp.append(strIsAssigActivity.get(i)+",");
					}
				}
			}

			if(getSubmit()!=null){
				for(int i = 0; getStrSelectedEmpId()!=null && i<getStrSelectedEmpId().length; i++){
					if(sbEmp == null){
						sbEmp = new StringBuilder();
						sbEmp.append(","+getStrSelectedEmpId()[i]+",");
					} else {
						sbEmp.append(getStrSelectedEmpId()[i]+",");
					}
				}
			}
			
				
			if(sbEmp !=null){
				String strDataType = null;
				if(getDataType() != null) {
					strDataType= getDataType();
				}else{
					strDataType= "A";
				}
				String strEmptemp = sbEmp.substring(1, sbEmp.length()-1);
				
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
						"and eod.emp_id in ("+strEmptemp+") ");
				if(uF.parseToInt(getRemEmpId()) > 0){
					sbQuery.append("and eod.emp_id !="+uF.parseToInt(getRemEmpId()));
				}
				sbQuery.append(" order by epd.emp_fname, epd.emp_lname");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst=======>"+pst);
				rs = pst.executeQuery();
//				Map<String, String> hmEmpMap = new LinkedHashMap<String, String>();
				sbEmp = null;
				while(rs.next()){
					if(sbEmp == null){
						sbEmp = new StringBuilder();
						sbEmp.append(","+rs.getString("emp_id")+",");
					} else {
						sbEmp.append(rs.getString("emp_id")+",");
					}
				}
				rs.close();
				pst.close();
				
//				System.out.println("sbEmp ==>"+sbEmp.toString());
				if(sbEmp != null){
					String strEmptp = sbEmp.substring(1, sbEmp.length()-1);
					
					sbQuery = new StringBuilder();
					sbQuery.append("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id " +
							"and eod.emp_id in ("+strEmptp+") ");	
					sbQuery.append(" order by epd.emp_fname, epd.emp_lname");
					pst = con.prepareStatement(sbQuery.toString());
//					System.out.println("pst1=======>"+pst);
					rs = pst.executeQuery();
	//				Map<String, String> hmEmpMap = new LinkedHashMap<String, String>();
					StringBuilder sbEmpList = null;
					while(rs.next()){
						
						String strEmpMName1 = "";
						if(flagMiddleName) {
							if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
								strEmpMName1 = " "+rs.getString("emp_mname");
							}
						}
					
						
	//					hmEmpMap.put(rs.getString("emp_id"), uF.showData(rs.getString("emp_fname"), "")+" "+uF.showData(rs.getString("emp_lname"), ""));
					
						
						
						String strEmpName = uF.showData(rs.getString("emp_fname"), "")+strEmpMName1+" "+uF.showData(rs.getString("emp_lname"), "") +" ["+uF.showData(rs.getString("empcode"), "")+"]";
						String removePath = "<a href=\"javascript:void(0);\" onclick=\"if(confirm('Are you sure, do you want to remove "+strEmpName+"?')) window.location='EmployeeBulkActivity.action?dataType="+strDataType+"&strEmpIds="+URLEncoder.encode(sbEmp.toString())+"&remEmpId="+rs.getString("emp_id")+"';\">" +
								"<img title=\"Remove\" src=\"images1/icons/hd_cross_16x16.png\" border=\"0\" /></a> ";
						if(sbEmpList == null){
							sbEmpList = new StringBuilder();
							sbEmpList.append(strEmpName+" "+removePath);
						} else {
							sbEmpList.append(" , "+strEmpName+" "+removePath);
						}
					}
					rs.close();
					pst.close();
					
	//				request.setAttribute("hmEmpMap", hmEmpMap);
					request.setAttribute("sbEmpList", sbEmpList.toString());
					request.setAttribute("sbEmp", sbEmp.toString());
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

	public String loadValidateEmpActivity(UtilityFunctions uF) {
		
//		System.out.println("EBA/2752---getDataType=="+getDataType());
		if(getDataType() == null || getDataType().equals("A")) {
			activityList = new FillActivity(request).fillActivityByNode(true, false);
		} else if(getDataType() == null || getDataType().equals("D")) {
			activityList = new FillActivity(request).fillActivityByNode(true, true);
		}
		
		
		orgList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
				
		empList=getEmpList(uF);
		getSelectedFilter(uF);
		
		/*organisationList1 = new FillOrganisation(request).fillOrganisation();
		wLocationList1 = new FillWLocation(request).fillWLocation(null);
		serviceList1 = new FillServices(request).fillServices(null, uF);
		departmentList1 = new FillDepartment(request).fillDepartment(0);
		levelList1 = new FillLevel(request).fillLevel(0);*/
		organisationList1 = new FillOrganisation(request).fillOrganisation();
		wLocationList1 = new FillWLocation(request).fillWLocation(getF_org());
		empTypeList = new FillEmploymentType().fillEmploymentType(request);
		serviceList1 = new FillServices(request).fillServices(getF_org(), uF);
		departmentList1 = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList1 = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		desigList = new ArrayList<FillDesig>();
		gradeChangeList =  new ArrayList<FillGrade>();
		
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					if(k==0) {
						strOrg=orgList.get(i).getOrgName();
					} else {
						strOrg+=", "+orgList.get(i).getOrgName();
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
				hmFilter.put("LEVEL", "All Level's");
			}
		} else {
			hmFilter.put("LEVEL", "All Level's");
		}
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	private List<FillEmployee> getEmpList(UtilityFunctions uF) {
		
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);

		try {

			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id ");
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
            if(getF_department()!=null && getF_department().length>0){
                sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
            }
            if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
            if(getF_service()!=null && getF_service().length>0){
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++){
                    sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
                
            }
			
			sbQuery.append(" order by epd.emp_fname");
			
			
			pst = con.prepareStatement(sbQuery.toString());
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	HttpServletResponse response;
	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	

	public List<FillActivity> getActivityList() {
		return activityList;
	}

	public void setActivityList(List<FillActivity> activityList) {
		this.activityList = activityList;
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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public List<String> getStrIsAssigActivity() {
		return strIsAssigActivity;
	}

	public void setStrIsAssigActivity(List<String> strIsAssigActivity) {
		this.strIsAssigActivity = strIsAssigActivity;
	}

	public String getStrEmpIds() {
		return strEmpIds;
	}

	public void setStrEmpIds(String strEmpIds) {
		this.strEmpIds = strEmpIds;
	}

	public String getStrActivity() {
		return strActivity;
	}

	public void setStrActivity(String strActivity) {
		this.strActivity = strActivity;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public String getStrExtendProbationDays() {
		return strExtendProbationDays;
	}

	public void setStrExtendProbationDays(String strExtendProbationDays) {
		this.strExtendProbationDays = strExtendProbationDays;
	}

	public String getStrTransferType() {
		return strTransferType;
	}

	public void setStrTransferType(String strTransferType) {
		this.strTransferType = strTransferType;
	}

	public String getStrIncrementType() {
		return strIncrementType;
	}

	public void setStrIncrementType(String strIncrementType) {
		this.strIncrementType = strIncrementType;
	}

	public List<FillOrganisation> getOrganisationList1() {
		return organisationList1;
	}

	public void setOrganisationList1(List<FillOrganisation> organisationList1) {
		this.organisationList1 = organisationList1;
	}

	public String getStrOrganisation() {
		return strOrganisation;
	}

	public void setStrOrganisation(String strOrganisation) {
		this.strOrganisation = strOrganisation;
	}

	public String getStrWLocation() {
		return strWLocation;
	}

	public void setStrWLocation(String strWLocation) {
		this.strWLocation = strWLocation;
	}

	public List<FillWLocation> getwLocationList1() {
		return wLocationList1;
	}

	public void setwLocationList1(List<FillWLocation> wLocationList1) {
		this.wLocationList1 = wLocationList1;
	}

	public String getStrSBU() {
		return strSBU;
	}

	public void setStrSBU(String strSBU) {
		this.strSBU = strSBU;
	}

	public List<FillServices> getServiceList1() {
		return serviceList1;
	}

	public void setServiceList1(List<FillServices> serviceList1) {
		this.serviceList1 = serviceList1;
	}

	public String getStrDepartment() {
		return strDepartment;
	}

	public void setStrDepartment(String strDepartment) {
		this.strDepartment = strDepartment;
	}

	public List<FillDepartment> getDepartmentList1() {
		return departmentList1;
	}

	public void setDepartmentList1(List<FillDepartment> departmentList1) {
		this.departmentList1 = departmentList1;
	}

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
	}

	public List<FillLevel> getLevelList1() {
		return levelList1;
	}

	public void setLevelList1(List<FillLevel> levelList1) {
		this.levelList1 = levelList1;
	}

	public List<FillDesig> getDesigList() {
		return desigList;
	}

	public void setDesigList(List<FillDesig> desigList) {
		this.desigList = desigList;
	}

	public String getStrDesignation() {
		return strDesignation;
	}

	public void setStrDesignation(String strDesignation) {
		this.strDesignation = strDesignation;
	}

	public String getEmpGrade() {
		return empGrade;
	}

	public void setEmpGrade(String empGrade) {
		this.empGrade = empGrade;
	}

	public List<FillGrade> getGradeChangeList() {
		return gradeChangeList;
	}

	public void setGradeChangeList(List<FillGrade> gradeChangeList) {
		this.gradeChangeList = gradeChangeList;
	}

	public String getEmpChangeGrade() {
		return empChangeGrade;
	}

	public void setEmpChangeGrade(String empChangeGrade) {
		this.empChangeGrade = empChangeGrade;
	}

	public String getStrNoticePeriod() {
		return strNoticePeriod;
	}

	public void setStrNoticePeriod(String strNoticePeriod) {
		this.strNoticePeriod = strNoticePeriod;
	}

	public String getStrProbationPeriod() {
		return strProbationPeriod;
	}

	public void setStrProbationPeriod(String strProbationPeriod) {
		this.strProbationPeriod = strProbationPeriod;
	}

	public String getStrIncrementPercentage() {
		return strIncrementPercentage;
	}

	public void setStrIncrementPercentage(String strIncrementPercentage) {
		this.strIncrementPercentage = strIncrementPercentage;
	}

	public String getStrReason() {
		return strReason;
	}

	public void setStrReason(String strReason) {
		this.strReason = strReason;
	}

	public String getStrUpdate() {
		return strUpdate;
	}

	public void setStrUpdate(String strUpdate) {
		this.strUpdate = strUpdate;
	}

	public String getStrUpdateDocument() {
		return strUpdateDocument;
	}

	public void setStrUpdateDocument(String strUpdateDocument) {
		this.strUpdateDocument = strUpdateDocument;
	}

	public String getEmpIds() {
		return empIds;
	}

	public void setEmpIds(String empIds) {
		this.empIds = empIds;
	}

	public String getStrNewStatus() {
		return strNewStatus;
	}

	public void setStrNewStatus(String strNewStatus) {
		this.strNewStatus = strNewStatus;
	}

	public List<FillEmployee> getEmpList() {
		return empList;
	}

	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
	}

	public String getSubmit() {
		return Submit;
	}

	public void setSubmit(String submit) {
		Submit = submit;
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

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public List<FillOrganisation> getOrgList() {
		return orgList;
	}

	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public String[] getStrSelectedEmpId() {
		return strSelectedEmpId;
	}

	public void setStrSelectedEmpId(String[] strSelectedEmpId) {
		this.strSelectedEmpId = strSelectedEmpId;
	}

	public String getRemEmpId() {
		return remEmpId;
	}

	public void setRemEmpId(String remEmpId) {
		this.remEmpId = remEmpId;
	}

	public List<FillEmploymentType> getEmpTypeList() {
		return empTypeList;
	}

	public void setEmpTypeList(List<FillEmploymentType> empTypeList) {
		this.empTypeList = empTypeList;
	}

	public String getEmploymentType() {
		return employmentType;
	}

	public void setEmploymentType(String employmentType) {
		this.employmentType = employmentType;
	}
	
}
