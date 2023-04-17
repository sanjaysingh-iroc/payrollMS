package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

public class ProfitabilityByIndustry extends ActionSupport implements ServletRequestAware, IStatements{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpServletRequest request;
	HttpSession session;
	CommonFunctions CF;
	String strSessionEmpId;
	String strUserType;
	
	String strType;
	
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

	String strProType;
	boolean poFlag;
	
	String btnSubmit;
	
	public String execute() {
		
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;

		strSessionEmpId = (String)session.getAttribute(EMPID);
		strUserType = (String)session.getAttribute(BASEUSERTYPE);
		UtilityFunctions uF = new UtilityFunctions();
		
		boolean isView = CF.getAccess(session, request, uF); 
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied); 
			return ACCESS_DENIED; 
		}
		request.setAttribute(PAGE, "/jsp/task/ProfitabilityByIndustry.jsp");
//		request.setAttribute(TITLE, "Profitability By Industry");
		
		if(getF_org() == null){
			setF_org((String)session.getAttribute(ORGID));
		}
		checkProjectOwner(uF);
		loadOutstandingReport(uF);
		
//		getProjectDetails(0, uF, CF, 0);
		
//		System.out.println("getStrStartDate() ===>>> " + getStrStartDate());
		
		if(getStrType() == null || getStrType().trim().equals("")){
//			setStrType("I");
			setStrType("S");//Created by dattatray Date:04-10-21
		}
		
		if(getStrType() != null && strType.equals("I")) {
			getProjectDetailsByIndustry();
			request.setAttribute(TITLE, "Profitability By Industry");
			
		} else if(strType != null && strType.equals("S")) {
			getProjectDetailsByService();
			request.setAttribute(TITLE, "Profitability By Service");
			
		} else if(strType != null && strType.equals("WL")) {
			getProjectDetailsByWorkLocation();
			request.setAttribute(TITLE, "Profitability By Work Location");
			
		} else if(strType != null && strType.equals("O")) {
			getProjectDetailsByOrg();
			request.setAttribute(TITLE, "Profitability By Organization");
			
		} else if(strType != null && strType.equals("D")) {
			getProjectDetailsByDepart();
			request.setAttribute(TITLE, "Profitability By Department");
			
		} else if(strType != null && strType.equals("C")) {
			getProjectDetailsByClient();
			request.setAttribute(TITLE, "Profitability By Client");
			
		} else if(strType != null && strType.equals("P")) {
			getProjectDetailsByProject();
			request.setAttribute(TITLE, "Profitability By Project");
			
		} else {
			getProjectDetailsByIndustry();
			request.setAttribute(TITLE, "Profitability By Industry");
		}
		
//		if(getBtnSubmit() != null) {
//			return SUCCESS;
//		} else {
			return LOAD;
//		}
	}
	
	private void checkProjectOwner(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		boolean poFlag = false;
		try{
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 14-10-2022===	
//			sbQuery.append("select * from projectmntnc pmc where project_owner=?");
			sbQuery.append("select * from projectmntnc pmc where project_owners like '%,"+strSessionEmpId+",%'");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strSessionEmpId));
		//===end parvez date: 14-10-2022===	
			rs = pst.executeQuery();
			if(rs.next()) {
				poFlag = true;
			}
			rs.close();
			pst.close();
			
			setPoFlag(poFlag);
//			"poFlag ===>> " + poFlag);
//			System.out.println("getStrProType() ===>> " + getStrProType());
			if(poFlag && uF.parseToInt(getStrProType()) == 0) {
				setStrProType("2");
			}
