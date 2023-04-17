package com.konnect.jpms.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.payroll.ApprovePayroll;
import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class OnBoardProcessing extends ActionSupport implements ServletRequestAware,IStatements{
	
	private static final long serialVersionUID = 1L;
	public HttpSession session;
	public String strSessionEmpId;
	public String strUserType; 
	public String strBaseUserType;
	public CommonFunctions CF;
	String strEmpId;
	String empType;
	private String fromPage;
	private String orgWebsite;
	private String orgFullName;
	private String orgDescription;
	private String strOrgId;
	private String strMonth;
	private String strYear;
	private String calendarYear; 
	private String userscreen;
	
	private List<FillCalendarYears> calendarYearList;
	
	public String execute() throws Exception {
		
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if (CF==null) return LOGIN; 
		
		strUserType = (String)session.getAttribute(USERTYPE);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strSessionEmpId = (String)session.getAttribute(EMPID);
		strOrgId		= (String)session.getAttribute(ORGID);

		request.setAttribute(TITLE, "OnBoard Process");
		request.setAttribute(PAGE, "/jsp/common/OnBoardProcessing.jsp");
		
		viewblock(uF);
		
		return SUCCESS;
	}
	
	public String viewblock(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		Database db = new Database();
		db.setRequest(request);
		
		try{
			con = db.makeConnection(con);

			//System.out.println("strUserType==>"+strUserType);
			//System.out.println("strSessionEmpId==>"+strSessionEmpId);

			request.setAttribute("DOC_RETRIVE_LOCATION", CF.getStrDocRetriveLocation());
			
			//System.out.println("CF.getStrDocRetriveLocation()==>"+CF.getStrDocRetriveLocation());
			
			int empHr=0;
			int empHod=0;
			int empMnger=0;
			
			pst=con.prepareStatement("select  supervisor_emp_id,hod_emp_id,emp_hr from employee_official_details where emp_id=?");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			rst=pst.executeQuery();
			while(rst.next()){
				empHr=rst.getInt("emp_hr");
				empHod=rst.getInt("hod_emp_id");
				empMnger=rst.getInt("supervisor_emp_id");
			}
			rst.close();
			pst.close();
			
			Map<String, String> hmEmpProfileHr=null;
			Map<String, String> hmEmpProfileHod=null;
			Map<String, String> hmEmpProfileMngr=null;
			Map<String, String> hmEmpProfile=null;
		
			hmEmpProfile = CF.getEmpProfileDetail(con, request, session, CF, uF, strUserType, strSessionEmpId);

	//************for hr,hod and mngr profile************
			if(empHr!=0){
				
				 hmEmpProfileHr = CF.getEmpProfileDetail(con, request, session, CF, uF, null, ""+empHr);
			}if(empHod!=0){
				
				 hmEmpProfileHod = CF.getEmpProfileDetail(con, request, session, CF, uF, null, ""+empHod);
			}if(empMnger!=0){
				
				 hmEmpProfileMngr= CF.getEmpProfileDetail(con, request, session, CF, uF, null, ""+empMnger);
			}
			
			request.setAttribute("hmEmpProfileHr", hmEmpProfileHr);
			request.setAttribute("hmEmpProfileHod", hmEmpProfileHod);
			request.setAttribute("hmEmpProfileMngr", hmEmpProfileMngr);
			request.setAttribute("hmEmpProfile", hmEmpProfile);
			
		
	//*************for About Company************
			pst = con.prepareStatement(selectSettings);
			rst = pst.executeQuery();
			while(rst.next()){
				
				if(rst.getString("options").equalsIgnoreCase(O_ORG_FULL_NAME)){
					setOrgFullName(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_ORG_DESCRIPTION)){
					setOrgDescription(rst.getString("value"));
				}
				
				if(rst.getString("options").equalsIgnoreCase(O_ORG_WEBSITE)){
					setOrgWebsite(rst.getString("value"));
				}
			}
			rst.close();
			pst.close();
			
//*****************for company manual*****************	
			pst = con.prepareStatement("select * from company_manual where status = 1 and org_id = ? order by _date desc limit 1");
			pst.setInt(1, uF.parseToInt(strOrgId));
			rst = pst.executeQuery();
//			System.out.println(" pst for company manual ===>> " + pst);
			while(rst.next()) {
				String extenstion = null;
				if(rst.getString("manual_doc") !=null && !rst.getString("manual_doc").trim().equals("")){
					extenstion = FilenameUtils.getExtension(rst.getString("manual_doc").trim());
				}
				
				String manualDocPath = "";
				if(rst.getString("manual_doc")!=null && !rst.getString("manual_doc").equals("")){
					if(CF.getStrDocSaveLocation()==null){
						manualDocPath =  DOCUMENT_LOCATION +"/"+rst.getString("manual_id")+"/"+rst.getString("manual_doc"); //+"/"+rst.getString("emp_id")
					} else {
						manualDocPath = CF.getStrDocRetriveLocation()+I_COMPANY_MANUAL+"/"+rst.getString("manual_id")+"/"+rst.getString("manual_doc"); //+"/"+rst.getString("emp_id")
					}
				}
				List<String> availableExt = CF.getAvailableExtention();
				request.setAttribute("availableExt", availableExt);
				request.setAttribute("extention",extenstion);
				request.setAttribute("manualDocPath",manualDocPath);
				request.setAttribute("MANUAL_ID", rst.getString("manual_id"));
				request.setAttribute("TITLE1", rst.getString("manual_title"));
				request.setAttribute("BODY", rst.getString("manual_body"));
				request.setAttribute("DATE", uF.getDateFormat(rst.getString("_date"), DBTIMESTAMP, CF.getStrReportDateFormat()));
			}
			rst.close();
			pst.close();	
			
			viewKRALearningAndKRAData(con,uF,strSessionEmpId);
			viewLeaveRosterSalaryStrucure(con,uF,request,strSessionEmpId,hmEmpProfile);
		
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rst);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		return LOAD;
	}
	
	public void viewKRALearningAndKRAData(Connection con,UtilityFunctions uF ,String strSessionEmpId){

		PreparedStatement pst1 = null;
		PreparedStatement pst = null;
		ResultSet rst = null;
		ResultSet rst1 = null;
		
		List<List<String>> alLiveLearnings=new ArrayList<List<String>>();
		List<String> alkra=new ArrayList<String>();

		try {
			/*pst=con.prepareStatement("select kra_description from goal_kras where emp_ids LIKE '%,"+strSessionEmpId+",%'");
//			System.out.println("pst==>"+pst);
			rst=pst.executeQuery();
			while(rst.next()){
				alkra.add(uF.showData(rst.getString("kra_description"), "-"));
			}
			rst.close();
			pst.close();*/
			
			pst = con.prepareStatement("select * from learning_plan_details lpd where lpd.learner_ids LIKE '%,"+strSessionEmpId+",%' and is_publish = true");
			rst = pst.executeQuery();
			while(rst.next()) {
			
				pst1 = con.prepareStatement("select min(from_date) as minDate, max(to_date) as maxDate from learning_plan_stage_details where learning_plan_id = ?");
				pst1.setInt(1, rst.getInt("learning_plan_id"));
				rst1 = pst1.executeQuery();
				String minFromDate = null, maxToDate = null; 
				while (rst1.next()) {
					minFromDate = rst1.getString("minDate");
					maxToDate = rst1.getString("maxDate");
				}
				rst1.close();
				pst1.close();
				String fromDateDiff = "0";
				String toDateDiff = "0";
				
				if(minFromDate != null && maxToDate != null && !minFromDate.equals("") && !maxToDate.equals("")){
					fromDateDiff = uF.dateDifference(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, minFromDate, DBDATE);
					toDateDiff = uF.dateDifference(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, maxToDate, DBDATE);
				}
				if(uF.parseToInt(fromDateDiff) <= 1 && uF.parseToInt(toDateDiff) >= 1) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rst.getString("learning_plan_name"));
					innerList.add(uF.getDateFormat(minFromDate, DBDATE, CF.getStrReportDateFormat())); //S Date
					innerList.add(uF.getDateFormat(maxToDate, DBDATE, CF.getStrReportDateFormat())); //E Date
					alLiveLearnings.add(innerList);
				}
			}
			rst.close();
			pst.close();
			
//			System.out.println("alLiveLearnings==>"+alLiveLearnings);
			request.setAttribute("alLiveLearnings", alLiveLearnings);
			List<List<String>> alKRADetails = CF.getEmpKRADetails(con, strSessionEmpId, uF);
			request.setAttribute("alKRADetails", alKRADetails);
			
//			System.out.println("alkra==>"+alkra);

		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(rst !=null){
				try {
					rst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public void viewLeaveRosterSalaryStrucure(Connection con,UtilityFunctions uF ,HttpServletRequest request ,String strSessionEmpId,Map<String, String> hmEmpProfile){
		PreparedStatement pst = null;
		ResultSet rs = null;
		
		
		String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
		String strFinancialYearStart = strFinancialYearDates[0];
		String strFinancialYearEnd = strFinancialYearDates[1];

		String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
		String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
		if(strPayCycleDate !=null && strPayCycleDate.length > 0){
			String startDate = strPayCycleDate[0];
			String endDate = strPayCycleDate[1];
			String strPC = strPayCycleDate[2];
		
		int nPayMonth = uF.parseToInt(uF.getDateFormat(endDate, DATE_FORMAT, "MM"));
		
		List<List<String>> reportList = new ArrayList<List<String>>();

		try{
		
	//***********************code for leave type name and remaining leave***************
			
			pst = con.prepareStatement("select * from probation_policy where emp_id = ?");
		    pst.setInt(1, uF.parseToInt(strSessionEmpId));
		    rs = pst.executeQuery();
		    String strEmpLeaveTypeId = null;
		    while (rs.next()) { 
		    	strEmpLeaveTypeId = rs.getString("leaves_types_allowed");
		    }
			
			String strWlocationid = CF.getEmpWlocationId(con, uF, strSessionEmpId);
		    
			Map<String, String> hmLeaveDetails = new HashMap<String,String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_leave_type elt,leave_type lt where lt.leave_type_id = elt.leave_type_id " +
					" and level_id = (select dd.level_id from level_details ld, designation_details dd, grades_details gd " +
					" where ld.level_id = dd.level_id and gd.designation_id = dd.designation_id " +
					"and gd.grade_id = (select grade_id from employee_official_details where emp_id = ?)) and wlocation_id=? and elt.org_id=?" +
					" and lt.is_compensatory=false and is_constant_balance=false ");
			if(strEmpLeaveTypeId!=null && !strEmpLeaveTypeId.trim().equals("") && !strEmpLeaveTypeId.trim().equalsIgnoreCase("NULL")){
				sbQuery.append("and lt.leave_type_id in ("+strEmpLeaveTypeId+")");
			}
			pst = con.prepareStatement(sbQuery.toString());
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
			pst.setInt(2, uF.parseToInt(strWlocationid));
			pst.setInt(3, uF.parseToInt(strOrgId));
//			System.out.println("1 pst=====>"+pst);
			rs = pst.executeQuery();
			while(rs.next()){
				
				hmLeaveDetails.put(rs.getString("leave_type_id"), rs.getString("leave_type_name"));
			}
			rs.close();
			pst.close(); 
			
			pst = con.prepareStatement("select sum(accrued) as accrued,a.leave_type_id from (select max(_date) as daa,leave_type_id " +
	            		"from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) " +
	            		"group by emp_id,leave_type_id )as a,leave_register1 lr where emp_id=? and _type!='C' and a.leave_type_id=lr.leave_type_id " +
	            		"and a.daa<=lr._date group by a.leave_type_id");
	            pst.setInt(1,  uF.parseToInt(strSessionEmpId));
	            pst.setInt(2,  uF.parseToInt(strSessionEmpId));
	            rs = pst.executeQuery();
	            Map<String, String> hmAccruedBalance=new HashMap<String, String>();
	            while (rs.next()) {
	            	hmAccruedBalance.put(rs.getString("leave_type_id"), rs.getString("accrued"));                
	            }
				rs.close();
				pst.close();
				
				 pst = con.prepareStatement("select sum(leave_no) as count,leave_type_id from(select a.daa,lar.* from (select max(_date) as daa,leave_type_id " +
		            		"from leave_register1 where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) " +
		            		"and register_id in (select max(register_id) from leave_register1 where emp_id=? and _type='C' " +
		            		"and leave_type_id in (select leave_type_id from leave_type) group by emp_id,leave_type_id) " +
		            		"group by emp_id,leave_type_id) as a,leave_application_register lar where emp_id=? and is_paid=true and (is_modify is null or is_modify=false) " +
		            		"and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date) as a group by leave_type_id");
	            pst.setInt(1, uF.parseToInt(strSessionEmpId));
	            pst.setInt(2, uF.parseToInt(strSessionEmpId));
	            pst.setInt(3, uF.parseToInt(strSessionEmpId));
	            rs = pst.executeQuery();
	            Map<String, String> hmPaidBalance=new HashMap<String, String>();
	            while (rs.next()) {
	            	hmPaidBalance.put(rs.getString("leave_type_id"), rs.getString("count"));
	            }
				rs.close();
				pst.close();
				
			pst = con.prepareStatement("select leave_type_id, balance from leave_register1 where register_id in (select max(register_id) from leave_register1 " +
			"where emp_id=? and _type='C' and leave_type_id in (select leave_type_id from leave_type) group by emp_id,leave_type_id)");
			pst.setInt(1, uF.parseToInt(strSessionEmpId));
//        System.out.println("2 pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmMainBalance=new HashMap<String, String>();
			while (rs.next()) {
				hmMainBalance.put(rs.getString("leave_type_id"), rs.getString("balance"));
			}
			rs.close();
			pst.close();
    
			Iterator it = hmLeaveDetails.keySet().iterator();
			while(it.hasNext()){
				String strLeaveTypeId = (String)it.next();
				String leaveTypeName = hmLeaveDetails.get(strLeaveTypeId);
				
				double dblBalance = uF.parseToDouble(hmMainBalance.get(strLeaveTypeId));
				dblBalance += uF.parseToDouble(hmAccruedBalance.get(strLeaveTypeId));
				
				double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get(strLeaveTypeId));
				
				if(dblBalance > 0 && dblBalance >= dblPaidBalance){
		            dblBalance = dblBalance - dblPaidBalance; 
		        }
				double dblRemaining = dblBalance;
			
				List<String> innerList=new ArrayList<String>();   
				innerList.add(leaveTypeName);
				innerList.add(uF.formatIntoTwoDecimalWithOutComma(dblRemaining)+"");
				
				reportList.add(innerList);

			}
			
			request.setAttribute("leaveList", reportList);
			
//***********************code for salary structure**********************
			
			viewPerkWithSalary(con,uF,strSessionEmpId);
			viewReimbursementPartofCTC(con,uF,strSessionEmpId);
			
			int nSalaryStrucuterType = uF.parseToInt(CF.getStrSalaryStructure());
//			System.out.println("nSalaryStrucuterType ===> " + nSalaryStrucuterType);
			if(nSalaryStrucuterType == S_GRADE_WISE){
				getSalaryHeadsforEmployeeByGrade(con, uF, uF.parseToInt(strSessionEmpId), hmEmpProfile);
			} else {
				getSalaryHeadsforEmployee(con, uF, uF.parseToInt(strSessionEmpId), hmEmpProfile);
			}
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			request.setAttribute("hmFeatureStatus", hmFeatureStatus);
			Map<String, List<String>> hmFeatureUserTypeId = (Map<String, List<String>>)request.getAttribute("hmFeatureUserTypeId");
			request.setAttribute("hmFeatureUserTypeId", hmFeatureUserTypeId);
			
//************code for roster*******************
			
			setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			setStrYear(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "yyyy"))+"");
		
			List<String> alDates = new ArrayList<String>();
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1 );
			cal.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
			int minDays = cal.getActualMinimum(Calendar.DATE);
			cal.set(Calendar.DAY_OF_MONTH, minDays);
			int maxDays = cal.getActualMaximum(Calendar.DATE);
			
			String strD11 = null;
			for(int i=0; i<maxDays; i++){
				
				strD11 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
				+ cal.get(Calendar.YEAR);
		
				alDates.add(uF.getDateFormat(strD11, DATE_FORMAT, CF.getStrReportDateFormat()));

				cal.add(Calendar.DATE, 1);
			}
			request.setAttribute("alDates", alDates);
			
			//System.out.println("alDates==>"+alDates);
			
			Map<String, String> hmRoster = new HashMap<String, String>();
			pst = con.prepareStatement("SELECT * FROM roster_details rd, employee_personal_details epd,employee_official_details eod WHERE rd.emp_id = epd.emp_per_id and eod.emp_id=epd.emp_per_id and epd.joining_date <= ? and epd.is_alive=true and "
					+" (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) AND rd.emp_id=? and _date between ? and ? order by _date desc");
			pst.setDate(1, uF.getDateFormat(endDate, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(endDate, DATE_FORMAT));
			pst.setInt(5, uF.parseToInt((String) session.getAttribute(EMPID))); 
			pst.setDate(6, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(7, uF.getDateFormat(endDate, DATE_FORMAT));
			
			//System.out.println("pst in onboarding===>"+pst);

			rs = pst.executeQuery();
			while (rs.next()) {
				
				hmRoster.put(uF.getDateFormat(rs.getString("_date"), DBDATE,CF.getStrReportDateFormat()) + "FROM",   uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
				hmRoster.put(uF.getDateFormat(rs.getString("_date"), DBDATE,CF.getStrReportDateFormat()) + "TO" , uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
			}
			rs.close();
			pst.close();
			
		//System.out.println("hmRoster==>"+hmRoster);	
		request.setAttribute("hmRoster", hmRoster);
		
		String startTime="";
		String endTime="";
		pst=con.prepareStatement("select wlocation_start_time,wlocation_end_time from employee_official_details eod,work_location_info wl,employee_personal_details epd " +
				"where eod.emp_id=epd.emp_per_id and epd.joining_date <= ? and epd.is_alive=true and  (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?))" +
				" and eod.wlocation_id=wl.wlocation_id and emp_id=?");
	
		pst.setDate(1, uF.getDateFormat(endDate, DATE_FORMAT));
		pst.setDate(2, uF.getDateFormat(endDate, DATE_FORMAT));
		pst.setDate(3, uF.getDateFormat(startDate, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(endDate, DATE_FORMAT));
		pst.setInt(5, uF.parseToInt((String) session.getAttribute(EMPID))); 
		
		//System.out.println("pst for location==>"+pst);
		rs=pst.executeQuery();
		while(rs.next()){
			startTime=rs.getString("wlocation_start_time");
			endTime=rs.getString("wlocation_end_time");
		}
		rs.close();
		pst.close();
		request.setAttribute("startTime", startTime);
		request.setAttribute("endTime", endTime);
		
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(rs !=null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			
			if(pst !=null){
				try {
					pst.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} 
	 }
  }

	private void viewReimbursementPartofCTC(Connection con, UtilityFunctions uF, String strEmpId) {
	//System.out.println("in viewReimbursementPartofCTC");
	PreparedStatement pst = null;
	ResultSet rs = null;

	try {

		String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
		String strFinancialYearStart = strFinancialYearDates[0];
		String strFinancialYearEnd = strFinancialYearDates[1];

		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
		String strOrgId = CF.getEmpOrgId(con, uF, strEmpId);
		String strLevelId = CF.getEmpLevelId(con, strEmpId);

		pst = con.prepareStatement("select * from reimbursement_ctc_details where level_id=? and org_id=? and reimbursement_ctc_id in " +
				"(select reimbursement_ctc_id from reimbursement_head_details where level_id=? and org_id=? and reimbursement_head_id in " +
				"(select reimbursement_head_id from reimbursement_head_amt_details where financial_year_start=? and financial_year_end=?))" +
				" order by reimbursement_name");
		pst.setInt(1, uF.parseToInt(strLevelId));
		pst.setInt(2, uF.parseToInt(strOrgId));
		pst.setInt(3, uF.parseToInt(strLevelId));
		pst.setInt(4, uF.parseToInt(strOrgId));
		pst.setDate(5, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(6, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//		System.out.println("pst==>"+pst);
		rs = pst.executeQuery();
		List<Map<String, String>> alReimbursementCTC = new ArrayList<Map<String,String>>();
		while(rs.next()){
			Map<String, String> hmReimbursementCTCInner = new HashMap<String, String>();
			hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_ID", rs.getString("reimbursement_ctc_id"));
			hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_CODE", uF.showData(rs.getString("reimbursement_code"), ""));
			hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_NAME", uF.showData(rs.getString("reimbursement_name"), ""));
			hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_LEVEL_ID", rs.getString("level_id"));
			hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_ORG_ID", rs.getString("org_id"));
			hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_ADDED_BY", uF.showData(hmEmpName.get(rs.getString("added_by")), ""));
			hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_ADDED_DATE", uF.getDateFormat(rs.getString("added_date"), DBDATE, CF.getStrReportDateFormat()));
			hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_UPDATE_BY", uF.showData(hmEmpName.get(rs.getString("update_by")), ""));
			hmReimbursementCTCInner.put("REIMBURSEMENT_CTC_UPDATE_DATE", uF.getDateFormat(rs.getString("update_date"), DBDATE, CF.getStrReportDateFormat()));
			
			alReimbursementCTC.add(hmReimbursementCTCInner);
		}
		rs.close();
		pst.close();
		request.setAttribute("alReimbursementCTC", alReimbursementCTC);
//		System.out.println("alReimbursementCTC===>"+alReimbursementCTC);
		
		pst = con.prepareStatement("select rhd.reimbursement_ctc_id,rhd.reimbursement_head_id,rhd.reimbursement_head_code,rhd.reimbursement_head_name," +
				"rhad.reimbursement_head_amt_id,rhad.amount,rhad.is_attachment,rhad.is_optimal from reimbursement_head_details rhd, " +
				"reimbursement_head_amt_details rhad where rhd.reimbursement_head_id=rhad.reimbursement_head_id and rhd.level_id=? " +
				"and rhd.org_id=? and rhad.financial_year_start=? and rhad.financial_year_end=?");
		pst.setInt(1, uF.parseToInt(strLevelId));
		pst.setInt(2, uF.parseToInt(strOrgId));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//		System.out.println("pst==>"+pst);
		rs = pst.executeQuery();
		Map<String, List<Map<String, String>>> hmReimbursementCTCHead = new HashMap<String, List<Map<String,String>>>(); 
		while(rs.next()){
			List<Map<String, String>> alReimCTCHead = hmReimbursementCTCHead.get(rs.getString("reimbursement_ctc_id"));
			if(alReimCTCHead == null) alReimCTCHead = new ArrayList<Map<String,String>>();				
			
			Map<String, String> hmReimCTCHeadInner = new HashMap<String, String>();
			hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_ID", rs.getString("reimbursement_head_id"));
			hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_CODE", rs.getString("reimbursement_head_code"));
			hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_NAME", rs.getString("reimbursement_head_name"));
			hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_AMT_ID", rs.getString("reimbursement_head_amt_id"));
			hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_AMOUNT", rs.getString("amount"));
			hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_IS_ATTACHMENT", rs.getString("is_attachment"));
			hmReimCTCHeadInner.put("REIMBURSEMENT_HEAD_IS_OPTIMAL", rs.getString("is_optimal"));
			
			alReimCTCHead.add(hmReimCTCHeadInner);
			
			hmReimbursementCTCHead.put(rs.getString("reimbursement_ctc_id"),alReimCTCHead);
		}
		rs.close();
		pst.close();
		request.setAttribute("hmReimbursementCTCHead", hmReimbursementCTCHead);
//		System.out.println("hmReimbursementCTCHead===>"+hmReimbursementCTCHead);
		
		String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
		String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
		if(strPayCycleDate !=null && strPayCycleDate.length > 0){
			String startDate = strPayCycleDate[0];
			String endDate = strPayCycleDate[1];
			String strPC = strPayCycleDate[2];
			
			pst = con.prepareStatement("select * from reimbursement_assign_head_details where emp_id=? and level_id=? and org_id=? " +
					"and financial_year_start=? and financial_year_end=? and trail_status=? and paycycle_from=? and paycycle_to=?" +
					" and paycycle=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strLevelId));
			pst.setInt(3, uF.parseToInt(strOrgId));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setBoolean(6, true);
			pst.setDate(7, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(endDate, DATE_FORMAT));
			pst.setInt(9, uF.parseToInt(strPC));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmAssignReimHead = new HashMap<String, Map<String,String>>();
			while(rs.next()){
				Map<String, String> hmAssignReim = new HashMap<String, String>();
				hmAssignReim.put("REIM_ASSIGN_SALARY_ID", rs.getString("reim_assign_head_id"));
				hmAssignReim.put("REIM_EMP_ID", rs.getString("emp_id"));
				hmAssignReim.put("REIM_HEAD_ID", rs.getString("reimbursement_head_id"));
				hmAssignReim.put("REIM_CTC_ID", rs.getString("reimbursement_ctc_id"));
				hmAssignReim.put("LEVEL_ID", rs.getString("level_id"));
				hmAssignReim.put("ORG_ID", rs.getString("org_id"));
				hmAssignReim.put("AMOUNT", rs.getString("amount"));
				hmAssignReim.put("FINANCIAL_YEAR_START", rs.getString("financial_year_start"));
				hmAssignReim.put("FINANCIAL_YEAR_END", rs.getString("financial_year_end"));
				hmAssignReim.put("STATUS", ""+uF.parseToBoolean(rs.getString("status")));
				hmAssignReim.put("TRAIL_STATUS", ""+uF.parseToBoolean(rs.getString("trail_status")));
				hmAssignReim.put("ADDED_BY", rs.getString("added_by"));
				hmAssignReim.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
				hmAssignReim.put("UPDATE_BY", rs.getString("update_by"));
				hmAssignReim.put("UPDATE_DATE", uF.getDateFormat(rs.getString("update_date"), DBDATE, DATE_FORMAT));
				hmAssignReim.put("PAYCYCLE_FROM", uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, DATE_FORMAT));
				hmAssignReim.put("PAYCYCLE_TO", uF.getDateFormat(rs.getString("paycycle_to"), DBDATE, DATE_FORMAT));
				hmAssignReim.put("PAYCYCLE", rs.getString("paycycle"));					
				
				hmAssignReimHead.put(rs.getString("reimbursement_head_id")+"_"+rs.getString("reimbursement_ctc_id"), hmAssignReim);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAssignReimHead", hmAssignReimHead);
//			System.out.println("hmAssignReimHead=====>"+hmAssignReimHead);	
			
			pst = con.prepareStatement("select reimbursement_head_id from reimbursement_ctc_applied_details where emp_id=? " +
					"and is_approved in (0,1) and financial_year_start=? and financial_year_end =? and reimbursement_head_id in " +
					"(select rhd.reimbursement_head_id from reimbursement_head_details rhd, reimbursement_ctc_details rcd " +
					"where rhd.reimbursement_ctc_id=rcd.reimbursement_ctc_id and rcd.level_id=? and rcd.org_id=?) " +
					"and reim_ctc_applied_id in (select reim_ctc_applied_id from reimbursement_ctc_applied_paycycle where emp_id =? " +
					"and paycycle_from=? and paycycle_to=? and paycycle=? and financial_year_start=? and financial_year_end =?)");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(4, uF.parseToInt(strLevelId));
			pst.setInt(5, uF.parseToInt(strOrgId));
			pst.setInt(6, uF.parseToInt(strEmpId));
			pst.setDate(7, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(endDate, DATE_FORMAT));
			pst.setInt(9, uF.parseToInt(strPC));
			pst.setDate(10, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(11, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			List<String> alReimbursementCTCAppliedId = new ArrayList<String>();
			while(rs.next()){
				alReimbursementCTCAppliedId.add(rs.getString("reimbursement_head_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alReimbursementCTCAppliedId", alReimbursementCTCAppliedId);
			
			Map<String, String> hmReimCTC = new HashMap<String, String>();
			CF.getReimbursementCTC(con, uF, uF.parseToInt(strEmpId), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(strLevelId), hmReimCTC);
			request.setAttribute("hmReimCTC", hmReimCTC);
//			System.out.println("hmReimCTC==>"+hmReimCTC);
			
			Map<String, String> hmReimCTCHeadAmount = new HashMap<String, String>();
			CF.getReimbursementCTCHeadAmount(con, uF, uF.parseToInt(strEmpId), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(strLevelId), hmReimCTCHeadAmount);
			request.setAttribute("hmReimCTCHeadAmount", hmReimCTCHeadAmount);
//			System.out.println("hmReimCTCHeadAmount==>"+hmReimCTCHeadAmount);
			request.setAttribute("strFinancialYearStart", strFinancialYearStart);
			request.setAttribute("strFinancialYearEnd", strFinancialYearEnd);
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rs != null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(pst != null){
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
	
	private void viewPerkWithSalary(Connection con, UtilityFunctions uF, String strEmpId) {

	//System.out.println("in viewPerkWithSalary*****");
	
	PreparedStatement pst = null;
	ResultSet rs = null;

	try {

		String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
		String strFinancialYearStart = strFinancialYearDates[0];
		String strFinancialYearEnd = strFinancialYearDates[1];

		Map<String, String> hmEmpName = CF.getEmpNameMap(con, null, null);
		String strOrgId = CF.getEmpOrgId(con, uF, strEmpId);
		String strLevelId = CF.getEmpLevelId(con, strEmpId);

		pst = con.prepareStatement("select * from salary_details where is_align_with_perk=true and level_id in (select level_id " +
				"from level_details where org_id=? and level_id=?) and (is_delete is null or is_delete=false)");
		pst.setInt(1, uF.parseToInt(strOrgId));
		pst.setInt(2, uF.parseToInt(strLevelId));
		rs = pst.executeQuery();
		Map<String, String> hmPerkAlign = new HashMap<String,String>(); 
		while (rs.next()){
			hmPerkAlign.put(rs.getString("salary_head_id"),rs.getString("salary_head_name"));
		}
		rs.close();
		pst.close();
		
		Map<String, List<Map<String, String>>> hmPerkAlignSalary = new HashMap<String, List<Map<String, String>>>();
		pst = con.prepareStatement("SELECT * FROM perk_salary_details where org_id=? and level_id=? and financial_year_start=? " +
				"and financial_year_end=? and salary_head_id in (select salary_head_id from salary_details where org_id=? " +
				"and level_id=? and (is_delete is null or is_delete =false))");
		pst.setInt(1, uF.parseToInt(strOrgId));
		pst.setInt(2, uF.parseToInt(strLevelId));
		pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
		pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
		pst.setInt(5, uF.parseToInt(strOrgId));
		pst.setInt(6, uF.parseToInt(strLevelId));
		rs = pst.executeQuery();
		while (rs.next()) {
			List<Map<String, String>> outerList = hmPerkAlignSalary.get(rs.getString("salary_head_id"));
			if (outerList == null) outerList = new ArrayList<Map<String, String>>();

			Map<String, String> hmPerkSalary = new HashMap<String, String>();
			hmPerkSalary.put("PERK_SALARY_ID",rs.getString("perk_salary_id"));
			hmPerkSalary.put("PERK_CODE",uF.showData(rs.getString("perk_code"), ""));
			hmPerkSalary.put("PERK_NAME",uF.showData(rs.getString("perk_name"), ""));
			hmPerkSalary.put("PERK_DESCRIPTION",uF.showData(rs.getString("perk_description"), ""));
			hmPerkSalary.put("PERK_AMOUNT",uF.showData(rs.getString("amount"), ""));
			hmPerkSalary.put("PERK_USER",hmEmpName.get(rs.getString("user_id")));
			hmPerkSalary.put("ENTRY_DATE",uF.getDateFormat(rs.getString("entry_date"), DBDATE, CF.getStrReportDateFormat()));
			hmPerkSalary.put("FINANCIAL_YEAR",uF.getDateFormat(rs.getString("financial_year_start"), DBDATE, CF.getStrReportDateFormat()) + " to "
					+ uF.getDateFormat(rs.getString("financial_year_end"), DBDATE, CF.getStrReportDateFormat()));
			hmPerkSalary.put("PERK_ATTACHMENT",rs.getString("is_attachment"));
			hmPerkSalary.put("PERK_IS_OPTIMAL",rs.getString("is_optimal"));

			outerList.add(hmPerkSalary);

			hmPerkAlignSalary.put(rs.getString("salary_head_id"), outerList);
		}
		rs.close();
		pst.close();
		
		request.setAttribute("hmPerkAlign", hmPerkAlign);
		request.setAttribute("hmPerkAlignSalary", hmPerkAlignSalary);
		
		String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
		String[] strPayCycleDate = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrgId);
		if(strPayCycleDate !=null && strPayCycleDate.length > 0){
			String startDate = strPayCycleDate[0];
			String endDate = strPayCycleDate[1];
			String strPC = strPayCycleDate[2];
			
			pst = con.prepareStatement("select * from perk_assign_salary_details where emp_id=? and level_id=? and org_id=? " +
					"and financial_year_start=? and financial_year_end=? and trail_status=? and paycycle_from=? and paycycle_to=?" +
					" and paycycle=?");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setInt(2, uF.parseToInt(strLevelId));
			pst.setInt(3, uF.parseToInt(strOrgId));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setBoolean(6, true);
			pst.setDate(7, uF.getDateFormat(startDate, DATE_FORMAT));
			pst.setDate(8, uF.getDateFormat(endDate, DATE_FORMAT));
			pst.setInt(9, uF.parseToInt(strPC));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, Map<String, String>> hmAssignPerkSalary = new HashMap<String, Map<String,String>>();
			while(rs.next()){
				Map<String, String> hmAssignPerk = new HashMap<String, String>();
				hmAssignPerk.put("PERK_ASSIGN_SALARY_ID", rs.getString("perk_assign_salary_id"));
				hmAssignPerk.put("PERK_EMP_ID", rs.getString("emp_id"));
				hmAssignPerk.put("PERK_SALARY_ID", rs.getString("perk_salary_id"));
				hmAssignPerk.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
				hmAssignPerk.put("LEVEL_ID", rs.getString("level_id"));
				hmAssignPerk.put("ORG_ID", rs.getString("org_id"));
				hmAssignPerk.put("AMOUNT", rs.getString("amount"));
				hmAssignPerk.put("FINANCIAL_YEAR_START", rs.getString("financial_year_start"));
				hmAssignPerk.put("FINANCIAL_YEAR_END", rs.getString("financial_year_end"));
				hmAssignPerk.put("STATUS", ""+uF.parseToBoolean(rs.getString("status")));
				hmAssignPerk.put("TRAIL_STATUS", ""+uF.parseToBoolean(rs.getString("trail_status")));
				hmAssignPerk.put("ADDED_BY", rs.getString("added_by"));
				hmAssignPerk.put("ENTRY_DATE", uF.getDateFormat(rs.getString("entry_date"), DBDATE, DATE_FORMAT));
				hmAssignPerk.put("UPDATE_BY", rs.getString("update_by"));
				hmAssignPerk.put("UPDATE_DATE", uF.getDateFormat(rs.getString("update_date"), DBDATE, DATE_FORMAT));
				hmAssignPerk.put("PAYCYCLE_FROM", uF.getDateFormat(rs.getString("paycycle_from"), DBDATE, DATE_FORMAT));
				hmAssignPerk.put("PAYCYCLE_TO", uF.getDateFormat(rs.getString("paycycle_to"), DBDATE, DATE_FORMAT));
				hmAssignPerk.put("PAYCYCLE", rs.getString("paycycle"));					
				
				hmAssignPerkSalary.put(rs.getString("salary_head_id")+"_"+rs.getString("perk_salary_id"), hmAssignPerk);
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAssignPerkSalary", hmAssignPerkSalary);
//			System.out.println("hmAssignPerkSalary=====>"+hmAssignPerkSalary);
			
			pst = con.prepareStatement("select * from perk_salary_applied_details where emp_id=? and is_approved in (0,1) " +
					"and financial_year_start=? and financial_year_end = ? and perk_salary_id in (select perk_salary_id " +
					"from perk_salary_details where financial_year_start=? and financial_year_end = ? " +
					"and salary_head_id in (select salary_head_id from salary_details where is_align_with_perk=true " +
					"and level_id in (select level_id from level_details where level_id=? and org_id=?) " +
					"and (is_delete is null or is_delete=false)))");
			pst.setInt(1, uF.parseToInt(strEmpId));
			pst.setDate(2, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(6, uF.parseToInt(strLevelId));
			pst.setInt(7, uF.parseToInt(strOrgId));
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			List<String> alPerkSalaryAppliedId = new ArrayList<String>();
			while(rs.next()){
				alPerkSalaryAppliedId.add(rs.getString("perk_salary_id"));
			}
			rs.close();
			pst.close();
			request.setAttribute("alPerkSalaryAppliedId", alPerkSalaryAppliedId);
			
			Map<String, String> hmPerkAlignAmount = new HashMap<String, String>();
			CF.getPerkAlignAmount(con, uF, uF.parseToInt(strEmpId), strFinancialYearStart, strFinancialYearEnd, startDate, endDate, strPC, uF.parseToInt(strOrgId), uF.parseToInt(strLevelId), hmPerkAlignAmount);
			request.setAttribute("hmPerkAlignAmount", hmPerkAlignAmount);
		}			
		
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rs != null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(pst != null){
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}

	
	

   public void getSalaryHeadsforEmployee(Connection con, UtilityFunctions uF, int intEmpIdReq, Map<String, String> hmEmpProfile) {
	
//	System.out.println("getSalaryHeadsforEmployee********");
	PreparedStatement pst = null;
	ResultSet rs = null;

	try {
//		System.out.println("getSalaryHeadsforEmployee ===> ");
		String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
		String strFinancialYearStart = strFinancialYearDates[0];
		String strFinancialYearEnd = strFinancialYearDates[1];
		
		String levelId = CF.getEmpLevelId(con, ""+intEmpIdReq);			
		String strOrg = CF.getEmpOrgId(con, uF, ""+intEmpIdReq);
		
		String currId = CF.getOrgCurrencyIdByOrg(con, strOrg);
		Map<String, Map<String, String>> hmCurrencyDetailsMap =  CF.getCurrencyDetails(con);
		if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String,String>>();
		Map<String, String> hmCurr = hmCurrencyDetailsMap.get(currId);
		if(hmCurr == null) hmCurr = new HashMap<String, String>();
		String strCurr = hmCurr.get("LONG_CURR")!=null && !hmCurr.get("LONG_CURR").equalsIgnoreCase("null") ? hmCurr.get("LONG_CURR")+" " : "";
		request.setAttribute("strCurr", strCurr);

//		Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
//		Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
		
		String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
		String[] strPayCycleDates = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrg);
		
		if(strPayCycleDates!=null && strPayCycleDates.length > 0){
			String strD1 = strPayCycleDates[0];
			String strD2 = strPayCycleDates[1];
			String strPC = strPayCycleDates[2];
			
			int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
		
			Map hmEmpMertoMap = new HashMap();
			Map hmEmpWlocationMap = new HashMap();
			Map hmEmpStateMap = new HashMap();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			
			String strStateId = (String)hmEmpStateMap.get(""+intEmpIdReq);
			String strEmpGender = CF.getEmpGender(con, uF, ""+intEmpIdReq);
			
//			Map<String, String> hmEmpServiceTaxMap = CF.getEmpServiceTax(con, uF, CF);
			
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmHRAExemption = new HashMap();
			while(rs.next()){
				hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
				hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
				hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
				hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
				
				hmOtherTaxDetails.put(rs.getString("state_id")+"_MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_REBATE_AMOUNT", rs.getString("rebate_amt"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));
				
				hmOtherTaxDetails.put(rs.getString("state_id")+"_CGST", rs.getString("cgst"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SGST", rs.getString("sgst"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			double dblInvestmentExemption = 0.0d;
			if (rs.next()) {
				dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from annual_variable_details where level_id=? and org_id=? " +
					"and financial_year_start=? and financial_year_end=? and emp_id=? and salary_head_id in (select salary_head_id from salary_details " +
					"where is_annual_variable=true and (is_delete is null or is_delete = false) and (is_contribution is null or is_contribution=false))");
			pst.setInt(1, uF.parseToInt(levelId));
			pst.setInt(2, uF.parseToInt(strOrg));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, intEmpIdReq);
			rs = pst.executeQuery();
			Map<String, String> hmAnnualVariableAmt = new HashMap<String, String>();
			while(rs.next()){
				hmAnnualVariableAmt.put(rs.getString("salary_head_id"), rs.getString("variable_amount"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmAnnualVariableAmt", hmAnnualVariableAmt);
			
			pst = con.prepareStatement("select * from annual_variable_details where level_id=? and org_id=? " +
					"and financial_year_start=? and financial_year_end=? and emp_id=? and salary_head_id in (select salary_head_id from salary_details " +
					"where is_contribution=true and (is_delete is null or is_delete = false))");
			pst.setInt(1, uF.parseToInt(levelId));
			pst.setInt(2, uF.parseToInt(strOrg));
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setInt(5, intEmpIdReq);
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			Map<String, String> hmContributionSalHeadAmt = new HashMap<String, String>();
			while(rs.next()){
				hmContributionSalHeadAmt.put(rs.getString("salary_head_id"), rs.getString("variable_amount"));
			}
			rs.close();
			pst.close();
			request.setAttribute("hmContributionSalHeadAmt", hmContributionSalHeadAmt);
			
			Map<String, String> hmSalaryDetails = new HashMap<String, String>();
			List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
			List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
			pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+","+CTC+") and org_id=? and level_id=? order by earning_deduction desc, salary_head_id, weight");
			pst.setInt(1, uF.parseToInt(strOrg));
			pst.setInt(2, uF.parseToInt(levelId)); 
//			System.out.println("pst==>"+pst);
			rs = pst.executeQuery();  
			List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
			List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
			while(rs.next()){
				if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("E")){
					int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
					
					if(index>=0){
						alEmpSalaryDetailsEarning.remove(index);
						alEarningSalaryDuplicationTracer.remove(index);
						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
					}else{
						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
					}
					
					alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
				}else if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("D")){
					int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
					if(index>=0){
						alEmpSalaryDetailsDeduction.remove(index);
						alDeductionSalaryDuplicationTracer.remove(index);
						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
					}else{
						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
					}
					alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
				}
				
				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();				

			Map<String, Double> hmSalaryTotal = new LinkedHashMap<String, Double>();
			double grossAmount = 0.0d;
			double grossYearAmount = 0.0d;
			double deductAmount = 0.0d;
			double deductYearAmount = 0.0d;				
			
			ApprovePayroll objAP = new ApprovePayroll();
			objAP.CF = CF;
			objAP.session = session;
			objAP.request = request; 
			
//			Map<String, Map<String, String>> hmEmpPaidAmountDetails =  objAP.getEmpPaidAmountDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
//			Map<String, String> hmEmpExemptionsMap = objAP.getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
//			Map<String, String> hmEmpHomeLoanMap = objAP.getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
//			Map<String,String> hmFixedExemptions = objAP.getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
//			Map<String, String> hmEmpRentPaidMap = objAP.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
			
//			Map<String, String> hmPrevEmpTdsAmount  = new HashMap<String, String>();
//			Map<String, String> hmPrevEmpGrossAmount  = new HashMap<String, String>();
//			objAP.getPrevEmpTdsAmount(con,uF,strFinancialYearStart,strFinancialYearEnd,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount);
			
//			Map<String, String> hmEmpIncomeOtherSourcesMap = objAP.getEmpIncomeOtherSources(con, uF, strFinancialYearStart, strFinancialYearEnd);
			
			pst = con.prepareStatement("SELECT * FROM gratuity_details where org_id=? and effective_date<=? order by effective_date desc limit 1");
			pst.setInt(1, uF.parseToInt(strOrg));
			pst.setDate(2, uF.getCurrentDate(CF.getStrTimeZone()));
			rs = pst.executeQuery();
			Map<String, String> hmGratuityPolicy = new HashMap<String,String>();
			while(rs.next()){
				hmGratuityPolicy.put("SALARY_HEAD", rs.getString("salary_head_id"));
				hmGratuityPolicy.put("EFFECTIVE_DATE", uF.getDateFormat(rs.getString("effective_date"), DBDATE, DATE_FORMAT));
				hmGratuityPolicy.put("CALCULATE_PERCENT", rs.getString("calculate_percent"));
			}
			rs.close();
			pst.close();
//			System.out.println("hmGratuityPolicy ===>> " + hmGratuityPolicy);
			request.setAttribute("hmGratuityPolicy", hmGratuityPolicy);
			
			Map<String, String> hmPerkAlignAmount = (Map<String, String>) request.getAttribute("hmPerkAlignAmount");
			if(hmPerkAlignAmount == null) hmPerkAlignAmount = new HashMap<String, String>();
			Map<String, String> hmPerkAlignTDSAmount = (Map<String, String>) request.getAttribute("hmPerkAlignTDSAmount");
			if(hmPerkAlignTDSAmount == null) hmPerkAlignTDSAmount = new HashMap<String, String>();
			pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id," +
					"salary_head_id FROM emp_salary_details WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) " +
					"FROM emp_salary_details WHERE emp_id = ? and is_approved = true " +
					"and isdisplay=true and level_id = ?) AND effective_date <= ? and level_id = ? group by salary_head_id) a, " +
					"emp_salary_details esd WHERE a.emp_salary_id=esd.emp_salary_id " +
					"and a.salary_head_id=esd.salary_head_id and emp_id = ? " +
					"AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details " +
					"WHERE emp_id = ? and is_approved = true and isdisplay=true and level_id = ?) " +
					"AND effective_date <= ? and esd.level_id = ?) asd RIGHT JOIN salary_details sd " +
					"ON asd.salary_head_id = sd.salary_head_id WHERE sd.level_id = ? " +
					"and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) " +
					"order by sd.earning_deduction desc, weight");
			pst.setInt(1, intEmpIdReq);
			pst.setInt(2, intEmpIdReq);
			pst.setInt(3, uF.parseToInt(levelId));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt(levelId));
			pst.setInt(6, intEmpIdReq);
			pst.setInt(7, intEmpIdReq);
			pst.setInt(8, uF.parseToInt(levelId));
			pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(10, uF.parseToInt(levelId));
			pst.setInt(11, uF.parseToInt(levelId));
//			System.out.println("in level pst ===>> " + pst); 
			rs = pst.executeQuery();
//			List alSalaryDuplicationTracer = new ArrayList();
			List<List<String>> salaryHeadDetailsList = new ArrayList<List<String>>();
			Map<String, String> hmTotal = new HashMap<String, String>();
			double dblGrossTDS = 0.0d;
			boolean isEPF = false;
			boolean isESIC = false;
			boolean isLWF = false;
			List<List<String>> salaryAnnualVariableDetailsList = new ArrayList<List<String>>();
			List<List<String>> salaryContributionDetailsList = new ArrayList<List<String>>();
			while (rs.next()) {

				if(uF.parseToInt(rs.getString("salary_head_id")) == CTC){
					continue;
				}
				
				if(!uF.parseToBoolean(rs.getString("isdisplay"))){
					continue;
				}

				if(rs.getString("earning_deduction").equals("E")) {
					if(uF.parseToBoolean(rs.getString("is_contribution"))) {
						
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("salary_head_name"));
						innerList.add(rs.getString("earning_deduction"));
						
						if(uF.parseToBoolean(rs.getString("isdisplay"))) {
							double dblAmount = uF.parseToDouble(hmContributionSalHeadAmt.get(rs.getString("salary_head_id")));
							double dblYearAmount = dblAmount*12;
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_contribution"));
						} else {
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_contribution"));
						}
						salaryContributionDetailsList.add(innerList);
					
					} else if(!uF.parseToBoolean(rs.getString("is_variable")) && uF.parseToBoolean(rs.getString("is_annual_variable"))){
						
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("salary_head_name"));
						innerList.add(rs.getString("earning_deduction"));
						
						if(uF.parseToBoolean(rs.getString("isdisplay"))){
							double dblAmount = 0.0d;
							double dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
							
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
							innerList.add(rs.getString("salary_head_id"));
							
							grossAmount += dblAmount;
							grossYearAmount += dblYearAmount;
							
							if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							}
							
							hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount);
						} else {
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(rs.getString("salary_head_id"));
							hmTotal.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
						}
						salaryAnnualVariableDetailsList.add(innerList);
					
					} else {	
						List<String> innerList = new ArrayList<String>();
						innerList.add(rs.getString("salary_head_name"));
						innerList.add(rs.getString("earning_deduction"));
						
						if(uF.parseToBoolean(rs.getString("isdisplay"))){
							double dblAmount = 0.0d;
							double dblYearAmount = 0.0d;
							if(uF.parseToBoolean(rs.getString("is_variable")) && uF.parseToBoolean(rs.getString("is_annual_variable"))){
								dblAmount = 0.0d;
								dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
							} else {
								dblAmount = rs.getDouble("amount");
								if(hmPerkAlignAmount.containsKey(rs.getString("salary_head_id"))){
									dblAmount = uF.parseToDouble(hmPerkAlignAmount.get(rs.getString("salary_head_id")));
								}
								dblYearAmount = dblAmount * 12;
							}
							
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
							innerList.add(rs.getString("salary_head_id"));
							
							grossAmount += dblAmount;
							grossYearAmount += dblYearAmount;
							
							if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
								dblGrossTDS += dblAmount;
							}
							
							hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount);
						} else {
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(rs.getString("salary_head_id"));
							hmTotal.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
						}
						salaryHeadDetailsList.add(innerList);
					}
				} else if(rs.getString("earning_deduction").equals("D")) {
					List<String> innerList = new ArrayList<String>();
					innerList.add(rs.getString("salary_head_name"));
					innerList.add(rs.getString("earning_deduction"));
					if(uF.parseToBoolean(rs.getString("is_contribution"))) {
						
						if(uF.parseToBoolean(rs.getString("isdisplay"))) {
							double dblYearAmount = uF.parseToDouble(hmAnnualVariableAmt.get(rs.getString("salary_head_id")));
							double dblAmount = dblYearAmount/12;
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_contribution"));
						} else {
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(rs.getString("salary_head_id"));
							innerList.add(rs.getString("is_contribution"));
						}
						salaryContributionDetailsList.add(innerList);
					
					} else {
						if(uF.parseToBoolean(rs.getString("isdisplay"))){
	//						int nPayMonth = uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "MM"));
							switch(rs.getInt("salary_head_id")){
														
								case PROFESSIONAL_TAX :
									  
									double dblAmount = calculateProfessionalTax(con, uF, grossAmount,strFinancialYearStart, strFinancialYearEnd, nPayMonth, strStateId,strEmpGender);
									double dblYearAmount =  getAnnualProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, strStateId, strEmpGender);
									
									deductAmount += dblAmount;
									deductYearAmount += dblYearAmount;
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
									innerList.add(rs.getString("salary_head_id"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount);
									
									break;
								
								case EMPLOYEE_EPF :
									isEPF = true;	
									double dblAmount1 = objAP.calculateEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmTotal, ""+intEmpIdReq, null, null, false, null);
									double dblYearAmount1 = dblAmount1 * 12;
									
									deductAmount += dblAmount1;
									deductYearAmount += dblYearAmount1;
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount1));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount1));
									innerList.add(rs.getString("salary_head_id"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount1);
									
									break;
								
								case EMPLOYEE_ESI :
									isESIC = true;
									double dblAmount4 = objAP.calculateEEESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, ""+intEmpIdReq);
									dblAmount4 = Math.ceil(dblAmount4);
									
									double dblYearAmount4 = dblAmount4 * 12;
									dblYearAmount4 = Math.ceil(dblYearAmount4);
									
									deductAmount += dblAmount4;
									deductYearAmount += dblYearAmount4;
	//								System.out.println("dblAmount4====>"+dblAmount4);
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount4));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount4));
									innerList.add(rs.getString("salary_head_id"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount4);
									
									break;
								
								case EMPLOYEE_LWF :
									isLWF = true;
									double dblAmount6 = objAP.calculateEELWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, ""+intEmpIdReq, nPayMonth, strOrg);
									double dblYearAmount6 = dblAmount6 * 12;
									
									deductAmount += dblAmount6;
									deductYearAmount += dblYearAmount6;
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount6));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount6));
									innerList.add(rs.getString("salary_head_id"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount6);
									
									break;
								
								case TDS :
									
									/*double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
									double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
									double dblHRA = uF.parseToDouble(hmTotal.get(HRA+""));
									
									String[] hraSalaryHeads = null;
									if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null){
										hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
									}
									
									double dblHraSalHeadsAmount = 0;
									for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++){
										dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotal.get(hraSalaryHeads[i]));
									}
									
									Map<String, String> hmPaidSalaryDetails =  hmEmpPaidAmountDetails.get(""+intEmpIdReq);
									if(hmPaidSalaryDetails==null){hmPaidSalaryDetails=new HashMap<String, String>();}
									
									double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(""+intEmpIdReq)+"_EDU_TAX"));
									double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(""+intEmpIdReq)+"_STD_TAX"));
									double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(""+intEmpIdReq)+"_FLAT_TDS"));
									 
									if(hmEmpServiceTaxMap.containsKey(""+intEmpIdReq)){
										dblGrossTDS = grossAmount;
										double  dblServiceTaxAmount = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
										dblGrossTDS = dblGrossTDS - dblServiceTaxAmount;
										
										double dblSwachhaBharatCess = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS+""));
										dblGrossTDS = dblGrossTDS - dblSwachhaBharatCess;
										
										double dblKrishiKalyanCess = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS+""));
										dblGrossTDS = dblGrossTDS - dblKrishiKalyanCess;
									}
									
									double dblAmount7 = objAP.calculateTDS(con, uF,strD2,strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
											nPayMonth,
											strD1, strFinancialYearStart, strFinancialYearEnd, ""+intEmpIdReq, hmEmpGenderMap.get(""+intEmpIdReq),  hmEmpAgeMap.get(""+intEmpIdReq), strStateId,
											hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails,
											hmTotal, hmSalaryDetails, hmEmpLevelMap, CF,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount,hmEmpIncomeOtherSourcesMap,hmOtherTaxDetails,hmEmpStateMap);
	//								dblAmount7 = Math.round(dblAmount7);
									double dblYearAmount7 = dblAmount7 * 12;
									
									deductAmount += dblAmount7;
									deductYearAmount += dblYearAmount7;
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount7));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount7));
									innerList.add(rs.getString("salary_head_id"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount7);*/
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
									innerList.add(rs.getString("salary_head_id"));
									hmTotal.put(rs.getString("salary_head_id"), ""+0.0d);
									
									break;
								
								default:
									
									double dblAmount9 = uF.parseToDouble(rs.getString("amount"));
									double dblYearAmount9 = dblAmount9 * 12;
									
									deductAmount += dblAmount9;
									deductYearAmount += dblYearAmount9;
									
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount9));
									innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount9));
									innerList.add(rs.getString("salary_head_id"));
									hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount9);
									
									break;
							}
						}  else {
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
							innerList.add(rs.getString("salary_head_id"));
							hmTotal.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
						}
						salaryHeadDetailsList.add(innerList);
					}
				}
				
			}
			rs.close();
			pst.close();

			hmSalaryTotal.put("GROSS_AMOUNT", grossAmount);
			hmSalaryTotal.put("GROSS_YEAR_AMOUNT", grossYearAmount);
			hmSalaryTotal.put("DEDUCT_AMOUNT", deductAmount);
			hmSalaryTotal.put("DEDUCT_YEAR_AMOUNT", deductYearAmount);
			
			request.setAttribute("hmSalaryTotal", hmSalaryTotal);
			request.setAttribute("salaryHeadDetailsList", salaryHeadDetailsList);
			request.setAttribute("salaryAnnualVariableDetailsList", salaryAnnualVariableDetailsList);
			
