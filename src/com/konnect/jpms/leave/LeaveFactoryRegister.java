package com.konnect.jpms.leave;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
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

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillEmployee;
import com.konnect.jpms.select.FillLeaveType;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.ArrayUtils;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class LeaveFactoryRegister extends ActionSupport implements ServletRequestAware,ServletResponseAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;

	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	
	String strEmpId;
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level; 
	String[] f_service;
	
	List<FillEmployee> empList;
	List<FillOrganisation> orgList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	
	String calendarYear;
	List<FillCalendarYears> calendarYearList;
	
	String exportType;

	private String typeOfLeave;
	private List<FillLeaveType> leaveTypeList;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		strUserType = (String) session.getAttribute(USERTYPE);
		 
		request.setAttribute(PAGE, "/jsp/leave/LeaveFactoryRegister.jsp");
		request.setAttribute(TITLE, "Leave Register Factory");
		

//		boolean isView  = CF.getAccess(session, request, uF);
//		if(!isView){
//			request.setAttribute(PAGE, PAccessDenied);
//			request.setAttribute(TITLE, TAccessDenied);
//			return ACCESS_DENIED;
//		}

		if(getF_org()==null || getF_org().trim().equals("")) {
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
		
		if(uF.parseToInt(getStrEmpId()) > 0){
			getLeaveList(uF);
			if(uF.parseToInt(getTypeOfLeave()) > 0){
				viewLeaveCardYearlyReport(uF);
			}
		} else {
			leaveTypeList = new ArrayList<FillLeaveType>();
		}

		return loadSalaryYearlyReport(uF);
	}
	
	private void getLeaveList(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			con = db.makeConnection(con);
			
			String strEmpOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			String strEmpWLocatonId = CF.getEmpWlocationId(con, uF, getStrEmpId());
			String strEmpLevelId = CF.getEmpLevelId(con, getStrEmpId());
			String gender = CF.getEmpGender(con, uF, getStrEmpId());
			
			boolean flag=CF.getMaternityFrequency(con,request,uF.parseToInt(strEmpLevelId), uF.parseToInt(getStrEmpId()),uF.parseToInt(strEmpOrgId), uF.parseToInt(strEmpWLocatonId));
			leaveTypeList = fillLeaveWithoutCompensetary(con,gender!=null && gender.equalsIgnoreCase("M")?true:false,flag,uF.parseToInt(strEmpLevelId), uF.parseToInt(getStrEmpId()),uF.parseToInt(strEmpOrgId), uF.parseToInt(strEmpWLocatonId),uF.getCurrentDate(CF.getStrTimeZone()));
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	public List<FillLeaveType> fillLeaveWithoutCompensetary(Connection con,boolean genderflag,boolean flag,int nLevelId, int nEmpId,int nOrgId, int nLocationId,Date currentDate) {
		List<FillLeaveType> al = new ArrayList<FillLeaveType>();
		
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {

			
			pst = con.prepareStatement("select * from probation_policy where emp_id = ?");
			pst.setInt(1, nEmpId);
			rs = pst.executeQuery();
			String strAllowedLeaves = null;
			boolean isProbation = false;
			while(rs.next()){
				strAllowedLeaves = rs.getString("leaves_types_allowed");
				isProbation = rs.getBoolean("is_probation");
			}
			rs.close();
			pst.close();
			
			String arr[] = null;
			if(isProbation && strAllowedLeaves!=null && strAllowedLeaves.length()>0){
				arr = strAllowedLeaves.split(",");
			}
			
//			pst = con.prepareStatement(selectLeaveTypeL);
			StringBuilder query=new StringBuilder("SELECT * FROM leave_type lt, emp_leave_type elt where lt.leave_type_id = elt.leave_type_id " +
					" and level_id=? and lt.org_id=? and wlocation_id=? and effective_date <=? and lt.is_compensatory = false and lt.is_work_from_home=false " +
					"and (elt.is_constant_balance is null or elt.is_constant_balance = false)");
			
			if(genderflag || !flag){
				query.append(" and is_maternity=false");
			}
			pst = con.prepareStatement(query.toString());
			pst.setInt(1, nLevelId);
			pst.setInt(2, nOrgId);
			pst.setInt(3, nLocationId);
			pst.setDate(4, currentDate);
			rs = pst.executeQuery();
			al.add(new FillLeaveType("", "Select Leave Type"));  
			while (rs.next()) {
				if(isProbation && arr!=null && ArrayUtils.contains(arr, rs.getString("leave_type_id"))>=0){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				}else if(!isProbation){
					al.add(new FillLeaveType(rs.getString("leave_type_id"), rs.getString("leave_type_name")));
				} 
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
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
		return al;
	}

	private void viewLeaveCardYearlyReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;

			if (getCalendarYear() != null && !getCalendarYear().trim().equals("") && !getCalendarYear().trim().equalsIgnoreCase("NULL")) {
				strCalendarYearDates = getCalendarYear().split("-");
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			} else {
				strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
				setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			}
			
			con = db.makeConnection(con);
			
			String strEmpOrgId = CF.getEmpOrgId(con, uF, getStrEmpId());
			
			List<String> alLeave = new ArrayList<String>();
			pst = con.prepareStatement("select * from leave_type where is_maternity=true and org_id=?");
			pst.setInt(1,uF.parseToInt(strEmpOrgId));
			rs = pst.executeQuery();
			while(rs.next()){
				alLeave.add(rs.getString("leave_type_id"));
			}
			rs.close();
			pst.close();
			
			
			String empEndDate = null;
			boolean isEndDateBetween = false;
			String empEndMonth = null;
			pst = con.prepareStatement("select * from employee_personal_details epd, employee_official_details eod where epd.emp_per_id = eod.emp_id and emp_per_id=?");
			pst.setInt(1,uF.parseToInt(getStrEmpId()));
			rs = pst.executeQuery();
			while(rs.next()){
				if(rs.getString("employment_end_date") !=null && !rs.getString("employment_end_date").trim().equals("") && !rs.getString("employment_end_date").trim().equalsIgnoreCase("")){
					empEndDate = uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT);
					if(empEndDate !=null && !empEndDate.trim().equals("") && !empEndDate.trim().equalsIgnoreCase("NULL")){
						isEndDateBetween = uF.isDateBetween(uF.getDateFormatUtil(strCalendarYearStart, DATE_FORMAT), uF.getDateFormatUtil(strCalendarYearEnd, DATE_FORMAT), uF.getDateFormatUtil(empEndDate, DATE_FORMAT));
						if(isEndDateBetween){
							empEndMonth = uF.getDateFormat(empEndDate+"", DATE_FORMAT, "MM");
						}
					}
				}
			}
			rs.close();
			pst.close();
			
			Calendar cal = GregorianCalendar.getInstance();
            cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "dd")));
            cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "MM")) - 1);
            cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "yyyy")));
			
            List<List<String>> reportList = new ArrayList<List<String>>();
			for (int i = 1; i <= 12; i++){
				
				int nMonthStart = cal.getActualMinimum(Calendar.DATE);
				int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
				int nMonth = (cal.get(Calendar.MONTH) + 1);
				int nYear = (cal.get(Calendar.YEAR));
				
//				String strDateStart =  (nMonthStart <10 ? "0"+nMonthStart : nMonthStart)+"/"+(nMonth <10 ? "0"+nMonth : nMonth)+"/"+cal.get(Calendar.YEAR);
//				String strDateEnd = (nMonthEnd <10 ? "0"+nMonthEnd : nMonthEnd) +"/"+(nMonth <10 ? "0"+nMonth : nMonth)+"/"+cal.get(Calendar.YEAR);
				String strDateStart =  uF.zero(nMonthStart)+"/"+uF.zero(nMonth)+"/"+cal.get(Calendar.YEAR);
				String strDateEnd = uF.zero(nMonthEnd) +"/"+uF.zero(nMonth)+"/"+cal.get(Calendar.YEAR);
				cal.add(Calendar.MONTH, 1);
				
//				System.out.println("strDateStart======>"+strDateStart+"=====strDateEnd======>"+strDateEnd);
				 
				if(isEndDateBetween && uF.parseToInt(empEndMonth) < nMonth){
					continue;
				}
				
				String[] strPayCycleDates = CF.getPayCycleFromDate(con, strDateStart, CF.getStrTimeZone(), CF, strEmpOrgId);
				pst = con.prepareStatement("select paid_days from approve_attendance where emp_id in ("+getStrEmpId()+") and approve_from=? " +
						"and approve_to=? and paycycle=?");
				pst.setDate(1, uF.getDateFormat(strPayCycleDates[0], DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strPayCycleDates[1], DATE_FORMAT));
				pst.setInt(3, uF.parseToInt(strPayCycleDates[2]));
		//		System.out.println("pst====>"+pst);
				rs = pst.executeQuery();
				double dblWorkDays = 0.0d;
				while(rs.next()){
					dblWorkDays = uF.parseToDouble(rs.getString("paid_days"));
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmTakenPaid = new HashMap<String, String>();
				pst = con.prepareStatement("select sum(leave_no) as leave_no,leave_type_id, emp_id, is_paid from leave_application_register " +
						"where is_paid=true and (is_modify is null or is_modify=false) and _date between ? and ? and emp_id in("+getStrEmpId()+") " +
						"and leave_type_id in("+getTypeOfLeave()+") group by leave_type_id, emp_id, is_paid");
				pst.setDate(1, uF.getDateFormat(strDateStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strDateEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()){
					hmTakenPaid.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("leave_no"));
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmTakenHalfDayPaid = new HashMap<String, String>();
				pst = con.prepareStatement("select sum(leave_no) as leave_no,leave_type_id, emp_id, is_paid from leave_application_register " +
						"where is_paid=true and (is_modify is null or is_modify=false) and leave_no=0.5 and _date between ? and ? and emp_id in("+getStrEmpId()+") " +
						"and leave_type_id in("+getTypeOfLeave()+") group by leave_type_id, emp_id, is_paid");
				pst.setDate(1, uF.getDateFormat(strDateStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strDateEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()){
					hmTakenHalfDayPaid.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("leave_no"));
				}
				rs.close();
				pst.close();
				
				Map<String, String> hmTakenUnPaid = new HashMap<String, String>();
				pst = con.prepareStatement("select sum(leave_no) as leave_no,leave_type_id, emp_id, is_paid from leave_application_register " +
						"where is_paid=false and (is_modify is null or is_modify=false) and _date between ? and ? and emp_id in("+getStrEmpId()+") " +
						"and leave_type_id in("+getTypeOfLeave()+")group by leave_type_id, emp_id, is_paid");
				pst.setDate(1, uF.getDateFormat(strDateStart, DATE_FORMAT));
				pst.setDate(2, uF.getDateFormat(strDateEnd, DATE_FORMAT));
				rs = pst.executeQuery();
				while(rs.next()){
					double dblUnpaid = uF.parseToDouble(hmTakenHalfDayPaid.get(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"))) + uF.parseToDouble(rs.getString("leave_no"));
					hmTakenUnPaid.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), ""+dblUnpaid);
				}
				rs.close();
				pst.close();
				
				StringBuilder sbQuery=new StringBuilder();
				sbQuery.append("select emp_id,leave_type_id,balance from leave_register1 where register_id in(select max(register_id) from leave_register1 " +
						"where _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id " +
						"from emp_leave_type where is_constant_balance=false and leave_type_id in("+getTypeOfLeave()+")) and is_compensatory=false and is_work_from_home=false )" +
						" and _date<= ? and emp_id in("+getStrEmpId()+") group by emp_id,leave_type_id) ");
				sbQuery.append("and emp_id in (select emp_id from employee_official_details where emp_id > 0 and emp_id in("+getStrEmpId()+")) ");
				sbQuery.append(" order by emp_id,leave_type_id");
				pst = con.prepareStatement(sbQuery.toString());
				 pst.setDate(1, uF.getDateFormat(strDateEnd, DATE_FORMAT));
//				System.out.println("3 pst======>"+pst);
			    rs = pst.executeQuery();
			    Map<String, String> hmMainBalance=new HashMap<String, String>();
			    Map<String, List<List<String>>> hmEmpLeaveMap=new HashMap<String, List<List<String>>>();
			    while (rs.next()) {
			        hmMainBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("balance"));
			        
			        List<List<String>> outerList = hmEmpLeaveMap.get(rs.getString("emp_id"));
			        if(outerList==null) outerList = new ArrayList<List<String>>();
			        
			        List<String> innerList = new ArrayList<String>();
			        innerList.add(rs.getString("leave_type_id"));
			        
			        outerList.add(innerList);
			        
			        hmEmpLeaveMap.put(rs.getString("emp_id"), outerList);
			    }
				rs.close(); 
				pst.close();
	//		    System.out.println("hmMainBalance======>"+hmMainBalance);
			    
			    sbQuery=new StringBuilder();
			    sbQuery.append("select sum(accrued) as accrued,a.leave_type_id,a.emp_id from " +
			    		"(select max(_date) as daa,leave_type_id,emp_id from leave_register1 where _type='C' " +
			    		"and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id " +
						"from emp_leave_type where is_constant_balance=false and leave_type_id in("+getTypeOfLeave()+"))) " +
						"and emp_id in("+getStrEmpId()+") and _date<=? group by emp_id,leave_type_id)as a,leave_register1 lr " +
			    		"where  _type!='C' and a.leave_type_id=lr.leave_type_id and a.daa<=lr._date and a.emp_id=lr.emp_id and lr.leave_type_id in("+getTypeOfLeave()+") ");
				sbQuery.append("and a.emp_id in (select emp_id from employee_official_details where emp_id > 0 and emp_id in("+getStrEmpId()+")) ");
			    sbQuery.append(" and lr._date <? group by a.emp_id,a.leave_type_id order by a.emp_id,leave_type_id");
			    pst = con.prepareStatement(sbQuery.toString());
			    pst.setDate(1, uF.getDateFormat(strDateEnd, DATE_FORMAT));
			    pst.setDate(2, uF.getDateFormat(strDateStart, DATE_FORMAT));
//			    System.out.println("4 pst======>"+pst);
			    rs = pst.executeQuery();
			    Map<String, String> hmAccruedBalance=new HashMap<String, String>();
			    while (rs.next()) {
			    	hmAccruedBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("accrued"));                
			    } 
				rs.close();
				pst.close();
//			    System.out.println("hmAccruedBalance======>"+hmAccruedBalance);
				
				sbQuery=new StringBuilder();
				sbQuery.append("select sum(accrued) as accrued,a.leave_type_id,a.emp_id from " +
			    		"(select max(_date) as daa,leave_type_id,emp_id from leave_register1 where _type='C' " +
			    		"and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id " +
						"from emp_leave_type where is_constant_balance=false and leave_type_id in("+getTypeOfLeave()+"))) " +
						"and emp_id in("+getStrEmpId()+") and _date<=? group by emp_id,leave_type_id)as a,leave_register1 lr " +
			    		"where  _type!='C' and a.leave_type_id=lr.leave_type_id and a.daa<=lr._date and a.emp_id=lr.emp_id and lr.leave_type_id in("+getTypeOfLeave()+") ");
					sbQuery.append("and a.emp_id in (select emp_id from employee_official_details where emp_id > 0 and emp_id in("+getStrEmpId()+")) ");
					sbQuery.append(" and lr._date between ? and ? and lr.compensate_id = 0 group by a.emp_id,a.leave_type_id order by a.emp_id,leave_type_id");
			    pst = con.prepareStatement(sbQuery.toString());
			    pst.setDate(1, uF.getDateFormat(strDateEnd, DATE_FORMAT));
			    pst.setDate(2, uF.getDateFormat(strDateStart, DATE_FORMAT));
			    pst.setDate(3, uF.getDateFormat(strDateEnd, DATE_FORMAT));
//			    System.out.println("5 pst======>"+pst);
			    rs = pst.executeQuery();
			    Map<String, String> hmCurrAccruedBalance=new HashMap<String, String>();
			    while (rs.next()) {
			    	hmCurrAccruedBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("accrued"));                
			    } 
				rs.close();
				pst.close();
	//		    System.out.println("hmCurrAccruedBalance======>"+hmCurrAccruedBalance);
				
				sbQuery=new StringBuilder();
			    sbQuery.append("select sum(accrued) as accrued,a.leave_type_id,a.emp_id from " +
			    		"(select max(_date) as daa,leave_type_id,emp_id from leave_register1 where _type='C' " +
			    		"and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id " +
						"from emp_leave_type where is_constant_balance=false and leave_type_id in("+getTypeOfLeave()+"))) " +
						"and emp_id in("+getStrEmpId()+") and _date<=? group by emp_id,leave_type_id)as a,leave_register1 lr " +
			    		"where  _type!='C' and a.leave_type_id=lr.leave_type_id and a.daa<=lr._date and a.emp_id=lr.emp_id and lr.leave_type_id in("+getTypeOfLeave()+")");
				sbQuery.append("and a.emp_id in (select emp_id from employee_official_details where emp_id > 0 and emp_id in("+getStrEmpId()+")) ");
			    sbQuery.append(" and lr._date between ? and ? and lr.compensate_id > 0 group by a.emp_id,a.leave_type_id order by a.emp_id,leave_type_id");
			    pst = con.prepareStatement(sbQuery.toString());
			    pst.setDate(1, uF.getDateFormat(strDateEnd, DATE_FORMAT));
			    pst.setDate(2, uF.getDateFormat(strDateStart, DATE_FORMAT));
			    pst.setDate(3, uF.getDateFormat(strDateEnd, DATE_FORMAT));
