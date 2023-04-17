package com.konnect.jpms.task;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ClusterWiseBudget extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	
	String f_org;
	String financialYear;
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String strStartDate;
	String strEndDate;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillFinancialYears> financialYearList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	
	String exportType;
	String freqENDDATE;
	String qfreqENDDATE;
	String freqNxtENDDATE;
	String qfreqNxtENDDATE;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		
		request.setAttribute(PAGE, "/jsp/task/ClusterWiseBudget.jsp");
		request.setAttribute(TITLE, "Cluster Wise Budget");
		
		if(getF_org()==null || getF_org().trim().equals("")){
			setF_org((String)session.getAttribute(ORGID));
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
		if(getStrLevel() != null && !getStrLevel().equals("")) {
			setF_level(getStrLevel().split(","));
		} else {
			setF_level(null);
		}
		
		viewClusterBudgetReport(uF);
		
		if(getExportType()!= null && getExportType().equalsIgnoreCase("excel")){
			generateClusterBudgetExcelReport(uF);
		}
		
		return loadReport(uF);
	}
	
	public String loadReport(UtilityFunctions uF) {
		
		Map<String, String> hmOrg = CF.getOrgDetails(uF, getF_org(),request);
		if(hmOrg == null) hmOrg = new HashMap<String, String>();
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(), uF);
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	public String viewClusterBudgetReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			con = db.makeConnection(con);
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			
			if (getFinancialYear() != null) {
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
				setStrStartDate(strFinancialYearStart);
				setStrEndDate(strFinancialYearEnd);
				
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
				setStrStartDate(strFinancialYearStart);
				setStrEndDate(strFinancialYearEnd);
			}
			
			
			String[] curStartDate = getStrStartDate().split("/");
			int nextYr = uF.parseToInt(curStartDate[2])+1;
			int lastYr = uF.parseToInt(curStartDate[2])-1;
			String preYrStartDate = curStartDate[0]+"/"+curStartDate[1]+"/"+lastYr;
			String nxtYrStartDate = curStartDate[0]+"/"+curStartDate[1]+"/"+nextYr;
			