//			System.out.println("salaryHeadDetailsList in getSalaryHeadsforEmployee=====>"+salaryHeadDetailsList); 
//			System.out.println("salaryAnnualVariableDetailsList======>"+salaryAnnualVariableDetailsList);
			/**
			 * Employer Contribution
			 * */ 
			Map<String,String> hmContribution = new HashMap<String, String>();
			if(isEPF){
//				double dblAmount = objAP.calculateERPFandEPS(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, ""+intEmpIdReq, null, null, false, null);
				double dblAmount = objAP.calculateERPF(con,CF, null, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, ""+intEmpIdReq, null, null, false, null);
//				dblAmount = Math.round(dblAmount);
				double dblYearAmount = dblAmount * 12;
				hmContribution.put("EPF_MONTHLY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
				hmContribution.put("EPF_ANNUALY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
			}
			if(isESIC){
				double dblAmount = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,""+intEmpIdReq, null, null);
				dblAmount = Math.ceil(dblAmount);
				double dblYearAmount = dblAmount * 12;
				dblYearAmount = Math.ceil(dblYearAmount);
				
				hmContribution.put("ESI_MONTHLY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
				hmContribution.put("ESI_ANNUALY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
			}
			if(isLWF){
				double dblAmount = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth, strOrg);
//				dblAmount = Math.round(dblAmount); 
				double dblYearAmount = dblAmount * 12;
				hmContribution.put("LWF_MONTHLY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
				hmContribution.put("LWF_ANNUALY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));				
			}
			request.setAttribute("isEPF", ""+isEPF);
			request.setAttribute("isESIC", ""+isESIC);
			request.setAttribute("isLWF", ""+isLWF);
			request.setAttribute("hmContribution", hmContribution);
			
			/**
			 * Employer Contribution End
			 * */ 
			
			
			pst = con.prepareStatement("select amount from payroll_generation where emp_id = ? and salary_head_id = ? and financial_year_from_date=? and financial_year_to_date=?");
			pst.setInt(1, intEmpIdReq);
			pst.setInt(2, PROFESSIONAL_TAX);
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			double dblAmount = 0;
			int nMonthCount = 0;
			while(rs.next()){
				dblAmount += uF.parseToDouble(rs.getString("amount"));
				nMonthCount++;
			}
			rs.close();
			pst.close(); 
			
			int nTotalCount = 12;
			if(((String)hmEmpProfile.get("JOINING_DATE")) != null && !((String)hmEmpProfile.get("JOINING_DATE")).trim().equals("") && !((String)hmEmpProfile.get("JOINING_DATE")).trim().equalsIgnoreCase("NULL")){
				java.util.Date dtJoiningDt = uF.getDateFormatUtil((String)hmEmpProfile.get("JOINING_DATE"), CF.getStrReportDateFormat());
				java.util.Date dtFinancialYearStartDt = uF.getDateFormatUtil(strFinancialYearStart, DATE_FORMAT);
				java.util.Date dtFinancialYearEndDt = uF.getDateFormatUtil(strFinancialYearEnd, DATE_FORMAT);
				if(dtJoiningDt!=null && dtJoiningDt.before(dtFinancialYearStartDt)) {
					nTotalCount = 12;
				} else if(dtJoiningDt != null) {
					int m1 = dtJoiningDt.getYear() * 12 + dtJoiningDt.getMonth();
				    int m2 = dtFinancialYearEndDt.getYear() * 12 + dtFinancialYearEndDt.getMonth();
				    nTotalCount = m2 - m1 + 1;
				}
			}
			int nRemainingCount = nTotalCount - nMonthCount;
			
			
			pst = con.prepareStatement("select amount from emp_salary_details where emp_id = ? " +
					"and earning_deduction = ? and is_approved =true " +
					"and effective_date = (select max(effective_date) from emp_salary_details " +
					"where emp_id = ? and earning_deduction = ? and is_approved = true " +
					"and level_id=?) and level_id=?");
			pst.setInt(1, intEmpIdReq);
			pst.setString(2, "E");
			pst.setInt(3, intEmpIdReq);
			pst.setString(4, "E");
			pst.setInt(5, uF.parseToInt(levelId));
			pst.setInt(6, uF.parseToInt(levelId));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			double dblGrossAmount = 0;
			while(rs.next()){
				dblGrossAmount += uF.parseToDouble(rs.getString("amount"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? " +
					"and financial_year_from=? and financial_year_to=? and gender =? limit 1");
			pst.setDouble(1, dblGrossAmount);
			pst.setDouble(2, dblGrossAmount);
			pst.setInt(3, uF.parseToInt((String)hmEmpStateMap.get(intEmpIdReq+"")));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(6, strEmpGender);
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			double dblDeductionAmount = 0;
			while(rs.next()) {
				dblDeductionAmount = rs.getDouble("deduction_amount");
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			String strCurrency = "";
			if(uF.parseToInt(hmEmpCurrency.get(intEmpIdReq+"")) > 0){
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(intEmpIdReq+""));
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			} 
			
			
			ApprovePayroll objAppPayroll = new ApprovePayroll();
			objAppPayroll.session = session;
			objAppPayroll.request = request;
			objAppPayroll.CF = CF;
			
			double dblMonthlyAmount = objAppPayroll.calculateProfessionalTax(con, uF, null, dblGrossAmount, strFinancialYearStart, strFinancialYearEnd, 6, (String)hmEmpStateMap.get(intEmpIdReq+""), strEmpGender);
			double dblVar = dblDeductionAmount - (dblMonthlyAmount * 12);
			dblAmount = dblAmount + (dblMonthlyAmount * nRemainingCount) + dblVar;
			
			request.setAttribute("dblAmount", strCurrency+uF.formatIntoOneDecimal(dblAmount));
			request.setAttribute("dblMonthlyAmount", strCurrency+uF.formatIntoOneDecimal(dblMonthlyAmount));
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rs != null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(pst != null){
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}

	public void getSalaryHeadsforEmployeeByGrade(Connection con, UtilityFunctions uF, int intEmpIdReq, Map<String, String> hmEmpProfile) {

//	System.out.println("in getSalaryHeadsforEmployeeByGrade****");
	
	PreparedStatement pst = null;
	ResultSet rs = null;
	try {
		
		String[] strFinancialYearDates = new FillFinancialYears(request).fillLatestFinancialYears();
		String strFinancialYearStart = strFinancialYearDates[0];
		String strFinancialYearEnd = strFinancialYearDates[1];

//		Map<String, String> hmEmpGradeMap = CF.getEmpGradeMap(con); 
//		String gradeId = hmEmpGradeMap.get(getEmpId());
		String gradeId = CF.getEmpGradeId(con, ""+intEmpIdReq);
		
		String strOrg = CF.getEmpOrgId(con, uF, ""+intEmpIdReq);
		
		String currId = CF.getOrgCurrencyIdByOrg(con, strOrg);
		Map<String, Map<String, String>> hmCurrencyDetailsMap =  CF.getCurrencyDetails(con);
		if(hmCurrencyDetailsMap == null) hmCurrencyDetailsMap = new HashMap<String, Map<String,String>>();
		Map<String, String> hmCurr = hmCurrencyDetailsMap.get(currId);
		if(hmCurr == null) hmCurr = new HashMap<String, String>();
		String strCurr = hmCurr.get("LONG_CURR")!=null && !hmCurr.get("LONG_CURR").equalsIgnoreCase("null") ? hmCurr.get("LONG_CURR")+" " : "";
		request.setAttribute("strCurr", strCurr);

//		Map<String, String> hmEmpGenderMap = CF.getEmpGenderMap(con);
//		Map<String, String> hmEmpAgeMap = CF.getEmpAgeMap(con,CF);
//		Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
		
		String currDate = uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
		String[] strPayCycleDates = CF.getPayCycleFromDate(con, currDate, CF.getStrTimeZone(), CF, strOrg);
		
		if(strPayCycleDates!=null && strPayCycleDates.length > 0){
			String strD1 = strPayCycleDates[0];
			String strD2 = strPayCycleDates[1];
			String strPC = strPayCycleDates[2];
			
			int nPayMonth = uF.parseToInt(uF.getDateFormat(strD2, DATE_FORMAT, "MM"));
			
			Map hmEmpMertoMap = new HashMap();
			Map hmEmpWlocationMap = new HashMap();
			Map hmEmpStateMap = new HashMap();
			CF.getEmpWlocationMap(con, hmEmpStateMap, hmEmpWlocationMap, hmEmpMertoMap);
			
			String strStateId = (String)hmEmpStateMap.get(""+intEmpIdReq);
			String strEmpGender = CF.getEmpGender(con, uF, ""+intEmpIdReq);
			
//			Map<String, String> hmEmpServiceTaxMap = CF.getEmpServiceTax(con, uF, CF);
			
			pst = con.prepareStatement("select * from hra_exemption_details where financial_year_from=? and financial_year_to=?");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			Map hmHRAExemption = new HashMap();
			while(rs.next()){
				hmHRAExemption.put("CONDITION_1", rs.getString("condition1"));
				hmHRAExemption.put("CONDITION_2", rs.getString("condition2"));
				hmHRAExemption.put("CONDITION_3", rs.getString("condition3"));
				hmHRAExemption.put("SALARY_HEAD_ID", rs.getString("salary_head_id"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmOtherTaxDetails = new HashMap<String, String>();
			pst = con.prepareStatement("select * from deduction_tax_misc_details where financial_year_from = ? and financial_year_to = ? and trail_status = 1");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
			while(rs.next()){
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SERVICE_TAX", rs.getString("service_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_EDU_TAX", rs.getString("education_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_STD_TAX", rs.getString("standard_tax"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_FLAT_TDS", rs.getString("flat_tds"));
				
				hmOtherTaxDetails.put(rs.getString("state_id")+"_MAX_TAX_INCOME", rs.getString("max_net_tax_income"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_REBATE_AMOUNT", rs.getString("rebate_amt"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SWACHHA_BHARAT_CESS", rs.getString("swachha_bharat_cess"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_KRISHI_KALYAN_CESS", rs.getString("krishi_kalyan_cess"));
				
				hmOtherTaxDetails.put(rs.getString("state_id")+"_CGST", rs.getString("cgst"));
				hmOtherTaxDetails.put(rs.getString("state_id")+"_SGST", rs.getString("sgst"));
				
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("SELECT * FROM section_details where financial_year_start=? and financial_year_end=? and section_id=3 order by section_code");
			pst.setDate(1, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			rs = pst.executeQuery();
//			System.out.println(" pst==>"+pst);
			double dblInvestmentExemption = 0.0d;
			if (rs.next()) {
				dblInvestmentExemption = uF.parseToDouble(rs.getString("section_exemption_limit"));
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmSalaryDetails = new HashMap<String, String>();
			List<String> alEmpSalaryDetailsEarning = new ArrayList<String>();
			List<String> alEmpSalaryDetailsDeduction = new ArrayList<String>();
			pst = con.prepareStatement("select * from salary_details where salary_head_id not in ("+GROSS+","+CTC+","+REIMBURSEMENT_CTC+")" +
				" and grade_id =? order by earning_deduction desc, salary_head_id, weight");
			pst.setInt(1, uF.parseToInt(gradeId));
			rs = pst.executeQuery();  
			List<String> alEarningSalaryDuplicationTracer = new ArrayList<String>();
			List<String> alDeductionSalaryDuplicationTracer = new ArrayList<String>();
			while(rs.next()){
				
				if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("E")){
					int index = alEarningSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
					
					if(index>=0){
						alEmpSalaryDetailsEarning.remove(index);
						alEarningSalaryDuplicationTracer.remove(index);
						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
					}else{
						alEmpSalaryDetailsEarning.add(rs.getString("salary_head_id"));
					}
					
					alEarningSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
				}else if(rs.getString("earning_deduction")!=null && rs.getString("earning_deduction").equalsIgnoreCase("D")){
					int index = alDeductionSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
					if(index>=0){
						alEmpSalaryDetailsDeduction.remove(index);
						alDeductionSalaryDuplicationTracer.remove(index);
						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
					}else{
						alEmpSalaryDetailsDeduction.add(rs.getString("salary_head_id"));
					}
					alDeductionSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
				}
				
				hmSalaryDetails.put(rs.getString("salary_head_id"), rs.getString("salary_head_name"));
			}
			rs.close();
			pst.close();
			

			Map<String, Double> hmSalaryTotal = new LinkedHashMap<String, Double>();
			double grossAmount = 0.0d;
			double grossYearAmount = 0.0d;
			double deductAmount = 0.0d;
			double deductYearAmount = 0.0d;
			
			ApprovePayroll objAP = new ApprovePayroll();
			objAP.CF = CF;
			objAP.session = session;
			objAP.request = request; 
			
//			Map<String, Map<String, String>> hmEmpPaidAmountDetails =  objAP.getEmpPaidAmountDetails(con, uF, strFinancialYearStart, strFinancialYearEnd);
//			Map<String, String> hmEmpExemptionsMap = objAP.getEmpInvestmentExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd, dblInvestmentExemption);
//			Map<String, String> hmEmpHomeLoanMap = objAP.getEmpHomeLoanExemptions(con, uF, strFinancialYearStart, strFinancialYearEnd);
//			Map<String,String> hmFixedExemptions = objAP.getFixedExemption(con, uF, strFinancialYearStart, strFinancialYearEnd);
//			Map<String, String> hmEmpRentPaidMap = objAP.getEmpRentPaid(con, uF, strFinancialYearStart, strFinancialYearEnd);
			
//			Map<String, String> hmPrevEmpTdsAmount  = new HashMap<String, String>();
//			Map<String, String> hmPrevEmpGrossAmount  = new HashMap<String, String>();
//			objAP.getPrevEmpTdsAmount(con,uF,strFinancialYearStart,strFinancialYearEnd,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount);
			
//			Map<String, String> hmEmpIncomeOtherSourcesMap = objAP.getEmpIncomeOtherSources(con, uF, strFinancialYearStart, strFinancialYearEnd);
		
			pst = con.prepareStatement("select * from vda_rate_details where desig_id = ? and from_date = (select max(from_date) as from_date from vda_rate_details where desig_id = ? and from_date <=?)");
			pst.setInt(1, uF.parseToInt(hmEmpProfile.get("DESIGNATION_ID")));
			pst.setInt(2, uF.parseToInt(hmEmpProfile.get("DESIGNATION_ID")));
			pst.setDate(3, uF.getCurrentDate(CF.getStrTimeZone()));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			double dblVDAAmount = 0.0d;
			while(rs.next()) {
				if(hmEmpProfile.get("EMPLOYMENT_TYPE")!=null && hmEmpProfile.get("EMPLOYMENT_TYPE").equalsIgnoreCase("PROBATION")) {
					dblVDAAmount = rs.getDouble("vda_amount_probation");
				} else if(hmEmpProfile.get("EMPLOYMENT_TYPE")!=null && hmEmpProfile.get("EMPLOYMENT_TYPE").equalsIgnoreCase("PERMANENT")) {
					dblVDAAmount = rs.getDouble("vda_amount_permanent");
				} else if(hmEmpProfile.get("EMPLOYMENT_TYPE")!=null && hmEmpProfile.get("EMPLOYMENT_TYPE").equalsIgnoreCase("TEMPORARY")) {
					dblVDAAmount = rs.getDouble("vda_amount_temporary");
				}
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmPerkAlignAmount = (Map<String, String>) request.getAttribute("hmPerkAlignAmount");
			if(hmPerkAlignAmount == null) hmPerkAlignAmount = new HashMap<String, String>();
			
			pst = con.prepareStatement("SELECT * FROM (select esd.* from (SELECT max(emp_salary_id) as emp_salary_id,salary_head_id FROM emp_salary_details " +
				"WHERE emp_id = ? AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? and is_approved = true " +
				"and isdisplay=true and grade_id = ?) AND effective_date <= ? and grade_id = ? group by salary_head_id) a, emp_salary_details esd " +
				"WHERE a.emp_salary_id=esd.emp_salary_id and a.salary_head_id=esd.salary_head_id and emp_id = ? " +
				"AND effective_date = (SELECT MAX(effective_date) FROM emp_salary_details WHERE emp_id = ? " +
				"and is_approved = true and isdisplay=true and grade_id = ?) AND esd.effective_date <= ? " +
				"and esd.grade_id = ?) asd RIGHT JOIN salary_details sd ON asd.salary_head_id = sd.salary_head_id " +
				"WHERE sd.grade_id = ? and asd.salary_head_id not in("+GROSS+") and (is_delete is null or is_delete=false) " +
				"order by sd.earning_deduction desc, weight");
			pst.setInt(1, intEmpIdReq);
			pst.setInt(2, intEmpIdReq);
			pst.setInt(3, uF.parseToInt(gradeId));
			pst.setDate(4, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(5, uF.parseToInt(gradeId));
			pst.setInt(6, intEmpIdReq);
			pst.setInt(7, intEmpIdReq);
			pst.setInt(8, uF.parseToInt(gradeId));
			pst.setDate(9, uF.getCurrentDate(CF.getStrTimeZone()));
			pst.setInt(10, uF.parseToInt(gradeId));
			pst.setInt(11, uF.parseToInt(gradeId));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			List alSalaryDuplicationTracer = new ArrayList();
			List<List<String>> salaryHeadDetailsList = new ArrayList<List<String>>();
			
			Map<String, String> hmTotal = new HashMap<String, String>();
			double dblGrossTDS = 0.0d;
			boolean isEPF = false;
			boolean isESIC = false;
			boolean isLWF = false;
			while (rs.next()) {

				if(uF.parseToInt(rs.getString("salary_head_id")) == CTC){
					continue;
				}
				
				if(!uF.parseToBoolean(rs.getString("isdisplay"))){
					continue;
				}
				
				List<String> innerList = new ArrayList<String>();
				innerList.add(rs.getString("salary_head_name"));
				innerList.add(rs.getString("earning_deduction"));
//				innerList.add(uF.parseToBoolean(rs.getString("isdisplay"))? rs.getString("amount") : "0");
//				double dblYearAmount = (uF.parseToBoolean(rs.getString("isdisplay")) ? rs.getDouble("amount") : 0.0d )* 12;
//				innerList.add(""+uF.formatIntoOneDecimalWithOutComma(dblYearAmount));

				if(rs.getString("earning_deduction").equals("E")) {
					if(uF.parseToBoolean(rs.getString("isdisplay"))) {
						double dblAmount = rs.getDouble("amount");
						if(hmPerkAlignAmount.containsKey(rs.getString("salary_head_id"))){
							dblAmount = uF.parseToDouble(hmPerkAlignAmount.get(rs.getString("salary_head_id")));
						}
						
						if(rs.getInt("salary_head_id") == VDA && !uF.parseToBoolean(hmEmpProfile.get("EMP_IS_DISABLE_SAL_CALCULATE"))) {
							dblAmount = dblVDAAmount;
						}
						double dblYearAmount = dblAmount * 12;
						
						innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
						innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
						innerList.add(rs.getString("salary_head_id"));
						
						grossAmount += dblAmount;
						grossYearAmount += dblYearAmount;
						
						if(uF.parseToInt(rs.getString("salary_head_id")) != REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != TRAVEL_REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != MOBILE_REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						} else if(uF.parseToInt(rs.getString("salary_head_id")) != OTHER_REIMBURSEMENT){
							dblGrossTDS += dblAmount;
						}
						
						hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount);
					} else {
						innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
						innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
						innerList.add(rs.getString("salary_head_id"));
						hmTotal.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
					}
				} else if(rs.getString("earning_deduction").equals("D")) {
					if(uF.parseToBoolean(rs.getString("isdisplay"))){
//						int nPayMonth = uF.parseToInt(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, "MM"));
//						System.out.println("sal Head Id ===>> " + rs.getInt("salary_head_id"));
						switch(rs.getInt("salary_head_id")) {
													
							case PROFESSIONAL_TAX :
								  
								double dblAmount = calculateProfessionalTax(con, uF, grossAmount,strFinancialYearStart, strFinancialYearEnd, nPayMonth, strStateId,strEmpGender);
								double dblYearAmount =  getAnnualProfessionalTax(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, strStateId, strEmpGender);
								
								deductAmount += dblAmount;
								deductYearAmount += dblYearAmount;
								
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
								innerList.add(rs.getString("salary_head_id"));
								hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount);
								
								break;
							
							case EMPLOYEE_EPF :
								isEPF = true;	
								double dblAmount1 = objAP.calculateEEPF(con, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, hmTotal, ""+intEmpIdReq, null, null, false, null);
								double dblYearAmount1 = dblAmount1 * 12;
								
								deductAmount += dblAmount1;
								deductYearAmount += dblYearAmount1;
								
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount1));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount1));
								innerList.add(rs.getString("salary_head_id"));
								hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount1);
								
								break;
							
//							case EMPLOYER_EPF :
//								
//								double dblAmount2 = objAP.calculateERPF(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, ""+intEmpIdReq, null, null, false, null);
//								double dblYearAmount2 = dblAmount2 * 12;
//								
//								deductAmount += dblAmount2;
//								deductYearAmount += dblYearAmount2;
//								
//								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount2));
//								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount2));
//								innerList.add(rs.getString("salary_head_id"));
//								hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount2);
//								
//								break;  
							
//							case EMPLOYER_ESI :
//								
//								double dblAmount3 = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,""+intEmpIdReq);
//								double dblYearAmount3 = dblAmount3 * 12;
//								
//								deductAmount += dblAmount3;
//								deductYearAmount += dblYearAmount3;
//								
//								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount3));
//								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount3));
//								innerList.add(rs.getString("salary_head_id"));
//								hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount3);
//								
//								break;
							
							case EMPLOYEE_ESI :
								isESIC = true;
//								System.out.println("in EMPLOYEE_ESI ========>> ");
								double dblAmount4 = objAP.calculateEEESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, ""+intEmpIdReq);
								dblAmount4 = Math.ceil(dblAmount4);
								
//								System.out.println("dblAmount4 ===>> " + dblAmount4);
								double dblYearAmount4 = dblAmount4 * 12;
								dblYearAmount4 = Math.ceil(dblYearAmount4);
								
								deductAmount += dblAmount4;
								deductYearAmount += dblYearAmount4;
								
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount4));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount4));
								innerList.add(rs.getString("salary_head_id"));
								hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount4);
								
								break;
							
//							case EMPLOYER_LWF :
//								
//								double dblAmount5 = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth);
//								double dblYearAmount5 = dblAmount5 * 12;
//								
//								deductAmount += dblAmount5;
//								deductYearAmount += dblYearAmount5;
//								
//								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount5));
//								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount5));
//								innerList.add(rs.getString("salary_head_id"));
//								hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount5);
//								
//								break;
							
							case EMPLOYEE_LWF :
								isLWF = true;
								double dblAmount6 = objAP.calculateEELWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, null, ""+intEmpIdReq, nPayMonth, strOrg);
								double dblYearAmount6 = dblAmount6 * 12;
								
								deductAmount += dblAmount6;
								deductYearAmount += dblYearAmount6;
								
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount6));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount6));
								innerList.add(rs.getString("salary_head_id"));
								hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount6);
								
								break;
							
							case TDS :
								
								/*double dblBasic = uF.parseToDouble(hmTotal.get(BASIC+""));
								double dblDA = uF.parseToDouble(hmTotal.get(DA+""));
								double dblHRA = uF.parseToDouble(hmTotal.get(HRA+""));
								
								String[] hraSalaryHeads = null;
								if(((String)hmHRAExemption.get("SALARY_HEAD_ID"))!=null){
									hraSalaryHeads = ((String)hmHRAExemption.get("SALARY_HEAD_ID")).split(",");
								}
								
								double dblHraSalHeadsAmount = 0;
								for(int i=0; hraSalaryHeads!=null && i<hraSalaryHeads.length; i++){
									dblHraSalHeadsAmount += uF.parseToDouble((String)hmTotal.get(hraSalaryHeads[i]));
								}
								
								Map<String, String> hmPaidSalaryDetails =  hmEmpPaidAmountDetails.get(""+intEmpIdReq);
								if(hmPaidSalaryDetails==null){hmPaidSalaryDetails=new HashMap<String, String>();}
								
								double dblCess1 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(""+intEmpIdReq)+"_EDU_TAX"));
								double dblCess2 = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(""+intEmpIdReq)+"_STD_TAX"));
								double dblFlatTDS = uF.parseToDouble(hmOtherTaxDetails.get(hmEmpStateMap.get(""+intEmpIdReq)+"_FLAT_TDS"));
								 
								 
								if(hmEmpServiceTaxMap.containsKey(""+intEmpIdReq)){
									dblGrossTDS = grossAmount;
									double  dblServiceTaxAmount = uF.parseToDouble(hmTotal.get(SERVICE_TAX+""));
									dblGrossTDS = dblGrossTDS - dblServiceTaxAmount;
									
									double dblSwachhaBharatCess = uF.parseToDouble(hmTotal.get(SWACHHA_BHARAT_CESS+""));
									dblGrossTDS = dblGrossTDS - dblSwachhaBharatCess;
									
									double dblKrishiKalyanCess = uF.parseToDouble(hmTotal.get(KRISHI_KALYAN_CESS+""));
									dblGrossTDS = dblGrossTDS - dblKrishiKalyanCess;
								}
								
								double dblAmount7 = objAP.calculateTDS(con, uF,strD2,strD1, dblGrossTDS, dblCess1, dblCess2, dblFlatTDS, dblInvestmentExemption, dblHRA, dblHraSalHeadsAmount,
										nPayMonth,
										strD1, strFinancialYearStart, strFinancialYearEnd, ""+intEmpIdReq, hmEmpGenderMap.get(""+intEmpIdReq),  hmEmpAgeMap.get(""+intEmpIdReq), strStateId,
										hmEmpExemptionsMap, hmEmpHomeLoanMap, hmFixedExemptions, hmEmpMertoMap, hmEmpRentPaidMap, hmPaidSalaryDetails,
										hmTotal, hmSalaryDetails, hmEmpLevelMap, CF,hmPrevEmpTdsAmount,hmPrevEmpGrossAmount,hmEmpIncomeOtherSourcesMap,hmOtherTaxDetails,hmEmpStateMap);
								
								double dblYearAmount7 = dblAmount7 * 12;
								
								deductAmount += dblAmount7;
								deductYearAmount += dblYearAmount7;
								
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount7));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount7));
								innerList.add(rs.getString("salary_head_id"));
								hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount7);*/
								
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
								innerList.add(rs.getString("salary_head_id"));
								hmTotal.put(rs.getString("salary_head_id"), ""+0.0d);
								
								break;
							
							default:
								
								double dblAmount9 = uF.parseToDouble(rs.getString("amount"));
								double dblYearAmount9 = dblAmount9 * 12;
								
								deductAmount += dblAmount9;
								deductYearAmount += dblYearAmount9;
								
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount9));
								innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount9));
								innerList.add(rs.getString("salary_head_id"));
								hmTotal.put(rs.getString("salary_head_id"), ""+dblAmount9);
								
								break;
						}
					}  else {
						innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
						innerList.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
						innerList.add(rs.getString("salary_head_id"));
						hmTotal.put(rs.getString("salary_head_id"), uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),0.0d));
					}
				}
				
				int index = alSalaryDuplicationTracer.indexOf(rs.getString("salary_head_id"));
				
				if(index>=0){
					salaryHeadDetailsList.remove(index);
					salaryHeadDetailsList.add(index, innerList);
				}else{
					alSalaryDuplicationTracer.add(rs.getString("salary_head_id"));
					salaryHeadDetailsList.add(innerList);
				}
			}

			hmSalaryTotal.put("GROSS_AMOUNT", grossAmount);
			hmSalaryTotal.put("GROSS_YEAR_AMOUNT", grossYearAmount);
			hmSalaryTotal.put("DEDUCT_AMOUNT", deductAmount);
			hmSalaryTotal.put("DEDUCT_YEAR_AMOUNT", deductYearAmount);
			
			request.setAttribute("hmSalaryTotal", hmSalaryTotal);
			request.setAttribute("salaryHeadDetailsList", salaryHeadDetailsList);
		System.out.println("salaryHeadDetailsList in emp_gradewise======>"+salaryHeadDetailsList);
			
			
			/**
			 * Employer Contribution
			 * */
			Map<String,String> hmContribution = new HashMap<String, String>();
