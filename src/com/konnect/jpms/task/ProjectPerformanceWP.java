package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
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

public class ProjectPerformanceWP extends ActionSupport implements ServletRequestAware, IStatements {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4511523914830394900L;
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
	
	public String execute(){
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		request.setAttribute(PAGE, "/jsp/task/ProjectPerformanceWP.jsp");
		request.setAttribute(TITLE, "Project Performance");
		
		UtilityFunctions uF=new UtilityFunctions();
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		String strReqEmpId = (String)request.getParameter("empId");
		
		if(getF_org() == null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)){
			getProjectDetails(uF.parseToInt((String)session.getAttribute(EMPID)), uF, CF, 0);
		}else if(strUserType!=null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(HRMANAGER))){
			getProjectDetails(uF.parseToInt(strReqEmpId), uF, CF, 0);
		}
		
		loadOutstandingReport(uF);
		
		return SUCCESS;
	}

	public String loadOutstandingReport(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
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
			
			boolean isComplete = false;
			if(getProType() != null && getProType().equals("C")) {
				isComplete = true;
			}
			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", isComplete, null, null, uF);
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from projectmntnc pmc where pro_id > 0 ");
			if(getProType() == null || getProType().equals("") || getProType().equals("L")) {
				sbQuery.append(" and approve_status = 'n' ");
			} else if(getProType() != null && getProType().equals("C")) {
				sbQuery.append(" and approve_status = 'approved' ");
			}
			
			if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0) || (getF_service() != null && getF_service().length>0) || (getF_level() != null && getF_level().length>0)) {
				sbQuery.append(" and pmc.pro_id in (select pro_id from projectmntnc where pro_id>0 ");
			}
			
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and pmc.org_id in ("+uF.parseToInt(getF_org())+")");
			}
			if(getF_strWLocation() != null && getF_strWLocation().length>0) {
				sbQuery.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and pmc.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_project_service() != null && getF_project_service().length>0) {
				String services = uF.getConcateData(getF_project_service());
				sbQuery.append(" and pmc.service in ("+services+") ");
			}
			if(getF_client() != null && getF_client().length>0) {
				sbQuery.append(" and pmc.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			
			if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0) || (getF_service() != null && getF_service().length>0) || (getF_level() != null && getF_level().length>0)) {
				sbQuery.append(" ) ");
			}
			
			if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase(LABEL_FROM_DATE) && !getStrEndDate().equalsIgnoreCase(LABEL_TO_DATE)) {
				sbQuery.append(" and pmc.start_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}
			
			if(nManagerId > 0) {
				sbQuery.append(" and added_by = "+ nManagerId +" ");
			}
			sbQuery.append(" order by deadline desc "+((nLimit>0)?" limit "+nLimit:""));
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst======>"+pst);
			rs=pst.executeQuery();
			List<List<String>> alOuter=new ArrayList<List<String>>(); 
			StringBuilder budgeted_cost = new StringBuilder();
			StringBuilder billable_amount = new StringBuilder();
			StringBuilder actual_amount = new StringBuilder();
			StringBuilder pro_name = new StringBuilder();
			while(rs.next()) {
//				String strBudgetedCost = getProjectBudgetedCost(con, uF, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
//				Map<String, String> hmProActualAndBillableCost = getProjectActualCost(con, uF, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
//				hmProjectData.put("PRO_START_DATE", rs.getString("start_date"));
//				hmProjectData.put("PRO_END_DATE", rs.getString("deadline"));
//				hmProjectData.put("PRO_ACTUAL_BILL_TYPE", rs.getString("actual_calculation_type"));
//				hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
//				hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
				
				Map<String, String> hmProActualCostTimeAndBillCost = CF.getProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
				Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, rs.getString("pro_id"), hmProjectData);
				
				double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
				double dblBudgeted = uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedCost"));
				double dblBillable = 0;
				if(rs.getString("billing_type") != null && rs.getString("billing_type").equals("F")) {
					dblBillable = uF.parseToDouble(rs.getString("billing_amount"));
				} else {
					dblBillable = uF.parseToDouble(hmProActualCostTimeAndBillCost.get("proBillableCost"));
				}
				double dblActual = uF.parseToDouble(hmProActualCostTimeAndBillCost.get("proActualCost")) + dblReimbursement;
				
				double diff = 0;
						
				if(dblBillable>0){
					diff = ((dblBillable-dblActual)/dblBillable) * 100;
				}
				
				List<String> alInner=new ArrayList<String>();
				alInner.add(rs.getString("pro_id"));
				alInner.add(rs.getString("pro_name"));
				alInner.add(uF.formatIntoTwoDecimal(dblBudgeted));
				alInner.add(uF.formatIntoTwoDecimal(dblActual));
				alInner.add(uF.formatIntoTwoDecimal(dblBillable));
				
				alInner.add(uF.formatIntoTwoDecimal(diff)+"");
				
				
				if (dblActual>dblBudgeted && dblActual<dblBillable){
					/*alInner.add("<img src=\"images1/icons/re_submit.png\" width=\"17px\">");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#f7ee1d\"></i>");
					
				}else if(dblActual<dblBudgeted){
					/*alInner.add("<img src=\"images1/icons/approved.png\" width=\"17px\">");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#54aa0d\"></i>");
					
				}else if(dblActual>dblBillable){
					/*alInner.add("<img src=\"images1/icons/denied.png\" width=\"17px\">");*/
					alInner.add("<i class=\"fa fa-circle\" aria-hidden=\"true\" style=\"color:#e22d25\"></i>");
					
				}else{
					alInner.add("&nbsp;");
				}
				
				alOuter.add(alInner);
				
				pro_name.append("'"+rs.getString("pro_name")+"',");
				billable_amount.append(uF.formatIntoTwoDecimalWithOutComma(dblBillable)+",");
				budgeted_cost.append(uF.formatIntoTwoDecimalWithOutComma(dblBudgeted)+",");
				actual_amount.append(uF.formatIntoTwoDecimalWithOutComma(dblActual)+",");
				
			}
			rs.close();
			pst.close();
			
			if(pro_name.length()>1) {
				pro_name.replace(0, pro_name.length(), pro_name.substring(0, pro_name.length()-1));
				billable_amount.replace(0, billable_amount.length(), billable_amount.substring(0, billable_amount.length()-1));
				budgeted_cost.replace(0, budgeted_cost.length(), budgeted_cost.substring(0, budgeted_cost.length()-1));
				actual_amount.replace(0, actual_amount.length(), actual_amount.substring(0, actual_amount.length()-1));
			}
//			StringBuilder sb = new StringBuilder();
//			sb.append("{name: \'"+getContent("Billable Cost")+"\', data:[");
//				sb.append(billable_amount.toString());
//				sb.append("]},");
//			
//			sb.append("{name: \'"+getContent("Budgeted Cost")+"\', data:[");
//				sb.append(budgeted_cost.toString());
//				sb.append("]},");
//				
//			sb.append("{name: \'"+getContent("Actual Cost")+"\', data:[");
//				sb.append(actual_amount.toString());
//				sb.append("]}");
			
			request.setAttribute("alOuter",alOuter);
			request.setAttribute("pro_name",pro_name.toString());
//			request.setAttribute("pro_amount",sb.toString());
			request.setAttribute("billable_amount",billable_amount);
			request.setAttribute("budgeted_cost",budgeted_cost);
			request.setAttribute("actual_amount",actual_amount);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
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
			int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
			
			String[] strFinancialYears = null;
			if (getFinancialYear() != null) {
				strFinancialYears = getFinancialYear().split("-");
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
			} else {
				strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
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
	
//	public void getCompletedProjectDetails(int nManagerId, UtilityFunctions uF, CommonFunctions CF, int nLimit) {
//		Database db = new Database();
//		db.setRequest(request);
//		Connection con = null;
//		PreparedStatement pst=null;
//		ResultSet rs=null;
//		try {
//			con = db.makeConnection(con);
//			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, null, null, uF);
//			
//			if(nManagerId==0){
//				pst = con.prepareStatement("select * from projectmntnc pmntc where approve_status = 'approved' order by pmntc.deadline desc "+((nLimit>0)?" limit "+nLimit:""));
//			}else{
//				pst = con.prepareStatement("select * from projectmntnc pmntc where approve_status = 'approved' and added_by=? order by pmntc.deadline desc "+((nLimit>0)?" limit "+nLimit:""));
//				pst.setInt(1, nManagerId);
//			}
////			System.out.println("pst======>"+pst); 
//			rs=pst.executeQuery();
//			List<List<String>> alOuter=new ArrayList<List<String>>();
//			
//			StringBuilder budgeted_cost 	= new StringBuilder();
//			StringBuilder billable_amount 	= new StringBuilder();
//			StringBuilder actual_amount 	= new StringBuilder();
//			StringBuilder pro_name 			= new StringBuilder();
//			 
//			while(rs.next()) {
//				String strBudgetedCost = getProjectBudgetedCost(con, uF, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
//				Map<String, String> hmProActualAndBillableCost = getProjectActualCost(con, uF, rs.getString("pro_id"), rs.getString("actual_calculation_type"));
//				
//				double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
////				double dblBudgeted = uF.parseToDouble(rs.getString("budgeted_cost"));
//				double dblBudgeted = uF.parseToDouble(strBudgetedCost);
////				double dblBillable=(uF.parseToDouble(rs.getString("billable_amount"))+uF.parseToDouble(rs.getString("variable_cost")));
//				double dblBillable = 0;
//				if(rs.getString("billing_type") != null && rs.getString("billing_type").equals("F")) {
//					dblBillable = uF.parseToDouble(rs.getString("billing_amount"));
//				} else {
//					dblBillable = uF.parseToDouble(hmProActualAndBillableCost.get("proBillableCost"));
//				}
//				double dblActual = uF.parseToDouble(hmProActualAndBillableCost.get("proActualCost")) + dblReimbursement;
//				double diff = 0;
//						
//				if(dblBillable>0){
//					diff = ((dblBillable-dblActual)/dblBillable) * 100;
//				}
//				
//				List<String> alInner=new ArrayList<String>();
//				alInner.add(rs.getString("pro_id"));
//				alInner.add(rs.getString("pro_name"));
//				alInner.add(uF.formatIntoTwoDecimal(dblBudgeted));
//				alInner.add(uF.formatIntoTwoDecimal(dblActual));
//				alInner.add(uF.formatIntoTwoDecimal(dblBillable));
//				
//				alInner.add(uF.formatIntoTwoDecimal(diff)+"");
//				
//				
//				if (dblActual > dblBudgeted && dblActual < dblBillable) {
//					alInner.add("<img src=\"images1/icons/re_submit.png\" width=\"17px\">");
//				} else if(dblActual < dblBudgeted) {
//					alInner.add("<img src=\"images1/icons/approved.png\" width=\"17px\">");
//				} else if(dblActual > dblBillable) {
//					alInner.add("<img src=\"images1/icons/denied.png\" width=\"17px\">");
//				} else {
//					alInner.add("&nbsp;");
//				}
//
//				alOuter.add(alInner);
//				
//				pro_name.append("'"+rs.getString("pro_name")+"',");
//				billable_amount.append(dblBillable+",");
//				budgeted_cost.append(dblBudgeted+",");
//				actual_amount.append(dblActual+",");
//			}
//			rs.close();
//			pst.close();
//			
//			if(pro_name.length()>1){
//				pro_name.replace(0, pro_name.length(), pro_name.substring(0, pro_name.length()-1));
//				billable_amount.replace(0, billable_amount.length(), billable_amount.substring(0, billable_amount.length()-1));
//				budgeted_cost.replace(0, budgeted_cost.length(), budgeted_cost.substring(0, budgeted_cost.length()-1));
//				actual_amount.replace(0, actual_amount.length(), actual_amount.substring(0, actual_amount.length()-1));
//			}
//			
//			request.setAttribute("alOuter",alOuter);
//			request.setAttribute("pro_name",pro_name);
//			request.setAttribute("billable_amount",billable_amount);
//			request.setAttribute("budgeted_cost",budgeted_cost);
//			request.setAttribute("actual_amount",actual_amount);
//
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	
	
//	private Map<String, String> getProjectActualCost(Connection con, UtilityFunctions uF, String proId, String billType) {
//		PreparedStatement pst=null;
//		ResultSet rs=null;
//		Map<String, String> hmProActualAndBillableCost = new HashMap<String, String>();
//		try {
//			
//			pst = con.prepareStatement("select * from project_emp_details where pro_id=?");
//			pst.setInt(1, uF.parseToInt(proId));
//			rs=pst.executeQuery();
//			Map<String, String> hmProEmpRate = new HashMap<String, String>();
//			while(rs.next()) {
//				hmProEmpRate.put(rs.getString("emp_id")+"_PER_HOUR", rs.getString("emp_actual_rate_per_hour"));
//				hmProEmpRate.put(rs.getString("emp_id")+"_PER_DAY", rs.getString("emp_actual_rate_per_day"));
//				hmProEmpRate.put(rs.getString("emp_id")+"_RATE_PER_HOUR", rs.getString("emp_rate_per_hour"));
//				hmProEmpRate.put(rs.getString("emp_id")+"_RATE_PER_DAY", rs.getString("emp_rate_per_day"));
//			}
////			System.out.println(proId + "   hmProEmpRate ===>> " + hmProEmpRate);
//			rs.close();
//			pst.close();
//			
//			pst = con.prepareStatement("select sum(a1.hrs) actual_hrs, sum(a1.days) actual_days, a1.emp_id from (" +
//				"select sum(ta.actual_hrs) hrs, count(distinct ta.task_date) days, ta.emp_id, ta.activity_id from task_activity ta group by " +
//				"ta.activity_id, ta.emp_id) as a1, activity_info ai where ai.task_id = a1.activity_id and ai.pro_id = ? group by a1.emp_id ");
//			pst.setInt(1, uF.parseToInt(proId));
////			System.out.println("pst======>"+pst); 
//			rs=pst.executeQuery();
//			Map<String, String> hmResourceActualTime = new HashMap<String,String>();
//			while(rs.next()) {
//				if(billType != null && billType.equals("H")) {
//					hmResourceActualTime.put(rs.getString("emp_id"), rs.getString("actual_hrs"));
//				} else {
//					hmResourceActualTime.put(rs.getString("emp_id"), rs.getString("actual_days"));
//				}
//			}
////			System.out.println(proId + "   hmResourceActualTime ===>> " + hmResourceActualTime);
//			rs.close();
//			pst.close();
//			
//			Iterator<String> it = hmResourceActualTime.keySet().iterator();
//			double proActualCost = 0;
//			double proBillableCost = 0;
//			while (it.hasNext()) {
//				String empId = it.next();
//				String actualTime = hmResourceActualTime.get(empId);
//				double taskResourceActualCost = 0;
//				double taskResourceBillableCost = 0;
//				if(billType != null && billType.equals("H")) {
//					taskResourceActualCost = uF.parseToDouble(actualTime) * uF.parseToDouble(hmProEmpRate.get(empId+"_PER_HOUR"));
//					taskResourceBillableCost = uF.parseToDouble(actualTime) * uF.parseToDouble(hmProEmpRate.get(empId+"_RATE_PER_HOUR"));
//				} else {
//					taskResourceActualCost = uF.parseToDouble(actualTime) * uF.parseToDouble(hmProEmpRate.get(empId+"_PER_DAY"));
//					taskResourceBillableCost = uF.parseToDouble(actualTime) * uF.parseToDouble(hmProEmpRate.get(empId+"_RATE_PER_DAY"));
//				}
//				proActualCost += taskResourceActualCost;
//				proBillableCost += taskResourceBillableCost;
////				System.out.println("taskResourceActualCost ===>> " + taskResourceActualCost);
//			}
//			hmProActualAndBillableCost.put("proActualCost", ""+proActualCost);
//			hmProActualAndBillableCost.put("proBillableCost", ""+proBillableCost);
////		System.out.println("hmProActualAndBillableCost ===>> " + hmProActualAndBillableCost);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return hmProActualAndBillableCost;
//	}


//	private String getProjectBudgetedCost(Connection con, UtilityFunctions uF, String proId, String billType) {
//		PreparedStatement pst=null;
//		ResultSet rs=null;
//		String strBudgetedCost = "";
//		try {
//			pst = con.prepareStatement("select * from project_emp_details where pro_id=?");
//			pst.setInt(1, uF.parseToInt(proId));
////			System.out.println("pst======>"+pst); 
//			rs=pst.executeQuery();
//			Map<String, String> hmProEmpRate = new HashMap<String, String>();
//			while(rs.next()) {
//				hmProEmpRate.put(rs.getString("emp_id")+"_PER_HOUR", rs.getString("emp_actual_rate_per_hour"));
//				hmProEmpRate.put(rs.getString("emp_id")+"_PER_DAY", rs.getString("emp_actual_rate_per_day"));
//			}
//			rs.close();
//			pst.close();
//			
//			double proVariableCost = CF.getProjectVariableCost(con, uF, proId);
//			/*pst = con.prepareStatement("select sum(variable_cost) as variable_cost from variable_cost where pro_id=?");
//			pst.setInt(1, uF.parseToInt(proId));
//			rs=pst.executeQuery();
//			while(rs.next()) {
//				proVariableCost = rs.getDouble("variable_cost");
//			}
//			rs.close();
//			pst.close();*/
//			
//			pst = con.prepareStatement("select task_id, activity_name, resource_ids, idealtime, parent_task_id from activity_info where " +
//				"task_id not in (select parent_task_id from activity_info where parent_task_id is not null) and (parent_task_id in (" +
//				"select task_id from activity_info) or parent_task_id = 0) and pro_id = ? ");
//			pst.setInt(1, uF.parseToInt(proId));
////			System.out.println("pst======>"+pst); 
//			rs=pst.executeQuery();
//			Map<String, Map<String, String>> hmTaskData = new HashMap<String, Map<String,String>>();
//			while(rs.next()) {
//				Map<String, String> hmInner = new HashMap<String, String>();
//				hmInner.put(rs.getString("task_id")+"_IDEAL_TIME", rs.getString("idealtime"));
//				hmInner.put(rs.getString("task_id")+"_RESOURCES", rs.getString("resource_ids"));
//				hmTaskData.put(rs.getString("task_id"), hmInner);
//			}
////			System.out.println("hmTaskData ===>> " + hmTaskData);
//			
//			rs.close();
//			pst.close();
//			
//			Iterator<String> it = hmTaskData.keySet().iterator();
//			double proBudgetedCost = 0;
////			System.out.println("billType ===>> " + billType);
//			while (it.hasNext()) {
//				String taskId = it.next();
//				Map<String, String> hmInner = hmTaskData.get(taskId);
//				List<String> alResources = new ArrayList<String>();
//				if(hmInner.get(taskId+"_RESOURCES") != null ) {
//					alResources = Arrays.asList(hmInner.get(taskId+"_RESOURCES").split(","));
//				}
////				System.out.println(taskId + "  -- alResources ===>> " + alResources);
////				System.out.println(taskId + "  -- hmProEmpRate ===>> " + hmProEmpRate);
//				int taskResourceCnt = 0;
//				double taskResourceCost = 0;
//				for(int i=0; alResources!=null && !alResources.isEmpty() && i<alResources.size(); i++) {
//					if(alResources.get(i) != null && !alResources.get(i).equals("")) {
////						System.out.println(taskId + "  -- alResources.get(i) ===>> " + alResources.get(i) + "  billType ===>> " + billType);
//						if(billType != null && billType.equals("H")) {
//							taskResourceCost += uF.parseToDouble(hmProEmpRate.get(alResources.get(i)+"_PER_HOUR"));
//						} else {
//							taskResourceCost += uF.parseToDouble(hmProEmpRate.get(alResources.get(i)+"_PER_DAY"));
//						}
//						taskResourceCnt++;
//					}
//				}
////				System.out.println(taskId + "  taskResourceCnt ===>> " + taskResourceCnt + "  taskResourceCost ====>> " + taskResourceCost +" IDEAL_TIME =>>>>> " + hmInner.get(taskId+"_IDEAL_TIME"));
//				double taskAvgResourceCost = 0;
//				if(taskResourceCnt > 0) {
//					taskAvgResourceCost = taskResourceCost / taskResourceCnt;
//				}
//				double taskBudgetedCost = taskAvgResourceCost * uF.parseToDouble(hmInner.get(taskId+"_IDEAL_TIME"));
////				System.out.println(taskId + "  taskBudgetedCost ===>> " + taskBudgetedCost);
//				proBudgetedCost += taskBudgetedCost;
//			}
//			proBudgetedCost += proVariableCost;
////		System.out.println("proBudgetedCost ===>> " + proBudgetedCost);
//		strBudgetedCost = proBudgetedCost+"";
//		
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return strBudgetedCost;
//	}
	
	
	public String getProType() {
		return proType;
	}

	public void setProType(String proType) {
		this.proType = proType;
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

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request=request;
		// TODO Auto-generated method stub
		
	}

}
