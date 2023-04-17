package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import com.konnect.jpms.select.FillProject;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class CostingReport extends ActionSupport implements ServletRequestAware, IStatements {
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
	String[] f_project;
	
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
	List<FillProject> projectList;
	List<FillClients> clientList;
	
	String strProType;
	boolean poFlag;
	
	String freqENDDATE;
	String qfreqENDDATE;
	
	public String execute() throws Exception {
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		

		UtilityFunctions uF = new UtilityFunctions();
		request.setAttribute(PAGE, "/jsp/task/CostingReport.jsp");
		request.setAttribute(TITLE, "Costing Report");
		strEmpId =(String) session.getAttribute(EMPID);
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		
//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView) {
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}
		
//		orgList = new FillOrganisation(request).fillOrganisation();
		
		if(getF_org() == null) {
			setF_org((String)session.getAttribute(ORGID));
		}
		
		checkProjectOwner(uF);
		loadCostingReport(uF);
		getCostingReport(uF);
		
		return SUCCESS;

	}
	
	public String loadCostingReport(UtilityFunctions uF) {
		if((getStrStartDate() == null || getStrStartDate().equals("") || getStrStartDate().equalsIgnoreCase("null")) && (getStrEndDate() == null || getStrEndDate().equals("") || getStrEndDate().equalsIgnoreCase("null"))) {
			String currDate = uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, DATE_FORMAT);
			String minMaxDate = uF.getCurrentMonthMinMaxDate(currDate, DATE_FORMAT);
			String[] tmpDate = minMaxDate.split("::::");
			setStrStartDate(tmpDate[0]);
			setStrEndDate(tmpDate[1]);
		}
		
		clientList = new FillClients(request).fillClients(false);
		projectList= new ArrayList<FillProject>();
		if(getF_client()!=null)	{
			projectList= new FillProject(request).fillProjects(getF_client());
		} else {
			projectList= new FillProject(request).fillProjects();
		}
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
		
//		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		monthList = new FillMonth().fillMonth();
		paycycleList = new FillPayCycles(request).fillPayCycles(CF, getF_org());
		
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
			sbQuery.append("select * from projectmntnc pmc where project_owners like '%,"+strEmpId+",%' ");
			pst = con.prepareStatement(sbQuery.toString());