//			System.out.println("nxtYrStartDate="+nxtYrStartDate);
			String[] curEndDate = getStrEndDate().split("/");
			int nextYr1 = uF.parseToInt(curEndDate[2])+1;
			int lastYr1 = uF.parseToInt(curEndDate[2])-1;
			String preYrEndDate = curEndDate[0]+"/"+curEndDate[1]+"/"+lastYr1;
			String nxtYrEndDate = curEndDate[0]+"/"+curEndDate[1]+"/"+nextYr1;
			
			List<String> monthYearsList = new ArrayList<String>();
			List<String> monthNxtYearsList = new ArrayList<String>();
			List<String> headerList = new ArrayList<String>();
			
			headerList.add("Last Year Actual "+uF.getDateFormat(preYrStartDate, DATE_FORMAT, "yyyy")+"-"+uF.getDateFormat(preYrEndDate, DATE_FORMAT, "yy"));
			headerList.add("Current Year Budget "+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, "yyyy")+"-"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, "yy"));
			headerList.add("Current Year Commitment "+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, "yyyy")+"-"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, "yy"));
			headerList.add("Next Year Budget "+uF.getDateFormat(nxtYrStartDate, DATE_FORMAT, "yyyy")+"-"+uF.getDateFormat(nxtYrEndDate, DATE_FORMAT, "yy"));
			headerList.add("Next Year Commitment "+uF.getDateFormat(nxtYrStartDate, DATE_FORMAT, "yyyy")+"-"+uF.getDateFormat(nxtYrEndDate, DATE_FORMAT, "yy"));
			
			Map<String, String> hmReportData = new HashMap<String, String>();
			Map<String, String> totalKpca = new HashMap<String, String>();
			Map<String, String> alSbuMap = new HashMap<String, String>();
			Map<String, String> alLocationMap = new HashMap<String, String>();
			Map<String, List<String>> hmSbuwiseProIds = new HashMap<String, List<String>>();
			Map<String, Map<String, String>> hmSbuwiseProData = new HashMap<String,Map<String, String>>();
			Map<String, String> hmSbuName = CF.getServicesMap(con, false);
			Map<String, Map<String, String>> hmLocation = CF.getWorkLocationMap(con);
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			
			StringBuilder sbQuery = new StringBuilder();
	//===start parvez date: 13-10-2022===		
			/*sbQuery.append("select pro_id,project_owner,start_date,deadline,billing_kind,billing_cycle_day,billing_cycle_weekday," +
					" billing_amount,curr_id,sbu_id,wlocation_id from projectmntnc where sbu_id>0 and approve_status != 'blocked' ");*/
			sbQuery.append("select pro_id,project_owners,start_date,deadline,billing_kind,billing_cycle_day,billing_cycle_weekday," +
					" billing_amount,curr_id,sbu_id,wlocation_id from projectmntnc where sbu_id>0 and approve_status != 'blocked' ");
	//===end parvez date: 13-10-2022===		
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
			
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && nxtYrEndDate != null && !nxtYrEndDate.equalsIgnoreCase("null") && !nxtYrEndDate.equals("")) {
				sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(nxtYrEndDate, DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(nxtYrEndDate, DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(nxtYrEndDate, DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (deadline >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(nxtYrEndDate, DATE_FORMAT, DBDATE) + "')) ");
			}
			
			sbQuery.append(" order by sbu_id ");
			pst = con.prepareStatement(sbQuery.toString());
	        rs = pst.executeQuery();
	        StringBuilder strProIds = null;
	        StringBuilder strProOwnerIds = null;
	        /*Map<String,String> hmProWiseOwner = new HashMap<String, String>();*/
	        Map<String,List<String>> hmProWiseOwner = new HashMap<String, List<String>>();
	        List<String> alProIdList = new ArrayList<String>();
	        while(rs.next()) {
	        	
	        	if(strProIds == null){
	        		strProIds = new StringBuilder();
	        		strProIds.append(rs.getString("pro_id"));
	        	} else {
	        		strProIds.append(","+rs.getString("pro_id"));
	        	}
	        	
	        //===start parvez date: 13-10-2022===	
	        	/*if(strProOwnerIds == null){
	        		strProOwnerIds = new StringBuilder();
	        		strProOwnerIds.append(rs.getString("project_owner"));
	        	} else {
	        		strProOwnerIds.append(","+rs.getString("project_owner"));
	        	}*/
	        	
	        	if(rs.getString("project_owners")!=null){
	        		List<String> tempList = Arrays.asList(rs.getString("project_owners").split(","));
	        		List<String> ownersList1 = new ArrayList<String>();
	        		for(int j=1; j<tempList.size();j++){
	        			if(strProOwnerIds == null){
	    	        		strProOwnerIds = new StringBuilder();
	    	        		strProOwnerIds.append(tempList.get(j));
	    	        	} else {
	    	        		strProOwnerIds.append(","+tempList.get(j));
	    	        	}
	        			ownersList1.add(tempList.get(j));
					}
	        		hmProWiseOwner.put(rs.getString("pro_id"),ownersList1);
	        	}
	        	
	        	alProIdList.add(rs.getString("pro_id"));
//	        	hmProWiseOwner.put(rs.getString("pro_id"),rs.getString("project_owner"));	
	        	
	        	Map<String, String> hmInner = hmSbuwiseProData.get(rs.getString("sbu_id")+"-"+rs.getString("wlocation_id"));
	        	if(hmInner == null) hmInner = new HashMap<String, String>();
	        	
	        	List<String> alInner = hmSbuwiseProIds.get(rs.getString("sbu_id")+"-"+rs.getString("wlocation_id"));
				if(alInner==null) alInner = new ArrayList<String>();
				
				alInner.add(rs.getString("pro_id"));
				
				hmInner.put(rs.getString("pro_id")+"_START_DATE", rs.getString("start_date"));
				hmInner.put(rs.getString("pro_id")+"_DEADLINE", rs.getString("deadline"));
				
				hmInner.put(rs.getString("pro_id")+"_BILL_FREQ", rs.getString("billing_kind"));
				hmInner.put(rs.getString("pro_id")+"_FREQ_DAY", rs.getString("billing_cycle_day"));
				hmInner.put(rs.getString("pro_id")+"_WEEKDAY", rs.getString("billing_cycle_weekday"));
				hmInner.put(rs.getString("pro_id")+"_BILLING_AMT", rs.getString("billing_amount"));
				hmInner.put(rs.getString("pro_id")+"_INVOICE_CURR_ID", rs.getString("curr_id"));
//				hmInner.put(rs.getString("pro_id")+"_PARTNER_ID", rs.getString("project_owner"));
				hmInner.put(rs.getString("pro_id")+"_PARTNER_ID", rs.getString("project_owners"));
			//===end parvez date: 13-10-2022===	
				
				hmSbuwiseProData.put(rs.getString("sbu_id")+"-"+rs.getString("wlocation_id"), hmInner);
				hmSbuwiseProIds.put(rs.getString("sbu_id")+"-"+rs.getString("wlocation_id"), alInner);
					
				
	        }
	        rs.close();
			pst.close();
			
			pst = con.prepareStatement("select pro_id, project_milestone_id, pro_milestone_amount, milestone_end_date from project_milestone_details where pro_id in ("+strProIds+") ");
		    rs = pst.executeQuery();
		    List<String> milestoneIds = new ArrayList<String>();
		    Map<String, String> hmMileStoneMap = new HashMap<String, String>();
		    int x = 0;
		    while(rs.next()){
		    	milestoneIds.add(rs.getString("project_milestone_id"));
		    	hmMileStoneMap.put(rs.getString("pro_id")+"_MS_AMT_"+x, rs.getString("pro_milestone_amount"));
		    	hmMileStoneMap.put(rs.getString("pro_id")+"_MS_END_DATE_"+x, rs.getString("milestone_end_date"));
		    	x++;
		    }
		    rs.close();
			pst.close();
			
			Map<String, String> hmBudgetMap = new HashMap<String, String>();
			Map<String, String> hmNxtYrBudgetMap = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from partner_budget_details where emp_id in ("+strProOwnerIds+") ");
			sbQuery.append(" and financial_year_start>= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and financial_year_end<= '"+uF.getDateFormat(nxtYrEndDate, DATE_FORMAT)+"'");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				String dbStartDate = uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, DATE_FORMAT);
				String dbEndDate = uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, DATE_FORMAT);
				if(uF.getDateFormat(dbStartDate, DATE_FORMAT).equals(uF.getDateFormat(getStrStartDate(), DATE_FORMAT)) &&  uF.getDateFormat(dbEndDate, DATE_FORMAT).equals(uF.getDateFormat(getStrEndDate(), DATE_FORMAT))){
					
					hmBudgetMap.put(rs.getString("emp_id"), rs.getString("total_amount"));
				
				} else if(uF.getDateFormat(dbStartDate, DATE_FORMAT).equals(uF.getDateFormat(nxtYrStartDate, DATE_FORMAT)) &&  uF.getDateFormat(dbEndDate, DATE_FORMAT).equals(uF.getDateFormat(nxtYrEndDate, DATE_FORMAT))){
					hmNxtYrBudgetMap.put(rs.getString("emp_id"), rs.getString("total_amount"));
				}
				
			}
			rs.close();
			pst.close();
			
			/*Map<String, String> hmInoviceAmt = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(invoice_particulars_amount) as invoice_particulars_amount,pro_owner_id from promntc_invoice_amt_details piad, promntc_invoice_details pid " +
					" where piad.promntc_invoice_id = pid.promntc_invoice_id and pro_owner_id in("+strProOwnerIds+")");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and invoice_generated_date between '"+uF.getDateFormat(preYrStartDate, DATE_FORMAT)+"' and '"+uF.getDateFormat(preYrEndDate, DATE_FORMAT)+"'");
			}
			sbQuery.append(" group by pro_owner_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				
				hmInoviceAmt.put(rs.getString("pro_owner_id"), rs.getString("invoice_particulars_amount"));
				
			}
			rs.close();
			pst.close();*/
			
			Map<String, String> hmInoviceAmt = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			
			sbQuery.append("select promntc_invoice_id,invoice_amount,pro_ids from promntc_invoice_details where promntc_invoice_id>0");
			if(alProIdList != null && !alProIdList.isEmpty()){
				sbQuery.append(" and (");
				for(int i=0; i<alProIdList.size(); i++){
					sbQuery.append("pro_ids like '%,"+alProIdList.get(i)+",%'");
					if(i<alProIdList.size()-1){
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" )");
			}
			
			
			if(preYrStartDate != null && !preYrStartDate.equals("") && !preYrStartDate.equalsIgnoreCase("null") && preYrEndDate != null && !preYrEndDate.equals("") && !preYrEndDate.equalsIgnoreCase("null")) {
				sbQuery.append(" and invoice_generated_date between '"+uF.getDateFormat(preYrStartDate, DATE_FORMAT)+"' and '"+uF.getDateFormat(preYrEndDate, DATE_FORMAT)+"'");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst="+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				if(rs.getString("pro_ids") != null){
					String[] tempProId = rs.getString("pro_ids").split(",");
					for(int i=0; i<tempProId.length; i++){
						if(i>0){
						//===start parvez date: 13-10-2022===
							/*String owner = hmProWiseOwner.get(tempProId[i]);
							double invoiceAmt = uF.parseToDouble(rs.getString("invoice_amount"))+uF.parseToDouble(hmInoviceAmt.get(owner));
							hmInoviceAmt.put(owner, invoiceAmt+"");*/
							List<String> alowners1 = hmProWiseOwner.get(tempProId[i]);
							for(int j=0; alowners1!=null && j<alowners1.size(); j++){
								String owner = alowners1.get(j);
								double invoiceAmt = uF.parseToDouble(rs.getString("invoice_amount"))+uF.parseToDouble(hmInoviceAmt.get(owner));
								hmInoviceAmt.put(owner, invoiceAmt+"");
							}
						//===end parvez date: 13-10-2022===	
						}
						
					}
				}
				
			}
			rs.close();
			pst.close();
			
			if(strFinancialYearStart != null && !strFinancialYearStart.equalsIgnoreCase("null") && !strFinancialYearStart.equals("") && strFinancialYearEnd != null && !strFinancialYearEnd.equalsIgnoreCase("null") && !strFinancialYearEnd.equals("")) {
				
				int startDay = 0;
				int endDay = 0;
				int startMonth = 0;
				int endMonth = 0;
				int startYear = 0;
				int endYear = 0;
				int start_month = 0;
				int start_year = 0;
				Date startDate = uF.getDateFormat(strFinancialYearStart, DATE_FORMAT);
				Date endDate1 = uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT);
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				startDay = cal.get(Calendar.DATE);
			    start_month = cal.get(Calendar.MONTH)+1;
			    startMonth = start_month;
			    
			    start_year = cal.get(Calendar.YEAR);
			    startYear= start_year;
			    
			    Calendar cal2 = Calendar.getInstance();
				cal2.setTime(endDate1);
				endDay = cal2.get(Calendar.DATE);
				endMonth = cal2.get(Calendar.MONTH)+1;
				endYear = cal2.get(Calendar.YEAR);
				
				long monthDiff = uF.getMonthsDifference(startDate, endDate1);
				
			    while(monthDiff > 0) {
			    	monthYearsList.add(String.valueOf(startMonth)+"/"+String.valueOf(startYear));
			    	
					startMonth++;
					
					if(startMonth > 12 && endMonth < 12) {
						startMonth = 1;
						startYear++;
					} else if(startMonth > endMonth && startYear == endYear) {
						break;
					}
			    }
			    
			 //===next year===   
			    int nxtStartDay = 0;
				int nxtEndDay = 0;
				int nxtStartMonth = 0;
				int nxtEndMonth = 0;
				int nxtStartYear = 0;
				int nxtEndYear = 0;
				int nxtStart_month = 0;
				int nxtStart_year = 0;
				Date nxtStartDate = uF.getDateFormat(nxtYrStartDate, DATE_FORMAT);
				Date nxtEndDate1 = uF.getDateFormat(nxtYrEndDate, DATE_FORMAT);
				
				Calendar calNxt = Calendar.getInstance();
				calNxt.setTime(nxtStartDate);
				nxtStartDay = calNxt.get(Calendar.DATE);
				nxtStart_month = calNxt.get(Calendar.MONTH)+1;
				nxtStartMonth = nxtStart_month;
			    
				nxtStart_year = calNxt.get(Calendar.YEAR);
				nxtStartYear= nxtStart_year;
			    
			    Calendar calNxt2 = Calendar.getInstance();
			    calNxt2.setTime(nxtEndDate1);
			    nxtEndDay = calNxt2.get(Calendar.DATE);
			    nxtEndMonth = calNxt2.get(Calendar.MONTH)+1;
			    nxtEndYear = calNxt2.get(Calendar.YEAR);
				
				long monthDiff1 = uF.getMonthsDifference(nxtStartDate, nxtEndDate1);
				
			    while(monthDiff1 > 0) {
			    	monthNxtYearsList.add(String.valueOf(nxtStartMonth)+"/"+String.valueOf(nxtStartYear));
			    	
			    	nxtStartMonth++;
					
					if(nxtStartMonth > 12 && nxtEndMonth < 12) {
						nxtStartMonth = 1;
						nxtStartYear++;
					} else if(nxtStartMonth > nxtEndMonth && nxtStartYear == nxtEndYear) {
						break;
					}
			    }
		//===next year===	    
			    
			    
			    Map<String, String> hmProjectData = new HashMap<String,String>();
			    Iterator<String> it1 = hmSbuwiseProData.keySet().iterator();
			    while (it1.hasNext()) {
			    	
			    	String strIds = it1.next();
			    	
			    	String[] newIds = strIds.split("-");
			    	String sbuId = newIds[0];
			    	String locId = newIds[1];
			    	
			    	double commitTot = 0;
				    double nxtCommitTot = 0;
				    double currYrBTot = 0;
			    	double nxtYrBudTot = 0;
			    	double actAmtTot = 0;
			    	
			    	double currYrB = 0;
			    	double nxtYrBud = 0;
			    	double actAmt = 0;
			    	double commitAmt = 0;
			    	double nxtCommitAmt = 0;
			    	Map<String,String> hmInnerMap = new HashMap<String, String>();
			    	
			    	String currencyId = "";
			    	String currExc = "";
			    	
			    	hmProjectData = hmSbuwiseProData.get(strIds);
			    	List<String> alInner = hmSbuwiseProIds.get(strIds);
			    //===start parvez date: 13-10-2022===	
			    	/*for(int i=0;i<alInner.size() && alInner!=null && !alInner.isEmpty();i++) {
			    		
			    		String proId = alInner.get(i);
			    	//===start parvez date: 13-10-2022===
			    		String partnerId = hmProjectData.get(proId+"_PARTNER_ID");
			    		
			    		Map<String, String> hmCurr = hmCurrencyMap.get(hmProjectData.get(proId+"_INVOICE_CURR_ID"));
			    		currencyId = hmCurr.get("SHORT_CURR");
			    		currExc = hmCurr.get("SHORT_CURR_INR");
			    		
					    int proStMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "MM")); 
						int proStYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "yyyy"));
						
					    int proEndMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "MM")); 
						int proEndYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "yyyy"));
						
						setQfreqENDDATE(null);
						
						Iterator<String> itr = monthYearsList.iterator();
						while(itr.hasNext()) {
							String month = itr.next();
							
							String[] dateArr = month.split("/");
							String strFirstDate = null;
							String strEndDate = null;
							
							String strDate = "01/"+dateArr[0]+"/"+dateArr[1];
							String minMaxDate = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
							String[] tmpDate = minMaxDate.split("::::");
							strFirstDate = tmpDate[0];
							strEndDate = tmpDate[1];
							
							int intMonths = uF.getMonthsDifference(uF.getDateFormat(strFirstDate, DATE_FORMAT), uF.getDateFormat(strEndDate, DATE_FORMAT));
							
							int intCount = 1;
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
								String strDays = uF.dateDifference(strFirstDate, DATE_FORMAT, strEndDate, DATE_FORMAT);
								intCount = (uF.parseToInt(strDays) / 7);
								intCount++;	
							} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
								if(milestoneIds.size()>0){
									intCount = milestoneIds.size();
								}
							}
							
							double dblBillAmount = 0;
							setFreqENDDATE(null);
							
							Date proStDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_START_DATE"), DBDATE);
							Date proEdDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE);
							Date mnthStDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
							Date mnthEdDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
							
							boolean flag = false;
							if(((proStDate.before(mnthStDate) || proStDate.equals(mnthStDate)) && (proEdDate.after(mnthEdDate) || proEdDate.equals(mnthEdDate))) ) {
								flag = true;
							}
							if(flag || (proStMnth == uF.parseToInt(dateArr[0]) && proStYr == uF.parseToInt(dateArr[1])) || (proEndMnth == uF.parseToInt(dateArr[0]) && proEndYr == uF.parseToInt(dateArr[1]))) {
								for(int j=0; j<intCount; j++) {
									
									String newStDate = null;
									
									if(getFreqENDDATE() != null) {
										newStDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormatUtil(getFreqENDDATE(), DATE_FORMAT), 1)+"", DBDATE, DATE_FORMAT);
									}
									
									if(newStDate == null || newStDate.equals("")) {
										newStDate = strFirstDate;
									}
									
									boolean frqFlag = false;
									String freqEndDate = strEndDate;
									
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
										if(hmMileStoneMap!=null && !hmMileStoneMap.isEmpty() && hmMileStoneMap.get(proId+"_MS_END_DATE_"+j) != null){
											freqEndDate = uF.getDateFormat(hmMileStoneMap.get(proId+"_MS_END_DATE_"+j), DBDATE, DATE_FORMAT);
										} else{
											freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
										}
								
									}
									
									if(uF.parseToInt(hmProjectData.get(proId+"_FREQ_DAY")) > 0) {
										
										freqEndDate = hmProjectData.get(proId+"_FREQ_DAY") + "/" + uF.getDateFormat(newStDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
										freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
										
										Date stDate = uF.getDateFormatUtil(newStDate, DATE_FORMAT);
										Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
										
										if(freqDate.after(stDate)) {
											frqFlag = true;
										}
										
										if(frqFlag) {
											if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
												freqEndDate = freqEndDate;
											} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
												freqEndDate = uF.getDateFormat(uF.getFutureDate(newStDate, 15)+"", DBDATE, DATE_FORMAT);
											}
										} else {
											if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
												freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
											} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
												freqEndDate = uF.getDateFormat(uF.getFutureDate(strFirstDate, 15)+"", DBDATE, DATE_FORMAT);
											}
										}
									}
									
									if(hmProjectData.get(proId+"_WEEKDAY") != null && !hmProjectData.get(proId+"_WEEKDAY").equals("") && hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("W")) {
										freqEndDate = uF.getDateFormat(uF.getDateOfPassedDay(newStDate, hmProjectData.get(proId+"_WEEKDAY"))+"", DBDATE, DATE_FORMAT);
										freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
									}
									
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("Q")) {
										 Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
										 
										 if(getQfreqENDDATE() == null) {
											 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
											 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
											 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -3)+"", DBDATE, DATE_FORMAT);
											 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 2, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
											
											 setQfreqENDDATE(freqEndDate);
											 
											} else {
												freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 3)+"", DBDATE, DATE_FORMAT);
											}
										 
										 if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
											freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
											
										 }
										 if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
											setQfreqENDDATE(freqEndDate);
										 }
											
									}
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("H")) {
										Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
										if(getQfreqENDDATE() == null) {
											 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
											 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
											 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -6)+"", DBDATE, DATE_FORMAT);
											 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 5, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
											 
											setQfreqENDDATE(freqEndDate);
										} else {
											freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 6)+"", DBDATE, DATE_FORMAT);
										}
										if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
											freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
										}
										if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
											setQfreqENDDATE(freqEndDate);
										}
									}
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("A")) {
										Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
										if(getQfreqENDDATE() == null) {
											String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
											String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
											String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -12)+"", DBDATE, DATE_FORMAT);
											freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 11, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
											setQfreqENDDATE(freqEndDate);
											 
										} else { 
											freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 12)+"", DBDATE, DATE_FORMAT);
										}
										
										if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
											freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
										}
										
										if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
											setQfreqENDDATE(freqEndDate);
										}
									}
									Date firstDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
									Date endDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
									Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
									
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O") && hmMileStoneMap!=null && !hmMileStoneMap.isEmpty()) {
										if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
											dblBillAmount += uF.parseToInt(hmMileStoneMap.get(proId+"_MS_AMT_"+j));
										}
										
									} else{
										if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
											dblBillAmount += uF.parseToInt(hmProjectData.get(proId+"_BILLING_AMT"));
											
										}
									}
									
								}
							}
							commitAmt += dblBillAmount;
							
						}
						
				//===next year===
						
						setQfreqNxtENDDATE(null);
						
						Iterator<String> itr1 = monthNxtYearsList.iterator();
						while(itr1.hasNext()) {
							String month = itr1.next();
							
							String[] dateArr = month.split("/");
							String strFirstDate = null;
							String strEndDate = null;
							
							String strDate = "01/"+dateArr[0]+"/"+dateArr[1];
							String minMaxDate = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
							String[] tmpDate = minMaxDate.split("::::");
							strFirstDate = tmpDate[0];
							strEndDate = tmpDate[1];
							
							int intMonths = uF.getMonthsDifference(uF.getDateFormat(strFirstDate, DATE_FORMAT), uF.getDateFormat(strEndDate, DATE_FORMAT));
							
							int intCount = 1;
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
								String strDays = uF.dateDifference(strFirstDate, DATE_FORMAT, strEndDate, DATE_FORMAT);
								intCount = (uF.parseToInt(strDays) / 7);
								intCount++;	
							} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
								if(milestoneIds.size()>0){
									intCount = milestoneIds.size();
								}
							}
							
							double dblNxtBillAmount = 0;
							setFreqNxtENDDATE(null);
							
							Date proStDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_START_DATE"), DBDATE);
							Date proEdDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE);
							Date mnthStDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
							Date mnthEdDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
							
							boolean flag = false;
							if(((proStDate.before(mnthStDate) || proStDate.equals(mnthStDate)) && (proEdDate.after(mnthEdDate) || proEdDate.equals(mnthEdDate))) ) {
								flag = true;
							}
							if(flag || (proStMnth == uF.parseToInt(dateArr[0]) && proStYr == uF.parseToInt(dateArr[1])) || (proEndMnth == uF.parseToInt(dateArr[0]) && proEndYr == uF.parseToInt(dateArr[1]))) {
								for(int j=0; j<intCount; j++) {
									
									String newStDate = null;
									
									if(getFreqNxtENDDATE() != null) {
										newStDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormatUtil(getFreqNxtENDDATE(), DATE_FORMAT), 1)+"", DBDATE, DATE_FORMAT);
									}
									
									if(newStDate == null || newStDate.equals("")) {
										newStDate = strFirstDate;
									}
									
									boolean frqFlag = false;
									String freqEndDate = strEndDate;
									
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
										if(hmMileStoneMap!=null && !hmMileStoneMap.isEmpty() && hmMileStoneMap.get(proId+"_MS_END_DATE_"+j) != null){
											freqEndDate = uF.getDateFormat(hmMileStoneMap.get(proId+"_MS_END_DATE_"+j), DBDATE, DATE_FORMAT);
										} else{
											freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
										}
								
									}
									
									if(uF.parseToInt(hmProjectData.get(proId+"_FREQ_DAY")) > 0) {
										
										freqEndDate = hmProjectData.get(proId+"_FREQ_DAY") + "/" + uF.getDateFormat(newStDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
										freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
										
										Date stDate = uF.getDateFormatUtil(newStDate, DATE_FORMAT);
										Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
										
										if(freqDate.after(stDate)) {
											frqFlag = true;
										}
										
										if(frqFlag) {
											if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
												freqEndDate = freqEndDate;
											} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
												freqEndDate = uF.getDateFormat(uF.getFutureDate(newStDate, 15)+"", DBDATE, DATE_FORMAT);
											}
										} else {
											if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
												freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
											} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
												freqEndDate = uF.getDateFormat(uF.getFutureDate(strFirstDate, 15)+"", DBDATE, DATE_FORMAT);
											}
										}
									}
									
									if(hmProjectData.get(proId+"_WEEKDAY") != null && !hmProjectData.get(proId+"_WEEKDAY").equals("") && hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("W")) {
										freqEndDate = uF.getDateFormat(uF.getDateOfPassedDay(newStDate, hmProjectData.get(proId+"_WEEKDAY"))+"", DBDATE, DATE_FORMAT);
										freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
									}
									
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("Q")) {
										 Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
										 
										 if(getQfreqNxtENDDATE() == null) {
											 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
											 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
											 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -3)+"", DBDATE, DATE_FORMAT);
											 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 2, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
											
											 setQfreqNxtENDDATE(freqEndDate);
											 
											} else {
												freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqNxtENDDATE(), 3)+"", DBDATE, DATE_FORMAT);
											}
										 
										 if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
											freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
											
										 }
										 if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
											setQfreqNxtENDDATE(freqEndDate);
										 }
											
									}
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("H")) {
										Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
										if(getQfreqNxtENDDATE() == null) {
											 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
											 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
											 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -6)+"", DBDATE, DATE_FORMAT);
											 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 5, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
											 
											setQfreqNxtENDDATE(freqEndDate);
										} else {
											freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqNxtENDDATE(), 6)+"", DBDATE, DATE_FORMAT);
										}
										if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
											freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
										}
										if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
											setQfreqNxtENDDATE(freqEndDate);
										}
									}
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("A")) {
										Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
										if(getQfreqNxtENDDATE() == null) {
											String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
											String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
											String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -12)+"", DBDATE, DATE_FORMAT);
											freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 11, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
											setQfreqNxtENDDATE(freqEndDate);
											 
										} else { 
											freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqNxtENDDATE(), 12)+"", DBDATE, DATE_FORMAT);
										}
										
										if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
											freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
										}
										
										if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
											setQfreqNxtENDDATE(freqEndDate);
										}
									}
									Date firstDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
									Date endDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
									Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
									
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O") && hmMileStoneMap!=null && !hmMileStoneMap.isEmpty()) {
										if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
											dblNxtBillAmount += uF.parseToInt(hmMileStoneMap.get(proId+"_MS_AMT_"+j));
										}
										
									} else{
										if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
											dblNxtBillAmount += uF.parseToInt(hmProjectData.get(proId+"_BILLING_AMT"));
											
										}
									}
									
								}
							}
							nxtCommitAmt += dblNxtBillAmount;
							
						}
				//===next year===		
						currYrB += uF.parseToDouble(hmBudgetMap.get(partnerId));
				    	nxtYrBud += uF.parseToDouble(hmNxtYrBudgetMap.get(partnerId));
				    	actAmt += uF.parseToDouble(hmInoviceAmt.get(partnerId));
				   //===end parvez date: 13-10-2022=== 	
			    	}*/
			    	
			    	for(int i=0;i<alInner.size() && alInner!=null && !alInner.isEmpty();i++) {
			    		
			    		String proId = alInner.get(i);
//			    		String partnerId = hmProjectData.get(proId+"_PARTNER_ID");
			    		String tempPartnerId = hmProjectData.get(proId+"_PARTNER_ID");
			    		List<String> arrPartnersIds = null;
			    		
			    		if(tempPartnerId != null){
			    			arrPartnersIds = Arrays.asList(tempPartnerId.split(","));
			    		}
			    		
			    		for(int ii=1; arrPartnersIds!=null && ii<arrPartnersIds.size(); ii++){
			    			String partnerId = arrPartnersIds.get(ii);
			    			
				    		Map<String, String> hmCurr = hmCurrencyMap.get(hmProjectData.get(proId+"_INVOICE_CURR_ID"));
				    		currencyId = hmCurr.get("SHORT_CURR");
				    		currExc = hmCurr.get("SHORT_CURR_INR");
				    		
						    int proStMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "MM")); 
							int proStYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "yyyy"));
							
						    int proEndMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "MM")); 
							int proEndYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "yyyy"));
							
							setQfreqENDDATE(null);
							
							Iterator<String> itr = monthYearsList.iterator();
							while(itr.hasNext()) {
								String month = itr.next();
								
								String[] dateArr = month.split("/");
								String strFirstDate = null;
								String strEndDate = null;
								
								String strDate = "01/"+dateArr[0]+"/"+dateArr[1];
								String minMaxDate = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
								String[] tmpDate = minMaxDate.split("::::");
								strFirstDate = tmpDate[0];
								strEndDate = tmpDate[1];
								
								int intMonths = uF.getMonthsDifference(uF.getDateFormat(strFirstDate, DATE_FORMAT), uF.getDateFormat(strEndDate, DATE_FORMAT));
								
								int intCount = 1;
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
									String strDays = uF.dateDifference(strFirstDate, DATE_FORMAT, strEndDate, DATE_FORMAT);
									intCount = (uF.parseToInt(strDays) / 7);
									intCount++;	
								} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
									if(milestoneIds.size()>0){
										intCount = milestoneIds.size();
									}
								}
								
								double dblBillAmount = 0;
								setFreqENDDATE(null);
								
								Date proStDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_START_DATE"), DBDATE);
								Date proEdDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE);
								Date mnthStDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
								Date mnthEdDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
								
								boolean flag = false;
								if(((proStDate.before(mnthStDate) || proStDate.equals(mnthStDate)) && (proEdDate.after(mnthEdDate) || proEdDate.equals(mnthEdDate))) ) {
									flag = true;
								}
								if(flag || (proStMnth == uF.parseToInt(dateArr[0]) && proStYr == uF.parseToInt(dateArr[1])) || (proEndMnth == uF.parseToInt(dateArr[0]) && proEndYr == uF.parseToInt(dateArr[1]))) {
									for(int j=0; j<intCount; j++) {
										
										String newStDate = null;
										
										if(getFreqENDDATE() != null) {
											newStDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormatUtil(getFreqENDDATE(), DATE_FORMAT), 1)+"", DBDATE, DATE_FORMAT);
										}
										
										if(newStDate == null || newStDate.equals("")) {
											newStDate = strFirstDate;
										}
										
										boolean frqFlag = false;
										String freqEndDate = strEndDate;
										
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
											if(hmMileStoneMap!=null && !hmMileStoneMap.isEmpty() && hmMileStoneMap.get(proId+"_MS_END_DATE_"+j) != null){
												freqEndDate = uF.getDateFormat(hmMileStoneMap.get(proId+"_MS_END_DATE_"+j), DBDATE, DATE_FORMAT);
											} else{
												freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
											}
									
										}
										
										if(uF.parseToInt(hmProjectData.get(proId+"_FREQ_DAY")) > 0) {
											
											freqEndDate = hmProjectData.get(proId+"_FREQ_DAY") + "/" + uF.getDateFormat(newStDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
											freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
											
											Date stDate = uF.getDateFormatUtil(newStDate, DATE_FORMAT);
											Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
											
											if(freqDate.after(stDate)) {
												frqFlag = true;
											}
											
											if(frqFlag) {
												if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
													freqEndDate = freqEndDate;
												} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
													freqEndDate = uF.getDateFormat(uF.getFutureDate(newStDate, 15)+"", DBDATE, DATE_FORMAT);
												}
											} else {
												if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
													freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
												} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
													freqEndDate = uF.getDateFormat(uF.getFutureDate(strFirstDate, 15)+"", DBDATE, DATE_FORMAT);
												}
											}
										}
										
										if(hmProjectData.get(proId+"_WEEKDAY") != null && !hmProjectData.get(proId+"_WEEKDAY").equals("") && hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("W")) {
											freqEndDate = uF.getDateFormat(uF.getDateOfPassedDay(newStDate, hmProjectData.get(proId+"_WEEKDAY"))+"", DBDATE, DATE_FORMAT);
											freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
										}
										
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("Q")) {
											 Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
											 
											 if(getQfreqENDDATE() == null) {
												 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
												 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
												 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -3)+"", DBDATE, DATE_FORMAT);
												 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 2, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
												
												 setQfreqENDDATE(freqEndDate);
												 
												} else {
													freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 3)+"", DBDATE, DATE_FORMAT);
												}
											 
											 if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
												freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
												
											 }
											 if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
												setQfreqENDDATE(freqEndDate);
											 }
												
										}
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("H")) {
											Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
											if(getQfreqENDDATE() == null) {
												 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
												 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
												 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -6)+"", DBDATE, DATE_FORMAT);
												 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 5, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
												 
												setQfreqENDDATE(freqEndDate);
											} else {
												freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 6)+"", DBDATE, DATE_FORMAT);
											}
											if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
												freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
											}
											if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
												setQfreqENDDATE(freqEndDate);
											}
										}
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("A")) {
											Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
											if(getQfreqENDDATE() == null) {
												String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
												String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
												String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -12)+"", DBDATE, DATE_FORMAT);
												freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 11, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
												setQfreqENDDATE(freqEndDate);
												 
											} else { 
												freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 12)+"", DBDATE, DATE_FORMAT);
											}
											
											if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
												freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
											}
											
											if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
												setQfreqENDDATE(freqEndDate);
											}
										}
										Date firstDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
										Date endDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
										Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
										
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O") && hmMileStoneMap!=null && !hmMileStoneMap.isEmpty()) {
											if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
												dblBillAmount += uF.parseToInt(hmMileStoneMap.get(proId+"_MS_AMT_"+j));
											}
											
										} else{
											if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
												dblBillAmount += uF.parseToInt(hmProjectData.get(proId+"_BILLING_AMT"));
												
											}
										}
										
									}
								}
								commitAmt += dblBillAmount;
								
							}
							
					//===next year===
							
							setQfreqNxtENDDATE(null);
							
							Iterator<String> itr1 = monthNxtYearsList.iterator();
							while(itr1.hasNext()) {
								String month = itr1.next();
								
								String[] dateArr = month.split("/");
								String strFirstDate = null;
								String strEndDate = null;
								
								String strDate = "01/"+dateArr[0]+"/"+dateArr[1];
								String minMaxDate = uF.getCurrentMonthMinMaxDate(strDate, DATE_FORMAT);
								String[] tmpDate = minMaxDate.split("::::");
								strFirstDate = tmpDate[0];
								strEndDate = tmpDate[1];
								
								int intMonths = uF.getMonthsDifference(uF.getDateFormat(strFirstDate, DATE_FORMAT), uF.getDateFormat(strEndDate, DATE_FORMAT));
								
								int intCount = 1;
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
									String strDays = uF.dateDifference(strFirstDate, DATE_FORMAT, strEndDate, DATE_FORMAT);
									intCount = (uF.parseToInt(strDays) / 7);
									intCount++;	
								} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
									if(milestoneIds.size()>0){
										intCount = milestoneIds.size();
									}
								}
								
								double dblNxtBillAmount = 0;
								setFreqNxtENDDATE(null);
								
								Date proStDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_START_DATE"), DBDATE);
								Date proEdDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE);
								Date mnthStDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
								Date mnthEdDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
								
								boolean flag = false;
								if(((proStDate.before(mnthStDate) || proStDate.equals(mnthStDate)) && (proEdDate.after(mnthEdDate) || proEdDate.equals(mnthEdDate))) ) {
									flag = true;
								}
								if(flag || (proStMnth == uF.parseToInt(dateArr[0]) && proStYr == uF.parseToInt(dateArr[1])) || (proEndMnth == uF.parseToInt(dateArr[0]) && proEndYr == uF.parseToInt(dateArr[1]))) {
									for(int j=0; j<intCount; j++) {
										
										String newStDate = null;
										
										if(getFreqNxtENDDATE() != null) {
											newStDate = uF.getDateFormat(uF.getFutureDate(uF.getDateFormatUtil(getFreqNxtENDDATE(), DATE_FORMAT), 1)+"", DBDATE, DATE_FORMAT);
										}
										
										if(newStDate == null || newStDate.equals("")) {
											newStDate = strFirstDate;
										}
										
										boolean frqFlag = false;
										String freqEndDate = strEndDate;
										
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
											if(hmMileStoneMap!=null && !hmMileStoneMap.isEmpty() && hmMileStoneMap.get(proId+"_MS_END_DATE_"+j) != null){
												freqEndDate = uF.getDateFormat(hmMileStoneMap.get(proId+"_MS_END_DATE_"+j), DBDATE, DATE_FORMAT);
											} else{
												freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
											}
									
										}
										
										if(uF.parseToInt(hmProjectData.get(proId+"_FREQ_DAY")) > 0) {
											
											freqEndDate = hmProjectData.get(proId+"_FREQ_DAY") + "/" + uF.getDateFormat(newStDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
											freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
											
											Date stDate = uF.getDateFormatUtil(newStDate, DATE_FORMAT);
											Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
											
											if(freqDate.after(stDate)) {
												frqFlag = true;
											}
											
											if(frqFlag) {
												if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
													freqEndDate = freqEndDate;
												} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
													freqEndDate = uF.getDateFormat(uF.getFutureDate(newStDate, 15)+"", DBDATE, DATE_FORMAT);
												}
											} else {
												if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("M")) {
													freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
												} else if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("B")) {
													freqEndDate = uF.getDateFormat(uF.getFutureDate(strFirstDate, 15)+"", DBDATE, DATE_FORMAT);
												}
											}
										}
										
										if(hmProjectData.get(proId+"_WEEKDAY") != null && !hmProjectData.get(proId+"_WEEKDAY").equals("") && hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("W")) {
											freqEndDate = uF.getDateFormat(uF.getDateOfPassedDay(newStDate, hmProjectData.get(proId+"_WEEKDAY"))+"", DBDATE, DATE_FORMAT);
											freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
										}
										
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("Q")) {
											 Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
											 
											 if(getQfreqNxtENDDATE() == null) {
												 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
												 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
												 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -3)+"", DBDATE, DATE_FORMAT);
												 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 2, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
												
												 setQfreqNxtENDDATE(freqEndDate);
												 
												} else {
													freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqNxtENDDATE(), 3)+"", DBDATE, DATE_FORMAT);
												}
											 
											 if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
												freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
												
											 }
											 if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
												setQfreqNxtENDDATE(freqEndDate);
											 }
												
										}
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("H")) {
											Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
											if(getQfreqNxtENDDATE() == null) {
												 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
												 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
												 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -6)+"", DBDATE, DATE_FORMAT);
												 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 5, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
												 
												setQfreqNxtENDDATE(freqEndDate);
											} else {
												freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqNxtENDDATE(), 6)+"", DBDATE, DATE_FORMAT);
											}
											if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
												freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
											}
											if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
												setQfreqNxtENDDATE(freqEndDate);
											}
										}
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("A")) {
											Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
											if(getQfreqNxtENDDATE() == null) {
												String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
												String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
												String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -12)+"", DBDATE, DATE_FORMAT);
												freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 11, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
												setQfreqNxtENDDATE(freqEndDate);
												 
											} else { 
												freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqNxtENDDATE(), 12)+"", DBDATE, DATE_FORMAT);
											}
											
											if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE))) {
												freqEndDate = uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
											}
											
											if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
												setQfreqNxtENDDATE(freqEndDate);
											}
										}
										Date firstDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
										Date endDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
										Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
										
										if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O") && hmMileStoneMap!=null && !hmMileStoneMap.isEmpty()) {
											if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
												dblNxtBillAmount += uF.parseToInt(hmMileStoneMap.get(proId+"_MS_AMT_"+j));
											}
											
										} else{
											if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
												dblNxtBillAmount += uF.parseToInt(hmProjectData.get(proId+"_BILLING_AMT"));
												
											}
										}
										
									}
								}
								nxtCommitAmt += dblNxtBillAmount;
								
							}
					//===next year===		
							currYrB += uF.parseToDouble(hmBudgetMap.get(partnerId));
					    	nxtYrBud += uF.parseToDouble(hmNxtYrBudgetMap.get(partnerId));
					    	actAmt += uF.parseToDouble(hmInoviceAmt.get(partnerId));
			    		}
			    	}
			   //===end parvez date: 13-10-2022===	
			    	
			    	hmReportData.put(strIds+"_C_YR_BUDGET", uF.formatIntoComma(currYrB));
			    	hmReportData.put(strIds+"_C_YR_COMMIT", uF.formatIntoComma(commitAmt));
			    	hmReportData.put(strIds+"_NXT_YR_BUDGET", uF.formatIntoComma(nxtYrBud));
			    	hmReportData.put(strIds+"_NXT_YR_COMMIT", uF.formatIntoComma(nxtCommitAmt));
			    	hmReportData.put(strIds+"_LAST_YR_ACT", uF.formatIntoComma(actAmt));
			    	
			    	actAmtTot = actAmt+uF.parseToDouble(hmReportData.get(locId+"_LAST_YR_ACT_TOT"));
			    	currYrBTot = currYrB+uF.parseToDouble(hmReportData.get(locId+"_C_YR_BUDGET_TOT"));
			    	commitTot = commitAmt+uF.parseToDouble(hmReportData.get(locId+"_C_YR_COMMIT_TOT"));
			    	nxtYrBudTot = nxtYrBud+uF.parseToDouble(hmReportData.get(locId+"_NXT_YR_BUDGET_TOT"));
			    	nxtCommitTot = nxtCommitAmt+uF.parseToDouble(hmReportData.get(locId+"_NXT_YR_COMMIT_TOT"));
			    	
			    	double klatot = uF.parseToDouble(hmReportData.get(strIds+"_LAST_YR_ACT"))+uF.parseToDouble(hmReportData.get(sbuId+"_LAST_YR_ACT_KTOT"));
			    	double kCBtot = uF.parseToDouble(hmReportData.get(strIds+"_C_YR_BUDGET"))+uF.parseToDouble(hmReportData.get(sbuId+"_C_YR_BUDGET_KTOT"));
			    	double kCCtot = uF.parseToDouble(hmReportData.get(strIds+"_C_YR_COMMIT"))+uF.parseToDouble(hmReportData.get(sbuId+"_C_YR_COMMIT_KTOT"));
			    	double kNBtot = uF.parseToDouble(hmReportData.get(strIds+"_NXT_YR_BUDGET"))+uF.parseToDouble(hmReportData.get(sbuId+"_NXT_YR_BUDGET_KTOT"));
			    	double kNCtot = uF.parseToDouble(hmReportData.get(strIds+"_NXT_YR_COMMIT"))+uF.parseToDouble(hmReportData.get(sbuId+"_NXT_YR_COMMIT_KTOT"));
			    	
			    	hmReportData.put(locId+"_LAST_YR_ACT_TOT", uF.formatIntoComma(actAmtTot));
			    	hmReportData.put(locId+"_C_YR_BUDGET_TOT", uF.formatIntoComma(currYrBTot));
			    	hmReportData.put(locId+"_C_YR_COMMIT_TOT", uF.formatIntoComma(commitTot));
			    	hmReportData.put(locId+"_NXT_YR_BUDGET_TOT", uF.formatIntoComma(nxtYrBudTot));
			    	hmReportData.put(locId+"_NXT_YR_COMMIT_TOT", uF.formatIntoComma(nxtCommitTot));
			    	
			    	hmReportData.put(sbuId+"_LAST_YR_ACT_KTOT", uF.formatIntoComma(klatot));
			    	hmReportData.put(sbuId+"_C_YR_BUDGET_KTOT", uF.formatIntoComma(kCBtot));
			    	hmReportData.put(sbuId+"_C_YR_COMMIT_KTOT", uF.formatIntoComma(kCCtot));
			    	hmReportData.put(sbuId+"_NXT_YR_BUDGET_KTOT", uF.formatIntoComma(kNBtot));
			    	hmReportData.put(sbuId+"_NXT_YR_COMMIT_KTOT", uF.formatIntoComma(kNCtot));
			    	
			    	double kActTot1 = uF.parseToDouble(hmReportData.get(sbuId+"_LAST_YR_ACT_KTOT"))+uF.parseToDouble(hmReportData.get("LAST_YR_ACT_KTOT"));
			    	double kCBtot1 = uF.parseToDouble(hmReportData.get(sbuId+"_C_YR_BUDGET_KTOT"))+uF.parseToDouble(hmReportData.get("C_YR_BUDGET_KTOT"));
			    	double kCCtot1 = uF.parseToDouble(hmReportData.get(sbuId+"_C_YR_COMMIT_KTOT"))+uF.parseToDouble(hmReportData.get("C_YR_COMMIT_KTOT"));
			    	double kNBtot1 = uF.parseToDouble(hmReportData.get(sbuId+"_NXT_YR_BUDGET_KTOT"))+uF.parseToDouble(hmReportData.get("NXT_YR_BUDGET_KTOT"));
			    	double kNCtot1 = uF.parseToDouble(hmReportData.get(sbuId+"_NXT_YR_COMMIT_KTOT"))+uF.parseToDouble(hmReportData.get("NXT_YR_COMMIT_KTOT"));
			    	