//			    System.out.println("6 pst======>"+pst);
			    rs = pst.executeQuery();
			    Map<String, String> hmAddedBalance=new HashMap<String, String>();
			    while (rs.next()) {
			    	hmAddedBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("accrued"));                
			    } 
				rs.close();
				pst.close();
	//		    System.out.println("hmAddedBalance======>"+hmAddedBalance);
			    
			    sbQuery=new StringBuilder();
			    sbQuery.append("select sum(leave_no) as count,leave_type_id,emp_id from (select a.daa,lar.* from " +
			    		"(select max(_date) as daa,leave_type_id,emp_id from leave_register1 where _type='C' " +
			    		"and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id " +
						"from emp_leave_type where is_constant_balance=false and leave_type_id in("+getTypeOfLeave()+"))) " +
						"and emp_id in("+getStrEmpId()+") and _date<=? group by emp_id,leave_type_id) as a,leave_application_register lar " +
			    		"where a.emp_id=lar.emp_id and (lar.is_modify is null or lar.is_modify=false) ");
			    sbQuery.append(" and lar.leave_id in (select leave_id from emp_leave_entry where approval_to_date<?) ");
			    sbQuery.append("and is_paid=true and (is_modify is null or is_modify=false) and a.leave_type_id=lar.leave_type_id and lar.leave_type_id in("+getTypeOfLeave()+") " +
			    		"and a.daa<=lar._date) as a where emp_id>0");
				sbQuery.append("and emp_id in (select emp_id from employee_official_details where emp_id > 0 and emp_id in("+getStrEmpId()+"))");
				sbQuery.append(" and a._date<? group by leave_type_id,emp_id order by emp_id,leave_type_id");
			    pst = con.prepareStatement(sbQuery.toString());
			    pst.setDate(1, uF.getDateFormat(strDateEnd, DATE_FORMAT));
			    pst.setDate(2, uF.getDateFormat(strDateStart, DATE_FORMAT));
			    pst.setDate(3, uF.getDateFormat(strDateStart, DATE_FORMAT));