//			pst.setInt(1, uF.parseToInt(strEmpId));
		//===end parvez date: 12-10-2022===	
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
	
	
	private void getCostingReport(UtilityFunctions uF) {
		Database db = new Database();
		db.setRequest(request);
		Connection con = null;
		List<List<String>> alOuter = new ArrayList<List<String>>();
		
		PreparedStatement pst = null;
		ResultSet rs  = null;
		
		try {
			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			
			Map<String, String> hmWorkLocation = CF.getWLocationMap(con, null, request, null);
			if(hmWorkLocation == null) hmWorkLocation = new HashMap<String, String>();
			
			Map<String, String> hmDept = CF.getDeptMap(con);
//			System.out.println("hmDept ===>> " + hmDept);
			if(hmDept == null) hmDept = new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select sum(received_amount) / (sum(exchange_rate) / count(exchange_rate)) as received_amount, " +
				"pa.pro_id from promntc_bill_amt_details pa where pro_id > 0 ");
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and entry_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery.append(" group by pa.pro_id ");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("pst==>>"+pst);
			Map<String, String> hmAmountReceived = new HashMap<String, String>();
			while(rs.next()) {
				hmAmountReceived.put(rs.getString("pro_id"), uF.formatIntoOneDecimalWithOutComma(rs.getDouble("received_amount")));
			}
			rs.close();
			pst.close();
				
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(oc_invoice_amount) as invoice_amount, pro_id from promntc_invoice_details where pro_id > 0 ");
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and invoice_generated_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery.append(" group by pro_id ");
			pst = con.prepareStatement(sbQuery.toString());		
			rs = pst.executeQuery();
			Map<String, String> hmProOutstandingAmount = new HashMap<String, String>();
			Map<String, String> hmInvoiceAmount = new HashMap<String, String>();
			while(rs.next()) { 
				double dblAmt = uF.parseToDouble(rs.getString("invoice_amount")) - uF.parseToDouble(hmAmountReceived.get(rs.getString("pro_id")));
				hmProOutstandingAmount.put(rs.getString("pro_id"), ""+uF.formatIntoOneDecimalWithOutComma(dblAmt)); 
				hmInvoiceAmount.put(rs.getString("pro_id"), uF.formatIntoOneDecimalWithOutComma(rs.getDouble("invoice_amount")));
			}
			rs.close();
			pst.close();
			
			/*Map<String, String> hmProjectionAmnt = new HashMap<String, String>();
			Map<String, String> hmProNonOutstandingAmount = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(billing_amount) as billing_amount, p.pro_id from projectmntnc p, projectmntnc_frequency pf where " +
					" billing_type ='F' and p.pro_id = pf.pro_id ");
			if(getStrStartDate() != null && !getStrStartDate().equals("From Date") && getStrEndDate() != null && !getStrEndDate().equals("To Date")) {
				sbQuery.append(" and ((freq_start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and freq_end_date >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (freq_start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and freq_start_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (freq_start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and freq_end_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (freq_start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and freq_end_date >= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (freq_end_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and freq_end_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "')) ");
			}
			sbQuery.append(" group by p.pro_id ");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()) {
				hmProjectionAmnt.put(rs.getString("pro_id"), uF.formatIntoOneDecimalWithOutComma(rs.getDouble("billing_amount")));
				double dblAmt = uF.parseToDouble(rs.getString("billing_amount")) - uF.parseToDouble(hmInvoiceAmount.get(rs.getString("pro_id")));
				hmProNonOutstandingAmount.put(rs.getString("pro_id"), ""+uF.formatIntoOneDecimalWithOutComma(dblAmt)); 
				
			}
			rs.close();
			pst.close();*/

			//employee costing
			sbQuery =new StringBuilder();
			Map<String, String> hmProjectData =  new HashMap<String, String>();
			Map<String, List<String>> hmClientProId = new HashMap<String, List<String>>();
			
			sbQuery.append("select cd.client_id, cd.client_name, p.pro_id, p.pro_name, p.org_id, p.wlocation_id, p.department_id, p.start_date," +
					" p.deadline, billing_kind, billing_cycle_day, billing_cycle_weekday, billing_amount, p.curr_id from client_details cd," +
					" projectmntnc p where cd.client_id = p.client_id ");
			if(isPoFlag() && uF.parseToInt(getStrProType()) == 2) {
			//===start parvez date: 13-10-2022===	
//				sbQuery.append(" and p.project_owner="+uF.parseToInt(strEmpId));
				sbQuery.append(" and p.project_owners like '%,"+strEmpId+",%' ");
			//===end parvez date: 13-10-2022===	
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
			if(getF_project()!=null && getF_project().length>0 ){
				sbQuery.append(" and p.pro_id in ("+StringUtils.join(getF_project(), ",")+") ");
			}
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (deadline >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "')) ");
				}
			sbQuery.append(" order by client_name, pro_id desc ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			List<String> alInner1 = new ArrayList<String>();
			List<String> alInner = new ArrayList<String>();
			while(rs.next()) {
				alInner1 = hmClientProId.get(rs.getString("client_id"));
				if(alInner1 == null) alInner1 = new ArrayList<String>();
				alInner1.add(rs.getString("pro_id"));
				alInner.add(rs.getString("pro_id"));
				hmClientProId.put(rs.getString("client_id"), alInner1);
				hmProjectData.put(rs.getString("pro_id")+"_CLIENT", rs.getString("client_name"));
				hmProjectData.put(rs.getString("pro_id")+"_PRO_NAME", rs.getString("pro_name"));
				hmProjectData.put(rs.getString("pro_id")+"_DEPART_NAME", hmDept.get(rs.getString("department_id")));
				hmProjectData.put(rs.getString("pro_id")+"_LOCATION_NAME", hmWorkLocation.get(rs.getString("wlocation_id")));
				hmProjectData.put(rs.getString("pro_id")+"_CURR_ID", rs.getString("curr_id"));
				
				hmProjectData.put(rs.getString("pro_id")+"_START_DATE", rs.getString("start_date"));
				hmProjectData.put(rs.getString("pro_id")+"_DEADLINE", rs.getString("deadline"));
				
				hmProjectData.put(rs.getString("pro_id")+"_BILL_FREQ", rs.getString("billing_kind"));
				hmProjectData.put(rs.getString("pro_id")+"_FREQ_DAY", rs.getString("billing_cycle_day"));
				hmProjectData.put(rs.getString("pro_id")+"_WEEKDAY", rs.getString("billing_cycle_weekday"));
				hmProjectData.put(rs.getString("pro_id")+"_BILLING_AMT", rs.getString("billing_amount"));
			}
			rs.close();
			pst.close();
						
			Map<String, String> hmProjectionAmnt = new HashMap<String, String>();
			Map<String, String> hmProNonOutstandingAmount = new HashMap<String, String>();
			
			Date currDate = uF.getCurrentDate(CF.getStrTimeZone());
			Date endDate = uF.getDateFormatUtil(getStrEndDate(), DATE_FORMAT);
			Date startDate = uF.getDateFormatUtil(getStrStartDate(), DATE_FORMAT);
			
			for(int i=0;i<alInner.size() && alInner!=null && !alInner.isEmpty();i++) {
				String proId = alInner.get(i);
//				System.out.println("proId==>"+proId +"\tbilling kind==>"+hmProjectData.get(proId+"_BILL_FREQ"));
				Date proStDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_START_DATE"), DBDATE);
				Date proEdDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE);
				
				String deadlineDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
				Date deadline = null;
				
				if(deadlineDate != null || !deadlineDate.equals("")){
					deadline = uF.getDateFormatUtil(deadlineDate, DATE_FORMAT);
				}
//....................................................................................................................................................
					setQfreqENDDATE(null);
					int intMonths = 0;
					int intCount = 1;
					if(endDate != null && startDate != null ){
//						System.out.println("proStart==>"+ proStDate+"\tendDate==>"+getStrEndDate()+"deadline==>"+deadlineDate);
						if(currDate.after(endDate) && (endDate.before(deadline))){
//							System.out.println("1 if");
							intMonths = uF.getMonthsDifference(uF.getDateFormat(getStrStartDate(), DATE_FORMAT), endDate);
						}else if((currDate.after(deadline)) && (endDate.after(deadline) || endDate.equals(deadline))){
//							System.out.println("2 ifs");
							intMonths = uF.getMonthsDifference(uF.getDateFormat(getStrStartDate(), DATE_FORMAT), deadline);
						}else{
//							System.out.println("3 if");
							intMonths = uF.getMonthsDifference(uF.getDateFormat(getStrStartDate(), DATE_FORMAT), uF.getCurrentDate(CF.getStrTimeZone()));
						}
					
//......................................................................................................................................................
					
						if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("A")) {
							intCount = intMonths/12;
							intCount++;
						}else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("H")) {
							intCount = intMonths/6;
							intCount++;
						}else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("Q")) {
							intCount = intMonths/3;
							intCount++;
						} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
							intCount = intMonths;
						} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
							intCount = (intMonths * 2);
						} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("W")) {
							String strDays ="";
							if(endDate != null){
								if(currDate.after(endDate)){
									strDays = uF.dateDifference(getStrStartDate(), DATE_FORMAT,uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT),DATE_FORMAT);
								}else{
									
									strDays = uF.dateDifference(getStrStartDate(), DATE_FORMAT, uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE);
								}
							}
							intCount = (uF.parseToInt(strDays) / 7);
							intCount++;
						}else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")){
							if(intMonths < 0){
								 intCount = 0;
							}
						}
