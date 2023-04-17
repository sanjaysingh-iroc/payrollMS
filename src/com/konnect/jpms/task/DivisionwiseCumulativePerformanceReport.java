package com.konnect.jpms.task;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
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

public class DivisionwiseCumulativePerformanceReport extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

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
		
		request.setAttribute(PAGE, "/jsp/task/DivisionwiseCumulativePerformanceReport.jsp");
		request.setAttribute(TITLE, "Divisionwise Cumulative Performance Report");
		
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
		
		viewDivisionwiseCumulativePerformanceReport(uF);
		
		if(getExportType()!= null && getExportType().equalsIgnoreCase("excel")){
			generateCumulativePerformanceExcelReport(uF);
		}
		
		return loadReport(uF);

	}
	
	public String viewDivisionwiseCumulativePerformanceReport(UtilityFunctions uF) {
		
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
			
			
			String orgName = CF.getOrgNameById(con, getF_org()).toUpperCase();
			String strFinancialYear = uF.getDateFormat(getStrStartDate(), DATE_FORMAT, "yyyy")+"-"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT, "yy");
			
			request.setAttribute("orgName", orgName);
			request.setAttribute("strFinancialYear", strFinancialYear);
			
			Map<String, Map<String, String>> hmCurrencyMap = CF.getCurrencyDetails(con);
			Map<String, String> hmSbuName = CF.getServicesMap(con, false);
			Map<String, List<String>> hmSbuwiseProIds = new HashMap<String, List<String>>();
			Map<String,String> hmReportData = new HashMap<String, String>();
			Map<String,String> hmSbuListMap = new HashMap<String, String>();
			Map<String, String> hmProOwner = new HashMap<String, String>();
			
			StringBuilder strProIds = null;
			StringBuilder strProOwnerIds = null;
			
			StringBuilder sbQuery = new StringBuilder();
		//===start parvez date: 13-10-2022===	
