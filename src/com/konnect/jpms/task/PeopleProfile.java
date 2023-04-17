package com.konnect.jpms.task;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.charts.SemiCircleMeter;
import com.konnect.jpms.employee.EmpDashboardData;
import com.konnect.jpms.payroll.ApprovePayroll;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UploadImage;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class PeopleProfile extends ActionSupport implements ServletRequestAware, IStatements {
 
	/** 
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType =  null;
	String strProductType =  null;
	CommonFunctions CF;
	private String empId; 
	String popup;
	String proPopup;
	
	String taskWorking;
	String proWorking; 
	
	private String empImageFileName;
	private File empImage;
	String submit;

	public String execute() throws Exception {
 
		
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)return LOGIN;

		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		strProductType = (String)session.getAttribute(PRODUCT_TYPE);
		
		request.setAttribute(PAGE, "/jsp/task/PeopleProfile.jsp");
//		request.setAttribute(TITLE, TViewProfile);
		
		
		request.setAttribute("arrEnabledModules", CF.getArrEnabledModules());
		request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
		request.setAttribute("IS_DEVICE_INTEGRATION", CF.getIsDeviceIntegration());
		UtilityFunctions uF = new UtilityFunctions();
		if(uF.parseToInt(strProductType) != 3) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		if(getTaskWorking() == null || uF.parseToInt(getTaskWorking().trim()) == 0) {
			setTaskWorking("1");
		}
		if(getProWorking() == null || uF.parseToInt(getProWorking().trim()) == 0) {
			setProWorking("1");
		}
		String strImgType = (String) request.getParameter("strImgType");
//		if(getSubmit()!=null && getSubmit().equals("Upload")){
		if(strImgType!=null && strImgType.equals("img")) {
			uploadEmpImages(uF);
			return "ajax";
		}
		
		if (getEmpId() != null && getEmpId().length() != 0) {
			viewProfile(uF,getEmpId());
			request.setAttribute("EMPID", getEmpId());
		} else {
			setEmpId((String) session.getAttribute(EMPID));
			viewProfile(uF,(String) session.getAttribute(EMPID));
		}

		return loadProfile();

	}
	
	
	private void uploadEmpImages(UtilityFunctions uF) {
		try {

			UploadImage uI = new UploadImage();
			uI.setServletRequest(request);
			uI.setImageType("EMPLOYEE_IMAGE");
			uI.setEmpImage(getEmpImage());
			uI.setEmpImageFileName(getEmpImageFileName());
			uI.setEmpId(getEmpId());
			uI.setCF(CF);
			uI.upoadImage();

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
	
	public String loadProfile() {
		if(popup!=null)
			return "popup";
		if(proPopup!=null)
			return "proPopup";
		return LOAD;
	}
	
	
	public void viewProfile(UtilityFunctions uF, String strEmpIdReq) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		List<List<String>> alEducation;
		try {

			con = db.makeConnection(con);
			
			EmpDashboardData dashboardData = new EmpDashboardData(request, session, CF, uF, con, strEmpIdReq);
			dashboardData.getPosition();
			
			Map<String, String> hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, strUserType, strEmpIdReq);
			
			CF.getElementList(con, request);
			CF.getAttributes(con, request, strEmpIdReq);
			CF.getEmpWorkedHours(con, request, uF, strEmpIdReq);
			
			List<List<String>> alSkills = new ArrayList<List<String>>();
			alSkills = CF.selectSkills(con, uF.parseToInt(strEmpIdReq));
			
			int intEmpIdReq = uF.parseToInt(strEmpIdReq);
			  
			alEducation = CF.selectEducation(con, intEmpIdReq);
			
			/**
			 * CTC
			 * */
//			MyProfile myProfile = new MyProfile();
//			myProfile.session = session;
//			myProfile.request = request;
//			myProfile.CF = CF;
//			myProfile.getSalaryHeadsforEmployee(con, uF, intEmpIdReq, hmEmpProfile);
//			double grossAmount = 0.0d;
//			double grossYearAmount = 0.0d;
//			double deductAmount = 0.0d;
//			double deductYearAmount = 0.0d;
//			double netAmount = 0.0d;
//			double netYearAmount = 0.0d;
//			
//			List<List<String>> salaryHeadDetailsList = (List<List<String>>) request.getAttribute("salaryHeadDetailsList");
//			for(int i=0; salaryHeadDetailsList != null && !salaryHeadDetailsList.isEmpty() && i<salaryHeadDetailsList.size(); i++) {
//				List<String> innerList = salaryHeadDetailsList.get(i);
//					if(innerList.get(1).equals("E")) {
//						grossAmount +=uF.parseToDouble(innerList.get(2));
//						grossYearAmount +=uF.parseToDouble(innerList.get(3));
//					} else if(innerList.get(1).equals("D")) {
//						deductAmount +=uF.parseToDouble(innerList.get(2));
//						deductYearAmount +=uF.parseToDouble(innerList.get(3));
//					}
//			}
//			
//			netAmount = grossAmount;
//			netAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(netAmount));
//			 
//			netYearAmount = grossYearAmount;
//			netYearAmount = uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(netYearAmount));
//			request.setAttribute("netAmount", netAmount);
//			request.setAttribute("netYearAmount", netYearAmount);
			
			
			
			String filePath = request.getRealPath("/userDocuments/");

			boolean isFilledStatus = CF.getEmpFilledStatus(con, getEmpId());
			
			getResourceTimeLine(con, uF, intEmpIdReq);
			getResourceWorkKPI(con, uF, intEmpIdReq);
			getResourceProjectKPI(con, uF, intEmpIdReq);
			
			getResourceMoneyWorkKPI(con, uF, intEmpIdReq);
			getResourceMoneyProjectKPI(con, uF, intEmpIdReq);

			getResourceTimeWorkKPI(con, uF, intEmpIdReq);
			getResourceTimeProjectKPI(con, uF, intEmpIdReq);
			
			getResourceRate(con, uF, intEmpIdReq);
			Map<String, String> hmInfoDisplay = CF.getProjectInformationDisplay(con);
			
			boolean isProjectOwner = checkProjectOwner(con, uF, intEmpIdReq);
			request.setAttribute("isProjectOwner", isProjectOwner);
			
			request.setAttribute("hmInfoDisplay", hmInfoDisplay);
			request.setAttribute("alSkills", alSkills);
			request.setAttribute("alEducation", alEducation);
			request.setAttribute("isFilledStatus", isFilledStatus+"");
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private boolean checkProjectOwner(Connection con, UtilityFunctions uF, int intEmpIdReq) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean flag = false;
		try {
		//===start parvez date: 13-10-2022===	
//			pst = con.prepareStatement("select * from projectmntnc where project_owner=? limit 1");
			pst = con.prepareStatement("select * from projectmntnc where project_owners like '%,"+intEmpIdReq+",%' limit 1");
//			pst.setInt(1, intEmpIdReq);
		//===end parvez date: 13-10-2022===	
			rs=pst.executeQuery();
			while(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from project_emp_details where emp_id=? and _isteamlead = true limit 1");
			pst.setInt(1, intEmpIdReq);
			rs=pst.executeQuery();
			while(rs.next()) {
				flag = true;
			}
			rs.close();
			pst.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	private void getResourceRate(Connection con, UtilityFunctions uF, int intEmpIdReq) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String strLevelId = CF.getEmpLevelId(con, ""+intEmpIdReq);
			String strwLocId = CF.getEmpWlocationId(con, uF, ""+intEmpIdReq);
			String strOrgId = CF.getEmpOrgId(con, uF, ""+intEmpIdReq);
			
			Map<String, Map<String, String>> hmCurrencyDetailsMap =  CF.getCurrencyDetails(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String,String>>();
			
			Map<String,String> hmEmpSkillRate = new HashMap<String,String>();
			StringBuilder sbskillRateQuery = new StringBuilder();
			sbskillRateQuery.append("SELECT * FROM level_skill_rates where wlocation_id=? and level_id=?");
			pst = con.prepareStatement(sbskillRateQuery.toString());
			pst.setInt(1, uF.parseToInt(strwLocId));
			pst.setInt(2, uF.parseToInt(strLevelId));
			rs=pst.executeQuery();
			while(rs.next()) {
				Map<String, String> hmCurr = hmCurrencyDetailsMap.get(rs.getString("curr_id"));
				if(hmCurr == null) hmCurr = new HashMap<String, String>();
				String strCurr = hmCurr.get("LONG_CURR")!=null && !hmCurr.get("LONG_CURR").equalsIgnoreCase("null") ? hmCurr.get("LONG_CURR")+" " : "";
				
				hmEmpSkillRate.put("DAILY_"+rs.getString("skill_id"), strCurr+uF.parseToDouble(rs.getString("rate_per_day")));
				hmEmpSkillRate.put("HOURLY_"+rs.getString("skill_id"), strCurr+uF.parseToDouble(rs.getString("rate_per_hour")));
				hmEmpSkillRate.put("MONTHLY_"+rs.getString("skill_id"), strCurr+uF.parseToDouble(rs.getString("rate_per_month")));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmEmpSkillRate", hmEmpSkillRate);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void getResourceTimeProjectKPI(Connection con, UtilityFunctions uF, int intEmpIdReq) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			String strProDate = null;
			if(uF.parseToInt(getProWorking()) == 1){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProWorking()) == 2){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 180), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProWorking()) == 3){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 90), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProWorking()) == 4){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
			} else {
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			}
			
			Map<String, String> hmPro = new HashMap<String, String>();
			List<String> alProjectId = new ArrayList<String>(); 
			
			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, null, null, uF);
			
			StringBuilder sbQuery = new StringBuilder();
	//===start parvez date: 13-10-2022===		
			/*sbQuery.append("select pro_id, pro_name, billing_type, billing_amount, idealtime, start_date, deadline, client_id, actual_calculation_type, " +
					"bill_days_type, hours_for_bill_day, added_by, curr_id from projectmntnc pmc where pmc.pro_id > 0  and pmc.approve_status != 'blocked'  " +
					"and project_owner=? and start_date > ?");*/
			sbQuery.append("select pro_id, pro_name, billing_type, billing_amount, idealtime, start_date, deadline, client_id, actual_calculation_type, " +
					"bill_days_type, hours_for_bill_day, added_by, curr_id from projectmntnc pmc where pmc.pro_id > 0  and pmc.approve_status != 'blocked'  " +
					"and project_owners like '%,"+intEmpIdReq+",%' and start_date > ?");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, intEmpIdReq);
			pst.setDate(1, uF.getDateFormat(strProDate, DATE_FORMAT));
	//===end parvez date: 13-10-2022===		
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			while (rs.next()){
				hmPro.put("PRO_ID_"+rs.getString("pro_id"), rs.getString("pro_id"));
				hmPro.put("ACTUAL_CAL_TYPE_"+rs.getString("pro_id"), rs.getString("actual_calculation_type"));
				hmPro.put("BILLING_TYPE_"+rs.getString("pro_id"), rs.getString("billing_type"));
				hmPro.put("BILLING_AMOUNT_"+rs.getString("pro_id"), rs.getString("billing_amount"));
				
				if(!alProjectId.contains(rs.getString("pro_id"))) {
					alProjectId.add(rs.getString("pro_id"));
				}
			}
			rs.close();
			pst.close();
			
			/**
			 * Project KPI Time
			 * */
			double dblActualTimeTotal = 0.0d;
			double dblBudgetedTimeTotal = 0.0d;
			
			for(int i = 0; alProjectId != null && i < alProjectId.size(); i++){
				
				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, (String)alProjectId.get(i));

				Map<String, String> hmProActualCostTime = new HashMap<String, String>();
				if("M".equalsIgnoreCase(hmPro.get("ACTUAL_CAL_TYPE_"+(String)alProjectId.get(i)))) { 
					hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con,request, CF, uF, (String)alProjectId.get(i), hmProjectData);
				} else {
					hmProActualCostTime = CF.getProjectActualCostAndTime(con,request, CF, uF, (String)alProjectId.get(i), hmProjectData, false, false);
				}
				Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, (String)alProjectId.get(i), hmProjectData);
				
				double dblBugedtedTime = uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedTime"));
				double dblActualTime = uF.parseToDouble(hmProActualCostTime.get("proActualTime"));
				 
				dblBudgetedTimeTotal += uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblBugedtedTime));
				dblActualTimeTotal += uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma((dblActualTime)));
			}
			
			
			double[] PRO_TIME_DATA  = new double[2];
			String[] PRO_TIME_LABEL  = new String[2];
			
			PRO_TIME_DATA[0] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblActualTimeTotal));
			PRO_TIME_DATA[1] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblBudgetedTimeTotal));
			
			request.setAttribute("PRO_TIME_KPI", new SemiCircleMeter().getSemiCircleChart(PRO_TIME_DATA, PRO_TIME_LABEL, "Time"));
			request.setAttribute("PRO_ACTUAL_TIME_KPI", uF.formatIntoZeroWithOutComma(dblActualTimeTotal));
			request.setAttribute("PRO_BUDGET_TIME_KPI", uF.formatIntoZeroWithOutComma(dblBudgetedTimeTotal));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void getResourceTimeWorkKPI(Connection con, UtilityFunctions uF, int intEmpIdReq) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			String strTaskDate = null;
			if(uF.parseToInt(getTaskWorking()) == 1){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getTaskWorking()) == 2){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 180), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getTaskWorking()) == 3){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 90), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getTaskWorking()) == 4){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
			} else {
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			}
			
			Map<String, String> hmTaskPro = new HashMap<String, String>();
			List<String> alTaskId = new ArrayList<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select a.*,pmc.idealtime from (select task_id,parent_task_id,pro_id,start_date from activity_info where resource_ids like '%,"+intEmpIdReq+",%' and task_id " +
					"not in (select parent_task_id from activity_info where resource_ids like '%,"+intEmpIdReq+",%' and parent_task_id is not null) and " +
					"task_accept_status = 1) a, projectmntnc pmc where pmc.pro_id=a.pro_id and (parent_task_id in (select task_id from activity_info " +
					"where resource_ids like '%,"+intEmpIdReq+",%') or parent_task_id = 0) and a.start_date > ?");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strTaskDate, DATE_FORMAT));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()){
				hmTaskPro.put("PRO_ID_"+rs.getString("task_id"), rs.getString("pro_id"));
				hmTaskPro.put("TASK_ID_"+rs.getString("task_id"), rs.getString("task_id"));
				hmTaskPro.put("IDEAL_TIME_"+rs.getString("task_id"), rs.getString("idealtime"));
				
				if(!alTaskId.contains(rs.getString("task_id"))) {
					alTaskId.add(rs.getString("task_id"));
				}
			}
			rs.close();
			pst.close();
			/**
			 * Work KPI Time
			 * */
			double dblActualTimeTotal = 0.0d;
			double dblBudgetedTimeTotal = 0.0d;
			for(int i = 0; alTaskId != null && i < alTaskId.size(); i++){
				String strProId =  hmTaskPro.get("PRO_ID_"+alTaskId.get(i));
				String strTaskId =  hmTaskPro.get("TASK_ID_"+alTaskId.get(i));
				
				pst = con.prepareStatement("SELECT * FROM projectmntnc where pro_id = ?");
				pst.setInt(1, uF.parseToInt(strProId));
				rs = pst.executeQuery();
				String strActualBilling = null;
				while(rs.next()) {
					strActualBilling = rs.getString("actual_calculation_type");
				}
				rs.close();
				pst.close();
				
				Map<String, String> empcostMp = CF.getProjectEmpActualRates(con, uF, strProId, strActualBilling);
				double dblEmpRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(empcostMp, ""+intEmpIdReq));
				double dblIdealTime = uF.parseToDouble(hmTaskPro.get("IDEAL_TIME_"+alTaskId.get(i)));
				
				double empBudgetedTime = 0;
				if(dblIdealTime > 0) {
					if(strActualBilling != null && strActualBilling.equals("M")) {
						empBudgetedTime = ((dblIdealTime *30) * 8);
					} else if(strActualBilling != null && strActualBilling.equals("D")) {
						empBudgetedTime = (dblIdealTime * 8);
					} else if(strActualBilling != null && strActualBilling.equals("H")) {
						empBudgetedTime = dblIdealTime;
					}
				}
				
				pst = con.prepareStatement("SELECT sum(actual_hrs) as actual_hrs FROM task_activity where activity_id = ? and emp_id=?");
				pst.setInt(1, uF.parseToInt(strTaskId));
				pst.setInt(2, intEmpIdReq);
				rs = pst.executeQuery();
				double empActualTime = 0;
				while(rs.next()) {
					empActualTime = uF.parseToDouble(rs.getString("actual_hrs"));
				}
				rs.close();
				pst.close();
				
				dblActualTimeTotal += empActualTime;
				dblBudgetedTimeTotal += empBudgetedTime;
			}
			
			
			double[] TASK_TIME_DATA  = new double[2];
			String[] TASK_TIME_LABEL  = new String[2];
			
			TASK_TIME_DATA[0] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblActualTimeTotal));
			TASK_TIME_DATA[1] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblBudgetedTimeTotal));
			
			request.setAttribute("TASK_TIME_KPI", new SemiCircleMeter().getSemiCircleChart(TASK_TIME_DATA, TASK_TIME_LABEL, "Time"));
			request.setAttribute("TASK_ACTUAL_TIME_KPI", uF.formatIntoZeroWithOutComma(dblActualTimeTotal));
			request.setAttribute("TASK_BUDGET_TIME_KPI", uF.formatIntoZeroWithOutComma(dblBudgetedTimeTotal));
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void getResourceMoneyWorkKPI(Connection con, UtilityFunctions uF, int intEmpIdReq) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			String strTaskDate = null;
			if(uF.parseToInt(getTaskWorking()) == 1){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getTaskWorking()) == 2){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 180), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getTaskWorking()) == 3){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 90), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getTaskWorking()) == 4){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
			} else {
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			}
			
			Map<String, String> hmTaskPro = new HashMap<String, String>();
			List<String> alTaskId = new ArrayList<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select a.*,pmc.idealtime from (select task_id,parent_task_id,pro_id,start_date from activity_info where resource_ids like '%,"+intEmpIdReq+",%' and task_id " +
					"not in (select parent_task_id from activity_info where resource_ids like '%,"+intEmpIdReq+",%' and parent_task_id is not null) and " +
					"task_accept_status = 1) a, projectmntnc pmc where pmc.pro_id=a.pro_id and (parent_task_id in (select task_id from activity_info " +
					"where resource_ids like '%,"+intEmpIdReq+",%') or parent_task_id = 0) and a.start_date > ? ");
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strTaskDate, DATE_FORMAT));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()){
				hmTaskPro.put("PRO_ID_"+rs.getString("task_id"), rs.getString("pro_id"));
				hmTaskPro.put("TASK_ID_"+rs.getString("task_id"), rs.getString("task_id"));
				hmTaskPro.put("IDEAL_TIME_"+rs.getString("task_id"), rs.getString("idealtime"));
				
				if(!alTaskId.contains(rs.getString("task_id"))) {
					alTaskId.add(rs.getString("task_id"));
				}
			}
			rs.close();
			pst.close();
			/**
			 * Work KPI Money
			 * */
			double dblActualCostTotal = 0.0d;
			double dblBudgetedCostTotal = 0.0d;
			for(int i = 0; alTaskId != null && i < alTaskId.size(); i++){
				String strProId =  hmTaskPro.get("PRO_ID_"+alTaskId.get(i));
				String strTaskId =  hmTaskPro.get("TASK_ID_"+alTaskId.get(i));
				
				pst = con.prepareStatement("SELECT * FROM projectmntnc where pro_id = ?");
				pst.setInt(1, uF.parseToInt(strProId));
				rs = pst.executeQuery();
				String strActualBilling = null;
				while(rs.next()) {
					strActualBilling = rs.getString("actual_calculation_type");
				}
				rs.close();
				pst.close();
				
				Map<String, String> empcostMp = CF.getProjectEmpActualRates(con, uF, strProId, strActualBilling);
				double dblEmpRate = uF.parseToDouble(uF.getTaskResourcesAvgCostOrBillRate(empcostMp, ""+intEmpIdReq));
				double dblIdealTime = uF.parseToDouble(hmTaskPro.get("IDEAL_TIME_"+alTaskId.get(i)));
				
				double dblTaskActualCost = 0.0d;
				if(strActualBilling!=null && strActualBilling.equalsIgnoreCase("M")){
					pst = con.prepareStatement("SELECT count(distinct(task_date)) as cnt FROM task_activity where activity_id = ? and emp_id=?");
					pst.setInt(1, uF.parseToInt(strTaskId));
					pst.setInt(2, intEmpIdReq);
					rs = pst.executeQuery();
					int nTaskCount = 0;
					while(rs.next()) {
						nTaskCount = rs.getInt("cnt");
					}
					rs.close();
					pst.close();
					
					dblTaskActualCost = (nTaskCount / 30) * dblEmpRate;
				} else if(strActualBilling!=null && strActualBilling.equalsIgnoreCase("D")){
					pst = con.prepareStatement("SELECT count(distinct(task_date)) as cnt FROM task_activity where activity_id = ? and emp_id=?");
					pst.setInt(1, uF.parseToInt(strTaskId));
					pst.setInt(2, intEmpIdReq);
					rs = pst.executeQuery();
					int nTaskCount = 0;
					while(rs.next()) {
						nTaskCount = rs.getInt("cnt");
					}
					rs.close();
					pst.close();
					
					dblTaskActualCost = nTaskCount * dblEmpRate;
				} else if(strActualBilling!=null && strActualBilling.equalsIgnoreCase("H")){
					pst = con.prepareStatement("SELECT sum(actual_hrs) as actual_hrs FROM task_activity where activity_id = ? and emp_id=?");
					pst.setInt(1, uF.parseToInt(strTaskId));
					pst.setInt(2, intEmpIdReq);
					rs = pst.executeQuery();
					double nTaskCount = 0;
					while(rs.next()) {
						nTaskCount = uF.parseToDouble(rs.getString("actual_hrs"));
					}
					rs.close();
					pst.close();
					
					dblTaskActualCost = nTaskCount * dblEmpRate;
				}
				
				
				double budgetedAmt = 0;
				if(dblIdealTime > 0) {
					budgetedAmt = dblEmpRate * dblIdealTime;
				}
				dblActualCostTotal += dblTaskActualCost;
				dblBudgetedCostTotal += budgetedAmt;
			}
			double[] TASK_MONEY_DATA  = new double[2];
			String[] TASK_MONEY_LABEL  = new String[2];
			
			TASK_MONEY_DATA[0] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblActualCostTotal));
			TASK_MONEY_DATA[1] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblBudgetedCostTotal));
			
			request.setAttribute("TASK_MONEY_KPI", new SemiCircleMeter().getSemiCircleChart(TASK_MONEY_DATA, TASK_MONEY_LABEL, "Money"));
			request.setAttribute("TASK_ACTUAL_MONEY_KPI", uF.formatIntoZeroWithOutComma(dblActualCostTotal));
			request.setAttribute("TASK_BUDGET_MONEY_KPI", uF.formatIntoZeroWithOutComma(dblBudgetedCostTotal));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void getResourceMoneyProjectKPI(Connection con, UtilityFunctions uF, int intEmpIdReq) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			
			String strProDate = null;
			if(uF.parseToInt(getProWorking()) == 1){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProWorking()) == 2){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 180), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProWorking()) == 3){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 90), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProWorking()) == 4){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
			} else {
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			}
			
			Map<String, String> hmPro = new HashMap<String, String>();
			List<String> alProjectId = new ArrayList<String>(); 
			
			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, null, null, uF);
			
			StringBuilder sbQuery = new StringBuilder();
	//===start parvez date: 13-10-2022===		
			/*sbQuery.append("select pro_id, pro_name, billing_type, billing_amount, idealtime, start_date, deadline, client_id, actual_calculation_type, " +
					"bill_days_type, hours_for_bill_day, added_by, curr_id from projectmntnc pmc where pmc.pro_id > 0  and approve_status !='blocked' " +
					"and project_owner=? and start_date > ?");*/
			sbQuery.append("select pro_id, pro_name, billing_type, billing_amount, idealtime, start_date, deadline, client_id, actual_calculation_type, " +
					"bill_days_type, hours_for_bill_day, added_by, curr_id from projectmntnc pmc where pmc.pro_id > 0  and approve_status !='blocked' " +
					"and project_owners like '%,"+intEmpIdReq+",%' and start_date > ?");
			
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, intEmpIdReq);
			pst.setDate(1, uF.getDateFormat(strProDate, DATE_FORMAT));
	//===end parvez date: 13-10-2022===		
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()){
				hmPro.put("PRO_ID_"+rs.getString("pro_id"), rs.getString("pro_id"));
				hmPro.put("ACTUAL_CAL_TYPE_"+rs.getString("pro_id"), rs.getString("actual_calculation_type"));
				hmPro.put("BILLING_TYPE_"+rs.getString("pro_id"), rs.getString("billing_type"));
				hmPro.put("BILLING_AMOUNT_"+rs.getString("pro_id"), rs.getString("billing_amount"));
				
				if(!alProjectId.contains(rs.getString("pro_id"))) {
					alProjectId.add(rs.getString("pro_id"));
				}
			}
			rs.close();
			pst.close();
			/**
			 * Project KPI Money
			 * */
			double dblProBudgetAmt = 0.0d;
			double dblProActualAmt = 0.0d;
			for(int i = 0; alProjectId != null && i < alProjectId.size(); i++){
				
				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, (String)alProjectId.get(i));

				Map<String, String> hmProActualCostTime = new HashMap<String, String>();
				if("M".equalsIgnoreCase(hmPro.get("ACTUAL_CAL_TYPE_"+(String)alProjectId.get(i)))) { 
					hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con,request, CF, uF, (String)alProjectId.get(i), hmProjectData);
				} else {
					hmProActualCostTime = CF.getProjectActualCostAndTime(con,request, CF, uF, (String)alProjectId.get(i), hmProjectData, false, false);
				}
				Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, (String)alProjectId.get(i), hmProjectData);
				double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get((String)alProjectId.get(i)));
				
				double dblBugedtedAmt = uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedCost"));
				double dblActualAmt = uF.parseToDouble(hmProActualCostTime.get("proActualCost"));
				 
				dblProBudgetAmt += uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblBugedtedAmt));
				dblProActualAmt += uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma((dblActualAmt + dblReimbursement)));
			}
			
			double[] PRO_MONEY_DATA  = new double[2];
			String[] PRO_MONEY_LABEL  = new String[2];
			
			PRO_MONEY_DATA[0] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblProActualAmt));
			PRO_MONEY_DATA[1] = uF.parseToDouble(uF.formatIntoZeroWithOutComma(dblProBudgetAmt));
			
			request.setAttribute("PRO_MONEY_KPI", new SemiCircleMeter().getSemiCircleChart(PRO_MONEY_DATA, PRO_MONEY_LABEL, "Money"));
			request.setAttribute("PRO_ACTUAL_MONEY_KPI", uF.formatIntoZeroWithOutComma(dblProActualAmt));
			request.setAttribute("PRO_BUDGET_MONEY_KPI", uF.formatIntoZeroWithOutComma(dblProBudgetAmt));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void getResourceProjectKPI(Connection con, UtilityFunctions uF, int intEmpIdReq) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			int workingProjects=0;
			int completedProjects=0;
			String strProDate = null;
			if(uF.parseToInt(getProWorking()) == 1){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProWorking()) == 2){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 180), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProWorking()) == 3){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 90), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getProWorking()) == 4){
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
			} else {
				strProDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			}
			
			pst = con.prepareStatement("select * from projectmntnc where pro_id in (select pro_id from project_emp_details where emp_id=?) " +
					"and approve_status !='blocked' and start_date > ?");
			pst.setInt(1, intEmpIdReq);
			pst.setDate(2, uF.getDateFormat(strProDate, DATE_FORMAT));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()){
				if(rs.getString("approve_status")!=null && rs.getString("approve_status").trim().equalsIgnoreCase("n")){
					workingProjects++;
				} else if(rs.getString("approve_status")!=null && rs.getString("approve_status").trim().equalsIgnoreCase("approved")){
					completedProjects++;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("workingProjects", ""+workingProjects);
			request.setAttribute("completedProjects", ""+completedProjects);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void getResourceWorkKPI(Connection con, UtilityFunctions uF, int intEmpIdReq) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			int workingTasks=0;
			int completedTasks=0;
			String strTaskDate = null;
			if(uF.parseToInt(getTaskWorking()) == 1){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getTaskWorking()) == 2){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 180), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getTaskWorking()) == 3){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 90), DBDATE, DATE_FORMAT);
			} else if(uF.parseToInt(getTaskWorking()) == 4){
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 30), DBDATE, DATE_FORMAT);
			} else {
				strTaskDate = uF.getDateFormat(""+uF.getPrevDate(CF.getStrTimeZone(), 365), DBDATE, DATE_FORMAT);
			}
			
			pst = con.prepareStatement("select ai.*,p.pro_name from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
					"and resource_ids like '%,"+intEmpIdReq+",%' and task_id not in (select parent_task_id from activity_info) " +
					"and ai.start_date > ?");
			pst.setDate(1, uF.getDateFormat(strTaskDate, DATE_FORMAT));