//			System.out.println("isEPF======>"+isEPF);
			if(isEPF){
//				double dblAmount = objAP.calculateERPFandEPS(con, CF, null, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, ""+intEmpIdReq, null, null, false, null);
				double dblAmount = objAP.calculateERPF(con,CF, null, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, ""+intEmpIdReq, null, null, false, null);
//				dblAmount = Math.round(dblAmount);
				double dblYearAmount = dblAmount * 12;
				hmContribution.put("EPF_MONTHLY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
				hmContribution.put("EPF_ANNUALY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
			}
			if(isESIC){
				double dblAmount = objAP.calculateERESI(con, uF, 0, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId,""+intEmpIdReq, null, null);
				dblAmount = Math.ceil(dblAmount);
				double dblYearAmount = dblAmount * 12;
				dblYearAmount = Math.ceil(dblYearAmount);
				
				hmContribution.put("ESI_MONTHLY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
				hmContribution.put("ESI_ANNUALY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));
			}
			if(isLWF){
				double dblAmount = objAP.calculateERLWF(con, uF, grossAmount, strFinancialYearStart, strFinancialYearEnd, hmTotal, strStateId, nPayMonth, strOrg);
//				dblAmount = Math.round(dblAmount);
				double dblYearAmount = dblAmount * 12;
				hmContribution.put("LWF_MONTHLY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblAmount));
				hmContribution.put("LWF_ANNUALY", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblYearAmount));				
			}
			request.setAttribute("isEPF", ""+isEPF);
			request.setAttribute("isESIC", ""+isESIC);
			request.setAttribute("isLWF", ""+isLWF);
			request.setAttribute("hmContribution", hmContribution);
			
			System.out.println("hmContribution in emp_gradewise"+hmContribution);
			
			pst = con.prepareStatement("select amount from payroll_generation where emp_id = ? and salary_head_id = ? and financial_year_from_date=? and financial_year_to_date=?");
			pst.setInt(1, intEmpIdReq);
			pst.setInt(2, PROFESSIONAL_TAX);
			pst.setDate(3, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			double dblAmount = 0;
			int nMonthCount = 0;
			while(rs.next()){
				dblAmount += uF.parseToDouble(rs.getString("amount"));
				nMonthCount++;
			}
			rs.close();
			pst.close(); 
			
			int nTotalCount = 12;
			
			if(((String)hmEmpProfile.get("JOINING_DATE")) != null && !((String)hmEmpProfile.get("JOINING_DATE")).trim().equals("") && !((String)hmEmpProfile.get("JOINING_DATE")).trim().equalsIgnoreCase("NULL")){
				java.util.Date dtJoiningDt = uF.getDateFormatUtil((String)hmEmpProfile.get("JOINING_DATE"), CF.getStrReportDateFormat());
				java.util.Date dtFinancialYearStartDt = uF.getDateFormatUtil(strFinancialYearStart, DATE_FORMAT);
				java.util.Date dtFinancialYearEndDt = uF.getDateFormatUtil(strFinancialYearEnd, DATE_FORMAT);
				if(dtJoiningDt!=null && dtJoiningDt.before(dtFinancialYearStartDt)) {
					nTotalCount = 12;
				} else if(dtJoiningDt != null) {
					int m1 = dtJoiningDt.getYear() * 12 + dtJoiningDt.getMonth();
				    int m2 = dtFinancialYearEndDt.getYear() * 12 + dtFinancialYearEndDt.getMonth();
				    nTotalCount = m2 - m1 + 1;
				}
			}
			int nRemainingCount = nTotalCount - nMonthCount;
			
			
			pst = con.prepareStatement("select amount from emp_salary_details where emp_id = ? " +
					"and earning_deduction = ? and is_approved =true " +
					"and effective_date = (select max(effective_date) from emp_salary_details " +
					"where emp_id = ? and earning_deduction = ? and is_approved = true and grade_id=?) and grade_id=?");
			pst.setInt(1, intEmpIdReq);
			pst.setString(2, "E");
			pst.setInt(3, intEmpIdReq);
			pst.setString(4, "E");
			pst.setInt(5, uF.parseToInt(gradeId));
			pst.setInt(6, uF.parseToInt(gradeId));
			rs = pst.executeQuery();
			double dblGrossAmount = 0;
			
			while(rs.next()){
				dblGrossAmount += uF.parseToDouble(rs.getString("amount"));
			}
			rs.close();
			pst.close();
			
			pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? " +
					"and financial_year_from=? and financial_year_to=? and gender =? limit 1");
			pst.setDouble(1, dblGrossAmount);
			pst.setDouble(2, dblGrossAmount);
			pst.setInt(3, uF.parseToInt((String)hmEmpStateMap.get(intEmpIdReq+"")));
			pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
			pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
			pst.setString(6, strEmpGender);
			rs = pst.executeQuery();				
//			System.out.println("pst ===>> " + pst);
			rs = pst.executeQuery();
			double dblDeductionAmount = 0;
			while(rs.next()) {
				dblDeductionAmount = rs.getDouble("deduction_amount");
			}
			rs.close();
			pst.close();
			
			Map<String, String> hmEmpCurrency = CF.getEmpCurrency(con);
			if(hmEmpCurrency == null) hmEmpCurrency = new HashMap<String, String>();
			String strCurrency = "";
			if(uF.parseToInt(hmEmpCurrency.get(intEmpIdReq+"")) > 0){
				Map<String, String> hmCurrency = hmCurrencyDetailsMap.get(hmEmpCurrency.get(intEmpIdReq+""));
				if(hmCurrency == null) hmCurrency = new HashMap<String, String>();
				strCurrency = uF.showData(hmCurrency.get("SHORT_CURR"), ""); 
			} 
			
			
			ApprovePayroll objAppPayroll = new ApprovePayroll();
			objAppPayroll.session = session;
			objAppPayroll.request = request;
			objAppPayroll.CF = CF;
			
			double dblMonthlyAmount = objAppPayroll.calculateProfessionalTax(con, uF, null, dblGrossAmount, strFinancialYearStart, strFinancialYearEnd, 6, (String)hmEmpStateMap.get(intEmpIdReq+""), strEmpGender);
			double dblVar = dblDeductionAmount - (dblMonthlyAmount * 12);
			dblAmount = dblAmount + (dblMonthlyAmount * nRemainingCount) + dblVar;
			
			request.setAttribute("dblAmount", strCurrency+uF.formatIntoOneDecimal(dblAmount));
			request.setAttribute("dblMonthlyAmount", strCurrency+uF.formatIntoOneDecimal(dblMonthlyAmount));
			
			
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rs != null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(pst != null){
			try {
				pst.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}

	
		private double calculateProfessionalTax(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd,
				int nPayMonth, String strStateId, String strEmpGender) {
			
			PreparedStatement pst = null;
			ResultSet rs = null;
			double dblAmount= 0;
			
			
			try {
				
				pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? " +
						"and financial_year_from=? and financial_year_to=? and gender =? limit 1");
				pst.setDouble(1, dblGross);
				pst.setDouble(2, dblGross);
				pst.setInt(3, uF.parseToInt(strStateId));
				pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(6, strEmpGender);
				rs = pst.executeQuery();  
				while(rs.next()){
					dblAmount = rs.getDouble("deduction_paycycle");
				}
				rs.close();
				pst.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(rs !=null){
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if(pst !=null){
					try {
						pst.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			return dblAmount;
		}	
		
		private double getAnnualProfessionalTax(Connection con, UtilityFunctions uF, double dblGross, String strFinancialYearStart, String strFinancialYearEnd, 
				String strStateId, String strEmpGender) {
			
			PreparedStatement pst = null;
			ResultSet rs = null;
			double dblDeductionAnnual= 0;
			
			try {
				
				pst = con.prepareStatement("select * from deduction_details_india where income_from<=? and income_to>=? and state_id=? " +
						"and financial_year_from=? and financial_year_to=? and gender =? limit 1");
				pst.setDouble(1, dblGross);
				pst.setDouble(2, dblGross);
				pst.setInt(3, uF.parseToInt(strStateId));
				pst.setDate(4, uF.getDateFormat(strFinancialYearStart, DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT));
				pst.setString(6, strEmpGender);			
				rs = pst.executeQuery();  
				while(rs.next()){
					dblDeductionAnnual = rs.getDouble("deduction_amount");
				}
				rs.close();
				pst.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if(rs !=null){
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
				if(pst !=null){
					try {
						pst.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
			return dblDeductionAnnual;
		}

	
//**********************code for roster********************

		
		
		/*public String viewRoster(UtilityFunctions uF) {
			
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(request);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;

				strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
				setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			
			
			int nselectedMonth = uF.parseToInt(getStrMonth());
			int nFYSMonth = uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "MM"));
			Calendar cal2 = GregorianCalendar.getInstance();
			cal2.set(Calendar.MONTH, uF.parseToInt(getStrMonth())-1);
			if(nselectedMonth>=nFYSMonth) {
				cal2.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "yyyy")));
			} else {
				cal2.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT, "yyyy")));
			}
			int nMonthStart = cal2.getActualMinimum(Calendar.DATE);
			int nMonthEnd = cal2.getActualMaximum(Calendar.DATE);
			
			String strDate1 =  nMonthStart+"/"+getStrMonth()+"/"+cal2.get(Calendar.YEAR);
			String strDate2 =  nMonthEnd+"/"+getStrMonth()+"/"+cal2.get(Calendar.YEAR);
			setPaycycle(strDate1 + "-" + strDate2);
			
			setStrYear(uF.parseToInt(uF.getDateFormat(strDate2, DATE_FORMAT, "yyyy"))+"");
			
			con = db.makeConnection(con);
			
			List<String> _alHolidays = new ArrayList<String>();
			Map<String, String> _hmHolidaysColour = new HashMap<String, String>();
			Map<String, Map<String, String>> hmServicesWorkrdFor = new HashMap<String, Map<String, String>>();
			Map<String, String> hmServices = CF.getServicesMap(con, true);
			Map<String, String> hmWLocation = CF.getEmpWlocationMap(con);
			
			List<List<String>> al = new ArrayList<List<String>>();
			List<String> alDay = new ArrayList<String>();
			List<String> alDate = new ArrayList<String>();
			List<String> alEmpId = new ArrayList<String>();

			List _alDay = new ArrayList();
			List _alDate = new ArrayList();

			String[] strDates = getPaycycle().split("-");
						
			
//			Map<String, String> hmWeekEnds = CF.getWeekEndList();
//			Map<String, String> hmWeekEnds = CF.getWeekEndDateList(con, strDates[0], strDates[1], CF, uF);
			Map<String, Set<String>> hmWeekEnds = CF.getWeekEndDateList(con, strDates[0], strDates[1], CF, uF,null,null);
		
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			if(hmEmpLevelMap==null)hmEmpLevelMap = new HashMap<String,String>();	
			Map<String, Set<String>> hmWeekEndHalfDates = new HashMap<String, Set<String>>();
			Map<String, Set<String>> hmWeekEndList = CF.getWeekEndDateList(con, strDates[0], strDates[1], CF, uF,hmWeekEndHalfDates,null);
			Map<String,String> hmEmpWlocation = CF.getEmpWlocationMap(con);
			List<String> alEmpCheckRosterWeektype = new ArrayList<String>();
			Map<String, Set<String>> hmRosterWeekEndDates = new HashMap<String, Set<String>>();
			CF.getEmpRosterWeekOffTypeByDate(con,uF,CF, strDates[0], strDates[1], alEmpCheckRosterWeektype,hmRosterWeekEndDates,hmWeekEndList,hmEmpLevelMap,hmEmpWlocation,hmWeekEndHalfDates);
			
			
			pst = con.prepareStatement(selectDates);
			pst.setDate(1, uF.getDateFormat(strDates[0], DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strDates[1], DATE_FORMAT));
			rs = pst.executeQuery();
			int i=0;
			while(rs.next()) {
				
				_alDay.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDayFormat()));
				_alDate.add(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()));
				
				if (hmHolidayDates != null && hmHolidayDates.containsKey(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT))) {
					_alHolidays.add(i + "");
					_hmHolidaysColour.put(i + "", (String) hmHolidayDates.get(uF.getDateFormat(rs.getString("_date"), DBDATE, DATE_FORMAT)));
				}
				i++;
			}
			
			pst.close();
	
			
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1 );
			cal.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
			
			int maxDays = cal.getActualMaximum(Calendar.DATE);
			int minDays = cal.getActualMinimum(Calendar.DATE);
			
			
			cal.set(Calendar.DAY_OF_MONTH, minDays);
			
			
			String strD1 = null;
			for(int ii=0; ii<maxDays; ii++) {
				
				strD1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				
				_alDay.add(uF.getDateFormat(strD1, DATE_FORMAT, CF.getStrReportDayFormat()));
				_alDate.add(uF.getDateFormat(strD1, DATE_FORMAT, CF.getStrReportDateFormat()));

				cal.add(Calendar.DATE, 1);
			}
			
			Map<String, String> hmHolidays = new HashMap<String, String>();
			Map<String, String> hmHolidayDates = new HashMap<String, String>();
			CF.getHolidayList(con,request, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat(), DATE_FORMAT), uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat(), DATE_FORMAT), CF, hmHolidayDates, hmHolidays, true);
			
			Map<String, Map<String, String>> hmLeaveDatesType = new HashMap<String, Map<String, String>>();
//			Map hmLeavesMap = CF.getLeaveDates(con, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat(), DATE_FORMAT), uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat(), DATE_FORMAT), CF, hmLeaveDatesType, false, null);
			Map hmLeavesMap = CF.getActualLeaveDates(con, CF, uF, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat(), DATE_FORMAT), uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat(), DATE_FORMAT), hmLeaveDatesType, false, null);
			Map hmLeavesColour = new HashMap();
			Map hmLeavesName = new HashMap();
			CF.getLeavesAttributes(con, uF, hmLeavesColour, hmLeavesName);
			
			for(int ii=0; ii<_alDate.size(); ii++) {
				if (hmHolidayDates != null && hmHolidayDates.containsKey(uF.getDateFormat((String)_alDate.get(ii), CF.getStrReportDateFormat(), DATE_FORMAT))) {
					_alHolidays.add(ii + "");
					_hmHolidaysColour.put(ii + "", (String) hmHolidayDates.get(uF.getDateFormat((String)_alDate.get(ii), CF.getStrReportDateFormat(), DATE_FORMAT)));
				}
			
			}
		
			Map hm = new HashMap();
			Map<String, String> hm1 = new HashMap<String, String>();
			Map<String, String> hm2 = new HashMap<String, String>();
			Map<String, String> hm3 = new HashMap<String, String>();

			if (strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
//				pst = con.prepareStatement(selectRosterDetailsV);
				pst = con.prepareStatement("SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and epd.joining_date <= ? and epd.is_alive=true and "
						+" (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) AND rd.emp_id=? and _date between ? and ? order by _date desc");
				pst.setDate(1, uF.getDateFormat(strDates[1], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strDates[1], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strDates[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strDates[1], DATE_FORMAT));
				pst.setInt(5, uF.parseToInt((String) session.getAttribute(EMPID))); 
				pst.setDate(6, uF.getDateFormat(strDates[0], DATE_FORMAT));
				pst.setDate(7, uF.getDateFormat(strDates[1], DATE_FORMAT));
//				System.out.println("1 pst====>"+pst); 
			} else if (strUserType != null && (strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) || strUserType.equalsIgnoreCase(HRMANAGER) ) && strAlphaValue!=null) {
				pst = con.prepareStatement("SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and epd.is_alive=true "
						+" and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) "
						+" and _date between ? and ? and emp_fname like ?  order by emp_id, _date desc");
				pst.setDate(1, uF.getDateFormat(strDates[1], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strDates[1], DATE_FORMAT));
				pst.setDate(3, uF.getDateFormat(strDates[0], DATE_FORMAT));
				pst.setDate(4, uF.getDateFormat(strDates[1], DATE_FORMAT));
				pst.setDate(5, uF.getDateFormat(strDates[0], DATE_FORMAT));
				pst.setDate(6, uF.getDateFormat(strDates[1], DATE_FORMAT));
				pst.setString(7, strAlphaValue+"%");
//				System.out.println("2 pst====>"+pst); 
			} else if (strUserType != null && ((strUserType.equalsIgnoreCase(MANAGER) && getCurrUserType() != null && getCurrUserType().equals(strBaseUserType)) || strUserType.equalsIgnoreCase(ADMIN) || strUserType.equalsIgnoreCase(CEO) || strUserType.equalsIgnoreCase(CFO) || strUserType.equalsIgnoreCase(HRMANAGER) )) {
				if(uF.parseToInt(profileEmpId) > 0) {
					pst = con.prepareStatement("SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and epd.is_alive=true "
							+"and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?))"
							+" AND rd.emp_id=? and _date between ? and ? order by _date desc");
					pst.setDate(1, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
					pst.setDate(2, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
					pst.setDate(3, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat()));
					pst.setDate(4, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
					pst.setInt(5, uF.parseToInt(profileEmpId)); 
					pst.setDate(6, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat()));
					pst.setDate(7, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
//					System.out.println("3 pst====>"+pst); 
				} 
//					pst = con.prepareStatement(selectRosterDetails);
					pst = con.prepareStatement(sbQuery.toString());
					pst.setDate(1, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat()));
					pst.setDate(2, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
					pst.setDate(3, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat()));
					pst.setDate(4, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
					pst.setDate(5, uF.getDateFormat((String)_alDate.get(0), CF.getStrReportDateFormat()));
					pst.setDate(6, uF.getDateFormat((String)_alDate.get(_alDate.size()-1), CF.getStrReportDateFormat()));
//					System.out.println("4 pst====>"+pst); 
				}
				
			} else if (strUserType != null && strUserType.equalsIgnoreCase(MANAGER) && (getCurrUserType() == null || !getCurrUserType().equals(strBaseUserType))) {
				
				if(uF.parseToInt(profileEmpId) > 0) {
					pst = con.prepareStatement("SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and epd.is_alive=true "
							+" and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) "
							+" AND rd.emp_id=? and _date between ? and ? order by _date desc");
					pst.setDate(1, uF.getDateFormat(strDates[1], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strDates[1], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strDates[0], DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strDates[1], DATE_FORMAT));
					pst.setInt(5, uF.parseToInt(profileEmpId)); 
					pst.setDate(6, uF.getDateFormat(strDates[0], DATE_FORMAT));
					pst.setDate(7, uF.getDateFormat(strDates[1], DATE_FORMAT));
//					System.out.println("5 pst====>"+pst); 
				} else {
					pst = con.prepareStatement("select * from (SELECT * FROM roster_details rd, employee_personal_details epd WHERE rd.emp_id = epd.emp_per_id and epd.is_alive=true " 
							+" and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) "
							+ " and _date between ? and ? and emp_id in (select emp_id from employee_official_details where supervisor_emp_id = ?) order by emp_id, _date desc) e order by emp_fname");
					pst.setDate(1, uF.getDateFormat(strDates[1], DATE_FORMAT));
					pst.setDate(2, uF.getDateFormat(strDates[1], DATE_FORMAT));
					pst.setDate(3, uF.getDateFormat(strDates[0], DATE_FORMAT));
					pst.setDate(4, uF.getDateFormat(strDates[1], DATE_FORMAT));
					pst.setDate(5, uF.getDateFormat(strDates[0], DATE_FORMAT));
					pst.setDate(6, uF.getDateFormat(strDates[1], DATE_FORMAT));
					pst.setInt(7, uF.parseToInt((String) session.getAttribute("EMPID")));
//					System.out.println("6 pst====>"+pst); 
				}
			} else {
				return ACCESS_DENIED;
			}

			System.out.println("pst====>"+pst); 
			
			rs = pst.executeQuery();

			StringBuilder sb = new StringBuilder();
			List alServices = new ArrayList();
			
			while (rs.next()) {
				if (strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
					if (!alEmpId.contains(rs.getString("emp_per_id"))) {
						alEmpId.add(rs.getString("emp_per_id"));
					}
					hm1 = (HashMap) hm.get(rs.getString("emp_per_id"));
					if (hm1 == null) {
						hm1 = new HashMap();
						hm2 = new HashMap();
					}

//					hm1.put("EMPNAME", rs.getString("emp_fname") + " (" + rs.getString("empcode") + ")");
					
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
				
					
					hm1.put("EMPNAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
					
					hm1.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "FROM" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
					hm1.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "TO" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));

					hm2.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), (String) hmServices.get(rs.getString("service_id")));
					hm2.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"ROSTER_ID", rs.getString("roster_id"));

					hm.put(rs.getString("emp_per_id"), hm1);

					hmServicesWorkrdFor.put(rs.getString("emp_per_id"), hm2);
					
				} else {
					
					hm3 = (HashMap) hm.get(rs.getString("emp_per_id")+"_"+rs.getString("service_id"));
					if(hm3==null) {
						hm3  = new HashMap();
					}
					
					if (!alEmpId.contains(rs.getString("emp_per_id"))) {
						alEmpId.add(rs.getString("emp_per_id"));
					}

//					hm1 = (HashMap) hm.get(rs.getString("emp_per_id"));
					if (hm1 == null) {
						hm1 = new HashMap();
						hm2 = new HashMap();
					}

//					hm1.put("EMPNAME", rs.getString("emp_fname") + " (" + rs.getString("empcode") + ")");
					String strEmpMName = "";
					if(flagMiddleName) {
						if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
							strEmpMName = " "+rs.getString("emp_mname");
						}
					}
					
					
					hm1.put("EMPNAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
					hm1.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "FROM" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
					hm1.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "TO" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));

					hm2.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), (String) hmServices.get(rs.getString("service_id")));
					hm2.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"_"+rs.getString("service_id")+"ROSTER_ID", rs.getString("roster_id"));

//					hm.put(rs.getString("emp_per_id"), hm1);
					hmServicesWorkrdFor.put(rs.getString("emp_per_id"), hm2);
					
					hm3.put("EMPNAME", rs.getString("emp_fname") +strEmpMName+ " " + rs.getString("emp_lname"));
					hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "FROM" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()));
					hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()) + "TO" + ((isEmpUserType) ? "_" + rs.getString("service_id") : ""), uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()));
					
					hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat()), (String) hmServices.get(rs.getString("service_id")));
					hm3.put(uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"_"+rs.getString("service_id")+"ROSTER_ID", rs.getString("roster_id"));

					hm.put(rs.getString("emp_per_id")+"_"+rs.getString("service_id"), hm3);
					
					alServices = (List)hm.get(rs.getString("emp_per_id"));
					if(alServices==null) {
						alServices = new ArrayList();
					}
					
					if(!alServices.contains(rs.getString("service_id")) && hmServices.containsKey(rs.getString("service_id"))) {
						alServices.add(rs.getString("service_id"));
					}
					hm.put(rs.getString("emp_per_id"), alServices);
				}

//				log.debug("roster_id===>"+rs.getString("roster_id"));
//				log.debug("_from===>"+rs.getString("_from"));
//				log.debug("_to===>"+rs.getString("_to"));
//				log.debug("_Date===>"+rs.getString("_date"));
				
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				sb.append(""+			
				"<div id=\"popup_name"+rs.getString("roster_id")+"\" class=\"popup_block posfix\">" +
				"<a href=\"javascript:void(0)\" onclick=\"hideBlock('popup_name"+rs.getString("roster_id")+"');\" class=\"close\"><img src=\""+request.getContextPath()+"/images/close_pop.png\" class=\"btn_close\" title=\"Close Window\" alt=\"Close\" /></a>"+
						
				"<h2 class=\"alignCenter\">Roster of "+rs.getString("emp_fname")+strEmpMName+" " + rs.getString("emp_lname")+" for "+uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"</h2><br/>"+
				"<form action=\"UpdateRosterReport.action\">"+
				"<input type=\"hidden\" name=\"f_org\" value=\""+ getF_org() +"\">"+
				"<input type=\"hidden\" name=\"f_strWLocation\" value=\""+ getF_strWLocation() +"\">"+
				"<input type=\"hidden\" name=\"f_department\" value=\""+ getF_department() +"\">"+
				"<input type=\"hidden\" name=\"f_service\" value=\""+ getF_service() +"\">"+
				"<input type=\"hidden\" name=\"f_level\" value=\""+ getF_level() +"\">"+
				"<input type=\"hidden\" name=\"strMonth\" value=\""+ getStrMonth() +"\">"+
				"<input type=\"hidden\" name=\"calendarYear\" value=\""+ getCalendarYear() +"\">"+
				"<table cellpadding=\"0\" cellspacing=\"0\" align=\"center\" width=\"250px\">"+				
				"<tr>"+
				"<td class=\"reportHeading\">Start Time</td><td class=\"reportHeading\">End Time</td>"+
				"</tr><tr>"+
				"<td class=\"reportLabel\"><input style=\"width:100px\" name=\"_from\" type=\"text\" value=\""+uF.showData(uF.getDateFormat(rs.getString("_from"), DBTIME, CF.getStrReportTimeFormat()),"")+"\" ></td><td class=\"reportLabel\"><input style=\"width:100px\" name=\"_to\" type=\"text\" value=\""+uF.showData(uF.getDateFormat(rs.getString("_to"), DBTIME, CF.getStrReportTimeFormat()),"")+"\" ></td>"+
				"</tr><tr>"+
				"<td height=\"60px\"  class=\"reportLabel\" align=\"center\" colspan=\"2\"><input name=\"UPD\" type=\"submit\" value=\"Update Roster\" class=\"input_button\">&nbsp;" +
				"<input onclick=\"return confirm('Are you sure you want to delete this roster entry of "+rs.getString("emp_fname")+strEmpMName+" " + rs.getString("emp_lname")+" for "+uF.getDateFormat(rs.getString("_date"), DBDATE, CF.getStrReportDateFormat())+"?')\" type=\"submit\" name=\"DEL\" value=\"Delete Roster\" class=\"input_button\">"+
				"<input name=\"roster_id\" type=\"hidden\" value=\""+rs.getString("roster_id")+"\" ></td>"+
				"</tr>"+
				"</table>"+
				"</form></div>");
			}
			rs.close();
			pst.close();

			Map hmRosterServiceName = new HashMap();
			Map hmRosterServiceId = new HashMap();
			List<String> alServiceId = new ArrayList<String>();
			
//			new CommonFunctions(CF).getRosterServicesIDList(strDates[0], strDates[1], hmRosterServiceName, hmRosterServiceId, alServiceId);
			CF.getRosterServicesIDList(con,CF,strDates[0], strDates[1], hmRosterServiceName, hmRosterServiceId, null);
//			hmRosterServiceName = new HashMap();
			hmRosterServiceName = CF.getServicesMap(con,true);
			
			if (strUserType != null && (strUserType.equalsIgnoreCase(EMPLOYEE) || strUserType.equalsIgnoreCase(ARTICLE) || strUserType.equalsIgnoreCase(CONSULTANT))) {
				pst = con.prepareStatement("select service_id from employee_official_details where emp_id = ?");
				pst.setInt(1, uF.parseToInt((String) session.getAttribute("EMPID")));
				rs = pst.executeQuery();
				String []arrServiceId = null;
				if (rs.next()) {
					if(rs.getString("service_id")!=null) {
						arrServiceId = rs.getString("service_id").split(",");
					}
				}
				rs.close();
				pst.close();
				for(int ii=0; arrServiceId!=null && ii<arrServiceId.length; ii++) {
					if(!alServiceId.contains(arrServiceId[ii])) {
						alServiceId.add(arrServiceId[ii]);
					}
				}
			} else {
				pst = con.prepareStatement("select service_id,emp_id from employee_official_details");
				rs = pst.executeQuery();
				while(rs.next()) {
					alServiceId = (List)hm.get(rs.getString("emp_id"));
					if(alServiceId == null) alServiceId = new ArrayList();
					
					String []arrServiceId = null;
					if(rs.getString("service_id")!=null) {
						arrServiceId = rs.getString("service_id").split(",");
					}
					for(int ii=0; arrServiceId!=null && ii<arrServiceId.length; ii++) {
						if(!alServiceId.contains(arrServiceId[ii])) {
							alServiceId.add(arrServiceId[ii]);
						}
					}
					hm.put(rs.getString("emp_id"), alServiceId);
				}
				rs.close();
				pst.close();
			}
			
			request.setAttribute("alDay", _alDay);
			request.setAttribute("alDate", _alDate);
			request.setAttribute("alEmpId", alEmpId);
			request.setAttribute("hmList", hm);
			request.setAttribute("_alHolidays", _alHolidays);
			request.setAttribute("_hmHolidaysColour", _hmHolidaysColour);
			request.setAttribute("hmHolidays", hmHolidays);
			request.setAttribute("hmHolidayDates", hmHolidayDates);
			request.setAttribute("hmWLocation", hmWLocation);
			request.setAttribute("hmWeekEnds", hmWeekEnds);
			request.setAttribute("hmServicesWorkrdFor", hmServicesWorkrdFor);
			request.setAttribute("hmRosterServiceId", hmRosterServiceId);
			request.setAttribute("hmRosterServiceName", hmRosterServiceName);
			request.setAttribute("alServiceId", alServiceId);
			request.setAttribute("hmServices", hmServices);
			request.setAttribute("empRosterDetails", sb.toString());
			request.setAttribute("paycycleDuration",CF.getStrPaycycleDuration());
			
			request.setAttribute("hmLeavesMap",hmLeavesMap);
			request.setAttribute("hmLeavesColour",hmLeavesColour);
			request.setAttribute("hmLeavesName",hmLeavesName);
			request.setAttribute("CC", new FillServices(request).fillServicesHtml());
			request.setAttribute("hmEmpLevelMap", hmEmpLevelMap);
			

			request.setAttribute("hmWeekEndList", hmWeekEndList);
			request.setAttribute("hmRosterWeekEndDates", hmRosterWeekEndDates);
			request.setAttribute("alEmpCheckRosterWeektype", alEmpCheckRosterWeektype);
			
//			System.out.println("hmWeekEndList======>"+hmWeekEndList);
			
			getSelectedFilter(uF);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;

	}
*/
		
		
	public HttpServletRequest request;   
	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}
	public String getStrEmpId() {
		return strEmpId;
	}

	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getEmpType() {
		return empType;
	}

	public void setEmpType(String empType) {
		this.empType = empType;
	}

	public String getOrgFullName() {
		return orgFullName;
	}

	public void setOrgFullName(String orgFullName) {
		this.orgFullName = orgFullName;
	}

	public String getOrgDescription() {
		return orgDescription;
	}

	public void setOrgDescription(String orgDescription) {
		this.orgDescription = orgDescription;
	}
	
	public String getOrgWebsite() {
		return orgWebsite;
	}

	public void setOrgWebsite(String orgWebsite) {
		this.orgWebsite = orgWebsite;
	}
	
	public String getFromPage() {
		return fromPage;
	}

	public void setFromPage(String fromPage) {
		this.fromPage = fromPage;
	}
	
	public String getCalendarYear() {
		return calendarYear;
	}

	public void setCalendarYear(String calendarYear) {
		this.calendarYear = calendarYear;
	}

	public List<FillCalendarYears> getCalendarYearList() {
		return calendarYearList;
	}

	public void setCalendarYearList(List<FillCalendarYears> calendarYearList) {
		this.calendarYearList = calendarYearList;
	}
	
	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrYear() {
		return strYear;
	}

	public void setStrYear(String strYear) {
		this.strYear = strYear;
	}
	public String getUserscreen() {
		return userscreen;
	}

	public void setUserscreen(String userscreen) {
		this.userscreen = userscreen;
	}
}
