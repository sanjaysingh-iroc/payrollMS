package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
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

public class AddPartnerwiseBudget extends ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	String strSessionEmpId = null;
	
	String financialYear;
	String strStartDate;
	String strEndDate;
	String strAprilAmt;
	String strMayAmt;
	String strJuneAmt;
	String strJulyAmt;
	String strAugustAmt;
	String strSeptemberAmt;
	String strOctoberAmt;
	String strNovemberAmt;
	String strDecemberAmt;
	String strJanuaryAmt;
	String strFebruaryAmt;
	String strMarchAmt;
	String strTotalAmt;
	String submit;
	
	String partnerId;
	String partnerName;
	String empId;
	String operation;
	
	String qfreqENDDATE;
	String freqENDDATE;
	
	List<FillFinancialYears> financialYearList;
	List<FillEmployee> partnerList;
	
	public String execute() throws Exception {
		
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, "/jsp/task/AddPartnerwiseBudget.jsp");
		request.setAttribute(TITLE, "Add Partner wise Budget");
		
//		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		boolean pageLoadFlag = false;
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
			pageLoadFlag = true;
		}
		
		if(getPartnerId() == null){
			setPartnerId(strSessionEmpId);
		}
		
		if(getPartnerId() != null){
			setEmpId(getPartnerId());
		}
		
