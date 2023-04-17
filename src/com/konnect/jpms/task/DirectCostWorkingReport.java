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
import java.util.HashSet;
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

public class DirectCostWorkingReport extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {
	
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
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	String[] f_service;
	
	
	List<FillMonth> monthList;
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillFinancialYears> financialYearList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	
	String exportType;
	
	public String execute() throws Exception {
		session = request.getSession();
		
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if (CF == null)
			return LOGIN;
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		
		request.setAttribute(PAGE, "/jsp/task/DirectCostWorkingReport.jsp");
		request.setAttribute(TITLE, "Direct Cost Working Report");
		
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
		
		viewDirectCostWorkingReport(uF);
		
		if(getExportType()!= null && getExportType().equalsIgnoreCase("excel")){
			generateDirectCostWorkingReport(uF);
		}
		
		return loadPartnerwiseReport(uF);

	}
	
	public String loadPartnerwiseReport(UtilityFunctions uF) {
		
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
	
	public String viewDirectCostWorkingReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
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
			
			con = db.makeConnection(con);
			
			Map<String,String> hmReportData = new HashMap<String, String>();
			Map<String,String> hmPartner = new HashMap<String, String>();
			List<String> monthYearsList = new ArrayList<String>();
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			
			Map<String, Map<String, String>> hmPartnerwiseProData = new HashMap<String,Map<String, String>>();
			Map<String, List<String>> hmPartnerwiseProIds = new HashMap<String, List<String>>();
			
			StringBuilder sbQuery = new StringBuilder();
//			sbQuery.append("select * from projectmntnc where project_owner>0 and approve_status != 'blocked' ");
			sbQuery.append("select * from projectmntnc where approve_status != 'blocked' ");
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
			
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (deadline >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "')) ");
			}
			sbQuery.append(" order by project_owners ");
	        pst = con.prepareStatement(sbQuery.toString());
	        rs = pst.executeQuery();
	        
	        StringBuilder strProIds = null;
	        List<String> alProIdList = new ArrayList<String>();
	        while(rs.next()) {
	        	/*if(!alPartnerList.contains(hmEmpName.get(rs.getString("project_owner")))){
	        		alPartnerList.add(hmEmpName.get(rs.getString("project_owner")));
	        	}*/
	        	
	        	/*if(strProIds == null){
	        		strProIds = new StringBuilder();
	        		strProIds.append(rs.getString("pro_id"));
	        	} else {
	        		strProIds.append(","+rs.getString("pro_id"));
	        	}
	        	alProIdList.add(rs.getString("pro_id"));
	        	
	        	Map<String, String> hmInner = hmPartnerwiseProData.get(rs.getString("project_owner"));
	        	if(hmInner == null) hmInner = new HashMap<String, String>();
	        	
	        	List<String> alInner = hmPartnerwiseProIds.get(rs.getString("project_owner"));
				if(alInner==null) alInner = new ArrayList<String>();
				
				alInner.add(rs.getString("pro_id"));

				hmInner.put(rs.getString("pro_id")+"_START_DATE", rs.getString("start_date"));
				hmInner.put(rs.getString("pro_id")+"_DEADLINE", rs.getString("deadline"));
				
				hmInner.put(rs.getString("pro_id")+"_INVOICE_CURR_ID", rs.getString("curr_id"));
	        	
				hmPartnerwiseProData.put(rs.getString("project_owner"), hmInner);
				hmPartnerwiseProIds.put(rs.getString("project_owner"), alInner);*/
				
				if(rs.getString("project_owners")!=null){
					List<String> tempList = Arrays.asList(rs.getString("project_owners").split(","));
					
					if(strProIds == null){
		        		strProIds = new StringBuilder();
		        		strProIds.append(rs.getString("pro_id"));
		        	} else {
		        		strProIds.append(","+rs.getString("pro_id"));
		        	}
		        	alProIdList.add(rs.getString("pro_id"));
					
		        	for(int i=1; i<tempList.size(); i++){
						
		        		Map<String, String> hmInner = hmPartnerwiseProData.get(tempList.get(i));
//			        	Map<String, String> hmInner = hmPartnerwiseProData.get(tempList.get(i)+"_"+rs.getString("pro_id"));
			        	if(hmInner == null) hmInner = new HashMap<String, String>();
			        	
			        	List<String> alInner = hmPartnerwiseProIds.get(tempList.get(i));
//			        	List<String> alInner = hmPartnerwiseProIds.get(tempList.get(i)+"_"+rs.getString("pro_id"));
						if(alInner==null) alInner = new ArrayList<String>();
						
						alInner.add(rs.getString("pro_id"));

						hmInner.put(rs.getString("pro_id")+"_START_DATE", rs.getString("start_date"));
						hmInner.put(rs.getString("pro_id")+"_DEADLINE", rs.getString("deadline"));
						
						hmInner.put(rs.getString("pro_id")+"_INVOICE_CURR_ID", rs.getString("curr_id"));
			        	
//						hmPartnerwiseProData.put(tempList.get(i)+"_"+rs.getString("pro_id"), hmInner);
//						hmPartnerwiseProIds.put(tempList.get(i)+"_"+rs.getString("pro_id"), alInner);
						hmPartnerwiseProData.put(tempList.get(i), hmInner);
						hmPartnerwiseProIds.put(tempList.get(i), alInner);
						
					}
				}
	        }
	        rs.close();
			pst.close();
			
