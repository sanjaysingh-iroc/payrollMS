package com.konnect.jpms.reports;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.konnect.jpms.export.DataStyle;
import com.konnect.jpms.select.FillDepartment;
import com.konnect.jpms.select.FillFinancialYears;
import com.konnect.jpms.select.FillLevel;
import com.konnect.jpms.select.FillMonth;
import com.konnect.jpms.select.FillOrganisation;
import com.konnect.jpms.select.FillPayCycles;
import com.konnect.jpms.select.FillServices;
import com.konnect.jpms.select.FillWLocation;
import com.konnect.jpms.util.CommonFunctions;
import com.konnect.jpms.util.ComparatorWeight;
import com.konnect.jpms.util.Database;
import com.konnect.jpms.util.IStatements;
import com.konnect.jpms.util.UtilityFunctions;
import com.opensymphony.xwork2.ActionSupport;

public class ConsolidateEmpSalaryYearlyReport extends  ActionSupport implements ServletRequestAware, IStatements {

	private static final long serialVersionUID = 1L;
	HttpSession session;
	String strEmpId = null;
	String strUserType = null;
	       
	CommonFunctions CF = null; 
	String profileEmpId;
	 
	String strD1 = null;
	String strD2 = null;
	String strPC = null;
	private String f_org;
	 

	private String strApprove;
	private String financialYear; 
	private String paycycle;
	private String approvePC;
	private String strMonth;
	private String []chbxApprove;
	private List<FillMonth> alMonthList;
	private List<FillOrganisation> orgList;	
	
	private String strLocation;
	private String strDepartment;
	private String strSbu;
	private String strLevel;
	
	private String []f_strWLocation;
	private String []f_level;
	private String []f_department;
	private String []f_service;
	
	private List<FillPayCycles> paycycleList ;
	private List<FillFinancialYears> financialYearList;
	private List<FillLevel> levelList;
	private List<FillWLocation> wLocationList;
	
	private List<FillDepartment> departmentList;
	private List<FillServices> serviceList;
	
	
	private static Logger log = Logger.getLogger(ConsolidateEmpSalaryYearlyReport.class);
	