//			    System.out.println("7 pst======>"+pst); 
			    rs = pst.executeQuery();
			    Map<String, String> hmPaidBalance=new HashMap<String, String>();
			    while (rs.next()) {
			    	hmPaidBalance.put(rs.getString("emp_id")+"_"+rs.getString("leave_type_id"), rs.getString("count"));
			    }
				rs.close();
				pst.close();
				
				pst = con.prepareStatement(selectProbationPolicy);
				pst.setInt(1, uF.parseToInt(getStrEmpId()));
//				System.out.println("pst==>"+pst);
				rs = pst.executeQuery();
				Map<String, List<String>> hmEmpLeaves = new HashMap<String, List<String>>();
				while(rs.next()) {
					String strAllowedLeaves = rs.getString("leaves_types_allowed");
					if(strAllowedLeaves!=null && strAllowedLeaves.length()>0){
						List<String> al = Arrays.asList(strAllowedLeaves.split(","));
						for(String leaveTypeId : al){
	 						if(uF.parseToInt(leaveTypeId) > 0){
								List<String> alEmpLeave = hmEmpLeaves.get(rs.getString("emp_id"));
								if(alEmpLeave == null) alEmpLeave = new ArrayList<String>();
								alEmpLeave.add(leaveTypeId);
								
								hmEmpLeaves.put(rs.getString("emp_id"), alEmpLeave);
							}
						}
					}
				}
				rs.close();
				pst.close();