//			sbQuery.append("select pro_id, sbu_id, curr_id, project_owner from projectmntnc where sbu_id>0 and approve_status != 'blocked' ");
			sbQuery.append("select pro_id, sbu_id, curr_id, project_owners from projectmntnc where sbu_id>0 and approve_status != 'blocked' ");
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
			
			if(getStrStartDate() != null && !getStrStartDate().equalsIgnoreCase("null") && !getStrStartDate().equals("") && getStrEndDate() != null && !getStrEndDate().equalsIgnoreCase("null") && !getStrEndDate().equals("")) {
				sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (start_date <= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "') ");
				sbQuery.append(" or (deadline >= '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "')) ");
			}
			sbQuery.append(" order by sbu_id ");
	        pst = con.prepareStatement(sbQuery.toString());
	        rs = pst.executeQuery();
//	        System.out.println("DCPR/191--pst="+pst);
	        while(rs.next()) {
	        	
	        	if(strProIds == null){
	        		strProIds = new StringBuilder();
	        		strProIds.append(rs.getString("pro_id"));
	        	} else {
	        		strProIds.append(","+rs.getString("pro_id"));
	        	}
	        	
	        	/*if(strProOwnerIds == null){
	        		strProOwnerIds = new StringBuilder();
	        		strProOwnerIds.append(rs.getString("project_owner"));
	        	} else {
	        		strProOwnerIds.append(","+rs.getString("project_owner"));
	        	}*/
	        	
	        	/*if(!alSbuList.contains(hmSbuName.get(rs.getString("sbu_id")))){
	        		alSbuList.add(hmSbuName.get(rs.getString("sbu_id")));
	        	}*/
	        	
	        	List<String> alInner = hmSbuwiseProIds.get(rs.getString("sbu_id"));
				if(alInner==null) alInner = new ArrayList<String>();
				alInner.add(rs.getString("pro_id"));
				
				hmSbuwiseProIds.put(rs.getString("sbu_id"), alInner);
//				
//				hmProOwner.put(rs.getString("pro_id"), rs.getString("project_owner"));
//				hmProOwner.put(rs.getString("sbu_id")+"_CURR_ID", rs.getString("curr_id"));
				
		//===start parvez date: 13-10-2022===		
				if(rs.getString("project_owners")!=null){
					List<String> tempList = Arrays.asList(rs.getString("project_owners").split(","));
					for(int j=1; j<tempList.size();j++){
						if(strProOwnerIds == null){
			        		strProOwnerIds = new StringBuilder();
			        		strProOwnerIds.append(tempList.get(j));
			        	} else {
			        		strProOwnerIds.append(","+tempList.get(j));
			        	}
						
						hmProOwner.put(rs.getString("pro_id"), tempList.get(j));
						hmProOwner.put(rs.getString("sbu_id")+"_CURR_ID", rs.getString("curr_id"));
					}
				}
		//===end parvez date: 13-10-2022===		
	        }
	        rs.close();
			pst.close();
			
			Map<String, String> hmChargeAbleAmt = new HashMap<String, String>();
			sbQuery = new StringBuilder();
//			sbQuery.append("select * from emp_reimbursement where pro_id in ("+strProIds+") and ispaid = true");
			sbQuery.append("select is_billable,sum(reimbursement_amount) as reimbursement_amount,pro_id from emp_reimbursement where pro_id in ("+strProIds+") and ispaid = true");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and to_date between '" + uF.getDateFormat(getStrStartDate(), DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery.append("group by pro_id,is_billable");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("DCPR/224--pst="+pst);
			while(rs.next()){
				if(uF.parseToBoolean(rs.getString("is_billable"))){
					
//					double strAmt = uF.parseToDouble(rs.getString("reimbursement_amount"));
//					double chargebleAmt = uF.parseToDouble(hmChargeAbleAmt.get(rs.getString("pro_id")+"_CHARGEBLE"))+strAmt;
//					hmChargeAbleAmt.put(rs.getString("pro_id")+"_CHARGEBLE", chargebleAmt+"");
					hmChargeAbleAmt.put(rs.getString("pro_id")+"_CHARGEBLE", rs.getString("reimbursement_amount"));
					
				} else {
					
//					double strAmt = uF.parseToDouble(rs.getString("reimbursement_amount"));
//					double nonChargebleAmt = uF.parseToDouble(hmChargeAbleAmt.get(rs.getString("pro_id")+"_NON_CHARGEBLE_DATE"))+strAmt;
//					hmChargeAbleAmt.put(rs.getString("pro_id")+"_NON_CHARGEBLE", nonChargebleAmt+"");
					hmChargeAbleAmt.put(rs.getString("pro_id")+"_NON_CHARGEBLE", rs.getString("reimbursement_amount"));
					
				}
			}
			rs.close();
			pst.close();
//			System.out.println("DCPR/240--hmChargeAbleAmt="+hmChargeAbleAmt);
			
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
			
			Map<String, Map<String,String>> proTaskDetails = new HashMap<String, Map<String,String>>();

			
		//===end parvez date: 25-03-2022===
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(ta.actual_hrs) as actual_hrs, pro_id,ta.emp_id from task_activity ta, activity_info ai where ta.activity_id = ai.task_id " +
					"and pro_id in("+strProIds+") ");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and ta.task_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			sbQuery.append(" group by ta.emp_id,pro_id order by ta.emp_id,pro_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
//			System.out.println("DCPR/255--pst="+pst);
			while (rs.next()) {
				double empActualHrs = 0;
				 
				Map<String, String> hmInner = proTaskDetails.get(rs.getString("pro_id"));
				if(hmInner == null) hmInner = new HashMap<String, String>();
				 
//				empActualHrs = uF.parseToDouble(hmInner.get(rs.getString("emp_id")))+uF.parseToDouble(rs.getString("actual_hrs"));
				 
				hmInner.put(rs.getString("emp_id"), rs.getString("actual_hrs"));
				proTaskDetails.put(rs.getString("pro_id"), hmInner);
				
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmDirectProfessionalCost = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			
//			sbQuery.append("select bill_particulars,bill_particulars_amount,pro_id from promntc_bill_amt_details pbad, promntc_bill_parti_amt_details pbpad " +
//					" where pbad.bill_id = pbpad.promntc_invoice_bill_id and pro_id in("+strProIds+")");
			sbQuery.append("select sum(bill_particulars_amount) as bill_particulars_amount, bill_particulars, pro_id from promntc_bill_amt_details pbad, promntc_bill_parti_amt_details pbpad " +
					" where pbad.bill_id = pbpad.promntc_invoice_bill_id and pro_id in("+strProIds+")");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and entry_date between '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			sbQuery.append(" group by bill_particulars,pro_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("DCPR/276--pst="+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				double costAmt = 0;
				if(PROFESSIONAL_FEES.equalsIgnoreCase(rs.getString("bill_particulars"))){
//					costAmt = uF.parseToDouble(hmDirectProfessionalCost.get(rs.getString("pro_id")+"_PROFESSIONAL"))+uF.parseToDouble(rs.getString("bill_particulars_amount"));
//					hmDirectProfessionalCost.put(rs.getString("pro_id")+"_PROFESSIONAL", costAmt+"");
					hmDirectProfessionalCost.put(rs.getString("pro_id")+"_PROFESSIONAL", rs.getString("bill_particulars_amount"));
				} else if(OUT_OF_POCKET_EXPENSES.equalsIgnoreCase(rs.getString("bill_particulars"))){
//					costAmt = uF.parseToDouble(hmDirectProfessionalCost.get(rs.getString("pro_id")+"_OUT_OF_POCKET"))+uF.parseToDouble(rs.getString("bill_particulars_amount"));
//					hmDirectProfessionalCost.put(rs.getString("pro_id")+"_OUT_OF_POCKET", costAmt+"");
					hmDirectProfessionalCost.put(rs.getString("pro_id")+"_OUT_OF_POCKET", rs.getString("bill_particulars_amount"));
				}
			}
			rs.close();
			pst.close();
			
			
			String[] curStartDate = getStrStartDate().split("/");
			int lastYr = uF.parseToInt(curStartDate[2])-1;
			String preYrStartDate = curStartDate[0]+"/"+curStartDate[1]+"/"+lastYr;
			
			String[] curEndDate = getStrEndDate().split("/");
			int lastYr1 = uF.parseToInt(curEndDate[2])-1;
			String preYrEndDate = curEndDate[0]+"/"+curEndDate[1]+"/"+lastYr1;
			
//			System.out.println("start date="+preYrStartDate+"---end Date="+preYrEndDate);
			
			sbQuery = new StringBuilder();
			sbQuery.append("select is_billable,sum(reimbursement_amount) as reimbursement_amount,pro_id from emp_reimbursement where pro_id in ("+strProIds+") and ispaid = true");
			if(preYrStartDate != null && !preYrStartDate.equals("") && !preYrStartDate.equalsIgnoreCase("null") && preYrEndDate != null && !preYrEndDate.equals("") && !preYrEndDate.equalsIgnoreCase("null")) {
				sbQuery.append(" and to_date between '" + uF.getDateFormat(preYrStartDate, DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(preYrEndDate, DATE_FORMAT, DBDATE) + "' ");
			}
			sbQuery.append("group by pro_id,is_billable");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while(rs.next()){
				if(uF.parseToBoolean(rs.getString("is_billable"))){
					
					hmChargeAbleAmt.put(rs.getString("pro_id")+"_LASTYR_CHARGEBLE", rs.getString("reimbursement_amount"));
					
				} else {
					
					hmChargeAbleAmt.put(rs.getString("pro_id")+"_LASTYR_NON_CHARGEBLE", rs.getString("reimbursement_amount"));
					
				}
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			sbQuery.append("select sum(ta.actual_hrs) as actual_hrs, pro_id,ta.emp_id from task_activity ta, activity_info ai where ta.activity_id = ai.task_id " +
					"and pro_id in("+strProIds+") ");
			if(preYrStartDate != null && !preYrStartDate.equals("") && !preYrStartDate.equalsIgnoreCase("null") && preYrEndDate != null && !preYrEndDate.equals("") && !preYrEndDate.equalsIgnoreCase("null")) {
				sbQuery.append(" and ta.task_date between '"+uF.getDateFormat(preYrStartDate, DATE_FORMAT)+"' and '"+uF.getDateFormat(preYrEndDate, DATE_FORMAT)+"'");
			}
			sbQuery.append(" group by ta.emp_id,pro_id order by ta.emp_id,pro_id");
			
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				 
				Map<String, String> hmInner = proTaskDetails.get(rs.getString("pro_id")+"_LASTYR");
				if(hmInner == null) hmInner = new HashMap<String, String>();
				 
				hmInner.put(rs.getString("emp_id"), rs.getString("actual_hrs"));
				proTaskDetails.put(rs.getString("pro_id")+"_LASTYR", hmInner);
			}
			rs.close();
			pst.close();
			
			sbQuery = new StringBuilder();
			
			sbQuery.append("select sum(bill_particulars_amount) as bill_particulars_amount, bill_particulars, pro_id from promntc_bill_amt_details pbad, promntc_bill_parti_amt_details pbpad " +
					" where pbad.bill_id = pbpad.promntc_invoice_bill_id and pro_id in("+strProIds+") ");
			if(preYrStartDate != null && !preYrStartDate.equals("") && !preYrStartDate.equalsIgnoreCase("null") && preYrEndDate != null && !preYrEndDate.equals("") && !preYrEndDate.equalsIgnoreCase("null")) {
				sbQuery.append(" and entry_date between '"+uF.getDateFormat(preYrStartDate, DATE_FORMAT)+"' and '"+uF.getDateFormat(preYrEndDate, DATE_FORMAT)+"'");
			}
			sbQuery.append(" group by bill_particulars,pro_id");
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				
				if(PROFESSIONAL_FEES.equalsIgnoreCase(rs.getString("bill_particulars"))){
					hmDirectProfessionalCost.put(rs.getString("pro_id")+"_LASTYR_PROFESSIONAL", rs.getString("bill_particulars_amount"));
				} else if(OUT_OF_POCKET_EXPENSES.equalsIgnoreCase(rs.getString("bill_particulars"))){
					hmDirectProfessionalCost.put(rs.getString("pro_id")+"_LASTYR_OUT_OF_POCKET", rs.getString("bill_particulars_amount"));
				}
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmBudgetMap = new HashMap<String, String>();
			sbQuery = new StringBuilder();
			sbQuery.append("select * from partner_budget_details where emp_id in ("+strProOwnerIds+") ");
			if(getStrStartDate() != null && !getStrStartDate().equals("") && !getStrStartDate().equalsIgnoreCase("null") && getStrEndDate() != null && !getStrEndDate().equals("") && !getStrEndDate().equalsIgnoreCase("null")) {
				sbQuery.append(" and financial_year_start= '"+uF.getDateFormat(getStrStartDate(), DATE_FORMAT)+"' and financial_year_end= '"+uF.getDateFormat(getStrEndDate(), DATE_FORMAT)+"'");
			}
			pst = con.prepareStatement(sbQuery.toString());
			rs = pst.executeQuery();
			while (rs.next()) {
				hmBudgetMap.put(rs.getString("emp_id"), rs.getString("total_amount"));
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmSbuwiseProIds.keySet().iterator();
			while (it.hasNext()) {
				String sbuId = it.next();
		    	List<String> alInner = hmSbuwiseProIds.get(sbuId);
		    	Map<String, String> hmCurr = hmCurrencyMap.get(hmProOwner.get(sbuId+"_CURR_ID"));
		    	
		    	double chargebleAmt = 0;
		    	double nonChargebleAmt = 0;
		    	double empHrCost = 0;
		    	double profFees = 0;
			    double otherDirCost = 0;
			    double budgetAmt = 0;
			    
			    double lYchargebleAmt = 0;
		    	double lYnonChargebleAmt = 0;
		    	double lYempHrCost = 0;
		    	double lYprofFees = 0;
			    double lYotherDirCost = 0;
		    	
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
					
					String proOwnerId = hmProOwner.get(proId);
					budgetAmt += uF.parseToDouble(hmBudgetMap.get(proOwnerId));	
					
			//===last Year===
					lYchargebleAmt += uF.parseToDouble(hmChargeAbleAmt.get(proId+"_LASTYR_CHARGEBLE"));
					lYnonChargebleAmt += uF.parseToDouble(hmChargeAbleAmt.get(proId+"_LASTYR_NON_CHARGEBLE"));
					
					Map<String, String> hmInnerLYEmpCost = proTaskDetails.get(proId+"_LASTYR");
					if(hmInnerLYEmpCost == null) hmInnerLYEmpCost = new HashMap<String, String>();
					Iterator<String> itr = hmInnerLYEmpCost.keySet().iterator();
					while(itr.hasNext()){
						String empId = itr.next();
						lYempHrCost += uF.parseToDouble(hmInnerLYEmpCost.get(empId)) * uF.parseToDouble(hmEmpNetSalary.get(empId));
					}
					
					lYprofFees += uF.parseToDouble(hmDirectProfessionalCost.get(proId+"_LASTYR_PROFESSIONAL"));
					lYotherDirCost += uF.parseToDouble(hmDirectProfessionalCost.get(proId+"_LASTYR_OUT_OF_POCKET"));
			//===last Year===		
					
		    	}
		    	
		    	double actRecTot = otherDirCost+profFees;
		    	double actTot = chargebleAmt+nonChargebleAmt+empHrCost;
		    	double actCont = actRecTot-actTot;
		    	
		    	double lastYrTot = lYchargebleAmt+lYnonChargebleAmt+lYempHrCost;
		    	double lastYrRecTot = lYotherDirCost+lYprofFees;
		    	double lastYrCont = lastYrRecTot-lastYrTot;
		    	
		    	hmReportData.put(sbuId+"_CHARGEBLE", uF.formatIntoComma(chargebleAmt));
		    	hmReportData.put(sbuId+"_NON_CHARGEBLE", uF.formatIntoComma(nonChargebleAmt));
		    	hmReportData.put(sbuId+"_EMP_COST", uF.formatIntoComma(empHrCost));
		    	hmReportData.put(sbuId+"_PROFESSIONAL_COST", uF.formatIntoComma(profFees));
		    	hmReportData.put(sbuId+"_OTHER_DIRECT_COST", uF.formatIntoComma(otherDirCost));
//		    	hmReportData.put(sbuId+"_TOTAL_REC_ACTUAL", uF.formatIntoComma(otherDirCost+profFees));
		    	hmReportData.put(sbuId+"_TOTAL_REC_ACTUAL", uF.formatIntoComma(actRecTot));
//		    	hmReportData.put(sbuId+"_TOTAL_ACTUAL", uF.formatIntoComma(chargebleAmt+nonChargebleAmt+empHrCost));
		    	hmReportData.put(sbuId+"_TOTAL_ACTUAL", uF.formatIntoComma(actTot));
		    	hmReportData.put(sbuId+"_ACTUAL_CONTRIBUTION", uF.formatIntoComma(actCont));
		    	
		    	hmReportData.put(sbuId+"_BUDGET_PROFESSIONAL", uF.formatIntoComma(budgetAmt));
		    	hmReportData.put(sbuId+"_BUDGET_REC_TOTAL", uF.formatIntoComma(budgetAmt));
		    	hmReportData.put(sbuId+"_BUDGET_CONTRIBUTION", uF.formatIntoComma(budgetAmt));
		    	
		    	hmReportData.put(sbuId+"_LASTYR_CHARGEBLE", uF.formatIntoComma(lYchargebleAmt));
		    	hmReportData.put(sbuId+"_LASTYR_NON_CHARGEBLE", uF.formatIntoComma(lYnonChargebleAmt));
		    	hmReportData.put(sbuId+"_LASTYR_EMP_COST", uF.formatIntoComma(lYempHrCost));
		    	hmReportData.put(sbuId+"_LASTYR_PROFESSIONAL_COST", uF.formatIntoComma(lYprofFees));
		    	hmReportData.put(sbuId+"_LASTYR_OTHER_DIRECT_COST", uF.formatIntoComma(lYotherDirCost));
//		    	hmReportData.put(sbuId+"_LASTYR_TOTAL_REC", uF.formatIntoComma(lYotherDirCost+lYprofFees));
		    	hmReportData.put(sbuId+"_LASTYR_TOTAL_REC", uF.formatIntoComma(lastYrRecTot));
//		    	hmReportData.put(sbuId+"_LASTYR_TOTAL", uF.formatIntoComma(lYchargebleAmt+lYnonChargebleAmt+lYempHrCost));
		    	hmReportData.put(sbuId+"_LASTYR_TOTAL", uF.formatIntoComma(lastYrTot));
		    	hmReportData.put(sbuId+"_LASTYR_CONTRIBUTION", uF.formatIntoComma(lastYrCont));
		    	hmReportData.put("CURRENCY", hmCurr.get("SHORT_CURR"));
		    	hmReportData.put("CURRENCY_EXCEL", hmCurr.get("SHORT_CURR_INR"));
		    	
		    	hmSbuListMap.put(sbuId, hmSbuName.get(sbuId));
			}
			
			request.setAttribute("hmSbuListMap", hmSbuListMap);
			request.setAttribute("hmReportData", hmReportData);
//			request.setAttribute("alSbuList", alSbuList);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return SUCCESS;
	}
	
	private void generateCumulativePerformanceExcelReport(UtilityFunctions uF) {
		
		try {
//			List alSbuList = (List)request.getAttribute("alSbuList");
			
			Map<String, String> hmSbuListMap = (Map<String, String>) request.getAttribute("hmSbuListMap");
			if(hmSbuListMap == null) hmSbuListMap = new HashMap<String, String>();
			
			Map<String, String> hmReportData = (Map<String, String>) request.getAttribute("hmReportData");
			if(hmReportData == null) hmReportData = new HashMap<String, String>();
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Divisionwise Cumulative Performance Report");
			
			List<DataStyle> mainHeader=new ArrayList<DataStyle>();
			List<DataStyle> header=new ArrayList<DataStyle>();
			List<DataStyle> subHeader=new ArrayList<DataStyle>();
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			
			mainHeader.add(new DataStyle(session.getAttribute(ORG_NAME)+" CHARTERED ACCOUNTANTS",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			mainHeader.add(new DataStyle("DIVISIONWISE DETAILED ANALYSIS OF CUMULATIVE PERFORMANCE FOR THE YEAR",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			mainHeader.add(new DataStyle("RS. IN LACS",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
			
//			header.add(new DataStyle("PARTICULARS",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
//			for(int i=0; i<alSbuList.size(); i++){
//				header.add(new DataStyle(alSbuList.get(i)+"",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
//			}
			
			List<DataStyle> listOPE = new ArrayList<DataStyle>();
			List<DataStyle> listPE = new ArrayList<DataStyle>();
			List<DataStyle> listEmpCost = new ArrayList<DataStyle>();
			List<DataStyle> listTC = new ArrayList<DataStyle>();
			List<DataStyle> listTNC = new ArrayList<DataStyle>();
			List<DataStyle> listTotRecp = new ArrayList<DataStyle>();
			List<DataStyle> listTotDE = new ArrayList<DataStyle>();
			List<DataStyle> listContribution = new ArrayList<DataStyle>();
			
			listPE.add(new DataStyle("a) Professional Fees",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			listOPE.add(new DataStyle("b) Out Of Pocket",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			listEmpCost.add(new DataStyle("b) Employees Cost",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			listTC.add(new DataStyle("c) Travelling N.C.",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			listTNC.add(new DataStyle("d) Travelling C.",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			listTotRecp.add(new DataStyle("Total Receipts",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			listTotDE.add(new DataStyle("TOTAL",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			listContribution.add(new DataStyle("Contribution",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			Iterator<String> it = hmSbuListMap.keySet().iterator();
			while (it.hasNext()) {
				String sbuId = it.next();
				
				header.add(new DataStyle(hmSbuListMap.get(sbuId)+"",Element.ALIGN_CENTER,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				
				listPE.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_PROFESSIONAL_COST"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listPE.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_BUDGET_PROFESSIONAL"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listPE.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_LASTYR_PROFESSIONAL_COST"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				
				listOPE.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_OTHER_DIRECT_COST"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listOPE.add(new DataStyle("",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listOPE.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_LASTYR_OTHER_DIRECT_COST"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				
				listEmpCost.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_EMP_COST"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listEmpCost.add(new DataStyle("",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listEmpCost.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_LASTYR_EMP_COST"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				
				listTC.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_CHARGEBLE"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listTC.add(new DataStyle("",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listTC.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_LASTYR_CHARGEBLE"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				
				listTNC.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_NON_CHARGEBLE"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listTNC.add(new DataStyle("",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listTNC.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_LASTYR_NON_CHARGEBLE"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				
				listTotRecp.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_TOTAL_REC_ACTUAL"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listTotRecp.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_BUDGET_TOTAL"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listTotRecp.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_LASTYR_TOTAL_REC"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				
				listTotDE.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_TOTAL_ACTUAL"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listTotDE.add(new DataStyle("",Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listTotDE.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_LASTYR_TOTAL"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				
				listContribution.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_ACTUAL_CONTRIBUTION"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listContribution.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_BUDGET_CONTRIBUTION"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				listContribution.add(new DataStyle(uF.showData(hmReportData.get(sbuId+"_LASTYR_CONTRIBUTION"),"0"),Element.ALIGN_RIGHT,"NEW_ROMAN",10,"0","0",BaseColor.LIGHT_GRAY));
				
			}
			
			reportData.add(listPE);				//0
			reportData.add(listOPE);			//1
			reportData.add(listTotRecp);		//2
			reportData.add(listEmpCost);		//3
			reportData.add(listTC);				//4
			reportData.add(listTNC);			//5
			reportData.add(listTotDE);			//6
			reportData.add(listContribution);	//7
			
			subHeader.add(new DataStyle("Actual"+" ("+uF.showData(hmReportData.get("CURRENCY_EXCEL"),"")+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			subHeader.add(new DataStyle("Budget"+" ("+uF.showData(hmReportData.get("CURRENCY_EXCEL"),"")+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			subHeader.add(new DataStyle("Last Yr"+" ("+uF.showData(hmReportData.get("CURRENCY_EXCEL"),"")+")",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			List<DataStyle> rowHeader=new ArrayList<DataStyle>();
//			rowHeader.add(new DataStyle("Receipts",Element.ALIGN_LEFT,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//			rowHeader.add(new DataStyle("a) Professional Fees",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("b) Out Of Pocket",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("Total Receipts",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("Direct Expenses",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("a) Partners related",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("b) Employees Cost",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("c) Travelling N.C.",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("d) Travelling C.",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("TOTAL",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("Contribution",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("Direct Expenses",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("e) I.T. 7% on Receipt",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("f) Facilities exp",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("g) Admin & Office",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("Total Indirect Expenses",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
//			rowHeader.add(new DataStyle("NET SURPLUS",Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
			
			
			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			sheetDesign.generateExcelSheetforCumulativePerformanceReport(workbook,sheet,mainHeader,header,subHeader,rowHeader,reportData);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=DivisionwiseCumulativePerformanceReport.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public String loadReport(UtilityFunctions uF) {
		
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

	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}

	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}
	
}
