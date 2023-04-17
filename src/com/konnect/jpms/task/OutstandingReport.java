package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

public class OutstandingReport extends ActionSupport implements ServletRequestAware, IStatements {
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	String strEmpId;
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
	String monthFinancialYear;
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
	
	public String execute() throws Exception {
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;

		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/task/OutstandingReport.jsp");
		request.setAttribute(TITLE, "Outstanding Report");
		strEmpId =(String) session.getAttribute(EMPID);
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		
		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView){
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
//		orgList = new FillOrganisation(request).fillOrganisation();
		
		if(getF_org() == null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		checkProjectOwner(uF);
		
		getOutstandingReport(uF);
		
		return loadOutstandingReport(uF);

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
		getSelectedFilter(uF);
		 
		return SUCCESS;
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
		//===start parvez date: 13-10-2022===	
//			sbQuery.append("select * from projectmntnc pmc where project_owner=?");
			sbQuery.append("select * from projectmntnc pmc where project_owners like '%,"+strEmpId+",%'");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strEmpId));
		//===end parvez date: 13-10-2022===	
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			if(rs.next()) {
				poFlag = true;
			}
			rs.close();
			pst.close();
			
			setPoFlag(poFlag);
			
			if(poFlag && uF.parseToInt(getStrProType()) == 0){
				setStrProType("2");
			}
		} catch (Exception e) {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	
	private void getOutstandingReport(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		List<List<String>> alOuter = new ArrayList<List<String>>();
		
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try {
			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			
			Map<String, Map<String, String>> hmWorkLocation = CF.getWorkLocationMap(con);
			if(hmWorkLocation==null)hmWorkLocation = new HashMap<String, Map<String,String>>();
			
			Map<String, String> hmDept = CF.getDeptMap(con);
			if(hmDept==null)hmDept = new HashMap<String, String>();
			
//			Map hmCurrMap = CF.getCurrencyDetails(con);
			
			if(getSelectOne() != null && getSelectOne().equals("1") && (getStrStartDate() == null || getStrStartDate().equals("")) && (getStrEndDate() == null || getStrEndDate().equals(""))) {
				
				Date currDate=uF.getCurrentDate(CF.getStrTimeZone());
				
				String startdate="01/"+uF.getDateFormat(""+currDate, DBDATE, "MM")+"/"+uF.getDateFormat(""+currDate, DBDATE, "yyyy");
				
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "MM"))-1);
				calendar.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(""+currDate, DBDATE, "yyyy")));
				calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));

				Date date = calendar.getTime();
				DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
				String endDate=DATE_FORMAT.format(date);
				
				setStrStartDate(startdate);
				setStrEndDate(endDate);
			} else if(getSelectOne() != null && getSelectOne().equals("2")) {
				String[] strFinancialYears = null;
				if (getFinancialYear() != null) {
					strFinancialYears = getFinancialYear().split("-");
					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					
					setStrStartDate(strFinancialYears[0]);
					setStrEndDate(strFinancialYears[1]);
				} else {
					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
					setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					
					setStrStartDate(strFinancialYears[0]);
					setStrEndDate(strFinancialYears[1]);
				}
			} else if(getSelectOne() != null && getSelectOne().equals("3")) {
				
				int nselectedMonth = uF.parseToInt(getStrMonth());
//				int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
				int nFYSMonth = 0;
				int nFYSDay = 0;
				String[] strFinancialYears = null;
//				System.out.println("getMonthFinancialYear() ===>> " + getMonthFinancialYear());
				if (getMonthFinancialYear() != null) {
					strFinancialYears = getMonthFinancialYear().split("-");
					setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
					nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
				} else {
					strFinancialYears = CF.getFinancialYear(con, uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
					setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
					nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
					nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
				}
				
				Calendar cal = GregorianCalendar.getInstance();
				cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
				cal.set(Calendar.DATE, nFYSDay);
				if(nselectedMonth>=nFYSMonth){
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "yyyy")));
				}else{
					cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, "yyyy")));
				}
				
				int nMonthStart = cal.getActualMinimum(Calendar.DATE);
				int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
				
				setStrStartDate(nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
				setStrEndDate(nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
				
			} else if(getSelectOne() != null && getSelectOne().equals("4")) {
				String[] strPayCycleDates = null;
				if (getPaycycle() != null) {
					strPayCycleDates = getPaycycle().split("-");
					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
					
					setStrStartDate(strPayCycleDates[0]);
					setStrEndDate(strPayCycleDates[1]);
				} else {
					strPayCycleDates = CF.getCurrentPayCycleByOrg(CF.getStrTimeZone(), CF, getF_org(),request);
					setPaycycle(strPayCycleDates[0] + "-" + strPayCycleDates[1] + "-" + strPayCycleDates[2]);
					
					setStrStartDate(strPayCycleDates[0]);
					setStrEndDate(strPayCycleDates[1]);
				}
			}


			/*pst = con.prepareStatement("select sum(received_amount) as received_amount, sum(exchange_rate) as exchange_rate, " +
				"count(exchange_rate) as exchange_rate_cnt, pa.pro_id, invoice_id from promntc_bill_amt_details pa group by pa.pro_id,invoice_id ");
			rs = pst.executeQuery();
			Map<String, String> hmAmountReceived = new HashMap<String, String>();
			while(rs.next()) {
				int exchange_rate_cnt = rs.getInt("exchange_rate_cnt");
				double exchange_rate = 0.0d;
				if(exchange_rate_cnt>0) {
					exchange_rate = rs.getDouble("exchange_rate") / exchange_rate_cnt;
				}
				double received_amount = 0.0d;
				if(exchange_rate>0) {
					received_amount = rs.getInt("received_amount")/exchange_rate;
				}
				hmAmountReceived.put(rs.getString("invoice_id"), uF.formatIntoOneDecimalWithOutComma(received_amount));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select entry_date, invoice_id from promntc_bill_amt_details pa order by bill_id");
			rs = pst.executeQuery();
			Map<String, String> hmAmountReceivedDate = new HashMap<String, String>();
			while(rs.next()) {
				hmAmountReceivedDate.put(rs.getString("invoice_id"), rs.getString("entry_date"));
			}
			rs.close();
			pst.close();
			
			
			Map<String, String> hmClientDetails = CF.getProjectClientMap(con, uF);
			Map<String, String> hmProjectName = CF.getProjectNameMap(con);
			Map<String, String> hmProServiceName = CF.getProjectServicesMap(con, false);
			Map<String, String> hmSBUName = CF.getServicesMap(con, false);
			
			StringBuilder sbQuery = new StringBuilder();			
			sbQuery.append("select promntc_invoice_id, invoice_code, invoice_amount, pi.pro_id, pi.client_id, service, pi.curr_id, pro_name, " +
				"project_description, pi.wlocation_id,department_id,invoice_generated_date,pi.curr_id from promntc_invoice_details pi, " +
				"projectmntnc p where p.pro_id = pi.pro_id and pi.pro_id > 0 ");
			
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
				sbQuery.append(" and p.project_owner="+uF.parseToInt(strEmpId));
			}
			
			if(strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) {
				sbQuery.append(" and invoice_generated_by = "+ uF.parseToInt(strEmpId) +" ");
			}
			if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0) || (getF_level() != null && getF_level().length>0)) {
				sbQuery.append(" and pi.pro_id in (select pro_id from projectmntnc where pro_id>0 ");
			}
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and p.org_id in ("+getF_org()+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and p.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and p.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and p.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and p.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service() != null && getF_service().length>0) {
				sbQuery.append(" and p.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			if(getF_project_service() != null && getF_project_service().length>0) {
				String services = uF.getConcateData(getF_project_service());
				sbQuery.append(" and p.service in ("+services+") ");
			}
			if(getF_client() != null && getF_client().length>0) {
				sbQuery.append(" and p.client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			if(uF.parseToInt(getF_org())>0 || (getF_strWLocation() != null && getF_strWLocation().length>0) || (getF_department() != null && getF_department().length>0) ||  (getF_level() != null && getF_level().length>0)) {
				sbQuery.append(" ) ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and to_date(invoice_generated_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery.append(" order by pi.pro_id,promntc_invoice_id ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			
			List<String> alInner = new ArrayList<String>();
//			int count=0;
			while(rs.next()){
//				count++;
				alInner = new ArrayList<String>();
				Map<String, String> hmCurr = hmCurrencyMap.get(rs.getString("curr_id"));
				
				alInner.add(rs.getString("invoice_code"));
				alInner.add(rs.getString("pro_name"));
				alInner.add(hmClientDetails.get(rs.getString("client_id")));
				alInner.add(uF.showData(hmSBUName.get(rs.getString("service")), ""));
				
				alInner.add(uF.showData(hmDept.get(rs.getString("department_id")), "") );
				Map<String, String> hm = hmWorkLocation.get(rs.getString("wlocation_id"));
				if(hm==null)hm = new HashMap<String, String>();
				alInner.add(uF.showData(hm.get("WL_NAME"), "")); //6
				alInner.add(uF.showData(rs.getString("project_description"),"-")); //7
				
				alInner.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(rs.getString("invoice_amount"),"0")); //8
				alInner.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(hmAmountReceived.get(rs.getString("promntc_invoice_id")),"0")); //9 
				
				double dblAmt = uF.parseToDouble(rs.getString("invoice_amount")) - uF.parseToDouble(hmAmountReceived.get(rs.getString("promntc_invoice_id")));
				alInner.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoOneDecimalWithOutComma(dblAmt)); //10
				
				alInner.add(uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat())); //11
				
				String amtReceivedDate = "";
				if(hmAmountReceivedDate != null && hmAmountReceivedDate.get(rs.getString("promntc_invoice_id")) != null) {
					amtReceivedDate = hmAmountReceivedDate.get(rs.getString("promntc_invoice_id"));
				} else {
					amtReceivedDate = ""+uF.getCurrentDate(CF.getStrTimeZone());
				}
				String strCnt = uF.dateDifference(rs.getString("invoice_generated_date"), DBDATE, amtReceivedDate, DBDATE);
				
				alInner.add(strCnt+" days"); //12
				
				alOuter.add(alInner);
			}
			rs.close();
			pst.close();*/
			
	//===start parvez date: 03-03-2022===
		pst = con.prepareStatement("select received_amount, exchange_rate, " +
			"count(exchange_rate) as exchange_rate_cnt, invoice_ids from promntc_bill_amt_details pa group by bill_id ");
		rs = pst.executeQuery();
		Map<String, String> hmAmountReceived = new HashMap<String, String>();
		Map<String,String> hmTempReceiveAmt = new HashMap<String, String>();
		while(rs.next()) {
			
			String[] strInvIds = null;
			if(rs.getString("invoice_ids") != null){
				strInvIds = rs.getString("invoice_ids").split(",");
			}
				
			for(int i=0; strInvIds!=null && i<strInvIds.length; i++){
				double totExchange_rate = rs.getDouble("exchange_rate")+uF.parseToDouble(hmTempReceiveAmt.get(strInvIds[i]+"_EXCH_RATE"));
				int exchange_rate_cnt = rs.getInt("exchange_rate_cnt")+uF.parseToInt(hmTempReceiveAmt.get(strInvIds[i]+"_EXCH_RATE_CNT"));
				double totRecvAmt = rs.getDouble("received_amount")+uF.parseToDouble(hmTempReceiveAmt.get(strInvIds[i]+"_RECEIVED"));
				hmTempReceiveAmt.put(strInvIds[i]+"_EXCH_RATE",totExchange_rate+"");
				hmTempReceiveAmt.put(strInvIds[i]+"_EXCH_RATE_CNT",exchange_rate_cnt+"");
				hmTempReceiveAmt.put(strInvIds[i]+"_RECEIVED",totRecvAmt+"");
				
				double exchange_rate = 0.0d;
				if(exchange_rate_cnt>0) {
					exchange_rate = totExchange_rate / exchange_rate_cnt;
				}
				
				double received_amount = 0.0d;
				if(exchange_rate>0) {
					received_amount = totRecvAmt/exchange_rate;
				}
				hmAmountReceived.put(strInvIds[i], uF.formatIntoOneDecimalWithOutComma(received_amount));
			}
			
			
		}
		rs.close();
		pst.close();
		
		pst = con.prepareStatement("select entry_date, invoice_ids from promntc_bill_amt_details pa order by bill_id");
		rs = pst.executeQuery();
		Map<String, String> hmAmountReceivedDate = new HashMap<String, String>();
		while(rs.next()) {
			String[] strInsId = null;
			if(rs.getString("invoice_ids")!=null){
				strInsId = rs.getString("invoice_ids").split(",");
			}
			for(int i=0; strInsId!=null && i<strInsId.length; i++){
				if(i>0){
					hmAmountReceivedDate.put(strInsId[i], rs.getString("entry_date"));
				}
			}
			
		}
		rs.close();
		pst.close();
		
		
		Map<String, String> hmClientDetails = CF.getProjectClientMap(con, uF);
		Map<String, String> hmProjectName = CF.getProjectNameMap(con);
		Map<String, String> hmProServiceName = CF.getProjectServicesMap(con, false);
		Map<String, String> hmSBUName = CF.getServicesMap(con, false);
		
		StringBuilder sbQuery = new StringBuilder();
			
			Map<String, String> hmProjectDetailsMap = new HashMap<String, String>();
			List<String> alProId = new ArrayList<String>();
			List<String> alInner = new ArrayList<String>();
			sbQuery.append("select pro_id,service,pro_name,department_id,client_id,wlocation_id," +
					" curr_id from projectmntnc where pro_id>0 ");
			
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2){
		//===start parvez date: 13-10-2022===		
//				sbQuery.append(" and project_owner="+uF.parseToInt(strEmpId));
				sbQuery.append(" and project_owners like '%,"+strEmpId+",%'");
		//===end parvez date: 13-10-2022===		
			}
			
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and org_id in ("+getF_org()+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			
			if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service() != null && getF_service().length>0) {
				sbQuery.append(" and sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			if(getF_project_service() != null && getF_project_service().length>0) {
				String services = uF.getConcateData(getF_project_service());
				sbQuery.append(" and service in ("+services+") ");
			}
			if(getF_client() != null && getF_client().length>0) {
				sbQuery.append(" and client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("OR/501--pst="+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				
				alProId.add(rs.getString("pro_id"));
				
				hmProjectDetailsMap.put(rs.getString("pro_id")+"_PRO_NAME", rs.getString("pro_name"));
				hmProjectDetailsMap.put(rs.getString("pro_id")+"_PRO_SERVICE", rs.getString("service"));
				hmProjectDetailsMap.put(rs.getString("pro_id")+"_PRO_DEPARTMENT", rs.getString("department_id"));
				hmProjectDetailsMap.put(rs.getString("pro_id")+"_PRO_CLIENT", rs.getString("client_id"));
				hmProjectDetailsMap.put(rs.getString("pro_id")+"_PRO_LOCATION", rs.getString("wlocation_id"));
				hmProjectDetailsMap.put(rs.getString("pro_id")+"_PRO_CURRENCY", rs.getString("curr_id"));
				
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select promntc_invoice_id,invoice_code,invoice_amount,invoice_generated_date,project_description,pro_ids " +
					" from promntc_invoice_details where ");
			
			for(int i=0; i<alProId.size(); i++){
				sbQuery.append("pro_ids like '%,"+alProId.get(i)+",%'");
				if(i<alProId.size()-1){
					sbQuery.append(" OR ");
				}
			}
			
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and to_date(invoice_generated_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
//			System.out.println("sbQuery===>"+sbQuery);
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			List<String> alInvoiceIds = new ArrayList<String>();
			while(rs.next()){
//				if(!alInvoiceIds.contains(rs.getString("promntc_invoice_id"))){
					String[] projectIds = null;
					
					if(rs.getString("pro_ids")!=null){
						projectIds = rs.getString("pro_ids").split(",");
					}
					
					for(int k=0; projectIds!=null && k<projectIds.length; k++){
						if(k>0 && alProId.contains(projectIds[k])){
							alInner = new ArrayList<String>();
//							System.out.println("CurrencyId="+hmProjectDetailsMap.get(projectIds[k]+"_PRO_CURRENCY"));
//							System.out.println("projectIds[k]="+projectIds[k]);
							Map<String, String> hmCurr = hmCurrencyMap.get(hmProjectDetailsMap.get(projectIds[k]+"_PRO_CURRENCY"));
							
							alInner.add(uF.showData(rs.getString("invoice_code"), "-"));
							alInner.add(hmProjectDetailsMap.get(projectIds[k]+"_PRO_NAME"));
							alInner.add(hmClientDetails.get(hmProjectDetailsMap.get(projectIds[k]+"_PRO_CLIENT")));
							alInner.add(uF.showData(hmSBUName.get(hmProjectDetailsMap.get(projectIds[k]+"_PRO_SERVICE")), ""));
							
							alInner.add(uF.showData(hmDept.get(hmProjectDetailsMap.get(projectIds[k]+"_PRO_DEPARTMENT")), "") );
							Map<String, String> hm = hmWorkLocation.get(hmProjectDetailsMap.get(projectIds[k]+"_PRO_LOCATION"));
							if(hm==null)hm = new HashMap<String, String>();
							alInner.add(uF.showData(hm.get("WL_NAME"), "")); //6
							alInner.add(uF.showData(rs.getString("project_description"),"-")); //7
							
							alInner.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(rs.getString("invoice_amount"),"0")); //8
							alInner.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(hmAmountReceived.get(rs.getString("promntc_invoice_id")),"0")); //9 
							
							double dblAmt = uF.parseToDouble(rs.getString("invoice_amount")) - uF.parseToDouble(hmAmountReceived.get(rs.getString("promntc_invoice_id")));
							alInner.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoOneDecimalWithOutComma(dblAmt)); //10
							
							alInner.add(uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat())); //11
							
							String amtReceivedDate = "";
							if(hmAmountReceivedDate != null && hmAmountReceivedDate.get(rs.getString("promntc_invoice_id")) != null) {
								amtReceivedDate = hmAmountReceivedDate.get(rs.getString("promntc_invoice_id"));
							} else {
								amtReceivedDate = ""+uF.getCurrentDate(CF.getStrTimeZone());
							}
							String strCnt = uF.dateDifference(rs.getString("invoice_generated_date"), DBDATE, amtReceivedDate, DBDATE);
							
							alInner.add(strCnt+" days"); //12
							
							alOuter.add(alInner);
						}
					}
				}