			Map<String, String> hmChargeAbleAmt = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_reimbursement where pro_id in ("+strProIds+") and ispaid = true");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and to_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("DCWR/265--pst="+pst);
			while(rs.next()){
				
				if(uF.parseToBoolean(rs.getString("is_billable"))){
//					
					hmChargeAbleAmt.put(rs.getString("pro_id")+"_CHARGEBLE_DATE", rs.getString("entry_date"));
					
					String entryDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, "MM/yyyy");
					double strAmt = uF.parseToDouble(rs.getString("reimbursement_amount"));
					double chargebleAmt = uF.parseToDouble(hmChargeAbleAmt.get(rs.getString("pro_id")+"_"+entryDate+"_CHARGEBLE"))+strAmt;
					hmChargeAbleAmt.put(rs.getString("pro_id")+"_"+entryDate+"_CHARGEBLE", chargebleAmt+"");
					
				} else {
					hmChargeAbleAmt.put(rs.getString("pro_id")+"_NON_CHARGEBLE_DATE", rs.getString("entry_date"));
					
					String entryDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, "MM/yyyy");
					double strAmt = uF.parseToDouble(rs.getString("reimbursement_amount"));
					double nonChargebleAmt = uF.parseToDouble(hmChargeAbleAmt.get(rs.getString("pro_id")+"_"+entryDate+"_NON_CHARGEBLE_DATE"))+strAmt;
					hmChargeAbleAmt.put(rs.getString("pro_id")+"_"+entryDate+"_NON_CHARGEBLE", nonChargebleAmt+"");
				}
			}
			rs.close();
			pst.close();
			
			
		//===start parvez date: 25-03-2022===	
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			Map<String, String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, getStrStartDate(), getStrEndDate(), CF, uF, hmWeekEndHalfDates, null);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con, uF, CF, getStrStartDate(), getStrEndDate(), alEmpCheckRosterWeektype, hmRosterWeekEndDates, hmWeekEnds, hmEmpLevelMap, hmEmpWlocation, hmWeekEndHalfDates);
			
			Map<String,String> hmWorkDays = new HashMap<String, String>();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select emp_per_id,wlocation_id from employee_personal_details epd, employee_official_details eod WHERE epd.is_alive=true and epd.emp_per_id=eod.emp_id and approved_flag=true ");
			if(strUserType != null && strUserType.equals(MANAGER)) {
				sbQuery.append(" and (supervisor_emp_id = "+uF.parseToInt((String)session.getAttribute(EMPID))+" or eod.emp_id = "+uF.parseToInt((String)session.getAttribute(EMPID))+")");
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
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			if(getF_service()!=null && getF_service().length>0) {
                sbQuery.append(" and (");
                for(int i=0; i<getF_service().length; i++) {
                    sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
                    
                    if(i<getF_service().length-1){
                        sbQuery.append(" OR "); 
                    }
                }
                sbQuery.append(" ) ");
            }
			if(getF_level()!=null && getF_level().length>0){
                sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
            }
			sbQuery.append(" order by emp_fname ");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()) {
				String strWLocationId = rs.getString("wlocation_id");
				Set<String> weeklyOffEndDate = hmWeekEnds.get(strWLocationId);
				if (weeklyOffEndDate == null) weeklyOffEndDate = new HashSet<String>();

				Set<String> rosterWeeklyOffSet = hmRosterWeekEndDates.get(rs.getString("emp_per_id"));
				if (rosterWeeklyOffSet == null) rosterWeeklyOffSet = new HashSet<String>();

				Map<String, String> hmHolidaysCnt = new HashMap<String, String>();
				Map<String, String> hmHolidayDates = new HashMap<String, String>();
				
				if (alEmpCheckRosterWeektype != null && alEmpCheckRosterWeektype.contains(rs.getString("emp_per_id"))) {
					CF.getHolidayListCount(con, request, getStrStartDate(), getStrEndDate(), CF, hmHolidayDates, hmHolidaysCnt, rosterWeeklyOffSet, true);
				} else {
					CF.getHolidayListCount(con, request, getStrStartDate(), getStrEndDate(), CF, hmHolidayDates, hmHolidaysCnt, weeklyOffEndDate, true);
				}
				
				String diffInDays = uF.dateDifference(getStrStartDate(), DATE_FORMAT, getStrEndDate(), DATE_FORMAT, CF.getStrTimeZone());

				int nWeekEnd = (alEmpCheckRosterWeektype != null && alEmpCheckRosterWeektype.contains(rs.getString("emp_per_id"))) ? rosterWeeklyOffSet.size() : weeklyOffEndDate.size();
				int nHolidayCnt = uF.parseToInt(hmHolidaysCnt.get(strWLocationId));
				double nWorkDay = (uF.parseToDouble(diffInDays) - nWeekEnd) - nHolidayCnt;
				
				double avgMonthDays = uF.parseToDouble(diffInDays)/30;
				double avgWorkDay = nWorkDay/avgMonthDays;
				
				hmWorkDays.put(rs.getString("emp_per_id"), avgWorkDay+"");
			}
			rs.close();
			pst.close();
				
			Map<String, String> hmEmpNetSalary = CF.getEmpNetSalary(uF, CF, con, uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE), "H",hmWorkDays);

		//===end parvez date: 25-03-2022===	
			Map<String,Map<String,Map<String, String>>> hmEmpCost = new HashMap<String, Map<String,Map<String,String>>>();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select ta.actual_hrs,pro_id,ta.emp_id,ta.task_date from task_activity ta, activity_info ai where ta.activity_id = ai.task_id " +
					"and pro_id in("+strProIds+") ");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and ta.task_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			sbQuery.append(" order by ta.emp_id,pro_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("DCWR/306--pst="+pst);
			
			while (rs.next()) {
				
				double empActualHrs = 0;
				Map<String, Map<String,String>> proTaskDetails = hmEmpCost.get(uF.getDateFormat(rs.getString("task_date"), DBDATE, "MM/yyyy"));
				if(proTaskDetails == null) proTaskDetails = new HashMap<String, Map<String,String>>();
				 
				Map<String, String> hmInner = proTaskDetails.get(rs.getString("pro_id"));
				if(hmInner == null) hmInner = new HashMap<String, String>();
				 
				empActualHrs = uF.parseToDouble(hmInner.get(rs.getString("emp_id")))+uF.parseToDouble(rs.getString("actual_hrs"));
				 
				hmInner.put(rs.getString("emp_id"), empActualHrs+"");
				proTaskDetails.put(rs.getString("pro_id"), hmInner);
				hmEmpCost.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, "MM/yyyy"), proTaskDetails);
				
				
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmDirectProfessionalCost = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select invoice_particulars,invoice_particulars_amount,invoice_generated_date,piad.pro_id from promntc_invoice_amt_details piad, promntc_invoice_details pid " +
					" where piad.promntc_invoice_id = pid.promntc_invoice_id and piad.pro_id in("+strProIds+")");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and invoice_generated_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();

			while (rs.next()) {
				
				String strGaneratDate = uF.getDateFormat(rs.getString("invoice_generated_date"), DBDATE, "MM/yyyy");
				double costAmt = 0;
				if(PROFESSIONAL_FEES.equalsIgnoreCase(rs.getString("invoice_particulars"))){
					costAmt = uF.parseToDouble(hmDirectProfessionalCost.get(rs.getString("pro_id")+"_"+strGaneratDate+"_PROFESSIONAL"))+uF.parseToDouble(rs.getString("invoice_particulars_amount"));
					hmDirectProfessionalCost.put(rs.getString("pro_id")+"_"+strGaneratDate+"_PROFESSIONAL", costAmt+"");
				} else if(OUT_OF_POCKET_EXPENSES.equalsIgnoreCase(rs.getString("invoice_particulars"))){
					costAmt = uF.parseToDouble(hmDirectProfessionalCost.get(rs.getString("pro_id")+"_"+strGaneratDate+"_OUT_OF_POCKET"))+uF.parseToDouble(rs.getString("invoice_particulars_amount"));
					hmDirectProfessionalCost.put(rs.getString("pro_id")+"_"+strGaneratDate+"_OUT_OF_POCKET", costAmt+"");
				}
			}
			rs.close();
			pst.close();
			
			
			/*Map<String, String> hmDirectProfessionalCost = new HashMap<String, String>();
			StringBuilder sbInvoiceIds = null;
			Map<String, String> hmInvoiceMap = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select promntc_invoice_id,invoice_generated_date,pro_ids from promntc_invoice_details where ");
			for(int i=0; i<alProIdList.size(); i++){
				sbQuery.append("pro_ids like '%,"+alProIdList.get(i)+",%'");
				if(i<alProIdList.size()-1){
					sbQuery.append(" OR ");
				}
			}
			sbQuery.append(" )");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and invoice_generated_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				if(sbInvoiceIds == null){
					sbInvoiceIds = new StringBuilder();
					sbInvoiceIds.append(rs.getString("promntc_invoice_id"));
				} else{
					sbInvoiceIds.append(","+rs.getString("promntc_invoice_id"));
				}
				
				hmInvoiceMap.put(rs.getString("promntc_invoice_id"), rs.getString("invoice_generated_date"));
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select invoice_particulars,invoice_particulars_amount,pro_id,promntc_invoice_id from promntc_invoice_amt_details where promntc_invoice_id in("+sbInvoiceIds+")");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();

			while (rs.next()) {
				
				String strGaneratDate = uF.getDateFormat(hmInvoiceMap.get(rs.getString("promntc_invoice_id")), DBDATE, "MM/yyyy");
				double costAmt = 0;
				if(PROFESSIONAL_FEES.equalsIgnoreCase(rs.getString("invoice_particulars"))){
					costAmt = uF.parseToDouble(hmDirectProfessionalCost.get(rs.getString("pro_id")+"_"+strGaneratDate+"_PROFESSIONAL"))+uF.parseToDouble(rs.getString("invoice_particulars_amount"));
					hmDirectProfessionalCost.put(rs.getString("pro_id")+"_"+strGaneratDate+"_PROFESSIONAL", costAmt+"");
				} else if(OUT_OF_POCKET_EXPENSES.equalsIgnoreCase(rs.getString("invoice_particulars"))){
					costAmt = uF.parseToDouble(hmDirectProfessionalCost.get(rs.getString("pro_id")+"_"+strGaneratDate+"_OUT_OF_POCKET"))+uF.parseToDouble(rs.getString("invoice_particulars_amount"));
					hmDirectProfessionalCost.put(rs.getString("pro_id")+"_"+strGaneratDate+"_OUT_OF_POCKET", costAmt+"");
				}
			}
			rs.close();
			pst.close();*/
			
			
			
