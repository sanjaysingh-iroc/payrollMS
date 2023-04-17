package com.konnect.jpms.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ProjectEngagementCalendar extends ActionSupport implements ServletRequestAware, IStatements {
	
	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strUserType;
	String strSessionEmpId;
	String strOrgId = null;
	boolean isEmpUserType = false;
	CommonFunctions CF = null;
	
	String paramSelection;
	String calendarYear;
	String strMonth;
	String strYear;
	
	String f_org;
	String[] f_strWLocation;
	String[] f_department;
	String[] f_level;
	
	List<FillOrganisation> organisationList;
	
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	
	List<FillMonth> monthList;
	
	
	List<FillCalendarYears> calendarYearList;
	
	String exportType;
	
	String btnSubmit;
	
	public String execute() throws Exception {
		session = request.getSession(); 
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN; 
		
		UtilityFunctions uF = new UtilityFunctions();
		strUserType = (String) session.getAttribute(BASEUSERTYPE);
		strOrgId  = (String) session.getAttribute(ORGID);
		strSessionEmpId = (String) session.getAttribute(EMPID);
		
		request.setAttribute(TITLE, TProjectEngagementCalendar);
		request.setAttribute(PAGE, PProjectEngagementCalendar);
		

		boolean isView  = CF.getAccess(session, request, uF);
		if(!isView) {
			request.setAttribute(PAGE, PAccessDenied);
			request.setAttribute(TITLE, TAccessDenied);
			return ACCESS_DENIED;
		}
		
		if(getF_org()==null){
			setF_org((String)session.getAttribute(ORGID));
		}
		
		if(getParamSelection() == null){
			setParamSelection("SBU");
		}

		return getProjectEngagement(uF);
	}
	
	private String getProjectEngagement(UtilityFunctions uF) {
		if(getParamSelection().equals("ORG")){
			viewProjectEngagementByOrg(uF);
		} else if(getParamSelection().equals("SBU")){
			viewProjectEngagementBySbu(uF);
		}
		return loadProjectEngagement(uF);
	}
	
	public String viewProjectEngagementByOrg(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
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
			
			if(getStrMonth() == null){
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
			setStrYear(uF.parseToInt(uF.getDateFormat(strCalendarYearEnd+"", DATE_FORMAT, "yyyy"))+"");
			
			List<String> alDates = new ArrayList<String>();
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1 );
			cal.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
			
			int minDays = cal.getActualMinimum(Calendar.DATE);
			int maxDays = cal.getActualMaximum(Calendar.DATE);
			cal.set(Calendar.DAY_OF_MONTH, minDays);
			
			String strD1 = null;
			for(int i=0; i<maxDays; i++) {
				strD1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				alDates.add(uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT));
				cal.add(Calendar.DATE, 1);
			}
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select eod.emp_id,eod.org_id from employee_personal_details epd,employee_official_details eod " +
					"where epd.emp_per_id=eod.emp_id and epd.is_alive=true and eod.org_id>0 and eod.org_id in (select org_id from org_details) " +
					"and extract(month from joining_date)<="+uF.parseToInt(getStrMonth())+" and extract(year from joining_date)<="+uF.parseToInt(getStrYear()));
