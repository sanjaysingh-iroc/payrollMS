package com.konnect.jpms.task;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.select.FillYears;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class MonthWiseCommittedReport extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements{
	
	private static final long serialVersionUID = 1L;
	
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	
	String f_org;
	String strMonth;
	String financialYear;
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	String strStartDate;
	String strEndDate;
	String strSelectedMonthStartDate;
	String strSelectedMonthEndDate;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	
	
	List<FillMonth> monthList;
	List<FillYears> yearList;
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillFinancialYears> financialYearList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	
	String exportType;
	String freqENDDATE;
	String qfreqENDDATE;
	
	public String execute() throws Exception {
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		
		request.setAttribute(PAGE, "/jsp/task/MonthWiseCommittedReport.jsp");
		request.setAttribute(TITLE, "Monthwise Committed Receipt");
		
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
		
//		System.out.println("getExportType="+getExportType());
		viewMonthWiseCommitedReport(uF);
		if(getExportType()!= null && getExportType().equalsIgnoreCase("excel")){
			generateEmpSalaryYearlyExcelReport(uF);
		}
		
		return loadMonthWiseCommitedReport(uF);

	}
	
	public String loadMonthWiseCommitedReport(UtilityFunctions uF) {
		
		Map<String, String> hmOrg = CF.getOrgDetails(uF, getF_org(),request);
		if(hmOrg == null) hmOrg = new HashMap<String, String>();
		
		monthList = new FillMonth().fillMonth();
		
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
	
	public String viewMonthWiseCommitedReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		List<List<String>> alOuter = new ArrayList<List<String>>();
		
		try {
			
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
			if(getStrMonth()==null){
				setStrMonth("4");
			}
			
			if(getStrMonth()!=null){
				int nselectedMonth = uF.parseToInt(getStrMonth());
//				int nFYSMonth = uF.parseToInt(uF.getDateFormat(CF.getStrFinancialYearFrom(), DATE_FORMAT, "MM"));
				int nFYSMonth = 0;
				int nFYSDay = 0;
				String[] strFinancialYears = null;
//				System.out.println("getMonthFinancialYear() ===>> " + getMonthFinancialYear());
				strFinancialYears = getFinancialYear().split("-");
				nFYSMonth = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "MM"));
				nFYSDay = uF.parseToInt(uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, "dd"));
				
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
				
				setStrSelectedMonthStartDate(nMonthStart+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
				setStrSelectedMonthEndDate(nMonthEnd+"/"+getStrMonth()+"/"+cal.get(Calendar.YEAR));
			}
			
			con = db.makeConnection(con);
			
			List<String> reportAllHeads = new ArrayList<String>();
			String orgName = CF.getOrgNameById(con, getF_org());
			reportAllHeads.add(orgName);
			reportAllHeads.add("FOR THE YEAR "+uF.getDateFormat(strFinancialYearDates[0], DATE_FORMAT, "yyyy")+"-"+uF.getDateFormat(strFinancialYearDates[1], DATE_FORMAT, "yy"));
//			if(getStrMonth()!=null){
//				reportAllHeads.add("MIS "+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, "MMMM")+" "+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, "yyyy"));
//			}else{
			reportAllHeads.add("MIS "+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, "MMMM")+" "+uF.getDateFormat(getStrStartDate(), DATE_FORMAT, "yyyy"));
//			}
			
			request.setAttribute("reportAllHeads", reportAllHeads);
			
