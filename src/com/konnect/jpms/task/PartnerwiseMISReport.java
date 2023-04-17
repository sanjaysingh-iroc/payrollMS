package com.konnect.jpms.task;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

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

public class PartnerwiseMISReport extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

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
	List<FillYears> yearList;
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
		
		request.setAttribute(PAGE, "/jsp/task/PartnerwiseMISReport.jsp");
		request.setAttribute(TITLE, "Partnerwise MIS Report");
		
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
		
		viewPartnerwiseMISReport(uF);
		
		if(getExportType()!= null && getExportType().equalsIgnoreCase("excel")){
			generatePartnerwiseMISReport(uF);
		}
		
		return loadPartnerwiseReport(uF);

	}
	
	public String loadPartnerwiseReport(UtilityFunctions uF) {
		
		Map<String, String> hmOrg = CF.getOrgDetails(uF, getF_org(),request);
		if(hmOrg == null) hmOrg = new HashMap<String, String>();
		
//		monthList = new FillMonth().fillMonth();
//		
//		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		
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
	
	public String viewPartnerwiseMISReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			
			/*String[] strFinancialYearDates = null;
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
			}*/
			
			if(getStrStartDate()!=null && getStrStartDate().equalsIgnoreCase("NULL")) {
				setStrStartDate(null);
				setStrEndDate(null);
			}		
			if(getStrEndDate()!=null && getStrEndDate().equalsIgnoreCase("NULL")) {
				setStrStartDate(null);
				setStrEndDate(null);
			}
			if(getStrStartDate()==null && getStrEndDate()==null) {
				Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(CF.getStrTimeZone()));
				int nMaxDate = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				int nMinDate = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
				
				setStrStartDate(uF.getDateFormat(nMinDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
				setStrEndDate(uF.getDateFormat(nMaxDate+"/"+(cal.get(Calendar.MONTH)+ 1)+"/"+cal.get(Calendar.YEAR), DATE_FORMAT, DATE_FORMAT));
			}
			
			con = db.makeConnection(con);
			
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			Map<String,String> hmReportData = new HashMap<String, String>();
			Map<String,String> hmPartner = new HashMap<String, String>();
			Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
			Map<String, List<String>> hmPartnerwiseProIds = new HashMap<String, List<String>>();
			StringBuilder strProIds = null;
			List<String> alProIdList = new ArrayList<String>();
			Map<String,String> hmProData = new HashMap<String, String>();
			
			StringBuilder sbQuery = new StringBuilder();
		//start parvez date: 18-10-2022===	
//			sbQuery.append("select * from projectmntnc where project_owner > 0 and approve_status != 'blocked' ");
			sbQuery.append("select * from projectmntnc where approve_status != 'blocked' ");
		//===end parvez date: 18-10-2022===	
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
//			sbQuery.append(" order by project_owner ");
	        pst = con.prepareStatement(sbQuery.toString());
	        rs = pst.executeQuery();
	        while(rs.next()) {
	        	
	        	if(rs.getString("project_owners")==null || (rs.getString("project_owner")!=null && (rs.getString("project_owner").equals("") || rs.getString("project_owner").contains(",0,")))){
	        		continue;
	        	}
	        	
	        	if(strProIds == null){
	        		strProIds = new StringBuilder();
	        		strProIds.append(rs.getString("pro_id"));
	        	} else {
	        		strProIds.append(","+rs.getString("pro_id"));
	        	}
	        	
	        	alProIdList.add(rs.getString("pro_id"));
	        	
	        	/*List<String> alInner = hmPartnerwiseProIds.get(rs.getString("project_owner"));
				if(alInner==null) alInner = new ArrayList<String>();
				alInner.add(rs.getString("pro_id"));
				
				hmPartnerwiseProIds.put(rs.getString("project_owner"), alInner);
				
				hmProData.put(rs.getString("project_owner")+"_CURR_ID", rs.getString("curr_id"));*/
	        	if(rs.getString("project_owners")!=null){
	        		List<String> tempList = Arrays.asList(rs.getString("project_owners").split(","));
	        		for(int j=1; j<tempList.size();j++){
	        			List<String> alInner = hmPartnerwiseProIds.get(tempList.get(j));
	    				if(alInner==null) alInner = new ArrayList<String>();
	    				alInner.add(rs.getString("pro_id"));
	    				
	    				hmPartnerwiseProIds.put(tempList.get(j), alInner);
	    				
	    				hmProData.put(tempList.get(j)+"_CURR_ID", rs.getString("curr_id"));
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
			while(rs.next()){
				if(uF.parseToBoolean(rs.getString("is_billable"))){
					
					double strAmt = uF.parseToDouble(rs.getString("reimbursement_amount"));
					double chargebleAmt = uF.parseToDouble(hmChargeAbleAmt.get(rs.getString("pro_id")+"_CHARGEBLE"))+strAmt;
					hmChargeAbleAmt.put(rs.getString("pro_id")+"_CHARGEBLE", chargebleAmt+"");
				} else {
					
					double strAmt = uF.parseToDouble(rs.getString("reimbursement_amount"));
					double nonChargebleAmt = uF.parseToDouble(hmChargeAbleAmt.get(rs.getString("pro_id")+"_NON_CHARGEBLE_DATE"))+strAmt;
					hmChargeAbleAmt.put(rs.getString("pro_id")+"_NON_CHARGEBLE", nonChargebleAmt+"");
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
			
			Map<String, Map<String,String>> proTaskDetails = new HashMap<String, Map<String,String>>();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select ta.actual_hrs,pro_id,ta.emp_id,ta.task_date from task_activity ta, activity_info ai where ta.activity_id = ai.task_id " +
					"and pro_id in("+strProIds+") ");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and ta.task_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			sbQuery.append(" order by ta.emp_id,pro_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				double empActualHrs = 0;
				 
				Map<String, String> hmInner = proTaskDetails.get(rs.getString("pro_id"));
				if(hmInner == null) hmInner = new HashMap<String, String>();
				 
				empActualHrs = uF.parseToDouble(hmInner.get(rs.getString("emp_id")))+uF.parseToDouble(rs.getString("actual_hrs"));
				 
				hmInner.put(rs.getString("emp_id"), empActualHrs+"");
				proTaskDetails.put(rs.getString("pro_id"), hmInner);
			}
			rs.close();
			pst.close();
			
			/*Map<String, String> hmDirectProfessionalCost = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			
			sbQuery.append("select bill_particulars,bill_particulars_amount,entry_date,pro_id from promntc_bill_amt_details pbad, promntc_bill_parti_amt_details pbpad " +
					" where pbad.bill_id = pbpad.promntc_invoice_bill_id and pro_id in("+strProIds+")");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and entry_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strEntryDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, "MM/yyyy");
				double costAmt = 0;
				if(PROFESSIONAL_FEES.equalsIgnoreCase(rs.getString("bill_particulars"))){
					costAmt = uF.parseToDouble(hmDirectProfessionalCost.get(rs.getString("pro_id")+"_PROFESSIONAL"))+uF.parseToDouble(rs.getString("bill_particulars_amount"));
					hmDirectProfessionalCost.put(rs.getString("pro_id")+"_PROFESSIONAL", costAmt+"");
				} else if(OUT_OF_POCKET_EXPENSES.equalsIgnoreCase(rs.getString("bill_particulars"))){
					costAmt = uF.parseToDouble(hmDirectProfessionalCost.get(rs.getString("pro_id")+"_OUT_OF_POCKET"))+uF.parseToDouble(rs.getString("bill_particulars_amount"));
					hmDirectProfessionalCost.put(rs.getString("pro_id")+"_OUT_OF_POCKET", costAmt+"");
				}
			}
			rs.close();
			pst.close();*/
			
			Map<String, String> hmDirectProfessionalCost = new HashMap<String, String>();
			
			sbQuery = new StringBuilder();
			
			sbQuery.append("select promntc_invoice_id,pro_ids from promntc_invoice_details where promntc_invoice_id>0 ");
			if(alProIdList!=null && !alProIdList.isEmpty()){
				sbQuery.append(" and ( ");
				for(int i=0; i<alProIdList.size(); i++){
					sbQuery.append("pro_ids like '%,"+alProIdList.get(i)+",%'");
					if(i<alProIdList.size()-1){
						sbQuery.append(" OR ");
					}
				}
				sbQuery.append(" )");
			}
			
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and invoice_generated_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
//			System.out.println("sbQuery="+sbQuery);
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			StringBuilder sbInvoiceId = null;
			Map<String,List<String>> hmInvoiceProIds = new HashMap<String, List<String>>();
			while (rs.next()) {
				if(rs.getString("pro_ids") != null){
					String[] tempProId = rs.getString("pro_ids").split(",");
					List<String> alInner = new ArrayList<String>();
					
					if(sbInvoiceId == null){
						sbInvoiceId = new StringBuilder();
						sbInvoiceId.append(rs.getString("promntc_invoice_id"));
					} else{
						sbInvoiceId.append(","+rs.getString("promntc_invoice_id"));
					}
					
					for(int i=0; i<tempProId.length; i++){
						if(i>0){
							alInner.add(tempProId[i]);
						}
					}
					hmInvoiceProIds.put(rs.getString("promntc_invoice_id"), alInner);
				}
				
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select bill_particulars,bill_particulars_amount,entry_date,pbpad.invoice_id from promntc_bill_amt_details pbad, promntc_bill_parti_amt_details pbpad " +
					" where pbad.bill_id = pbpad.promntc_invoice_bill_id and pbpad.invoice_id in("+sbInvoiceId+")");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and entry_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strEntryDate = uF.getDateFormat(rs.getString("entry_date"), DBDATE, "MM/yyyy");
				List<String> innerList = hmInvoiceProIds.get(rs.getString("invoice_id"));
				
				for(int k=0; k<innerList.size(); k++){
					double costAmt = 0;
					if(PROFESSIONAL_FEES.equalsIgnoreCase(rs.getString("bill_particulars"))){
						costAmt = uF.parseToDouble(hmDirectProfessionalCost.get(innerList.get(k)+"_PROFESSIONAL"))+uF.parseToDouble(rs.getString("bill_particulars_amount"));
						hmDirectProfessionalCost.put(innerList.get(k)+"_PROFESSIONAL", costAmt+"");
					} else if(OUT_OF_POCKET_EXPENSES.equalsIgnoreCase(rs.getString("bill_particulars"))){
						costAmt = uF.parseToDouble(hmDirectProfessionalCost.get(innerList.get(k)+"_OUT_OF_POCKET"))+uF.parseToDouble(rs.getString("bill_particulars_amount"));
						hmDirectProfessionalCost.put(innerList.get(k)+"_OUT_OF_POCKET", costAmt+"");
					}
				}
				
				
			}
			rs.close();
			pst.close();
			
			double totChargebleAmt = 0;
	    	double totNonChargebleAmt = 0;
	    	double totEmpHrCost = 0;
	    	double totProfFees = 0;
		    double totOtherDirCost = 0;
		    String CurrencyId = null;
		    String CurrExcel = "";
		    
			Iterator<String> it = hmPartnerwiseProIds.keySet().iterator();
			while (it.hasNext()) {
		    	String partnerId = it.next();
		    	List<String> alInner = hmPartnerwiseProIds.get(partnerId);
		    	
		    	double chargebleAmt = 0;
		    	double nonChargebleAmt = 0;
		    	double empHrCost = 0;
		    	double profFees = 0;
			    double otherDirCost = 0;
			    
			    Map<String, String> hmCurr = hmCurrencyMap.get(hmProData.get(partnerId+"_CURR_ID"));
			    CurrencyId = hmCurr.get("SHORT_CURR");
			    CurrExcel = hmCurr.get("SHORT_CURR_INR");
		    	
		    	for(int i=0;i<alInner.size() && alInner!=null && !alInner.isEmpty();i++) {
		    		String proId = alInner.get(i);
		    		
		    		chargebleAmt += uF.parseToDouble(hmChargeAbleAmt.get(proId+"_CHARGEBLE"));
					nonChargebleAmt += uF.parseToDouble(hmChargeAbleAmt.get(proId+"_NON_CHARGEBLE"));
					
					Map<String, String> hmInnerEmpCost = proTaskDetails.get(proId);
					if(hmInnerEmpCost == null) hmInnerEmpCost = new HashMap<String, String>();
					Iterator<String> itrate = hmInnerEmpCost.keySet().iterator();
					while(itrate.hasNext()){
						String empId = itrate.next();
						empHrCost += uF.parseToDouble(hmInnerEmpCost.get(empId)) * uF.parseToDouble(hmEmpNetSalary.get(empId));
					}
					
					profFees += uF.parseToDouble(hmDirectProfessionalCost.get(proId+"_PROFESSIONAL"));
					otherDirCost += uF.parseToDouble(hmDirectProfessionalCost.get(proId+"_OUT_OF_POCKET"));
					
		    	}
		    	
		    	totChargebleAmt += chargebleAmt;
		    	totNonChargebleAmt += nonChargebleAmt;
		    	totEmpHrCost += empHrCost;
		    	totProfFees += profFees;
			    totOtherDirCost += otherDirCost;
		    	
		    	hmReportData.put(partnerId+"_CHARGEBLE", uF.formatIntoComma(chargebleAmt));
		    	hmReportData.put(partnerId+"_NON_CHARGEBLE", uF.formatIntoComma(nonChargebleAmt));
		    	hmReportData.put(partnerId+"_EMP_COST", uF.formatIntoComma(empHrCost));
		    	hmReportData.put(partnerId+"_PROFESSIONAL_COST", uF.formatIntoComma(profFees));
		    	hmReportData.put(partnerId+"_OTHER_DIRECT_COST", uF.formatIntoComma(otherDirCost));
		    	
		    	hmReportData.put(partnerId+"_TOTAL_RECEIPT", uF.formatIntoComma(profFees+otherDirCost));
		    	
		    	hmReportData.put(partnerId+"_TOTAL_DIRECT_EXP", uF.formatIntoComma(chargebleAmt+nonChargebleAmt+empHrCost));
		    	
		    	hmPartner.put(partnerId, hmEmpName.get(partnerId));
			}
			
			hmReportData.put("_TOTAL_RECEIPT_AMT", uF.formatIntoComma(totProfFees+totOtherDirCost));
			hmReportData.put("_TOTAL_DIRECT_EXP", uF.formatIntoComma(totChargebleAmt+totNonChargebleAmt+totEmpHrCost));
			
			hmReportData.put("_TOTAL_CHARGEBLE", uF.formatIntoComma(totChargebleAmt));
	    	hmReportData.put("_TOTAL_NON_CHARGEBLE", uF.formatIntoComma(totNonChargebleAmt));
	    	hmReportData.put("_TOTAL_EMP_COST", uF.formatIntoComma(totEmpHrCost));
	    	hmReportData.put("_TOTAL_PROFESSIONAL_COST", uF.formatIntoComma(totProfFees));
	    	hmReportData.put("_TOTAL_OTHER_DIRECT_COST", uF.formatIntoComma(totOtherDirCost));
			hmReportData.put("CURRENCY_EXCEL",CurrExcel);
			hmReportData.put("CURRENCY",CurrencyId);
			
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
	
	private void generatePartnerwiseMISReport(UtilityFunctions uF) {
		
		try {
			
			Map<String, String> hmPartner = (Map<String, String>) request.getAttribute("hmPartner");
			if(hmPartner == null) hmPartner = new HashMap<String, String>();
			
			Map<String, String> hmReportData = (Map<String, String>) request.getAttribute("hmReportData");
			if(hmReportData == null) hmReportData = new HashMap<String, String>();
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Partner wise MIS Report");
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			
//			for(int i=0; i<alPartnerList.size(); i++){
//				header.add(new DataStyle(alPartnerList.get(i)+"",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
//			}
			
			List<DataStyle> listOPE = new ArrayList<DataStyle>();
			List<DataStyle> listPE = new ArrayList<DataStyle>();
			List<DataStyle> listEmpCost = new ArrayList<DataStyle>();
			List<DataStyle> listTC = new ArrayList<DataStyle>();
			List<DataStyle> listTNC = new ArrayList<DataStyle>();
			List<DataStyle> listTotRecp = new ArrayList<DataStyle>();
			List<DataStyle> listTotDE = new ArrayList<DataStyle>();
			
			
			listOPE.add(new DataStyle("b) Out Of Pocket",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			listOPE.add(new DataStyle(hmReportData.get("_TOTAL_OTHER_DIRECT_COST")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			
			listPE.add(new DataStyle("a) Professional Fees",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			listPE.add(new DataStyle(hmReportData.get("_TOTAL_PROFESSIONAL_COST")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			
			listEmpCost.add(new DataStyle("a) Employees Cost",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			listEmpCost.add(new DataStyle(hmReportData.get("_TOTAL_EMP_COST")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			
			listTC.add(new DataStyle("c) Travelling C.",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			listTC.add(new DataStyle(hmReportData.get("_TOTAL_CHARGEBLE")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			
			listTNC.add(new DataStyle("d) Travelling N.C.",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			listTNC.add(new DataStyle(hmReportData.get("_TOTAL_NON_CHARGEBLE")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			
			listTotRecp.add(new DataStyle("Total Receipts",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			listTotRecp.add(new DataStyle(hmReportData.get("_TOTAL_RECEIPT_AMT")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			
			listTotDE.add(new DataStyle("Total Direct Expenses",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			listTotDE.add(new DataStyle(hmReportData.get("_TOTAL_DIRECT_EXP")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			
			header.add(new DataStyle("Total KPCA"+" ("+uF.showData(hmReportData.get("CURRENCY_EXCEL"),"")+")",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			
			List<String> alPartnerId = new ArrayList<String>();
			Iterator<String> it = hmPartner.keySet().iterator();
			while (it.hasNext()) {
				String partnerId = it.next();
				alPartnerId.add(partnerId);
				
				listOPE.add(new DataStyle(hmReportData.get(partnerId+"_OTHER_DIRECT_COST")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listPE.add(new DataStyle(hmReportData.get(partnerId+"_PROFESSIONAL_COST")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listEmpCost.add(new DataStyle(hmReportData.get(partnerId+"_EMP_COST")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listTC.add(new DataStyle(hmReportData.get(partnerId+"_CHARGEBLE")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listTNC.add(new DataStyle(hmReportData.get(partnerId+"_NON_CHARGEBLE")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				
				listTotRecp.add(new DataStyle(hmReportData.get(partnerId+"_TOTAL_RECEIPT")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listTotDE.add(new DataStyle(hmReportData.get(partnerId+"_TOTAL_DIRECT_EXP")+"",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				
				header.add(new DataStyle(hmPartner.get(partnerId)+" ("+uF.showData(hmReportData.get("CURRENCY_EXCEL"),"")+")",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			}
			
			reportData.add(listEmpCost);		//0
			reportData.add(listPE);				//1
			reportData.add(listTC);				//2
			reportData.add(listTNC);			//3
			reportData.add(listOPE);			//4
			
			reportData.add(listTotRecp);			//5
			reportData.add(listTotDE);			//6
			
			List<DataStyle> rowHeader=new ArrayList<DataStyle>();
//			rowHeader.add(new DataStyle("a) Professional Fees",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("b) Inter Firm Billing",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("b) Out Of Pocket",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			
//			rowHeader.add(new DataStyle("a) Employees Cost",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("b) Professional Fees",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("c) Travelling C.",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("d) Travelling N.C.",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("e) Other Direct cost",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("f) Inter Firm Billing",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			sheetDesign.generateExcelSheetforPartnerwiseMISReport(workbook,sheet,header,rowHeader,reportData);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=PartnerwiseMISReport.xls");
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
		
		/*alFilter.add("FINANCIALYEAR");
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
		*/
		
		if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))){
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
		}
		
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