//			System.out.println("pst======>"+pst);
			rs = pst.executeQuery();
			while (rs.next()){
				if(rs.getString("approve_status")!=null && rs.getString("approve_status").trim().equalsIgnoreCase("n")){
					workingTasks++;
				} else if(rs.getString("approve_status")!=null && rs.getString("approve_status").trim().equalsIgnoreCase("approved")){
					completedTasks++;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("workingTasks", ""+workingTasks);
			request.setAttribute("completedTasks", ""+completedTasks);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	private void getResourceTimeLine(Connection con,  final UtilityFunctions uF, int intEmpIdReq) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			List<Map<String, String>> alResourceTimeLine = new ArrayList<Map<String,String>>();
			pst = con.prepareStatement("select * from projectmntnc where pro_id in (select pro_id from project_emp_details where emp_id=?) and approve_status !='blocked'");
			pst.setInt(1, intEmpIdReq);
			rs = pst.executeQuery();
			while (rs.next()){
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("PRO_ID", rs.getString("pro_id"));
				hmInner.put("PRO_NAME", rs.getString("pro_name"));
				hmInner.put("START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("COMPLETED_DATE", uF.getDateFormat(rs.getString("approve_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("APPROVE_STATUS", rs.getString("approve_status"));
				hmInner.put("PROJECT_TASK", "1");
				
				alResourceTimeLine.add(hmInner);
			}
			
			pst = con.prepareStatement("select ai.*,p.pro_name from activity_info ai,projectmntnc p where ai.pro_id=p.pro_id " +
					"and resource_ids like '%,"+intEmpIdReq+",%' and task_id not in (select parent_task_id from activity_info)");
			rs = pst.executeQuery();
			while (rs.next()){
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("TASK_ID", rs.getString("task_id"));
				hmInner.put("PRO_ID", rs.getString("pro_id"));
				hmInner.put("PRO_NAME", rs.getString("pro_name"));
				hmInner.put("ACTIVITY_NAME", rs.getString("activity_name"));
				hmInner.put("START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("COMPLETED_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("APPROVE_STATUS", rs.getString("approve_status"));
				hmInner.put("PROJECT_TASK", "2");
				
				alResourceTimeLine.add(hmInner);
			}
			rs.close();
			pst.close();
			Collections.sort(alResourceTimeLine, Collections.reverseOrder(new Comparator<Map<String, String>>()
					{
						@Override
						public int compare(Map<String, String> hm, Map<String, String> hm1) {
							String stDate1 = hm.get("APPROVE_STATUS").equals("n") ? hm.get("START_DATE") : hm.get("COMPLETED_DATE");
							String stDate2 = hm1.get("APPROVE_STATUS").equals("n") ? hm1.get("START_DATE") : hm1.get("COMPLETED_DATE");
							
							if(stDate1 != null && !stDate1.equalsIgnoreCase("null") && !stDate1.equals("") && stDate2 != null && !stDate2.equalsIgnoreCase("null") && !stDate2.equals("")) {
								Date date1 = uF.getDateFormat(stDate1, CF.getStrReportDateFormat());
								Date date2 = uF.getDateFormat(stDate2, CF.getStrReportDateFormat());
								if(date1!=null && date2!=null) {
									return date1.compareTo(date2);
								} else {
									return 0;
								}
							} else {
								return 0;
							}
						}
					}));
			
			request.setAttribute("alResourceTimeLine", alResourceTimeLine);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void getSalaryHeadsforEmployee(Connection con, UtilityFunctions uF, int intEmpIdReq, Map<String, String> hmEmpProfile) {
		PreparedStatement pst = null;
		ResultSet rs = null;

		try {
			
			String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
			String strFinancialYearStart = strFinancialYearDates[0];
			String strFinancialYearEnd = strFinancialYearDates[1];


			Map<String, String> hmEmpLevel = CF.getEmpLevelMap(con);
			String levelId = hmEmpLevel.get(intEmpIdReq + "");
			
			String strOrg = CF.getEmpOrgId(con, uF, ""+intEmpIdReq);
			String strEmpGender = CF.getEmpGender(con, uF, ""+intEmpIdReq);

			Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
			Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			
			String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String[] strPayCycleDates = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrg);
			String strD1 = strPayCycleDates[0];
			String strD2 = strPayCycleDates[1];
			String strPC = strPayCycleDates[2];
			
			int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
			
			Map hmEmpMertoMap = new HashMap();
			Map hmEmpWlocationMap = new HashMap();
			Map hmEmpStateMap = new HashMap();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			
			String strStateId = (String)hmEmpStateMap.get(""+intEmpIdReq);
			
			Map<String, String> hmEmpServiceTaxMap = CF.getEmpServiceTax(con, uF, CF);
			
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmHRAExemption = new HashMap();
			while(rs.next()){
				hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
				hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
				hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
				hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
				
				hmOtherTaxDetails.put(rs.getString("state_id")+"_MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_REBATE_AMOUNT", rs.getString("rebate_amt"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));
				
//				dblInvestmentExemption = 100000;
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			double dblInvestmentExemption = 0.0d;
			if (rs.next()) {
				dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmSalaryDetails = new HashMap<String, String>();
			List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
			List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
			pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+") and (is_contribution is null or is_contribution=false) and org_id =? order by earning_deduction desc, salary_head_id, weight");
			pst.setInt(1, uF.parseToInt(strOrg));
			rs = pst.executeQuery();  
			List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
			List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
			while(rs.next()){
				
				if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("E")){
					int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
					
					if(index>=0){
						alEmpSalaryDetailsEarning.remove(index);
						alEarningSalaryDuplicationTracer.remove(index);
						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
					}else{
						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
					}
					
					alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
				}else if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("D")){
					int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
					if(index>=0){
						alEmpSalaryDetailsDeduction.remove(index);
						alDeductionSalaryDuplicationTracer.remove(index);
						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
					}else{
						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
					}
					alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
				}
				
				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();
			

			Map<String, Double> hmSalaryTotal = new LinkedHashMap<String, Double>();
			double grossAmount = 0.0d;
			double grossYearAmount = 0.0d;
			double deductAmount = 0.0d;
			double deductYearAmount = 0.0d;
			
			
			ApprovePayroll objAP = new ApprovePayroll();
			objAP.CF = CF;
			objAP.session = session;
			objAP.request = request; 
			
			Map<String, Map<String, String>> hmEmpPaidAmountDetails =  objAP.getEmpPaidAmountDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String, String> hmEmpExemptionsMap = objAP.getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
			Map<String, String> hmEmpHomeLoanMap = objAP.getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String,String> hmFixedExemptions = objAP.getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
			Map<String, String> hmEmpRentPaidMap = objAP.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
			
			Map<String, String> hmPrevEmpTdsAmount  = new HashMap<String, String>();
			Map<String, String> hmPrevEmpGrossAmount  = new HashMap<String, String>();
			objAP.getPrevEmpTdsAmount(con,uF,strFinancialYearStart,strFinancialYearEnd,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount);
			
			Map<String, String> hmEmpIncomeOtherSourcesMap = objAP.getEmpIncomeOtherSources(con, uF, strFinancialYearStart, strFinancialYearEnd);
				
			pst = con.prepareStatement("SELECT * FROM (SELECT * FROM emp_salary_details WHERE emp_id=? " +
					"AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
					"WHERE emp_id=? and is_approved = true and level_id=?) AND effective_date <= ? " +
					"and level_id=?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id=sd.salary_head_id " +
					"WHERE sd.level_id=? and asd.salary_head_id not in("+GROSS+") " +
					"and (is_delete is null or is_delete=false) and (is_contribution is null or is_contribution=false) order by sd.earning_deduction desc, weight");
			pst.setInt(1, intEmpIdReq);
			pst.setInt(2, intEmpIdReq);
			pst.setInt(3, uF.parseToInt(levelId));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt(levelId));
			pst.setInt(6, uF.parseToInt(levelId));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			List alSalaryDuplicationTracer = new ArrayList();
			List<List<String>> salaryHeadDetailsList = new ArrayList<List<String>>();
			Map<String, String> hmTotal = new HashMap<String, String>();
			double dblGrossTDS = 0.0d;
			while (rs.next()) {

				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("salary_head_name"));
				innerList.add(rs.getString("earning_deduction"));
//				innerList.add(uF.parseToBoolean(rs.getString("isdisplay"))? rs.getString("amount") : "0");
//				double dblYearAmount = (uF.parseToBoolean(rs.getString("isdisplay")) ? rs.getDouble("amount") : 0.0d )* 12;
//				innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));

				if(rs.getString("earning_deduction").equals("E")) {
					if(uF.parseToBoolean(rs.getString("isdisplay"))){
						double dblYearAmount = rs.getDouble("amount") * 12;
						
						innerList.add(rs.getString("amount"));
						innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
						
						grossAmount += rs.getDouble("amount");
						grossYearAmount += dblYearAmount;
						
						if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
							dblGrossTDS += rs.getDouble("amount");
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
							dblGrossTDS += rs.getDouble("amount");
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
							dblGrossTDS += rs.getDouble("amount");
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
							dblGrossTDS += rs.getDouble("amount");
						}
						
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
					} else {
						innerList.add("0.0");
						innerList.add("0.0");
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
					}
				} else if(rs.getString("earning_deduction").equals("D")) {
					if(uF.parseToBoolean(rs.getString("isdisplay"))){
//						int nPayMonth = uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "MM"));
						switch(rs.getInt("salary_head_id")){
													
							case PROFESSIONAL_TAX :
								  
								if(uF.parseToDouble(rs.getString("amount")) > 0.0d){
									double dblYearAmount = rs.getDouble("amount") * 12;
									
									deductAmount += rs.getDouble("amount");
									deductYearAmount += dblYearAmount + 100;
									
									innerList.add(rs.getString("amount"));
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma((dblYearAmount + 100)));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
								} else {
									
									double dblAmount = objAP.calculateProfessionalTax(con, uF, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT),grossAmount,strFinancialYearStart, strFinancialYearEnd, nPayMonth, strStateId, strEmpGender);
									dblAmount = Math.round(dblAmount);
									double dblYearAmount = getAnnualProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, strStateId, strEmpGender);
									dblYearAmount = Math.round(dblYearAmount);
									
									deductAmount += dblAmount;
									deductYearAmount += dblYearAmount + 100;
									
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount));
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma((dblYearAmount + 100)));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
								}
								
							break;
							
							case EMPLOYEE_EPF :
									
								if(uF.parseToDouble(rs.getString("amount")) > 0.0d){
									double dblYearAmount = rs.getDouble("amount") * 12;
									
									deductAmount += rs.getDouble("amount");
									deductYearAmount += dblYearAmount;
									
									innerList.add(rs.getString("amount"));
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
								} else {
									double dblAmount = objAP.calculateEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmTotal, getEmpId(), null, null, false, null);
									dblAmount = Math.round(dblAmount);
									double dblYearAmount = dblAmount * 12;
									
									deductAmount += dblAmount;
									deductYearAmount += dblYearAmount;
									
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount));
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
								}
								
							break;
							
							case EMPLOYER_EPF :
								
								if(uF.parseToDouble(rs.getString("amount")) > 0.0d){
									double dblYearAmount = rs.getDouble("amount") * 12;
									
									deductAmount += rs.getDouble("amount");
									deductYearAmount += dblYearAmount;
									
									innerList.add(rs.getString("amount"));
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
								} else {
									double dblAmount = objAP.calculateERPF(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, getEmpId(), null, null, false, null);
									dblAmount = Math.round(dblAmount);
									double dblYearAmount = dblAmount * 12; 
									
									deductAmount += dblAmount;
									deductYearAmount += dblYearAmount;
									
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount));
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
								}
								
							break;  
							
							case EMPLOYER_ESI :
								
								if(uF.parseToDouble(rs.getString("amount")) > 0.0d){
									double dblYearAmount = rs.getDouble("amount") * 12;
									
									deductAmount += rs.getDouble("amount");
									deductYearAmount += dblYearAmount;
									
									innerList.add(rs.getString("amount"));
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
								} else {
									double dblAmount = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,getEmpId(), null, null);
									dblAmount = Math.ceil(dblAmount);
									double dblYearAmount = dblAmount * 12;
									dblYearAmount = Math.ceil(dblYearAmount);
									
									deductAmount += dblAmount;
									deductYearAmount += dblYearAmount;
									
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount));
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
								}
								
							break;
							
							case EMPLOYEE_ESI :
								
								if(uF.parseToDouble(rs.getString("amount")) > 0.0d){
									double dblYearAmount = rs.getDouble("amount") * 12;
									
									deductAmount += rs.getDouble("amount");
									deductYearAmount += dblYearAmount;
									
									innerList.add(rs.getString("amount"));
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
								} else {
									double dblAmount = objAP.calculateEEESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, null);
									dblAmount = Math.ceil(dblAmount);
									double dblYearAmount = dblAmount * 12;
									dblYearAmount = Math.ceil(dblYearAmount);
									
									deductAmount += dblAmount;
									deductYearAmount += dblYearAmount;
									
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount));
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
								}
								
							break;
							
							case EMPLOYER_LWF :
								
								if(uF.parseToDouble(rs.getString("amount")) > 0.0d){
									double dblYearAmount = rs.getDouble("amount") * 12;
									
									deductAmount += rs.getDouble("amount");
									deductYearAmount += dblYearAmount;
									
									innerList.add(rs.getString("amount"));
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
								} else {
									double dblAmount = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth, strOrg);
									dblAmount = Math.round(dblAmount);
									double dblYearAmount = dblAmount * 12;
									
									deductAmount += dblAmount;
									deductYearAmount += dblYearAmount;
									
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount));
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
								}
								
							break;
							
							case EMPLOYEE_LWF :
								
								if(uF.parseToDouble(rs.getString("amount")) > 0.0d){
									double dblYearAmount = rs.getDouble("amount") * 12;
									
									deductAmount += rs.getDouble("amount");
									deductYearAmount += dblYearAmount;
									
									innerList.add(rs.getString("amount"));
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
								} else {
									double dblAmount = objAP.calculateEELWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, getEmpId(), nPayMonth, strOrg);
									dblAmount = Math.round(dblAmount);
									double dblYearAmount = dblAmount * 12;
									
									deductAmount += dblAmount;
									deductYearAmount += dblYearAmount;
									
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount));
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
								}
								
							break;
							
							case TDS :
								