//			System.out.println("MWCR/237--startDate="+getStrStartDate()+"---EndDate="+getStrEndDate());
			Map<String, Map<String, String>> hmSbuwiseProjectData = new HashMap<String,Map<String, String>>();
			List<String> monthYearsList = new ArrayList<String>();
			StringBuilder sbQuery = new StringBuilder();
			Map<String, List<String>> hmSbuwiseProjectIds = new HashMap<String, List<String>>();
			
			Map<String, String> hmProWLocation = CF.getWLocationMap(con, null, request, null);
			Map<String, String> hmDepartment = CF.getDeptMap(con);
			Map<String, String> hmSbuName = CF.getServicesMap(con, false);
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			
			sbQuery.append("select cd.client_id, cd.client_name, p.pro_id, p.pro_name, p.org_id, p.project_code, p.wlocation_id, p.department_id, p.sbu_id, p.start_date," +
					" p.deadline, billing_type, billing_kind, billing_cycle_day, billing_cycle_weekday, billing_amount, curr_id from client_details cd, projectmntnc p " +
					" where cd.client_id = p.client_id and p.sbu_id>0 and (approve_status='n' or approve_status='pipelined') ");
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
//			sbQuery.append("and p.sbu_id in (130)");
			
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (deadline >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "')) ");
			}
			sbQuery.append(" order by p.sbu_id ");
	        pst = con.prepareStatement(sbQuery.toString());