	public String execute() throws Exception {
		
		UtilityFunctions uF = new UtilityFunctions();
		session = request.getSession();
			
		CF = (CommonFunctions) session.getAttribute(CommonFunctions);
		if(CF==null)return LOGIN;
		
		request.setAttribute(PAGE, "/jsp/reports/ConsolidateEmpSalaryYearlyReport.jsp");
		request.setAttribute(TITLE, "Consolidate Employee Salary Yearly Report");
		request.setAttribute("roundOffCondition", ""+uF.parseToInt(CF.getRoundOffCondtion()));
		
		try {
			
			strEmpId =(String) session.getAttribute(EMPID);
			strUserType =(String) session.getAttribute(USERTYPE);
			
			if(getF_strWLocation()==null){
				setF_strWLocation(((String)session.getAttribute(WLOCATIONID)).split(","));
			}
			
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
			
			/*boolean isView  = CF.getAccess(session, request, uF);
			if(!isView){
				request.setAttribute(PAGE, PAccessDenied);
				request.setAttribute(TITLE, TAccessDenied);
				return ACCESS_DENIED;
			}*/
			
			viewConsolidateEmpSalaryYearlyReport(uF);
			
			
			alMonthList = new FillMonth().fillMonth();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return loadPaySlips(uF);

	}
	
	
	public String loadPaySlips(UtilityFunctions uF){
		
		paycycleList = new FillPayCycles(request).fillPayCycles(CF,getF_org());
		financialYearList = new FillFinancialYears(request).fillFinancialYears(CF);
		alMonthList = new FillMonth().fillMonth();

		if(strUserType!=null && !strUserType.equalsIgnoreCase(ADMIN)){
			wLocationList = new FillWLocation(request).fillWLocation(getF_org(), (String)session.getAttribute(WLOCATION_ACCESS));
			orgList = new FillOrganisation(request).fillOrganisation((String)session.getAttribute(ORG_ACCESS));
		}else{
			orgList = new FillOrganisation(request).fillOrganisation();
			wLocationList = new FillWLocation(request).fillWLocation(getF_org());
		}
		levelList = new FillLevel(request).fillLevel(uF.parseToInt(getF_org()));
		serviceList = new FillServices(request).fillServices(getF_org(),uF);
		departmentList = new FillDepartment(request).fillDepartment(uF.parseToInt(getF_org()));
		
		getSelectedFilter(uF);
		
		return LOAD;
	}
	
	
	private void getSelectedFilter(UtilityFunctions uF) {
		Map<String,String> hmFilter=new HashMap<String, String>();
		List<String> alFilter = new ArrayList<String>();

		alFilter.add("FINANCIALYEAR");
		String[] strFinancialYears = null;
		if (getFinancialYear() != null) {
			strFinancialYears = getFinancialYear().split("-");
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		} else {
			strFinancialYears = CF.getFinancialYear(uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT), CF, uF);
			setFinancialYear(strFinancialYears[0] + "-" + strFinancialYears[1]);
		}
		hmFilter.put("FINANCIALYEAR", uF.getDateFormat(strFinancialYears[0], DATE_FORMAT, CF.getStrReportDateFormat()) +" - "+ uF.getDateFormat(strFinancialYears[1], DATE_FORMAT, CF.getStrReportDateFormat()));
		
		
		alFilter.add("ORGANISATION");
		if(getF_org()!=null) {
			String strOrg="";
			for(int i=0;orgList!=null && i<orgList.size();i++){
				if(getF_org().equals(orgList.get(i).getOrgId())) {
					strOrg=orgList.get(i).getOrgName();
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
		
		
		String selectedFilter=CF.getSelectedFilter2(CF,uF,alFilter,hmFilter);
		request.setAttribute("selectedFilter", selectedFilter);
	}
	
	
	public String viewConsolidateEmpSalaryYearlyReport(UtilityFunctions uF){
		
		Connection con = null;
		PreparedStatement pst=null;
		ResultSet rs = null;
		Database db = new Database();
		db.setRequest(request);

		try {
			
			String[] strFianacialDates = null;
			String strFinancialYearStart = null;
			String strFinancialYearEnd = null;

			if (getFinancialYear() != null) {				
				strFianacialDates = getFinancialYear().split("-");
				strFinancialYearStart = strFianacialDates[0];
				strFinancialYearEnd = strFianacialDates[1];			
			} else {				
				strFianacialDates = new FillFinancialYears(request).fillLatestFinancialYears();
				setFinancialYear(strFianacialDates[0] + "-" + strFianacialDates[1]);				
				strFinancialYearStart = strFianacialDates[0];
				strFinancialYearEnd = strFianacialDates[1];				 
			}
			
			con = db.makeConnection(con);
			
			
			Map<String, String> hmEmpJoiningDate=CF.getEmpJoiningDateMap(con, uF);
			Map<String,String> hmEmpMap = CF.getEmpNameMap(con,null,null);
			Map<String,String> hmSalaryDetails = CF.getSalaryHeadsMap(con);
			Map<String, String> hmEmpCode =CF.getEmpCodeMap(con);
			Map<String, String> hmEmpDepartment =CF.getEmpDepartmentMap(con);			
			Map<String, String> hmDept =CF.getDeptMap(con);
			Map<String, String> hmEmpPanNo =CF.getEmpPANNoMap(con);	//added by parvez
			
			Map<String, String> hmSalaryWeight = new HashMap<String, String>();
			Map<String, Boolean> hmSalaryVariable = new HashMap<String, Boolean>();

			pst = con.prepareStatement("select salary_head_id, weight,is_variable from salary_details order by level_id,weight");
			rs = pst.executeQuery();
			while(rs.next()){
				if(hmSalaryWeight.get(rs.getString("salary_head_id"))==null){
					hmSalaryWeight.put(rs.getString("salary_head_id"), rs.getString("weight"));
					hmSalaryVariable.put(rs.getString("salary_head_id"),uF.parseToBoolean(rs.getString("is_variable")));
				}
			}
			rs.close();
			pst.close();
			
			//System.out.println("hmSalaryVariable=====>"+hmSalaryVariable);
			
			pst=con.prepareStatement("select org_id,org_name from org_details");
			Map<String, String> hmOrg=new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmOrg.put(rs.getString("org_id"), rs.getString("org_name")); 
			}
			rs.close();
			pst.close();
			
			pst=con.prepareStatement("select emp_per_id,employment_end_date from employee_personal_details where employment_end_date is not null");
			Map<String, String> hmEmpEndDate=new HashMap<String, String>();
			rs=pst.executeQuery();
			while(rs.next()){
				hmEmpEndDate.put(rs.getString("emp_per_id"), uF.getDateFormat(rs.getString("employment_end_date"), DBDATE, DATE_FORMAT));
			}
			rs.close();
			pst.close();
			
		
			
			StringBuilder sbQuery = new StringBuilder();
			sbQuery.append("select * from payroll_generation pg, employee_official_details eod where eod.emp_id = pg.emp_id ");
			sbQuery.append(" and financial_year_from_date='" + uF.getDateFormat(strFinancialYearStart, DATE_FORMAT, DBDATE) + "' and financial_year_to_date='" + uF.getDateFormat(strFinancialYearEnd, DATE_FORMAT, DBDATE) + "' ");
			
			
			if(uF.parseToInt(getF_org())>0){
				sbQuery.append("and eod.org_id="+uF.parseToInt(getF_org()));
			}
			
			if(getF_strWLocation()!=null && getF_strWLocation().length>0){
				sbQuery.append(" and wlocation_id in ("+StringUtils.join(getF_strWLocation(), ",")+") ");
			}
			
			if(getF_department()!=null && getF_department().length>0){
				sbQuery.append(" and depart_id in ("+StringUtils.join(getF_department(), ",")+") ");
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
			
			if(getF_level()!=null && getF_level().length>0){
				sbQuery.append(" and grade_id in (select gd.grade_id from grades_details gd,  level_details ld, designation_details dd where gd.designation_id = dd.designation_id and dd.level_id = ld.level_id  and ld.level_id in ( "+StringUtils.join(getF_level(), ",")+") )");	
			}
			
			if((String)session.getAttribute(WLOCATION_ACCESS)!=null){
				sbQuery.append(" and wlocation_id in ("+(String)session.getAttribute(WLOCATION_ACCESS)+")");
			}
			
			sbQuery.append(" order by pg.emp_id,pg.year,pg.month,earning_deduction desc, salary_head_id");
			pst = con.prepareStatement(sbQuery.toString());
			
			
			//System.out.println("pst==="+pst);
			
			rs = pst.executeQuery();
			
			
			List<String> alEarningss = new ArrayList<String>();
			List<String> alDeductionss = new ArrayList<String>();
			
			List<ComparatorWeight> alEarnings = new ArrayList<ComparatorWeight>();
			List<ComparatorWeight> alDeductions = new ArrayList<ComparatorWeight>();
			
			Map<String,Map<String,String>> hmPayPayroll = new LinkedHashMap<String,Map<String,String>>();
			Map<String,List<String>> deptEmpMap=new LinkedHashMap<String,List<String>>();
			Map<String,Map<String,String>> empSalaryMap=new HashMap<String,Map<String,String>>();
			Map<String,String> payMothEmpMap=new LinkedHashMap<String,String>();
			while(rs.next()){
				
				List<String> empList=deptEmpMap.get(rs.getString("emp_id")+"_"+rs.getString("month"));
				if(empList==null)empList=new ArrayList<String>();
				if(!empList.contains(rs.getString("emp_id"))){
					empList.add(rs.getString("emp_id"));
				}
				
				deptEmpMap.put(rs.getString("emp_id")+"_"+rs.getString("month"),empList);
				payMothEmpMap.put(rs.getString("emp_id")+"_"+rs.getString("month"),uF.getMonth(uF.parseToInt(rs.getString("month")))+" "+rs.getString("year"));
				Map<String,String> salaryMap=empSalaryMap.get(rs.getString("emp_id")+"_"+rs.getString("month"));
				if(salaryMap==null)salaryMap=new HashMap<String,String>();
				salaryMap.put(rs.getString("salary_head_id"), rs.getString("amount"));
				
				
				
				if("E".equalsIgnoreCase(rs.getString("earning_deduction"))){
					double dblAmount = rs.getDouble("amount");
					double dblGross = uF.parseToDouble(salaryMap.get("GROSS"));
					dblGross += dblAmount;
					salaryMap.put("GROSS",  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblGross)));
					
					double dblNet = uF.parseToDouble(salaryMap.get("NET"));
					dblNet+=dblAmount;
					salaryMap.put("NET",  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblNet)));
				}else{
					double dblAmount = rs.getDouble("amount");
					double dblNet = uF.parseToDouble(salaryMap.get("NET"));
					dblNet-=dblAmount;
					salaryMap.put("NET",  uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),(dblNet)));
					
				}
				empSalaryMap.put(rs.getString("emp_id")+"_"+rs.getString("month"),salaryMap);
				
				
				if("E".equalsIgnoreCase(rs.getString("earning_deduction")) && !alEarningss.contains(rs.getString("salary_head_id")) && hmSalaryDetails.containsKey(rs.getString("salary_head_id"))){
					boolean isvariable=hmSalaryVariable.get(rs.getString("salary_head_id"))!=null ? hmSalaryVariable.get(rs.getString("salary_head_id")) : false; 
					alEarningss.add(rs.getString("salary_head_id"));
					alEarnings.add(new ComparatorWeight(rs.getString("salary_head_id"),uF.parseToInt(hmSalaryWeight.get(rs.getString("salary_head_id"))),isvariable));
					
				}else if("D".equalsIgnoreCase(rs.getString("earning_deduction")) && !alDeductionss.contains(rs.getString("salary_head_id")) && hmSalaryDetails.containsKey(rs.getString("salary_head_id"))){
					boolean isvariable=hmSalaryVariable.get(rs.getString("salary_head_id"))!=null ? hmSalaryVariable.get(rs.getString("salary_head_id")) : false;
					alDeductionss.add(rs.getString("salary_head_id"));
					alDeductions.add(new ComparatorWeight(rs.getString("salary_head_id"),uF.parseToInt(hmSalaryWeight.get(rs.getString("salary_head_id"))),isvariable));
				}

			}
			rs.close();
			pst.close();
			
			
