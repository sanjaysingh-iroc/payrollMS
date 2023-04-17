package com.konnect.jpms.task;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class BudgetReport extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

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
		
		request.setAttribute(PAGE, "/jsp/task/BudgetReport.jsp");
		request.setAttribute(TITLE, "Budget Report");
		
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
		
		viewBudgetReport(uF);
		
		/*if(getExportType()!= null && getExportType().equalsIgnoreCase("excel")){
			generateBudgetExcelReport(uF);
		}*/
		
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
	
	public String viewBudgetReport(UtilityFunctions uF) {
		
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
			
			List<List<String>> alOuter = new ArrayList<List<String>>();
			Map<String, List<String>> hmPartnerwiseProIds = new HashMap<String, List<String>>();
			Map<String, Map<String, String>> hmPartnerwiseProData = new HashMap<String,Map<String, String>>();
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			
			StringBuilder sbQuery = new StringBuilder();
	//===start parvez date: 13-10-2022===		
//			sbQuery.append("select pro_id,project_owner,start_date,deadline,billing_kind,billing_cycle_day,billing_cycle_weekday," +
//					" billing_amount,curr_id from projectmntnc where project_owner>0 and approve_status != 'blocked' ");
			sbQuery.append("select pro_id,project_owners,start_date,deadline,billing_kind,billing_cycle_day,billing_cycle_weekday," +
					" billing_amount,curr_id from projectmntnc where approve_status != 'blocked' ");
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
			
			/*if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (deadline >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "')) ");
			}*/
			
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && nxtYrEndDate != null && !nxtYrEndDate.equalsIgnoreCase("null") && !nxtYrEndDate.equals("")) {
				sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(nxtYrEndDate, DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(nxtYrEndDate, DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(nxtYrEndDate, DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (deadline >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(nxtYrEndDate, DATE_FORMAT, DBDATE) + "')) ");
			}
			
		//===start parvez date: 13-10-2022===	
//			sbQuery.append(" order by project_owner ");
			sbQuery.append(" order by project_owners ");
		//===end parvez date: 13-10-2022===	
			pst = con.prepareStatement(sbQuery.toString());
	        rs = pst.executeQuery();
	        StringBuilder strProIds = null;
	        StringBuilder strProOwnerIds = null;
//	        Map<String,String> hmProWiseOwner = new HashMap<String, String>();
	        Map<String,List<String>> hmProWiseOwner = new HashMap<String, List<String>>();
	        List<String> alProIdList = new ArrayList<String>();
	        while(rs.next()) {
	        	
	        	/*if(strProIds == null){
	        		strProIds = new StringBuilder();
	        		strProIds.append(rs.getString("pro_id"));
	        	} else {
	        		strProIds.append(","+rs.getString("pro_id"));
	        	}
	        	
	        	if(strProOwnerIds == null){
	        		strProOwnerIds = new StringBuilder();
	        		strProOwnerIds.append(rs.getString("project_owner"));
	        	} else {
	        		strProOwnerIds.append(","+rs.getString("project_owner"));
	        	}
	        	
	        	alProIdList.add(rs.getString("pro_id"));
	        	hmProWiseOwner.put(rs.getString("pro_id"),rs.getString("project_owner"));
	        	
	        	Map<String, String> hmInner = hmPartnerwiseProData.get(rs.getString("project_owner"));
	        	if(hmInner == null) hmInner = new HashMap<String, String>();
	        	
	        	List<String> alInner = hmPartnerwiseProIds.get(rs.getString("project_owner"));
				if(alInner==null) alInner = new ArrayList<String>();
				
				alInner.add(rs.getString("pro_id"));
				
				hmInner.put(rs.getString("pro_id")+"_START_DATE", rs.getString("start_date"));
				hmInner.put(rs.getString("pro_id")+"_DEADLINE", rs.getString("deadline"));
				
				hmInner.put(rs.getString("pro_id")+"_BILL_FREQ", rs.getString("billing_kind"));
				hmInner.put(rs.getString("pro_id")+"_FREQ_DAY", rs.getString("billing_cycle_day"));
				hmInner.put(rs.getString("pro_id")+"_WEEKDAY", rs.getString("billing_cycle_weekday"));
				hmInner.put(rs.getString("pro_id")+"_BILLING_AMT", rs.getString("billing_amount"));
				hmInner.put(rs.getString("pro_id")+"_INVOICE_CURR_ID", rs.getString("curr_id"));
	        	
				hmPartnerwiseProData.put(rs.getString("project_owner"), hmInner);
	        	hmPartnerwiseProIds.put(rs.getString("project_owner"), alInner);*/
	        	
	        //===start parvez date: 13-10-2022===
	        	if(rs.getString("project_owners")!=null){
	        		if(strProIds == null){
    	        		strProIds = new StringBuilder();
    	        		strProIds.append(rs.getString("pro_id"));
    	        	} else {
    	        		strProIds.append(","+rs.getString("pro_id"));
    	        	}
	        		
	        		alProIdList.add(rs.getString("pro_id"));
	        		
	        		List<String> tempList = Arrays.asList(rs.getString("project_owners").split(","));
	        		for(int j=1; j<tempList.size();j++){
	        			
	    	        	if(strProOwnerIds == null){
	    	        		strProOwnerIds = new StringBuilder();
	    	        		strProOwnerIds.append(tempList.get(j));
	    	        	} else {
	    	        		strProOwnerIds.append(","+tempList.get(j));
	    	        	}
	    	        	
	    	        	List<String> alProOwner = hmProWiseOwner.get(rs.getString("pro_id"));
	    	        	if(alProOwner == null) alProOwner = new ArrayList<String>();
	    	        	
	    	        	alProOwner.add(tempList.get(j));
	    	        	
	    	        	hmProWiseOwner.put(rs.getString("pro_id"),alProOwner);
	    	        	
//	    	        	Map<String, String> hmInner = hmPartnerwiseProData.get(tempList.get(j)+"_"+rs.getString("pro_id"));
	    	        	Map<String, String> hmInner = hmPartnerwiseProData.get(tempList.get(j));
	    	        	if(hmInner == null) hmInner = new HashMap<String, String>();
	    	        	
//	    	        	List<String> alInner = hmPartnerwiseProIds.get(tempList.get(j)+"_"+rs.getString("pro_id"));
	    	        	List<String> alInner = hmPartnerwiseProIds.get(tempList.get(j));
	    				if(alInner==null) alInner = new ArrayList<String>();
	    				
	    				alInner.add(rs.getString("pro_id"));
	    				
	    				hmInner.put(rs.getString("pro_id")+"_START_DATE", rs.getString("start_date"));
	    				hmInner.put(rs.getString("pro_id")+"_DEADLINE", rs.getString("deadline"));
	    				
	    				hmInner.put(rs.getString("pro_id")+"_BILL_FREQ", rs.getString("billing_kind"));
	    				hmInner.put(rs.getString("pro_id")+"_FREQ_DAY", rs.getString("billing_cycle_day"));
	    				hmInner.put(rs.getString("pro_id")+"_WEEKDAY", rs.getString("billing_cycle_weekday"));
	    				hmInner.put(rs.getString("pro_id")+"_BILLING_AMT", rs.getString("billing_amount"));
	    				hmInner.put(rs.getString("pro_id")+"_INVOICE_CURR_ID", rs.getString("curr_id"));
	    	        	
//	    				hmPartnerwiseProData.put(tempList.get(j)+"_"+rs.getString("pro_id"), hmInner);
//	    	        	hmPartnerwiseProIds.put(tempList.get(j)+"_"+rs.getString("pro_id"), alInner);
	    				hmPartnerwiseProData.put(tempList.get(j), hmInner);
	    	        	hmPartnerwiseProIds.put(tempList.get(j), alInner);
	        		}
	        	}
	        //===end parvez date: 13-10-2022===	
	        	
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
			/*if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and financial_year_start= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and financial_year_end= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}*/
			
			sbQuery.append(" and financial_year_start>= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and financial_year_end<= '"+uF.getDateFormat(nxtYrEndDate, DATE_FORMAT)+"'");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("pst="+pst);
			while (rs.next()) {
//				hmBudgetMap.put(rs.getString("emp_id"), rs.getString("total_amount"));
				String dbStartDate = uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, DATE_FORMAT);
				String dbEndDate = uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, DATE_FORMAT);
				if(uF.getDateFormat(dbStartDate, DATE_FORMAT).equals(uF.getDateFormat(getStrStartDate(), DATE_FORMAT)) &&  uF.getDateFormat(dbEndDate, DATE_FORMAT).equals(uF.getDateFormat(getStrEndDate(), DATE_FORMAT))){
					
					hmBudgetMap.put(rs.getString("emp_id"), rs.getString("total_amount"));
				
				} else if(uF.getDateFormat(dbStartDate, DATE_FORMAT).equals(uF.getDateFormat(nxtYrStartDate, DATE_FORMAT)) &&  uF.getDateFormat(dbEndDate, DATE_FORMAT).equals(uF.getDateFormat(nxtYrEndDate, DATE_FORMAT))){
					hmNxtYrBudgetMap.put(rs.getString("emp_id"), rs.getString("total_amount"));
//					System.out.println("total_amount="+rs.getString("total_amount"));
				}
				
			}
			rs.close();
			pst.close();
//			System.out.println("hmNxtYrBudgetMap="+hmNxtYrBudgetMap);
			
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
			System.out.println("APWB/338--pst="+pst);
			while (rs.next()) {
				
				hmInoviceAmt.put(rs.getString("pro_owner_id"), rs.getString("invoice_particulars_amount"));
				
			}
			rs.close();
			pst.close();*/
			
			Map<String, String> hmInoviceAmt = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			
			sbQuery.append("select promntc_invoice_id,invoice_amount,pro_ids from promntc_invoice_details where promntc_invoice_id>0 ");
			if(alProIdList != null && !alProIdList.isEmpty()){
				sbQuery.append(" and ( ");
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
							List<String> alOwner = hmProWiseOwner.get(tempProId[i]);
							for(int j=0; alOwner!=null && j<alOwner.size();j++){
								String owner = alOwner.get(j);
								double invoiceAmt = uF.parseToDouble(rs.getString("invoice_amount"))+uF.parseToDouble(hmInoviceAmt.get(owner));
								hmInoviceAmt.put(owner, invoiceAmt+"");
							}
							
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
			    
			    double commitTot = 0;
			    double nxtCommitTot = 0;
			    double currYrBTot = 0;
		    	double nxtYrBudTot = 0;
		    	double actAmtTot = 0;
		    	String currency = "";
			    Map<String, String> hmProjectData = new HashMap<String,String>();
			    Iterator<String> it1 = hmPartnerwiseProData.keySet().iterator();
			    while (it1.hasNext()) {
			    	
			    	double commitAmt = 0;
			    	double nxtCommitAmt = 0;
			    	
			    	List<String> partnerList = new ArrayList<String>();
			    	
			    	String partnerId = it1.next();
			    	hmProjectData = hmPartnerwiseProData.get(partnerId);
			    	List<String> alInner = hmPartnerwiseProIds.get(partnerId);
//			    	String key = it1.next();
//			    	String[] keySplit = key.split("_");
//			    	String partnerId = keySplit[0];
//			    	hmProjectData = hmPartnerwiseProData.get(key);
//			    	List<String> alInner = hmPartnerwiseProIds.get(key);
			    	for(int i=0;i<alInner.size() && alInner!=null && !alInner.isEmpty();i++) {
			    		
			    		String proId = alInner.get(i);
			    		Map<String, String> hmCurr = hmCurrencyMap.get(hmProjectData.get(proId+"_INVOICE_CURR_ID"));
						currency = hmCurr.get("SHORT_CURR");
			    		
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
						
			    	}
			    	
			    	double currYrB = uF.parseToDouble(hmBudgetMap.get(partnerId));
			    	double nxtYrBud = uF.parseToDouble(hmNxtYrBudgetMap.get(partnerId));
			    	double actAmt = uF.parseToDouble(hmInoviceAmt.get(partnerId));
//			    	System.out.println("partnerId="+partnerId+"---currYrB="+currYrB+"---nxtYrBud="+nxtYrBud);
			    	
			    	currYrBTot += currYrB;
			    	nxtYrBudTot += nxtYrBud;
			    	actAmtTot += actAmt;
			    	nxtCommitTot += nxtCommitAmt;
			    	commitTot += commitAmt;
			    	
			    	partnerList.add(hmEmpName.get(partnerId));										//0
			    	partnerList.add(currency+" "+uF.showData(uF.formatIntoComma(actAmt),"0"));					//1										//1
			    	partnerList.add(currency+" "+uF.showData(uF.formatIntoComma(currYrB),"0"));					//2
			    	partnerList.add(currency+" "+uF.showData(uF.formatIntoComma(commitAmt),"0"));				//3
			    	partnerList.add(currency+" "+uF.showData(uF.formatIntoComma(nxtYrBud),"0"));					//4
			    	partnerList.add(currency+" "+uF.showData(uF.formatIntoComma(nxtCommitAmt),"0"));				//5
			    	partnerList.add("");															//6
			    	alOuter.add(partnerList);
			    }
			    
			    List<String> alTotal = new ArrayList<String>();
			    alTotal.add("Total");
			    alTotal.add(currency+" "+uF.showData(uF.formatIntoComma(actAmtTot),"0"));
			    alTotal.add(currency+" "+uF.showData(uF.formatIntoComma(currYrBTot),"0"));
			    alTotal.add(currency+" "+uF.showData(uF.formatIntoComma(commitTot),"0"));
			    alTotal.add(currency+" "+uF.showData(uF.formatIntoComma(nxtYrBudTot),"0"));
			    alTotal.add(currency+" "+uF.showData(uF.formatIntoComma(nxtCommitTot),"0"));
			    alTotal.add("");
			    alOuter.add(alTotal);
			}
			
			request.setAttribute("alOuter", alOuter);
			request.setAttribute("headerList", headerList);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return SUCCESS;
	}
	
	/*private void generateBudgetExcelReport(UtilityFunctions uF) {
		
	}*/
	
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