//						System.out.println("intMonths ==>"+ intMonths + "\t --- intCount ===>> " + intCount);
						double dblBillAmount = 0;
						setFreqENDDATE(null);
						
						int freqEndCnt = 0;
						for(int j=0; j<intCount; j++) {
							String newStDate = getNewProjectStartDate(con, uF, proId, getFreqENDDATE());
						
							if(newStDate == null || newStDate.equals("")) {
									newStDate = getStrStartDate();
							}
//							System.out.println("newStDate ===>> " + newStDate);
							boolean frqFlag = false;
							String freqEndDate = strEndDate;
								if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
									freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
								} else {
									if(proStDate.before(startDate) || proStDate.before(endDate)){
										if(uF.parseToInt(hmProjectData.get(proId+"_FREQ_DAY")) > 0) {
			//								System.out.println("freqDay==>"+hmProjectData.get(proId+"_FREQ_DAY"));
											freqEndDate = hmProjectData.get(proId+"_FREQ_DAY") + "/" + uF.getDateFormat(newStDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
											freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
											Date stDate = uF.getDateFormatUtil(newStDate, DATE_FORMAT);
											Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
											
//											System.out.println("innerList.get(4) ===>> " + uF.parseToInt(hmProjectData.get(proId+"_FREQ_DAY")));
			//								System.out.println("freqDate ===>> " + freqDate + " stDate ===>> " + stDate);
											int dayCycle = uF.parseToInt(hmProjectData.get(proId+"_FREQ_DAY"));
											if(freqDate.after(stDate)) {
												frqFlag = true;
											}
//											System.out.println("frqFlag ===>> " + frqFlag+" -- proId ===>> " + proId);
											if(frqFlag) {
												if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
													freqEndDate = freqEndDate;
												}else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
													//freqEndDate = uF.getDateFormat(uF.getFutureDate(newStDate, 15)+"", DBDATE, DATE_FORMAT);
													freqEndDate = freqEndDate;
													String freqEndDate1 = freqEndDate;
						
													int startDateMonth = uF.parseToInt(uF.getDateFormat(newStDate, DATE_FORMAT, "MM"));
													int freqDateMonth = uF.parseToInt(uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM"));
													if(startDateMonth==freqDateMonth){
						
														long diff = uF.getDateDiffinDays(stDate,freqDate);
														 if(diff>14){
															freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 14)+"", DBDATE, DATE_FORMAT);
														}
													}
												}
											} else {
													if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
													//	freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
				//										System.out.println("freqEndDate in else ===>> " + freqEndDate +" -- proId ===>> " + proId);
														
														if(dayCycle == 1){
															int month = uF.parseToInt(uF.getDateFormat(newStDate, DATE_FORMAT, "MM"));
															int year = uF.parseToInt(uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy"));
															int days = uF.getNoOfDaysMonth(year, month-1);
															if(days == 31){
																freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 30)+"", DBDATE, DATE_FORMAT);
															}else if(days == 30){
																freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 29)+"", DBDATE, DATE_FORMAT);
															}else if(days == 29){
																freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 28)+"", DBDATE, DATE_FORMAT);
															}else if(days == 28){
																freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 27)+"", DBDATE, DATE_FORMAT);
															}
																
														}else{
															freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
														}
														String freqEndDate1 = dayCycle  + "/" + uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
														freqEndDate1 = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate1, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
														
														Date newfreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
														Date freqDate1 = uF.getDateFormatUtil(freqEndDate1, DATE_FORMAT);
														
														if(freqDate1.after(stDate) && (freqDate1.before(newfreqDate) || freqDate1.after(newfreqDate))) {
															freqEndDate = freqEndDate1;
														}
													} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
														//freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(getStrStartDate(), 15)+"", DBDATE, DATE_FORMAT);
			
														if(freqEndDate.equals(newStDate)){
			//												System.out.println("in equal");
															freqEndDate = newStDate;
														}else{
															freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 14)+"", DBDATE, DATE_FORMAT);
															int startDateYear = uF.parseToInt(uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy"));
															int startDateMonth = uF.parseToInt(uF.getDateFormat(newStDate, DATE_FORMAT, "MM"));
															int freqDateMonth = uF.parseToInt(uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM"));
														
															if(startDateMonth!=freqDateMonth){
																int noOfDays = uF.getNoOfDaysMonth(startDateYear, startDateMonth-1);
																if(noOfDays == 31){
																	freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 15)+"", DBDATE, DATE_FORMAT);
																}else if(noOfDays == 29){
																	freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 13)+"", DBDATE, DATE_FORMAT);
																}else if(noOfDays == 28){
																	freqEndDate = uF.getDateFormat(uF.getBiweeklyDate(newStDate, 12)+"", DBDATE, DATE_FORMAT);
																}
															}
														}
														String freqEndDate1 =  dayCycle+ "/" + uF.getDateFormat(freqEndDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
														String freqEndDate2 = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate1, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
														
													    Date newfreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
														Date freqDate1 = uF.getDateFormatUtil(freqEndDate2, DATE_FORMAT);
														
													    if((freqDate1.after(stDate))  && (freqDate1.before(newfreqDate) )){
														    	freqEndDate = freqEndDate2	;
													    }
													}
											}
											Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
											if(newFreqDate.after(endDate) && (endDate.before(deadline) || endDate.equals(deadline))) {
												freqEndDate = getStrEndDate();
											}