//	        System.out.println("MWCR/249--pst =====>> "  +pst);
			rs = pst.executeQuery();
			StringBuilder sbProIds = null;
			while(rs.next()) {
				if(sbProIds ==null) {
					sbProIds = new StringBuilder();
					sbProIds.append(rs.getString("pro_id"));
				} else {
					sbProIds.append(","+rs.getString("pro_id"));
				}
				Map<String, String> hmInner = hmSbuwiseProjectData.get(rs.getString("sbu_id"));
				if(hmInner==null) hmInner = new HashMap<String, String>();
				
				List<String> alInner = hmSbuwiseProjectIds.get(rs.getString("sbu_id"));
				if(alInner==null) alInner = new ArrayList<String>();
				
				alInner.add(rs.getString("pro_id"));
				hmInner.put(rs.getString("pro_id")+"_LOCATION", hmProWLocation.get(rs.getString("wlocation_id")));

				hmInner.put(rs.getString("pro_id")+"_START_DATE", rs.getString("start_date"));
				hmInner.put(rs.getString("pro_id")+"_DEADLINE", rs.getString("deadline"));
				
				hmInner.put(rs.getString("pro_id")+"_BILL_FREQ", rs.getString("billing_kind"));
				hmInner.put(rs.getString("pro_id")+"_FREQ_DAY", rs.getString("billing_cycle_day"));
				hmInner.put(rs.getString("pro_id")+"_WEEKDAY", rs.getString("billing_cycle_weekday"));
				hmInner.put(rs.getString("pro_id")+"_BILLING_AMT", rs.getString("billing_amount"));
				hmInner.put(rs.getString("pro_id")+"_INVOICE_CURR_ID", rs.getString("curr_id"));
				
				if(rs.getString("sbu_id") != null){
					hmSbuwiseProjectData.put(rs.getString("sbu_id"), hmInner);
					hmSbuwiseProjectIds.put(rs.getString("sbu_id"), alInner);
				}
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmMileStoneMap = new HashMap<String, String>();
			if(sbProIds!=null) {
				pst = con.prepareStatement("select pro_id,project_milestone_id, pro_milestone_amount, milestone_end_date from project_milestone_details where pro_id in ("+sbProIds.toString()+")");
			    rs = pst.executeQuery();
//			    List<String> milestoneIds = new ArrayList<String>();
			    while(rs.next()) {
//			    	milestoneIds.add(rs.getString("project_milestone_id"));
			    	int x = uF.parseToInt(hmMileStoneMap.get(rs.getString("pro_id")+"_MS_COUNT"));
			    	x++;
			    	hmMileStoneMap.put(rs.getString("pro_id")+"_MS_AMT_"+x, rs.getString("pro_milestone_amount"));
			    	hmMileStoneMap.put(rs.getString("pro_id")+"_MS_END_DATE_"+x, rs.getString("milestone_end_date"));
			    	hmMileStoneMap.put(rs.getString("pro_id")+"_MS_COUNT", x+"");
			    }
			    rs.close();
				pst.close();
			}
			
			String currExcel ="";
			
			if(strFinancialYearStart != null && !strFinancialYearStart.equalsIgnoreCase("null") && !strFinancialYearStart.equals("") && strFinancialYearEnd != null && !strFinancialYearEnd.equalsIgnoreCase("null") && !strFinancialYearEnd.equals("")) {
				
				Date startDate = uF.getDateFormat(strFinancialYearStart, DATE_FORMAT);
				Date endDate1 = uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT);
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(startDate);
				int startMonth = cal.get(Calendar.MONTH)+1;
				int startYear = cal.get(Calendar.YEAR);
			    
			    Calendar cal2 = Calendar.getInstance();
				cal2.setTime(endDate1);
				int endMonth = cal2.get(Calendar.MONTH)+1;
				int endYear = cal2.get(Calendar.YEAR);
				
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
			    
			    Map<String, String> hmProjectData = new HashMap<String,String>();
			    Iterator<String> it1 = hmSbuwiseProjectData.keySet().iterator();
			    while (it1.hasNext()) {
			    	String sbuId = it1.next();
			    	hmProjectData = hmSbuwiseProjectData.get(sbuId);
			    	List<String> alInner = hmSbuwiseProjectIds.get(sbuId);
			    	
			    	List<String> proList = new ArrayList<String>();
			    	proList.add(hmSbuName.get(sbuId)); 		//0
			    	
			    	Map<String, String> hmBillAmt = new HashMap<String, String>();
//			    	Map<String, String> hmInvoiceDetails = new HashMap<String, String>();
			    	String currId = null;
			    	
			    	double additionAmt = 0;
			    	double receiptAmt = 0;
			    	double bigningMonthAmt = 0;
			    	double addCurrentMonthAmt = 0;
			    	double totalAmt = 0;
			    	for(int i=0;i<alInner.size() && alInner!=null && !alInner.isEmpty();i++) {
						String proId = alInner.get(i);
						
						Map<String, String> hmCurr = hmCurrencyMap.get(hmProjectData.get(proId+"_INVOICE_CURR_ID"));
						currId = hmCurr.get("SHORT_CURR");
						currExcel = hmCurr.get("SHORT_CURR_INR");
						
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
//							System.out.println("MWCR/378--tmpDate="+tmpDate+"--strFirstDate="+strFirstDate+"---strEndDate="+strEndDate);
							
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
								if(uF.parseToInt(hmMileStoneMap.get(proId+"_MS_COUNT"))>0){
									intCount = uF.parseToInt(hmMileStoneMap.get(proId+"_MS_COUNT"));
								}
							}
							
							double dblBillAmount = 0;
							setFreqENDDATE(null);
							
							Date proStDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_START_DATE"), DBDATE);
							Date proEdDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE);
							Date mnthStDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
							Date mnthEdDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
							
							boolean flag = false;
//							System.out.println("MWCR/415--proStDate="+proStDate+"--mnthStDate="+mnthStDate+"--mnthEdDate="+mnthEdDate);
							if(((proStDate.before(mnthStDate) || proStDate.equals(mnthStDate)) && (proEdDate.after(mnthEdDate) || proEdDate.equals(mnthEdDate))) ) {
								flag = true;
							}
							
							if(flag || (proStMnth == uF.parseToInt(dateArr[0]) && proStYr == uF.parseToInt(dateArr[1])) || (proEndMnth == uF.parseToInt(dateArr[0]) && proEndYr == uF.parseToInt(dateArr[1]))) {
								
								for(int j=0; j<intCount; j++) {
									String newStDate = getNewProjectStartDate(con, uF, proId, getFreqENDDATE());
								
									if(newStDate == null || newStDate.equals("")) {
											newStDate = strFirstDate;
									}
										
									boolean frqFlag = false;
									String freqEndDate = strEndDate;
									
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O")) {
										if(hmMileStoneMap!=null && !hmMileStoneMap.isEmpty() && hmMileStoneMap.get(proId+"_MS_END_DATE_"+(j+1)) != null) {
											freqEndDate = uF.getDateFormat(hmMileStoneMap.get(proId+"_MS_END_DATE_"+(j+1)), DBDATE, DATE_FORMAT);
										} else {
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
									dblBillAmount = uF.parseToDouble(hmBillAmt.get(sbuId+"_"+strFirstDate));
									
									setFreqENDDATE(freqEndDate);
									
									if(hmProjectData.get(proId+"_BILL_FREQ") != null && hmProjectData.get(proId+"_BILL_FREQ").equals("O") && hmMileStoneMap!=null && !hmMileStoneMap.isEmpty()) {
										if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
											dblBillAmount += uF.parseToInt(hmMileStoneMap.get(proId+"_MS_AMT_"+(j+1)));
										}
										
									} else{
										if((firstDate.before(newFreqDate) || firstDate.equals(newFreqDate)) && (endDate.after(newFreqDate) || endDate.equals(newFreqDate))) {
											dblBillAmount += uF.parseToInt(hmProjectData.get(proId+"_BILLING_AMT"));
											
										}
									}
									
									hmBillAmt.put(sbuId+"_"+strFirstDate, dblBillAmount+"");
									
									/*
									 * Date fEndDate2 =
									 * uF.getDateFormat(getStrEndDate(),
									 * DATE_FORMAT); Date fStartDate2 =
									 * uF.getDateFormat(getStrStartDate(),
									 * DATE_FORMAT);
									 * if(fEndDate2.after(firstDate) &&
									 * fStartDate2.before(endDate)){ //
									 * additionAmt+=
									 * uF.parseToDouble(hmBillAmt.get(sbuId+"_"+
									 * firstDate)); String strAdditionAmt =
									 * getInvoiceAmount(con, uF, proId,
									 * strFirstDate, strEndDate); additionAmt +=
									 * uF.parseToDouble(strAdditionAmt); }
									 */
									
								}
							}
						}
						
