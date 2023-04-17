package com.konnect.jpms.reports.master;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillDepartment;
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

public class LeaveBalanceReport extends ActionSupport implements ServletRequestAware, IStatements  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF = null;
	String strUserType = null;
	String strUserTypeId = null; 
	String strBaseUserType = null;
	String strBaseUserTypeId = null;
	String strSessionEmpId = null; 
	
	String alertStatus;
	String alert_type;
	
	String strStartDate;
	String strEndDate;
	
	String strMonth;
	String strYear;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	
	String f_org;
	String[] f_wLocation; 
	String[] f_level;
	String[] f_department;
	String[] f_service;
	List<FillMonth> monthList;
	List<FillYears> yearList;
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillCalendarYears> calendarYearList;
	String calendarYear;
	
	private String proPage;
	private String minLimit;
	private String strSearch;
	 
	public String execute() throws Exception {
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
			  
		strUserType = (String) session.getAttribute(USERTYPE);
		strUserTypeId = (String) session.getAttribute(USERTYPEID);
		strBaseUserType = (String) session.getAttribute(BASEUSERTYPE);
		strBaseUserTypeId = (String) session.getAttribute(BASEUSERTYPEID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(PAGE, PLeaveBlanceReport);
		request.setAttribute(TITLE, "Leave Balance Report");
		UtilityFunctions uF = new UtilityFunctions(); 
		
		if(getF_org()==null || getF_org().trim().equals("")){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getStrLocation() != null && !getStrLocation().equals("")) {
			setF_wLocation(getStrLocation().split(","));
		} else {
			setF_wLocation(null);
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
		
		if(getStrStartDate()!=null && getStrStartDate().equalsIgnoreCase("NULL")){
			setStrStartDate(null);
			setStrEndDate(null);
		}
		
		if(getStrEndDate()!=null && getStrEndDate().equalsIgnoreCase("NULL")){
			setStrStartDate(null);
			setStrEndDate(null);
		}
		
		if(uF.parseToInt(getProPage()) == 0) {
			setProPage("1");
		}
		
		getSearchAutoCompleteData(uF);
		
		viewLeaveBalanceReport(uF);
		
		return loadLeaveBalanceReport(uF);

	}
	
	private void getSearchAutoCompleteData(UtilityFunctions uF) {
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
			con = db.makeConnection(con);
			
			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			SortedSet<String> setSearchList = new TreeSet<String>();
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod, probation_policy pp " +
					"where epd.emp_per_id=eod.emp_id and epd.emp_per_id=pp.emp_id and eod.emp_id=pp.emp_id and is_alive = true " +
					"and emp_per_id>0 ");
			if(getF_level()!=null && getF_level().length>0){
	            sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
	        if(getF_department()!=null && getF_department().length>0){
	            sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
	        
	        if(getF_service()!=null && getF_service().length>0){
	            sbQuery.append(" and (");
	            for(int i=0; i<getF_service().length; i++){
	            	sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
	                
	                if(i<getF_service().length-1){
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
	            
	        } 
	        if(getF_wLocation()!=null && getF_wLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_wLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	        
	        if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
	        sbQuery.append(" and (pp.leaves_types_allowed is not null and pp.leaves_types_allowed !='') " +
					"and epd.joining_date is not null order by epd.emp_fname,epd.emp_lname ");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst=Search=="+pst);
			rs = pst.executeQuery();
			while (rs.next()) {
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
			
				
				setSearchList.add(rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
			}
			rs.close();
			pst.close();
			
			StringBuilder sbData = null;
			Iterator<String> it = setSearchList.iterator();
			while (it.hasNext()){
				String strData = it.next();
				if(sbData == null){
					sbData = new StringBuilder();
					sbData.append("\""+strData+"\"");
				} else {
					sbData.append(",\""+strData+"\"");
				}
			}
			
			if(sbData == null){
				sbData = new StringBuilder();
			}
			request.setAttribute("sbData", sbData.toString());
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void viewLeaveBalanceReport(UtilityFunctions uF) {
	
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		try {
	
			String[] strCalendarYearDates = null;
			String strCalendarYearStart = null;
			String strCalendarYearEnd = null;
	
			if (getCalendarYear() != null) {
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
			

			Map<String, String> hmFeatureStatus = CF.getFeatureStatusMap(con);
			boolean flagMiddleName = uF.parseToBoolean(hmFeatureStatus.get(F_SHOW_EMPLOYEE_MIDDLE_NAME));
			
			
			Map<String, String> hmEmpLevelMap = CF.getEmpLevelMap(con);
			if(hmEmpLevelMap == null) hmEmpLevelMap = new HashMap<String, String>();
			Map<String, String> hmDepartment =CF.getDepartmentMap(con, null, null);
			if(hmDepartment == null) hmDepartment = new HashMap<String, String>();
			Map<String, String> hmEmployeeNameMap = CF.getEmpNameMap(con, null, null);
			if(hmEmployeeNameMap == null) hmEmployeeNameMap = new HashMap<String, String>();
			
			Calendar cal = GregorianCalendar.getInstance();
	        cal.set(Calendar.DATE, uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "dd")));
	        cal.set(Calendar.MONTH, uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "MM")) - 1);
	        cal.set(Calendar.YEAR, uF.parseToInt(uF.getDateFormat(strCalendarYearStart, DATE_FORMAT, "yyyy")));
	        
	        setStrYear(""+(cal.get(Calendar.YEAR)));
	        
	        List<String> almonth = new ArrayList<String>();
	    	Map<String, Map<String, String>> hmMonths = new LinkedHashMap<String, Map<String,String>>();
	        for (int i = 1; i <= 12; i++){
	            int nMonthStart = cal.getActualMinimum(Calendar.DATE);
				int nMonthEnd = cal.getActualMaximum(Calendar.DATE);
				int nMonth = (cal.get(Calendar.MONTH) + 1);
				int nYear = (cal.get(Calendar.YEAR));
				
				String strDateStart =  (nMonthStart <10 ? "0"+nMonthStart : nMonthStart)+"/"+(nMonth <10 ? "0"+nMonth : nMonth)+"/"+cal.get(Calendar.YEAR);
				String strDateEnd = (nMonthEnd <10 ? "0"+nMonthEnd : nMonthEnd) +"/"+(nMonth <10 ? "0"+nMonth : nMonth)+"/"+cal.get(Calendar.YEAR);
				cal.add(Calendar.MONTH, 1);
	//			almonth.add(uF.getMonth(nMonth));
				almonth.add(nMonth+"/"+nYear);
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("START_DATE", strDateStart);
				hmInner.put("END_DATE", strDateEnd);
				
				hmMonths.put(""+nMonth, hmInner);
	        }
	//        System.out.println("almonth===>"+almonth);
	//        System.out.println("hmMonths===>"+hmMonths);
	     //   System.out.println("year===>"+getStrYear());
	        
	        StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from emp_leave_type elt, leave_type lt where elt.leave_type_id=lt.leave_type_id and elt.is_paid=true " +
					"and elt.leave_type_id in (select leave_type_id from leave_type where is_compensatory = false and is_work_from_home=false ");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			sbQuery.append(") and elt.is_constant_balance=false ");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and elt.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and elt.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}	
			sbQuery.append(" and (lt.is_leave_opt_holiday is null or lt.is_leave_opt_holiday=false) order by elt.level_id");
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst====>"+pst);
			rs = pst.executeQuery();
			List<String> alLeaveType = new ArrayList<String>();
			Map<String, List<String>> hmLeavesType = new HashMap<String, List<String>>();
			Map<String, String> hmLeaveTypeMap = new HashMap<String, String>();
			while(rs.next()){
				if(uF.parseToInt(rs.getString("leave_type_id"))>0){
					if(!alLeaveType.contains(rs.getString("leave_type_id"))){
						alLeaveType.add(rs.getString("leave_type_id"));
					}
	
					List<String> alLeave = hmLeavesType.get(rs.getString("level_id")+"_"+rs.getString("wlocation_id"));
					if(alLeave == null) alLeave = new ArrayList<String>();
					alLeave.add(rs.getString("leave_type_id"));
					
					hmLeavesType.put(rs.getString("level_id")+"_"+rs.getString("wlocation_id"), alLeave);
					
					hmLeaveTypeMap.put(rs.getString("leave_type_id"), rs.getString("leave_type_name"));
				}
			}
			rs.close();
			pst.close();
			
			sbQuery=new StringBuilder();
//			sbQuery.append("select count(eod.emp_id) as empCount from employee_personal_details epd, employee_official_details eod, probation_policy pp " +
//						"where epd.emp_per_id=eod.emp_id and epd.emp_per_id=pp.emp_id and eod.emp_id=pp.emp_id and is_alive = true " +
//						"and emp_per_id>0 ");
			sbQuery.append("select count(eod.emp_id) as empCount from employee_personal_details epd, employee_official_details eod, probation_policy pp " +
					"where epd.emp_per_id=eod.emp_id and epd.emp_per_id=pp.emp_id and eod.emp_id=pp.emp_id " +
					"and emp_per_id>0 ");
			if(getF_level()!=null && getF_level().length>0){
	            sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
	        if(getF_department()!=null && getF_department().length>0){
	            sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
	        
	        if(getF_service()!=null && getF_service().length>0){
	            sbQuery.append(" and (");
	            for(int i=0; i<getF_service().length; i++){
	            	sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
	                
	                if(i<getF_service().length-1){
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
	            
	        } 
	        if(getF_wLocation()!=null && getF_wLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_wLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	        
	        if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
	        if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")){
	        	if(flagMiddleName) {
					sbQuery.append(" and (upper(epd.emp_fname)||' '||upper(epd.emp_mname)||' '||upper(epd.emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(epd.emp_fname)||' '||upper(epd.emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
				} else {
					sbQuery.append(" and upper(epd.emp_fname)||' '||upper(epd.emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
				}
			}
//	        sbQuery.append(" and (pp.leaves_types_allowed is not null and pp.leaves_types_allowed !='') " +
//					"and epd.joining_date is not null");
	        
	 //===start parvez date: 02-02-2022===
	        sbQuery.append(" and (pp.leaves_types_allowed is not null and pp.leaves_types_allowed !='') " +
			"and epd.joining_date is not null and (employment_end_date is null or (employment_end_date > ? or employment_end_date between ? and ?))");
	 //===end parvez date: 02-02-2022====
	        
		    pst = con.prepareStatement(sbQuery.toString());
		    
	//===start parvez date: 02-02-2022===
		    pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
		    pst.setDate(2, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
		    pst.setDate(3, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
	//===end parvez date: 02-02-2022===
		    
//		    System.out.println("LBR/396--pst====>"+pst);
		    rs = pst.executeQuery();
		    int recCnt = 0;
			int pageCount = 0;
			while (rs.next()) {
				recCnt = rs.getInt("empCount");
				pageCount = rs.getInt("empCount")/10;
				if(rs.getInt("empCount")%10 != 0) {
					pageCount++;
				}
			}
			rs.close();
			pst.close();
			request.setAttribute("pageCount", pageCount+"");
			request.setAttribute("recCnt", recCnt+"");
			
			sbQuery=new StringBuilder();
//			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod, probation_policy pp " +
//						"where epd.emp_per_id=eod.emp_id and epd.emp_per_id=pp.emp_id and eod.emp_id=pp.emp_id and is_alive = true " +
//						"and emp_per_id>0 ");
			sbQuery.append("select * from employee_personal_details epd, employee_official_details eod, probation_policy pp " +
					"where epd.emp_per_id=eod.emp_id and epd.emp_per_id=pp.emp_id and eod.emp_id=pp.emp_id " +
					"and emp_per_id>0 ");
			if(getF_level()!=null && getF_level().length>0){
	            sbQuery.append(" and eod.grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") ) ");
	        }
	        if(getF_department()!=null && getF_department().length>0){
	            sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
	        }
	        
	        if(getF_service()!=null && getF_service().length>0){
	            sbQuery.append(" and (");
	            for(int i=0; i<getF_service().length; i++){
	            	sbQuery.append(" eod.service_id like '%,"+getF_service()[i]+",%'");
	                
	                if(i<getF_service().length-1){
	                    sbQuery.append(" OR "); 
	                }
	            }
	            sbQuery.append(" ) ");
	            
	        } 
	        if(getF_wLocation()!=null && getF_wLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_wLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
	        
	        if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
				sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
	        if(getStrSearch()!=null && !getStrSearch().trim().equals("") && !getStrSearch().trim().equalsIgnoreCase("NULL")){
	        	if(flagMiddleName) {
					sbQuery.append(" and (upper(epd.emp_fname)||' '||upper(epd.emp_mname)||' '||upper(epd.emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%' or upper(epd.emp_fname)||' '||upper(epd.emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%')");
				} else {
					sbQuery.append(" and upper(epd.emp_fname)||' '||upper(epd.emp_lname) like '%"+getStrSearch().trim().toUpperCase()+"%'");
				}
			}
//	        sbQuery.append(" and (pp.leaves_types_allowed is not null and pp.leaves_types_allowed !='') " +
//					"and epd.joining_date is not null order by epd.emp_fname,epd.emp_lname ");
	        
	 //===start parvez date: 02-02-2022===
	        sbQuery.append(" and (pp.leaves_types_allowed is not null and pp.leaves_types_allowed !='') " +
			"and epd.joining_date is not null and (employment_end_date is null or (employment_end_date > ? or employment_end_date between ? and ?)) order by epd.emp_fname,epd.emp_lname ");
	 //===end parvez date: 02-02-2022===       
	        int intOffset = uF.parseToInt(getMinLimit());
			sbQuery.append(" limit 10 offset "+intOffset+"");
		    pst = con.prepareStatement(sbQuery.toString());
		    
	//===start parvez date: 02-02-2022===
		    pst.setDate(1, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
		    pst.setDate(2, uF.getDateFormat(strCalendarYearStart, DATE_FORMAT));
		    pst.setDate(3, uF.getDateFormat(strCalendarYearEnd, DATE_FORMAT));
	//===end parvez date: 02-02-2022===
		    
//		    System.out.println("LBR/461--pst====>"+pst);
		    rs = pst.executeQuery();
			List<String> alEmp = new ArrayList<String>();
			Map<String, List<String>> hmEmpLeaves = new HashMap<String, List<String>>();
			Map<String, Map<String, String>> hmEmp = new HashMap<String, Map<String,String>>();
			while(rs.next()){
				int nLevelId = uF.parseToInt(hmEmpLevelMap.get(rs.getString("emp_id")));
				
				List<String> alLeave = hmLeavesType.get(nLevelId+"_"+rs.getString("wlocation_id"));
				if(alLeave == null) alLeave = new ArrayList<String>();
				
				String strAllowedLeaves = rs.getString("leaves_types_allowed");
				if(strAllowedLeaves!=null && strAllowedLeaves.length()>0){
					List<String> al = Arrays.asList(strAllowedLeaves.split(","));
					for(String leaveTypeId : al){
							if(uF.parseToInt(leaveTypeId) > 0 && alLeaveType.contains(leaveTypeId) && alLeave.contains(leaveTypeId)){
							if(!alEmp.contains(rs.getString("emp_id"))){
								alEmp.add(rs.getString("emp_id"));
							}
							
							List<String> alEmpLeave = hmEmpLeaves.get(rs.getString("emp_id"));
							if(alEmpLeave == null) alEmpLeave = new ArrayList<String>();
							alEmpLeave.add(leaveTypeId);
							
							hmEmpLeaves.put(rs.getString("emp_id"), alEmpLeave);
						}
					}
				}		
				
				Map<String, String> hmInner = new HashMap<String, String>();
				hmInner.put("EMP_ID", rs.getString("emp_id"));
				hmInner.put("EMP_CODE", rs.getString("empcode"));
				
				String strEmpMName = "";
				if(flagMiddleName) {
					if(rs.getString("emp_mname") != null && rs.getString("emp_mname").trim().length()>0) {
						strEmpMName = " "+rs.getString("emp_mname");
					}
				}
				
				hmInner.put("EMP_NAME", rs.getString("emp_fname")+strEmpMName+" "+rs.getString("emp_lname"));
				hmInner.put("EMP_DEPARTMENT",hmDepartment.get(rs.getString("depart_id")));
				hmInner.put("EMP_SUPERVISOR",hmEmployeeNameMap.get(rs.getString("supervisor_emp_id")));
				hmInner.put("EMP_DATE_OF_BIRTH", uF.getDateFormat(rs.getString("emp_date_of_birth"), DBDATE, CF.getStrReportDateFormat()));
				hmInner.put("JOINING_DATE", uF.getDateFormat(rs.getString("joining_date"), DBDATE, CF.getStrReportDateFormat()));
						
				
				hmEmp.put(rs.getString("emp_id"), hmInner);
			}
			rs.close();
			pst.close();
	        
			List<List<String>> reportList = new ArrayList<List<String>>();
			int nAlEmp = alEmp.size();
			for(int i = 0; i < nAlEmp; i++){
				int nEmpId = uF.parseToInt(alEmp.get(i));
				
				List<String> alEmpLeave = hmEmpLeaves.get(""+nEmpId);
				if(alEmpLeave == null) alEmpLeave = new ArrayList<String>(); 
				
				Map<String, String> hmEmpInner = hmEmp.get(""+nEmpId);
				
				for(int j = 0; j < alLeaveType.size(); j++){
					int nLeaveTypeId = uF.parseToInt(alLeaveType.get(j));
					if(!alEmpLeave.contains(""+nLeaveTypeId)){
						continue;
					}
					
					List<String> alInner = new ArrayList<String>();
					alInner.add(uF.showData(hmEmpInner.get("EMP_CODE"), ""));
					alInner.add(uF.showData(hmEmpInner.get("EMP_NAME"), ""));
					alInner.add(uF.showData(hmEmpInner.get("EMP_DEPARTMENT"), ""));
					alInner.add(uF.showData(hmEmpInner.get("EMP_SUPERVISOR"), ""));
					alInner.add(uF.showData(hmLeaveTypeMap.get(""+nLeaveTypeId), ""));
					alInner.add(uF.showData(hmEmpInner.get("EMP_DATE_OF_BIRTH"), ""));
					alInner.add(uF.showData(hmEmpInner.get("JOINING_DATE"), ""));
					
					Iterator<String> it = hmMonths.keySet().iterator();
					double dblLeaveAvailed = 0.0d;
					double dblNewLeaveAvailed = 0.0d;
					double dblBalance = 0.0d;
					while(it.hasNext()){
						String strMth = it.next();
						Map<String, String> hmMonthInner = hmMonths.get(strMth);
						
						String strStartDate = hmMonthInner.get("START_DATE");
						String strEndDate = hmMonthInner.get("END_DATE");
						
						/**
						 * Main Balance
						 * */
						sbQuery=new StringBuilder();
						sbQuery.append("select balance,_date from leave_register1 where register_id in(select max(register_id) from leave_register1 where _type='C' " +
							"and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id from emp_leave_type " +
							"where is_constant_balance=false and leave_type_id=?) and is_compensatory=false and is_work_from_home=false) and _date<=? and emp_id=? group by emp_id,leave_type_id) " +
							"and emp_id in (select emp_id from employee_official_details where emp_id > 0 and emp_id=?)");
						pst = con.prepareStatement(sbQuery.toString());
						pst.setInt(1, nLeaveTypeId);
						pst.setDate(2, uF.getDateFormat(strEndDate, DATE_FORMAT));
						pst.setInt(3, nEmpId);
						pst.setInt(4, nEmpId);
						/*if(nLeaveTypeId == 10) {
							System.out.println("3 pst======>"+pst);
						}*/
					    rs = pst.executeQuery();
					    String strEffectiveDate = null;
					    while (rs.next()) {
					    	dblBalance = uF.parseToDouble(rs.getString("balance"));
					    	strEffectiveDate = rs.getString("_date");
					    }
						rs.close(); 
						pst.close();
						/*if(nLeaveTypeId == 10) {
							System.out.println("strEffectiveDate ===>> " + strEffectiveDate);
						}*/
						/**
						 * Accrued Balance
						 * */
						sbQuery=new StringBuilder();
					    sbQuery.append("select sum(accrued) as accrued from (select max(_date) as daa,leave_type_id,emp_id from leave_register1 where _type='C' " +
				    		"and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id from emp_leave_type " +
					    	"where is_constant_balance=false and leave_type_id=?)) and emp_id=? and _date<=? group by emp_id,leave_type_id)as a,leave_register1 lr " +
					    	"where _type!='C' and a.leave_type_id=lr.leave_type_id and lr.leave_type_id=? and a.daa<=lr._date and a.emp_id=lr.emp_id and " +
					    	"a.emp_id in (select emp_id from employee_official_details where emp_id > 0 and emp_id=?) and lr._date <?");
					    pst = con.prepareStatement(sbQuery.toString());
					    pst.setInt(1, nLeaveTypeId);
					    pst.setInt(2, nEmpId);
					    pst.setDate(3, uF.getDateFormat(strEndDate, DATE_FORMAT));
					    pst.setInt(4, nLeaveTypeId);
					    pst.setInt(5, nEmpId);
					    pst.setDate(6, uF.getDateFormat(strStartDate, DATE_FORMAT));
	//				    System.out.println("4 pst======>"+pst);
					    rs = pst.executeQuery();
					    while (rs.next()) {
					    	dblBalance += uF.parseToDouble(rs.getString("accrued"));                
					    } 
						rs.close();
						pst.close();
						
						/**
						 *Current Accrued Balance
						 * */
						sbQuery=new StringBuilder();
						sbQuery.append("select sum(accrued) as accrued from (select max(_date) as daa,leave_type_id,emp_id from leave_register1 where _type='C' " +
							"and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id from emp_leave_type " +
							"where is_constant_balance=false and leave_type_id=?)) and emp_id=? and _date<=? group by emp_id,leave_type_id)as a,leave_register1 lr " +
							"where _type!='C' and a.leave_type_id=lr.leave_type_id and lr.leave_type_id=? and a.daa<=lr._date and a.emp_id=lr.emp_id " +
							"and a.emp_id in (select emp_id from employee_official_details where emp_id > 0 and emp_id=?) and lr._date between ? and ? " +
							"and lr.compensate_id = 0");
					    pst = con.prepareStatement(sbQuery.toString());
					    pst.setInt(1, nLeaveTypeId);
					    pst.setInt(2, nEmpId);
					    pst.setDate(3, uF.getDateFormat(strEndDate, DATE_FORMAT));
					    pst.setInt(4, nLeaveTypeId);
					    pst.setInt(5, nEmpId);
					    pst.setDate(6, uF.getDateFormat(strStartDate, DATE_FORMAT));
					    pst.setDate(7, uF.getDateFormat(strEndDate, DATE_FORMAT));
	//				    System.out.println("5 pst======>"+pst);
					    rs = pst.executeQuery();
					    while (rs.next()) {
					    	dblBalance += uF.parseToDouble(rs.getString("accrued"));                
					    } 
						rs.close();
						pst.close();
						
						/**
						 *Added Balance
						 * */
						sbQuery=new StringBuilder();
					    sbQuery.append("select sum(accrued) as accrued from (select max(_date) as daa,leave_type_id,emp_id from leave_register1 where _type='C' " +
					   		"and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id from emp_leave_type " +
					    	"where is_constant_balance=false and leave_type_id=?)) and emp_id=? and _date<=? group by emp_id,leave_type_id)as a,leave_register1 lr " +
					    	"where _type!='C' and a.leave_type_id=lr.leave_type_id and lr.leave_type_id=? and a.daa<=lr._date and a.emp_id=lr.emp_id " +
					    	"and a.emp_id in (select emp_id from employee_official_details where emp_id > 0 and emp_id=?)and lr._date between ? and ? " +
					    	"and lr.compensate_id > 0 ");
					    pst = con.prepareStatement(sbQuery.toString());
					    pst.setInt(1, nLeaveTypeId);
					    pst.setInt(2, nEmpId);
					    pst.setDate(3, uF.getDateFormat(strEndDate, DATE_FORMAT));
					    pst.setInt(4, nLeaveTypeId);
					    pst.setInt(5, nEmpId);
					    pst.setDate(6, uF.getDateFormat(strStartDate, DATE_FORMAT));
					    pst.setDate(7, uF.getDateFormat(strEndDate, DATE_FORMAT));
	//				    System.out.println("6 pst======>"+pst);
					    rs = pst.executeQuery();
					    while (rs.next()) {
					    	dblBalance += uF.parseToDouble(rs.getString("accrued"));                
					    } 
						rs.close();
						pst.close();					
						
						/**
						 *Avail Leave Count
						 * */
						pst = con.prepareStatement("select sum(leave_no) as leavecnt from leave_application_register where emp_id=? and leave_type_id=? " +
							"and is_paid=true and (is_modify is null or is_modify=false) and _date between ? and ? and leave_id in (select leave_id from " +
							"emp_leave_entry where emp_id=? and leave_type_id=? and (is_modify is null or is_modify=false) and ((? between approval_from " +
							"and approval_to_date) or (? between approval_from and approval_to_date) or (approval_from >= ? and approval_from<=?)))");
			            pst.setInt(1, nEmpId);
			            pst.setInt(2, nLeaveTypeId);
			            pst.setDate(3, uF.getDateFormat(strStartDate, DATE_FORMAT));
			            pst.setDate(4, uF.getDateFormat(strEndDate, DATE_FORMAT));
			            pst.setInt(5, nEmpId);
			            pst.setInt(6, nLeaveTypeId);
			            pst.setDate(7, uF.getDateFormat(strStartDate, DATE_FORMAT));
			            pst.setDate(8, uF.getDateFormat(strEndDate, DATE_FORMAT));
			            pst.setDate(9, uF.getDateFormat(strStartDate, DATE_FORMAT));
			            pst.setDate(10, uF.getDateFormat(strEndDate, DATE_FORMAT));
			            if(nEmpId==510){
			            	System.out.println(" 7 pst==> " + pst);
			            }
			            rs = pst.executeQuery();
			            double dblLeaveCnt = 0;
			            while (rs.next()) {
			            	dblLeaveCnt = uF.parseToDouble(rs.getString("leavecnt"));			            	
			            }
						rs.close();
						pst.close();
						
						
						Date dtEffective = uF.getDateFormat(strEffectiveDate, DBDATE);
						Date dtStart = uF.getDateFormat(strStartDate, DATE_FORMAT);
						
						if(strEffectiveDate != null && (dtEffective.after(dtStart) || dtEffective.equals(dtStart))) {
							strStartDate = uF.getDateFormat(strEffectiveDate, DBDATE, DATE_FORMAT);
						}
						
						/**
						 *New Avail Leave Count
						 * */
						
						sbQuery=new StringBuilder();
					    sbQuery.append("select sum(leave_no) as count,leave_type_id,emp_id from (select a.daa,lar.* from (select max(_date) as daa,leave_type_id," +
					    	"emp_id from leave_register1 where _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id in " +
					    	"(select leave_type_id from emp_leave_type where is_constant_balance=false)) and register_id in (select max(register_id) as register_id " +
					    	"from leave_register1 where _type='C' and leave_type_id in (select leave_type_id from leave_type where leave_type_id in (select leave_type_id " +
					    	"from emp_leave_type where is_constant_balance=false)) and emp_id in("+nEmpId+") and _date<=? and leave_type_id=? " +
			    			"group by emp_id,leave_type_id) and emp_id in("+nEmpId+") and _date<=? group by emp_id,leave_type_id) as a,leave_application_register lar " +
					    	"where a.emp_id=lar.emp_id and (lar.is_modify is null or lar.is_modify=false) and lar.leave_id in (select leave_id from emp_leave_entry where " +
					    	"approval_to_date<?) and is_paid=true and (is_modify is null or is_modify=false) and a.leave_type_id=lar.leave_type_id and a.daa<=lar._date) as a " +
					    	"where emp_id>0 and emp_id in (select emp_id from employee_official_details where emp_id > 0 and emp_id in("+nEmpId+")) and a._date<? " +
			    			" group by leave_type_id,emp_id order by emp_id,leave_type_id");
					    pst = con.prepareStatement(sbQuery.toString());
					    pst.setDate(1, uF.getDateFormat(strEndDate, DATE_FORMAT));
					    pst.setInt(2, nLeaveTypeId);
					    pst.setDate(3, uF.getDateFormat(strEndDate, DATE_FORMAT));
//					    pst.setDate(4, uF.getDateFormat(strStartDate, DATE_FORMAT));
//					    pst.setDate(5, uF.getDateFormat(strStartDate, DATE_FORMAT));
					    pst.setDate(4, uF.getDateFormat(strEndDate, DATE_FORMAT));
					    pst.setDate(5, uF.getDateFormat(strEndDate, DATE_FORMAT));
					    /*if(nLeaveTypeId == 1 && nEmpId==67) {
					    	System.out.println(" 8 pst ======>> " + pst);
					    }*/
					    rs = pst.executeQuery();
					    while (rs.next()) {
					    	dblNewLeaveAvailed = uF.parseToDouble(rs.getString("count"));
					    }
						rs.close();
						pst.close();
						
						
				        alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblLeaveCnt));
						
						dblLeaveAvailed += uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblLeaveCnt));
						
					}
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblLeaveAvailed));
					
					/*if((nLeaveTypeId==1 || nLeaveTypeId==13) && nEmpId==67){
						System.out.println("LBR/751---dblBalance=="+uF.formatIntoTwoDecimalWithOutComma(dblBalance)+"---dblNewLeaveAvailed=="+uF.formatIntoTwoDecimalWithOutComma(dblNewLeaveAvailed));
					}*/
					double dblTotalBalance = (uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblBalance)) - uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblNewLeaveAvailed))) > 0.0d ? (uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblBalance)) - uF.parseToDouble(uF.formatIntoTwoDecimalWithOutComma(dblNewLeaveAvailed))) : 0.0d;
					alInner.add(uF.formatIntoTwoDecimalWithOutComma(dblTotalBalance));
					
					reportList.add(alInner);				
					
				}
			}
			request.setAttribute("almonth", almonth);
			request.setAttribute("reportList", reportList);
			
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}

	public String loadLeaveBalanceReport(UtilityFunctions uF){	
		monthList = new FillMonth().fillMonth();
		yearList = new FillYears().fillFutureYears(uF.getCurrentDate(CF.getStrTimeZone()), 2, 2);
		calendarYearList = new FillCalendarYears(request).fillCalendarYears(CF);
		
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++) {
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
		if(getF_wLocation()!=null) {
			String strLocation="";
			int k=0;
			for(int i=0;wLocationList!=null && i<wLocationList.size();i++) {
				for(int j=0;j<getF_wLocation().length;j++) {
					if(getF_wLocation()[j].equals(wLocationList.get(i).getwLocationId())) {
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
				hmFilter.put("LEVEL", "All Levels");
			}
		} else {
			hmFilter.put("LEVEL", "All Levels");
		}

		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}

	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
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


	public String getAlertStatus() {
		return alertStatus;
	}


	public void setAlertStatus(String alertStatus) {
		this.alertStatus = alertStatus;
	}


	public String getAlert_type() {
		return alert_type;
	}


	public void setAlert_type(String alert_type) {
		this.alert_type = alert_type;
	}

	public String getF_org() {
		return f_org;
	}

	public void setF_org(String f_org) {
		this.f_org = f_org;
	}

	public String[] getF_wLocation() {
		return f_wLocation;
	}

	public void setF_wLocation(String[] f_wLocation) {
		this.f_wLocation = f_wLocation;
	}

	public String[] getF_level() {
		return f_level;
	}

	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}

	public String[] getF_department() {
		return f_department;
	}

	public void setF_department(String[] f_department) {
		this.f_department = f_department;
	}

	public String[] getF_service() {
		return f_service;
	}

	public void setF_service(String[] f_service) {
		this.f_service = f_service;
	}

	public List<FillOrganisation> getOrganisationList() {
		return organisationList;
	}

	public void setOrganisationList(List<FillOrganisation> organisationList) {
		this.organisationList = organisationList;
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

	public List<FillCalendarYears> getCalendarYearList() {
		return calendarYearList;
	}

	public void setCalendarYearList(List<FillCalendarYears> calendarYearList) {
		this.calendarYearList = calendarYearList;
	}

	public String getCalendarYear() {
		return calendarYear;
	}

	public void setCalendarYear(String calendarYear) {
		this.calendarYear = calendarYear;
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
	public String getProPage() {
		return proPage;
	}

	public void setProPage(String proPage) {
		this.proPage = proPage;
	}

	public String getMinLimit() {
		return minLimit;
	}

	public void setMinLimit(String minLimit) {
		this.minLimit = minLimit;
	}
	
	public String getStrSearch() {
		return strSearch;
	}

	public void setStrSearch(String strSearch) {
		this.strSearch = strSearch;
	}
}