//											else if(newFreqDate.after(deadline)){
//												freqEndDate = deadlineDate;
//											}
										}
							
										if(hmProjectData.get(proId+"_WEEKDAY") != null && !hmProjectData.get(proId+"_WEEKDAY").equals("") && hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("W")) {
											freqEndDate = uF.getDateFormat(uF.getDateOfPassedDay(newStDate, hmProjectData.get(proId+"_WEEKDAY"))+"", DBDATE, DATE_FORMAT);
											freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
											Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
			//								System.out.println("freqEndDate==>"+newFreqDate);
											if(newFreqDate.after(deadline)){
												freqEndDate = deadlineDate;
											}
											
										}
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("Q")){
											freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(newStDate, 3)+"", DBDATE, DATE_FORMAT);
											freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
											Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
			//								System.out.println("freqEndDate==>"+newFreqDate);
											if(newFreqDate.after(deadline)){
												freqEndDate = deadlineDate;
											}
											
										}
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("H")){
											freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(newStDate, 6)+"", DBDATE, DATE_FORMAT);
											freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
											Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
			//								System.out.println("freqEndDate==>"+newFreqDate);
											if(newFreqDate.after(deadline)){
												freqEndDate = deadlineDate;
											}
											
										}
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("A")){
											freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(newStDate, 12)+"", DBDATE, DATE_FORMAT);
											freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
											Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
			//								System.out.println("freqEndDate==>"+newFreqDate);
											if(newFreqDate.after(deadline)){
												freqEndDate = deadlineDate;
											}
										}	
									}
							}
							    	Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
		//							System.out.println("getStrEndDate==>"+getStrEndDate());
									if(newFreqDate.after(endDate)) {
										freqEndDate = getStrEndDate();
									}