//			System.out.println("alEarnings==="+alEarnings);
//			System.out.println("alDeductions==="+alDeductions);
			Collections.sort(alEarnings);
//			System.out.println("alEarnings==="+alEarnings);

//			System.out.println("salaryMap==="+empSalaryMap);
			
			List<List<DataStyle>> reportListExport = new ArrayList<List<DataStyle>>();
			List<DataStyle> alInnerExport = new ArrayList<DataStyle>();
			
			alInnerExport.add(new DataStyle(uF.showData(hmOrg.get(getF_org()), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			alInnerExport.add(new DataStyle("Consolidate Employee Salary Yearly Report from "+strFinancialYearStart+" - "+strFinancialYearEnd,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			String curr_date=uF.getDateFormat(""+uF.getCurrentDate(CF.getStrTimeZone()), DBDATE, DATE_FORMAT);
			alInnerExport.add(new DataStyle("Date- "+curr_date,Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
		   	alInnerExport.add(new DataStyle("Sr. No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Employee Code",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Employee Name",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Employee PAN No.",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		   	alInnerExport.add(new DataStyle("Joining Date",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
		   	alInnerExport.add(new DataStyle("Quit Date",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY)); 
			alInnerExport.add(new DataStyle("Pay Month",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			boolean flag=false;
			for (int ii=0; ii<alEarnings.size(); ii++){
				ComparatorWeight comparatorWeight=alEarnings.get(ii);
				
				if(flag!=comparatorWeight.isVariable()){
					flag=true;
					alInnerExport.add(new DataStyle("Gross Earning",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}
				alInnerExport.add(new DataStyle(((String)hmSalaryDetails.get(((ComparatorWeight)alEarnings.get(ii)).getStrName()))+"(+)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
			}
			alInnerExport.add(new DataStyle("Gross",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			for (int ii=0; ii<alDeductions.size(); ii++){
				alInnerExport.add(new DataStyle(((String)hmSalaryDetails.get(((ComparatorWeight)alDeductions.get(ii)).getStrName()))+"(-)",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				
			}
			alInnerExport.add(new DataStyle("Net",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			
			reportListExport.add(alInnerExport); 
			
			
			Set set = deptEmpMap.keySet();			
			Iterator it = set.iterator();
			List<List<String>> alReportList = new ArrayList<List<String>>();
			Map<String,String> GrandtotalList=new HashMap<String,String>();
			int count=0;
			List<String> empMonthList=null;
			
			String oldEmp=null;
			String newEmp= null;
			Map<String,String> totalSalaryHead=new HashMap<String,String>();
			int sizeCount=0;
			double dblNet=0;
			double dblGross=0;
			while(it.hasNext()){
				sizeCount++;
				String deptId = (String)it.next();
				
				List<String> empList=deptEmpMap.get(deptId);
//				double dblNet=0;
//				double dblGross=0;
//				Map<String,String> totalSalaryHead=new HashMap<String,String>();
				
				for(String emp:empList){
					newEmp=emp;
					
					
					if(oldEmp!=null && !newEmp.equals(oldEmp)){

						List<String> alReportInner = new ArrayList<String>();
						alReportInner.add(" ");
						alReportInner.add("");
						alReportInner.add("");
						alReportInner.add("");
						alReportInner.add("");
						alReportInner.add("");
						alReportInner.add("<strong>Total</strong>");				
						
						if(GrandtotalList.size()==0){
							GrandtotalList.put("1","");
							GrandtotalList.put("2","");
							GrandtotalList.put("3","");
							GrandtotalList.put("4","");
							GrandtotalList.put("5","");
							GrandtotalList.put("6","");
							GrandtotalList.put("7","Grand Total");				
						}
						
						
						
						alInnerExport=new ArrayList<DataStyle>();
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						alInnerExport.add(new DataStyle("Total",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						
						boolean flag1=false;
						double grossEarning=0;
						double GrandgrossEarning=0;
//						int mpkey=7;
						int mpkey=8;
						for(int i=0; i<alEarnings.size(); i++){
							ComparatorWeight comparatorWeight=alEarnings.get(i);
							
							if(flag1!=comparatorWeight.isVariable()){
								flag1=true;
								alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),grossEarning),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
								alReportInner.add("<strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),grossEarning)+"</strong>");
								GrandtotalList.put((mpkey++)+"", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),GrandgrossEarning));
							}
								grossEarning+=uF.parseToDouble(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()));
								alReportInner.add("<strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0")))+"</strong>");
								double a=uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))+uF.parseToDouble(GrandtotalList.get(mpkey+""));
								GrandgrossEarning+=a;
								GrandtotalList.put(mpkey+"", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),a));
								alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
								mpkey++;
							
			    		}
						alReportInner.add("<strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblGross)+"</strong>");
						alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblGross),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
						
						if(GrandtotalList.size()==0){
							GrandtotalList.put(mpkey+"",uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblGross));
						}else{
							double b=dblGross+uF.parseToDouble(GrandtotalList.get(mpkey+""));
							GrandtotalList.put(mpkey+"", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),b));
						}
						
						
			    		for(int i=0; i<alDeductions.size(); i++){
			    			alReportInner.add("<strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0")))+"</strong>");
			    			alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			    			
							double a=uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))+uF.parseToDouble(GrandtotalList.get(mpkey+1+i+""));
							GrandtotalList.put(mpkey+1+i+"",uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),a));
			    		}
			    		alReportInner.add("<strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet)+"</strong>");
			    		alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
			    		if(GrandtotalList.size()==0){
							GrandtotalList.put((mpkey+1+alDeductions.size())+"",uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet));
						}else{
							double a=dblNet+uF.parseToDouble(GrandtotalList.get((mpkey+1+alDeductions.size())+""));
							GrandtotalList.put((mpkey+1+alDeductions.size())+"",uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),a));
						}
			    		
			    		alReportList.add(alReportInner);
						
			    		reportListExport.add(alInnerExport);
			    		
			    		totalSalaryHead=new HashMap<String,String>();			    		
					}
					
					
					count++;
					Map<String,String> salaryMap=empSalaryMap.get(deptId);
					List<String> alReportInner = new ArrayList<String>();
					alReportInner.add(""+count);
					alReportInner.add(hmEmpCode.get(emp));
					alReportInner.add(hmEmpMap.get(emp));
					alReportInner.add(uF.showData(hmEmpPanNo.get(emp),""));
					alReportInner.add(uF.showData(hmEmpJoiningDate.get(emp),""));
					alReportInner.add(uF.showData(hmEmpEndDate.get(emp),""));
					alReportInner.add(uF.showData(payMothEmpMap.get(deptId), ""));	
										
					dblNet+=uF.parseToDouble(salaryMap.get("NET"));
					dblGross+=uF.parseToDouble(salaryMap.get("GROSS"));
					
					
					alInnerExport=new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle(uF.showData(""+count, ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpCode.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
					alInnerExport.add(new DataStyle(uF.showData(hmEmpMap.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpPanNo.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpJoiningDate.get(emp), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle(uF.showData(hmEmpEndDate.get(emp),""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
					alInnerExport.add(new DataStyle(uF.showData(payMothEmpMap.get(deptId), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					boolean flag1=false;
					double grossEarning=0;
					for(int i=0; i<alEarnings.size(); i++){
						ComparatorWeight comparatorWeight=alEarnings.get(i);
						
						if(flag1!=comparatorWeight.isVariable()){
							flag1=true;
							alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),grossEarning),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							alReportInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),grossEarning));

						}
							grossEarning+=uF.parseToDouble(salaryMap.get(((ComparatorWeight)alEarnings.get(i)).getStrName()));
							double earningHead=uF.parseToDouble(salaryMap.get(((ComparatorWeight)alEarnings.get(i)).getStrName()))+uF.parseToDouble(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()));
							totalSalaryHead.put(((ComparatorWeight)alEarnings.get(i)).getStrName(), earningHead+"");
							
							alReportInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))));
							alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					}
					alReportInner.add(salaryMap.get("GROSS"));
					alInnerExport.add(new DataStyle(uF.showData(salaryMap.get("GROSS"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		    		for(int i=0; i<alDeductions.size(); i++){
		    			double earningHead=uF.parseToDouble(salaryMap.get(((ComparatorWeight)alDeductions.get(i)).getStrName()))+uF.parseToDouble(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()));
		    			totalSalaryHead.put(((ComparatorWeight)alDeductions.get(i)).getStrName(), earningHead+"");
		    			
		    			alReportInner.add(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))));
		    			
		    			alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(salaryMap.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		    		}
		    		alReportInner.add(salaryMap.get("NET"));
		    		alInnerExport.add(new DataStyle(uF.showData(salaryMap.get("NET"), ""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		    		
		    		alReportList.add(alReportInner);
		    		
		    		reportListExport.add(alInnerExport);
					
//				}
						
				
				oldEmp=newEmp;
				
				
				}
				
				if(sizeCount==deptEmpMap.size()){

					List<String> alReportInner = new ArrayList<String>();
					alReportInner.add(" ");
					alReportInner.add("");
					alReportInner.add("");
					alReportInner.add("");
					alReportInner.add("");
					alReportInner.add("");
					alReportInner.add("<strong>Total</strong>");				
					
					if(GrandtotalList.size()==0){
						GrandtotalList.put("1","");
						GrandtotalList.put("2","");
						GrandtotalList.put("3","");
						GrandtotalList.put("4","");
						GrandtotalList.put("5","");
						GrandtotalList.put("6","");
						GrandtotalList.put("7","Grand Total");				
					}
					
					
					
					alInnerExport=new ArrayList<DataStyle>();
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));	
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					alInnerExport.add(new DataStyle("Total",Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					
					boolean flag1=false;
					double grossEarning=0;
					double GrandgrossEarning=0;
//					int mpkey=7;
					int mpkey=8;
					for(int i=0; i<alEarnings.size(); i++){
						ComparatorWeight comparatorWeight=alEarnings.get(i);
						
						if(flag1!=comparatorWeight.isVariable()){
							flag1=true;
							alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),grossEarning),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							alReportInner.add("<strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),grossEarning)+"</strong>");
							GrandtotalList.put((mpkey++)+"", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),GrandgrossEarning));
						}
							grossEarning+=uF.parseToDouble(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()));
							alReportInner.add("<strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0")))+"</strong>");
							double a=uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))+uF.parseToDouble(GrandtotalList.get(mpkey+""));
							GrandgrossEarning+=a;
							GrandtotalList.put(mpkey+"", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),a));
							alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alEarnings.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
							mpkey++;
						
		    		}
					alReportInner.add("<strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblGross)+"</strong>");
					alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblGross),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
					
					if(GrandtotalList.size()==0){
						GrandtotalList.put(mpkey+"",uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblGross));
					}else{
						double b=dblGross+uF.parseToDouble(GrandtotalList.get(mpkey+""));
						GrandtotalList.put(mpkey+"", uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),b));
					}
					
					
		    		for(int i=0; i<alDeductions.size(); i++){
		    			alReportInner.add("<strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0")))+"</strong>");
		    			alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		    			
						double a=uF.parseToDouble(uF.showData(totalSalaryHead.get(((ComparatorWeight)alDeductions.get(i)).getStrName()), "0"))+uF.parseToDouble(GrandtotalList.get(mpkey+1+i+""));
						GrandtotalList.put(mpkey+1+i+"",uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),a));
		    		}
		    		alReportInner.add("<strong>"+uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet)+"</strong>");
		    		alInnerExport.add(new DataStyle(uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
		    		if(GrandtotalList.size()==0){
						GrandtotalList.put((mpkey+1+alDeductions.size())+"",uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),dblNet));
					}else{
						double a=dblNet+uF.parseToDouble(GrandtotalList.get((mpkey+1+alDeductions.size())+""));
						GrandtotalList.put((mpkey+1+alDeductions.size())+"",uF.getRoundOffValue(uF.parseToInt(CF.getRoundOffCondtion()),a));
					}
		    		
		    		alReportList.add(alReportInner);
					
		    		reportListExport.add(alInnerExport);
		    		
		    		totalSalaryHead=new HashMap<String,String>();			    		
				}
			}
			
			
			
			List<String> alReportInner = new ArrayList<String>();
			
			if(GrandtotalList.size()>0){
				alInnerExport=new ArrayList<DataStyle>();
				for(int i=0;i<GrandtotalList.size();i++){
					alReportInner.add("<strong>"+GrandtotalList.get(i+1+"")+"</strong>");
					alInnerExport.add(new DataStyle(GrandtotalList.get(i+1+""),Element.ALIGN_CENTER,"NEW_ROMAN",6,"0","0",BaseColor.LIGHT_GRAY));
				}
				
			}
			
			if (GrandtotalList.size() > 0) {
				alReportList.add(alReportInner);
				reportListExport.add(alInnerExport);
			}
			request.setAttribute("reportListExport",reportListExport);
			System.out.println("alReportList=="+alReportList.get(1));
			
			request.setAttribute("reportList", alReportList);
			request.setAttribute("alEarnings", alEarnings);
			request.setAttribute("alDeductions", alDeductions);
			request.setAttribute("hmSalaryDetails", hmSalaryDetails);
			request.setAttribute("hmPayPayroll", hmPayPayroll);
			request.setAttribute("hmEmpMap", hmEmpMap);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.getClass() + ": " +  e.getMessage(), e);
		}finally{
			db.closeResultSet(rs);
			db.closeStatements(pst);
			db.closeConnection(con);
		}
		
		
		return SUCCESS;
		
	}
	
	private HttpServletRequest request;

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


	public String getPaycycle() {
		return paycycle;
	}


	public void setPaycycle(String paycycle) {
		this.paycycle = paycycle;
	}


	public List<FillPayCycles> getPaycycleList() {
		return paycycleList;
	}


	public void setPaycycleList(List<FillPayCycles> paycycleList) {
		this.paycycleList = paycycleList;
	}


	public List<FillFinancialYears> getFinancialYearList() {
		return financialYearList;
	}


	public void setFinancialYearList(List<FillFinancialYears> financialYearList) {
		this.financialYearList = financialYearList;
	}


	public String getStrMonth() {
		return strMonth;
	}


	public void setStrMonth(String strMonth) {
		this.strMonth = strMonth;
	}


	public String getStrApprove() {
		return strApprove;
	}


	public void setStrApprove(String strApprove) {
		this.strApprove = strApprove;
	}


	public String[] getChbxApprove() {
		return chbxApprove;
	}


	public void setChbxApprove(String[] chbxApprove) {
		this.chbxApprove = chbxApprove;
	}


	public List<FillMonth> getAlMonthList() {
		return alMonthList;
	}


	public void setAlMonthList(List<FillMonth> alMonthList) {
		this.alMonthList = alMonthList;
	}


	public String getApprovePC() {
		return approvePC;
	}


	public void setApprovePC(String approvePC) {
		this.approvePC = approvePC;
	}


	public String[] getF_strWLocation() {
		return f_strWLocation;
	}


	public void setF_strWLocation(String[] f_strWLocation) {
		this.f_strWLocation = f_strWLocation;
	}


	public String[] getF_level() {
		return f_level;
	}


	public void setF_level(String[] f_level) {
		this.f_level = f_level;
	}


	public List<FillLevel> getLevelList() {
		return levelList;
	}


	public void setLevelList(List<FillLevel> levelList) {
		this.levelList = levelList;
	}


	public List<FillWLocation> getwLocationList() {
		return wLocationList;
	}


	public void setwLocationList(List<FillWLocation> wLocationList) {
		this.wLocationList = wLocationList;
	}


	public String[] getF_department() {
		return f_department;
	}


	public void setF_department(String []f_department) {
		this.f_department = f_department;
	}


	public String[] getF_service() {
		return f_service;
	}


	public void setF_service(String []f_service) {
		this.f_service = f_service;
	}


	public List<FillDepartment> getDepartmentList() {
		return departmentList;
	}


	public void setDepartmentList(List<FillDepartment> departmentList) {
		this.departmentList = departmentList;
	}


	public List<FillServices> getServiceList() {
		return serviceList;
	}


	public void setServiceList(List<FillServices> serviceList) {
		this.serviceList = serviceList;
	}


	public String getF_org() {
		return f_org;
	}


	public void setF_org(String f_org) {
		this.f_org = f_org;
	}


	public List<FillOrganisation> getOrgList() {
		return orgList;
	}


	public void setOrgList(List<FillOrganisation> orgList) {
		this.orgList = orgList;
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

}