//								if(uF.parseToDouble(rs.getString("amount")) > 0.0d){
//									double dblYearAmount = rs.getDouble("amount") * 12;
//									
//									deductAmount += rs.getDouble("amount");
//									deductYearAmount += dblYearAmount;
//									
//									innerList.add(rs.getString("amount"));
//									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
//									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
//								} else {
//									
//									double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
//									double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
//									double dblHRA = uF.parseToDouble(hmTotal.get(HRA+""));
//									
//									String[] hraSalaryHeads = null;
//									if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null){
//										hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
//									}
//									
//									double dblHraSalHeadsAmount = 0;
//									for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++){
//										dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotal.get(hraSalaryHeads[i]));
//									}
//									
//									Map<String, String> hmPaidSalaryDetails =  hmEmpPaidAmountDetails.get(""+intEmpIdReq);
//									if(hmPaidSalaryDetails==null){hmPaidSalaryDetails=new HashMap<String, String>();}
//									
//									double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(""+intEmpIdReq)+"_EDU_TAX"));
//									double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(""+intEmpIdReq)+"_STD_TAX"));
//									double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(""+intEmpIdReq)+"_FLAT_TDS"));
//									 
//									 
//									if(hmEmpServiceTaxMap.containsKey(""+intEmpIdReq)){
//										dblGrossTDS = grossAmount;
//										double  dblServiceTaxAmount = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
//										dblGrossTDS = dblGrossTDS - dblServiceTaxAmount;
//									}
//									
//									double dblAmount = objAP.calculateTDS(con, uF,strD2,strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
//											nPayMonth,
//											strD1, strFinancialYearStart, strFinancialYearEnd, ""+intEmpIdReq, hmEmpGenderMap.get(""+intEmpIdReq),  hmEmpAgeMap.get(""+intEmpIdReq), strStateId,
//											hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails,
//											hmTotal, hmSalaryDetails, hmEmpLevelMap, CF,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount,hmEmpIncomeOtherSourcesMap,hmOtherTaxDetails,hmEmpStateMap);
//									
//									dblAmount = Math.round(dblAmount);
//									double dblYearAmount = dblAmount * 12;
//									
//									deductAmount += dblAmount;
//									deductYearAmount += dblYearAmount;
//									
//									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount));
//									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
//									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
//								}
								
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
								innerList.add(rs.getString("salary_head_id"));
								hmTotal.put(rs.getString("salary_head_id"), ""+0.0d);
								
							break;
							
							default:
								
								if(uF.parseToDouble(rs.getString("amount")) > 0.0d){
									double dblYearAmount = rs.getDouble("amount") * 12;
									
									deductAmount += rs.getDouble("amount");
									deductYearAmount += dblYearAmount;
									
									innerList.add(rs.getString("amount"));
									innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
									hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
								} else {  
									
									if("P".equalsIgnoreCase(rs.getString("salary_head_amount_type"))){
										
										double dblAmount = uF.parseToDouble((String)hmTotal.get(rs.getString("sub_salary_head_id")));
										dblAmount = dblAmount * rs.getDouble("salary_head_amount") /100;
										double dblYearAmount = dblAmount * 12;
										
										deductAmount += dblAmount;
										deductYearAmount += dblYearAmount;
										
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblAmount));
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
										hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(dblAmount));
									}else{
										double dblYearAmount = rs.getDouble("amount") * 12;
										
										deductAmount += rs.getDouble("amount");
										deductYearAmount += dblYearAmount;
										
										innerList.add(rs.getString("amount"));
										innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));
										hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(rs.getString("amount"))));
									}
								}
								
							break;
						}
					}  else {
						innerList.add("0.0");
						innerList.add("0.0");
						hmTotal.put(rs.getString("salary_head_id"), uF.formatIntoTwoDecimalWithOutComma(0.0d));
					}
					
					
