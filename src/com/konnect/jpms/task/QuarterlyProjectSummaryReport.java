package com.konnect.jpms.task;

import java.io.ByteArrayOutputStream;
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
import com.konnect.jpms.select.FillCalendarYears;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillProject;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;

public class QuarterlyProjectSummaryReport extends ActionSupport implements ServletRequestAware, ServletResponseAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	CommonFunctions CF;
	String strUserType;
	
	String strStartDate;
	String strEndDate;
	
	String strLocation;
	String strDepartment;
	String strSbu;
	String strLevel;
	
	String f_org;
	String[] f_wLocation; 
	String[] f_level;
	String[] f_department;
	String[] f_service;
	String[] tmpMonths;
	String[] f_project;
	String calendarYear;
	String strMonth;
	String strQuarter;
	String exportType;
	
	List<FillCalendarYears> calendarYearList;
	List<FillMonth> monthList;
	List<FillOrganisation> organisationList;
	List<FillWLocation> wLocationList;
	List<FillDepartment> departmentList;
	List<FillLevel> levelList;
	List<FillServices> serviceList;
	List<FillProject> projectList;
	
	public String execute() throws Exception {
		UtilityFunctions uF = new UtilityFunctions(); 
		session = request.getSession();
		CF = (CommonFunctions)session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		strUserType =(String) session.getAttribute(BASEUSERTYPE);
		
		request.setAttribute(PAGE, "/jsp/task/QuarterlyProjectSummaryReport.jsp");
		request.setAttribute(TITLE, "Quarterly Summary Report");
		
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
		
		viewQuarterlyTimesheetSummaryReport(uF);
		if(getExportType()!= null && getExportType().equalsIgnoreCase("excel")){
			generateQuarterlySummaryExcelReport(uF);
		}
		
		return loadQuarterlyProjectSummaryReport(uF);
	}
	
	private void viewQuarterlyTimesheetSummaryReport(UtilityFunctions uF) {
		
		Connection con = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			con=db.makeConnection(con);
			
			StringBuilder sbQuery = new StringBuilder();
			
			List<List<String>> alOuter = new ArrayList<List<String>>();
			
			String[] strPayCycleDates = null;
			String strCalenderYearStart = null;
			String strCalenderYearEnd = null;

			if (getCalendarYear() != null) {
				
				strPayCycleDates = getCalendarYear().split("-");
				strCalenderYearStart = strPayCycleDates[0];
				strCalenderYearEnd = strPayCycleDates[1];
			
			} else {
				
				strPayCycleDates = new FillCalendarYears(request).fillLatestCalendarYears();
				setCalendarYear(strPayCycleDates[0] + "-" + strPayCycleDates[1]);
				
				strCalenderYearStart = strPayCycleDates[0];
				strCalenderYearEnd = strPayCycleDates[1];
				 
			}
			
			String quarterNo = "";
			String currDate = uF.getDateFormat("" + uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			String currMonth = uF.getMonthInt(uF.getDateFormatUtil(currDate, DATE_FORMAT))+"";
			String months = null;
			if(getStrMonth()!=null){
				if(getStrMonth().equals("1,2,3")){
					quarterNo = "Q1";
					months = "1,2,3";
					setStrQuarter("01/01/"+uF.parseToInt(uF.getDateFormat(strCalenderYearEnd, DATE_FORMAT, "yyyy"))+" to 31/03/"+uF.parseToInt(uF.getDateFormat(strCalenderYearEnd, DATE_FORMAT, "yyyy"))+"");
				} else if(getStrMonth().equals("4,5,6")){
					quarterNo = "Q2";
					months = "4,5,6";
					setStrQuarter("01/04/"+uF.parseToInt(uF.getDateFormat(strCalenderYearStart, DATE_FORMAT, "yyyy"))+" to 30/06/"+uF.parseToInt(uF.getDateFormat(strCalenderYearStart, DATE_FORMAT, "yyyy"))+"");
				} else if(getStrMonth().equals("7,8,9")){
					quarterNo = "Q3";
					months = "7,8,9";
					setStrQuarter("01/07/"+uF.parseToInt(uF.getDateFormat(strCalenderYearStart, DATE_FORMAT, "yyyy"))+" to 30/09/"+uF.parseToInt(uF.getDateFormat(strCalenderYearStart, DATE_FORMAT, "yyyy"))+"");
				} else {
					quarterNo = "Q4";
					months = "10,11,12";
					setStrQuarter("01/10/"+uF.parseToInt(uF.getDateFormat(strCalenderYearStart, DATE_FORMAT, "yyyy"))+" to 31/12/"+uF.parseToInt(uF.getDateFormat(strCalenderYearStart, DATE_FORMAT, "yyyy"))+"");
				}
			} else {
//				months = "1,2,3";
//				setStrQuarter("01/01/"+uF.parseToInt(uF.getDateFormat(strCalenderYearEnd, DATE_FORMAT, "yyyy"))+" to 31/03/"+uF.parseToInt(uF.getDateFormat(strCalenderYearEnd, DATE_FORMAT, "yyyy"))+"");
				if(currMonth.equals("1") || currMonth.equals("2") || currMonth.equals("3")){
					quarterNo = "Q1";
					months = "1,2,3";
					setStrQuarter("01/01/"+uF.parseToInt(uF.getDateFormat(strCalenderYearEnd, DATE_FORMAT, "yyyy"))+" to 31/03/"+uF.parseToInt(uF.getDateFormat(strCalenderYearEnd, DATE_FORMAT, "yyyy"))+"");
					setStrMonth("1,2,3");
				} else if(currMonth.equals("4") || currMonth.equals("5") || currMonth.equals("6")){
					quarterNo = "Q2";
					months = "4,5,6";
					setStrQuarter("01/04/"+uF.parseToInt(uF.getDateFormat(strCalenderYearStart, DATE_FORMAT, "yyyy"))+" to 30/06/"+uF.parseToInt(uF.getDateFormat(strCalenderYearStart, DATE_FORMAT, "yyyy"))+"");
					setStrMonth("4,5,6");
				} else if(currMonth.equals("7") || currMonth.equals("8") || currMonth.equals("9")){
					quarterNo = "Q3";
					months = "7,8,9";
					setStrQuarter("01/07/"+uF.parseToInt(uF.getDateFormat(strCalenderYearStart, DATE_FORMAT, "yyyy"))+" to 30/09/"+uF.parseToInt(uF.getDateFormat(strCalenderYearStart, DATE_FORMAT, "yyyy"))+"");
					setStrMonth("7,8,9");
				} else{
					quarterNo = "Q4";
					months = "10,11,12";
					setStrQuarter("01/10/"+uF.parseToInt(uF.getDateFormat(strCalenderYearStart, DATE_FORMAT, "yyyy"))+" to 31/12/"+uF.parseToInt(uF.getDateFormat(strCalenderYearStart, DATE_FORMAT, "yyyy"))+"");
					setStrMonth("10,11,12");
				}
			}
			
			tmpMonths = months.split(",");
			String [] str = getStrQuarter().split("to");
			
			Map<String,String> hmBillableHrs = new HashMap<String, String>();
			Map<String,String> hmNonBillableHrs = new HashMap<String, String>();
			Map<String,String> hmProjectName = new HashMap<String, String>();
			Map<String,List<String>> hmAlProjects = new HashMap<String, List<String>>();
			
			/*sbQuery.append("select * from activity_info ai,task_activity ta,projectmntnc p where ai.task_id=ta.activity_id and ai.pro_id=p.pro_id ");
			if(uF.parseToInt(getF_org())>0) {
				sbQuery.append(" and p.org_id in ("+getF_org()+")");
			} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
				sbQuery.append(" and p.org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
			}
			if(getF_wLocation()!=null && getF_wLocation().length>0) {
				sbQuery.append(" and p.wlocation_id in ("+StringUtils.join(getF_wLocation(), ",")+")");
	        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
	        	sbQuery.append(" and p.wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			if(getF_department() != null && getF_department().length>0) {
				sbQuery.append(" and p.department_id in ("+StringUtils.join(getF_department(), ",")+") ");
			}
			
			if(getF_service() != null && getF_service().length>0) {
				sbQuery.append(" and p.sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
			}
			
			sbQuery.append(" and task_date between '" + uF.getDateFormat(str[0], DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(str[1], DATE_FORMAT, DBDATE) + "' ");
			
			sbQuery.append(" order by ta.task_date ");*/
			sbQuery.append("select * from activity_info ai,task_activity ta where ai.task_id=ta.activity_id ");
			sbQuery.append(" and task_date between '" + uF.getDateFormat(str[0], DATE_FORMAT, DBDATE) + "' and '"+ uF.getDateFormat(str[1], DATE_FORMAT, DBDATE) + "' ");
			
			sbQuery.append(" order by ta.task_date ");
			
			pst = con.prepareStatement(sbQuery.toString());
//			System.out.println("pst===>"+pst);
			rs = pst.executeQuery();
			while(rs.next()) {
				if(uF.parseToBoolean(rs.getString("is_billable"))){
					String strTaskDate = uF.getDateFormat(rs.getString("task_date"), DBDATE, "MM/yyyy");
					String billHrs = hmBillableHrs.get(rs.getString("pro_id")+"_"+strTaskDate);
					double totalBillHrs = uF.parseToDouble(rs.getString("actual_hrs"))+uF.parseToDouble(billHrs);
					hmBillableHrs.put(rs.getString("pro_id")+"_"+strTaskDate, totalBillHrs+"");
				} else{
					String strTaskDate = uF.getDateFormat(rs.getString("task_date"), DBDATE, "MM/yyyy");
					String nbillHrs = hmBillableHrs.get(rs.getString("pro_id")+"_"+strTaskDate);
					double nTotalBillHrs = uF.parseToDouble(rs.getString("actual_hrs"))+uF.parseToDouble(nbillHrs);
					hmNonBillableHrs.put(rs.getString("pro_id")+"_"+strTaskDate, nTotalBillHrs+"");
				}
				/*hmProjectName.put(rs.getString("pro_id"), rs.getString("pro_name"));
				
				List<String> innPro = hmAlProjects.get(uF.getDateFormat(rs.getString("task_date"), DBDATE, "MM/yyyy"));
				if(innPro == null)
					innPro = new ArrayList<String>();
				if(!innPro.contains(rs.getString("pro_id"))){
					innPro.add(rs.getString("pro_id"));
				}
				hmAlProjects.put(uF.getDateFormat(rs.getString("task_date"), DBDATE, "MM/yyyy"), innPro);*/
			}
			rs.close();
			pst.close();
			
			Map<String,List<String>> hmProEmpIds = new HashMap<String, List<String>>();
			pst = con.prepareStatement("select * from project_emp_details");
			rs = pst.executeQuery();
			while(rs.next()) {
				List<String> alEmpIds= hmProEmpIds.get(rs.getString("pro_id"));
				if(alEmpIds == null) {
					alEmpIds = new ArrayList<String>();
				}
				alEmpIds.add(rs.getString("emp_id"));
				hmProEmpIds.put(rs.getString("pro_id"), alEmpIds);
			}
			rs.close();
			pst.close();
			
			List<String> monthYearsList = new ArrayList<String>();
			
			Date startDate = uF.getDateFormat(str[0], DATE_FORMAT);
			Date endDate1 = uF.getDateFormat(str[1], DATE_FORMAT);
			
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
		    
		    
		    Iterator<String> itr = monthYearsList.iterator();
		    List<String> innerList = new ArrayList<String>();
		    List<String> innerList1 = new ArrayList<String>();
		    List<String> ptoInnerList = new ArrayList<String>();
		    List<String> totInnerList = new ArrayList<String>();
		    int size = 0;
		    double totBillableHrs = 0;
		    double totNonBillableHrs = 0;
		    double totalPotHrs = 0;
		    
		    innerList.add("Billable Hours");
		    innerList1.add("Non-Billable Hours");
		    ptoInnerList.add("PTO");
		    totInnerList.add("Total Hours");
		    
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
		    	
		    	sbQuery = new StringBuilder();
		    	sbQuery.append("select * from projectmntnc where pro_id>0 ");
		    	if(uF.parseToInt(getF_org())>0) {
					sbQuery.append(" and org_id in ("+getF_org()+")");
				} else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(ORG_ACCESS)!=null) {
					sbQuery.append(" and org_id in ("+(String)session.getAttribute(ORG_ACCESS)+")");
				}
		    	
				if(getF_wLocation()!=null && getF_wLocation().length>0) {
					sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_wLocation(), ",")+")");
		        } else if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN) && (String)session.getAttribute(WLOCATION_ACCESS)!=null) {
		        	sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
				}
				
				if(getF_department() != null && getF_department().length>0) {
					sbQuery.append(" and department_id in ("+StringUtils.join(getF_department(), ",")+") ");
				}
				
				if(getF_service() != null && getF_service().length>0) {
					sbQuery.append(" and sbu_id in ("+StringUtils.join(getF_service(), ",")+") ");
				}
				if(strFirstDate != null && !strFirstDate.equalsIgnoreCase("null") && !strFirstDate.equals("") && strEndDate != null && !strEndDate.equalsIgnoreCase("null") && !strEndDate.equals("")) {
					sbQuery.append(" and ((start_date <= '" + uF.getDateFormat(strFirstDate, DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(strFirstDate, DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (start_date >= '" + uF.getDateFormat(strFirstDate, DATE_FORMAT, DBDATE) + "' and start_date <= '"+ uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (start_date >= '" + uF.getDateFormat(strFirstDate, DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (start_date <= '" + uF.getDateFormat(strFirstDate, DATE_FORMAT, DBDATE) + "' and deadline >= '"+ uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE) + "') ");
					sbQuery.append(" or (deadline >= '" + uF.getDateFormat(strFirstDate, DATE_FORMAT, DBDATE) + "' and deadline <= '"+ uF.getDateFormat(strEndDate, DATE_FORMAT, DBDATE) + "')) ");
				}
				pst = con.prepareStatement(sbQuery.toString());
//				System.out.println("QPSR/362---pst=="+pst);
				rs = pst.executeQuery();
				while(rs.next()) {
					hmProjectName.put(rs.getString("pro_id"), rs.getString("pro_name"));
					
					List<String> innPro = hmAlProjects.get(month);
					if(innPro == null)
						innPro = new ArrayList<String>();
					if(!innPro.contains(rs.getString("pro_id"))){
						innPro.add(rs.getString("pro_id"));
					}
					hmAlProjects.put(month, innPro);
					
				}
				rs.close();
				pst.close();
				
				List<String> alProIds = hmAlProjects.get(month);
		    	if(alProIds!=null && !alProIds.isEmpty() && alProIds.size()>0){
		    		for(int i=0; alProIds!=null && i<alProIds.size(); i++){
			    		innerList.add(hmBillableHrs.get(alProIds.get(i)+"_"+month));
			    		innerList1.add(hmNonBillableHrs.get(alProIds.get(i)+"_"+month));
			    		totBillableHrs += uF.parseToDouble(hmBillableHrs.get(alProIds.get(i)+"_"+month));
			    		totNonBillableHrs += uF.parseToDouble(hmNonBillableHrs.get(alProIds.get(i)+"_"+month));
			    		
			    		double ptoHrs = 0;
			    		List<String> alEmpIds = hmProEmpIds.get(alProIds.get(i));
			    		for(int j=0; alEmpIds!=null && j<alEmpIds.size();j++){
			    			pst = con.prepareStatement("select * from leave_application_register where emp_id=? and to_date(_date::text,'yyyy-MM-dd') between ? and ? "
										+ " and (is_modify is null or is_modify=false)");
							pst.setInt(1, uF.parseToInt(alEmpIds.get(j)));
							pst.setDate(2, uF.getDateFormat(strFirstDate, DATE_FORMAT));
							pst.setDate(3, uF.getDateFormat(strEndDate, DATE_FORMAT));
//							System.out.println("pst ===>> " + pst);
							rs = pst.executeQuery();
							while (rs.next()) {
								ptoHrs += Math.abs(uF.parseToDouble(rs.getString("leave_no")))*8;
							}
							rs.close();
							pst.close();
			    		}
			    		totalPotHrs += ptoHrs;
//			    		System.out.println("ptoHrs="+ptoHrs);
			    		ptoInnerList.add(ptoHrs+"");
			    		double total = uF.parseToDouble(hmBillableHrs.get(alProIds.get(i)+"_"+month))+uF.parseToDouble(hmNonBillableHrs.get(alProIds.get(i)+"_"+month))+ptoHrs;
			    		totInnerList.add(total+"");
			    	}
		    	}else{
		    		innerList.add("");
		    		innerList1.add("");
		    		ptoInnerList.add("");
		    		totInnerList.add("");
		    	}
		    	
		    }
		    innerList.add(totBillableHrs+"");
		    innerList1.add(totNonBillableHrs+"");
		    ptoInnerList.add(totalPotHrs+"");
		    double total1 = totBillableHrs+totNonBillableHrs+totalPotHrs;
		    totInnerList.add(total1+"");
		    
		    alOuter.add(innerList);
		    alOuter.add(innerList1);
		    alOuter.add(ptoInnerList);
		    alOuter.add(totInnerList);
//		    System.out.println("alOuter=="+alOuter);
			request.setAttribute("alOuter", alOuter);
			request.setAttribute("monthYearsList", monthYearsList);
			request.setAttribute("hmAlProjects", hmAlProjects);
			request.setAttribute("hmProjectName", hmProjectName);
			request.setAttribute("quarterNo", quarterNo);
			
		} catch (Exception e) {
			e.printStackTrace(); 
		} finally {
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
	}
	
	private void generateQuarterlySummaryExcelReport(UtilityFunctions uF) {
		try {
			List<List<String>> alOuter = (List<List<String>>)request.getAttribute("alOuter");
			if(alOuter == null) alOuter = new ArrayList<List<String>>();
			List<String> monthYearsList = (List<String>) request.getAttribute("monthYearsList");
			
			Map<String,List<String>> hmAlProjects = (Map<String,List<String>>)request.getAttribute("hmAlProjects");
			if(hmAlProjects == null) hmAlProjects = new HashMap<String, List<String>>();
			Map<String,String> hmProjectName = (Map<String,String>)request.getAttribute("hmProjectName");
			if(hmProjectName == null) hmProjectName = new HashMap<String, String>();
			
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet sheet = workbook.createSheet("Quarterly Summary Report");			
			
			List<DataStyle> header=new ArrayList<DataStyle>();
			List<DataStyle> subHeader=new ArrayList<DataStyle>();
			List<List<DataStyle>> reportData=new ArrayList<List<DataStyle>>();
			
			header.add(new DataStyle("Summary of "+uF.showData((String)request.getAttribute("quarterNo"),"")+" hours-"+uF.getDateFormat(monthYearsList.get(1),"MM/yyyy","yyyy"),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	//0
			for(int i=0;i<monthYearsList.size();i++){
				List alProIds = hmAlProjects.get(monthYearsList.get(i));
				int strSize = alProIds!=null ? alProIds.size() : 1;
//				header.add(new DataStyle(monthYearsList.get(i),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				header.add(new DataStyle(monthYearsList.get(i)+"_"+strSize,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				for(int j=1; alProIds!=null && j<alProIds.size(); j++){
//					header.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
//				}
				
			}
			for(int i=0;i<monthYearsList.size();i++){
				List alProIds = hmAlProjects.get(monthYearsList.get(i));
				for(int j=0; alProIds!=null && j<alProIds.size(); j++){
					subHeader.add(new DataStyle(uF.showData(hmProjectName.get(alProIds.get(j)),""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}
				
			}
			
			for(int i=0; alOuter!=null && i<alOuter.size();i++){
				List<String> alInner = (List<String>)alOuter.get(i);
				List<DataStyle> innerList=new ArrayList<DataStyle>();
				for(int j=0; alInner!=null && j<alInner.size();j++){
					innerList.add(new DataStyle((String)alInner.get(j),Element.ALIGN_LEFT,"NEW_ROMAN",10,"0","0",BaseColor.WHITE));
				}
				reportData.add(innerList);
			}
			
			ExcelSheetDesign sheetDesign=new ExcelSheetDesign();
			sheetDesign.generateExcelSheetforQuarterlyProjectReport(workbook,sheet,header,subHeader,reportData);
			
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			workbook.write(buffer);
			response.setContentType("application/vnd.ms-excel:UTF-8");
			response.setContentLength(buffer.size());
			response.setHeader("Content-Disposition","attachment; filename=QuarterlyProjectSummary.xls");
			ServletOutputStream out = response.getOutputStream();
			buffer.writeTo(out);
			out.flush();
			buffer.close();
			out.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String loadQuarterlyProjectSummaryReport(UtilityFunctions uF){	
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		calendarYearList = new FillCalendarYears(request).fillCalendarYears(CF);	
		monthList = new FillMonth().fillQuarterlyMonthNew();
		
		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(),(String) session.getAttribute(WLOCATION_ACCESS));
			organisationList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			organisationList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		projectList= new FillProject(request).fillProjects();
		
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
		
		alFilter.add("PROJECT");
		if(getF_project()!=null) {
				String strProject="";
				int k=0;
				for(int i=0; projectList!=null && i<projectList.size();i++) {
					for(int j=0;j<getF_project().length;j++) {
						if(getF_project()[j].equals(projectList.get(i).getId())) {
							if(k==0) {
								strProject=projectList.get(i).getName();
							} else {
								strProject+=", "+projectList.get(i).getName();
							}
							k++;
						}
					}
				}
			if(strProject!=null && !strProject.equals("")) {
				hmFilter.put("PROJECT", strProject);
			} else {
				hmFilter.put("PROJECT", "All Projects");
			}
		} else {
			hmFilter.put("PROJECT", "All Project");
		}
		
		if((getStrStartDate()!=null && !getStrStartDate().equals(""))  && (getStrEndDate()!=null && !getStrEndDate().equals(""))){
			alFilter.add("FROMTO");
			hmFilter.put("FROMTO", uF.getDateFormat(getStrStartDate(), DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(getStrEndDate(), DATE_FORMAT, CF.getStrReportDateFormat()));
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
		
		
		
		StringBuilder strBuilder = new StringBuilder();
		alFilter.add("MONTH");
		for (int i = 0; i < tmpMonths.length; i++) {
		   int nselectedMonth = uF.parseToInt(tmpMonths[i]);
		   String strMonth = uF.getMonth(nselectedMonth);
		   strBuilder.append(strMonth + ",");
		}
		String newString = strBuilder.toString();
		hmFilter.put("MONTH", newString);
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
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

	public String getCalendarYear() {
		return calendarYear;
	}

	public void setCalendarYear(String calendarYear) {
		this.calendarYear = calendarYear;
	}

	public String getStrMonth() {
		return strMonth;
	}

	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}

	public String getStrQuarter() {
		return strQuarter;
	}

	public void setStrQuarter(String strQuarter) {
		this.strQuarter = strQuarter;
	}

	public List<FillCalendarYears> getCalendarYearList() {
		return calendarYearList;
	}

	public void setCalendarYearList(List<FillCalendarYears> calendarYearList) {
		this.calendarYearList = calendarYearList;
	}

	public List<FillMonth> getMonthList() {
		return monthList;
	}

	public void setMonthList(List<FillMonth> monthList) {
		this.monthList = monthList;
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

	public String[] getF_project() {
		return f_project;
	}

	public void setF_project(String[] f_project) {
		this.f_project = f_project;
	}

	public String getExportType() {
		return exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}
	
}