//				System.out.println("hmEmpLeaves==>"+hmEmpLeaves);
				
				List<String> alEmpLeave = hmEmpLeaves.get(getStrEmpId());
				if(alEmpLeave == null) alEmpLeave = new ArrayList<String>(); 
				
				Iterator<String> it = hmEmpLeaveMap.keySet().iterator();
			    while(it.hasNext()){
			    	String strEmpId = it.next();
			    	 List<List<String>> outerList = hmEmpLeaveMap.get(strEmpId);
			    	 if(outerList == null) outerList = new ArrayList<List<String>>();
			    	 int nOuter = outerList.size();
			    	 for(int k=0;k<nOuter;k++){
			    		 List<String> innerList = outerList.get(k);
			    		 String leaveTypeId = innerList.get(0);
			    		 
			    		if(!alEmpLeave.contains(leaveTypeId)){
							continue;
						}
			    		 
						double dblOpeningBalance = uF.parseToDouble(hmMainBalance.get(strEmpId+"_"+leaveTypeId));
						dblOpeningBalance += uF.parseToDouble(hmAccruedBalance.get(strEmpId+"_"+leaveTypeId));
						double dblPaidBalance = uF.parseToDouble(hmPaidBalance.get(strEmpId+"_"+leaveTypeId));
						if(dblOpeningBalance > 0 && dblOpeningBalance >= dblPaidBalance){
							dblOpeningBalance = dblOpeningBalance - dblPaidBalance; 
				        }
						
						double dblCurrAccruedBalance = uF.parseToDouble(hmCurrAccruedBalance.get(strEmpId+"_"+leaveTypeId));
						double dblhmAddedBalance = uF.parseToDouble(hmAddedBalance.get(strEmpId+"_"+leaveTypeId));
						double dblTakenPaid = uF.parseToDouble(hmTakenPaid.get(strEmpId+"_"+leaveTypeId));
						double dblTakenUnPaid = uF.parseToDouble(hmTakenUnPaid.get(strEmpId+"_"+leaveTypeId));
						
						
						double dblClosingBalance = (dblOpeningBalance + dblCurrAccruedBalance + dblhmAddedBalance) - dblTakenPaid;
						dblClosingBalance = dblClosingBalance - dblTakenUnPaid;
						
						if(dblClosingBalance < 0.0d){ 
							dblClosingBalance = 0.0d;
						}
						
						/**
						 * Start Date and End Date
						 * */
						pst = con.prepareStatement("select min(_date) as fromdate,max(_date) as todate,count(_date) as cntleave from leave_application_register where emp_id in ("+getStrEmpId()+") " +
								"and leave_type_id in ("+leaveTypeId+") and _date between ? and ? and is_paid=true group by leave_id order by fromdate");
						pst.setDate(1, uF.getDateFormat(strDateStart, DATE_FORMAT));
						pst.setDate(2, uF.getDateFormat(strDateEnd, DATE_FORMAT));
						rs = pst.executeQuery();
						List<Map<String, String>> alLeaveList = new ArrayList<Map<String,String>>();
						while(rs.next()){
							Map<String, String> hmLeaveFromTo = new HashMap<String, String>();
							hmLeaveFromTo.put("FROM_DATE", uF.showData(rs.getString("fromdate"), ""));
							hmLeaveFromTo.put("TO_DATE", uF.showData(rs.getString("todate"), ""));
							hmLeaveFromTo.put("CNT_LEAVE", uF.showData(rs.getString("cntleave"), ""));
							
							alLeaveList.add(hmLeaveFromTo);
						}
						rs.close();
						pst.close();
						
						
						if(alLeaveList != null && !alLeaveList.isEmpty()){
							double dbLeaveBalance = dblOpeningBalance + dblCurrAccruedBalance + dblhmAddedBalance;
							for(Map<String, String> hmLeaveFromTo : alLeaveList){
								String strMaternity = "";
								String strNotMaternity = uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmLeaveFromTo.get("CNT_LEAVE")));
								if(alLeave.contains(leaveTypeId)){
									strMaternity = uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmLeaveFromTo.get("CNT_LEAVE")));
									strNotMaternity = "";
								}
								dbLeaveBalance = (dbLeaveBalance - uF.parseToDouble(hmLeaveFromTo.get("CNT_LEAVE")));
								
								List<String> alInner = new ArrayList<String>();
								alInner.add(""+nYear);
								alInner.add(uF.getMonth(nMonth));
								alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblWorkDays));
								alInner.add("0");
								alInner.add(strMaternity);
								alInner.add(strNotMaternity);
								alInner.add(uF.formatIntoTwoDecimalWithOutComma(uF.parseToDouble(hmLeaveFromTo.get("CNT_LEAVE"))));
								alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblOpeningBalance));
								alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblCurrAccruedBalance + dblhmAddedBalance));
								alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblOpeningBalance + dblCurrAccruedBalance + dblhmAddedBalance));
								alInner.add("");
								alInner.add("");
								alInner.add(uF.showData(hmLeaveFromTo.get("FROM_DATE"), ""));
								alInner.add(uF.showData(hmLeaveFromTo.get("TO_DATE"), ""));
								alInner.add(uF.formatIntoTwoDecimalWithOutComma(dbLeaveBalance));
								alInner.add("");
								alInner.add("");
								alInner.add("");
								
								reportList.add(alInner);  
							}
						} else {					
							String strMaternity = "";
							String strNotMaternity = uF.formatIntoTwoDecimalWithOutComma(dblTakenPaid);
							if(alLeave.contains(leaveTypeId)){
								strMaternity = uF.formatIntoTwoDecimalWithOutComma(dblTakenPaid);
								strNotMaternity = "";
							}
							
							List<String> alInner = new ArrayList<String>();
							alInner.add(""+nYear);
							alInner.add(uF.getMonth(nMonth));
							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblWorkDays));
							alInner.add("0");
							alInner.add(strMaternity);
							alInner.add(strNotMaternity);
							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblTakenPaid));
							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblOpeningBalance));
							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblCurrAccruedBalance + dblhmAddedBalance));
							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblOpeningBalance + dblCurrAccruedBalance + dblhmAddedBalance));
							alInner.add("");
							alInner.add("");
							alInner.add("");
							alInner.add("");
							alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblClosingBalance));
							alInner.add("");
							alInner.add("");
							alInner.add("");
							
							reportList.add(alInner);  
						}
			    	 }
			    }
				
			}
			request.setAttribute("reportList", reportList);