//			if(uF.parseToInt(getF_org())>0){
//				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
//			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
//					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
//			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			sbQuery.append(" order by eod.org_id");
			pst = con.prepareStatement(sbQuery.toString()); 
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmOrgEmpCnt = new HashMap<String, String>();
			Map<String, String> hmOrgEmp = new HashMap<String, String>();
			while(rs.next()) {
				int nEmp = uF.parseToInt(hmOrgEmpCnt.get(rs.getString("org_id")));
				nEmp++;
				hmOrgEmpCnt.put(rs.getString("org_id"),""+nEmp);
				
				String sbOrgEmp = hmOrgEmp.get(rs.getString("org_id"));
				if(sbOrgEmp == null || sbOrgEmp.trim().equals("")){
					sbOrgEmp = rs.getString("emp_id");
				} else {
					sbOrgEmp +=","+ rs.getString("emp_id");
				}
				hmOrgEmp.put(rs.getString("org_id"),sbOrgEmp);
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmOrgEmp.keySet().iterator();
			Map<String, String> hmOrgTaskCntDaywise = new HashMap<String, String>();
			while(it.hasNext()){
				String strOrgId = it.next();
				String strResource = hmOrgEmp.get(strOrgId);
				String[] strTemp = null;
				if(strResource!=null){
					strTemp = strResource.split(",");
				}
				
				StringBuilder sbResource = null;
				if(strTemp!=null && strTemp.length>0){
					sbResource = new StringBuilder();
					sbResource.append(" and (");
	                for(int i=0; i<strTemp.length; i++){
	                	sbResource.append(" resource_ids like '%,"+strTemp[i]+",%'");
	                    
	                    if(i<strTemp.length-1){
	                    	sbResource.append(" OR "); 
	                    }
	                }
	                sbResource.append(" ) ");
				}   
				
				if(sbResource!=null && sbResource.length() > 0){
					for(int i=0; i<alDates.size(); i++) {
						sbQuery = new StringBuilder();
						sbQuery.append("select count(task_id) as task_no from activity_info where task_id>0 "+sbResource.toString()+" and task_id not in (select " +
							"parent_task_id from activity_info where task_id>0 "+sbResource.toString()+" and parent_task_id is not null) and ? between start_date " +
							"and deadline and (completed < 100 or completed is null) and pro_id in (select pro_id from projectmntnc where org_id = ?) ");
						pst = con.prepareStatement(sbQuery.toString());
						pst.setDate(1, uF.getDateFormat(alDates.get(i), DATE_FORMAT));
						pst.setInt(2, uF.parseToInt(strOrgId));
						rs = pst.executeQuery();
						while(rs.next()) {
							int sbuTaskCnt = uF.parseToInt(hmOrgTaskCntDaywise.get(strOrgId+"_"+alDates.get(i)));
							sbuTaskCnt += rs.getInt("task_no");
							hmOrgTaskCntDaywise.put(strOrgId+"_"+alDates.get(i), sbuTaskCnt+"");
						}
						rs.close();
						pst.close();
					}
				}
			}
			
			pst = con.prepareStatement("select * from org_details order by org_name"); 
			rs = pst.executeQuery();
			Map<String, String> hmOrg = new LinkedHashMap<String, String>();
			while(rs.next()) {
				hmOrg.put(rs.getString("org_id"), rs.getString("org_name"));
			}
			rs.close();
			pst.close();
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			List<List<String>> reportListPrint = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			List<String> alInnerPrint = new ArrayList<String>();
			
			Iterator<String> it1 = hmOrg.keySet().iterator();
			while(it1.hasNext()){
				String strOrgId = it1.next();
				String strOrgName = hmOrg.get(strOrgId);
				
				alInner = new ArrayList<String>();
				alInnerPrint = new ArrayList<String>();
				
				alInner.add(uF.showData(strOrgName,""));
				alInnerPrint.add(uF.showData(strOrgName,""));
				
				for (int ii=0; ii<alDates.size(); ii++) {
					Map<String, String> hmInner  = new HashMap<String, String>();
					Map<String, String> hmInnerPrint  = new HashMap<String, String>();
					
					double dblActualCount = uF.parseToDouble(hmOrgEmpCnt.get(strOrgId));
					double dblTaskCount = uF.parseToDouble(hmOrgTaskCntDaywise.get(strOrgId+"_"+alDates.get(ii)));
					
					double dblRatio = 0;
					
					if(dblActualCount > 0) {
						dblRatio = dblTaskCount / dblActualCount;
					}
					
					if (dblRatio > 0.8) {
						hmInner.put(strOrgId, "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\"><p style=\"border-bottom:solid 1px black\">"+ uF.parseToInt(hmOrgTaskCntDaywise.get(strOrgId+"_"+alDates.get(ii))) +"</p><p>"+ uF.parseToInt(hmOrgEmpCnt.get(strOrgId)) +"</p></div>");
						hmInnerPrint.put(strOrgId, uF.parseToInt(hmOrgTaskCntDaywise.get(strOrgId+"_"+alDates.get(ii))) +"/"+ uF.parseToInt(hmOrgEmpCnt.get(strOrgId))+"");
					} else if(dblRatio > 0.40) {
						hmInner.put(strOrgId, "<div style=\"width: 100%; height: 100%; text-align: center; background-color: yellow;\"><p style=\"border-bottom:solid 1px black\">"+ uF.parseToInt(hmOrgTaskCntDaywise.get(strOrgId+"_"+alDates.get(ii))) +"</p><p>"+ uF.parseToInt(hmOrgEmpCnt.get(strOrgId)) +"</p></div>");
						hmInnerPrint.put(strOrgId, uF.parseToInt(hmOrgTaskCntDaywise.get(strOrgId+"_"+alDates.get(ii))) +"/"+ uF.parseToInt(hmOrgEmpCnt.get(strOrgId)) +"");
					} else {
						hmInner.put(strOrgId, "<div style=\"width: 100%; height: 100%; text-align: center; background-color: red;\"><p style=\"border-bottom:solid 1px black\">"+ uF.parseToInt(hmOrgTaskCntDaywise.get(strOrgId+"_"+alDates.get(ii))) +"</p><p>"+ uF.parseToInt(hmOrgEmpCnt.get(strOrgId)) +"</p></div>");
						hmInnerPrint.put(strOrgId, uF.parseToInt(hmOrgTaskCntDaywise.get(strOrgId+"_"+alDates.get(ii))) +"/"+ uF.parseToInt(hmOrgEmpCnt.get(strOrgId))+"");
					}
					
					alInner.add(uF.showData(hmInner.get(strOrgId), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\"><p style=\"border-bottom:1px solid black\">0</p><p>0</p></div>"));
					alInnerPrint.add(uF.showData(hmInnerPrint.get(strOrgId), "0"));
					
				}
				
				reportList.add(alInner);
				reportListPrint.add(alInnerPrint);
			}
			
			request.setAttribute("alDates", alDates);
			
			request.setAttribute("reportList", reportList);
			request.setAttribute("reportListPrint", reportListPrint);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}
	
	public String viewProjectEngagementBySbu(UtilityFunctions uF) {

		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);
		
		try {
			con = db.makeConnection(con);
			
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
			
			if(getStrMonth() == null){
				setStrMonth(uF.parseToInt(uF.getDateFormat(uF.getCurrentDate(CF.getStrTimeZone())+"", DBDATE, "MM"))+"");
			}
			setStrYear(uF.parseToInt(uF.getDateFormat(strCalendarYearEnd+"", DATE_FORMAT, "yyyy"))+"");
			
			List<String> alDates = new ArrayList<String>();
			
			Calendar cal = GregorianCalendar.getInstance();
			cal.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1);
			cal.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
			
			int minDays = cal.getActualMinimum(Calendar.DATE);
			int maxDays = cal.getActualMaximum(Calendar.DATE);
			
			cal.set(Calendar.DAY_OF_MONTH, minDays);
			
			String strD1 = null;
			for(int i=0; i<maxDays; i++) {
				strD1 = ((cal.get(Calendar.DAY_OF_MONTH) < 10) ? "0" + cal.get(Calendar.DAY_OF_MONTH) : cal.get(Calendar.DAY_OF_MONTH)) + "/" + (((cal.get(Calendar.MONTH) + 1) < 10) ? "0" + (cal.get(Calendar.MONTH) + 1) : (cal.get(Calendar.MONTH) + 1)) + "/"
						+ cal.get(Calendar.YEAR);
				alDates.add(uF.getDateFormat(strD1, DATE_FORMAT, DATE_FORMAT));
				cal.add(Calendar.DATE, 1);
			}
			
			