//					innerList.add(rs.getString("amount"));
//					double dblYearAmount = rs.getDouble("amount") * 12;
//					
//					deductAmount += rs.getDouble("amount");
//					deductYearAmount += dblYearAmount;
				}
				
				int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
				
				if(index>=0){
					salaryHeadDetailsList.remove(index);
					salaryHeadDetailsList.add(index, innerList);
				}else{
					alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					salaryHeadDetailsList.add(innerList);
				}
			}
			rs.close();
			pst.close();
			hmSalaryTotal.put("GROSS_AMOUNT", grossAmount);
			hmSalaryTotal.put("GROSS_YEAR_AMOUNT", grossYearAmount);
			hmSalaryTotal.put("DEDUCT_AMOUNT", deductAmount);
			hmSalaryTotal.put("DEDUCT_YEAR_AMOUNT", deductYearAmount);
			
			request.setAttribute("hmSalaryTotal", hmSalaryTotal);
			request.setAttribute("salaryHeadDetailsList", salaryHeadDetailsList);
//			System.out.println("salaryHeadDetailsList======>"+salaryHeadDetailsList); 
			
			pst = con.prepareStatement("select amount from payroll_generation where emp_id = ? and salary_head_id = ? and financial_year_from_date=? and financial_year_to_date=?");
			pst.setInt(1, intEmpIdReq);
			pst.setInt(2, PROFESSIONAL_TAX);
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			double dblAmount = 0;
			int nMonthCount = 0;
			while(rs.next()){
				dblAmount += uF.parseToDouble(rs.getString("amount"));
				nMonthCount++;
			}
			rs.close();
			pst.close();
			
			int nTotalCount = 12;
			
			java.util.Date dtJoiningDt = uF.getDateFormatUtil((String)hmEmpProfile.get("JOINING_DATE"), CF.getStrReportDateFormat());
			java.util.Date dtFinancialYearStartDt = uF.getDateFormatUtil(strFinancialYearStart, DATE_FORMAT);
			java.util.Date dtFinancialYearEndDt = uF.getDateFormatUtil(strFinancialYearEnd, DATE_FORMAT);
			if(dtJoiningDt!=null && dtJoiningDt.before(dtFinancialYearStartDt)) {
				nTotalCount = 12;
			} else if(dtJoiningDt != null) {
				int m1 = dtJoiningDt.getYear() * 12 + dtJoiningDt.getMonth();
			    int m2 = dtFinancialYearEndDt.getYear() * 12 + dtFinancialYearEndDt.getMonth();
			    nTotalCount = m2 - m1 + 1;
			}
			int nRemainingCount = nTotalCount - nMonthCount;
			
			
			pst = con.prepareStatement("select amount from emp_salary_details where emp_id=? " +
					"and earning_deduction=? and is_approved =true " +
					"and effective_date=(select max(effective_date) from emp_salary_details " +
					"where emp_id= ? and earning_deduction = ? and is_approved = true and level_id=?) " +
					"and level_id=?");
			pst.setInt(1, intEmpIdReq);
			pst.setString(2, "E");
			pst.setInt(3, intEmpIdReq);
			pst.setString(4, "E");
			pst.setInt(5, uF.parseToInt(levelId));
			pst.setInt(6, uF.parseToInt(levelId));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			double dblGrossAmount = 0;
			while(rs.next()){
				dblGrossAmount += uF.parseToDouble(rs.getString("amount"));
			}
			rs.close();
			pst.close();
			pst = con.prepareStatement("select  * from deduction_details_india where income_from<= ? and income_to>= ? and state_id=? and financial_year_from = (select max(financial_year_from) from deduction_details_india) limit 1");
			pst.setDouble(1, dblGrossAmount);
			pst.setDouble(2, dblGrossAmount);
			pst.setInt(3, uF.parseToInt((String)hmEmpStateMap.get(intEmpIdReq+"")));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			double dblDeductionAmount = 0;
			while(rs.next()) {
				dblDeductionAmount = rs.getDouble("deduction_amount");
			}
			
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			Map<String, Map<String, String>> hmCurrencyDetailsMap = CF.getCurrencyDetailsForPDF(con);
			if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String, String>>();
			String strCurrency = "";
			if(uF.parseToInt(hmEmpCurrency.get(intEmpIdReq+"")) > 0){
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(intEmpIdReq+""));
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			} 
			
			ApprovePayroll objAppPayroll = new ApprovePayroll();
			objAppPayroll.CF = CF;
			objAppPayroll.session = session;
			objAppPayroll.request = request;
			
			double dblMonthlyAmount = objAppPayroll.calculateProfessionalTax(con, uF, null, dblGrossAmount, strFinancialYearStart, strFinancialYearEnd, 6, (String)hmEmpStateMap.get(intEmpIdReq+""),strEmpGender);
			double dblVar = dblDeductionAmount - (dblMonthlyAmount * 12);
			dblAmount = dblAmount + (dblMonthlyAmount * nRemainingCount) + dblVar;
			
			request.setAttribute("dblAmount", strCurrency+uF.formatIntoOneDecimal(dblAmount));
			request.setAttribute("dblMonthlyAmount", strCurrency+uF.formatIntoOneDecimal(dblMonthlyAmount));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pst != null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private double getAnnualProfessionalTax(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, 
			String strStateId, String strEmpGender) {
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		double dblDeductionAnnual= 0;
		
		
		try {
			
			pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? " +
					"and financial_year_from=? and financial_year_to=? and gender =? limit 1");
			pst.setDouble(1, dblGross);
			pst.setDouble(2, dblGross);
			pst.setInt(3, uF.parseToInt(strStateId));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(6, strEmpGender);			
			rs = pst.executeQuery();  
			while(rs.next()){
				dblDeductionAnnual = rs.getDouble("deduction_amount");
			}
			rs.close();
			pst.close();
			
			
			
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
		return dblDeductionAnnual;
	}
	
	HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}
	public String getProPopup() { 
		return proPopup;
	}

	public void setProPopup(String proPopup) {
		this.proPopup = proPopup;
	}

	public String getPopup() {
		return popup;
	}

	public void setPopup(String popup) {
		this.popup = popup; 
	}
	public String getTaskWorking() {
		return taskWorking;
	}
	public void setTaskWorking(String taskWorking) {
		this.taskWorking = taskWorking;
	}
	public String getProWorking() {
		return proWorking;
	}
	public void setProWorking(String proWorking) {
		this.proWorking = proWorking;
	}
	public String getEmpImageFileName() {
		return empImageFileName;
	}
	public void setEmpImageFileName(String empImageFileName) {
		this.empImageFileName = empImageFileName;
	}
	public File getEmpImage() {
		return empImage;
	}
	public void setEmpImage(File empImage) {
		this.empImage = empImage;
	}
	public String getSubmit() {
		return submit;
	}
	public void setSubmit(String submit) {
		this.submit = submit;
	}
	
}
