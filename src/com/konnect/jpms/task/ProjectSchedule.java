package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillClients;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectSchedule extends ActionSupport implements ServletRequestAware, IStatements{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	
	String f_org;
	String[] f_strWLocation; 
	String[] f_department;
	String[] f_service;
	String[] f_level;
	String[] f_project_service;
	String[] f_client;
	
	String selectOne;
	String strStartDate;
	String strEndDate;
	String financialYear;
	String paycycle;
	String strMonth; 
	
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillServices> serviceList;
	List<FillServices> projectServiceList;
	List<FillLevel> levelList;
	List<FillFinancialYears> financialYearList;
	List<FillMonth> monthList;
	List<FillPayCycles> paycycleList;
	
	List<FillClients> clientList;
	
	String proType;
	
	String proPage;
	String minLimit;
	String loadMore;
	String project_service;
	
	public String execute() {
		
		request.setAttribute(PAGE, "/jsp/task/ProjectSchedule.jsp");
		request.setAttribute(TITLE, "Project Schedule");
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		UtilityFunctions uF=new UtilityFunctions();
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		
//		boolean isView = CF.getAccess(session, request, uF); 
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied); 
//			return ACCESS_DENIED; 
//		}
		
		if(getF_org() == null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
			getProjectDetails(uF.parseToInt((String)session.getAttribute(EMPID)), uF, CF, 0);
		} else if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER) || strUserType.equalsIgnoreCase(CEO))) {
			getProjectDetails(0, uF, CF, 0);
		}
		
		loadOutstandingReport(uF);
		
		return SUCCESS;
	}
	
	
	public String loadOutstandingReport(UtilityFunctions uF) {
		organisationList = new FillOrganisation(request).fillOrganisation();
		wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		projectServiceList = new FillServices(request).fillProjectServices();
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		clientList = new FillClients(request).fillClients(false);
		getSelectedFilter(uF);
		
		return SUCCESS;
	}
	
	
	public void getProjectDetails(int nManagerId, UtilityFunctions uF, CommonFunctions CF, int nLimit) {
		
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmEmpNMap = CF.getEmpNameMap(con, null, null);
			
			Map<String, String> hmProjectClient  = new HashMap<String, String>();
			boolean isComplete = false;
			if(getProType() != null && getProType().equals("C")) {
				isComplete = true;
			}
			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", isComplete, getStrStartDate(), getStrEndDate(), uF);
			
			StringBuilder budgeted_cost 	= new StringBuilder();
			StringBuilder billable_amount 	= new StringBuilder();
			StringBuilder actual_amount 	= new StringBuilder();
			StringBuilder pro_name 			= new StringBuilder();

			
			
			int proCount = 0;
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select count(*) as proCount from projectmntnc pmc where pmc.pro_id > 0 ");
			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
				sbQuery.append(" and pmc.approve_status = 'n' ");
			} else if(getProType() != null && getProType().equals("C")) {
				sbQuery.append(" and pmc.approve_status = 'approved' ");
			}
			
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and pmc.org_id in ("+uF.parseToInt(getF_org())+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and pmc.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0 && !getF_strWLocation()[0].trim().equals("")) {
				sbQuery.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
	        	sbQuery.append(" and pmc.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}

			if(getF_department() != null && getF_department().length>0 && !getF_department()[0].trim().equals("")) {
				sbQuery.append(" and pmc.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			
			if(getF_service() != null && getF_service().length>0 && !getF_service()[0].trim().equals("")) {
				sbQuery.append(" and pmc.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			
			if(getF_project_service()!=null && getF_project_service().length > 0 && getLoadMore() == null) {
				String service = uF.getConcateData(getF_project_service());
				sbQuery.append(" and pmc.service in ("+service+") ");
			} else if(getProject_service()!=null && getProject_service().length() > 0) {
				String service = getConcateDataLoadMore(getProject_service());
				sbQuery.append(" and pmc.service in ("+service+") ");
			}
			
			if(getF_client() != null && getF_client().length>0 && !getF_client()[0].trim().equals("")) {
				sbQuery.append(" and pmc.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			
			if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase(LABEL_FROM_DATE) && !getStrEndDate().equalsIgnoreCase(LABEL_TO_DATE)) {
				sbQuery.append(" and pmc.start_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}
			if(nManagerId>0) {
				sbQuery.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "+nManagerId+" ) or added_by = "+nManagerId+" ) ");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				proCount = rs.getInt("proCount")/10;
				if(rs.getInt("proCount")%10 != 0) {
					proCount++;
				}
			}
			rs.close();
			pst.close();
			
			request.setAttribute("proCount", proCount+"");
			
			StringBuilder sbQuery1 = new StringBuilder();
			sbQuery1.append("select pro_id, pro_name, billing_type, billing_amount, idealtime, start_date, deadline, client_id, actual_calculation_type, " +
				"bill_days_type, hours_for_bill_day, added_by, curr_id,project_owner,project_owners from projectmntnc pmc where pmc.pro_id > 0 ");
			if(getProType() == null || getProType().equals("") || getProType().equals("null") || getProType().equals("L")) {
				sbQuery1.append(" and pmc.approve_status = 'n' ");
			} else if(getProType() != null && getProType().equals("C")) {
				sbQuery1.append(" and pmc.approve_status = 'approved' ");
			}
			
			if(uF.parseToInt(getF_org())>0) {
				sbQuery1.append(" and pmc.org_id in ("+uF.parseToInt(getF_org())+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery1.append(" and pmc.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0 && !getF_strWLocation()[0].trim().equals("")) {
	            sbQuery1.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery1.append(" and pmc.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}

			if(getF_department() != null && getF_department().length>0 && !getF_department()[0].trim().equals("")) {
				sbQuery1.append(" and pmc.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			
			if(getF_service() != null && getF_service().length>0 && !getF_service()[0].trim().equals("")) {
				sbQuery1.append(" and pmc.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			
			if(getF_project_service()!=null && getF_project_service().length > 0 && getLoadMore() == null) {
				String service = uF.getConcateData(getF_project_service());
				sbQuery1.append(" and pmc.service in ("+service+") ");
			} else if(getF_project_service()!=null && getF_project_service().length > 0) {
				String service = getConcateDataLoadMore(getProject_service());
				sbQuery1.append(" and pmc.service in ("+service+") ");
			}
			
			if(getF_client() != null && getF_client().length>0 && !getF_client()[0].trim().equals("")) {
				sbQuery1.append(" and pmc.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			
			if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase(LABEL_FROM_DATE) && !getStrEndDate().equalsIgnoreCase(LABEL_TO_DATE)) {
				sbQuery1.append(" and pmc.start_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}
			if(nManagerId>0) {
				sbQuery1.append(" and ( pro_id in (select pro_id from project_emp_details where _isteamlead = true and emp_id = "+nManagerId+" ) or added_by = "+nManagerId+" ) ");
			}
			int intOffset = uF.parseToInt(minLimit);
			sbQuery1.append(" order by pmc.pro_id limit 10 offset "+intOffset+"");
			pst = con.prepareStatement(sbQuery1.toString());
			rs = pst.executeQuery();
//			System.out.println("pst==>" + pst);
			
			double dblBugedtedAmt = 0;
			double dblActualAmt = 0;
			double dblBillableAmt = 0;
			
			double dblBugedtedTime = 0; 
			double dblActualTime = 0;
			
			double dblIdealTimeHrs = 0; 
			double dblActualTimeHrs = 0;
			
			Map<String, String> hmProPerformaceBudget = new HashMap<String, String>();
			Map<String, String> hmProPerformaceActual = new HashMap<String, String>();
			Map<String, String> hmProPerformaceBillable = new HashMap<String, String>();
			
			Map<String, String> hmProPerformaceIdealTime = new HashMap<String, String>();
			Map<String, String> hmProPerformaceActualTime = new HashMap<String, String>();
			Map<String, String> hmProPerformaceProjectName = new HashMap<String, String>();
			Map<String, String> hmProPerformaceCurrency = new HashMap<String, String>();
			
			Map<String, String> hmProPerformaceProjectProfit = new HashMap<String, String>();
			Map<String, String> hmProPerformaceProjectManager = new HashMap<String, String>();
			Map<String, String> hmProOwner = new HashMap<String, String>();
			
			Map<String, String> hmProPerformaceProjectAmountIndicator = new HashMap<String, String>();
			Map<String, String> hmProPerformaceProjectTimeIndicator = new HashMap<String, String>();
			
			Map<String, String> hmProName = new HashMap<String, String>();
			
			List<String> alProjectId = new ArrayList<String>(); 
			
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			
			Map<String, String> hmProActIdealTimeHRS = new HashMap<String, String>();
			
			while(rs.next()) {
				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));

				Map<String, String> hmProActualCostTime = new HashMap<String, String>();
				Map<String, String> hmProBillCost = new HashMap<String, String>();
				if("M".equalsIgnoreCase(rs.getString("actual_calculation_type"))) { 
					hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData);
					hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData);
				} else {
					hmProActualCostTime = CF.getProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
					hmProBillCost = CF.getProjectBillableCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
				}
				Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, rs.getString("pro_id"), hmProjectData);
				double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
				hmProName.put(rs.getString("pro_id"), rs.getString("pro_name"));
				hmProjectClient.put(rs.getString("pro_id"), CF.getClientNameById(con, rs.getString("client_id")));
				
				 if("F".equalsIgnoreCase(rs.getString("billing_type"))) { 
					 dblBillableAmt = uF.parseToDouble(rs.getString("billing_amount"));
				 } else {
					 dblBillableAmt = uF.parseToDouble(hmProBillCost.get("proBillableCost"));
				 }
				 
				 dblBugedtedTime = uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedTime"));
				 
				 if("H".equalsIgnoreCase(rs.getString("actual_calculation_type"))) {
					 dblActualTime = uF.parseToDouble(hmProActualCostTime.get("proActualTime"));
					 hmProPerformaceActualTime.put(rs.getString("pro_id"), uF.getTotalTimeMinutes100To60(""+dblActualTime)+" hours");
					 hmProPerformaceIdealTime.put(rs.getString("pro_id"), uF.getTotalTimeMinutes100To60(rs.getString("idealtime"))+" hours");
				 } else if("M".equalsIgnoreCase(rs.getString("actual_calculation_type"))) { 
					 dblActualTime = uF.parseToDouble(hmProActualCostTime.get("proActualTime"));
					 hmProPerformaceActualTime.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblActualTime)+" months");
					 hmProPerformaceIdealTime.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" months");
				 } else {
					 dblActualTime = uF.parseToDouble(hmProActualCostTime.get("proActualTime"));
					 hmProPerformaceActualTime.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblActualTime)+" days");
					 hmProPerformaceIdealTime.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(uF.parseToDouble(rs.getString("idealtime")))+" days");
				 }
				 
				 dblBugedtedAmt = uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedCost"));
				 dblActualAmt = uF.parseToDouble(hmProActualCostTime.get("proActualCost"));
				 
				 hmProPerformaceBudget.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblBugedtedAmt));
				 hmProPerformaceActual.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblActualAmt + dblReimbursement));
				 hmProPerformaceBillable.put(rs.getString("pro_id"), uF.formatIntoTwoDecimal(dblBillableAmt));
				 
				 hmProPerformaceProjectName.put(rs.getString("pro_id"), rs.getString("pro_name"));
				 Map<String, String> hmCurr = hmCurrencyMap.get(rs.getString("curr_id"));
				 hmProPerformaceCurrency.put(rs.getString("pro_id"), hmCurr != null ? hmCurr.get("SHORT_CURR") : "");
				 
				 double diff = 0;
				 if(dblBillableAmt > 0) {
					 diff = ((dblBillableAmt - (dblActualAmt + dblReimbursement))/dblBillableAmt) * 100;
				 }
				 hmProPerformaceProjectProfit.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(diff)+"%");
					
				if (dblActualAmt > dblBugedtedAmt && dblActualAmt < dblBillableAmt) {
					 /*hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
					hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
					
					
				} else if(dblActualAmt < dblBugedtedAmt) {
					 /*hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
					hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
					
				} else if(dblActualAmt > dblBillableAmt) {
					/*hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
					hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
					
				} else {
					hmProPerformaceProjectAmountIndicator.put(rs.getString("pro_id"), "&nbsp;");
				}
				 
				Date dtDeadline = uF.getDateFormat(rs.getString("deadline"), DBDATE);
				Date dtCurrentDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
				
				if(dtDeadline!=null && dtCurrentDate!=null && dtDeadline.after(dtCurrentDate)) {
					if(dblActualTime <= dblBugedtedTime) {
						/*hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
						hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
						
					} else {
						/*hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
						hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
						
					}
				} else if(dtDeadline!=null && dtCurrentDate!=null && dtCurrentDate.after(dtDeadline)) {
					/*hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
					hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
					
				} else {
					hmProPerformaceProjectTimeIndicator.put(rs.getString("pro_id"), "");
				}
				
			 	if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
			 		hmProPerformaceProjectManager.put(rs.getString("pro_id"), uF.showData(hmEmpNMap.get(rs.getString("added_by")), ""));
				} else {
					hmProPerformaceProjectManager.put(rs.getString("pro_id"), uF.showData(hmEmpNMap.get(rs.getString("added_by")), ""));
				}
				
			 //===start parvez date: 18-10-2022===	
			 	StringBuilder sbOwners = null;
				if(rs.getString("project_owners")!=null){
					List<String> tempList = Arrays.asList(rs.getString("project_owners").split(","));
					
					for(int j=1; j<tempList.size();j++){
						if(sbOwners==null){
							sbOwners = new StringBuilder();
							sbOwners.append(hmEmpNMap.get(tempList.get(j)));
						}else{
							sbOwners.append(", "+hmEmpNMap.get(tempList.get(j)));
						}
					}
				}
			 	
//			 	hmProOwner.put(rs.getString("pro_id"), uF.showData(hmEmpNMap.get(rs.getString("project_owner")), ""));
				hmProOwner.put(rs.getString("pro_id"), uF.showData(sbOwners+"", ""));
			 //===end parvez date: 18-10-2022===	

//				 ********************************* Time in HRS ***************************************
				 dblActualTimeHrs = uF.parseToDouble(hmProActIdealTimeHRS.get(rs.getString("pro_id")+"ACT_TIME_HRS"));
				 String proActualTimeHRS = getProjectActualTimeHRS(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
				 dblActualTimeHrs += uF.parseToDouble(proActualTimeHRS);
				 
				 dblIdealTimeHrs = uF.parseToDouble(hmProActIdealTimeHRS.get(rs.getString("pro_id")+"IDEAL_TIME_HRS"));
				 String proIdealTimeHRS = getProjectIdealTimeHRS(con, uF, rs.getString("pro_id"), hmProjectData);
				 dblIdealTimeHrs += uF.parseToDouble(proIdealTimeHRS);
				 
				 hmProActIdealTimeHRS.put(rs.getString("pro_id")+"_ACT_TIME_HRS", uF.formatIntoTwoDecimalWithOutComma(dblActualTimeHrs));
				 hmProActIdealTimeHRS.put(rs.getString("pro_id")+"_IDEAL_TIME_HRS", uF.formatIntoTwoDecimalWithOutComma(dblIdealTimeHrs));
			 	
				 if(!alProjectId.contains(rs.getString("pro_id"))) {
					 alProjectId.add(rs.getString("pro_id"));
				 }
				 if(alProjectId.size() > nLimit && nLimit > 0) break;
			}
			rs.close();
			pst.close();
			
			
			pro_name= new StringBuilder();
			billable_amount= new StringBuilder();
			budgeted_cost= new StringBuilder();
			actual_amount= new StringBuilder();

			StringBuilder projectCost 	= new StringBuilder();
			for(int i=0; i<alProjectId.size(); i++) {
				pro_name.append("'"+hmProPerformaceProjectName.get(alProjectId.get(i))+" "+hmProPerformaceCurrency.get(alProjectId.get(i))+"',");
				billable_amount.append(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProPerformaceBillable.get(alProjectId.get(i))))+",");
				budgeted_cost.append(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProPerformaceBudget.get(alProjectId.get(i))))+",");
				actual_amount.append(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProPerformaceActual.get(alProjectId.get(i))))+",");
				
				projectCost.append("{'project':'"+hmProPerformaceProjectName.get(alProjectId.get(i)).replaceAll("[^a-zA-Z0-9]", "")+"', " +
						"'Billable Cost': "+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProPerformaceBillable.get(alProjectId.get(i))))+"," +
						"'Budgeted Cost': "+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProPerformaceBudget.get(alProjectId.get(i))))+"," +
						"'Actual Cost': "+uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmProPerformaceActual.get(alProjectId.get(i))))+"},");
			}
			
			if(pro_name.length()>1) {
				pro_name.replace(0, pro_name.length(), pro_name.substring(0, pro_name.length()-1));
				billable_amount.replace(0, billable_amount.length(), billable_amount.substring(0, billable_amount.length()-1));
				budgeted_cost.replace(0, budgeted_cost.length(), budgeted_cost.substring(0, budgeted_cost.length()-1));
				actual_amount.replace(0, actual_amount.length(), actual_amount.substring(0, actual_amount.length()-1));
				
				projectCost.replace(0, projectCost.length(), projectCost.substring(0, projectCost.length()-1));
			}
			
			request.setAttribute("hmProPerformaceBillable", hmProPerformaceBillable);
			request.setAttribute("hmProPerformaceActual", hmProPerformaceActual);
			request.setAttribute("hmProPerformaceBudget", hmProPerformaceBudget);
			request.setAttribute("hmProPerformaceProjectProfit", hmProPerformaceProjectProfit);
			request.setAttribute("hmProPerformaceProjectAmountIndicator", hmProPerformaceProjectAmountIndicator);
			request.setAttribute("hmProPerformaceProjectTimeIndicator", hmProPerformaceProjectTimeIndicator);
			
			request.setAttribute("hmProPerformaceActualTime", hmProPerformaceActualTime);
			request.setAttribute("hmProPerformaceIdealTime", hmProPerformaceIdealTime);
			
			request.setAttribute("hmProPerformaceProjectName", hmProPerformaceProjectName);
			request.setAttribute("hmProPerformaceCurrency", hmProPerformaceCurrency);
			request.setAttribute("hmProPerformaceProjectManager", hmProPerformaceProjectManager);
			
			request.setAttribute("hmProjectClient", hmProjectClient);
			request.setAttribute("alProjectId", alProjectId);
			
			request.setAttribute("pro_name",pro_name.toString());
			request.setAttribute("billable_amount",billable_amount.toString());
			request.setAttribute("budgeted_cost",budgeted_cost.toString());
			request.setAttribute("actual_amount",actual_amount.toString());
			
			request.setAttribute("projectCost",projectCost.toString());
			
			request.setAttribute("hmProOwner", hmProOwner);
			request.setAttribute("hmProActIdealTimeHRS", hmProActIdealTimeHRS);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String getProjectIdealTimeHRS(Connection con, UtilityFunctions uF, String proId, Map<String, String> hmProjectData) {
		PreparedStatement pst=null;
		ResultSet rs=null;
		String proIdealTimeHrs = null;
		try {
			
			pst = con.prepareStatement("select task_id, activity_name, resource_ids, idealtime, parent_task_id from activity_info where " +
					" parent_task_id = 0 and pro_id = ? ");
			pst.setInt(1, uF.parseToInt(proId));
//			System.out.println("pst======>"+pst); 
			rs=pst.executeQuery();
			Map<String, Map<String, String>> hmTaskData = new HashMap<String, Map<String,String>>();
			while(rs.next()) {
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put(rs.getString("task_id")+"_IDEAL_TIME", rs.getString("idealtime"));
				hmTaskData.put(rs.getString("task_id"), hmInner);
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmTaskData.keySet().iterator();
			double proBudgetedTime = 0;
//			System.out.println("billType ===>> " + billType);
			while (it.hasNext()) {
				String taskId = it.next();
				Map<String, String> hmInner = hmTaskData.get(taskId);

				if(hmProjectData != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("H")) {
					proBudgetedTime += uF.parseToDouble(hmInner.get(taskId+"_IDEAL_TIME"));
				} else if(hmProjectData != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("D")) {
					proBudgetedTime += (uF.parseToDouble(hmInner.get(taskId+"_IDEAL_TIME"))* 8);
				} else if(hmProjectData != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE") != null && hmProjectData.get("PRO_BILLING_ACTUAL_TYPE").equals("M")) {
					proBudgetedTime += ((uF.parseToDouble(hmInner.get(taskId+"_IDEAL_TIME"))* 8) * 30);
				}
//				System.out.println(taskId + "  taskResourceCnt ===>> " + taskResourceCnt + "  taskResourceCost ====>> " + taskResourceCost +" IDEAL_TIME =>>>>> " + hmInner.get(taskId+"_IDEAL_TIME"));
			}
			proIdealTimeHrs = proBudgetedTime+"";
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proIdealTimeHrs;
	}
	
	
	public String getProjectActualTimeHRS(Connection con, CommonFunctions CF, UtilityFunctions uF, String proId, Map<String, String> hmProjectData, boolean isSubmit, boolean isApprove) {
		PreparedStatement pst=null;
		ResultSet rs=null;
		String proActualTimeHrs = null;
		try {
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(a1.hrs) actual_hrs, sum(a1.days) actual_days, a1.emp_id from (select sum(ta.actual_hrs) hrs, " +
				"count(distinct ta.task_date) days, ta.emp_id, ta.activity_id from task_activity ta where task_date between ? and ? ");
			if(isSubmit && isApprove) {
				sbQuery.append(" and (is_approved = 1 or is_approved = 2)");
			} else if(isSubmit) {
				sbQuery.append(" and is_approved = 1 ");
			} else if(isApprove) {
				sbQuery.append(" and is_approved = 2 ");
			}
			sbQuery.append(" group by ta.activity_id, ta.emp_id) as a1, activity_info ai where ai.task_id = a1.activity_id and ai.pro_id = ? " +
				"group by a1.emp_id");
			
			pst = con.prepareStatement(sbQuery.toString()); // and (is_approved = 1 or is_approved = 2) 
			pst.setDate(1, uF.getDateFormat(hmProjectData.get("PRO_START_DATE"), DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(hmProjectData.get("PRO_END_DATE"), DATE_FORMAT));
			pst.setInt(3, uF.parseToInt(proId));
//			System.out.println("pst======> " + pst);
			rs=pst.executeQuery();
			Map<String, String> hmResourceActualTime = new HashMap<String,String>();
			while(rs.next()) {
				hmResourceActualTime.put(rs.getString("emp_id"), rs.getString("actual_hrs"));
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmResourceActualTime.keySet().iterator();
			double proActualTime = 0;
			while (it.hasNext()) {
				String empId = it.next();
				String actualTime = hmResourceActualTime.get(empId);
				proActualTime += uF.parseToDouble(actualTime);
//				System.out.println(proId +"  empId ===>> " + empId + "  actualTime ===>>> " + actualTime + " taskResourceActualCost ===>> " + taskResourceActualCost);
			}
			proActualTimeHrs = ""+proActualTime;
//		System.out.println(proId + "   hmProActualAndBillableCost ===>> " + hmProActualAndBillableCost);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return proActualTimeHrs;
	}
	
	private String getConcateDataLoadMore(String data) {
		StringBuilder sb = new StringBuilder();
		List<String> dataList = Arrays.asList(data.split(","));
		for(int i=0;i<dataList.size();i++) {
			if(i==0) {
				sb.append("'"+dataList.get(i)+"'");
			} else {
				sb.append(",'"+dataList.get(i)+"'");
			}
		}
		return sb.toString();
	}
	
public String getContent(String data) {
		data=data.replace("'", "");
		return data;
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
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}
		
		alFilter.add("PROJECT_SERVICE");
		if(getF_project_service()!=null) {
			String strProjectService="";
			int k=0;
			for(int i=0;projectServiceList!=null && i<projectServiceList.size();i++) {
				for(int j=0;j<getF_project_service().length;j++) {
					if(getF_project_service()[j].equals(projectServiceList.get(i).getServiceId())) {
						if(k==0) {
							strProjectService=projectServiceList.get(i).getServiceName();
						} else {
							strProjectService+=", "+projectServiceList.get(i).getServiceName();
						}
						k++;
					}
				}
			}
			if(strProjectService!=null && !strProjectService.equals("")) {
				hmFilter.put("PROJECT_SERVICE", strProjectService);
			} else {
				hmFilter.put("PROJECT_SERVICE", "All Services");
			}
		} else {
			hmFilter.put("PROJECT_SERVICE", "All Services");
		}
		
		alFilter.add("CLIENT");
		if(getF_client()!=null) {
			String strClient="";
			int k=0;
			for(int i=0; clientList!=null && i<clientList.size();i++) {
				for(int j=0;j<getF_client().length;j++) {
					if(getF_client()[j].equals(clientList.get(i).getClientId())) {
						if(k==0) {
							strClient=clientList.get(i).getClientName();
						} else {
							strClient+=", "+clientList.get(i).getClientName();
						}
						k++;
					}
				}
			}
			if(strClient!=null && !strClient.equals("")) {
				hmFilter.put("CLIENT", strClient);
			} else {
				hmFilter.put("CLIENT", "All Clients");
			}
		} else {
			hmFilter.put("CLIENT", "All Clients");
		}

		
		if(getSelectOne()!= null && !getSelectOne().equals("")) {
			alFilter.add("PERIOD");
			
			String strSelectOne="";
			if(uF.parseToInt(getSelectOne()) == 1) {
				strSelectOne="From - To";
			} else if(uF.parseToInt(getSelectOne()) == 2) {
				strSelectOne="Financial Year";
			} else if(uF.parseToInt(getSelectOne()) == 3) {
				strSelectOne="Month";
			} else if(uF.parseToInt(getSelectOne()) == 4) {
				strSelectOne="Paycycle";
			}
			if(strSelectOne!=null && !strSelectOne.equals("")) {
				hmFilter.put("PERIOD", strSelectOne);
			}
			
		}
		
		if(uF.parseToInt(getSelectOne()) == 1) {
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		} else if(uF.parseToInt(getSelectOne()) == 2) {
			alFilter.add("FINANCIALYEAR");
			String[] strFinancialYears = null;
			if (getFinancialYear() != null) {
				strFinancialYears = getFinancialYear().split("-");
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				
				setStrStartDate(strFinancialYears[0]);
				setStrEndDate(strFinancialYears[1]);
			} else {
				strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				
				setStrStartDate(strFinancialYears[0]);
				setStrEndDate(strFinancialYears[1]);
			}
			hmFilter.put("FINANCIALYEAR", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		} else if(uF.parseToInt(getSelectOne()) == 3) {
			alFilter.add("MONTH");
			int nselectedMonth = uF.parseToInt(getStrMonth());
			String strMonth = uF.getMonth(nselectedMonth);
//			int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
			int nFYSMonth = 0;
			String[] strFinancialYears = null;
			if (getFinancialYear() != null) {
				strFinancialYears = getFinancialYear().split("-");
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
			} else {
				strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
			}
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			if(nselectedMonth>=nFYSMonth){
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "yyyy")));
			}else{
				cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, "yyyy")));
			}
			  
			int nMonthStart = cal.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
			
			setStrStartDate(nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
			setStrEndDate(nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
			
			hmFilter.put("MONTH", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()) + " - " + strMonth);
		} else if(uF.parseToInt(getSelectOne()) == 4) {
			alFilter.add("PAYCYCLE");
			String strPaycycle = "";
			String[] strPayCycleDates = null;
			if (getPaycycle() != null) {
				strPayCycleDates = getPaycycle().split("-");
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				
				strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
				setStrStartDate(strPayCycleDates[0]);
				setStrEndDate(strPayCycleDates[1]);
			} else {
				strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
				setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
				
				strPaycycle = "Pay Cycle "+ strPayCycleDates[2]+", ";
				setStrStartDate(strPayCycleDates[0]);
				setStrEndDate(strPayCycleDates[1]);
			}
			hmFilter.put("PAYCYCLE", strPaycycle + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}

		String selectedFilter=CF.getSelectedFilter1(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
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

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_project_service() {
		return f_project_service;
	}

	public void setF_project_service(String[] f_project_service) {
		this.f_project_service = f_project_service;
	}

	public String[] getF_client() {
		return f_client;
	}

	public void setF_client(String[] f_client) {
		this.f_client = f_client;
	}

	public String getSelectOne() {
		return selectOne;
	}

	public void setSelectOne(String selectOne) {
		this.selectOne = selectOne;
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

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}

	public String getPaycycle() {
		return paycycle;
	}

	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
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

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}

	public List<FillServices> getProjectServiceList() {
		return projectServiceList;
	}

	public void setProjectServiceList(List<FillServices> projectServiceList) {
		this.projectServiceList = projectServiceList;
	}

	public List<FillLevel> getLevelList() {
		return levelList;
	}

	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}

	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
	}

	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
	}

	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}

	public String getLoadMore() {
		return loadMore;
	}

	public void setLoadMore(String loadMore) {
		this.loadMore = loadMore;
	}

	public String getProject_service() {
		return project_service;
	}

	public void setProject_service(String project_service) {
		this.project_service = project_service;
	}

}