//			String startdate="01/"+uF.getDateFormat(""+currDate, DBDATE, "MM")+"/"+uF.getDateFormat(""+currDate, DBDATE, "yyyy");
//			Calendar calendar = Calendar.getInstance();
//			calendar.set(Calendar.MONTH, uF.parseToInt(getStrMonth()) -1);
//			calendar.set(Calendar.YEAR, uF.parseToInt(getStrYear()));
//			calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
//
//			Date date = calendar.getTime();
//			DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
//			String endDate=DATE_FORMAT.format(date);
			
			
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select a.emp_id,s.service_id from (select eod.emp_id,eod.service_id from employee_personal_details epd,employee_official_details eod " +
					"where epd.emp_per_id=eod.emp_id and epd.is_alive=true and (eod.service_id is not null or eod.service_id !='') " +
					"and extract(month from joining_date)<="+uF.parseToInt(getStrMonth())+" and extract(year from joining_date)<="+uF.parseToInt(getStrYear())+" ");
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append(" and eod.org_id = "+uF.parseToInt(getF_org()));
			}else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null){
					sbQuery.append(" and eod.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
	            sbQuery.append(" and eod.wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
	        }else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and eod.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and eod.depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			sbQuery.append(") a, services s where a.service_id like '%'||','||s.service_id||','||'%' and s.service_id > 0 and a.emp_id>0 order by s.service_id");
			pst = con.prepareStatement(sbQuery.toString()); 
//			System.out.println("pst=====>"+pst);
			rs = pst.executeQuery();
			Map<String, String> hmServiceEmpCnt = new HashMap<String, String>();
			Map<String, String> hmServiceEmp = new HashMap<String, String>();
			while(rs.next()) {
				int nEmp = uF.parseToInt(hmServiceEmpCnt.get(rs.getString("service_id")));
				nEmp++;
				hmServiceEmpCnt.put(rs.getString("service_id"),""+nEmp);
				
				String sbServiceEmp = hmServiceEmp.get(rs.getString("service_id"));
				if(sbServiceEmp == null || sbServiceEmp.trim().equals("")){
					sbServiceEmp = rs.getString("emp_id");
				} else {
					sbServiceEmp +=","+ rs.getString("emp_id");
				}
				hmServiceEmp.put(rs.getString("service_id"),sbServiceEmp);
			}
			rs.close();
			pst.close();
			
			Iterator<String> it = hmServiceEmp.keySet().iterator();
			Map<String, String> hmSbuTaskCntDaywise = new HashMap<String, String>();
			while(it.hasNext()){
				String strServiceId = it.next();
				String strResource = hmServiceEmp.get(strServiceId);
				String[] strTemp = null;
				if(strResource!=null){
					strTemp = strResource.split(",");
				}
				
				StringBuilder sbResource = null;
				if(strTemp!=null && strTemp.length>0){
					sbResource = new StringBuilder();
					sbResource.append(" and (");
	                for(int i=0; i<strTemp.length; i++){
	                	sbResource.append(" resource_ids like '%,"+strTemp[i]+",%'");
	                    
	                    if(i<strTemp.length-1){
	                    	sbResource.append(" OR "); 
	                    }
	                }
	                sbResource.append(" ) ");
				}   
				
				if(sbResource!=null && sbResource.length() > 0){
					for(int i=0; i<alDates.size(); i++) {
						sbQuery = new StringBuilder();
						sbQuery.append("select count(task_id) as task_no from activity_info where task_id>0 "+sbResource.toString()+" and task_id not in (select " +
							"parent_task_id from activity_info where task_id>0 "+sbResource.toString()+" and parent_task_id is not null) and ? between start_date " +
							"and deadline and (completed < 100 or completed is null) and pro_id in (select pro_id from projectmntnc where sbu_id = ?) ");
						pst = con.prepareStatement(sbQuery.toString());
						pst.setDate(1, uF.getDateFormat(alDates.get(i), DATE_FORMAT)); 
						pst.setInt(2, uF.parseToInt(strServiceId));
//						System.out.println("pst ===>> " + pst);
						rs = pst.executeQuery();
						while(rs.next()) {
							int sbuTaskCnt = uF.parseToInt(hmSbuTaskCntDaywise.get(strServiceId+"_"+alDates.get(i)));
							sbuTaskCnt += rs.getInt("task_no");
							hmSbuTaskCntDaywise.put(strServiceId+"_"+alDates.get(i), sbuTaskCnt+"");
						}
						rs.close();
						pst.close();
					}
				}
			}
			
			pst = con.prepareStatement("select * from services order by service_name"); 
			rs = pst.executeQuery();
			Map<String, String> hmServices = new LinkedHashMap<String, String>();
			while(rs.next()) {
				hmServices.put(rs.getString("service_id"), rs.getString("service_name"));
			}
			rs.close();
			pst.close();
			
			List<List<String>> reportList = new ArrayList<List<String>>();
			List<List<String>> reportListPrint = new ArrayList<List<String>>();
			List<String> alInner = new ArrayList<String>();
			List<String> alInnerPrint = new ArrayList<String>();
			
			Iterator<String> it1 = hmServices.keySet().iterator();
			while(it1.hasNext()){
				String strSbuId = it1.next();
				String strSbuName = hmServices.get(strSbuId);
				
				alInner = new ArrayList<String>();
				alInnerPrint = new ArrayList<String>();
				
				alInner.add(uF.showData(strSbuName,""));
				alInnerPrint.add(uF.showData(strSbuName,""));
				
				for (int ii=0; ii<alDates.size(); ii++) {
					Map<String, String> hmInner  = new HashMap<String, String>();
					Map<String, String> hmInnerPrint  = new HashMap<String, String>();
					
					double dblActualCount = uF.parseToDouble(hmServiceEmpCnt.get(strSbuId));
					double dblTaskCount = uF.parseToDouble(hmSbuTaskCntDaywise.get(strSbuId+"_"+alDates.get(ii)));
					
					double dblRatio = 0;
					
					if(dblActualCount > 0) {
						dblRatio = dblTaskCount / dblActualCount;
					}
					
					if (dblRatio > 0.8) {
						hmInner.put(strSbuId, "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\"><p style=\"border-bottom:solid 1px black\">"+ uF.parseToInt(hmSbuTaskCntDaywise.get(strSbuId+"_"+alDates.get(ii))) +"</p><p>"+ uF.parseToInt(hmServiceEmpCnt.get(strSbuId)) +"</p></div>");
						hmInnerPrint.put(strSbuId, uF.parseToInt(hmSbuTaskCntDaywise.get(strSbuId+"_"+alDates.get(ii))) +"/"+ uF.parseToInt(hmServiceEmpCnt.get(strSbuId))+"");
					} else if(dblRatio > 0.40) {
						hmInner.put(strSbuId, "<div style=\"width: 100%; height: 100%; text-align: center; background-color: yellow;\"><p style=\"border-bottom:solid 1px black\">"+ uF.parseToInt(hmSbuTaskCntDaywise.get(strSbuId+"_"+alDates.get(ii))) +"</p><p>"+ uF.parseToInt(hmServiceEmpCnt.get(strSbuId)) +"</p></div>");
						hmInnerPrint.put(strSbuId, uF.parseToInt(hmSbuTaskCntDaywise.get(strSbuId+"_"+alDates.get(ii))) +"/"+ uF.parseToInt(hmServiceEmpCnt.get(strSbuId)) +"");
					} else if(dblActualCount > 0) {
						hmInner.put(strSbuId, "<div style=\"width: 100%; height: 100%; text-align: center; background-color: red;\"><p style=\"border-bottom:solid 1px black\">"+ uF.parseToInt(hmSbuTaskCntDaywise.get(strSbuId+"_"+alDates.get(ii))) +"</p><p>"+ uF.parseToInt(hmServiceEmpCnt.get(strSbuId)) +"</p></div>");
						hmInnerPrint.put(strSbuId, uF.parseToInt(hmSbuTaskCntDaywise.get(strSbuId+"_"+alDates.get(ii))) +"/"+ uF.parseToInt(hmServiceEmpCnt.get(strSbuId))+"");
					} else {
						hmInner.put(strSbuId, "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgray;\"><p style=\"border-bottom:solid 1px black\">"+ uF.parseToInt(hmSbuTaskCntDaywise.get(strSbuId+"_"+alDates.get(ii))) +"</p><p>"+ uF.parseToInt(hmServiceEmpCnt.get(strSbuId)) +"</p></div>");
						hmInnerPrint.put(strSbuId, uF.parseToInt(hmSbuTaskCntDaywise.get(strSbuId+"_"+alDates.get(ii))) +"/"+ uF.parseToInt(hmServiceEmpCnt.get(strSbuId))+"");
					}
					
					alInner.add(uF.showData(hmInner.get(strSbuId), "<div style=\"width: 100%; height: 100%; text-align: center; background-color: lightgreen;\"><p style=\"border-bottom:1px solid black\">0</p><p>0</p></div>"));
					alInnerPrint.add(uF.showData(hmInnerPrint.get(strSbuId), "0"));
					
				}
				
				reportList.add(alInner);
				reportListPrint.add(alInnerPrint);
			}
			
			request.setAttribute("alDates", alDates);
			
			request.setAttribute("reportList", reportList);
			request.setAttribute("reportListPrint", reportListPrint);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		return SUCCESS;
	}

	public String loadProjectEngagement(UtilityFunctions uF) {
		calendarYearList = new FillCalendarYears(request).fillCalendarYears(CF);
		monthList = new FillMonth().fillMonth();
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
				
		getSelectedFilter(uF);
		
		if(getBtnSubmit() != null) {
			return SUCCESS;
		} else {
			return LOAD;
		}
	}
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("ORGANISATION");
		if(getF_org()!=null)  {
			String strOrg="";
			int k=0;
			for(int i=0;organisationList!=null && i<organisationList.size();i++){
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
		
		
		alFilter.add("CALENDARYEAR");
		String[] strCalendarYearDates = null;
		if (getCalendarYear() != null) {
			strCalendarYearDates = getCalendarYear().split("-");
			setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
		} else {
			strCalendarYearDates = new FillCalendarYears(request).fillLatestCalendarYears();
			setCalendarYear(strCalendarYearDates[0] + "-" + strCalendarYearDates[1]);
		}
		hmFilter.put("CALENDARYEAR", uF.getDateFormat(strCalendarYearDates[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strCalendarYearDates[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		
		alFilter.add("MONTH");
		int nselectedMonth = uF.parseToInt(getStrMonth());
		String strMonth = uF.getMonth(nselectedMonth);
		hmFilter.put("MONTH", strMonth);
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	private HttpServletRequest request;

	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;

	}

	public String getStrOrgId() {
		return strOrgId;
	}

	public void setStrOrgId(String strOrgId) {
		this.strOrgId = strOrgId;
	}

	public String getParamSelection() {
		return paramSelection;
	}

	public void setParamSelection(String paramSelection) {
		this.paramSelection = paramSelection;
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

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
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

	public String getBtnSubmit() {
		return btnSubmit;
	}

	public void setBtnSubmit(String btnSubmit) {
		this.btnSubmit = btnSubmit;
	}

}