//			    	System.out.println("kCBtot1="+kCBtot1+"---_C_YR_BUDGET_KTOT="+hmReportData.get("C_YR_BUDGET_KTOT"));
			    	
			    	hmReportData.put("LAST_YR_ACT_KTOT", uF.formatIntoComma(kActTot1));
			    	hmReportData.put("C_YR_BUDGET_KTOT", uF.formatIntoComma(kCBtot1));
			    	hmReportData.put("C_YR_COMMIT_KTOT", uF.formatIntoComma(kCCtot1));
			    	hmReportData.put("NXT_YR_BUDGET_KTOT", uF.formatIntoComma(kNBtot1));
			    	hmReportData.put("NXT_YR_COMMIT_KTOT", uF.formatIntoComma(kNCtot1));
			    	hmReportData.put("CURRENCY", currencyId);
			    	hmReportData.put("CURRENCY_EXCEL", currExc);
			    	
			    	alSbuMap.put(sbuId, hmSbuName.get(sbuId));
			    	alLocationMap.put(locId, hmLocation.get(locId).get("WL_NAME"));
			    }
			    
			}
			
			request.setAttribute("headerList", headerList);
			request.setAttribute("alSbuMap", alSbuMap);
			request.setAttribute("alLocationMap", alLocationMap);
			request.setAttribute("hmReportData", hmReportData);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return SUCCESS;
	}
	
	public void generateClusterBudgetExcelReport(UtilityFunctions uF){
		
		try {
			
			Map<String, String> alSbuMap = (Map<String, String>)request.getAttribute("alSbuMap");
			if(alSbuMap == null) alSbuMap = new HashMap<String, String>();
			
			Map<String, String> alLocationMap = (Map<String, String>)request.getAttribute("alLocationMap");
			if(alLocationMap == null) alLocationMap = new HashMap<String, String>();
			
			Map<String, String> hmReportData = (Map<String, String>)request.getAttribute("hmReportData");
			if(hmReportData == null) hmReportData = new HashMap<String, String>();
			List headerList = (List)request.getAttribute("headerList");
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Cluster wise Budget Report");
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			List<List<DataStyle>> kpcaTotal=new ArrayList<List<DataStyle>>();
			
			Iterator<String> it = alSbuMap.keySet().iterator();
			List<String> alSbuList = new ArrayList<String>();
			while (it.hasNext()) {
				String sbuId = it.next();
				alSbuList.add(sbuId);
				header.add(new DataStyle(alSbuMap.get(sbuId)+"",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			}
			header.add(new DataStyle("Total",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			
			Iterator<String> itr = alLocationMap.keySet().iterator();
			while (itr.hasNext()) {
				String locId = itr.next();
				List<DataStyle> locList = new ArrayList<DataStyle>();
				List<DataStyle> lActList = new ArrayList<DataStyle>();
				List<DataStyle> cBudList = new ArrayList<DataStyle>();
				List<DataStyle> cCommitList = new ArrayList<DataStyle>();
				List<DataStyle> nBudList = new ArrayList<DataStyle>();
				List<DataStyle> nCommitList = new ArrayList<DataStyle>();
				List<DataStyle> gap = new ArrayList<DataStyle>();
				
				locList.add(new DataStyle(alLocationMap.get(locId)+" ("+uF.showData(hmReportData.get("CURRENCY_EXCEL"),"")+")",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				lActList.add(new DataStyle(headerList.get(0)+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				cBudList.add(new DataStyle(headerList.get(1)+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				cCommitList.add(new DataStyle(headerList.get(2)+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				nBudList.add(new DataStyle(headerList.get(3)+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				nCommitList.add(new DataStyle(headerList.get(4)+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				gap.add(new DataStyle("Gap",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				
				for(int i=0; i<alSbuList.size(); i++){ 
					lActList.add(new DataStyle(uF.showData(hmReportData.get(alSbuList.get(i)+"-"+locId+"_LAST_YR_ACT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
					cBudList.add(new DataStyle(uF.showData(hmReportData.get(alSbuList.get(i)+"-"+locId+"_C_YR_BUDGET"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
					cCommitList.add(new DataStyle(uF.showData(hmReportData.get(alSbuList.get(i)+"-"+locId+"_C_YR_COMMIT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
					nBudList.add(new DataStyle(uF.showData(hmReportData.get(alSbuList.get(i)+"-"+locId+"_NXT_YR_BUDGET"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
					nCommitList.add(new DataStyle(uF.showData(hmReportData.get(alSbuList.get(i)+"-"+locId+"_NXT_YR_COMMIT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
					gap.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				}
				
				lActList.add(new DataStyle(uF.showData(hmReportData.get(locId+"_LAST_YR_ACT_TOT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				cBudList.add(new DataStyle(uF.showData(hmReportData.get(locId+"_C_YR_BUDGET_TOT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				cCommitList.add(new DataStyle(uF.showData(hmReportData.get(locId+"_C_YR_COMMIT_TOT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				nBudList.add(new DataStyle(uF.showData(hmReportData.get(locId+"_NXT_YR_BUDGET_TOT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				nCommitList.add(new DataStyle(uF.showData(hmReportData.get(locId+"_NXT_YR_COMMIT_TOT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				gap.add(new DataStyle("",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				
				reportData.add(locList);
				reportData.add(lActList);
				reportData.add(cBudList);
				reportData.add(cCommitList);
				reportData.add(nBudList);
				reportData.add(nCommitList);
				reportData.add(gap);
			}
			
			List<DataStyle> lATotList = new ArrayList<DataStyle>();
			List<DataStyle> cBTotList = new ArrayList<DataStyle>();
			List<DataStyle> cCTotList = new ArrayList<DataStyle>();
			List<DataStyle> nBTotList = new ArrayList<DataStyle>();
			List<DataStyle> nCTotList = new ArrayList<DataStyle>();
			
			lATotList.add(new DataStyle(headerList.get(0)+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			cBTotList.add(new DataStyle(headerList.get(1)+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			cCTotList.add(new DataStyle(headerList.get(2)+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			nBTotList.add(new DataStyle(headerList.get(3)+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			nCTotList.add(new DataStyle(headerList.get(4)+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			
			for(int i=0; i<alSbuList.size(); i++){ 
				lATotList.add(new DataStyle(uF.showData(hmReportData.get(alSbuList.get(i)+"_LAST_YR_ACT_KTOT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				cBTotList.add(new DataStyle(uF.showData(hmReportData.get(alSbuList.get(i)+"_C_YR_BUDGET_KTOT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				cCTotList.add(new DataStyle(uF.showData(hmReportData.get(alSbuList.get(i)+"_C_YR_COMMIT_KTOT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				nBTotList.add(new DataStyle(uF.showData(hmReportData.get(alSbuList.get(i)+"_NXT_YR_BUDGET_KTOT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				nCTotList.add(new DataStyle(uF.showData(hmReportData.get(alSbuList.get(i)+"_NXT_YR_COMMIT_KTOT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			}
			lATotList.add(new DataStyle(uF.showData(hmReportData.get("LAST_YR_ACT_KTOT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			cBTotList.add(new DataStyle(uF.showData(hmReportData.get("C_YR_BUDGET_KTOT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			cCTotList.add(new DataStyle(uF.showData(hmReportData.get("C_YR_COMMIT_KTOT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			nBTotList.add(new DataStyle(uF.showData(hmReportData.get("NXT_YR_BUDGET_KTOT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			nCTotList.add(new DataStyle(uF.showData(hmReportData.get("NXT_YR_COMMIT_KTOT"),"0"),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			
			kpcaTotal.add(lATotList);
			kpcaTotal.add(cBTotList);
			kpcaTotal.add(cCTotList);
			kpcaTotal.add(nBTotList);
			kpcaTotal.add(nCTotList);
			
			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			sheetDesign.generateExcelSheetforClusterwiseBudgetReport(workbook,sheet,header,reportData,kpcaTotal);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=ClusterwiseBudgetReport.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String getFinancialYear() {
		return financialYear;
	}

	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
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

	public String getStrLevel() {
		return strLevel;
	}

	public void setStrLevel(String strLevel) {
		this.strLevel = strLevel;
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

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
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

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
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

	public String getFreqNxtENDDATE() {
		return freqNxtENDDATE;
	}

	public void setFreqNxtENDDATE(String freqNxtENDDATE) {
		this.freqNxtENDDATE = freqNxtENDDATE;
	}

	public String getQfreqNxtENDDATE() {
		return qfreqNxtENDDATE;
	}

	public void setQfreqNxtENDDATE(String qfreqNxtENDDATE) {
		this.qfreqNxtENDDATE = qfreqNxtENDDATE;
	}
	
}