//			System.out.println("getStrProType() after ===>> " + getStrProType());
		} catch (Exception e) {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public String loadOutstandingReport(UtilityFunctions uF) {
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)) {
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		} else {
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		projectServiceList = new FillServices(request).fillProjectServices();
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		clientList = new FillClients(request).fillAllClients(false);
//		System.out.println("getStrStartDate() -- ===>>> " + getStrStartDate());
		
		getSelectedFilter(uF);
		
		return SUCCESS;
	}
	
	
	public void getProjectDetailsByIndustry() {

			UtilityFunctions uF=new UtilityFunctions();
			Database db = new Database();
			db.setRequest(request);
			Connection con = null;
			PreparedStatement pst = null;
			ResultSet rs = null;
			try {
				con = db.makeConnection(con);
				
				pst = con.prepareStatement("select * from client_industry_details");
				rs=pst.executeQuery();

				Map<String, String> hmIndustryDetailsMap = new HashMap<String, String>();
				while(rs.next()) {
					hmIndustryDetailsMap.put(rs.getString("industry_id"), rs.getString("industry_name"));
				}
				rs.close();
				pst.close();
				
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select * from client_details cd, projectmntnc pmc where pmc.client_id = cd.client_id and pmc.approve_status = 'approved' ");
				if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
				//===start parvez date: 14-10-2022===	
//					sbQuery.append(" and pmc.project_owner="+uF.parseToInt(strSessionEmpId));
					sbQuery.append(" and pmc.project_owners like '%,"+strSessionEmpId+",%'");
				//===end parvez date: 14-10-2022===	
				}
				if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and pmc.org_id in ("+uF.parseToInt(getF_org())+")");
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
						sbQuery.append(" and pmc.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
				if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
		            sbQuery.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
					sbQuery.append(" and pmc.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
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
				
				if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase("") && !getStrEndDate().equalsIgnoreCase("")) {
					sbQuery.append(" and pmc.start_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
				}
				
				if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
					sbQuery.append(" and pmc.added_by = "+uF.parseToInt((String)session.getAttribute(EMPID)));
				}
				sbQuery.append(" order by cd.client_id");
//				sbQuery.append(" group by pmntc.client_id) as d,client_details cd where d.client_id=cd.client_id");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===> " + pst);
				rs=pst.executeQuery();
				List<List<String>> alOuter = new ArrayList<List<String>>();
				
				Map<String, List<String>> hmIndustry = new HashMap<String, List<String>>(); 
				List<String> alProIdIndWise = new ArrayList<String>();
				List<String> alProId = new ArrayList<String>();
				while(rs.next()) {
					
					String arrClientIndustry[] = null;
					if(rs.getString("client_industry") != null && !rs.getString("client_industry").trim().equals("")) {
						arrClientIndustry = rs.getString("client_industry").split(",");
						if(arrClientIndustry != null && arrClientIndustry.length > 0 && uF.parseToInt(arrClientIndustry[0].trim())>0){
							alProIdIndWise = hmIndustry.get(arrClientIndustry[0]);
							if(alProIdIndWise == null) alProIdIndWise = new ArrayList<String>();
							alProIdIndWise.add(rs.getString("pro_id"));
							
							hmIndustry.put(arrClientIndustry[0], alProIdIndWise);
							
							if(!alProId.contains(rs.getString("pro_id"))){
								alProId.add(rs.getString("pro_id"));
							}
						}
					}
				}
				rs.close();
				pst.close();
				
//				System.out.println("alIndustry ===>> " + alIndustry);
//				System.out.println("hmIndustry ===>> " + hmIndustry);
				String[] strArray = (String[]) alProId.toArray(new String[0]);
				getProjectDetails(con, uF, StringUtils.join(strArray, ','));

				Map<String, String> hmProPerformaceBillable = (Map<String, String>)request.getAttribute("hmProPerformaceBillable");
				Map<String, String> hmProPerformaceActual = (Map<String, String>)request.getAttribute("hmProPerformaceActual");
				
				StringBuilder sbName = new StringBuilder();
				StringBuilder sbActual = new StringBuilder();
				StringBuilder sbBilled = new StringBuilder();
				StringBuilder sbProfit = new StringBuilder();
				StringBuilder sbProfitC = new StringBuilder();
				
				StringBuilder sbProfitChart = new StringBuilder();
				sbProfitChart.append("{'Profitability': 'Profitability',");
				List<String> alProfit = new ArrayList<String>();
				int x = 1;
				
				Iterator<String> it = hmIndustry.keySet().iterator();
				while (it.hasNext()) {
					String indId = it.next();
					List<String> proIdList = hmIndustry.get(indId);
					double billedAmount = 0;
					double actualCost = 0;
					double profitAmount = 0;
					double profitPercent = 0;
					
					for (int i = 0; proIdList != null && i < proIdList.size(); i++) {
						billedAmount += uF.parseToDouble(hmProPerformaceBillable.get(proIdList.get(i)));
						actualCost += uF.parseToDouble(hmProPerformaceActual.get(proIdList.get(i)));
					}
					
					profitAmount = billedAmount - actualCost;
					if(billedAmount > 0) {
						profitPercent = (profitAmount / billedAmount) * 100;
					} else {
						profitPercent = profitAmount * 100;
					}
					
					List<String> alInner = new ArrayList<String>();
					alInner.add(indId);
					alInner.add(uF.showData(hmIndustryDetailsMap.get(indId), "N/A"));
					alInner.add(uF.formatIntoTwoDecimal(billedAmount));
					alInner.add(uF.formatIntoTwoDecimal(actualCost));
					alInner.add(uF.formatIntoTwoDecimal(profitAmount));
					alInner.add(uF.formatIntoOneDecimal(profitPercent)+"%");
					
					alOuter.add(alInner);
					
					sbName.append("'"+uF.showData(hmIndustryDetailsMap.get(indId), "N/A")+"'"+",");
					sbBilled.append(uF.formatIntoTwoDecimalWithOutComma(billedAmount)+",");
					sbActual.append(uF.formatIntoTwoDecimalWithOutComma(actualCost)+",");
					sbProfit.append(uF.formatIntoOneDecimalWithOutComma(profitPercent)+",");
					
					String industryName = uF.showData(hmIndustryDetailsMap.get(indId), "N/A");
					industryName = industryName.replace("'", "\\\'");
					sbProfitC.append("{name: '"+industryName+"',data: ["+uF.formatIntoTwoDecimalWithOutComma(profitAmount)+"]}"+",");
					
					sbProfitChart.append("'"+industryName+"': "+uF.formatIntoTwoDecimalWithOutComma(profitAmount)+",");
					StringBuilder sbGraph = new StringBuilder();
					sbGraph.append("var graph"+x+" = new AmCharts.AmGraph();" +
									"graph"+x+".type = \"column\";" +
									"graph"+x+".title = \""+industryName+"\";" +
									"graph"+x+".valueField = \""+industryName+"\";" +
									"graph"+x+".balloonText = \""+industryName+":[[value]]\";" +
									"graph"+x+".lineAlpha = 0;" +
									"graph"+x+".fillAlphas = 1;" +
									"chart.addGraph(graph"+x+");");
					alProfit.add(sbGraph.toString());
					x++;
				}
				if(sbName.length()>1) {
					sbName.replace(0, sbName.length(), sbName.substring(0, sbName.length()-1));
					sbBilled.replace(0, sbBilled.length(), sbBilled.substring(0, sbBilled.length()-1));
					sbActual.replace(0, sbActual.length(), sbActual.substring(0, sbActual.length()-1));
					sbProfit.replace(0, sbProfit.length(), sbProfit.substring(0, sbProfit.length()-1));
					sbProfitC.replace(0, sbProfitC.length(), sbProfitC.substring(0, sbProfitC.length()-1));
					
					sbProfitChart.replace(0, sbProfitChart.length(), sbProfitChart.substring(0, sbProfitChart.length()-1));
				}
				sbProfitChart.append("}");
				request.setAttribute("sbProfitChart", sbProfitChart.toString());
				request.setAttribute("alProfit", alProfit);
				
				request.setAttribute("sbName", sbName.toString());
				request.setAttribute("sbBilled", sbBilled.toString());
				request.setAttribute("sbActual", sbActual.toString());
				request.setAttribute("sbProfit", sbProfit.toString());
				request.setAttribute("sbProfitC", sbProfitC.toString());
				
				request.setAttribute("alOuter", alOuter);
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				db.closeResultSet(rs);
				db.closeStatements(pst);
				db.closeConnection(con);
			}
	}
	
	public void getProjectDetails(Connection con, UtilityFunctions uF, String strProIds) {
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {
			
			if(strProIds!=null && !strProIds.trim().equals("")){
				
				Map<String, String> hmEmpCodeName = CF.getEmpNameMap(con, null, null);
				Map<String, String> hmPprojectClientMap = CF.getProjectClientMap(con, uF);
				if(hmPprojectClientMap == null) hmPprojectClientMap = new HashMap<String, String>();
				Map<String, String> hmCustName = CF.getCustomerNameMap(con);
				if(hmCustName == null) hmCustName = new HashMap<String, String>();
				Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
				Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
				
//				Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, getStrStartDate(), getStrEndDate(), uF);
				Map<String, String> hmReimbursementAmountMap = new HashMap<String, String>();
				StringBuilder sbQuery = new StringBuilder();
				sbQuery.append("select sum(reimbursement_amount) as reimbursement_amount, pro_id as group_type from emp_reimbursement where approval_2 = 1 " +
						"and pro_id in (select pmc.pro_id from projectmntnc pmc, activity_info ai where ai.pro_id = pmc.pro_id ");
					sbQuery.append(" and pmc.approve_status = 'approved'");
				if (getStrStartDate() != null && getStrEndDate() != null && !getStrStartDate().equalsIgnoreCase("") && !getStrEndDate().equalsIgnoreCase("")) {
					sbQuery.append(" and from_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"
							+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
				}
				sbQuery.append(" ) and pro_id in("+strProIds+") and reimbursement_type1 = 'P' group by pro_id");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst===>"+pst);  
				rs = pst.executeQuery();
				while (rs.next()) {
					String arr[] = null;
					if (rs.getString("group_type") != null) {
						arr = rs.getString("group_type").split(",");
						hmReimbursementAmountMap.put(arr[0], rs.getString("reimbursement_amount"));
					}
				}
				rs.close();
				pst.close();
				
				pst = con.prepareStatement("select * from project_emp_details where pro_id>0 and pro_id in("+strProIds+") order by pro_id");
				rs=pst.executeQuery();
				Map<String, String> hmProEmpRate = new HashMap<String, String>();
				while(rs.next()) {
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_HOUR", rs.getString("emp_actual_rate_per_hour"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_DAY", rs.getString("emp_actual_rate_per_day"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_PER_MONTH", rs.getString("emp_actual_rate_per_month"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_HOUR", rs.getString("emp_rate_per_hour"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_DAY", rs.getString("emp_rate_per_day"));
					hmProEmpRate.put(rs.getString("pro_id")+"_"+rs.getString("emp_id")+"_RATE_PER_MONTH", rs.getString("emp_rate_per_month"));
					
				}
//				System.out.println("hmProEmpRate ===>> " + hmProEmpRate);
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select org_id, calculation_type, days from cost_calculation_settings ");
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("pst ===> " + pst);
				rs = pst.executeQuery();
				Map<String, String> hmOrgCalType = new HashMap<String, String>();
				while (rs.next()) {
					hmOrgCalType.put(rs.getString("org_id"), rs.getString("calculation_type"));
					hmOrgCalType.put(rs.getString("org_id")+"_DAYS", rs.getString("days"));
				}
				
				
				sbQuery = new StringBuilder();
//				sbQuery.append("select pro_id, pro_name, billing_type, billing_amount, idealtime, start_date, deadline, client_id, actual_calculation_type, " +
//				"bill_days_type, hours_for_bill_day, added_by from projectmntnc pmc where pmc.pro_id > 0 and pmc.approve_status = 'approved' " +
//				"and pmc.pro_id in("+strProIds+") ");
				sbQuery.append("select * from projectmntnc pmc where pmc.pro_id > 0 and pmc.approve_status = 'approved' " +
						"and pmc.pro_id in("+strProIds+") ");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
//				System.out.println("pst==>" + pst);
				double dblBugedtedAmt = 0;
				double dblActualAmt = 0;
				double dblBillableAmt = 0;
	//			double dblBugedtedTime = 0;
				double dblActualTime = 0;
				
				Map<String, String> hmProPerformaceBudget = new HashMap<String, String>();
				Map<String, String> hmProPerformaceActual = new HashMap<String, String>();
				Map<String, String> hmProPerformaceBillable = new HashMap<String, String>();
				
				Map<String, String> hmProPerformaceProjectProfit = new HashMap<String, String>();
				
				Map<String, String> hmProName = new HashMap<String, String>();
				Map<String, Map<String, String>> hmAlProData = new HashMap<String, Map<String,String>>();
				while(rs.next()) {
					
//					Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
					Map<String, String> hmProjectData = new HashMap<String, String>();
					hmProjectData.put("PRO_ID", rs.getString("pro_id"));
					hmProjectData.put("PRO_NAME", rs.getString("pro_name"));
					hmProjectData.put("PRO_CUSTOMER_NAME", uF.showData(hmPprojectClientMap.get(rs.getString("client_id")), ""));
					hmProjectData.put("PRO_CUST_SPOC_ID", rs.getString("poc"));
					hmProjectData.put("PRO_CUST_SPOC_NAME", uF.showData(hmCustName.get(rs.getString("poc")), ""));
					hmProjectData.put("PRO_OWNER_ID", rs.getString("project_owner"));
					hmProjectData.put("PRO_OWNER_NAME", uF.showData(hmEmpCodeName.get(rs.getString("project_owner")), ""));
					hmProjectData.put("PRO_BILLING_TYPE", CF.getBillinType(rs.getString("billing_type")));
					hmProjectData.put("PRO_BILL_TYPE", rs.getString("billing_type"));
					hmProjectData.put("PRO_BILLING_ACTUAL_TYPE", rs.getString("actual_calculation_type"));
					hmProjectData.put("PRO_BILLING_FREQUENCY", CF.getBillinFreq(rs.getString("billing_kind"), rs.getString("billing_type")));
					hmProjectData.put("PRO_BILL_FREQUENCY", rs.getString("billing_kind"));
					hmProjectData.put("PRO_START_DATE", uF.getDateFormat(rs.getString("start_date"), DBDATE, DATE_FORMAT));
					hmProjectData.put("PRO_END_DATE", uF.getDateFormat(rs.getString("deadline"), DBDATE, DATE_FORMAT));
					hmProjectData.put("PRO_BILL_DAYS_TYPE", rs.getString("bill_days_type"));
					hmProjectData.put("PRO_HOURS_FOR_BILL_DAY", rs.getString("hours_for_bill_day"));
					hmProjectData.put("PRO_SERVICE_ID", rs.getString("service"));
					hmProjectData.put("PRO_REPORT_CURR_ID", rs.getString("curr_id"));
					hmProjectData.put("PRO_BILLING_CURR_ID", rs.getString("billing_curr_id"));
					hmProjectData.put("PRO_BILLING_AMOUNT", rs.getString("billing_amount"));
					hmProjectData.put("PRO_ORG_ID", rs.getString("org_id"));
					
			//===start parvez date: 05-03-2022===		
					hmAlProData.put(rs.getString("pro_id"), hmProjectData);
			//===end parvez date: 05-03-2022===		
					/*Map<String, String> hmProActualCostTime = new HashMap<String, String>(); 
					Map<String, String> hmProBillCost = new HashMap<String, String>();
					if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equals("M")) {
//						hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData);
						hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation,hmOrgCalType);
//						hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData);
						hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con,request, CF, uF, rs.getString("pro_id"), hmProjectData,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation,hmOrgCalType);
						System.out.println("if====>");
					} else {
						System.out.println("else====>");
//						hmProActualCostTime = CF.getProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
						hmProActualCostTime = CF.getProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation); //,hmOrgCalType
//						hmProBillCost = CF.getProjectBillableCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
						hmProBillCost = CF.getProjectBillableCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation); //,hmOrgCalType
					}
//					Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, rs.getString("pro_id"), hmProjectData);
					Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con,CF, uF, rs.getString("pro_id"), hmProjectData,hmProEmpRate);
					
					double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
					 
					hmProName.put(rs.getString("pro_id"), rs.getString("pro_name"));
					
					if("F".equalsIgnoreCase(rs.getString("billing_type"))) { 
						 dblBillableAmt = uF.parseToDouble(rs.getString("billing_amount"));
					} else {
						 dblBillableAmt = uF.parseToDouble(hmProBillCost.get("proBillableCost"));
					}
					 
					 dblBugedtedAmt = uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedCost"));
					 dblActualAmt = uF.parseToDouble(hmProActualCostTime.get("proActualCost"));
					 
					 hmProPerformaceBudget.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(dblBugedtedAmt));
					 hmProPerformaceActual.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(dblActualAmt + dblReimbursement));
					 hmProPerformaceBillable.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(dblBillableAmt));
					 
					 double diff = 0;
					 if(dblBillableAmt > 0) {
						 diff = ((dblBillableAmt-(dblActualAmt + dblReimbursement))/dblBillableAmt) * 100;
					 }
					 hmProPerformaceProjectProfit.put(rs.getString("pro_id"), Math.round(diff)+"%");
					*/	
				}
				rs.close();
				pst.close();
				
				//===start Parvez date: 05-03-2022===
				sbQuery = new StringBuilder();
				sbQuery.append("select ai.pro_id,sum(a1.hrs) actual_hrs, sum(a1.days) actual_days, a1.emp_id from ("
						+ "select sum(ta.actual_hrs) hrs, count(distinct ta.task_date) days, ta.emp_id, ta.activity_id from task_activity ta "
						+ " group by ta.activity_id, ta.emp_id) as a1, activity_info ai where ai.task_id = a1.activity_id "
						+ "and ai.pro_id in("+strProIds+") group by a1.emp_id,ai.pro_id ");
				pst = con.prepareStatement(sbQuery.toString()); 
				rs = pst.executeQuery();
//				System.out.println("PBI/576--pst=="+pst);
				Map<String, String> hmEmpTaskDetails = new HashMap<String, String>();
				Map<String,List<String>> hmProwiseEmpIds = new HashMap<String, List<String>>();
				while (rs.next()) {
					double dblActualHrs = uF.parseToDouble(rs.getString("actual_hrs"))+uF.parseToDouble(hmEmpTaskDetails.get(rs.getString("pro_id")+rs.getString("emp_id")+"_HRS"));
					hmEmpTaskDetails.put(rs.getString("pro_id")+rs.getString("emp_id")+"_HRS", dblActualHrs+"");
					double dblActualDays = uF.parseToDouble(rs.getString("actual_days"))+uF.parseToDouble(hmEmpTaskDetails.get(rs.getString("pro_id")+rs.getString("emp_id")+"_DAYS"));
					hmEmpTaskDetails.put(rs.getString("pro_id")+rs.getString("emp_id")+"_DAYS", dblActualDays+"");
					List<String> alInner = hmProwiseEmpIds.get(rs.getString("pro_id"));
					if(alInner==null) alInner = new ArrayList<String>();
					alInner.add(rs.getString("emp_id"));
					hmProwiseEmpIds.put(rs.getString("pro_id"), alInner);
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select task_id, activity_name,pro_id, resource_ids, idealtime, parent_task_id from activity_info where "
						+ " parent_task_id = 0 and pro_id in ( "+strProIds+" )");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				Map<String, Map<String, Map<String, String>>> hmProjectWiseTaskData = new HashMap<String, Map<String, Map<String, String>>>();
				Map<String, Map<String,String>> hmInnerTaskData = new HashMap<String, Map<String,String>>();
				while (rs.next()) {
					Map<String,String> hmInner = new HashMap<String, String>();
					
					hmInner.put(rs.getString("task_id")+"_IDEAL_TIME", rs.getString("idealtime"));
					hmInner.put(rs.getString("task_id")+"_RESOURCES", rs.getString("resource_ids"));
					hmInnerTaskData.put(rs.getString("task_id"), hmInner);
					hmProjectWiseTaskData.put(rs.getString("pro_id"),hmInnerTaskData);
				}
				rs.close();
				pst.close();
				
				sbQuery = new StringBuilder();
				sbQuery.append("select pro_id, variable_cost from variable_cost where pro_id in ("+strProIds+" )");
				pst = con.prepareStatement(sbQuery.toString());
				rs = pst.executeQuery();
				Map<String, String> hmProVaribaleCost = new HashMap<String, String>();
				while (rs.next()) {
					double proVariableCost = rs.getDouble("variable_cost")+uF.parseToDouble(hmProVaribaleCost.get(rs.getString("pro_id")));
					hmProVaribaleCost.put(rs.getString("pro_id"), proVariableCost+"");
				}
				rs.close();
				pst.close();
				
//				System.out.println("PBI/622--hmAlProData.size=="+hmAlProData.size());
				Map<String, String> hmProjectData1 = new HashMap<String,String>();
				Iterator<String> it = hmAlProData.keySet().iterator();
				int cnt1 = 1;
				while (it.hasNext()) {
					String proId = it.next();
					hmProjectData1 = hmAlProData.get(proId);
					Map<String, String> hmProActualCostTime = new HashMap<String, String>(); 
					Map<String, String> hmProBillCost = new HashMap<String, String>();
					
					List<String> alEmpId = hmProwiseEmpIds.get(proId);
					
					Map<String, Map<String, String>> hmTaskData = hmProjectWiseTaskData.get(proId);
					
					
					if(hmProjectData1.get("PRO_BILLING_ACTUAL_TYPE")!=null && hmProjectData1.get("PRO_BILLING_ACTUAL_TYPE").equals("M")) {
						hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime1(con,request, CF, uF, proId, hmProjectData1,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation,hmOrgCalType,alEmpId,hmEmpTaskDetails);
						hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con,request, CF, uF, proId, hmProjectData1,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation,hmOrgCalType);
					} else {
						hmProActualCostTime = CF.getProjectActualCostAndTime1(con, CF, uF, proId, hmProjectData1, hmProEmpRate, hmEmpLevelMap, hmEmpWlocation, alEmpId, hmEmpTaskDetails);
						hmProBillCost = CF.getProjectBillableCostAndTime(con, CF, uF, proId, hmProjectData1, false, false,hmProEmpRate,hmEmpLevelMap,hmEmpWlocation);
					}
//					System.out.println("PBI/645---cnt1=="+cnt1);
					cnt1++;
					Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost1(con,CF, uF, proId, hmProjectData1,hmProEmpRate,hmTaskData,hmProVaribaleCost);
					
					double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(proId));
					 
//					hmProName.put(rs.getString("pro_id"), rs.getString("pro_name"));
					
					if("F".equalsIgnoreCase(hmProjectData1.get("PRO_BILL_TYPE"))) { 
						 dblBillableAmt = uF.parseToDouble(hmProjectData1.get("PRO_BILLING_AMOUNT"));
					} else {
						 dblBillableAmt = uF.parseToDouble(hmProBillCost.get("proBillableCost"));
					}
					 
					 dblBugedtedAmt = uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedCost"));
					 dblActualAmt = uF.parseToDouble(hmProActualCostTime.get("proActualCost"));
					 
					 hmProPerformaceBudget.put(proId, uF.formatIntoOneDecimal(dblBugedtedAmt));
					 hmProPerformaceActual.put(proId, uF.formatIntoOneDecimal(dblActualAmt + dblReimbursement));
					 hmProPerformaceBillable.put(proId, uF.formatIntoOneDecimal(dblBillableAmt));
					 
					 double diff = 0;
					 if(dblBillableAmt > 0) {
						 diff = ((dblBillableAmt-(dblActualAmt + dblReimbursement))/dblBillableAmt) * 100;
					 }
					 hmProPerformaceProjectProfit.put(proId, Math.round(diff)+"%");
				}
				//===end parvez date: 05-03-2022===
				
				
				request.setAttribute("hmProPerformaceBillable", hmProPerformaceBillable);
				request.setAttribute("hmProPerformaceActual", hmProPerformaceActual);
				request.setAttribute("hmProPerformaceBudget", hmProPerformaceBudget);
				request.setAttribute("hmProPerformaceProjectProfit", hmProPerformaceProjectProfit);
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
	}
	
	
	

	public void getProjectDetailsByService() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from(select sum(a.variable_cost) as variable_cost, sum(a.billable_amount)as billable_amount, sum(a.budgeted_cost) as budgeted_cost,  sum(a.already_work) as already_work, sum(a.actual_amount) as actual_amount,sum(a.actual_hrs) as actual_hrs,sum(a.idealtime) as idealtime , pmntc.client_id  from (select pc.*,pt.actual_hrs,pt.idealtime from project_cost pc LEFT JOIN project_time pt ON(pc.pro_id=pt.pro_id) )as a, projectmntnc pmntc where pmntc.pro_id=a.pro_id ");
			sbQuery.append("select pro_id, service from projectmntnc pmc where pmc.approve_status = 'approved' and service is not null");
			
//			if(uF.parseToInt(getF_org())>0) {
//				sbQuery.append(" and pmc.org_id = "+uF.parseToInt(getF_org()));
//			}
//			if(getF_strWLocation() != null && getF_strWLocation().length>0) {
//				sbQuery.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//			}
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
			//===start parvez date: 14-10-2022===	
//				sbQuery.append(" and pmc.project_owner="+uF.parseToInt(strSessionEmpId));
				sbQuery.append(" and pmc.project_owners like '%,"+strSessionEmpId+",%'");
			//===end parvez date: 14-10-2022===	
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and pmc.org_id in ("+uF.parseToInt(getF_org())+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and pmc.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and pmc.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
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
			
			if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase("") && !getStrEndDate().equalsIgnoreCase("")) {
				sbQuery.append(" and pmc.start_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and pmc.added_by = "+uF.parseToInt((String)session.getAttribute(EMPID)));
			}
			sbQuery.append(" order by service");
//			sbQuery.append(" group by pmntc.client_id) as d,client_details cd where d.client_id=cd.client_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===> " + pst);
			rs=pst.executeQuery();
			List<List<String>> alOuter = new ArrayList<List<String>>();
			
			Map<String, List<String>> hmService = new HashMap<String, List<String>>(); 
			List<String> alProIdServWise = new ArrayList<String>();
			List<String> alProId = new ArrayList<String>();
			while(rs.next()) {
				
				String arrService[] = null;
				if(rs.getString("service") != null) {
					arrService = rs.getString("service").split(",");
					
					alProIdServWise = hmService.get(arrService[0]);
					if(alProIdServWise == null) alProIdServWise = new ArrayList<String>();
					alProIdServWise.add(rs.getString("pro_id"));
					
					hmService.put(arrService[0], alProIdServWise);
					
					if(!alProId.contains(rs.getString("pro_id"))){
						alProId.add(rs.getString("pro_id"));
					}
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmService ===>> " + hmService);
			String[] strArray = (String[]) alProId.toArray(new String[0]);
			getProjectDetails(con, uF, StringUtils.join(strArray, ','));

			Map<String, String> hmProPerformaceBillable = (Map<String, String>)request.getAttribute("hmProPerformaceBillable");
			Map<String, String> hmProPerformaceActual = (Map<String, String>)request.getAttribute("hmProPerformaceActual");
			
			StringBuilder sbName = new StringBuilder();
			StringBuilder sbActual = new StringBuilder();
			StringBuilder sbBilled = new StringBuilder();
			StringBuilder sbProfit = new StringBuilder();
			StringBuilder sbProfitC = new StringBuilder();
			
			StringBuilder sbProfitChart = new StringBuilder();
			sbProfitChart.append("{'Profitability': 'Profitability',");
			List<String> alProfit = new ArrayList<String>();
			int x = 1;
			
			Iterator<String> it = hmService.keySet().iterator();
			while (it.hasNext()) {
				String serviceId = it.next();
				List<String> proIdList = hmService.get(serviceId);
				double billedAmount = 0;
				double actualCost = 0;
				double profitAmount = 0;
				double profitPercent = 0;
				
				for (int i = 0; proIdList != null && i < proIdList.size(); i++) {
					billedAmount += uF.parseToDouble(hmProPerformaceBillable.get(proIdList.get(i)));
					actualCost += uF.parseToDouble(hmProPerformaceActual.get(proIdList.get(i)));
				}
				
				profitAmount = billedAmount - actualCost;
				if(billedAmount > 0) {
					profitPercent = (profitAmount / billedAmount) * 100;
				} else {
					profitPercent = profitAmount * 100;
				}
				List<String> alInner = new ArrayList<String>();
				alInner.add(serviceId);
				alInner.add(uF.showData(CF.getProjectServiceNameById(con, serviceId), "N/A"));
				alInner.add(uF.formatIntoTwoDecimal(billedAmount));
				alInner.add(uF.formatIntoTwoDecimal(actualCost));
				alInner.add(uF.formatIntoTwoDecimal(profitAmount));
				alInner.add(uF.formatIntoOneDecimal(profitPercent)+"%");
				
				alOuter.add(alInner);
				
				sbName.append("'"+uF.showData(CF.getProjectServiceNameById(con, serviceId), "N/A")+"'"+",");
				sbBilled.append(uF.formatIntoTwoDecimalWithOutComma(billedAmount)+",");
				sbActual.append(uF.formatIntoTwoDecimalWithOutComma(actualCost)+",");
				sbProfit.append(uF.formatIntoOneDecimalWithOutComma(profitPercent)+",");
				
				String proServiceName = uF.showData(CF.getProjectServiceNameById(con, serviceId), "N/A");
				proServiceName = proServiceName.replace("'", "\\\'");
				sbProfitC.append("{name: '"+proServiceName+"',data: ["+uF.formatIntoTwoDecimalWithOutComma(profitAmount)+"]}"+",");
				
				sbProfitChart.append("'"+proServiceName+"': "+uF.formatIntoTwoDecimalWithOutComma(profitAmount)+",");
				StringBuilder sbGraph = new StringBuilder();
				sbGraph.append("var graph"+x+" = new AmCharts.AmGraph();" +
								"graph"+x+".type = \"column\";" +
								"graph"+x+".title = \""+proServiceName+"\";" +
								"graph"+x+".valueField = \""+proServiceName+"\";" +
								"graph"+x+".balloonText = \""+proServiceName+":[[value]]\";" +
								"graph"+x+".lineAlpha = 0;" +
								"graph"+x+".fillAlphas = 1;" +
								"chart.addGraph(graph"+x+");");
				alProfit.add(sbGraph.toString());
				x++;
				
			}
			
			if(sbName.length()>1) {
				sbName.replace(0, sbName.length(), sbName.substring(0, sbName.length()-1));
				sbBilled.replace(0, sbBilled.length(), sbBilled.substring(0, sbBilled.length()-1));
				sbActual.replace(0, sbActual.length(), sbActual.substring(0, sbActual.length()-1));
				sbProfit.replace(0, sbProfit.length(), sbProfit.substring(0, sbProfit.length()-1));
				sbProfitC.replace(0, sbProfitC.length(), sbProfitC.substring(0, sbProfitC.length()-1));
				
				sbProfitChart.replace(0, sbProfitChart.length(), sbProfitChart.substring(0, sbProfitChart.length()-1));
			}
			sbProfitChart.append("}");
			request.setAttribute("sbProfitChart", sbProfitChart.toString());
			request.setAttribute("alProfit", alProfit);
			
			request.setAttribute("sbName", sbName.toString());
			request.setAttribute("sbBilled", sbBilled.toString());
			request.setAttribute("sbActual", sbActual.toString());
			request.setAttribute("sbProfit", sbProfit.toString());
			request.setAttribute("sbProfitC", sbProfitC.toString());
			
			request.setAttribute("alOuter", alOuter);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getProjectDetailsByWorkLocation() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from(select sum(a.variable_cost) as variable_cost, sum(a.billable_amount)as billable_amount, sum(a.budgeted_cost) as budgeted_cost,  sum(a.already_work) as already_work, sum(a.actual_amount) as actual_amount,sum(a.actual_hrs) as actual_hrs,sum(a.idealtime) as idealtime , pmntc.client_id  from (select pc.*,pt.actual_hrs,pt.idealtime from project_cost pc LEFT JOIN project_time pt ON(pc.pro_id=pt.pro_id) )as a, projectmntnc pmntc where pmntc.pro_id=a.pro_id ");
			sbQuery.append("select pro_id, wlocation_id from projectmntnc pmc where pmc.approve_status = 'approved' and wlocation_id > 0");

//			if(uF.parseToInt(getF_org())>0) {
//				sbQuery.append(" and pmc.org_id = "+uF.parseToInt(getF_org()));
//			}
//			if(getF_strWLocation() != null && getF_strWLocation().length>0) {
//				sbQuery.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//			}
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
			//===start parvez date: 14-10-2022===	
//				sbQuery.append(" and pmc.project_owner="+uF.parseToInt(strSessionEmpId));
				sbQuery.append(" and pmc.project_owners like '%,"+strSessionEmpId+",%'");
			//===end parvez date: 14-10-2022===	
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and pmc.org_id in ("+uF.parseToInt(getF_org())+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and pmc.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and pmc.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
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
			
			if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase("") && !getStrEndDate().equalsIgnoreCase("")) {
				sbQuery.append(" and pmc.start_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and pmc.added_by = "+uF.parseToInt((String)session.getAttribute(EMPID)));
			}
			sbQuery.append(" order by wlocation_id");
//			sbQuery.append(" group by pmntc.client_id) as d,client_details cd where d.client_id=cd.client_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===> " + pst);
			rs=pst.executeQuery();
			List<List<String>> alOuter = new ArrayList<List<String>>();
			
			Map<String, List<String>> hmWLocation = new HashMap<String, List<String>>(); 
			List<String> alProIdWLocWise = new ArrayList<String>();
			List<String> alProId = new ArrayList<String>();
			while(rs.next()) {
				
				if(rs.getString("wlocation_id") != null) {

					alProIdWLocWise = hmWLocation.get(rs.getString("wlocation_id"));
					if(alProIdWLocWise == null) alProIdWLocWise = new ArrayList<String>();
					alProIdWLocWise.add(rs.getString("pro_id"));
					
					hmWLocation.put(rs.getString("wlocation_id"), alProIdWLocWise);
					
					if(!alProId.contains(rs.getString("pro_id"))){
						alProId.add(rs.getString("pro_id"));
					}
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmWLocation ===>> " + hmWLocation);
			
			String[] strArray = (String[]) alProId.toArray(new String[0]);
			getProjectDetails(con, uF, StringUtils.join(strArray, ','));
			
			Map<String, String> hmProPerformaceBillable = (Map<String, String>)request.getAttribute("hmProPerformaceBillable");
			Map<String, String> hmProPerformaceActual = (Map<String, String>)request.getAttribute("hmProPerformaceActual");
			
			StringBuilder sbName = new StringBuilder();
			StringBuilder sbActual = new StringBuilder();
			StringBuilder sbBilled = new StringBuilder();
			StringBuilder sbProfit = new StringBuilder();
			StringBuilder sbProfitC = new StringBuilder();
			
			StringBuilder sbProfitChart = new StringBuilder();
			sbProfitChart.append("{'Profitability': 'Profitability',");
			List<String> alProfit = new ArrayList<String>();
			int x = 1;
			
			Iterator<String> it = hmWLocation.keySet().iterator();
			while (it.hasNext()) {
				String wLocId = it.next();
				List<String> proIdList = hmWLocation.get(wLocId);
				double billedAmount = 0;
				double actualCost = 0;
				double profitAmount = 0;
				double profitPercent = 0;
				
				for (int i = 0; proIdList != null && i < proIdList.size(); i++) {
					billedAmount += uF.parseToDouble(hmProPerformaceBillable.get(proIdList.get(i)));
					actualCost += uF.parseToDouble(hmProPerformaceActual.get(proIdList.get(i)));
				}
				
				profitAmount = billedAmount - actualCost;
				if(billedAmount > 0) {
					profitPercent = (profitAmount / billedAmount) * 100;
				} else {
					profitPercent = profitAmount * 100;
				}
				List<String> alInner = new ArrayList<String>();
				alInner.add(wLocId);
				alInner.add(uF.showData(CF.getWorkLocationNameById(con, wLocId), "N/A"));
				alInner.add(uF.formatIntoTwoDecimal(billedAmount));
				alInner.add(uF.formatIntoTwoDecimal(actualCost));
				alInner.add(uF.formatIntoTwoDecimal(profitAmount));
				alInner.add(uF.formatIntoOneDecimal(profitPercent)+"%");
				
				alOuter.add(alInner);
				
				sbName.append("'"+uF.showData(CF.getWorkLocationNameById(con, wLocId), "N/A")+"'"+",");
				sbBilled.append(uF.formatIntoTwoDecimalWithOutComma(billedAmount)+",");
				sbActual.append(uF.formatIntoTwoDecimalWithOutComma(actualCost)+",");
				sbProfit.append(uF.formatIntoOneDecimalWithOutComma(profitPercent)+",");
				
				String wLocationName = uF.showData(CF.getWorkLocationNameById(con, wLocId), "N/A");
				wLocationName = wLocationName.replace("'", "\\\'");
				sbProfitC.append("{name: '"+wLocationName+"',data: ["+uF.formatIntoTwoDecimalWithOutComma(profitAmount)+"]}"+",");
				
				sbProfitChart.append("'"+wLocationName+"': "+uF.formatIntoTwoDecimalWithOutComma(profitAmount)+",");
				StringBuilder sbGraph = new StringBuilder();
				sbGraph.append("var graph"+x+" = new AmCharts.AmGraph();" +
								"graph"+x+".type = \"column\";" +
								"graph"+x+".title = \""+wLocationName+"\";" +
								"graph"+x+".valueField = \""+wLocationName+"\";" +
								"graph"+x+".balloonText = \""+wLocationName+":[[value]]\";" +
								"graph"+x+".lineAlpha = 0;" +
								"graph"+x+".fillAlphas = 1;" +
								"chart.addGraph(graph"+x+");");
				alProfit.add(sbGraph.toString());
				x++;
			}
			
			if(sbName.length() > 1) {
				sbName.replace(0, sbName.length(), sbName.substring(0, sbName.length()-1));
				sbBilled.replace(0, sbBilled.length(), sbBilled.substring(0, sbBilled.length()-1));
				sbActual.replace(0, sbActual.length(), sbActual.substring(0, sbActual.length()-1));
				sbProfit.replace(0, sbProfit.length(), sbProfit.substring(0, sbProfit.length()-1));
				sbProfitC.replace(0, sbProfitC.length(), sbProfitC.substring(0, sbProfitC.length()-1));
				
				sbProfitChart.replace(0, sbProfitChart.length(), sbProfitChart.substring(0, sbProfitChart.length()-1));
			}
			sbProfitChart.append("}");
			request.setAttribute("sbProfitChart", sbProfitChart.toString());
			request.setAttribute("alProfit", alProfit);
			
			request.setAttribute("sbName", sbName.toString());
			request.setAttribute("sbBilled", sbBilled.toString());
			request.setAttribute("sbActual", sbActual.toString());
			request.setAttribute("sbProfit", sbProfit.toString());
			request.setAttribute("sbProfitC", sbProfitC.toString());
			
			request.setAttribute("alOuter", alOuter);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getProjectDetailsByOrg() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from(select sum(a.variable_cost) as variable_cost, sum(a.billable_amount)as billable_amount, sum(a.budgeted_cost) as budgeted_cost,  sum(a.already_work) as already_work, sum(a.actual_amount) as actual_amount,sum(a.actual_hrs) as actual_hrs,sum(a.idealtime) as idealtime , pmntc.client_id  from (select pc.*,pt.actual_hrs,pt.idealtime from project_cost pc LEFT JOIN project_time pt ON(pc.pro_id=pt.pro_id) )as a, projectmntnc pmntc where pmntc.pro_id=a.pro_id ");
			sbQuery.append("select pro_id, org_id from projectmntnc pmc where pmc.approve_status = 'approved' and pmc.org_id > 0 ");

//			if(uF.parseToInt(getF_org())>0) {
//				sbQuery.append(" and pmc.org_id = "+uF.parseToInt(getF_org()));
//			}
//			if(getF_strWLocation() != null && getF_strWLocation().length>0) {
//				sbQuery.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//			}
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
			//===start parvez date: 14-10-2022===	
//				sbQuery.append(" and pmc.project_owner="+uF.parseToInt(strSessionEmpId));
				sbQuery.append(" and pmc.project_owners like '%,"+strSessionEmpId+",%'");
			//===end parvez date: 14-10-2022===	
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and pmc.org_id in ("+uF.parseToInt(getF_org())+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and pmc.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and pmc.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
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
			
			if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase("") && !getStrEndDate().equalsIgnoreCase("")) {
				sbQuery.append(" and pmc.start_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and pmc.added_by = "+uF.parseToInt((String)session.getAttribute(EMPID)));
			}
			sbQuery.append(" order by org_id");
//			sbQuery.append(" group by pmntc.client_id) as d,client_details cd where d.client_id=cd.client_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===> " + pst);
			rs=pst.executeQuery();
			List<List<String>> alOuter = new ArrayList<List<String>>();
			
			Map<String, List<String>> hmOrganisation = new HashMap<String, List<String>>(); 
			List<String> alProIdOrgWise = new ArrayList<String>();
			List<String> alProId = new ArrayList<String>();
			while(rs.next()) {
				
				if(rs.getString("org_id") != null) {

					alProIdOrgWise = hmOrganisation.get(rs.getString("org_id"));
					if(alProIdOrgWise == null) alProIdOrgWise = new ArrayList<String>();
					alProIdOrgWise.add(rs.getString("pro_id"));
					
					hmOrganisation.put(rs.getString("org_id"), alProIdOrgWise);
					
					if(!alProId.contains(rs.getString("pro_id"))){
						alProId.add(rs.getString("pro_id"));
					}
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmOrganisation ===>> " + hmOrganisation);
			
			String[] strArray = (String[]) alProId.toArray(new String[0]);
			getProjectDetails(con, uF, StringUtils.join(strArray, ','));
			
			Map<String, String> hmProPerformaceBillable = (Map<String, String>)request.getAttribute("hmProPerformaceBillable");
			Map<String, String> hmProPerformaceActual = (Map<String, String>)request.getAttribute("hmProPerformaceActual");
			
			StringBuilder sbName = new StringBuilder();
			StringBuilder sbActual = new StringBuilder();
			StringBuilder sbBilled = new StringBuilder();
			StringBuilder sbProfit = new StringBuilder();
			StringBuilder sbProfitC = new StringBuilder();
			
			StringBuilder sbProfitChart = new StringBuilder();
			sbProfitChart.append("{'Profitability': 'Profitability',");
			List<String> alProfit = new ArrayList<String>();
			int x = 1;
			
			Iterator<String> it = hmOrganisation.keySet().iterator();
			while (it.hasNext()) {
				String orgId = it.next();
				List<String> proIdList = hmOrganisation.get(orgId);
				double billedAmount = 0;
				double actualCost = 0;
				double profitAmount = 0;
				double profitPercent = 0;
				
				for (int i = 0; proIdList != null && i < proIdList.size(); i++) {
					billedAmount += uF.parseToDouble(hmProPerformaceBillable.get(proIdList.get(i)));
					actualCost += uF.parseToDouble(hmProPerformaceActual.get(proIdList.get(i)));
				}
				
				profitAmount = billedAmount - actualCost;
				if(billedAmount > 0) {
					profitPercent = (profitAmount / billedAmount) * 100;
				} else {
					profitPercent = profitAmount * 100;
				}
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(orgId);
				alInner.add(uF.showData(CF.getOrgNameById(con, orgId), "N/A"));
				alInner.add(uF.formatIntoTwoDecimal(billedAmount));
				alInner.add(uF.formatIntoTwoDecimal(actualCost));
				alInner.add(uF.formatIntoTwoDecimal(profitAmount));
				alInner.add(uF.formatIntoOneDecimal(profitPercent)+"%");
				
				alOuter.add(alInner);
				
				sbName.append("'"+uF.showData(CF.getOrgNameById(con, orgId), "N/A")+"'"+",");
				sbBilled.append(uF.formatIntoTwoDecimalWithOutComma(billedAmount)+",");
				sbActual.append(uF.formatIntoTwoDecimalWithOutComma(actualCost)+",");
				sbProfit.append(uF.formatIntoOneDecimalWithOutComma(profitPercent)+",");
				
				String orgName = uF.showData(CF.getOrgNameById(con, orgId), "N/A");
				orgName = orgName.replace("'", "\\\'");
				sbProfitC.append("{name: '"+orgName+"',data: ["+uF.formatIntoTwoDecimalWithOutComma(profitAmount)+"]}"+",");
				
				sbProfitChart.append("'"+orgName+"': "+uF.formatIntoTwoDecimalWithOutComma(profitAmount)+",");
				StringBuilder sbGraph = new StringBuilder();
				sbGraph.append("var graph"+x+" = new AmCharts.AmGraph();" +
								"graph"+x+".type = \"column\";" +
								"graph"+x+".title = \""+orgName+"\";" +
								"graph"+x+".valueField = \""+orgName+"\";" +
								"graph"+x+".balloonText = \""+orgName+":[[value]]\";" +
								"graph"+x+".lineAlpha = 0;" +
								"graph"+x+".fillAlphas = 1;" +
								"chart.addGraph(graph"+x+");");
				alProfit.add(sbGraph.toString());
				x++;
			}
			
			if(sbName.length() > 1) {
				sbName.replace(0, sbName.length(), sbName.substring(0, sbName.length()-1));
				sbBilled.replace(0, sbBilled.length(), sbBilled.substring(0, sbBilled.length()-1));
				sbActual.replace(0, sbActual.length(), sbActual.substring(0, sbActual.length()-1));
				sbProfit.replace(0, sbProfit.length(), sbProfit.substring(0, sbProfit.length()-1));
				sbProfitC.replace(0, sbProfitC.length(), sbProfitC.substring(0, sbProfitC.length()-1));
				
				sbProfitChart.replace(0, sbProfitChart.length(), sbProfitChart.substring(0, sbProfitChart.length()-1));
			}
			sbProfitChart.append("}");
			request.setAttribute("sbProfitChart", sbProfitChart.toString());
			request.setAttribute("alProfit", alProfit);
			
			request.setAttribute("sbName", sbName.toString());
			request.setAttribute("sbBilled", sbBilled.toString());
			request.setAttribute("sbActual", sbActual.toString());
			request.setAttribute("sbProfit", sbProfit.toString());
			request.setAttribute("sbProfitC", sbProfitC.toString());
			
			request.setAttribute("alOuter", alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
	public void getProjectDetailsByDepart() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from(select sum(a.variable_cost) as variable_cost, sum(a.billable_amount)as billable_amount, sum(a.budgeted_cost) as budgeted_cost,  sum(a.already_work) as already_work, sum(a.actual_amount) as actual_amount,sum(a.actual_hrs) as actual_hrs,sum(a.idealtime) as idealtime , pmntc.client_id  from (select pc.*,pt.actual_hrs,pt.idealtime from project_cost pc LEFT JOIN project_time pt ON(pc.pro_id=pt.pro_id) )as a, projectmntnc pmntc where pmntc.pro_id=a.pro_id ");
			sbQuery.append("select pro_id, department_id from projectmntnc pmc where pmc.approve_status = 'approved' and department_id > 0");
			
//			if(uF.parseToInt(getF_org())>0) {
//				sbQuery.append(" and pmc.org_id = "+uF.parseToInt(getF_org()));
//			}
//			if(getF_strWLocation() != null && getF_strWLocation().length>0) {
//				sbQuery.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//			}
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
		//===start parvez date: 14-10-2022===		
//				sbQuery.append(" and pmc.project_owner="+uF.parseToInt(strSessionEmpId));
				sbQuery.append(" and pmc.project_owners like '%,"+strSessionEmpId+",%'");
		//===end parvez date: 14-10-2022===		
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and pmc.org_id in ("+uF.parseToInt(getF_org())+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and pmc.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and pmc.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
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
			
			if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase("") && !getStrEndDate().equalsIgnoreCase("")) {
				sbQuery.append(" and pmc.start_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and pmc.added_by = "+uF.parseToInt((String)session.getAttribute(EMPID)));
			}
			sbQuery.append(" order by department_id");
//			sbQuery.append(" group by pmntc.client_id) as d,client_details cd where d.client_id=cd.client_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===> " + pst);
			rs=pst.executeQuery();
			List<List<String>> alOuter = new ArrayList<List<String>>();
			
			Map<String, List<String>> hmDepart = new HashMap<String, List<String>>(); 
			List<String> alProIdDepartWise = new ArrayList<String>();
			List<String> alProId = new ArrayList<String>();
			while(rs.next()) {
				
				if(rs.getString("department_id") != null) {

					alProIdDepartWise = hmDepart.get(rs.getString("department_id"));
					if(alProIdDepartWise == null) alProIdDepartWise = new ArrayList<String>();
					alProIdDepartWise.add(rs.getString("pro_id"));
					
					hmDepart.put(rs.getString("department_id"), alProIdDepartWise);
					
					if(!alProId.contains(rs.getString("pro_id"))){
						alProId.add(rs.getString("pro_id"));
					}
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmDepart ===>> " + hmDepart);
			
			String[] strArray = (String[]) alProId.toArray(new String[0]);
			getProjectDetails(con, uF, StringUtils.join(strArray, ','));
			Map<String, String> hmProPerformaceBillable = (Map<String, String>)request.getAttribute("hmProPerformaceBillable");
			Map<String, String> hmProPerformaceActual = (Map<String, String>)request.getAttribute("hmProPerformaceActual");
			
			StringBuilder sbName = new StringBuilder();
			StringBuilder sbActual = new StringBuilder();
			StringBuilder sbBilled = new StringBuilder();
			StringBuilder sbProfit = new StringBuilder();
			StringBuilder sbProfitC = new StringBuilder();
			
			StringBuilder sbProfitChart = new StringBuilder();
			sbProfitChart.append("{'Profitability': 'Profitability',");
			List<String> alProfit = new ArrayList<String>();
			int x = 1;
			
			Iterator<String> it = hmDepart.keySet().iterator();
			while (it.hasNext()) {
				String departId = it.next();
				List<String> proIdList = hmDepart.get(departId);
				double billedAmount = 0;
				double actualCost = 0;
				double profitAmount = 0;
				double profitPercent = 0;
				
				for (int i = 0; proIdList != null && i < proIdList.size(); i++) {
					billedAmount += uF.parseToDouble(hmProPerformaceBillable.get(proIdList.get(i)));
					actualCost += uF.parseToDouble(hmProPerformaceActual.get(proIdList.get(i)));
				}
				
				profitAmount = billedAmount - actualCost;
				if(billedAmount > 0) {
					profitPercent = (profitAmount / billedAmount) * 100;
				} else {
					profitPercent = profitAmount * 100;
				}
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(departId);
				alInner.add(uF.showData(CF.getDepartMentNameById(con, departId), "N/A"));
				alInner.add(uF.formatIntoTwoDecimal(billedAmount));
				alInner.add(uF.formatIntoTwoDecimal(actualCost));
				alInner.add(uF.formatIntoTwoDecimal(profitAmount));
				alInner.add(uF.formatIntoOneDecimal(profitPercent)+"%");
				
				alOuter.add(alInner);
				
				sbName.append("'"+uF.showData(CF.getDepartMentNameById(con, departId), "N/A")+"'"+",");
				sbBilled.append(uF.formatIntoTwoDecimalWithOutComma(billedAmount)+",");
				sbActual.append(uF.formatIntoTwoDecimalWithOutComma(actualCost)+",");
				sbProfit.append(uF.formatIntoOneDecimalWithOutComma(profitPercent)+",");
				
				String departName = uF.showData(CF.getDepartMentNameById(con, departId), "N/A");
				departName = departName.replace("'", "\\\'");
				sbProfitC.append("{name: '"+departName+"',data: ["+uF.formatIntoTwoDecimalWithOutComma(profitAmount)+"]}"+",");
				
				sbProfitChart.append("'"+departName+"': "+uF.formatIntoTwoDecimalWithOutComma(profitAmount)+",");
				StringBuilder sbGraph = new StringBuilder();
				sbGraph.append("var graph"+x+" = new AmCharts.AmGraph();" +
					"graph"+x+".type = \"column\";" +
					"graph"+x+".title = \""+departName+"\";" +
					"graph"+x+".valueField = \""+departName+"\";" +
					"graph"+x+".balloonText = \""+departName+":[[value]]\";" +
					"graph"+x+".lineAlpha = 0;" +
					"graph"+x+".fillAlphas = 1;" +
					"chart.addGraph(graph"+x+");");
				alProfit.add(sbGraph.toString());
				x++;
			}
			
			if(sbName.length() > 1) {
				sbName.replace(0, sbName.length(), sbName.substring(0, sbName.length()-1));
				sbBilled.replace(0, sbBilled.length(), sbBilled.substring(0, sbBilled.length()-1));
				sbActual.replace(0, sbActual.length(), sbActual.substring(0, sbActual.length()-1));
				sbProfit.replace(0, sbProfit.length(), sbProfit.substring(0, sbProfit.length()-1));
				sbProfitC.replace(0, sbProfitC.length(), sbProfitC.substring(0, sbProfitC.length()-1));
				
				sbProfitChart.replace(0, sbProfitChart.length(), sbProfitChart.substring(0, sbProfitChart.length()-1));
			}
			sbProfitChart.append("}");
			request.setAttribute("sbProfitChart", sbProfitChart.toString());
			request.setAttribute("alProfit", alProfit);
			
			request.setAttribute("sbName", sbName.toString());
			request.setAttribute("sbBilled", sbBilled.toString());
			request.setAttribute("sbActual", sbActual.toString());
			request.setAttribute("sbProfit", sbProfit.toString());
			request.setAttribute("sbProfitC", sbProfitC.toString());
			
			request.setAttribute("alOuter", alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getProjectDetailsByClient() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from(select sum(a.variable_cost) as variable_cost, sum(a.billable_amount)as billable_amount, sum(a.budgeted_cost) as budgeted_cost,  sum(a.already_work) as already_work, sum(a.actual_amount) as actual_amount,sum(a.actual_hrs) as actual_hrs,sum(a.idealtime) as idealtime , pmntc.client_id  from (select pc.*,pt.actual_hrs,pt.idealtime from project_cost pc LEFT JOIN project_time pt ON(pc.pro_id=pt.pro_id) )as a, projectmntnc pmntc where pmntc.pro_id=a.pro_id ");
			sbQuery.append("select pro_id, client_id from projectmntnc pmc where pmc.approve_status = 'approved' and client_id > 0");

//			if(uF.parseToInt(getF_org())>0) {
//				sbQuery.append(" and pmc.org_id = "+uF.parseToInt(getF_org()));
//			}
//			if(getF_strWLocation() != null && getF_strWLocation().length>0) {
//				sbQuery.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//			}
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
			//===start parvez date: 14-10-2022===	
//				sbQuery.append(" and pmc.project_owner="+uF.parseToInt(strSessionEmpId));
				sbQuery.append(" and pmc.project_owners like '%,"+strSessionEmpId+",%'");
			//===end parvez date: 14-10-2022===	
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and pmc.org_id in ("+uF.parseToInt(getF_org())+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and pmc.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and pmc.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
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
			
			if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase("") && !getStrEndDate().equalsIgnoreCase("")) {
				sbQuery.append(" and pmc.start_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and pmc.added_by = "+uF.parseToInt((String)session.getAttribute(EMPID)));
			}
			sbQuery.append(" order by client_id");
//			sbQuery.append(" group by pmntc.client_id) as d,client_details cd where d.client_id=cd.client_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===> " + pst);
			rs=pst.executeQuery();
			List<List<String>> alOuter = new ArrayList<List<String>>();
			
			Map<String, List<String>> hmClient = new HashMap<String, List<String>>(); 
			List<String> alProIdClientWise = new ArrayList<String>();
			List<String> alProId = new ArrayList<String>();
			while(rs.next()) {
				
				if(rs.getString("client_id") != null) {

					alProIdClientWise = hmClient.get(rs.getString("client_id"));
					if(alProIdClientWise == null) alProIdClientWise = new ArrayList<String>();
					alProIdClientWise.add(rs.getString("pro_id"));
					
					hmClient.put(rs.getString("client_id"), alProIdClientWise);
					
					if(!alProId.contains(rs.getString("pro_id"))){
						alProId.add(rs.getString("pro_id"));
					}
				}
			}
			rs.close();
			pst.close();
			
//			System.out.println("hmClient ===>> " + hmClient);
			
			String[] strArray = (String[]) alProId.toArray(new String[0]);
			getProjectDetails(con, uF, StringUtils.join(strArray, ','));
			Map<String, String> hmProPerformaceBillable = (Map<String, String>)request.getAttribute("hmProPerformaceBillable");
			Map<String, String> hmProPerformaceActual = (Map<String, String>)request.getAttribute("hmProPerformaceActual");
			
			StringBuilder sbName = new StringBuilder();
			StringBuilder sbActual = new StringBuilder();
			StringBuilder sbBilled = new StringBuilder();
			StringBuilder sbProfit = new StringBuilder();
			StringBuilder sbProfitC = new StringBuilder();
			
			StringBuilder sbProfitChart = new StringBuilder();
			sbProfitChart.append("{'Profitability': 'Profitability',");
			List<String> alProfit = new ArrayList<String>();
			int x = 1;
			
			Iterator<String> it = hmClient.keySet().iterator();
			while (it.hasNext()) {
				String clientId = it.next();
				List<String> proIdList = hmClient.get(clientId);
				double billedAmount = 0;
				double actualCost = 0;
				double profitAmount = 0;
				double profitPercent = 0;
				
				for (int i = 0; proIdList != null && i < proIdList.size(); i++) {
					billedAmount += uF.parseToDouble(hmProPerformaceBillable.get(proIdList.get(i)));
					actualCost += uF.parseToDouble(hmProPerformaceActual.get(proIdList.get(i)));
				}
				
				profitAmount = billedAmount - actualCost;
				if(billedAmount > 0) {
					profitPercent = (profitAmount / billedAmount) * 100;
				} else {
					profitPercent = profitAmount * 100;
				}
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(clientId);
				alInner.add(uF.showData(CF.getClientNameById(con, clientId), "N/A"));
				alInner.add(uF.formatIntoTwoDecimal(billedAmount));
				alInner.add(uF.formatIntoTwoDecimal(actualCost));
				alInner.add(uF.formatIntoTwoDecimal(profitAmount));
				alInner.add(uF.formatIntoOneDecimal(profitPercent)+"%");
				
				alOuter.add(alInner);
				
				sbName.append("'"+uF.showData(CF.getClientNameById(con, clientId), "N/A")+"'"+",");
				sbBilled.append(uF.formatIntoTwoDecimalWithOutComma(billedAmount)+",");
				sbActual.append(uF.formatIntoTwoDecimalWithOutComma(actualCost)+",");
				sbProfit.append(uF.formatIntoOneDecimalWithOutComma(profitPercent)+",");
				
				String clientName = uF.showData(CF.getClientNameById(con, clientId), "N/A");
				clientName = clientName.replace("'", "\\\'");
				sbProfitC.append("{name: '"+clientName+"',data: ["+uF.formatIntoTwoDecimalWithOutComma(profitAmount)+"]}"+",");
				
				sbProfitChart.append("'"+clientName+"': "+uF.formatIntoTwoDecimalWithOutComma(profitAmount)+",");
				StringBuilder sbGraph = new StringBuilder();
				sbGraph.append("var graph"+x+" = new AmCharts.AmGraph();" +
								"graph"+x+".type = \"column\";" +
								"graph"+x+".title = \""+clientName+"\";" +
								"graph"+x+".valueField = \""+clientName+"\";" +
								"graph"+x+".balloonText = \""+clientName+":[[value]]\";" +
								"graph"+x+".lineAlpha = 0;" +
								"graph"+x+".fillAlphas = 1;" +
								"chart.addGraph(graph"+x+");");
				alProfit.add(sbGraph.toString());
				x++;
			}
			
			if(sbName.length() > 1) {
				sbName.replace(0, sbName.length(), sbName.substring(0, sbName.length()-1));
				sbBilled.replace(0, sbBilled.length(), sbBilled.substring(0, sbBilled.length()-1));
				sbActual.replace(0, sbActual.length(), sbActual.substring(0, sbActual.length()-1));
				sbProfit.replace(0, sbProfit.length(), sbProfit.substring(0, sbProfit.length()-1));
				sbProfitC.replace(0, sbProfitC.length(), sbProfitC.substring(0, sbProfitC.length()-1));
				
				sbProfitChart.replace(0, sbProfitChart.length(), sbProfitChart.substring(0, sbProfitChart.length()-1));
			}
			sbProfitChart.append("}");
			
//			System.out.println("sbProfitC ===>> " + sbProfitC.toString());
			
			request.setAttribute("sbProfitChart", sbProfitChart.toString());
			request.setAttribute("alProfit", alProfit);
			
			request.setAttribute("sbName", sbName.toString());
			request.setAttribute("sbBilled", sbBilled.toString());
			request.setAttribute("sbActual", sbActual.toString());
			request.setAttribute("sbProfit", sbProfit.toString());
			request.setAttribute("sbProfitC", sbProfitC.toString());
			
			request.setAttribute("alOuter", alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	public void getProjectDetailsByProject() {

		UtilityFunctions uF=new UtilityFunctions();
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			con = db.makeConnection(con);
//			System.out.println("getStrStartDate() 1 ===>>> " + getStrStartDate());
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from(select sum(a.variable_cost) as variable_cost, sum(a.billable_amount)as billable_amount, sum(a.budgeted_cost) as budgeted_cost,  sum(a.already_work) as already_work, sum(a.actual_amount) as actual_amount,sum(a.actual_hrs) as actual_hrs,sum(a.idealtime) as idealtime , pmntc.client_id  from (select pc.*,pt.actual_hrs,pt.idealtime from project_cost pc LEFT JOIN project_time pt ON(pc.pro_id=pt.pro_id) )as a, projectmntnc pmntc where pmntc.pro_id=a.pro_id ");
			sbQuery.append("select pro_id, client_id from projectmntnc pmc where pmc.approve_status = 'approved' and client_id > 0 ");

//			if(uF.parseToInt(getF_org())>0) {
//				sbQuery.append(" and pmc.org_id = "+uF.parseToInt(getF_org()));
//			}
//			if(getF_strWLocation() != null && getF_strWLocation().length>0) {
//				sbQuery.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
//			}
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
		//===start parvez date: 14-10-2022===		
//				sbQuery.append(" and pmc.project_owner="+uF.parseToInt(strSessionEmpId));
				sbQuery.append(" and pmc.project_owners like '%,"+strSessionEmpId+",%'");
		//===end parvez date: 14-10-2022===		
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and pmc.org_id in ("+uF.parseToInt(getF_org())+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and pmc.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and pmc.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
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
			
			if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase("") && !getStrEndDate().equalsIgnoreCase("")) {
				sbQuery.append(" and pmc.start_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and pmc.added_by = "+uF.parseToInt((String)session.getAttribute(EMPID)));
			}
			sbQuery.append(" order by client_id, pro_id");
//			sbQuery.append(" group by pmntc.client_id) as d,client_details cd where d.client_id=cd.client_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===> " + pst); 
			rs=pst.executeQuery();
			List<List<String>> alOuter = new ArrayList<List<String>>();
			
//			Map<String, List<String>> hmProject = new HashMap<String, List<String>>(); 
			List<String> alProIdProWise = new ArrayList<String>();
			List<String> alProId = new ArrayList<String>();
			while(rs.next()) {
//					alProIdProWise = hmProject.get(rs.getString("pro_id"));
//					if(alProIdProWise == null) alProIdProWise = new ArrayList<String>();
					alProIdProWise.add(rs.getString("pro_id"));
//					hmProject.put(rs.getString("pro_id"), alProIdProWise);
					
					if(!alProId.contains(rs.getString("pro_id"))){
						alProId.add(rs.getString("pro_id"));
					}
			}
			rs.close();
			pst.close();
			
//			System.out.println("getStrStartDate() 2 ===>>> " + getStrStartDate());
			
//			System.out.println("hmProject ===>> " + hmProject);
			
			String[] strArray = (String[]) alProId.toArray(new String[0]);
			getProjectDetails(con, uF, StringUtils.join(strArray, ','));
			Map<String, String> hmProPerformaceBillable = (Map<String, String>)request.getAttribute("hmProPerformaceBillable");
			Map<String, String> hmProPerformaceActual = (Map<String, String>)request.getAttribute("hmProPerformaceActual");
			
			StringBuilder sbName = new StringBuilder();
			StringBuilder sbActual = new StringBuilder();
			StringBuilder sbBilled = new StringBuilder();
			StringBuilder sbProfit = new StringBuilder();
			StringBuilder sbProfitC = new StringBuilder();
			
			StringBuilder sbProfitChart = new StringBuilder();
			sbProfitChart.append("{'Profitability': 'Profitability',");
			List<String> alProfit = new ArrayList<String>();
			int x = 1;
			
//			Iterator<String> it = hmProject.keySet().iterator();
//			while (it.hasNext()) {
			for (int i = 0; alProIdProWise != null && i < alProIdProWise.size(); i++) {
//				String proId = it.next();
				String proId = alProIdProWise.get(i);
//				List<String> proIdList = hmProject.get(proId);
				double billedAmount = 0;
				double actualCost = 0;
				double profitAmount = 0;
				double profitPercent = 0;
				
//				for (int i = 0; proIdList != null && i < proIdList.size(); i++) {
					billedAmount = uF.parseToDouble(hmProPerformaceBillable.get(alProIdProWise.get(i)));
					actualCost = uF.parseToDouble(hmProPerformaceActual.get(alProIdProWise.get(i)));
//				}
				
				profitAmount = billedAmount - actualCost;
				if(billedAmount > 0) {
					profitPercent = (profitAmount / billedAmount) * 100;
				} else {
					profitPercent = profitAmount * 100;
				}
				
				List<String> alInner = new ArrayList<String>();
				alInner.add(proId);
				alInner.add(uF.showData(CF.getProjectNameById(con, proId), "N/A"));
				alInner.add(uF.formatIntoTwoDecimal(billedAmount));
				alInner.add(uF.formatIntoTwoDecimal(actualCost));
				alInner.add(uF.formatIntoTwoDecimal(profitAmount));
				alInner.add(uF.formatIntoOneDecimal(profitPercent)+"%");
				
				alOuter.add(alInner);
				
				sbName.append("'"+uF.showData(CF.getProjectNameById(con, proId), "N/A")+"'"+",");
				sbBilled.append(uF.formatIntoTwoDecimalWithOutComma(billedAmount)+",");
				sbActual.append(uF.formatIntoTwoDecimalWithOutComma(actualCost)+",");
				sbProfit.append(uF.formatIntoOneDecimalWithOutComma(profitPercent)+",");
				
				String projectName = uF.showData(CF.getProjectNameById(con, proId), "N/A");
				projectName = projectName.replace("'", "\\\'");
				sbProfitC.append("{name: '"+projectName+"',data: ["+uF.formatIntoTwoDecimalWithOutComma(profitAmount)+"]}"+",");
				
				sbProfitChart.append("'"+projectName+"': "+uF.formatIntoTwoDecimalWithOutComma(profitAmount)+",");
				StringBuilder sbGraph = new StringBuilder();
				sbGraph.append("var graph"+x+" = new AmCharts.AmGraph();" +
								"graph"+x+".type = \"column\";" +
								"graph"+x+".title = \""+projectName+"\";" +
								"graph"+x+".valueField = \""+projectName+"\";" +
								"graph"+x+".balloonText = \""+projectName+":[[value]]\";" +
								"graph"+x+".lineAlpha = 0;" +
								"graph"+x+".fillAlphas = 1;" +
								"chart.addGraph(graph"+x+");");
				alProfit.add(sbGraph.toString());
				x++;
			}
			
//			System.out.println("getStrStartDate() 3 ===>>> " + getStrStartDate());
			
			if(sbName.length() > 1) {
				sbName.replace(0, sbName.length(), sbName.substring(0, sbName.length()-1));
				sbBilled.replace(0, sbBilled.length(), sbBilled.substring(0, sbBilled.length()-1));
				sbActual.replace(0, sbActual.length(), sbActual.substring(0, sbActual.length()-1));
				sbProfit.replace(0, sbProfit.length(), sbProfit.substring(0, sbProfit.length()-1));
				sbProfitC.replace(0, sbProfitC.length(), sbProfitC.substring(0, sbProfitC.length()-1));
				
				sbProfitChart.replace(0, sbProfitChart.length(), sbProfitChart.substring(0, sbProfitChart.length()-1));
			}
			sbProfitChart.append("}");
			request.setAttribute("sbProfitChart", sbProfitChart.toString());
			request.setAttribute("alProfit", alProfit);
			
			request.setAttribute("sbName", sbName.toString());
			request.setAttribute("sbBilled", sbBilled.toString());
			request.setAttribute("sbActual", sbActual.toString());
			request.setAttribute("sbProfit", sbProfit.toString());
			request.setAttribute("sbProfitC", sbProfitC.toString());
			
			request.setAttribute("alOuter", alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	
//public void getProjectDetails(int nManagerId, UtilityFunctions uF, CommonFunctions CF, int nLimit) {
//		
//		Database db = new Database();
//		db.setRequest(request);
//		Connection con = null;
//		PreparedStatement pst=null;
//		ResultSet rs=null;
//		try {
//			con = db.makeConnection(con);
//			
//			Map<String, String> hmReimbursementAmountMap = CF.getReimbursementAmount(con, "P", "P", true, getStrStartDate(), getStrEndDate(), uF);
//			
//			StringBuilder sbQuery1 = new StringBuilder();
//			
//			sbQuery1.append("select pro_id, pro_name, billing_type, billing_amount, idealtime, start_date, deadline, client_id, actual_calculation_type, " +
//			"bill_days_type, hours_for_bill_day, added_by from projectmntnc pmc where pmc.pro_id > 0 and pmc.approve_status = 'approved' ");
//			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
//				sbQuery1.append(" and pmc.project_owner="+uF.parseToInt(strSessionEmpId));
//			}
//			if(uF.parseToInt(getF_org())>0) {
//				sbQuery1.append(" and pmc.org_id in ("+uF.parseToInt(getF_org())+")");
//			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
//					sbQuery1.append(" and pmc.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//			}
//			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
//	            sbQuery1.append(" and pmc.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
//	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
//				sbQuery1.append(" and pmc.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
//			}
//			
//			if(getF_department() != null && getF_department().length>0) {
//				sbQuery1.append(" and pmc.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
//			}
//			if(getF_project_service() != null && getF_project_service().length>0) {
//				String services = uF.getConcateData(getF_project_service());
//				sbQuery1.append(" and pmc.service in ("+services+") ");
//			}
//			if(getF_client() != null && getF_client().length>0) {
//				sbQuery1.append(" and pmc.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
//			}
//			
//			if(getStrStartDate()!=null && getStrEndDate()!=null && !getStrStartDate().equalsIgnoreCase(LABEL_FROM_DATE) && !getStrEndDate().equalsIgnoreCase(LABEL_TO_DATE)) {
//				sbQuery1.append(" and pmc.start_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE)+"' ");
//			}
//			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
//				sbQuery1.append(" and pmc.added_by = "+uF.parseToInt((String)session.getAttribute(EMPID)));
//			}
//			sbQuery1.append(" order by pmc.pro_id ");
//			
//			pst = con.prepareStatement(sbQuery1.toString());
//			rs = pst.executeQuery();
//			
////			System.out.println("pst==>" + pst);
//			
//			double dblBugedtedAmt = 0;
//			double dblActualAmt = 0;
//			double dblBillableAmt = 0;
//			
////			double dblBugedtedTime = 0;
//			double dblActualTime = 0;
//			
//			Map<String, String> hmProPerformaceBudget = new HashMap<String, String>();
//			Map<String, String> hmProPerformaceActual = new HashMap<String, String>();
//			Map<String, String> hmProPerformaceBillable = new HashMap<String, String>();
//			
//			Map<String, String> hmProPerformaceProjectProfit = new HashMap<String, String>();
//			
//			Map<String, String> hmProName = new HashMap<String, String>();
//			
////			String strProjectIdNew = null;
////			String strProjectIdOld = null;
//			
////			double dblEmpRate = 0.0d;
//			
//			while(rs.next()) {
//				
//				Map<String, String> hmProjectData = CF.getProjectDetailsByProId(con, rs.getString("pro_id"));
//				Map<String, String> hmProActualCostTime = new HashMap<String, String>();
//				Map<String, String> hmProBillCost = new HashMap<String, String>();
//				if(rs.getString("actual_calculation_type")!=null && rs.getString("actual_calculation_type").equals("M")) {
//					hmProActualCostTime = CF.getMonthlyProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData);
//					hmProBillCost = CF.getMonthlyProjectBillableCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData);
//				} else {
//					hmProActualCostTime = CF.getProjectActualCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
//					hmProBillCost = CF.getProjectBillableCostAndTime(con, CF, uF, rs.getString("pro_id"), hmProjectData, false, false);
//				}
//				Map<String, String> hmProBudgetedCostAndTime = CF.getProjectBudgetedCost(con, uF, rs.getString("pro_id"), hmProjectData);
//				
////				strProjectIdNew = rs.getString("pro_id");
//				double dblReimbursement = uF.parseToDouble(hmReimbursementAmountMap.get(rs.getString("pro_id")));
//				
//				hmProName.put(rs.getString("pro_id"), rs.getString("pro_name"));
//				
//				if("F".equalsIgnoreCase(rs.getString("billing_type"))) { 
//					 dblBillableAmt = uF.parseToDouble(rs.getString("billing_amount"));
//				 } else {
//					 dblBillableAmt = uF.parseToDouble(hmProBillCost.get("proBillableCost"));
//				 }
//				 
//				 dblBugedtedAmt = uF.parseToDouble(hmProBudgetedCostAndTime.get("proBudgetedCost"));
//				 dblActualAmt = uF.parseToDouble(hmProActualCostTime.get("proActualCost"));
//				 
//				 hmProPerformaceBudget.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(dblBugedtedAmt));
//				 hmProPerformaceActual.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(dblActualAmt + dblReimbursement));
//				 hmProPerformaceBillable.put(rs.getString("pro_id"), uF.formatIntoOneDecimal(dblBillableAmt));
//				 
//				 double diff = 0;
//				 if(dblBillableAmt > 0) {
//					 diff = ((dblBillableAmt-(dblActualAmt + dblReimbursement))/dblBillableAmt) * 100;
//				 }
//				 hmProPerformaceProjectProfit.put(rs.getString("pro_id"), Math.round(diff)+"%");
//					
////				 strProjectIdOld = strProjectIdNew;
//			}
//			rs.close();
//			pst.close();
//			
//			request.setAttribute("hmProPerformaceBillable", hmProPerformaceBillable);
//			request.setAttribute("hmProPerformaceActual", hmProPerformaceActual);
//			request.setAttribute("hmProPerformaceBudget", hmProPerformaceBudget);
//			request.setAttribute("hmProPerformaceProjectProfit", hmProPerformaceProjectProfit);
//			
////			request.setAttribute("hmProPerformaceActualTime", hmProPerformaceActualTime);
////			request.setAttribute("hmProPerformaceIdealTime", hmProPerformaceIdealTime);
//			
////			System.out.println("hmProPerformaceBillable===="+hmProPerformaceBillable);
////			System.out.println("hmProPerformaceActual===="+hmProPerformaceActual);
////			System.out.println("hmProPerformaceBudget===="+hmProPerformaceBudget);
////			System.out.println("hmProPerformaceProjectProfit===="+hmProPerformaceProjectProfit);
////			System.out.println("hmProPerformaceActualTime===="+hmProPerformaceActualTime);
////			System.out.println("hmProPerformaceIdealTime==="+hmProPerformaceIdealTime);
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		} finally {
//			db.closeResultSet(rs);
//			db.closeStatements(pst);
//			db.closeConnection(con);
//		}
//	}
	

	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
	
		if(isPoFlag()){
			alFilter.add("PROJECT_TYPE");
			if(getStrProType()!=null) {
				String strProType="";
				if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
					strProType = "My Projects";
				}
				if(strProType!=null && !strProType.equals("")) {
					hmFilter.put("PROJECT_TYPE", strProType);
				} else {
					hmFilter.put("PROJECT_TYPE", "All Projects");
				}
			} else {
				hmFilter.put("PROJECT_TYPE", "All Projects");
			}
		}
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++){
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
//		System.out.println(getSelectOne() + " -- getStrStartDate() -- ===>>> " + getStrStartDate());
		
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
//		System.out.println(getSelectOne() + " -- 1 getStrStartDate() -- ===>>> " + getStrStartDate());
		
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

	public String getStrType() {
		return strType;
	}

	public void setStrType(String strType) {
		this.strType = strType;
	}

	public String getStrProType() {
		return strProType;
	}

	public void setStrProType(String strProType) {
		this.strProType = strProType;
	}

	public boolean isPoFlag() {
		return poFlag;
	}

	public void setPoFlag(boolean poFlag) {
		this.poFlag = poFlag;
	}

	public String getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

}