//						String strReceptAmount = getInvoiceAmount(con, uF, proId, strFinancialYearStart, strFinancialYearEnd);
//						receiptAmt += uF.parseToDouble(strReceptAmount);
//						
//						String strBigningMAmt = getInvoiceAmount(con, uF, proId, strFinancialYearStart, strFinancialYearStart);
//						bigningMonthAmt += uF.parseToDouble(strBigningMAmt);
						
//						if(getStrMonth()!=null){
////				    		proList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmBillAmt.get(sbuId+"_"+getStrEndDate())))+"");		//3
//				    		String strAddCurrentMonthAmt = getInvoiceAmount(con, uF, proId, getStrEndDate(), getStrEndDate());
//				    		addCurrentMonthAmt += uF.parseToDouble(strAddCurrentMonthAmt);
//				    	}else{
//				    		String strAddCurrentMonthAmt = getInvoiceAmount(con, uF, proId, getStrStartDate(), getStrStartDate());
//				    		addCurrentMonthAmt += uF.parseToDouble(strAddCurrentMonthAmt);
//				    	}
					}
			    	
//			    	proList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()), uF.parseToDouble(hmBillAmt.get(sbuId+"_"+getStrStartDate())))+"");		//1
			    	
			    	for(int k=0; k < monthYearsList.size(); k++){
			    		String strMonth1 = monthYearsList.get(k);
			    		String[] dateArr1 = strMonth1.split("/");
						String strFirstDate1 = null;
						
						String strDate1 = "01/"+dateArr1[0]+"/"+dateArr1[1];
						String minMaxDate1 = uF.getCurrentMonthMinMaxDate(strDate1, DATE_FORMAT);
						String[] tmpDate1 = minMaxDate1.split("::::");
						strFirstDate1 = tmpDate1[0];
						double strBillAmount = uF.parseToDouble(hmBillAmt.get(sbuId+"_"+strFirstDate1));
						if(k==0) {
							bigningMonthAmt = strBillAmount; 
						}
						
						Date fStartDate = uF.getDateFormat(getStrSelectedMonthStartDate(), DATE_FORMAT);
						Date mStartDate = uF.getDateFormat(strFirstDate1, DATE_FORMAT);
//						if(getStrMonth()!=null) {
//						if((fStartDate.after(mStartDate) || fStartDate.equals(mStartDate))){
							if(fStartDate.after(mStartDate)){
								additionAmt +=strBillAmount;
							}
							if(fStartDate.equals(mStartDate)){
								addCurrentMonthAmt = strBillAmount;
							}
							proList.add(uF.showData(uF.formatIntoComma(strBillAmount), "0"));
							totalAmt += strBillAmount;
//						} else {
//							proList.add("-");
//						}
//						}
						receiptAmt += strBillAmount;
			    	}
			    	
			    	proList.add(uF.showData(uF.formatIntoComma(totalAmt), "0"));	// 13

			    	proList.add(uF.showData(uF.formatIntoComma(bigningMonthAmt), "0"));	//14
			    	
			    	proList.add(uF.showData(uF.formatIntoComma(additionAmt), "0"));	//15
			    	proList.add(uF.showData(uF.formatIntoComma(addCurrentMonthAmt), "0"));	//16
			    	
			    	proList.add(uF.showData(uF.formatIntoComma(receiptAmt), "0")); 	//17
			    	proList.add(uF.showData(uF.formatIntoComma(receiptAmt), "0")); 	//18
			    	
			    	proList.add(currId);	//19
			    	
			    	alOuter.add(proList);
			    }
			}