//		System.out.println("submit="+getSubmit());
		/*if(getSubmit() != null){
			insertPartnerBudget();
		}*/
		
		if(getOperation() != null){
			insertPartnerBudget();
		}
		
		viewPartnerBudgetDetails(uF);
		
		//return LOAD;
		return loadPartnerBudgetDetails(uF,pageLoadFlag);
	}
	
	public String loadPartnerBudgetDetails(UtilityFunctions uF, boolean pageLoadFlag) {
		
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
		partnerList = new FillEmployee(request).fillEmployeeNameByParentLevel(0);
		
		getSelectedFilter(uF);
		
//		System.out.println("pageLoadFlag ==>>> " + pageLoadFlag);
		
//		return LOAD;
		if (!pageLoadFlag) {
			return SUCCESS;
		} else {
			return LOAD;
		}
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
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
	
	public String viewPartnerBudgetDetails(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			
			/*String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			
			if (getFinancialYear() != null) {
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
//				setStrStartDate(strFinancialYearStart);
//				setStrEndDate(strFinancialYearEnd);
				
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
//				setStrStartDate(strFinancialYearStart);
//				setStrEndDate(strFinancialYearEnd);
			}*/
			
			con = db.makeConnection(con);
			
			setPartnerName(CF.getEmpNameMapByEmpId(con, getEmpId()));
			
			List<String> alPartnersBudget = new ArrayList<String>();
			List<String> monthYearsList = new ArrayList<String>();
			Map<String, String> hmEmpProDetails = new HashMap<String, String>();
			List<String> alInner = new ArrayList<String>();
			
			pst = con.prepareStatement("select * from partner_budget_details where emp_id=? and financial_year_start=? and financial_year_end=?");
			pst.setInt(1, uF.parseToInt(getPartnerId()));
			pst.setDate(2, uF.getDateFormat(getStrStartDate(), DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(getStrEndDate(), DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println("pst="+pst);
			while(rs.next()){
				
				alPartnersBudget.add(rs.getString("month_apr_amount"));
				alPartnersBudget.add(rs.getString("month_may_amount"));
				alPartnersBudget.add(rs.getString("month_jun_amount"));
				alPartnersBudget.add(rs.getString("month_jul_amount"));
				alPartnersBudget.add(rs.getString("month_aug_amount"));
				alPartnersBudget.add(rs.getString("month_sep_amount"));
				alPartnersBudget.add(rs.getString("month_oct_amount"));
				alPartnersBudget.add(rs.getString("month_nov_amount"));
				alPartnersBudget.add(rs.getString("month_dec_amount"));
				alPartnersBudget.add(rs.getString("month_jan_amount"));
				alPartnersBudget.add(rs.getString("month_feb_amount"));
				alPartnersBudget.add(rs.getString("month_mar_amount"));
				alPartnersBudget.add(rs.getString("total_amount"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmProData = new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 12-10-2022===	
			/*sbQuery.append("select * from projectmntnc where project_owner = ? and approve_status != 'blocked' ");*/
			sbQuery.append("select * from projectmntnc where project_owners like '%,"+getPartnerId()+",%' and approve_status != 'blocked' ");
			
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (deadline >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "')) ");
			}
	        pst = con.prepareStatement(sbQuery.toString());
//	        pst.setInt(1, uF.parseToInt(getPartnerId()));
	   //===end parvez date: 12-10-2022=== 
	        rs = pst.executeQuery();
//	        System.out.println("pst==>"+pst);
	        StringBuilder strProIds = null;
	        while(rs.next()) {
	        	
	        	alInner.add(rs.getString("pro_id"));
	        	if(strProIds == null){
	        		strProIds = new StringBuilder();
	        		strProIds.append(rs.getString("pro_id"));
	        	} else {
	        		strProIds.append(","+rs.getString("pro_id"));
	        	}
	        	
				hmProData.put(rs.getString("pro_id")+"_START_DATE", rs.getString("start_date"));
				hmProData.put(rs.getString("pro_id")+"_DEADLINE", rs.getString("deadline"));
				
				hmProData.put(rs.getString("pro_id")+"_BILL_FREQ", rs.getString("billing_kind"));
				hmProData.put(rs.getString("pro_id")+"_FREQ_DAY", rs.getString("billing_cycle_day"));
				hmProData.put(rs.getString("pro_id")+"_WEEKDAY", rs.getString("billing_cycle_weekday"));
				hmProData.put(rs.getString("pro_id")+"_BILLING_AMT", rs.getString("billing_amount"));
	        	
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
			
			/*Map<String, String> hmInoviceAmt = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select invoice_particulars,invoice_particulars_amount,invoice_generated_date,pro_id from promntc_invoice_amt_details piad, promntc_invoice_details pid " +
					" where piad.promntc_invoice_id = pid.promntc_invoice_id and pro_id in("+strProIds+")");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and invoice_generated_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strGaneratDate = uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, "MM/yyyy");
				double costAmt = uF.parseToDouble(hmInoviceAmt.get(rs.getString("pro_id")+"_"+strGaneratDate))+uF.parseToDouble(rs.getString("invoice_particulars_amount"));
				hmInoviceAmt.put(rs.getString("pro_id")+"_"+strGaneratDate, costAmt+"");
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmRecivedAmt = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select received_amount, pro_id, entry_date from promntc_bill_amt_details ");
			if(strProIds!=null) {
				sbQuery.append(" where pro_id in ("+strProIds.toString()+") ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()) {
				
				String strDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, "MM/yyyy");
				double recievedAmt = uF.parseToDouble(hmRecivedAmt.get(rs.getString("pro_id")+"_"+strDate))+uF.parseToDouble(rs.getString("received_amount"));
				hmRecivedAmt.put(rs.getString("pro_id")+"_"+strDate, recievedAmt+"");
			}
			rs.close();
			pst.close();*/
			
			Map<String, String> hmInoviceAmt = new HashMap<String, String>();
			
			sbQuery = new StringBuilder();
		//===start parvez date: 22-01-2022===	
			sbQuery.append("select promntc_invoice_id,invoice_amount, pro_ids,invoice_generated_date from promntc_invoice_details where promntc_invoice_id>0 ");
			if(alInner !=null && !alInner.isEmpty()){
				sbQuery.append(" and ( ");
				for(int i=0; i<alInner.size(); i++){
					sbQuery.append("pro_ids like '%,"+alInner.get(i)+",%'");
					if(i<alInner.size()-1){
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" )");
			}
		//===end parvez date: 22-01-2022===	
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and invoice_generated_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("APWB/346--pst="+pst);
			rs = pst.executeQuery();
			
			Map<String,List<String>> hmInvoiceProIds = new HashMap<String, List<String>>();
			List<String> alInoviceId = new ArrayList<String>();
			while(rs.next()) {
				if(rs.getString("pro_ids") != null){
					String[] tempProId = rs.getString("pro_ids").split(",");
					List<String> alInnerList = new ArrayList<String>();
					for(int i=0; i<tempProId.length; i++){
						if(i>0){
							String strGaneratDate = uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, "MM/yyyy");
							double costAmt = uF.parseToDouble(hmInoviceAmt.get(tempProId[i]+"_"+strGaneratDate))+uF.parseToDouble(rs.getString("invoice_amount"));
							hmInoviceAmt.put(tempProId[i]+"_"+strGaneratDate, costAmt+"");
							alInnerList.add(tempProId[i]);
						}
					}
					alInoviceId.add(rs.getString("promntc_invoice_id"));
					hmInvoiceProIds.put(rs.getString("promntc_invoice_id"), alInnerList);
				}
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmRecivedAmt = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select received_amount, invoice_ids,entry_date from promntc_bill_amt_details where ");
			
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" entry_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"' ");
			}
			
			if(alInoviceId != null && !alInoviceId.isEmpty()){
				sbQuery.append(" and (");
				for(int i=0; i<alInoviceId.size(); i++){
					sbQuery.append("invoice_ids like '%,"+alInoviceId.get(i)+",%'");
					if(i<alInoviceId.size()-1){
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" )");
			}
			
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst="+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				
				if(rs.getString("invoice_ids")!=null){
					String[] invoiceId = rs.getString("invoice_ids").split(",");
					for(int k=0; k<invoiceId.length; k++){
						if(k>0){
							List<String> inner = hmInvoiceProIds.get(invoiceId[k]);
							for(int j=0; j<inner.size(); j++){
								String strDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, "MM/yyyy");
								double recievedAmt = uF.parseToDouble(hmRecivedAmt.get(inner.get(j)+"_"+strDate))+uF.parseToDouble(rs.getString("received_amount"));
								hmRecivedAmt.put(inner.get(j)+"_"+strDate, recievedAmt+"");
							}
						}
					}
				}
				
			}
			rs.close();
			pst.close();
			
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				int startDay = 0;
				int endDay = 0;
				int startMonth = 0;
				int endMonth = 0;
				int startYear = 0;
				int endYear = 0;
				int start_month = 0;
				int start_year = 0;
				Date startDate = uF.getDateFormat(getStrStartDate(), DATE_FORMAT);
				Date endDate1 = uF.getDateFormat(getStrEndDate(), DATE_FORMAT);
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
				
//				Iterator<String> it = hmProData.keySet().iterator();
//				while (it.hasNext()) {
				for(int i=0;i<alInner.size() && alInner!=null && !alInner.isEmpty();i++) {
					
//					String proId = it.next();
					String proId = alInner.get(i);
					
					int proStMnth = uF.parseToInt(uF.getDateFormat(hmProData.get(proId+"_START_DATE"), DBDATE, "MM")); 
					int proStYr = uF.parseToInt(uF.getDateFormat(hmProData.get(proId+"_START_DATE"), DBDATE, "yyyy"));
					
				    int proEndMnth = uF.parseToInt(uF.getDateFormat(hmProData.get(proId+"_DEADLINE"), DBDATE, "MM")); 
					int proEndYr = uF.parseToInt(uF.getDateFormat(hmProData.get(proId+"_DEADLINE"), DBDATE, "yyyy"));
					
					setQfreqENDDATE(null);
					
					Iterator<String> itr = monthYearsList.iterator();
					double totalRecivedAmt = 0;
				    double totalInvoiceAmt = 0;
				    double totalBillAmount = 0;
					while(itr.hasNext()){
						
						String month = itr.next();
						
						double recivedAmt = 0;
					    double invoiceAmt = 0;
					    
					   
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
						if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("A")) {
							intCount = intMonths/12;
							intCount++;
						}else if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("H")) {
							intCount = intMonths/6;
							intCount++;
						}else if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("Q")) {
							intCount = intMonths/3;
							intCount++;
						} else if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("M")) {
							intCount = intMonths;
						} else if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("B")) {
							intCount = (intMonths * 2);
						} else if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("W")) {
							String strDays = uF.dateDifference(strFirstDate, DATE_FORMAT, strEndDate, DATE_FORMAT);
							intCount = (uF.parseToInt(strDays) / 7);
							intCount++;	
						} else if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("O")) {
							if(milestoneIds.size()>0){
								intCount = milestoneIds.size();
							}
						}
						
						double dblBillAmount = 0;
						setFreqENDDATE(null);
						
						Date proStDate = uF.getDateFormatUtil(hmProData.get(proId+"_START_DATE"), DBDATE);
						Date proEdDate = uF.getDateFormatUtil(hmProData.get(proId+"_DEADLINE"), DBDATE);
						Date mnthStDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
						Date mnthEdDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
//						System.out.println("proStDate="+proStDate+"----proEdDate="+proEdDate);
						
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
								
								if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("O")) {
									if(hmMileStoneMap!=null && !hmMileStoneMap.isEmpty() && hmMileStoneMap.get(proId+"_MS_END_DATE_"+j) != null){
										freqEndDate = uF.getDateFormat(hmMileStoneMap.get(proId+"_MS_END_DATE_"+j), DBDATE, DATE_FORMAT);
									} else{
										freqEndDate = uF.getDateFormat(hmProData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
									}
							
								}
								
								if(uF.parseToInt(hmProData.get(proId+"_FREQ_DAY")) > 0) {
									
									freqEndDate = hmProData.get(proId+"_FREQ_DAY") + "/" + uF.getDateFormat(newStDate, DATE_FORMAT, "MM")+ "/" +uF.getDateFormat(newStDate, DATE_FORMAT, "yyyy");
									freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
									
									Date stDate = uF.getDateFormatUtil(newStDate, DATE_FORMAT);
									Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
									
									if(freqDate.after(stDate)) {
										frqFlag = true;
									}
									
									if(frqFlag) {
										if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("M")) {
											freqEndDate = freqEndDate;
										} else if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("B")) {
											freqEndDate = uF.getDateFormat(uF.getFutureDate(newStDate, 15)+"", DBDATE, DATE_FORMAT);
										}
									} else {
										if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("M")) {
											freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(freqEndDate, 1)+"", DBDATE, DATE_FORMAT);
										} else if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("B")) {
											freqEndDate = uF.getDateFormat(uF.getFutureDate(strFirstDate, 15)+"", DBDATE, DATE_FORMAT);
										}
									}
								}
								
								if(hmProData.get(proId+"_WEEKDAY") != null && !hmProData.get(proId+"_WEEKDAY").equals("") && hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("W")) {
									freqEndDate = uF.getDateFormat(uF.getDateOfPassedDay(newStDate, hmProData.get(proId+"_WEEKDAY"))+"", DBDATE, DATE_FORMAT);
									freqEndDate = uF.getDateFormat(""+uF.getFutureDate(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT), -1),DBDATE, DATE_FORMAT);
								}
								
								if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("Q")) {
									 Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
									 
									 if(getQfreqENDDATE() == null) {
										 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
										 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
										 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -3)+"", DBDATE, DATE_FORMAT);
										 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 2, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
										
										 setQfreqENDDATE(freqEndDate);
										 
										} else {
											freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 3)+"", DBDATE, DATE_FORMAT);
										}
									 
									 if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProData.get(proId+"_DEADLINE"), DBDATE))) {
										freqEndDate = uF.getDateFormat(hmProData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
										
									 }
									 if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
										setQfreqENDDATE(freqEndDate);
									 }
										
								}
								if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("H")) {
									Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
									if(getQfreqENDDATE() == null) {
										 String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
										 String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
										 String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -6)+"", DBDATE, DATE_FORMAT);
										 freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 5, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
										 
										setQfreqENDDATE(freqEndDate);
									} else {
										freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 6)+"", DBDATE, DATE_FORMAT);
									}
									if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProData.get(proId+"_DEADLINE"), DBDATE))) {
										freqEndDate = uF.getDateFormat(hmProData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
									}
									if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
										setQfreqENDDATE(freqEndDate);
									}
								}
								if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("A")) {
									Date freqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
									if(getQfreqENDDATE() == null) {
										String tmpMinMaxDate = uF.getCurrentMonthMinMaxDate(uF.getDateFormat(hmProData.get(proId+"_START_DATE"), DBDATE, DATE_FORMAT), DATE_FORMAT);
										String[] arrTmpMinMaxDate = tmpMinMaxDate.split("::::");
										String prevfreqDate = uF.getDateFormat(uF.getFutureMonthDate(uF.getDateFormatUtil(freqDate, DATE_FORMAT), -12)+"", DBDATE, DATE_FORMAT);
										freqEndDate = uF.getFrequencyEndDate(uF, uF.getDateFormatUtil(prevfreqDate, DATE_FORMAT), 11, arrTmpMinMaxDate[1], arrTmpMinMaxDate[1], freqDate, 0);
										setQfreqENDDATE(freqEndDate);
										 
									} else { 
										freqEndDate = uF.getDateFormat(uF.getFutureMonthDate(getQfreqENDDATE(), 12)+"", DBDATE, DATE_FORMAT);
									}
									
									if(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT).after(uF.getDateFormatUtil(hmProData.get(proId+"_DEADLINE"), DBDATE))) {
										freqEndDate = uF.getDateFormat(hmProData.get(proId+"_DEADLINE"), DBDATE, DATE_FORMAT);
									}
									
									if(freqDate.after(uF.getDateFormatUtil(freqEndDate, DATE_FORMAT))) {
										setQfreqENDDATE(freqEndDate);
									}
								}
								Date firstDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
								Date endDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
								Date newFreqDate = uF.getDateFormatUtil(freqEndDate, DATE_FORMAT);
								
								setFreqENDDATE(freqEndDate);
								
								if(hmProData.get(proId+"_BILL_FREQ") != null && hmProData.get(proId+"_BILL_FREQ").equals("O") && hmMileStoneMap!=null && !hmMileStoneMap.isEmpty()) {
									if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
										dblBillAmount += uF.parseToInt(hmMileStoneMap.get(proId+"_MS_AMT_"+j));
									}
									
								} else{
									if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
										dblBillAmount += uF.parseToInt(hmProData.get(proId+"_BILLING_AMT"));
									}
								}
								
							}
						}
						
						invoiceAmt = uF.parseToDouble(hmInoviceAmt.get(proId+"_"+uF.getDateFormat(strFirstDate, DATE_FORMAT, "MM/yyyy")))+uF.parseToDouble(hmEmpProDetails.get(month+"_INVOICE"));
						recivedAmt = uF.parseToDouble(hmRecivedAmt.get(proId+"_"+uF.getDateFormat(strFirstDate, DATE_FORMAT, "MM/yyyy"))) + uF.parseToDouble(hmEmpProDetails.get(month+"_RECEIVED"));
						
						totalInvoiceAmt += invoiceAmt;
						totalRecivedAmt += recivedAmt;
						totalBillAmount += dblBillAmount;
						hmEmpProDetails.put(month+"_COMMITMENT", uF.formatIntoComma(dblBillAmount));
						hmEmpProDetails.put(month+"_INVOICE", uF.formatIntoComma(invoiceAmt));
						hmEmpProDetails.put(month+"_RECEIVED", uF.formatIntoComma(recivedAmt));
					}
					hmEmpProDetails.put("TOTAL_COMMITMENT", uF.formatIntoComma(totalBillAmount));
					hmEmpProDetails.put("TOTAL_INVOICE", uF.formatIntoComma(totalInvoiceAmt));
					hmEmpProDetails.put("TOTAL_RECEIVED", uF.formatIntoComma(totalRecivedAmt));
				}
			}
			
			request.setAttribute("alPartnersBudget", alPartnersBudget);
			request.setAttribute("hmEmpProDetails", hmEmpProDetails);
			request.setAttribute("monthYearsList", monthYearsList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
		
		
		return SUCCESS;
		
	}
	
	public void insertPartnerBudget(){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		UtilityFunctions uF = new UtilityFunctions();
		
		try{
			con = db.makeConnection(con);
			
			String[] strFinancialYearDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;
			
			if (getFinancialYear() != null) {
				strFinancialYearDates = getFinancialYear().split("-");
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			} else {
				strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFinancialYearDates[0] + "-" + strFinancialYearDates[1]);
				
				strFinancialYearStart = strFinancialYearDates[0];
				strFinancialYearEnd = strFinancialYearDates[1];
			}
			
			pst = con.prepareStatement("update partner_budget_details set month_apr_amount=?, month_may_amount=?, month_jun_amount=?," +
					" month_jul_amount=?, month_aug_amount=?, month_sep_amount=?, month_oct_amount=?, month_nov_amount=?, month_dec_amount=?, month_jan_amount=?, " +
					"month_feb_amount=?, month_mar_amount=?, total_amount=?, updated_by=?, update_date=? where emp_id=? and financial_year_start=? and financial_year_end=?");
			pst.setDouble(1, uF.parseToDouble(getStrAprilAmt()));
			pst.setDouble(2, uF.parseToDouble(getStrMayAmt()));
			pst.setDouble(3, uF.parseToDouble(getStrJuneAmt()));
			pst.setDouble(4, uF.parseToDouble(getStrJulyAmt()));
			pst.setDouble(5, uF.parseToDouble(getStrAugustAmt()));
			pst.setDouble(6, uF.parseToDouble(getStrSeptemberAmt()));
			pst.setDouble(7, uF.parseToDouble(getStrOctoberAmt()));
			pst.setDouble(8, uF.parseToDouble(getStrNovemberAmt()));
			pst.setDouble(9, uF.parseToDouble(getStrDecemberAmt()));
			pst.setDouble(10, uF.parseToDouble(getStrJanuaryAmt()));
			pst.setDouble(11, uF.parseToDouble(getStrFebruaryAmt()));
			pst.setDouble(12, uF.parseToDouble(getStrMarchAmt()));
			pst.setDouble(13, uF.parseToDouble(getStrTotalAmt()));
			pst.setInt(14, uF.parseToInt(strSessionEmpId));
			pst.setTimestamp(15, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
			pst.setInt(16, uF.parseToInt(getPartnerId()));
			pst.setDate(17, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(18, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("APB/257--pst="+pst);
			int update = pst.executeUpdate();
			pst.close();
			
			if(update == 0){
				pst = con.prepareStatement("insert into partner_budget_details(emp_id, financial_year_start, financial_year_end, month_apr_amount, month_may_amount, " +
						" month_jun_amount, month_jul_amount, month_aug_amount, month_sep_amount, month_oct_amount, month_nov_amount, month_dec_amount, month_jan_amount, month_feb_amount, " +
						" month_mar_amount, total_amount, added_by, entry_date)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				pst.setInt(1, uF.parseToInt(getPartnerId()));
				pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setDouble(4, uF.parseToDouble(getStrAprilAmt()));
				pst.setDouble(5, uF.parseToDouble(getStrMayAmt()));
				pst.setDouble(6, uF.parseToDouble(getStrJuneAmt()));
				pst.setDouble(7, uF.parseToDouble(getStrJulyAmt()));
				pst.setDouble(8, uF.parseToDouble(getStrAugustAmt()));
				pst.setDouble(9, uF.parseToDouble(getStrSeptemberAmt()));
				pst.setDouble(10, uF.parseToDouble(getStrOctoberAmt()));
				pst.setDouble(11, uF.parseToDouble(getStrNovemberAmt()));
				pst.setDouble(12, uF.parseToDouble(getStrDecemberAmt()));
				pst.setDouble(13, uF.parseToDouble(getStrJanuaryAmt()));
				pst.setDouble(14, uF.parseToDouble(getStrFebruaryAmt()));
				pst.setDouble(15, uF.parseToDouble(getStrMarchAmt()));
				pst.setDouble(16, uF.parseToDouble(getStrTotalAmt()));
				pst.setInt(17, uF.parseToInt(strSessionEmpId));
				pst.setTimestamp(18, uF.getTimeStamp(uF.getCurrentDate(CF.getStrTimeZone())+""+ uF.getCurrentTime(CF.getStrTimeZone()), DBDATE+DBTIME));
//				System.out.println("APB/282--pst="+pst);
				update = pst.executeUpdate();
				pst.close();
			}
			
			if(update >= 0){
				request.setAttribute(MESSAGE, SUCCESSM+ "Budget has been updated successfully."+END);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeConnection(con);
		}
	}
	
	HttpServletRequest request;
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getFinancialYear() {
		return financialYear;
	}
	
	public void setFinancialYear(String financialYear) {
		this.financialYear = financialYear;
	}
	
	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}
	
	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
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

	public String getStrAprilAmt() {
		return strAprilAmt;
	}

	public void setStrAprilAmt(String strAprilAmt) {
		this.strAprilAmt = strAprilAmt;
	}

	public String getStrMayAmt() {
		return strMayAmt;
	}

	public void setStrMayAmt(String strMayAmt) {
		this.strMayAmt = strMayAmt;
	}

	public String getStrJuneAmt() {
		return strJuneAmt;
	}

	public void setStrJuneAmt(String strJuneAmt) {
		this.strJuneAmt = strJuneAmt;
	}

	public String getStrJulyAmt() {
		return strJulyAmt;
	}

	public void setStrJulyAmt(String strJulyAmt) {
		this.strJulyAmt = strJulyAmt;
	}

	public String getStrAugustAmt() {
		return strAugustAmt;
	}

	public void setStrAugustAmt(String strAugustAmt) {
		this.strAugustAmt = strAugustAmt;
	}

	public String getStrSeptemberAmt() {
		return strSeptemberAmt;
	}

	public void setStrSeptemberAmt(String strSeptemberAmt) {
		this.strSeptemberAmt = strSeptemberAmt;
	}

	public String getStrOctoberAmt() {
		return strOctoberAmt;
	}

	public void setStrOctoberAmt(String strOctoberAmt) {
		this.strOctoberAmt = strOctoberAmt;
	}

	public String getStrNovemberAmt() {
		return strNovemberAmt;
	}

	public void setStrNovemberAmt(String strNovemberAmt) {
		this.strNovemberAmt = strNovemberAmt;
	}

	public String getStrDecemberAmt() {
		return strDecemberAmt;
	}

	public void setStrDecemberAmt(String strDecemberAmt) {
		this.strDecemberAmt = strDecemberAmt;
	}

	public String getStrJanuaryAmt() {
		return strJanuaryAmt;
	}

	public void setStrJanuaryAmt(String strJanuaryAmt) {
		this.strJanuaryAmt = strJanuaryAmt;
	}

	public String getStrFebruaryAmt() {
		return strFebruaryAmt;
	}

	public void setStrFebruaryAmt(String strFebruaryAmt) {
		this.strFebruaryAmt = strFebruaryAmt;
	}

	public String getStrMarchAmt() {
		return strMarchAmt;
	}

	public void setStrMarchAmt(String strMarchAmt) {
		this.strMarchAmt = strMarchAmt;
	}

	public String getStrTotalAmt() {
		return strTotalAmt;
	}

	public void setStrTotalAmt(String strTotalAmt) {
		this.strTotalAmt = strTotalAmt;
	}

	public String getSubmit() {
		return submit;
	}

	public void setSubmit(String submit) {
		this.submit = submit;
	}

	public List<FillEmployee> getPartnerList() {
		return partnerList;
	}

	public void setPartnerList(List<FillEmployee> partnerList) {
		this.partnerList = partnerList;
	}

	public String getPartnerId() {
		return partnerId;
	}

	public void setPartnerId(String partnerId) {
		this.partnerId = partnerId;
	}

	public String getEmpId() {
		return empId;
	}

	public void setEmpId(String empId) {
		this.empId = empId;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getQfreqENDDATE() {
		return qfreqENDDATE;
	}

	public void setQfreqENDDATE(String qfreqENDDATE) {
		this.qfreqENDDATE = qfreqENDDATE;
	}

	public String getFreqENDDATE() {
		return freqENDDATE;
	}

	public void setFreqENDDATE(String freqENDDATE) {
		this.freqENDDATE = freqENDDATE;
	}

	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
	
}