//			System.out.println("DCWR/354--hmDirectProfessionalCost="+hmDirectProfessionalCost);
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
			    
			    Map<String, String> hmProjectData = new HashMap<String,String>();
			    Iterator<String> it1 = hmPartnerwiseProData.keySet().iterator();
			    
			    while (it1.hasNext()) {
			    	
			    	List<String> proList = new ArrayList<String>();
			    	String partnerId = it1.next();
			    	hmProjectData = hmPartnerwiseProData.get(partnerId);
			    	List<String> alInner = hmPartnerwiseProIds.get(partnerId);
//			    	String key = it1.next();
//			    	String[] keySplit = key.split("_");
//			    	String partnerId = keySplit[0];
//			    	hmProjectData = hmPartnerwiseProData.get(key);
//			    	List<String> alInner = hmPartnerwiseProIds.get(key);
			    	
			    	double partnerTot = 0;
			    	double partnersEmpCostTot = 0;
			    	double partnerNoNChargTot = 0;
			    	double partnerprofFeesTot = 0;
			    	double partnerOtherCostTot = 0;
			    	
			    	double mEmpCostTot = 0;
			    	double chTotal = 0;
			    	double nonChTotal = 0;
			    	double empCostTotal = 0;
			    	double profFeesTotal = 0;
			    	double otherDirCostTotal = 0;
			    	
			    	double mChargeTot = 0;
			    	double mNonChargeTot = 0;
			    	double mProfFeesTot = 0;
			    	double mOtherDirCostTot = 0;
			    	
			    	String currId = null;
			    	String currExcel = "";
			    	
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
					
						double chargebleAmt = 0;
					    double nonChargebleAmt = 0;
					    double empHrCost = 0;
					    double profFees = 0;
					    double otherDirCost = 0;
					    
						for(int i=0;i<alInner.size() && alInner!=null && !alInner.isEmpty();i++) {
					    	
							String proId = alInner.get(i);
							
							Map<String, String> hmCurr = hmCurrencyMap.get(hmProjectData.get(proId+"_INVOICE_CURR_ID"));
							currId = hmCurr.get("SHORT_CURR");
							currExcel = hmCurr.get("SHORT_CURR_INR");
							
					    	int proStMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "MM")); 
							int proStYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_START_DATE"), DBDATE, "yyyy"));
								
							int proEndMnth = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "MM")); 
							int proEndYr = uF.parseToInt(uF.getDateFormat(hmProjectData.get(proId+"_DEADLINE"), DBDATE, "yyyy"));		
							
							Date proStDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_START_DATE"), DBDATE);
							Date proEdDate = uF.getDateFormatUtil(hmProjectData.get(proId+"_DEADLINE"), DBDATE);
							Date mnthStDate = uF.getDateFormatUtil(strFirstDate, DATE_FORMAT);
							Date mnthEdDate = uF.getDateFormatUtil(strEndDate, DATE_FORMAT);
								
							boolean flag = false;
							if(((proStDate.before(mnthStDate) || proStDate.equals(mnthStDate)) && (proEdDate.after(mnthEdDate) || proEdDate.equals(mnthEdDate))) ) {
								flag = true;
							}
								
							if(flag || (proStMnth == uF.parseToInt(dateArr[0]) && proStYr == uF.parseToInt(dateArr[1])) || (proEndMnth == uF.parseToInt(dateArr[0]) && proEndYr == uF.parseToInt(dateArr[1]))) {
										
								chargebleAmt += uF.parseToDouble(hmChargeAbleAmt.get(proId+"_"+uF.getDateFormat(strFirstDate, DATE_FORMAT, "MM/yyyy")+"_CHARGEBLE"));
								nonChargebleAmt += uF.parseToDouble(hmChargeAbleAmt.get(proId+"_"+uF.getDateFormat(strFirstDate, DATE_FORMAT, "MM/yyyy")+"_NON_CHARGEBLE"));
								
								if(hmEmpCost.get(uF.getDateFormat(strFirstDate, DATE_FORMAT, "MM/yyyy")) != null){
									Map<String, String> hmInnerEmpCost = hmEmpCost.get(uF.getDateFormat(strFirstDate, DATE_FORMAT, "MM/yyyy")).get(proId);
									if(hmInnerEmpCost == null) hmInnerEmpCost = new HashMap<String, String>();
									
									Iterator<String> itrate = hmInnerEmpCost.keySet().iterator();
									while(itrate.hasNext()){
										String empId = itrate.next();
										empHrCost += uF.parseToDouble(hmInnerEmpCost.get(empId)) * uF.parseToDouble(hmEmpNetSalary.get(empId));
									}
								}
								
								profFees += uF.parseToDouble(hmDirectProfessionalCost.get(proId+"_"+uF.getDateFormat(strFirstDate, DATE_FORMAT, "MM/yyyy")+"_PROFESSIONAL"));
								otherDirCost += uF.parseToDouble(hmDirectProfessionalCost.get(proId+"_"+uF.getDateFormat(strFirstDate, DATE_FORMAT, "MM/yyyy")+"_OUT_OF_POCKET"));
								
							}
								
						}
						
						mChargeTot += chargebleAmt;
						mNonChargeTot += nonChargebleAmt;
						mEmpCostTot += empHrCost;
						mProfFeesTot += profFees;
						mOtherDirCostTot += otherDirCost;
						
						partnersEmpCostTot = uF.parseToDouble(hmReportData.get(month+"__EMP_COST_TOT"))+empHrCost;
						partnerTot = uF.parseToDouble(hmReportData.get(month+"_CHARGEBLE_TOT"))+chargebleAmt;
						partnerNoNChargTot = uF.parseToDouble(hmReportData.get(month+"_NON_CHARGEBLE_TOT"))+nonChargebleAmt;
						partnerprofFeesTot = uF.parseToDouble(hmReportData.get(month+"_PROFESSIONAL_TOT"))+profFees;
						partnerOtherCostTot = uF.parseToDouble(hmReportData.get(month+"_OTHER_COST_TOT"))+otherDirCost;
						
						chTotal += partnerTot;
						nonChTotal += partnerNoNChargTot;
						empCostTotal += partnersEmpCostTot;
						profFeesTotal += partnerprofFeesTot;
						otherDirCostTotal += partnerOtherCostTot;
						
						hmReportData.put(partnerId+"_"+month+"_CHARGEBLE", uF.formatIntoComma(chargebleAmt));
						hmReportData.put(partnerId+"_"+month+"_NON_CHARGEBLE", uF.formatIntoComma(nonChargebleAmt));
						hmReportData.put(partnerId+"_"+month+"_EMP_COST", uF.formatIntoComma(empHrCost));
						hmReportData.put(partnerId+"_"+month+"_PROFESSIONAL_COST", uF.formatIntoComma(profFees));
						hmReportData.put(partnerId+"_"+month+"_OTHER_DIRECT_COST", uF.formatIntoComma(otherDirCost));
						
						hmReportData.put(month+"__EMP_COST_TOT", uF.formatIntoComma(partnersEmpCostTot));
						hmReportData.put(month+"_CHARGEBLE_TOT", uF.formatIntoComma(partnerTot));
						hmReportData.put(month+"_NON_CHARGEBLE_TOT", uF.formatIntoComma(partnerNoNChargTot));
						hmReportData.put(month+"_PROFESSIONAL_TOT", uF.formatIntoComma(partnerprofFeesTot));
						hmReportData.put(month+"_OTHER_COST_TOT", uF.formatIntoComma(partnerOtherCostTot));
			    	}
					
					hmReportData.put(partnerId+"_EMP_COST_TOTAL", uF.formatIntoComma(mEmpCostTot));
					hmReportData.put(partnerId+"_CHARGEBLE_TOTAL", uF.formatIntoComma(mChargeTot));
					hmReportData.put(partnerId+"_NON_CHARGE_TOTAL", uF.formatIntoComma(mNonChargeTot));
					hmReportData.put(partnerId+"_PROFESSIONAL_FEES_TOTAL", uF.formatIntoComma(mProfFeesTot));
					hmReportData.put(partnerId+"_OTHER_COST_TOTAL", uF.formatIntoComma(mOtherDirCostTot));
					
					hmReportData.put("TOTAL_MONTH_EMP_COST", uF.formatIntoComma(empCostTotal));
					hmReportData.put("TOTAL_MONTH_CHARGEBLE", uF.formatIntoComma(chTotal));
					hmReportData.put("TOTAL_MONTH_NON_CHARGE", uF.formatIntoComma(nonChTotal));
					hmReportData.put("TOTAL_MONTH_PROFESSIONAL_FEES", uF.formatIntoComma(profFeesTotal));
					hmReportData.put("TOTAL_MONTH_OTHER_COST", uF.formatIntoComma(otherDirCostTotal));
					
					hmReportData.put("CURRENCY", currId);
					hmReportData.put("CURRENCY_EXCEL", currExcel);
			    	hmPartner.put(partnerId, hmEmpName.get(partnerId));