//			}
			rs.close();
			pst.close();
			
			
	//===end parvez date: 03-03-2022===		
			
			
			sbQuery = new StringBuilder();			
			sbQuery.append("select promntc_invoice_id, invoice_code, invoice_amount, pi.pro_id, pi.client_id, service_id, pi.curr_id, pi.wlocation_id," +
					"depart_id,invoice_generated_date,pi.curr_id,other_description from promntc_invoice_details pi where pi.pro_id = 0 and (invoice_type = "+ADHOC_INVOICE+" or" +
					" invoice_type = "+ADHOC_PRORETA_INVOICE+" )");
			
			if((strUserType!=null && strUserType.equalsIgnoreCase(MANAGER)) || (isPoFlag() && uF.parseToInt(getStrProType()) == 2)) {
				sbQuery.append(" and invoice_generated_by = "+ uF.parseToInt(strEmpId) +" ");
			}
			
			if(uF.parseToInt(getF_org())>0 && (getF_strWLocation()==null || getF_strWLocation().length == 0)) {
				String loctionIds = CF.getOrgLocationIds(con, uF, getF_org());
				if(loctionIds != null && loctionIds.trim().length()>0) {
					sbQuery.append(" and wlocation_id in ("+loctionIds+")");
				}
			} else if(getF_strWLocation()!=null && getF_strWLocation().length>0) {
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			
			/*if(getF_project_service() != null && getF_project_service().length>0) {
				sbQuery.append(" and service_id in ("+StringUtils.join(getF_project_service(), ",")+") ");
			}*/
			
			if(getF_client() != null && getF_client().length>0) {
				sbQuery.append(" and client_id in ("+StringUtils.join(getF_client(), ",")+") ");
			}
			
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and to_date(invoice_generated_date::text, 'YYYY-MM-DD') between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			
			sbQuery.append(" order by pi.pro_id,promntc_invoice_id ");
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			
			while(rs.next()){
//				count++;
				
				alInner = new ArrayList<String>();
				Map<String, String> hmCurr = hmCurrencyMap.get(rs.getString("curr_id"));
				
				alInner.add(rs.getString("invoice_code"));
				alInner.add(uF.showData(hmProjectName.get(rs.getString("pro_id")), "-"));
				alInner.add(hmClientDetails.get(rs.getString("client_id")));
				alInner.add(uF.showData(hmProServiceName.get(rs.getString("service_id")), ""));
				
				alInner.add(uF.showData(hmDept.get(rs.getString("depart_id")), "") );
				
				Map<String, String> hm = hmWorkLocation.get(rs.getString("wlocation_id"));
				if(hm==null) hm = new HashMap<String, String>();
				
				alInner.add(uF.showData(hm.get("WL_NAME"), "")); //6
				alInner.add(uF.showData(rs.getString("other_description"), "-")); //7
				
				alInner.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(rs.getString("invoice_amount"),"0")); //8
				alInner.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(hmAmountReceived.get(rs.getString("promntc_invoice_id")),"0")); //9 
				
				double dblAmt = uF.parseToDouble(rs.getString("invoice_amount")) - uF.parseToDouble(hmAmountReceived.get(rs.getString("promntc_invoice_id")));
				alInner.add(hmCurr.get("SHORT_CURR")+" "+uF.formatIntoOneDecimalWithOutComma(dblAmt)); //10
				
				alInner.add(uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, CF.getStrReportDateFormat())); //11
				
				String amtReceivedDate = "";
				if(hmAmountReceivedDate != null && hmAmountReceivedDate.get(rs.getString("promntc_invoice_id")) != null) {
					amtReceivedDate = hmAmountReceivedDate.get(rs.getString("promntc_invoice_id"));
				} else {
					amtReceivedDate = ""+uF.getCurrentDate(CF.getStrTimeZone());
				}
				String strCnt = uF.dateDifference(rs.getString("invoice_generated_date"), DBDATE, amtReceivedDate, DBDATE);
				
				alInner.add(strCnt+" days"); //12
				
				alOuter.add(alInner);
			}
			rs.close();
			pst.close();
			
			request.setAttribute("alOuter", alOuter);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs); 
			db.closeStatements(pst);  
			db.closeConnection(con);
		}
		
	}


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
		
		/*alFilter.add("SERVICE");
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
		*/
		/*alFilter.add("LEVEL");
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
		}*/
		
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
			String strtDate = "";
			String endDate = "";
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("")) {
				strtDate = uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat());
			}
			if(getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				endDate = uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat());
			}
			hmFilter.put("FROMTO", strtDate +" - "+ endDate);
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
			int nFYSDay = 0;
			String[] strFinancialYears = null;
//			System.out.println("getMonthFinancialYear() ===>> " + getMonthFinancialYear());
			if (getMonthFinancialYear() != null) {
				strFinancialYears = getMonthFinancialYear().split("-");
				setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
				nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
			} else {
				strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
				setMonthFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
				nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
			}
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			cal.set(Calendar.DATE, nFYSDay);
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
	

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
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

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
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

	public String[] getF_department() {
		return f_department;
	}

	public List<FillServices> getServiceList() {
		return serviceList;
	}

	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
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

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
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

	public List<FillServices> getProjectServiceList() {
		return projectServiceList;
	}

	public void setProjectServiceList(List<FillServices> projectServiceList) {
		this.projectServiceList = projectServiceList;
	}

	public List<FillClients> getClientList() {
		return clientList;
	}

	public void setClientList(List<FillClients> clientList) {
		this.clientList = clientList;
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

	public String getMonthFinancialYear() {
		return monthFinancialYear;
	}

	public void setMonthFinancialYear(String monthFinancialYear) {
		this.monthFinancialYear = monthFinancialYear;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
		
}