//			System.out.println("MWCR/573--alOuter="+alOuter.get(0));
			request.setAttribute("monthYearsList", monthYearsList);
			request.setAttribute("currencyExc", currExcel);
			
			request.setAttribute("alOuter", alOuter);
		//===end===	
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return SUCCESS;

	}
	
	public String getInvoiceAmount(Connection con, UtilityFunctions uF, String proId, String strFromDate, String strToDate){
		
		double amount = 0;
		PreparedStatement pst = null;
		ResultSet rst = null;
		try {
			
			String minMaxStartDate = uF.getCurrentMonthMinMaxDate(strFromDate, DATE_FORMAT);
			String[] tmpStartDate = minMaxStartDate.split("::::");
			
			String minMaxEndDate = uF.getCurrentMonthMinMaxDate(strFromDate, DATE_FORMAT);
			String[] tmpEndDate = minMaxEndDate.split("::::");
			
			pst = con.prepareStatement("select * from promntc_invoice_details where pro_id=? and invoice_from_date between ? and ? " +
			" and invoice_to_date between ? and ?");
			pst.setInt(1, uF.parseToInt(proId));
			pst.setDate(2, uF.getDateFormat(tmpStartDate[0], DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(tmpStartDate[1], DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(tmpEndDate[0], DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(tmpEndDate[1], DATE_FORMAT));
			rst = pst.executeQuery();
			while(rst.next()){
				amount+= uF.parseToDouble(rst.getString("invoice_amount"));
			}
			rst.close();
			pst.close();
			
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (rst != null) {
				try {
					rst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			if (pst != null) {
				try {
					pst.close();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}
		
		
		return amount+"";
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
	
	private void generateEmpSalaryYearlyExcelReport(UtilityFunctions uF) {
		try {
			
			List reportAllHeads = (List)request.getAttribute("reportAllHeads");
			List monthYearsList = (List)request.getAttribute("monthYearsList");
			List alOuter = (List)request.getAttribute("alOuter");
			String currency = (String) request.getAttribute("currencyExc");
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Monthwise Commited Report");			
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			
//			header.add(new DataStyle(uF.showData(org_name, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			header.add(new DataStyle(reportAllHeads.get(0)+"",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	//0
			header.add(new DataStyle("CHARTERED ACCOUNTANTS",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));		//1
			header.add(new DataStyle(reportAllHeads.get(1)+"",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	//2	
			header.add(new DataStyle(reportAllHeads.get(2)+"",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	//3
			header.add(new DataStyle("MONTHWISE COMMITTED RECEIPTS",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	//4
			header.add(new DataStyle("TOTAL ASSIGNMENTS",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));		//5
			header.add(new DataStyle("PLANNED RECEIPTS",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));		//6
			header.add(new DataStyle("SR.NO",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));					//7
			header.add(new DataStyle("DIVISION",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));				//8
			header.add(new DataStyle("OP.COMM.AT THE BEGINNING OF THE YEAR",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	//9
			header.add(new DataStyle("ADDITIONS/ DELETIONS DURING THE YEAR",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	//10
			header.add(new DataStyle("COMMITED AT THE END OF THE MONTH",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));		//11
			header.add(new DataStyle("("+currency+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));		//12
			header.add(new DataStyle("ADDITION UPTO THE BEGINNING OF THE MONTH"+" ("+currency+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	//12
			header.add(new DataStyle("ADDITION DURING THIS MONTH"+" ("+currency+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));		//13
			header.add(new DataStyle("RECEIPTS TO HAPPEN DURING THIS YEAR"+" ("+currency+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));		//14
			header.add(new DataStyle("RECEIPTS TO HAPPEN IN THE NEXT YEAR"+" ("+currency+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));		//15
			for(int i=0; i<monthYearsList.size(); i++){
				header.add(new DataStyle(uF.getDateFormat(monthYearsList.get(i)+"","MM/yyyy","MMM").toUpperCase()+" ("+currency+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			}
			header.add(new DataStyle("TOTAL ("+currency+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			/*for(int j=0; j<monthWiseCommitedList.size(); j++){
				List<DataStyle> innerList=new ArrayList<DataStyle>();
				innerList.add(new DataStyle((j+1)+"",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle((String)monthWiseCommitedList.get(j),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				reportData.add(innerList);
			}*/
			
			for(int j=0; j<alOuter.size(); j++){
				List<String> alInner = (List<String>)alOuter.get(j);
				List<DataStyle> innerList=new ArrayList<DataStyle>();
				innerList.add(new DataStyle((j+1)+"",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle((String)alInner.get(0),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle((String)alInner.get(14),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle((String)alInner.get(15),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle((String)alInner.get(16),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle((String)alInner.get(17),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				innerList.add(new DataStyle((String)alInner.get(18),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				for(int k=1; k<=12; k++){
					innerList.add(new DataStyle((String)alInner.get(k),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				}
				innerList.add(new DataStyle((String)alInner.get(13),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				reportData.add(innerList);
			}

			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			//sheetDesign.generateExcelSheet(workbook,sheet,header,reportData);
			sheetDesign.generateExcelSheetforMonthwiseCommitedReport(workbook,sheet,header,reportData);
		
		
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=MonthwiseCommitedReport.xls");
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

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
	}

	public List<FillYears> getYearList() {
		return yearList;
	}

	public void setYearList(List<FillYears> yearList) {
		this.yearList = yearList;
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

	public String[] getF_strWLocation() {
		return f_strWLocation;
	}

	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}

	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
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

	public String getStrSelectedMonthEndDate() {
		return strSelectedMonthEndDate;
	}

	public void setStrSelectedMonthEndDate(String strSelectedMonthEndDate) {
		this.strSelectedMonthEndDate = strSelectedMonthEndDate;
	}

	public String getStrSelectedMonthStartDate() {
		return strSelectedMonthStartDate;
	}

	public void setStrSelectedMonthStartDate(String strSelectedMonthStartDate) {
		this.strSelectedMonthStartDate = strSelectedMonthStartDate;
	}
	
}