//			    	
			    }
			    
			}
			request.setAttribute("monthYearsList", monthYearsList);
			request.setAttribute("hmPartner", hmPartner);
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
	
	private void generateDirectCostWorkingReport(UtilityFunctions uF) {
		
		try {
			
//			List alPartnerList = (List) request.getAttribute("alPartnerList");
			List monthYearsList = (List) request.getAttribute("monthYearsList");
			
			Map<String, String> hmPartner = (Map<String, String>) request.getAttribute("hmPartner");
			if(hmPartner == null) hmPartner = new HashMap<String, String>();
			
			Map<String, String> hmReportData = (Map<String, String>) request.getAttribute("hmReportData");
			if(hmReportData == null) hmReportData = new HashMap<String, String>();
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Direct Cost Working Report");
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			List<DataStyle> subHeader=new ArrayList<DataStyle>();
			List<DataStyle> rowHeader=new ArrayList<DataStyle>();
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			
			header.add(new DataStyle("Total KPCA",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			/*for(int i=0; i<alPartnerList.size(); i++){
				header.add(new DataStyle(alPartnerList.get(i)+"",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			}*/
			
			List<String> alPartnerId = new ArrayList<String>();
			Iterator<String> it = hmPartner.keySet().iterator();
			while (it.hasNext()) {
				String partnerId = it.next();
				alPartnerId.add(partnerId);
				header.add(new DataStyle(hmPartner.get(partnerId)+"",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			}
			
			subHeader.add(new DataStyle("Employee Cost"+" ("+uF.showData(hmReportData.get("CURRENCY_EXCEL"),"")+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			subHeader.add(new DataStyle("Prof Fees"+" ("+uF.showData(hmReportData.get("CURRENCY_EXCEL"),"")+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			subHeader.add(new DataStyle("Travelling chargeble"+" ("+uF.showData(hmReportData.get("CURRENCY_EXCEL"),"")+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			subHeader.add(new DataStyle("Travelling Non Charg"+" ("+uF.showData(hmReportData.get("CURRENCY_EXCEL"),"")+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			subHeader.add(new DataStyle("Other Direct cost"+" ("+uF.showData(hmReportData.get("CURRENCY_EXCEL"),"")+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			
			/*for(int j=0; j<monthYearsList.size(); j++){
				rowHeader.add(new DataStyle(uF.getDateFormat(monthYearsList.get(j)+"", "MM/yyyy", "MMMM")+"",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			}*/
			
			for(int j=0; j<monthYearsList.size(); j++){
				
				List<DataStyle> innerList=new ArrayList<DataStyle>();
				innerList.add(new DataStyle(uF.getDateFormat(monthYearsList.get(j)+"", "MM/yyyy", "MMMM")+"",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				innerList.add(new DataStyle(hmReportData.get(monthYearsList.get(j)+"__EMP_COST_TOT")+"",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				innerList.add(new DataStyle(hmReportData.get(monthYearsList.get(j)+"_PROFESSIONAL_TOT")+"",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				innerList.add(new DataStyle(hmReportData.get(monthYearsList.get(j)+"_CHARGEBLE_TOT")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				innerList.add(new DataStyle(hmReportData.get(monthYearsList.get(j)+"_NON_CHARGEBLE_TOT")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				innerList.add(new DataStyle(hmReportData.get(monthYearsList.get(j)+"_OTHER_COST_TOT")+"",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				for(int k=0; k<alPartnerId.size(); k++){
					innerList.add(new DataStyle(hmReportData.get(alPartnerId.get(k)+"_"+monthYearsList.get(j)+"_EMP_COST")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					innerList.add(new DataStyle(hmReportData.get(alPartnerId.get(k)+"_"+monthYearsList.get(j)+"_PROFESSIONAL_COST")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					innerList.add(new DataStyle(hmReportData.get(alPartnerId.get(k)+"_"+monthYearsList.get(j)+"_CHARGEBLE")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					innerList.add(new DataStyle(hmReportData.get(alPartnerId.get(k)+"_"+monthYearsList.get(j)+"_NON_CHARGEBLE")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					innerList.add(new DataStyle(hmReportData.get(alPartnerId.get(k)+"_"+monthYearsList.get(j)+"_OTHER_DIRECT_COST")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}
				reportData.add(innerList);
			}
			
			List<DataStyle> totInner=new ArrayList<DataStyle>();
			
			totInner.add(new DataStyle(hmReportData.get("TOTAL_MONTH_EMP_COST")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			totInner.add(new DataStyle(hmReportData.get("TOTAL_MONTH_PROFESSIONAL_FEES")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			totInner.add(new DataStyle(hmReportData.get("TOTAL_MONTH_CHARGEBLE")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			totInner.add(new DataStyle(hmReportData.get("TOTAL_MONTH_NON_CHARGE")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			totInner.add(new DataStyle(hmReportData.get("TOTAL_MONTH_OTHER_COST")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			for(int l=0; l<alPartnerId.size(); l++){
				totInner.add(new DataStyle(hmReportData.get(alPartnerId.get(l)+"_EMP_COST_TOTAL")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				totInner.add(new DataStyle(hmReportData.get(alPartnerId.get(l)+"_PROFESSIONAL_FEES_TOTAL")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				totInner.add(new DataStyle(hmReportData.get(alPartnerId.get(l)+"_CHARGEBLE_TOTAL")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				totInner.add(new DataStyle(hmReportData.get(alPartnerId.get(l)+"_NON_CHARGE_TOTAL")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				totInner.add(new DataStyle(hmReportData.get(alPartnerId.get(l)+"_OTHER_COST_TOTAL")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			}
			reportData.add(totInner);
			
			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			sheetDesign.generateExcelSheetforDirectcostWorkingReport(workbook,sheet,header,subHeader,reportData);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=DirectCostWorkingReport.xls");
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

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
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

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
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
	
	
}