//									System.out.println("freqStartDate  ===>> " + newStDate + "freqEndDate 2 ===>> " + freqEndDate);
		//							System.out.println("proStDate ===>> " + proStDate + " -- proEdDate ======>> " + proEdDate +" -- newFreqDate ===>> " + newFreqDate +" -- proId ===>> " + proId);
									setFreqENDDATE(freqEndDate);
									
									if(proStDate != null && proEdDate != null && (proStDate.before(newFreqDate) || proStDate.equals(newFreqDate)) && (proEdDate.after(newFreqDate) || proEdDate.equals(newFreqDate))) {
//										System.out.println("dblBillAmount1==>"+hmProjectData.get(proId+"_BILLING_AMT"));
										dblBillAmount += uF.parseToInt(hmProjectData.get(proId+"_BILLING_AMT"));
									}
							  }
								
//								System.out.println("dblBillAmount2==>"+dblBillAmount);
								hmProjectionAmnt.put(proId, uF.formatIntoOneDecimalWithOutComma(dblBillAmount));
								double dblAmt = dblBillAmount - uF.parseToDouble(hmInvoiceAmount.get(proId));
								if(dblAmt < 0) {
									dblAmt = 0;
								}
								hmProNonOutstandingAmount.put(proId, ""+uF.formatIntoOneDecimalWithOutComma(dblAmt)); 
					}
			}
			
			
			Iterator<String> it = hmClientProId.keySet().iterator();
			while (it.hasNext()) {
				String clientId = it.next();
				List<String> alList = hmClientProId.get(clientId);
				
				int count = 0;
//				double totalCost =0;
//				double totalDays = 0;
				Map<String, String> hmProwiseHrs = new LinkedHashMap<String, String>();
				for(int i=0; alList != null && !alList.isEmpty() && i< alList.size(); i++) {
					double proTotCost =0;
					pst = con.prepareStatement("select * from project_emp_details where pro_id in ("+alList.get(i)+")");
					rs = pst.executeQuery();
					List<String> alEmpIds = new ArrayList<String>();
					Map<String, String> hmEmpRatePerDay = new LinkedHashMap<String, String>();
					while(rs.next()) {
						alEmpIds.add(rs.getString("emp_id"));
						hmEmpRatePerDay.put(rs.getString("emp_id"), rs.getString("emp_actual_rate_per_day"));		
					}
					rs.close();
					pst.close();
					Map<String, String> hmEmpwiseHrs = new LinkedHashMap<String, String>();
					 
					if(alEmpIds != null && !alEmpIds.isEmpty()){
						count++;
					}
					for(int j=0;alEmpIds != null && !alEmpIds.isEmpty() && j< alEmpIds.size(); j++) {

						pst = con.prepareStatement("select task_id, pro_id from activity_info where pro_id = ? and resource_ids like '%,"+alEmpIds.get(j)+",%' ");
						pst.setInt(1, uF.parseToInt(alList.get(i)));
						rs = pst.executeQuery();

						StringBuilder sbTaskIds = null;
						while(rs.next()) {
							if(sbTaskIds == null) {
								sbTaskIds = new StringBuilder();
								sbTaskIds.append(rs.getString("task_id"));
							} else {
								sbTaskIds.append(","+rs.getString("task_id"));
							}
						}
						rs.close();
						pst.close();
				
						if(sbTaskIds != null && !sbTaskIds.equals("")) {
							StringBuilder sbQue = new StringBuilder();
							sbQue.append("select sum(actual_hrs) as hrs from task_activity where emp_id = ? and activity_id in ("+sbTaskIds.toString()+") ");
							if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
								sbQue.append(" and task_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
							}
							pst = con.prepareStatement(sbQue.toString());
//							pst = con.prepareStatement("select sum(actual_hrs) as hrs, emp_id from task_activity where emp_id = ? and activity_id  in ("+sbTaskIds.toString()+") group by emp_id");
							pst.setInt(1, uF.parseToInt(alEmpIds.get(j)));
							rs = pst.executeQuery();
							while(rs.next()) {
								double actHrs = uF.parseToDouble(hmEmpwiseHrs.get(alEmpIds.get(j)));
								actHrs += rs.getDouble("hrs");
								hmEmpwiseHrs.put(alEmpIds.get(j), ""+actHrs);
								
								double proActHrs = uF.parseToDouble(hmProwiseHrs.get(alList.get(i)));
								proActHrs += rs.getDouble("hrs");
								hmProwiseHrs.put(alList.get(i), ""+proActHrs);
								
//								double clientActHrs = uF.parseToDouble(hmClientwiseHrs.get(clientId));
//								clientActHrs += rs.getDouble("hrs");
//								hmClientwiseHrs.put(clientId, ""+clientActHrs);
							}
							rs.close();
							pst.close();
						}
						double actDays = uF.parseToDouble(hmProwiseHrs.get(alList.get(i))) / 8;
						double actCost = actDays * uF.parseToDouble(hmEmpRatePerDay.get(alEmpIds.get(j)));
						
						proTotCost += actCost;
					}
					
					Map<String, String> hmCurr = hmCurrencyMap.get(hmProjectData.get(alList.get(i)+"_CURR_ID"));
					
					List<String> proList = new ArrayList<String>();
					proList.add(hmProjectData.get(alList.get(i)+"_CLIENT")); //0
					proList.add(hmProjectData.get(alList.get(i)+"_PRO_NAME")); //1
					proList.add(uF.showData(hmProjectData.get(alList.get(i)+"_DEPART_NAME"), "") ); //2
					proList.add(uF.showData(hmProjectData.get(alList.get(i)+"_LOCATION_NAME"), "") ); //3
					
					proList.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(hmProOutstandingAmount.get(alList.get(i)))), "0"));//4
					proList.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(hmProNonOutstandingAmount.get(alList.get(i)))), "0"));//5
					proList.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(hmAmountReceived.get(alList.get(i)))), "0"));//6
					proList.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(uF.formatIntoOneDecimalWithOutComma(uF.parseToDouble(hmProjectionAmnt.get(alList.get(i)))), "0"));//7
					proList.add(hmCurr.get("SHORT_CURR")+" "+uF.showData(uF.formatIntoOneDecimalWithOutComma(proTotCost), "0"));//8
					alOuter.add(proList);
				}
			}
					
			request.setAttribute("alOuter", alOuter);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public double getProjectDblBillAmount(Connection con,UtilityFunctions uF,String strStartDate,String strEndDate,Map<String,String> hmProjectData,String proId){
		double dblBillAmount = 0 ;
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try{
			   StringBuilder sb = new StringBuilder();
			   sb.append("select * from projectmntnc_frequency where pro_id= ? ");
			   sb.append(" and freq_start_date  between '" + uF.getDateFormat(strStartDate, DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE) + "'"
					   		+" and freq_end_date  between '" + uF.getDateFormat(strStartDate, DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE) + "' ");
			   pst = con.prepareStatement(sb.toString());
			   pst.setInt(1, uF.parseToInt(proId));
			   rs = pst.executeQuery();
			   while(rs.next()){
//				   System.out.println("dblBillAmount in method ==>"+hmProjectData.get(proId+"_BILLING_AMT"));
				   dblBillAmount += uF.parseToInt(hmProjectData.get(proId+"_BILLING_AMT")); 
			   }
			   rs.close();
			   pst.close();
			  
		}catch (SQLException e) {
			e.printStackTrace();
		} 
		return dblBillAmount;
	}
 
	private String getNewProjectStartDate(Connection con, UtilityFunctions uF, String proId, String freqEndDate) {

		String newStDate = null;
		try {
			if(freqEndDate != null) {
				newStDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), 1)+"", DBDATE, DATE_FORMAT);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return newStDate;
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
		
		alFilter.add("PROJECT");
		if(getF_project()!=null) {
				String strProject="";
				int k=0;
				for(int i=0; projectList!=null && i<projectList.size();i++) {
					for(int j=0;j<getF_project().length;j++) {
						if(getF_project()[j].equals(projectList.get(i).getId())) {
							if(k==0) {
								strProject=projectList.get(i).getName();
							} else {
								strProject+=", "+projectList.get(i).getName();
							}
							k++;
						}
					}
				}
			if(strProject!=null && !strProject.equals("")) {
				hmFilter.put("PROJECT", strProject);
			} else {
				hmFilter.put("PROJECT", "All projects");
			}
		} else {
			hmFilter.put("PROJECT", "All Project");
		}
		
		alFilter.add("FROM_TO");
		String strFdt = "-";
		String strEdt = "-";
		if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null")) {
			strFdt = uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat());
		}
		if(getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
			strEdt = uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat());
		}
		hmFilter.put("FROM_TO",  strFdt+" - "+ strEdt);
		
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
	public String[] getF_project() {
		return f_project;
	}
	public void setF_project(String[] f_project) {
		this.f_project = f_project;
	}

	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	public List<FillProject> getProjectList() {
		return projectList;
	}
	public void setProjectList(List<FillProject> projectList) {
		this.projectList = projectList;
	}

	public String getFreqENDDATE() {
		return freqENDDATE;
	}

	public void setFreqENDDATE(String freqENDDATE) {
		this.freqENDDATE = freqENDDATE;
	}

	public String getQfreqENDDATE() {
		return qfreqENDDATE;
	}

	public void setQfreqENDDATE(String qfreqENDDATE) {
		this.qfreqENDDATE = qfreqENDDATE;
	}
	
}