//			System.out.println("reportList==>"+reportList);
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	

	public String loadSalaryYearlyReport(UtilityFunctions uF) {
		
		calendarYearList = new FillCalendarYears(request).fillCalendarYears(CF);
		
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
				
		empList=getEmployeeList(uF);
		getSelectedFilter(uF);
		
		return LOAD;
	}
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("EMP");
		if(getStrEmpId()!=null)  {
			String strEmp="";
			int k=0;
			for(int i=0;empList!=null && i<empList.size();i++){
				if(getStrEmpId().equals(empList.get(i).getEmployeeId())) {
					if(k==0) {
						strEmp=empList.get(i).getEmployeeCode();
					} else {
						strEmp+=", "+empList.get(i).getEmployeeCode();
					}
					k++;
				}
			}
			if(strEmp!=null && !strEmp.equals("")) {
				hmFilter.put("EMP", strEmp);
			} else {
				hmFilter.put("EMP", "");
			}
			
		} else {
			hmFilter.put("EMP", "");
		}
		
		alFilter.add("LEAVE");
		if(getTypeOfLeave()!=null)  {
			String strLeave="";
			int k=0;
			for(int i=0;leaveTypeList!=null && i<leaveTypeList.size();i++){
				if(getTypeOfLeave().equals(leaveTypeList.get(i).getLeaveTypeId())) {
					if(k==0) {
						strLeave=leaveTypeList.get(i).getLeavetypeName();
					} else {
						strLeave+=", "+leaveTypeList.get(i).getLeavetypeName();
					}
					k++;
				}
			}
			if(strLeave!=null && !strLeave.equals("")) {
				hmFilter.put("LEAVE", strLeave);
			} else {
				hmFilter.put("LEAVE", "");
			}
			
		} else {
			hmFilter.put("LEAVE", "");
		}
		
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
		
		alFilter.add("CALENDARYEAR");
		String[] strCalendarYearDates = null;
		if (getCalendarYear() != null && !getCalendarYear().trim().equals("") && !getCalendarYear().trim().equalsIgnoreCase("NULL")) {
			strCalendarYearDates = getCalendarYear().split("-");
			setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
		} else {
			strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
			setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
		}
		hmFilter.put("CALENDARYEAR", uF.getDateFormat(strCalendarYearDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strCalendarYearDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	private List<FillEmployee> getEmployeeList(UtilityFunctions uF) {
		
		List<FillEmployee> al = new ArrayList<FillEmployee>();
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rsEmpCode = null;
		Database db = new Database();
		db.setRequest(request);
	
		try {
	
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;
			if (getCalendarYear() != null && !getCalendarYear().trim().equals("") && !getCalendarYear().trim().equalsIgnoreCase("NULL")) {
				String[] strCalendarYearDates = getCalendarYear().split("-");
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			} else {
				String[] strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
				setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
				strCalendarYearStart = strCalendarYearDates[0];
				strCalendarYearEnd = strCalendarYearDates[1];
			}
			
			con = db.makeConnection(con);
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			StringBuilder sbQuery=new StringBuilder();
			sbQuery.append("select * from employee_official_details eod,employee_personal_details epd where epd.emp_per_id=eod.emp_id and is_delete=false ");
						
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	        if(getF_department()!=null && getF_department().length>0){
	            sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
	        if(getF_level()!=null && getF_level().length>0){
	            sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
	        if(getF_service()!=null && getF_service().length>0){
	            sbQuery.append(" and (");
	            for(int i=0; i<getF_service().length; i++){
	                sbQuery.append(" service_id like '%,"+getF_service()[i]+",%'");
	                
	                if(i<getF_service().length-1){
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
	            
	        }
	        sbQuery.append(" and epd.joining_date <= ? and (employment_end_date is null or (employment_end_date >= ? or employment_end_date between ? and ?)) ");
			sbQuery.append(" order by epd.emp_fname");			
			pst = con.prepareStatement(sbQuery.toString());
			pst.setDate(1, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
			pst.setDate(2, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
			pst.setDate(3, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
			pst.setDate(4, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
			rsEmpCode = pst.executeQuery();
			while (rsEmpCode.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rsEmpCode.getString("emp_mname") != null && rsEmpCode.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rsEmpCode.getString("emp_mname");
					}
				}
				
				al.add(new FillEmployee(rsEmpCode.getString("emp_per_id"), rsEmpCode.getString("emp_fname") +strEmpMName+ " " + rsEmpCode.getString("emp_lname") + " ["
						+ rsEmpCode.getString("empcode") + "]"));
			}
			rsEmpCode.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rsEmpCode);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return al;
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	private HttpServletResponse response;

	@Override
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}



	public String getStrEmpId() {
		return strEmpId;
	}



	public void setStrEmpId(String strEmpId) {
		this.strEmpId = strEmpId;
	}

	public String getF_org() {
		return f_org;
	}



	public void setF_org(String f_org) {
		this.f_org = f_org;
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



	public List<FillEmployee> getEmpList() {
		return empList;
	}



	public void setEmpList(List<FillEmployee> empList) {
		this.empList = empList;
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

	public List<FillLeaveType> getLeaveTypeList() {
		return leaveTypeList;
	}

	public void setLeaveTypeList(List<FillLeaveType> leaveTypeList) {
		this.leaveTypeList = leaveTypeList;
	}

	public String getTypeOfLeave() {
		return typeOfLeave;
	}

	public void setTypeOfLeave(String typeOfLeave) {
		this.typeOfLeave = typeOfLeave;
	}
	
}
